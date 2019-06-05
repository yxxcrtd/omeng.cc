package com.shanjin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IPushDao;
import com.shanjin.service.ICommonService;
import com.shanjin.service.impl.PushServiceImpl;


/**
 * 推送配置（安卓个推配置和苹果证书配置）
 * @author Huang yulai
 *
 */
public class PushConfig {

	/*** 个推配置信息  ***/
	public static String host = "http://sdk.open.api.igexin.com/apiex.htm";
	
	public static String appkey = "lDnQTlbOVt9QYaOSBzUjl5";
	
	public static String appSecret = "ioFt9wEjQo5WAqad53kZf5";
	
	public static String masterSecret = "JWjFiCC0FFAkS9pjYAsbo3";
	
	public static String appId = "sGYXyAlzdI9DWk8XCnZDM2";
	
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
		list.add("pushType-11");
		list.add("pushType-12");
		list.add("pushType-13");
		list.add("pushType-14");
		list.add("pushType-15");
		list.add("pushType-16");
		list.add("pushType-20");
		list.add("pushType-21");
		list.add("push-appId");
		list.add("push-appkey");
		list.add("push-host");
		list.add("push-masterSecret");
		list.add("push-appSecret");
		list.add("push-iphoneCertPath");
		list.add("push-iphoneCertPassword");	
		list.add("is_open_iosOrderPush");	
		list.add("push_size");	
		list.add("is_open_brushOrder");	
		list.add("is_open_androidOrderPush");	
		list.add("rangePush_limit");	
		list.add("cityPush_limit");	
		list.add("push_notLogin_days");	
		list.add("is_open_search_pushOrder");	
		list.add("is_open_mqPush");
		list.add("order_sms_content_link");
		list.add("total_sendSms_count");
		list.add("sendorderSms_time");
		return list;
	}
	
	
	
	public static List<String> getConfigKeyList(String appType){
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
		list.add("pushType-11");
		list.add("pushType-12");
		list.add("pushType-13");
		list.add("pushType-14");
		list.add("pushType-15");
		list.add("pushType-16");
		list.add("pushType-20");
		list.add("pushType-21");
		list.add("push-appId_"+appType);
		list.add("push-appkey_"+appType);
		list.add("push-host");
		list.add("push-masterSecret_"+appType);
		list.add("push-appSecret_"+appType);
		list.add("push-iphoneCertPath");
		list.add("push-iphoneCertPassword");
		list.add("push-iphoneCertPath_"+appType);
		list.add("push-iphoneCertPassword_"+appType);	
		list.add("is_open_iosOrderPush");	
		list.add("push_size");	
		list.add("is_open_brushOrder");	
		list.add("is_open_androidOrderPush");	
		list.add("rangePush_limit");	
		list.add("cityPush_limit");	
		list.add("push_notLogin_days");	
		list.add("is_open_search_pushOrder");	
		list.add("is_open_mqPush");
		list.add("order_sms_content_link");
		list.add("total_sendSms_count");
		list.add("sendorderSms_time");
		return list;
	}
}
