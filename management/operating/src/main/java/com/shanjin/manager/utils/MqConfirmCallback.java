package com.shanjin.manager.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

import com.shanjin.common.util.BusinessUtil;

public class MqConfirmCallback implements RabbitTemplate.ConfirmCallback {

	@Override
	public void confirm(CorrelationData correlationData, boolean ack,
			String cause) {
		String msg = correlationData.getId();
		if (ack) {
			System.out.println("写入MQ消息成功：" + msg);
		} else {
			System.out.println("写入MQ消息失败：" + msg);
			BusinessUtil.writeLog("manager-writeToMqFail", msg);
		}
	}
}
