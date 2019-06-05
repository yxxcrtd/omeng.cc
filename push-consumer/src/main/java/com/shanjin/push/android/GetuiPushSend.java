package com.shanjin.push.android;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;

public class GetuiPushSend {
	/**
	 * 推送发送
	 * @param msg  消息体
	 * @param iosPushInfoList  推送设备对象
	 */
	public static void send(Map<String, Object> configMap,Map<String, Object> msg,String concatKey,List<Map<String, Object>> androidPushInfoList){
		
//		int pushType=StringUtil.nullToInteger(msg.get("pushType"));		
//		if(pushType==8){//暂时去除多终端登录推送
//			System.out.println("暂时去除多终端登录推送");
//			return ;
//		}
		String host=StringUtil.null2Str(configMap.get("push-host"));
		if(StringUtil.isEmpty(host)){
			host=PushConfig.host;
		}
		String appId=StringUtil.null2Str(configMap.get("push-appId"));
		if(StringUtil.isEmpty(appId)){
			appId=PushConfig.appId;
		}
		String appkey=StringUtil.null2Str(configMap.get("push-appkey"));
		if(StringUtil.isEmpty(appkey)){
			appkey=PushConfig.appkey;
		}
		String masterSecret=StringUtil.null2Str(configMap.get("push-masterSecret"));
		if(StringUtil.isEmpty(masterSecret)){
			masterSecret=PushConfig.masterSecret;
		}

		//判断推送数量是否超过设定的最大值
		int push_size =StringUtil.nullToInteger(configMap.get("push_size")==null?"20":configMap.get("push_size"));
	
		long orderId=StringUtil.nullToLong(msg.get("orderId"));		
		
		IGtPush push = new IGtPush(host, appkey, masterSecret);
		try {
			push.connect();

			IPushResult ret = null;
			for (int i=0;i<androidPushInfoList.size();i++) {
				
				//超过指定数量，则不推送
				if(push_size!=0 && i>push_size){
					BusinessUtil.writeLog("push",orderId+"-当前推送第"+i+"个，超过上限"+push_size);
					return ;
				}
				
				Map<String, Object> pushInfo = androidPushInfoList.get(i);
				msg.put("concatKey", pushInfo.get(concatKey));				
				
				Long merchantId=StringUtil.nullToLong(pushInfo.get("merchantId"));
				if((long)merchantId != 0){
					msg.put("merchantId", merchantId);
				}
				TransmissionTemplate template = TransmissionTemplate(appId,appkey,msg.toString());
				SingleMessage message = new SingleMessage();
				message.setData(template);
				message.setOffline(true);
				message.setOfflineExpireTime(24 * 1000 * 3600);

				Target target = new Target();
				target.setAppId(appId);
				target.setClientId(pushInfo.get("pushId")==null?"":pushInfo.get("pushId").toString());
				
				if(!Constant.PRESSURETEST){//如果压力测试则不打印
					BusinessUtil.writeLog("push",orderId+"-Android推送对象:" + pushInfo);
					BusinessUtil.writeLog("push",orderId+"-Android推送消息:" + msg);
				}
				
				try {
					ret = push.pushMessageToSingle(message, target);
				} catch (Exception e) {
					e.printStackTrace();
					ret = push.pushMessageToSingle(message, target);
				}
				if (ret != null) {
					if(!Constant.PRESSURETEST){//如果压力测试则不打印
						BusinessUtil.writeLog("push",orderId+"-个推返回结果：" + ret.getResponse().toString()+"\n");
					}
				} else {
					BusinessUtil.writeLog("push",orderId+"-推送失败！");
					ret = push.pushMessageToSingle(message, target);
				}
			}

			push.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (push != null) {
				try {
					push.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static TransmissionTemplate TransmissionTemplate(String appId,String appkey,String transmissionContent) throws Exception {
		
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTransmissionType(2);
		template.setTransmissionContent(transmissionContent);
		return template;
	}
}
