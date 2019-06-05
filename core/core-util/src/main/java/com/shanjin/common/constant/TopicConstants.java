package com.shanjin.common.constant;

/**
 * 存放主题常量
 * @author Revoke 2015.12.9
 *
 */
public class TopicConstants {

	/**
	 * 合法用户端发起的请求
	 */
	public static String CLIENT_ACCESS_LOG="accessLog";
	
	
	/**
	 * IP 解析成 城市
	 */
	public static String IP_CITY_RESOLVE="IpToCity";
	
	
	
	/**
	 * 验证客户端用户成功后，
	 */
	public static String CLIENT_UPDATE_PUSH_LOGIN="clientUpatePushInLogin";
	
	
	
	
	/**
	 * 接口调用错误主题。
	 */
	public static String  ERROR_TOPIC = "serviceError";
	
	
	
	
	/**
	 * 错误消息的分隔符            错误消息的key  由原始的topic+拼装分隔复符+原始key组成  
	 */
	public static String  ERRO_TOPIC_SEPERATOR = "$|$";
	
	
	
	/**
	 * 卡夫卡消息重试时，在content 中追加的栏位，以记录重试次数
	 */
	public static String  KAFKA_MSG_RETRY = "kafkaRetry";
	
}
