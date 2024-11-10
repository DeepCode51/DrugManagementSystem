package com.zcurd.account.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.zcurd.common.util.UrlUtil;

public class LoginFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		HttpSession session = request.getSession();
		
		String currUrl = UrlUtil.formatUrl(request.getRequestURI());
		String contextPath = request.getContextPath();
		//排除登陆页面和res目录
		if(!currUrl.endsWith("/login") && !currUrl.endsWith("/login/randomCode") && !currUrl.startsWith(contextPath + "/res")) {
			if(session.getAttribute("sysUser") == null) {
				response.sendRedirect(request.getContextPath() + "/login");//如果session为空表示用户没有登录就重定向到login页面
				return;
			}
			
			//首页跳转到main页面
			if(currUrl.equals(contextPath) || currUrl.equals(contextPath + "/index")) {
				response.sendRedirect(request.getContextPath() + "/main");
				return;
			}
		}
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig chain) throws ServletException {
	}

}
