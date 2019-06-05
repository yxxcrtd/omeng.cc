package com.shanjin.kafka.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

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
public class VitalConsumer extends ConsumerBase implements ConsumerInterface {
	 private static final Logger logger = LoggerFactory.getLogger(VitalConsumer.class);
		
	 private static VitalConsumer _instance = null;
	 
	 public static synchronized VitalConsumer getInstance(String groupId,String[] topics,String consumerId) {
		    if (_instance==null)
		    	_instance = new VitalConsumer(groupId,topics,consumerId);
		  	return _instance;
	 }
	 
	  private   VitalConsumer(String groupId,String[] topics,String consumerId){
		  InputStream propertiesCfg = VitalConsumer.class
					.getResourceAsStream("/consumer.properties");
			Properties properties = new Properties();

			try {
				properties.load(propertiesCfg);
				properties.setProperty("group.id", groupId);
				properties.setProperty("enable.auto.commit", "false");
				properties.setProperty("session.timeout.ms", "6000"); //5秒检测客户端是否dead.
				properties.setProperty("fetch.max.wait.ms", "5");//每5毫秒取一次
				properties.setProperty("consumer.id", consumerId);//每5毫秒取一次
				
				consumer = new KafkaConsumer<String, String>(properties);
				
				consumer.subscribe(Arrays.asList(topics));

			} catch (IOException e) {
				logger.error("消息发送者初始化失败："+e.getMessage(), e);
				e.printStackTrace();
			}
	  }
	   

}
