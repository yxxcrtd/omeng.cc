package com.shanjin.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.service.IMyMerchantService;
import com.shanjin.service.IUserMerchantService;

/**
 * 用户商户控制器
 * 
 * @author 李焕民
 * @version 2015年5月22日
 *
 */

@Controller
@RequestMapping("/userMerchant")
public class UserMerchantController {
	@Reference
	private IUserMerchantService userMerchantService;

	@Reference
	private IMyMerchantService myMerchantService;

	// 本地失败日志记录对象
	private static final Logger logger = Logger.getLogger(UserMerchantController.class);
	/**
	 * 用户收藏商家
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/collectionMerchant")
	@SystemControllerLog(description = "用户收藏商家")
	public @ResponseBody Object collectionMerchant(Long userId, Long merchantId, Long receiveEmployeesId) throws InterruptedException {
		JSONObject jsonObject = null;
		try {
			jsonObject = userMerchantService.collectionMerchant(userId, merchantId,receiveEmployeesId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"collectionMerchant");
			jsonObject = new ResultJSONObject("collectionMerchant_exception", "用户收藏商家失败");
			logger.error("用户收藏商家失败", e);
		}
		return jsonObject;
	}

	/**
	 * 删除用户收藏商家
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/delCollectionMerchant")
	@SystemControllerLog(description = "删除用户收藏商家")
	public @ResponseBody Object delCollectionMerchant(Long userId, String merchantId) throws InterruptedException {
		JSONObject jsonObject = null;
		try {
			jsonObject = userMerchantService.delCollectionMerchant(userId, merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"delCollectionMerchant");
			jsonObject = new ResultJSONObject("delCollectionMerchant_exception", "删除用户收藏商家失败");
			logger.error("删除用户收藏商家失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获得收藏的商家信息
	 * 
	 * @throws InterruptedException
	 */
	@RequestMapping("/getCollectionMerchant")
	@SystemControllerLog(description = "获得收藏的商家信息")
	public @ResponseBody Object getCollectionMerchant(String appType, Long userId, int pageNo) throws InterruptedException {
		JSONObject jsonObject = null;
		try {
			jsonObject = userMerchantService.getCollectionMerchant(appType, userId, pageNo);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"getCollectionMerchant");
			
			jsonObject = new ResultJSONObject("getCollectionMerchant_exception", "获得收藏的商家信息失败");
			logger.error("获得收藏的商家信息失败", e);
		}
		return jsonObject;
	}

	/** 用户端查看店铺商品分类信息 */
	@RequestMapping("/getMerchantGoodsClassificationInfo")
	@SystemControllerLog(description = "用户端查看店铺商品分类信息")
	public @ResponseBody Object getMerchantGoodsClassificationInfo(String appType, Long userId, Long merchantId,String isSelect) {
		JSONObject jsonObject;
		try {
			jsonObject= this.myMerchantService.selectGoodsClassificationInfo(appType, merchantId,isSelect);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"getMerchantGoodsClassificationInfo");
			
			jsonObject = new ResultJSONObject("getMerchantGoodsClassificationInfo_exception", "用户端查看店铺商品分类信息失败");
			logger.error("用户端查看店铺商品分类信息失败", e);
		}
		return jsonObject;
	}

	/** 用户端查看店铺商品信息 */
	@RequestMapping("/getMerchantGoodsInfo")
	@SystemControllerLog(description = "用户端查看店铺商品信息")
	public @ResponseBody Object selectMerchantGoodsInfo(String appType, Long userId, Long merchantId, Long classificationId, int pageNo) {
		JSONObject jsonObject;
		try {
			jsonObject= this.myMerchantService.selectGoodsInfo(appType, merchantId, classificationId, pageNo);
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getMerchantGoodsInfo");
			
			jsonObject = new ResultJSONObject("getMerchantGoodsInfo_exception", "用户端查看店铺商品信息失败");
			logger.error("用户端查看店铺商品信息失败", e);
		}
		return jsonObject;
	}
}
