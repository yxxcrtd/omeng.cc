 package com.shanjin.common.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtil {

	/** 获得HttpServletRequest对象 */
	public static HttpServletRequest getRequest() {
	    return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}

	/** 获得ServletContext对象 */
	public static ServletContext getServletContext() {
	    return getRequest().getServletContext();
	}

	/** 获得服务器地址的基本路径 */
	public static String getBasePath() {
		HttpServletRequest request = getRequest();
		StringBuilder basePath = new StringBuilder(request.getScheme());
		basePath = basePath.append("://").append(request.getServerName()).append(":").append(
				request.getServerPort()).append(request.getContextPath());
		return basePath.toString();
	}
}
