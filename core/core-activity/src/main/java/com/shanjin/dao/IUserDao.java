package com.shanjin.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface IUserDao {

	/** 根据手机号查询用户信息 */
	Map<String, Object> getUserInfoByPhone(Map<String, Object> paramMap);
	
	/** 注册新用户,填写基本手机号和验证码、验证时间 */
	int insertUserInfoWithVerification(Map<String, Object> paramMap);	

	/** 初始化用户头像 */
	int initUserInfoPortraitDirect(Map<String, Object> paramMap);
	
	/** 验证微信用户是否存在 */
	int checkWechatUserIsEmpty(Map<String, Object> paramMap);
	
	/** 保存微信用户信息 */
	int insertWechatUser(Map<String, Object> paramMap);	
	
	/** 查询微信用户信息 */
	Map<String, Object> getWechatByOpenId(Map<String, Object> paramMap);
	
	/** 更新微信用户信息 */
	int updateWechatUser(Map<String, Object> paramMap);
	
	/** 更新微信用户关联的平台用户ID */
	int updateWechatUserForId(Map<String, Object> paramMap);
	
    /** 增加用户商家收藏 */
    int collectionMerchant(Map<String, Object> paramMap);
    
    /** 用户是否收藏商家 */
    int isCollectionMerchant(Map<String, Object> paramMap);
    
    /** 获取注册用户数 */
    int getUserCount(Map<String, Object> paramMap);
    
    /** 获取用户表platform是否存在 */
    int getPlatform(Map<String, Object> paramMap);

	/**
	 * 根据用户Id获取用户信息包括 手机号，url
	 * @param userId
	 * @return
     */
	Map<String,String> getUserInfo(@Param("userId") Long userId);
	
}
