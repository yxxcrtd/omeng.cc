package com.shanjin.dao;

import java.util.Map;

/**
 * 商户店铺资金转出
 * @author xmsheng
 *
 */
public interface IMerchantTransoutDao {
	
	/**
	 * 保存店铺资金转入到钱包记录
	 * @param paramsMap
	 * @return
	 */
	int saveTransoutRecord(Map<String,Object> paramsMap);
	
	/**保存商户转出至钱包日志记录**/
	 int saveTransoutLogs(Map<String,Object> paramsMap) ;
	
	
	/**插入o盟系统宕机日志记录**/
	 int saveSystemErrorLogs(Map<String,Object> paramsMap);
	
	
	/***删除系统宕机日志记录**/
	 int deleteSystemErrorLogs(long errorId);
	 
	 
	 /***更新转入交易状态**/
	int updateTransoutRecordStatus(Map<String,Object> paramsMap);
	
}
