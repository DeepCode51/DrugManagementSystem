package com.zcurd.account.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.zcurd.account.common.kit.ZcurdKit;

public class SessionInterceptor implements Interceptor {
	
	public void intercept(Invocation inv) {
		ZcurdKit.putSession(inv.getController().getSession(true));
		try {
			inv.invoke();
		} finally {
			ZcurdKit.removeSession();
		}
	}
}