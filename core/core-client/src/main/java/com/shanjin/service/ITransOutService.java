package com.shanjin.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
/*import com.shanjin.opay.api.bean.Header;*/

/**
 * 金额转出服务
 * @author xmSheng
 *
 */
public interface ITransOutService {/*
*//**
	 * 金额转出到钱包
	 * @param userId
	 * @param merchantId
	 * @param moneyAmount
	 * @param userPhone
	 * @param appType
	 * @return
	 *//*
	public JSONObject transMoneyToWallet(Map<String,Object> parasMap,Header header) throws Exception;
	
	
	*//**保存商户转出至钱包日志记录**//*
	public int saveTransoutLogs(Map<String,Object> paramsMap) ;
	
	*//**进行转出参数校验 单独抽离，不做事务控制**//*
	public JSONObject transoutCheck(Map<String,Object> parasMap,Header header);
		
	*//***删除系统异常日志**//*
	public int deleteSystemErrorLogs(Long errorId);
	
	*//**收入转出预处理**//*
	public Long transoutBefore(Map<String,Object> parasMap,Header header);

	*//**收入转出后处理**//*
	public JSONObject transoutAfter(Map<String,Object> parasMap,JSONObject jsonObject) throws Exception;

*/}
