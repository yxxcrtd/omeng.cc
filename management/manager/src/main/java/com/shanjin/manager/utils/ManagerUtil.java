package com.shanjin.manager.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 后台访问的公共工具类
 * @author Huang yulai
 *
 */
public class ManagerUtil {

	/**
	 * 获取请求的IP地址
	 * @param request
	 * @return
	 */
    public static String getIpAddr(HttpServletRequest request) {  
        String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
		if(ip.contains(",")){
			ip = ip.split(",")[0];
		}
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    }
    
    public static  HttpSession getSession(HttpServletRequest request){
		HttpSession session = request.getSession();
		return session;
    }
}
