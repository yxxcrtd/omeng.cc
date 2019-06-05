package com.shanjin.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 活动相关接口
 * @author Huang yulai
 *
 */
public interface IActivityService {

	/** 获取商铺开店信息和剪彩统计信息 */
	public JSONObject getCuttingInfo(String merchantId) throws Exception;
	
	/** 保存商铺祝福信息 */
	public JSONObject saveCuttingInfo(Map<String, Object> paramMap) throws Exception;

	/** 获取店铺开店总数 */
	public JSONObject getMerchantNum() throws Exception;

	/** 获取剪彩统计信息分页 */
	public JSONObject getLabelList(String merchantId,int pageNo, int pageSize);

	/** H5展示商家信息 */
	public JSONObject merchantInfoForH5(Long merchantId, Double longitude, Double latitude, String openid) throws Exception;

	/** 商户是否设置过接单计划 */
	public JSONObject alreadySetOrderPlan(Long merchantId) throws Exception;

	/** 剪彩过后商家宣传信息 */
	public JSONObject getMerchantInfo(String merchantId);
	
	/** 获取发单活动详情  */
	public JSONObject getOrderActivityDetail(String activityId);

	/** 1000万粉丝计划商家信息查询 
	 * @param openId */
	public JSONObject getMerchantInfoFanSi(Long merchantId, String openId) throws Exception;

	/** 1000万粉丝计划,城市排行榜  */
	public JSONObject getRankingFanSiByCity(Long merchantId);

	/** 1000万粉丝计划,城市排行榜分页  */
	public JSONObject getRankingFanSiByCityPage(Long merchantId, int pageIndex, int pageSize);

	/** 定时器，1000万粉丝计划截止时间  */
	public void checkFansiActEndTime();

	public JSONObject getActivityShare(String activityId);

}
