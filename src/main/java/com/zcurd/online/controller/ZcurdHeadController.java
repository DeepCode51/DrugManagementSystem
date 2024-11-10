package com.zcurd.online.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Duang;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.RenderException;
import com.zcurd.common.DBTool;
import com.zcurd.common.DbMetaTool;
import com.zcurd.common.base.BaseController;
import com.zcurd.common.enums.IncludeFunc;
import com.zcurd.common.util.StringUtil;
import com.zcurd.online.model.ZcurdField;
import com.zcurd.online.model.ZcurdHead;
import com.zcurd.online.service.ZcurdService;
import com.zcurd.online.vo.ZcurdMeta;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 表单管理
 * @author 钟世云 2016.2.5
 */
public class ZcurdHeadController extends BaseController {
	
	//表单列表
	public void listPage() {
		render("head/list.html");
	}
	
	//列表页面数据
	public void listData() {
		Object[] queryParams = getQueryParams();
		String[] properties = (String[]) queryParams[0];
		String[] symbols = (String[]) queryParams[1];
		Object[] values = (Object[]) queryParams[2];
		
		String orderBy = getOrderBy();
		if(StringUtil.isEmpty(orderBy)) {
			orderBy = "id desc";
		}
		
		renderDatagrid(
			DBTool.findByMultProperties("zcurd_head", properties, symbols, values, orderBy, getPager()), 
			DBTool.countByMultProperties("zcurd_head", properties, symbols, values)
		);
	}

	//修改页面
	public void updatePage() {
		setAttr("model", ZcurdHead.dao.findById(getParaToInt("id")));
		setAttr("includeFuncList", IncludeFunc.getIncludeFuncList());
		render("head/update.html");
	}
	
	//修改
	public void update() {
		final String fields = getPara("rowsStr");
		final JSONArray jsonObjs = JSONObject.parseArray(fields);
		Db.tx(new IAtom(){
			@SuppressWarnings("unchecked")
			public boolean run() throws SQLException {
				ZcurdHead zcurdHead = getModel(ZcurdHead.class, "model");
				zcurdHead.update();
				final Long headId = zcurdHead.getLong("id");
				Db.update("delete from zcurd_field where head_id=" + headId);
				if(jsonObjs.size() > 0) {
					for (Object object : jsonObjs) {
						ZcurdField field = new ZcurdField();
						field.set("head_id", headId);
						field.put((Map<String, Object>)object);
						field.save();
					}
				}
				DbMetaTool.updateMetaData(headId.intValue());
				return true; 
			}
		});
		
		addOpLog("[在线表单] 修改");
		renderSuccess("保存成功！");
	}
	
	//生成表单页面
	public void genFormPage() {
		render("head/genForm.html");
	}
	
	//生成表单页面
	public void genFormData() {
		String dbSource = getPara("db_source");
		String dbName = (String) DBTool.use(dbSource).execute(new ICallback() {
			@Override
			public Object call(Connection conn) throws SQLException {
				return conn.getCatalog();
			}
		});
		String sql = "select TABLE_SCHEMA, TABLE_TYPE, a.TABLE_NAME, TABLE_COMMENT, CREATE_TIME from information_schema.TABLES a where a.TABLE_SCHEMA='" + dbName + "' order by CREATE_TIME desc";
		renderDatagrid(DBTool.use(dbSource).find(sql));
	}
	
	//生成表单
	public void genForm() {
		String tableName = getPara("tableName");
		String dbSource = getPara("db_source");
		ZcurdService zcurdService = Duang.duang(ZcurdService.class);
		zcurdService.genForm(tableName, dbSource);
		
		addOpLog("[在线表单] 生成表单");
		renderSuccess();
	}
	
	//删除
	public void delete() {
		Integer[] ids = getParaValuesToInt("id[]");
		for (Integer id : ids) {
			ZcurdHead.dao.deleteById(id);
			Db.update("delete from zcurd_field where head_id=?", id);
			DbMetaTool.updateMetaData(id);
		}
		
		addOpLog("[在线表单] 删除");
		renderSuccess();
	}
	
	//字段列表
	public void listField() {
		renderDatagrid(ZcurdField.me.paginate(getParaToInt("page", 1), getParaToInt("rows", 500), getParaToInt("head_id")));
	}
	
	//代码生成
	public void genCode() throws IOException, TemplateException {
		int headId =  getParaToInt("headId");
		ZcurdService zcurdService = Duang.duang(ZcurdService.class);
		ZcurdMeta metaMap = zcurdService.getMetaData(headId);
		ZcurdHead head = metaMap.getHead();
		String tableName = head.getTableName();
		String className = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
		int index = className.indexOf("_");
		while(index > 0) {
			String s = className.substring(index + 1, index + 2);
			className = className.replace("_" + s, s.toUpperCase());
			index = className.indexOf("_");
		}
		String lowerClassName = className.substring(0, 1).toLowerCase() + className.substring(1);
		
		Map<String, Object> mateDate = metaMap.toMap();
		mateDate.put("className", className);
		mateDate.put("queryPara", new HashMap<>());
		
		//复制模板
		copyTemp("listPage.html");		//列表页面
        copyTemp("addPage.html");		//增加页面
        copyTemp("updatePage.html");	//编辑页面
        copyTemp("detailPage.html");	//详情页面
		
        String genCodePath = PropKit.get("genCodePath") + className + "/";
        String genCodePagePath = genCodePath + lowerClassName + "/";
        new File(genCodePath).mkdirs();
        new File(genCodePagePath).mkdirs();
        
        gen(mateDate, "/zcurd/zcurd/genCode/listPage.html", genCodePagePath + "list.html");
        gen(mateDate, "/zcurd/zcurd/genCode/addPage.html", genCodePagePath + "add.html");
        gen(mateDate, "/zcurd/zcurd/genCode/updatePage.html", genCodePagePath + "update.html");
        gen(mateDate, "/zcurd/zcurd/genCode/detailPage.html", genCodePagePath + "detail.html");
        
        gen(mateDate, "/zcurd/zcurd/genCode/controller.html", genCodePath + className + "Controller.java");
        gen(mateDate, "/zcurd/zcurd/genCode/model.html", genCodePath + className + ".java");
        
        addOpLog("[在线表单] 生成代码");
        renderSuccess("代码生成成功！保存在" + genCodePath);
	}
	
	/**
	 * 生成代码
	 */
	private void gen(Map<String, Object> mateDate, String tempFile, String genFile) throws FileNotFoundException, UnsupportedEncodingException {
		Configuration config = FreeMarkerRender.getConfiguration();
		PrintWriter pw = new PrintWriter(new File(genFile), "utf8");
        try {
			Template template = config.getTemplate(tempFile);
			template.process(mateDate, pw);
		} catch (Exception e) {
			throw new RenderException(e);
		}
		finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
	
	/**
	 * 复制模板文件（共用zcurd的模板）
	 */
	private void copyTemp(String fileName) {
		String basePath = PathKit.getWebRootPath() + "/zcurd/zcurd/";
		String content = StringUtil.readTxt2String(new File(basePath + fileName));
		
		//对特定标签进行替换，使freemark不解析
		//替换include标签
		content = content.replaceAll("<#include", "\\${\"<\"}#include");
		//替换字典数据输出
		content = content.replace("<#list item.dict.keySet() as key>,{id:'${key}', text:'${item.dict.get(key)}'}</#list>", 
				"<${'#'}list dictData${item.field_name}.keySet() as key>,{id:'${'$'}{key}', text:'${'$'}{dictData${item.field_name}.get(key)}'}</${'#'}list>");
		
		if("addPage.html".equals(fileName) || "updatePage.html".equals(fileName) || "detailPage.html".equals(fileName)) {
			//删除块
			content = content.replace("\"headId\": ${headId},", "");
			//替换主键、model输出
			content = content.replace("model[item.field_name]??", "1==1");	//临时处理，默认都显示图片
			content = content.replace("${model[head.id_field]}", "${'$'}{model.${head.id_field}}");
			content = content.replaceAll("\\$\\{model\\[item\\.field_name\\]", "\\${'\\$'}{model.\\${item.field_name}");
			content = content.replaceAll("\\$\\{modelDetail\\[item\\.field_name\\]", "\\${'\\$'}{modelDetail.\\${item.field_name}");
		}
		
		StringUtil.saveToFile(basePath + "genCode/" + fileName, content);
	}
	
}
