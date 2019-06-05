package com.shanjin.service;

/**
 * 验证token接口
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */
public interface IValidateService {

	/** 获取上次验证时间 */
	public String lastValidatedTime(String clientId);

	/** 更新最后验证时间 */
	public void updateLastValidatedTime(String clientId, String time);

	/** 获取动态密钥 */
	public String getDynamicKey(String clientId)throws Exception;

	public void removeDynamicKey(String clientId);
}
