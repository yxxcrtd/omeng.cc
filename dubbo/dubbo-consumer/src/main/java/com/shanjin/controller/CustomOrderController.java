package com.shanjin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.*;
import com.shanjin.exception.ApplicationException;
import com.shanjin.service.ICommonService;
import com.shanjin.service.ICustomOrderService;
import com.shanjin.service.IMongoCustomOrderService;
import com.shanjin.service.IMyMerchantService;
import com.shanjin.service.IUserInfoService;
import com.shanjin.service.IUserOrderService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通用订单控制类
 * 
 * @author Huang yulai
 *
 */
@Controller
@RequestMapping("/customOrder")
public class CustomOrderController {

	Logger logger = LoggerFactory.getLogger(CustomOrderController.class);

	@Reference
	private ICommonService commonService;

	@Reference
	private ICustomOrderService customOrderService;

	@Reference
	private IUserInfoService userService;

	@Reference
	private IMongoCustomOrderService mongoCustomOrderService;

	@Resource
	private IUserOrderService userOrderService;
	@Resource
	private IMyMerchantService myMerchantService;

	@RequestMapping("/getRecommedServiceList")
	@SystemControllerLog(description = "获取推荐服务列表")
	public @ResponseBody Object getRecommedServiceList(
			HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			String httpStr = HttpRequest.getDomain(request);
			List<Map<String, Object>> orderCatalogList = customOrderService
					.getRecommedServiceList(httpStr);
			jsonObject = new ResultJSONObject("000", "获取推荐服务列表成功");
			jsonObject.put("getRecommedServiceList", orderCatalogList);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getRecommedServiceList");
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取推荐服务列表错误");
		}
		return jsonObject;
	}

	@RequestMapping("/orderCatalogList")
	@SystemControllerLog(description = "获取发单分类")
	public @ResponseBody Object orderCatalogList(HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			String httpStr = HttpRequest.getDomain(request);
			List<Map<String, Object>> orderCatalogList = customOrderService
					.orderCatalogList(httpStr);
			if (orderCatalogList != null && orderCatalogList.size() > 0) {
				String url = "";
				for (Map<String, Object> map : orderCatalogList) {
					String flag = StringUtil.null2Str(map.get("flag"));// 分类标识（0：行业分类，1：个性服务，2：第三方服务）
					if ("2".equals(flag)) {
						url = HttpRequest.getDomain(request)
								+ "/customOrder/thirdServiceList?catalogId="
								+ map.get("id");
					} else {
						// nothing to do
					}
					map.put("url", url);
				}
			}
			jsonObject = new ResultJSONObject("000", "获取发单分类成功");
			jsonObject.put("orderCatalogList", orderCatalogList);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "orderCatalogList");
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取发单分类错误");
		}
		return jsonObject;
	}

	@RequestMapping("/thirdServiceList")
	@SystemControllerLog(description = "获取第三方服务列表")
	public @ResponseBody Object thirdServiceList(String catalogId,
			HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			List<Map<String, Object>> thirdServiceList = customOrderService
					.thirdServiceList(catalogId);
			jsonObject = new ResultJSONObject("000", "获取第三方服务列表成功");
			jsonObject.put("thirdServiceList", thirdServiceList);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "thirdServiceList");
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取第三方服务列表错误");
		}
		return jsonObject;
	}

	/** test */
	@RequestMapping("/catalogListTest")
	@SystemControllerLog(description = "获取发单一级分类")
	public @ResponseBody Object catalogListTest(HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			List<Map<String, Object>> orderCatalogList = customOrderService
					.getOrderCatalogList();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (orderCatalogList != null && orderCatalogList.size() > 0) {
				String Url = "";
				for (Map<String, Object> map : orderCatalogList) {
					String flag = StringUtil.null2Str(map.get("flag"));// 分类标识（0：行业分类，1：个性服务，2：第三方服务）
					List<Map<String, Object>> orderCatalogAndServiceList = new ArrayList<Map<String, Object>>();
					if ("2".equals(flag)) {
						Url = StringUtil.null2Str(map.get("url"));
						continue;
					} else {
						Url = "";
						orderCatalogAndServiceList = customOrderService
								.getOrderCatalogAndServiceList(map.get("id")
										+ "");
						if (orderCatalogAndServiceList != null
								&& orderCatalogAndServiceList.size() > 0) {
							String url = "";
							for (Map<String, Object> map2 : orderCatalogAndServiceList) {
								String isService = StringUtil.null2Str(map2
										.get("isService"));
								if ("1".equals(isService)) {
									// 服务
									url = HttpRequest.getDomain(request)
											+ "/customOrder/getOrderForm?serviceId="
											+ map2.get("id");
									map2.put("url", url);
								} else {
									// 分类，需解析下级服务列表
									List<Map<String, Object>> serviceList = (List<Map<String, Object>>) map2
											.get("serviceList");
									if (serviceList != null
											&& serviceList.size() > 0) {
										for (Map<String, Object> smap : serviceList) {
											url = HttpRequest
													.getDomain(request)
													+ "/customOrder/getOrderForm?serviceId="
													+ smap.get("id");
											smap.put("url", url);
										}
									}
									map2.put("serviceList", serviceList);
								}
							}
						}
					}

					map.put("url", Url);
					map.put("orderCatalogAndServiceList",
							orderCatalogAndServiceList);
					list.add(map);
				}
			}
			jsonObject = new ResultJSONObject("000", "获取发单一级分类成功");
			jsonObject.put("orderCatalogList", list);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "orderCatalogList");
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取发单一级分类错误");
		}
		return jsonObject;
	}

	/** 获取发单一级分类 */
	@RequestMapping("/getOrderCatalogList")
	@SystemControllerLog(description = "获取发单一级分类")
	public @ResponseBody Object getOrderCatalogList(HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			List<Map<String, Object>> orderCatalogList = customOrderService
					.getOrderCatalogList();
			if (orderCatalogList != null && orderCatalogList.size() > 0) {
				String url = "";
				for (Map<String, Object> map : orderCatalogList) {
					String flag = StringUtil.null2Str(map.get("flag"));// 分类标识（0：行业分类，1：个性服务，2：第三方服务）
					/**
					 * 行业或个性，一级进入二级；第三方直接进入服务页
					 */
					if ("2".equals(flag)) {
						url = StringUtil.null2Str(map.get("url"));
					} else {
						url = HttpRequest.getDomain(request)
								+ "/customOrder/getOrderCatalogAndServiceList?catalogId="
								+ map.get("id");
					}
					map.put("url", url);
				}
			}
			jsonObject = new ResultJSONObject("000", "获取发单一级分类成功");
			jsonObject.put("orderCatalogList", orderCatalogList);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "orderCatalogList");
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取发单一级分类错误");
		}
		return jsonObject;
	}

	/** 根据一级分类ID获取发单二级分类及服务 */
	@RequestMapping("/getOrderCatalogAndServiceList")
	@SystemControllerLog(description = "根据一级分类ID获取发单二级分类及服务")
	public @ResponseBody Object getOrderCatalogAndServiceList(String catalogId,
			HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			List<Map<String, Object>> orderCatalogAndServiceList = customOrderService
					.getOrderCatalogAndServiceList(catalogId);
			if (orderCatalogAndServiceList != null
					&& orderCatalogAndServiceList.size() > 0) {
				String url = "";
				for (Map<String, Object> map : orderCatalogAndServiceList) {
					String isService = StringUtil
							.null2Str(map.get("isService"));
					if ("1".equals(isService)) {
						// 服务
						url = HttpRequest.getDomain(request)
								+ "/customOrder/getOrderForm?serviceId="
								+ map.get("id");
						map.put("url", url);
					} else {
						// 分类，需解析下级服务列表
						List<Map<String, Object>> serviceList = (List<Map<String, Object>>) map
								.get("serviceList");
						if (serviceList != null && serviceList.size() > 0) {
							for (Map<String, Object> smap : serviceList) {
								url = HttpRequest.getDomain(request)
										+ "/customOrder/getOrderForm?serviceId="
										+ smap.get("id");
								smap.put("url", url);
							}
						}
						map.put("serviceList", serviceList);
					}
				}
			}

			jsonObject = new ResultJSONObject("000", "根据一级分类ID获取发单二级分类及服务成功");
			jsonObject.put("orderCatalogAndServiceList",
					orderCatalogAndServiceList);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "orderCatalogAndServiceList");
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "根据一级分类ID获取发单二级分类及服务错误");
		}
		return jsonObject;
	}

	/**
	 * 获取自定义订单表单
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderForm")
	@SystemControllerLog(description = "获取订单表单配置数据")
	public @ResponseBody Object getOrderForm(String serviceId,
			HttpServletRequest request) {
		JSONObject jsonObject = new ResultJSONObject("fail", "获取订单表单配置数据失败");

		try {
			if (!StringUtil.isNullStr(serviceId)) {
				String version = customOrderService
						.getCustomOrderFormVersion(serviceId);
				List<Map<String, Object>> orderForm = customOrderService
						.getCustomOrderForm(serviceId);
				jsonObject = new ResultJSONObject("000", "获得自定义订单表单成功");
				if (orderForm != null && orderForm.size() > 0) {
					for (Map<String, Object> map : orderForm) {
						String link = StringUtil.null2Str(map.get("link"));
						if ("NEXT_SCENE".equals(StringUtil.null2Str(map
								.get("controlType")))
								&& !StringUtil.isNullStr(link)) {
							// 下页链接控件，根据字典类型自动匹配
							link = HttpRequest.getDomain(request)
									+ "/dictInfo/getFormDictList?dictType="
									+ link;
						}
						map.put("link", link);
					}
				}
				Map<String, Object> banner = customOrderService
						.getCustomOrderBanner(serviceId);
				jsonObject.put("version", version);
				jsonObject.put("banner", banner);
				jsonObject.put("orderForm", orderForm);
				jsonObject
						.put("saveOrderUrl", HttpRequest.getDomain(request)
								+ "/customOrder/saveCustomOrder?serviceId="
								+ serviceId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取订单表单配置数据失败");
		}
		return jsonObject;
	}

	/**
	 * 获取自定义订单表单（重新发单）
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderFormByOrderId")
	@SystemControllerLog(description = "获取订单表单配置数据")
	public @ResponseBody Object getOrderFormByOrderId(String serviceId,
			String orderId, HttpServletRequest request) {
		JSONObject jsonObject = new ResultJSONObject("fail", "获取订单表单配置数据失败");

		try {
			if (!StringUtil.isNullStr(serviceId)) {
				String version = customOrderService
						.getCustomOrderFormVersion(serviceId);
				List<Map<String, Object>> orderForm = customOrderService
						.getCustomOrderFormByOrderId(serviceId, orderId);
				jsonObject = new ResultJSONObject("000", "获得自定义订单表单成功");
				if (orderForm != null && orderForm.size() > 0) {
					for (Map<String, Object> map : orderForm) {
						String link = StringUtil.null2Str(map.get("link"));
						if ("NEXT_SCENE".equals(StringUtil.null2Str(map
								.get("controlType")))
								&& !StringUtil.isNullStr(link)) {
							// 下页链接控件，根据字典类型自动匹配
							link = HttpRequest.getDomain(request)
									+ "/dictInfo/getFormDictList?dictType="
									+ link;
						}
						map.put("link", link);
					}
				}
				Map<String, Object> banner = customOrderService
						.getCustomOrderBanner(serviceId);
				jsonObject.put("version", version);
				jsonObject.put("banner", banner);
				jsonObject.put("orderForm", orderForm);
				jsonObject
						.put("saveOrderUrl", HttpRequest.getDomain(request)
								+ "/customOrder/saveCustomOrder?serviceId="
								+ serviceId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取订单表单配置数据失败");
		}
		return jsonObject;
	}

	/** 订单保存 */
	@RequestMapping("/saveCustomOrder")
	@SystemControllerLog(description = "保存订单")
	public @ResponseBody Object saveCustomOrder(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		List<String> voicePaths = new ArrayList<String>();
		List<String> picturePaths = new ArrayList<String>();
		String ip = IPutil.getIpAddr(ServletUtil.getRequest());
		// String appType = (String) params.get("appType");
		String serviceId = (String) params.get("serviceId");
		String longitude = (String) params.get("longitude");
		String latitude = (String) params.get("latitude");
		String province = (String) params.get("province");
		String city = (String) params.get("city");
		String userId = params.get("userId") == null ? "" : params
				.get("userId").toString();

		// 验证当前用户是否在黑名单之列
		if (customOrderService.checkInBlacklist(StringUtil.nullToLong(userId))) {
			return new ResultJSONObject("blacklist",
					"对不起，你已被禁止发单，如有疑问请与O盟联系400-020-0505");
		}

		if (userId.equals("") || userId.equals("0")) {
			logger.error("---------------------------------参数userId有误: "
					+ params.get("userId"));
			return new ResultJSONObject("saveOrder_faile", "你未登录，请先登录！");
		}

		// 增加保存订单的时候，先对时间进行判断，如果服务/预约/开始 时间大于当前时间，则不允许保存
		if (DateUtil.checkOrderTime(params)) {
			return new ResultJSONObject("timefail", "请选择大于当前的时间");
		}

		// 2015年12月23日 增加判断有没有开通城市
		try {
			if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city)) {
				// 处理省市信息
				String[] infos = BusinessUtil.handlerProvinceAndCity(province,
						city);
				province = infos[0];
				city = infos[1];
				if (commonService.checkServiceCity(province, city)) {
				} else {
					return new ResultJSONObject("fail", province + " " + city
							+ " 尚未开通服务");
				}
			} else {
				return new ResultJSONObject("fail", "省 或 市" + " 参数为空");
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "saveCustomOrder");
			logger.error("查询开通城市有误: " + e.getStackTrace());
			return new ResultJSONObject("fail", province + " " + city
					+ " 尚未开通服务");
		}

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
					String resultPath = BusinessUtil.saveOrderFile(voice,
							"voice");
					if (resultPath.length() > 1) {
						voicePaths.add(resultPath);
					}
				}
				if (picture0 != null && !picture0.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture0,
							"image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture1 != null && !picture1.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture1,
							"image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture2 != null && !picture2.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture2,
							"image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture3 != null && !picture3.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture3,
							"image");
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture4 != null && !picture4.isEmpty()) {
					String resultPath = BusinessUtil.saveOrderFile(picture4,
							"image");
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
			jsonObject = customOrderService.saveCustomOrder(serviceId, params,
					voicePaths, picturePaths, ip);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "saveOrder", params.toString());
			jsonObject = new ResultJSONObject("saveOrder_exception", "保存订单失败");
			logger.error("保存订单失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取发单用户订单列表
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderListForSender")
	@SystemControllerLog(description = "获取用户订单详情")
	public @ResponseBody Object getOrderListForSender(
			@RequestParam Map<String, Object> params, HttpServletRequest request)
			throws InterruptedException {
		/**
		 * 需要的参数：
		 * 
		 * @RequestParam(required = false)Long catalogId, Long userId,
		 * @RequestParam(required = false) String orderStatus, int pageNo
		 */
		JSONObject jsonObject = null;
		try {
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				jsonObject = mongoCustomOrderService
						.getOrderListForSender(params);
			} else {
				jsonObject = customOrderService.getOrderListForSender(params);
				/**
				 * 如果查询的是全部且本页无数据，则再查询mongodb 看是否存在历史订单 Revoke 2016.10.11
				String orderStatus = StringUtil.null2Str(params
						.get("orderStatus"));
				int pageNo = StringUtil.nullToInteger(params.get("pageNo"));

				if (orderStatus.equals("")
						&& jsonObject.getString("resultCode").equals("000")
						&& (jsonObject.getJSONArray("orderList").size() < Constant.PAGESIZE || pageNo + 1 >= jsonObject
								.getIntValue("totalPage"))) {
					Boolean hasHistory = mongoCustomOrderService
							.hasUserHistoryOrder(params);
					jsonObject.put("hasHistory", hasHistory ? 1 : 0);
				}
				**/
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderListForSender",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderListForSender_exception", "获取发单用户订单列表失败");
			logger.error("获取发单用户订单列表失败", e);
		}
		return jsonObject;
	}
	/**
	 * 获取发单用户订单列表 v1110 CUIJIAJUN
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderListForSenderV1110")
	@SystemControllerLog(description = "获取用户订单详情")
	public @ResponseBody Object getOrderListForSenderV1110(
			@RequestParam Map<String, Object> params, HttpServletRequest request)
			throws InterruptedException {
		/**
		 * 需要的参数：
		 * 
		 * @RequestParam(required = false)Long catalogId, Long userId,
		 * @RequestParam(required = false) String orderStatus, int pageNo
		 */
		JSONObject jsonObject = null;
		try {
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				jsonObject = mongoCustomOrderService.getOrderListForSender(params);
			} else {
				jsonObject = customOrderService.getOrderListForSenderV1110(params);
				/**
				 * 如果查询的是全部且本页无数据，则再查询mongodb 看是否存在历史订单 Revoke 2016.10.11
				String orderStatus = StringUtil.null2Str(params
						.get("orderStatus"));
				int pageNo = StringUtil.nullToInteger(params.get("pageNo"));

				if (orderStatus.equals("")
						&& jsonObject.getString("resultCode").equals("000")
						&& (jsonObject.getJSONArray("orderList").size() < Constant.PAGESIZE || pageNo + 1 >= jsonObject
								.getIntValue("totalPage"))) {
					Boolean hasHistory = mongoCustomOrderService
							.hasUserHistoryOrder(params);
					jsonObject.put("hasHistory", hasHistory ? 1 : 0);
				}
				*/
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderListForSender",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderListForSender_exception", "获取发单用户订单列表失败");
			logger.error("获取发单用户订单列表失败", e);
		}
		return jsonObject;
	}
	/** 获取接单商户订单列表 */
	@RequestMapping("/getOrderListForReceiver")
	@SystemControllerLog(description = "获取接单商户订单列表")
	public @ResponseBody Object getOrderListForReceiver(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/**
		 * 需要的参数：
		 * 
		 * @RequestParam(required = false)Long catalogId, Long userId, Long
		 *                        merchantId,
		 * @RequestParam(required = false)Long serviceType,
		 * @RequestParam(required = false)int status, int pageNo
		 */
		JSONObject jsonObject = null;
		try {
			if (isHistory(params)) {
//				jsonObject = mongoCustomOrderService.getOrderListForReceiver(params);
			} else {
				jsonObject = this.customOrderService
						.getOrderListForReceiver(params);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderListForReceiver",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderListForReceiver_exception", "获取接单商户订单列表失败");
			logger.error("获取接单商户订单列表失败", e);
		}
		return jsonObject;
	}
	/** 获取接单商户订单列表 */
	@RequestMapping("/getOrderListForReceiverV1110")
	@SystemControllerLog(description = "获取接单商户订单列表")
	public @ResponseBody Object getOrderListForReceiverV1110(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/**
		 * 需要的参数：
		 * 
		 * @RequestParam(required = false)Long catalogId, Long userId, Long
		 *                        merchantId,
		 * @RequestParam(required = false)Long serviceType,
		 * @RequestParam(required = false)int status, int pageNo
		 */
		JSONObject jsonObject = null;
		try {
			if (isHistory(params)) {
//				jsonObject = mongoCustomOrderService.getOrderListForReceiver(params);
			} else {
				jsonObject = this.customOrderService
						.getOrderListForReceiverV1110(params);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderListForReceiver",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderListForReceiver_exception", "获取接单商户订单列表失败");
			logger.error("获取接单商户订单列表失败", e);
		}
		return jsonObject;
	}
	/**
	 * 获取发单用户订单详情
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderDetailForSender")
	@SystemControllerLog(description = "获取用户订单详情")
	public @ResponseBody Object getOrderDetailForSender(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				jsonObject = mongoCustomOrderService
						.getOrderListForSender(params);
			} else {
				jsonObject = customOrderService.getOrderListForSender(params);
				/**
				 * 如果查询的是全部且本页无数据，则再查询mongodb 看是否存在历史订单 Revoke 2016.10.11
				 */
				String orderStatus = StringUtil.null2Str(params
						.get("orderStatus"));
				int pageNo = StringUtil.nullToInteger(params.get("pageNo"));

				if (orderStatus.equals("")
						&& jsonObject.getString("resultCode").equals("000")
						&& (jsonObject.getJSONArray("orderList").size() < Constant.PAGESIZE || pageNo + 1 >= jsonObject
								.getIntValue("totalPage"))) {
					Boolean hasHistory = mongoCustomOrderService
							.hasUserHistoryOrder(params);
					jsonObject.put("hasHistory", hasHistory ? 1 : 0);
				}
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderDetailForSender",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderDetailForSender_exception", "获取用户订单详情失败");
			logger.error("获取用户订单详情失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取发单用户订单详情-----V730版本 Revoke
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderDetailForSenderV730")
	@SystemControllerLog(description = "获取用户订单详情")
	public @ResponseBody Object getOrderDetailForSenderV730(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			String orderId = StringUtil.null2Str(params.get("orderId"));
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				Long userId = StringUtil.nullToLong(params.get("userId"));
				if (userId.longValue() != 0) {
					jsonObject = mongoCustomOrderService
							.getOrderDetailForSender(orderId, userId);
				} else {
					logger.error("获取用户侧订单详情失败--缺少userId");
					throw new Exception("获取用户侧订单详情失败--缺少userId");
				}
			} else {
				jsonObject = customOrderService
						.getOrderDetailForSenderV730(orderId);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderDetailForSender",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderDetailForSender_exception", "获取用户订单详情失败");
			logger.error("获取用户订单详情失败", e);
		}
		return jsonObject;
	}


	/**
	 * 获取发单用户订单详情-----V1110 CUIJIAJUN
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderDetailForSenderV1110")
	@SystemControllerLog(description = "获取用户订单详情")
	public @ResponseBody Object getOrderDetailForSenderV1110(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			String orderId = StringUtil.null2Str(params.get("orderId"));
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				Long userId = StringUtil.nullToLong(params.get("userId"));
				if (userId.longValue() != 0) {
					jsonObject = mongoCustomOrderService
							.getOrderDetailForSender(orderId, userId);
				} else {
					logger.error("获取用户侧订单详情失败--缺少userId");
					throw new Exception("获取用户侧订单详情失败--缺少userId");
				}
			} else {
				jsonObject = customOrderService
						.getOrderDetailForSenderV1110(orderId);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderDetailForSender",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderDetailForSender_exception", "获取用户订单详情失败");
			logger.error("获取用户订单详情失败", e);
		}
		return jsonObject;
	}
	/**
	 * 获取发单用户订单详情-----V1110 CUIJIAJUN
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/readMerchantPlan")
	@SystemControllerLog(description = "更新方案状态-已读")
	public @ResponseBody Object readMerchantPlan(@RequestParam Map<String, Object> params) {
		JSONObject jsonObject = null;
		try {
	    	if(!params.containsKey("orderId") || !params.containsKey("planId")){
	    		throw new ApplicationException("001","readMerchantPlan，参数传入不正确。");
	    	}
	    	jsonObject = customOrderService.readMerchantPlan(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "readMerchantPlan",
					params.toString());
			jsonObject = new ResultJSONObject(
					"readMerchantPlan_exception", "更新方案状态-已读失败");
			logger.error("更新方案状态-已读失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取接单商户订单详情
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderDetailForReceiver")
	@SystemControllerLog(description = "获取接单商户订单详情")
	public @ResponseBody Object getOrderDetailForReceiver(
			@RequestParam Map<String, Object> params, HttpServletRequest request)
			throws InterruptedException {
		/**
		 * 需要的参数： Long orderId, Long merchantId,
		 */
		JSONObject jsonObject = null;
		try {
			if (isHistory(params)) {
//				jsonObject = mongoCustomOrderService.getOrderDetailForReceiver(params);
			} else {
				jsonObject = customOrderService
						.getOrderDetailForReceiverV730(params);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderDetailForReceiver",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderDetailForReceiver_exception", "获取接单商户订单详情失败");
			logger.error("获取接单商户订单详情失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取接单商户订单详情
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderDetailForReceiverV730")
	@SystemControllerLog(description = "获取接单商户订单详情")
	public @ResponseBody Object getOrderDetailForReceiverV730(
			@RequestParam Map<String, Object> params, HttpServletRequest request)
			throws InterruptedException {
		/**
		 * 需要的参数： Long orderId, Long merchantId,
		 */
		JSONObject jsonObject = null;
		try {
            if (isHistory(params)) {
//                jsonObject = mongoCustomOrderService.getOrderDetailForReceiver(params);
            } else {
                jsonObject = customOrderService.getOrderDetailForReceiverV730(params);
            }
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderDetailForReceiver_V160730",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderDetailForReceiver_V160730_exception",
					"获取接单商户订单详情失败");
			logger.error("获取接单商户订单详情失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取接单商户订单详情 V1110 CUIJIAJUN
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getOrderDetailForReceiverV1110")
	@SystemControllerLog(description = "获取接单商户订单详情")
	public @ResponseBody Object getOrderDetailForReceiverV1110(
			@RequestParam Map<String, Object> params, HttpServletRequest request)
			throws InterruptedException {
		/**
		 * 需要的参数： Long orderId, Long merchantId,
		 */
		JSONObject jsonObject = null;
		try {
            if (isHistory(params)) {
//                jsonObject = mongoCustomOrderService.getOrderDetailForReceiver(params);
            } else {
                jsonObject = customOrderService.getOrderDetailForReceiverV1110(params);
            }
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderDetailForReceiver_V1110",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderDetailForReceiver_V1110_exception",
					"获取接单商户订单详情失败");
			logger.error("获取接单商户订单详情失败", e);
		}
		return jsonObject;
	}
	
	/**
	 * 获取自定义报价方案表单
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getPricePlanForm")
	@SystemControllerLog(description = "获取报价表单配置数据")
	public @ResponseBody Object getPricePlanForm(String serviceId) {
		JSONObject jsonObject = new ResultJSONObject("fail", "获取报价表单配置数据失败");

		try {
			jsonObject = customOrderService.getPricePlanForm(serviceId);

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getPricePlanForm");
			jsonObject = new ResultJSONObject("getPricePlanForm_exception",
					"获取自定义报价方案表单失败");
			logger.error("获取自定义报价方案表单失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取认证，抢单金等信息
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getSupplyPricePlanCheckInfo")
	@SystemControllerLog(description = "获取提交报价方案验证信息")
	public @ResponseBody Object getSupplyPricePlanCheckInfo(
			@RequestParam Map<String, Object> params) {
		JSONObject jsonObject = new ResultJSONObject("fail", "获取提交报价方案验证信息失败");

		try {
			jsonObject = customOrderService.getSupplyPricePlanCheckInfo(params);

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getSupplyPricePlanCheckInfo",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getSupplyPricePlanCheckInfo_exception", "获取提交报价方案验证信息失败");
			logger.error("获取提交报价方案验证信息失败", e);
		}
		return jsonObject;
	}

	/**
	 * 接单用户提供报价方案
	 * 
	 * @param params
	 * @param request
	 * @return
	 */
	@RequestMapping("/supplyPricePlan")
	@SystemControllerLog(description = "立即抢单提供方案")
	public @ResponseBody Object supplyPricePlan(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/**
		 * 需要的参数： String phone, Long merchantId, Long orderId, BigDecimal
		 * planPrice, BigDecimal discountPrice, Long planType, String detail,
		 */

		// 提供报价方案前验证信息
		JSONObject verifyResult = null;
		try {
			verifyResult = this.customOrderService.pricePlanVerify(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "supplyPricePlan");
			logger.error("接单用户提供报价方案失败", e);
			return new ResultJSONObject("supplyPricePlan_exception",
					"接单用户提供报价方案失败");
		}
		if (!StringUtils.equals("000", verifyResult.get("resultCode")
				.toString())) {
			return verifyResult;
		}
		List<String> voicePaths = new ArrayList<String>();
		List<String> picturePaths = new ArrayList<String>();

		MultipartFile voice = null;
		MultipartFile image0 = null;
		MultipartFile image1 = null;
		MultipartFile image2 = null;
		MultipartFile image3 = null;
		MultipartFile image4 = null;
		try {
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				voice = multipartRequest.getFile("voice");
				image0 = multipartRequest.getFile("picture0");
				image1 = multipartRequest.getFile("picture1");
				image2 = multipartRequest.getFile("picture2");
				image3 = multipartRequest.getFile("picture3");
				image4 = multipartRequest.getFile("picture4");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				String filePath = new StringBuilder(
						Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH)
						.append("pricePlan").append(Constant.FILE_EPARATOR)
						.append(sdf.format(new Date()))
						.append(Constant.FILE_EPARATOR).toString();
				if (voice != null && !voice.isEmpty()) {
					String resultPath = BusinessUtil
							.fileUpload(voice, filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						voicePaths.add(resultPath);
					}
				}

				filePath = new StringBuilder(
						Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH)
						.append("pricePlan").append(Constant.FILE_EPARATOR)
						.append(sdf.format(new Date()))
						.append(Constant.FILE_EPARATOR).toString();
				if (image0 != null && !image0.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image0,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image1 != null && !image1.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image1,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image2 != null && !image2.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image2,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image3 != null && !image3.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image3,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image4 != null && !image4.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image4,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "supplyPricePlan");
			logger.error("", e);
		}

		JSONObject jsonObject = null;
		try {
			params.put("picturePaths", picturePaths);
			params.put("voicePaths", voicePaths);
			jsonObject = customOrderService.wrapSupplyPricePlan(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "supplyPricePlan", params.toString());
			jsonObject = new ResultJSONObject("supplyPricePlan_exception",
					"接单用户提供报价方案失败");
			logger.error("接单用户提供报价方案失败", e);
		}
		return jsonObject;
	}

	/**
	 * 接单用户提供报价方案
	 * 
	 * @param params
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateOrderPricePlan")
	@SystemControllerLog(description = "修改报价方案")
	public @ResponseBody Object updateOrderPricePlan(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {

		List<String> voicePaths = new ArrayList<String>();
		List<String> picturePaths = new ArrayList<String>();

		MultipartFile voice = null;
		MultipartFile image0 = null;
		MultipartFile image1 = null;
		MultipartFile image2 = null;
		MultipartFile image3 = null;
		MultipartFile image4 = null;
		try {
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				voice = multipartRequest.getFile("voice");
				image0 = multipartRequest.getFile("picture0");
				image1 = multipartRequest.getFile("picture1");
				image2 = multipartRequest.getFile("picture2");
				image3 = multipartRequest.getFile("picture3");
				image4 = multipartRequest.getFile("picture4");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				String filePath = new StringBuilder(
						Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH)
						.append("pricePlan").append(Constant.FILE_EPARATOR)
						.append(sdf.format(new Date()))
						.append(Constant.FILE_EPARATOR).toString();
				if (voice != null && !voice.isEmpty()) {
					String resultPath = BusinessUtil
							.fileUpload(voice, filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						voicePaths.add(resultPath);
					}
				}

				filePath = new StringBuilder(
						Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH)
						.append("pricePlan").append(Constant.FILE_EPARATOR)
						.append(sdf.format(new Date()))
						.append(Constant.FILE_EPARATOR).toString();
				if (image0 != null && !image0.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image0,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image1 != null && !image1.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image1,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image2 != null && !image2.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image2,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image3 != null && !image3.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image3,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image4 != null && !image4.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image4,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updateOrderPricePlan");
			logger.error("", e);
		}

		JSONObject jsonObject = null;
		try {
			params.put("picturePaths", picturePaths);
			params.put("voicePaths", voicePaths);
			jsonObject = customOrderService.updateOrderPricePlan(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updateOrderPricePlan",
					params.toString());
			jsonObject = new ResultJSONObject("updateOrderPricePlan_exception",
					"修改报价方案失败");
			logger.error("修改报价方案失败", e);
		}
		return jsonObject;
	}

	/**
	 * 发单用户获取报价方案列表
	 * 
	 * @return
	 */
	@RequestMapping("/getPricePlanList")
	@SystemControllerLog(description = "发单用户获取报价方案列表")
	public @ResponseBody Object getPricePlanList(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/**
		 * 需要的参数： Long orderId, int pageNo,
		 * 
		 * @RequestParam(required = false) String orderBy
		 */
		JSONObject jsonObject = null;
		try {
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				jsonObject = mongoCustomOrderService.getPricePlanList(params);
			} else {
				jsonObject = customOrderService.getPricePlanList(params);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderMerchantPlan",
					params.toString());
			jsonObject = new ResultJSONObject("getOrderMerchantPlan_exception",
					"发单用户获取报价方案列表失败");
			logger.error("发单用户获取报价方案列表失败", e);
		}
		return jsonObject;
	}

	/**
	 * 发单用户获取报价方案列表
	 * 
	 * @return
	 */
	@RequestMapping("/getPricePlanListV1110")
	@SystemControllerLog(description = "发单用户获取报价方案列表")
	public @ResponseBody Object getPricePlanListV1110(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/**
		 * 需要的参数： Long orderId, int pageNo,
		 * 
		 * @RequestParam(required = false) String orderBy
		 */
		JSONObject jsonObject = null;
		try {
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				jsonObject = mongoCustomOrderService.getPricePlanList(params);
			} else {
				params.put("isOther", 1);
				jsonObject = customOrderService.getPricePlanList(params);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderMerchantPlan",
					params.toString());
			jsonObject = new ResultJSONObject("getOrderMerchantPlan_exception",
					"发单用户获取报价方案列表失败");
			logger.error("发单用户获取报价方案列表失败", e);
		}
		return jsonObject;
	}

	/**
	 * 发单用户选择报价方案
	 * 
	 * @return
	 */
	@RequestMapping("/choosePricePlan")
	@SystemControllerLog(description = "发单用户选择报价方案")
	public @ResponseBody Object choosePricePlan(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/**
		 * 需要的参数： String appType, Long merchantId, Long merchantPlanId, Long
		 * orderId
		 */
		JSONObject jsonObject = null;
		try {
//			String appType = StringUtil.null2Str(params.get("appType"));
//			String shopName = StringUtil.null2Str(params.get("shopName"));
//			Long orderId = StringUtil.nullToLong(params.get("orderId"));
//			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
//			Long merchantPlanId = StringUtil.nullToLong(params
//					.get("merchantPlanId"));
//			jsonObject = customOrderService.choosePricePlan(appType, shopName,
//					orderId, merchantId, merchantPlanId);
			jsonObject = new ResultJSONObject("002","请升级至最新版本。");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "choosePricePlan", params.toString());
			jsonObject = new ResultJSONObject("002","请升级至最新版本。");
			logger.error("发单用户选择报价方案失败", e);
		}
		return jsonObject;
	}

	/**
	 * 保存商品
	 * 
	 * @param params
	 * @param request
	 * @return Object
	 * @throws
	 */
	@RequestMapping("/saveOrderGoods")
	@SystemControllerLog(description = "保存商品")
	public @ResponseBody Object saveOrderGoods(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/*
		 * String appType, Long orderId, Long merchantId, Long merchantPlanId,
		 * String goodsIds, String goodsNums
		 */
		JSONObject jsonObject = null;
		try {
			String appType = StringUtil.null2Str(params.get("appType"));
			String shopName = StringUtil.null2Str(params.get("shopName"));
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			Long merchantPlanId = StringUtil.nullToLong(params
					.get("merchantPlanId"));
			String goodsIds = StringUtil.null2Str(params.get("goodsIds"));
			String goodsNums = StringUtil.null2Str(params.get("goodsNums"));
			jsonObject = customOrderService.saveOrderGoods(appType, shopName,
					orderId, merchantId, merchantPlanId, goodsIds, goodsNums);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "saveOrderGoods", params.toString());
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/**
	 * 查询报价方案 // * @param appType // * @param merchantId // * @param orderId
	 * 
	 * @return
	 */
	@RequestMapping("/detailOrderPlanInfo")
	@SystemControllerLog(description = "订单方案详情查询")
	public @ResponseBody Object detailOrderPlanInfo(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/**
		 * String appType, Long merchantId, Long orderId
		 */
		JSONObject jsonObject = null;
		try {
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				jsonObject = mongoCustomOrderService.getPricePlanDetail(params);
			} else {
				jsonObject = this.customOrderService.getPricePlanDetail(params);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "detailOrderPlanInfo",
					params.toString());
			jsonObject = new ResultJSONObject("detailOrderPlanInfo_exception",
					"订单方案详情查询失败");
			logger.error("订单方案详情查询失败", e);
		}
		return jsonObject;
	}

	/** 发单用户取消订单 */
	@RequestMapping("/cancelOrderForSender")
	@SystemControllerLog(description = "取消订单")
	public @ResponseBody Object cancelOrderForSender(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			/**
			 * Long orderId, String cancelReason, String remark
			 */
			jsonObject = customOrderService.cancelOrderForSender(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "cancelOrderForSender",
					params.toString());
			jsonObject = new ResultJSONObject("cancelOrderForSender_exception",
					"取消订单失败");
			logger.error("取消订单失败", e);
		}
		return jsonObject;
	}

	/** 发单用户删除订单 */
	@RequestMapping("/deleteOrderForSender")
	@SystemControllerLog(description = "删除订单")
	public @ResponseBody Object deleteOrderForSender(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			jsonObject = customOrderService.deleteOrderForSender(orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "deleteOrder", params.toString());
			jsonObject = new ResultJSONObject("deleteOrder_exception", "删除订单失败");
			logger.error("删除订单失败", e);
		}
		return jsonObject;
	}

	/** 接单用户屏蔽订单 */
	@RequestMapping("/shieldOrderForReceiver")
	@SystemControllerLog(description = "屏蔽订单")
	public @ResponseBody Object shieldOrderForReceiver(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			jsonObject = this.customOrderService.shieldOrderForReceiver(
					merchantId, orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "shieldOrderForReceiver",
					params.toString());
			jsonObject = new ResultJSONObject(
					"shieldOrderForReceiver_exception", "屏蔽订单失败");
			logger.error("屏蔽订单失败", e);
		}
		return jsonObject;
	}

	/** 发单用户获得订单的推送状态 */
	@RequestMapping("/getOrderPushTypeForSender")
	@SystemControllerLog(description = "获得订单的推送状态")
	public @ResponseBody Object getOrderPushType(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			jsonObject = customOrderService.getOrderPushTypeForSender(orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderPushTypeForSender",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getOrderPushTypeForSender_exception", "获得订单的推送状态失败");
			logger.error("获得订单的推送状态失败", e);
		}
		return jsonObject;
	}

	/** 获取用户此次订单可以使用的代金券列表 */
	@RequestMapping("/getOrderCanUsedVouchers")
	@SystemControllerLog(description = "获取用户在这个订单中可用使用的代金券")
	public @ResponseBody Object getOrderCanUsedVouchers(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long userId = StringUtil.nullToLong(params.get("userId"));
			Long serviceType = StringUtil.nullToLong(params.get("serviceType"));
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			// Double
			// orderPayPrice=StringUtil.nullToDouble(params.get("orderPayPrice"));//暂时不做
			int pageNo = StringUtil.nullToInteger(params.get("pageNo"));
			jsonObject = customOrderService.getOrderCanUsedVouchers(userId,
					serviceType, merchantId, pageNo);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserAvailablePayVouchersInfo",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getUserAvailablePayVouchersInfo_exception",
					"获取用户此次订单可以使用的代金券列表失败");
			logger.error("获取用户此次订单可以使用的代金券列表失败", e);
		}
		return jsonObject;
	}

	/** 确认订单的金额信息 */
	@RequestMapping("/confirmOrderPrice")
	@SystemControllerLog(description = "确认订单的金额信息")
	public @ResponseBody Object confirmOrder(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {

		JSONObject jsonObject = null;
		int result = 0;
		try {
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			Double price = Double.parseDouble(StringUtil.null2Str(params
					.get("price")));
			Object payType = params.get("payType");
			Double vouchersPrice = 0.0;
			if (params.get("vouchersPrice") != null) {
				vouchersPrice = Double.parseDouble(StringUtil.null2Str(params
						.get("vouchersPrice")));
			}
			Long vouchersId = StringUtil.nullToLong(params.get("vouchersId"));
			Double actualPrice = 0.0;
			if (params.get("actualPrice") != null) {
				actualPrice = Double.parseDouble(StringUtil.null2Str(params
						.get("actualPrice")));
			}

			result = customOrderService.confirmOrderPrice(orderId, merchantId,
					price, vouchersPrice, vouchersId, actualPrice, payType);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "confirmOrderPrice", params.toString());
			jsonObject = new ResultJSONObject("confirmOrderPrice_exception",
					"确认订单的金额信息失败");
			logger.error("确认订单的金额信息失败", e);
		}
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "确认订单金额成功");
		} else if (result == -1) {
			jsonObject = new ResultJSONObject("010", "确认订单金额失败");
		} else if (result == -2) {
			jsonObject = new ResultJSONObject("020", "服务商已修改交付内容，请重新提交");
		} else if (result == -3) {
			jsonObject = new ResultJSONObject("030", "服务商已修改交付内容，请重新提交");
		} else {
			jsonObject = new ResultJSONObject("009", "确认订单金额失败");
		}
		return jsonObject;
	}

	/** 确认订单的金额信息 */
	@RequestMapping("/confirmOrderPriceV160815")
	@SystemControllerLog(description = "确认订单的金额信息")
	public @ResponseBody Object confirmOrderV160815(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {

		JSONObject jsonObject = null;
		int result = 0;
		try {
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			Double price = Double.parseDouble(StringUtil.null2Str(params
					.get("price")));
			Object payType = params.get("payType");
			Double vouchersPrice = 0.0;
			if (params.get("vouchersPrice") != null) {
				vouchersPrice = Double.parseDouble(StringUtil.null2Str(params
						.get("vouchersPrice")));
			}
			Long vouchersId = StringUtil.nullToLong(params.get("vouchersId"));
			Double actualPrice = 0.0;
			if (params.get("actualPrice") != null) {
				actualPrice = Double.parseDouble(StringUtil.null2Str(params
						.get("actualPrice")));
			}

			jsonObject = customOrderService.confirmOrderPriceV160815(orderId,
					merchantId, price, vouchersPrice, vouchersId, actualPrice,
					payType);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "confirmOrderPrice", params.toString());
			jsonObject = new ResultJSONObject("confirmOrderPrice_exception",
					"确认订单的金额信息失败");
			logger.error("确认订单的金额信息失败", e);
		}

		return jsonObject;
	}

	/** 完成支付宝订单 */
	@RequestMapping("/finishAliPayOrder")
	@SystemControllerLog(description = "完成支付宝订单")
	public @ResponseBody Object finishAliPayOrder(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		int result = 0;
		Long orderId = StringUtil.nullToLong(params.get("orderId"));
		String tradeNo = StringUtil.null2Str(params.get("tradeNo"));
		try {
			JSONObject jsonObj = customOrderService.finishAliPayOrder(orderId,
					tradeNo, null,null);
			if (null != jsonObj.get("result")) {
				result = Integer
						.parseInt(String.valueOf(jsonObj.get("result")));
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "finishAliPayOrder", params.toString());
			jsonObject = new ResultJSONObject("finishAliPayOrder_exception",
					"完成支付宝订单信息");
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
	public @ResponseBody Object finishWeChatOrder(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		int result = 0;
		Long orderId = StringUtil.nullToLong(params.get("orderId"));
		String tradeNo = StringUtil.null2Str(params.get("tradeNo"));
		try {
			JSONObject jsonObj = customOrderService.finishWeChatOrder(orderId, tradeNo, null,null);
			if (null != jsonObj.get("result")) {
				result = Integer
						.parseInt(String.valueOf(jsonObj.get("result")));
			}
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "finishWeChatOrder", params.toString());
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "完成微信订单成功");
		} else if (result == -1)
			jsonObject = new ResultJSONObject("010", "订单已支付过，无需再支付");
		else {
			jsonObject = new ResultJSONObject("009", "完成微信订单失败");
		}
		return jsonObject;
	}

	/** 完成现金订单 需要根据填写的金额来确认订单金额 */
	@RequestMapping("/finishCashOrder")
	@SystemControllerLog(description = "完成现金订单")
	public @ResponseBody Object finishCashOrder(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/*
		 * String appType, Long orderId, Long merchantId, String price
		 */
		JSONObject jsonObject = null;
		try {
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			String price = StringUtil.null2Str(params.get("price"));
			Object payType = params.get("payType");
			int result = customOrderService.finishCashOrder(orderId,
					merchantId, price, payType);
			if (result > 0) {
				jsonObject = new ResultJSONObject("000", "完成现金订单");
			} else if (result == -1) {
				jsonObject = new ResultJSONObject("010", "订单已支付过，无需再支付");
			} else if (result == -3) {
				jsonObject = new ResultJSONObject("030", "服务商已修改交付内容，请重新提交");
			} else {
				jsonObject = new ResultJSONObject("009", "完成现金失败");
			}
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "finishCashOrder", params.toString());
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 用户直接结束订单 */
	@RequestMapping("/overOrder")
	@SystemControllerLog(description = "结束订单")
	public @ResponseBody Object overOrder(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		/*
		 * String appType, Long orderId, Long merchantId, String price
		 */
		JSONObject jsonObject = null;
		try {
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			String price = StringUtil.null2Str(params.get("price"));
			int result = customOrderService.overOrder(orderId, merchantId,
					price);
			if (result > 0) {
				jsonObject = new ResultJSONObject("000", "完成现金订单");
			} else if (result == -1) {
				jsonObject = new ResultJSONObject("010", "订单已支付过，无需再支付");
			} else if (result == -2) {
				jsonObject = new ResultJSONObject("020", "服务商已修改交付内容，请重新提交");
			} else {
				jsonObject = new ResultJSONObject("009", "完成现金失败");
			}
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "finishCashOrder", params.toString());
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 评价订单 */
	@RequestMapping("/evaluationOrder")
	@SystemControllerLog(description = "评价订单")
	public @ResponseBody Object evaluationOrder(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			String attitudeEvaluation = StringUtil.null2Str(params
					.get("attitudeEvaluation"));
			String qualityEvaluation = StringUtil.null2Str(params
					.get("qualityEvaluation"));
			String speedEvaluation = StringUtil.null2Str(params
					.get("speedEvaluation"));
			String textEvaluation = StringUtil.null2Str(params
					.get("textEvaluation"));
			// 图片上传
			// 保存附件
			List<String> paths = new ArrayList<String>();
			MultipartFile picture0 = null;
			MultipartFile picture1 = null;
			MultipartFile picture2 = null;
			MultipartFile picture3 = null;
			MultipartFile picture4 = null;
			// 转型为MultipartHttpRequest：

			if (request instanceof MultipartHttpServletRequest) {
				try {
					MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
					picture0 = multipartRequest.getFile("picture0");
					picture1 = multipartRequest.getFile("picture1");
					picture2 = multipartRequest.getFile("picture2");
					picture3 = multipartRequest.getFile("picture3");
					picture4 = multipartRequest.getFile("picture4");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					String filePath = new StringBuilder(
							Constant.USER_IMAGE_UPLOAD_BASE_PTAH)
							.append("evaluation")
							.append(Constant.FILE_EPARATOR)
							.append(sdf.format(new Date()))
							.append(Constant.FILE_EPARATOR).toString();

					filePath = new StringBuilder(
							Constant.USER_IMAGE_UPLOAD_BASE_PTAH)
							.append("evaluation")
							.append(Constant.FILE_EPARATOR)
							.append(sdf.format(new Date()))
							.append(Constant.FILE_EPARATOR).toString();
					if (picture0 != null && !picture0.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture0,
								filePath);
						if (StringUtils.isNotEmpty(resultPath)) {
							paths.add(resultPath);
						}
					}
					if (picture1 != null && !picture1.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture1,
								filePath);
						if (StringUtils.isNotEmpty(resultPath)) {
							paths.add(resultPath);
						}
					}
					if (picture2 != null && !picture2.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture2,
								filePath);
						if (StringUtils.isNotEmpty(resultPath)) {
							paths.add(resultPath);
						}
					}
					if (picture3 != null && !picture3.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture3,
								filePath);
						if (StringUtils.isNotEmpty(resultPath)) {
							paths.add(resultPath);
						}
					}
					if (picture4 != null && !picture4.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture4,
								filePath);
						if (StringUtils.isNotEmpty(resultPath)) {
							paths.add(resultPath);
						}
					}

				} catch (Exception e) {
					MsgTools.sendMsgOrIgnore(e, "saveMerchantServiceRecord");
					logger.error(" 保存服务记录:" + e.getMessage(), e);
				}
			}

			jsonObject = customOrderService.evaluationOrder(orderId,
					attitudeEvaluation, qualityEvaluation, speedEvaluation,
					textEvaluation, paths);
			String msg = String.valueOf(jsonObject.get("msg_comment"));
			try {
				customOrderService.writeToMQ("cplan.orderCommentExchange", msg);
				BusinessUtil.writeLog("c_plan_evaluation_mq_success", "订单ID:"
						+ orderId + "，消息体：" + msg);
			} catch (Exception e) {
				customOrderService.insertMQSendFailure(
						"cplan.orderCommentExchange", msg, orderId);
				BusinessUtil.writeLog("c_plan_evaluation_mq_failure", "订单ID:"
						+ orderId + "，消息体：" + msg);
				throw new ApplicationException(e, "send_evaluation_mq_failure",
						"订单评价消息MQ消息失败");
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "evaluationOrder", params.toString());
			jsonObject = new ResultJSONObject("evaluationOrder_exception",
					"用户评价订单失败");
			logger.error("用户评价订单失败", e);
		}

		return jsonObject;
	}

	/** 商户确认订单支付金额 */
	@RequestMapping("/confirmOrderPayPrice")
	@SystemControllerLog(description = "商户确认订单支付金额")
	public @ResponseBody Object confirmOrderPayPrice(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			jsonObject = customOrderService.confirmOrderPayPrice(merchantId,
					orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "confirmOrderPayPrice",
					params.toString());
			jsonObject = new ResultJSONObject("confirmOrderPayPrice_exception",
					"商户确认订单支付金额 失败");
			logger.error("商户确认订单支付金额 失败", e);
		}

		return jsonObject;
	}

	/** 商户服务完成后，保存服务记录 */
	@RequestMapping("/saveMerchantServiceRecord")
	@SystemControllerLog(description = "保存服务记录")
	public @ResponseBody Object saveMerchantServiceRecord(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {

			List<String> paths = new ArrayList<String>();
			// 保存附件
			MultipartFile picture0 = null;
			MultipartFile picture1 = null;
			MultipartFile picture2 = null;
			MultipartFile picture3 = null;
			MultipartFile picture4 = null;
			// 转型为MultipartHttpRequest：

			if (request instanceof MultipartHttpServletRequest) {
				try {
					MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
					picture0 = multipartRequest.getFile("picture0");
					picture1 = multipartRequest.getFile("picture1");
					picture2 = multipartRequest.getFile("picture2");
					picture3 = multipartRequest.getFile("picture3");
					picture4 = multipartRequest.getFile("picture4");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

					String filePath = new StringBuilder(
							Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH)
							.append("merchantServiceRecord")
							.append(Constant.FILE_EPARATOR)
							.append(sdf.format(new Date()))
							.append(Constant.FILE_EPARATOR).toString();
					if (picture0 != null && !picture0.isEmpty()) {

					}
					if (picture0 != null && !picture0.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture0,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}
					if (picture1 != null && !picture1.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture1,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}
					if (picture2 != null && !picture2.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture2,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}
					if (picture3 != null && !picture3.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture3,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}
					if (picture4 != null && !picture4.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture4,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}

				} catch (Exception e) {
					MsgTools.sendMsgOrIgnore(e, "saveMerchantServiceRecord",
							params.toString());
					logger.error(" 保存服务记录:" + e.getMessage(), e);
				}
			}
			jsonObject = customOrderService.saveMerchantServiceRecord(params,
					paths);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "saveMerchantServiceRecord");
			jsonObject = new ResultJSONObject(
					"saveMerchantServiceRecord_exception", "保存服务记录失败");
			logger.error("保存服务记录失败", e);
		}

		return jsonObject;
	}

	/** 商户服务完成后，修改服务记录 */
	@RequestMapping("/updateMerchantServiceRecord")
	@SystemControllerLog(description = "修改服务记录")
	public @ResponseBody Object updateMerchantServiceRecord(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {

			List<String> paths = new ArrayList<String>();
			// 保存附件
			MultipartFile picture0 = null;
			MultipartFile picture1 = null;
			MultipartFile picture2 = null;
			MultipartFile picture3 = null;
			MultipartFile picture4 = null;
			// 转型为MultipartHttpRequest：

			if (request instanceof MultipartHttpServletRequest) {
				try {
					MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
					picture0 = multipartRequest.getFile("picture0");
					picture1 = multipartRequest.getFile("picture1");
					picture2 = multipartRequest.getFile("picture2");
					picture3 = multipartRequest.getFile("picture3");
					picture4 = multipartRequest.getFile("picture4");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

					String filePath = new StringBuilder(
							Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH)
							.append("merchantServiceRecord")
							.append(Constant.FILE_EPARATOR)
							.append(sdf.format(new Date()))
							.append(Constant.FILE_EPARATOR).toString();
					if (picture0 != null && !picture0.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture0,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}
					if (picture1 != null && !picture1.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture1,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}
					if (picture2 != null && !picture2.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture2,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}
					if (picture3 != null && !picture3.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture3,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}
					if (picture4 != null && !picture4.isEmpty()) {
						String resultPath = BusinessUtil.fileUpload(picture4,
								filePath);
						if (resultPath.length() > 1) {
							paths.add(resultPath);
						}
					}

				} catch (Exception e) {
					MsgTools.sendMsgOrIgnore(e, "updateMerchantServiceRecord",
							params.toString());
					logger.error(" 修改服务记录:" + e.getMessage(), e);
				}
			}
			jsonObject = customOrderService.updateMerchantServiceRecord(params,
					paths);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updateMerchantServiceRecord");
			jsonObject = new ResultJSONObject(
					"updateMerchantServiceRecord_exception", "修改服务记录失败");
			logger.error("修改服务记录失败", e);
		}

		return jsonObject;
	}

	/** 查询服务记录 */
	@RequestMapping("/getMerchantServiceRecord")
	@SystemControllerLog(description = "查询服务记录")
	public @ResponseBody Object getMerchantServiceRecord(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				Long userId = StringUtil.nullToLong(params.get("userId"));
				if (userId.longValue() != 0) {
					jsonObject = mongoCustomOrderService
							.getMerchantServiceRecord(merchantId, orderId,
									userId);
				} else {
					logger.error("获取服务记录失败--缺少userId");
					throw new Exception("获取服务记录失败--缺少userId");
				}

			} else {
				jsonObject = customOrderService.getMerchantServiceRecord(
						merchantId, orderId);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getMerchantServiceRecord",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getMerchantServiceRecord_exception", "查询服务记录失败");
			logger.error("查询服务记录失败", e);
		}
		return jsonObject;
	}

	/** 查询服务记录 */
	@RequestMapping("/getMerchantServiceRecordForUser")
	@SystemControllerLog(description = "查询服务记录")
	public @ResponseBody Object getMerchantServiceRecordForUser(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long merchnatId = StringUtil.nullToLong(params.get("merchnatId"));
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			jsonObject = customOrderService.getMerchantServiceRecordForUser(
					merchnatId, orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getMerchantServiceRecord",
					params.toString());
			jsonObject = new ResultJSONObject(
					"getMerchantServiceRecord_exception", "查询服务记录失败");
			logger.error("查询服务记录失败", e);
		}
		return jsonObject;
	}

	/** 查看推送服务商列表 */
	@RequestMapping("/getPushMerchantInfos")
	@SystemControllerLog(description = "查询推送服务商列表")
	public @ResponseBody Object getPushMerchantInfos(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Integer pageNo = StringUtil.nullToInteger(params.get("pageNo"));
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			jsonObject = customOrderService.getPushMerchantInfos(orderId,
					pageNo, Constant.PAGESIZE);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getPushMerchantInfos",
					params.toString());
			jsonObject = new ResultJSONObject("getPushMerchantInfos_exception",
					"查询推送服务商列表");
			logger.error("查询推送服务商列表", e);
		}
		return jsonObject;
	}

	/** 查看订单详情及其附件 */
	@RequestMapping("/getOrderMoreDetails")
	@SystemControllerLog(description = "查看订单详情及其附件")
	public @ResponseBody Object getOrderMoreDetails(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			jsonObject = customOrderService.getOrderMoreDetail(orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getOrderMoreDetails",
					params.toString());
			jsonObject = new ResultJSONObject("getOrderMoreDetails_exception",
					"查看订单详情及其附件");
			logger.error("查看订单详情及其附件", e);
		}
		return jsonObject;
	}

	/** 未选择报价方案的过期订单处理 */
	@RequestMapping("/testNoChoosedExpired")
	@SystemControllerLog(description = "查询服务记录")
	public @ResponseBody void testNoChoosedExpired() {
		userOrderService.handleNoChoosedOrders();
	}

	/** 无报价方案的订单过期处理 */
	@RequestMapping("/testNoBidExpired")
	@SystemControllerLog(description = "查询服务记录")
	public @ResponseBody void testNoBidExpired() {
		userOrderService.handNoBidOrder();
	}

	/**
	 * 查询时间轴数据
	 */
	@RequestMapping("/getTimeline")
	@SystemControllerLog(description = "查询时间轴数据")
	public @ResponseBody JSONObject getTimeline(
			@RequestParam Map<String, Object> params) {
		JSONObject jsonObject = null;
		try {
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
			Long orderId = StringUtil.nullToLong(params.get("orderId"));
			int orderStatus = StringUtil.nullToInteger(params
					.get("orderStatus"));
			int type = StringUtil.nullToInteger(params.get("type"));
			if (params.containsKey("isHistory")
					&& StringUtil.nullToInteger(params.get("isHistory")) == 1) {
				jsonObject = mongoCustomOrderService.getTimeline(merchantId,
						orderId, orderStatus, type);
			} else {
				jsonObject = customOrderService.getTimeline(merchantId,
						orderId, orderStatus, type);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getTimeline", params.toString());
			jsonObject = new ResultJSONObject("getTimeline_exception",
					"查询时间轴数据");
			logger.error("查询时间轴数据", e);
		}
		return jsonObject;
	}

	// /** 临时测试用 */
	// @RequestMapping("/sendMQ")
	// public @ResponseBody JSONObject sendMQ(@RequestParam Map<String, Object>
	// params) {
	// JSONObject jsonObject = null;
	// Long orderId = StringUtil.nullToLong(params.get("orderId"));
	// String tradeNo = StringUtil.null2Str(params.get("tradeNo"));
	// try {
	// boolean returnBoolean = customOrderService.sendMQ(orderId, tradeNo, 1);
	// jsonObject = new ResultJSONObject("sendMQ_interface", returnBoolean ?
	// "success" : "error");
	// } catch (Exception e) {
	// logger.error("测试用sendMQ错误信息：", e);
	// }
	// return jsonObject;
	// }

	/**
	 * 获取服务端打包的支付参数
	 */
	@RequestMapping("/getPayParm")
	@SystemControllerLog(description = "获取服务端打包的支付参数")
	public @ResponseBody Object getPayParm(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		// type 1订单 2顾问号 3抢单金 4VIP
		JSONObject jsonObject = null;
		int result = 0;
		String outTradeNo = "";
		try {
			Integer clientType = StringUtil.nullToInteger(ClientParamUtil
					.getClientType(request));
			Integer type = StringUtil.nullToInteger(params.get("type"));
			if (type == 1) {
				Long orderId = StringUtil.nullToLong(params.get("orderId"));
				Long merchantId = StringUtil.nullToLong(params
						.get("merchantId"));
				Double price = Double.parseDouble(StringUtil.null2Str(params
						.get("price")));
				Object payType = params.get("payType");
				Double vouchersPrice = 0.0;
				if (params.get("vouchersPrice") != null) {
					vouchersPrice = Double.parseDouble(StringUtil
							.null2Str(params.get("vouchersPrice")));
				}
				Long vouchersId = StringUtil.nullToLong(params
						.get("vouchersId"));
				Double actualPrice = 0.0;
				if (params.get("actualPrice") != null) {
					actualPrice = Double.parseDouble(StringUtil.null2Str(params
							.get("actualPrice")));
				}
				jsonObject = customOrderService.confirmOrderPriceV160815(
						orderId, merchantId, price, vouchersPrice, vouchersId,
						actualPrice, payType);
				if (StringUtils.equals("000", jsonObject.get("resultCode")
						.toString())) {
					outTradeNo = jsonObject.get("orderNo").toString();
				} else {
					return jsonObject;
				}
			} else {
				String merchantId = StringUtil.nullToString(params
						.get("merchantId"));
				outTradeNo = merchantId;
			}
			String appType = StringUtil.null2Str(params.get("appType"));
			if (StringUtil.isNullStr(appType)) {
				appType = "0";
			}
			Integer paymentType = StringUtil.nullToInteger(params
					.get("paymentType"));
			Integer employeeNumber = StringUtil.nullToInteger(params
					.get("employeeNumber"));
			Double totalFee = StringUtil.nullToDouble(params.get("totalFee"));
			String subject = StringUtil.nullToString(params.get("subject"));
			System.out.println("outTradeNo=" + outTradeNo + "employeeNumber="
					+ employeeNumber + "appType=" + appType);
			jsonObject = this.customOrderService.getPayParm(type, outTradeNo,
					totalFee, paymentType, subject, employeeNumber, appType,
					clientType, 0, 0l, "0",0.0,"0");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "testMerchantPlan", params.toString());
			jsonObject = new ResultJSONObject("003", "获取服务端打包的支付参数异常");
		}
		return jsonObject;
	}

	/**
	 * 获取服务端打包的支付参数(加密)
	 */
	@RequestMapping("/getPaySecretParm")
	@SystemControllerLog(description = "获取服务端打包的支付参数(加密)")
	public @ResponseBody Object getPaySecretParm(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {

		// type 1订单 2顾问号 3抢单金 4:vip（VIP）
		// 1订单 2emloyee_num顾问号 3order_push抢单金 4:vip（VIP）5consultant私人助理
		// 6merchant签约特权 7hosting托管代运营
		JSONObject jsonObject = null;
		int result = 0;
		String outTradeNo = "";
		Integer paymentType = 0;
		try {
			Integer clientType = StringUtil.nullToInteger(ClientParamUtil
					.getClientType(request));
			Integer type = StringUtil.nullToInteger(params.get("type"));
            String appType = StringUtil.null2Str(params.get("appType"));
			if (type == 1) {
				Long orderId = StringUtil.nullToLong(params.get("orderId"));
				Long merchantId = StringUtil.nullToLong(params
						.get("merchantId"));
				Double price = Double.parseDouble(StringUtil.null2Str(params
						.get("price")));
				Object payType = params.get("payType");
				Double vouchersPrice = 0.0;
				if (params.get("vouchersPrice") != null) {
					vouchersPrice = Double.parseDouble(StringUtil
							.null2Str(params.get("vouchersPrice")));
				}
				Long vouchersId = StringUtil.nullToLong(params
						.get("vouchersId"));
				Double actualPrice = 0.0;
				if (params.get("actualPrice") != null) {
					actualPrice = Double.parseDouble(StringUtil.null2Str(params
							.get("actualPrice")));
				}
				jsonObject = customOrderService.confirmOrderPriceV160815(
						orderId, merchantId, price, vouchersPrice, vouchersId,
						actualPrice, payType);
				if (StringUtils.equals("000", jsonObject.get("resultCode")
						.toString())) {
					outTradeNo = jsonObject.get("orderNo").toString();
				} else {
					return jsonObject;
				}
				//区分新老支付
                appType="0";
			} else {
				String merchantId = StringUtil.nullToString(params
						.get("merchantId"));
				outTradeNo = merchantId;
			}
			Integer pkgId = StringUtil.nullToInteger(params.get("pkgId"));
			Long userId = StringUtil.nullToLong(params.get("userId"));

			double consumePrice=StringUtil.nullToDouble(params.get("consumePrice"));
			String inviteCode= StringUtil.null2Str(params.get("inviteCode"));
			if (StringUtil.isNullStr(appType)) {
				appType = "0";
			}
			if (StringUtil.isNullStr(inviteCode)) {
				inviteCode = "0";
			}
			// 1:支付宝2：微信3：银联6:h5微信
			paymentType = StringUtil.nullToInteger(params.get("paymentType"));
			Integer employeeNumber = StringUtil.nullToInteger(params
					.get("employeeNumber"));
			Double totalFee = StringUtil.nullToDouble(params.get("totalFee"));
			String subject = StringUtil.nullToString(params.get("subject"));
			String openId = StringUtil.nullToString(params.get("openId"));
			System.out.println("outTradeNo=" + outTradeNo + "employeeNumber="
					+ employeeNumber + "appType=" + appType);
			jsonObject = this.customOrderService.getPayParm(type, outTradeNo,
					totalFee, paymentType, subject, employeeNumber, appType,
					clientType, pkgId, userId, openId,consumePrice,inviteCode);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "testMerchantPlan", params.toString());
			jsonObject = new ResultJSONObject("003", "获取服务端打包的支付参数异常");
		}
		System.out.println("第一次jsonObject" + jsonObject);
		JSONObject respJson = new ResultJSONObject("000", "获取服务端打包的支付参数成功");
		String userKey = params.get("userKey").toString();
		String encryptedResp = "";
		try {
			if (6 == paymentType) {
				encryptedResp = AESUtil4WX.encrypt(userKey,
						jsonObject.toString());
			} else {
				encryptedResp = AESUtil.parseByte2HexStr(AESUtil.encrypt(
						jsonObject.toString(), userKey));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			encryptedResp = "";
		}
		respJson.put("encryptedResp", encryptedResp);
		return respJson;
	}
	
	/**
	 * 获取服务端打包的支付参数(加密)
	 */
	@RequestMapping("/getPaySecretParmV1110")
	@SystemControllerLog(description = "获取服务端打包的支付参数(加密)")
	public @ResponseBody Object getPaySecretParmV1110(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {

		// type 1订单 2顾问号 3抢单金 4:vip（VIP）
		// 1订单 2emloyee_num顾问号 3order_push抢单金 4:vip（VIP）5consultant私人助理
		// 6merchant签约特权 7hosting托管代运营20王牌计划
		JSONObject jsonObject = null;
		int result = 0;
		String outTradeNo = "";
		Integer paymentType = 0;
        System.out.println("支付参数" + params);
		Long orderId = StringUtil.nullToLong(params.get("orderId"));
		try {
			Integer clientType = StringUtil.nullToInteger(ClientParamUtil
					.getClientType(request));
			Integer type = StringUtil.nullToInteger(params.get("type"));
			String appType = StringUtil.null2Str(params.get("appType"));
			if (type == 1) {
				Long merchantId = StringUtil.nullToLong(params
						.get("merchantId"));
				//是否有未支付记录
				Map<String, Object> para=new HashMap<String, Object>();
				para.put("orderId", orderId);
				Map<String, Object> resultMap = myMerchantService.selectpaymentStatus(para);
				if (null != resultMap) {
					Integer paymentStatus = StringUtil.nullToInteger(params.get("payment_status"));
					if(paymentStatus>0){
					jsonObject = new ResultJSONObject("002", "订单有未支付成功记录");
					return jsonObject;
					}
				}
				//20161029选择报价方案
				String shopName = StringUtil.null2Str(params.get("shopName"));
				Long merchantPlanId = StringUtil.nullToLong(params.get("merchantPlanId"));
				jsonObject=customOrderService.choosePricePlanV1110(appType, shopName, orderId, merchantId, merchantPlanId,true);
				if (StringUtils.equals("000", jsonObject.get("resultCode").toString())) {
				} else {
					return jsonObject;
				}
				outTradeNo =customOrderService.getOrderNoByOrderId(orderId);
                appType="1";
			} else if(20==type){
                //王牌计划
                String userId = StringUtil.nullToString(params.get("userId"));
                outTradeNo = userId;
            } else {
                String merchantId = StringUtil.nullToString(params
                        .get("merchantId"));
                outTradeNo = merchantId;
            }

			
			Integer pkgId = StringUtil.nullToInteger(params.get("pkgId"));
			Long userId = StringUtil.nullToLong(params.get("userId"));
			
			double consumePrice=StringUtil.nullToDouble(params.get("consumePrice"));
			String inviteCode= StringUtil.null2Str(params.get("inviteCode"));
			if (StringUtil.isNullStr(appType)) {
				appType = "0";
			}
			if (StringUtil.isNullStr(inviteCode)) {
				inviteCode = "0";
			}
			// 1:支付宝2：微信3：银联6:h5微信
			paymentType = StringUtil.nullToInteger(params.get("paymentType"));
			Integer employeeNumber = StringUtil.nullToInteger(params
					.get("employeeNumber"));
			Double totalFee = StringUtil.nullToDouble(params.get("totalFee"));
			String subject = StringUtil.nullToString(params.get("subject"));
			String openId = StringUtil.nullToString(params.get("openId"));
			System.out.println("outTradeNo=" + outTradeNo + "employeeNumber="
					+ employeeNumber + "appType=" + appType);
			jsonObject = this.customOrderService.getPayParm(type, outTradeNo,
					totalFee, paymentType, subject, employeeNumber, appType,
					clientType, pkgId, userId, openId,consumePrice,inviteCode);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "testMerchantPlan", params.toString());
			jsonObject = new ResultJSONObject("003", "获取服务端打包的支付参数异常");
		}
		System.out.println("第一次jsonObject" + jsonObject);
		JSONObject respJson = new ResultJSONObject("000", "获取服务端打包的支付参数成功");
		String userKey = params.get("userKey").toString();
		String encryptedResp = "";
		try {
			if (6 == paymentType||7==paymentType) {
				encryptedResp = AESUtil4WX.encrypt(userKey,
						jsonObject.toString());
			} else {
				encryptedResp = AESUtil.parseByte2HexStr(AESUtil.encrypt(
						jsonObject.toString(), userKey));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			encryptedResp = "";
		}
		
		Map<String, Object> para=new HashMap<String, Object>();
		para.put("orderId", orderId);
		para.put("actualPrice", "0.01");
		//将订单金额写入order_info表中
		customOrderService.updateOrderActualPrice(para);
		
		respJson.put("encryptedResp", encryptedResp);
		return respJson;
	}

	// // 测试用
	// @RequestMapping("insertMQSendFailure")
	// public @ResponseBody JSONObject insertMQSendFailure(@RequestParam
	// Map<String, Object> params) {
	// JSONObject jsonObject;
	// try {
	// Long orderId = StringUtil.nullToLong(params.get("orderId"));
	// String tradeNo = StringUtil.null2Str(params.get("tradeNo"));
	// int type = StringUtil.nullToInteger(params.get("type"));
	// jsonObject = customOrderService.insertMQSendFailure(orderId, tradeNo,
	// type);
	// } catch (Exception e) {
	// jsonObject = new ResultJSONObject("insertMQSendFailure_exception",
	// "插入MQ发送失败记录");
	// }
	// return jsonObject;
	// }
	//
	// // 测试用
	// @RequestMapping("updateMQSendFailure")
	// public @ResponseBody JSONObject updateMQSendFailure(@RequestParam
	// Map<String, Object> params) {
	// JSONObject jsonObject;
	// try {
	// Long id = StringUtil.nullToLong(params.get("id"));
	// jsonObject = customOrderService.updateMQSendFailure(id);
	// } catch (Exception e) {
	// jsonObject = new ResultJSONObject("updateMQSendFailure_exception",
	// "修改MQ发送失败记录");
	// }
	// return jsonObject;
	// }

	// 测试用
	@RequestMapping("/sendMQ")
	public @ResponseBody JSONObject sendMQ() {
		JSONObject jsonObject = new ResultJSONObject();
		try {
			Map<String, Object> parms = new HashMap<String, Object>();
			parms.put("transAmount", 20);
			parms.put("thirdTransSeq", "4006062001201608171540183081");
			parms.put("remark", "服务类型：住家保姆");
			parms.put("orderType", 1);
			parms.put("orderPayType", 1);
			parms.put("merchantUserPhone", "15895200736"); // 老板手机号
			parms.put("transSeq", 299911);
			parms.put("orderTime", "2016-08-17 11:31:35");
			parms.put("payTime", "2016-08-17 11:34:54");
			parms.put("serviceName", "美容");
			parms.put("merchantUserId", 146484180278009476L); // 商户用户Id
			parms.put("merchantName", "凯哥美容美发");
			parms.put("userName", "");
			parms.put("merchantId", 146804649225295862L);
			parms.put("orderPrice", 68);
			parms.put("payUserhone", "18260714419");
			parms.put("payUserId", 147140457641286286L);
			parms.put("orderId", "14714046953562635991");

			// String source =
			// "{\"transAmount\":36,\"thirdTransSeq\":\"4007902001201608171577290385\",\"remark\":\"服务类型：面包\",\"orderType\":\"1\",\"orderPayType\":1, \"merchantUserPhone\":\"15819653177\",\"transSeq\":307376,\"orderTime\":\"2016-08-17 19:47:32\",\"payTime\":\"2016-08-17 19:48:22\",\"serviceName\":\"面包\", \"merchantUserId\":146804572538705733,\"merchantName\":\"莱茵河畔蛋糕店\",\"userName\":\"\",\"merchantId\":146804578752235966,\"orderPrice\":36, \"payUserhone\":\"18613183366\",\"payUserId\":147143437866705958,\"orderId\":\"14714344527284224465\"}";

			// String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(source,
			// "367937E1967092280C56077755E4C65B"));

			String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(
					JSONObject.toJSONString(parms),
					"367937E1967092280C56077755E4C65B"));
			System.out.println("加密：" + msg);
			jsonObject.put("msg", msg);
			customOrderService.writeToMQ("opay.orderExchange", msg);

			String decrypt = AESUtil.decrypt(msg,
					"367937E1967092280C56077755E4C65B");
			System.out.println("解密:" + decrypt);
			jsonObject.put("decrypt", decrypt);

		} catch (Exception e) {
			jsonObject.put("error", "发送失败：" + e);
		}
		return jsonObject;
	}

	// 测试用
	@RequestMapping("/getMQ")
	public @ResponseBody JSONObject getMQ() throws Exception {
		return customOrderService.getMQ("305399",
				"4004762001201608171546873729", 1); // 1:微信 2:支付宝
	}

	/**
	 * 修改商户营业类型
	 */
	@RequestMapping("/updMerchantBusinessType")
	@SystemControllerLog(description = "修改商户营业类型")
	public @ResponseBody Object updMerchantBusinessType(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			jsonObject = customOrderService.updMerchantBusinessType(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updMerchantBusinessType",
					params.toString());
			jsonObject = new ResultJSONObject(
					"updMerchantBusinessType_exception", "修改商户营业类型失败");
		}
		return jsonObject;

	}

	/**
	 * 判断是否属于历史接口
	 *
	 * @param params
	 * @return
	 */
	private boolean isHistory(Map<String, Object> params) {
		return 1 == StringUtil.nullToInteger(params.get("isHistory"));
	}

    /**
     * 保存收支明细
     *
     * @throws Exception
     */
    @RequestMapping("/tobeConfirmed")
    @SystemControllerLog(description = "保存收支明细")
    public @ResponseBody Object tobeConfirmed(@RequestParam Map<String, Object> params) throws Exception {
        JSONObject jsonObject = null;
        try {
        	//20161029选择报价方案
        	String appType=StringUtil.null2Str(params.get("appType"));
        	Long orderId=StringUtil.nullToLong(params.get("businessId"));
        	Long merchantId=StringUtil.nullToLong(params.get("merchantId"));
			String shopName = StringUtil.null2Str(params.get("shopName"));
			Long merchantPlanId = StringUtil.nullToLong(params.get("merchantPlanId"));
		
			Integer tradeType = StringUtil.nullToInteger(params.get("tradeType"));
			
			if (tradeType.byteValue()==9){
					//纯消费金支付
				 	jsonObject = customOrderService.payBuyConsumerMoney(params);
				
			}else{
				 	jsonObject = customOrderService.tobeConfirmed(params);
			}
        } catch (Exception e) {
            jsonObject = new ResultJSONObject("tobeConfirmed_exception", "保存收支明细失败");
            logger.error("保存收支明细失败", e);
        }
        return jsonObject;
    }

    /**
     * 查询收支明细
     *
     * @throws Exception
     */
    @RequestMapping("/findPaymentByOrderId")
    @SystemControllerLog(description = "保存收支明细")
    public @ResponseBody Object findPaymentByOrderId(@RequestParam Map<String, Object> params) throws Exception {
        JSONObject jsonObject = null;
        try {
            Long orderId = StringUtil.nullToLong(params.get("orderId"));
            jsonObject = customOrderService.findPaymentByOrderId(orderId);
        } catch (Exception e) {
            jsonObject = new ResultJSONObject("findPaymentByOrderId_exception", "查询收支明细失败");
            logger.error("查询收支明细失败", e);
        }
        return jsonObject;
    }

    /**
     * 修改收支明细
     *
     * @throws Exception
     */
    @RequestMapping("/updatePaymentStatus")
    @SystemControllerLog(description = "修改收支明细")
    public @ResponseBody Object updatePaymentStatus(@RequestParam Map<String, Object> params) throws Exception {
        JSONObject jsonObject = null;
        try {
            jsonObject = customOrderService.updatePaymentStatus(params);
        } catch (Exception e) {
            jsonObject = new ResultJSONObject("updatePaymentStatus_exception", "修改收支明细失败");
            logger.error("修改收支明细失败", e);
        }
        return jsonObject;
    }
	
	/**
	 * 
	 *  ｛商户订单确认服务完成｝
	 *  @return
	 *  @author Liuxingwen
	 *  @created 2016年10月25日 下午5:30:57
	 *  @lastModified       
	 *  @history 
	 *  /customOrder/CompletionOrder?orderId=383657
	 */
	@RequestMapping("/CompletionOrder")
	@SystemControllerLog(description = "商户订单确认服务完成")
	public @ResponseBody Object completionOrder(Long orderId,Long merchantId,Long merchantPlanId,String appType,String shopName) {
		JSONObject jsonObject = null;
		try {
			
			if(null==orderId || null==merchantId || null ==merchantPlanId || null ==appType ||"".equals(appType)|| null ==shopName ||"".equals(shopName))
			{
				jsonObject = new ResultJSONObject("002", "商户订单确认服务完成失败，参数传入有空值，请核查");
				return jsonObject;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("orderId", orderId);// orderId
			map.put("orderStatus", 5);// 用户端订单的状态 1-新预约 2-待选择 3-已确认 4-已完成 5-支付完成 6-订单已过期 7-无效订单',
			map.put("merchantId", merchantId);
			map.put("merchantPlanId", merchantPlanId);
			map.put("appType", appType);
			map.put("shopName", shopName);

			jsonObject =customOrderService.completionOrder(map);
			
		} catch (Exception e) {
			if("002".equals(e.getMessage().trim()))
			{
				jsonObject = new ResultJSONObject("002", "商户订单状态不符合直接进行订单确认！");
			}
			else if("003".equals(e.getMessage().trim()))
			{
				jsonObject = new ResultJSONObject("003", "商户订单确认服务完成未成功，核实订单是否存在");
			}
			else {
				jsonObject = new ResultJSONObject("001", "商户订单确认服务完成失败");
			}
			logger.error("商户订单确认服务完成失败", e);
		}
		return jsonObject;
	}


    /**
     * 增值服务购买待确认
     *
     * @throws Exception
     */
    @RequestMapping("/incServiceToBeConfirmed")
    @SystemControllerLog(description = "增值服务购买待确认")
    public @ResponseBody Object incServiceToBeConfirmed(@RequestParam Map<String, Object> params) throws Exception {
        JSONObject jsonObject = null;
        try {
            jsonObject = customOrderService.incServiceToBeConfirmed(params);
        } catch (Exception e) {
            jsonObject = new ResultJSONObject("incServiceToBeConfirmed_exception", "增值服务购买待确认失败");
            logger.error("增值服务购买待确认失败", e);
        }
        return jsonObject;
    }

}
