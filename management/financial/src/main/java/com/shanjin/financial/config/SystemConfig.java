package com.shanjin.financial.config;


import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.render.ViewType;
import com.shanjin.financial.routes.SystemRoutes;
import com.shanjin.financial.util.CommonUtil;
import com.shanjin.financial.util.DBUtil;


public class SystemConfig extends JFinalConfig {


    public void configConstant(Constants me) {
        me.setDevMode(true);
        me.setViewType(ViewType.JSP);
        me.setMaxPostSize(10 * me.getMaxPostSize());  //默认10M,此处设置为最大100M
    }

    public void configInterceptor(Interceptors me) {

    }

    public void configRoute(Routes me) {
        me.add(new SystemRoutes());
    }

    public void configPlugin(Plugins me) {
        DBUtil.configPlugins(me);
        DBUtil.configPlugins_Opay(me); //钱包数据源
    }

    public void configHandler(Handlers me) {
        me.add(new ContextPathHandler());
    }

    //在系统启动时调用的方法
    @Override
    public void afterJFinalStart() {
        CommonUtil.initSystemResourceMap();
    }
}
