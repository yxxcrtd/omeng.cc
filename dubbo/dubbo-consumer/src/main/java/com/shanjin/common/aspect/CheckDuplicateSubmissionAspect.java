package com.shanjin.common.aspect;

import java.lang.reflect.Method;

import javax.annotation.Resource;
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

import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.Constant;

/**
 * 切点类
 */
@Aspect
@Component
public class CheckDuplicateSubmissionAspect {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(CheckDuplicateSubmissionAspect.class);

	// Controller层切点
	@Pointcut("@annotation(com.shanjin.common.aspect.CheckDuplicateSubmission)")
	public void controllerAspect() {
	}

	@Resource
	private ICommonCacheService commonCacheService;

	@Around("controllerAspect()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		//如果是开发模式，则去除重复提交功能
		if(Constant.PRESSURETEST){
			return  pjp.proceed();
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		// 获取操作内容
		String type = getControllerMethodType(pjp);
		String arg = getControllerMethodArgs(pjp);
		String values = getParameterValue(arg, request);
		if (commonCacheService.getObject(CacheConstants.CHECK_DUPLICATE_SUBMISSION, type, values) == null) {
			commonCacheService.setObject(type + values, CacheConstants.CHECK_DUPLICATE_SUBMISSION_TIME_OUT, CacheConstants.CHECK_DUPLICATE_SUBMISSION, type, values);
			Object result = pjp.proceed();
			commonCacheService.deleteObject(CacheConstants.CHECK_DUPLICATE_SUBMISSION, type, values);
			return result;
		} else {
			request.getRequestDispatcher("/error/repeatSubmit").forward(request, response);
			return null;
		}
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解z
	 * 
	 * @param joinPoint
	 *            切点
	 * @return 方法描述
	 * @throws Exception
	 */
	public static String getControllerMethodArgs(JoinPoint joinPoint) throws Exception {
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		String description = method.getAnnotation(CheckDuplicateSubmission.class).args();
		return description;
	}

	public static String getControllerMethodType(JoinPoint joinPoint) throws Exception {
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		String description = method.getAnnotation(CheckDuplicateSubmission.class).type();
		return description;
	}

	public static String getParameterValue(String arg, HttpServletRequest request) throws Exception {
		arg=arg.replace(" ", "");
		String[] args = arg.split(",");
		String argsValue = "";
		String value = "";
		for (String i : args) {			
			value = (String)request.getParameter(i);
			if(value!=null){
				argsValue += value + ",";			
			}
		}
		if (argsValue.length() > 0) {
			argsValue = argsValue.substring(0, argsValue.length() - 1);
		}
		return argsValue;
	}
}