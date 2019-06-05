package com.shanjin.service.impl;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IMerchantInfoDao;
import com.shanjin.dao.IMerchantPaymentDetailsDao;
import com.shanjin.dao.IMerchantStatisticsDao;
import com.shanjin.dao.IMerchantTransoutDao;
/*import com.shanjin.opay.api.bean.Header;
import com.shanjin.opay.api.service.ITransferService;*/
import com.shanjin.service.ITransOutService;

/**
 * 
 * 商户店铺金额转入到钱包
 * @author xmsheng
 *
 */
@Service("transoutService")
public class TransOutServiceImpl implements ITransOutService {/*
	
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(TransOutServiceImpl.class);
	
	@Resource
	private IMerchantStatisticsDao merchantStatisticsDao;

	@Resource
	private IMerchantInfoDao merchantInfoDao;
	
	@Resource
	private IMerchantTransoutDao merchantTransoutDao;

	@Reference
	private ITransferService transferService;
	
	@Resource
	private IMerchantPaymentDetailsDao merchantPaymentDetailsDao;
	

	*//**
	 * 备注 ： moneyAmount 使用String类型，防止精度丢失
	 *//*
	@Override
	public JSONObject transoutCheck(Map<String,Object> parasMap,Header header) {
		//参数校验
		if(null == parasMap.get("userId") || null == parasMap.get("merchantId") ||null ==parasMap.get("moneyAmount") 
				|| StringUtil.isEmpty(parasMap.get("userPhone"))){
			return new ResultJSONObject("params_error","参数错误");
		}
		//判断转出金额是否合法
		BigDecimal transAmount =(BigDecimal) parasMap.get("moneyAmount");
		if(((BigDecimal)parasMap.get("moneyAmount")).compareTo(new BigDecimal(0)) < 1){
			return new ResultJSONObject("transout_money_error","转出金额不能小于等于0");
		}
		//查询当月可转出的总金额
		Map<String,Object> merchantAccountMoneyRes =  merchantStatisticsDao.selectTransoutAmount((Long)parasMap.get("merchantId"));
		if(null == merchantAccountMoneyRes){
			return new ResultJSONObject("transout_account_error","商户账户余额不足");
		}
		
		BigDecimal avaliableTrasoutAmount = (BigDecimal) (merchantAccountMoneyRes.get("avaliableTrasoutAmount") == null
													? new BigDecimal(0) : merchantAccountMoneyRes.get("avaliableTrasoutAmount"));
		BigDecimal totalSurpluPrice = (BigDecimal) (merchantAccountMoneyRes.get("totalSurpluPrice") == null
												  ? new BigDecimal(0) : merchantAccountMoneyRes.get("totalSurpluPrice"));		
		if(avaliableTrasoutAmount.compareTo(transAmount) < 0){
			return new ResultJSONObject("transout_account_error","商户账户余额不足");
		}
		//判断用户是否有权限转出店铺收入到钱包
		if(merchantInfoDao.checkTransoutMerchantInfo(parasMap)<1){
			return new ResultJSONObject("check_transout_user_error","用户无权转出店铺金额至钱包");
		}
		//校验商户状态信息
		Map<String,Object> merchantInfos = merchantInfoDao.selectMerchantNameAndAuthStatusById((Long)parasMap.get("merchantId"));
		if(merchantInfos == null || 1 != (Integer)merchantInfos.get("auth_status")){
			return new ResultJSONObject("check_transout_merchant_error","商户未认证，暂时无法进行收入转出");
		}
	
		JSONObject  jsonObject =new ResultJSONObject("000","转出参数校验正常");
		//系统宕机正在交易的日志记录参数
		Map<String,Object> systeErrorParams = new HashMap<String,Object>();
		//记录系统异常日志
		systeErrorParams.put("transoutSeq", header.getTradeNo()); //交易流水
		systeErrorParams.put("userId", parasMap.get("userId")); //转出操作用户userId
		systeErrorParams.put("merchantId", parasMap.get("merchantId")); //商户id
		systeErrorParams.put("transoutMoney", transAmount.doubleValue()); //转账金额
		systeErrorParams.put("avaliableMoney", totalSurpluPrice.doubleValue()); //操作转账前账户总可用余额
		if(this.merchantTransoutDao.saveSystemErrorLogs(systeErrorParams)<1){
		  return new ResultJSONObject("check_transout_error","系统异常，请稍后重试");
		}
		jsonObject.put("errorId", systeErrorParams.get("errorId"));
		jsonObject.put("merchantName",  merchantInfos.get("name")); //设置商户名称
		return jsonObject;
	}
	

	public JSONObject transMoneyToWallet(Map<String,Object> paramsMap,Header header) throws Exception{
		JSONObject jsonObj = null;
		BigDecimal transAmount =(BigDecimal) paramsMap.get("moneyAmount");
		Map<String,Object> merchantStatisticsParams = new HashMap<String,Object>();
		merchantStatisticsParams.put("withdrawPrice", -transAmount.doubleValue());
		merchantStatisticsParams.put("merchantId", paramsMap.get("merchantId"));
	 try{
		//调用钱包转入服务
		 Map<String,Object> postParams = new HashMap<String,Object>();
		 postParams.put("moneyAmount", transAmount.doubleValue());
		 postParams.put("userId", paramsMap.get("userId"));
		 postParams.put("merchantId",  paramsMap.get("merchantId"));
		 postParams.put("userPhone", paramsMap.get("userPhone"));
		 postParams.put("merchantName", paramsMap.get("merchantName"));
		jsonObj = transferService.doProcess(header, postParams);
		logger.info(jsonObj);
		if(jsonObj == null || !"000".equals(jsonObj.getString("resultCode"))){
			//opay交易失败，回滚扣钱记录	
			if(merchantStatisticsDao.updatePrice(merchantStatisticsParams) < 1){
				return new ResultJSONObject("merchant_transout_error","商铺账户余额转出异常");
			}	
			return jsonObj;
		}	
		}catch(Exception e){
			logger.error(e);
			
			//opay异常，回滚扣钱记录
			if(merchantStatisticsDao.updatePrice(merchantStatisticsParams) < 1){
				return new ResultJSONObject("merchant_transout_error","商铺账户余额转出异常");
			}	
			//opay异常，设置收入转出状态为3：失败
			Map<String,Object> parasMap = new HashMap<String,Object>();
			parasMap.put("transStatus", 3); //设置收收入转出状态为失败
			parasMap.put("transoutId", paramsMap.get("transRecord"));//系统交易流水
			parasMap.put("remark","交易失败");//系统交易流水
			if(merchantTransoutDao.updateTransoutRecordStatus(parasMap) < 1){
				return new ResultJSONObject("merchant_transout_error","商铺账户余额转出异常");
			}	
			
			throw e;
		}	
		return jsonObj;
	}
	
	@Override
	public int saveTransoutLogs(Map<String, Object> paramsMap) {
		return this.merchantInfoDao.saveTransoutLogs(paramsMap);
	}

	@Override
	public int deleteSystemErrorLogs(Long errorId) {
		return this.merchantTransoutDao.deleteSystemErrorLogs(errorId);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long transoutBefore(Map<String,Object> parasMap,Header header) {
		BigDecimal transAmount =(BigDecimal) parasMap.get("moneyAmount");
      //1、先扣除用户收入转出资金
		Map<String,Object> merchantStatisticsParams = new HashMap<String,Object>();
		merchantStatisticsParams.put("withdrawPrice", transAmount.doubleValue());
		merchantStatisticsParams.put("merchantId", parasMap.get("merchantId"));
		if(merchantStatisticsDao.updatePrice(merchantStatisticsParams) < 1){
			return null;
		}	
		//2、插入转出记录，设置状态为资金已扣除，钱包资金待转入状态 
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("merchantId", parasMap.get("merchantId"));
		paramsMap.put("userId", parasMap.get("userId"));
		paramsMap.put("appType", parasMap.get("appType"));
		paramsMap.put("userPhone", parasMap.get("userPhone"));
		paramsMap.put("moneyAmount", transAmount.doubleValue());
		paramsMap.put("merchantName", parasMap.get("merchantName"));
		paramsMap.put("transSeq", header.getTradeNo());//系统交易流水
		paramsMap.put("transStatus", 1);//本地已经扣款
		paramsMap.put("transDate", DateUtil.parseDate(DateUtil.DATE_YYYYMMDDHHMMSSSSS_PATTER,header.getTime()));//交易时间
		paramsMap.put("remark","o盟已扣款");
		if(merchantTransoutDao.saveTransoutRecord(paramsMap) < 1){
			return null;
		}	
		return (Long)paramsMap.get("id");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject transoutAfter(Map<String,Object> trasoutParasMap,JSONObject jsonObject) {
			if(null == jsonObject){
				return new ResultJSONObject("merchant_transout_error","商铺账户余额转出异常");
			}			
			//更新转出记录状态
			Map<String,Object> parasMap = new HashMap<String,Object>();
			int trasStatus = 3;//设置状态为失败 0:收入转入成功 1：异常转入需要与opay对账 2:收入转入失败
			if("000".equals(jsonObject.getString("resultCode"))){
				trasStatus = 0;
			}
			parasMap.put("transStatus", trasStatus);
			parasMap.put("transoutId", trasoutParasMap.get("transRecord"));//系统交易流水
			parasMap.put("remark", jsonObject.getString("message")); //设置remark信息
			parasMap.put("opayTransSeq", jsonObject.getString("transNo"));//设置opay交易流水号
			if(merchantTransoutDao.updateTransoutRecordStatus(parasMap) < 1){
				return new ResultJSONObject("merchant_transout_error","商铺账户余额转出异常");
			}	
			if(!"000".equals(jsonObject.getString("resultCode"))){
				return jsonObject;
			}
			//生成交易详情记录
			Map<String,Object> paymentDetailMap = new HashMap<String,Object>();
			paymentDetailMap.put("merchantId", trasoutParasMap.get("merchantId"));
			paymentDetailMap.put("paymentType", 6);//收支类型6为转出至钱包
			paymentDetailMap.put("businessId",trasoutParasMap.get("transRecord"));
			paymentDetailMap.put("paymentPrice", trasoutParasMap.get("moneyAmount"));
			paymentDetailMap.put("appType", trasoutParasMap.get("appType"));
			paymentDetailMap.put("tradeNo", trasoutParasMap.get("transSeq")); //交易流水号
			if(merchantPaymentDetailsDao.insertPaymentDetails(paymentDetailMap)<1){
				return new ResultJSONObject("merchant_transout_error","商铺账户余额转出异常");
			}
		
		return jsonObject;
	}

*/}
