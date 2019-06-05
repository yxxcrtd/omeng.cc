package com.shanjin.mongo.dao;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 历史报价方案查询 接口
 * @author Revoke 2016.10.9
 *
 */
public interface IMongoMerchantPlanDao {
	
	/**
	 * 按指定排序方式获取订单的报价方案列表
	 * @param orderId
	 * @param pageNo
	 * @param orderBy
	 * @param direction
	 * @return
	 */
	JSONObject  getMerchantPlanList(Long orderId,int pageNo,String orderBy,String direction);

	
	/**
	 * 获取特定的报价方案
	 * @param orderID
	 * @param merchantId
	 * @return
	 */
	Map<String,Object> getMerchantPlan(Long orderID,Long merchantId);
	
	/**
	 * 查询订单的服务记录
	 * @param orderId
	 * @param merchantId
	 * @return
	 */
	Map<String,Object>  getMerchantServiceRecord(Long orderId,Long merchantId);
}
