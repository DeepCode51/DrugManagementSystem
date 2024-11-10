package com.zcurd.common.util;

import java.util.HashMap;
import java.util.Map;

import com.zcurd.online.vo.ZcurdMeta;

/**
 * 在线表单工具类
 * @author 钟世云 2020年3月29日 下午2:53:08
 */
public class ZcurdKit {
	private static ThreadLocal<Map<String, Object>> tl = new ThreadLocal<>();
	
	/**
	 * 设置在线表单数据
	 */
	public static void putZcurdMeta(ZcurdMeta zcurdMeta) {
		get().put("zcurdMeta", zcurdMeta);
	}
	
	/**
	 * 获得在线表单数据
	 */
	public static ZcurdMeta getZcurdMeta() {
		return (ZcurdMeta) get().get("zcurdMeta");
	}
	
	public static void remove() {
		tl.remove();
	}
	
	private static Map<String, Object> get() {
		if(tl.get() == null) {
			tl.set(new HashMap<String, Object>());
		}
		return tl.get();
	}

}
