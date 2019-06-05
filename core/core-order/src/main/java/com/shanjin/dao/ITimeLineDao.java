package com.shanjin.dao;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author Revoke 2016.5.13 
 *
 */
public interface ITimeLineDao {
	
	
	/** 新增用户行为 **/
	int insertTimeLine(Map<String, Object> paramMap);
	
	
	
	/**
	 * 按订单号查询某订单相关的时间轴信息
	 * @param orderId
	 * @return
	 */
	List<Map<String,Object>> getTimeLineByOrderId(Integer orderId);

}
