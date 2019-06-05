package com.shanjin.service;

import java.util.Map;

import open.wechat.model.UserEntity;

/**
 * 微信服务接口
 * @author Huang yulai
 *
 */
public interface IWechatService {

    Map<String, String> getSignParam(String url);
    
	Map<String, Object> getUserInfo(String orderNo,String openId);

	void saveUserInfo(String orderNo, String demand);

	void saveUserInfoByOpenId(String openId, UserEntity entity);

	Map<String, Object> getUser(String openId);
    
}
