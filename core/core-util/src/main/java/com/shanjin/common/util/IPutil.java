package com.shanjin.common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;

public class IPutil {
	
	public final static String WHILELOCALIP ="127.0.0.1";
	
	public static String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static JSONObject getIpLocationByBaidu(String ip) {
		JSONObject jsonobject = new ResultJSONObject();
		String param = "type=baidu&ip=" + ip;
		try {
			String resultString = HttpRequest.sendGet(Constant.WEB_PROXY_URL + "/ipService", param);
			jsonobject = JSONObject.parseObject(resultString);
		} catch (Exception e) {
			return null;
		}
		return jsonobject;
	}

	public static JSONObject getIpLocationBySina(String ip) {
		JSONObject jsonobject = new ResultJSONObject();
		String param = "type=sina&ip=" + ip;
		try {
			String resultString = HttpRequest.sendGet(Constant.WEB_PROXY_URL + "/ipService", param);
			jsonobject = JSONObject.parseObject(resultString);
			if (!jsonobject.containsKey("province") && !jsonobject.containsKey("city")) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		return jsonobject;
	}
	
	public static String getLocalIpAddr(){
		String ipAddr ="";
		try {
			Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						if(!WHILELOCALIP.equals(ip.getHostAddress())){
							ipAddr =  ip.getHostAddress();
							return ipAddr;
						}
					}
				}
			}
		} catch (SocketException e) {
			return "";
		}
		return "";
	}

	public static void main(String[] args) throws Exception {
		
		System.out.println(IPutil.getLocalIpAddr());
		/*
		String city = "";
		String province = "";
		String IP = "60.166.231.13";
		// 根据IP获得城市名称
		JSONObject jsonObjectIp = IPutil.getIpLocationBySina(IP);
		if (jsonObjectIp != null) {
			System.out.println("来自于新浪");
			province = (String) jsonObjectIp.get("province");
			city = (String) jsonObjectIp.get("city");
		} else {
			jsonObjectIp = IPutil.getIpLocationByBaidu(IP);
			if (jsonObjectIp != null) {
				System.out.println("来自于百度");
				JSONObject content = ((JSONObject) jsonObjectIp.get("content"));
				if (content != null) {
					JSONObject addressDetail = ((JSONObject) content.get("address_detail"));
					if (addressDetail != null) {
						province = (String) addressDetail.get("province");
						city = (String) addressDetail.get("city");
					}
				}
			}
			if (jsonObjectIp != null) {
				if (jsonObjectIp.get("address") != null) {
					String address = (String) jsonObjectIp.get("address");
					if (address != null && address.length() > 0) {
						String[] addressDetail = address.split("\\|");
						if (addressDetail != null) {
							System.out.println(addressDetail[1]);
							System.out.println(addressDetail[2]);
						}
					}
				}
			}
		}

		System.out.println(province);
		System.out.println(city);

	*/}
}
