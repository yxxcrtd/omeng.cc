package com.shanjin.bank.config;



import java.util.Timer;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.render.ViewType;
import com.shanjin.bank.routes.SystemRoutes;
import com.shanjin.bank.task.QueryResultTaskService;
import com.shanjin.bank.util.PropUtil;


public class SystemConfig extends JFinalConfig{
	
		private Timer timer = new Timer();
		
		
		public static final Long TASK_TIME_PERIOD = Long.parseLong(PropUtil.getPropUtil("config.properties").getProperty("task.time.period"));
	
	
	    public void configConstant(Constants me) {
		me.setDevMode(true);
		me.setViewType(ViewType.JSP);
		me.setMaxPostSize(10*me.getMaxPostSize());  //默认10M,此处设置为最大100M
		}
	    public void configInterceptor(Interceptors me) { 
		}
		public void configRoute(Routes me) {
		    me.add(new SystemRoutes());
		}
		public void configPlugin(Plugins me) {
			//CodeMsgUtil.configPlugins();
		}
		public void configHandler(Handlers me) {
		    me.add(new ContextPathHandler());
		}	  
		
	    //在系统启动时调用的方法
	    @Override
	    public void afterJFinalStart() {
	    	super.afterJFinalStart();
	        timer.schedule(new QueryResultTaskService(), 0, TASK_TIME_PERIOD);
	    }
	 
	    @Override
	    public void beforeJFinalStop() {
	        super.beforeJFinalStop();
	        timer.cancel();
	    }
}
