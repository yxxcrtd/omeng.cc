package com.shanjin.common.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.AESUtil;
import com.shanjin.service.IMyMerchantService;

public class ParamEncryptInterceptor implements HandlerInterceptor {

	@Resource
    private IMyMerchantService merchantService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String encryptedParams = request.getParameter("encryptedParams");
		System.out.println("-------------------encryptedParams: "+encryptedParams);
		String clientId = request.getParameter("clientId");
		String appType = request.getParameter("appType");
		String phone = request.getParameter("phone");
		Map<String, String[]> newParams = new HashMap<String, String[]>();
		newParams.put("clientId", new String[]{clientId});
		newParams.put("appType", new String[]{appType});
		newParams.put("phone", new String[]{phone});
		System.out.println("-------------------newParams: "+newParams.toString());
		String employeeKey = merchantService.getEmployeeKey(phone);
		System.out.println("-------------------newParams: "+employeeKey);
		String paramsJson = "";
		try{
			paramsJson = AESUtil.decrypt(encryptedParams, employeeKey);
			System.out.println("-------------------paramsJson: "+paramsJson);
		} catch(Exception e) {
			return false;
		}
		
		JSONObject params = JSONObject.parseObject(paramsJson);
		for(Entry<String, Object> entry : params.entrySet()) {
			newParams.put(entry.getKey(), new String[]{entry.getValue().toString()});
		}
		
		request = new ParameterRequestWrapper(request, newParams);
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
