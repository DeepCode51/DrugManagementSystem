package com.zcurd.common.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zcurd.common.util.PasswordUtil;
import com.zcurd.online.vo.ZcurdMeta;

/**
 * 密码CurdHandle
 * @author 钟世云 2017.1.27
 */
public class PasswordCurdHandle implements CurdHandle {

	@Override
	public void add(ZcurdMeta zcurdMeta, HttpServletRequest req, Map<String, String[]> paraMap) {
		String password = PasswordUtil.defaultPassword;
		String[] passwordPara = paraMap.get("model.password");
		if(passwordPara != null && passwordPara.length > 0) {
			password = passwordPara[0];
		}
		paraMap.put("model.password", new String[]{PasswordUtil.encodePassword(password)});
	}

	@Override
	public void update(ZcurdMeta zcurdMeta, HttpServletRequest req, Map<String, String[]> paraMap) {
		System.out.println("------------------CurdHandle to update!");
		
	}

	@Override
	public void delete(ZcurdMeta zcurdMeta, HttpServletRequest req, Map<String, String[]> paraMap) {
		System.out.println("------------------CurdHandle to delete!");
		
	}

}
