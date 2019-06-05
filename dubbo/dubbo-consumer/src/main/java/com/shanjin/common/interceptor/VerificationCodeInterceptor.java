package com.shanjin.common.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.shanjin.cache.CacheConstants;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.ICommonService;

public class VerificationCodeInterceptor implements HandlerInterceptor {

	@Resource
	private ICommonService commonService;
	private Logger log = Logger.getLogger(VerificationCodeInterceptor.class);

	@SuppressWarnings("unchecked")
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3) throws Exception {
		String phone = StringUtil.null2Str(request.getParameter("phone"));
		String verificationCode = StringUtil.null2Str(request.getParameter("verificationCode"));
		commonService.cleanVerificationCodeCache(CacheConstants.VALIDATE_VERIFICATION_CODE_KEY, phone, verificationCode);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3) throws Exception {

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 获取操作内容
		try {
			String phone = StringUtil.null2Str(request.getParameter("phone"));
			String verificationCode = StringUtil.null2Str(request.getParameter("verificationCode"));

			if (phone.equals("") || verificationCode.equals("")) {
				request.getRequestDispatcher("/error/paraError").forward(request, response);
				return false;
			}

			if (commonService.getVerificationCodeCache(CacheConstants.VALIDATE_VERIFICATION_CODE_KEY, phone, verificationCode) != null) {
				request.getRequestDispatcher("/error/error").forward(request, response);
				return false;
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("phone", phone);
			paramMap.put("verificationCode", verificationCode);
			commonService.saveVerificationCodeCache(paramMap, CacheConstants.MERCHANT_PRE_VERIFY_TIMEOUT, CacheConstants.VALIDATE_VERIFICATION_CODE_KEY, phone, verificationCode);
			return true;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}

	}
}
