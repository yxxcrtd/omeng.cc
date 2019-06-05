package com.shanjin.dao;

import java.util.Map;

public interface IUserCPlanDao {
	/** 根据手机号查询用户信息 */
	Map<String, Object> getUserInfoByPhone(Map<String, Object> paramMap);
	/** 注册新用户,填写基本手机号和验证码、验证时间 */
	int insertUserInfoWithVerification(Map<String, Object> paramMap);	
}
