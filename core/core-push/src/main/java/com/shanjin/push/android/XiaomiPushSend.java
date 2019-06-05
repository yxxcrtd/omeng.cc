package com.shanjin.push.android;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;

public class XiaomiPushSend {

	static String APP_SECRET_KEY = "t0m/GRKz+n3QKV10MFbgaA==";
	static String MY_PACKAGE_NAME = "com.xiaomi.mipushdemo";


	private static Message buildMessage(Map<String, Object> map,Map<String, Object> msg,String concatKey) throws Exception {
		long orderId=StringUtil.nullToLong(msg.get("orderId"));	
		String concatValue=StringUtil.null2Str(map.get(concatKey));
		if(StringUtil.isEmpty(concatKey)){
			concatValue=StringUtil.null2Str(map.get("userId"));
		}			
		Long merchantId=StringUtil.nullToLong(map.get("merchantId"));
		String pushType=StringUtil.null2Str(msg.get("pushType"));
		
		JSONObject jobj=new JSONObject();
		jobj.put("orderId", orderId);
		jobj.put("userId", concatValue);
		jobj.put("merchantId", merchantId);
		jobj.put("pushType", pushType);
		
		// 设置在通知栏展示的通知的标题，不允许全是空白字符，长度小于16，中英文均以一个计算。
		String title = "O盟";
		// 设置要发送的消息内容payload，不允许全是空白字符，长度小于4K，中英文均以一个计算。
		String messagePayload=jobj.toJSONString();
		// 设置在通知栏展示的通知的描述，不允许全是空白字符，长度小于128，中英文均以一个计算。
		String description =StringUtil.null2Str(msg.get("alertMsg"));

		Message message = new Message.Builder()
			.title(title)
			.description(description)
			.payload(messagePayload)
			.restrictedPackageName(MY_PACKAGE_NAME).passThrough(0) // 设置消息是否通过透传的方式送给app，1表示透传消息，0表示通知栏消息。
			.notifyType(1) // 使用默认提示音提示
			.extra("flow_control", "4000")// 设置平滑推送, 推送速度4000每秒(qps=4000)				
			.build();
		return message;
	}

	public static void send(Map<String, Object> configMap,Map<String, Object> msg,String concatKey,List<Map<String, Object>> xiaomiList) {
		try{
			Constants.useOfficial();
			Sender sender = new Sender(APP_SECRET_KEY);
			for(Map<String, Object> map : xiaomiList){				
				String regId=StringUtil.null2Str(map.get("pushId"));
//				regId=java.net.URLEncoder.encode(regId,"utf-8");
				Message message = buildMessage(map,msg,concatKey);
				Result res = sender.send(message, regId, 0); // 根据regID，发送消息到指定设备上，不重试。
				System.out.println("小米推送消息响应："+res);
				BusinessUtil.writeLog("push",StringUtil.nullToLong(msg.get("orderId"))+"-小米推送消息响应:" + res);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
