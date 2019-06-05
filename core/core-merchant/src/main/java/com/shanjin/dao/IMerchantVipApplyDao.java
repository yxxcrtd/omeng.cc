package com.shanjin.dao;

import java.util.Map;

/**
 * 商户会员申请表Dao
 */
public interface IMerchantVipApplyDao {

	/** 保存商户会员申请 */
	int insertMerchantVipApply(Map<String, Object> paramMap);

	/** 查询商户会员申请状态 */
	Integer selectMerchantVipApplyStatus(Map<String, Object> paramMap);

	/** 通过商户会员申请 */
	int updateMerchantVipApply(Map<String, Object> paramMap);

	/** 检查商户会员申请状态 */
	Integer checkMerchantVipApplyByPayNo(Map<String, Object> paramMap);
}
