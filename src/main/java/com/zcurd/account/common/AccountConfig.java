package com.zcurd.account.common;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.template.Engine;
import com.zcurd.account.common.interceptor.AuthInterceptor;
import com.zcurd.account.common.interceptor.SessionInterceptor;
import com.zcurd.account.controller.LoginController;
import com.zcurd.account.controller.MenuController;
import com.zcurd.account.controller.RoleController;
import com.zcurd.account.controller.SysUserController;
import com.zcurd.online.controller.MainController;

/**
 * 账号模块配置
 * @author 钟世云 2018年3月31日 上午1:48:59
 */
public class AccountConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/login", LoginController.class, "/zcurd/login");
		me.add("/menu", MenuController.class, "/zcurd/menu");
		me.add("/main", MainController.class, "/zcurd");
		me.add("/role", RoleController.class, "/zcurd/role");
		me.add("/user", SysUserController.class, "/zcurd/sysUser");
	}

	@Override
	public void configEngine(Engine me) {
		
	}

	@Override
	public void configPlugin(Plugins me) {
		
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new SessionInViewInterceptor());
		me.add(new SessionInterceptor());
		me.add(new AuthInterceptor());
	}

	@Override
	public void configHandler(Handlers me) {
		
	}

}
