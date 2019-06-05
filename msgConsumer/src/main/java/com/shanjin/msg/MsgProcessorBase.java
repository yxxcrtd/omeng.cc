package com.shanjin.msg;

import java.util.concurrent.ExecutorService;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.TopicConstants;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.kafka.ProducerFactory;
import com.shanjin.kafka.ProducerInterface;

/**
 * 消息处理通用类
 * @author Revoke 2015.12.10
 *
 */
public abstract class MsgProcessorBase implements IMsgProcessor {
	protected ExecutorService executors;
	protected ClassPathXmlApplicationContext context;
	private static ProducerInterface producer = ProducerFactory.getProducer(true, BusinessUtil.getIpAddress());
	
	
	protected MsgProcessorBase(ClassPathXmlApplicationContext context,ExecutorService executors){
		 this.context = context;
		 this.executors = executors;
	}
	

	
	public void processError(String topic,String key,JSONObject content){
			if (isLastRetryChance(content)){
				sendErrorMsg(topic,key,content.toJSONString());
			}else{
				sendRetryMsg(topic,key,content);
			}
	}
	
	/**
	 * 发送错误处理消息到KAFA 消息队列中， 可由另外的consumer 进行监控，比如插入数据库或外发邮件等等。
	 * @param topic
	 * @param key
	 * @param content
	 */
	private void sendErrorMsg(String topic, String key, String content) {
		StringBuffer newKey = new StringBuffer(topic);
		newKey.append("$$").append(key);
		producer.sendMsg(TopicConstants.ERROR_TOPIC, newKey.toString(), content);
	}



	/**
	 * 重新发送处理出错的消息，以重试
	 * @param topic
	 * @param key
	 * @param content
	 */
	private void sendRetryMsg(String topic, String key, JSONObject content) {
			producer.sendMsg(topic, key,modifyTryTimes(content));
	}

	/**
	 * 更新重试次数
	 * @param content
	 * @return
	 */
	private String modifyTryTimes(JSONObject content){
		if (content.containsKey(TopicConstants.KAFKA_MSG_RETRY)){
			 int retryTimes = content.getIntValue(TopicConstants.KAFKA_MSG_RETRY)+1;
			 content.put(TopicConstants.KAFKA_MSG_RETRY, retryTimes);
		}else{
			content.put(TopicConstants.KAFKA_MSG_RETRY, 1);
		}
		
		return content.toJSONString();
	}
	
	/**
	 * 判断试错次数是否大于3次  
	 * @param  content         消息内容
	 * @return
	 */
	private boolean isLastRetryChance(JSONObject content){
			if (content.containsKey(TopicConstants.KAFKA_MSG_RETRY)){
				 return content.getIntValue(TopicConstants.KAFKA_MSG_RETRY)==2?true:false;
			}
			return false;
	}
}
