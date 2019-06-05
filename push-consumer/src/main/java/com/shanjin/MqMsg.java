package com.shanjin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.other.Util;
import com.shanjin.push.AndroidPushSend;
import com.shanjin.push.IosPushSend;


public class MqMsg  {
	
	private RabbitTemplate sendTemplate;
	public RabbitTemplate getSendTemplate() {
		return sendTemplate;
	}


	public void setSendTemplate(RabbitTemplate sendTemplate) {
		this.sendTemplate = sendTemplate;
	}


	public void getMQMsg(String msg){
		//1 从新订单对列读取一条订单信息
		Util.writeLog("pushMQ","从MQ中得到消息："+msg);
		if(StringUtil.isEmpty(msg)  || msg.equals("{}")){
			System.out.println("msg is null");
			return ;
		}
		//2 通过dubbo 调用 provider方法，获取推送信息
		JSONObject pushParams=null;
		try{
			pushParams=JSONObject.parseObject(msg);
//			System.out.println("pushParams："+pushParams);
		}catch(Exception e){
			System.out.println("异常0："+e.getMessage());
			return ;
		}
		if(StringUtil.isEmpty(pushParams) || pushParams.toString().equals("{}")){
			System.out.println("pushParams is null");
			return ;
		}
		try{

			//推送			
			String androidPushInfo="";
			try{
				androidPushInfo=StringUtil.null2Str(pushParams.get("androidPushInfo"));
			}catch(Exception e){
				System.out.println("异常1："+e.getMessage());
			}
//			System.out.println("androidPushInfo："+androidPushInfo);
			if(StringUtil.isNotEmpty(androidPushInfo) && !androidPushInfo.equals("{}")){
				doAndroidPush(androidPushInfo);
			}
			
			String iosPushInfo="";
			try{
				iosPushInfo=StringUtil.null2Str(pushParams.get("iosPushInfo"));

			}catch(Exception e){
				System.out.println("异常2："+e.getMessage());
			}
//			System.out.println("iosPushInfo："+iosPushInfo);
			if(StringUtil.isNotEmpty(iosPushInfo) && !iosPushInfo.equals("{}")){
				doIosPush(iosPushInfo);
			}
			
		}catch(Exception e){
			System.out.println("异常3："+e.getMessage());
		}
		
	}
	public Message getMsg(String msg) throws Exception{
		return MessageBuilder.withBody(msg.getBytes("UTF-8"))
		.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).build();
	}
	
	@SuppressWarnings("unchecked")
	public void doAndroidPush(String androidPushInfo) {
		
		try{			
			String androidPushInfoListStr = getParam(androidPushInfo,"androidPushInfoList");
			String msg = getParam(androidPushInfo,"msg");
			String configMap=getParam(androidPushInfo,"configMap");		
			String concatKey=getParam(androidPushInfo,"concatKey");
			
			if(!Constant.PRESSURETEST){//如果压力测试则不打印
				BusinessUtil.writeLog("push","安卓版推送时间： " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				BusinessUtil.writeLog("push","推送内容：" + msg);
			}
			if (androidPushInfoListStr != null && msg != null) {
				List<Map<String, Object>> androidPushInfoList = (List<Map<String, Object>>) JSONObject.parse(androidPushInfoListStr);
				Map<String, Object> configObject = JSONObject.parseObject(configMap);
				Map<String, Object> msgObject = JSONObject.parseObject(msg);
	
				//判断是否开启Android推送
				String isOpenAndroidOrderPush=configObject.get("is_open_androidOrderPush")==null?"1":configObject.get("is_open_androidOrderPush").toString();//默认开启
				if(isOpenAndroidOrderPush.equals("0")){//关闭订单Android推送
					BusinessUtil.writeLog("push","Android推送关闭");
					return ;
				}
				
				AndroidPushSend.send(configObject,msgObject,concatKey, androidPushInfoList);
			}
		}catch(Exception e){
			System.out.println("异常4："+e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	public void doIosPush(String iosPushInfo) throws Exception{		
		try{
	
			String iosPushInfoList = getParam(iosPushInfo,"iosPushInfoList");
			String msg = getParam(iosPushInfo,"msg");
			String configMap=getParam(iosPushInfo,"configMap");
			
			if(!Constant.PRESSURETEST){//如果压力测试则不打印
				BusinessUtil.writeLog("push","IOS推送时间： " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				BusinessUtil.writeLog("push","推送内容：" + msg);
			}
			List<Map<String, Object>> iosPushInfoListObject = (List<Map<String, Object>>) JSONObject.parse(iosPushInfoList);
			Map<String, Object> configObject = JSONObject.parseObject(configMap);
	
			//判断是否开启IOS推送
			String isOpenIosOrderPush=configObject.get("is_open_iosOrderPush")==null?"1":configObject.get("is_open_iosOrderPush").toString();//默认开启
			if(isOpenIosOrderPush.equals("0")){//关闭订单IOS推送
				BusinessUtil.writeLog("push","IOS推送关闭");
				return ;
			}
			Map<String, Object> msgObject = JSONObject.parseObject(msg);
			IosPushSend.send(configObject,msgObject, iosPushInfoListObject);
		}catch(Exception e){
			System.out.println("异常5："+e.getMessage());
		}	
	}
	
	public String getParam(String str1,String str2){
		try{
			if(StringUtil.isEmpty(str1)){
				return "";
			}
			if(StringUtil.isEmpty(str2)){
				return "";
			}
			String[] params=str1.split("&");
			for(String param : params){
				String key=param.split("=")[0];
				String value=param.split("=")[1];
				if(key.equals(str2)){
					return value;
				}
			}
		}catch(Exception e){
			System.out.println("异常6："+e.getMessage());
		}
		return "";
	}
	
}
