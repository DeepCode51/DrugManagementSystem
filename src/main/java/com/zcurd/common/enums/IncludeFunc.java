package com.zcurd.common.enums;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 在线表单-包含功能
 * @author 钟世云 2020年3月29日 下午4:06:41
 */
public enum IncludeFunc {
	add("增加"), delete("删除"), update("修改"), detail("详情"), list("列表"), export("导出");
	
	private String cn;
	private IncludeFunc(String cn) {
		this.setCn(cn);
	}
	
	public String getCn() {
		return cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}

	/**
	 * 获得全部功能列表
	 */
	public static List<String[]> getIncludeFuncList() {
		List<String[]> list = new ArrayList<>();
		for (IncludeFunc item : IncludeFunc.values()) {
			list.add(new String[]{item.toString(), item.getCn()});
		}
		return list;
	}
	
	/**
	 * 当前功能是否需要刷新字典数据
	 */
	public boolean isNeedFlushDictData(Method method) {
		// 可设置不需要刷新字典数据
		return true;
	}
}
