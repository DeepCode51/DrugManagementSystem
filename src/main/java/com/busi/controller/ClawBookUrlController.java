package com.busi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.busi.model.ClawBookUrl;
import com.zcurd.common.DBTool;
import com.zcurd.common.base.BaseController;
import com.zcurd.common.render.csv.CsvRender;
import com.zcurd.common.util.StringUtil;

/**
 * 小说管理
 */
public class ClawBookUrlController extends BaseController {
	
	public void listPage() {
		setAttr("dictDatastatus", ClawBookUrl.me.getDictDatastatus());
		render("list.html");
	}
	
	public void listData() {
		Object[] queryParams = getQueryParams();
		String[] properties = (String[]) queryParams[0];
		String[] symbols = (String[]) queryParams[1];
		Object[] values = (Object[]) queryParams[2];
		
		String orderBy = getOrderBy();
		if(StringUtil.isEmpty(orderBy)) {
			orderBy = "id desc";
		}
		
		List<Record> list = DBTool.findByMultPropertiesDbSource("zcurd_busi", "claw_book_url", properties, symbols, values, orderBy, getPager());
		
		Map<String, Object> dictDatastatus = ClawBookUrl.me.getDictDatastatus();
		for(Record record : list) {
			String fieldName = "status";
			if(dictDatastatus.get(record.get(fieldName).toString()) != null) {
				record.set(fieldName, dictDatastatus.get(record.get(fieldName).toString()));
			}
		}
		
		renderDatagrid(
			list, 
			DBTool.countByMultPropertiesDbSource("zcurd_busi", "claw_book_url", properties, symbols, values)
		);
	}
	
	//增加页面
	public void addPage() {
		setAttr("dictDatastatus", ClawBookUrl.me.getDictDatastatus());
		render("add.html");
	}
	
	//增加
	public void add() {
		getModel(ClawBookUrl.class, "model").save();
		renderSuccess();
	}
	
	//修改页面
	public void updatePage() {
		setAttr("dictDatastatus", ClawBookUrl.me.getDictDatastatus());
		setAttr("model", ClawBookUrl.me.findById(getPara("id")));
		render("update.html");
	}
	
	//修改
	public void update() {
		ClawBookUrl model = ClawBookUrl.me.findById(getPara("id"));
		model.set("source", getPara("model.source"));
		model.set("book_name", getPara("model.book_name"));
		model.set("url", getPara("model.url"));
		model.set("status", getPara("model.status"));
		model.set("create_time", getPara("model.create_time"));
		model.set("last_update_time", getPara("model.last_update_time"));
		model.update();
		renderSuccess();
	}
	
	//删除
	public void delete() {
		Integer[] ids = getParaValuesToInt("id[]");
		for (Integer id : ids) {
			new ClawBookUrl().set("id", id).delete();
			
		}
		renderSuccess();
	}
	
	//详情页面
	public void detailPage() {
		ClawBookUrl model = ClawBookUrl.me.findById(getParaToInt("id"));
		String fieldName = "status";	
		Map<String, Object> dictDatastatus = ClawBookUrl.me.getDictDatastatus();
		if(dictDatastatus.get(model.get(fieldName).toString()) != null) {
			model.set(fieldName, dictDatastatus.get(model.get(fieldName).toString()));
		}
		setAttr("model", model);
		render("detail.html");
	}
	
	//导出csv
	public void exportCsv() {
		Object[] queryParams = getQueryParams();
		String[] properties = (String[]) queryParams[0];
		String[] symbols = (String[]) queryParams[1];
		Object[] values = (Object[]) queryParams[2];
		
		String orderBy = getOrderBy();
		if(StringUtil.isEmpty(orderBy)) {
			orderBy = "id desc";
		}
		
		List<Record> list = DBTool.findByMultPropertiesDbSource("zcurd_busi", "claw_book_url", properties, symbols, values);
		Map<String, Object> dictDatastatus = ClawBookUrl.me.getDictDatastatus();
		for(Record record : list) {
			String fieldName = "status";
			if(dictDatastatus.get(record.get(fieldName).toString()) != null) {
				record.set(fieldName, dictDatastatus.get(record.get(fieldName).toString()));
			}
		}
		
		List<String> headers = new ArrayList<String>();
		List<String> clomuns = new ArrayList<String>();
		headers.add("来源");
		clomuns.add("source");
		headers.add("书名");
		clomuns.add("book_name");
		headers.add("阅读地址");
		clomuns.add("url");
		headers.add("状态");
		clomuns.add("status");
		headers.add("创建时间");
		clomuns.add("create_time");
		headers.add("最后更新时间");
		clomuns.add("last_update_time");
		
		CsvRender csvRender = new CsvRender(headers, list);
		csvRender.clomuns(clomuns);
		csvRender.fileName("小说管理");
		render(csvRender);
	}
}
