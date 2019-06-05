package com.shanjin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.service.IIpCityCacheService;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.CheckDuplicateSubmission;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.ServletUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.exception.ApplicationException;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IMerchantOrderManageService;
import com.shanjin.service.IMyMerchantService;
import com.shanjin.service.IUserOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 用户 业务控制器
 * 
 * @author 李焕民
 * @version 2015-3-26
 *
 */

@Controller
@RequestMapping("/userOrder")
public class UserOrderController {

	@Reference
	private IMyMerchantService myMerchantService;

	@Reference
	private ICommonService commonService;

	@Reference
	private IMerchantOrderManageService merchantOrderManageService;

	@Resource
	private IUserOrderService userOrderService;

	@Resource
	private IIpCityCacheService ipCityCacheServices;

	private static final String DEFAULT_ORDER = "default";

	Logger logger = LoggerFactory.getLogger(UserOrderController.class);

	/**
	 * 获取用户基础订单列表
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getBasicOrderList")
	@SystemControllerLog(description = "获取用户订单列表")
	@CheckDuplicateSubmission(args = "userId,appType,orderStatus,pageNo", type = "getBasicOrderList")
	public @ResponseBody Object getBasicOrderList(@RequestParam(required = false) Long catalogId, Long userId, @RequestParam(required = false) String orderStatus, int pageNo)
			throws InterruptedException {
		JSONObject jsonObject = null;
		try {
//			if (appType == null || appType.trim().equals("") || appType.trim().length() < 1)
//				jsonObject = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).getBasicOrderList(appType, userId, orderStatus, pageNo);
//			else
//				jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).getBasicOrderList(appType, userId, orderStatus, pageNo);
			jsonObject = userOrderService.getBasicOrderList(catalogId, userId, orderStatus, pageNo);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getBasicOrderList");

			jsonObject = new ResultJSONObject("getBasicOrderList_exception", "获取用户订单列表失败");
			logger.error("获取用户订单列表失败", e);
		}
		return jsonObject;
	}

	@RequestMapping("/getBasicOrder")
	@SystemControllerLog(description = "获取用户某订单概要")
	public @ResponseBody Object getBasicOrderInfo(Long orderId) throws InterruptedException {
		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).getBasicOrder(orderId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getBasicOrder");

			jsonObject = new ResultJSONObject("getBasicOrder_exception", "获取用户某订单概要失败");
			logger.error("获取用户某订单概要失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取用户订单详情
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getDetialOrderInfo")
	@SystemControllerLog(description = "获取用户订单详情")
	public @ResponseBody Object getDetialOrderInfo(String appType, Long orderId, Long serviceType) throws InterruptedException {
		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).getDetialOrderInfo(appType, orderId, serviceType);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getDetialOrderInfo");

			jsonObject = new ResultJSONObject("getDetialOrderInfo_exception", "获取用户订单详情失败");
			logger.error("获取用户订单详情失败", e);
		}
		return jsonObject;
	}

	/** 保存订单 */
	@RequestMapping("/saveOrder")
	@SystemControllerLog(description = "保存订单")
	public @ResponseBody Object saveOrder(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		List<String> voicePaths = new ArrayList<String>();
		List<String> picturePaths = new ArrayList<String>();
		String ip = IPutil.getIpAddr(ServletUtil.getRequest());
		String appType = (String) params.get("appType");
		String serviceType = (String) params.get("serviceType");
		String longitude = (String) params.get("longitude");
		String latitude = (String) params.get("latitude");
		String province = (String) params.get("province");
		String city = (String) params.get("city");
		String userId = params.get("userId") == null ? "" : params.get("userId").toString();

		if (userId.equals("") || userId.equals("0")) {
			logger.error("---------------------------------参数userId有误: " + params.get("userId"));
			return new ResultJSONObject("saveOrder_faile", "你未登录，请先登录！");
		}

		// 增加保存订单的时候，先对时间进行判断，如果服务/预约/开始 时间大于当前时间，则不允许保存
		if (DateUtil.checkOrderTime(params)) {
			return new ResultJSONObject("timefail", "请选择大于当前的时间");
		}

		// 2015年12月23日 增加判断有没有开通城市
		Boolean checkCity = true;
		// if (params.get("goodsIds") != null) {
		// checkCity = false;
		// }
		// if (appType.equals("swg")) {
		// checkCity = false;
		// }
		// if (checkCity) {
		try {
			if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city)) {
				// 处理省市信息
				String[] infos = BusinessUtil.handlerProvinceAndCity(province, city);
				province = infos[0];
				city = infos[1];
				if (commonService.checkServiceCity(province, city)) {
				} else {
					return new ResultJSONObject("fail", province + " " + city + " 尚未开通服务");
				}
			} else {
				return new ResultJSONObject("fail", "省 或 市" + " 参数为空");
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "saveOrder");

			logger.error("查询开通城市有误: " + e.getStackTrace());
			return new ResultJSONObject("fail", province + " " + city + " 尚未开通服务");
		}
		// }

		if (latitude != null && !"".equals(latitude)) {
			params.put("latitude", Double.parseDouble(latitude));
		}

		if (longitude != null && !"".equals(longitude)) {
			params.put("longitude", Double.parseDouble(longitude));
		}

		MultipartFile voice = null;
		MultipartFile picture0 = null;
		MultipartFile picture1 = null;
		MultipartFile picture2 = null;
		MultipartFile picture3 = null;
		MultipartFile picture4 = null;
		// 转型为MultipartHttpRequest：

		if (request instanceof MultipartHttpServletRequest) {
			try {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				voice = multipartRequest.getFile("voice");
				picture0 = multipartRequest.getFile("picture0");
				picture1 = multipartRequest.getFile("picture1");
				picture2 = multipartRequest.getFile("picture2");
				picture3 = multipartRequest.getFile("picture3");
				picture4 = multipartRequest.getFile("picture4");

				if (voice != null && !voice.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(voice, "voice");
					if (resultPath.length() > 1) {
						voicePaths.add(resultPath);
					}
				}
				if (picture0 != null && !picture0.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture0, "image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture1 != null && !picture1.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture1, "image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture2 != null && !picture2.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture2, "image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture3 != null && !picture3.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture3, "image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture4 != null && !picture4.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture4, "image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}

			} catch (Exception e) {
				MsgTools.sendMsgOrIgnore(e, "saveOrder");
				logger.error(" 保存订单失败:" + e.getMessage(), e);
			}
		}
		try {
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).saveOrder(appType, serviceType, params, voicePaths, picturePaths, ip);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "saveOrder");
			jsonObject = new ResultJSONObject("saveOrder_exception", "保存订单失败");
			logger.error("保存订单失败", e);
		}
		return jsonObject;
	}

	/** 获取订单供应商列表 */
	@RequestMapping("/getOrderMerchantPlan")
	@SystemControllerLog(description = "获取订单供应商列表")
	public @ResponseBody Object getOrderMerchantPlan(String appType, Long orderId, int pageNo, @RequestParam(required = false) String orderBy) {
		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).getOrderMerchantPlan(appType, orderId, pageNo, orderBy);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderMerchantPlan");
			jsonObject = new ResultJSONObject("getOrderMerchantPlan_exception", "获取订单供应商列表失败");
			logger.error("获取订单供应商列表失败", e);
		}
		return jsonObject;
	}

	/** 为订单选择商户方案 */
	@RequestMapping("/chooseMerchantPlan")
	@SystemControllerLog(description = "用户选择订单的供应商")
	public @ResponseBody Object chooseMerchantPlan(String appType, Long merchantId, Long merchantPlanId, Long orderId) {
		JSONObject jsonObject = null;
		int result = 0;
		try {
			result = AppOrderSvrManager.getOrderServiceByAppType(appType).chooseMerchantPlan(appType, merchantId, merchantPlanId, orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "chooseMerchantPlan");
			jsonObject = new ResultJSONObject("chooseMerchantPlan_exception", "为订单选择商户方案失败");
			logger.error("为订单选择商户方案失败", e);
		}
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "选择商户成功");
		} else {
			jsonObject = new ResultJSONObject("008", "选择商户失败");
		}
		return jsonObject;
	}

	/** 获取订单评价信息 */
	@RequestMapping("/getAssessOrder")
	@SystemControllerLog(description = "获取订单的评价信息")
	public @ResponseBody Object getAssessOrder(Long orderId) {
		JSONObject jsonObject = null;
		List<Map<String, Object>> assessOrder = null;
		try {
			assessOrder = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).getAssessOrder(orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getAssessOrder");
			jsonObject = new ResultJSONObject("getAssessOrder_exception", "获取订单评价信息失败");
			logger.error("获取订单评价信息失败", e);
		}
		jsonObject = new ResultJSONObject("000", "获取订单评价信息成功");
		jsonObject.put("assessOrder", assessOrder);
		return jsonObject;
	}

	/** 评价订单 */
	@RequestMapping("/assessOrder")
	@SystemControllerLog(description = "用户评价订单")
	public @ResponseBody Object assessOrder(String appType, Long orderId, String attitudeEvaluation, String qualityEvaluation, String speedEvaluation, String textEvaluation) {
		JSONObject jsonObject = null;

		int result = 0;
		try {
			result = AppOrderSvrManager.getOrderServiceByAppType(appType).assessOrder(appType, orderId, attitudeEvaluation, qualityEvaluation, speedEvaluation, textEvaluation);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "assessOrder");

			jsonObject = new ResultJSONObject("assessOrder_exception", "用户评价订单失败");
			logger.error("用户评价订单失败", e);
		}
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "更新评价成功");
		} else if (result == -1) {
			jsonObject = new ResultJSONObject("010", "不能重复评价");
		} else {
			jsonObject = new ResultJSONObject("001", "更新评价失败");
		}

		return jsonObject;
	}

	/** 获取用户此次订单可以使用的代金券列表 */
	@RequestMapping("/getUserAvailablePayVouchersInfo")
	@SystemControllerLog(description = "获取用户在这个订单中可用使用的代金券")
	public @ResponseBody Object getUserAvailablePayVouchersInfo(String appType, Long userId, Long serviceType, Long merchantId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).getUserAvailablePayVouchersInfo(appType, userId, serviceType, merchantId, pageNo);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserAvailablePayVouchersInfo");
			jsonObject = new ResultJSONObject("getUserAvailablePayVouchersInfo_exception", "获取用户此次订单可以使用的代金券列表失败");
			logger.error("获取用户此次订单可以使用的代金券列表失败", e);
		}
		return jsonObject;
	}

	/** 确认订单的金额信息 */
	@RequestMapping("/confirmOrderPrice")
	@SystemControllerLog(description = "确认订单的金额信息")
	public @ResponseBody Object confirmOrder(Long orderId, Long merchantId, Double price, Long vouchersId, Double actualPrice) {
		JSONObject jsonObject = null;
		int result = 0;
		try {
			result = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).confirmOrderPrice(orderId, merchantId, price, vouchersId, actualPrice);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "confirmOrderPrice");

			jsonObject = new ResultJSONObject("confirmOrderPrice_exception", "确认订单的金额信息失败");
			logger.error("确认订单的金额信息失败", e);
		}
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "确认订单成功");
		} else if (result == -1) {
			jsonObject = new ResultJSONObject("010", "验证订单失败");
		} else {
			jsonObject = new ResultJSONObject("009", "确认订单失败");
		}
		return jsonObject;
	}

	/** 完成支付宝订单 */
	@RequestMapping("/finishAliPayOrder")
	@SystemControllerLog(description = "完成支付宝订单")
	public @ResponseBody Object finishAliPayOrder(String appType, Long orderId) {
		JSONObject jsonObject = null;
		int result = 0;
		try {
			result = AppOrderSvrManager.getOrderServiceByAppType(appType).finishAliPayOrder(appType, orderId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "finishAliPayOrder");

			jsonObject = new ResultJSONObject("finishAliPayOrder_exception", "完成支付宝订单信息");
			logger.error("完成支付宝订单信息", e);
		}
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "完成支付宝订单成功");
		} else if (result == -1)
			jsonObject = new ResultJSONObject("010", "订单已支付过，无需再支付");
		else {
			jsonObject = new ResultJSONObject("009", "完成支付宝订单失败");
		}
		return jsonObject;
	}

	/** 完成微信订单 */
	@RequestMapping("/finishWeChatOrder")
	@SystemControllerLog(description = "完成微信订单")
	public @ResponseBody Object finishWeChatOrder(String appType, Long orderId) {
		JSONObject jsonObject = null;
		try {
			int result = AppOrderSvrManager.getOrderServiceByAppType(appType).finishWeChatOrder(appType, orderId);
			if (result > 0) {
				jsonObject = new ResultJSONObject("000", "完成微信订单成功");
			} else if (result == -1)
				jsonObject = new ResultJSONObject("010", "订单已支付过，无需再支付");
			else {
				jsonObject = new ResultJSONObject("009", "完成微信订单失败");
			}
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "finishWeChatOrder");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 完成现金订单 需要根据填写的金额来确认订单金额 */
	@RequestMapping("/finishCashOrder")
	@SystemControllerLog(description = "完成现金订单")
	public @ResponseBody Object finishCashOrder(String appType, Long orderId, Long merchantId, String price) {
		JSONObject jsonObject = null;
		try {
			int result = AppOrderSvrManager.getOrderServiceByAppType(appType).finishCashOrder(appType, orderId, merchantId, price);
			if (result > 0) {
				jsonObject = new ResultJSONObject("000", "完成现金订单");
			} else if (result == -1) {
				jsonObject = new ResultJSONObject("010", "订单已支付过，无需再支付");
			} else {
				jsonObject = new ResultJSONObject("009", "完成现金失败");
			}
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "finishCashOrder");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 推送信息给商户版 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/pushMessageToMer")
	@SystemControllerLog(description = "推送信息给商户版")
	public @ResponseBody Object pushMessageToMer(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Map<String, String[]> map = request.getParameterMap();
		System.out.println("-------推送发过来的参数-------pushMessageToMer:" + map.toString());
		String paraString = "";
		String appType = "";
		String phone = "";
		String data = "";
		Long orderId = 0L;
		String IP = IPutil.getIpAddr(request);
		String city = "";
		String province = "";
		JSONObject jsonObject = null;
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			paraString = (String) entry.getKey();
		}
		System.out.println("--------------paraString: " + map.toString());
		JSONObject para = JSON.parseObject(paraString);
		if (para != null) {
			appType = para.getString("appType");
			phone = para.getString("phone");
			data = para.getString("data");
			orderId = para.getLong("data");

			if (para.getString("province") != null && para.getString("city") != null) {
				province = para.getString("province");
				city = para.getString("city");
			} else {
				if (!Constant.DEVMODE) {
					// 根据IP获得城市名称
					JSONObject cachedIpAddress = ipCityCacheServices.getCity(IP);

					if (cachedIpAddress != null) {
						province = cachedIpAddress.getString("province");
						city = cachedIpAddress.getString("city");
					} else {
						String[] provinceAndCity = BusinessUtil.getProvinceAndCityByIp(IP);
						province = provinceAndCity[0];
						city = provinceAndCity[1];
					}
				}
			}
			// 获得请求的参数
			Map<String, Object> paras = new HashMap<String, Object>();
			paras.put("appType", appType);
			paras.put("phone", phone);
			paras.put("orderId", orderId);
			paras.put("data", data);
			paras.put("city", city);
			paras.put("province", province);
			try {
				jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).pushMessageToListForSHBByProxy(paras);
			} catch (Exception e) {
				MsgTools.sendMsgOrIgnore(e, "pushMessageToMer");
				jsonObject = new ResultJSONObject("pushMessageToMer_exception", "推送信息给商户版 失败");
				logger.error("推送信息给商户版 失败", e);
			}
		} else {
			jsonObject = new ResultJSONObject("para_error", "参数有误，请检查参数");
		}

		return jsonObject;
	}

	/** 推送信息给商户版 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/pushMessageSingleToMer")
	@SystemControllerLog(description = "推送信息给商户版")
	public @ResponseBody Object pushMessageSingleToMer(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Map<String, String[]> map = request.getParameterMap();
		System.out.println("-------推送发过来的参数-------" + map.toString());
		String paraString = "";
		String appType = "";
		String data = "";
		Long orderId = 0L;
		String IP = IPutil.getIpAddr(request);
		String city = "";
		String province = "";
		JSONObject jsonObject = null;
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			paraString = (String) entry.getKey();
		}
		System.out.println("--------------paraString: " + map.toString());
		JSONObject para = JSON.parseObject(paraString);
		if (para != null) {
			appType = para.getString("appType");
			data = para.getString("data");
			orderId = para.getLong("data");

			if (para.getString("province") != null && para.getString("city") != null) {
				province = para.getString("province");
				city = para.getString("city");
			} else {
				if (!Constant.DEVMODE) {
					// 根据IP获得城市名称
					JSONObject cachedIpAddress = ipCityCacheServices.getCity(IP);

					if (cachedIpAddress != null) {
						province = cachedIpAddress.getString("province");
						city = cachedIpAddress.getString("city");
					} else {
						String[] provinceAndCity = BusinessUtil.getProvinceAndCityByIp(IP);
						province = provinceAndCity[0];
						city = provinceAndCity[1];
					}
				}
			}
			// 获得请求的参数
			Map<String, Object> paras = new HashMap<String, Object>();
			paras.put("appType", appType);
			paras.put("orderId", orderId);
			paras.put("data", data);
			paras.put("city", city);
			paras.put("province", province);
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).pushMessageToListForSingleSHBByProxy(paras);
		} else {
			jsonObject = new ResultJSONObject("para_error", "参数有误，请检查参数");
		}

		return jsonObject;
	}

	/** 推送信息给用户版 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/pushMessageToUsr")
	@SystemControllerLog(description = "推送信息给用户版")
	public @ResponseBody Object pushMessageToUsr(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Map<String, String[]> map = request.getParameterMap();
		logger.info("-------推送发过来的参数-------" + map.toString());
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

		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("appType", appType);
		paras.put("orderId", orderId);
		paras.put("data", data);
		JSONObject jsonObject = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).pushMessageToListForYHBByProxy(paras);
		return jsonObject;
	}

	/** 删除订单 */
	@RequestMapping("/deleteOrder")
	@SystemControllerLog(description = "删除订单")
	public @ResponseBody Object deleteOrder(Long orderId) {
		JSONObject jsonObject = null;

		int result = 0;
		try {
			result = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).deleteOrder(orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "deleteOrder");

			jsonObject = new ResultJSONObject("deleteOrder_exception", "删除订单失败");
			logger.error("删除订单失败", e);
		}
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "删除订单成功");
		} else {
			jsonObject = new ResultJSONObject("009", "删除订单失败");
		}

		return jsonObject;
	}

	/** 取消订单 */
	@RequestMapping("/cancelOrder")
	@SystemControllerLog(description = "取消订单")
	public @ResponseBody Object cancelOrder(Long orderId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).cancelOrder(orderId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "cancelOrder");
			jsonObject = new ResultJSONObject(e.getResult(), "取消订单失败");
		}
		return jsonObject;
	}

	/** 获得订单的推送状态 */
	@RequestMapping("/getOrderPushType")
	@SystemControllerLog(description = "获得订单的推送状态")
	public @ResponseBody Object getOrderPushType(Long orderId) {
		JSONObject jsonObject = null;
		int result = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).getOrderPushType(orderId);
		jsonObject = new ResultJSONObject("000", "获得订单的推送状态成功");
		jsonObject.put("pushType", result);
		return jsonObject;
	}

	/** 用户端查看店铺详细信息 */
	@RequestMapping("/getMerchantInfo")
	@SystemControllerLog(description = "用户端查看店铺详细信息")
	public @ResponseBody Object getMerchantInfo(String appType, Long userId, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			JSONObject resultObject = this.myMerchantService.selectMyMerchantForUser(appType, userId, merchantId);
			jsonObject = new ResultJSONObject("000", "店铺信息加载成功");
			jsonObject.put("info", resultObject);
		} catch (Exception ex) {
			MsgTools.sendMsgOrIgnore(ex, "getMerchantInfo");
			ex.printStackTrace();
			jsonObject = new ResultJSONObject("105", "店铺信息加载失败");
		}
		return jsonObject;
	}

	/** 用户端查看店铺详细信息 */
	@RequestMapping("/getMerchantInfoV24")
	@SystemControllerLog(description = "用户端查看店铺详细信息")
	public @ResponseBody Object getMerchantInfoV24(Long userId, Long merchantId, @RequestParam(required = false)Double longitude,
			@RequestParam(required = false)Double latitude) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectMyMerchantForUserV24(userId, merchantId, longitude,
					latitude);
		} catch (Exception ex) {
			MsgTools.sendMsgOrIgnore(ex, "getMerchantInfoV24");
			ex.printStackTrace();
			jsonObject = new ResultJSONObject("105", "店铺信息加载失败");
		}
		return jsonObject;
	}

	/** 用户端查看店铺详细信息2 */
	@RequestMapping("/getMerchantInfoV24_2")
	@SystemControllerLog(description = "用户端查看店铺详细信息2")
	public @ResponseBody Object getMerchantInfoV24_2(Long userId, Long merchantId, @RequestParam(required = false)Double longitude,
			@RequestParam(required = false)Double latitude) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectMyMerchantForUserV24_2(userId, merchantId, longitude,
					latitude);
		} catch (Exception ex) {
			MsgTools.sendMsgOrIgnore(ex, "getMerchantInfoV24_2");
			ex.printStackTrace();
			jsonObject = new ResultJSONObject("105", "用户端查看店铺详细信息2加载失败");
		}
        return jsonObject;
	}

	/** 用户端查看店铺基本信息 */
	@RequestMapping("/getMerchantBasicInfo")
	@SystemControllerLog(description = "用户端查看店铺基本信息")
	public @ResponseBody Object getMerchantBasicInfo(Long merchantId) {
		JSONObject jsonObject = null;
		try {
			Map<String, Object> map = myMerchantService.selectMerchantBasicForUser(merchantId);
			if (map != null) {
				jsonObject = new ResultJSONObject("000", "店铺信息加载成功");
				jsonObject.put("info", map);
			} else {
				jsonObject = new ResultJSONObject("001", "店铺信息为空，请检查参数是否正确");
				jsonObject.put("info", map);
			}
		} catch (Exception ex) {
			MsgTools.sendMsgOrIgnore(ex, "getMerchantBasicInfo");

			ex.printStackTrace();
			jsonObject = new ResultJSONObject("105", "店铺信息加载失败");
		}
		return jsonObject;
	}

	/** 顾客评价 */
	@RequestMapping("/userEvaluation")
	@SystemControllerLog(description = "顾客评价")
	public @ResponseBody Object userEvaluation(String appType, Long userId, Long merchantId, int pageNo) {
		return this.myMerchantService.userEvaluationForUser(appType, userId, merchantId, pageNo);
	}


	/** 订单商品详情查询 */
	@RequestMapping("/detailOrderGoodsInfo")
	@SystemControllerLog(description = "订单商品详情查询")
	public @ResponseBody Object detailOrderGoodsInfo(String appType, Long orderId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.merchantOrderManageService.selectDetailOrderGoodsInfo(appType, null, orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "detailOrderGoodsInfo");
			jsonObject = new ResultJSONObject("detailOrderGoodsInfo_exception", "订单商品详情查询失败");
			logger.error("订单商品详情查询失败", e);
		}
		return jsonObject;
	}

	/** 保存商品 */
	@RequestMapping("/saveOrderGoods")
	@SystemControllerLog(description = "保存商品")
	public @ResponseBody Object saveOrderGoods(String appType, Long orderId, Long merchantId, Long merchantPlanId, String goodsIds, String goodsNums) {
		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).saveOrderGoods(appType, orderId, merchantId, merchantPlanId, goodsIds, goodsNums);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "saveOrderGoods");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 查询商户相册 */
	@RequestMapping("/selectMerchantAlbum")
	@SystemControllerLog(description = "查询商户相册")
	public @ResponseBody Object selectMerchantAlbum(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectAlbum(appType, merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "selectMerchantAlbum");
			jsonObject = new ResultJSONObject("selectAlbum_exception", "查询商户相册失败");
			logger.error("查询商户相册失败", e);
		}
		return jsonObject;
	}

	/** 查询商户相片 */
	@RequestMapping("/selectMerchantPhoto")
	@SystemControllerLog(description = "查询商户相片")
	public @ResponseBody Object selectMerchantPhoto(String appType, Long albumId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectPhoto(appType, albumId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "selectMerchantPhoto");

			jsonObject = new ResultJSONObject("selectMerchantPhoto_exception", "查询商户相片失败");
			logger.error("查询商户相片失败", e);
		}
		return jsonObject;
	}

	/** 推送信息给商户版,直接读取参数 */
	@RequestMapping("/pushMessageToMerByPara")
	@SystemControllerLog(description = "推送信息给商户版,直接读取参数")
	public @ResponseBody Object pushMessageToMerByPara(String appType, String data, Long orderId, @RequestParam(required = false) String province, @RequestParam(required = false) String city,
			HttpServletRequest request, HttpServletResponse response, ModelMap model) {

		JSONObject jsonObject = null;
		try {
			String IP = IPutil.getIpAddr(request);
			Map<String, String[]> map = request.getParameterMap();
			System.out.println("-------推送发过来的参数：" + map + ",province: " + province + ",city: " + city);

			String phone = request.getParameter("phone");
			if (appType != null && data != null && orderId != null) {
				if (province != null && city != null) {
					String[] provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
					province = provinceAndCity[0];
					city = provinceAndCity[1];
				} else {
					if (!Constant.DEVMODE) {
						// 根据IP获得城市名称
						JSONObject cachedIpAddress = ipCityCacheServices.getCity(IP);
						if (cachedIpAddress != null) {
							province = cachedIpAddress.getString("province");
							city = cachedIpAddress.getString("city");
						} else {
							String[] provinceAndCity = BusinessUtil.getProvinceAndCityByIp(IP);
							province = provinceAndCity[0];
							city = provinceAndCity[1];
						}
					}
				}
				// 获得请求的参数
				Map<String, Object> paras = new HashMap<String, Object>();
				paras.put("appType", appType);
				paras.put("orderId", orderId);
				paras.put("data", data);
				paras.put("city", city);
				paras.put("phone", phone);
				paras.put("province", province);
				System.out.println("--------------paras" + paras.toString());
				jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).pushMessageToListForSHBByProxy(paras);

			} else {
				jsonObject = new ResultJSONObject("para_error", "参数有误，请检查参数");
			}

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "pushMessageToMerByPara");

			jsonObject = new ResultJSONObject("pushMessageToMerByPara_exception", "推送信息给商户版,直接读取参数 失败");
			logger.error("推送信息给商户版,直接读取参数 失败", e);
		}
		return jsonObject;
	}

	/** 推送信息给单独的商户版,直接读取参数 */
	@RequestMapping("/pushMessageSingleToMerByPara")
	@SystemControllerLog(description = "推送信息给单独的商户版,直接读取参数")
	public @ResponseBody Object pushMessageSingleToMerByPara(String appType, String data, Long orderId, @RequestParam(required = false) String province, @RequestParam(required = false) String city,
			HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		System.out.println("-------推送发过来的参数-------" + request.getParameterMap());
		String IP = IPutil.getIpAddr(request);
		JSONObject jsonObject = null;
		if (appType != null && data != null && orderId != null) {
			if (province != null && city != null) {
			} else {
				if (Constant.DEVMODE) {
					// 根据IP获得城市名称
					JSONObject cachedIpAddress = ipCityCacheServices.getCity(IP);

					if (cachedIpAddress != null) {
						province = cachedIpAddress.getString("province");
						city = cachedIpAddress.getString("city");
					} else {
						JSONObject jsonObjectIp = IPutil.getIpLocationBySina(IP);
						if (jsonObjectIp != null) {
							province = (String) jsonObjectIp.get("province");
							city = (String) jsonObjectIp.get("city");
							ipCityCacheServices.cachedCity(IP, province, city);
						} else {
							jsonObjectIp = IPutil.getIpLocationByBaidu(IP);
							if (jsonObjectIp != null) {
								if (jsonObjectIp.get("address") != null) {
									String address = (String) jsonObjectIp.get("address");
									if (address != null && address.length() > 0) {
										String[] addressDetail = address.split("\\|");
										if (addressDetail != null) {
											province = addressDetail[1];
											city = addressDetail[2];
											ipCityCacheServices.cachedCity(IP, province, city);
										}
									}
								}
							}
						}
					}
				}
			}
			// 获得请求的参数
			Map<String, Object> paras = new HashMap<String, Object>();
			paras.put("appType", appType);
			paras.put("orderId", orderId);
			paras.put("data", data);
			paras.put("city", city);
			paras.put("province", province);
			jsonObject = AppOrderSvrManager.getOrderServiceByAppType(appType).pushMessageToListForSingleSHBByProxy(paras);
		} else {
			jsonObject = new ResultJSONObject("para_error", "参数有误，请检查参数");
		}

		return jsonObject;
	}

	/** 推送信息给用户版,直接读取参数 */
	@RequestMapping("/pushMessageToUsrByPara")
	@SystemControllerLog(description = "推送信息给用户版,直接读取参数")
	public @ResponseBody Object pushMessageToUsrByPara(String data, Long orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		String appType = Constant.PUSH_CONFIG.APP_TYPE_OMENG;
		// 获得请求的参数
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("appType", appType);
		paras.put("orderId", orderId);
		paras.put("data", data);
		JSONObject jsonObject = AppOrderSvrManager.getOrderServiceByAppType(DEFAULT_ORDER).pushMessageToListForYHBByProxy(paras);
		return jsonObject;
	}


	/** 订单方案详情查询 */
	@RequestMapping("/detailOrderPlanInfo")
	@SystemControllerLog(description = "订单方案详情查询")
	public @ResponseBody Object detailOrderPlanInfo(String appType, Long merchantId, Long orderId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.merchantOrderManageService.selectDetailOrderPlanInfo(appType, merchantId, orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "detailOrderPlanInfo");
			jsonObject = new ResultJSONObject("selectMerchantPhoto_exception", "订单方案详情查询失败");
			logger.error("订单方案详情查询失败", e);
		}
		return jsonObject;
	}


}
