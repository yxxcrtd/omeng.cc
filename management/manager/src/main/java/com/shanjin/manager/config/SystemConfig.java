package com.shanjin.manager.config;


import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.spring.IocInterceptor;
import com.jfinal.render.ViewType;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.intercepter.ClientLogInterceptor;
import com.shanjin.manager.intercepter.OperateLogInterceptor;
import com.shanjin.manager.intercepter.RequestCheckInterceptor;
import com.shanjin.manager.routes.ContextPathHandler;
import com.shanjin.manager.routes.SystemRoutes;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.DBUtil;
import com.shanjin.manager.utils.MqUtil;
import com.shanjin.manager.utils.StringUtil;

public class SystemConfig extends JFinalConfig{
	
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	
	    public void configConstant(Constants me) {
		me.setDevMode(true);
		me.setViewType(ViewType.JSP);
		me.setFreeMarkerViewExtension("ftl");
		me.setMaxPostSize(10*me.getMaxPostSize());  //默认10M,此处设置为最大100M
		}
		public void configInterceptor(Interceptors me) { 
			 me.add(new RequestCheckInterceptor());
			 me.add(new OperateLogInterceptor()); 
			 me.add(new IocInterceptor());
			 me.add(new ClientLogInterceptor()); 
			// me.add(new RepeatSubmitInterceptor());
		}
		public void configRoute(Routes me) {
		    me.add(new SystemRoutes());
		}
		public void configPlugin(Plugins me) {
//			DBUtil.configSqlInXmlPlugins(me);
			DBUtil.configPlugins(me);
			DBUtil.configStatisticPlugins(me);
			Properties config=loadPropertyFile("config.properties");
			Constant.config = config;
			Constant.databaseConfig = loadPropertyFile("database.properties");
			// 邮件配置
			Constant.mailMap.put("mailTitle", config.get("mail.title"));
			Constant.mailMap.put("mailContent", config.get("mail.content"));
			Constant.mailMap.put("mailFrom", config.get("mail.from"));
			Constant.mailMap.put("mailTos", config.get("mail.tos"));
			Constant.mailMap.put("userName", config.get("mail.username"));
			Constant.mailMap.put("mailPwd", config.get("mail.password"));
			Constant.mailMap.put("mailServer", config.get("mail.server"));
			Constant.mailMap.put("mailPort", config.get("mail.port"));
			Constant.mailMap.put("mailSsl", config.get("mail.ssl"));
			// ftp 文件配置
			Constant.HTTP.port = StringUtil.null2Str(config.get("FTP.SERVER"));
			Constant.FTPConfig.ADDR = StringUtil.null2Str(config.get("FTPConfig.ADDR"));
			Constant.FTPConfig.PORT = StringUtil.nullToInteger(config.get("FTPConfig.PORT"));
			Constant.FTPConfig.USERNAME = StringUtil.null2Str(config.get("FTPConfig.USERNAME"));
			Constant.FTPConfig.PASSWORD = StringUtil.null2Str(config.get("FTPConfig.PASSWORD"));
			Constant.FTP_MODE_OSS = StringUtil.null2Str(config.get("FTP.MODE.OSS")); // 文件上传模式
			Constant.DEVMODE = StringUtil.nullToBoolean(config.get("DEVMODE")); // 文件上传模式
			
			String searchUrl = StringUtil.null2Str(config.get("web.search.url"));
			if(!StringUtil.isNullStr(searchUrl)){
				Constant.WEB_SERACH_URL = searchUrl;
			}
			commonCacheService.deleteObject("sessionMap");
		}
		public void configHandler(Handlers me) {
		    me.add(new ContextPathHandler());
		}	  
		
	    //在系统启动时调用的方法
	    @Override
	    public void afterJFinalStart() {
	        // TODO Auto-generated method stub
	        //super.afterJFinalStart();
	        CommonUtil.initSystemResourceMap();
	        CommonUtil.initServiceType();
	        CommonUtil.initServiceTypeById();
	        CommonUtil.initAppInfoList();
			CommonUtil.flushImageCache(CacheConstants.IMAGE_CACHE.SHOW_ICON,false);
			CommonUtil.flushImageCache(CacheConstants.IMAGE_CACHE.ORDER_ICON,false);
			CommonUtil.initIpCityCache();
			//定时任务
			//TimerUtil.executeTimer();
	        //CommonUtil.handleDbData();
			
			ApplicationContext ac = new ClassPathXmlApplicationContext("mq-beans.xml");
			MqUtil.ctx = ac;
	    }
}
