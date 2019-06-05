package com.shanjin.service;

import java.math.BigDecimal;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface IMerchantPayService {
	/**
	 * 得到商户订单余额
	 * @param merchantId
	 * @return
	 */
	public BigDecimal getOrderSurplusMoney(Map<String,Object> paramMap);

	/**
	 * 查询抢单费用
	 * @param merchantId
	 * @return
	 */
	public BigDecimal getOrderPrice(Map<String,Object> paramMap);
	/**
	 * 余额中扣除抢单费
	 * @param paramMap
	 * @return
	 */
	public int deductOrderMoney(Map<String,Object> paramMap);
	/**
	 * 余额中扣除抢单费
	 * @param merchantId
	 * @return
	 */
	public int addMerchantOrderPaymentDetails(Map<String,Object> paramMap);
	 /**
   	 * 所有支付打包返回给客户端支付
   	 * @param 
   	 * @return    
   	 */
   	public JSONObject getPayParm(Integer type,String outTradeNo,Double totalFee,Integer payType,String subject,Integer employeeNumber,String appType,Integer clientType,Integer pkgId,Long userId,String openId,Double consumePrice, String inviteCode) throws Exception;

    // 更新商户订单金额
    int updateMerchantOrderSurplusPrice(Map<String, Object> paramMap);
}
