package com.shanjin.push.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.shanjin.cache.CacheConstants;

import com.shanjin.cache.service.impl.CommonCacheServiceImpl;



/**
 * 推送配置（安卓个推配置和苹果证书配置）
 * @author Huang yulai
 *
 */
public class PushConfig {

	/*** 个推配置信息  ***/
	public static String host = "http://sdk.open.api.igexin.com/apiex.htm";
	
	public static String appkey = "u9y0RZChBO8aijX3j99oN9";
	
	public static String appSecret = "wPZzEUR8ci9Mx3kSU8FQk";
	
	public static String masterSecret = "GCgnSk55jnAlikRk73f5gA";
	
	public static String appId = "MOqAK6IGFF69Qn7wr7Oge4";
	
	/*** ios推送配置信息  ***/
	
	//public static String iphoneCertPath = "D:/aps_dev_merchant.p12";
	public static String iphoneCertPath = "/usr/local/aps_cert.p12";
	
	public static String iphoneCertPassword = "123456";
	
	public static boolean isProduction = true;
	
	public static String getConfigValue(String key){
//		PushServiceImpl push=new PushServiceImpl();
//		Map<String, Object> map=push.getConfigurationInfoByKey(key); 
//		
//		if(map==null){
//			return "";
//		}
		String msg="";//StringUtil.null2Str(map.get("config_key"));
		
		return msg;
	}
	public static Map<String,Object> getConfigurationInfoByKey(String key){		
		CommonCacheServiceImpl commonCacheService=new CommonCacheServiceImpl();			
		// 读取配置信息缓存
		List<Map<String, Object>> listConfigurationInfo = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.CONFIG_KEY);
		if (listConfigurationInfo == null) {// 如果没有缓存配置信息则读取数据库
			return null;
		}
		for (Map<String, Object> map : listConfigurationInfo) {
			String configKey = map.get("config_key") == null ? "" : map.get("config_key").toString();
			if (key.equals(configKey)) {
				return map;
			}
		}
		return null;
	}
	
	public static List<String> getConfigKeyList(){
		List<String> list=new ArrayList<String>();
		list.add("pushType-1");
		list.add("pushType-2");
		list.add("pushType-3");
		list.add("pushType-4");
		list.add("pushType-5");
		list.add("pushType-6");
		list.add("pushType-7");
		list.add("pushType-8");
		list.add("pushType-9");
		list.add("pushType-10");
		list.add("push-appId");
		list.add("push-appkey");
		list.add("push-host");
		list.add("push-masterSecret");
		list.add("push-appSecret");
		list.add("push-iphoneCertPath");
		list.add("push-iphoneCertPassword");		
		return list;
	}
}
