package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IEvaluationDao {

	public List<Map<String, Object>> getUserEvaluations(Long userId);
	
	public List<Map<String, Object>> getMerchantEvaluations(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getMerchantEvaluationsForUser(Map<String, Object> paramMap);
	
	public float getMerchantAverageScore(Long merchantId);
	
	public int getMerchantEvaluationNum(Long merchantId);
	
	public List<Map<String, Object>> getTextEvaluationTop2(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getTextEvaluationTopN(Map<String, Object> paramMap);

	public Map<String, Object> selectStatisticsInfo(Map<String, Object> paramMap);
}
