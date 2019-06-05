package com.shanjin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.CalloutServices;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;


public class PushManager {

	public static void main(String[] args){
//		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>(); 
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("clientId", "78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e");
//		map.put("userId", "145743968466565900");
//		iosPushInfoList.add(map);
//		Map<String, Object> pushMsg=PushMessage.getPushMsg("",0L, 0);
//		if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
//			// 通过代理推送
//			StringBuilder pushParams = new StringBuilder();
//			pushParams.append("iosPushInfoList=").append(JSONObject.toJSONString(iosPushInfoList)).append("&msg=").append(JSONObject.toJSONString(pushMsg));
//			PushMessageByProxy pushByProxy = new PushMessageByProxy("http://localhost:8080/doIosPush?", pushParams.toString());
//			CalloutServices.executor(pushByProxy);
//		}		
	}
	
	/**
	 * android推送
	 * @param androidPushInfoList 推送对象
	 * @param transmissionContent 推送类容
	 * @param concatKey 
	 * @throws Exception    
	 * void   
	 * @throws
	 */
	public static String androidPush(Map<String,Object> configMap,List<Map<String, Object>> androidPushInfoList,Map<String, Object> paras,String concatKey) throws Exception{
		StringBuilder pushParams = new StringBuilder();
		String data=paras.get("data")==null?"":paras.get("data").toString();
		
		Map<String,Object> pushMsg=PushMessage.getPushMsg(configMap,paras);
		pushMsg.put("data", data);
		
		System.out.println(DateUtil.getNowYYYYMMDDHHMMSS()+"-推送的消息："+pushMsg.toString());
		// 通知之前的终端登出
		if (androidPushInfoList != null && androidPushInfoList.size() > 0) {
			
			pushParams.append("androidPushInfoList=").append(JSONObject.toJSONString(androidPushInfoList))
			.append("&msg="+JSONObject.toJSONString(pushMsg))
			.append("&concatKey="+concatKey)			
			.append("&configMap=").append(JSONObject.toJSONString(configMap));

			int isOpenMqPush=configMap.get("is_open_mqPush")==null?0:StringUtil.nullToInteger(configMap.get("is_open_mqPush"));//默认开启
			if(isOpenMqPush==0){//原始推送
				PushMessageByProxy pushByProxy = new PushMessageByProxy(Constant.WEB_PROXY_URL + "/doAndroidPush?", pushParams.toString());
				CalloutServices.executor(pushByProxy);
			}
		}
		return StringUtil.null2Str(pushParams);
	}
	/**
	 * ios推送
	 * @param iosPushInfoList 推送对象
	 * @param appType 应用类型
	 * @throws Exception    
	 * void   
	 * @throws
	 */
	public static String iosPush(Map<String,Object> configMap,List<Map<String, Object>> iosPushInfoList,Map<String,Object>  paras) throws Exception{
		StringBuilder pushParams = new StringBuilder();
		
		Map<String, Object> pushMsg=PushMessage.getPushMsg(configMap,paras);
		if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
			// 通过代理推送			
			pushParams.append("iosPushInfoList=").append(JSONObject.toJSONString(iosPushInfoList))
			.append("&msg=").append(JSONObject.toJSONString(pushMsg))
			.append("&configMap=").append(JSONObject.toJSONString(configMap));
			
			int isOpenMqPush=configMap.get("is_open_mqPush")==null?0:StringUtil.nullToInteger(configMap.get("is_open_mqPush"));//默认开启
			if(isOpenMqPush==0){//原始推送
				PushMessageByProxy pushByProxy = new PushMessageByProxy(Constant.WEB_PROXY_URL + "/doIosPush?", pushParams.toString());
				CalloutServices.executor(pushByProxy);
			}
		}	
		return StringUtil.null2Str(pushParams);		
	}
	/**推送**/
	public static JSONObject push(Map<String,Object> configMap,List<Map<String, Object>> onlineMerchantList,Map<String, Object> paras,String concatKey) throws Exception {
		JSONObject jobj=new JSONObject();
		List<Map<String, Object>> androidPushInfoList = new ArrayList<Map<String, Object>>();// 安卓终端
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		
		if (onlineMerchantList != null && onlineMerchantList.size() > 0) {
			for (Map<String, Object> tempmap : onlineMerchantList) {
				String pushId = StringUtil.null2Str(tempmap.get("pushId"));
				String clientType=StringUtil.null2Str(tempmap.get("clientType"));	
				String phoneModel=StringUtil.null2Str(tempmap.get("phoneModel"));
				
				if (!StringUtil.isNullStr(pushId)) {
					if (StringUtil.nullToInteger(clientType)==1) {// 安卓	
						if(phoneModel.toLowerCase().startsWith("xiaomi_")){
							pushId=java.net.URLEncoder.encode(pushId,"utf-8");
						}
						tempmap.put("pushId", pushId);
						androidPushInfoList.add(tempmap);
					} else if (StringUtil.nullToInteger(clientType)==2){//IOS
						iosPushInfoList.add(tempmap);
					}else{
						
					}
				}
			}
		}
		String androidPushInfo=PushManager.androidPush(configMap,androidPushInfoList, paras, concatKey);
		String iosPushInfo=PushManager.iosPush(configMap,iosPushInfoList,paras);
		jobj.put("androidPushInfo", androidPushInfo);
		jobj.put("iosPushInfo", iosPushInfo);
		return jobj;
	}	
	


}
