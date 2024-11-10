package com.zcurd.account.controller;

import java.util.Date;

import com.jfinal.aop.Duang;
import com.zcurd.account.common.annotation.ClearAuth;
import com.zcurd.account.model.SysLoginLog;
import com.zcurd.account.model.SysMenu;
import com.zcurd.account.model.SysUser;
import com.zcurd.account.service.AuthService;
import com.zcurd.account.service.LoginService;
import com.zcurd.common.base.BaseController;
import com.zcurd.common.util.PasswordUtil;
import com.zcurd.common.util.StringUtil;

/**
 * 登陆
 * @author 钟世云 2016.2.5
 */
@ClearAuth
public class LoginController extends BaseController {
	AuthService authService = enhance(AuthService.class);
	LoginService loginService = enhance(LoginService.class);
	
	/**
	 * 登陆页面
	 */
	public void index() {
		setAttr("isShowRandomCode", isShowRandomCode());
		render("login.html");
	}
	
	/**
	 * 登陆
	 */
	public void login() {
		String userName = getPara("user_name", "").trim();
		String remark = "登陆成功";
		
		// 登陆日志
		SysLoginLog log = new SysLoginLog().setUsername(userName)
				.setSessionId(getSession().getId()).setIp(getRemoteAddress()).setCreateTime(new Date());
		
		// 验证验证码
		boolean isShowRandomCode = isShowRandomCode();
		if(isShowRandomCode && (StringUtil.isEmpty(getPara("randomCode")) || !validateCaptcha("randomCode"))) {
			remark = "验证码错误！";
			log.setIsSuccess(0).setRemark(remark).save();
			renderFailed(remark, isShowRandomCode);
			return;
		}
		
		SysUser user = SysUser.dao.findByUserNameAndPassword(userName, PasswordUtil.encodePassword(getPara("password")));
		if(user != null) {
			setSessionAttr("sysUser", user);
			setSessionAttr("authData", authService.getAuthData(getSessionUser().getId()));
			addOpLog("登陆系统");
			log.setIsSuccess(1).setRemark(remark).save();
			renderSuccess();
		}else {
			remark = "用户名或密码错误！";
			log.setIsSuccess(0).setRemark(remark).save();
			renderFailed(remark, isShowRandomCode);
		}
	}
	
	/**
	 * 获得用户菜单
	 */
	public void getMenu() {
		// admin用户拥有所有页面
		if("admin".equals(getSessionUser().get("user_name"))) {
			renderJson(SysMenu.dao.findAll());
		}else {
			renderJson(authService.getAuthMenuForTree(SysUser.dao.findById(getSessionUser().getId())));
		}
	}
	
	/**
	 * 退出登陆
	 */
	public void logout() {
		addOpLog("退出系统");
		removeSessionAttr("sysUser");
		redirect("/login");
	}
	
	/**
	 * 图形验证码
	 */
	public void randomCode() {
		renderCaptcha();
	}
	
	/**
	 * 是否显示图形验证码
	 */
	private boolean isShowRandomCode() {
		LoginService loginService = Duang.duang(LoginService.class);
		return loginService.isShowRandomCode(getSession().getId(), getRemoteAddress());
	}
}
