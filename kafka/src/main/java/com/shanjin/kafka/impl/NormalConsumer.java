package com.shanjin.kafka.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shanjin.kafka.ConsumerInterface;

/**
 * 常规消息消费者，支持部署多个实例。 实例间共享消息。
 * @author Revoke
 *
 */
public class NormalConsumer extends ConsumerBase implements ConsumerInterface {
	 private static final Logger logger = LoggerFactory.getLogger(NormalConsumer.class);

	 private static Map<String,NormalConsumer> consumerMap =new ConcurrentHashMap<String,NormalConsumer>();
	 
	 private NormalConsumer(String groupId,String[] topics,String consumerId){
		  InputStream propertiesCfg = NormalConsumer.class
					.getResourceAsStream("/consumer.properties");
			Properties properties = new Properties();

			try {
				properties.load(propertiesCfg);
				properties.setProperty("group.id", groupId);
				properties.setProperty("consumer.id", consumerId);
				
				consumer = new KafkaConsumer<String, String>(properties);
				
				consumer.subscribe(Arrays.asList(topics));
				

			} catch (IOException e) {
				logger.error("消息接收者初始化失败："+e.getMessage(), e);
				e.printStackTrace();
			}
	  }
	   
	  public static synchronized NormalConsumer getInstance(String groupId,String[] topics,String consumerId){
		     String  consumerKey = 	getKeyFromTopics(topics);
		     if  (!consumerMap.containsKey(consumerKey)){
		    	 consumerMap.put(consumerKey, new NormalConsumer(groupId,topics,consumerId));
		      }
		      return consumerMap.get(consumerKey);
	  }
	  
	  
	  private static String getKeyFromTopics(String[] topics){
		  StringBuffer topicBuffer = new StringBuffer("");
		  for(String topic:topics){
			  topicBuffer.append(topic);
		  }
		  return topicBuffer.toString();
	  }
}
