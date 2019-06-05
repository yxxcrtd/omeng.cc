package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface IMessageCenterService {
	/**
	 * 消息列表
	 * @param paramMap
	 * @return
	 * @throws Exception    
	 * @throws
	 */
	public JSONObject msgList(Map<String,Object> paramMap) throws Exception;	

    /**
     * 消息标记为已读
     * @param id
     * @return
     * @throws Exception    
     * @throws
     */
    public JSONObject markReadMsg(Long id) throws Exception;

//    /**
//     * 查询未读消息数量
//     * @param paramMap
//     * @return
//     * @throws Exception    
//     * @throws
//     */
//    public JSONObject getUnreadMsgCount(Map<String,Object> paramMap) throws Exception;
    /**
     * 查询未读消息数量
     * @param paramMap
     * @return
     * @throws Exception    
     * @throws
     */
    public int getUnreadMsgCount(Map<String,Object> paramMap) throws Exception;

    /** 
     * 删除消息
     * @return
     * @throws
     */
    public JSONObject delMsg(String ids,int isRead)throws Exception;

    // 开通增值服务后，添加提醒消息
    int saveCustomerMessageCenter(Map<String, Object> map) throws Exception;

}
