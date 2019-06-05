package com.shanjin.common.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 客户端接口请求参数解析工具类
 * @author Huang yulai
 *
 */
public class ClientParamUtil {

	
	public static String getUUID(HttpServletRequest request) {
		String uuid = StringUtil.null2Str(request.getHeader("UUID"));
		if (StringUtil.isNullStr(uuid)) {
			uuid = StringUtil.null2Str(request.getParameter("UUID"));
		}
		if (StringUtil.isNullStr(uuid)) {
			uuid = StringUtil.null2Str(request.getParameter("uuid"));
		}
		return uuid;
	}

	public static String getUserId(HttpServletRequest request) {
		String userId = StringUtil.null2Str(request.getHeader("userId"));
		if (StringUtil.isNullStr(userId)) {
			userId = StringUtil.null2Str(request.getParameter("userId"));
		}
		if (StringUtil.isNullStr(userId)) {
			userId = StringUtil.null2Str(request.getParameter("user_id"));
		}
		return userId;
	}

	public static String getClientType(HttpServletRequest request) {
		String clientType = StringUtil.null2Str(request.getHeader("CLIENT-TYPE"));
		if (StringUtil.isNullStr(clientType)) {
			clientType = StringUtil.null2Str(request.getParameter("CLIENT-TYPE"));
		}
		if (StringUtil.isNullStr(clientType)) {
			clientType = StringUtil.null2Str(request.getHeader("CLIENT_TYPE"));
		}
		if (StringUtil.isNullStr(clientType)) {
			clientType = StringUtil.null2Str(request.getParameter("CLIENT_TYPE"));
		}
		if (StringUtil.isNullStr(clientType)) {
			clientType = StringUtil.null2Str(request.getParameter("clientType"));
		}
		return clientType;
	}

	public static String getUa(HttpServletRequest request) {
		String ua = StringUtil.null2Str(request.getHeader("UA"));
		if (StringUtil.isNullStr(ua)) {
			ua = StringUtil.null2Str(request.getParameter("UA"));
		}
		return ua;
	}

	public static String getVersion(HttpServletRequest request) {
		String version = StringUtil.null2Str(request.getHeader("VERSION"));
		if (StringUtil.isNullStr(version)) {
			version = StringUtil.null2Str(request.getParameter("VERSION"));
		}
		return version;
	}

	public static String getChannel(HttpServletRequest request) {
		String channel = StringUtil.null2Str(request.getHeader("CHANNEL"));
		if (StringUtil.isNullStr(channel)) {
			channel = StringUtil.null2Str(request.getParameter("CHANNEL"));
		}
		return channel;
	}

	public static String getResolution(HttpServletRequest request) {
		String resolution = StringUtil.null2Str(request.getHeader("RESOLUTION"));
		if (StringUtil.isNullStr(resolution)) {
			resolution = StringUtil.null2Str(request.getParameter("RESOLUTION"));
		}
		return resolution;
	}

	public static String getSystem(HttpServletRequest request) {
		String system = StringUtil.null2Str(request.getHeader("SYSTEM"));
		if (StringUtil.isNullStr(system)) {
			system = StringUtil.null2Str(request.getParameter("SYSTEM"));
		}
		return system;
	}

//	public static String getUA(HttpServletRequest request) {
//		String ua = StringUtil.null2Str(request.getHeader("User-Agent"));
//		return ua;
//	}

	
	public static String getModel(HttpServletRequest request){
		String model = StringUtil.null2Str(request.getHeader("MODEL"));
		if (StringUtil.isNullStr(model)) {
			model = StringUtil.null2Str(request.getParameter("MODEL"));
		}
		return model;
	}
	
	public static String getClientFlag(HttpServletRequest request) {
		String clientFlag = StringUtil.null2Str(request.getHeader("CLIENT-FLAG"));
		if (StringUtil.isNullStr(clientFlag)) {
			clientFlag = StringUtil.null2Str(request.getParameter("CLIENT-FLAG"));
		}
		if (StringUtil.isNullStr(clientFlag)) {
			clientFlag = StringUtil.null2Str(request.getHeader("CLIENT_FLAG"));
		}
		if (StringUtil.isNullStr(clientFlag)) {
			clientFlag = StringUtil.null2Str(request.getParameter("CLIENT_FLAG"));
		}
		return clientFlag;
	}
	
	public static String getLongitude(HttpServletRequest request){
		String longitude = StringUtil.null2Str(request.getHeader("LONGITUDE"));
		if (StringUtil.isNullStr(longitude)) {
			longitude = StringUtil.null2Str(request.getParameter("LONGITUDE"));
		}
		return longitude;
	}
	
	public static String getLatitude(HttpServletRequest request){
		String latitude = StringUtil.null2Str(request.getHeader("LATITUDE"));
		if (StringUtil.isNullStr(latitude)) {
			latitude = StringUtil.null2Str(request.getParameter("LATITUDE"));
		}
		return latitude;
	}
	
	public static String getAPIVersion(HttpServletRequest request) {
		String apiVersion = StringUtil.null2Str(request.getHeader("APIVERSION"));
		if (StringUtil.isNullStr(apiVersion)) {
			apiVersion = StringUtil.null2Str(request.getParameter("APIVERSION"));
		}
		return apiVersion;
	}
	
	
	
	public static String getSecurityInfo(HttpServletRequest request) {
		String postFix = StringUtil.null2Str(request.getHeader("POSTFIX"));
		if (StringUtil.isNullStr(postFix)) {
			postFix = StringUtil.null2Str(request.getParameter("POSTFIX"));
		}
		return postFix;
	}

	public static String getTimestamp(HttpServletRequest request) {
		String timestamp = StringUtil.null2Str(request.getHeader("TIME"));
		return timestamp;
	}
	
	
	/**
	 * 获取请求的IP地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if(ip.contains(",")){
			ip = ip.split(",")[0];
		}
		return ip;
	}

	public static String getPhone(HttpServletRequest request) {
		String phone = StringUtil.null2Str(request.getHeader("PHONE"));
		if (StringUtil.isNullStr(phone)) {
			phone = StringUtil.null2Str(request.getHeader("phone"));
		}
		if (StringUtil.isNullStr(phone)) {
			phone = StringUtil.null2Str(request.getParameter("phone"));
		}
		return phone;
	}

}
