package com.shanjin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.UploadFile;
import com.shanjin.common.aspect.CheckDuplicateSubmission;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.*;
import com.shanjin.exception.ApplicationException;
import com.shanjin.outServices.aliOss.AliOssUtil;
import com.shanjin.service.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.shanjin.common.util.StringUtil.filterFourCharString;

/*import com.shanjin.opay.api.bean.Header;*/

/**
 * 商户版，我的店铺信息展示
 */
@Controller
@RequestMapping("/myMerchant")
public class MyMerchantController {

	@Reference
	private IMerchantInvoiceService merchantInvoiceService;

	@Reference
	private IMyMerchantService myMerchantService;

	@Reference
	private ICommonService commonService;

	@Reference
	private IMerchantOrderManageService merchantManagerService;

	/*
	 * @Reference private ITransOutService transoutService;
	 */

	@Reference
	private IMyIncomeService myIncomeService;

	private static final Logger logger = Logger
			.getLogger(OrderManageController.class);

	/** 获取商户验证码 */
	@RequestMapping("/getVerificationCode")
	@SystemControllerLog(description = "获取商户验证码")
	@CheckDuplicateSubmission(args = "appType,phone", type = "getVerificationCode")
	public @ResponseBody Object getVerificationCode(String appType,
			String phone, String clientId) {
		JSONObject jsonObject = null;
		// 获取商户的公网IP
		String ip = IPutil.getIpAddr(ServletUtil.getRequest());
		try {
			jsonObject = this.myMerchantService.getVerificationCode(appType,
					phone, clientId, ip);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getVerificationCode");

			jsonObject = new ResultJSONObject("getVerificationCode_exception",
					"获取商户验证码失败");
			logger.error("获取商户验证码失败", e);
		}
		return jsonObject;
	}

	/** 获取商户语音验证码 */
	@RequestMapping("/getVoiceVerificationCode")
	@SystemControllerLog(description = "获取商户语音验证码")
	@CheckDuplicateSubmission(args = "appType,phone", type = "getVoiceVerificationCode")
	public @ResponseBody Object getVoiceVerificationCode(String appType,
			String phone, String clientId) {
		JSONObject jsonObject = null;
		// 获取商户的公网IP
		String ip = IPutil.getIpAddr(ServletUtil.getRequest());
		try {
			jsonObject = this.myMerchantService.getVoiceVerificationCode(
					appType, phone, clientId, ip);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getVoiceVerificationCode");

			jsonObject = new ResultJSONObject("getVerificationCode_exception",
					"获取商户验证码失败");
			logger.error("获取商户验证码失败", e);
		}
		return jsonObject;
	}

	/** 验证商户验证码 */
	@RequestMapping("/validateVerificationCode")
	@SystemControllerLog(description = "验证商户验证码")
	@CheckDuplicateSubmission(args = "appType,phone", type = "validateVerificationCode")
	public @ResponseBody Object validateVerificationCode(String appType,
			String phone, String verificationCode, String clientId,
			String clientType, String pushId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.validateVerificationCode(
					appType, phone, verificationCode, clientId, clientType,
					pushId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "validateVerificationCode");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 开店 */
	@RequestMapping("/openShop")
	@SystemControllerLog(description = "开店")
	@CheckDuplicateSubmission(args = "appType, userId, phone", type = "openShop")
	public @ResponseBody Object openShop(String appType, String userId,
			String phone) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.openShop(appType, userId, phone);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "openShop");
			jsonObject = new ResultJSONObject("openShop_exception", "开店失败");
			logger.error("开店失败", e);
		}
		return jsonObject;
	}

	// /** 我的店铺 */
	// @RequestMapping("/myMerchantShow")
	// @SystemControllerLog(description = "我的店铺")
	// @CheckDuplicateSubmission(args = "appType,phone,merchantId", type =
	// "myMerchantShow")
	// public @ResponseBody Object myMerchantShow(String appType, String phone,
	// Long merchantId) {
	// JSONObject jsonObject = null;
	// try {
	// jsonObject = this.myMerchantService.selectMyMerchant(appType, phone,
	// merchantId);
	// } catch (Exception e) {
	// MsgTools.sendMsgOrIgnore(e, "myMerchantShow");
	//
	// jsonObject = new ResultJSONObject("myMerchantShow_exception",
	// "我的店铺信息查询失败");
	// logger.error("我的店铺信息查询失败", e);
	// }
	// return jsonObject;
	// }

	/** 我的店铺V2.3版本 */
	@RequestMapping("/myMerchantV23")
	@SystemControllerLog(description = "我的店铺")
	@CheckDuplicateSubmission(args = "appType,phone,merchantId", type = "myMerchantV23")
	public @ResponseBody Object myMerchantV23(String appType, String phone,
			Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectMyMerchantV23(appType,
					phone, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "myMerchantV23");

			jsonObject = new ResultJSONObject("my_merchant_query_exception",
					"我的店铺信息查询失败");
			logger.error("我的店铺信息查询失败", e);
		}
		return jsonObject;
	}

	/** 我的店铺V2.4版本 */
	@RequestMapping("/myMerchantV24")
	@SystemControllerLog(description = "我的店铺")
	@CheckDuplicateSubmission(args = "appType,phone,merchantId", type = "myMerchantV24")
	public @ResponseBody Object myMerchantV24(String appType, String phone,
			Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectMyMerchantV24(appType,
					phone, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "myMerchantV24");

			jsonObject = new ResultJSONObject("my_merchant_query_exception",
					"我的店铺信息查询失败");
			logger.error("我的店铺信息查询失败", e);
		}
		return jsonObject;
	}

	/** 店铺详细信息 */
	@RequestMapping("/merchantDetailInfo")
	@SystemControllerLog(description = "店铺详细信息")
	@CheckDuplicateSubmission(args = "merchantId", type = "merchantDetailInfo")
	public @ResponseBody Object merchantDetailInfo(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		Long merchantId = Long
				.parseLong((String) (params.get("merchantId") == null ? "0"
						: params.get("merchantId")));
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.merchantDetailInfo(merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "merchantDetailInfo");

			jsonObject = new ResultJSONObject(
					"merchant_detail_info_query_exception", "店铺详细信息查询失败");
			logger.error("店铺详细信息查询失败", e);
		}
		return jsonObject;
	}

	/** 店铺详细信息2 */
	@RequestMapping("/merchantDetailInfo_2")
	@SystemControllerLog(description = "店铺详细信息2")
	@CheckDuplicateSubmission(args = "merchantId", type = "merchantDetailInfo_2")
	public @ResponseBody Object merchantDetailInfo_2(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		Long merchantId = Long
				.parseLong((String) (params.get("merchantId") == null ? "0"
						: params.get("merchantId")));
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.merchantDetailInfo_2(merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "merchantDetailInfo_2");

			jsonObject = new ResultJSONObject(
					"merchant_detail_info_query_exception", "店铺详细信息查询失败");
			logger.error("店铺详细信息查询失败", e);
		}
		return jsonObject;
	}

	/** 魅力值信息查询 */
	@RequestMapping("/charmValueInfo")
	@SystemControllerLog(description = "魅力值信息查询")
	@CheckDuplicateSubmission(args = "appType,merchantId", type = "charmValueInfo")
	public @ResponseBody Object charmValueInfo(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.charmValueInfo(appType,
					merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "charmValueInfo");

			jsonObject = new ResultJSONObject("charm_value_info_query_failure",
					"魅力值信息查询失败");
			logger.error("魅力值信息查询失败", e);
		}
		return jsonObject;
	}

	/** 商户编辑页信息查询 */
	@RequestMapping("/merchantEditPageInfo")
	@SystemControllerLog(description = "商户编辑页信息查询")
	@CheckDuplicateSubmission(args = "merchantId", type = "merchantEditPageInfo")
	public @ResponseBody Object merchantEditPageInfo(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		Long merchantId = Long
				.parseLong((String) (params.get("merchantId") == null ? "0"
						: params.get("merchantId")));
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.merchantEditPageInfo(merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "merchantEditPageInfo");
			jsonObject = new ResultJSONObject(
					"merchant_edit_page_info_query_failure", "商户编辑页信息查询失败");
			logger.error("商户编辑页信息查询失败", e);
		}
		return jsonObject;
	}

	/** 店铺名称 保存 */
	@RequestMapping("/merchantNameSave")
	@SystemControllerLog(description = "店铺名称保存")
	public @ResponseBody Object merchantNameSave(String appType,
			Long merchantId, String name,
			@RequestParam(required = false) String detail,
			@RequestParam(required = false) String microWebsiteUrl,
			@RequestParam(required = false) String invitationCode) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.updateNameAndDetail(appType,
					merchantId, name, detail, microWebsiteUrl, invitationCode);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "merchantNameSave");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 店铺详情 保存 */
	@RequestMapping("/updateMerchantDetail")
	@SystemControllerLog(description = "店铺详情保存")
	public @ResponseBody Object updateMerchantDetail(Long merchantId,
			@RequestParam(required = false) String detail) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.updateMerchantDetail(
					merchantId, filterFourCharString(detail));
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "merchantDetailSave");
			jsonObject = new ResultJSONObject("merchantDetailSave_failure",
					"店铺详情保存失败");
			logger.error("店铺详情保存失败", e);
		}
		return jsonObject;
	}

	/** 店铺图标 保存 */
	@RequestMapping("/merchantIconSave")
	@SystemControllerLog(description = "店铺图标保存")
	public @ResponseBody Object merchantIconSave(String appType,
			Long merchantId, MultipartFile iconFile) {
		String resultPath = "";
		if (!iconFile.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String filePath = new StringBuilder(
					Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH)
					.append("portrait").append(Constant.FILE_EPARATOR)
					.append(sdf.format(new Date()))
					.append(Constant.FILE_EPARATOR).toString();
			resultPath = BusinessUtil.fileUpload(iconFile, filePath);
		}
		if (StringUtils.isEmpty(resultPath)) {
			return new ResultJSONObject("shop_icon_save_failure", "店铺图标保存失败");
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.updateMerchantIcon(appType,
					merchantId, resultPath);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "merchantIconSave");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** vip背景列表 */
	@RequestMapping("/selectVipBackgroundUrlList")
	@SystemControllerLog(description = "vip背景列表")
	public @ResponseBody Object selectVipBackgroundUrlList(String appType,
			Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectVipBackgroundUrlList(
					appType, merchantId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject(
					"select_Vip_Background_Url_List_exception", "vip背景列表查询异常");
			logger.error("vip背景列表查询失败", e);
		}
		return jsonObject;
	}

	/** 设置vip背景 */
	@RequestMapping("/setVipBackgroundUrl")
	@SystemControllerLog(description = "设置vip背景")
	public @ResponseBody Object setVipBackgroundUrl(String appType,
			Long merchantId,
			@RequestParam(required = false) String vipBackgroundUrl,
			HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			String resultPath = Constant.EMPTY;

			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest vipBackgrounImage = (MultipartHttpServletRequest) request;
				MultipartFile image = null;
				image = vipBackgrounImage.getFile("vipBackgrounImage");
				if (image != null && !image.isEmpty()) {
					String filePath = new StringBuilder(
							Constant.MERCHANT_VIP_BG_UPLOAD_BASE_PTAH)
							.append("vipImage").append(Constant.FILE_EPARATOR)
							.toString();
					resultPath = BusinessUtil.fileUpload(image, filePath);
				} else {
					if (StringUtil.isEmpty(vipBackgroundUrl)) {
						throw new Exception("请选择vip模板或者上传图片。");
					}
				}
			} else {
				if (StringUtil.isEmpty(vipBackgroundUrl)) {
					throw new Exception("请选择vip模板或者上传图片。");
				}
				resultPath = vipBackgroundUrl.substring(vipBackgroundUrl
						.indexOf(Constant.MERCHANT_VIP_BG_UPLOAD_BASE_PTAH),
						vipBackgroundUrl.length());
			}

			jsonObject = this.myMerchantService.setVipBackgroundUrl(merchantId,
					resultPath);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject(
					"set_Vip_Background_Url_exception", "设置vip背景异常");
			logger.error("vip背景设置失败", e);
		}
		return jsonObject;
	}

	/** 服务项目 （商户已经选择的项目显示选中） */
	@RequestMapping("/serviceItem")
	@SystemControllerLog(description = "服务项目查询")
	public @ResponseBody Object serviceItem(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			if (appType.startsWith("yxt") || appType.startsWith("ams")) {
				jsonObject = this.myMerchantService
						.selectMerchantServiceForChooseMultilevel(appType,
								merchantId);
			} else {
				jsonObject = this.myMerchantService
						.selectMerchantServiceForChoose(appType, merchantId);
			}
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "serviceItem");

			jsonObject = new ResultJSONObject("serviceItem_exception",
					"服务项目 （商户已经选择的项目显示选中）失败");
			logger.error("服务项目 （商户已经选择的项目显示选中）失败", e);
		}
		return jsonObject;
	}

	/** 服务项目 保存 */
	@RequestMapping("/serviceItemSave")
	@SystemControllerLog(description = "服务项目保存")
	public @ResponseBody Object serviceItemSave(String appType,
			String serviceTypes, Long merchantId, String phone) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.insertMerchantServiceType(
					appType, serviceTypes, merchantId, phone);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "serviceItemSave");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/**
	 * 服务查询 （商户已经选择的项目显示选中）
	 * 
	 * // * @param merchantId 商户ID // * @param merchantType 商户类型 1 行业商户 2个性（人）商户
	 * // * @param appType 行业
	 * 
	 **/
	@RequestMapping("/serviceQuery")
	@SystemControllerLog(description = "服务查询")
	public @ResponseBody Object serviceQuery(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		Long merchantId = Long
				.parseLong((String) (params.get("merchantId") == null ? "0"
						: params.get("merchantId")));
		String merchantType = (String) params.get("merchantType");
		String appType = (String) params.get("appType");
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectMerchantServiceForChoose(
					merchantId, merchantType, appType);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "serviceQuery");

			jsonObject = new ResultJSONObject("service_query_exception",
					"服务查询 （商户已经选择的项目显示选中）失败");
			logger.error("服务查询 （商户已经选择的项目显示选中）失败", e);
		}
		return jsonObject;
	}

	/**
	 * 服务 保存
	 * 
	 * // * @param merchantId 商户ID // * @param serviceTypeIds 服务类型ID（服务类型表主键ID）
	 * 
	 **/
	@RequestMapping("/serviceSave")
	@SystemControllerLog(description = "服务保存")
	public @ResponseBody Object serviceSave(
			@RequestParam Map<String, Object> params, HttpServletRequest request) {
		Long merchantId = Long
				.parseLong((String) (params.get("merchantId") == null ? "0"
						: params.get("merchantId")));
		String serviceTypeIds = (String) params.get("serviceTypeIds");
		String appType = (String) params.get("appType");
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.insertMerchantServiceType(
					merchantId, serviceTypeIds, appType);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "serviceSave");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 个人申请的服务的保存 */
	@RequestMapping("/personApplyServiceSave")
	@SystemControllerLog(description = "个人申请的服务的保存 ")
	public @ResponseBody Object personApplyServiceSave(String serviceNames,
			Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.personApplyServiceSave(
					serviceNames, merchantId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "personApplyServiceSave");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 个人申请的服务的查询 */
	@RequestMapping("/personApplyServiceQuery")
	@SystemControllerLog(description = "个人申请的服务的查询 ")
	public @ResponseBody Object personApplyServiceQuery(Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.personApplyServiceQuery(merchantId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "personApplyServiceQuery");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 个人服务（技能）搜索 */
	@RequestMapping("/personServiceSearch")
	@SystemControllerLog(description = "个人服务（技能）搜索 ")
	public @ResponseBody Object personServiceSearch(String keyword) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.personServiceSearch(keyword);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "personServiceSearch");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 个人申请的服务的删除 */
	@RequestMapping("/personApplyServiceDelete")
	@SystemControllerLog(description = "个人申请的服务的删除 ")
	public @ResponseBody Object personApplyServiceDelete(Long merchantId,
			Long id, int isAudit) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.personApplyServiceDelete(
					merchantId, id, isAudit);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "personApplyServiceDelete");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 省市 保存 */
	@RequestMapping("/citySave")
	@SystemControllerLog(description = "省市保存")
	public @ResponseBody Object citySave(String appType, Long merchantId,
			String province, String city) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.citySave(appType, merchantId,
					province, city);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "citySave");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 地理位置 保存 */
	@RequestMapping("/locationSave")
	@SystemControllerLog(description = "地理位置保存")
	public @ResponseBody Object locationSave(String appType, Long merchantId,
			String province, String city, Double latitude, Double longitude,
			@RequestParam(required = false) String mapType, String location,
			@RequestParam(required = false) String detailAddress) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.updateLocation(appType,
					merchantId, province, city, latitude, longitude, mapType,
					location, detailAddress);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "locationSave");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 联系方式 保存 */
	@RequestMapping("/contactSave")
	@SystemControllerLog(description = "联系方式保存")
	public @ResponseBody Object contactSave(String appType, Long merchantId,
			String telephone) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.contactSave(appType,
					merchantId, telephone);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "contactSave");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 当前代金券 */
	@RequestMapping("/currentVouchersInfo")
	@SystemControllerLog(description = "当前代金券查询")
	public @ResponseBody Object currentVouchersInfo(String appType,
			Long merchantId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectCurrentVouchersInfo(
					appType, merchantId, pageNo);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "currentVouchersInfo");

			jsonObject = new ResultJSONObject("currentVouchersInfo_exception",
					"当前代金券失败");
			logger.error("当前代金券失败", e);
		}
		return jsonObject;
	}

	/** 历史代金券 */
	@RequestMapping("/historyVouchersInfo")
	@SystemControllerLog(description = "历史代金券查询")
	public @ResponseBody Object historyVouchersInfo(String appType,
			Long merchantId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectHistoryVouchersInfo(
					appType, merchantId, pageNo);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "historyVouchersInfo");

			jsonObject = new ResultJSONObject("historyVouchersInfo_exception",
					"历史代金券失败");
			logger.error("历史代金券失败", e);
		}
		return jsonObject;
	}

	/** 删除历史代金券 */
	@RequestMapping("/deleteHistoryVouchers")
	@SystemControllerLog(description = "删除历史代金券")
	public @ResponseBody Object deleteHistoryVouchers(String appType,
			String userPhone, Long id, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.deleteHistoryVouchers(appType,
					userPhone, id, merchantId);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "deleteHistoryVouchers");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 代金券 添加代金券（代金券信息加载） */
	@RequestMapping("/vouchersTypeShow")
	@SystemControllerLog(description = "代金券信息加载")
	public @ResponseBody Object vouchersTypeShow(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectVouchersType(appType,
					merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "vouchersTypeShow");

			jsonObject = new ResultJSONObject("vouchersTypeShow_exception",
					"代金券 添加代金券（代金券信息加载）失败");
			logger.error("代金券 添加代金券（代金券信息加载）失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取代金券剩余数量
	 */
	@RequestMapping("/surplusVouchersNumber")
	@SystemControllerLog(description = "获取代金券剩余数量")
	public @ResponseBody Object getSurplusVouchersNumber(Long vouchersId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.getSurplusVouchersNumber(vouchersId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "surplusVouchersNumber");

			jsonObject = new ResultJSONObject(
					"surplusVouchersNumber_exception", "获取代金券剩余数量失败");
			logger.error("获取代金券剩余数量失败", e);
		}
		return jsonObject;
	}

	/** 添加代金券 确定 */
	@RequestMapping("/vouchersPermissionsSave")
	@SystemControllerLog(description = "添加代金券")
	public @ResponseBody Object vouchersPermissionsSave(String appType,
			Long merchantId, Long vouchersId, String count, String cutoffTime) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.insertMerchantVouchersPermissions(appType, merchantId,
							vouchersId, count, cutoffTime);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "vouchersPermissionsSave");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 商户查看顾客评价 */
	@RequestMapping("/userEvaluation")
	@SystemControllerLog(description = "商户查看顾客评价")
	public @ResponseBody Object userEvaluation(String appType, Long merchantId,
			Long orderId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.commonService.selectUserEvaluation(appType,
					merchantId, orderId, pageNo);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "userEvaluation");

			jsonObject = new ResultJSONObject("userEvaluation_exception",
					"查看评价失败");
			logger.error("查看评价失败", e);
		}
		return jsonObject;
	}

	/** 认证信息查询 */
	@RequestMapping("/applyAuth")
	@SystemControllerLog(description = "认证信息查询")
	public @ResponseBody Object applyAuth(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectApplyAuthInfo(appType,
					merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "applyAuth");

			jsonObject = new ResultJSONObject("applyAuth_exception", "认证信息查询失败");
			logger.error("认证信息查询失败", e);
		}
		return jsonObject;
	}

	/** 取消认证 */
	@RequestMapping("/cancelAuth")
	@SystemControllerLog(description = "取消认证")
	public @ResponseBody Object cancelAuth(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.cancelAuth(appType, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "cancelAuth");

			jsonObject = new ResultJSONObject("applyAuth_exception", "取消认证失败");
			logger.error("取消认证失败", e);
		}
		return jsonObject;
	}

	/** 提交认证申请 */
	@RequestMapping("/submitApplyAuth")
	@SystemControllerLog(description = "提交认证申请")
	@CheckDuplicateSubmission(args = "appType,merchantId", type = "myMerchantShow")
	public @ResponseBody Object submitApplyAuth(String appType,
			Long merchantId, int authType, MultipartFile authPicFile) {

		String resultPath = "";
		if (!authPicFile.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String filePath = new StringBuilder(
					Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("auth")
					.append(Constant.FILE_EPARATOR)
					.append(sdf.format(new Date()))
					.append(Constant.FILE_EPARATOR).toString();
			resultPath = BusinessUtil.fileUpload(authPicFile, filePath);
		}
		if (StringUtils.isEmpty(resultPath)) {
			return new ResultJSONObject("submit_apply_auth_failure", "认证图片上传失败");
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.insertMerchantAuth(appType,
					merchantId, authType, resultPath);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "submitApplyAuth");

			BusinessUtil.deleteOssFile(resultPath);
			jsonObject = new ResultJSONObject("submitApplyAuth_exception",
					"提交认证申请失败");
			logger.error("提交认证申请失败：", e);
		}
		return jsonObject;
	}

	/** 查询相册 */
	@RequestMapping("/selectAlbum")
	@SystemControllerLog(description = "查询相册")
	public @ResponseBody Object selectAlbum(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.selectAlbum(appType, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "selectAlbum");

			jsonObject = new ResultJSONObject("selectAlbum_exception", "查询相册失败");
			logger.error("查询相册失败", e);
		}
		return jsonObject;
	}

	/** 新建相册 */
	@RequestMapping("/insertAlbum")
	@SystemControllerLog(description = "新建相册")
	public @ResponseBody Object insertAlbum(String appType, Long merchantId,
			String albumName) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.insertAlbum(appType,
					merchantId, albumName);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "insertAlbum");

			jsonObject = new ResultJSONObject("insertAlbum_exception", "新建相册失败");
			logger.error("新建相册失败", e);
		}
		return jsonObject;
	}

	/** 重命名相册 */
	@RequestMapping("/updateAlbum")
	@SystemControllerLog(description = "重命名相册")
	public @ResponseBody Object updateAlbum(String appType, Long merchantId,
			Long albumId, String albumName) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.updateAlbum(appType, albumId,
					albumName);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "updateAlbum");

			jsonObject = new ResultJSONObject("updateAlbum_exception",
					"重命名相册失败");
			logger.error("重命名相册失败", e);
		}
		return jsonObject;
	}

	/** 删除相册 */
	@RequestMapping("/deleteAlbum")
	@SystemControllerLog(description = "删除相册")
	public @ResponseBody Object deleteAlbum(String appType, Long merchantId,
			Long albumId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.deleteAlbum(appType,
					merchantId, albumId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "deleteAlbum");

			jsonObject = new ResultJSONObject("deleteAlbum_exception", "删除相册失败");
			logger.error("删除相册失败", e);
		}
		return jsonObject;
	}

	/** 查询相片 */
	@RequestMapping("/selectPhoto")
	@SystemControllerLog(description = "查询相片")
	public @ResponseBody Object selectPhoto(String appType, Long merchantId,
			Long albumId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectPhoto(appType, albumId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "selectPhoto");

			jsonObject = new ResultJSONObject("selectPhoto_exception", "查询相片失败");
			logger.error("查询相片失败", e);
		}
		return jsonObject;
	}

	/** 新建相片 */
	@RequestMapping("/insertPhoto")
	@SystemControllerLog(description = "新建相片")
	public @ResponseBody Object insertPhoto(String appType, Long merchantId,
			Long albumId, MultipartFile iconFile) {
		JSONObject jsonObject = this.myMerchantService.insertPhotoVerify(
				appType, merchantId, albumId);
		if (jsonObject != null) {
			return jsonObject;
		}
		String resultPath = "";
		if (!iconFile.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String filePath = new StringBuilder(
					Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("album")
					.append(Constant.FILE_EPARATOR)
					.append(sdf.format(new Date()))
					.append(Constant.FILE_EPARATOR).toString();
			resultPath = BusinessUtil.fileUpload(iconFile, filePath);
		}
		if (StringUtils.isEmpty(resultPath)) {
			return new ResultJSONObject("new_photo_failure", "相片上传失败");
		}
		try {
			jsonObject = this.myMerchantService.insertPhoto(appType,
					merchantId, albumId, resultPath);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "insertPhoto");

			BusinessUtil.deleteOssFile(resultPath);
			jsonObject = new ResultJSONObject("insertPhoto_exception", "新建相片失败");
			logger.error("新建相片失败", e);
		}
		return jsonObject;
	}

	/** 新建多个相片 */
	@RequestMapping("/insertPhotos")
	@SystemControllerLog(description = "新建相片")
	public @ResponseBody Object insertPhotos(String appType, Long merchantId,
			Long albumId, HttpServletRequest request) {
		JSONObject jsonObject = null;
		List<String> photoPaths = new ArrayList<String>();
		MultipartFile photo0 = null;
		MultipartFile photo1 = null;
		MultipartFile photo2 = null;
		MultipartFile photo3 = null;
		MultipartFile photo4 = null;

		try {
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				photo0 = multipartRequest.getFile("photo0");
				photo1 = multipartRequest.getFile("photo1");
				photo2 = multipartRequest.getFile("photo2");
				photo3 = multipartRequest.getFile("photo3");
				photo4 = multipartRequest.getFile("photo4");

				int newPhotoNum = 0;
				if (photo0 != null && !photo0.isEmpty()) {
					newPhotoNum++;
					if (photo1 != null && !photo1.isEmpty()) {
						newPhotoNum++;
						if (photo2 != null && !photo2.isEmpty()) {
							newPhotoNum++;
							if (photo3 != null && !photo3.isEmpty()) {
								newPhotoNum++;
								if (photo4 != null && !photo4.isEmpty()) {
									newPhotoNum++;
								}
							}
						}
					}
				}
				if (newPhotoNum == 0) {
					return new ResultJSONObject("new_photo_failure", "新建相片失败");
				}
				jsonObject = this.myMerchantService.insertPhotosVerify(appType,
						merchantId, albumId, newPhotoNum);
				if (jsonObject != null) {
					return jsonObject;
				}

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				String filePath = new StringBuilder(
						Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH)
						.append("album").append(Constant.FILE_EPARATOR)
						.append(sdf.format(new Date()))
						.append(Constant.FILE_EPARATOR).toString();
				if (photo0 != null && !photo0.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(photo0,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						photoPaths.add(resultPath);
					}
				}
				if (photo1 != null && !photo1.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(photo1,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						photoPaths.add(resultPath);
					}
				}
				if (photo2 != null && !photo2.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(photo2,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						photoPaths.add(resultPath);
					}
				}
				if (photo3 != null && !photo3.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(photo3,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						photoPaths.add(resultPath);
					}
				}
				if (photo4 != null && !photo4.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(photo4,
							filePath);
					if (StringUtils.isNotEmpty(resultPath)) {
						photoPaths.add(resultPath);
					}
				}
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "insertPhotos");
			logger.error("", e);
		}
		if (photoPaths.isEmpty()) {
			return new ResultJSONObject("new_photo_failure", "新建相片失败");
		}
		try {
			jsonObject = this.myMerchantService.insertPhotos(appType,
					merchantId, albumId, photoPaths);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "insertPhotos");

			for (String photoPath : photoPaths) {
				BusinessUtil.deleteOssFile(photoPath);
			}
			jsonObject = new ResultJSONObject("insertPhotos_exception",
					"新建相片失败");
			logger.error("新建相片失败", e);
		}
		return jsonObject;
	}

	/** 删除相片 */
	@RequestMapping("/deletePhoto")
	@SystemControllerLog(description = "删除相片")
	public @ResponseBody Object deletePhoto(String appType, Long merchantId,
			String photoIds) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.deletePhoto(appType,
					merchantId, photoIds);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "deletePhoto");

			jsonObject = new ResultJSONObject("insertPhotos_exception",
					"删除相片失败");
			logger.error("删除相片失败", e);
		}
		return jsonObject;
	}

	// /** 商户销户 */
	// @RequestMapping("/close")
	// @SystemControllerLog(description = "商户销户")
	// public @ResponseBody Object close(String appType, Long merchantId) {
	// JSONObject jsonObject = null;
	// try {
	// jsonObject = this.myMerchantService.closeMerchant(appType, merchantId);
	// } catch (ApplicationException e) {
	// jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
	// }
	// return jsonObject;
	// }

	/** 员工列表显示 */
	@RequestMapping("/employeesInfo")
	@SystemControllerLog(description = "员工列表显示")
	public @ResponseBody Object employeesInfo(String appType, Long merchantId,
			int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.employeesInfo(appType,
					merchantId, pageNo);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "employeesInfo");

			jsonObject = new ResultJSONObject("employeesInfo_exception",
					"员工列表显示失败");
			logger.error("员工列表显示失败", e);
		}
		return jsonObject;
	}

	/** 添加员工 获取验证码 */
	@RequestMapping("/getVerificationCodeForAddEmployee")
	@SystemControllerLog(description = "添加员工 获取验证码")
	public @ResponseBody Object getVerificationCodeForAddEmployee(
			String appType, Long merchantId, String name, String phone) {
		JSONObject jsonObject = null;
		try {

			jsonObject = this.myMerchantService
					.getVerificationCodeForAddEmployee(appType, merchantId,
							name, phone, 1);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getVerificationCodeForAddEmployee");

			jsonObject = new ResultJSONObject(
					"getVerificationCodeForAddEmployee_exception",
					"添加员工 获取验证码失败");
			logger.error("添加员工 获取验证码失败", e);
		}
		return jsonObject;
	}

	/** 添加员工 获取验证码 */
	@RequestMapping("/getVerificationCodeForAddEmployeeNew")
	@SystemControllerLog(description = "添加员工 获取验证码")
	public @ResponseBody Object getVerificationCodeForAddEmployeeNew(
			String appType, Long merchantId, String name, String employeePhone) {
		JSONObject jsonObject = null;
		try {
			String phone = "";
			jsonObject = this.myMerchantService
					.getVerificationCodeForAddEmployee(appType, merchantId,
							name, employeePhone, 1);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getVerificationCodeForAddEmployeeNew");

			jsonObject = new ResultJSONObject(
					"getVerificationCodeForAddEmployeeNew_exception",
					"添加员工 获取验证码失败");
			logger.error("添加员工 获取验证码失败", e);
		}
		return jsonObject;
	}

	/** 添加员工 获取语音验证码 */
	@RequestMapping("/getVoiceVerificationCodeForAddEmployee")
	@SystemControllerLog(description = "添加员工 获取语音验证码")
	public @ResponseBody Object getVoiceVerificationCodeForAddEmployee(
			String appType, Long merchantId, String name, String phone) {
		JSONObject jsonObject = null;
		try {
			String employeePhone = "";
			jsonObject = this.myMerchantService
					.getVerificationCodeForAddEmployee(appType, merchantId,
							name, phone, 2);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e,
					"getVoiceVerificationCodeForAddEmployee");

			jsonObject = new ResultJSONObject(
					"getVerificationCodeForAddEmployee_exception",
					"添加员工 获取验证码失败");
			logger.error("添加员工 获取验证码失败", e);
		}
		return jsonObject;
	}

	/** 添加员工 获取语音验证码 */
	@RequestMapping("/getVoiceVerificationCodeForAddEmployeeNew")
	@SystemControllerLog(description = "添加员工 获取语音验证码")
	public @ResponseBody Object getVoiceVerificationCodeForAddEmployeeNew(
			String appType, Long merchantId, String name, String employeePhone) {
		JSONObject jsonObject = null;
		try {
			String phone = "";
			jsonObject = this.myMerchantService
					.getVerificationCodeForAddEmployee(appType, merchantId,
							name, employeePhone, 2);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e,
					"getVoiceVerificationCodeForAddEmployeeNew");

			jsonObject = new ResultJSONObject(
					"getVoiceVerificationCodeForAddEmployeeNew_exception",
					"添加员工 获取验证码失败");
			logger.error("添加员工 获取验证码失败", e);
		}
		return jsonObject;
	}

	/** 剩余可添加员工（顾问号）数 */
	@RequestMapping("/surplusEmployeesNum")
	@SystemControllerLog(description = "剩余可添加员工数")
	public @ResponseBody Object surplusEmployeesNum(String appType,
			Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.surplusEmployeesNum(appType,
					merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "surplusEmployeesNum");

			jsonObject = new ResultJSONObject("surplusEmployeesNum_exception",
					"剩余可添加员工（顾问号）数失败");
			logger.error("剩余可添加员工（顾问号）数失败", e);
		}
		return jsonObject;
	}

	/** 添加员工 确定 */
	@RequestMapping("/addEmployeeConfirm")
	@SystemControllerLog(description = "添加员工 确定")
	public @ResponseBody Object addEmployeeConfirm(String appType,
			Long merchantId, String employeePhone, String verificationCode) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.addEmployeeConfirm(appType,
					merchantId, employeePhone, verificationCode);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "addEmployeeConfirm");

			jsonObject = new ResultJSONObject("addEmployeeConfirm_exception",
					"添加员工失败");
			logger.error("添加员工失败", e);
		}
		return jsonObject;
	}

	/** 删除员工 */
	@RequestMapping("/deleteEmployee")
	@SystemControllerLog(description = "删除员工")
	public @ResponseBody Object deleteEmployee(String appType,
			@RequestParam(required = false) Long userId, Long merchantId,
			String employeePhone) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.deleteEmployee(appType, userId,
					merchantId, employeePhone);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "deleteEmployee");
			jsonObject = new ResultJSONObject("deleteEmployee_exception",
					"删除员工失败");
			logger.error("删除员工失败", e);
		}
		return jsonObject;
	}

	/** 员工老板身份转换 */
	@RequestMapping("/updateBossEmployeeType")
	@SystemControllerLog(description = "员工老板身份转换")
	public @ResponseBody Object updateBossEmployeeType(String appType,
			Long merchantId, String newBossPhone, String newEmployeePhone,
			String newEmployeeName) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.updateBossEmployeeType(appType, merchantId, newBossPhone,
							newEmployeePhone, newEmployeeName);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updateBossEmployeeType");
			jsonObject = new ResultJSONObject(
					"updateBossEmployeeType_exception", "员工老板身份转换失败");
			logger.error("员工老板身份转换失败", e);
		}
		return jsonObject;
	}

	/** 增加员工数申请 */
	@RequestMapping("/increaseEmployeeNumApply")
	@SystemControllerLog(description = "增加员工数申请")
	public @ResponseBody Object increaseEmployeeNumApply(
			HttpServletRequest request, int pkgId, String appType,
			Long merchantId, int increaseEmployeeNum, String money) {
		JSONObject jsonObject = null;
		try {
			// 保存支付凭证附件
			String imageUploadPath = new StringBuilder(
					Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append(
					"payEvidence").toString();
			String voiceUPloadPath = new StringBuilder(
					Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH).append(
					"payEvidence").toString();
			List<String> paths = UploadFile.uplaodFile(request,
					voiceUPloadPath, imageUploadPath);

			int applyStatus = 0;// 苹果设备不需要上传凭证，申请状态为待支付，安卓设备需要上传凭证，申请状态为待开通
			String clientType = request.getHeader("CLIENT_TYPE");
			if ("2".equals(clientType)) {// ios

			} else {// android
				if (paths == null || paths.size() == 0) {
					// return new ResultJSONObject("upload_pay_voucher",
					// "请上传转账凭证。");
				} else {
					applyStatus = 1;
				}
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("paths", paths);// 附件列表
			paramMap.put("clientType", clientType);// 大后台需要判断记录来源是安卓还是ios
			jsonObject = this.myMerchantService.increaseEmployeeNumApply(pkgId,
					appType, merchantId, increaseEmployeeNum, money,
					applyStatus, UUID.randomUUID().toString().replace("-", ""),
					"3", paramMap);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "increaseEmployeeNumApply");
			jsonObject = new ResultJSONObject(
					"increaseEmployeeNumApply_exception", "增加员工数申请失败");
			logger.error("增加员工数申请失败", e);
		}
		return jsonObject;
	}

	/** VIP申请 */
	@RequestMapping("/vipApply")
	@SystemControllerLog(description = "VIP申请")
	public @ResponseBody Object vipApply(HttpServletRequest request,
			String appType, Long merchantId, String money, String tradeNo) {
		JSONObject jsonObject = new ResultJSONObject("vipApply_ignore",
				"VIP已全新升级，请先将O盟升级至最新版本，再进行购买申请，更多服务等着你哟。");
		return jsonObject;
		/*
		 * try { //保存支付凭证附件 String imageUploadPath = new
		 * StringBuilder(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH
		 * ).append("payEvidence").toString(); String voiceUPloadPath = new
		 * StringBuilder
		 * (Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH).append("payEvidence"
		 * ).toString(); List<String> paths = UploadFile.uplaodFile(request,
		 * voiceUPloadPath, imageUploadPath);
		 * 
		 * int applyStatus = 0;//苹果设备不需要上传凭证，申请状态为待支付，安卓设备需要上传凭证，申请状态为待开通 String
		 * clientType = request.getHeader("CLIENT_TYPE");
		 * if("2".equals(clientType)){//ios
		 * 
		 * }else{//android if(paths == null || paths.size() == 0){ // return new
		 * ResultJSONObject("upload_pay_voucher", "请上传转账凭证。"); }else{
		 * applyStatus = 1; } } Map<String,Object> paramMap = new
		 * HashMap<String,Object>(); paramMap.put("paths", paths);//附件列表
		 * paramMap.put("clientType", clientType);//大后台需要判断记录来源是安卓还是ios
		 * jsonObject = this.myMerchantService.vipApply(appType, merchantId,
		 * money, applyStatus, UUID.randomUUID().toString().replace("-", ""),
		 * "3", tradeNo,paramMap); } catch (Exception e) {
		 * MsgTools.sendMsgOrIgnore(e, "vipApply"); jsonObject = new
		 * ResultJSONObject("vipApply_exception", "VIP申请失败");
		 * logger.error("VIP申请失败", e); }
		 * 
		 * return jsonObject;
		 */
	}

	/** VIP申请状态 */
	@RequestMapping("/vipApplyStatus")
	@SystemControllerLog(description = "VIP申请状态")
	public @ResponseBody Object vipApplyStatus(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.vipApplyStatus(appType,
					merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "vipApplyStatus");
			jsonObject = new ResultJSONObject("vipApplyStatus_exception",
					"VIP申请状态失败");
			logger.error("VIP申请状态失败", e);
		}
		return jsonObject;
	}

	/** 获取商户在当前应用程序中所能提供的服务项目(订单页面标题显示用) */
	@RequestMapping("/merchantServiceItemName")
	@SystemControllerLog(description = "获取商户在当前应用程序中所能提供的服务项目(订单页面标题显示用)")
	public @ResponseBody Object getMerchantServiceItemName(String appType,
			Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.selectMerchantProvideServiceType(appType, merchantId, true);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "merchantServiceItemName");

			jsonObject = new ResultJSONObject(
					"merchantServiceItemName_exception",
					"获取商户在当前应用程序中所能提供的服务项目失败");
			logger.error("获取商户在当前应用程序中所能提供的服务项目失败", e);
		}
		return jsonObject;
	}

	/** 根据经纬度计算用户与商户之间的距离 */
	@RequestMapping("/calcDistance")
	@SystemControllerLog(description = "根据经纬度计算用户与商户之间的距离")
	public @ResponseBody Object calcDistance(String appType, double longitude,
			double latitude, int range) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new ResultJSONObject("000", "店铺距离加载成功");
			jsonObject.put("distanceList", this.myMerchantService
					.selectCalcDistance(appType, longitude, latitude, range));
		} catch (Exception ex) {

			MsgTools.sendMsgOrIgnore(ex, "calcDistance");

			jsonObject = new ResultJSONObject("calcDistance_exception",
					"根据经纬度计算用户与商户之间的距离失败");
			logger.error("根据经纬度计算用户与商户之间的距离失败", ex);
		}
		return jsonObject;
	}

	/** 商户端退出应用 */
	@RequestMapping("/merchantExit")
	@SystemControllerLog(description = "商户端退出应用")
	public @ResponseBody Object merchantExit(String appType, String clientId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.deleteMerchantPush(clientId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "merchantExit");

			jsonObject = new ResultJSONObject("merchantExit_exception",
					"商户端退出应用失败");
			logger.error("商户端退出应用失败", e);
		}
		return jsonObject;
	}

	/** 验证商户是否被退出 */
	@RequestMapping("/checkClient")
	@SystemControllerLog(description = "验证商户是否被退")
	public @ResponseBody Object checkClient(String appType,
			@RequestParam(required = false) Long userId, String clientId,
			@RequestParam(required = false) String pushId, String phone,
			Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.checkClient(appType, userId,
					clientId, pushId, phone, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "checkClient");

			jsonObject = new ResultJSONObject("checkClient_exception",
					"验证商户是否被退出失败");
			logger.error("验证商户是否被退出失败", e);
		}
		return jsonObject;
	}

	/** 更改当前使用的设备记录的clientId */
	@RequestMapping("/updateClientId")
	@SystemControllerLog(description = "更改当前使用的设备记录的clientId")
	public @ResponseBody Object updateClientId(String appType,
			@RequestParam(required = false) Long userId, String clientId,
			String pushId, String phone, Long merchantId, String clientType) {
		if (pushId == null) {
			pushId = clientId;
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.updateClientId(appType, userId,
					pushId, phone, merchantId, clientType, clientId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "updateClientId");

			jsonObject = new ResultJSONObject("updateClientId_exception",
					"更改当前使用的设备记录的clientId失败");
			logger.error("更改当前使用的设备记录的clientId失败", e);
		}
		return jsonObject;
	}

	/** 更新微官网URL */
	@RequestMapping("/updateMicroWebsiteUrl")
	@SystemControllerLog(description = "更新微官网URL")
	public @ResponseBody Object updateMicroWebsiteUrl(String appType,
			Long merchantId, String microWebsiteUrl) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.updateMicroWebsiteUrl(appType,
					merchantId, microWebsiteUrl);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "updateMicroWebsiteUrl");

			jsonObject = new ResultJSONObject(
					"updateMicroWebsiteUrl_exception", "更新微官网URL失败");
			logger.error("更新微官网URL失败", e);
		}
		return jsonObject;
	}

	/** 增值服务 */
	@RequestMapping("/addedServices")
	@SystemControllerLog(description = "增值服务")
	@CheckDuplicateSubmission(args = "appType,merchantId", type = "addedServices")
	public @ResponseBody Object addedServices(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.addedServices(appType,
					merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "addedServices");

			jsonObject = new ResultJSONObject("addedServices_exception",
					"增值服务失败");
			logger.error("增值服务失败", e);
		}
		return jsonObject;
	}

	/** 查询（商品）分类信息 */
	@RequestMapping("/selectGoodsClassificationInfo")
	@SystemControllerLog(description = "查询商品分类信息")
	public @ResponseBody Object selectGoodsClassificationInfo(String appType,
			Long merchantId, String isSelect) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectGoodsClassificationInfo(
					appType, merchantId, isSelect);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "selectGoodsClassificationInfo");

			jsonObject = new ResultJSONObject(
					"selectGoodsClassificationInfo_exception", "查询（商品）分类信息失败");
			logger.error("查询（商品）分类信息失败", e);
		}
		return jsonObject;
	}

	/** 新建（商品）分类 */
	@RequestMapping("/addGoodsClassification")
	@SystemControllerLog(description = "新建商品分类")
	public @ResponseBody Object addGoodsClassification(String appType,
			Long merchantId, String classificationName) {
		JSONObject jsonObject = null;
		try {
			// 处理$
			classificationName = StringUtil
					.formatDollarSign(classificationName);
			jsonObject = this.myMerchantService.addGoodsClassification(appType,
					merchantId, filterFourCharString(classificationName));
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "addGoodsClassification");
			jsonObject = new ResultJSONObject(
					"addGoodsClassification_exception", "新建商品分类失败");
			logger.error("新建商品分类失败", e);
		}
		return jsonObject;
	}

	/** 重命名（商品）分类 */
	@RequestMapping("/renameGoodsClassification")
	@SystemControllerLog(description = "重命名商品分类")
	public @ResponseBody Object renameGoodsClassification(String appType,
			Long merchantId, Long classificationId, String classificationName) {
		JSONObject jsonObject = null;
		try {
			// 处理$
			classificationName = StringUtil
					.formatDollarSign(classificationName);
			jsonObject = this.myMerchantService.renameGoodsClassification(
					appType, merchantId, classificationId, classificationName);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "renameGoodsClassification");
			jsonObject = new ResultJSONObject(
					"renameGoodsClassification_exception", "重命名商品分类失败");
			logger.error("重命名商品分类失败", e);
		}
		return jsonObject;
	}

	/** 删除（商品）分类 */
	@RequestMapping("/deleteGoodsClassification")
	@SystemControllerLog(description = "删除商品分类")
	public @ResponseBody Object deleteGoodsClassification(String appType,
			Long merchantId, Long classificationId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.deleteGoodsClassification(
					appType, merchantId, classificationId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "deleteGoodsClassification");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
			logger.error("删除商品分类失败", e);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "deleteGoodsClassification");

			jsonObject = new ResultJSONObject(
					"deleteGoodsClassification_exception", "删除商品分类失败");
			logger.error("删除商品分类失败", e);
		}
		return jsonObject;
	}

	/** 查询商品信息 */
	@RequestMapping("/selectGoodsInfo")
	@SystemControllerLog(description = "查询商品信息")
	public @ResponseBody Object selectGoodsInfo(String appType,
			Long merchantId, Long classificationId, int pageNo, String isSelect) {
		JSONObject jsonObject = null;
		try {
			if (isSelect != null && "1".equals(isSelect)) {// 选择模式，只查上架商品
				jsonObject = this.myMerchantService.selectGoodsList(merchantId,
						classificationId, pageNo);
			} else {
				jsonObject = this.myMerchantService.selectGoodsInfo(appType,
						merchantId, classificationId, pageNo);
			}

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "selectGoodsInfo");
			jsonObject = new ResultJSONObject("selectGoodsInfo_exception",
					"查询商品信息失败");
			logger.error("查询商品信息失败", e);
		}
		return jsonObject;
	}

	/** 查询商品信息 */
	@RequestMapping("/selectGoodsList")
	@SystemControllerLog(description = "查询商品信息")
	public @ResponseBody Object selectGoodsList(Long merchantId,
			Long classificationId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectGoodsList(merchantId,
					classificationId, pageNo);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "selectGoodsList");
			jsonObject = new ResultJSONObject("selectGoodsList_exception",
					"查询商品信息失败");
			logger.error("查询商品信息失败", e);
		}
		return jsonObject;
	}

	/** 新建商品 */
	@RequestMapping("/addGoods")
	@SystemControllerLog(description = "新建商品")
	public @ResponseBody Object addGoods(String appType, Long merchantId,
			String classificationIds, String goodsName, String goodsPrice,
			@RequestParam(required = false) String goodsDescribe,
			MultipartFile goodsPicture, String goodsPriceUnit,
			HttpServletRequest request) {

		// 处理$

		goodsName = StringUtil.formatDollarSign(goodsName);
		goodsDescribe = StringUtil.formatDollarSign(goodsDescribe);

		JSONObject jsonObject = this.myMerchantService.goodsInfoCheck(
				merchantId, null, goodsName, classificationIds, 1);
		if (jsonObject != null) {
			return jsonObject;
		}
		String resultPath = Constant.EMPTY;
		if (goodsPicture != null && !goodsPicture.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String filePath = new StringBuilder(
					Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("goods")
					.append(Constant.FILE_EPARATOR)
					.append(sdf.format(new Date()))
					.append(Constant.FILE_EPARATOR).toString();
			resultPath = BusinessUtil.fileUpload(goodsPicture, filePath);
		}

		// 保存商品图片 2016-08-16 by CUIJIAJUN
		String imageUploadPath = new StringBuilder(
				Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("goods")
				.toString();
		String voiceUPloadPath = new StringBuilder(
				Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH).append("goods")
				.toString();
		List<String> paths = UploadFile.uplaodFile(request, voiceUPloadPath,
				imageUploadPath);

		if (StringUtils.isEmpty(resultPath) && paths.size() == 0) {
			return new ResultJSONObject("add_goods_failure", "商品保存失败");
		} else {
			if (!StringUtils.isEmpty(resultPath)) {// 兼容旧版app
				paths.add(resultPath);
			}
		}
		try {
			jsonObject = this.myMerchantService.addGoods(appType, merchantId,
					classificationIds, goodsName, goodsPrice, goodsDescribe,
					goodsPriceUnit, paths);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "addGoods");

			BusinessUtil.deleteOssFile(resultPath);
			jsonObject = new ResultJSONObject(e.getResult(), "新建商品失败:"
					+ e.getMsg());
			logger.error("新建商品失败", e);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "addGoods");

			BusinessUtil.deleteOssFile(resultPath);
			jsonObject = new ResultJSONObject("addGoods_exception", "新建商品失败");
			logger.error("新建商品失败", e);
		}
		return jsonObject;
	}

	/** 更新商品信息 */
	@RequestMapping("/updateGoodsInfo")
	@SystemControllerLog(description = "更新商品信息")
	public @ResponseBody Object updateGoodsInfo(String appType,
			Long merchantId, Long goodsId, String classificationIds,
			String goodsName, String goodsPrice,
			@RequestParam(required = false) String goodsDescribe,
			String goodsPriceUnit, HttpServletRequest request, String paths) {
		JSONObject jsonObject = this.myMerchantService.goodsInfoCheck(
				merchantId, goodsId, goodsName, classificationIds, 2);
		if (jsonObject != null) {
			return jsonObject;
		}
		MultipartFile goodsPicture = null;
		// 原图片传入了路径,需要进行排序等操作
		List<String> newPaths = new ArrayList<String>();
		String[] pathArr = new String[5];
		if (StringUtil.isNotEmpty(paths)) {
			JSONArray jsonArray = JSONArray.parseArray(paths);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				int sort = json.getIntValue("sort");
				Long picId = json.getLong("id");
				String path = json.getString("path");
				//
				path = path.substring(
						path.indexOf(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH),
						path.length());
				if (sort != -1) {
					pathArr[sort] = path;
				}
			}
			newPaths = Arrays.asList(pathArr);
		} else {

		}
		String resultPath = Constant.EMPTY;
		try {
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				goodsPicture = multipartRequest.getFile("goodsPicture");// 老版本
				if (goodsPicture != null && !goodsPicture.isEmpty()) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					String filePath = new StringBuilder(
							Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH)
							.append("goods").append(Constant.FILE_EPARATOR)
							.append(sdf.format(new Date()))
							.append(Constant.FILE_EPARATOR).toString();
					resultPath = BusinessUtil
							.fileUpload(goodsPicture, filePath);
				} else {
					// 保存商品图片 2016-08-16 by CUIJIAJUN
					String imageUploadPath = new StringBuilder(
							Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append(
							"goods").toString();
					String voiceUPloadPath = new StringBuilder(
							Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH).append(
							"goods").toString();
					newPaths = UploadFile.uplaodFile4Goods(request,
							voiceUPloadPath, imageUploadPath, newPaths);
				}

				if (StringUtils.isEmpty(resultPath) && newPaths.size() == 0
						&& paths != null) {
					return new ResultJSONObject("goods_info_update_failure",
							"商品保存失败");
				} else {
					if (!StringUtils.isEmpty(resultPath)) {// 兼容旧版app
						newPaths.add(resultPath);
					}
				}
			} else {
				// return new ResultJSONObject("goods_info_update_failure",
				// "商品保存失败");
			}
		} catch (Exception ex) {
			MsgTools.sendMsgOrIgnore(ex, "updateGoodsInfo");
			logger.error("", ex);
		}
		try {
			jsonObject = this.myMerchantService.updateGoodsInfo(appType,
					merchantId, goodsId, classificationIds, goodsName,
					goodsPrice, goodsDescribe, goodsPriceUnit, newPaths);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "updateGoodsInfo");
			jsonObject = new ResultJSONObject(e.getResult(), "更新商品失败:"
					+ e.getMsg());
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "addGoods");

			BusinessUtil.deleteOssFile(resultPath);
			jsonObject = new ResultJSONObject("addGoods_exception", "更新商品失败");
			logger.error("更新商品失败", e);
		}
		return jsonObject;
	}

	/** 删除商品 */
	@RequestMapping("/deleteGoods")
	@SystemControllerLog(description = "删除商品")
	public @ResponseBody Object deleteGoods(String appType, Long merchantId,
			Long classificationId, String goodsId) {
		// goodsId多个商品id为“,”隔开
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.deleteGoods(appType,
					merchantId, classificationId, goodsId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "deleteGoods");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 查询商品图片 */
	@RequestMapping("/selectGoodsDetail")
	@SystemControllerLog(description = "查询商品详情")
	public @ResponseBody Object selectGoodsDetail(String appType,
			Long merchantId, String goodsId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectGoodsDetail(appType,
					merchantId, goodsId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "selectGoodsPic");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "selectGoodsPic");
			jsonObject = new ResultJSONObject("select_goods_pic_exception",
					e.getMessage());
		}
		return jsonObject;

	}

	/**
	 * 商品上架下架
	 * 
	 * @param appType
	 * @param merchantId
	 * @param targetStatus
	 *            0-上架，1-上架
	 * @param goodsId
	 * @return
	 */
	@RequestMapping("/changeGoodsStatus")
	@SystemControllerLog(description = "商品上架下架")
	public @ResponseBody Object changeGoodsStatus(String appType,
			Long merchantId, int targetStatus, String goodsId) {
		// goodsId多个商品id为“,”隔开
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.changeGoodsStatus(appType,
					merchantId, targetStatus, goodsId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "changeGoodsStatus");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "changeGoodsStatus");
			jsonObject = new ResultJSONObject("change_goods_status_failure",
					e.getMessage());
		}
		return jsonObject;
	}

	/** 创建商品快照 */
	@RequestMapping("/createGoodsHistory")
	@SystemControllerLog(description = "创建商品快照")
	public @ResponseBody Object createGoodsHistory(Long goodsId, int version) {
		JSONObject jsonObject = null;
		try {
			long id = this.myMerchantService.createGoodsHistory(goodsId,
					version);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "deleteGoods");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

	/** 查看商品快照信息 */
	@RequestMapping("/selectGoodsHistoryInfo")
	@SystemControllerLog(description = "查看商品快照信息")
	public @ResponseBody Object selectGoodsHistoryInfo(String goodsHistoryId,
			String goodsId, String version) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectGoodsHistoryInfo(
					goodsHistoryId, goodsId, version);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "selectGoodsHistoryInfo");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "selectGoodsHistoryInfo");
			jsonObject = new ResultJSONObject(
					"select_goods_history_info_exception", "查看商品快照信息失败");
		}
		return jsonObject;
	}

	/** 服务标签保存 */
	@RequestMapping("/serviceTagSave")
	@SystemControllerLog(description = "服务标签保存")
	public @ResponseBody Object serviceTagSave(String tag, Long merchantId) {
		// 处理$
		tag = StringUtil.formatDollarSign(tag);
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.serviceTagSave(tag, merchantId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "serviceTagSave");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 选择推荐的服务标签保存 */
	@RequestMapping("/chooseServiceTagSave")
	@SystemControllerLog(description = "选择推荐的服务标签保存")
	public @ResponseBody Object chooseServiceTagSave(String tags,
			Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.chooseServiceTagSave(tags,
					merchantId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "chooseServiceTagSave");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 服务标签删除 */
	@RequestMapping("/deleteServiceTag")
	@SystemControllerLog(description = "服务标签删除")
	public @ResponseBody Object deleteServiceTag(String tag, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.deleteServiceTag(tag,
					merchantId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "deleteServiceTag");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 查询服务标签 */
	@RequestMapping("/selectServiceTag")
	@SystemControllerLog(description = "查询服务标签")
	public @ResponseBody Object selectServiceTag(Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectServiceTag(merchantId);
		} catch (ApplicationException e) {

			MsgTools.sendMsgOrIgnore(e, "selectServiceTag");

			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 查询增值服务记录 */
	@RequestMapping("/selectValueAddedService")
	@SystemControllerLog(description = "查询增值服务记录")
	public @ResponseBody Object selectValueAddedService(Long merchantId,
			int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.selectValueAddedService(
					merchantId, pageNo);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "selectValueAddedService");

			jsonObject = new ResultJSONObject(
					"selectValueAddedService_exception", "查询增值服务记录失败");
			logger.error("查询增值服务记录失败", e);
		}
		return jsonObject;
	}

	/** 删除增值服务记录 */
	@RequestMapping("/delValueAddedService")
	@SystemControllerLog(description = "删除增值服务记录")
	public @ResponseBody Object delValueAddedService(Long merchantId,
			Long serviceId, String serviceType) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.delValueAddedService(
					merchantId, serviceId, serviceType);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "delValueAddedService");

			jsonObject = new ResultJSONObject("delValueAddedService_exception",
					"删除增值服务记录失败");
			logger.error("删除增值服务记录失败", e);
		}
		return jsonObject;
	}

	/** 验证token */
	@RequestMapping("/validateToken")
	@SystemControllerLog(description = "验证token")
	public @ResponseBody Object validateToken() {
		// 这个接口啥都不做，只验证token,通过拦截器后能访问这个接口说明可以通过验证
		JSONObject jsonObject = new ResultJSONObject("000", "验证token通过");
		return jsonObject;
	}

	/** 检查商户信息完成度 */
	@RequestMapping("/checkMerchantInfo")
	@SystemControllerLog(description = "检查商户信息完成度")
	public @ResponseBody Object checkMerchantInfo(Long merchantId) {
		JSONObject jsonObject = new ResultJSONObject("000", "检查商户信息完成度成功");
		Map<String, Object> merchantInfo = this.myMerchantService
				.checkMerchantInfo(merchantId);
		if (merchantInfo == null) {
			jsonObject = new ResultJSONObject("fail", "商户信息为空");
		} else {
			jsonObject.put("merchantInfo", merchantInfo);
		}
		return jsonObject;
	}

	/** 个性服务商户快速注册 */
	@RequestMapping("/quickRegGxfwMerchantInfo")
	@SystemControllerLog(description = "检查商户信息完成度")
	public @ResponseBody Object quickRegGxfwMerchantInfo(String phone,
			String name, String detial,
			@RequestParam(required = false) String tags) {
		JSONObject jsonObject = null;
		// 获取商户的公网IP
		String ip = IPutil.getIpAddr(ServletUtil.getRequest());
		jsonObject = this.myMerchantService.quickRegGxfwMerchantInfo(phone,
				name, detial, tags, ip);
		return jsonObject;
	}

	/** 我的店铺列表 */
	@RequestMapping("/myMerchantList")
	@SystemControllerLog(description = "我的店铺列表")
	public @ResponseBody Object myMerchantList(Long userId) {
		JSONObject jsonObject = new ResultJSONObject("001", "我的店铺列表查询失败");
		if (userId == null) {
			jsonObject = new ResultJSONObject("001", "参数错误，userId必传");
			return jsonObject;
		}
		try {
			List<Map<String, Object>> myMerchantList = this.myMerchantService
					.myMerchantList(userId);
			jsonObject = new ResultJSONObject("000", "我的店铺列表查询成功");
			jsonObject.put("myMerchantList", myMerchantList);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "myMerchantList", userId + "");
			jsonObject = new ResultJSONObject("myMerchantList_failure",
					"我的店铺列表失败");
			logger.error("我的店铺列表", e);
		}
		return jsonObject;
	}

	/** 添加代金券（多条）用于接单计划 */
	@RequestMapping("/vouchersPermissionsBatchSave")
	@SystemControllerLog(description = "添加代金券（多条）")
	public @ResponseBody Object vouchersPermissionsBatchSave(Long merchantId,
			String vouchersIds, String counts, String cutoffTimes,
			@RequestParam(required = false) Integer planNum) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService
					.insertMerchantVouchersPermissions(merchantId, vouchersIds,
							counts, cutoffTimes, planNum);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "vouchersPermissionsSave");
			jsonObject = new ResultJSONObject(
					"vouchers_permissions_save_failure", "添加代金券（多条）失败");
			logger.error("添加代金券（多条）用于接单计划", e);
		}
		return jsonObject;
	}

	/** 接单计划信息展示 */
	@RequestMapping("/orderPlan")
	@SystemControllerLog(description = "接单计划信息展示")
	public @ResponseBody Object orderPlan(Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.orderPlan(merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "orderPlan");
			jsonObject = new ResultJSONObject("order_plan_select_failure",
					"接单计划信息展示失败");
			logger.error("接单计划信息展示", e);
		}
		return jsonObject;
	}

	/** 获取关注列表 */
	@RequestMapping("/getCollections")
	@SystemControllerLog(description = "获取关注列表")
	public @ResponseBody Object getCollections(Long merchantId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myMerchantService.getCollections(merchantId,
					pageNo);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getCollections");
			jsonObject = new ResultJSONObject("getCollections_failure",
					"获取关注列表失败");
			logger.error("获取关注列表展示", e);
		}
		return jsonObject;
	}

	/** 商户转出店铺金额至钱包 */
	/*
	 * @RequestMapping("/transoutMoneyToOpay")
	 * 
	 * @SystemControllerLog(description = "转出店铺金额至钱包") public @ResponseBody
	 * Object transoutMoneyToWallet(Long merchantId,Long userId,String
	 * amountMoney,String userPhone,String appType,HttpServletRequest request) {
	 * JSONObject jsonObject = null; boolean transFlag = true; Long errorId =
	 * null; String transSeq =
	 * KeyGenerator.generateWithdrawBatchNo(String.valueOf(userId)); String
	 * localIp = IPutil.getLocalIpAddr(); String version =
	 * ClientParamUtil.getVersion(request); Header header = new Header(localIp,
	 * localIp, "transferService", "omeng",
	 * DateUtil.getCurrentTimeYYYYMMddHHMMssSSS(), transSeq, "token",version);
	 * try { Map<String,Object> paramsMap = new HashMap<String,Object>();
	 * paramsMap.put("merchantId", merchantId); paramsMap.put("userId", userId);
	 * BigDecimal money =StringUtil.isEmpty(amountMoney) ? new BigDecimal(0) :
	 * new BigDecimal(amountMoney); paramsMap.put("moneyAmount", money);
	 * paramsMap.put("userPhone",userPhone); paramsMap.put("appType",appType);
	 * //校验转出参数，并记录系统宕机异常交易日志 jsonObject =
	 * transoutService.transoutCheck(paramsMap, header); if(jsonObject == null
	 * || !"000".equals(jsonObject.getString("resultCode"))){ return jsonObject;
	 * } //设置商户名称 paramsMap.put("merchantName", jsonObject.get("merchantName"));
	 * errorId= jsonObject.getLong("errorId"); //错误日志Id //交易预处理 Long transRecord
	 * = transoutService.transoutBefore(paramsMap, header); if(transRecord ==
	 * null){ jsonObject = new
	 * ResultJSONObject("transout_to_opay_error","转入出账户余额至钱包系统异常"); return
	 * jsonObject; } paramsMap.put("transRecord", transRecord);
	 * paramsMap.put("transSeq", transSeq); //执行收入转出到钱包交易 jsonObject =
	 * transoutService.transMoneyToWallet(paramsMap,header); if(jsonObject ==
	 * null){ jsonObject = new
	 * ResultJSONObject("transout_to_opay_error","转入出账户余额至钱包系统异常"); return
	 * jsonObject; } //交易完成处理
	 * jsonObject=transoutService.transoutAfter(paramsMap, jsonObject);
	 * if(!"000".equals(jsonObject.getString("resultCode"))){ return jsonObject;
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); MsgTools.sendMsgOrIgnore(e,
	 * "transoutMoneyToOpay"); transFlag = false; jsonObject = new
	 * ResultJSONObject("transout_to_opay_error", "转入出账户余额至钱包系统异常");
	 * logger.error("转出到钱包异常", e); }finally{ Map<String,Object> logParams = new
	 * HashMap<String,Object>(); logParams.put("merchantId", merchantId);
	 * logParams.put("userId", userId); logParams.put("transAmount",
	 * amountMoney); //转账金额 int transStatus = 1; // 1 交易成功 0 交易失败 if(!transFlag
	 * ||(jsonObject != null &&
	 * !"000".equals(jsonObject.getString("resultCode")))){ transStatus = 0; }
	 * logParams.put("transStatus", transStatus); logParams.put("transCode",
	 * jsonObject.getString("resultCode"));//交易状态码 logParams.put("transDesc",
	 * jsonObject.getString("message")); //交易信息
	 * logParams.put("transWalletBatchNo"
	 * ,jsonObject.getString("transNo"));//钱包交易流水 logParams.put("trasnSeq",
	 * transSeq);//系统流水 logParams.put("clientFlag",
	 * ClientParamUtil.getClientFlag(request));//客户端唯一标示
	 * logParams.put("clientType",
	 * ClientParamUtil.getClientType(request));//客户端请求类型
	 * logParams.put("version", version); //系统版本号 logParams.put("clientIp",
	 * ClientParamUtil.getIpAddr(request)); //客戶端ip地址 logParams.put("model",
	 * ClientParamUtil.getModel(request));//手机型号 //保存交易日志
	 * transoutService.saveTransoutLogs(logParams); //如果provider未宕机，删除此条宕机交易记录
	 * if(null != errorId){ transoutService.deleteSystemErrorLogs(errorId); } }
	 * jsonObject.remove("errorId"); return jsonObject; }
	 */

	/** 查询商铺账户余额 */
	@RequestMapping("/queryMerchantAccount")
	@SystemControllerLog(description = "查询商铺账户资金信息")
	public @ResponseBody Object queryMerchantAccount(Long merchantId,
			String appType) {
		JSONObject jsonObject = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("merchantId", merchantId);
			params.put("appType", appType);
			jsonObject = this.myMerchantService
					.selectMerchantAccountStatistics(params);
			// 统计今日五条交易明细
			JSONObject paymentListObject = myIncomeService
					.selectPaymentDetailsList(appType, merchantId, 0, 1, -1); // 统计今日交易明细
			if (paymentListObject != null) {
				jsonObject.put("paymentList",
						paymentListObject.get("paymentList"));
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "queryMerchantAccount");
			logger.error("查询商铺账户资金信息失败", e);
		}
		return jsonObject;
	}

	/**
	 * 开通增值服务
	 */
	@RequestMapping("/openIncreaseService")
	@SystemControllerLog(description = "开通增值服务")
	public @ResponseBody JSONObject openIncreaseService(
			@RequestParam Map<String, Object> params) {
		JSONObject jsonObject = null;
		try {
			jsonObject = myMerchantService.openIncreaseService(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "openIncreaseService");
			logger.error("开通增值服务错误信息：", e);
		}
		return jsonObject;
	}

	/**
	 * 根据包ID获取包的基本信息
	 * 
	 * @param pkgId
	 * @return
	 */
	@RequestMapping("/getPkgInfoById")
	@SystemControllerLog(description = "开通增值服务")
	public @ResponseBody JSONObject getPkgInfoById(int pkgId) {
		JSONObject jsonObject = new ResultJSONObject("000", "获取包的基本信息成功");
		try {
			Map<String, Object> params = myMerchantService
					.getPkgInfoById(pkgId);
			jsonObject.put("charge", params.get("charge"));
			jsonObject.put("name", params.get("name"));
			jsonObject.put("typeKey", params.get("serviceKey"));
			// 2emloyee_num顾问号 3order_push抢单金 4:vip（VIP）5consultant私人助理
			// 6merchant签约特权 7hosting托管代运营
			String key = String.valueOf(params.get("serviceKey"));
			int value = 0;
			if ("emloyee_num".equals(key)) {
				value = 2;
			} else if ("order_push".equals(key)) {
				value = 3;
			} else if ("vip".equals(key)) {
				value = 4;
			} else if ("consultant".equals(key)) {
				value = 5;
			} else if ("merchant".equals(key)) {
				value = 6;
			} else if ("hosting".equals(key)) {
				value = 7;
			} else {
				value = 0;
			}
			jsonObject.put("typeValue", value);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "openIncreaseService");
			logger.error("根据包ID获取包的错误信息：", e);
		}
		return jsonObject;
	}

	@RequestMapping("/test_merchantInfo")
	public @ResponseBody Object test_merchantInfo(Long merchantId) {
		JSONObject jsonObject = null;
		try {

			this.myMerchantService.merchantBasicInfo(merchantId);

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "queryMerchantAccount");
			logger.error("查询商铺账户资金信息失败", e);
		}
		return jsonObject;
	}

	/**
	 * 开票申请
	 *
	 */
	// /myMerchant/createinvoice?merchantId=146950166512245253
	@RequestMapping("/createinvoice")
	@SystemControllerLog(description = "开票申请")
	public @ResponseBody JSONObject createinvoice(Long merchantId) {
		JSONObject jsonObject = null;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("merchantId", merchantId);

			jsonObject = merchantInvoiceService.selectIncOrders(paramMap);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "createinvoice");
			jsonObject = new ResultJSONObject("001", "开票申请");
			logger.error("开通增值服务错误信息：", e);
		}
		return jsonObject;
	}

	/**
	 * 
	 * ｛保存发票信息｝
	 * 
	 * @param params
	 * @return
	 * @author Liuxingwen
	 * @created 2016年10月14日 下午5:54:26
	 * @lastModified
	 * @history
	 */
	// /myMerchant/createinvoicepage
	@RequestMapping("/saveinvoice")
	@SystemControllerLog(description = "保存发票信息")
	public @ResponseBody JSONObject saveInvoice(
			@RequestParam Map<String, Object> params) {
		JSONObject jsonObject = null;
		try {
			jsonObject = merchantInvoiceService.saveInvoice(params);
		} catch (Exception e) {
			if(e.getMessage().equals("errorsvoice"))
			{
				jsonObject = new ResultJSONObject("001", "保存发票信息失败,进行了重复开票申请，请确认！");
			}else {
				jsonObject = new ResultJSONObject("001", "保存发票信息失败");
			}
			
			jsonObject.put("voice_id", 0);
			MsgTools.sendMsgOrIgnore(e, "saveinvoice");
			logger.error("保存发票信息错误：", e);
		}
		return jsonObject;
	}

	/**
	 * 发票详情
	 *
	 */
	// /myMerchant/getinvoice?merchantId=146950166512245253&voiceId=30
	@RequestMapping("/getinvoice")
	@SystemControllerLog(description = "发票详情")
	public @ResponseBody JSONObject getinvoice(Long merchantId, long voiceId) {
		JSONObject jsonObject = null;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("merchantId", merchantId);
			paramMap.put("voice_id", voiceId);
			jsonObject = merchantInvoiceService.selectInvoice(paramMap);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("001", "发票详情失败");
			MsgTools.sendMsgOrIgnore(e, "createinvoice");
			logger.error("发票详情错误信息：", e);
		}
		return jsonObject;
	}

	/**
	 * 发票历史记录
	 *
	 */
	// /myMerchant/getInvoiceList?merchantId=146950166512245253&pageNo=1
	@RequestMapping("/getInvoiceList")
	@SystemControllerLog(description = "发票历史记录")
	public @ResponseBody JSONObject getInvoiceList(Long merchantId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("merchantId", merchantId);
			paramMap.put("startNum", pageNo * Constant.PAGESIZE);
			paramMap.put("pageSize", Constant.PAGESIZE);

			jsonObject = merchantInvoiceService.getInvoiceList(paramMap);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("001", "发票历史记录失败");
			MsgTools.sendMsgOrIgnore(e, "getInvoiceList");
			logger.error("发票历史记录错误信息：", e);
		}
		return jsonObject;
	}
	
	/** 生成商户二维码 */
	@RequestMapping("/createQRcode")
	@SystemControllerLog(description = "生成商户二维码")
	public @ResponseBody Object createQRcode(Long merchantId,HttpServletRequest request) {
	   JSONObject jsonObject = null;
	   // 先查询商户头像（有头像生成带logo二维码，无头像生成不带logo二维码）
	   // 1.查询 merchant_attachment，
		// 2.有头像logo下载到本地服务器，
		// 3.生成二维码在本地指定目录，
		// 4.上传至阿里oss服务器，
		// 5.保存上传路径至DB.merchant_qr_code


        // 生成二维码
        try {
			if(merchantId == null || merchantId == 0L){
				jsonObject = new ResultJSONObject("001", "二维码信息异常，商户不存在");
				return jsonObject;
			}
			String content = HttpRequest.getDomain(request)
					+ "/wechat/readQRcode?merchantId="
					+ merchantId; // 生成二维码信息url
			String imgPath = "/tmp/qrcode/" + merchantId+".png"; // 生成本地服务器二维码图片路径

			File tmpDir = new File("/tmp/qrcode/");
			if(!tmpDir.exists()){
				tmpDir.mkdirs();
			}

			File imgFile = new File(imgPath);

			String iconPath = myMerchantService.selectMerchantIcon(merchantId);
			if(iconPath == null || iconPath.length() == 0){
				imgPath = QRCodeUtil.createQRcode(content, imgPath); // 生成普通二维码
				if(StringUtils.isEmpty(imgPath)){
					jsonObject = new ResultJSONObject("002", "二维码创建失败");
					return jsonObject;
				}
			}else{
				String iconHttpUrl = BusinessUtil.disposeImagePath(iconPath);
				String logoPath = "/tmp/qrcode/" + merchantId+"_logo.png"; // 下载商户头像至本地服务器路径
				if(BusinessUtil.readNetFile(iconHttpUrl,logoPath)){
					String minLogoPath = "/tmp/qrcode/" + merchantId+"_logo_min.png";
					EditImage.saveImageAsJpg(logoPath,minLogoPath,50,50);
					QRCodeUtil.createQRcodeWithLogo(content, imgPath, minLogoPath); // 生成带logo二维码
				}else{
					imgPath = QRCodeUtil.createQRcode(content, imgPath); // 生成普通二维码
					if(StringUtils.isEmpty(imgPath)){
						jsonObject = new ResultJSONObject("003", "二维码创建失败");
						return jsonObject;
					}
				}
			}
			File f = new File(imgPath);
			FileInputStream fi = null;
			fi = new FileInputStream(f);
			AliOssUtil.upload("/qrcode/merchant/",merchantId+".png",fi);
			BusinessUtil.deleteFile(f);
			IOUtils.closeQuietly(fi);

			String aliossPath = "/qrcode/merchant/"+merchantId+".png";

			String oldQrPath = myMerchantService.selectMerchantQrcode(merchantId);
			if(StringUtils.isEmpty(oldQrPath)){
				myMerchantService.insertMerchantQrcode(merchantId,aliossPath);
			}

			jsonObject = new ResultJSONObject("000", "二维码创建成功");
			jsonObject.put("qrcode",BusinessUtil.disposeImagePath(aliossPath));

			return jsonObject;
       } catch (Exception e) {
			logger.error("create merchant qrcode error",e);
			jsonObject = new ResultJSONObject("004", "二维码创建失败");
			return jsonObject;
		}finally {
			BusinessUtil.deleteDir(new File("/tmp/qrcode/"));
		}

	}

}
