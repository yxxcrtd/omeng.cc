package com.shanjin.dubbo.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IUserManageService;
import com.shanjin.service.IUserOrderService;

public class Client {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath:*/**/*.xml" });
		
		context.start();
		
		ICommonService commonService = (ICommonService)context.getBean("commonService");
		System.out.println(commonService.getAllAppInfo());
		
		
		IUserManageService umService = (IUserManageService)context.getBean("userManageService");
		System.out.println(umService.selectMerchantUsers("cbt", 121L, 1));
		
		
		IUserOrderService uoService = (IUserOrderService)context.getBean("userOrderService");
		System.out.println(uoService.getOrderPushType(11l));
		
		
		uoService = (IUserOrderService)context.getBean("cbtUserOrderService");
		System.out.println(uoService.getDetialOrderInfo("cbt", 104L, 4l));
		
		
		uoService = (IUserOrderService)context.getBean("qzyUserOrderService");
		System.out.println(uoService.getDetialOrderInfo("qzy",253L, 1l));
		
	}
}