package com.shanjin.service.api.impl;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import com.shanjin.service.api.IOrderApiService;

@Service
public class OrderApiServiceFactory implements ApplicationContextAware{
	
	private ApplicationContext context;

	public IOrderApiService getOrderApiService(String appType) {
		String beanName = appType + "OrderApiService";
		IOrderApiService orderApiService = (IOrderApiService)context.getBean(beanName);
		return orderApiService;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}	
}
