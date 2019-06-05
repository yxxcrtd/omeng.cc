package com.shanjin.manager.utils;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.context.ApplicationContext;

public class MqUtil {
	
    public static ApplicationContext ctx;  
	/**
	 * 写入MQ
	 * @param msg
	 * @throws Exception
	 */
	public static void writeToMQ(String queueName,String exchange,String msg) throws Exception{	
		try{
			RabbitTemplate orderTemplate = (RabbitTemplate) ctx.getBean(queueName);
			orderTemplate.send(exchange,null, MessageBuilder.withBody(msg.getBytes("UTF-8"))
			.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build(), new CorrelationData(msg));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
    public static Object getBean(String name){
        return ctx.getBean(name);
   }
}
