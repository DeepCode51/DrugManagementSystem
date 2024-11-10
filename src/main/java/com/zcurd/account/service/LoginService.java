package com.zcurd.account.service;

import com.zcurd.account.model.SysLoginLog;

/**
 * 登陆、权限业务
 * @author 钟世云 2016.2.5
 */
public class LoginService {
	
	/**
	 * 是否显示图形验证码
	 * @return true: 显示，false: 不显示
	 */
	public boolean isShowRandomCode(String sessionId, String ip) {
		// 10分钟内，当前session登录失败5次以上
		if(SysLoginLog.dao.findCountBySessionId(sessionId, 0, 10) >= 5) {
			return true;
		}
		
		// 1小时内，当前ip登录失败10次以上
		if(SysLoginLog.dao.findCountByIp(ip, 0, 60) >= 10) {
			return true;
		}
		
		// 24小时内，当前ip登录失败100次以上
		if(SysLoginLog.dao.findCountByIp(ip, 0, 60 * 24) >= 100) {
			return true;
		}
		return false;
	}
}
