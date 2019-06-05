package com.shanjin.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IValidateService;

@Controller
@RequestMapping("/common")
public class CommonController {

	@Resource
	private ICommonService commonService;

	@Resource
	private IValidateService validateService;

	/** 获取当前应用程序所有的服务项目 */
	@RequestMapping("/serviceTypeName")
	@SystemControllerLog(description = "获取当前应用程序所有的服务项目")
	public @ResponseBody Object getServiceTypeName(String appType, Long parentId) {
		return this.commonService.getServiceInfo(appType, parentId);
	}

	/** 获取加密秘钥 */
	@RequestMapping("/regDevice")
	@SystemControllerLog(description = "获取加密秘钥")
	public @ResponseBody Object regDevice(String appType, String actionType, String deviceId) {
		JSONObject jsonObject = new ResultJSONObject();
		jsonObject = commonService.regDevice(appType, actionType, deviceId);
		return jsonObject;
	}

	/** 获得APP更新 */
	@RequestMapping("/checkUpdate")
	@SystemControllerLog(description = "获得APP更新")
	public @ResponseBody Object checkUpdate(String packageName, int version) {
		JSONObject jsonObject = new ResultJSONObject();
		jsonObject = commonService.checkUpdate(packageName, version);
		return jsonObject;
	}

	/** 获得所有应用程序列表 */
	@RequestMapping("/getAllApps")
	@SystemControllerLog(description = "获得APP更新")
	public @ResponseBody Object getAppList() {
		JSONObject jsonObject = new ResultJSONObject();
		jsonObject = commonService.getAllAppInfo();
		return jsonObject;
	}

	/** 获取动态密钥 */
	@RequestMapping("/getDynamicKey")
	@SystemControllerLog(description = "获得动态密钥")
	public @ResponseBody Object getDynamicKey(String clientId) {
		JSONObject jsonObject = new ResultJSONObject("000", "获得动态密钥成功");
		String dynamicKey = validateService.getDynamicKey(clientId);
		jsonObject.put("dynamicKey", dynamicKey);
		return jsonObject;
	}

	/** 获得轮播图 */
	@RequestMapping("/getSliderPics")
	@SystemControllerLog(description = "获得轮播图")
	public @ResponseBody Object getSliderPics(String appType) {
		JSONObject jsonObject = new ResultJSONObject("000", "获得轮播图成功");
		List<Map<String, Object>> sliderPics = commonService.getSliderPics(appType);
		jsonObject.put("sliderPics", sliderPics);
		return jsonObject;
	}

}
