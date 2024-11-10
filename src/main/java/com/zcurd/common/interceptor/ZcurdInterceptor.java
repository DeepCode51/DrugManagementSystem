package com.zcurd.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.zcurd.common.DbMetaTool;
import com.zcurd.common.ZcurdTool;
import com.zcurd.common.annotation.IncludFunc;
import com.zcurd.common.util.ZcurdKit;
import com.zcurd.online.vo.ZcurdMeta;

/**
 * 在线表单Interceptor
 * @author 钟世云 2020年3月29日 下午2:37:23
 */
public class ZcurdInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		try {
			Controller c = inv.getController();
			Integer headId = Integer.parseInt(c.getAttr("headId"));
			
			// 获取在线表单数据
			ZcurdMeta zcurdMeta = DbMetaTool.getMetaData(headId);
			ZcurdKit.putZcurdMeta(zcurdMeta);
			
			// 检查在线表单包含功能
			IncludFunc includFunc = inv.getMethod().getAnnotation(IncludFunc.class);
			if(!ZcurdTool.isIncludeFunc(includFunc)) {
				inv.getController().renderError(404);
				return;
			}
			
			// 刷新字典SQL
			if(includFunc.value().isNeedFlushDictData(inv.getMethod())) {
				DbMetaTool.flushDictData(zcurdMeta);
			}
			
			inv.invoke();
		} finally {
			ZcurdKit.remove();
		}
	}

}
