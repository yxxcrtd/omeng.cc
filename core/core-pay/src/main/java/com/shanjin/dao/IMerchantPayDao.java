package com.shanjin.dao;

import java.util.Map;

public interface IMerchantPayDao {

	/**
	 * 得到商户订单余额
	 * @param merchantId
	 * @return
	 */
	public Object getOrderSurplusMoney(Map<String,Object> paramMap); 
	/**
	 * 查询抢单费用
	 * @param merchantId
	 * @return
	 */
	public Object getOrderPrice(Map<String,Object> paramMap); 
	
	/**
	 * 订单余额中扣除抢单费
	 * @param money
	 * @return
	 */
	public int deductOrderMoney(Map<String,Object> paramMap);
	
	/**
	 * 生成扣费记录
	 * @param paramMap
	 * @return
	 */
	public int addMerchantOrderPaymentDetails(Map<String,Object> paramMap);
	
	/**
	 * 根据订单ID查找服务类型
	 * @return    
	 * Object   
	 * @throws
	 */
	public Object getServiceTypeIdByOrderId(Long orderId);
	/**
	 * 支付打包返回给客户端
	 * @return    
	 * Object   
	 * @throws
	 */
	public Object getPayParm(String outTradeNo,double totalFee,Integer payType);
	
	/**
	 * 获取服务支付记录的版本
	 * @param orderNo
	 * @return
	 */
	public Integer getMerchantServiceRecordByID(String orderNo);

    /**
     * 更新商户订单金额
     * @param paramMap
     * @return
     */
    int updateMerchantOrderSurplusPrice(Map<String, Object> paramMap);

}
