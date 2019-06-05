package com.shanjin.msg.impl;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.mail.MailTools;
import com.shanjin.msg.IMsgProcessor;
import com.shanjin.msg.MsgProcessorBase;
import com.shanjin.msg.impl.ClientPushInLoginProcessor.Handler;
import com.shanjin.service.IUserInfoService;

/**
 * 消息TOPIC 为错误，邮件外发
 * @author Revoke 2016.1.6
 *
 */
public class ErrorInfoProcessor extends MsgProcessorBase implements
		IMsgProcessor {
	Logger logger = LoggerFactory.getLogger(ErrorInfoProcessor.class);
	
	public ErrorInfoProcessor(ClassPathXmlApplicationContext context,
			ExecutorService executors) {
		super(context, executors);
		
	}

	@Override
	public void submitTask(String topic, String key, String content) {
		
		logger.info("异常消息推送主题处理中。。。"+ key);
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
			logger.info("send out email to notify");
			 try {
				MailTools.sendMail("服务调用异常: "+key, content);
			} catch (Exception e) {
				logger.warn("邮件发送失败："+key+"  内容为："+content);
			}
		}
		
	}
}
