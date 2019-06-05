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
 * IP 解析成 城市的消息处理器
 * @author Revoke  2015.12.10
 *
 */
public class IpToCityProcessor extends MsgProcessorBase implements IMsgProcessor {
	Logger logger = LoggerFactory.getLogger(IpToCityProcessor.class);
	
	public IpToCityProcessor(ClassPathXmlApplicationContext context,ExecutorService executors){
		  super(context,executors);
	}
	
	@Override
	public void submitTask(String topic, String key, String content) {
		logger.info(" ip 解析成省份城市 消息处理中 "+content);
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
			 String ip = jsonObject.getString("ip");
			 String userId = jsonObject.getString("userId");
			 IUserInfoService userInfoService= (IUserInfoService) context.getBean("userService");
			 boolean result = userInfoService.updateUserLocation(ip, userId);
			 
			 if(!result){
				 processError(topic,key,jsonObject);
			 }
		}
		
	}
}
