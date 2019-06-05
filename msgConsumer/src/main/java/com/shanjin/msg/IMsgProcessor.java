package com.shanjin.msg;

import com.alibaba.fastjson.JSONObject;

/**
 * 消息处理器接口
 * @author Revoke 2015.12.10
 *
 */
public interface IMsgProcessor {
	
	/**
	 * 提交待处理任务到特定的消息处理器 待处理队列中。
	 * @param topic
	 * @param key
	 * @param value
	 */
	public void  submitTask(String topic,String key,String value);
	
	
	
	/**
	 * 消息调用服务出错时，生成重试消息或生成错误消息
	 * @param topic
	 * @param key
	 * @param content
	 */
	public void processError(String topic,String key,JSONObject content);
	

}
