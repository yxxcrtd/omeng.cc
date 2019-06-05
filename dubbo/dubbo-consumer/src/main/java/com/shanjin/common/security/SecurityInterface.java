package com.shanjin.common.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求加密接口
 * @author Revoke Yu 2016.5.21
 *
 */
public interface SecurityInterface {
	
		public String  encode(HttpServletRequest request);

}
