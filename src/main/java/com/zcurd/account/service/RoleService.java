package com.zcurd.account.service;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.zcurd.common.util.StringUtil;

/**
 * 角色权限管理（权限实现请查看LoginService）
 * @author 钟世云 2016.2.17
 */
public class RoleService {
	
	/**
	 * 保存权限
	 */
	@Before(Tx.class)
	public void saveAuth(String menuIds, String btnIds, String dataruleIds, int roleId) {
		//保存菜单权限
		Db.update("delete from sys_role_menu where role_id=?", roleId);
		if(StringUtil.isNotEmpty(menuIds)) {
			for (String menuId : menuIds.split(",")) {
				Db.update("INSERT INTO sys_role_menu (role_id, menu_id) VALUES (?, ?)", new Object[]{roleId, menuId});
			}
		}
		//保存按钮权限
		Db.update("delete from sys_role_btn where role_id=?", roleId);
		if(StringUtil.isNotEmpty(btnIds)) {
			for (String btnId : btnIds.split(",")) {
				Db.update("INSERT INTO sys_role_btn (role_id, btn_id) VALUES (?, ?)", new Object[]{roleId, btnId});
			}
		}
		
		//保存数据权限
		Db.update("delete from sys_role_datarule where role_id=?", roleId);
		if(StringUtil.isNotEmpty(dataruleIds)) {
			for (String dataruleId : dataruleIds.split(",")) {
				Db.update("INSERT INTO sys_role_datarule (role_id, datarule_id) VALUES (?, ?)", new Object[]{roleId, dataruleId});
			}
		}	
		//删除没有菜单权限的数据权限
		Db.update("delete from sys_role_datarule where id in(select * from (select a.id from sys_role_datarule a "
				+ "left join sys_menu_datarule b on a.datarule_id = b.id "
				+ "left join sys_role_menu c on (b.menu_id=c.menu_id and a.role_id=c.role_id) "
				+ "where a.role_id=? and c.id is null) a)", roleId);
	}

}
