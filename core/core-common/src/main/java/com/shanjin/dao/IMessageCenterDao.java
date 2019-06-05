package com.shanjin.dao;

import java.util.List;
import java.util.Map;


public interface IMessageCenterDao {

	/**
	 * 消息列表数量
	 */
	public int msgListCount(Map<String,Object> paramMap);
	
	/**
	 * 消息列表
	 */
	public List<Map<String,Object>> msgList(Map<String,Object> paramMap);
	
	/**
	 * 获取未读消息数量
	 */
	public int getUnreadMsgCount(Map<String,Object> paramMap);
//	/**
//	 * 获取未读消息数量
//	 */
//	public int getUnreadMsgCount(long maxMsgId);
	
	/**
	 * 标记为已读
	 */
	public int markReadMsg (Long id);
	
	/**
	 * 将已读信息插入到历史表
	 */
	public int insertToHistoryMsg(Long id);
	
	/**
	 * 删除已读消息
	 */
	public int delMarkRead(Long id);

	/**
	 * 删除消息
	 */
	public int delMsg (Map<String,Object> paramMap);
	/**
	 * 查询最大消息ID
	 * @param customerId
	 * @return    
	 * @throws
	 */
	public Long getMaxMsgId(String customerId);
	/**
	 * 保存最大的消息ID
	 * @param customerId
	 * @return    
	 * @throws
	 */
	public int saveMaxMsgId(Map<String,Object> paramMap);

    int saveCustomerMessageCenter(Map<String, Object> map) throws Exception;

}
