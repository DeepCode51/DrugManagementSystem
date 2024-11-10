package com.zcurd.common.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zcurd.online.vo.ZcurdMeta;

public interface CurdHandle {
	
	public void add(ZcurdMeta zcurdMeta, HttpServletRequest req, Map<String, String[]> paraMap);
	
	public void update(ZcurdMeta zcurdMeta, HttpServletRequest req, Map<String, String[]> paraMap);
	
	public void delete(ZcurdMeta zcurdMeta, HttpServletRequest req, Map<String, String[]> paraMap);

}
