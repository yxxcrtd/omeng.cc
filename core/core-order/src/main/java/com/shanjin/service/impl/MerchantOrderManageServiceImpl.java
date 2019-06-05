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
import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.MerchantThreadServices;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IMerchantOrderAbandonDao;
import com.shanjin.dao.IMerchantPlanAttachmentDao;
import com.shanjin.dao.IMerchantPlanDao;
import com.shanjin.dao.IMerchantPlanDetailDao;
import com.shanjin.dao.IOrderInfoDao;
import com.shanjin.dao.IPushDao;
import com.shanjin.exception.ApplicationException;
import com.shanjin.model.RuleConfig;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IMerchantOrderManageService;
import com.shanjin.service.IMyMerchantService;
import com.shanjin.service.IOrderInfoService;
import com.shanjin.service.IncService;
import com.shanjin.service.api.IOrderApiService;
import com.shanjin.thread.UpdateMerchantPlan;

@Service("merchantOrderManageService")
public class MerchantOrderManageServiceImpl implements IMerchantOrderManageService {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(MerchantOrderManageServiceImpl.class);
	@Resource
	private IOrderInfoDao iOrderInfoDao;

	@Resource
	private IMerchantPlanDao iMerchantPlanDao;
	

	@Resource
	private IMerchantOrderAbandonDao iMerchantOrderAbandonDao;
	@Resource
	private IMerchantPlanAttachmentDao iMerchantPlanAttachmentDao;
	
	
	@Resource
	private IMerchantPlanDetailDao  iMerchantPlanDetailDao;

	@Resource
	private IOrderApiService orderApiService;

	@Resource
	private IOrderInfoService orderInfoService;

	@Resource
	private IUserRelatedCacheServices userRelatedCacheServices;

	@Resource
	private MerchantPayService merchantPayService;
	@Resource
	private ICommonService commonService;
	@Resource
	private IMyMerchantService myMerchantService;
	
	@Resource
	private IncService    incService;
	
	@Resource
	private IPushDao pushDao;

	/** 测试用接口-认证申请通过 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject testAuthPass(Long merchantId) throws Exception {
		iOrderInfoDao.testAuthPass(merchantId);
		iOrderInfoDao.testUpdateMerchant(merchantId);
		return new ResultJSONObject("000", "认证申请通过！");
	}

	/** 商户抢单前的校验 */
	@Override
	public JSONObject provideSchemeVerify(String appType, Long merchantId, Long orderId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		// 判断是否是认证商户
		String authType= myMerchantService.checkIsNotAuth(merchantId);
		if("0".equals(authType)){
			return new ResultJSONObject("not_anth_merchant", "你不是认证商户，无法抢单，请申请成为认证商户");
		}
		paramMap.put("orderId", orderId);
		// 判断是否重复抢单
		if (this.iMerchantPlanDao.selectPlanNum(paramMap) > 0) {
			return new ResultJSONObject("acquire_order_repetition", "你已成功抢单，请勿重复抢单");
		}
		// 判断订单状态
		int orderStatus = this.iOrderInfoDao.selectOrderStatus(paramMap);
		if (orderStatus == 0) {
			return new ResultJSONObject("user_cancel_order", "用户已取消订单，抢单无效");
		}
		if (orderStatus != 1 && orderStatus != 2) {
			return new ResultJSONObject("order_status_changed", "订单状态已改变，抢单失败，请重新刷新");
		}
		// 判断余额
		paramMap.put("appType", appType);
		BigDecimal orderSurplusMoney = merchantPayService.getOrderSurplusMoney(paramMap);
		BigDecimal orderPrice = merchantPayService.getOrderPrice(paramMap);
        if (orderPrice.compareTo(BigDecimal.ZERO)!=0 && (orderSurplusMoney.compareTo(BigDecimal.ZERO) ==0 || orderSurplusMoney.compareTo(orderPrice)==-1)) {
			return new ResultJSONObject("acquire_order_failure", "你的抢单余额不足，请及时充值");
		}
		// 判断是否超过抢单最大数，查询订单方案数量
		int grabOrderNum = iMerchantPlanDao.getOrderPlanCount(orderId);
		int maxGrabOrderNum = -1;
		Map<String, Object> maxGrabOrderNumMap = commonService.getConfigurationInfoByKey("max_grabOrder_num");
		if (maxGrabOrderNumMap != null) {
			Object maxGrabOrderNumObj = maxGrabOrderNumMap.get("config_value");
			maxGrabOrderNum = Integer.parseInt(maxGrabOrderNumObj == null ? "0" : maxGrabOrderNumObj.toString());
		}
		if (grabOrderNum > maxGrabOrderNum) {
			return new ResultJSONObject("order_status_failure", "该订单报价的服务商已达上限，不能再进行抢单！");
		}
		return new ResultJSONObject("000", "校验通过");
	}

	/** 商户抢单后改变订单状态 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject immediately(String appType, String phone, Long merchantId, Long orderId, BigDecimal planPrice, BigDecimal discountPrice, Long planType, String detail, List<String> voicePaths,
			List<String> picturePaths) {
		JSONObject jsonObject = null;
		try {
			// 处理$
			detail = StringUtil.formatDollarSign(detail);

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			paramMap.put("merchantId", merchantId);
			// 查询商户提供的方案数量
			int checkImmediately = this.iOrderInfoDao.checkImmediatelyOrder(paramMap);
			if (checkImmediately > 1) {// 商户未提供方案			
				return new ResultJSONObject("002", "该商户已经抢过这个订单");			
			}
			//查询订单状态
			int orderStatus = this.iOrderInfoDao.selectOrderStatus(paramMap);
			if (orderStatus != 1 && orderStatus != 2) {
				return new ResultJSONObject("order_status_changed", "订单状态已改变，抢单失败，请重新刷新");
			}
			// 订单状态为1的场合，将订单状态更新成2
			if (orderStatus == 1) {
				paramMap = new HashMap<String, Object>();
				paramMap.put("orderId", orderId);
				paramMap.put("orderStatus", 2);
				// 更新订单的状态
				this.iOrderInfoDao.updateOrderStatus(paramMap);
			}
			paramMap = new HashMap<String, Object>();
			paramMap.put("merchantId", merchantId);
//			paramMap.put("appType", appType);
			// 更新商户总抢单次数
			this.iMerchantPlanDao.updateGrabFrequency(paramMap);

			// 订单ID
			paramMap.put("orderId", orderId);
			// (商户)登陆的手机号码
			paramMap.put("phone", phone);
			// 方案价格
			paramMap.put("planPrice", planPrice);
			// 优惠价
			paramMap.put("discountPrice", discountPrice);
			// 方案内容
			paramMap.put("content", detail);
			// 方案类型
			paramMap.put("planType", planType);

			// 查询employeeId，此处的employeeId是userId
			Object employeeId = iMerchantPlanDao.getEmployeeIdByPhone(paramMap);
			paramMap.put("employeeId", employeeId);
			// 保存商户方案
			this.iMerchantPlanDao.insertMerchantPlan(paramMap);

			Long merchantPlanId = (Long) paramMap.get("merchantPlanId");

			List<Map<String, Object>> merchantPlanAttachmentList = new ArrayList<Map<String, Object>>();

			if (voicePaths.size() > 0) {
				for (String path : voicePaths) {
					Map<String, Object> merchantPlanAttachmentMap = new HashMap<String, Object>();
					merchantPlanAttachmentMap.put("merchantPlanId", merchantPlanId);
					merchantPlanAttachmentMap.put("attachmentType", 2);
					merchantPlanAttachmentMap.put("attachmentUse", 21);
					merchantPlanAttachmentMap.put("path", path);
					merchantPlanAttachmentList.add(merchantPlanAttachmentMap);
				}
			}

			if (picturePaths.size() > 0) {
				for (String path : picturePaths) {
					Map<String, Object> merchantPlanAttachmentMap = new HashMap<String, Object>();
					merchantPlanAttachmentMap.put("merchantPlanId", merchantPlanId);
					merchantPlanAttachmentMap.put("attachmentType", 1);
					merchantPlanAttachmentMap.put("attachmentUse", 11);
					merchantPlanAttachmentMap.put("path", path);
					merchantPlanAttachmentList.add(merchantPlanAttachmentMap);
				}
			}

			if (!merchantPlanAttachmentList.isEmpty()) {
				this.iMerchantPlanAttachmentDao.insertMerchantPlanAttachment(merchantPlanAttachmentList);
			}

			paramMap = new HashMap<String, Object>();
			paramMap.put("merchantId", merchantId);
			paramMap.put("appType", appType);
			paramMap.put("orderId", orderId);
			// 订单金额
			BigDecimal orderPrice = merchantPayService.getOrderPrice(paramMap);
			if (orderPrice.compareTo(BigDecimal.ZERO)==1) {// > 0
				// 抢单成功,订单余额中扣除抢单费
				Map<String, Object> payMap = new HashMap<String, Object>();
				payMap.put("appType", appType);
				payMap.put("merchantId", merchantId);
				payMap.put("money", orderPrice);
				merchantPayService.deductOrderMoney(payMap);

				// 生成一条扣费记录
				payMap = new HashMap<String, Object>();
				payMap.put("appType", appType);
				payMap.put("merchantId", merchantId);
				payMap.put("orderId", orderId);
				payMap.put("payType", 2);
				payMap.put("payMoney", "-"+orderPrice);
				merchantPayService.addMerchantOrderPaymentDetails(payMap);
			}

			jsonObject = new ResultJSONObject("000", "抢单成功");

			// 如果订单缓存存在，则同步更新用户-订单 缓存的状态。
			Map<String, Object> cachedOrder = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
			if (cachedOrder != null) {
				cachedOrder.put("orderStatus", 2);
				userRelatedCacheServices.cacheUserOrderWithJson(cachedOrder.get("userId").toString(), cachedOrder);
			}

			// 增加返回商户的信息完成度，魅力值
			Map<String, Object> merchantInfo = this.myMerchantService.checkMerchantInfo(merchantId);
			jsonObject.put("merchantInfo", merchantInfo);

			// 开启异步处理，更新方案的距离、评论数、好评率
			Map<String, Object> orderLocationMap = this.pushDao.selectOrderLocation(orderId);
			Map<String, Object> merchantLocationMap = this.myMerchantService.getLocationInfo(merchantId);
			UpdateMerchantPlan updateMerchantPlan = new UpdateMerchantPlan(merchantId, orderId,  orderLocationMap, merchantLocationMap, iMerchantPlanDao);
			MerchantThreadServices.executor(updateMerchantPlan);
		
		} catch (Exception ex) {
			logger.error("抢单失败", ex);
			throw new ApplicationException(ex, "acquire_order_failure", "抢单失败");
		}
		return jsonObject;
	}

	/** 屏蔽订单 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject shieldOrder(Long currentMerchantId, Long orderId) {
		JSONObject jsonObject = null;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			Map<String, Object> orderMap = this.iOrderInfoDao.selectStatusAndMerchantId(paramMap);
			int orderStatus = StringUtil.nullToInteger(orderMap.get("orderStatus"));
			Long merchantId = 0L;
			if (orderMap.get("merchantId") != null) {
				merchantId = StringUtil.nullToLong(orderMap.get("merchantId"));
			}
			if (orderStatus == 0 || orderStatus == 5 || orderStatus == 6 || (merchantId != 0 && (currentMerchantId != merchantId))) {
				paramMap.put("merchantId", currentMerchantId);
				this.iMerchantOrderAbandonDao.insertMerchantOrderAbandon(paramMap);
				jsonObject = new ResultJSONObject("000", "订单删除成功");
			} else {
				jsonObject = new ResultJSONObject("order_delete_failure", "订单删除失败");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("订单删除失败", ex);
			throw new ApplicationException(ex, "order_delete_failure", "订单删除失败");
		}
		return jsonObject;
	}

	/** 订单基础信息列表查询 */
	@Override
	public JSONObject selectBasicOrderList(Long catalogId, Long userId,Long merchantId, Long serviceType, int pageNo) throws Exception {
		JSONObject jsonObject = null;
		jsonObject = new ResultJSONObject("000", "订单基础信息列表加载成功");
		Map<String, Object> paramMap = new HashMap<String, Object>();

		// 分类ID
		paramMap.put("catalogId", catalogId);
		// 用户ID
		paramMap.put("userId", userId);
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 订单类型
		paramMap.put("serviceType", serviceType);
		// 应用类型
//		paramMap.put("appType", appType);
//		if (appType.startsWith("yxt")) {
//			paramMap.put("appTypeFlg", "yxt");
//		} else {
//			paramMap.put("appTypeFlg", Constant.EMPTY);
//		}
		paramMap.put("catalogIds", 0);
		int orderCount = this.iOrderInfoDao.selectBasicOrderCount(paramMap);
		if (orderCount == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("orderList", new ArrayList<HashMap<String, String>>());
		} else {
			// 查询起始记录行号
			paramMap.put("rows", pageNo * Constant.PAGESIZE);
			// 每页显示的记录数
			paramMap.put("pageSize", Constant.PAGESIZE);
			List<Map<String, Object>> orderList = this.iOrderInfoDao.selectBasicOrder(paramMap);
			for (Map<String, Object> orderMap : orderList) {
				// 订单状态对于不同商户的区分显示编辑
				orderStatusForMerchantShow(orderMap, merchantId);
				orderMap.remove("merchantId");

//				if (appType.startsWith("ams")) {
//					// 对于艾秘书，需要单独处理下订单的图标
//					String[] icons = orderMap.get("serviceTypePath").toString().split(",");
//					orderMap.put("serviceTypePath", icons[1]);
//				}
//				//格式化服务时间  -----2016.1.4
//				String serviceType_=orderMap.get("serviceType")==null?"":orderMap.get("serviceType").toString();
//				String serviceTime="";
//				if ("".equals(orderMap.get("serviceTime")) || orderMap.get("serviceTime")==null){
////					logger.warn("服务时间未初始值，订单号"+ orderMap.get("orderId"));
//				}else{
//					serviceTime=BusinessUtil.formatServiceTime((java.util.Date)orderMap.get("serviceTime"),appType,serviceType_);
//				}	
//				orderMap.put("serviceTime", serviceTime);
				// 文件路径的补全
				BusinessUtil.disposePath(orderMap, "serviceTypePath");
				BusinessUtil.handlerOrderServiceTime(orderMap);
			}

			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(orderCount));
			jsonObject.put("orderList", orderList);
		}
		return jsonObject;
	}

	/** 订单详情查询 */
	@Override
	public JSONObject selectDetailOrderInfo(String appType, Long merchantId, Long orderId, Long serviceType) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 订单ID
		paramMap.put("orderId", orderId);
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);
		if (appType.startsWith("yxt")) {
			paramMap.put("appTypeFlg", "yxt");
		} else {
			paramMap.put("appTypeFlg", Constant.EMPTY);
		}
		
		Map<String, Object> orderInfo = this.iOrderInfoDao.selectOrderGeneral(paramMap);
		if(StringUtil.isEmpty(serviceType)){//ios推送订单，点击推送通知，跳转到订单详情，没有传serviceType参数，需要后台获取
			serviceType=StringUtil.nullToLong(orderInfo.get("serviceType"));
		}
		paramMap.put("serviceType", serviceType);
		
		String orderStatus = orderInfo.get("orderStatus")==null?"":orderInfo.get("orderStatus").toString();
		paramMap.put("orderStatus", orderStatus);		
		Map<String, Object> orderNote = this.orderInfoService.getOrderNote(paramMap);
		Map<String, Object> orderText = this.orderApiService.selectOrderText(paramMap);
		Map<String, Object> orderUserInfo = this.iOrderInfoDao.selectOrderUserInfo(paramMap);

		if (orderText == null || orderText.size() <= 0) {
			return new ResultJSONObject("detial_order_failure", "订单详情加载失败");
		}

		String demand = (String) orderText.remove("demand");
		if (demand != null) {
			demand = demand.trim();
			if (!demand.equals(""))
				orderNote.put("demand", "补充说明：" + demand);
		}

		// 订单状态对于不同商户的区分显示编辑
		orderStatusForMerchantShow(orderInfo, merchantId);
		// 文件路径的补全
		BusinessUtil.disposePath(orderInfo, "serviceTypePath");
		BusinessUtil.disposePath(orderUserInfo, "userPortrait");
		BusinessUtil.disposeManyPath(orderNote, "voicePath", "picturePath");

		
		jsonObject = new ResultJSONObject("000", "订单详情加载成功");
		// 如果订单状态不是待提供，则不需要查余额
		if (orderStatus.equals("1") || orderStatus.equals("2")) {// 待提供状态，即可以抢单
			// 订单余额
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("merchantId", merchantId);
			map.put("appType", appType);
			BigDecimal orderSurplusMoney = merchantPayService.getOrderSurplusMoney(map);
			BigDecimal orderPrice = merchantPayService.getOrderPrice(map);
			jsonObject.put("orderSurplusPrice", orderSurplusMoney + "");// 订单余额
			jsonObject.put("orderPrice", orderPrice + "");// 抢单费用
		}

		// 抢单数量
		// orderCount=this.iOrderInfoDao.selectGrabFrequency(map).get("grabFrequency");

		// 将值为空的移除
		BusinessUtil.removeNullValueFromMap(orderText);

		orderInfo.remove("merchantId");
		jsonObject.put("orderInfo", orderInfo);
		jsonObject.put("orderText", orderText.values().toArray());
		jsonObject.put("orderNote", orderNote);
		jsonObject.put("orderUserInfo", orderUserInfo);
		jsonObject.put("orderGoodsInfo", orderGoodsInfoEdit(appType, orderId, false));
		// jsonObject.put("grabFrequency",orderCount+"");//抢单数量

		// 判断是否是认证商户		
		jsonObject.put("auth", myMerchantService.checkIsNotAuth(merchantId));
		
		/*JSONObject authInfo = myMerchantService.selectApplyAuthInfo(appType, merchantId);
		if (authInfo.containsKey("authInfo") && authInfo.get("authInfo") != null) {
			Map<String, Object> authMap = (Map<String, Object>) authInfo.get("authInfo");
			String authType = authMap.get("authType") == null ? "0" : authMap.get("authType").toString();
			String authStatus = authMap.get("authStatus") == null ? "0" : authMap.get("authStatus").toString();
			if (!authStatus.equals("1")) {
				jsonObject.put("auth", 0);
			} else {
				jsonObject.put("auth", authType);
			}
		} else {
			jsonObject.put("auth", 0);
		}*/
	
		String serviceTime=orderText.get("serviceTime")==null?"":orderText.get("serviceTime").toString();
		//判断订单是否过期
		boolean bool=BusinessUtil.isPastDateofServiceTime(orderInfo); 
		if(bool){
			jsonObject.put("isPastDate", "0");
		}else{
			jsonObject.put("isPastDate", "1");
		}

		return jsonObject;
	}

	/** 根据订单ID查询订单的基础信息 */
	@Override
	public JSONObject selectBasicOrderForPush(String appType, Long orderId) throws Exception {

		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 订单ID
		paramMap.put("appType", appType);
		paramMap.put("orderId", orderId);
		if (appType.startsWith("yxt")) {
			paramMap.put("appTypeFlg", "yxt");
		} else {
			paramMap.put("appTypeFlg", Constant.EMPTY);
		}
		Map<String, Object> map = this.iOrderInfoDao.selectOrderGeneral(paramMap);
		BusinessUtil.disposePath(map, "serviceTypePath");
		if (map == null) {
			jsonObject = new ResultJSONObject("select_basicOrder_forPush", "获取订单信息失败");
		} else {
			jsonObject = new ResultJSONObject("000", "获取订单信息成功");
			jsonObject.put("orderInfo", map);
		}
		return jsonObject;
	}

	/** 订单方案详情查询 */
	@Override
	public JSONObject selectDetailOrderPlanInfo(String appType, Long merchantId, Long orderId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 订单ID
		paramMap.put("orderId", orderId);
		Map<String, Object> orderPlanInfoMap = this.iOrderInfoDao.selectOrderPlanInfo(paramMap);
		
		//修订vipStatus  2016.8.26 Revoke
		orderPlanInfoMap.put("vipStatus", -1);
		List<RuleConfig> rulConfigs=incService.getRuleConfig(merchantId);
		if (rulConfigs!=null){
				if (rulConfigs.get(0).isVipMerchantOrder()){
					orderPlanInfoMap.put("vipStatus", 2);
				}
		}
		
		BusinessUtil.disposeManyPath(orderPlanInfoMap, "icoPath", "planVoicePath", "planPicturePath");
		JSONObject jsonObject = new ResultJSONObject("000", "获取订单方案信息成功");
		jsonObject.put("orderPlanInfo", orderPlanInfoMap);
		return jsonObject;
	}

	/** 订单商品详情查询 */
	@Override
	public JSONObject selectDetailOrderGoodsInfo(String appType, Long merchantId, Long orderId) throws Exception {
		return (JSONObject) this.orderGoodsInfoEdit(appType, orderId, true);
	}

	private void orderStatusForMerchantShow(Map<String, Object> orderMap, long currentMerchantId) {
		int orderStatus = StringUtil.nullToInteger(orderMap.get("orderStatus"));
		if (orderMap.get("merchantId") == null) {//为未
			if (orderStatus == 2) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("merchantId", currentMerchantId);
				paramMap.put("orderId", orderMap.get("orderId"));
				int num = this.iMerchantPlanDao.selectPlanNum(paramMap);
				if (num == 0) {
					orderMap.put("orderStatus", 1);
					// orderMap.put("orderStatusName", "待提供");
				}
			}
		} else {
			long merchantId = StringUtil.nullToLong(orderMap.get("merchantId"));
			if (merchantId != currentMerchantId) {
				if (orderStatus == 3 || orderStatus == 4 || orderStatus == 5) {
					orderMap.put("orderStatus", 7);
					// orderMap.put("orderStatusName", "无效订单");
				}
			}
		}
	}

	/** 商品信息编辑 */
	@Override
	public Object orderGoodsInfoEdit(String appType, Long orderId, boolean detailFlg) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 订单ID
		paramMap.put("orderId", orderId);
		List<Map<String, Object>> orderGoodsInfoList = this.iOrderInfoDao.selectOrderGoodsInfo(paramMap);

		int goodsNumTotal = 0;
		BigDecimal goodsPriceTotal = BigDecimal.ZERO;
		for (Map<String, Object> temp : orderGoodsInfoList) {
			Integer goodsNum = (Integer) temp.get("goodsNum");
			BigDecimal goodsPrice = new BigDecimal(String.valueOf(temp.get("goodsPrice")));

			if (!detailFlg) {
				goodsNumTotal = goodsNumTotal + goodsNum;
			}
			goodsPriceTotal = goodsPriceTotal.add(goodsPrice.multiply(new BigDecimal(goodsNum)));
		}
		if (!detailFlg) {
			Map<String, Object> orderGoodsInfo = new HashMap<String, Object>();
			if (goodsNumTotal != 0) {
				orderGoodsInfo.put("goodsNumTotal", goodsNumTotal);
				orderGoodsInfo.put("goodsPriceTotal", goodsPriceTotal);
				
				Map<String, Object> goodsInfoTitleMap = null;
				if ("dgf".equals(appType)) {
					goodsInfoTitleMap = commonService.getConfigurationInfoByKey("goodsInfoTitle_dgf");
				} else if ("xhf".equals(appType)) {
					goodsInfoTitleMap = commonService.getConfigurationInfoByKey("goodsInfoTitle_xhf");
				} else if ("ydc".equals(appType)) {
					goodsInfoTitleMap = commonService.getConfigurationInfoByKey("goodsInfoTitle_ydc");
				} else if ("swg".equals(appType)) {
					goodsInfoTitleMap = commonService.getConfigurationInfoByKey("goodsInfoTitle_swg");
				}
				if (goodsInfoTitleMap != null) {
					Object goodsInfoTitleObj = goodsInfoTitleMap.get("config_value");
					orderGoodsInfo.put("goodsInfoTitle", goodsInfoTitleObj.toString());
				}
			}
			return orderGoodsInfo;
		}
		JSONObject jsonObject = new ResultJSONObject("000", "获取订单商品信息成功");
		jsonObject.put("orderGoodsInfoList", orderGoodsInfoList);
		jsonObject.put("goodsPriceTotal", goodsPriceTotal);
		return jsonObject;
	}

	@Override
	public JSONObject testMerchantPlan(Long merchantId, Long orderId, String appType) throws Exception {
		// 开启异步处理，更新方案的距离、评论数、好评率
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
//		paramMap.put("appType", appType);
		paramMap.put("orderId", orderId);
		Map<String, Object> orderLocationMap = orderApiService.selectOrderLocation(paramMap);
		Map<String, Object> merchantLocationMap = myMerchantService.getLocationInfo(merchantId);
		UpdateMerchantPlan updateMerchantPlan = new UpdateMerchantPlan(merchantId, orderId,  orderLocationMap, merchantLocationMap, iMerchantPlanDao);
		MerchantThreadServices.executor(updateMerchantPlan);
		return null;
	}

	@Override
	public JSONObject immediatelyV316(String appType, String phone,
			Long merchantId, Long orderId, BigDecimal planPrice,
			BigDecimal discountPrice, Long planType, String detail,
			List<Map<String, String>> voicePaths,
			List<Map<String, String>> picturePaths) {
		
		JSONObject jsonObject = null;
		try {
			// 处理$
			detail = StringUtil.formatDollarSign(detail);

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orderId);
			paramMap.put("merchantId", merchantId);
			// 查询商户提供的方案数量
			int checkImmediately = this.iOrderInfoDao.checkImmediatelyOrder(paramMap);
			if (checkImmediately < 1) {// 商户未提供方案
				int orderStatus = this.iOrderInfoDao.selectOrderStatus(paramMap);
				if (orderStatus != 1 && orderStatus != 2) {
					return new ResultJSONObject("order_status_changed", "订单状态已改变，抢单失败，请重新刷新");
				}
				// 订单状态为1的场合，将订单状态更新成2
				if (orderStatus == 1) {
					paramMap = new HashMap<String, Object>();
					paramMap.put("orderId", orderId);
					paramMap.put("orderStatus", 2);
					// 更新订单的状态
					this.iOrderInfoDao.updateOrderStatus(paramMap);
				}
				paramMap = new HashMap<String, Object>();
				paramMap.put("merchantId", merchantId);
				paramMap.put("appType", appType);
				// 更新商户总抢单次数
				this.iMerchantPlanDao.updateGrabFrequency(paramMap);

				// 订单ID
				paramMap.put("orderId", orderId);
				// (商户)登陆的手机号码
				paramMap.put("phone", phone);
				// 方案价格
				paramMap.put("planPrice", planPrice);
				// 优惠价
				paramMap.put("discountPrice", discountPrice);
				// 方案内容
				paramMap.put("content", detail);
				// 方案类型
				paramMap.put("planType", planType);

				// 查询employeeId
				Object employeeId = iMerchantPlanDao.getEmployeeIdByPhone(paramMap);
				paramMap.put("employeeId", employeeId);
				// 保存商户方案
				this.iMerchantPlanDao.insertMerchantPlan(paramMap);

				Long merchantPlanId = (Long) paramMap.get("merchantPlanId");

				List<Map<String, Object>> merchantPlanAttachmentList = new ArrayList<Map<String, Object>>();

				Map<String,Object> merchantDetilMap = new HashMap<String,Object>();
				merchantDetilMap.put("merchantPlanId", merchantPlanId);
				merchantDetilMap.put("content", detail);
				iMerchantPlanDetailDao.insertMerchantPlainDetail(merchantDetilMap);

				paramMap = new HashMap<String, Object>();
				paramMap.put("merchantId", merchantId);
				paramMap.put("appType", appType);
				paramMap.put("orderId", orderId);
				// 订单金额
				BigDecimal orderPrice = merchantPayService.getOrderPrice(paramMap);
				if (orderPrice.compareTo(BigDecimal.ZERO)==1) {// > 0 
					// 抢单成功,订单余额中扣除抢单费
					Map<String, Object> payMap = new HashMap<String, Object>();
					payMap.put("appType", appType);
					payMap.put("merchantId", merchantId);
					payMap.put("money", orderPrice);
					merchantPayService.deductOrderMoney(payMap);

					// 生成一条扣费记录
					payMap = new HashMap<String, Object>();
					payMap.put("appType", appType);
					payMap.put("merchantId", merchantId);
					payMap.put("orderId", orderId);
					payMap.put("payType", 2);
					payMap.put("payMoney", "-"+orderPrice);
					merchantPayService.addMerchantOrderPaymentDetails(payMap);
				}

				jsonObject = new ResultJSONObject("000", "抢单成功");

				// 如果订单缓存存在，则同步更新用户-订单 缓存的状态。
				Map<String, Object> cachedOrder = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
				if (cachedOrder != null) {
					cachedOrder.put("orderStatus", 2);
					userRelatedCacheServices.cacheUserOrderWithJson(cachedOrder.get("userId").toString(), cachedOrder);
				}

				// 增加返回商户的信息完成度，魅力值
				Map<String, Object> merchantInfo = this.myMerchantService.checkMerchantInfo(merchantId);
				jsonObject.put("merchantInfo", merchantInfo);

				// 开启异步处理，更新方案的距离、评论数、好评率
				Map<String, Object> orderLocationMap = this.orderApiService.selectOrderLocation(paramMap);
				Map<String, Object> merchantLocationMap = this.myMerchantService.getLocationInfo(merchantId);
				UpdateMerchantPlan updateMerchantPlan = new UpdateMerchantPlan(merchantId, orderId,  orderLocationMap, merchantLocationMap, iMerchantPlanDao);
				MerchantThreadServices.executor(updateMerchantPlan);
			} else {
				jsonObject = new ResultJSONObject("002", "该商户已经抢过这个订单");
			}
		} catch (Exception ex) {
			logger.error("抢单失败", ex);
			throw new ApplicationException(ex, "acquire_order_failure", "抢单失败");
		}
		return jsonObject;
	}

	
	 public void testText(){
			Map<String,Object> merchantDetilMap = new HashMap<String,Object>();
			merchantDetilMap.put("merchantPlanId", 12121212);
			merchantDetilMap.put("content", "张三李四，网二麻");
			iMerchantPlanDetailDao.insertMerchantPlainDetail(merchantDetilMap);

	 }
}
