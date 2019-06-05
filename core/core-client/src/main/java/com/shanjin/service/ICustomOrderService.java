package com.shanjin.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.exception.ApplicationException;

/**
 * 
 * 项目名称：core-client
 * 类名称：ICustomOrderService 
 * 类描述：自定义订单接口（其中包括：表单生成，订单保存，发单人/接单人查询订单列表和详情接口）
 * 创建人：Huang yulai
 * 创建时间：2016年3月17日 上午10:54:25
 * 修改人：
 * 修改时间：
 * 修改备注：
 * @version V1.0
 */
public interface ICustomOrderService {
	
	/**
	 * 获取发单分类
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getRecommedServiceList(String httpStr) throws Exception;
	
	/**
	 * 获取发单分类
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> orderCatalogList(String httpStr) throws Exception;
	
	/**
	 * 获取第三方服务列表
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> thirdServiceList(String catalogId) throws Exception;
	
	/**
	 * 获取发单一级分类
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOrderCatalogList() throws Exception;
	
	/**
	 * 根据一级分类ID获取发单二级分类及服务
	 * @param catalogId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOrderCatalogAndServiceList(String catalogId) throws Exception;
	
	/**
	 * 获取订单空表单版本
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getCustomOrderFormVersion(String serviceId) throws Exception;
	
	/**
	 * 获取订单空表单（表单生成）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getCustomOrderForm(String serviceId) throws Exception;
	
	/**
	 * 获取订单空表单（表单生成,重新发单）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getCustomOrderFormByOrderId(String serviceId,String orderId) throws Exception;
	
	/**
	 * 获取订单消费引导
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getCustomOrderBanner(String serviceId) throws Exception;
	
	/**
	 * 保存订单
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject saveCustomOrder(String serviceId, Map<String, Object> orderInfoMap, List<String> voicePaths, List<String> picturePaths, String ip) throws Exception;
	
	/**
	 * 订单列表查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderListForSender(Map<String, Object> params) throws Exception;
	
	
	
	/**
	 * 订单列表查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderListForReceiver(Map<String, Object> params) throws Exception;
	/**
	 * 订单列表查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderListForReceiverV1110(Map<String, Object> params) throws Exception;
	
	/**
	 * 订单详情查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderDetailForSender(String orderId) throws Exception;
	
	
	/**
	 * 订单详情查询（发单用户视图）-----V730 版本  Revoke Yu
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderDetailForSenderV730(String orderId) throws Exception;


	/**
	 * 订单详情查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderDetailForReceiver(Map<String, Object> params) throws Exception;

	/**
	 * 订单详情查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderDetailForReceiverV730(Map<String, Object> params) throws Exception;
	
	/**
	 * 商品信息编辑
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Object orderGoodsInfoEdit(Long orderId, boolean detailFlg) throws Exception;

	/**
	 * 接单用户提供报价方案前验证
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject pricePlanVerify(Map<String,Object> params) throws Exception;

	/**
	 * 接单用户提供报价方案
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JSONObject wrapSupplyPricePlan(Map<String, Object> params) throws Exception;
	
	/**
	 * 接单用户提供报价方案
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSupplyPricePlanCheckInfo(Map<String, Object> params) throws Exception;
	
	/**
	 * 发单用户查看报价方案列表
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanList(Map<String, Object> params) throws Exception;
	/**
	 * 发单用户选择报价方案	 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject choosePricePlan(String appType,String shopName,Long orderId,Long merchantId,Long merchantPlanId) throws Exception;
	/**
	 * 发单用户选择报价方案	 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject choosePricePlanV1110(String appType,String shopName,Long orderId,Long merchantId,Long merchantPlanId,Boolean isPush) throws Exception;
	/**
	 * 报价方案详情查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanDetail(Map<String, Object> param) throws Exception;
	
	/**
	 * 修改报价方案
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JSONObject updateOrderPricePlan(Map<String, Object> params) throws Exception;
	
	/**
	 * 发单用户取消订单
	 * @param orderId
	 * @return    
	 * JSONObject   
	 * @throws
	 */
	public JSONObject cancelOrderForSender(Map<String, Object> params) throws Exception;
	
	/**
	 * 发单用户删除订单
	 * @param orderId
	 * @return    
	 * JSONObject   
	 * @throws
	 */
	public JSONObject deleteOrderForSender(Long orderId)throws Exception;
	
	/**
	 * 接单用户取消订单
	 * @param merchantId 
	 * @param orderId
	 * @return    
	 * JSONObject   
	 * @throws
	 */
	public JSONObject shieldOrderForReceiver(Long merchantId,Long orderId) throws Exception;
	
	/**
	 * 发单用户获取订单推送状态
	 * @param orderId
	 * @return    
	 * JSONObject   
	 * @throws
	 */
	public JSONObject getOrderPushTypeForSender(Long orderId)throws Exception;
	
	/**
	 * 发单用户获取订单推送状态
	 * @param orderId
	 * @param attitudeEvaluation
	 * @param qualityEvaluation
	 * @param speedEvaluation
	 * @param textEvaluation
	 * @return    
	 * JSONObject   
	 * @throws
	 */
	public JSONObject evaluationOrder(Long orderId, String attitudeEvaluation,String qualityEvaluation,String  speedEvaluation,String  textEvaluation,List<String> paths)throws Exception;

	/**
	 * 获取用户此次订单可以使用的代金券列表
	 * @param userId
	 * @param serviceType
	 * @param merchantId
	 * @param pageNo
	 * @return    
	 * JSONObject   
	 * @throws
	 */
	public JSONObject getOrderCanUsedVouchers(Long userId, Long serviceType, Long merchantId, int pageNo) throws Exception;
	
	/**
	 * 确认订单 金额 
	 * @param orderId
	 * @param merchantId
	 * @param price
	 * @param vouchersId
	 * @param actualPrice
	 * @return
	 * @throws Exception    
	 * int   
	 * @throws
	 */
	public int confirmOrderPrice(Long orderId, Long merchantId, Double price,Double vouchersPrice, Long vouchersId, Double actualPrice,Object payType) throws Exception;
	/**
	 * 确认订单 金额 
	 * @param orderId
	 * @param merchantId
	 * @param price
	 * @param vouchersId
	 * @param actualPrice
	 * @return
	 * @throws Exception    
	 * int   
	 * @throws
	 */
	public JSONObject confirmOrderPriceV160815(Long orderId, Long merchantId, Double price,Double vouchersPrice, Long vouchersId, Double actualPrice,Object payType) throws Exception;

	/** 
	 * 完成支付宝订单
	 * @param orderId
	 * @param tradeNo   交易号
	 * @return
	 * @throws Exception    
	 * int   
	 * @throws
	 */
	JSONObject finishAliPayOrder(Long orderId,String tradeNo,String payDate,String buyNo) throws Exception;
    JSONObject finishAliPayOrderV1110(Long orderId,String tradeNo,String payDate,String buyNo, Double consumePrice, Double totalFee, String outTradeNo) throws Exception;

    /**
     * 完成微信订单
     * @param orderId
     * @param tradeNo  交易号
     * @return
     * int
     * @throws
     */
    JSONObject finishWeChatOrder(Long orderId,String tradeNo,String endTime,String openId);

	/** 
	 * 完成微信订单
	 * @param orderId
	 * @param tradeNo  交易号
	 * @return    
	 * int   
	 * @throws
	 */
    JSONObject finishWeChatOrderV1110(Long orderId,String tradeNo,String endTime,String openId, Double consumePrice, Double totalFee, String outTradeNo);

	/** 
	 * 完成现金订单
	 * @param orderId
	 * @param merchantId
	 * @param price
	 * @return    
	 * int   
	 * @throws
	 */
	public int finishCashOrder(Long orderId, Long merchantId, String price,Object payType);

	/** 
	 * 结束订单
	 * @param orderId
	 * @param merchantId
	 * @param price
	 * @return    
	 * int   
	 * @throws
	 */
	public int overOrder(Long orderId, Long merchantId, String price);
	/** 
	 * 完成银联订单
	 * @param appType
	 * @param orderId
	 * @param tradeNo   交易号
	 * @return
	 * @throws Exception    
	 * int   
	 * @throws
	 */
	JSONObject finishUnionOrder(Long orderId,String tradeNo,String payDate) throws Exception;

	/** 
	 * 保存用户选择的商品
	 * @param orderId
	 * @param merchantId
	 * @param merchantPlanId
	 * @param goodsIds
	 * @param goodsNums
	 * @return    
	 * JSONObject   
	 * @throws
	 */
	public JSONObject saveOrderGoods(String appType,String shopName,Long orderId, Long merchantId, Long merchantPlanId, String goodsIds, String goodsNums);
	
	/**
	 * 获取报价方案空表单（表单生成）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanForm(String serviceId) throws Exception;
	
	/**
	 * 报价方案列表查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanListForSender(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案列表查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	//public JSONObject getPricePlanListForReceiver(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案详情查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanDetailForSender(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案详情查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanDetailForReceiver(Map<String, Object> param) throws Exception;

	/**
	 * 保存交付清单
	 * @param params
	 * @param paths
	 * @return
	 * @throws Exception
	 */
	public JSONObject saveMerchantServiceRecord(Map<String, Object> params, List<String> paths) throws Exception;
	
	/**
	 * 修改交付清单
	 * @param params
	 * @param paths
	 * @return
	 * @throws Exception
	 */
	public JSONObject updateMerchantServiceRecord(Map<String, Object> params, List<String> paths) throws Exception;
	
	/**
	 * 商户查询服务记录
	 * @param merchnatId
	 * @param orderId
	 * @return
	 */
	public JSONObject getMerchantServiceRecord(Long merchantId,Long orderId)throws Exception;
	
	/**
	 * 用户查询服务记录
	 * @param merchnatId
	 * @param orderId
	 * @return
	 */
	public JSONObject getMerchantServiceRecordForUser(Long merchnatId,Long orderId)throws Exception;
	
	/**
	 * 
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public JSONObject confirmOrderPayPrice(Long merchantId,Long orderId) throws Exception;


	/**
	 * 清理商户侧订单缓存
	 */
	public void cleanMerchantCache();
	
	/**
	 * 迁移商户订单数据到历史推送表中
	 * @return
	 */
	public int moveToHistoryPushOrder();
	
	/**
	 * 获取订单对应推送服务商列表
	 * @param orderId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPushMerchantInfos(Long orderId,int pageNo,Integer pageSize) throws Exception;


	/**
	 * 单独抽取获取订单详情及附件的接口  ----2016.7.20  Revoke Yu
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderMoreDetail(Long orderId) throws Exception;
	
	/**
	 * 获取时间轴
	 * @param orderId
	 * @param orderStatus
	 * @param type 1用户，2 商户
	 * @return
	 * @throws Exception
	 */
	public JSONObject getTimeline(Long merchantId,Long orderId,int orderStatus,int type) throws Exception;

    /**
     * 检查是否为黑名单用户
     *
     * @param userId 用户ID
     * @return true 是；false 不是
     */
    boolean checkInBlacklist(Long userId);
    
    Long getOrderIdByOrderNo(String orderNo);
    
   	
    /**
   	 * 所有支付打包返回给客户端支付
   	 * @param 
   	 * @return    
   	 */
   	public JSONObject getPayParm(Integer type,String outTradeNo,Double totalFee,Integer payType,String subject,Integer employeeNumber,String appType,Integer clientType,Integer pkgId,Long userId,String openId,Double consumePrice,String inviteCode) throws Exception;

    /**
     * 处理未发送的MQ
     *
     * @throws Exception
     */
    void dealUnSentList() throws Exception;

    /**
     * 发送MQ
     *
     * @param mqExchange MQ exchange
     * @param msg 发送的消息
     * @throws Exception
     */
    void writeToMQ(String mqExchange, String msg) throws Exception;

    /**
     * 插入MQ发送失败记录
     *
     * @param orderId 订单ID
     * @return
     * @throws Exception
     */
    void insertMQSendFailure(String mqExchange, String msg, Long orderId) throws Exception;
    
    public  List<Map<String,Object>> getPushOrderList();

    JSONObject getMQ(String orderId, String tradeNo, int type) throws Exception;
    
    /**
     * 修改商户营业类型
     * @param params
     * @return
     */
    public JSONObject updMerchantBusinessType(Map<String, Object> params)throws Exception;

    /**
     * 查询用户订单列表v1110
     * @param params
     * @return
     * @throws Exception 
     */
	public JSONObject getOrderListForSenderV1110(Map<String, Object> params) throws Exception;

	/**
	 * 用户查看订单详情
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	JSONObject getOrderDetailForSenderV1110(String orderId) throws Exception;
    
    /**
     * 处理订单来源，是主动发起还是被邀请
     * @param params
     * @throws Exception
     */
//    public void handOrderSource(Map<String, Object> params) throws Exception;


    JSONObject tobeConfirmed(Map<String, Object> params) throws Exception;

    JSONObject findPaymentByOrderId(Long orderId) throws Exception;

    JSONObject updatePaymentStatus(Map<String, Object> params) throws Exception;

    /**
     * 更新方案状态为已读
     * @param params
     * @return
     * @throws ApplicationException
     * @throws Exception
     */
    public JSONObject readMerchantPlan(Map<String, Object> params) throws ApplicationException,Exception;
    /**
	 * 
	 *  ｛商户订单确认服务完成｝
	 *  @param para
	 *  @return
	 *  @author Liuxingwen
	 *  @created 2016年10月26日 上午10:49:42
	 *  @lastModified       
	 *  @history
	 */
	public JSONObject completionOrder(Map<String, Object> para) throws Exception;

	/**
	 * 接单商户获取订单详情 V1110
	 * @param params
	 * @return
	 * @throws Exception
	 */
	JSONObject getOrderDetailForReceiverV1110(Map<String, Object> params) throws ApplicationException,Exception;

	/**
	 * 获取订单号
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public String getOrderNoByOrderId(Long orderId);

    // 购买王牌计划
    JSONObject buyKingPlan(Map<String, Object> params) throws Exception;

    // 增值服务购买待确认
    JSONObject incServiceToBeConfirmed(Map<String, Object> params) throws Exception;


    // 王牌计划预下单
    JSONObject insertKingPlanToBeConfirmed(Map<String, Object> params) throws Exception;
    public int updateOrderActualPrice(Map<String, Object> paramMap);
    
    
    //纯消费金抵扣的支付
    public JSONObject payBuyConsumerMoney(Map<String,Object> params) throws Exception;

}
