package com.shanjin.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 用户订单业务接口
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */
public interface IUserOrderService {

	/** 获取用户订单列表详情 */
	public JSONObject getBasicOrderList(Long catalogId, Long userId, String orderStatus, int pageNo) throws Exception;

	/** 获取某条用户列表信息 */
	public JSONObject getBasicOrder(Long orderId) throws Exception;

	/** 获取O盟订单详情 */
	public JSONObject getDetialOrderInfo(String appType, Long orderId, Long serviceType) throws Exception;

	/** 获取基础订单列表详情 */
	public List<Map<String, Object>> getBasicOrderListInfo(String appType, Long userId, String orderStatus, int pageNo);

	/** 获取基础订单列表总页数 */
	public int getBasicOrderListInfoTotalPage(String appType, String orderStatus, Long userId);

	/** 获取订单供应商列表 */
	public JSONObject getOrderMerchantPlan(String appType, Long orderId, int pageNo, String orderBy) throws Exception;

	/** 为订单选择商户方案 */
	public int chooseMerchantPlan(String appType, Long merchantId, Long merchantPlanId, Long orderId) throws Exception;

	/** 获取订单评价信息 */
	public List<Map<String, Object>> getAssessOrder(Long orderId) throws Exception;

	/** 对订单进行评价 */
	public int assessOrder(String appType, Long orderId, String attitudeEvaluation, String qualityEvaluation, String speedEvaluation, String textEvaluation) throws Exception;

	/** 获取用户此次订单可以使用的代金券列表 */
	public JSONObject getUserAvailablePayVouchersInfo(String appType, Long userId, Long serviceType, Long merchantId, int pageNo) throws Exception;

	/** 确认订单 金额 */
	public int confirmOrderPrice(Long orderId, Long merchantId, Double price, Long vouchersId, Double actualPrice) throws Exception;

	/** 完成支付宝订单 */
	public int finishAliPayOrder(String appType, Long orderId) throws Exception;

	/** 完成微信订单 */
	public int finishWeChatOrder(String appType, Long orderId);

	/** 完成现金订单 */
	public int finishCashOrder(String appType, Long orderId, Long merchantId, String price);

	/** 删除订单 */
	public int deleteOrder(Long orderId) throws Exception;

	/** 更改订单状态 */
	public int updateOrderStatus(Long orderId, String orderStatus);

	/** 取消订单 */
	public JSONObject cancelOrder(Long orderId);

	/** 推送给商户端 */
	public JSONObject pushMessageToListForSHB(Map<String, Object> paras);

	/** 推送给用户端 */
	public JSONObject pushMessageToListForYHB(Map<String, String[]> map) throws Exception;

	/** 推送给商户端根据代理 */
	public JSONObject pushMessageToListForSHBByProxy(Map<String, Object> paras) throws Exception;

	/** 推送给具体的商户端根据代理 */
	public JSONObject pushMessageToListForSingleSHBByProxy(Map<String, Object> paras);

	/** 推送给用户端根据代理 */
	public JSONObject pushMessageToListForYHBByProxy(Map<String, Object> map);

	/** 获得订单的推送状态 */
	public int getOrderPushType(Long orderId);

	/** 处理未选择报价方案的过期订单 */
	public int handleNoChoosedOrders();
	
	/** 处理无报价方案的过期订单  */
	public int handNoBidOrder();
	
	
	
	/** 无报价方案的过期订单 ---清理推送记录及商户侧缓存 */
	public int handlePurifyNoBidExpireOrder();
	
	
	/**
	 * 未选定报价方案的过期订单---清理推送记录及商户侧缓存
	 * @return
	 */
	public int handlePurifyNoChoosedOrders();
	
	/**
	 * 用户主动取消的订单-----清理推送记录及商户侧缓存
	 * @return
	 */
	public int handlePurifyCancelOrder();
	
	
	/**
	 * 处理中的订单-------清理推送记录及商户侧缓存
	 * @return
	 */
	public int handlePurifyInProcessOrder();
	
	
	/** 报价方案的过期 返回抢单金*/
	public int handleReturnBidFee();

	/**
	 * 
	 * @param appType
	 *            行业类型
	 * @param serviceType
	 *            服务类型
	 * @param orderInfo
	 *            订单信息
	 * @return
	 */
	public JSONObject saveOrder(String appType, String serviceType, Map<String, Object> orderInfo, List<String> voicePaths, List<String> picturePaths, String ip) throws Exception;

	/**
	 * 清理用户缓存
	 */
	public void cleanUserCache();

	/**
	 * 清里指定用户的订单缓存
	 * 
	 * @param userId
	 * @return
	 */
	public JSONObject removeUserCachedOrders(String userId) throws Exception;

	/** 保存用户选择的商品 */
	public JSONObject saveOrderGoods(String appType, Long orderId, Long merchantId, Long merchantPlanId, String goodsIds, String goodsNums);

	/** 测试用户订单列表 */
	public JSONObject testOrderList(Long userId, int pageNo);

	/** 测试订单推送 */
	public JSONObject testOrderPush(Map<String, Object> para);

}
