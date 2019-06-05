package com.shanjin.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.shanjin.common.util.SmsUtil;
import com.shanjin.dao.IMerchantPlanDao;
import com.shanjin.service.IMessageCenterService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IMerchantCacheService;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.MD5Util;
import com.shanjin.common.util.PaymentDetailFormUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IDictionaryDao;
import com.shanjin.dao.IMerchantApplyWithdrawRecordDao;
import com.shanjin.dao.IMerchantEmployeesDao;
import com.shanjin.dao.IMerchantInfoDao;
import com.shanjin.dao.IMerchantPaymentDetailsDao;
import com.shanjin.dao.IMerchantStatisticsDao;
import com.shanjin.dao.IMerchantWithdrawDao;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IMyIncomeService;
import com.shanjin.service.IValueAddedIncomeService;

import static com.shanjin.common.util.StringUtil.null2Str;

/**
 * @ClassName: MyIncomeServiceImpl
 * @Description: TODO(商户我的收入)
 * @author 王瑞
 * @Date 2015年9月7日 下午7:48:00
 */
@Service("myIncomeService")
public class MyIncomeServiceImpl implements IMyIncomeService {

	/** */
	@Resource
	private IMerchantStatisticsDao merchantStatisticsDao;

	/** */
	@Resource
	private IMerchantWithdrawDao merchantWithdrawDao;

	/** */
	@Resource
	private IMerchantApplyWithdrawRecordDao merchantApplyWithdrawRecordDao;

	/** */
	@Resource
	private IMerchantInfoDao merchantInfoDao;

	/** */
	@Resource
	private IMerchantPaymentDetailsDao merchantPaymentDetailsDao;

	/** */
	@Resource
	private IDictionaryDao idictionaryDao;

	/** */
	@Resource
	private IMerchantCacheService merchantCacheService;

	/** */
	@Resource
	private IMerchantEmployeesDao merchantEmployeesDao;

	/** */
	@Resource
	private MerchantPayService merchantPayService;

	/** */
	@Resource
	private ICommonCacheService commonCacheService;

	@Resource
	private IValueAddedIncomeService valueAddedIncomeService;
	
	@Resource
	private ICommonService commonService;

    @Resource
    private IMessageCenterService messageCenterService;

    @Resource
    private IMerchantPlanDao merchantPlanDao;

	/**
	 * 查询商户收入信息
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject selectMyIncome(String appType, Long merchantId) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 应用程序类型
		paramMap.put("appType", appType);

		Map<String, BigDecimal> myIncomeMap = this.merchantStatisticsDao.selectMyIncome(paramMap);
		if (myIncomeMap != null) {
			jsonObject = new ResultJSONObject("000", "我的收入信息加载成功");
			// 未提取金额
			jsonObject.put("surplusPrice", myIncomeMap.get("surplusPrice"));
			// 已提取金额
			jsonObject.put("totalWithdrawPrice", myIncomeMap.get("totalWithdrawPrice"));
			// 查看是否绑定过银行卡
			String payPassword = this.selectPayPassword(merchantId);
			jsonObject.put("isBindedBank", (payPassword == null || payPassword.equals("")) ? "0" : "1");
			// 订单余额
			jsonObject.put("orderSurplusPrice", myIncomeMap.get("orderSurplusPrice"));

		} else {
			jsonObject = new ResultJSONObject("my_income_info_null", "我的收入信息为空");
			jsonObject.put("surplusPrice", 0);
			jsonObject.put("totalWithdrawPrice", 0);
			jsonObject.put("orderSurplusPrice", 0);
			jsonObject.put("isBindedBank", 0);
		}

		return jsonObject;
	}

	/**
	 * 查询商户提现密码
	 * 
	 * @param merchantId
	 *            商户ID
	 * @return 支付密码 String
	 * @throws
	 */
	private String selectPayPassword(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		return this.merchantInfoDao.selectPayPassword(paramMap);
	}

	/**
	 * 查询商户已绑定的提现账户信息
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 */
	private List<Map<String, Object>> selectWithdrawNo(String appType, Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 应用程序类型
		paramMap.put("appType", appType);
		List<Map<String, Object>> withdrawInfoList = this.merchantWithdrawDao.selectWithdrawNo(paramMap);
		for (Map<String, Object> withdrawInfoMap : withdrawInfoList) {
			BusinessUtil.disposePath(withdrawInfoMap, "bankIcon");
		}
		return withdrawInfoList;
	}

	/**
	 * 查询商户余额
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 */
	private BigDecimal selectSurplusPrice(String appType, Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 应用程序类型
		paramMap.put("appType", appType);
		return this.merchantStatisticsDao.selectSurplusPrice(paramMap);
	}

	/**
	 * 绑定银行卡初始化
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 */
	@Override
	public JSONObject bindingBank(String appType, Long merchantId) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dictType", "bank");
		params.put("dictLevel", "app");
		params.put("dictDomain", Constant.EMPTY);

		// 银行信息列表
		List<Map<String, Object>> bankInfoList = this.idictionaryDao.getParentDict(params);
		for (Map<String, Object> map : bankInfoList) {
			BusinessUtil.disposePath(map, "path");// 文件路径的补全
		}

		// 已绑定银行卡
		List<Map<String, Object>> withdrawInfoList = this.selectWithdrawNo(appType, merchantId);

		// 商户支付密码
		String payPassword = this.selectPayPassword(merchantId);
		payPassword = (payPassword == null || payPassword.equals("")) ? "0" : "1";
		if (!bankInfoList.isEmpty()) {
			jsonObject = new ResultJSONObject("000", "银行信息加载成功");
			jsonObject.put("bankInfoList", bankInfoList);
			jsonObject.put("withdrawNoCount", withdrawInfoList.size());
			jsonObject.put("withdrawInfoList", withdrawInfoList);
			jsonObject.put("payPassword", payPassword);
		} else {
			jsonObject = new ResultJSONObject("bank_info_null", "银行信息为空");
		}

		return jsonObject;
	}

	/**
	 * 申请提现 初始化（已绑定的银行卡信息加载）
	 * 
	 * @param appType
	 * @param merchantId
	 * @return
	 */
	@Override
	public JSONObject applyWithdrawInit(String appType, Long merchantId) throws Exception {
		JSONObject jsonObject = null;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);

		// 已绑定的银行卡
		List<Map<String, Object>> withdrawInfoList = this.selectWithdrawNo(appType, merchantId);

		// 当前可提现金额
//		BigDecimal surplusPrice = this.selectSurplusPrice(appType, merchantId);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// 预计到账时间（当前时间两天后）
		String toTheAccountTime = sdf.format(DateUtils.addDays(new Date(), 2));
		
		//获取当月之前所有收入
		String nowDate=DateUtil.getNowYYYYMMDD();
		String time=nowDate.substring(0,7)+"-01 00:00:00";
		paramMap.put("time", time);
		BigDecimal allMoney=this.merchantWithdrawDao.getAllMoney(paramMap);
		//获取所有提现金额
		BigDecimal allWithdrawMoney=this.merchantWithdrawDao.getAllWithdrawMoney(paramMap);
		//得到当月之前可提现收入
		BigDecimal surplusPrice =allMoney.subtract(allWithdrawMoney);
		
		Map<String, Object> configMap=commonService.getConfigurationInfoByKey("is_open_withdraw_date");
		int startWithdrawDate=configMap==null?0:StringUtil.nullToInteger(configMap.get("standby_field1"));//提现限制开始日期
		int endWithdrawDate=configMap==null?0:StringUtil.nullToInteger(configMap.get("standby_field2"));//提现限制结束日期				
		
		//判断是否规定提现金额
		configMap=commonService.getConfigurationInfoByKey("is_open_withdraw_money");
		int startMoney=configMap==null?0:StringUtil.nullToInteger(configMap.get("standby_field1"));//提现限制开始日期
		int endMoney=configMap==null?0:StringUtil.nullToInteger(configMap.get("standby_field2"));//提现限制结束日期
		
	
		
		
		jsonObject = new ResultJSONObject("000", "已绑定的银行卡信息加载成功");
		jsonObject.put("withdrawInfoList", withdrawInfoList);
		jsonObject.put("surplusPrice", surplusPrice);
		jsonObject.put("withdrawDate", startWithdrawDate+"-"+endWithdrawDate);
		jsonObject.put("withdrawMoney", startMoney+"-"+endMoney);
		jsonObject.put("toTheAccountTime", toTheAccountTime);	
		return jsonObject;
	}

	/**
	 * 提现申请的保存
	 * 
	 * @param appType
	 * @param merchantId
	 * @param withdraw
	 *            银行类型
	 * @param withdrawNo
	 *            银行账号
	 * @param withdrawPrice
	 *            提现金额
	 * @param payPassword
	 *            支付密码
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertApplyWithdrawRecord(String appType, Long merchantId, Long withdraw, String withdrawNo, String withdrawPrice, String payPassword,Short tip) throws Exception {
		JSONObject jsonObject = null;

		//加入关闭商店里的提现提示
		Object cachedDisableWithDraw=commonCacheService.getObject(CacheConstants.OPAY_DISABLE_WITHDRAW);
		boolean disabledWithDraw=false;
		if (cachedDisableWithDraw==null){
			int  rowCount = merchantWithdrawDao.isDisableWithDraw();
			if (rowCount>0){
				disabledWithDraw =true;
			}
			commonCacheService.setObject(disabledWithDraw,CacheConstants.OPAY_DISABLE_WITHDRAW);
		}else{
			disabledWithDraw=(boolean) cachedDisableWithDraw;
		}
		if(disabledWithDraw){
					if  (tip==null){
						//toast 提示
						jsonObject = new  ResultJSONObject("-1","钱包功能上线后可申请提现，敬请期待");
					}else {
						//弹窗提示
						jsonObject = new  ResultJSONObject("-2","钱包功能上线后可申请提现，提现日为每周三，如需立即提现，可在其他平台下载最新版进行操作");
					}
					return jsonObject;
		}
		
		
		
		
		//获取当前日期
		String nowDate=DateUtil.getNowYYYYMMDD();
		
		//判断是否规定日期提现
		Map<String, Object> configMap=commonService.getConfigurationInfoByKey("is_open_withdraw_date");
		int isOpenWithdrawDate=configMap==null?1:StringUtil.nullToInteger(configMap.get("config_value"));
		int startWithdrawDate=configMap==null?15:StringUtil.nullToInteger(configMap.get("standby_field1"));//提现限制开始日期
		int endWithdrawDate=configMap==null?20:StringUtil.nullToInteger(configMap.get("standby_field2"));//提现限制结束日期		
		if(isOpenWithdrawDate==1){//开启提现日期限制
			int nowDays=StringUtil.nullToInteger(nowDate.split("-")[2]);			
			if (nowDays < startWithdrawDate || nowDays > endWithdrawDate) {
				return new ResultJSONObject("apply_withdraw_day_error", "请于每月"+startWithdrawDate+"-"+endWithdrawDate+"号申请提现！");
			}
		}
		
		//判断是否规定提现金额
		configMap=commonService.getConfigurationInfoByKey("is_open_withdraw_money");
		int isOpenWithdrawMoney=configMap==null?1:StringUtil.nullToInteger(configMap.get("config_value"));
		int startMoney=configMap==null?500:StringUtil.nullToInteger(configMap.get("standby_field1"));//提现限制开始日期
		int endMoney=configMap==null?50000:StringUtil.nullToInteger(configMap.get("standby_field2"));//提现限制结束日期		
		if(isOpenWithdrawMoney==1){//开启提现金额限制
			//获取当前日期
			double withdrawPrice_=StringUtil.nullToDouble(withdrawPrice);			
			if (withdrawPrice_ < startMoney || withdrawPrice_ > endMoney) {
				return new ResultJSONObject("apply_withdraw_day_error", "提现金额为"+startMoney+"-"+endMoney+"！");
			}
		}
		
		//判断每月只能提现1次
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);
		Map<String,Object> lastWithdrawRecordMap = this.merchantApplyWithdrawRecordDao.selectLastWithdrawRecord(paramMap);
		if(lastWithdrawRecordMap!=null){
			int withdrawStatus=StringUtil.nullToInteger(lastWithdrawRecordMap.get("withdrawStatus"));
			String withdrawTime=StringUtil.null2Str(lastWithdrawRecordMap.get("withdrawTime"));
			int nowMonth=StringUtil.nullToInteger(nowDate.split("-")[1]);
			int withdrawMonth=StringUtil.nullToInteger(withdrawTime.split("-")[1]);		
			if(withdrawMonth >= nowMonth && withdrawStatus != 0){//
				if(withdrawStatus==2){
					return new ResultJSONObject("apply_withdraw_repeat", "本月已提现，正在审核中，请勿重复提现");
				}else{
					return new ResultJSONObject("apply_withdraw_repeat", "本月已提现，请勿重复提现");
				}
			}
		}
		
		//判断
		BigDecimal price = new BigDecimal(withdrawPrice);
		BigDecimal surplusPrice = this.selectSurplusPrice(appType, merchantId);
		if (price.compareTo(surplusPrice) == 1) {
			return new ResultJSONObject("apply_withdraw_beyond_surplus", "提现金额不得超过余额");
		}
//		if (BigDecimal.ZERO.compareTo(price) == 0) {
//			return new ResultJSONObject("apply_withdraw_not_zero", "提现金额不得为0");
//		}
//		if (new BigDecimal(500).compareTo(price) == 1) {
//			return new ResultJSONObject("apply_withdraw_less_500", "可提现金额为500至50000元");
//		}
//		if (new BigDecimal(50000).compareTo(price) == -1) {
//			return new ResultJSONObject("apply_withdraw_beyond_50000", "可提现金额为500至50000元");
//		}

		//支付密码验证
		JSONObject result = this.verificationPayPassword(appType, merchantId, payPassword);
		if (!result.getString("resultCode").equals("000")) {
			return result;
		}
		
		paramMap.put("withdraw", withdraw);// 提现账户类型
		paramMap.put("withdrawNo", withdrawNo);// 提现账号
		paramMap.put("withdrawPrice", price);// 提现金额
		paramMap.put("payPassword", payPassword);// 密码

		// 申请提现的保存
		int i = this.merchantApplyWithdrawRecordDao.insertMerchantApplyWithdrawRecord(paramMap);
		if (i == 0) {
			return new ResultJSONObject("apply_withdraw_failure", "提现信息保存失败！");
		}
		// 更新总提取金额，余额
		i = this.merchantStatisticsDao.updatePrice(paramMap);
		if (i == 0) {
			return new ResultJSONObject("apply_withdraw_failure", "更新余额信息失败！");
		}

		paramMap.put("paymentType", 1);// 收支类型
		paramMap.put("paymentPrice", price);// 提现金额
		// 添加收支明细记录
		i = this.merchantPaymentDetailsDao.insertPaymentDetails(paramMap);
		if (i == 0) {
			return new ResultJSONObject("apply_withdraw_failure", "更新收支信息失败！");
		}
		jsonObject = new ResultJSONObject("000", "申请提现成功");

		return jsonObject;
	}

	/**
	 * 收支明细查询
	 * 
	 * @param appType
	 * @param merchantId
	 * @param pageNo
	 * @return
	 */
	@Override
	public JSONObject selectPaymentDetails(String appType, Long merchantId, int pageNo) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);// 商户ID
		paramMap.put("appType", appType);// 应用程序类型

		int count = this.merchantPaymentDetailsDao.selectPaymentDetailsCount(paramMap);
		jsonObject = new ResultJSONObject("000", "收支明细加载成功");
		if (count == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("paymentDetails", new ArrayList<HashMap<String, String>>());
		} else {
			paramMap.put("rows", pageNo * Constant.PAGESIZE);// 查询起始记录行号
			paramMap.put("pageSize", Constant.PAGESIZE);// 每页显示的记录数

			List<Map<String, Object>> paymentDetailsList = this.merchantPaymentDetailsDao.selectPaymentDetails(paramMap);
			DecimalFormat df = new DecimalFormat("0.##");
			for (Map<String, Object> paymentDetailsMap : paymentDetailsList) {
				if ((Integer) paymentDetailsMap.get("paymentType") == 1) {
					paymentDetailsMap.put("paymentPrice", "-" + df.format(paymentDetailsMap.get("paymentPrice")));
				} else {
					paymentDetailsMap.put("paymentPrice", "+" + df.format(paymentDetailsMap.get("paymentPrice")));
				}
			}
			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(count));
			jsonObject.put("paymentDetails", paymentDetailsList);
		}

		return jsonObject;
	}

	/**
	 * 绑定银行卡
	 * 
	 * @param requestParamMap
	 *            前端传递的参数
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertMerchantWithdraw(Map<String, Object> requestParamMap) throws Exception {

		JSONObject jsonObject = null;
		String appType = (String) requestParamMap.get("appType");
		String realName = (String) requestParamMap.get("realName");// 真实姓名
		String moneyIdNo = (String) requestParamMap.get("moneyIdNo");// 身份证号
		String payPassword = (String) requestParamMap.get("payPassword");// 提现密码
		int cardType = (Integer.parseInt((String) (requestParamMap.get("cardType"))));// 银行卡类型
		String cardNo = (String) requestParamMap.get("cardNo");// 银行卡号
		Long merchantId = (Long.parseLong((String) (requestParamMap.get("merchantId"))));// 商户ID

		if (StringUtils.isEmpty(appType) || StringUtils.isEmpty(realName) || StringUtils.isEmpty(moneyIdNo) || StringUtils.isEmpty(payPassword) || StringUtils.isEmpty(cardNo)) {
			return new ResultJSONObject("save_bank_card_repetition", "关键参数不能为空！");
		}

		// 查询已绑定的银行卡
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("appType", appType);// 应用程序类型
		map.put("merchantId", merchantId);
		map.put("withdrawNo", cardNo);// 提现账号
		Integer count = merchantWithdrawDao.selectWithdrawNoByCardNo(map);
		if (count != null && (int) count > 0) {
			return new ResultJSONObject("save_bank_card_repetition", "该银行卡已绑定，请勿重复绑定");
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 第一次绑定提现账户的时候，填写的信息,如果支付密码为空，则认为是第一次绑定
		String payPassword_ = this.selectPayPassword(merchantId);
		if (StringUtils.isEmpty(payPassword_)) {
			// 设置提现密码
			payPassword = MD5Util.MD5_32(payPassword);
			paramMap.put("moneyPassword", payPassword);
			this.merchantInfoDao.updateWithdrawInfo(paramMap);
		}

		paramMap.put("realName", realName);// 提现真实姓名
		paramMap.put("IDNo", moneyIdNo);// 提现真实身份证号
		paramMap.put("moneyRealName", realName);// 提现真实姓名
		paramMap.put("moneyIdNo", moneyIdNo);// 提现真实身份证号
		paramMap.put("withdraw", cardType);// 提现账户类型
		paramMap.put("withdrawNo", cardNo);// 提现账号
		paramMap.put("appType", appType);// 应用程序类型

		int i = this.merchantWithdrawDao.insertMerchantWithdraw(paramMap);
		if (i == 0) {
			return new ResultJSONObject("save_bank_card_failure", "保存银行卡信息失败");
		}

		jsonObject = new ResultJSONObject("000", "绑定银行卡成功");
		jsonObject.put("merchantWithdrawList", this.selectWithdrawNo(appType, merchantId));

		return jsonObject;
	}

	/**
	 * 找回支付密码
	 * 
	 * @param requestParamMap
	 *            前端传递的参数
	 * @return 返回验证结果
	 * @throws Exception
	 */
	@Override
	public JSONObject findPayPassWord(Map<String, Object> requestParamMap) throws Exception {

		JSONObject jsonObject = null;

		String appType = (String) requestParamMap.get("appType"); // 应用类型
		Long merchantId = (Long.parseLong((String) (requestParamMap.get("merchantId") == null ? "0" : requestParamMap.get("merchantId"))));// 商户ID
		String realName = (String) requestParamMap.get("realName");// 真实姓名
		String IDNo = (String) requestParamMap.get("IDNo");// 证件号
		String cardNo = (String) requestParamMap.get("cardNo");// 银行卡号

		if (realName == null || IDNo == null || cardNo == null) {
			return new ResultJSONObject("find_pay_passWord_failure", "所填信息信息为空！");
		}

		// 根据填写的信息查找银行卡信息，如果查找到，则信息填写正确，否则信息填写错误
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("appType", appType);
		map.put("merchantId", merchantId);
		map.put("realName", realName);
		map.put("IDNo", IDNo);
		map.put("cardNo", cardNo);

		int withdraw = merchantWithdrawDao.isExistOfWithdraw(map);

		if (withdraw == 0) {// 验证信息匹配是否正确
			jsonObject = new ResultJSONObject("find_pay_passWord_failure", "信息不匹配，请核对你所填写的信息！");
		} else {// 信息验证成功，则随机生成一个6位数密码，以短信方式发送给用户

			// 生成一个随机密码
			String randomPwd = ((int) ((Math.random() * 9 + 1) * 100000)) + "";
			// 将用户密码修改成随机生成的密码
			JSONObject result = this.updPayPassword(appType, merchantId, randomPwd);
			if (result.getString("resultCode").equals("000")) {// 修改成功
				// 查询商户手机号
				String phone = merchantEmployeesDao.getPhoneByMerchantId(merchantId);
				if (StringUtils.isEmpty(phone)) {
					return new ResultJSONObject("find_pay_passWord_failure", "查询手机号码失败！");
				}
				// 将随机密码发送给商户
				if (BusinessUtil.sendPayPassword(phone, randomPwd)) {
					jsonObject = new ResultJSONObject("000", "密码已发送，请及时查收！");
				} else {
					jsonObject = new ResultJSONObject("find_pay_passWord_failure", "密码发送失败,请稍后重试");
				}
			} else {// 修改失败
				jsonObject = new ResultJSONObject("find_pay_passWord_failure", "随机密码修改失败！");
			}
		}
		return jsonObject;
	}

	/**
	 * 查询商户已绑定的提现账户信息
	 * 
	 * @param appType
	 *            应用类型
	 * @param merchantId
	 *            商户ID
	 * @return 已绑定的银行卡信息列表
	 * @throws Exception
	 */
	public JSONObject getBindedBankCards(String appType, Long merchantId) throws Exception {
		List<Map<String, Object>> list = this.selectWithdrawNo(appType, merchantId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("bindedBankCards", list);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获取已绑定银行卡成功");
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
	 * @return 解绑成功或者失败
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject unbindBankCard(String appType, Long merchantId, Long withdrawId, String payPassword) throws Exception {

		// 解绑前验证支付密码
		JSONObject result = this.verificationPayPassword(appType, merchantId, payPassword);
		if (!result.getString("resultCode").equals("000")) {
			return result;
		}

		// 解除银行卡绑定，即将is_del改为1
		int i = merchantWithdrawDao.unbindBankCard(withdrawId);
		if (i == 0) {
			return new ResultJSONObject("unbind_bankCard_failure", "解绑失败！");
		}
		return new ResultJSONObject("000", "解绑成功！");
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
	 * @throws Exception
	 */
	@Override
	public JSONObject verificationPayPassword(String appType, Long merchantId, String payPassword) throws Exception {
		// 校验锁定状态
		Long lockTime = (Long) commonCacheService.getObject("verification_lock_time", merchantId + "");
		if (lockTime != null) {
			if (System.currentTimeMillis() - lockTime < 15 * 60 * 1000) {
				return new ResultJSONObject("verification_lock_15_minute", "支付密码输入3次错误，请15分钟后重试！");
			} else {
				commonCacheService.deleteObject("verification_lock_time", merchantId + "");
			}
		}

		// 校验错误次数
		Long times = (Long) commonCacheService.getObject("verification_times", merchantId + "");
		if (times == null) {
			times = 0l;
		}

		// 判断支付密码是否正确 E10ADC3949BA59ABBE56E057F20F883E ：123456
		String payPassword_ = this.selectPayPassword(merchantId);// 获取支付密码
		if (payPassword == null || payPassword_ == null) {
			times++;
			commonCacheService.setObject(times, "verification_times", merchantId + "");
			if (times >= 3) { // 失败大于3次，锁定请求15分钟
				commonCacheService.setObject(System.currentTimeMillis(), "verification_lock_time", merchantId + "");
				commonCacheService.deleteObject("verification_times", merchantId + "");
				return new ResultJSONObject("verification_lock_15_minute", "支付密码输入3次错误，请15分钟后重试！");
			}
			return new ResultJSONObject("verification_payPassword_failure", "验证失败！");
		}
		// 针对支付密码没有加密的商户
		if (payPassword_.equals(payPassword)) {
			payPassword = MD5Util.MD5_32(payPassword);// 支付密码加密
			// 将商户加密密码修改成已加密的
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("merchantId", merchantId);
			map.put("newPayPassword", payPassword);
			merchantWithdrawDao.updPayPassword(map);// 修改支付密码
		} else {
			payPassword = MD5Util.MD5_32(payPassword);// 支付密码加密
			if (!payPassword_.equals(payPassword)) {// 支付密码输入错误
				times++;
				commonCacheService.setObject(times, "verification_times", merchantId + "");
				if (times >= 3) { // 失败大于3次，锁定请求15分钟
					commonCacheService.setObject(System.currentTimeMillis(), "verification_lock_time", merchantId + "");
					commonCacheService.deleteObject("verification_times", merchantId + "");
					return new ResultJSONObject("verification_lock_15_minute", "支付密码输入3次错误，请15分钟后重试！");
				}
				return new ResultJSONObject("verification_payPassword_failure", "支付密码输入错误！");
			}

			commonCacheService.deleteObject("verification_times", merchantId + "");
		}
		return new ResultJSONObject("000", "支付密码验证成功！");
	}

	/**
	 * 修改支付密码
	 * 
	 * @param appType应用类型
	 * @param merchantId商户ID
	 * @param newPayPassword
	 *            新支付密码
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updPayPassword(String appType, Long merchantId, String newPayPassword) throws Exception {

		if (newPayPassword == null || newPayPassword.equals("")) {
			return new ResultJSONObject("upd_payPassword_failure", "新密码为空！");
		}

		String payPassword_ = this.selectPayPassword(merchantId);// 获取支付密码
		if (payPassword_ == null || payPassword_.equals("")) {
			return new ResultJSONObject("upd_payPassword_failure", "旧密码为空！");
		}
		newPayPassword = MD5Util.MD5_32(newPayPassword);// 支付密码加密

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("merchantId", merchantId);
		map.put("newPayPassword", newPayPassword);
		// 修改支付密码
		merchantWithdrawDao.updPayPassword(map);

		return new ResultJSONObject("000", "修改支付密码成功！");
	}

	/**
	 * 订单余额充值申请
	 * 
	 * @param appType
	 * @param merchantId
	 * @param money
	 *            申请金额
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject topupApply(Map<String, Object> map) throws Exception {
		// 只有当没有订单交易号的时候才插入，重复的记录不插入
		Integer checkResult = merchantPaymentDetailsDao.checkTopupApplyByInnerTradeNo(map);
	
			// 新增申请,增加了支付时间
			if (!map.containsKey("openTime") || map.get("openTime")==null || map.get("openTime").equals("")){
				if (!map.get("payType").equals(3)){
					map.put("openTime", new Date(System.currentTimeMillis()));
				}
			}
			
			
			if (checkResult==0) {
				//	merchantPaymentDetailsDao.addTopupApply(map);
				    merchantPaymentDetailsDao.addTopupApplyWithConfirm(map);
			}else{
					merchantPaymentDetailsDao.confirmGrapBuyRecord(map);	
			}

			//保存缴费凭证信息
			List<String> paths = (List<String>) map.get("paths");
			this.savePayApplyFile(paths, map.get("payNo").toString(), "topup");

			if (StringUtil.null2Str(map.get("applyStatus")).equals("2")) {
				merchantStatisticsDao.updateOrderSurplusPrice(map);
				valueAddedIncomeService.calculateIncome(StringUtil.nullToLong(map.get("merchantId")), Double.parseDouble(StringUtil.null2Str(map.get("money"))), "API", 2);
			}
			commonCacheService.deleteObject(CacheConstants.VALUE_ADD_SERVICE, StringUtil.null2Str(map.get("merchantId")));
	

        Map<String, Object> bossInfo = merchantPlanDao.getBossIdByMerchant(StringUtil.nullToLong(map.get("merchantId")));
        String phone = "";
        if (null != bossInfo) {
            phone = null2Str(bossInfo.get("phone"));
        }
        sendMessageAndSMS(map, phone);
		return new ResultJSONObject("000", "充值申请成功！");
	}

    // 消息和短信
    private void sendMessageAndSMS(Map<String, Object> map, String phone) throws Exception {
        String msg = "你已成功购买" + map.get("money") + "元抢单金";

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("messageId", 0);
        messageMap.put("messageType", 1);
        messageMap.put("customerType", 1);
        messageMap.put("customerId", map.get("merchantId"));
        messageMap.put("title", "购买抢单金");
        messageMap.put("content", msg);
        messageCenterService.saveCustomerMessageCenter(messageMap);

        SmsUtil.asyncSendMsg(phone, msg);
    }

	/**
	 * 保存缴费凭证信息
	 * @param paths 路径
	 * @param payNo 支付编号
	 * @param serviceType 业务类型       topup 充值 vip vip申请 employeesNum 顾问号申请
	 * @return 保存结果数
	 */
	@Transactional(rollbackFor = Exception.class)
	public int savePayApplyFile(List<String> paths,String payNo,String serviceType){
		
		//保存缴费凭证信息
		List<Map> filePaths = new ArrayList<Map>();
		if (paths != null && paths.size() > 0) {
			for (String path : paths) {
				Map<String, Object> attaMap = new HashMap<String, Object>();
				attaMap.put("path", path);
				attaMap.put("payNo", payNo);
				attaMap.put("serviceType", serviceType);
				filePaths.add(attaMap);
			}
		}
		int result = 0;
		if(filePaths.size()>0){
			result = merchantPaymentDetailsDao.saveTopupApplyFile(filePaths);
		}
		return result;
	}
	
	/**
	 * 订单记录收支明细
	 * 
	 * @param appType
	 * @param merchantId
	 * @param pageNo
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject orderPaymentDetails(String appType, Long merchantId, int pageNo) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);// 商户ID
		paramMap.put("appType", appType);// 应用程序类型

		int count = this.merchantPaymentDetailsDao.selectOrderPaymentDetailsCount(paramMap);
		jsonObject = new ResultJSONObject("000", "订单记录明细加载成功");
		if (count == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("orderPaymentDetails", new ArrayList<HashMap<String, Object>>());
		} else {
			paramMap.put("rows", pageNo * Constant.PAGESIZE);// 查询起始记录行号
			paramMap.put("pageSize", Constant.PAGESIZE);// 每页显示的记录数

			List<Map<String, Object>> orderPaymentDetails = this.merchantPaymentDetailsDao.selectOrderPaymentDetails(paramMap);

			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(count));
			jsonObject.put("orderPaymentDetails", orderPaymentDetails);
		}

		return jsonObject;
	}

	@Override
	public JSONObject selectPaymentDetailsList(String appType, Long merchantId, int pageNo,int todayQuery,int paymentType) throws Exception {
		JSONObject jsonObject =new ResultJSONObject("000","查询成功");
		List<Map<String,Object>> paymentList = new ArrayList<Map<String,Object>>();
		Map<String,Object> selectParams = new HashMap<String,Object>();
		selectParams.put("merchantId", merchantId);
		if(todayQuery == 1){
			selectParams.put("rows", 0);// 当天交易记录只查询12条
			selectParams.put("pageSize", 12);//
			selectParams.put("todayQueryFlag", 1);//查询今日标记
		}else{
			Map<String,Object> countParasMap = new HashMap<String,Object>();
			countParasMap.put("paymentType", paymentType);
			countParasMap.put("merchantId", merchantId);
			int total = merchantPaymentDetailsDao.selectPaymentlistCount(countParasMap);
			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(total));
			if(total == 0){
				jsonObject.put("totalPage", 0);
				jsonObject.put("paymentList", paymentList);
				return jsonObject;
			}
			selectParams.put("rows", pageNo * Constant.PAGESIZE);// 查询起始记录行号
			selectParams.put("pageSize", Constant.PAGESIZE);// 每页显示的记录数
		}
		selectParams.put("parentPaymentId", paymentType); //查询类型	
		List<Map<String,Object>> paymentDetailsList = this.merchantPaymentDetailsDao.selectPaymentDetailsList(selectParams);
		for (Map<String, Object> paymentDetailsMap : paymentDetailsList) {
			int payFlag = 1;
			if((Integer) paymentDetailsMap.get("paymentType") == 1 ){
				payFlag =  0;
			}
			paymentDetailsMap.put("payFlag",payFlag) ;//0:消费，1：收入 	
			paymentList.add(paymentDetailsMap);
		}
		jsonObject.put("paymentList", paymentList);
	return jsonObject;
	}

	@Override
	public JSONObject selectPaymentDetailInfo(int paymentType, Long paymentId) {
		JSONObject jsonObject = new ResultJSONObject("000","查询交易明细信息");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("paymentType", paymentType);
		params.put("paymentId", paymentId);
		Map<String,Object> paymentInfoMap = null;
		if(paymentType == 0){
		  //订单收入
			paymentInfoMap = merchantPaymentDetailsDao.selectOrderPaymentDetailInfo(params);
		}else if(paymentType == 5){
			//订单奖励
			paymentInfoMap  =merchantPaymentDetailsDao.seletOrderAwardPaymentDeatilInfo(params);
		}else if(paymentType ==2){
			//剪彩红包
			paymentInfoMap  =merchantPaymentDetailsDao.selectCuttingPaymentDetailInfo(params);
		}else if(paymentType == 6){
			//收入转出明细
			paymentInfoMap  =merchantPaymentDetailsDao.selectTransoutPaymentDetailInfo(params);
		}else{
			 //其它 活动
			paymentInfoMap  =merchantPaymentDetailsDao.selectActivityPaymentDetailInfo(params);
		}
		Map<String,Object> paymentDetailMap = new HashMap<String,Object>();
		if(paymentInfoMap != null){
			paymentDetailMap.put("paymentId", paymentInfoMap.get("paymentId"));//交易id	
			paymentDetailMap.put("paymentTime", paymentInfoMap.get("paymentTime")); //交易时间
			paymentDetailMap.put("paymentTypeDesc", paymentInfoMap.get("paymentTypeDesc"));//交易描述
			paymentDetailMap.put("detailsName", paymentInfoMap.get("detailsName")); //服务类型
			paymentDetailMap.put("orderNo", paymentInfoMap.get("orderNo")); //订单编号
			paymentDetailMap.put("userPhone", StringUtil.formatPhoneStr((String)paymentInfoMap.get("userPhone"),3,7)); //手机号
			//paymentDetailMap.put("orderPayTypeName", paymentInfoMap.get("orderPayTypeName")); //支付方式
			//paymentDetailMap.put("orderPayType", paymentInfoMap.get("orderPayType")); //支付方式
			paymentDetailMap.put("tradeNo", paymentInfoMap.get("tradeNo")); //交易流水号
			paymentDetailMap.put("paymentPrice", paymentInfoMap.get("paymentPrice"));
			paymentDetailMap.put("remark", paymentInfoMap.get("remark"));
			int payFlag = 1;
			if(paymentType == 1 ||paymentType == 6){
				payFlag =  0;
			}
			paymentDetailMap.put("payFlag",payFlag) ;//0:消费，1：收入 
		}
		Map<String,Object> contMap = PaymentDetailFormUtil.constructOrderpaymentForm(paymentDetailMap);
		jsonObject.put("content", contMap);		
		return jsonObject;
	}

	@Override
	public JSONObject viewTopupApply(Map<String, Object> requestParamMap) {
		JSONObject jsonObject = new ResultJSONObject("000","查看银行转账凭证");
		List<Map> topupApplys = merchantPaymentDetailsDao.findTopupApplys(requestParamMap);

		for (Map<String, Object> photoMap : topupApplys) {
			BusinessUtil.disposePath(photoMap, "path");
		}
		jsonObject.put("path", topupApplys);
		
		return jsonObject;
	}

	@Override
	@Transactional
	public void updatePayVoucher(Map<String, Object> requestParamMap) {
		requestParamMap.put("applyStatus", 1);
		// 获取服务类型
		String serviceType = (String) requestParamMap.get("type");
		String merchantId =  (String) requestParamMap.get("merchantId");
		List<String> paths = (List<String>) requestParamMap.get("paths");
		String payNo =  (String) requestParamMap.get("payNo");
		if(serviceType.equals("topup")){//充值
			int a= merchantPaymentDetailsDao.updateTopupApplyStatus(requestParamMap);
		}else if(serviceType.equals("vip")){//vip
			merchantPaymentDetailsDao.updateVipApplyStatus(requestParamMap);
		}else{//员工数
			merchantPaymentDetailsDao.updateEmployeeApplyStatus(requestParamMap);
		}
		commonCacheService.deleteObject(CacheConstants.VALUE_ADD_SERVICE, merchantId.toString());
		// 删除旧凭证
		int a= merchantPaymentDetailsDao.deleteTopupApplyFile(requestParamMap);
		this.savePayApplyFile(paths, payNo, serviceType);
	}

	@Override
	public JSONObject addGrapMoneyNeeConfirm(Map<String, Object> params) {
		JSONObject jsonObject = new ResultJSONObject("000","抢单金充值支付待确认记录增加成功");
		int paymentType=StringUtil.nullToInteger(params.get("paymentType"));
        String innerTradeNo=StringUtil.nullToString(params.get("innerTradeNo"));
        String merchantId = StringUtil.nullToString(params.get("merchantId"));
        String payTime=StringUtil.nullToString(params.get("paymentTime"));
        int pkgId=StringUtil.nullToInteger(params.get("packageId"));
        double payAmount=StringUtil.nullToDouble(params.get("paymentAmount"));
        int employeeNum = StringUtil.nullToInteger(params.get("increaseEmployeeNum"));
        String inviteCode =StringUtil.nullToString(params.get("inviteCode"));
        String appType =StringUtil.nullToString(params.get("appType"));
        Integer clientType =StringUtil.nullToInteger(params.get("clientType"));
        Map<String,Object>  applyMap = new HashMap<String,Object>();
        applyMap.put("merchantId", merchantId);
        applyMap.put("increaseEmployeeNum", employeeNum);
        applyMap.put("appType", appType);
        applyMap.put("applyStatus", 0);
        applyMap.put("money", payAmount);
        applyMap.put("payNo", null);
        applyMap.put("payType", paymentType);
        applyMap.put("clientType", clientType);
        applyMap.put("buyConfirm", 0);
        applyMap.put("innerTradeNo", innerTradeNo);
        applyMap.put("inviteCode", inviteCode);
        if (merchantPaymentDetailsDao.checkTopupApplyByInnerTradeNo(applyMap)==0){
        		merchantPaymentDetailsDao.addTopupApplyWithConfirm(applyMap);
        }else{
        	jsonObject = new ResultJSONObject("001","回调请求已提前到达并插入记录");
        }
        return jsonObject;
		
	}
}
