package com.shanjin.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.service.ICustomPriceService;

/**
 * 
 * 项目名称：dubbo-consumer
 * 类名称：CustomPriceController 
 * 类描述：自定义报价方案
 * 创建人：Huang yulai
 * 创建时间：2016年3月17日 下午2:32:57
 * 修改人：
 * 修改时间：
 * 修改备注：
 * @version V1.0
 */
@Controller
@RequestMapping("/customPrice")
public class CustomPriceController {

	@Reference
	private ICustomPriceService customPriceService;
	/**
	 * 获取自定义报价方案表单
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getPricePlanForm")
	@SystemControllerLog(description = "获取报价表单配置数据")
	public @ResponseBody Object getPricePlanForm(String serviceId) {
		JSONObject jsonObject = new ResultJSONObject("fail", "获取报价表单配置数据失败");
		
		try {
			jsonObject = customPriceService.getPricePlanForm(serviceId);

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "获取报价表单配置数据失败");
		}
		return jsonObject;
	}
}
