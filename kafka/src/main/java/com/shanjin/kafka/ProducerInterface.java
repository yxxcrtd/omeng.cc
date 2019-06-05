package com.shanjin.kafka;

public interface ProducerInterface {
	
	
	/**
	 * 异步发送普通消息， 适合于日志类，对于精确度要求不高的场景。 不等待消息服务器应答。 
	 * 消息分区数为默认值3
	 * 
	 * @param topic   主题
	 * @param content   消息内容
	 */
	public  void sendMsg(String topic, String content);
	
	
	/**
	 * 异步发送普通消息， 适合于日志类，对于精确度要求不高的场景。 不等待消息服务器应答。 
	 * 消息分区数为默认值3
	 * 
	 * @param topic   主题
	 * @param key     消息键
	 * @param content   消息内容
	 */
	public  void sendMsg(String topic, String key, String content);
	
	
	/**
	 * 分区数不是默认值3的，手工命令行创建分区； 分区数一定要和partition 返回的数字一致。
	 * @param topic
	 * @param partition
	 */
	public  void setPartition(String topic,Partition partition);
	
	
	/**
	 * 刷新发送缓存区的消息
	 */
	public  void flush();
	
	
	/**
	 * 关闭消息发送者，并释放资源。
	 */
	public  void close();
	
}
