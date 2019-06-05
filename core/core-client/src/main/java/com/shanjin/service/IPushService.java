package com.shanjin.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface IPushService {
	public void basicPush(Map<String, Object> paras) throws Exception;
	
	public JSONObject commonPush(Map<String, Object> paras) throws Exception;
	public JSONObject repeatLoginPush(Map<String, Object> param)throws Exception;	
	public JSONObject orderPush(Map<String, Object> paras) throws Exception;
	public JSONObject privateAssistantPush(Map<String, Object> paras) throws Exception;
	
	public JSONObject pushOrderToAssistants(Map<String, Object> paras) throws Exception;
	
	
	/**共通推送**/
	public void asyncCommonPush(Map<String, Object> paras) throws Exception;

	/**重复登录推送**/
	public void asyncRepeatLoginPush(Map<String, Object> param)throws Exception;
	
	/**订单推送**/
	public void asyncOrderPush(Map<String, Object> paras) throws Exception;
	
	/**私人助理推送**/
	public void asyncPrivateAssistantPush(Map<String, Object> paras) throws Exception;
	
	
	/**推送订单通知给私人助理APP **/
	public void asyncPushOrderToAssistants(Map<String, Object> paras) throws Exception;
	
	Map<String,Object> getConfigurationInfoByKey();
	
	/**
	 * 获取某应用的推送配置信息
	 * @param key
	 * @return
	 */
	Map<String,Object> getConfigurationInfoByKey(String appType);
	
	
	public void writeToMQ(String queueName,String msg)throws Exception;
	
	
	

	
}
