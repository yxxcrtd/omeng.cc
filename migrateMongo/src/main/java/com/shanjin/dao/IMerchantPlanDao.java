package com.shanjin.dao;

import java.util.List;
import java.util.Map;


/**
 * 历史订单报价方案迁移的相关DAO
 * @author RevokeYu  2016.9.21
 *
 */
public interface IMerchantPlanDao {

	/**
	 * 按订单ids获取 报价方案列表
	 * @param orderIds
	 * @return
	 */
	List<Map<String,Object>>  getPlanListByOrderIds(Map<String,String> orderIds);
	
	/**
	 * 按报价方案ids 获取报价方案的附件列表
	 * @param planIds
	 * @return
	 */
	List<Map<String,Object>>  getPlanAttachmentListByPlanIds(Map<String,Object> planIds);
	
	
	/**
	 * 按报价方案ids 获取报价方案中的商品列表
	 * @param planIds
	 * @return
	 */
	List<Map<String,Object>>  getPlanGoodsListByPlanIds(Map<String,Object> planIds);
	
	
	
	
	/**
	 * 获取被选中的报价方案的商家信息
	 * @param orderIds
	 * @return
	 */
	List<Map<String,Object>>  getWinnerInfos(Map<String,Object> orderIds);
	
	
	/**
	 * 获取商户在指定时间有没订购某增值服务。
	 * @param param
	 * @return
	 */
	Integer getIncBookNum(Map<String,Object> param);
	
	
	/**
	 * 按订单的ids 获取报价方案的ids
	 * @param params
	 * @return
	 */
	String getMerchantPlanIds(Map<String,Object> params);
	
	
	/**
	 * 按报价方案的ids 删除报价方案的附件
	 * @param params
	 */
	void delMerchantPlansAttachmentsIds(Map<String,Object> params);
	
	
	
	/**
	 * 按报价方案的ids 删除报价方案对应的明细
	 * @param params
	 */
	void delMerchantPlansDetailsByIds(Map<String,Object> params);
	
	
	/**
	 * 按报价方案的ids 删除报价方案中的商品
	 * @param params
	 */
	void delMerchantPlansGoods(Map<String,Object> params);
	
	
	/**
	 * 按订单ids 删除商户报价方案
	 * @param params
	 */
	void delMerchantPlansByIds(Map<String,Object> params);
	
	
}
