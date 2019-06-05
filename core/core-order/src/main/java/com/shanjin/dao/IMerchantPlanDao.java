package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户方案表Dao
 */
public interface IMerchantPlanDao {

	/** 查询商户是否对订单提供了方案 */
	int selectPlanNum(Map<String, Object> paramMap);

	/** 商户抢单后提供的方案的保存 */
	int insertMerchantPlan(Map<String, Object> paramMap);
	
	/** 商户抢单后提供的方案的修改 */
	int updateMerchantPlan(Map<String, Object> paramMap);

	/** 商户抢单后, 商户统计信息表中的总抢单次数加1 */
	int updateGrabFrequency(Map<String, Object> paramMap);

	/**
	 * 查询订单方案数量
	 * 
	 * @param orderId
	 *            订单ID
	 * @return 方案数量
	 */
	public int getOrderPlanCount(Long orderId);

	/**
	 * 查询employeeId
	 */
	public Object getEmployeeIdByPhone(Map<String, Object> paramMap);

	/**
	 * 更改商户方案的距离 总评价数 好评率
	 * 
	 * @param paramMap
	 * @return
	 */
	int updateMerchantPlanExtInfo(Map<String, Object> paramMap);

	/** 获得商家的总评价数 */
	Map<String, Object> selectMerchantTotalCountEvaluation(Map<String, Object> paramMap);

	/** 获得商家的好评数 */
	Map<String, Object> selectMerchantGoodEvaluationCount(Map<String, Object> paramMap);
	
	
	/** 获取商户基本信息  */
	Map<String,Object> getMerchantInfoForChoosePlan(Long merchantId);
	
	
	/** 获取商户报价方案基本信息  */
	Map<String,Object> getMerchantPlan(Long merchantId);
	
	/** 获取提供报价方案的雇员ID  */
	List<Map<String,Object>>  getEmployeeIdsOfMerchantPlan(Map<String,Object> paramMap);
	
	/**
	 *  获取为某订单提供报价的商户列表
	 * @param orderId
	 * @return
	 */
	List<Long>  getMerchantsByOrderId(Long orderId);
	
	/**
	 * 获取商店对应的BOSS 的userId
	 * @param merchantId
	 * @return
	 */
	public Map<String,Object> getBossIdByMerchant(Long merchantId);
	
	
	/**
	 * 获取报价方案中的商品数量
	 * @param planId
	 * @return
	 */
	public Integer  getTotalPlainGoods(Long planId);
	
	
	/**
	 * 获取报价方案中的商品首选信息
	 * @param planId
	 * @return
	 */
	public Map<String,Object> getDefaultGoodsInfo(long planId);
	
	/**
	 * 获取报价方案对应的商品列表
	 * @param planId
	 * @return
	 */
	public List<Map<String,Object>> getGoodsInfoByPlan(long planId);
	
	
}
