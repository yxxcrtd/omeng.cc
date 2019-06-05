package com.shanjin.dao;

import java.util.List;
import java.util.Map;


/**
 * 历史订单迁移的相关DAO
 * @author RevokeYu  2016.9.19
 *
 */
public interface IOrderDao {

	/** 查询需要进行迁移的已取消订单 */
	List<Map<String,Object>>  getCancelOrders(Map<String,Object> paramMap);
	
	
	
	/** 查询需要进行迁移的无报价方案的订单  */
	List<Map<String,Object>>  getNoBidOrders(Map<String,Object> paramMap);
	
	
	/** 查询需要进行迁移的未选择报价方案的订单  */
	List<Map<String,Object>>  getNoChoosedOrders(Map<String,Object> paramMap);
	
	
	
	/** 查询需要进行迁移的的正常订单  */
	List<Map<String,Object>>  getNormalOrders(Map<String,Object> paramMap);
	
	
	/**
	 * 
	 * @param orderIds
	 * @return
	 */
	List<Map<String,Object>>  getOrderSummaryList(Map<String,String> orderIds);
	
	
	/**
	 * 获取订单附件
	 * @param orderIds
	 * @return
	 */
	List<Map<String,Object>>  selectOrderAttachment(Map<String,Object> orderIds);
	
	
	
	/**
	 * 获取需要清理的已迁移的取消订单
	 * @param params
	 * @return
	 */
	List<Map<String,Object>>  getNeedPurifyCancelOrders(Map<String,Object> params);
	
	
	/**
	 * 获取需要清理的无报价方案关闭的订单
	 * @param params
	 * @return
	 */
	List<Map<String,Object>>  getNeedPurifyNobidOrders(Map<String,Object> params);
	
	
	/**
	 * 获取需要清理的未选中方案关闭的订单
	 * @param params
	 * @return
	 */
	List<Map<String,Object>>  getNeedPurifyNochoosedOrders(Map<String,Object> params);
	
	/**
	 * 获取需要清理的已完成订单
	 * @param params
	 * @return
	 */
	List<Map<String,Object>>  getNeedPurifyNormalOrders(Map<String,Object> params);
	
	
	/**
	 * 按id删除用户订单附件
	 * @param ids
	 */
	void delUserOrderAttachments(Map<String,Object> ids);
	
	
	
	/**
	 * 按id删除用户订单详情
	 * @param ids
	 */
	void delUserOrderDetail(Map<String,Object> ids);
	
	
	
	/**
	 * 按id删除用户订单
	 * @param ids
	 */
	void delUserOrderByIds(Map<String,Object> ids);
}
