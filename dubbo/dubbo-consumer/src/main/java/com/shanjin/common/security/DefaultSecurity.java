package com.shanjin.common.security;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.MD5Util;

/**
 * 缺省加密算法
 * @author Revoke Yu 2016.5.21
 *
 */
public class DefaultSecurity implements SecurityInterface {

	@Override
	public String encode(HttpServletRequest request) {
		return genServerSecurityInfo(request);
	}
	
	
	private String genServerSecurityInfo(HttpServletRequest request){
		 return getRequestToken(request);
		
	}
	
	
	private String getRequestToken(HttpServletRequest request) {
		String token = "";
		String ip = IPutil.getIpAddr(request);
		String path = request.getServletPath();
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
		token = MD5Util.MD5_32(ip + path + paraString.toString());
		
		return token;
	}


}
