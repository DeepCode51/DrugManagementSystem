package com.zcurd.common;

import java.util.Date;

import com.zcurd.account.common.kit.ZcurdKit;
import com.zcurd.account.model.SysUser;

public class ZcurdConst {
	
	/** 后台session user key **/
	public final static String ADMIN_SESSIOIN_USER_KEY = "sysUser";
	
	/** 搜索类型 or2In (多选搜索，用or代替in，防止SQL注入) **/
	public final static String SEARCH_TYPE_OR2IN = "or2In";
	
	/** 默认删除标识 **/
	public final static String DEF_SYS_DELETE_FLAG = "sys_delete_flag";
	
	/**
	 * 系统约定字段-增加
	 */
	public static Object[][] getSystemDefField4Add() {
		SysUser user = ZcurdKit.getSessionUser();
		return new Object[][] {
				{"sys_create_user_id", user.getId()}, 
				{"sys_create_user", user.getUserName()}, 
				{"sys_create_time", new Date()},
				
				{"sys_update_user_id", user.getId()}, 
				{"sys_update_user", user.getUserName()}, 
				{"sys_update_user_number", user.getUserName()}, 
				{"sys_update_time", new Date()}
		};
	}
	
	/**
	 * 系统约定字段-修改
	 */
	public static Object[][] getSystemDefField4Update() {
		SysUser user = ZcurdKit.getSessionUser();
		return new Object[][] {
				{"sys_update_user_id", user.getId()}, 
				{"sys_update_user_number", user.getUserName()}, 
				{"sys_update_user", user.getUserName()}, 
				{"sys_update_time", new Date()}
		};
	}

}
