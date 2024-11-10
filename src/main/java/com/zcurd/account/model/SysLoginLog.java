package com.zcurd.account.model;

import com.jfinal.plugin.activerecord.Db;
import com.zcurd.account.model.base.BaseSysLoginLog;

/**
 * 登陆日志
 */
@SuppressWarnings("serial")
public class SysLoginLog extends BaseSysLoginLog<SysLoginLog> {
	public static final SysLoginLog dao = new SysLoginLog().dao();
	
	/**
	 * 查询量
	 * @param sessionId	sessionId
	 *  @param isSuccess 是否成功（0：失败，1：成功）
	 * @param minute	最后分钟数
	 */
	public long findCountBySessionId(String sessionId, int isSuccess, int minute) {
		if(minute > 0) {
			minute = minute * -1;
		}
		return Db.queryLong("select count(*) from sys_login_log where session_id=? and is_success=? and create_time >= date_add(now(), interval " + minute + " minute)", sessionId, isSuccess);
	}
	
	
	/**
	 * 查询量
	 * @param ip		ip
	 * @param isSuccess 是否成功（0：失败，1：成功）
	 * @param minute	最后分钟数
	 */
	public long findCountByIp(String ip, int isSuccess, int minute) {
		if(minute > 0) {
			minute = minute * -1;
		}
		return Db.queryLong("select count(*) from sys_login_log where ip=? and is_success=? and create_time >= date_add(now(), interval " + minute + " minute)", ip, isSuccess);
	}
}
