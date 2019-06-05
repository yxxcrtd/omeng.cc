package com.alipay.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.IMyIncomeService;
import com.shanjin.service.IMyMerchantService;

import java.util.HashMap;
import java.util.Map;

public class AlipayMerchantInfoService {
	@Reference
	private static IMyMerchantService myMerchantService;
	@Reference
	private static IMyIncomeService myIncomeService;

	public static boolean vipApply(String outTradeNo, String money, String tradeNo) {
		String[] paras = outTradeNo.split("iii");
		String payNo = "alipayiii" + outTradeNo;
		Long merchantId = 0L;
		String appType = "";
		// 检查参数
		if (paras[0] != null) {
			merchantId = Long.parseLong(StringUtil.null2Str(paras[0]));
		} else {
			return false;
		}
		if (paras[1] != null) {
			appType = StringUtil.null2Str(paras[1]);
		} else {
			return false;
		}
		System.out.println("支付宝支付");
		System.out.println("outTradeNo:" + outTradeNo);
		System.out.println("appType:" + appType);
		System.out.println("merchantId:" + merchantId);
		System.out.println("money:" + money);
		System.out.println("支付宝交易号:" + tradeNo);
		try {
			myMerchantService.vipApply(appType, merchantId, money, 2, payNo, "1", tradeNo,null);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return true;
	}

	public static boolean topupApply(String outTradeNo, String money, String tradeNo) throws Exception {
		String[] paras = outTradeNo.split("iii");
		Long merchantId = 0L;
		String appType = "";
		// 检查参数
		if (paras[0] != null) {
			merchantId = Long.parseLong(StringUtil.null2Str(paras[0]));
		} else {
			return false;
		}
		if (paras[1] != null) {
			appType = StringUtil.null2Str(paras[1]);
		} else {
			return false;
		}

		Map<String, Object> requestParamMap = new HashMap<String, Object>();
		requestParamMap.put("merchantId", merchantId);
		requestParamMap.put("appType", appType);
		requestParamMap.put("money", money);
        requestParamMap.put("applyStatus", 2);
        requestParamMap.put("payNo", "alipayiii" + outTradeNo);
        requestParamMap.put("payType", "1");
        requestParamMap.put("tradeNo", tradeNo);
        myIncomeService.topupApply(requestParamMap);
		return true;
	}

	public static boolean increaseEmployeeNumApply(int pkgId, String outTradeNo, String money,String tradeNo) throws Exception {
		String[] paras = outTradeNo.split("iii");
		String payNo = "alipayiii" + outTradeNo;
		Long merchantId = 0L;
		Integer increaseEmployeeNum = 0;
		String appType = "";

		// 检查参数
		if (paras[0] != null) {
			merchantId = Long.parseLong(StringUtil.null2Str(paras[0]));
		} else {
			return false;
		}
		if (paras[1] != null) {
			increaseEmployeeNum = Integer.parseInt(StringUtil.null2Str(paras[1]));
		} else {
			return false;
		}
		if (paras[2] != null) {
			appType = StringUtil.null2Str(paras[2]);
		} else {
			return false;
		}
		System.out.println("支付宝支付");
		System.out.println("outTradeNo:" + outTradeNo);
		System.out.println("appType:" + appType);
		System.out.println("merchantId:" + merchantId);
		System.out.println("increaseEmployeeNum:" + increaseEmployeeNum);
		System.out.println("money:" + money);
		
		Map<String,Object> param = new HashMap<String,Object>(2);
		param.put("tradeNo", tradeNo);
		myMerchantService.increaseEmployeeNumApply(pkgId, appType, merchantId, increaseEmployeeNum, money, 2, payNo, "1",param);
		return true;
	}

	public IMyMerchantService getMyMerchantService() {
		return myMerchantService;
	}

	public void setMyMerchantService(IMyMerchantService myMerchantService) {
		AlipayMerchantInfoService.myMerchantService = myMerchantService;
	}

	public IMyIncomeService getMyIncomeService() {
		return myIncomeService;
	}

	public void setMyIncomeService(IMyIncomeService myIncomeService) {
		AlipayMerchantInfoService.myIncomeService = myIncomeService;
	}
}
