package com.shanjin.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.Sha1Util;
import com.shanjin.common.util.StringUtil;
import com.shanjin.common.util.TenpayUtil;
import com.shanjin.service.IActivityService;
import com.shanjin.service.IWechatService;

/**
 * 活动相关
 * 
 * @author
 *
 */
@Controller
@RequestMapping("/activity")
public class ActivityController {

	@Autowired
	private IWechatService wechatService;

	@Autowired
	private IActivityService activityService;

	private static final Logger logger = Logger
			.getLogger(ActivityController.class);

	/** 获取商铺开店信息和剪彩统计信息 */
	@RequestMapping("/getCuttingInfo")
	@SystemControllerLog(description = "获取商铺开店信息和剪彩统计信息")
	public @ResponseBody Object getCuttingInfo(String merchantId) {
		JSONObject jsonObject = null;
		if (StringUtil.isNullStr(merchantId)) {
			jsonObject = new ResultJSONObject("001", "商户ID不可为空");
			return jsonObject;
		}
		try {
			jsonObject = activityService.getCuttingInfo(merchantId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getCuttingInfo_exception",
					"获取商铺开店信息和剪彩统计信息失败");
			logger.error("获取商铺开店信息和剪彩统计信息失败", e);
		}
		return jsonObject;
	}

	/** 获取剪彩统计信息分页 */
	@RequestMapping("/getLabelList")
	@SystemControllerLog(description = "获取剪彩统计信息分页")
	public @ResponseBody Object getLabelList(String merchantId, int pageIndex,
			int pageSize) {
		JSONObject jsonObject = null;
		try {
			jsonObject = activityService.getLabelList(merchantId, pageIndex,
					pageSize);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getLabelList_exception",
					"获取剪彩统计信息分页失败");
			logger.error("获取剪彩统计信息分页失败", e);
		}
		return jsonObject;
	}

	/** 保存剪彩信息 */
	@RequestMapping("/saveCuttingInfo")
	@SystemControllerLog(description = "保存剪彩信息")
	public @ResponseBody Object saveCuttingInfo(String merchantId,
			String label, String labelName, String demand, String openId) {
		JSONObject jsonObject = null;
		if (StringUtil.isNullStr(merchantId)) {
			jsonObject = new ResultJSONObject("001", "商户ID不可为空");
			return jsonObject;
		}
		if (StringUtil.isNullStr(label)) {
			jsonObject = new ResultJSONObject("002", "标签不可为空");
			return jsonObject;
		}
		if (StringUtil.isNullStr(openId)) {
			jsonObject = new ResultJSONObject("003", "用户openId不可为空");
			return jsonObject;
		}
		if (!StringUtil.isNullStr(demand)) {
			if (demand.length() > 10) {
				jsonObject = new ResultJSONObject("004", "贺词超出限制");
				return jsonObject;
			}

		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("merchantId", merchantId);
		param.put("type", "1");
		param.put("openId", openId);
		Map<String, Object> user = wechatService.getUser(openId);

		if (user != null) {
			param.put("nickname", user.get("nickname"));
			param.put("headimgurl", user.get("headimgUrl"));
		} else {
//			 param.put("nickname", "未知");
//			 param.put("headimgurl",
//			 "http://omg.oomeng.cn/manFile/image/recommend/1457666328697.png");
			jsonObject = new ResultJSONObject("006", "用户信息不可为空");
			return jsonObject;
		}
		param.put("label", label);
		param.put("labelName", labelName);
		param.put("demand", demand);

		try {
			jsonObject = activityService.saveCuttingInfo(param);
		} catch (Exception e) {
			// MsgTools.sendMsgOrIgnore(e, "saveCuttingInfo");
			jsonObject = new ResultJSONObject("saveCuttingInfo_exception",
					"保存剪彩信息失败");
			logger.error("保存剪彩信息失败", e);
		}

		return jsonObject;
	}

	/** 商家信息H5 */
	@RequestMapping("/merchantInfoForH5")
	@SystemControllerLog(description = "商家信息H5")
	public @ResponseBody Object merchantInfoForH5(Long merchantId, @RequestParam(required = false)Double longitude,
			@RequestParam(required = false)Double latitude, @RequestParam(required = false)String openId) {
		JSONObject jsonObject = null;
		try{
			jsonObject = this.activityService.merchantInfoForH5(merchantId, longitude, latitude, openId);
		} catch (Exception e) {
//			MsgTools.sendMsgOrIgnore(e, "merchantInfoForH5");
			jsonObject = new ResultJSONObject("merchantInfoForH5_exception", "商家信息H5查询失败");
			logger.error("商家信息H5查询失败", e);
		}
		return jsonObject;
	}

	/** 商户是否设置过接单计划 */
	@RequestMapping("/alreadySetOrderPlan")
	@SystemControllerLog(description = "商户是否设置过接单计划")
	public @ResponseBody Object alreadySetOrderPlan(Long merchantId) {
		JSONObject jsonObject = null;
		try{
			jsonObject = this.activityService.alreadySetOrderPlan(merchantId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("alreadySetOrderPlan_exception", "商户是否设置过接单计划查询失败");
			logger.error("商户是否设置过接单计划查询失败", e);
		}
		return jsonObject;
	}
	
	
	/** 商家宣传信息 */
	@RequestMapping("/getMerchantInfo")
	@SystemControllerLog(description = "商家宣传信息")
	public @ResponseBody Object getMerchantInfo(String merchantId) {
		JSONObject jsonObject = null;
		try{
			jsonObject = this.activityService.getMerchantInfo(merchantId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getMerchantInfo_exception", "商家宣传信息查询失败");
			logger.error("商家宣传信息查询失败", e);
		}
		return jsonObject;
	}
	
	/** 获取发单活动详情 */
	@RequestMapping("/getOrderActivityDetail")
	@SystemControllerLog(description = "获取发单活动详情")
	public @ResponseBody Object getOrderActivityDetail(String activityId) {
		JSONObject jsonObject = null;
		if(StringUtil.isNullStr(activityId)){
			jsonObject = new ResultJSONObject("001", "参数异常");
			return jsonObject;
		}
		
		try{
			jsonObject = this.activityService.getOrderActivityDetail(activityId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getMerchantInfo_exception", "获取发单活动详情失败");
			logger.error("获取发单活动详情失败", e);
		}
		return jsonObject;
	}
	
	/** 1000万粉丝计划 */
	@RequestMapping("/getMerchantInfoFanSi")
	@SystemControllerLog(description = "1000万粉丝计划")
	public @ResponseBody Object getMerchantInfoFanSi(Long merchantId, @RequestParam(required = false)String openId) {
		JSONObject jsonObject = null;
		try{
			jsonObject = this.activityService.getMerchantInfoFanSi(merchantId,openId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getMerchantInfoFanSi_exception", "1000万粉丝计划商家信息查询失败");
			logger.error("1000万粉丝计划商家信息查询失败", e);
		}
		return jsonObject;
	}
	
	/** 1000万粉丝计划,城市排行榜 */
	@RequestMapping("/getRankingFanSiByCity")
	@SystemControllerLog(description = "1000万粉丝计划,城市排行榜")
	public @ResponseBody Object getRankingFanSiByCity(Long merchantId) {
		JSONObject jsonObject = null;
		try{
			jsonObject = this.activityService.getRankingFanSiByCity(merchantId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getMerchantInfoFanSi_exception", "1000万粉丝计划,城市排行榜查询失败");
			logger.error("1000万粉丝计划,城市排行榜查询失败", e);
		}
		return jsonObject;
	}
	
	/** 1000万粉丝计划,城市排行榜 分页*/
	@RequestMapping("/getRankingFanSiByCityPage")
	@SystemControllerLog(description = "1000万粉丝计划,城市排行榜分页")
	public @ResponseBody Object getRankingFanSiByCityPage(Long merchantId, int pageIndex,
			int pageSize) {
		JSONObject jsonObject = null;
		try{
			jsonObject = this.activityService.getRankingFanSiByCityPage(merchantId,pageIndex,pageSize);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getRankingFanSiByCityPage_exception", "1000万粉丝计划,城市排行榜查询失败");
			logger.error("1000万粉丝计划,城市排行榜分页查询失败", e);
		}
		return jsonObject;
	}
	
	/** 获取活动分享详情 */
	@RequestMapping("/getActivityShare")
	@SystemControllerLog(description = "获取活动分享详情")
	public @ResponseBody Object getActivityShare(String activityId) {
		JSONObject jsonObject = null;
		if(StringUtil.isNullStr(activityId)){
			jsonObject = new ResultJSONObject("001", "活动Id不可为空");
			return jsonObject;
		}
		
		try{
			jsonObject = this.activityService.getActivityShare(activityId);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("getMerchantInfo_exception", "获取活动分享详情失败");
			logger.error("获取活动分享详情失败", e);
		}
		return jsonObject;
	}
	
//	/** 测试*/
//	@RequestMapping("/test")
//	@SystemControllerLog(description = "测试红包")
//	public @ResponseBody Object test(String merchantId) {
//		JSONObject jsonObject = null;
//		Map<String,Object> param = new HashMap<String,Object>();
//		param.put("merchantId", merchantId);
//		param.put("total_fee", 1);
//		
//		param.put("nickname", "未知");
//		param.put("headimgurl", "");
//		param.put("type", "2");
//		param.put("label", "label7");
//		param.put("labelName","红包");
//		param.put("demand","hahahah");
//		
//		String appid ="wx1303f436d8e44d80";
//		String orderNo = appid + Sha1Util.getTimeStamp();
//		
//		param.put("out_trade_no", orderNo);
//		
//		String currTime = TenpayUtil.getCurrTime();
//		// 8位日期
//		String strTime = currTime.substring(8, currTime.length());
//		// 四位随机数
//		String strRandom = TenpayUtil.buildRandom(4) + "";
//		// 10位序列号,可以自行调整。
//		String strReq = strTime + strRandom;
//		System.out.println("strReq:"+strReq);
//		param.put("transaction_id", strReq);
//		param.put("openId", "olYzZs_TUmhunSjPUCjZJIBERVKg");
//		boolean flag=false;
//		try {
//			jsonObject=activityService.saveCuttingInfo(param);
//			System.out.println(jsonObject.getString("resultCode"));
//			if(jsonObject==null || !jsonObject.getString("resultCode").equals("000")){
//				System.out.println("失败");
//			}else{
//				flag=true;
//				System.out.println("成功");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return flag;
//	}
}
