package com.shanjin.dao;


import java.util.Date;
import java.util.Map;

/**
 * 推送清理记录DAO
 * @author Revoke 2016.7.12
 *
 */
public interface IPurifyDao {
	//取消订单的清理
	public static String  PURIFY_CANCEL ="PURIFY_CANCEL";
	
	//无报价方案的订单的清理
	public static String  PURIFY_NO_BID ="PURIFY_NO_BID";
	
	//未选定报价方案的订单的清理
	public static String  PURIFY_NO_CHOOSED ="PURIFY_NO_CHOOSED";
	
	
	//已选择报价方案的订单，对未提供报价方案的清理
	public static String  PURIFY_IN_PROCESS ="PURIFY_IN_PROCESS";	

	//取消订单的清理
	public static String  NMINUTES_NOPLANORDER_TO_PUSH ="NMINUTES_NOPLANORDER_TO_PUSH";
	
	/**
	 * 新增一条推送清理记录
	 */
	public void insertPurify(Map<String,Object> purifyVO);
	
	
	/**
	 * 获取某个推送清理的最后一次处理记录
	 * @param purifyType
	 * @return
	 */
	public Map<String,Object> getLastPurify(String purifyType);

}
