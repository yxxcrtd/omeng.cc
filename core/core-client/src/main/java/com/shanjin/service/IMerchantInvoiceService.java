/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.service <br>
 * FileName: IMerchantInvoiceService.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年10月14日
 * @last Modified 
 * @history
 */
package com.shanjin.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * {开票申请}
 * 
 * @author Liuxingwen
 * @created 2016年10月14日 下午1:35:14
 * @lastModified
 * @history
 */
public interface IMerchantInvoiceService {
	
	
	public JSONObject selectIncOrders(Map<String, Object> paramMap)
			throws Exception;

	/*保存发票*/
	public JSONObject saveInvoice(Map<String, Object> paramMap)
			throws Exception;
	
	/*获取发票详情*/
	public JSONObject selectInvoice(Map<String, Object> paramMap)
			throws Exception;
	

	/*获取发票历史记录*/
	public JSONObject getInvoiceList(Map<String, Object> paramMap)
			throws Exception;

}
