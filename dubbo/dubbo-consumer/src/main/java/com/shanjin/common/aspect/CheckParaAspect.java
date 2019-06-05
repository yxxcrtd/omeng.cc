package com.shanjin.common.aspect;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.StringUtil;

/**
 * 切点类
 */
@Aspect
@Component
public class CheckParaAspect {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(CheckParaAspect.class);

	// Controller层切点
	@Pointcut("@annotation(com.shanjin.common.aspect.CheckPara)")
	public void controllerAspect() {
	}

	@Around("controllerAspect()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		// 获取操作内容
		String[] args = getControllerMethodArgs(pjp);
		Map<String, String[]> values = getParameterValue(request);
		if (values != null) {
			boolean checkRight = true;
			String errorArgs = "";
			for (String arg : args) {
				if (!values.containsKey(arg)) {
					errorArgs += arg + ",";
					checkRight = false;
				} else if (values.get(arg) == null) {
					errorArgs += arg + ",";
					checkRight = false;
				} else if (StringUtil.isNullStr(values.get(arg)[0])) {
					errorArgs += arg + ",";
					checkRight = false;
				}
			}
			if (!checkRight) {
				if (errorArgs.length() > 0) {
					errorArgs = errorArgs.substring(0, errorArgs.length() - 1);
				}
				logger.info("参数错误："+errorArgs);
				JSONObject jsonObject = new ResultJSONObject("01", "paramError：令牌失效，请重新登录");
				return jsonObject;
			}
			Object result = pjp.proceed();
			return result;
		} else {
			JSONObject jsonObject = new ResultJSONObject("01", "paramError：令牌失效，请重新登录");
			return jsonObject;
		}
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 * 
	 * @param joinPoint
	 *            切点
	 * @return 方法描述
	 * @throws Exception
	 */
	public static String[] getControllerMethodArgs(JoinPoint joinPoint) throws Exception {
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		String description = method.getAnnotation(CheckPara.class).args();
		return description.split(",");
	}

	public static Map<String, String[]> getParameterValue(HttpServletRequest request) throws Exception {
		return request.getParameterMap();
	}
}