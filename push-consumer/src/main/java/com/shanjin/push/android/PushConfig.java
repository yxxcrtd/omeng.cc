package com.shanjin.push.android;

import java.util.ArrayList;
import java.util.List;


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
}
