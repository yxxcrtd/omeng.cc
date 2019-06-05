package com.shanjin.dao;

import java.util.Map;

public interface IRongCloudDao {

	/** 获取用户端融云token **/
	public Map<String, Object> getRongCloudUserToken(Map<String, Object> paramMap);
	
	/** 获取商户端融云token **/
	public Map<String, Object> getRongCloudMerchantToken(Map<String, Object> paramMap);
	
	/** 保存用户端融云token */
	public Integer saveRongCloudUserToken(Map<String, Object> paramMap);
	
	/** 保存商户端融云token */
	public Integer saveRongCloudMerchantToken(Map<String, Object> paramMap);
	
	/** 删除用户端融云token */
	public int delRongCloudUserToken(Map<String, Object> paramMap);
	
	/** 删除商户端融云token */
	public int delRongCloudMerchantToken(Map<String, Object> paramMap);
}
