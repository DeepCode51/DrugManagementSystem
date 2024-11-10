package com.zcurd.common.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class ZcurdHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		// 在线表单管理url处理（/zcurd/{headId}/method 转换为--> /zcurd/method）
		String pre = "/zcurd/";
		if(target.startsWith(pre)) {
			String[] params = target.split("/");
			if(params.length > 3) {
				target = pre + params[3];
				request.setAttribute("headId", params[2]);
				for (int i = 4; i < params.length; i++) {
					target += params[i];
				}
			}
		}
		
		next.handle(target, request, response, isHandled);
	}

}
