package com.shanjin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.CheckDuplicateSubmission;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.ServletUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.exception.ApplicationException;
import com.shanjin.service.IUserInfoService;

/**
 * 用户 业务控制器
 * 
 * @author 李焕民
 * @version 2015-3-26
 *
 */

@Controller
@RequestMapping("/userInfo")
public class UserInfoController {
	@Reference
	private IUserInfoService userService;
	private static final Logger logger = Logger.getLogger(UserInfoController.class);

	/**
	 * 获取用户验证码
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getVerificationCode")
	@SystemControllerLog(description = "获取用户验证码")
	public @ResponseBody Object getVerificationCode(String phone, String clientId, HttpServletRequest request) throws InterruptedException {
		JSONObject jsonObject = null;
		try {
			String device = StringUtil.null2Str(request.getHeader("UUID"));
			String ip = StringUtil.null2Str(BusinessUtil.getIpAddr(request));
			jsonObject = this.userService.getVerificationCode(phone, clientId,device,ip);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getVerificationCode");

			jsonObject = new ResultJSONObject("getVerificationCode_exception", "获取验证码失败");
			logger.error("获取验证码失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取用户语音验证码
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getVoiceVerificationCode")
	@SystemControllerLog(description = "获取用户语音验证码")
	public @ResponseBody Object getVoiceVerificationCode(String phone, String clientId, HttpServletRequest request) throws InterruptedException {
		JSONObject jsonObject = null;
		try {
			String device = StringUtil.null2Str(request.getHeader("UUID"));
			String ip = StringUtil.null2Str(BusinessUtil.getIpAddr(request));
			jsonObject = this.userService.getVoiceVerificationCode(phone, clientId,device,ip);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getVoiceVerificationCode");
			jsonObject = new ResultJSONObject("getVoiceVerificationCode_exception", "获取语音验证码失败");
			logger.error("获取语音验证码失败", e);
		}
		return jsonObject;
	}

	/** 验证用户验证码 */
	@RequestMapping("/validateVerificationCode")
	@SystemControllerLog(description = "验证用户验证码")
	@CheckDuplicateSubmission(args = "phone", type = "userValidateVerificationCode")
	public @ResponseBody Object validateVerificationCode(String phone, String verificationCode, String clientId, String clientType, String pushId,String phoneModel) {
		String ip = IPutil.getIpAddr(ServletUtil.getRequest());
		JSONObject jsonObject = null;
		try {
			jsonObject = this.userService.validateVerificationCode(phone, verificationCode, clientId, clientType, pushId, ip,phoneModel);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "validateVerificationCode");

			jsonObject = new ResultJSONObject("validateVerificationCode_exception", "验证用户验证码失败");
			logger.error("验证用户验证码失败", e);
		}
		return jsonObject;
	}

	/** 更新用户信息 */
	@RequestMapping("/updateUserInfo")
	@SystemControllerLog(description = "更新用户信息")
	public @ResponseBody Object updateUserInfo(Long userId, int sex, @RequestParam MultipartFile portrait) {
		JSONObject jsonObject = null;
		String resultPath = "";
		if (!portrait.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String filePath = "/upload/userInfo/image/portrait/" + sdf.format(new Date()) + "/";
			resultPath = BusinessUtil.fileUpload(portrait, filePath);
		}

		try {
			if(userId==null){
				System.out.println("userId="+userId);
				return new ResultJSONObject("004", "参数有误");
			}
			if (this.userService.updateUserInfo(userId, sex, resultPath)) {
				jsonObject = new ResultJSONObject("000", "成功修改用户个人信息");
				resultPath = BusinessUtil.disposeImagePath(resultPath);
				jsonObject.put("portraitPath", resultPath);
			} else {
				jsonObject = new ResultJSONObject("005", "修改用户个人信息失败,请重新尝试");
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updateUserInfo");
			jsonObject = new ResultJSONObject("updateUserInfo_exception", "更新用户信息失败");
			logger.error("更新用户信息失败", e);
		}
		return jsonObject;
	}

	/** 上传用户头像 */
	@RequestMapping("/uploadUserPortrait")
	@SystemControllerLog(description = "上传用户头像")
	public @ResponseBody Object uploadUserPortrait(Long userId, @RequestParam MultipartFile portrait) {
		JSONObject jsonObject = null;
		if (portrait.isEmpty()) {
			jsonObject = new ResultJSONObject("006", "头像图片为空");
		} else {
			try {
				String resultPath = "";
				if (!portrait.isEmpty()) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					String filePath = "/upload/userInfo/image/portrait/" + sdf.format(new Date()) + "/";
					resultPath = BusinessUtil.fileUpload(portrait, filePath);
				}
				try {
					if (this.userService.uploadUserPortrait(userId, resultPath)) {
						jsonObject = new ResultJSONObject("000", "成功上传用户头像");
						resultPath = BusinessUtil.disposeImagePath(resultPath);
						jsonObject.put("portraitPath", resultPath);

					} else {
						jsonObject = new ResultJSONObject("007", "上传用户头像出错");
					}
				} catch (Exception e) {
					MsgTools.sendMsgOrIgnore(e, "uploadUserPortrait");
					jsonObject = new ResultJSONObject("uploadUserPortrait_exception", "上传用户头像失败");
					logger.error("上传用户头像失败", e);
				}
			} catch (Exception e) {
				MsgTools.sendMsgOrIgnore(e, "uploadUserPortrait");
				jsonObject = new ResultJSONObject("007", "上传用户头像出错");
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	/** 获取用户的位置信息 */
	@RequestMapping("/getUserAddressInfo")
	@SystemControllerLog(description = "获取用户位置信息")
	public @ResponseBody Object getUserAddressInfo(Long userId, String addressType) {
		JSONObject jsonObject = null;
		List<Map<String, Object>> resultMap = null;
		try {
			resultMap = this.userService.getUserAddressInfo(userId, addressType);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserAddressInfo");
			jsonObject = new ResultJSONObject("getUserAddressInfo_exception", "获取用户的位置信息失败");
			logger.error("获取用户的位置信息失败", e);
		}
		jsonObject = new ResultJSONObject("000", "获取用户的位置信息成功");
		jsonObject.put("addressInfo", resultMap);
		return jsonObject;
	}

	/** 更新用户地址信息 */
	@RequestMapping("/updateUserAddressInfo")
	@SystemControllerLog(description = "更新用户位置信息")
	public @ResponseBody Object updateUserAddressInfo(Long userId, String addressType, Double latitude, Double longitude, String addressInfo) {
		JSONObject jsonObject = null;
		try {
			this.userService.updateUserAddressInfo(userId, addressType, latitude, longitude, addressInfo);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updateUserAddressInfo");
			jsonObject = new ResultJSONObject("updateUserAddressInfo_exception", "更新用户地址信息失败");
			logger.error("更新用户地址信息失败", e);
		}
		jsonObject = new ResultJSONObject("000", "成功更新用户地址信息");
		return jsonObject;
	}

	/** 获取用户可以使用的代金券列表 */
	@RequestMapping("/getUserAvailableVouchersInfo")
	@SystemControllerLog(description = "获取用户当前可以使用的代金券列表")
	public @ResponseBody Object getUserAvailableVouchersInfo(Long userId, int pageNo) {
		JSONObject jsonObject = null;
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		int totalPage = 0;
		try {
			totalPage = this.userService.getUserAvailableVouchersCount(userId);
			if (totalPage != 0) {
				resultMap = this.userService.getUserAvailableVouchersInfo(userId, pageNo);
				totalPage = BusinessUtil.totalPageCalc(totalPage);
			}

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserAvailableVouchersInfo");
			jsonObject = new ResultJSONObject("getUserAvailableVouchersInfo_exception", "获取用户可以使用的代金券列表失败");
			logger.error("获取用户可以使用的代金券列表失败", e);
		}
		jsonObject = new ResultJSONObject("000", "获取用户可以使用的代金券列表成功");
		jsonObject.put("vouchersInfo", resultMap);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

	/** 获取用户历史的代金券列表 */
	@RequestMapping("/getUserHistoryVouchersInfo")
	@SystemControllerLog(description = "获取用户历史的代金券列表")
	public @ResponseBody Object getUserHistoryVouchersInfo(Long userId, int pageNo) {
		JSONObject jsonObject = null;
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		int totalPage = 0;
		try {
			totalPage = this.userService.getUserHistoryVouchersCount(userId);
			if (totalPage != 0) {
				resultMap = this.userService.getUserHistoryVouchersInfo(userId, pageNo);
				totalPage = BusinessUtil.totalPageCalc(totalPage);
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserHistoryVouchersInfo");
			jsonObject = new ResultJSONObject("getUserHistoryVouchersInfo_exception", "获取用户历史的代金券列表 失败");
			logger.error("获取用户历史的代金券列表 失败", e);
		}
		jsonObject = new ResultJSONObject("000", "获取用户历史的代金券列表成功");
		jsonObject.put("vouchersInfo", resultMap);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

	/** 删除代金券 */
	@RequestMapping("/deleteVouchersInfo")
	@SystemControllerLog(description = "删除用户代金券")
	public @ResponseBody Object deleteVouchersInfo(Long vouchersId, Long userId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.userService.deleteVouchersInfo(vouchersId, userId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "deleteVouchersInfo");
			jsonObject = new ResultJSONObject("deleteVouchersInfo_exception", "删除代金券失败");
			logger.error("删除代金券失败", e);
		}
		return jsonObject;
	}

	/** 获取用户信息 */
	@RequestMapping("/getUserInfo")
	@SystemControllerLog(description = "根据手机号获取用户信息")
	public @ResponseBody Object getUserInfo(String phone) {
		JSONObject jsonObject = null;
		try {
			Map<String, String> userInfo = this.userService.getUserInfoByPhoneWithStr(phone);
			if (userInfo != null) {
				jsonObject = new ResultJSONObject("000", "获取用户信息成功");
				jsonObject.put("userInfo", userInfo);
			} else {
				jsonObject = new ResultJSONObject("get_user_info_fail", "获取用户信息失败");
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserInfo");
			jsonObject = new ResultJSONObject("getUserInfo_exception", "获取用户信息失败");
			logger.error("获取用户信息失败", e);
		}
		return jsonObject;
	}

	/** 用户端退出应用 */
	@RequestMapping("/userExit")
	@SystemControllerLog(description = "用户端退出应用")
	public @ResponseBody Object userExit(Long userId, String clientId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.userService.cleanUserInfoPush(userId,clientId);
		} catch (ApplicationException e) {
			MsgTools.sendMsgOrIgnore(e, "userExit");
			jsonObject = new ResultJSONObject(e.getResult(), e.getMsg());
		}
		return jsonObject;
	}

	/** 验证用户是否被退出 */
	@RequestMapping("/checkClient")
	@SystemControllerLog(description = "验证用户是否被退")
	public @ResponseBody Object checkClient(String clientId, Long userId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.userService.checkClient(clientId, userId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "checkClient");
			jsonObject = new ResultJSONObject("checkClient_exception", "验证用户是否被退出失败");
			logger.error("验证用户是否被退出失败", e);
		}
		return jsonObject;
	}

	/** 更改当前使用的设备记录的clientId */
	@RequestMapping("/updateClientId")
	@SystemControllerLog(description = "更改当前使用的设备记录的clientId")
	public @ResponseBody Object updateClientId(String clientId, String pushId, Long userId, String clientType,String phoneModel) {
		if (pushId == null) {
			pushId = clientId;
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = this.userService.updateClientId(pushId, userId, clientType, clientId,phoneModel);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updateClientId");

			jsonObject = new ResultJSONObject("updateClientId_exception", "更改当前使用的设备记录的clientId失败");
			logger.error("更改当前使用的设备记录的clientId失败", e);
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

	
	/** 获取融云token信息 */
	@RequestMapping("/getRongCloudToken")
	@SystemControllerLog(description = "获取融云token信息")
	public @ResponseBody Object getRongCloudToken(String phone) {
		JSONObject jsonObject = new ResultJSONObject("getRongCloudToken_exception", "获取融云token信息失败");

		if (StringUtil.isNullStr(phone)) {
			jsonObject = new ResultJSONObject("001", "phone不能为空");
			return jsonObject;
		}
		String token = "";
		try {
			token = userService.getRongCloudToken(phone);
			if (!StringUtil.isNullStr(token)) {
				if ("error".equals(token)) {
					jsonObject = new ResultJSONObject("001", "获取token失败【融云服务器响应异常】");
					return jsonObject;
				} else if ("proxyerror".equals(token)) {
					jsonObject = new ResultJSONObject("001", "获取token失败【本地代理proxy服务器响应异常】");
					return jsonObject;
				} else {
					jsonObject = new ResultJSONObject("000", "获取融云token信息成功");
					jsonObject.put("token", token);
				}
			} else {
				jsonObject = new ResultJSONObject("001", "获取token失败【本地provider服务器响应异常】");
				return jsonObject;
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getRongCloudToken");

			jsonObject = new ResultJSONObject("getRongCloudToken_exception", "获取融云token信息失败");
			logger.error("获取融云token信息失败", e);
		}
		return jsonObject;
	}
}
