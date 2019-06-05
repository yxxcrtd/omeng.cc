package com.shanjin.dao;

import java.util.List;
import java.util.Map;


/**
 * 
 * 项目名称：core-order
 * 类名称：ICustomOrderDao 
 * 类描述：自定义订单相关操作（其中包括：表单生成，订单保存，发单人/接单人查询订单列表和详情接口）
 * 创建人：Huang yulai
 * 创建时间：2016年3月17日 上午10:39:52
 * 修改人：
 * 修改时间：
 * 修改备注：
 * @version V1.0
 */
public interface ICustomOrderDao {
	
	/**
	 * 根据服务别名查询服务
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getServiceByNick(Map<String, Object> param) throws Exception;

	/**
	 * 获取订单空表单版本
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getOrderFormVersion(Map<String, Object> param);
	
	/**
	 * 获取订单空表单（表单生成）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOrderForm(Map<String, Object> param);
	
	/**
	 * 获取订单消费引导banner
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getOrderBanner(Map<String, Object> param);
	
	/**
	 * 获取控件参考数据
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getControlData(Map<String, Object> param);
	
	/**
	 * 保存订单
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int saveCustomOrderDetail(Map<String, Object> param);
	
	/**
	 * 根据订单id查询详情
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getOrderDetailByOrderId(Map<String, Object> param);
	
	/**
	 * 订单列表查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOrderListForSender(Map<String, Object> param);
	
	/**
	 * 订单列表查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOrderListForReceiver(Map<String, Object> param);
	
	/**
	 * 订单详情查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getOrderDetailForSender(Map<String, Object> param);
	
	/**
	 * 订单详情查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getOrderDetailForReceiver(Map<String, Object> param);

	/**
	 * 获取TOP N 用户订单列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getTopnBasicOrderListInfo(Map<String, Object> params);
	
	/**
	 * 获取报价方案空表单版本
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getPlanFormVersion(Map<String, Object> param) throws Exception;
	
	/**
	 * 获取报价方案空表单（表单生成）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getPricePlanForm(Map<String, Object> param) throws Exception;
	
	/**
	 * 保存报价方案
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int savePricePlan(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案列表查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getPricePlanListForSender(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案列表查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getPricePlanListForReceiver(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案详情查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getPricePlanDetailForSender(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案详情查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getPricePlanDetailForReceiver(Map<String, Object> param) throws Exception;
	
	  
	  /**
	   * 获取发单分类
	   * @return
	   */
	public List<Map<String,Object>>  getCatalogList(Map<String,Object> param);
	  
	  /**
	   * 获取服务
	   * @return
	   */
	public List<Map<String,Object>>  getServiceList(Map<String,Object> param);
	
	/**
	 * 将订单状态设为已读
	 * @param param
	 * @return    
	 * int   
	 * @throws
	 */
	public Integer setReadStatus(Map<String,Object> param);
	
	String getAppTypeByMerchantId(Long merchantId);
	
	/**
	 * 商户服务完成后保存服务记录
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Integer saveMerchantServiceRecord(Map<String,Object> param);
	
	/**
	 * 商户服务完成后修改服务记录
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Integer updateMerchantServiceRecord(Map<String,Object> param);
	/**
	 * 评价附件保存
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Integer saveEvaluationAttachment(Map<String,Object> param);
	
	/**
	 * 商户服务完成后保存服务记录
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Integer saveMerchantServiceRecordAttachment(Map<String,Object> param);
	
	/**
	 * 商户服务完成后修改服务记录
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Integer deleteMerchantServiceRecordAttachment(Map<String,Object> param);
	/**
	 * 修改订单实付金额
	 * @param param
	 */
	public void updOrderActualPrice(Map<String,Object> param);
	/**
	 * 修改订单实付金额
	 * @param param
	 */
	public void updOrderMerchantActualPrice(Map<String,Object> param);
	/**
	 * 查询服务记录
	 * @param param
	 * @return
	 */
	public Map<String, Object> getMerchantServiceRecord(Map<String,Object> param);
	/**
	 * 商户确认订单支付金额
	 * @param orderId
	 * @return
	 */
	public Map<String, Object> getPlanPriceInfo(Map<String,Object> param);

	/** 
	 * 商户订单数量查询（根据状态查询）
	 * @param param
	 * @return 商户订单数量
	 */
	int selectOrderListForReceiverByStatusCount(Map<String, Object> param);
	
	/** 
	 * 查询订单信息
	 * @param param
	 * @return 商户订单列表
	 */
	List<Map<String, Object>> getMerchantOrderInfo(Map<String, Object> param);
	
	/** 
	 * 查询订单报价方案信息
	 * @param param
	 * @return 商户订单列表
	 */
	List<Map<String, Object>> getOrderPlanList(Map<String, Object> param);
	
	/** 
	 * 查询订单报价方案信息
	 * @param param
	 * @return 商户订单列表
	 */
	List<Map<String, Object>> getOrderPlanListV110(Map<String, Object> param);
	/** 
	 * 查询订单报价方案信息
	 * @param param
	 * @return 商户订单列表
	 */
	List<Map<String, Object>> getOrderPlanCountList(Map<String, Object> param);
	
	/**
	 * 查询商户订单列表
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> getMerchantOrderList(Map<String, Object> param);
	
	/**
	 * 查询商户订单详情
	 * @param param
	 * @return
	 */
	Map<String, Object> getMerchantOrderMap(Map<String, Object> param);
	
	/**
	 * 保存报价表单详情
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int saveCustomPlanDetail(Map<String, Object> param);
	
	/**
	 * 修改报价表单详情
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateCustomPlanDetail(Map<String, Object> param);
	
	/**
	 * 根据方案id查询详情
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getPlanDetailByPlanId(Map<String, Object> param);


	/** 根据订单Id查询订的状态 */
	int selectOrderStatus(Map<String, Object> paramMap);
	/** 根据订单Id查询订的状态 */
	Map<String,Object> selectOrderStatusMap(Map<String, Object> paramMap);

	/** 更新订单的状态 */
	int updateOrderStatus(Map<String, Object> paramMap);

	/** 保存取消理由 */
	int saveCancelReason(Map<String, Object> paramMap);

	/**
	 * 获取订单服务状态记录
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> getTimeline(Map<String, Object> param);
	
	/**
	 * 获取订单状态对应的描述
	 */
	String getOrderStatusText(Map<String, Object> param);
	
	
	/**
	 * 获取订单状态对应的描述列表
	 * @return
	 */
	List<Map<String,Object>> getOrderStatustTextList();
	
	
	int shieldOrder(Map<String, Object> param);
	
	/**
	 * 查询商户头像和店名用于查询服务记录
	 * @param param
	 * @return
	 */
	Map<String,Object> getMerchantInfo(Map<String, Object> param);
	
	/**
	 * 获取订单价格信息
	 * @param param
	 * @return
	 */
	Map<String,Object> getOrderPriceInfo(Map<String, Object> param);
	
	/**
	 * 获取商户订单状态
	 * @param param
	 * @return
	 */
	Integer getMerchantOrderStatus(Map<String, Object> param);

	/** 更新商户订单的状态 */
	int updateMerchantOrderStatus(Map<String, Object> paramMap);
	
	Long getReceiveEmployeeUserId(Map<String, Object> paramMap);
	
	/**查询商户表标记位**/
	int getMerchantFlag(Map<String, Object> paramMap);
	
	/**更新商户表标记位**/
	int updateMerchantFlag(Map<String, Object> paramMap);
	
	/**查询商户订单数**/
	int getMerchantOrderCount(Map<String, Object> paramMap);
	
	/**更新订单余额**/
	int updateOrderSurplusPrice(Map<String, Object> paramMap);
	
	
	/** 获取某订单对应的推送商户ID列表**/
	List<Map<String,Object>> getMerchantsForSpeicalOrder(Map<String, Object> paramMap);
	
	  /**
	   * 获取推荐服务
	   * @return
	   */
	public List<Map<String,Object>>  getRecommedServiceList(Map<String, Object> paramMap);
	
	/**
	 * 用户取消的订单，删除商户侧订单中的推送记录
	 * @param orderId
	 */
	public  void removeCanceldOrderPushInfo(String orderId);
	
	
	/**
	 * 未提供报价方案的推送记录，删除商户侧订单
	 * @param orderId
	 */
	public  void removeNoBidOrderPushInfo(String orderId);
	
	
	/**
	 * 删除商户推送表中无报价方案的推送记录
	 */
	public  void removeOrderPushWithoutPlan(Map<String,String> param);
	
	
	/**
	 * 获取某订单提供报价方案的商户id
	 * @param orderId
	 * @return
	 */
	public  String getMerchantHasPlan(String orderId);
	
	/**
	 * 获取订单的商户报价方案数量
	 * @param 
	 * @return
	 */
	public  List<Map<String,Long>> getMerchantsPlan(Map<String,Object> param);
	
	/**
	 * 查询报价方案简介
	 * @param param
	 * @return
	 */
	public Map<String,Object> getOrderPlanBrief(Map<String,Object> param);

	
	
	/**
	 * 获取订单推送的总商户数
	 * @param orderId
	 * @return
	 */
	public Integer  getPushMerchantNum(Long orderId);
	
	
	/**
	 * 获取一页订单推送的商户列表
	 * @param param
	 * @return
	 */
	public List<Map<String,Object>>  getPushMerchantInfos(Map<String,Object> param);
	
	/**
	 * 获取时间轴描述
	 * @param excludeCode
	 * @return
	 */
	public List<Map<String,Object>>  getTimeLineText(Map<String,Object> paramMap);

	/**
	 * 获取为删除并且已发布的服务数
	 * @param excludeCode
	 * @return
	 */
	public Integer getServiceTypeStatus(Map<String, Object> param);
	
	public String getOrderNoByOrderId(Long orderId);
	
	public Long getOrderIdByOrderNo(String orderNo);

	/**
	 * 批量返还抢单费
	 * @param bidFreeList
	 * @return
	 */
	int batchAddReturnBidFee(List<Map<String, Object>> bidFreeList);
	
	/**
	 * 批量修改商户的余额
	 * @param params
	 */
	public void updateMerchantStaticsByIds(Map<String,Object> params);

    Integer insertMQSendFailure(Map<String, Object> params);

    void updateMQSendFailure(Long id);

    List<Map<String, Object>> getUnSentList();

    List<Map<String,Object>> getNoPlanOrderList(Map<String, Object> map);
    
    /**
     * 保存报价方案-商品信息
     * @param map
     * @return
     */
    Integer  savePlanAndGoodsInfo(List<Map<String,Object>> goodsHistoryList);
    
    /**
     * 查看报价方案的商品信息
     * @param map
     * @return
     */
    List<Map<String,Object>> getOrderPlanGoodsList(Map<String, Object> map);
    
    /**
     * 修改店铺营业状态
     * @param merchantId
     * @return
     */
    Integer updMerchantBusinessType(Map<String, Object> map);
    
    Integer getIsPrivateAssistantByMerchantId(Map<String, Object> map);
    
    /**
     * 根据订单IDS 获取订单服务时间列表
     * @param map
     * @return
     */
    List<Map<String,Object>> getOrderServiceTimeByIds(Map<String,Object> map);

    Integer findPaymentByTradeNo(Map<String, Object> params);

    List<Map<String, Object>> findPaymentByOrderId(Map<String, Object> params);

    Long findConsumeBizIdByTradeNo(Map<String, Object> paramMap);

    Integer insertMerchantPaymentDetails(Map<String, Object> params);

    Integer updatePaymentStatus(Map<String, Object> params);

    /**
 	 * 
 	 *  ｛商户订单确认服务完成｝
 	 *  @param orderId
 	 *  @return
 	 *  @author Liuxingwen
 	 *  @created 2016年10月26日 上午10:52:42
 	 *  @lastModified       
 	 *  @history
 	 */
 	public int completionOrder(Map<String, Object> paramMap);
 	
 	/**
 	 * 查询商户订单的支付次数
 	 * @param paramMap
 	 * @return
 	 */
 	public int getOrderPayCount(Map<String, Object> paramMap);
	/** 
	 * 查询订单报价方案信息
	 * @param param
	 * @return 商户订单列表
	 */
	int getOrderPlanCount(Map<String, Object> param);
 	
 	/**
 	 * 查询商户订单的支付记录
 	 * @param paramMap
 	 * @return
 	 */
 	public List<Map<String, Object>> getOrderPayList(Map<String, Object> paramMap);
 	
 	public Map<String, Object>getOrderEvaluationInfo(Map<String, Object> paramMap);
 	
 	public int updateOrderActualPrice(Map<String, Object> paramMap);
}
