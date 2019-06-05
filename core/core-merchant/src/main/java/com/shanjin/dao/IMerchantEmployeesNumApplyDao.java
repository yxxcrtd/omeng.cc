package com.shanjin.dao;

import java.util.Map;

/**
 * 商户员工数增加申请表Dao
 */
public interface IMerchantEmployeesNumApplyDao {

	/** 保存商户员工数增加的申请 */
	int insertMerchantEmployeesNumApply(Map<String, Object> paramMap);
	
	
	/**
	 *  修改增加雇员数量的待确认记录状态为已取人
	 * @param paramMap
	 * @return
	 */
	int confirmBuyEmployeeNumRecord(Map<String,Object> paramMap);

	/** 更改商户员工数 */
	int updateMerchantEmployeesNum(Map<String, Object> paramMap);

	/** 设置商户员工数 */
	int setMerchantEmployeesNum(Map<String, Object> paramMap);

	/** 根据交易号检查商户员工数增加的申请 */
	Integer checkEmployeesNumApplyByPayNo(Map<String, Object> paramMap);
	
	
	
	/** 根据内部交易号检查商户员工数增加的申请 */
	Integer checkEmployeesNumApplyByInnerTradeNo(Map<String, Object> paramMap);
	

	/** 查询商户最大人数申请状态 -1-可申请，1-确认中，不可申请 */
	Integer selectEmployeesNumApplyStatus(Map<String, Object> paramMap);

	/**
	 * 保存商户员工增加数量申请 */
	int addMerchantEmployeesNumApplyWithConfirm(Map<String,Object> paramMap);
}
