package com.shanjin.controller;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.shanjin.service.ICustomOrderService;
import com.shanjin.service.IMerchantOrderManageService;
import com.shanjin.service.IMyIncomeService;
import com.shanjin.service.IMyMerchantService;
import com.shanjin.service.IUserInfoService;
import com.shanjin.service.IUserOrderService;

public class AppOrderSvrManager implements ApplicationContextAware {
	private static final Log logger = LogFactory.getLog(AppOrderSvrManager.class); 
	
	private static ApplicationContext appCtx; 
	
	private static final AppOrderSvrManager  srvMgr =new AppOrderSvrManager();
	

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
			appCtx =ctx;
	}
	
	
	public  static  AppOrderSvrManager getInstance(){
		
		return srvMgr;
	}
	
	
	private AppOrderSvrManager(){
	}
	

	public static ICustomOrderService getNewOrderService() {
		
		 return  (ICustomOrderService) appCtx.getBean("customOrderService");
	}
	

	public static IUserInfoService getUserService() {
		
		 return  (IUserInfoService) appCtx.getBean("userService");
	}
	
	public static IMyMerchantService getMyMerchantService() {
		
		 return  (IMyMerchantService) appCtx.getBean("myMerchantService");
	}
	
	public static IMyIncomeService getMyIncomeService() {
		
		 return  (IMyIncomeService) appCtx.getBean("myIncomeService");
	}

	public static IUserOrderService getOrderServiceByAppType(String appType) {
		 Properties properties =  (Properties) appCtx.getBean("appAndBeanIdMap");
		 
		 if (!properties.containsKey(appType)){
			 logger.warn("该服务类型不支持，请检查配置文件是否设置!! appType="+appType);
		 }
		 return   (IUserOrderService) appCtx.getBean(properties.getProperty(appType));
	}
	
	
	public static IMerchantOrderManageService getMerchantOrderServiceByAppType(String appType) {
		 Properties properties =  (Properties) appCtx.getBean("appAndBeanIdMap");
		 
		 if (!properties.containsKey(appType+"Merchant")){
			 logger.warn("该服务类型不支持，请检查配置文件是否设置!! appType="+appType);
		 }
		 return   (IMerchantOrderManageService) appCtx.getBean(properties.getProperty(appType+"Merchant"));
	}

}
