package com.shanjin.common.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hazelcast.util.StringUtil;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.MD5Util;
import com.shanjin.service.IMyMerchantService;
import com.shanjin.service.IValidateService;

/**
 * 验证token拦截器
 * 
 * @author lihuanmin
 *
 */
public class MerchantValidateTokenInterceptor implements AsyncHandlerInterceptor {

	@Resource
	private IValidateService validateService;

	@Resource
	private IMyMerchantService merchantService;

	private final Logger logger = Logger.getLogger(MerchantValidateTokenInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		boolean success = true;

		if(Constant.PRESSURETEST){
			return true;
		}
		String time = (String) request.getParameter("time");
		String clientId = (String) request.getParameter("clientId");
		String phone = (String) request.getParameter("phone");
		String token = (String) request.getParameter("token");
		
		if (StringUtil.isNullOrEmpty(time) || StringUtil.isNullOrEmpty(clientId) || StringUtil.isNullOrEmpty(phone) || StringUtil.isNullOrEmpty(token)) {
			success = false;
		} else {
			String lastTime = validateService.lastValidatedTime(clientId);
			if (time.equals(lastTime)) {
				String param="time="+time+",phone="+phone+",clientId="+clientId+",token="+token;
				BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
				success = false;
			} else {
				String merchantKey = merchantService.getEmployeeKey(phone);
				if (StringUtil.isNullOrEmpty(merchantKey)) {
					String param="time="+time+",merchantKey="+merchantKey+",phone="+phone+",clientId="+clientId+",token="+token;
					BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
					success = false;
				} else {
					String validToken = MD5Util.MD5_32(time + "" + clientId + "" + phone + "" + merchantKey);
					if (!validToken.equals(token.toUpperCase())) {
						String param="time="+time+",merchantKey="+merchantKey+",phone="+phone+",clientId="+clientId+",token="+token+",validToken="+validToken;
						BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
						success = false;
					} else {
						validateService.updateLastValidatedTime(clientId, time);
					}
				}
			}

		}
		if (!success) {
			logger.error("---------------token问题---------------");
			if (request.getHeader("x-requested-with") != null) { // 暂时用这个来判断是否为ajax请求
				response.getWriter().print(new ResultJSONObject("01", "令牌失效，为了你的安全请退出重新登录"));
			} else {
				request.getRequestDispatcher("/error/vaildateFailed").forward(request, response);
			}

		}
		return success;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		boolean success = true;
		String time = (String) request.getParameter("time");
		String clientId = (String) request.getParameter("clientId");
		String phone = (String) request.getParameter("phone");
		String token = (String) request.getParameter("token");

		if (StringUtil.isNullOrEmpty(time) || StringUtil.isNullOrEmpty(clientId) || StringUtil.isNullOrEmpty(phone) || StringUtil.isNullOrEmpty(token)) {
			String param="time="+time+",phone="+phone+",clientId="+clientId+",token="+token;
			BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
			success = false;
		} else {
			String lastTime = validateService.lastValidatedTime(clientId);
			if (time.equals(lastTime)) {
				String param="time="+time+",lastTime="+lastTime+",phone="+phone+",clientId="+clientId+",token="+token;
				BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
				success = false;
			} else {
				String merchantKey = merchantService.getEmployeeKey(phone);
				if (StringUtil.isNullOrEmpty(merchantKey)) {
					String param="time="+time+",merchantKey="+merchantKey+",phone="+phone+",clientId="+clientId+",token="+token;
					BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
					success = false;
				} else {
					String validToken = MD5Util.MD5_32(time + "" + clientId + "" + phone + "" + merchantKey);
					if (!validToken.equals(token.toUpperCase())) {
						String param="time="+time+",merchantKey="+merchantKey+",phone="+phone+",clientId="+clientId+",token="+token+",validToken="+validToken;
						BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
						success = false;
					} else {
						validateService.updateLastValidatedTime(clientId, time);
					}
				}
			}

		}
		if (!success) {
			logger.error("---------------token问题---------------");
			response.getWriter().print(new ResultJSONObject("01", "你的登录已超时，请重新登录"));
		}
	}
	
	public static void main(String[] args) {
		String token="53938F048587408BD60B067CB003CF9A";
		String time="1465031467851";
		String clientId="6080B31A05D1AA95C2C7B1A8984ED0D1";
		String phone="18250338350";
		String merchantKey="0E74FA981F0C29358556E417E124B105";
		String validToken = MD5Util.MD5_32(time + "" + clientId + "" + phone + "" + merchantKey);
		
		System.out.println(token);
		System.out.println(validToken);
		
	}

}
