package com.shanjin.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *  商户报价方案明细Dao
 */
public interface IMerchantPlanDetailDao {

	/** 新增商户报价方案明细 */
	int insertMerchantPlainDetail(Map<String, Object> paramMap);

	
	/**
	 * 根据商户报价方案查询报价方案明细
	 * @param merchantPlanId
	 * @return
	 */
	List<Map<String,Object>> getMerchantPlainDetail(long merchantPlanId);
	
}
