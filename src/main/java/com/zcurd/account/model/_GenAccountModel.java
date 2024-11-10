package com.zcurd.account.model;

import com.zcurd.common.genmodel.RegenerateModel;

/**
 * 生成账号权限Model（仅生成BaseModel） 
 */
public class _GenAccountModel {
	
	public static void main(String[] args) {
		RegenerateModel.generate("com.zcurd.account.model", "sys_.*");
	}
	
}