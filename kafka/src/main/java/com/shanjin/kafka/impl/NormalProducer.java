package com.shanjin.kafka.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.shanjin.kafka.ProducerInterface;

/**
 * KAFKA 0.9.0 版本 JAVA API  消息发送包装类---常规消息
 * @author Revoke 2015.12.4
 *
 */
public class NormalProducer extends ProducerBase implements ProducerInterface {
	final Logger logger = LoggerFactory.getLogger(NormalProducer.class);
	private static ProducerInterface _instance = null;
	
	private NormalProducer(String client){
		
		InputStream propertiesCfg = NormalProducer.class
				.getResourceAsStream("/producer.properties");
		Properties properties = new Properties();

		try {
			properties.load(propertiesCfg);
			properties.setProperty("client.id", client);
			producer = new KafkaProducer<String,String>(properties);

		} catch (IOException e) {
			logger.error("消息发送者初始化失败："+e.getMessage(), e);
			e.printStackTrace();
		}

	}
	
	public static synchronized ProducerInterface getInstance(String client){
		  if (_instance==null){
			   _instance = new NormalProducer(client);
		  }
		  return _instance;
	}
	
	
}
