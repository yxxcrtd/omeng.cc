package com.shanjin.common.security;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密算法工厂
 * @author Revoke 2016.5.21
 *
 */
public class SecurityFactoy {
	  
	private static SecurityFactoy self =new SecurityFactoy();
	
	private Map<String,SecurityInterface> securityMap= new HashMap<String,SecurityInterface>();
	 
	//初始化支持的加密算法
	private SecurityFactoy(){
		securityMap.put("1.0", new ApiSecurity());
	}
	  
	//获取特定版本对应的加密算法
    public static SecurityInterface getSecurity(String version){
    		return  self.securityMap.get(version);
    }
    
    //判断改版本是否存在加密算法
    public static boolean isSupportVersion(String version){
    	   return  self.securityMap.containsKey(version);
    }
    
}
