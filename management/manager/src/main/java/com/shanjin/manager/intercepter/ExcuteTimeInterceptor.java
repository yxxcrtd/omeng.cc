package com.shanjin.manager.intercepter;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.util.ClientParamUtil;
import com.shanjin.manager.utils.StringUtil;


public class ExcuteTimeInterceptor implements HandlerInterceptor{
	@Resource
	private ICommonCacheService commonCacheService;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handle, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handle, ModelAndView modelAndView) throws Exception {
		try {
			long endTime = new Date().getTime();
			String ip = ClientParamUtil.getIpAddr(request);
			String interfaceName = request.getRequestURI();
			Object objStartTime = commonCacheService.getObject("manInterface" + interfaceName, ip);
			long startTime = StringUtil.nullToLong(objStartTime);
			System.out.println("执行时间：" + (endTime - startTime) + "ms\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
		// 获取操作内容
		try {
			 String interfaceName = request.getRequestURI();
		     System.out.println("接口信息：" + interfaceName);
			 String ip = ClientParamUtil.getIpAddr(request);
			 long startTime = new Date().getTime();
			 commonCacheService.setObject(startTime, 60, "manInterface" +interfaceName, ip);
			//logger.info("接口信息("+interfaceName+")：" + logInfo );
//			BusinessUtil.writeInterfaceLog("接口信息("+interfaceName+")：" + logInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}



}
