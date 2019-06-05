package com.shanjin.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.CheckDuplicateSubmission;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.exception.ApplicationException;
import com.shanjin.service.IMerchantOrderManageService;

/**
 * 商户版 订单管理
 */
@Controller
@RequestMapping("/merchantOrderManage")
public class OrderManageController {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(OrderManageController.class);
	@Reference
	private IMerchantOrderManageService merchantOrderManageService;

	/** 订单基础信息列表查询 */
	@RequestMapping("/basicOrderList")
	@SystemControllerLog(description = "订单基础信息列表查询")
	public @ResponseBody Object basicOrderList(Long catalogId, Long userId,Long merchantId, Long serviceType, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.merchantOrderManageService.selectBasicOrderList(catalogId, userId,merchantId, serviceType, pageNo);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"basicOrderList");
			jsonObject = new ResultJSONObject("basicOrderList_exception", "订单基础信息列表查询失败");
			logger.error("订单基础信息列表查询失败", e);
		}
		return jsonObject;
	}

	/** 订单详情信息查询 */
	@RequestMapping("/detailOrderInfo")
	@SystemControllerLog(description = "订单详情信息查询")
	public @ResponseBody Object detailOrderInfo(String appType, Long merchantId, Long orderId, Long serviceType) {
		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getMerchantOrderServiceByAppType(appType).selectDetailOrderInfo(appType, merchantId, orderId, serviceType);
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"detailOrderInfo");
			
			jsonObject = new ResultJSONObject("detailOrderInfo_exception", "订单详情信息查询失败");
			logger.error("订单详情信息查询失败", e);
		}
		return jsonObject;
	}

	/** 立即抢单提供方案 */
	@RequestMapping("/immediately")
	@CheckDuplicateSubmission(args = "merchantId,orderId", type = "immediately")
	@SystemControllerLog(description = "立即抢单提供方案")
	public @ResponseBody Object provideScheme(String appType, String phone, Long merchantId, Long orderId, BigDecimal planPrice, BigDecimal discountPrice, Long planType, String detail,
			HttpServletRequest request) {
		JSONObject verifyResult = this.merchantOrderManageService.provideSchemeVerify(appType, merchantId, orderId);
		if (!StringUtils.equals("000", verifyResult.get("resultCode").toString())) {
			return verifyResult;
		}
		// 如果优惠为空，则等于方案价
		if (discountPrice == null) {
			discountPrice = planPrice;
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
				String filePath = new StringBuilder(Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH).append("order").append(Constant.FILE_EPARATOR).append(sdf.format(new Date()))
						.append(Constant.FILE_EPARATOR).toString();
				if (voice != null && !voice.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(voice, filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						voicePaths.add(resultPath);
					}
				}

				filePath = new StringBuilder(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("order").append(Constant.FILE_EPARATOR).append(sdf.format(new Date())).append(Constant.FILE_EPARATOR)
						.toString();
				if (image0 != null && !image0.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image0, filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image1 != null && !image1.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image1, filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image2 != null && !image2.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image2, filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image3 != null && !image3.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image3, filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
				if (image4 != null && !image4.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(image4, filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						picturePaths.add(resultPath);
					}
				}
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"immediately");
			logger.error("", e);
		}

		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getMerchantOrderServiceByAppType(appType).immediately(appType, phone, merchantId, orderId, planPrice, discountPrice, planType, detail, voicePaths,
					picturePaths);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e,"immediately");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	
	
	/** 立即抢单提供方案 --------------自定义报价方案版本 ----Revoke*/
	@RequestMapping("/immediatelyV316")
	@CheckDuplicateSubmission(args = "merchantId,orderId", type = "immediately")
	@SystemControllerLog(description = "立即抢单提供方案")
	public @ResponseBody Object provideSchemeV316(String appType, String phone, Long merchantId, Long orderId, BigDecimal planPrice, BigDecimal discountPrice, Long planType, String detail,
			String[] imgNames,String[] voiceNames,String[] attachNames,
			HttpServletRequest request) {
		JSONObject verifyResult = this.merchantOrderManageService.provideSchemeVerify(appType, merchantId, orderId);
		if (!StringUtils.equals("000", verifyResult.get("resultCode").toString())) {
			return verifyResult;
		}
		// 如果优惠为空，则等于方案价
		if (discountPrice == null) {
			discountPrice = planPrice;
		}
		List<Map<String,String>> voicePaths = new ArrayList<Map<String,String>>();
		List<Map<String,String>> picturePaths = new ArrayList<Map<String,String>>();

		MultipartFile[] voiceFile = null;
		MultipartFile[] imageFile = null;
		try {
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				
				SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
				
				if (imgNames!=null && imgNames.length>0){
						String filePath = new StringBuilder(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("order").append(Constant.FILE_EPARATOR).append(format.format(new Date())).append(Constant.FILE_EPARATOR)
							.toString();
						imageFile = new MultipartFile[imgNames.length];
						int i=0;
						for(String imgName:imgNames){
							imageFile[i] =  multipartRequest.getFile("imgName");
							if (imageFile[i] != null && !imageFile[i].isEmpty()) {
								String resultPath = BusinessUtil.fileUpload(imageFile[i], filePath);
								if (StringUtils.isNotEmpty(resultPath)) {
									Map<String,String> imgFileAndPathMap = new HashMap<String,String>();
									imgFileAndPathMap.put(imgName, resultPath);
									picturePaths.add(imgFileAndPathMap);
								}
							}
						}
				}
				
				
				if (voiceNames!=null && voiceNames.length>0){
					String filePath = new StringBuilder(Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH).append("order").append(Constant.FILE_EPARATOR).append(format.format(new Date()))
							.append(Constant.FILE_EPARATOR).toString();
					voiceFile = new MultipartFile[voiceNames.length];
					int i=0;
					for(String voiceName:voiceNames){
						voiceFile[i] =  multipartRequest.getFile("voiceName");
						if (voiceFile[i] != null && !voiceFile[i].isEmpty()) {
							String resultPath = BusinessUtil.fileUpload(voiceFile[i], filePath);
							if (StringUtils.isNotEmpty(resultPath)) {
								Map<String,String> imgFileAndPathMap = new HashMap<String,String>();
								imgFileAndPathMap.put(voiceName, resultPath);
								voicePaths.add(imgFileAndPathMap);
							}
						}
						
					}
				}
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"immediately");
			logger.error("", e);
		}

		JSONObject jsonObject = null;
		try {
			jsonObject = AppOrderSvrManager.getMerchantOrderServiceByAppType(appType).immediatelyV316(appType, phone, merchantId, orderId, planPrice, discountPrice, planType, detail, voicePaths,
					picturePaths);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e,"immediately");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	
	
	
	/** 隐藏订单 */
	@RequestMapping("/shieldOrder")
	@SystemControllerLog(description = "隐藏订单")
	public @ResponseBody Object shieldOrder(String appType, Long merchantId, Long orderId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.merchantOrderManageService.shieldOrder(merchantId, orderId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e,"shieldOrder");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 推送场合，根据订单ID查询订单的基础信息 */
	@RequestMapping("/pushBasicOrder")
	@SystemControllerLog(description = "推送场合，根据订单ID查询订单的基础信息")
	public @ResponseBody Object basicOrderForPush(String appType, String orderId) {
		JSONObject jsonObject = null;
		try {
			Long lorderId = Long.parseLong(orderId.toString().trim());
			jsonObject = this.merchantOrderManageService.selectBasicOrderForPush(appType, lorderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"pushBasicOrder");
			jsonObject = new ResultJSONObject("pushBasicOrder_exception", "推送场合，根据订单ID查询订单的基础信息失败");
			logger.error("推送场合，根据订单ID查询订单的基础信息失败", e);
		}
		return jsonObject;
	}

	/** 推送场合，根据订单ID查询订单的详细信息 */
	@RequestMapping("/pushDetailOrder")
	@SystemControllerLog(description = "推送场合，根据订单ID查询订单的详细信息")
	public @ResponseBody Object detailOrderForPush(String appType, Long merchantId, Long orderId, Long serviceType) {
		return detailOrderInfo(appType, merchantId, orderId, serviceType);
	}

	/** 订单方案详情查询 */
	@RequestMapping("/detailOrderPlanInfo")
	@SystemControllerLog(description = "订单方案详情查询")
	public @ResponseBody Object detailOrderPlanInfo(String appType, Long merchantId, Long orderId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.merchantOrderManageService.selectDetailOrderPlanInfo(appType, merchantId, orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"detailOrderPlanInfo");
			jsonObject = new ResultJSONObject("detailOrderPlanInfo_exception", "订单方案详情查询失败");
			logger.error("订单方案详情查询失败", e);
		}
		return jsonObject;
	}

	/** 订单商品详情查询 */
	@RequestMapping("/detailOrderGoodsInfo")
	@SystemControllerLog(description = "订单商品详情查询")
	public @ResponseBody Object detailOrderGoodsInfo(String appType, Long merchantId, Long orderId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.merchantOrderManageService.selectDetailOrderGoodsInfo(appType, merchantId, orderId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"detailOrderGoodsInfo");
			jsonObject = new ResultJSONObject("detailOrderGoodsInfo_exception", "订单商品详情查询失败");
			logger.error("订单商品详情查询失败", e);
		}
		return jsonObject;
	}

	/**
	 * 测试用接口-认证通过
	 * 
	 * @return Object
	 * @throws
	 */
	@RequestMapping("/testAuthPass")
	@SystemControllerLog(description = "测试用接口-认证通过")
	public @ResponseBody Object testAuthPass(Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.merchantOrderManageService.testAuthPass(merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"testAuthPass");
			jsonObject = new ResultJSONObject("testAuthPass_excetion", "测试用接口-认证通过异常");
		}
		return jsonObject;
	}

	@RequestMapping("/testMerchantPlan")
	@SystemControllerLog(description = "测试用接口-认证通过")
	public @ResponseBody Object testMerchantPlan(Long merchantId, Long orderId, String appType) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.merchantOrderManageService.testMerchantPlan(merchantId, orderId, appType);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"testMerchantPlan");
			jsonObject = new ResultJSONObject("testAuthPass_excetion", "测试用接口-认证通过异常");
		}
		return jsonObject;
	}

}
