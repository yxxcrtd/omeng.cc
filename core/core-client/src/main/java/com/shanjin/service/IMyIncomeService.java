package com.shanjin.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface IMyIncomeService {

    /** 
     * 查询商户收入信息
     * @param appType
     * @param merchantId
     * @return
     * @throws Exception
     */
    public JSONObject selectMyIncome(String appType, Long merchantId)throws Exception;

    /** 
     * 绑定银行卡 初期化操作
     * @param appType
     * @param merchantId
     * @return
     * @throws Exception
     */
    public JSONObject bindingBank(String appType, Long merchantId)throws Exception;

    /** 
     * 可绑定的银行卡信息加载
     * @param appType
     * @param merchantId
     * @return
     * @throws Exception
     */
    public JSONObject applyWithdrawInit(String appType, Long merchantId)throws Exception;

    /** 
     * 保存申请提现
     * @param appType
     * @param merchantId
     * @param withdraw 银行卡类型
     * @param withdrawNo 银行卡号
     * @param withdrawPrice 提现金额
     * @param payPassword 支付密码
     * @return
     */
    public JSONObject insertApplyWithdrawRecord(String appType, Long merchantId, Long withdraw, String withdrawNo,
            String withdrawPrice, String payPassword,Short tip)throws Exception;

    /** 
     * 查询收支明细
     * @param appType
     * @param merchantId
     * @param pageNo
     * @return
     * @throws Exception
     */
    public JSONObject selectPaymentDetails(String appType, Long merchantId, int pageNo)throws Exception;
    /** 
     * 保存提现账号
     * @param requestParamMap
     * @return
     */
    public JSONObject insertMerchantWithdraw(Map<String, Object> requestParamMap) throws Exception;
    
    /** 
     * 找回支付密码 
     * @param requestParamMap 前端返回的参数
     * @return 返回验证结果
     * @throws Exception
     */
    public JSONObject findPayPassWord(Map<String, Object> requestParamMap)throws Exception;
    
    /** 
     * 查询商户已绑定的提现账户信息 
     * @param appType 应用类型
     * @param merchantId 商户ID
     * @return
     */
    public JSONObject getBindedBankCards(String appType, Long merchantId) throws Exception ;
    /**
     * 解绑银行卡 
     * @param appType 应用类型
     * @param merchantId 商户ID
     * @param withdrawId 商户-银行卡表ID
     * @return 
     */
    public JSONObject unbindBankCard(String appType, Long merchantId,Long withdrawId,String payPassword)throws Exception;
    
    /** 
     * 验证支付密码
     * @param appType 应用类型
     * @param merchantId 商户ID
     * @param payPassword 支付密码
     * @return 验证结果
     * @throws Exception
     */
    public JSONObject verificationPayPassword(String appType, Long merchantId,String payPassword)throws Exception;
    
    /**
     * 修改支付密码
     * @param appType应用类型
     * @param merchantId商户ID
     * @param newPayPassword 新支付密码
     * @throws Exception
     */
    public JSONObject updPayPassword(String appType, Long merchantId,String newPayPassword)throws Exception;
    
    /**
     * 订单余额充值申请
     * @param appType
     * @param merchantId
     * @param money 申请金额
     * @return
     * @throws Exception
     */
    public JSONObject topupApply(Map<String,Object> map)throws Exception;
    
    /**
     * 保存线下支付凭证附件
     * @param paths
     * @param payNo
     * @param serviceType 业务类型      topup 充值 ，vip vip申请 ，employeesNum 顾问号申请
     * @return 保存结果数
     */
    public int savePayApplyFile(List<String> paths,String payNo,String serviceType);
    
    /**
     * 订单余额收支明细
     * @param appType
     * @param merchantId
     * @param pageNo
     * @return
     * @throws Exception
     */
    public JSONObject orderPaymentDetails(String appType, Long merchantId,int pageNo)throws Exception;
    
    
    /**
     * 查询交易明细，新添加收入转入   addby xmsheng 2016/7/5 
     * @param appType
     * @param merchantId
     * @param pageNo
     * @param todayQuery 是否查询今日订单 1：查询今日订单 只查询5条记录
     * @param paymentType 明细类型 -1：查询全部 0：订单支付  1：账户提现  2：剪彩红包 3：粉丝奖 4：城市人气服务商奖 5：订单奖励  6：收入转入
     * @return 
     * @throws Exception
     */
    public JSONObject selectPaymentDetailsList(String appType, Long merchantId, int pageNo,int todayQuery,int paymentType) throws Exception;
    
    
    /**
     * 查询交易明细详细信息 ，
     * @param merchantId
     * @param paymentType
     * @param paymentId
     * @return
     */
    public JSONObject selectPaymentDetailInfo(int paymentType,Long paymentId);

    /**
     * 查看银行转账凭证
     * @param requestParamMap
     * @return
     */
	public JSONObject viewTopupApply(Map<String, Object> requestParamMap);

	/**
	 * 更新交易状态
	 * @param paths
	 * @param payNo
	 * @param object
	 */
	public void updatePayVoucher(Map<String, Object> requestParamMap);
  
	
	/**
	 * 增加抢单金待确认记录
	 * @param param
	 */
	public JSONObject addGrapMoneyNeeConfirm(Map<String,Object> param);
}
