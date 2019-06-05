package com.shanjin.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.shanjin.kafka.impl.NormalConsumer;
import com.shanjin.kafka.impl.VitalConsumer;

/**
 * 消费者工厂类
 * @author Revoke 2015.12.7
 *
 */
public class ConsumerFactory {
	
	/**
	 * 
	 * @param groupId  组标识别，同组类的不同消费者实例共享消息
	 * @param topics	订阅的主题列表
	 * @param flag		true 重要消息  false 一般性消息
	 * @param consumerID 消费者标识
	 * @return
	 */
	 public static ConsumerInterface getConsumer(String groupId,String[] topics,boolean flag,String consumerId){
		  if (flag)
			  	return VitalConsumer.getInstance(groupId,topics,consumerId);
		  else
			    return NormalConsumer.getInstance(groupId,topics,consumerId);
	 }

}
