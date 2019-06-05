package com.shanjin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IMyMerchantService;
import com.shanjin.service.IUserOrderService;

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
	@Resource
	private IUserOrderService userOrderService;

	@Resource
	private IMyMerchantService myMerchantService;

	@Resource
	private ICommonService commonService;

	/**
	 * 获取用户基础订单列表
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getBasicOrderList")
	@SystemControllerLog(description = "获取用户订单列表")
	public @ResponseBody Object getBasicOrderList(@RequestParam(required = false) String appType, Long userId, @RequestParam(required = false) String orderStatus, int pageNo) throws InterruptedException {
		JSONObject jsonObject = new ResultJSONObject();
		jsonObject = userOrderService.getBasicOrderList(appType, userId, orderStatus, pageNo);
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
		JSONObject jsonObject = new ResultJSONObject();
		jsonObject = userOrderService.getDetialOrderInfo(appType, orderId, serviceType);
		return jsonObject;
	}

	/** 保存订单 */
	@RequestMapping("/saveOrder")
	@SystemControllerLog(description = "保存订单")
	public @ResponseBody Object saveOrder(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		JSONObject jsonObject = null;
		List<String> voicePaths = new ArrayList<String>();
		List<String> picturePaths = new ArrayList<String>();
		String appType = (String) params.get("appType");
		String serviceType = (String) params.get("serviceType");
		String longitude = (String) params.get("longitude");
		String latitude = (String) params.get("latitude");

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
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			voice = multipartRequest.getFile("voice");
			picture0 = multipartRequest.getFile("picture0");
			picture1 = multipartRequest.getFile("picture1");
			picture2 = multipartRequest.getFile("picture2");
			picture3 = multipartRequest.getFile("picture3");
			picture4 = multipartRequest.getFile("picture4");

			if (voice != null && !voice.isEmpty()) {
				String resultPath = this.saveFile(voice, "voice");
				if (resultPath.length() > 1) {
					voicePaths.add(resultPath);
				}
			}
			if (picture0 != null && !picture0.isEmpty()) {
				String resultPath = this.saveFile(picture0, "image");
				if (resultPath.length() > 1) {
					picturePaths.add(resultPath);
				}
			}
			if (picture1 != null && !picture1.isEmpty()) {
				String resultPath = this.saveFile(picture1, "image");
				if (resultPath.length() > 1) {
					picturePaths.add(resultPath);
				}
			}
			if (picture2 != null && !picture2.isEmpty()) {
				String resultPath = this.saveFile(picture2, "image");
				if (resultPath.length() > 1) {
					picturePaths.add(resultPath);
				}
			}
			if (picture3 != null && !picture3.isEmpty()) {
				String resultPath = this.saveFile(picture3, "image");
				if (resultPath.length() > 1) {
					picturePaths.add(resultPath);
				}
			}
			if (picture4 != null && !picture4.isEmpty()) {
				String resultPath = this.saveFile(picture4, "image");
				if (resultPath.length() > 1) {
					picturePaths.add(resultPath);
				}
			}

		} catch (Exception e) {

		}

		jsonObject = this.userOrderService.saveOrder(appType, serviceType, params, voicePaths, picturePaths);
		return jsonObject;
	}

	/**
	 * 保存文件，获取文件路径
	 * */
	@Transactional(rollbackFor = Exception.class)
	private String saveFile(MultipartFile file, String type) {
		String refilePath = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String filePath = "/upload/userInfo/" + type + "/order/" + sdf.format(new Date()) + "/";
		refilePath = BusinessUtil.fileUpload(file, filePath);
		return refilePath;
	}

	/** 获取订单供应商列表 */
	@RequestMapping("/getOrderMerchantPlan")
	@SystemControllerLog(description = "获取订单供应商列表")
	public @ResponseBody Object getOrderMerchantPlan(String appType, Long orderId, int pageNo) {
		JSONObject jsonObject = null;
		jsonObject = this.userOrderService.getOrderMerchantPlan(appType, orderId, pageNo);
		return jsonObject;
	}

	/** 为订单选择商户方案 */
	@RequestMapping("/chooseMerchantPlan")
	@SystemControllerLog(description = "用户选择订单的供应商")
	public @ResponseBody Object chooseMerchantPlan(String appType, Long merchantId, Long merchantPlanId, Long orderId) {
		JSONObject jsonObject = null;
		int result = this.userOrderService.chooseMerchantPlan(appType, merchantId, merchantPlanId, orderId);
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
		List<Map<String, Object>> assessOrder = this.userOrderService.getAssessOrder(orderId);
		jsonObject = new ResultJSONObject("000", "获取订单评价信息成功");
		jsonObject.put("assessOrder", assessOrder);
		return jsonObject;
	}

	/** 评价订单 */
	@RequestMapping("/assessOrder")
	@SystemControllerLog(description = "用户评价订单")
	public @ResponseBody Object assessOrder(String appType, Long orderId, String attitudeEvaluation, String qualityEvaluation, String speedEvaluation, String textEvaluation) {
		JSONObject jsonObject = null;
		int result = this.userOrderService.assessOrder(appType, orderId, attitudeEvaluation, qualityEvaluation, speedEvaluation, textEvaluation);
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "更新评价成功");
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
		jsonObject = this.userOrderService.getUserAvailablePayVouchersInfo(appType, userId, serviceType, merchantId, pageNo);
		return jsonObject;
	}

	/** 确认订单的金额信息 */
	@RequestMapping("/confirmOrderPrice")
	@SystemControllerLog(description = "确认订单的金额信息")
	public @ResponseBody Object confirmOrder(Long orderId, Long merchantId, Double price, Long vouchersId, Double actualPrice) {
		JSONObject jsonObject = null;
		int result = this.userOrderService.confirmOrderPrice(orderId, merchantId, price, vouchersId, actualPrice);
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
		int result = this.userOrderService.finishAliPayOrder(appType, orderId);
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "完成支付宝订单成功");
		} else {
			jsonObject = new ResultJSONObject("009", "完成支付宝订单失败");
		}
		return jsonObject;
	}

	/** 完成微信订单 */
	@RequestMapping("/finishWeChatOrder")
	@SystemControllerLog(description = "完成微信订单")
	public @ResponseBody Object finishWeChatOrder(String appType, Long orderId) {
		JSONObject jsonObject = null;
		int result = this.userOrderService.finishWeChatOrder(appType, orderId);
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "完成微信订单成功");
		} else {
			jsonObject = new ResultJSONObject("009", "完成微信订单失败");
		}
		return jsonObject;
	}

	/** 完成现金订单 需要根据填写的金额来确认订单金额 */
	@RequestMapping("/finishCashOrder")
	@SystemControllerLog(description = "完成现金订单")
	public @ResponseBody Object finishCashOrder(String appType, Long orderId, Long merchantId, String price) {
		JSONObject jsonObject = null;
		int result = this.userOrderService.finishCashOrder(appType, orderId, merchantId, price);
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "完成现金订单");
		} else {
			jsonObject = new ResultJSONObject("009", "完成现金失败");
		}
		return jsonObject;
	}

	/** 推送信息给商户版 */
	@RequestMapping("/pushMessageToMer")
	@SystemControllerLog(description = "推送信息给商户版")
	public @ResponseBody Object pushMessageToMer(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		JSONObject jsonObject = this.userOrderService.pushMessageToListForSHB(request);
		return jsonObject;
	}

	/** 推送信息给用户版 */
	@RequestMapping("/pushMessageToUsr")
	@SystemControllerLog(description = "推送信息给用户版")
	public @ResponseBody Object pushMessageToList(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		JSONObject jsonObject = this.userOrderService.pushMessageToListForYHB(request);
		return jsonObject;
	}

	/** 删除订单 */
	@RequestMapping("/deleteOrder")
	@SystemControllerLog(description = "删除订单")
	public @ResponseBody Object deleteOrder(Long orderId) {
		JSONObject jsonObject = null;
		int result = this.userOrderService.deleteOrder(orderId);
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
		int result = this.userOrderService.updateOrderStatus(orderId, "0");
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "取消订单成功");
		} else {
			jsonObject = new ResultJSONObject("009", "取消订单失败");
		}
		return jsonObject;
	}

	/** 获得订单的推送状态 */
	@RequestMapping("/getOrderPushType")
	@SystemControllerLog(description = "获得订单的推送状态")
	public @ResponseBody Object getOrderPushType(Long orderId) {
		JSONObject jsonObject = null;
		int result = this.userOrderService.getOrderPushType(orderId);
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
			ex.printStackTrace();
			jsonObject = new ResultJSONObject("105", "店铺信息加载失败");
		}
		return jsonObject;
	}

	/** 顾客评价 */
	@RequestMapping("/userEvaluation")
	@SystemControllerLog(description = "顾客评价")
	public @ResponseBody Object userEvaluation(String appType, Long merchantId, int pageNo) {
		return commonService.selectUserEvaluation(appType, merchantId, pageNo);
	}

}
