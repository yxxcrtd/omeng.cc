package com.shanjin.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.TopicConstants;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.kafka.ProducerFactory;
import com.shanjin.kafka.ProducerInterface;

/**
 * 消息发送类 ----提供发送消息的静态方法
 * 
 * @author Revoke 2016.1.4
 *
 */
public class MsgTools {

	private static String ip = BusinessUtil.getIpAddress();

	private static ProducerInterface vitalProducer;

	static {
		if (Constant.KAFKA_CAPTURE_EXCEPTION)
			vitalProducer = ProducerFactory.getProducer(true, BusinessUtil.getIpAddress());
	}

	/**
	 * 如果启动KAFKA 捕获异常，则 将API 调用产生的异常信息放入 KAFKA 中 topic 为 serviceError
	 * 
	 * @param ex
	 */
	public static void sendMsgOrIgnore(Exception ex, String apiName) {
		String errorStr=ExceptionUtils.getStackTrace(ex);
		if (Constant.KAFKA_CAPTURE_EXCEPTION) {
			try{
				vitalProducer.sendMsg(TopicConstants.ERROR_TOPIC, ip + "-" + apiName, errorStr);					
			}catch(Exception e){
				//捕获自身异常，避免返回可混
				e.printStackTrace();
			}
		}
		//异常信息保存日志
		StringBuffer content=new StringBuffer();
		content.append("时间："+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))+"\r\n")
		.append("接口："+apiName+"\r\n")
		.append(errorStr+"\r\n");
		BusinessUtil.writeLog("exception",content.toString());
	}
	public static void sendMsgOrIgnore(Exception ex, String apiName,String...param) {
		String errorStr=ExceptionUtils.getStackTrace(ex);
		if (Constant.KAFKA_CAPTURE_EXCEPTION) {
			try{
				vitalProducer.sendMsg(TopicConstants.ERROR_TOPIC, ip + "-" + apiName, errorStr);					
			}catch(Exception e){
				//捕获自身异常，避免返回可混
				e.printStackTrace();
			}
		}
		String params="";
		for(String p : param){
			params+=p+",";
		}
		if(!params.equals("")){
			params=params.substring(0,params.length()-1);
		}
		//异常信息保存日志
		StringBuffer content=new StringBuffer();
		content.append("时间："+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))+"\r\n")
		.append("接口："+apiName+"\r\n")
		.append("参数："+params+"\r\n")
		.append(errorStr+"\r\n");
		BusinessUtil.writeLog("exception",content.toString());
	}
}
