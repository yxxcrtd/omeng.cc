package com.shanjin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.ServletUtil;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IMyMerchantService;

/**
 * 商户版，我的店铺信息展示
 */
@Controller
@RequestMapping("/myMerchant")
public class MyMerchantController {

	@Resource
	private IMyMerchantService myMerchantService;
	@Resource
	private ICommonService commonService;

	/** 获取商户验证码 */
	@RequestMapping("/getVerificationCode")
	@SystemControllerLog(description = "获取商户验证码")
	public @ResponseBody Object getVerificationCode(String appType, String clientId, String phone, @RequestParam(required = false) String handsetMakers, @RequestParam(required = false) String mobileVersion, @RequestParam(required = false) String mobileNumber) {
	       // 获取商户的公网IP
        String ip = IPutil.getIpAddr(ServletUtil.getRequest());
		return this.myMerchantService.getVerificationCode(appType, clientId, phone, ip, handsetMakers, mobileVersion, mobileNumber);
	}

	/** 验证商户验证码 */
	@RequestMapping("/validateVerificationCode")
	@SystemControllerLog(description = "验证商户验证码")
	public @ResponseBody Object validateVerificationCode(String appType, String phone, String verificationCode, String clientId, String clientType) {
		return this.myMerchantService.validateVerificationCode(appType, phone, verificationCode, clientId, clientType);
	}

	/** 我的店铺 */
	@RequestMapping("/myMerchantShow")
	@SystemControllerLog(description = "我的店铺")
	public @ResponseBody Object myMerchantShow(String appType, String phone, Long merchantId) {
		return this.myMerchantService.selectMyMerchant(appType, phone, merchantId);
	}

	/** 店铺名称 保存 */
	@RequestMapping("/merchantNameSave")
	@SystemControllerLog(description = "店铺名称保存")
	public @ResponseBody Object merchantNameSave(String appType, Long merchantId, String name, String detail) {
		return this.myMerchantService.updateNameAndDetail(appType, merchantId, name, detail);
	}

	/** 店铺图标 保存 */
	@RequestMapping("/merchantIconSave")
	@SystemControllerLog(description = "店铺图标保存")
	public @ResponseBody Object merchantIconSave(String appType, Long merchantId, MultipartFile iconFile) {
		String resultPath = "";
		if (!iconFile.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String filePath = new StringBuilder(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("portrait").append(Constant.FILE_EPARATOR).append(sdf.format(new Date())).append(Constant.FILE_EPARATOR).toString();
			resultPath = BusinessUtil.fileUpload(iconFile, filePath);
		}
		return this.myMerchantService.updateMerchantIcon(appType, merchantId, resultPath);
	}

	/** 服务项目 （商户已经选择的项目显示选中） */
	@RequestMapping("/serviceItem")
	@SystemControllerLog(description = "服务项目查询")
	public @ResponseBody Object serviceItem(String appType, Long merchantId) {
		if (appType.equals("yxt")) {
			return this.myMerchantService.selectMerchantServiceTypeForYxt(appType, merchantId);
		} else {
			return this.myMerchantService.selectMerchantServiceType(appType, merchantId);
		}
	}

	/** 服务项目 保存 */
	@RequestMapping("/serviceItemSave")
	@SystemControllerLog(description = "服务项目保存")
	public @ResponseBody Object serviceItemSave(String appType, String serviceTypes, Long merchantId) {
		return this.myMerchantService.insertMerchantServiceType(appType, serviceTypes, merchantId);
	}

	/** 地理位置 保存 */
	@RequestMapping("/locationSave")
	@SystemControllerLog(description = "地理位置保存")
	public @ResponseBody Object locationSave(String appType, Long merchantId, Double latitude, Double longitude, String location) {
		return this.myMerchantService.updateLocation(appType, merchantId, latitude, longitude, location);
	}

	/** 联系方式 保存 */
	@RequestMapping("/contactSave")
	@SystemControllerLog(description = "联系方式保存")
	public @ResponseBody Object contactSave(String appType, Long merchantId, String telephone) {
		return this.myMerchantService.contactSave(appType, merchantId, telephone);
	}

	/** 当前代金券 */
	@RequestMapping("/currentVouchersInfo")
	@SystemControllerLog(description = "当前代金券查询")
	public @ResponseBody Object currentVouchersInfo(String appType, Long merchantId, int pageNo) {
		return this.myMerchantService.selectCurrentVouchersInfo(appType, merchantId, pageNo);
	}

	/** 历史代金券 */
	@RequestMapping("/historyVouchersInfo")
	@SystemControllerLog(description = "历史代金券查询")
	public @ResponseBody Object historyVouchersInfo(String appType, Long merchantId, int pageNo) {
		return this.myMerchantService.selectHistoryVouchersInfo(appType, merchantId, pageNo);
	}

	/** 删除历史代金券 */
	@RequestMapping("/deleteHistoryVouchers")
	@SystemControllerLog(description = "删除历史代金券")
	public @ResponseBody Object deleteHistoryVouchers(String appType, String phone, Long id) {
		JSONObject jsonObject = new ResultJSONObject();
		try {
			this.myMerchantService.deleteHistoryVouchers(appType, phone, id);
			jsonObject.put("resultCode", "000");
			jsonObject.put("message", "删除历史代金成功");
		} catch (Exception ex) {
			jsonObject.put("resultCode", "113");
			jsonObject.put("message", "删除历史代金失败");
		}
		return jsonObject;
	}

	/** 代金券 添加代金券（代金券信息加载） */
	@RequestMapping("/vouchersTypeShow")
	@SystemControllerLog(description = "代金券信息加载")
	public @ResponseBody Object vouchersTypeShow(String appType) {
		return this.myMerchantService.selectVouchersType(appType);
	}

	/**
	 * 获取代金券剩余数量
	 */
	@RequestMapping("/surplusVouchersNumber")
	@SystemControllerLog(description = "获取代金券剩余数量")
	public @ResponseBody Object getSurplusVouchersNumber(Long vouchersId) {
		return this.myMerchantService.getSurplusVouchersNumber(vouchersId);
	}

	/** 添加代金券 确定 */
	@RequestMapping("/vouchersPermissionsSave")
	@SystemControllerLog(description = "添加代金券")
	public @ResponseBody Object vouchersPermissionsSave(String appType, Long merchantId, Long vouchersId, String count, String cutoffTime) {
		return this.myMerchantService.insertMerchantVouchersPermissions(appType, merchantId, vouchersId, count, cutoffTime);
	}

	/** 顾客评价 */
	@RequestMapping("/userEvaluation")
	@SystemControllerLog(description = "顾客评价")
	public @ResponseBody Object userEvaluation(String appType, Long merchantId, int pageNo) {
		return this.commonService.selectUserEvaluation(appType, merchantId, pageNo);
	}

	/** 申请认证 */
	@RequestMapping("/applyAuth")
	@SystemControllerLog(description = "申请认证")
	public @ResponseBody Object applyAuth(String appType, Long merchantId, int authType) {
		return this.myMerchantService.selectApplyAuthInfo(appType, merchantId, authType);
	}

	/** 提交认证申请 */
	@RequestMapping("/submitApplyAuth")
	@SystemControllerLog(description = "提交认证申请")
	public @ResponseBody Object submitApplyAuth(String appType, Long merchantId, int authType, MultipartFile authPicFile) {
		String resultPath = "";
		if (!authPicFile.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String filePath = new StringBuilder(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("auth").append(Constant.FILE_EPARATOR).append(sdf.format(new Date())).append(Constant.FILE_EPARATOR).toString();
			resultPath = BusinessUtil.fileUpload(authPicFile, filePath);
		}
		return this.myMerchantService.insertMerchantAuth(appType, merchantId, authType, resultPath);
	}

	/** 查询相册 */
	@RequestMapping("/selectAlbum")
	@SystemControllerLog(description = "查询相册")
	public @ResponseBody Object selectAlbum(String appType, Long merchantId) {
		return this.myMerchantService.selectAlbum(appType, merchantId);
	}

	/** 新建相册 */
	@RequestMapping("/insertAlbum")
	@SystemControllerLog(description = "新建相册")
	public @ResponseBody Object insertAlbum(String appType, Long merchantId, String albumName) {
		return this.myMerchantService.insertAlbum(appType, merchantId, albumName);
	}

	/** 重命名相册 */
	@RequestMapping("/updateAlbum")
	@SystemControllerLog(description = "重命名相册")
	public @ResponseBody Object updateAlbum(String appType, Long merchantId, Long albumId, String albumName) {
		return this.myMerchantService.updateAlbum(appType, albumId, albumName);
	}

	/** 删除相册 */
	@RequestMapping("/deleteAlbum")
	@SystemControllerLog(description = "删除相册")
	public @ResponseBody Object deleteAlbum(String appType, Long merchantId, Long albumId) {
		return this.myMerchantService.deleteAlbum(appType, albumId);
	}

	/** 查询相片 */
	@RequestMapping("/selectPhoto")
	@SystemControllerLog(description = "查询相片")
	public @ResponseBody Object selectPhoto(String appType, Long merchantId, Long albumId) {
		return this.myMerchantService.selectPhoto(appType, albumId);
	}

	/** 新建相片 */
	@RequestMapping("/insertPhoto")
	@SystemControllerLog(description = "新建相片")
	public @ResponseBody Object insertPhoto(String appType, Long merchantId, Long albumId, MultipartFile iconFile) {
		String resultPath = "";
		if (!iconFile.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String filePath = new StringBuilder(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("album").append(Constant.FILE_EPARATOR).append(sdf.format(new Date())).append(Constant.FILE_EPARATOR).toString();
			resultPath = BusinessUtil.fileUpload(iconFile, filePath);
		}
		return this.myMerchantService.insertPhoto(appType, albumId, resultPath);
	}

	/** 删除相片 */
	@RequestMapping("/deletePhoto")
	@SystemControllerLog(description = "删除相片")
	public @ResponseBody Object deletePhoto(String appType, Long merchantId, String photoIds) {
		return this.myMerchantService.deletePhoto(appType, photoIds);
	}

	// /** 商户销户 */
	// @RequestMapping("/close")
	// @SystemControllerLog(description = "商户销户")
	// public @ResponseBody Object close(String appType, Long merchantId) {
	// return this.myMerchantService.closeMerchant(appType, merchantId);
	// }

	/** 员工列表显示 */
	@RequestMapping("/employeesInfo")
	@SystemControllerLog(description = "员工列表显示")
	public @ResponseBody Object employeesInfo(String appType, Long merchantId, int pageNo) {
		return this.myMerchantService.employeesInfo(appType, merchantId, pageNo);
	}

	/** 添加员工 获取验证码 */
	@RequestMapping("/getVerificationCodeForAddEmployee")
	@SystemControllerLog(description = "添加员工 获取验证码")
	public @ResponseBody Object getVerificationCodeForAddEmployee(String appType, Long merchantId, String name, String phone) {
		return this.myMerchantService.getVerificationCodeForAddEmployee(appType, merchantId, name, phone);
	}

	/** 添加员工 确定 */
	@RequestMapping("/addEmployeeConfirm")
	@SystemControllerLog(description = "添加员工 确定")
	public @ResponseBody Object addEmployeeConfirm(String appType, Long merchantId, String phone, String verificationCode) {
		return this.myMerchantService.addEmployeeConfirm(appType, merchantId, phone, verificationCode);
	}

	/** 删除员工 */
	@RequestMapping("/deleteEmployee")
	@SystemControllerLog(description = "删除员工")
	public @ResponseBody Object deleteEmployee(String appType, Long merchantId, String phone) {
		return this.myMerchantService.deleteEmployee(appType, merchantId, phone);
	}

	/** 增加员工数申请 */
	@RequestMapping("/increaseEmployeeNumApply")
	@SystemControllerLog(description = "增加员工数申请")
	public @ResponseBody Object increaseEmployeeNumApply(String appType, Long merchantId, int increaseEmployeeNum) {
		return this.myMerchantService.increaseEmployeeNumApply(appType, merchantId, increaseEmployeeNum);
	}

	/** 会员申请 */
	@RequestMapping("/vipApply")
	@SystemControllerLog(description = "会员申请")
	public @ResponseBody Object vipApply(String appType, Long merchantId) {
		return this.myMerchantService.vipApply(appType, merchantId);
	}

	/** 获取商户在当前应用程序中所能提供的服务项目(订单页面标题显示用) */
	@RequestMapping("/merchantServiceItemName")
	@SystemControllerLog(description = "获取商户在当前应用程序中所能提供的服务项目(订单页面标题显示用)")
	public @ResponseBody Object getMerchantServiceItemName(String appType, Long merchantId) {
		return this.myMerchantService.selectMerchantProvideServiceType(appType, merchantId, true);
	}

	/** 根据经纬度计算用户与商户之间的距离 */
	@RequestMapping("/calcDistance")
	@SystemControllerLog(description = "根据经纬度计算用户与商户之间的距离")
	public @ResponseBody Object calcDistance(String appType, double longitude, double latitude, int range) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new ResultJSONObject("000", "店铺距离加载成功");
			jsonObject.put("distanceList", this.myMerchantService.selectCalcDistance(appType, longitude, latitude, range));
		} catch (Exception ex) {
			jsonObject = new ResultJSONObject("902", "店铺距离加载失败");
		}
		return jsonObject;
	}

	/** 商户端退出应用 */
	@RequestMapping("/merchantExit")
	@SystemControllerLog(description = "商户端退出应用")
	public @ResponseBody Object merchantExit(String appType, String clientId) {
		return this.myMerchantService.deleteMerchantPush(clientId);
	}

	/** 判断商户是否处于登陆状态 */
	@RequestMapping("/checkClient")
	@SystemControllerLog(description = "判断商户是否处于登陆状态")
	public @ResponseBody Object checkClient(String appType, String clientId, String merchantId) {
		JSONObject jsonObject = new ResultJSONObject();
		jsonObject = myMerchantService.checkClient(appType, clientId, merchantId);
		return jsonObject;
	}

}
