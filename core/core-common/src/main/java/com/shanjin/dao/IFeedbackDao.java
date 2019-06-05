package com.shanjin.dao;

import java.util.Map;

public interface IFeedbackDao {

	/** 用户反馈信息 */
	int addFeedback(Map<String, Object> paramMap);
	
	int addFeedbackAttachment(Map<String, Object> paramMap);

	Long getBossUserIdByMerchantId(Map<String, Object> paramMap);
}
