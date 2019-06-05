package com.shanjin.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.IOrderRewardService;

/**
 * 订单奖励
 * 
 * @author
 *  李杰
 */
@Controller
@RequestMapping("/orderReward")
public class OrderRewardController {

	@Autowired
	private IOrderRewardService orderRewardService;
	
	private static final Logger logger = Logger
			.getLogger(OrderRewardController.class);
	
	/** 获取商铺开店信息和剪彩统计信息 */
	@RequestMapping("/getOrderRewardByMerId")
	@SystemControllerLog(description = "获取商铺订单奖励活动收入")
	public @ResponseBody Object getOrderRewardByMerId(@RequestParam(required = false)String merchantId,String activityId) {
		JSONObject jsonObject = null;
		if (StringUtil.isNullStr(activityId)) {
			jsonObject = new ResultJSONObject("001", "活动ID不可为空");
			return jsonObject;
		}
		try {
			jsonObject = orderRewardService.getOrderRewardByMerId(merchantId,activityId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getOrderRewardByMerId_exception",
					"获取商铺订单奖励活动收入失败");
			logger.error("获取商铺订单奖励活动收入失败", e);
		}
		return jsonObject;
	}
	
	
	/** 获取商铺开店信息和剪彩统计信息 */
	@RequestMapping("/getOrderRewardList")
	@SystemControllerLog(description = "获取商铺订单奖励活动列表")
	public @ResponseBody Object getOrderRewardList(String merchantId,String activityId ,@RequestParam(required = false)String pageIndex,@RequestParam(required = false)String pageSize) {
		JSONObject jsonObject = null;
		if (StringUtil.isNullStr(merchantId)) {
			jsonObject = new ResultJSONObject("001", "商户ID不可为空");
			return jsonObject;
		}
		if (StringUtil.isNullStr(activityId)) {
			jsonObject = new ResultJSONObject("001", "活动ID不可为空");
			return jsonObject;
		}
		try {
			int pageNo=pageIndex==null?0:Integer.parseInt(pageIndex);
			int pagesize=pageSize==null?0:Integer.parseInt(pageSize);
			jsonObject = orderRewardService.getOrderRewardList(merchantId,activityId ,pageNo,pagesize);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getOrderRewardList_exception","获取商铺订单奖励活动列表失败");
			logger.error("获取商铺订单奖励活动列表失败", e);
		}
		return jsonObject;
	}
}
