package com.shanjin.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 商户订单相关
 * 
 * @author Huang yulai
 *
 */
public interface IMerchantOrderManageService {

	/** 商户抢单前的校验 */
	public JSONObject provideSchemeVerify(String appType, Long merchantId, Long orderId);

	/** 商户抢单后改变订单状态 */
	public JSONObject immediately(String appType, String phone, Long merchantId, Long orderId, BigDecimal planPrice, BigDecimal discountPrice, Long planType, String detail, List<String> voicePaths,
			List<String> picturePaths);

	
	
	/** 商户抢单后改变订单状态 */
	public JSONObject immediatelyV316(String appType, String phone, Long merchantId, Long orderId, BigDecimal planPrice, BigDecimal discountPrice, Long planType, String detail, List<Map<String,String>> voicePaths,
			List<Map<String,String>> picturePaths);
	
	/** 屏蔽订单 */
	public JSONObject shieldOrder(Long merchantId, Long orderId);

	/** 订单基础信息列表查询 */
	public JSONObject selectBasicOrderList(Long catalogId,Long userId, Long merchantId, Long serviceType, int pageNo) throws Exception;

	/** 根据订单ID查询订单的详细信息 */
	public JSONObject selectDetailOrderInfo(String appType, Long merchantId, Long orderId, Long serviceType) throws Exception;

	/** 根据订单ID查询订单的基础信息 */
	public JSONObject selectBasicOrderForPush(String appType, Long orderId) throws Exception;

	/** 订单方案详情查询 */
	public JSONObject selectDetailOrderPlanInfo(String appType, Long merchantId, Long orderId) throws Exception;

	/** 订单商品详情查询 */
	public JSONObject selectDetailOrderGoodsInfo(String appType, Long merchantId, Long orderId) throws Exception;

	/** 商品信息编辑 */
	public Object orderGoodsInfoEdit(String appType, Long orderId, boolean detailFlg) throws Exception;

	/** 测试用接口-认证申请通过 */
	public JSONObject testAuthPass(Long merchantId) throws Exception;

	/** 测试用接口-认证申请通过 */
	public JSONObject testMerchantPlan(Long merchantId, Long orderId, String appType) throws Exception;
	
	
	 public void testText();

}
