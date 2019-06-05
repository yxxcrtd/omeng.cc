package com.shanjin.log.analysis.config;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;
import com.shanjin.log.analysis.Constant;

public class SystemConfig extends JFinalConfig{
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
		me.setViewType(ViewType.JSP);
	}

	@Override
	public void configRoute(Routes me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configPlugin(Plugins me) {
		Properties dbpProp = loadPropertyFile("jdbc.properties");
		Constant.DB.DRIVERCLASS = dbpProp.getProperty("driverClass");
		Constant.DB.URL = dbpProp.getProperty("jdbcUrl");
		Constant.DB.USERNAME= dbpProp.getProperty("user");
		Constant.DB.PASSWORD = dbpProp.getProperty("password");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(dbpProp);
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		arp.setShowSql(false);
		me.add(arp);
		
		Properties config = loadPropertyFile("config.properties"); 
		Constant.VISIT_LOG_DIRS = config.getProperty("VISIT_LOG_DIRS").split(",");
		System.out.println("日志处理服务器启动成功！");
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configHandler(Handlers me) {
		// TODO Auto-generated method stub
		
	}

}
