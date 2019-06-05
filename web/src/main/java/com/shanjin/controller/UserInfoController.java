package com.shanjin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
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
	@Resource
	private IUserInfoService userService;

	/**
	 * 获取用户验证码
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getVerificationCode")
	@SystemControllerLog(description = "获取用户验证码")
	public @ResponseBody Object getVerificationCode(String appType, String phone, String clientId, @RequestParam(required = false) String handsetMakers, @RequestParam(required = false) String mobileVersion, @RequestParam(required = false) String mobileNumber) throws InterruptedException {
		JSONObject jsonObject = null;
		int code = this.userService.getVerificationCode(appType, phone, clientId, handsetMakers, mobileVersion, mobileNumber);
		if (code > 0) {
			jsonObject = new ResultJSONObject("000", "验证码发送成功");
		} else {
			jsonObject = new ResultJSONObject("001", "验证码发送失败,请稍后重试");
		}
		return jsonObject;
	}

	/** 验证用户验证码 */
	@RequestMapping("/validateVerificationCode")
	@SystemControllerLog(description = "验证用户验证码")
	public @ResponseBody Object validateVerificationCode(String appType, String phone, String verificationCode, String clientId, String clientType) {
		JSONObject jsonObject = null;
		jsonObject = this.userService.validateVerificationCode(appType, phone, verificationCode, clientId, clientType);
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
		if (this.userService.updateUserInfo(userId, sex, resultPath)) {
			jsonObject = new ResultJSONObject("000", "成功修改用户个人信息");
		} else {
			jsonObject = new ResultJSONObject("005", "修改用户个人信息失败,请重新尝试");
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

				if (this.userService.uploadUserPortrait(userId, resultPath)) {
					jsonObject = new ResultJSONObject("000", "成功上传用户头像");
				} else {
					jsonObject = new ResultJSONObject("007", "上传用户头像出错");
				}

			} catch (Exception e) {
				jsonObject = new ResultJSONObject("007", "上传用户头像出错");
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	/** 获取用户的位置信息 */
	@RequestMapping("/getUserAddressInfo")
	@SystemControllerLog(description = "获取用户位置信息")
	public @ResponseBody Object getUserAddressInfo(String appType, Long userId, String addressType) {
		JSONObject jsonObject = null;
		List<Map<String, Object>> resultMap = this.userService.getUserAddressInfo(appType, userId, addressType);
		jsonObject = new ResultJSONObject("000", "获取用户的位置信息成功");
		jsonObject.put("addressInfo", resultMap);
		return jsonObject;
	}

	/** 更新用户地址信息 */
	@RequestMapping("/updateUserAddressInfo")
	@SystemControllerLog(description = "更新用户位置信息")
	public @ResponseBody Object updateUserAddressInfo(String appType, Long userId, String addressType, Double latitude, Double longitude, String addressInfo) {
		JSONObject jsonObject = null;
		if (this.userService.updateUserAddressInfo(appType, userId, addressType, latitude, longitude, addressInfo)) {
			jsonObject = new ResultJSONObject("000", "成功更新用户地址信息");
		} else {
			jsonObject = new ResultJSONObject("005", "更新用户地址信息失败,请重新尝试");
		}
		return jsonObject;
	}

	/** 用户反馈 */
	@RequestMapping("/feedback")
	@SystemControllerLog(description = "用户反馈")
	public @ResponseBody Object feedback(String appType, Long userId, String content) {
		JSONObject jsonObject = null;
		if (this.userService.feedback(appType, userId, content)) {
			jsonObject = new ResultJSONObject("000", "成功反馈信息");
		} else {
			jsonObject = new ResultJSONObject("001", "反馈信息失败，请稍后重试");
		}
		return jsonObject;
	}

	/** 获取用户可以使用的代金券列表 */
	@RequestMapping("/getUserAvailableVouchersInfo")
	@SystemControllerLog(description = "获取用户当前可以使用的代金券列表")
	public @ResponseBody Object getUserAvailableVouchersInfo(String appType, Long userId, int pageNo) {
		JSONObject jsonObject = null;
		List<Map<String, Object>> resultMap = this.userService.getUserAvailableVouchersInfo(appType, userId, pageNo);
		int totalPage = this.userService.getUserAvailableVouchersCount(appType, userId);
		totalPage = BusinessUtil.totalPageCalc(totalPage);
		jsonObject = new ResultJSONObject("000", "获取用户可以使用的代金券列表成功");
		jsonObject.put("vouchersInfo", resultMap);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

	/** 获取用户历史的代金券列表 */
	@RequestMapping("/getUserHistoryVouchersInfo")
	@SystemControllerLog(description = "更新用户历史代金券列表")
	public @ResponseBody Object getUserHistoryVouchersInfo(String appType, Long userId, int pageNo) {
		JSONObject jsonObject = null;
		List<Map<String, Object>> resultMap = this.userService.getUserHistoryVouchersInfo(appType, userId, pageNo);
		int totalPage = this.userService.getUserHistoryVouchersCount(appType, userId);
		totalPage = BusinessUtil.totalPageCalc(totalPage);
		jsonObject = new ResultJSONObject("000", "获取用户历史的代金券列表成功");
		jsonObject.put("vouchersInfo", resultMap);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

	/** 删除代金券 */
	@RequestMapping("/deleteVouchersInfo")
	@SystemControllerLog(description = "更新用户历史代金券列表")
	public @ResponseBody Object deleteVouchersInfo(Long vouchersId, Long userId) {
		JSONObject jsonObject = null;
		jsonObject = this.userService.deleteVouchersInfo(vouchersId, userId);
		return jsonObject;
	}

	/** 获取用户信息 */
	@RequestMapping("/getUserInfo")
	@SystemControllerLog(description = "根据手机号获取用户信息")
	public @ResponseBody Object getUserInfo(String phone) {
		JSONObject jsonObject = null;
		Map<String, Object> userInfo = this.userService.getUserInfoByPhoneWithStr(phone);
		jsonObject = new ResultJSONObject("000", "获取用户信息成功");
		jsonObject.put("userInfo", userInfo);
		return jsonObject;
	}

	/** 用户端退出应用 */
	@RequestMapping("/userExit")
	@SystemControllerLog(description = "用户端退出应用")
	public @ResponseBody Object userExit(String clientId) {
		return this.userService.cleanUserInfoPush(clientId);
	}

	/** 判断用户是否处于登陆状态 */
	@RequestMapping("/checkClient")
	@SystemControllerLog(description = "判断用户是否处于登陆状态")
	public @ResponseBody Object checkClient(String appType, String clientId, String userId) {
		JSONObject jsonObject = new ResultJSONObject();
		jsonObject = userService.checkClient(appType, clientId, userId);
		return jsonObject;
	}
}
