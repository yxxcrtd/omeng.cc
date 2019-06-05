package com.shanjin.controller;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;

import com.shanjin.common.util.StringUtil;
import com.shanjin.service.IWeekStarService;

/**
 * 活动相关
 * @author 
 *
 */
@Controller
@RequestMapping("/weekStar")
public class WeekStarController {

	@Resource
	private IWeekStarService weekStarService;
	
	private static final Logger logger = Logger.getLogger(WeekStarController.class);
	
	/** 获取每周之星信息 */
	@RequestMapping("/getWeekStar")
	@SystemControllerLog(description = "获取商铺开店信息和剪彩统计信息")
	public @ResponseBody Object getWeekStar(String name) {
		JSONObject jsonObject = null;
		try {
			jsonObject = weekStarService.getWeekStar(name);
		} catch (Exception e) {
			
			jsonObject = new ResultJSONObject("getWeekStar_exception", "获取每周之星信息失败");
			logger.error("获取每周之星信息失败", e);
		}
		
		return jsonObject;
	}

	/** 获取地推人员详情 */
	@RequestMapping("/getWeekStarDetail")
	@SystemControllerLog(description = "获取地推人员详情")
	public @ResponseBody Object getWeekStarDetail(String employeeId,String authTotal) {
		JSONObject jsonObject = null;
		if(StringUtil.isNullStr(employeeId)){
			jsonObject = new ResultJSONObject("001", "员工ID不可为空");
			return jsonObject;
		}
		try {
			jsonObject = weekStarService.getWeekStarDetail(employeeId,authTotal);
		} catch (Exception e) {
			
			jsonObject = new ResultJSONObject("getWeekStarDetail_exception", "获取地推人员详情失败");
			logger.error("获取地推人员详情失败", e);
		}
		return jsonObject;
	}

    
}
