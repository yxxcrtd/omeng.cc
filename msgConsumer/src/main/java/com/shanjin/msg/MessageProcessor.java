package com.shanjin.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.TopicConstants;
import com.shanjin.msg.impl.ClientPushInLoginProcessor;
import com.shanjin.msg.impl.ErrorInfoProcessor;
import com.shanjin.msg.impl.IpToCityProcessor;

/**
 * 消息处理主控模块
 * @author Revoke 2015.12.10
 *
 */
public class MessageProcessor {
	  private static Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
	
	  private static Map<String,IMsgProcessor> processorMap= new HashMap<String,IMsgProcessor>();
	
	  private static ExecutorService executors = Executors.newFixedThreadPool(30);
	  
	  public static void initProcessor(ClassPathXmlApplicationContext context){
		  	processorMap.put(TopicConstants.CLIENT_UPDATE_PUSH_LOGIN, new ClientPushInLoginProcessor(context,executors));
		  	processorMap.put(TopicConstants.IP_CITY_RESOLVE, new IpToCityProcessor(context,executors));
		  	processorMap.put(TopicConstants.ERROR_TOPIC, new ErrorInfoProcessor(context,executors));
	  }
	  
	  /**
	   * 分发消息到特定的 消息处理器实例
	   * @param topic
	   * @param key
	   * @param content
	   */
	  public static void dispatch(String topic,String key,String content) {
	  	 
		     if (processorMap.containsKey(topic)){
		    	 processorMap.get(topic).submitTask(topic, key, content);
		     }else{
		    	 logger.warn(" 未找到相关消息处理器："+topic);
		     }
	  }
	  
	  
	
}
