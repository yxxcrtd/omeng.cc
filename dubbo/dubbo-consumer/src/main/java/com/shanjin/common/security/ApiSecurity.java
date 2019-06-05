package com.shanjin.common.security;

import com.shanjin.common.util.AESUtil;
import com.shanjin.common.util.ClientParamUtil;
import com.shanjin.common.util.MD5Util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * 接口请求认证加密算法
 * @author John Tang 2016.5.23
 *
 */
public class ApiSecurity implements SecurityInterface {

	@Override
	public String encode(HttpServletRequest request) {
		return genServerSecurityInfo(request);
	}
	
	
	private String genServerSecurityInfo(HttpServletRequest request){
		 return getRequestToken(request);
		
	}
	
	
	private String getRequestToken(HttpServletRequest request) {
		String token = "";
		String clientType = ClientParamUtil.getClientType(request);
		String version = ClientParamUtil.getVersion(request);
		String apiVersion = ClientParamUtil.getAPIVersion(request);
		String timestamp = ClientParamUtil.getTimestamp(request);
		String key = MD5Util.MD5_32(timestamp);
		try {
			token = AESUtil.parseByte2HexStr(AESUtil.encrypt(clientType+"|"+version+"|"+apiVersion+"|"+timestamp,key));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return token;
	}

}
