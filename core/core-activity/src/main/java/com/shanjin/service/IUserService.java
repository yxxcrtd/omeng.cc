package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

public interface IUserService {
	
	/** 第三方平台获取短信验证码 */
	public JSONObject getVerificationCode(String phone,String content);
	
	/** 验证验证码 */
	public JSONObject validateVerificationCode(String phone, String verificationCode,String ip,String openId) throws Exception;
	
	/** 验证验证码 */
	public JSONObject validateVerificationCodeForDream(String phone, String verificationCode,String ip) throws Exception;
	
	/** 微信用户收藏商户 */
    public JSONObject collectionMerchant(String openId, Long merchantId)throws Exception;
    
	/** 获取平台用户数 */
	public JSONObject getUserCount();
	
}
