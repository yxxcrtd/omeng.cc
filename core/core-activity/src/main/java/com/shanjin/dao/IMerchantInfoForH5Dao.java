package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户信息分享（用于商户信息分享，此方法为过渡性方案）
 */
public interface IMerchantInfoForH5Dao {

	/** 查询店铺基本信息 */
	Map<String, Object> selectMerchantInfo(Map<String, Object> paramMap);

	Map<String, Object> selectAuthType(Map<String, Object> paramMap);

    /** 查询当前还未被领取的代金券记录数 */
    int selectCurrentVouchersInfoCount(Map<String, Object> paramMap);

	/** 查询商户服务项目名 */
	List<String> selectMerchantServiceTypeName(Map<String, Object> paramMap);

    /** 查询单个商户的总相片数 */
    int selectPhotoTotal(Map<String, Object> paramMap);

    /** 根据商户ID查询最新的4张照片 */
    Map<String, Object> selectLast4Photo(Map<String, Object> paramMap);

    /** 单个商家可用商品数量查询 */
	int selectGoodsCount(Map<String, Object> paramMap);

    /** 查询商户最新3个商品信息 */
    List<Map<String, Object>> selectLast3GoodsInfo(Map<String, Object> paramMap);

	public List<Map<String, Object>> getTextEvaluationTop2(Map<String, Object> paramMap);

	/** 根据商户ID查询商户的所有统计信息 */
	Map<String, Object> selectStatisticsInfo(Map<String, Object> paramMap);

	/** 根据微信的openid查询用户id */
	Long selectUserIdByopenId(Map<String, Object> paramMap);

    /** 验证用户是否收藏商家 */
    int checkCollectionMerchant(Map<String, Object> paramMap);

	/** 查询商户被多少用户收藏 */
	int selectCollectionNum(Map<String, Object> paramMap);

	/** 查询商户接单计划 */
	Integer selectAlreadySetOrderPlan(Map<String, Object> paramMap);

	List<Map<String, Object>> getMerchantServiceList(Map<String, Object> paramMap);

	int getVipStatus(Map<String, Object> paramMap);

	List<Map<String, Object>> selectVipBackgroundUrlList(
			Map<String, Object> paramMap);

    /**
     * 根据订单获取摸金次数
     * @param userId
     * @return
     */
	int getNumberByOrder(Map<String, Object> paramMap);

	/**
	 * 根据朋友分享获取摸金次数
	 * @param userPhones
	 * @return
	 */
	int getNumberByShare(List<Map<String, Long>> userPhones);

	/**
	 * 获取活动有效
	 * @param param
	 * @return
	 */
	Map<String, Object> findActivityInfo(Map<String, Object> param);
}
