package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户收支明细记录表Dao
 */
public interface IMerchantPaymentDetailsDao {

	/** 查询收支明细记录数 */
	int selectPaymentDetailsCount(Map<String, Object> paramMap);

	/** 查询收支明细 */
	List<Map<String, Object>> selectPaymentDetails(Map<String, Object> paramMap);

	/** 插入收支明细记录 */
	int insertPaymentDetails(Map<String, Object> paramMap);

	/**
	 * 新增订单金额充值申请记录
	 * 
	 * @param paramMap
	 * @return
	 */
	int addTopupApply(Map<String, Object> paramMap);

	/**
	 * 查询订单余额收支明细记录数量
	 * 
	 * @param paramMap
	 * @return
	 */
	int selectOrderPaymentDetailsCount(Map<String, Object> paramMap);

	/**
	 * 查询订单余额收支明细记录列表
	 * 
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> selectOrderPaymentDetails(Map<String, Object> paramMap);

	/** 根据订单号确认是否已经添加 */
	Integer checkTopupApplyByPayNo(Map<String, Object> paramMap);
	
	
	/**
	 * 根据内部交易号判断待确认记录是否存在
	 * 
	 */
	
	Integer checkTopupApplyByInnerTradeNo(Map<String,Object> paramMap);
	
	
	
	/****查询商户交易明细 含店铺转入 addby xmsheng 2016/7/5*****/
	List<Map<String,Object>> selectPaymentDetailsList(Map<String,Object> parasMap);
	
	
	  /** 统计交易明细，添加转出记录统计 addby xmSheng 2016/7/5 **/
     int selectPaymentlistCount(Map<String,Object> paramsMap);
     
     /***查询订单的交易详细信息 ***/
     Map<String,Object> selectOrderPaymentDetailInfo(Map<String,Object> params);
     
     /*** 查询转收入转出的交易的详细信息 ***/
     Map<String,Object> selectTransoutPaymentDetailInfo(Map<String,Object> params);
     
     /***查询订单奖励交易详细信息***/
     Map<String,Object> seletOrderAwardPaymentDeatilInfo(Map<String,Object> params);
     
     /*****查询活动交易详细信息***/
     Map<String,Object> selectActivityPaymentDetailInfo(Map<String,Object> params);
     
     /*****剪彩红包***/
     Map<String,Object> selectCuttingPaymentDetailInfo(Map<String,Object> params);

     /*****保存转账凭证附件***/
	int saveTopupApplyFile(List<Map> filePaths);

    /*****查看转账凭证附件***/
	List<Map> findTopupApplys(Map<String, Object> requestParamMap);

    /*****删除转账凭证附件***/
	int deleteTopupApplyFile(Map<String, Object> paramMap);

    /*****根据payNo转账凭证服务类型***/
	String getServiceType(Map<String, Object> paramMap);

	/*****更新充值审核状态***/
	int updateTopupApplyStatus(Map<String, Object> paramMap);
	/*****更新vip审核状态***/
	int updateVipApplyStatus(Map<String, Object> paramMap);
	/*****更新最大员工书审核状态***/
	int updateEmployeeApplyStatus(Map<String, Object> paramMap);
     
	/**
	 * 
	 *  ｛获取订单交易状态｝
	 *  @param params
	 *  @return
	 *  @author Liuxingwen
	 *  @created 2016年10月26日 下午3:55:54
	 *  @lastModified       
	 *  @history
	 */
	 Map<String,Object> selectpaymentStatus(Map<String,Object> params);
	 
	 Map<String,Object> selectpaymentByOrderId(Map<String,Object> params);
	 
	 /**
	  * 增加抢单金记录-----有确认版本
	  * @param params
	  * @return
	  */
	 int addTopupApplyWithConfirm(Map<String,Object> params);
	 
	 
	 /**
	  * 更新增加抢单金待确认记录标志为已确认
	  * @param params
	  * @return
	  */
	 int confirmGrapBuyRecord(Map<String,Object> params);
}
