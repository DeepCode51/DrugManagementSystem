package com.zcurd.account.controller;

import com.zcurd.account.model.SysMenu;
import com.zcurd.common.base.BaseController;

public class MenuController extends BaseController {
	
	public void listAll() {
		renderJson(SysMenu.dao.findAll());
	}

	public void list() {
		render("list.html");
	}
	
	public void addPage() {
		render("add.html");
	}
	
	public void add() {
		if(getModel(SysMenu.class, "model").save()) {
			addOpLog("[菜单管理] 增加");
			renderSuccess();
		}else {
			renderFailed();
		}
	}
	
	public void updatePage() {
		setAttr("model", SysMenu.dao.findById(getParaToInt("id")));
		render("update.html");
	}
	
	public void update() {
		if(getModel(SysMenu.class, "model").update()) {
			addOpLog("[菜单管理] 修改");
			renderSuccess();
		}else {
			renderFailed();
		}
	}
	
	public void delete() {
		Integer[] ids = getParaValuesToInt("id[]");
		for (Integer id : ids) {
			SysMenu.dao.deleteById(id);
		}
		addOpLog("[菜单管理] 删除");
		renderSuccess();
	}
}
