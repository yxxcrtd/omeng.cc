package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 剪彩活动相关
 * @author Huang yulai
 *
 */
public interface ICuttingDao {

	/**
	 * 获取商铺基本信息
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> getMerchantInfo(Map<String, Object> paramMap); 
	
    /**
     * 获取商户剪彩数
     * @param paramMap
     * @return
     */
    int getMerchantCuttingNum(Map<String, Object> paramMap);
    
    /**
     * 获取剪彩活动统计
     * @param paramMap
     * @return
     */
    int getMerchantCuttingCount(Map<String, Object> paramMap);
    
    /**
     * 获取剪彩活动标签列表
     * @param paramMap
     * @return
     */
    List<Map<String,Object>> getLabelList();
    
    /**
     * 获取剪彩标签统计
     * @param paramMap
     * @return
     */
    Map<String, Object> getLabelCount(Map<String, Object> paramMap); 
	
	/**
	 * 插入剪彩信息
	 * @param paramMap
	 * @return
	 */
    int insertCuttingInfo(Map<String, Object> paramMap);
    
	/**
	 * 更新剪彩信息
	 * @param paramMap
	 * @return
	 */
    int updateCuttingInfo(Map<String, Object> paramMap);
    
    /**
     * 校验商户是否有剪彩记录
     * @param paramMap
     * @return
     */
    int checkMerchantCutting(Map<String, Object> paramMap);
    
	/**
	 * 获取开店总数
	 * @param paramMap
	 * @return
	 */
    int getMerchantCount();
    
	
	void insertCuttingInfoLabel(Map<String, Object> paramMap);

	List<Map<String, Object>> getCuttingDetail(Map<String, Object> paramMap);

	void updateCuttingInfoCash(Map<String, Object> paramMap);

	void insertCuttingInfoCash(Map<String, Object> paramMap);

	void updateMerchantStatics(Map<String, Object> paramMap);

	List<Map<String, Object>> getAllServiceList();

	List<Map<String, Object>> getPushMerchant(Map<String, Object> paramMap);

	String getAppType(Map<String, Object> paramMap);

	Map<String, Object> getMerchantInfoForAdver(Map<String, Object> paramMap);

	int getRankingMerchant(Map<String, Object> paramMap);

	int getCityCount(Map<String, Object> paramMap);

	int getFansCount(Map<String, Object> paramMap);

	int getPerFansCount(Map<String, Object> paramMap);

	int getperRanking(Map<String, Object> paramMap);

	List<Map<String, Object>> getEndTime(Map<String, Object> paramMap);

	List<Map<String, Object>> getCityRanking(Map<String, Object> paramMap);

	List<Map<String, Object>> getCountyRanking(Map<String, Object> paramMap);

	String getCityByMerchantId(Map<String, Object> paramMap);

	List<Map<String, Object>> getOpenCity();

	int getFansAllCountByCity(Map<String, Object> paramMap);

	void insertFansiEndTime(Map<String, Object> paramMap);

	int checkFocusMerbyOpenId(Map<String, Object> paramMap);

	void insertMerchantPayMentDetail(Map<String, Object> paramMap);

	List<Map<String, Object>> getProAndCity(Map<String, Object> paramMap);

	Map<String, Object> getMerchantBasic(Map<String, Object> paramMap);

	void insertMqFailure(Map<String, Object> paramMap);

	Map<String, Object> getActivityShare(Map<String, Object> paramMap);

	int checkCuttingInfobyTranId(Map<String, Object> paramMap);

	long getCuttingIdByMer(Map<String, Object> paramMap);
	
	
}
