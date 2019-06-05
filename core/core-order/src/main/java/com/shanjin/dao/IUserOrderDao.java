package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 用户订单业务DAO
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */
public interface IUserOrderDao {

	/** 保存通用订单 */
	int insertCommonOrder(Map<String, Object> paramMap);

	/** 保存订单附件 */
	int insertOrderAttachment(Map<String, Object> paramMap);

	/** 验证该用户的统计表是否已经存在 */
	int checkUserStatisticsIsEmpty(Map<String, Object> paramMap);

	/** 注册成功之后初始化用户统计信息表 */
	int initUserInfoStatistics(Map<String, Object> paramMap);

	/** 更改用户的统计 */
	int updateUserSstatisticsBespeak(Map<String, Object> paramMap);

	/** 获取订单的用户ID */
	Long getOrderUserId(Map<String, Object> paramMap);

	/** 获取订单的商户ID */
	Long getOrderMerchantId(Map<String, Object> paramMap);

    /** 根据订单ID获取订单编号 */
    String getOrderNoByOrderId(Map<String, Object> paramMap);

    Map<String,Object> getPaymentTime(Map<String,Object> param);

	/** 获取订单方案的的接单人ID */
	Long getMerchantPlanReceiveEmployeesId(Map<String, Object> paramMap);

	/** 获取订单方案代金券ID */
	Long getOrderVouchersId(Map<String, Object> paramMap);

	/** 获取订单的金额 */
	Double getOrderPrice(Map<String, Object> paramMap);

	/** 获取订单的实际支付金额 */
	Double getOrderActualPrice(Map<String, Object> paramMap);

	/** 获取订单供应商列表 */
	List<Map<String, Object>> getOrderMerchantPlan(Map<String, Object> paramMap);

	/** 获取订单供应商列表总页数 */
	int getOrderMerchantPlanTotalPage(Map<String, Object> paramMap);

	/** 获取订单评价信息 */
	List<Map<String, Object>> getAssessOrder(Map<String, Object> paramMap);

	/** 验证是否评价订单 */
	int checkAssessOrder(Map<String, Object> paramMap);

	/** 评价订单 */
	int assessOrder(Map<String, Object> paramMap);

	/** 更改商户的统计 */
	int updateAssessMerchantStatistics(Map<String, Object> paramMap);

	/** 验证是否选择商户方案 */
	int checkChooseMerchantPlan(Map<String, Object> paramMap);

	/** 为订单选择商户方案 */
	int chooseMerchantPlan(Map<String, Object> paramMap);

	/** 获取用户此次订单可以使用的代金券列表 */
	List<Map<String, Object>> getUserAvailablePayVouchersInfo(Map<String, Object> paramMap);

	/** 获取用户此次订单可以使用的代金券列表总页数 */
	int getUserAvailablePayVouchersInfoTotalPage(Map<String, Object> paramMap);

	/** 确认订单金额 */
	int confirmOrderPrice(Map<String, Object> paramMap);

	/** 完成支付宝订单 */
	int finishPayOrder(Map<String, Object> paramMap);

	/** 完成微信订单 */
	int finishWeChatOrder(Map<String, Object> paramMap);

	/** 完成现金订单 */
	int finishCashOrder(Map<String, Object> paramMap);
	
	
	/** 设置订单状态为完成 */
	int setOrderFinished(Map<String, Object> paramMap);
	

	/** 完成订单更改用户的统计 */
	int updateUserStatisticsService(Map<String, Object> paramMap);

	/** 完成现金订单更改用户的统计 */
	int updateUserStatisticsCashService(Map<String, Object> paramMap);

	/** 完成订单更改商户的统计 */
	int updateMerchantStatisticsService(Map<String, Object> paramMap);

	/** 完成现金订单更改商户的统计 */
	int updateMerchantStatisticsCashService(Map<String, Object> paramMap);

	/** 更新代金券状态值 */
	int updateUserVouchersInfo(Map<String, Object> paramMap);

	/** 更新商户代金券的使用情况 */
	int updateMerchantVouchersInfo(Map<String, Object> paramMap);

	/** 插入商户的收支明细记录 */
	int insertMerchantPaymentDetails(Map<String, Object> paramMap);

	/** 删除订单 */
	int deleteOrder(Map<String, Object> paramMap);

	/** 更改订单状态 */
	int updateOrderStatus(Map<String, Object> paramMap);

	/** 获取所有appType对应的商户clentId */
	List<Map<String, Object>> getMerchantClientIds(Map<String, Object> paramMap);

	/** 获取订单指定的商户clentId */
	List<Map<String, Object>> getMerchantOneClientId(Map<String, Object> paramMap);

	/** 根据订单ID获得用户的clentid */
	List<Map<String, Object>> getUserClientIdsByOrderId(Map<String, Object> paramMap);

	/** 根据订单ID获得订单的serviceType */
	int getServiceType(Map<String, Object> paramMap);

	/** 获得订单推送状态 */
	int getOrderPushType(Map<String, Object> paramMap);

	/** 判断订单是否被评价 */
	int checkIsEvaluation(Map<String, Object> paramMap);

	/** 判断商户是否存在该客户 */
	int checkMerchantUsers(Map<String, Object> paramMap);

	/** 给商户新增客户信息 */
	int insertMerchantUsers(Map<String, Object> paramMap);

	/** 更改商户的客户信息 */
	int updateMerchantUsers(Map<String, Object> paramMap);

	/** 记录推送的订单和商户ID */
	int insertPushMerchantOrder(Map<String, Object> paramMap);

	/** 获取APP列表 */
	List<Map<String, Object>> getAPPList();

	/** 获取基础订单详情 */
	Map<String, Object> getBasicOrderInfo(Map<String, Object> paramMap);

	/** 获取订单详情中的订单信息 */
	Map<String, Object> getOrderInfo(Map<String, Object> paramMap);

	/** 获取订单详情中的商户信息 */
	Map<String, Object> getOrderMerchantInfo(Map<String, Object> paramMap);
	
	
	/**
	 * 根据商户ID 获取商户详细信息
	 * @param merchantId
	 * @return
	 */
	Map<String, Object> getOrderMerchantInfoById(String merchantId);


	/** 获取基础订单列表信息 */
	List<Map<String, Object>> getBasicOrderListInfo(Map<String, Object> paramMap);

	/** 获取基础订单列表信息总数 */
	int getBasicOrderListInfoTotalPage(Map<String, Object> paramMap);

	/** 获取TOP N 用户订单列表 */
	List<Map<String, Object>> getTopnBasicOrderListInfo(Map<String, Object> params);

	/** 根据城市得到商家ClientId */
	List<Map<String, Object>> getClientIdsByCity(Map<String, Object> paramMap);

	/** 插入订单的商品信息 */
	int insertOrderGoods(List<Map<String, Object>> goodsOrderList);

	/** 获得易学堂的订单图标 */
	String getYxtAppIcon(String orderId);

	/** 获得易学堂的全部订单图标 */
	List<Map<String, Object>> getAllYxtAppIcon();

	/** 获得易学堂的订单信息 */
	List<Map<String, Object>> getYxtOrderInfo(List<String> orderIds);

	Map<String, String> getYxtServiceTypeByOrderId(Map<String, Object> params);
	
	Long getReceiveEmployeesIdFromMerchantPlan(Map<String, Object> map);

	List<Map<String,Long>>  getServiceTypeIdsByCatalogId(Long catalogId);
	
	String getAliasByOrderId(Long orderId);
	
	/**
	 * 获取用户聊天的token
	 * @param userId
	 * @return
	 */
	String getRongYunToken(Long userId);
	
	int getOrderPayRecord(Map<String, Object> params);
	/** 根据订单Id查询订的状态 */
	int selectOrderStatus(Map<String, Object> paramMap);
	
	Long getOrderServiceTypeId(Map<String, Object> paramMap);

    Integer accumulateOrderPrice(Map<String, Object> params);

    /**
     * 根据订单查找所有推送的商户id
     * @param planParamMap
     * @return
     */
	List<Map<String, Object>> getOrderPushIds(Map<String, Object> planParamMap);

}