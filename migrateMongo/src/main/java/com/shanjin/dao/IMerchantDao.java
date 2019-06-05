package com.shanjin.dao;

import java.util.List;
import java.util.Map;


/**
 * 历史订单--商户迁移的相关DAO
 * @author RevokeYu  2016.10.9
 *
 */
public interface IMerchantDao {

	/**
	 * 按订单ids获取商户的认证信息
	 * @param merchantId
	 * @return
	 */
	List<Map<String,Object>>  selectMerchantAuthList(Long merchantId);
	
}
