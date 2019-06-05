package com.shanjin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.model.reward.GoodRewardInfo;
import com.shanjin.service.IGoodRewardService;
import com.shanjin.service.IValueAddedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 增值服务控制类
 * @author Huang yulai
 *
 */
@Controller
@RequestMapping("/valueAdd")
public class ValueAddController {
	
Logger logger = LoggerFactory.getLogger(ValueAddController.class);
	
	@Reference
	private IValueAddedService valueAddedService;
	@Autowired
	private IGoodRewardService goodRewardService;
	
	@RequestMapping("/getServicePackageDetail")
	@SystemControllerLog(description = "获取增值服务详情")
	public @ResponseBody Object getServicePackageDetail(String serviceId, String merchantId) {
		JSONObject jsonObject = null;
		try {
			 jsonObject = valueAddedService.getServicePackageDetail(serviceId, merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getServicePackageDetail");
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取增值服务详情错误");
		}
		return jsonObject;
	}
	
	/** 获取增值服务列表信息 */
	@RequestMapping("/getValueAddServiceList")
	@SystemControllerLog(description = "获取增值服务列表信息")
	public @ResponseBody Object getValueAddServiceList(Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject= this.valueAddedService.getValueAddServiceList(merchantId);
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getValueAddServiceList");
			
			jsonObject = new ResultJSONObject("getCitys_exception", "获取增值服务列表信息失败");
			logger.error("获取增值服务列表信息失败", e);	
		}
		return jsonObject;
	}

	/**
     * @return
     */
	@RequestMapping(value = "/goodRewardInfo")
	@ResponseBody
	public JSONObject getGoodRewardInfo(@RequestParam("merchantId") Long merchantId){

		JSONObject resultJSONObject = new JSONObject();

		//参数校验
		if(null ==merchantId){
			resultJSONObject.put("resultCode","001");
			resultJSONObject.put("message","参数不能为空");
			return resultJSONObject;
		}
		try {
			GoodRewardInfo info = goodRewardService.queryGoodRewardInfo(merchantId);
			resultJSONObject.put("resultCode","000");
			resultJSONObject.put("message","调用成功");
			resultJSONObject.put("data",info);
			logger.debug("好评奖励明细:"+ info.toString());
		} catch (Exception e) {
			resultJSONObject.put("resultCode","002");
			resultJSONObject.put("message","调用失败,失败原因:"+e.getMessage());
			logger.error("好评奖励明细查询异常",e);
		}

		return resultJSONObject;
	}

}
