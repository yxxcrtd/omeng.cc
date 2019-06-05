package com.shanjin.msg.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.msg.IMsgProcessor;
import com.shanjin.msg.MsgProcessorBase;
import com.shanjin.service.IUserInfoService;

/**
 * 用户端登陆时 消息推送主题  消息处理器
 * @author Revoke 2015.12.10
 *
 */
public class ClientPushInLoginProcessor  extends MsgProcessorBase implements IMsgProcessor {
	Logger logger = LoggerFactory.getLogger(ClientPushInLoginProcessor.class);
	
	public ClientPushInLoginProcessor(ClassPathXmlApplicationContext context,ExecutorService executors){
		 super(context,executors);
	}
	
	
	@Override
	public void submitTask(String topic, String key, String content) {
		logger.info("用户登陆消息推送主题处理中。。。"+content);
		executors.submit(new Handler(topic,key,content));
	}

	class Handler implements Runnable {
		private String topic;
		private String key;
		private String content;
		
		Handler(String topic,String key,String content){
			 this.topic = topic;
			 this.key = key;
			 this.content = content;
		}
		
		@Override
		public void run() {
			JSONObject jsonObject = JSONObject.parseObject(content);
			String clientId = jsonObject.getString("clientId");
			String deviceId = jsonObject.getString("deviceId");
		//	String appType = jsonObject.getString("appType");
			String userId = jsonObject.getString("userId");
			String clientType = jsonObject.getString("clientType");
			IUserInfoService userInfoService= (IUserInfoService) context.getBean("userService");
			boolean result=userInfoService.updateUserPushInfo(clientId, deviceId, userId, clientType);
			
			if(!result){
				 processError(topic,key,jsonObject);
			}
				
		}
		
	
	}
}
