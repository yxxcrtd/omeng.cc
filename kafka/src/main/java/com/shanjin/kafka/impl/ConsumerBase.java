package com.shanjin.kafka.impl;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.shanjin.kafka.ConsumerInterface;

public class ConsumerBase implements ConsumerInterface {
	protected KafkaConsumer<String, String> consumer =null;
	 
	@Override
	public KafkaConsumer<String,String> getOrginalConsumer() {
		return  consumer;
	}


}
