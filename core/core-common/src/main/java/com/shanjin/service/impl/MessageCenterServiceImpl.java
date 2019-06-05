package com.shanjin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.dao.IMessageCenterDao;
import com.shanjin.service.IMessageCenterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("messageCenterService")
public class MessageCenterServiceImpl implements IMessageCenterService {
	@Resource
	private IMessageCenterDao messageCenterDao;
	/**
	 * 消息列表
	 */
	public JSONObject msgList(Map<String,Object> paramMap)throws Exception{		
		JSONObject jsonObject=new ResultJSONObject("000", "获得消息列表成功");
		int count=messageCenterDao.msgListCount(paramMap);
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		if(count>0){	
			int pageNo=Integer.parseInt(paramMap.get("pageNo")==null?"0":paramMap.get("pageNo").toString());
			paramMap.put("rows", pageNo * Constant.PAGESIZE);// 查询起始记录行号			
			paramMap.put("pageSize", Constant.PAGESIZE);// 每页显示的记录数			
			list=messageCenterDao.msgList(paramMap);
		}
		jsonObject.put("totalPage", BusinessUtil.totalPageCalc(count));
		jsonObject.put("msgList", list);
		
		//将最大的消息ID保存起来
		String customerType=paramMap.get("customerType")==null?"0":paramMap.get("customerType").toString();
		String customerId=paramMap.get("customerId")==null?"0":paramMap.get("customerId").toString();
		if(customerType.equals("1")){//商户
			//查询最大的消息ID
			Long maxMsgId=messageCenterDao.getMaxMsgId(customerId);
			if(maxMsgId!=null && maxMsgId>0){
				Map<String,Object> param=new HashMap<String, Object>();
				param.put("customerId", customerId);
				param.put("maxMsgId", maxMsgId);
				//将消息ID保存到商户表中
				messageCenterDao.saveMaxMsgId(param);
			}
		}
		return jsonObject;
	}

//    /**
//     * 查询未读消息数量
//     * @param paramMap
//     * @return
//     * @throws Exception    
//     * @throws
//     */
//    public JSONObject getUnreadMsgCount(Map<String,Object> paramMap) throws Exception{
//    	int count=messageCenterDao.getUnreadMsgCount(paramMap);
//    	JSONObject jsonObject=new ResultJSONObject("000", "获得未读消息数量成功");
//    	jsonObject.put("unreadMsgCount", count);
//    	return jsonObject;
//    }
    /**
     * 查询未读消息数量
     */
    public int getUnreadMsgCount(Map<String,Object> paramMap) throws Exception{
    	return messageCenterDao.getUnreadMsgCount(paramMap);
    }
	/**
	 * 消息标记为已读
	 */
	@Transactional(rollbackFor = Exception.class)
	public JSONObject markReadMsg(Long id)throws Exception{
		
		JSONObject jobj=new ResultJSONObject("000", "消息标记为已读成功");
		int i=messageCenterDao.insertToHistoryMsg(id);		
		if(i>0){
			messageCenterDao.delMarkRead(id);
		}		
		return jobj;
	}
	/**
	 * 删除消息
	 */
	@Transactional(rollbackFor = Exception.class)
	public JSONObject delMsg(String ids,int isRead)throws Exception{
		String ids_str="";
		for (String id : ids.split(",")) {
			ids_str+=id+",";
		}
		if(!ids_str.equals("") && ids_str.contains(",")){
			ids_str=ids_str.substring(0,ids_str.length()-1);
		}
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("ids", ids_str);
		paramMap.put("isRead", isRead);
		JSONObject jobj=new ResultJSONObject("000", "删除消息成功");
		int i=messageCenterDao.delMsg(paramMap);
		if(i==0){
			jobj=new ResultJSONObject("delMsg_failure", "删除消息失败");
		}
		return jobj;
	}

    @Transactional(rollbackFor = Exception.class)
    public int saveCustomerMessageCenter(Map<String, Object> map) throws Exception {
        return messageCenterDao.saveCustomerMessageCenter(map);
    }

}
