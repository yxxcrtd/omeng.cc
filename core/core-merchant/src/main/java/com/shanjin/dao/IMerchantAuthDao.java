package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户认证信息表Dao
 */
public interface IMerchantAuthDao {

	Map<String, Object> selectMerchantAuth(Map<String, Object> paramMap);
    /** 申请认证的信息查询 */
	List<Map<String, Object>> selectMerchantAuthList(Map<String, Object> paramMap);

	/**
	 * 查询商户认证类型（1-企业认证 2-个人认证）
	 * @param paramMap
	 * @return
     */
	Map<String, Object> selectAuthType(Map<String, Object> paramMap);

    /** 认证申请记录数查询（防重复提交） */
    Integer selectAuthCount(Map<String, Object> paramMap);
    Integer selectPendingAuthCount(Map<String, Object> paramMap);

    /** 商户认证信息的保存 */
    int insertMerchantAuth(Map<String, Object> paramMap);

	/** 取消认证申请 */
	public int cancelAuth(Long merchantId);
	
	/** 更新商户表认证状态 */
	void updateMerchantAuth(Map<String, Object> paramMap);
	
	/** 取消认证，更新商户表认证状态 */
	void cancelAuthUpdateMerchant(Long merchantId);
}
