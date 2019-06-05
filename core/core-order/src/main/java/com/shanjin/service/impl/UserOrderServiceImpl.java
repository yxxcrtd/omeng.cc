package com.shanjin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.*;
import com.shanjin.cache.service.impl.JedisLock;
import com.shanjin.cache.util.JedisPoolUtil;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.*;
import com.shanjin.dao.*;
import com.shanjin.exception.ApplicationException;
import com.shanjin.push.*;
import com.shanjin.service.*;
import com.shanjin.service.api.IOrderApiService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;

import static com.shanjin.common.util.BusinessUtil.judgeAuth;

/**
 * 用户订单业务接口实现类
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */
@Service("userOrderService")
public class UserOrderServiceImpl implements IUserOrderService {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(UserOrderServiceImpl.class);

	@Resource
	private IUserOrderDao userOrderDao;
	@Resource
	private IAppInfoDao appInfoDao;
	@Resource
	private IOrderInfoDao orderInfoDao;
	@Resource
	private IVouchersInfoDao vouchersInfoDao;
	@Resource
	private IOrderApiService orderApiService;
	
	@Resource
	private IOrderInfoService orderInfoService;
	
	@Resource
	private IScheduleDao	 scheduleDao;
	
	@Resource
	private IAndroidAppPushConfigDao androidAppPushConfigDao;
	@Resource
	private IUserRelatedCacheServices userRelatedCacheServices;

	@Resource
	private IDictionaryService dictionaryService;

	@Resource
	private ICommonCacheService commonCacheService;
	
	@Resource
	private IMerchantCacheService merchantCacheService;

	@Resource
	private IMerchantOrderManageService merchantOrderManageService;

	@Resource
	private IIpCityCacheService ipCityCacheServices;

	@Resource
	private ICommonService commonService;

	@Resource
	private IImageCacheService imageCacheService;
	
	@Resource
	private ITimeLineDao   timeLineDao;
	
	@Resource
	private IPurifyDao   purifyDao;
	
	@Resource
	private ICustomOrderDao   customOrderDao;
	
	@Resource
	private IMerchantCacheService merchantCache;

	@Resource
	private IMerchantPlanDao   merchantPlanDao;
	

	/** 获取订单供应商列表 */
	@Override
	public JSONObject getOrderMerchantPlan(String appType, Long orderId, int pageNo, String orderBy) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int totalPage = 0;
		JSONObject jsonObject = null;
		paramMap.put("orderId", orderId);
		paramMap.put("startNum", pageNo * Constant.PAGESIZE);
		paramMap.put("pageSize", Constant.PAGESIZE);
		paramMap.put("appType", appType);

//		if (StringUtils.isEmpty(orderBy)) {
//			// 默认按加入时间升序排列
//			paramMap.put("orderby", "mp.join_time");
//			paramMap.put("sort", "asc");
//		} else if (StringUtils.equals("1", orderBy)) {
//			// 1的场合按优惠价格升序排列
//			paramMap.put("orderby", "mp.discount_price");
//			paramMap.put("sort", "asc");
//		} else if (StringUtils.equals("2", orderBy)) {
//			// 2的场合按距离升序排列
//			paramMap.put("orderby", "mp.distance");
//			paramMap.put("sort", "asc");
//		}
		if (orderBy.equals("1")) { // 或者：StringUtils.equals("1", orderBy)
			paramMap.put("orderby", "mp.discount_price");
		} else if (orderBy.equals("2")) { // StringUtils.equals("2", orderBy)
			paramMap.put("orderby", "mp.distance");
		} else { // 最后一种情况就是为空的情况
			paramMap.put("orderby", "mp.join_time");
		}
		paramMap.put("sort", "asc");

		List<Map<String, Object>> merchantPlans = this.userOrderDao.getOrderMerchantPlan(paramMap);
		for (int i = 0; i < merchantPlans.size(); i++) {
			BusinessUtil.disposeManyPath(merchantPlans.get(i), "icoPath", "picturePath", "voicePath");
			if (merchantPlans.get(i).get("merchantId") != null) {
				paramMap.put("merchantId", (Long) merchantPlans.get(i).get("merchantId"));
				int count = orderInfoDao.selectEvaluationOrderNum(paramMap);
				int re = 5;
				if (count != 0) {
					Integer totalAttitudeEvaluation = Integer.parseInt(merchantPlans.get(i).get("totalAttitudeEvaluation") == null ? "0" : merchantPlans.get(i).get("totalAttitudeEvaluation") + "");
					Integer totalQualityEvaluation = Integer.parseInt(merchantPlans.get(i).get("totalQualityEvaluation") == null ? "0" : merchantPlans.get(i).get("totalQualityEvaluation") + "");
					Integer totalSpeedEvaluation = Integer.parseInt(merchantPlans.get(i).get("totalSpeedEvaluation") == null ? "0" : merchantPlans.get(i).get("totalSpeedEvaluation") + "");
					// 总服务态度评价+总服务质量评价+总服务速度评价
					Integer totalEvaluation = totalAttitudeEvaluation + totalQualityEvaluation + totalSpeedEvaluation;
					// 星级
					BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(new BigDecimal(count).multiply(new BigDecimal(3)), 0, BigDecimal.ROUND_HALF_UP);
					re = starLevel.intValue();
				}
				if (re > 5) {
					re = 5;
				}
				if (re < 0) {
					re = 0;
				}
				merchantPlans.get(i).put("merchantPoint", re);
			}

			/** 判断企业认证类型 1-企业认证 2-个人认证 0-没有认证 */
//			if ((Long) merchantPlans.get(i).get("enterpriseAuth") > 0) {
//				merchantPlans.get(i).put("auth", 1);
//			} else if ((Long) merchantPlans.get(i).get("personalAuth") > 0) {
//				merchantPlans.get(i).put("auth", 2);
//			} else {
//				merchantPlans.get(i).put("auth", 0);
//			}

			// 判断企业认证类型 1-企业认证 2-个人认证 0-没有认证
			judgeAuth(merchantPlans, i);

			// 对用户和商户之间的距离进行编辑
			String distanceStr = "";
			if (merchantPlans.get(i).get("distance") != null) {
				int distance = (Integer) merchantPlans.get(i).get("distance");
				if (distance >= 1000) {
					double kmDistance = ((double) distance) / 1000;
					BigDecimal bd = new BigDecimal(kmDistance);
					kmDistance = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					distanceStr = "距离商户" + kmDistance + "公里";
				} else {
					distanceStr = "距离商户" + Math.round(distance) + "米";
				}
				merchantPlans.get(i).put("distance", distanceStr);
			} else {
				merchantPlans.get(i).put("distance", "");
			}
		}

		totalPage = this.userOrderDao.getOrderMerchantPlanTotalPage(paramMap);
		totalPage = BusinessUtil.totalPageCalc(totalPage);

		jsonObject = new ResultJSONObject("000", "获取订单供应商列表成功");
		jsonObject.put("totalPage", totalPage);
		jsonObject.put("merchantPlans", merchantPlans);
		return jsonObject;
	}

	/** 获取订单评价信息 */
	@Override
	public List<Map<String, Object>> getAssessOrder(Long orderId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		return this.userOrderDao.getAssessOrder(paramMap);
	}

	/** 评价订单 */
	@Transactional(rollbackFor = Exception.class)
	public int assessOrder(String appType, Long orderId, String attitudeEvaluation, String qualityEvaluation, String speedEvaluation, String textEvaluation) throws Exception {

		// 格式化$符号
		textEvaluation = StringUtil.formatDollarSign(textEvaluation);
		int result = 0;
		int check = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		Map<String, Object> cachedOrderInfo = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
		paramMap.put("orderId", orderId);

		if (cachedOrderInfo == null) {
			check = this.userOrderDao.checkAssessOrder(paramMap);
		} else {
			check = StringUtil.nullToInteger(cachedOrderInfo.get("evaluate"));
		}
		if (check == 0) {
			paramMap.put("attitudeEvaluation", attitudeEvaluation);
			paramMap.put("qualityEvaluation", qualityEvaluation);
			paramMap.put("speedEvaluation", speedEvaluation);
			paramMap.put("textEvaluation", textEvaluation);
			paramMap.put("appType", appType);
			Long userId = this.userOrderDao.getOrderUserId(paramMap);
			paramMap.put("userId", userId);
			Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
			paramMap.put("merchantId", merchantId);
			Integer serviceTypeId = this.userOrderDao.getServiceType(paramMap);
			paramMap.put("serviceTypeId", serviceTypeId);
			result += this.userOrderDao.assessOrder(paramMap);
			result += this.userOrderDao.updateAssessMerchantStatistics(paramMap);

			if (cachedOrderInfo != null) {
				cachedOrderInfo.put("evaluate", 1);
				userRelatedCacheServices.cacheUserOrderWithJson(cachedOrderInfo.get("userId").toString(), cachedOrderInfo);
			}
//			// 清除商户缓存信息  TODO 暂时去除
//			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO_V23, StringUtil.null2Str(merchantId));
//			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO_V24, StringUtil.null2Str(merchantId));
			
			//评价订单后，强制清除商户的评价缓存  Revoke 2016.4.26
			commonCacheService.deleteObject(CacheConstants.MERCHANT_ESTIMATE, merchantId.toString());
		} else {
			return -1;
		}
		return result;
	}

	/** 为订单选择商户方案 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int chooseMerchantPlan(String appType, Long merchantId, Long merchantPlanId, Long orderId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int result = 0;
		int check = 0;
		paramMap.put("orderId", orderId);
		paramMap.put("merchantId", merchantId);
		paramMap.put("merchantPlanId", merchantPlanId);
		paramMap.put("appType", appType);

		// 如果缓存存在，则根君缓存进行判断
		Map<String, Object> cachedOrderInfo = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
		if (cachedOrderInfo != null) {
			if (!cachedOrderInfo.containsKey("merchantId") && !cachedOrderInfo.containsKey("merchantPlanId") && cachedOrderInfo.get("merchantId") == null
					&& cachedOrderInfo.get("merchantPlanId") == null) {
				check = 1;
			}
		} else {
			check = this.userOrderDao.checkChooseMerchantPlan(paramMap);
		}

		if (check > 0) {
			Long receiveEmployeesId = userOrderDao.getMerchantPlanReceiveEmployeesId(paramMap);
			paramMap.put("receiveEmployeesId", receiveEmployeesId);

			Long userId = this.userOrderDao.getOrderUserId(paramMap);
			paramMap.put("userId", userId);

			if (StringUtils.equals("sxd", appType)) {
				paramMap.put("orderStatus", 5);
			} else {
				paramMap.put("orderStatus", 3);
			}
			result += this.userOrderDao.chooseMerchantPlan(paramMap);
			if (this.userOrderDao.checkMerchantUsers(paramMap) > 0) {
				result += this.userOrderDao.updateMerchantUsers(paramMap);
			} else {
				result += this.userOrderDao.insertMerchantUsers(paramMap);
			}

			// 如果缓存存在，同步更新缓存中的状态
			if (cachedOrderInfo != null) {
				cachedOrderInfo.put("merchantId", merchantId);
				cachedOrderInfo.put("merchantPlanId", merchantPlanId);
				if (StringUtils.equals("sxd", appType)) {
					cachedOrderInfo.put("orderStatus", 5);
				} else {
					cachedOrderInfo.put("orderStatus", 3);
				}
				cachedOrderInfo.put("confirmTime", System.currentTimeMillis());
				userRelatedCacheServices.cacheUserOrderWithJson((String) cachedOrderInfo.get("userId"), cachedOrderInfo);
			}
		}
		return result;
	}

	/** 获取用户此次订单可以使用的代金券列表 */
	@Override
	public JSONObject getUserAvailablePayVouchersInfo(String appType, Long userId, Long serviceType, Long merchantId, int pageNo) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("serviceType", serviceType);
		paramMap.put("merchantId", merchantId);
		paramMap.put("startNum", pageNo * Constant.PAGESIZE);
		paramMap.put("pageSize", Constant.PAGESIZE);
		paramMap.put("appType", appType);

		JSONObject jsonObject = null;
		int totalPage = 0;
		List<Map<String, Object>> resultMap = this.userOrderDao.getUserAvailablePayVouchersInfo(paramMap);
		for (int i = 0; i < resultMap.size(); i++) {
			BusinessUtil.disposePath(resultMap.get(i), "couponsTypePicPath");
		}
		totalPage = this.userOrderDao.getUserAvailablePayVouchersInfoTotalPage(paramMap);
		totalPage = BusinessUtil.totalPageCalc(totalPage);
		jsonObject = new ResultJSONObject("000", "获取用户此次订单可以使用的代金券列表成功");
		jsonObject.put("vouchersInfo", resultMap);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

	/** 确认订单 金额 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int confirmOrderPrice(Long orderId, Long merchantId, Double price, Long vouchersId, Double actualPrice) throws Exception {
		int result = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		paramMap.put("merchantId", merchantId);
		paramMap.put("price", price);
		if (vouchersId != null && vouchersId > 0) {
			paramMap.put("userVouchersId", vouchersId);
			Map<String, Object> voucherInfo = this.vouchersInfoDao.getVouchersInfo(paramMap);
			BigDecimal vPrice = (BigDecimal) voucherInfo.get("price");
			// 判断 输入的金额 当 大于 代金券金额时 执行
			if (BigDecimal.valueOf(price).compareTo(vPrice) == 1) {
				// 判断 输入的金额 减 代金券金额时 是否等于实际金额
				if (BigDecimal.valueOf(price).subtract(vPrice).compareTo(BigDecimal.valueOf(actualPrice)) != 0) {
					return -1;
				}
			}
		}
		paramMap.put("actualPrice", actualPrice);
		result += this.userOrderDao.confirmOrderPrice(paramMap);

		// 如果缓存存在，同步更新缓存的状态
		Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
		if (cachedOrders != null) {
			cachedOrders.put("orderPrice", price);
			cachedOrders.put("orderActualPrice", actualPrice);
			cachedOrders.put("orderStatus", 4);
			cachedOrders.put("vouchersId", vouchersId);
			cachedOrders.put("confirmTime", System.currentTimeMillis());
			userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
		}
		// 删除(商户当前)代金券缓存
		commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + StringUtil.null2Str(merchantId), true);
		return result;
	}

	/** 完成支付宝订单 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int finishAliPayOrder(String appType, Long orderId) throws Exception {
		int result = 0;

		Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
		if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null && cachedOrders.containsKey("orderStatus")
				&& StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
			return -1; // 重复支付
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		paramMap.put("appType", appType);

		Long userId = this.userOrderDao.getOrderUserId(paramMap);
		paramMap.put("userId", userId);

		Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
		paramMap.put("merchantId", merchantId);

		Long userVouchersId = this.userOrderDao.getOrderVouchersId(paramMap);
		paramMap.put("userVouchersId", userVouchersId);

		Double orderPrice = this.userOrderDao.getOrderPrice(paramMap);
		paramMap.put("orderPrice", orderPrice);

		Double actualPrice = this.userOrderDao.getOrderActualPrice(paramMap);
		paramMap.put("actualPrice", actualPrice);

		result += this.userOrderDao.finishPayOrder(paramMap);
		result += this.userOrderDao.updateUserStatisticsService(paramMap);
		result += this.userOrderDao.updateMerchantStatisticsService(paramMap);
		if (this.userOrderDao.updateUserVouchersInfo(paramMap) > 0) {
			this.userOrderDao.updateMerchantVouchersInfo(paramMap);
			commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + merchantId, true);
		}
		result += this.userOrderDao.insertMerchantPaymentDetails(paramMap);

		// 如果缓存存在，同步更新缓存的状态
		if (cachedOrders != null) {
			cachedOrders.put("orderStatus", 5);
			cachedOrders.put("orderPayType", 1);
			cachedOrders.put("dealTime", System.currentTimeMillis());
			userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
		}

		return result;
	}

	/** 完成微信订单 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int finishWeChatOrder(String appType, Long orderId) {
		try {
			int result = 0;
			Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
			if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null && cachedOrders.containsKey("orderStatus")
					&& StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
				return -1; // 重复支付
			}

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			paramMap.put("appType", appType);

			Long userId = this.userOrderDao.getOrderUserId(paramMap);
			paramMap.put("userId", userId);

			Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
			paramMap.put("merchantId", merchantId);

			Long userVouchersId = this.userOrderDao.getOrderVouchersId(paramMap);
			paramMap.put("userVouchersId", userVouchersId);

			Double orderPrice = this.userOrderDao.getOrderPrice(paramMap);
			paramMap.put("orderPrice", orderPrice);

			Double actualPrice = this.userOrderDao.getOrderActualPrice(paramMap);
			paramMap.put("actualPrice", actualPrice);

			result += this.userOrderDao.finishWeChatOrder(paramMap);
			result += this.userOrderDao.updateUserStatisticsService(paramMap);
			result += this.userOrderDao.updateMerchantStatisticsService(paramMap);
			if (this.userOrderDao.updateUserVouchersInfo(paramMap) > 0) {
				this.userOrderDao.updateMerchantVouchersInfo(paramMap);
				commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + merchantId, true);
			}
			result += this.userOrderDao.insertMerchantPaymentDetails(paramMap);

			// 如果缓存存在，同步更新缓存的状态
			if (cachedOrders != null) {
				cachedOrders.put("orderStatus", 5);
				cachedOrders.put("orderPayType", 2);
				cachedOrders.put("dealTime", System.currentTimeMillis());
				userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
			}

			return result;
		} catch (Exception e) {
			logger.error("微信支付出错：" + e.getMessage(), e);
			throw new ApplicationException(e, "wechatOrder", "微信支付出错");
		}
	}

	/** 完成现金订单 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int finishCashOrder(String appType, Long orderId, Long merchantId, String price) {
		try {
			int result = 0;

			Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
			if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null && cachedOrders.containsKey("orderStatus")
					&& StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
				return -1; // 重复支付
			}

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			paramMap.put("merchantId", merchantId);
			paramMap.put("price", price);
			paramMap.put("actualPrice", price);
			paramMap.put("appType", appType);

			result += this.userOrderDao.confirmOrderPrice(paramMap);
			result += this.userOrderDao.finishCashOrder(paramMap);
			result += this.userOrderDao.updateUserStatisticsCashService(paramMap);
			result += this.userOrderDao.updateMerchantStatisticsCashService(paramMap);
			result += this.userOrderDao.insertMerchantPaymentDetails(paramMap);

			// 如果缓存存在，同步更新缓存的状态
			if (cachedOrders != null) {
				cachedOrders.put("orderPrice", price);
				cachedOrders.put("orderActualPrice", price);
				cachedOrders.put("merchantId", merchantId);
				cachedOrders.put("confirmTime", System.currentTimeMillis());
				cachedOrders.put("orderStatus", 5);
				cachedOrders.put("orderPayType", 3);
				cachedOrders.put("dealTime", System.currentTimeMillis());
				userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
			}

			return result;
		} catch (Exception e) {
			logger.error("现金支付出错 :" + e.getMessage(), e);
			throw new ApplicationException(e, "cashOrder", "现金支付出错");
		}
	}

	/** 删除订单 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteOrder(Long orderId) throws Exception {
		try {
			int result = 0;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			result += this.userOrderDao.deleteOrder(paramMap);

			userRelatedCacheServices.rmUserOrderCache(orderId.toString());

			return result;
		} catch (Exception e) {
			logger.error("删除订单出错:" + e.getMessage(), e);
			throw new ApplicationException(e, "deleteOrder", "删除订单出错");
		}
	}

	/** 更新订单状态 */
	@Transactional(rollbackFor = Exception.class)
	public int updateOrderStatus(Long orderId, String orderStatus) {
		try {
			int result = 0;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			paramMap.put("orderStatus", orderStatus);
			result += this.userOrderDao.updateOrderStatus(paramMap);

			Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
			if (cachedOrders != null) {
				cachedOrders.put("orderStatus", orderStatus);
				userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
			}

			return result;
		} catch (Exception e) {
			logger.error("订单状态更新错误：" + e.getMessage(), e);
			throw new ApplicationException(e, "updateOrderStatus", "更新订单状态错误");
		}
	}

	/** 推送信息给用户  可删除*/
	@Override
	public JSONObject pushMessageToListForYHB(Map<String, String[]> map) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("-------推送发过来的参数-------" + map.toString());
		}
		String paraString = "";
		String appType = Constant.PUSH_CONFIG.APP_TYPE_OMENG;
		String data = "";
		//
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			paraString = (String) entry.getKey();
		}
		JSONObject para = JSON.parseObject(paraString);
		// appType = para.getString("appType");
		data = para.getString("data");
		Long orderId = StringUtil.nullToLong(para.get("orderId"));
		JSONObject jsonObject = null;

		// 获取用户ClientID
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("orderId", orderId);
		// 获取订单状态
		int pushType = this.getOrderPushType(orderId);
		List<Map<String, Object>> resultList = this.userOrderDao.getUserClientIdsByOrderId(paramMap);
		List<Map<String, Object>> AndroidPushInfo = new ArrayList<Map<String, Object>>();// 安卓终端
		// List<String> iosClientIDs = new ArrayList<String>();// 苹果终端
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, Object> tempmap : resultList) {
				String cid = StringUtil.null2Str(tempmap.get("client_id"));
				if (!StringUtil.isNullStr(cid)) {
					if (Constant.PUSH_CLIENT_TYPE_IOS == StringUtil.nullToInteger(tempmap.get("client_type"))) {
						// ios
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("token", cid);
						info.put("userId", StringUtil.null2Str(tempmap.get("user_id")));
						iosPushInfoList.add(info);
					} else {
						// 安卓
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("clientId", cid);
						info.put("userId", StringUtil.null2Str(tempmap.get("user_id")));
						// 安卓
						AndroidPushInfo.add(info);
					}
				}
			}
		}
		data = data + "," + pushType;
		try {
			// 推送给用户
			if (AndroidPushInfo != null && AndroidPushInfo.size() > 0) {
				// 安卓推送
				// PushUtil.pushMessageToListForYHB(appType, clientIDs, data);
				PushAndroidMessageToYHB pushToYhb = new PushAndroidMessageToYHB(appType, AndroidPushInfo, data, androidAppPushConfigDao, commonCacheService);
				Thread push = new Thread(pushToYhb);
				push.start();
			}
			if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
				// ios推送
				Map<String, Object> msg = PushMessageUtil.getIosUserPushMsg(appType, StringUtil.nullToLong(orderId), 0, "0");
				Map<String, Object> cert = dictionaryService.getIosPushCertFromDict(Constant.IOS_USER_CERT);
				cert.put(Constant.IOS_CERT_TYPE, Constant.IOS_USER_CERT);
				IosPushUtil.push(iosPushInfoList, msg, cert);
			}
			jsonObject = new ResultJSONObject("000", "推送成功");
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("error", "推送失败");
			logger.error("推送失败:" + e.getMessage(), e);
			return jsonObject;
		}
		return jsonObject;

	}

	/** 获得订单推送状态 */
	@Override
	public int getOrderPushType(Long orderId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		int result = 0;
		result = this.userOrderDao.getOrderPushType(paramMap);
		// 当已经支付时候
		if (result == 5) {
			// 判断是否评价
			if (this.userOrderDao.checkIsEvaluation(paramMap) == 1) {
				result = 8;
			}
		}
		return result;
	}

	//处理未选择报价方案的过期订单----以下单时间+过期设置的间隔时间为准
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int handleNoChoosedOrders() {
//		System.out.println(DateUtil.getNowYYYYMMDDHHMMSS()+"开始调度处理未选择报价方案的过期订单");
		int result = 0;
		// 只有获取到订单定时调度锁的进程才进行处理。
		JedisLock schduleLock = userRelatedCacheServices.getOrderNoChoosedExireLock();
		try {
			if (schduleLock.acquire()) {
//				System.out.println("获取到订单定时调度锁");
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("orderStatus", Constant.OVERDUE_ORDER);				
				//Integer days = orderInfoDao.getExpiredTime(BusinessAction.NO_CHOOSED_ORDER);
				/**
				 *  有报价方案的订单，有两种情况需要关闭
				 *  1.立即服务:立即服务时间+设定的天数【配置】< 当前时间
				 *  2.预约服务:预约时间+设定的天数【配置】< 当前时间
				 */
				Integer immediatedays = orderInfoDao.getExpiredTime(CacheConstants.EXPIRES_IMMEDIATE_DAY);//立即服务过期时间配置
				Date immediatedaysCheckTime = new Date(System.currentTimeMillis() - immediatedays * 24 * 3600 * 1000);//days * 24 * 
				paramMap.put("immediate_check_time", immediatedaysCheckTime);	
				Integer days = orderInfoDao.getExpiredTime(CacheConstants.EXPIRES_NO_IMMEDIATE_DAY);//预约服务过期时间配置
				Date checkTime = new Date(System.currentTimeMillis() - days * 24 * 3600 * 1000);//days * 24 * 
				paramMap.put("check_time", checkTime);	
				//Date currentTime = new Date(System.currentTimeMillis());
				//paramMap.put("curr_time", DateUtil.dateToString(currentTime));
				
					//List<Map<String, String>> orders = orderInfoDao.getNoChoosedExpiredOrders(paramMap);

					List<Map<String, String>> orders = orderInfoDao.selectNoChoosedExpiredOrders(paramMap);
//					System.out.println("共有："+orders.size()+"个订单需要处理");
					
					if (orders != null && orders.size() > 0) {
							String ids = getIds(orders);
//							System.out.println("需要处理的订单ID为："+ids);
							
							Map<String,Object> updateCondition= new HashMap<String,Object>();
							
							updateCondition.put("ids", ids);
							
							updateCondition.put("orderStatus",Constant.OVERDUE_ORDER);
						
							//更新用户侧订单状态
						    int i=orderInfoDao.batchUpdateClientOrderStatus(updateCondition);
//						    if(i>0){
//								System.out.println("更新用户侧订单状态成功");						    	
//						    }
						    // 如果订单在缓存中，则更新缓存中的状态。
							userRelatedCacheServices.cancelOrderInCache(orders);
							
							//更新商户侧订单状态
							i=orderInfoDao.batchUpdateMerchantOrderStatus(updateCondition);							
//							if(i>0){
//								System.out.println("更新商户侧订单状态成功");						    	
//							}
					
							
							//插入时间轴
							Map<String,Object> actionMap= new HashMap<String,Object>();
							actionMap.put("actionCode",BusinessAction.TIMEOUT_NOT_CHOOSE);
							for(Map<String,String> order:orders) {
									actionMap.put("orderId",order.get("orderId"));
									timeLineDao.insertTimeLine(actionMap);
							}
							
							
							//更新商户侧订单缓存。 2016.6.6
							Map<String,Object> param = new HashMap<String,Object>();
							for(Map<String,String> order:orders) {
								String orderId= order.get("orderId");
								param.put("orderId", orderId);
								List<Map<String,Object>> merchantIds = this.orderInfoDao.getMerchantsForSpeicalOrder(param);
								merchantCacheService.batchUpdateMerchantOrderStatus(orderId, merchantIds, Constant.OVERDUE_ORDER+"");
							}

						  return orders.size();
					}else{

						    return 0;
					}
			}else{
//				System.out.println("没有获取到订单定时调度锁");
			}
		} catch (InterruptedException e) {
			logger.error("处理用户未选定报价方案过期订单失败：" + e.getMessage(), e);
		} finally {
			userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject saveOrder(String appType, String serviceType, Map<String, Object> orderInfoMap, List<String> voicePaths, List<String> picturePaths, String ip) throws Exception {
		orderInfoMap = StringUtil.formatDollarSign(orderInfoMap);
		ResultJSONObject jsonObject = new ResultJSONObject("000", "订单保存成功");
		Boolean needPush = false;
		// 如果省市为空，则根据IP查省市
		String province = (String) orderInfoMap.get("province");
		String city = (String) orderInfoMap.get("city");
		orderInfoMap.put("province", province);
		orderInfoMap.put("city", city);

		// 生成订单编号，使用20位随机字符串
		String orderNo = IdGenerator.getOrderNo(20);
		orderInfoMap.put("orderNo", orderNo);

		if (orderInfoMap.get("goodsIds") != null) {
			orderInfoMap.put("orderStatus", 3);
			needPush = true;
		} else {
			orderInfoMap.put("orderStatus", 1);
			orderInfoMap.put("merchantId", null);
		}
//		//订单列表 判断预约今天，明天需要服务时间
//		String serviceTime_=this.orderApiService.extractServiceTime(orderInfoMap);
//		if(serviceTime_.equals("")){
//			orderInfoMap.put("serviceTime",null);			
//		}else{
//			orderInfoMap.put("serviceTime",serviceTime_);		
//		}

		this.userOrderDao.insertCommonOrder(orderInfoMap);
		// 保存订单行业明细
		this.orderApiService.saveAppOrder(orderInfoMap);

		// 保存商品明细
		if (orderInfoMap.get("goodsIds") != null) {
			this.saveOrderGoods((Long) orderInfoMap.get("orderId"), (String) orderInfoMap.get("goodsIds"), (String) orderInfoMap.get("goodsNums"));
		}

		// 判断是否有统计信息,来确认在不同APP下面是否有记录
		int result = this.userOrderDao.checkUserStatisticsIsEmpty(orderInfoMap);
		if (result == 0) {
			this.userOrderDao.initUserInfoStatistics(orderInfoMap);
		}
		this.userOrderDao.updateUserSstatisticsBespeak(orderInfoMap);

		// 保存附件信息
		Long orderId = (Long) orderInfoMap.get("orderId");
		this.insertOrderAttachment(orderId, voicePaths, picturePaths);

		Map<String, Object> orderInfo = userOrderDao.getBasicOrderInfo(orderInfoMap);
//		if (appType.startsWith("yxt")) {
//			// orderInfo.remove("appIcon");
//			// orderInfo.put("appIcon",
//			// this.userOrderDao.getYxtAppIcon(StringUtil.null2Str(orderId)));
//			// 如果是易学堂，则重新获取服务类型
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap.put("appType", "yxt");
//			paramMap.put("orderId", orderId);
//			Map<String, String> serviceTypeMap = userOrderDao.getYxtServiceTypeByOrderId(paramMap);
//			if (serviceTypeMap != null) {
//				orderInfo.put("serviceTypeName", serviceTypeMap.get("serviceTypeName"));
//				orderInfo.put("appIcon", serviceTypeMap.get("path"));
//			}
//		} else if (appType.startsWith("ams")) {
//			String[] pathes = orderInfo.remove("appIcon").toString().split(",");
//			orderInfo.put("appIcon", pathes[1]);
//		}
		BusinessUtil.disposePath(orderInfo, "appIcon");

		String userId = orderInfoMap.get("userId").toString();

		// 用户户订单缓存过，则把新加的订单也插入缓存
		if (userRelatedCacheServices.getUserOrderEntrySize(userId) > 0) {
			String serviceType_=orderInfo.get("serviceType")==null?"":orderInfo.get("serviceType").toString();
			String serviceTime="";
			if ("".equals(orderInfo.get("serviceTime")) || orderInfo.get("serviceTime")==null){
//				logger.warn("服务时间未初始值，订单号"+ orderInfo.get("orderId"));
			}else{
				serviceTime=BusinessUtil.formatServiceTime((java.util.Date)orderInfo.get("serviceTime"),appType,serviceType_);
			}	
			orderInfo.put("serviceTime", serviceTime);
			userRelatedCacheServices.addUserOrderCache(orderInfo, userId, orderId.toString());
		}

		jsonObject = new ResultJSONObject("000", "订单保存成功");
		jsonObject.put("orderInfo", orderInfo);

		return jsonObject;

	}


	@Override
	public JSONObject getDetialOrderInfo(String appType, Long orderId, Long serviceType) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		paramMap.put("appType", appType);
		// 基础订单信息
		Map<String, Object> orderInfo = userRelatedCacheServices.getCachedUserOrder(orderId.toString());

		if (orderInfo == null) {
			orderInfo = this.userOrderDao.getOrderInfo(paramMap);
		} else {
			// 返回参数略有不同，需要加个key,value
			orderInfo.put("id", orderInfo.get("orderId"));
		}
		if(StringUtil.isEmpty(serviceType)){//ios推送订单，点击推送通知，跳转到订单详情，没有传serviceType参数，需要后台获取
			serviceType=StringUtil.nullToLong(orderInfo.get("serviceType"));
		}
		paramMap.put("serviceType", serviceType);
		// 判断这个订单有没有选择方案
		Integer planCount = userOrderDao.checkChooseMerchantPlan(paramMap);
		if (planCount > 0) {
			orderInfo.put("planCount", 0);
		} else {
			orderInfo.put("planCount", 1);
		}
		Map<String, Object> merchantInfo = null;
		Map<String, Object> reMerchantInfo = this.userOrderDao.getOrderMerchantInfo(paramMap);
		if (reMerchantInfo != null && reMerchantInfo.get("merchantId") != null) {
			merchantInfo = reMerchantInfo;
		}
		String orderStatus_ = orderInfo.get("orderStatus")==null?"":orderInfo.get("orderStatus").toString();
		paramMap.put("orderStatus", orderStatus_);	
		Map<String, Object> orderText = orderApiService.selectAppOrderText(paramMap);
		Map<String, Object> orderNote = this.orderInfoService.getOrderNote(paramMap);

		if (orderText == null) {
			orderText = new LinkedHashMap<String, Object>();
		}
		String demand = (String) orderText.remove("demand");

		if (demand != null) {
			demand = demand.trim();
			if (!demand.equals(""))
				orderNote.put("demand", "补充说明：" + demand);
		}

		BusinessUtil.disposeManyPath(orderInfo, "serviceTypePicPath");
		BusinessUtil.disposeManyPath(merchantInfo, "merchantIcon");
		BusinessUtil.disposeManyPath(orderNote, "picturePath", "voicePath");

		// 将值为空的移除
		BusinessUtil.removeNullValueFromMap(orderText);

		/** start 2015-12-22 王瑞 新增返回员工ID */
		// 如果用户选择服务商了，但是抢单员工ID却没有，则继续查询方案表
		int orderStatus = Integer.parseInt(orderInfo.get("orderStatus") == null ? "0" : orderInfo.get("orderStatus").toString());
		Long receiveEmployeesId = Long.parseLong(orderInfo.get("receiveEmployeesId") == null ? "0" : orderInfo.get("receiveEmployeesId").toString());
		if (orderStatus >= 3 && receiveEmployeesId == 0 && merchantInfo != null) {
			Long merchantId = Long.parseLong(merchantInfo.get("merchantId") == null ? "0" : merchantInfo.get("merchantId").toString());
			// 查询方案表，得到抢单员工ID
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("orderId", orderId);
			map.put("merchantId", merchantId);
			receiveEmployeesId = userOrderDao.getReceiveEmployeesIdFromMerchantPlan(map);
			orderInfo.put("receiveEmployeesId", receiveEmployeesId);
		}
		/** end */

		jsonObject.put("orderInfo", orderInfo);
		jsonObject.put("orderText", orderText.values().toArray());
		jsonObject.put("merchantInfo", merchantInfo);
		jsonObject.put("orderNote", orderNote);
		jsonObject.put("orderGoodsInfo", this.merchantOrderManageService.orderGoodsInfoEdit(appType, orderId, false));

		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获取用户订单列表成功");

		return jsonObject;
	}

	@Override
	public JSONObject getBasicOrderList(Long catalogId, Long userId, String orderStatus, int pageNo) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		int totalPage = 0;
		JSONObject jsonObject = new ResultJSONObject();
		if (userId == null || String.valueOf(userId).equals("0") || String.valueOf(userId).equals("")) {
			return new ResultJSONObject("error", "请登录后查看此信息");
		} else {
			List<Map<String, Object>> cachedOrders = null;
			// 获取当前订单图标对应的版本号
			Integer lastImgVersion = imageCacheService.getImageVersionCache(CacheConstants.IMAGE_CACHE.ORDER_ICON_VERSION);

			// 生成缓存时，订单图标对应的版本。
			Integer orderCacheImageVersion = userRelatedCacheServices.getVersionOfOrderCache("" + userId);

			// 如果 缓存列表存在，则从缓存中读取订单，否则先从数据库中读取订单，再从缓存中加载。
			if (userRelatedCacheServices.getUserOrderEntrySize("" + userId) < 1) {
				cachedOrders = this.getTopnBasicOrderListInfo(userId, CacheConstants.ORDER_PER_USER_SIZE);
			} else {
				cachedOrders = userRelatedCacheServices.getCachedUserOrders("" + userId);
				if (hasNewImageVersion(lastImgVersion, orderCacheImageVersion)) {
					cachedOrders = this.getTopnBasicOrderListInfo(userId, CacheConstants.ORDER_PER_USER_SIZE);
					userRelatedCacheServices.regOrderCacheVersion("" + userId, lastImgVersion.intValue());
				}
			}
			totalPage = constructResultFromCachedOrders(cachedOrders, resultList, catalogId, orderStatus, pageNo);
		}
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获取用户订单列表成功");
		jsonObject.put("orderList", resultList);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

	public List<Map<String, Object>> getBasicOrderListInfo(String appType, Long userId, String orderStatus, int pageNo) {
		List<Map<String, Object>> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (pageNo < 0) {
			pageNo = 0;
		}

		paramMap.put("userId", userId);
		// 商户订单状态字典表类型
		paramMap.put("dictType", "userOrderStatus");
		paramMap.put("startNum", pageNo * Constant.PAGESIZE);
		paramMap.put("pageSize", Constant.PAGESIZE);

		if (orderStatus != null) {
			paramMap.put("orderStatus", orderStatus);
		}
		if (appType != null) {
			paramMap.put("appType", appType);
		}

		resultList = this.userOrderDao.getBasicOrderListInfo(paramMap);
		// 文件路径的补全
		for (Map<String, Object> orderMap : resultList) {
			BusinessUtil.disposeManyPath(orderMap, "appIcon");
		}
		return resultList;
	}

	public int getBasicOrderListInfoTotalPage(String appType, String orderStatus, Long userId) {
		int totalPage = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("dictType", "userOrderStatus");
		if (orderStatus != null) {
			paramMap.put("orderStatus", orderStatus);
		}
		if (appType != null) {
			paramMap.put("appType", appType);
		}
		totalPage = this.userOrderDao.getBasicOrderListInfoTotalPage(paramMap);
		return totalPage;
	}

	// 从数据库中加载TOPn 并写入缓存
	private List<Map<String, Object>> getTopnBasicOrderListInfo(Long userId, int ORDER_PER_USER_SIZE) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("startNum", 0);
		params.put("pageSize", ORDER_PER_USER_SIZE);
		List<Map<String, Object>> resultList = this.userOrderDao.getTopnBasicOrderListInfo(params);
//		List<String> yxtOrderIds = new ArrayList<String>();
		// 文件路径的补全
		for (Map<String, Object> orderMap : resultList) {
//			String appType = StringUtil.null2Str(orderMap.get("appType"));
//			String orderId = StringUtil.null2Str(orderMap.get("orderId"));
//			if (appType.startsWith("yxt")) {
//				yxtOrderIds.add(StringUtil.null2Str(orderId));
//				// 如果是易学堂，则重新获取服务类型
//				Map<String, Object> paramMap = new HashMap<String, Object>();
//				paramMap.put("appType", "yxt");
//				paramMap.put("orderId", orderId);
//				Map<String, String> serviceTypeMap = userOrderDao.getYxtServiceTypeByOrderId(paramMap);
//				if (serviceTypeMap != null) {
//					orderMap.put("serviceTypeName", serviceTypeMap.get("serviceTypeName"));
//					orderMap.put("appIcon", serviceTypeMap.get("path"));
//				}
//			} else if (appType.startsWith("ams")) {
//				String[] pathes = orderMap.remove("appIcon").toString().split(",");
//				orderMap.put("appIcon", pathes[1]);
//			}
			BusinessUtil.disposeManyPath(orderMap, "appIcon");
//			String serviceType_=orderMap.get("serviceType")==null?"":orderMap.get("serviceType").toString();
//			String serviceTime="";
//			if ("".equals(orderMap.get("serviceTime")) || orderMap.get("serviceTime")==null){
//				logger.warn("服务时间未初始值，订单号"+ orderMap.get("orderId"));
//			}else{
//				serviceTime=BusinessUtil.formatServiceTime((java.util.Date)orderMap.get("serviceTime"),appType,serviceType_);
//			}	
//			orderMap.put("serviceTime", serviceTime);
		}
		if (resultList != null && resultList.size() > 0) {
			userRelatedCacheServices.cacheUserOrders(resultList, "" + userId);
		}
		return resultList;
	}

	// 根据缓存的订单来过滤复合条件的记录。
	private int constructResultFromCachedOrders(List<Map<String, Object>> cachedOrders, List<Map<String, Object>> resultList, Long catalogId, String orderStatus, int pageNo) {

		List<Map<String, Object>> filterOrders = new ArrayList<Map<String, Object>>();

		if (cachedOrders == null || cachedOrders.size() < 1) {
			return 0;
		}

//		// 截取appType _ 前面部分
//		if (appType != null && appType.indexOf("_") > 0) {
//			appType = appType.split("_")[0];
//		}
		//获取appType下所属的serviceTypeId,然后匹配
		List<Map<String,Long>> serviceTypeIdsList=new ArrayList<Map<String,Long>>();
		if(catalogId!=null){
			serviceTypeIdsList= userOrderDao.getServiceTypeIdsByCatalogId(catalogId);
		}
		for (Map<String, Object> record : cachedOrders) {
//			String cachedAppType = record.get("appType").toString();
			Long cacheServiceType=StringUtil.nullToLong(record.get("serviceType")) ;
//			System.out.println(cacheServiceType.toString());
			if(catalogId!=null){
				int temp=0;
				for(Map<String,Long> map :serviceTypeIdsList){
					Long id=map.get("id");
					if(id==cacheServiceType){
						temp=1;
					}
				}
				if(temp==0){
					continue;
				}
			}
			//此处应该加一个从缓存中读取appType
//			if (appType != null && !appType.equals("")) {
//				// 用以区分ydc和yd
//				if (cachedAppType.contains("_") && !cachedAppType.startsWith(appType) && !appType.equals("mst")) {
//					continue;
//				}
//				if (!cachedAppType.contains("_") && !cachedAppType.equals(appType) && !appType.equals("mst")) {
//					continue;
//				}
//				if (appType.equals("yd") && cachedAppType.endsWith("xxb")) {
//					continue;
//				}
//				if (cachedAppType.contains("_") && cachedAppType.startsWith(appType) && cachedAppType.endsWith("xxb")) {
//					continue;
//				}
//				if (appType.equals("mst")) {
//					if (!cachedAppType.startsWith(appType) && !cachedAppType.endsWith("xxb")) {
//						continue;
//					}
//				}
//			}
			if (orderStatus != null && !orderStatus.equals("") && !(record.get("orderStatus").toString()).equals(orderStatus)) {
				continue;
			}			
			// 针对服务时间做处理
			BusinessUtil.handlerOrderServiceTime(record);
			filterOrders.add(record);
		}
		TimeDescComparator cp = new TimeDescComparator("joinTimeStamp");
		Collections.sort(filterOrders, cp);

		if (filterOrders.size() < 1 || pageNo * Constant.PAGESIZE > filterOrders.size()) {
			return 0;
		} else {
			for (int num = 0, startIndex = pageNo * Constant.PAGESIZE; num < Constant.PAGESIZE && startIndex < filterOrders.size(); num++, startIndex++) {
				resultList.add(filterOrders.get(startIndex));
			}
			return 1 + (filterOrders.size() - 1) / Constant.PAGESIZE;
		}

	}

	/*
	 * 保存文件到数据库中
	 */
	@Transactional(rollbackFor = Exception.class)
	private int insertOrderAttachment(Long orderId, List<String> voicePaths, List<String> picturePaths) {
		int result = 0;
		try {

			if (voicePaths.size() > 0) {
				for (String path : voicePaths) {
					Map<String, Object> attaMap = new HashMap<String, Object>();
					attaMap.put("orderId", orderId);
					attaMap.put("attachmentType", Constant.VOICE);
					attaMap.put("attachmentUse", Constant.BUSINESS_VOICE);
					attaMap.put("path", path);
					result += this.userOrderDao.insertOrderAttachment(attaMap);
				}
			}

			if (picturePaths.size() > 0) {
				for (String path : picturePaths) {
					Map<String, Object> attaMap = new HashMap<String, Object>();
					attaMap.put("orderId", orderId);
					attaMap.put("attachmentType", Constant.IMAGE);
					attaMap.put("attachmentUse", Constant.BUSINESS_IMAGE);
					attaMap.put("path", path);
					result += this.userOrderDao.insertOrderAttachment(attaMap);
				}
			}
		} catch (Exception e) {
			result = 0;
			logger.error("订单附件保存失败：" + e.getMessage(), e);
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void cleanUserCache() {
		userRelatedCacheServices.cleanUserCache();
	}

	@Override
	public JSONObject getBasicOrder(Long orderId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> order = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
		if (order == null || order.size() < 1) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("orderId", orderId);
			order = userOrderDao.getBasicOrderInfo(param);

			BusinessUtil.disposeManyPath(order, "appIcon");
		}
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获取用户订单概要信息成功");
		jsonObject.put("orderInfo", order);
		return jsonObject;
	}

	/**
	 * 推送信息给商户 可删除
	 * 
	 * */
	@Override
	public JSONObject pushMessageToListForSHB(Map<String, Object> paras) {
		JSONObject jsonObject = null;

		Long orderId = StringUtil.nullToLong(paras.get("orderId"));
		String appType = StringUtil.null2Str(paras.get("appType"));
		String data = StringUtil.null2Str(paras.get("data"));
		// 获取订单状态
		int pushType = this.getOrderPushType(orderId);
		List<Map<String, Object>> resultList = null;
		if (pushType == 1) {
			/*
			 * if (Constant.DEVMODE) { resultList =
			 * this.userOrderDao.getMerchantClientIds(paras); } else {
			 */
			resultList = this.orderApiService.getMerchantClientIds(paras);
			if (resultList == null || resultList.size() < 1) {
				resultList = this.userOrderDao.getClientIdsByCity(paras);
			}
			// }
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			paramMap.put("clientIdList", resultList);
			if (resultList != null && resultList.size() > 0) {
				this.userOrderDao.insertPushMerchantOrder(paramMap);
			}

		} else if (pushType == 3 || pushType == 4 || pushType == 5 || pushType == 8) {
			resultList = this.userOrderDao.getMerchantOneClientId(paras);
		}

		List<Map<String, Object>> AndroidPushInfo = new ArrayList<Map<String, Object>>();// 安卓终端
		// List<String> iosClientIDs = new ArrayList<String>();// 苹果终端
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, Object> tempmap : resultList) {
				String cid = StringUtil.null2Str(tempmap.get("client_id"));
				if (!StringUtil.isNullStr(cid)) {
					if (Constant.PUSH_CLIENT_TYPE_IOS == StringUtil.nullToInteger(tempmap.get("client_type"))) {
						// ios
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("token", cid);
						info.put("merchantId", StringUtil.null2Str(tempmap.get("midd")));
						iosPushInfoList.add(info);
					} else {
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("clientId", cid);
						info.put("merchantId", StringUtil.null2Str(tempmap.get("midd")));
						// 安卓
						AndroidPushInfo.add(info);
					}
				}
			}
		}

		data = data + "," + pushType;

		try {
			// 推送给商户
			if (AndroidPushInfo != null && AndroidPushInfo.size() > 0) {
				// 安卓推送
				// PushUtil.pushMessageToListForSHB(appType, clientIDs, data);
				PushAndroidMessageToSHB pushToShb = new PushAndroidMessageToSHB(appType, AndroidPushInfo, data, androidAppPushConfigDao, commonCacheService);
				Thread push = new Thread(pushToShb);
				push.start();
			}
			if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
				// ios推送
				Map<String, Object> msg = PushMessageUtil.getIosMerchantPushMsg(appType, orderId, pushType, "0", "0");
				Map<String, Object> cert = dictionaryService.getIosPushCertFromDict(Constant.IOS_MERCHANT_CERT);
				cert.put(Constant.IOS_CERT_TYPE, Constant.IOS_MERCHANT_CERT);
				IosPushUtil.push(iosPushInfoList, msg, cert);
			}
			jsonObject = new ResultJSONObject("000", "推送成功");
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("error", "推送失败");
			logger.error("推送失败" + e.getMessage(), e);
			return jsonObject;
		}
		return jsonObject;

	}

	/**
	 * 推送信息给商户 可删除
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject pushMessageToListForSHBByProxy(Map<String, Object> paras) throws Exception {
		JSONObject jsonObject = null;

		Long orderId = StringUtil.nullToLong(paras.get("orderId"));
		String appType = StringUtil.null2Str(paras.get("appType"));
		String data = StringUtil.null2Str(paras.get("data"));
		String phone = StringUtil.null2Str(paras.get("phone"));
		String province = "";
		String city = "";
		String[] provinceAndCity = null;
		String isTest = "0";// 默认不是测试
		// if (Constant.DEVMODE) {
		// isTest = "0";
		// } else {
		// if (phone.startsWith(Constant.TESTPHONE)) {
		// isTest = "1";
		// }
		// }
		// 获取订单状态
		int orderStatus = this.getOrderPushType(orderId);
		List<Map<String, Object>> allMerchantList = new ArrayList<Map<String, Object>>();// 所有商户
		List<Map<String, Object>> onlineMerchantList = new ArrayList<Map<String, Object>>();// 在线商户
		if (orderStatus == 1) {
			// 获取推送范围和推送类型
			Map<String, Object> pushInfo = this.commonService.getPushInfo(appType);
			String pushRange = pushInfo.get("pushRange") == null ? "5" : pushInfo.get("pushRange").toString();
			String pushType = pushInfo.get("pushType") == null ? "0" : pushInfo.get("pushType").toString();
			paras.put("pushRange", pushRange);
			paras.put("pushType", pushType);
			paras.put("isTest", isTest);
			if (pushType.equals("0")) {
				// 按照经纬度查询
				allMerchantList = this.commonService.getAllMerchantByRange(paras);
				if (allMerchantList == null || allMerchantList.size() < 1) {
					logger.error("经纬度没查询到，根据省市查询");
					// 如果按照经纬度查不到，则按照城市查询
					if (paras.get("province") != null && !paras.get("province").toString().equals("") && paras.get("city") != null && !paras.get("city").toString().equals("")) {
						allMerchantList = this.commonService.getAllMerchantByCity(paras);
					}
				}
			} else if (pushType.equals("1")) {
				// 按照城市推查询
				if (StringUtils.equals(appType, "sxd")) {
					Map<String, Object> map = this.orderApiService.getProvinceAndCityByorderId(orderId);
					if (map != null) {
						province = map.get("province") == null ? "" : map.get("province").toString();
						city = map.get("city") == null ? "" : map.get("city").toString();
					}
				}

				provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
				if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
					paras.put("province", provinceAndCity[0]);
					paras.put("city", provinceAndCity[1]);
				}
				allMerchantList = this.commonService.getAllMerchantByCity(paras);
			} else if (pushType.equals("2")) {
				// 标签查询
				allMerchantList = this.commonService.getMerchantClientIdsByServiceTag(paras);
			} else if (pushType.equals("3")) {// 推送给所有
				// 在云端如果选择地址，则按照地址推送，如果没有选择地址则推送所有
				if (appType.equals("zyd")) {
					// 判断是否填写地址信息
					Map<String, Object> map = this.orderApiService.getProvinceAndCityByorderId(orderId);
					if (map != null) {
						province = map.get("province") == null ? "" : map.get("province").toString();
						city = map.get("city") == null ? "" : map.get("city").toString();
					}
				}

				provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
				if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
					// 如果城市不为空，则按照城市推送
					paras.put("province", provinceAndCity[0]);
					paras.put("city", provinceAndCity[1]);
					allMerchantList = this.commonService.getAllMerchantByCity(paras);
				} else {// 如果城市为空，则推送给所有商户
					allMerchantList = this.commonService.getAllMerchant(paras);
				}
			} else {// 其他

			}
			// 如果没有推送对象，则直接返回
			if (allMerchantList == null || allMerchantList.size() == 0) {
				logger.error("推送结果：无推送服务商。appType=" + appType + "，orderId=" + orderId + "，pushType=" + pushType + "，pushRange=" + pushRange);
				return new ResultJSONObject("000", "推送成功");
			}
			// 查询在线商户
			onlineMerchantList = this.commonService.getOnlineMerchant(allMerchantList);
			if (onlineMerchantList == null || onlineMerchantList.size() == 0) {
				logger.error("推送结果：无在线商户。appType=" + appType + "，orderId=" + orderId + "，pushType=" + pushType + "，pushRange=" + pushRange);
				return new ResultJSONObject("000", "推送成功");
			}
			// 将allMerchantList 处理成 onlineMerchantList 的数据格式，以便插入
			allMerchantList = this.HandlerPushMerchants(allMerchantList, onlineMerchantList,"");

			// 去重复
			List<Map<String, Object>> allMerchantListResult = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> s : allMerchantList) {
				if (Collections.frequency(allMerchantListResult, s) < 1)
					allMerchantListResult.add(s);
			}
			allMerchantList = allMerchantListResult;

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			paramMap.put("clientIdList", allMerchantList);
			if (allMerchantList != null && allMerchantList.size() > 0) {
				// 将查询到的商户插入商户-订单表
				this.userOrderDao.insertPushMerchantOrder(paramMap);
			}

		} else if (orderStatus == 3 || orderStatus == 4 || orderStatus == 5 || orderStatus == 8) {
			onlineMerchantList = this.userOrderDao.getMerchantOneClientId(paras);
		}
		// logger.error("onlineMerchantList："+onlineMerchantList.size()+"，"+onlineMerchantList.toString());
		// if(Constant.DEVMODE){
		// return new ResultJSONObject("000", "压力测试：推送功能暂停，只做推送用户查询，不做实际推送！");
		// }

		List<Map<String, Object>> AndroidPushInfo = new ArrayList<Map<String, Object>>();// 安卓终端

		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		if (onlineMerchantList != null && onlineMerchantList.size() > 0) {
			for (Map<String, Object> tempmap : onlineMerchantList) {
				String cid = StringUtil.null2Str(tempmap.get("clientId"));
				if (!StringUtil.isNullStr(cid)) {
					if (Constant.PUSH_CLIENT_TYPE_IOS == StringUtil.nullToInteger(tempmap.get("clientType"))) {
						// ios
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("token", cid);
						info.put("merchantId", StringUtil.null2Str(tempmap.get("merchantId")));
						iosPushInfoList.add(info);
					} else {
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("clientId", cid);
						info.put("merchantId", StringUtil.null2Str(tempmap.get("merchantId")));
						info.put("appType", StringUtil.null2Str(tempmap.get("appType")));
						// 安卓
						AndroidPushInfo.add(info);
					}
				}
			}
		}

		data = data + "," + orderStatus;

		try {
			// 推送给商户
			if (AndroidPushInfo != null && AndroidPushInfo.size() > 0) {
				Map<String, Object> pushMap = null;
				pushMap = (Map<String, Object>) commonCacheService.getObject(CacheConstants.PUSH_MAP, Constant.PUSH_CONFIG.APP_TYPE_OMENGP);
				if (pushMap == null) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("appType", Constant.PUSH_CONFIG.APP_TYPE_OMENGP);
					pushMap = androidAppPushConfigDao.getPushConfig(paramMap);
					commonCacheService.setObject(pushMap, CacheConstants.PUSH_MAP, Constant.PUSH_CONFIG.APP_TYPE_OMENGP);
				}

				// 安卓推送
				StringBuilder param = new StringBuilder();
				param.append("AndroidPushInfo=").append(JSONObject.toJSONString(AndroidPushInfo)).append("&transmissionContent=").append(data).append("&pushMap=")
						.append(JSONObject.toJSONString(pushMap)).append("&concatKey=merchantId");

				// logger.error("安卓推送："+param.toString());

				PushAndroidMessageByProxy pushByProxy = new PushAndroidMessageByProxy(Constant.WEB_PROXY_URL + "/doPushToSHB?", param.toString());
				CalloutServices.executor(pushByProxy);
			}
			if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
				// ios推送
				Map<String, Object> msg = PushMessageUtil.getIosMerchantPushMsg(appType, orderId, orderStatus, "0", "0");
				Map<String, Object> cert = dictionaryService.getIosPushCertFromDict(Constant.IOS_MERCHANT_CERT);
				cert.put(Constant.IOS_CERT_TYPE, Constant.IOS_MERCHANT_CERT);
				StringBuilder param = new StringBuilder();
				param.append("iosPushInfoList=").append(JSONObject.toJSONString(iosPushInfoList)).append("&msg=").append(JSONObject.toJSONString(msg)).append("&cert=")
						.append(JSONObject.toJSONString(cert));
				// logger.error("ios推送："+param.toString());
				PushAndroidMessageByProxy pushByProxy = new PushAndroidMessageByProxy(Constant.WEB_PROXY_URL + "/doIosPush?", param.toString());
				CalloutServices.executor(pushByProxy);

			}
			jsonObject = new ResultJSONObject("000", "推送成功");
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("error", "推送失败");
			logger.error("推送失败" + e.getMessage(), e);
			return jsonObject;
		}
		return jsonObject;

	}

	/**
	 * 推送信息给商户 可删除
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject pushMessageToListForSingleSHBByProxy(Map<String, Object> paras) {
		JSONObject jsonObject = null;

		Long orderId = StringUtil.nullToLong(paras.get("orderId"));
		String appType = StringUtil.null2Str(paras.get("appType"));
		String data = StringUtil.null2Str(paras.get("data"));
		// 获取订单状态
		int pushType = this.getOrderPushType(orderId);
		List<Map<String, Object>> resultList = null;
		resultList = this.userOrderDao.getMerchantOneClientId(paras);

		List<Map<String, Object>> AndroidPushInfo = new ArrayList<Map<String, Object>>();// 安卓终端
		// List<String> iosClientIDs = new ArrayList<String>();// 苹果终端
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, Object> tempmap : resultList) {
				String cid = StringUtil.null2Str(tempmap.get("clientId"));
				if (!StringUtil.isNullStr(cid)) {
					if (Constant.PUSH_CLIENT_TYPE_IOS == StringUtil.nullToInteger(tempmap.get("clientType"))) {
						// ios
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("token", cid);
						info.put("merchantId", StringUtil.null2Str(tempmap.get("merchantId")));
						iosPushInfoList.add(info);
					} else {
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("clientId", cid);
						info.put("merchantId", StringUtil.null2Str(tempmap.get("merchantId")));
						info.put("appType", StringUtil.null2Str(tempmap.get("appType")));
						// 安卓
						AndroidPushInfo.add(info);
					}
				}
			}
		}

		data = data + "," + pushType;

		try {
			// 推送给商户
			if (AndroidPushInfo != null && AndroidPushInfo.size() > 0) {
				appType = Constant.PUSH_CONFIG.APP_TYPE_OMENGP;
				Map<String, Object> pushMap = null;
				pushMap = (Map<String, Object>) commonCacheService.getObject(CacheConstants.PUSH_MAP, appType);
				if (pushMap == null) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("appType", appType);
					pushMap = androidAppPushConfigDao.getPushConfig(paramMap);
					commonCacheService.setObject(pushMap, CacheConstants.PUSH_MAP, appType);
				}

				// 安卓推送
				StringBuilder param = new StringBuilder();
				param.append("AndroidPushInfo=").append(JSONObject.toJSONString(AndroidPushInfo)).append("&transmissionContent=").append(data).append("&pushMap=")
						.append(JSONObject.toJSONString(pushMap)).append("&concatKey=merchantId");
				// String param = "AndroidPushInfo=" +
				// JSONObject.toJSONString(AndroidPushInfo) +
				// "&transmissionContent=" + data + "&pushMap=" +
				// JSONObject.toJSONString(pushMap) + "&concatKey=merchantId";
				if (logger.isInfoEnabled()) {
					logger.info(param.toString());
				}
				PushAndroidMessageByProxy pushByProxy = new PushAndroidMessageByProxy(Constant.WEB_PROXY_URL + "/doPushToSHB?", param.toString());
				CalloutServices.executor(pushByProxy);
			}
			if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
				// ios推送
				Map<String, Object> msg = PushMessageUtil.getIosMerchantPushMsg(appType, orderId, pushType, "0", "0");
				Map<String, Object> cert = dictionaryService.getIosPushCertFromDict(Constant.IOS_MERCHANT_CERT);
				cert.put(Constant.IOS_CERT_TYPE, Constant.IOS_MERCHANT_CERT);
				StringBuilder param = new StringBuilder();
				param.append("iosPushInfoList=").append(JSONObject.toJSONString(iosPushInfoList)).append("&msg=").append(JSONObject.toJSONString(msg)).append("&cert=")
						.append(JSONObject.toJSONString(cert));
				// String param = "iosPushInfoList=" +
				// JSONObject.toJSONString(iosPushInfoList) + "&msg=" +
				// JSONObject.toJSONString(msg) + "&cert=" +
				// JSONObject.toJSONString(cert);
				PushAndroidMessageByProxy pushByProxy = new PushAndroidMessageByProxy(Constant.WEB_PROXY_URL + "/doIosPush?", param.toString());
				CalloutServices.executor(pushByProxy);

			}
			jsonObject = new ResultJSONObject("000", "推送成功");
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("error", "推送失败");
			logger.error("推送失败" + e.getMessage(), e);
			return jsonObject;
		}
		return jsonObject;

	}

	/** 推送信息给用户 可删除 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject pushMessageToListForYHBByProxy(Map<String, Object> paras) {
		String appType = StringUtil.null2Str(paras.get("appType"));
		String data = StringUtil.null2Str(paras.get("data"));
		Long orderId = StringUtil.nullToLong(paras.get("orderId"));
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("orderId", orderId);
		// 获取订单状态
		int pushType = this.getOrderPushType(orderId);
		List<Map<String, Object>> resultList = this.userOrderDao.getUserClientIdsByOrderId(paramMap);
		List<Map<String, Object>> AndroidPushInfo = new ArrayList<Map<String, Object>>();// 安卓终端
		// List<String> iosClientIDs = new ArrayList<String>();// 苹果终端
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, Object> tempmap : resultList) {
				String cid = StringUtil.null2Str(tempmap.get("clientId"));
				if (!StringUtil.isNullStr(cid)) {
					if (Constant.PUSH_CLIENT_TYPE_IOS == StringUtil.nullToInteger(tempmap.get("clientType"))) {
						// ios
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("token", cid);
						info.put("userId", StringUtil.null2Str(tempmap.get("userId")));
						iosPushInfoList.add(info);
					} else {
						// 安卓
						Map<String, Object> info = new HashMap<String, Object>();
						info.put("clientId", cid);
						info.put("userId", StringUtil.null2Str(tempmap.get("userId")));
						// 安卓
						AndroidPushInfo.add(info);
					}
				}
			}
		}
		// 2016年1月14日 用户版推送前面追加一个client字段，用于区分是用户版的推送
		data = "client," + data + "," + pushType;
		try {
			// 推送给用户
			if (AndroidPushInfo != null && AndroidPushInfo.size() > 0) {

				Map<String, Object> pushMap = null;
				pushMap = (Map<String, Object>) commonCacheService.getObject(CacheConstants.PUSH_MAP, appType);
				if (pushMap == null) {
					Map<String, Object> paramPushMap = new HashMap<String, Object>();
					paramPushMap.put("appType", appType);
					pushMap = androidAppPushConfigDao.getPushConfig(paramPushMap);
					commonCacheService.setObject(pushMap, CacheConstants.PUSH_MAP, appType);
				}

				// 安卓推送
				StringBuilder param = new StringBuilder();
				param.append("AndroidPushInfo=").append(JSONObject.toJSONString(AndroidPushInfo)).append("&transmissionContent=").append(data).append("&pushMap=")
						.append(JSONObject.toJSONString(pushMap));
				PushAndroidMessageByProxy pushByProxy = new PushAndroidMessageByProxy(Constant.WEB_PROXY_URL + "/doPushToYHB?", param.toString());
				CalloutServices.executor(pushByProxy);
			}
			if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
				// ios推送
				Map<String, Object> msg = PushMessageUtil.getIosUserPushMsg(appType, StringUtil.nullToLong(orderId), 0, "0");
				Map<String, Object> cert = dictionaryService.getIosPushCertFromDict(Constant.IOS_USER_CERT);
				cert.put(Constant.IOS_CERT_TYPE, Constant.IOS_USER_CERT);
				StringBuilder param = new StringBuilder();
				param.append("iosPushInfoList=").append(JSONObject.toJSONString(iosPushInfoList)).append("&msg=").append(JSONObject.toJSONString(msg)).append("&cert=")
						.append(JSONObject.toJSONString(cert));
				// String param = "iosPushInfoList=" +
				// JSONObject.toJSONString(iosPushInfoList) + "&msg=" +
				// JSONObject.toJSONString(msg) + "&cert=" +
				// JSONObject.toJSONString(cert);
				PushAndroidMessageByProxy pushByProxy = new PushAndroidMessageByProxy(Constant.WEB_PROXY_URL + "/doIosPush?", param.toString());
				CalloutServices.executor(pushByProxy);
			}
			jsonObject = new ResultJSONObject("000", "推送成功");
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("error", "推送失败");
			logger.error("推送失败:" + e.getMessage(), e);
			return jsonObject;
		}
		return jsonObject;

	}

	/** 用户保存选择的商品 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject saveOrderGoods(String appType, Long orderId, Long merchantId, Long merchantPlanId, String goodsIds, String goodsNums) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new ResultJSONObject("000", "用户保存选择的商品成功");
			int i = this.saveOrderGoods(orderId, goodsIds, goodsNums);
			if (i == 0) {
				jsonObject = new ResultJSONObject("add_order_goods_failure", "用户保存选择的商品失败");
			}
			this.chooseMerchantPlan(appType, merchantId, merchantPlanId, orderId);
		} catch (Exception ex) {
			logger.error("用户保存选择的商品失败", ex);
			throw new ApplicationException(ex, "add_order_goods_failure", "用户保存选择的商品失败");
		}
		return jsonObject;
	}

	/** 保存用户选择的商品 */
	private int saveOrderGoods(Long orderId, String goodsIds, String goodsNums) {
		String[] goodsIdArray = goodsIds.split(Constant.COMMA_EN);
		String[] goodsNumArray = goodsNums.split(Constant.COMMA_EN);

		if (goodsIdArray.length != goodsNumArray.length) {
			return 0;
		}

		List<Map<String, Object>> goodsOrderList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i <= goodsIdArray.length - 1; i++) {
			Map<String, Object> goodsOrderMap = new HashMap<String, Object>();
			goodsOrderMap.put("orderId", orderId);
			goodsOrderMap.put("goodsId", goodsIdArray[i]);
			goodsOrderMap.put("goodsNum", goodsNumArray[i]);
			goodsOrderList.add(goodsOrderMap);
		}
		int i = this.userOrderDao.insertOrderGoods(goodsOrderList);
		return i;
	}

	@Override
	public JSONObject removeUserCachedOrders(String userId) {
		JSONObject result = new JSONObject();

		if (userRelatedCacheServices.removeUserCachedOrders(userId)) {
			result.put("000", "用户订单缓存成功");
		} else {
			result.put("001", "用户订单清理失败");
		}

		return null;
	}

	// 测试订单列表
	@Override
	public JSONObject testOrderList(Long userId, int pageNo) {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("startNum", 0);
		params.put("pageSize", 300);
		List<Map<String, Object>> resultList = this.userOrderDao.getTopnBasicOrderListInfo(params);
		List<Map<String, Object>> yxtIcons = this.userOrderDao.getAllYxtAppIcon();
		List<String> yxtOrderIds = new ArrayList<String>();
		// 文件路径的补全
		for (Map<String, Object> orderMap : resultList) {
			String appType = StringUtil.null2Str(orderMap.get("appType"));
			String orderId = StringUtil.null2Str(orderMap.get("orderId"));
			if (appType.startsWith("yxt")) {
				yxtOrderIds.add(StringUtil.null2Str(orderId));
			} else if (appType.startsWith("ams")) {
				String[] pathes = orderMap.remove("appIcon").toString().split(",");
				orderMap.put("appIcon", pathes[1]);
			}
		}
		if (yxtOrderIds.size() > 0) {
			List<Map<String, Object>> yxtOrders = this.userOrderDao.getYxtOrderInfo(yxtOrderIds);
			for (Map<String, Object> orderMap : resultList) {
				String orderId = StringUtil.null2Str(orderMap.get("orderId"));
				if (yxtOrderIds.contains(orderId)) {
					for (Map<String, Object> yxtOrder : yxtOrders) {
						if (StringUtil.null2Str(yxtOrder.get("orderId")).equals(orderId)) {
							String icoType = StringUtil.null2Str(yxtOrder.get("icoType"));
							for (Map<String, Object> yxtIcon : yxtIcons) {
								if (StringUtil.null2Str(yxtIcon.get("icoType")).equals(icoType)) {
									String path = StringUtil.null2Str(yxtIcon.get("path"));
									orderMap.put("appIcon", path);
								}
							}
						}
					}
				}
			}
		}
		for (Map<String, Object> orderMap : resultList) {
			BusinessUtil.disposeManyPath(orderMap, "appIcon");
		}
//		System.out.println(resultList);

		jsonObject.put("orderList", resultList);
		return jsonObject;
	}

	@Override
	public JSONObject cancelOrder(Long orderId) {
		try {
			JSONObject jsonObject = null;
			int result = 0;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String orderStatus = "0";
			paramMap.put("orderId", orderId);
			paramMap.put("orderStatus", orderStatus);

			int status = this.orderInfoDao.selectOrderStatus(paramMap);
			if (status > 1) {
				return new ResultJSONObject("order_status_changed", "订单状态已改变，请重新刷新");
			}
			result += this.userOrderDao.updateOrderStatus(paramMap);

			Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
			if (cachedOrders != null) {
				cachedOrders.put("orderStatus", orderStatus);
				userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
			}
			if (result > 0) {
				jsonObject = new ResultJSONObject("000", "取消订单成功");
			} else {
				jsonObject = new ResultJSONObject("009", "取消订单失败");
			}
			return jsonObject;
		} catch (Exception e) {
			logger.error("订单状态更新错误：" + e.getMessage(), e);
			throw new ApplicationException(e, "updateOrderStatus", "更新订单状态错误");
		}
	}

	// 检查订单对应的图标版本是否有更新
	private boolean hasNewImageVersion(Integer lastImageVersion, Integer cachedImageVersion) {
		return lastImageVersion > cachedImageVersion;
	}

	// 测试订单列表
	@Override
	public JSONObject testOrderPush(Map<String, Object> paras) {
		JSONObject jsonObject = null;
		Long orderId = StringUtil.nullToLong(paras.get("orderId"));
		String appType = StringUtil.null2Str(paras.get("appType"));
		String data = StringUtil.null2Str(paras.get("data"));
		String phone = StringUtil.null2Str(paras.get("phone"));
		String province = "";
		String city = "";
		String[] provinceAndCity = null;
		String isTest = "0";// 默认不是测试
		// 获取订单状态
		List<Map<String, Object>> allMerchantList = new ArrayList<Map<String, Object>>();// 所有商户
		List<Map<String, Object>> onlineMerchantList = new ArrayList<Map<String, Object>>();// 在线商户

		Map<String, Object> pushInfo = this.commonService.getPushInfo(appType);
		String pushRange = pushInfo.get("pushRange") == null ? "5" : pushInfo.get("pushRange").toString();
		String pushType = pushInfo.get("pushType") == null ? "0" : pushInfo.get("pushType").toString();
		paras.put("pushRange", pushRange);
		paras.put("pushType", pushType);
		paras.put("isTest", isTest);
		if (pushType.equals("0")) {
			// 按照经纬度查询
			allMerchantList = this.commonService.getAllMerchantByRange(paras);
			if (allMerchantList == null || allMerchantList.size() < 1) {
				logger.error("经纬度没查询到，根据省市查询");
				// 如果按照经纬度查不到，则按照城市查询
				if (paras.get("province") != null && !paras.get("province").toString().equals("") && paras.get("city") != null && !paras.get("city").toString().equals("")) {
					allMerchantList = this.commonService.getAllMerchantByCity(paras);
				}
			}
		} else if (pushType.equals("1")) {
			// 按照城市推查询
			if (StringUtils.equals(appType, "sxd")) {
				Map<String, Object> map = this.orderApiService.getProvinceAndCityByorderId(orderId);
				if (map != null) {
					province = map.get("province") == null ? "" : map.get("province").toString();
					city = map.get("city") == null ? "" : map.get("city").toString();
				}
			}

			provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
			if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
				paras.put("province", provinceAndCity[0]);
				paras.put("city", provinceAndCity[1]);
			}
			allMerchantList = this.commonService.getAllMerchantByCity(paras);
		} else if (pushType.equals("2")) {
			// 标签查询
			allMerchantList = this.commonService.getMerchantClientIdsByServiceTag(paras);
		} else if (pushType.equals("3")) {// 推送给所有
			// 在云端如果选择地址，则按照地址推送，如果没有选择地址则推送所有
			if (appType.equals("zyd")) {
				// 判断是否填写地址信息
				Map<String, Object> map = this.orderApiService.getProvinceAndCityByorderId(orderId);
				if (map != null) {
					province = map.get("province") == null ? "" : map.get("province").toString();
					city = map.get("city") == null ? "" : map.get("city").toString();
				}
			}

			provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
			if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
				// 如果城市不为空，则按照城市推送
				paras.put("province", provinceAndCity[0]);
				paras.put("city", provinceAndCity[1]);
				allMerchantList = this.commonService.getAllMerchantByCity(paras);
			} else {// 如果城市为空，则推送给所有商户
				allMerchantList = this.commonService.getAllMerchant(paras);
			}
		} else {// 其他

		}
		// 如果没有推送对象，则直接返回
		if (allMerchantList == null || allMerchantList.size() == 0) {
			logger.error("推送结果：无推送服务商。appType=" + appType + "，orderId=" + orderId + "，pushType=" + pushType + "，pushRange=" + pushRange);
			return new ResultJSONObject("000", "推送成功");
		}
		// 查询在线商户
		onlineMerchantList = this.commonService.getOnlineMerchant(allMerchantList);
		if (onlineMerchantList == null || onlineMerchantList.size() == 0) {
			logger.error("推送结果：无在线商户。appType=" + appType + "，orderId=" + orderId + "，pushType=" + pushType + "，pushRange=" + pushRange);
			return new ResultJSONObject("000", "推送成功");
		}
		// 将allMerchantList 处理成 onlineMerchantList 的数据格式，以便插入
		allMerchantList =this.HandlerPushMerchants(allMerchantList, onlineMerchantList,"");

		// 去重复
		List<Map<String, Object>> allMerchantListResult = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> s : allMerchantList) {
			if (Collections.frequency(allMerchantListResult, s) < 1)
				allMerchantListResult.add(s);
		}
		allMerchantList = allMerchantListResult;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		paramMap.put("clientIdList", allMerchantList);
		if (allMerchantList != null && allMerchantList.size() > 0) {
		}
		return jsonObject;
	}

	
	private String getIds(List<Map<String, String>> orders){
		 StringBuilder result = new StringBuilder("");
		 for(Map<String,String> order:orders){
			   result.append(",");
			   result.append(order.get("orderId"));
		 }
		 if (result.length()>1){
			 result.deleteCharAt(0);
		 }
		 return result.toString();
	}

	@Override
	/**
	 *  无报价方案的订单，有两种情况需要关闭
	 *  1. 下单时间+设定的天数【目前为7天】> 当前时间
	 *  2. 服务时间 < 当前时间
	 */
	@Transactional(rollbackFor = Exception.class)
	public int handNoBidOrder() {
		System.out.println(DateUtil.getNowYYYYMMDDHHMMSS()+"开始调度处理无报价方案订单");
		int result = 0;
		// 只有获取到订单定时调度锁的进程才进行处理。
		JedisLock schduleLock = userRelatedCacheServices.getOrderNoBidExireLock();
		try {
			if (schduleLock.acquire()) {
				System.out.println("获取到订单定时调度锁");
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("orderStatus", Constant.OVERDUE_ORDER);
				/**
				 *  无报价方案的订单，有两种情况需要关闭20161014
				 *  1.立即服务:下单时间+设定的天数【配置】< 当前时间
				 *  2.预约服务: 预约时间< 当前时间
				 */
				//Integer days = orderInfoDao.getExpiredTime(BusinessAction.NO_BID_ORDER);
				Integer immediatedays = orderInfoDao.getExpiredTime(CacheConstants.EXPIRES_IMMEDIATE_DAY);//立即服务过期时间配置
			    Date immediateCheckTime = new Date(System.currentTimeMillis() - immediatedays * 24 * 3600 * 1000);//days * 24 * 3600 * 1000
				paramMap.put("immediate_check_time", DateUtil.dateToString(immediateCheckTime));
				Date currentTime = new Date(System.currentTimeMillis());
				paramMap.put("curr_time", DateUtil.dateToString(currentTime));

			
					//List<Map<String, String>> orders = orderInfoDao.getNoBidExpiredOrders(paramMap);
				List<Map<String, String>> orders = orderInfoDao.selectNoBidExpiredOrders(paramMap);
					System.out.println("共有："+orders.size()+"个订单需要处理");
					if (orders != null && orders.size() > 0) {
							String ids = getIds(orders);
							
							System.out.println("需要处理的订单ID为："+ids);
							
							Map<String,Object> updateCondition= new HashMap<String,Object>();
							
							updateCondition.put("ids", ids);
							
							updateCondition.put("orderStatus",Constant.OVERDUE_ORDER);
						
							//更新用户侧订单状态
						    int i=orderInfoDao.batchUpdateClientOrderStatus(updateCondition);
//						    if(i>0){
//								System.out.println("更新用户侧订单状态成功");						    	
//						    }
						    // 如果订单在缓存中，则更新缓存中的状态。
							userRelatedCacheServices.cancelOrderInCache(orders);
							
							//更新商户侧订单状态
							//2016.5.27 改为9 
							updateCondition.put("orderStatus",Constant.NO_PLAN_ORDER);
							i=orderInfoDao.batchUpdateMerchantOrderStatus(updateCondition);

//						    if(i>0){
//								System.out.println("更新商户侧订单状态成功");						    	
//						    }
							
							//插入时间轴
							Map<String,Object> actionMap= new HashMap<String,Object>();
							actionMap.put("actionCode",BusinessAction.TIMEOUT_ORDER);
							for(Map<String,String> order:orders) {
									actionMap.put("orderId",order.get("orderId"));
									timeLineDao.insertTimeLine(actionMap);
							}
							
							
							//更新商户侧订单缓存。 2016.6.6
							Map<String,Object> param = new HashMap<String,Object>();
							for(Map<String,String> order:orders) {
								String orderId= order.get("orderId");
								param.put("orderId", orderId);
								List<Map<String,Object>> merchantIds = this.orderInfoDao.getMerchantsForSpeicalOrder(param);
								merchantCacheService.batchUpdateMerchantOrderStatus(orderId, merchantIds, Constant.NO_PLAN_ORDER+"");
							}
							
							
							return  orders.size();			
					}else{
							return 0;
					}
			}else{
//				System.out.println("未获取到订单定时调度锁");
			}
		} catch (InterruptedException e) {
			logger.error("处理无报价方案的过期订单失败：" + e.getMessage(), e);
		} finally {
			userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
		}
		return result;
	}
	//推送商户处理
		public List<Map<String, Object>> HandlerPushMerchants(List<Map<String, Object>> allMerchantList, List<Map<String, Object>> onlineMerchantList,String pushNotLoginDays) {
			
			List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		    String temp="";
		    String nowDate=DateUtil.getNowYYYYMMDD();
		    for (Map<String, Object> allMap : allMerchantList) {
				String merchantId = allMap.get("merchantId") == null ? "" : allMap.get("merchantId").toString();
				//去重复(员工，老板)
				if(temp.equals(merchantId)){
					continue;
				}
				temp=merchantId;
				//去重复数据
				if (Collections.frequency(tempList, allMap) < 1){//没有重复的数据		

					String lastActiveTime=StringUtil.null2Str(allMap.get("lastActiveTime"));
					long days= 1000;
					if(!lastActiveTime.equals("")){
						lastActiveTime=lastActiveTime.split(" ")[0];//取日期
						days=DateUtil.getDaysBetweenDAY1andDAY2(lastActiveTime, nowDate);
					}				
					if(days <= StringUtil.nullToLong(pushNotLoginDays)){//如果最后登录时间超过pushNotLoginDays，则不推送
						tempList.add(allMap);
					}
				}	

				
		    }		
			return tempList;
		}

	@Transactional(rollbackFor = Exception.class)
	public int handlePurifyNoBidExpireOrder() {
				//获取配置的清理间隔时间
				Integer days = orderInfoDao.getExpiredTime(BusinessAction.PURIFY_NOBID_ORDER);
				
				int result = 0;
				// 只有锁的进程才进行处理。
				JedisLock schduleLock = userRelatedCacheServices
						.getPurifyNoBidExireLock();
				Jedis jedis=null;
				try {
					if (schduleLock.acquire()) {
							//获取最近一次被清理的数据的业务时间
							Map<String,Object> lastPurifyRec=purifyDao.getLastPurify(purifyDao.PURIFY_NO_BID);
							Date lastDataPurify=null;
							Long lastMaxOrderId = -1L;
							long now=System.currentTimeMillis();
							if (lastPurifyRec==null){
								lastDataPurify = new Date(now-100*24*3600*1000l);
							}else{
								lastDataPurify = (Date) lastPurifyRec.get("lastPurifyDataTime");
								lastMaxOrderId = (Long) lastPurifyRec.get("maxOrderId");
							}
							Map<String,Object> param = new HashMap<String,Object>();
							param.put("purify_time", lastDataPurify);
							param.put("maxOrderId", lastMaxOrderId);
							param.put("end_PurifyTime", new Date(now - days*24*3600*1000l));
							
							List<Map<String, Object>> orders = orderInfoDao.getPurifyExpireNoBidOrders(param);
							Map<String,Object> orderParam = new HashMap<String,Object>();
							if (orders != null && orders.size() > 0) {
								 result = orders.size();
								 //清理该订单关联的商户订单缓存
								 jedis=JedisPoolUtil.getJedis();
								 String orderId=null;
								 for(Map<String,Object> order:orders){
									 orderId=order.get("orderId").toString();
									 orderParam.put("orderId", orderId);
									 //获取本次待清理的被取消的订单
									 List<Map<String,Object>> pushMerchants=customOrderDao.getMerchantsForSpeicalOrder(orderParam);
									 if (pushMerchants!=null && pushMerchants.size()>0){
											Pipeline pipeLine = jedis.pipelined();
										 	for(Map<String,Object> pushMerchant:pushMerchants){
										 		 	String merchantPushOrderMapKey=CacheConstants.MERCHANT_PUSH_ORDER_PREFIX+pushMerchant.get("merchant_id");
										 		 	String merchantPushOrderListKey=CacheConstants.MERCHANT_PUSH_ORDER_IDS+pushMerchant.get("merchant_id");
										 		 	pipeLine.hdel(merchantPushOrderMapKey, orderId);
										 		 	pipeLine.lrem(merchantPushOrderListKey, 1, orderId);
										 	}
										 	pipeLine.sync();
									 }
									 //清理推送表
									 customOrderDao.removeNoBidOrderPushInfo(orderId);
								 }
								 //插入本次清理日志
								 Map<String,Object> purifyRec=new HashMap<String,Object>(2);
								 purifyRec.put("purifyType", purifyDao.PURIFY_NO_BID);
								 purifyRec.put("maxOrderId", orderId);
								 purifyRec.put("purifyDataTime", orders.get(orders.size()-1).get("actionTime"));
								 purifyDao.insertPurify(purifyRec);
								 
							}
					}else{
						result = -1; 
					}
				} catch (InterruptedException e) {
					logger.error("无报价方案的过期订单--对应的推送记录及商户侧缓存清理失败：" + e.getMessage(), e);
					throw new RuntimeException(e);
				} finally {
					JedisPoolUtil.returnRes(jedis);
					userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
				}
				return result;
	}

	@Transactional(rollbackFor = Exception.class)
	public int handlePurifyNoChoosedOrders() {
		//获取配置的清理间隔时间
		Integer days = orderInfoDao.getExpiredTime(BusinessAction.PURIFY_NOCHOOSED_ORDER);
		
		int result = 0;
		// 只有锁的进程才进行处理。
		JedisLock schduleLock = userRelatedCacheServices
				.getPurifyNoChoosedExireLock();
		Jedis jedis=null;
		try {
			if (schduleLock.acquire()) {
					//获取最近一次被清理的数据的业务时间
					Map<String,Object> lastPurifyRec=purifyDao.getLastPurify(purifyDao.PURIFY_NO_CHOOSED);
					Date lastDataPurify=null;
					Long lastMaxOrderId = -1L;
					long now=System.currentTimeMillis();
					if (lastPurifyRec==null){
						lastDataPurify = new Date(now-100*24*3600*1000l);
					}else{
						lastDataPurify = (Date) lastPurifyRec.get("lastPurifyDataTime");
						lastMaxOrderId = (Long) lastPurifyRec.get("maxOrderId");
					}
					Map<String,Object> param = new HashMap<String,Object>();
					param.put("purify_time", lastDataPurify);
					param.put("maxOrderId", lastMaxOrderId);
					param.put("end_PurifyTime", new Date(now - days*24*3600*1000l));
					List<Map<String, Object>> orders = orderInfoDao.getPurifyExpireNoChoosedOrders(param);
					Map<String,Object> orderParam = new HashMap<String,Object>();
					if (orders != null && orders.size() > 0) {
						 result = orders.size();
						 
						 //清理该订单关联的商户订单缓存
						 jedis=JedisPoolUtil.getJedis();
						 String orderId=null;
						 for(Map<String,Object> order:orders){
							 orderId=order.get("orderId").toString();
							 orderParam.put("orderId", orderId);
							 //获取本次待清理的被取消的订单
							 List<Map<String,Object>> pushMerchants=customOrderDao.getMerchantsForSpeicalOrder(orderParam);
							 
							 //获取提供报价方案的商户
							 String merchantsHasPlan=customOrderDao.getMerchantHasPlan(orderId);
							 Map<String,String> merchantWithPlans=getMap(merchantsHasPlan);
							 
							 if (pushMerchants!=null && pushMerchants.size()>0){
									Pipeline pipeLine = jedis.pipelined();
								 	for(Map<String,Object> pushMerchant:pushMerchants){
								 		    String merchantId=pushMerchant.get("merchant_id").toString();
								 		    if (merchantWithPlans.containsKey(merchantId) ){
								 		    	//已提供报价方案的商户，不清理，跳过。
								 		    	continue;
								 		    }
								 		 	String merchantPushOrderMapKey=CacheConstants.MERCHANT_PUSH_ORDER_PREFIX+merchantId;
								 		 	String merchantPushOrderListKey=CacheConstants.MERCHANT_PUSH_ORDER_IDS+merchantId;
								 		 	pipeLine.hdel(merchantPushOrderMapKey, orderId);
								 		 	pipeLine.lrem(merchantPushOrderListKey, 1, orderId);
								 	}
								 	pipeLine.sync();
							 }
							 if (merchantsHasPlan==null || merchantsHasPlan==""){
								 	//手工触发且无报价方案的过期，会走到这里
								 	customOrderDao.removeNoBidOrderPushInfo(orderId);
							 }else{
								 	//清理未提供报价方案的商户推送表
								 	Map<String,String> queryParam = new HashMap<String,String>();
								 	queryParam.put("orderId", orderId);
								 	queryParam.put("merchantIds", merchantsHasPlan);
								 	customOrderDao.removeOrderPushWithoutPlan(queryParam);
							 }
						 }
						 //插入本次清理日志
						 Map<String,Object> purifyRec=new HashMap<String,Object>(2);
						 purifyRec.put("purifyType", purifyDao.PURIFY_NO_CHOOSED);
						 purifyRec.put("maxOrderId", orderId);
						 purifyRec.put("purifyDataTime", orders.get(orders.size()-1).get("actionTime"));
						 purifyDao.insertPurify(purifyRec);
					}
			}else{
				   result = -1;
			}
		} catch (InterruptedException e) {
			logger.error("未选定方案的过期订单--对应的推送记录及商户侧缓存清理失败：" + e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			JedisPoolUtil.returnRes(jedis);
			userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
		}
		return result;
	}

	

	@Transactional(rollbackFor = Exception.class)
	public int handlePurifyCancelOrder() {
		//获取配置的清理间隔时间
		Integer days = orderInfoDao.getExpiredTime(BusinessAction.PURIFY_CANCEL_ORDER);
		
		int result = 0;
		// 只有锁的进程才进行处理。
		JedisLock schduleLock = userRelatedCacheServices
				.getPurifyCancelOrderLock();
		Jedis jedis=null;
		try {
			if (schduleLock.acquire()) {
					//获取最近一次被清理的数据的业务时间
					Map<String,Object> lastPurifyRec=purifyDao.getLastPurify(purifyDao.PURIFY_CANCEL);
					Date lastDataPurify=null;
					Long lastMaxOrderId = -1L;
					long now = System.currentTimeMillis();
					if (lastPurifyRec==null){
						lastDataPurify = new Date(now-100*24*3600*1000l);
					}else{
						lastDataPurify = (Date) lastPurifyRec.get("lastPurifyDataTime");
						lastMaxOrderId = (Long) lastPurifyRec.get("maxOrderId");
					}
					Map<String,Object> param = new HashMap<String,Object>();
					param.put("purify_time", lastDataPurify);
					param.put("maxOrderId", lastMaxOrderId);
					param.put("end_PurifyTime", new Date(now - days*24*3600*1000l));
					List<Map<String, Object>> orders = orderInfoDao.getPurifyCancelOrders(param);
					Map<String,Object> orderParam = new HashMap<String,Object>();
					if (orders != null && orders.size() > 0) {
						 result = orders.size();
						 
						 //清理该订单关联的商户订单缓存
						 jedis=JedisPoolUtil.getJedis();
						 String orderId=null;
						 for(Map<String,Object> order:orders){
							 orderId=order.get("orderId").toString();
							 orderParam.put("orderId", orderId);
							 //获取本次待清理的被取消的订单
							 List<Map<String,Object>> pushMerchants=customOrderDao.getMerchantsForSpeicalOrder(orderParam);
							 
							 //获取提供报价方案的商户20161017
							 String merchantsHasPlan=customOrderDao.getMerchantHasPlan(orderId);
							 Map<String,String> merchantWithPlans=getMap(merchantsHasPlan);
							 
							 if (pushMerchants!=null && pushMerchants.size()>0){
									Pipeline pipeLine = jedis.pipelined();
								 	for(Map<String,Object> pushMerchant:pushMerchants){
								 		//20161017,去除有报价方案的商户
								 		 String merchantId=pushMerchant.get("merchant_id").toString();
								 		    if (merchantWithPlans.containsKey(merchantId) ){
								 		    	//已提供报价方案的商户，不清理，跳过。
								 		    	continue;
								 		    }
								 		 	String merchantPushOrderMapKey=CacheConstants.MERCHANT_PUSH_ORDER_PREFIX+pushMerchant.get("merchant_id");
								 		 	String merchantPushOrderListKey=CacheConstants.MERCHANT_PUSH_ORDER_IDS+pushMerchant.get("merchant_id");
								 		 	pipeLine.hdel(merchantPushOrderMapKey, orderId);
								 		 	pipeLine.lrem(merchantPushOrderListKey, 1, orderId);
								 	}
								 	pipeLine.sync();
							 }
							//20161017,去除有报价方案的商户
							 if (merchantsHasPlan==null || merchantsHasPlan==""){
								 //清理推送表
								 customOrderDao.removeCanceldOrderPushInfo(orderId);
							 }else{
								 	//清理未提供报价方案的商户推送表
								 	Map<String,String> queryParam = new HashMap<String,String>();
								 	queryParam.put("orderId", orderId);
								 	queryParam.put("merchantIds", merchantsHasPlan);
								 	customOrderDao.removeOrderPushWithoutPlan(queryParam);
							 }
							
						 }
						 //插入本次清理日志
						 Map<String,Object> purifyRec=new HashMap<String,Object>(2);
						 purifyRec.put("purifyType", purifyDao.PURIFY_CANCEL);
						 purifyRec.put("purifyDataTime", orders.get(orders.size()-1).get("actionTime"));
						 purifyRec.put("maxOrderId", orderId);
						 purifyDao.insertPurify(purifyRec);
						 
					}
			}else{
				result = -1;
			}
		} catch (InterruptedException e) {
			logger.error("用户取消订单--对应的推送记录及商户侧缓存清理失败：" + e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			JedisPoolUtil.returnRes(jedis);
			userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
		}
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	public int handlePurifyInProcessOrder() {
		//获取配置的清理间隔时间
				Integer days = orderInfoDao.getExpiredTime(BusinessAction.PURIFY_INPROCESS_ORDER);
				
				int result = 0;
				// 只有锁的进程才进行处理。
				JedisLock schduleLock = userRelatedCacheServices
						.getPurifyInProcessOrderLock();
				Jedis jedis=null;
				try {
					if (schduleLock.acquire()) {
							//获取最近一次被清理的数据的业务时间
							Map<String,Object> lastPurifyRec=purifyDao.getLastPurify(purifyDao.PURIFY_IN_PROCESS);
							Date lastDataPurify=null;
							Long lastMaxOrderId = -1L;
							long now = System.currentTimeMillis();
							if (lastPurifyRec==null){
								lastDataPurify = new Date(now-100*24*3600*1000l);
							}else{
								lastDataPurify = (Date) lastPurifyRec.get("lastPurifyDataTime");
								lastMaxOrderId = (Long) lastPurifyRec.get("maxOrderId");
							}
							Map<String,Object> param = new HashMap<String,Object>();
							param.put("purify_time", lastDataPurify);
							param.put("maxOrderId", lastMaxOrderId);
							param.put("end_PurifyTime", new Date(now - days*24*3600*1000l));
							List<Map<String, Object>> orders = orderInfoDao.getPurifyInProcessOrders(param);
							Map<String,Object> orderParam = new HashMap<String,Object>();
							if (orders != null && orders.size() > 0) {
								 result = orders.size();
								 
								 //清理该订单关联的商户订单缓存
								 jedis=JedisPoolUtil.getJedis();
								 String orderId=null;
								 for(Map<String,Object> order:orders){
									 orderId=order.get("orderId").toString();
									 orderParam.put("orderId", orderId);
									 //获取本次待清理的被取消的订单
									 List<Map<String,Object>> pushMerchants=customOrderDao.getMerchantsForSpeicalOrder(orderParam);
									 
									 //获取提供报价方案的商户
									 String merchantsHasPlan=customOrderDao.getMerchantHasPlan(orderId);
									 Map<String,String> merchantWithPlans=getMap(merchantsHasPlan);
									 
									 if (pushMerchants!=null && pushMerchants.size()>0){
											Pipeline pipeLine = jedis.pipelined();
										 	for(Map<String,Object> pushMerchant:pushMerchants){
										 		    String merchantId=pushMerchant.get("merchant_id").toString();
										 		    if (merchantWithPlans.containsKey(merchantId) ){
										 		    	//已提供报价方案的商户，不清理，跳过。
										 		    	continue;
										 		    }
										 		 	String merchantPushOrderMapKey=CacheConstants.MERCHANT_PUSH_ORDER_PREFIX+merchantId;
										 		 	String merchantPushOrderListKey=CacheConstants.MERCHANT_PUSH_ORDER_IDS+merchantId;
										 		 	pipeLine.hdel(merchantPushOrderMapKey, orderId);
										 		 	pipeLine.lrem(merchantPushOrderListKey, 1, orderId);
										 	}
										 	pipeLine.sync();
									 }
									 if (merchantsHasPlan==null || merchantsHasPlan==""){
										 	//应该走不到这里
										 	System.out.println("进行中或已完成的订单-未找到提供报价方案的商户："+orderId);
									 }else{
										 	//清理未提供报价方案的商户推送表
										 	Map<String,String> queryParam = new HashMap<String,String>();
										 	queryParam.put("orderId", orderId);
										 	queryParam.put("merchantIds", merchantsHasPlan);
										 	customOrderDao.removeOrderPushWithoutPlan(queryParam);
									 }
								 }
								 //插入本次清理日志
								 Map<String,Object> purifyRec=new HashMap<String,Object>(2);
								 purifyRec.put("purifyType", purifyDao.PURIFY_IN_PROCESS);
								 purifyRec.put("maxOrderId", orderId);
								 purifyRec.put("purifyDataTime", orders.get(orders.size()-1).get("actionTime"));
								 purifyDao.insertPurify(purifyRec);
							}
					}else{
						result = -1;
					}
				} catch (InterruptedException e) {
					logger.error("进行中或已完成的订单--未提供报价方案的商户对应的推送记录及缓存清理失败：" + e.getMessage(), e);
					throw new RuntimeException(e);
				} finally {
					JedisPoolUtil.returnRes(jedis);
					userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
				}
				return result;
	}

		
	
	//根据，分隔的商户ID，生成Map
	private Map<String, String> getMap(String merchantsHasPlan) {
		Map<String,String> result = new HashMap<String,String>();
		if (merchantsHasPlan!=null && merchantsHasPlan.length()>0){
			 String[] merchants=merchantsHasPlan.split(",");
			 for(String merchant:merchants){
				 	result.put(merchant, merchant);
			 }
		}
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	public int handleReturnBidFee() {
		//获取配置的
		Integer days = orderInfoDao.getExpiredTime(BusinessAction.REETURN_BID_FEE);
		int result = 0;
		// 只有锁的进程才进行处理。
		JedisLock schduleLock = userRelatedCacheServices
				.getReturnBidFeeLock();
		Jedis jedis=null;
		try {
			if (schduleLock.acquire()) {
					//获取最近一次返回抢单费数据的业务时间
	//				Map<String,Object> lastScheduleRec=scheduleDao.getLastScheduleRec(scheduleDao.RETURN_BIDFEE_OVERTIME);
	//				Date lastDataTime=null;
	//				Long lastMaxOrderId = -1L;
	//				long now = System.currentTimeMillis();
	//				if (lastScheduleRec==null){
	//					lastDataTime = new Date(now-100*24*3600*1000l);
	//				}else{
	//					lastDataTime = (Date) lastScheduleRec.get("lastBusinessDataTime");
	//					lastMaxOrderId = (Long) lastScheduleRec.get("maxOrderId");
	//				}
	//				Map<String,Object> param = new HashMap<String,Object>();
	//				param.put("process_time", lastDataTime);
	//				param.put("maxOrderId", lastMaxOrderId);
	//				param.put("end_ProcessTime", new Date(now - days*24*3600*1000l));
				//20161019 catalog 区分取消报价方案CANCEL和报价未选择逾期 NOCHOOSED
					String nochooseOrder=scheduleDao.PLAN_ORDER_NOCHOOSED_EXPIRES;
					String cancelOrder=scheduleDao.PLAN_ORDER_NOCHOOSED_CANCEL;
				    Map<String,Object> param = new HashMap<String,Object>();
					param.put("businessType", scheduleDao.RETURN_BIDFEE_OVERTIME);
					param.put("catalog", nochooseOrder);
					Map<String,Object> noChooseLastScheduleRec=scheduleDao.selectLastScheduleRec(param);
					param.put("catalog",cancelOrder);
					Map<String,Object> cancelLastScheduleRec=scheduleDao.selectLastScheduleRec(param);
				    Date noChooseLastDataTime=null;
					Long noChooseLastMaxOrderId = -1L;
					Date cancelChooseLastDataTime=null;
					Long cancelLastMaxOrderId = -1L;
					long now = System.currentTimeMillis();
					if (noChooseLastScheduleRec==null){
						noChooseLastDataTime = new Date(now-100*24*3600*1000l);
					}else{
						noChooseLastDataTime = (Date) noChooseLastScheduleRec.get("lastBusinessDataTime");
						noChooseLastMaxOrderId = (Long) noChooseLastScheduleRec.get("maxOrderId");
					}
					if (cancelLastScheduleRec==null){
						cancelChooseLastDataTime = new Date(now-100*24*3600*1000l);
					}else{
						cancelChooseLastDataTime = (Date) noChooseLastScheduleRec.get("lastBusinessDataTime");
						cancelLastMaxOrderId = (Long) noChooseLastScheduleRec.get("maxOrderId");
					}
					Map<String,Object> mparam = new HashMap<String,Object>();
					mparam.put("nprocess_time", noChooseLastDataTime);
					mparam.put("nmaxOrderId", noChooseLastMaxOrderId);
					mparam.put("nend_ProcessTime", new Date(now - days*24*3600*1000l));
					mparam.put("cprocess_time", cancelChooseLastDataTime);
					mparam.put("cmaxOrderId", cancelLastMaxOrderId);
					mparam.put("cend_ProcessTime", new Date(now - days*24*3600*1000l));
					List<Map<String, Object>> orders = orderInfoDao.selectNeedReturnBidFeeOrders(mparam);
					List<Map<String,Object>> returnFeeList = new ArrayList<Map<String,Object>>(); 
					Map<String,Object> orderParam = new HashMap<String,Object>();
					
					//20161019报价未选择逾期方案最大订单id，actionTime
					Map<String,Object> nparam = new HashMap<String,Object>();
					//20161019报价取消方案最大订单id，actionTime
					Map<String,Object> cparam = new HashMap<String,Object>();
					
					
					if (orders != null && orders.size() > 0) {
						 result = orders.size();
						 
						 Long orderId=null;
						 for(Map<String,Object> order:orders){
							 orderId=(Long) order.get("orderId");
							 orderParam.put("orderId", orderId);
							 //20101019两个if 
							 if(nochooseOrder.equals(order.get("catalog"))&&StringUtil.isNotEmpty(order.get("actionTime"))){
								 nparam.put("businessType", scheduleDao.RETURN_BIDFEE_OVERTIME);
								 nparam.put("maxOrderId", orderId);
								 nparam.put("businessDataTime", order.get("actionTime"));
								 nparam.put("catalog", nochooseOrder);
							 }
							 if(cancelOrder.equals(order.get("catalog"))&&StringUtil.isNotEmpty(order.get("actionTime"))){
								 cparam.put("businessType", scheduleDao.RETURN_BIDFEE_OVERTIME);
								 cparam.put("maxOrderId", orderId);
								 cparam.put("businessDataTime", order.get("actionTime"));
								 cparam.put("catalog", cancelOrder);
							 }
							 //获取订单的内容
							 Map<String,Object> orderInfo =orderInfoDao.selectOrderGeneral(orderParam);
							 
							 if (orderInfo==null){
								 //测试环境有此数据
								 continue;
								 
							 }
							 
							 //获取提供报价方案的商户
							 List<Long> merchants=merchantPlanDao.getMerchantsByOrderId(orderId);
							
							 StringBuffer merchantIds= new StringBuffer("");
							 
							 if (merchants.size()>0){
								 for (Long merchantId:merchants){
									  Map<String,Object> item= new HashMap<String,Object>();
									  item.put("merchantId",merchantId);
									  item.put("orderId",orderId);
									  item.put("payType",5);
									  item.put("payMoney",1);
									  item.put("payTime",new Date(now));
									  item.put("serviceTypeId",orderInfo.get("serviceType"));
									  returnFeeList.add(item);
									  merchantIds.append(",").append(merchantId.toString());
								 }				 
								 //插入退抢单金的记录
								 customOrderDao.batchAddReturnBidFee(returnFeeList);
								 
								 //更新商户的总金额
								 Map<String,Object> params=new HashMap<String,Object>();
								 params.put("money", 1);
								 params.put("merchantIds", merchantIds.delete(0, 1).toString());
								 customOrderDao.updateMerchantStaticsByIds(params);
								 
								 returnFeeList =  new ArrayList<Map<String,Object>>(); 
						      } 
						 }
						 
						 //插入本次处理日志
//						 Map<String,Object> scheduleRec=new HashMap<String,Object>(2);
//						 scheduleRec.put("businessType", scheduleDao.RETURN_BIDFEE_OVERTIME);
//						 scheduleRec.put("maxOrderId", orderId);
//						 scheduleRec.put("businessDataTime", orders.get(orders.size()-1).get("actionTime"));
						 //20161019 
						 if(StringUtil.isNotEmpty(nparam.get("businessDataTime"))){
						    scheduleDao.insertScheduleRec(nparam);
						 }
						 if(StringUtil.isNotEmpty(cparam.get("businessDataTime"))){
							 scheduleDao.insertScheduleRec(cparam);
						}
					}
			}
		} catch (InterruptedException e) {
			logger.error("过期的商户报价--返回抢单金失败：" + e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
		}
		return result;
	}
}
