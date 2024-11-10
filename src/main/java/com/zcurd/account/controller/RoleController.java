package com.zcurd.account.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Duang;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zcurd.account.common.annotation.ClearAuth;
import com.zcurd.account.model.SysMenu;
import com.zcurd.account.model.SysMenuBtn;
import com.zcurd.account.service.RoleService;
import com.zcurd.common.base.BaseController;

/**
 * 角色权限
 * @author 钟世云 2016.2.5
 */
public class RoleController extends BaseController {
	
	//编辑权限页面
	public void editAuthPage() {
		int roleId = getParaToInt("roleId");
		setAttr("roleId", roleId);
		List<Record> btnIds = Db.find("select b.menu_id, b.id from sys_role_btn a join sys_menu_btn b on a.btn_id=b.id where role_id=?", roleId);
		List<Record> dataruleIds = Db.find("select b.menu_id, b.id from sys_role_datarule a join sys_menu_datarule b on a.datarule_id=b.id where role_id=?", roleId);
		setAttr("btnIds", btnIds);
		setAttr("dataruleIds", dataruleIds);
		render("editAuth.html");
	}
	
	//编辑权限
	public void editAuth() {
		String menuIds = getPara("menuIds");
		String btnIds = getPara("btnIds");
		String dataruleIds = getPara("dataruleIds");
		int roleId = getParaToInt("roleId");
		RoleService roleService = Duang.duang(RoleService.class);
		roleService.saveAuth(menuIds, btnIds, dataruleIds, roleId);
		
		addOpLog("[权限管理] 修改");
		renderSuccess();
	}
	
	//所有菜单
	public void getAllMenu() {
		int roleId = getParaToInt("roleId", 0);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("menuIds", Db.find("select * from sys_role_menu where role_id=?", roleId));
		result.put("menuList", SysMenu.dao.findAll());
		renderJson(result);
	}
	
	//一键生成增、删、改、导出权限按钮
	@ClearAuth
	public void genAuthBtnBatch() {
		int menuId = getParaToInt("menuId");
		List<SysMenuBtn> modelList = new ArrayList<SysMenuBtn>();
		
		modelList.add(new SysMenuBtn().set("menu_id", menuId).set("btn_name", "增加").set("class_name", "addBtn").set("method_name", "add,addPage"));
		modelList.add(new SysMenuBtn().set("menu_id", menuId).set("btn_name", "修改").set("class_name", "updateBtn").set("method_name", "update,updatePage"));
		modelList.add(new SysMenuBtn().set("menu_id", menuId).set("btn_name", "删除").set("class_name", "delBtn").set("method_name", "delete"));
		modelList.add(new SysMenuBtn().set("menu_id", menuId).set("btn_name", "导出").set("class_name", "exportBtn").set("method_name", "exportCsv"));
		Db.batchSave(modelList, 1000);
		
		renderSuccess();
	}
}
