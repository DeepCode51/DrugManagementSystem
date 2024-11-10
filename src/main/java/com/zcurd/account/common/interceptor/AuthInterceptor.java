package com.zcurd.account.common.interceptor;

import com.jfinal.aop.Duang;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.zcurd.account.common.annotation.ClearAuth;
import com.zcurd.account.model.SysUser;
import com.zcurd.account.service.AuthService;
import com.zcurd.account.vo.AuthDataVO;
import com.zcurd.common.util.StringUtil;

/**
 * 权限处理拦截器
 * @author 钟世云 2016.2.5
 */
public class AuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		
		// 处理ClearAuth注解，不需要权限管控
		if(isContainsClearAuth(inv) ) {
			inv.invoke();
			return;
		}
		
		// admin不做权限管控
		SysUser user = c.getSessionAttr("sysUser");
		if("admin".equals(user.getUserName())) {
			inv.invoke();
			return;
		}
		
		String currUrl = AuthService.formatUrl(c.getRequest().getRequestURI()
				.substring(c.getRequest().getContextPath().length()));
		AuthDataVO authData = (AuthDataVO) c.getSession().getAttribute("authData");
		//AuthDataVO authData = Duang.duang(AuthService.class).getAuthData(user.getId().intValue());
		
		// 菜单URL权限
		boolean isAuthURL = authData.getAuthUrl().contains(currUrl);
		if(!isAuthURL) {
			isAuthURL = authData.getAuthBtnUrl().contains(currUrl);
		} 
		if(!isAuthURL) {
			for (String url : authData.getAuthBtnUrl()) {
				if(currUrl.matches(url)) {	// 按钮URL权限，正则表达式匹配
					isAuthURL = true;
					break;
				}
			}
		}
		if(!isAuthURL) {
			c.renderText("No authorization");
			return;
		}
		
		// 需隐藏的按钮
		c.setAttr("noAuthBtn", StringUtil.join(authData.getNoAuthPageBtn().get(currUrl), ","));
		
		// 数据权限
		Integer headId = AuthService.getHeadId(currUrl);
		c.setAttr("authDataRule", authData.getAuthDataRule().get(headId == null ? currUrl : headId.toString()));	// 在线表单，数据权限以headId为key
		
		inv.invoke();
	}
	
	/**
	 * 是否包含ClearAuth注解
	 */
	private boolean isContainsClearAuth(Invocation inv) {
		// 方法注解
		if(inv.getMethod().getAnnotation(ClearAuth.class) != null) {
			return true;
		}
		
		// 类注解
		if(inv.getTarget().getClass().getAnnotation(ClearAuth.class) != null) {
			return true;
		}
		return false;
	}
}
