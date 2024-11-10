package com.zcurd.common;

import com.busi.controller.ClawBookUrlController;
import com.busi.controller.StockHistoryLogController;
import com.busi.model.ClawBookUrl;
import com.busi.model.StockHistoryLog;
import com.jfinal.aop.Duang;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.zcurd.account.common.AccountConfig;
import com.zcurd.common.handler.ZcurdHandler;
import com.zcurd.common.interceptor.ErrorInterceptor;
import com.zcurd.online.controller.CommonController;
import com.zcurd.online.controller.TaskBaseController;
import com.zcurd.online.controller.ZcurdController;
import com.zcurd.online.controller.ZcurdHeadController;
import com.zcurd.online.service.TaskService;

import freemarker.template.TemplateModelException;

/**
 * API引导式配置
 */
public class ZcurdConfig extends JFinalConfig {
	private AccountConfig accountConfig = new AccountConfig();
	private static Constants jfinalConstants;
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		accountConfig.configConstant(me);
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("a_little_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setViewType(ViewType.FREE_MARKER);
		jfinalConstants = me;
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		accountConfig.configRoute(me);
		me.add("/zcurd", ZcurdController.class, "/zcurd/zcurd");
		me.add("/zcurdHead", ZcurdHeadController.class, "/zcurd");
		me.add("/common", CommonController.class, "/zcurd");
		me.add("/task", TaskBaseController.class, "/zcurd/taskBase");
		
		me.add("/stockHistoryLog", StockHistoryLogController.class, "/busi/stockHistoryLog");
		me.add("/clawBookUrl", ClawBookUrlController.class, "/busi/clawBookUrl");
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		accountConfig.configPlugin(me);
		// 配置C3p0数据库连接池插件
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("base_jdbcUrl"), PropKit.get("base_user"), PropKit.get("base_password").trim());
		me.add(c3p0Plugin);
		
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin("zcurd_base", c3p0Plugin);
		arp.setShowSql(true);
		me.add(arp);
		// 账号权限model
		com.zcurd.account.model._MappingKit.mapping(arp);
		// 在线表单model
		com.zcurd.online.model._MappingKit.mapping(arp);
		
		//业务数据库
		C3p0Plugin c3p0PluginAir = new C3p0Plugin(PropKit.get("busi_jdbcUrl"), PropKit.get("busi_user"), PropKit.get("busi_password").trim());
		me.add(c3p0PluginAir);
		ActiveRecordPlugin arpAir = new ActiveRecordPlugin("zcurd_busi", c3p0PluginAir);
		arpAir.setShowSql(true);
		
		arpAir.addMapping("stock_history_log", StockHistoryLog.class);
		arpAir.addMapping("claw_book_url", ClawBookUrl.class);
		
		me.add(arpAir);
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		accountConfig.configInterceptor(me);
		me.add(new ErrorInterceptor());
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		accountConfig.configHandler(me);
		me.add(new ZcurdHandler());
		
	}
	
	@Override
	public void afterJFinalStart() {
		accountConfig.afterJFinalStart();
		try {
			FreeMarkerRender.getConfiguration().setSharedVariable("basePath", JFinal.me().getContextPath());
			FreeMarkerRender.getConfiguration().setSharedVariable("zcurd", new ZcurdTool());
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		
		//定时任务
		TaskService taskService = Duang.duang(TaskService.class);
		taskService.startAll();
	}
	
	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 8080, "/", 5);
	}

	@Override
	public void configEngine(Engine me) {
		accountConfig.configEngine(me);
	}
	
	public static boolean getDevModel() {
		return jfinalConstants.getDevMode();
	}
}
