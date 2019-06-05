package com.shanjin.other;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;


public class MqConfirmCallback implements RabbitTemplate.ConfirmCallback {

	@Override
	public void confirm(CorrelationData correlationData, boolean ack,
			String cause) {
		String msg=correlationData.getId();
		  if (ack){
			  System.out.println("写入MQ消息成功："+msg);
		  }else{
			  System.out.println("写入MQ消息失败："+msg);
			 
		  }
		
	}

}
