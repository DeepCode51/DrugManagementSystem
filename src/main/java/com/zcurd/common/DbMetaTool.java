package com.zcurd.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.zcurd.common.util.StringUtil;
import com.zcurd.common.util.ZcurdKit;
import com.zcurd.online.model.ZcurdField;
import com.zcurd.online.model.ZcurdHead;
import com.zcurd.online.model.ZcurdHeadBtn;
import com.zcurd.online.model.ZcurdHeadJs;
import com.zcurd.online.vo.ZcurdMeta;

public class DbMetaTool {
	
	private static Map<Integer, ZcurdMeta> metaDataMap = new Hashtable<Integer, ZcurdMeta>();

	public static ZcurdMeta getMetaData(int headId) {
		ZcurdMeta metaData = metaDataMap.get(headId);
		// 在线表单开发模式不走缓存
		if(metaData == null || ZcurdConfig.getDevModel()) {
			metaData = getMetaDataFromDb(headId);
			metaDataMap.put(headId, metaData);
		}
		return metaData;
	}
	
	private static ZcurdMeta getMetaDataFromDb(int headId) {
		// 从本地线程获取
		if(ZcurdKit.getZcurdMeta() != null && ZcurdKit.getZcurdMeta().getHead().getId() == headId) {
			return ZcurdKit.getZcurdMeta();
		}
		
		ZcurdHead head = ZcurdHead.dao.findById(headId);
		if(StringUtil.isEmpty(head.getTableNameUpdate())) {
			// 更新表为空时，设置默认为查询表
			head.setTableNameUpdate(head.getTableName());
		}
		
		List<ZcurdField> fieldList = ZcurdField.me.findByHeadId(head.getLong("id").intValue());
		
		Map<String, Map<String, Object>> dictMap = new HashMap<String, Map<String, Object>>();
		for (ZcurdField zcurdField : fieldList) {
			String dictSql = zcurdField.getStr("dict_sql");
			if(StringUtil.isNotEmpty(dictSql)) {
				Map<String, Object> dictData = getDictData(dictSql);
				dictMap.put(zcurdField.getStr("field_name"), dictData);
				zcurdField.put("dict", dictData);
			}
		}
		List<ZcurdField> addFieldList = new ArrayList<ZcurdField>();
		List<ZcurdField> updateFieldList = new ArrayList<ZcurdField>();
		List<ZcurdField> footerFieldList = new ArrayList<ZcurdField>();
		for (ZcurdField zcurdField : fieldList) {
			if(zcurdField.getIsShowList() == 1 && zcurdField.getIsSum() == 1) {
				footerFieldList.add(zcurdField);
			}
			
			if(!zcurdField.getStr("field_name").equals(head.getStr("id_field"))) {
				if(zcurdField.getInt("is_allow_add") == 1) {
					addFieldList.add(zcurdField);
				}
				if(zcurdField.getInt("is_allow_update") == 1 || zcurdField.getInt("is_allow_detail") == 1) {
					updateFieldList.add(zcurdField);
				}
			}
		}
		List<ZcurdHeadBtn> btnList = ZcurdHeadBtn.me.findByHeadId(headId);
		List<ZcurdHeadBtn> topList = new ArrayList<ZcurdHeadBtn>(); 
		List<ZcurdHeadBtn> lineList = new ArrayList<ZcurdHeadBtn>(); 
		for (ZcurdHeadBtn btn : btnList) {
			if(btn.getInt("location") == 1) {
				topList.add(btn);
			}else if(btn.getInt("location") == 2) {
				lineList.add(btn);
			}
		}
		for (ZcurdHeadBtn btn : btnList) {
			if(btn.getInt("action") == 1) {
				head.set("form_type", 2); //设置表单类型为主从
				break;
			}
		}
		List<ZcurdHeadJs> jsList = ZcurdHeadJs.me.findByHeadId(headId);
		
		ZcurdMeta zcurdMeta = new ZcurdMeta();
		zcurdMeta.setHead(head);
		zcurdMeta.setFieldList(fieldList);
		zcurdMeta.setDictMap(dictMap);
		zcurdMeta.setAddFieldList(addFieldList);
		zcurdMeta.setUpdateFieldList(updateFieldList);
		zcurdMeta.setFooterFieldList(footerFieldList);
		zcurdMeta.setBtnList(btnList);
		zcurdMeta.setTopList(topList);
		zcurdMeta.setLineList(lineList);
		zcurdMeta.setJsList(jsList);
		return zcurdMeta;
	}
	
	public static void updateMetaData(int headId) {
		metaDataMap.remove(headId);
	}
	
	/**
	 * 根据字典sql获得字典数据
	 */
	public static Map<String, Object> getDictData(String dictSql) {
		String[] parseSql = DBTool.parseSQL4DbSource(dictSql);
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		List<Record> listRecord = DBTool.use(parseSql[0]).find("select 'key', 'text' union all select * from (" + parseSql[1] + ") a");
		for (int i = 1; i < listRecord.size(); i++) {
			Record record = listRecord.get(i);
			map.put(record.getStr("key"), record.getStr("text"));
		}
		return map;
	}
	
	/**
	 * 刷新字典数据
	 */
	public static void flushDictData(ZcurdMeta zcurdMeta) {
		for (ZcurdField zcurdField : zcurdMeta.getFieldList()) {
			String dictSql = zcurdField.getStr("dict_sql");
			if(StringUtil.isNotEmpty(dictSql)) {
				Map<String, Object> dictData = DbMetaTool.getDictData(dictSql);
				zcurdMeta.getDictMap().put(zcurdField.getStr("field_name"), dictData);
				zcurdField.put("dict", dictData);
			}
		}
	}
	
}
