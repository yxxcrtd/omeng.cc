package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 订单表Dao
 */
public interface IOrderInfoDao {

	/** 查询已评价订单的数量 */
	int selectEvaluationOrderNum(Map<String, Object> paramMap);

	/** 预览店铺的时候 查看顾客评价 */
	List<Map<String, Object>> selectUserEvaluation(Map<String, Object> paramMap);

	/** 订单类型查询订单记录数 */
	// int selectOrderCount(Map<String, Object> paramMap);

	/** 订单类型查询 */
	// List<Map<String, Object>> selectOrder(Map<String, Object> paramMap);

	/** 订单详情查询 */
	// Map<String, Object> selectOrderDetail(Map<String, Object> paramMap);

	/** 验证这个单有没有被抢过 */
	int checkImmediatelyOrder(Map<String, Object> paramMap);
	
	/**获取订单服务时间*/
	String getOrderServiceTime(Map<String, Object> paramMap);

	/** 根据订单Id查询订的状态 */
	int selectOrderStatus(Map<String, Object> paramMap);

	/** 更新订单的状态 */
	int updateOrderStatus(Map<String, Object> paramMap);

	/** 更新商户订单的状态 */
	int updateMerchantOrderStatus(Map<String, Object> paramMap);

	/** 更新商户订单的状态 */
	int updateOtherMerchantOrderStatus(Map<String, Object> paramMap);

	/**
	 * 更新订单接单人,订单的状态 int updateReceiveEmployee(Map<String, Object> paramMap);
	 */

	/** 根据订单Id查询订单的类型 */
	Long selectServiceType(Map<String, Object> paramMap);

	/**
	 * 查看当前订单的客户评价 Map<String, Object> lookEvaluation(Map<String, Object>
	 * paramMap);
	 */

	/** 根据订单Id查询订单状态和商户Id */
	Map<String, Object> selectStatusAndMerchantId(Map<String, Object> paramMap);

	/** 处理过期订单 */
	int handleBackOrders(Map<String, Object> paramMap);
	
	
	
	/** 批更新用户端订单状态*/
	int batchUpdateClientOrderStatus(Map<String,Object> paramMap);
	
	
	
	/** 批更新商户侧订单状态*/
	int batchUpdateMerchantOrderStatus(Map<String,Object> paramMap);
	

	/** 查询未选择报价方案的过期订单 前100条 */
	List<Map<String, String>> getNoChoosedExpiredOrders(Map<String, Object> paramMap);
	/** 查询未选择报价方案的过期订单 前100条20161014 */
	List<Map<String, String>> selectNoChoosedExpiredOrders(Map<String, Object> paramMap);
	
	/** 查询无报价方案的过期订单的前 100条  */
	List<Map<String,String>>  getNoBidExpiredOrders(Map<String,Object> paramMap);
	/** 查询无报价方案的过期订单的前 100条20161014  */
	List<Map<String,String>>  selectNoBidExpiredOrders(Map<String,Object> paramMap);
	
	/** 查询用户取消的订单-已过清理时间间隔的订单的前 100条  */
	List<Map<String,Object>>  getPurifyCancelOrders(Map<String,Object> paramMap);
	
	
	/** 查询无报价方案的过期订单-已过清理时间间隔的订单的前 100条  */
	List<Map<String,Object>>  getPurifyExpireNoBidOrders(Map<String,Object> paramMap);
	
	
	
	/** 查询未选定报价方案的过期订单-已过清理时间间隔的订单的前 100条  */
	List<Map<String,Object>>  getPurifyExpireNoChoosedOrders(Map<String,Object> paramMap);
	
	
	/** 查询进行中或已完成的订单-已过清理时间间隔的订单的前 100条  */
	List<Map<String,Object>>  getPurifyInProcessOrders(Map<String,Object> paramMap);
	
	
	/** 查询报价方案已过期的订单-已过返还处理时间间隔的订单的前 100条  */
	List<Map<String,Object>>  getNeedReturnBidFeeOrders(Map<String,Object> paramMap);
	/** 查询报价方案已过期和取消的订单-已过返还处理时间间隔的订单的前 100条  */
	List<Map<String,Object>>  selectNeedReturnBidFeeOrders(Map<String,Object> paramMap);
	
	

	/** 处理待选择的过期订单 */
	int handleChoiceBackOrders(Map<String, Object> paramMap);

	/** 处理待选择的过期订单 */
	List<Map<String, String>> getChoiceExpiredLongerOrders(Map<String, Object> paramMap);

	/** 基础订单记录数查询 */
	int selectBasicOrderCount(Map<String, Object> paramMap);

	/** 基础订单查询 */
	List<Map<String, Object>> selectBasicOrder(Map<String, Object> paramMap);

	/** 订单详情通用信息 */
	Map<String, Object> selectOrderGeneral(Map<String, Object> paramMap);

	/** 订单详情附加信息 */
	// Map<String, Object> selectOrderNote(Map<String, Object> paramMap);

	/** 订单详情业务信息 */
	// Map<String, Object> selectOrderText(Map<String, Object> paramMap);

	List<Map<String, Object>> selectOrderAttachment(Map<String, Object> paramMap);

	/** 订单详情的用户信息 */
	Map<String, Object> selectOrderUserInfo(Map<String, Object> paramMap);

	/** 订单详情的方案信息 */
	Map<String, Object> selectOrderPlanInfo(Map<String, Object> paramMap);

	/** 商户抢单次数查询 */
	Map<String, Object> selectGrabFrequency(Map<String, Object> paramMap);

	/** 订单详情的商品信息 */
	List<Map<String, Object>> selectOrderGoodsInfo(Map<String, Object> paramMap);

	/**
	 * 获取订单过期时间
	 * @return
	 */
	Integer getExpiredTime(String configEntry);
	
	/** 测试用接口-认证申请通过 */
	int testAuthPass(Long merchantId);

	/** 获取某订单对应的推送商户ID列表**/
	List<Map<String,Object>> getMerchantsForSpeicalOrder(Map<String, Object> paramMap);
	
	/**
	 * 获取配置表中特定配置项信息
	 * @param configKey
	 * @return
	 */
	Map<String,Object> getConfigByKey(String configKey);
	
	int testUpdateMerchant(Long merchantId);

	/**
	 * 更新订单方案已读
	 * @param params
	 * @return
	 */
	int readMerchantPlan(Map<String, Object> params);

	/**
	 * 根据订单id查找用户订单评价
	 * @param paramMap
	 * @return
	 */
	Map getEvaluateByOrderId(Map<String, Object> paramMap);
	
	
}
