package com.shanjin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.service.IIpCityCacheService;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IDictionaryService;
import com.shanjin.service.IPushService;

@Controller
@RequestMapping("/push")
public class PushController {
	@Reference
	private IPushService pushService;
	@Resource
	private IIpCityCacheService ipCityCacheServices;

	private Logger logger = Logger.getLogger(PushController.class);
	
	/** 共通推送 */
	@RequestMapping("/commonPush")
	@SystemControllerLog(description = "推送信息给单独的商户版,直接读取参数")
	public @ResponseBody Object commonPush(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		
		//需要的参数为：appType，orderId,data（推送内容），userId, pushId,pushType
		
		System.out.println("-------推送发过来的参数-------" + params.toString());
		JSONObject jsonObject = null;		
		try {			
			pushService.asyncCommonPush(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "commonPush");
			jsonObject = new ResultJSONObject("commonPush-exception", "共通推送 失败");
			logger.error("共通推送 失败", e);
		}
		return jsonObject;
	}
	/** 订单推送*/
	@RequestMapping("/orderPush")
	@SystemControllerLog(description = "订单推送")
	public @ResponseBody Object orderPush(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		
		//参数：phone，orderId，data，pushType，pushWay，province，city

		System.out.println("-------推送发过来的参数：" + params.toString());
//		String province=(String)params.get("province");
//		String city=(String)params.get("city");
		JSONObject jsonObject = null;		
		try {
//			if (!StringUtil.isNotEmpty(province) || !StringUtil.isNotEmpty(province)) {			
//				if (!Constant.DEVMODE) {
//					String IP = IPutil.getIpAddr(request);
//					// 从缓存根据IP获得城市名称
//					JSONObject cachedIpAddress = ipCityCacheServices.getCity(IP);
//					if (cachedIpAddress != null) {
//						province = cachedIpAddress.getString("province");
//						city = cachedIpAddress.getString("city");
//					} else {
//						//根据IP从新浪百度查找省市信息
//						String[] provinceAndCity = BusinessUtil.getProvinceAndCityByIp(IP);
//						province = provinceAndCity[0];
//						city = provinceAndCity[1];
//					}
//				}
//			}
			//对省市格式进行处理
//			String[] provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
//			province = provinceAndCity[0];
//			city = provinceAndCity[1];
//			params.put("province", province);
//			params.put("city", city);
			pushService.asyncOrderPush(params);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "orderPush");
			jsonObject = new ResultJSONObject("orderPush_exception", "订单推送 失败");
			logger.error("订单推送 失败", e);
		}
		return jsonObject;
	}
	/** 测试推送 */
	@RequestMapping("/testPush")
	@SystemControllerLog(description = "测试推送")
	public @ResponseBody Object testPush(String appType, Long orderId, String phone) {
		JSONObject jsonObject = null;
		String province = "";
		String city = "";

		// 获得请求的参数
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("appType", appType);
		paras.put("orderId", orderId);
		paras.put("phone", phone);
		paras.put("province", province);
		paras.put("city", city);
		paras.put("data", "");
		try {
			 pushService.asyncOrderPush(paras);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "testPush");
			jsonObject = new ResultJSONObject("testPush_exception", "测试推送 失败");
			logger.error("测试推送 失败", e);
		}
		return jsonObject;
	}
}
