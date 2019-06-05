package com.shanjin.thread;

import java.util.HashMap;
import java.util.Map;

import com.shanjin.common.util.LocationUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IMerchantPlanDao;

public class UpdateMerchantPlan implements Runnable {
	private Long merchantId;
	private Long orderId;
	//private String appType;
	private Map<String, Object> orderLocationMap;
	private Map<String, Object> merchantLocationMap;
	private IMerchantPlanDao iMerchantPlanDao;

	public UpdateMerchantPlan(Long merchantId, Long orderId, Map<String, Object> orderLocationMap, Map<String, Object> merchantLocationMap, IMerchantPlanDao iMerchantPlanDao) {
		this.merchantId = merchantId;
		this.orderId = orderId;
		//this.appType = appType;
		this.orderLocationMap = orderLocationMap;
		this.merchantLocationMap = merchantLocationMap;
		this.iMerchantPlanDao = iMerchantPlanDao;
	}

	@Override
	public void run() {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("orderId", orderId);
		//paramMap.put("appType", appType);

		// 给订单方案增加距离
		try {
			// 获得订单的经纬度
			if (orderLocationMap != null) {
				// 获得商户的定位度
				if (merchantLocationMap != null) {
					// 先判断 订单经纬度 商户经纬度 是否为空
					if (StringUtil.isNotEmpty(orderLocationMap.get("longitude")) && StringUtil.isNotEmpty(orderLocationMap.get("latitude"))
							&& StringUtil.isNotEmpty(merchantLocationMap.get("longitude")) && StringUtil.isNotEmpty(merchantLocationMap.get("latitude"))) {
						// 如果不为空则查询距离
						Double distance = LocationUtil.getDistance(Double.parseDouble(StringUtil.null2Str(orderLocationMap.get("longitude"))),
								Double.parseDouble(StringUtil.null2Str(orderLocationMap.get("latitude"))), Double.parseDouble(StringUtil.null2Str(merchantLocationMap.get("longitude"))),
								Double.parseDouble(StringUtil.null2Str(merchantLocationMap.get("latitude"))));
						Long distanceValue = Math.round(distance);
						paramMap.put("distance", distanceValue);
					}else{
						//未存经纬度，择默认距离为200KM
						paramMap.put("distance", 200*1000);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 给订单方案增加总评论数 和 好评数
		try {
			Map<String, Object> merchantCountMap = iMerchantPlanDao.selectMerchantTotalCountEvaluation(paramMap);
			if (merchantCountMap != null) {
				paramMap.put("totalCount", StringUtil.nullToInteger(merchantCountMap.get("totalCountEvaluation")));
				// 总评价数
				Integer totalCountEvaluation = StringUtil.nullToInteger(merchantCountMap.get("totalCountEvaluation"));
				// Double praiseRate = (double) 1;
				Integer goodCount = 0;
				if (totalCountEvaluation > 0) {

					// 总评分数
					// Integer totalAttitudeEvaluation =
					// StringUtil.nullToInteger(merchantCountMap.get("totalAttitudeEvaluation"));
					// Integer totalQualityEvaluation =
					// StringUtil.nullToInteger(merchantCountMap.get("totalQualityEvaluation"));
					// Integer totalSpeedEvaluation =
					// StringUtil.nullToInteger(merchantCountMap.get("totalSpeedEvaluation"));
					// praiseRate = (double) (totalAttitudeEvaluation +
					// totalQualityEvaluation + totalSpeedEvaluation);
					// 好评数
					merchantCountMap = iMerchantPlanDao.selectMerchantGoodEvaluationCount(paramMap);
					goodCount = StringUtil.nullToInteger(merchantCountMap.get("goodCount"));
					// praiseRate = ((double) goodCount / totalCountEvaluation);
				}
				paramMap.put("praiseCount", goodCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		iMerchantPlanDao.updateMerchantPlanExtInfo(paramMap);
	}
}
