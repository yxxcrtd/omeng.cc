package com.shanjin.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IUserMerchantDao;
import com.shanjin.model.RuleConfig;
import com.shanjin.service.IUserMerchantService;
import com.shanjin.service.IncService;

/**
 * 用户订单业务接口实现类
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */
@Service("userMerchantService")
public class UserMerchantServiceImpl implements IUserMerchantService {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(UserMerchantServiceImpl.class);
	@Resource
	private IUserMerchantDao userMerchantDao;
	
	@Resource
	private ICommonCacheService commonCacheService;
	
	@Resource
	private IncService    incService;
	

	/** 收藏商家 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject collectionMerchant(Long userId, Long merchantId, Long receiveEmployeesId) throws Exception{
		int fans = getFans(merchantId);
		
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("merchantId", merchantId);
		paramMap.put("receiveEmployeesId", receiveEmployeesId);
		if (userMerchantDao.collectionMerchant(paramMap) > 0) {
			jsonObject.put("resultCode", "000");
			jsonObject.put("message", "用户关注商户成功");
		} else {
			jsonObject.put("resultCode", "fail");
			jsonObject.put("message", "用户关注商户失败");
		}
		
		//粉丝数+1            Revoke 2016.4.26
		fans++;
		commonCacheService.setObject(fans, CacheConstants.MERCHANT_FANS, merchantId.toString());

		//商户粉丝数
		Map<String,Object> newValue = new HashMap<String, Object>();
		int fansCount = this.userMerchantDao.selectTotalFans(paramMap);
		newValue.put("fansCount", fansCount);
		this.updateMerchantCache(merchantId, newValue);
		return jsonObject;
	}

	/** 删除用户收藏的商家 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject delCollectionMerchant(Long userId, String merchantId)throws Exception {
		int fans = getFans(Long.parseLong(merchantId));
		
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String[] merchantIds = merchantId.split(",");
		paramMap.put("userId", userId);
		paramMap.put("merchantIds", merchantIds);
		if (userMerchantDao.delCollectionMerchant(paramMap) > 0) {
			jsonObject.put("resultCode", "000");
			jsonObject.put("message", "取消用户关注成功");
		} else {
			jsonObject.put("resultCode", "fail");
			jsonObject.put("message", "取消用户关注失败");
		}
		
		//粉丝数-1          Revoke 2016.4.26
		fans--;
		commonCacheService.setObject(fans, CacheConstants.MERCHANT_FANS, merchantId.toString());

		//商户粉丝数
		Map<String,Object> newValue = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		int fansCount = this.userMerchantDao.selectTotalFans(paramMap);
		newValue.put("fansCount", fansCount);
		this.updateMerchantCache(Long.parseLong(merchantId), newValue);
		return jsonObject;
	}

	private void updateMerchantCache(long merchantId,Map newValue) {
		if(newValue != null){
			Map<String, Object> merchantInfo = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,
							StringUtil.null2Str(merchantId));
			if(merchantInfo != null){
				merchantInfo.putAll(newValue);
				// 更新缓存中的商铺信息(暂定一天未访问则移除缓存)
				commonCacheService.setObject(merchantInfo,CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,
						CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
			}
		}
	}
	
	/** 获得用户收藏的商家 */
	@Override
	public JSONObject getCollectionMerchant(String appType, Long userId, int pageNo) throws Exception{
		JSONObject jsonObject = new ResultJSONObject();
		if (pageNo < 0) {
			pageNo = 0;
		}
		int totalPage = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("appType", appType);
		paramMap.put("startNum", pageNo * Constant.PAGESIZE);
		paramMap.put("pageSize", Constant.PAGESIZE);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		int count = userMerchantDao.getCollectionMerchantTotalPage(paramMap);
		if(count!=0){
			resultList =userMerchantDao.getCollectionMerchant(paramMap);
			totalPage = BusinessUtil.totalPageCalc(count);
		}
		HandleCollectionMerchant(resultList);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获得用户收藏商家 成功");
		jsonObject.put("resultList", resultList);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

	/** 对收藏的商家信息处理 */
	private List<Map<String, Object>> HandleCollectionMerchant(List<Map<String, Object>> resultList) {
		for (Map<String, Object> infoMap : resultList) {
			// 处理路径
			BusinessUtil.disposePath(infoMap, "path");
			try {
				infoMap.get("");
				//加入VIP 信息
				infoMap.put("vipStatus", -1);
				List<RuleConfig> rulConfigs=incService.getRuleConfig((Long)infoMap.get("id"));
				if (rulConfigs!=null){
						if (rulConfigs.get(0).isVipMerchantOrder()){
							infoMap.put("vipStatus", 2);
						}
				}
				int evaluationOrderNum = StringUtil.nullToInteger(infoMap.get("orderNum"));
				if (evaluationOrderNum == 0) {
					infoMap.put("starLevel", 5);
				} else {
					Integer totalAttitudeEvaluation = Integer.parseInt(infoMap.get("totalAttitudeEvaluation") == null ? "0" : infoMap.get("totalAttitudeEvaluation") + "");
					Integer totalQualityEvaluation = Integer.parseInt(infoMap.get("totalQualityEvaluation") == null ? "0" : infoMap.get("totalQualityEvaluation") + "");
					Integer totalSpeedEvaluation = Integer.parseInt(infoMap.get("totalSpeedEvaluation") == null ? "0" : infoMap.get("totalSpeedEvaluation") + "");
					// 总服务态度评价+总服务质量评价+总服务速度评价
					Integer totalEvaluation = totalAttitudeEvaluation + totalQualityEvaluation + totalSpeedEvaluation;
					// 星级
					BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(new BigDecimal(evaluationOrderNum).multiply(new BigDecimal(3)), 0, BigDecimal.ROUND_HALF_UP);
					if (starLevel.compareTo(new BigDecimal(5)) > 0) {
						starLevel = new BigDecimal(5);
					}
					if (starLevel.compareTo(new BigDecimal(0)) < 0) {
						starLevel = new BigDecimal(0);
					}
					infoMap.put("starLevel", starLevel);
				}
			} catch (Exception e) {
				e.printStackTrace();
				infoMap.put("starLevel", 5);
				logger.error("", e);
			}
		}
		return resultList;
	}

	
	
	private int  getFans(Long merchantId){
		Object cachedFansNum=commonCacheService.getObject(CacheConstants.MERCHANT_FANS, merchantId.toString());
		if (cachedFansNum==null) {
				// 粉丝数
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("merchantId", merchantId);
				int collectionNum = this.userMerchantDao.selectTotalFans(param);
				commonCacheService.setObject(collectionNum,CacheConstants.MERCHANT_FANS, merchantId.toString());
				return collectionNum;
		}
		return (int) cachedFansNum;
	}
	
}
