package com.zcurd.online.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.zcurd.account.common.kit.ZcurdKit;
import com.zcurd.common.DBTool;
import com.zcurd.common.DbMetaTool;
import com.zcurd.common.ZcurdConst;
import com.zcurd.common.ZcurdTool;
import com.zcurd.common.util.FreemarkUtil;
import com.zcurd.common.util.StringUtil;
import com.zcurd.online.model.ZcurdField;
import com.zcurd.online.model.ZcurdHead;
import com.zcurd.online.vo.QueryParamVO;
import com.zcurd.online.vo.ZcurdMeta;

/**
 * 在线表单相关业务
 * @author 钟世云 2016.2.5
 */
public class ZcurdService {
	
	public void add(int headId, Map<String, String[]> paraMap, HttpServletRequest request) {
		ZcurdMeta mapmeta = getMetaData(headId);
		ZcurdHead head = mapmeta.getHead();
		List<ZcurdField> addFieldList = mapmeta.getAddFieldList();
		
		Record record = new Record();
		for (ZcurdField field : addFieldList) {
			String[] paramValues = paraMap.get("model." + field.getStr("field_name"));
			record.set(field.getStr("field_name"), paramValues == null ? null : paramValues[0]);
		}
		
		//变量
		Map<String, Object> varData = new HashMap<>();
		varData.put("user", ZcurdKit.getSessionUser());
		varData.put("metaData", mapmeta);
		varData.put("request", request);
		varData.put("session", request.getSession());
		
		//系统约定字段处理
		defFieldHandle(record, mapmeta.getFieldList(), ZcurdConst.getSystemDefField4Add());
		
		//默认值处理
		for (ZcurdField field : mapmeta.getFieldList()) {
			String defaultValue = field.getStr("default_value");
			//默认值为空，参数值不为空
			if(StringUtil.isNotEmpty(defaultValue) && StringUtil.isEmpty(record.getStr(field.getStr("field_name")))) {
				record.set(field.getStr("field_name"), FreemarkUtil.parse(defaultValue, varData));
			}
		}
		
		Db.use(ZcurdTool.getDbSource(head.getDbSource())).save(head.getTableNameUpdate(), head.getIdField(), record);
	}
	
	public void update(int headId, int id, Map<String, String[]> paraMap) {
		ZcurdMeta mapmeta = getMetaData(headId);
		ZcurdHead head = mapmeta.getHead();
		List<ZcurdField> updateFieldList = mapmeta.getUpdateFieldList();

		Record record = get(headId, id);
		for (ZcurdField field : updateFieldList) {
			if(field.getInt("is_allow_update") == 1) {
				String[] paramValues = paraMap.get("model." + field.getStr("field_name"));
				record.set(field.getStr("field_name"), paramValues == null ? null : paramValues[0]);
			}
		}
		
		//系统约定字段处理
		defFieldHandle(record, mapmeta.getFieldList(), ZcurdConst.getSystemDefField4Update());
		
		Db.use(ZcurdTool.getDbSource(head.getDbSource())).update(head.getTableNameUpdate(), head.getStr("id_field"), record);
	}
	
	public void delete(int headId, Integer[] ids) {
		ZcurdMeta mapmeta = getMetaData(headId);
		ZcurdHead head = mapmeta.getHead();
		
		for (Integer id : ids) {
			// 判断逻辑删除标识
			if(StringUtil.isNotEmpty(head.getDeleteFlagField())) {
				Db.use(ZcurdTool.getDbSource(head.getDbSource()))
				  .update("update " + head.getTableNameUpdate() + " set " + head.getDeleteFlagField() + "=1 where " + head.getIdField() + "=?", id);
			}else {
				Db.use(ZcurdTool.getDbSource(head.getDbSource())).deleteById(head.getTableNameUpdate(), head.getStr("id_field"), id);
			}	
		}
		DbMetaTool.updateMetaData(headId);
	}
	
	public List<Map<String, Object>> getFooter(ZcurdMeta zcurdMeta, QueryParamVO vo) {
		return getFooter(zcurdMeta, vo.getPropertiesToArray(), vo.getSymbolsToArray(), vo.getValuesToArray(), vo.getWhereSql());
	}
	
	public List<Map<String, Object>> getFooter(ZcurdMeta mapmeta, String[] properties, String[] symbols, Object[] values, String whereSql) {
		List<Map<String, Object>> footer = new ArrayList<>();
		ZcurdHead head = mapmeta.getHead();
		List<ZcurdField> footFieldList = mapmeta.getFooterFieldList();
		
		if(footFieldList.size() > 0) {
			StringBuilder sql = new StringBuilder("select ");
			for (int i = 0; i < footFieldList.size(); i++) {
				ZcurdField field = footFieldList.get(i);
				if(i > 0) {
					sql.append(",");
				}
				sql.append(" sum(" + field.getStr("field_name") + ")");
			}
			sql.append(" from " + head.getTableName());
			
			List<Object> list = DBTool.findDbSource(head.getDbSource(), sql.toString(), properties, symbols, values, whereSql);
			Object[] result = list.toArray(new Object[]{});
			//只一列时处理
			if(footFieldList.size() > 1) {
				result = (Object[]) list.get(0);
			}
			
			Map<String, Object> sumMap = new HashMap<String, Object>();
			for (int i = 0; i < footFieldList.size(); i++) {
				sumMap.put(footFieldList.get(i).getStr("field_name"), "<span style='color:blue;'>合计：" + result[i] + "</span>");
			}
			footer.add(sumMap);
		}
		return footer;
	}
	
	public Record get(int headId, int id) {
		ZcurdMeta mapmeta = getMetaData(headId);
		ZcurdHead head = mapmeta.getHead();
		
		QueryParamVO vo = new QueryParamVO().add(head.getStr("id_field"), id);
		vo.setDbSource(head.getDbSource());
		vo.setTable(head.getTableName());
		// 判断逻辑删除标识
		if(StringUtil.isNotEmpty(head.getDeleteFlagField())) {
			vo.add(head.getDeleteFlagField(), 0);
		}
		List<Record> list = DBTool.findList(vo);
		return list.size() == 0 ? null : list.get(0);
	}
	
	public ZcurdMeta getMetaData(int headId) {
		return com.zcurd.common.util.ZcurdKit.getZcurdMeta();
	}
	
	public ZcurdHead getHead(int headId) {
		return ZcurdHead.dao.findById(headId);
	}
	
	public void genForm(final String tableName, String dbSource) {
		final String dbs = ZcurdTool.getDbSource(dbSource);
		Db.use(dbs).execute(new ICallback() {
			@Override
			public Object call(Connection conn) throws SQLException {
				DatabaseMetaData metaData = conn.getMetaData();
				String dbName = conn.getCatalog();
				
				ResultSet pkRSet = metaData.getPrimaryKeys(dbName, null, tableName);
				String form_name = tableName;
				String idField = null;
				if (pkRSet.next()) {
					//获得表注释（jdbc无法获取到)
					try {
						String sql = "select TABLE_COMMENT from information_schema.TABLES a where a.TABLE_SCHEMA=? and a.table_name=?";
						form_name = Db.queryStr(sql, new Object[]{dbName, tableName});
					}catch(Exception e) {
						System.err.println("获得表注释失败！" + e.getMessage());
					}
					idField = (String) pkRSet.getObject(4);
				}
				
				ZcurdHead head = new ZcurdHead()
					.set("table_name", tableName)
					.set("form_name", form_name)
					.set("id_field", idField);
				head.set("db_source", dbs);
				head.save();
					
				ResultSet colRet = metaData.getColumns(dbName, "%", tableName, "%");
				int orderNum = 20;
				while (colRet.next()) {
					String field_name = colRet.getString("COLUMN_NAME");
					String column_name = colRet.getString("REMARKS");
					
					// 如果没有获取到id，则取第一个字段做为主键
					if(idField == null) {
						idField = field_name;
					}
					
					if(StringUtil.isEmpty(column_name)) {
						column_name = field_name;
					}
					
					ZcurdField field = new ZcurdField()
						.set("head_id", head.getLong("id").intValue())
						.set("field_name", field_name)
						.set("column_name", column_name)
						.set("data_type", colRet.getString("TYPE_NAME").toLowerCase())
						.set("order_num", orderNum)
						.set("is_allow_null", colRet.getInt("NULLABLE"));
					orderNum += 10;	// 排序递增10，方法后续调整顺序
					
					//主键
					if(field_name.equals(head.getIdField())) {
						field.set("order_num", 1);
						orderNum -= 10;
					}
					
					//控件类型
					String dataType = field.getStr("data_type");
					String inputType = "easyui-textbox";
					if(dataType.equals("timestamp") || dataType.equals("date") || dataType.equals("datetime")) {
						inputType = "easyui-datebox";
					}else if(dataType.equals("text")) {
						inputType = "textarea";
					}else if(dataType.endsWith("int") || dataType.equals("long")) {
						inputType = "easyui-numberspinner";
					}
					field.set("input_type", inputType);
					field.save();
					
					// 设置默认删除标识
					if(StringUtil.equals(ZcurdConst.DEF_SYS_DELETE_FLAG, field.getFieldName())) {
						head.set("delete_flag_field", ZcurdConst.DEF_SYS_DELETE_FLAG).update();
					}
				}
				
				if(head.get("id_field") == null) {
					head.set("id_field", idField).update();
				}
				return null;
			}
		});
	}
	
	/**
	 * 系统约定字段处理
	 * @param record
	 * @param fieldList
	 * @param defFields
	 */
	public void defFieldHandle(Record record, List<ZcurdField> fieldList, Object[][] defFields) {
		//系统约定字段处理
		for (Object[] sysDefField : defFields) {
			for (ZcurdField field : fieldList) {
				if(field.getFieldName().equals(sysDefField[0].toString())) {
					record.set(sysDefField[0].toString(), sysDefField[1]);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		C3p0Plugin c3p0Plugin = new C3p0Plugin("jdbc:mysql://127.0.0.1/zcurd?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull", "root", "123456");
		ActiveRecordPlugin arp = new ActiveRecordPlugin("zcurd", c3p0Plugin); 
		c3p0Plugin.start();
		arp.start();
		
		final String tableName = "blog";
		Db.use("zcurd").execute(new ICallback() {
			@Override
			public Object call(Connection conn) throws SQLException {
				DatabaseMetaData metaData = conn.getMetaData();
				System.out.println("数据库：" + conn.getCatalog());
				
				//获取tableName表列信息
				ResultSet tableSet = metaData.getTables(null, "%", "%", new String[]{"TABLE"});
				while(tableSet.next()) {
					System.out.println(tableSet.getString("TABLE_NAME") + "	" + tableSet.getString("REMARKS"));
				}

				ResultSet colRet = metaData.getColumns(null, "%", tableName, "%");
				while (colRet.next()) {
					String columnName = colRet.getString("COLUMN_NAME");
					String columnType = colRet.getString("TYPE_NAME");
					int datasize = colRet.getInt("COLUMN_SIZE");
					int digits = colRet.getInt("DECIMAL_DIGITS");
					int nullable = colRet.getInt("NULLABLE");
					System.out.println("字段：" + columnName + "\t" + columnType + "\t" + datasize + "\t" + digits + "\t" + nullable + "\t" + colRet.getString("REMARKS"));
				}
				
				ResultSet pkRSet = metaData.getPrimaryKeys(null, null, tableName);
				while (pkRSet.next()) {
					System.err.println("****** Comment ******");
					System.err.println("TABLE_CAT : " + pkRSet.getObject(1));
					System.err.println("TABLE_SCHEM: " + pkRSet.getObject(2));
					System.err.println("TABLE_NAME : " + pkRSet.getObject(3));
					System.err.println("COLUMN_NAME: " + pkRSet.getObject(4));
					System.err.println("KEY_SEQ : " + pkRSet.getObject(5));
					System.err.println("PK_NAME : " + pkRSet.getObject(6));
					System.err.println("****** ******* ******");
				}
				
				return null;
			}
		});
	}

}
