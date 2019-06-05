package com.shanjin.push.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.CalloutServices;
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
	public static void androidPush(Map<String,Object> configMap,List<Map<String, Object>> androidPushInfoList,Map<String, Object> paras,String concatKey) throws Exception{
		String data=paras.get("data")==null?"":paras.get("data").toString();
		String appType=paras.get("appType")==null?"":paras.get("appType").toString();
		Long merchantId=paras.get("merchantId")==null?0:StringUtil.nullToLong(paras.get("merchantId"));
		Long orderId=Long.parseLong(paras.get("orderId")==null?"0":paras.get("orderId").toString());
		int pushType=Integer.parseInt(paras.get("pushType")==null?"-1":paras.get("pushType").toString());
		Map<String,Object> pushMsg=PushMessage.getPushMsg(configMap,appType,merchantId,orderId, pushType,data);
		pushMsg.put("data", data);
		
		System.out.println("推送的消息："+pushMsg.toString());
		// 通知之前的终端登出
		if (androidPushInfoList != null && androidPushInfoList.size() > 0) {
			StringBuilder pushParams = new StringBuilder();
			pushParams.append("androidPushInfoList=").append(JSONObject.toJSONString(androidPushInfoList))
			.append("&msg="+JSONObject.toJSONString(pushMsg))
			.append("&concatKey="+concatKey)			
			.append("&configMap=").append(JSONObject.toJSONString(configMap));;
			 PushMessageByProxy pushByProxy = new PushMessageByProxy(Constant.WEB_PROXY_URL + "/doAndroidPush?", pushParams.toString());
			 CalloutServices.executor(pushByProxy);

		}
	}
	/**
	 * ios推送
	 * @param iosPushInfoList 推送对象
	 * @param appType 应用类型
	 * @throws Exception    
	 * void   
	 * @throws
	 */
	public static void iosPush(Map<String,Object> configMap,List<Map<String, Object>> iosPushInfoList,Map<String,Object>  paras) throws Exception{
		String data=paras.get("data")==null?"":paras.get("data").toString();
		Long orderId=Long.parseLong(paras.get("orderId")==null?"0":paras.get("orderId").toString());
		int pushType=Integer.parseInt(paras.get("pushType")==null?"-1":paras.get("pushType").toString());
		Long merchantId=paras.get("merchantId")==null?0:StringUtil.nullToLong(paras.get("merchantId"));
		String appType=paras.get("appType")==null?"":paras.get("appType").toString();
		Map<String, Object> pushMsg=PushMessage.getPushMsg(configMap,appType,merchantId,orderId, pushType,data);
		if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
			// 通过代理推送
			StringBuilder pushParams = new StringBuilder();
			pushParams.append("iosPushInfoList=").append(JSONObject.toJSONString(iosPushInfoList))
			.append("&msg=").append(JSONObject.toJSONString(pushMsg))
			.append("&configMap=").append(JSONObject.toJSONString(configMap));
			PushMessageByProxy pushByProxy = new PushMessageByProxy(Constant.WEB_PROXY_URL + "/doIosPush?", pushParams.toString());
			CalloutServices.executor(pushByProxy);
		}		
	}
	/**推送**/
	public static void push(Map<String,Object> configMap,List<Map<String, Object>> onlineMerchantList,Map<String, Object> paras,String concatKey) throws Exception {
		List<Map<String, Object>> androidPushInfoList = new ArrayList<Map<String, Object>>();// 安卓终端
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		
		if (onlineMerchantList != null && onlineMerchantList.size() > 0) {
			for (Map<String, Object> tempmap : onlineMerchantList) {
				String pushId = StringUtil.null2Str(tempmap.get("pushId"));
				String userId=StringUtil.null2Str(tempmap.get("userId"));
				String appType=StringUtil.null2Str(tempmap.get("appType"));
				String merchantId=StringUtil.null2Str(tempmap.get("merchantId"));
				Map<String, Object> info = new HashMap<String, Object>();
				info.put("pushId", pushId);
				info.put("userId", userId);
				info.put("appType", appType);
				info.put("merchantId", merchantId);
				if (!StringUtil.isNullStr(pushId)) {
					if (StringUtil.nullToInteger(tempmap.get("clientType"))==1) {// 安卓					
						androidPushInfoList.add(info);
					} else if (StringUtil.nullToInteger(tempmap.get("clientType"))==2){//IOS
						iosPushInfoList.add(info);
					}else{
						
					}
				}
			}
		}
		PushManager.androidPush(configMap,androidPushInfoList, paras, concatKey);
		PushManager.iosPush(configMap,iosPushInfoList,paras);
	}	
	


}
