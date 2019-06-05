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
import com.shanjin.common.util.MD5Util;
import com.shanjin.service.IMyMerchantService;
import com.shanjin.service.IUserInfoService;
import com.shanjin.service.IValidateService;

/**
 * 验证token拦截器
 * 
 * @author lihuanmin
 *
 */
public class commonValidateTokenInterceptor implements AsyncHandlerInterceptor {

	@Resource
	private IValidateService validateService;

	@Resource
	private IMyMerchantService merchantService;
    @Resource
    private IUserInfoService userService;

	private final Logger logger = Logger.getLogger(MerchantValidateTokenInterceptor.class);

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		boolean success = true;

//		 if(Constant.DEVMODE){//压力测试，暂时去掉token
//		 logger.error("---------------压力测试：暂时去除token功能...");
//		 return true;
//		 }
		String customerType=(String) request.getParameter("customerType");
		String time = (String) request.getParameter("time");
		String clientId = (String) request.getParameter("clientId");
		String phone = (String) request.getParameter("phone");
		String token = (String) request.getParameter("token");
		String appType = (String) request.getParameter("appType");

//		if (logger.isInfoEnabled()) {
//			logger.info("{customerType : " + customerType + "}");
//			logger.info("{time : " + time + "}");
//			logger.info("{clientId : " + clientId + "}");
//			logger.info("{phone : " + phone + "}");
//			logger.info("{token : " + token + "}");
//			logger.info("{appType : " + appType + "}");
//			logger.info("{url : " + request.getRequestURI() + "}");
//		}
		if (StringUtil.isNullOrEmpty(time) || StringUtil.isNullOrEmpty(clientId) || StringUtil.isNullOrEmpty(phone) || StringUtil.isNullOrEmpty(token)) {
			success = false;
		} else {
			String lastTime = validateService.lastValidatedTime(clientId);
			if (time.equals(lastTime)) {
				success = false;
			} else {
				String key ="";
				if("1".equals(customerType)){//商户
					key =merchantService.getEmployeeKey(phone);
				}else if("2".equals(customerType)){//用户
					key =userService.getUserKey(phone, clientId);
				}else{
					key =merchantService.getEmployeeKey(phone);
				}
//				logger.info("{merchantKey : " + key + "}");
				if (StringUtil.isNullOrEmpty(key)) {
					success = false;
				} else {
					String validToken = MD5Util.MD5_32(time + "" + clientId + "" + phone + "" + key);
//					logger.info("{validToken : " + validToken + "}");
					if (!validToken.equals(token.toUpperCase())) {
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
		String appType = (String) request.getParameter("appType");

		if (logger.isInfoEnabled()) {
			logger.info("{time : " + time + "}");
			logger.info("{clientId : " + clientId + "}");
			logger.info("{phone : " + phone + "}");
			logger.info("{token : " + token + "}");
			logger.info("{appType : " + appType + "}");
		}

		if (StringUtil.isNullOrEmpty(time) || StringUtil.isNullOrEmpty(clientId) || StringUtil.isNullOrEmpty(phone) || StringUtil.isNullOrEmpty(token)) {
			success = false;
		} else {
			String lastTime = validateService.lastValidatedTime(clientId);
			if (time.equals(lastTime) && !Constant.DEVMODE) {
				success = false;
			} else {
				String merchantKey = merchantService.getEmployeeKey(phone);
				if (StringUtil.isNullOrEmpty(merchantKey)) {
					success = false;
				} else {
					String validToken = MD5Util.MD5_32(time + "" + clientId + "" + phone + "" + merchantKey);
					if (!validToken.equals(token.toUpperCase())) {
						success = false;
					} else {
						validateService.updateLastValidatedTime(clientId, time);
					}
				}
			}

		}
		if (!success) {
			logger.error("---------------token问题---------------");
			response.getWriter().print(new ResultJSONObject("01", "验证失败，请检查token"));
		}
	}

}
