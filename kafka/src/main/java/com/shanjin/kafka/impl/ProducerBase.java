package com.shanjin.kafka.impl;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shanjin.kafka.Partition;
import com.shanjin.kafka.ProducerInterface;

/**
 * KAFKA 0.9.0 版本 JAVA API 消息发送包装类---基础类
 * 
 * @author Revoke 2015.12.4
 *
 */

public class ProducerBase implements ProducerInterface {
	final Logger logger = LoggerFactory.getLogger(ProducerBase.class);

	protected KafkaProducer<String, String> producer;

	private ConcurrentHashMap<String, Partition> map = new ConcurrentHashMap<String, Partition>();

	@Override
	public void sendMsg(String topic, String content) {
		if (map.containsKey(topic)) {
			Partition partition = map.get(topic);
			Integer partitionNum = partition.getPartition(topic, "", content);
			producer.send(new ProducerRecord<String, String>(topic, partitionNum,"", content));

		} else
			producer.send(new ProducerRecord<String, String>(topic, content));

	}

	@Override
	public void sendMsg(String topic, String key, String content) {
		if (map.containsKey(topic)){
			Partition partition = map.get(topic);
			Integer partitionNum = partition.getPartition(topic,key, content);
			producer.send(new ProducerRecord<String, String>(topic, partitionNum,key,
					content));
		}
		else
			producer.send(new ProducerRecord<String, String>(topic, key,
					content));
	}

	@Override
	public void flush() {
		producer.flush();

	}

	@Override
	public void close() {
		producer.close();

	}

	@Override
	public void setPartition(String topic, Partition partition) {
		if (map.containsKey(topic)) {
			logger.warn("主题：" + topic + "已存在分区算法！！！");
		} else {
			map.putIfAbsent(topic, partition);
		}
	}

}
