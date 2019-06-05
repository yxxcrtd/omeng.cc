package com.shanjin.kafka;

/**
 * 消息分区接口
 * @author Revoke Yu 2015.12.4
 *
 */
public interface Partition {
	
	/**
	 * 分区算法
	 * @param topic
	 * @param key
	 * @param content
	 * @return
	 */
	public int getPartition(String topic,String key,String content);
}
