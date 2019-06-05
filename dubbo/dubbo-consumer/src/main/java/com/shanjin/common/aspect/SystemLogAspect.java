package com.shanjin.common.aspect;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.ClientParamUtil;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.ICommonService;

/**
 * 切点类
 */
@Aspect
@Component
public class SystemLogAspect {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(SystemLogAspect.class);

	// Controller层切点
	@Pointcut("@annotation(com.shanjin.common.aspect.SystemControllerLog)")
	public void controllerAspect() {
	}

	@Resource
	private ICommonCacheService commonCacheService;
	@Resource
	private ICommonService commonService;

	/**
	 * 前置通知 用于拦截Controller层记录用户的操作
	 * 
	 * @param joinPoint
	 *            切点
	 */
	@Before("controllerAspect()")
	public void doBefore(JoinPoint joinPoint) {
		if(Constant.PRESSURETEST){
			return;
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		// 获取操作内容
		try {
			 String interfaceName = joinPoint.getSignature().getName();
			 String ip = IPutil.getIpAddr(request);
			 String phone=getPhone(request);
			 long startTime = new Date().getTime();
			 commonCacheService.setObject(startTime, 60, "sysLog_startTime_" +interfaceName,ip,phone);
			String logInfo = getSystemLogInfo(joinPoint, request);
			 commonCacheService.setObject(logInfo, 60, "sysLog_logInfo_" +interfaceName, ip,phone);
//			logger.info("接口信息("+interfaceName+")：" + logInfo );
//			Map<String, Object> isOpenInterfaceLogMap=commonService.getConfigurationInfoByKey("is_open_interfaceLog");
//			if(isOpenInterfaceLogMap!=null && isOpenInterfaceLogMap.get("value").toString().equals("1")){
//				BusinessUtil.writeLog("interface","接口信息("+interfaceName+")：" + logInfo);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterReturning(value = "@annotation(com.shanjin.common.aspect.SystemControllerLog)", argNames = "rtv", returning = "rtv")
	public void doAfter(JoinPoint joinPoint, Object rtv) {

		if(Constant.PRESSURETEST){
			return;
		}
		
		try {
			long endTime = new Date().getTime();
			String returnValue = rtv == null ? "null" : rtv.toString();

			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			String ip = IPutil.getIpAddr(request);
			String phone=getPhone(request);
			String interfaceName = joinPoint.getSignature().getName();
			Object objStartTime = commonCacheService.getObject("sysLog_startTime_" + interfaceName,ip, phone);
			long startTime = Long.parseLong(objStartTime == null ? "0" : objStartTime.toString());
//			logger.info("返回信息("+interfaceName+")：" + returnValue);
//			logger.info("执行时间("+interfaceName+")：" + (endTime - startTime) + "ms"+ System.getProperty("line.separator"));
			String logInfo = commonCacheService.getObject("sysLog_logInfo_" + interfaceName,ip,phone)+"";
			
			BusinessUtil.writeLog("interface",
					"接口信息("+interfaceName+")：" + logInfo+"\n"+
					"返回信息("+interfaceName+")：" + returnValue+"\n"+
					"执行时间("+interfaceName+")：" + (endTime - startTime) + "ms\n");
		} catch (Exception e) {
			e.printStackTrace();
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
	public String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		String description = method.getAnnotation(SystemControllerLog.class).description();
		return description;
	}

	public String getSystemLogInfo(JoinPoint joinPoint, HttpServletRequest request) throws Exception {
		String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String uuid = stringProcess(ClientParamUtil.getUUID(request));
		String ip = IPutil.getIpAddr(request);
		String version = stringProcess(ClientParamUtil.getVersion(request));
		String model = stringProcess(ClientParamUtil.getModel(request));
		String requestMethod = request.getMethod();
		String controllerPath = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
		String methodDescription = getControllerMethodDescription(joinPoint);
		StringBuffer paraString = new StringBuffer("[ ");
		Map<String, String[]> paramMap = request.getParameterMap();
		Set<Entry<String, String[]>> set = paramMap.entrySet();
		Iterator<Entry<String, String[]>> it = set.iterator();
		while (it.hasNext()) {
			Entry<String, String[]> entry = it.next();
			paraString.append(entry.getKey() + ":");
			for (String i : entry.getValue()) {
				paraString.append(i);
			}
			paraString.append("; ");
		}
		paraString.append("]");
		String paramNames = paraString.toString();
		String phone=getPhone(request);
		String logInfo = nowTime + "\t"+uuid+ "\t"+version+ "\t"+model+ "\t" +phone+ "\t" + ip + "\t" + 
				requestMethod + "\t" + controllerPath + "\t" + paramNames + "\t" + methodDescription;
		return logInfo;
	}
	public static String getPhone(HttpServletRequest request) {
		String phone = StringUtil.null2Str(request.getHeader("phone"));
		if (StringUtil.isNullStr(phone)) {
			phone = StringUtil.null2Str(request.getParameter("phone"));
		}
		return phone;
	}
	public String stringProcess(String s) {
		if (s != null) {
			s = s.replaceAll("\\|", " ");
			return s;
		}
		return "";
	}
}