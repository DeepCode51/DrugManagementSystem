package com.zcurd.account.common.kit;

import javax.servlet.http.HttpSession;

import com.zcurd.account.model.SysUser;

public class ZcurdKit {
	private static ThreadLocal<HttpSession> tlSession = new ThreadLocal<>();
	
	public static void putSession(HttpSession s) {
	     tlSession.set(s);
	  }

	  public static HttpSession getSession() {
	    return tlSession.get();
	  }

	  public static void removeSession() {
	    tlSession.remove();
	  }
	  
	  public static SysUser getSessionUser() {
		 return (SysUser) getSession().getAttribute("sysUser");
	  }
}
