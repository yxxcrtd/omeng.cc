package com.shanjin.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.UploadFile;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.service.IDictionaryService;
import com.shanjin.service.IMyIncomeService;

/**
 * 商户版 我的收入
 */
@Controller
@RequestMapping("/myIncome")
public class MyIncomeController {

	// 本地失败日志记录对象
	private static final Logger logger = Logger.getLogger(MyIncomeController.class);

	@Reference
	private IDictionaryService dictionaryService;

	@Reference
	private IMyIncomeService myIncomeService;

	/**
	 * 我的收入
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 */
	@RequestMapping("/myIncomeShow")
	@SystemControllerLog(description = "我的收入")
	public @ResponseBody Object myIncomeShow(String appType, Long merchantId) {

		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.selectMyIncome(appType, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "myIncomeShow");

			jsonObject = new ResultJSONObject("myIncomeShow_exception", "我的收入失败");
			logger.error("我的收入失败", e);
		}
		return jsonObject;
	}

	/**
	 * 绑定银行卡 初期化（银行信息列表加载,当前商户已绑定银行卡查询）
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 */
	@RequestMapping("/bindingBank")
	@SystemControllerLog(description = "银行信息列表加载,当前商户已绑定银行卡查询")
	public @ResponseBody Object bindingBank(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.bindingBank(appType, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "bindingBank");

			jsonObject = new ResultJSONObject("bindingBank_exception", "绑定银行卡 初期化失败");
			logger.error("绑定银行卡 初期化失败", e);
		}
		return jsonObject;
	}

	/**
	 * 绑定银行卡 确定
	 * 
	 * @param requestParamMap
	 * @return
	 */
	@RequestMapping("/bankCardSave")
	@SystemControllerLog(description = "绑定银行卡")
	public @ResponseBody Object bankCardSave(@RequestParam Map<String, Object> requestParamMap) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.insertMerchantWithdraw(requestParamMap);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "bankCardSave");

			jsonObject = new ResultJSONObject("save_bank_card_failure", "绑定银行卡失败");
			logger.error("绑定银行卡失败", e);
		}
		return jsonObject;
	}

	/**
	 * 申请提现 初期化（可绑定的银行卡信息加载）
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 */
	@RequestMapping("/applyWithdrawInit")
	@SystemControllerLog(description = "可绑定的银行卡信息加载")
	public @ResponseBody Object applyWithdrawInit(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.applyWithdrawInit(appType, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "applyWithdrawInit");

			jsonObject = new ResultJSONObject("applyWithdrawInit_exception", "申请提现 初期化失败");
			logger.error("申请提现 初期化失败", e);
		}
		return jsonObject;
	}

	/**
	 * 申请提现 确定
	 * 
	 * @param appType
	 * @param merchantId
	 * @param withdraw
	 *            银行卡类型
	 * @param withdrawNo
	 *            银行卡号
	 * @param withdrawPrice
	 *            提现金额
	 * @param payPassword
	 *            支付密码
	 * @return
	 */
	@RequestMapping("/applyWithdrawSave")
	@SystemControllerLog(description = "申请提现")
	public @ResponseBody Object applyWithdrawSave(String appType, Long merchantId, Long withdraw, String withdrawNo, String withdrawPrice, 
			String payPassword,@RequestParam(required = false) Short tip) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.insertApplyWithdrawRecord(appType, merchantId, withdraw, withdrawNo, withdrawPrice, payPassword,tip);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "applyWithdrawSave");

			jsonObject = new ResultJSONObject("apply_withdraw_failure", "申请提现失败");
			logger.error("申请提现失败", e);
		}
		return jsonObject;
	}

	/**
	 * 收支明细
	 * 
	 * @param appType
	 * @param merchantId
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/paymentDetails")
	@SystemControllerLog(description = "收支明细")
	public @ResponseBody Object paymentDetails(String appType, Long merchantId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.selectPaymentDetails(appType, merchantId, pageNo);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "paymentDetails");

			jsonObject = new ResultJSONObject("paymentDetails_exception", "收支明细失败");
			logger.error("收支明细失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取已绑定银行卡
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 */
	@RequestMapping("/getBindedBankCards")
	@SystemControllerLog(description = "获取已绑定银行卡")
	public @ResponseBody Object getBindedBankCards(String appType, Long merchantId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.getBindedBankCards(appType, merchantId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getBindedBankCards");

			jsonObject = new ResultJSONObject("getBindedBankCards_exception", "获取已绑定银行卡失败");
			logger.error("获取已绑定银行卡失败", e);
		}
		return jsonObject;
	}

	/**
	 * 找回支付密码
	 * 
	 * @param requestParamMap
	 *            前端传到后台的参数
	 * @return
	 */
	@RequestMapping("/findPayPassWord")
	@SystemControllerLog(description = "找回支付密码")
	public @ResponseBody Object findPayPassWord(@RequestParam Map<String, Object> requestParamMap) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.findPayPassWord(requestParamMap);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "findPayPassWord");

			jsonObject = new ResultJSONObject("findPayPassWord_exception", "找回支付密码失败");
			logger.error("找回支付密码失败", e);
		}
		return jsonObject;
	}

	/**
	 * 解绑银行卡
	 * 
	 * @param appType
	 *            应用类型
	 * @param merchantId
	 *            商户ID
	 * @param withdrawId
	 *            商户-银行卡表ID
	 * @param payPassword
	 *            支付密码
	 * @return
	 */
	@RequestMapping("/unbindBankCard")
	@SystemControllerLog(description = "解绑银行卡")
	public @ResponseBody Object unbindBankCard(String appType, Long merchantId, Long withdrawId, String payPassword) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.unbindBankCard(appType, merchantId, withdrawId, payPassword);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "unbindBankCard");

			jsonObject = new ResultJSONObject("unbindBankCard_exception", "解绑银行卡失败");
			logger.error("解绑银行卡失败", e);
		}
		return jsonObject;
	}

	/**
	 * 验证支付密码
	 * 
	 * @param appType
	 *            应用类型
	 * @param merchantId
	 *            商户ID
	 * @param payPassword
	 *            支付密码
	 * @return 验证结果
	 */
	@RequestMapping("/verificationPayPassword")
	@SystemControllerLog(description = "验证支付密码")
	public @ResponseBody Object verificationPayPassword(String appType, Long merchantId, String payPassword) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.verificationPayPassword(appType, merchantId, payPassword);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "verificationPayPassword");

			jsonObject = new ResultJSONObject("verificationPayPassword_exception", "验证支付密码失败");
			logger.error("验证支付密码失败", e);
		}
		return jsonObject;
	}

	/**
	 * 修改支付密码
	 * 
	 * @param appType
	 * @param merchantId
	 * @param newPayPassword
	 * @return
	 */
	@RequestMapping("/updPayPassword")
	@SystemControllerLog(description = "修改支付密码")
	public @ResponseBody Object updPayPassword(String appType, Long merchantId, String newPayPassword) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.updPayPassword(appType, merchantId, newPayPassword);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "updPayPassword");

			jsonObject = new ResultJSONObject("updPayPassword_exception", "修改支付密码失败");
			logger.error("修改支付密码失败", e);
		}
		return jsonObject;
	}

	/**
	 * 订单余额充值申请
	 * 
	 * @param appType
	 * @param merchantId
	 * @param money
	 *            申请金额
	 * @return
	 */
	@RequestMapping("/topupApply")
	@SystemControllerLog(description = "订单余额充值申请")
	public @ResponseBody Object topupApply(@RequestParam Map<String, Object> requestParamMap,HttpServletRequest request) {
		// map中需要有appType, merchantId, money这三个参数
		JSONObject jsonObject = null;
		try {
			//保存支付凭证附件
			String imageUploadPath = new StringBuilder(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("payEvidence").toString();
			String voiceUPloadPath = new StringBuilder(Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH).append("payEvidence").toString();
			List<String> paths = UploadFile.uplaodFile(request, voiceUPloadPath, imageUploadPath);
			
			int applyStatus = 0;//苹果设备不需要上传凭证，申请状态为待支付，安卓设备需要上传凭证，申请状态为待开通
			String clientType = request.getHeader("CLIENT_TYPE");
			if("2".equals(clientType)){//ios
				
			}else{//android
				if(paths == null || paths.size() == 0){
//					return new ResultJSONObject("upload_pay_voucher", "请上传转账凭证。");
				}else{
					applyStatus = 1;
				}
			}
			requestParamMap.put("paths", paths);//附件列表
			requestParamMap.put("clientType", clientType);//大后台需要判断记录来源是安卓还是ios
			requestParamMap.put("applyStatus", applyStatus);
			requestParamMap.put("payType", 3);
			requestParamMap.put("payNo", UUID.randomUUID().toString().replace("-", ""));
			jsonObject = this.myIncomeService.topupApply(requestParamMap);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "topupApply");

			jsonObject = new ResultJSONObject("topupApply_exception", "订单余额充值申请失败");
			logger.error("订单余额充值申请失败", e);
		}
		return jsonObject;
	}


	/**
	 * 查看银行转账凭证
	 * @param requestParamMap
	 * @return
	 */
	@RequestMapping("/viewPayVoucher")
	@SystemControllerLog(description = "查看银行转账凭证")
	public @ResponseBody Object viewPayVoucher(@RequestParam Map<String, Object> requestParamMap) {
		// map中需要有payNo 支付凭证流水号
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.viewTopupApply(requestParamMap);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "viewPayVoucher");

			jsonObject = new ResultJSONObject("view_pay_voucher_exception", "查看银行转账凭证失败");
			logger.error("查看银行转账凭证失败", e);
		}
		return jsonObject;
	}

	/**
	 * 更新银行凭证
	 * @param requestParamMap
	 * @param request
	 * @return
	 */
	@RequestMapping("/updatePayVoucher")
	@SystemControllerLog(description = "更新转账凭证")
	public @ResponseBody Object updatePayVoucher(@RequestParam Map<String, Object> requestParamMap,HttpServletRequest request) {
		// map中需要有type、payNo这个参数
		JSONObject jsonObject = new ResultJSONObject("000", "上传转账凭证成功。");;
		try {
			//保存支付凭证附件
			String imageUploadPath = new StringBuilder(Constant.MERCHANT_IMAGE_UPLOAD_BASE_PTAH).append("payEvidence").toString();
			String voiceUPloadPath = new StringBuilder(Constant.MERCHANT_VOICE_UPLOAD_BASE_PTAH).append("payEvidence").toString();
			List<String> paths = UploadFile.uplaodFile(request, voiceUPloadPath, imageUploadPath);
			requestParamMap.put("paths", paths);

			String clientType = request.getHeader("CLIENT_TYPE");
			if("2".equals(clientType)){//ios
				
			}else{//android
				if(paths == null || paths.size() == 0){
					return new ResultJSONObject("upload_pay_voucher", "请上传转账凭证。");
				}
			}
			this.myIncomeService.updatePayVoucher(requestParamMap);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "updateTopupApply");

			jsonObject = new ResultJSONObject("upload_pay_voucher_exception", "上传转账凭证失败。");
			logger.error("上传转账凭证失败", e);
		}
		return jsonObject;
	}
	
	
	/**
	 * 订单余额收支明细
	 * 
	 * @param appType
	 * @param merchantId
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/orderPaymentDetails")
	@SystemControllerLog(description = "订单余额收支明细")
	public @ResponseBody Object orderPaymentDetails(String appType, Long merchantId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.orderPaymentDetails(appType, merchantId, pageNo);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "orderPaymentDetails");

			jsonObject = new ResultJSONObject("orderPaymentDetails_exception", "订单记录明细加载失败");
			logger.error("订单记录明细加载失败", e);
		}
		return jsonObject;
	}
	
	
	/**
	 * 订单余额收支明细(最新版/添加了转出)
	 * 
	 * @param appType
	 * @param merchantId
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/queryPaymentList")
	@SystemControllerLog(description = "交易明细记录")
	public @ResponseBody Object queryPaymentList(String appType, Long merchantId, int pageNo,int paymentType) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.selectPaymentDetailsList(appType, merchantId, pageNo,0,paymentType);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "queryPaymentList");

			jsonObject = new ResultJSONObject("queryPaymentList_exception", "查询交易明细出错");
			logger.error("交易明细列表", e);
		}
		return jsonObject;
	}

	/**
	 * 订单余额收支明细详细信息查询(最新版/添加了转出)
	 * 
	 * @param appType
	 * @param merchantId
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/queryPaymentDetailInfo")
	@SystemControllerLog(description = "交易明细详细信息")
	public @ResponseBody Object queryPaymentDetailInfo(int paymentType,long paymentId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.myIncomeService.selectPaymentDetailInfo(paymentType,paymentId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "queryPaymentDetailInfo");
			jsonObject = new ResultJSONObject("queryPaymentDetailInfo_exception", "查询交易明细出错");
			logger.error("交易明细详情", e);
		}
		return jsonObject;
	}

	
}
