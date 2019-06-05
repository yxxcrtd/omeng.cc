/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.dao <br>
 * FileName: IMerchantInvoiceDao.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年10月14日
 * @last Modified 
 * @history
 */
package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * {开票申请}
 * 
 * @author Liuxingwen
 * @created 2016年10月14日 上午11:45:15
 * @lastModified
 * @history
 */
public interface IMerchantInvoiceDao {

	/**
	 * 
	 *  ｛获取未开发票的增值服务列表｝
	 *  @param paramMap:merchant_id,
	 *  @return JSONObject
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年10月14日 上午11:51:45
	 *  @lastModified       
	 *  @history
	 */
	public List<Map<String, Object>> selectIncOrders(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 
	 *  ｛获取商户自然月开票次数｝
	 *  @param paramMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年10月14日 下午3:00:35
	 *  @lastModified       
	 *  @history
	 */
	public int selectInvoiceCount(Map<String, Object> paramMap) throws Exception;
	
	
	/**
	 * 
	 *  ｛保存发票信息｝
	 *  @param paramMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年10月17日 下午2:34:17
	 *  @lastModified       
	 *  @history
	 */
	public int insertInvoice(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 
	 *  ｛保存发票附属信息｝
	 *  @param paramMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年10月17日 下午2:35:18
	 *  @lastModified       
	 *  @history
	 */
	public int insertInvoiceInfo(List<Map<String, Object>> lists) throws Exception;
	
	
	public Map<String, Object> selectinvoice1(Map<String, Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> selectinvoice2(Map<String, Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> selectinvoices(Map<String, Object> paramMap) throws Exception;
	
	public int selectinvoicesCount(Map<String, Object> paramMap) throws Exception;
	
	public int updateMerchant_topup_apply(List<Integer> lists) throws Exception;
	
	public int updateMerchant_employees_num_apply(List<Integer> lists) throws Exception;
	public int updateInc_pkg_order(List<Integer> lists) throws Exception;
	
	
	public List<Map<String, Object>> selectInvoiceInfoList(List<Integer> lists) throws Exception;
	
	public int selectInvoiceInfoCount(List<Integer> lists) throws Exception;
	
	
	public List<Map<String, Object>> selectInvoiceInfoList2(List<Map<String, Object>> lists) throws Exception;
}
