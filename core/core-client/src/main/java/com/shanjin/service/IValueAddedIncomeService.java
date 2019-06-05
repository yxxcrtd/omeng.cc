package com.shanjin.service;

/**
 * 增值服务收益计算接口
 * 
 * @author 李焕民
 * @version 2015年11月13日
 *
 */
public interface IValueAddedIncomeService {

	/** 增值服务收益计算 */
	public boolean calculateIncome(Long merchantId, double income, String operUserName, int incomeType);
}
