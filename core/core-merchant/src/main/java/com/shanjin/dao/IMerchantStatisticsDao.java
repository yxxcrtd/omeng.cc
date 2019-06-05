package com.shanjin.dao;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商户统计信息表 Dao
 */
public interface IMerchantStatisticsDao {

	/** 当前应用的商户统计信息记录数查询 */
	int selectMerchantStatisticsCount(Map<String, Object> paramMap);

	/** 商户统计信息表初期化 */
	int insertMerchantStatistics(Map<String, Object> paramMap);

	/** 统计排行信息查询 */
	Map<String, Object> selectMerchantStatistics(Map<String, Object> paramMap);

	/** 商户收入信息查询 */
	Map<String, BigDecimal> selectMyIncome(Map<String, Object> paramMap);

	/** 商户服务评价信息查询 */
	Map<String, Object> selectPreviewInfo(Map<String, Object> paramMap);

	/** 商户余额信息查询 */
	BigDecimal selectSurplusPrice(Map<String, Object> paramMap);

	/** 商户抢单后, 商户统计信息表中的总抢单次数加1 */
	int updateGrabFrequency(Map<String, Object> paramMap);

	/** 更新总收入金额,总提取金额 */
	int updatePrice(Map<String, Object> paramMap);

	/** 商户抢单次数查询 */
	Map<String, Object> selectGrabFrequency(Map<String, Object> paramMap);

	/** 更新订单余额 */
	int updateOrderSurplusPrice(Map<String, Object> paramMap);

	/** 更新appType */
	int updateAppType(Map<String, Object> paramMap);
	
	/** 根据商户ID查询商户的所有统计信息 */
	Map<String, Object> selectStatisticsInfo(Map<String, Object> paramMap);
	
	/**
	 * 账户总余额 
	 * 查询商户当月可以转入钱包的金额  可转入钱包的金额 = 商户余额 - 当月收入金额**/
	Map<String,Object> selectTransoutAmount(Long merchantId);
	
	/***查询店铺首页昨日收入 和 本月收入**/
	Map<String,Object> selectMerchantIncomeIndex(Map<String,Object> params);

	/**
	 * 查询商铺充值状态 1-可充值，-1-确认中，不可充值
	 * @param paramMap
	 * @return
	 */
	int selectMerchantPayApplyStatus(Map<String, Object> paramMap);
	
}
