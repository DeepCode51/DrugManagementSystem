package com.zcurd.online.model;

import com.zcurd.common.genmodel.RegenerateModel;

/**
 * 生成在线表单Model（仅生成BaseModel） 
 */
public class _GenOnlineModel {
	
	public static void main(String[] args) {
		RegenerateModel.generate("com.zcurd.online.model", "zcurd_.*", "task_.*");
	}
	
}