///**	
// * <br>
// * Copyright 2014 om.All rights reserved.<br>
// * <br>			 
// * Package: com.shanjin.service <br>
// * FileName: IMerchantEvaluationStatisService.java <br>
// * <br>
// * @version
// * @author Liuxingwen
// * @created 2016年8月16日
// * @last Modified 
// * @history
// */
//package com.shanjin.service;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * {该处说明该构造函数的含义及作用}
// * 
// * @author Liuxingwen
// * @created 2016年8月16日 下午6:09:45
// * @lastModified
// * @history
// */
//public interface IMerchantEvaluationStatisService {
//	
//	/**
//	 * 
//	 *  ｛好评奖励跑批任务实现｝
//	 *  @throws Exception
//	 *  @author Liuxingwen
//	 *  @created 2016年8月20日 下午4:22:45
//	 *  @lastModified       
//	 *  @history
//	 */
//	public void getMerchantByOrderStart(Map<String, Object> params) throws Exception;
//
//	/**
//	 * 
//	 * ｛根据订单获取，开通原子服务的商户列表｝
//	 * 
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月16日 下午3:48:31
//	 * @lastModified
//	 * @history
//	 */
//	public List<Map<String, Object>> getMerchantByOrder(
//			Map<String, Object> param) throws Exception;
//
//	/**
//	 * 
//	 * ｛根据订单获取，开通原子服务的商户列表总数｝
//	 * 
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 上午10:39:16
//	 * @lastModified
//	 * @history
//	 */
//	public Integer getMerchantByOrderCount(Map<String, Object> param)
//			throws Exception;
//
//	/**
//	 * 
//	 * ｛获取上月订单评价信息｝
//	 * 
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月16日 下午3:49:36
//	 * @lastModified
//	 * @history
//	 */
//
//	public List<Map<String, Object>> getEvaluationByMonth(
//			Map<String, Object> param) throws Exception;
//
//	public Integer getEvaluationByMonthCount(Map<String, Object> param)
//			throws Exception;
//
//	/**
//	 * 
//	 * ｛批量将评价统计信息添加到数据库｝
//	 * 
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月16日 下午5:26:00
//	 * @lastModified
//	 * @history
//	 */
//	public int addMerchantEvaluationStatis(List<Map<String, Object>> addList)
//			throws Exception;
//
//	/**
//	 * 
//	 * ｛将单个评价统计信息添加到数据库｝
//	 * 
//	 * @param mapparm
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午6:32:09
//	 * @lastModified
//	 * @history
//	 */
//	public int addMerchantEvaluationStatisOne(Map<String, Object> mapparm)
//			throws Exception;
//
//	/**
//	 * 
//	 * ｛批量逻辑删除统计数据｝
//	 * 
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月16日 下午6:12:20
//	 * @lastModified
//	 * @history
//	 */
//	public String delMerchantEvaluationStatis(Map<String, Object> param)
//			throws Exception;
//
//	/**
//	 * ｛批量修改｝
//	 * 
//	 * @param updateList
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午6:47:43
//	 * @lastModified
//	 * @history
//	 */
//	public int updateMerchantEvaluationStatis(
//			List<Map<String, Object>> updateList) throws Exception;
//
//	/**
//	 * ｛单个修改｝
//	 * 
//	 * @param updateList
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午6:47:43
//	 * @lastModified
//	 * @history
//	 */
//	public int updateMerchantEvaluationStatisOne(Map<String, Object> mpMap)
//			throws Exception;
//
//	/**
//	 * ｛获取上月的好评统计缓存｝
//	 * 
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 下午2:04:50
//	 * @lastModified
//	 * @history
//	 */
//	public List<Map<String, Object>> getMerChantEvaluationStatis(
//			Map<String, Object> param) throws Exception;
//
//	/**
//	 * ｛获取上月的好评统计缓存总数｝
//	 * 
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 下午2:04:50
//	 * @lastModified
//	 * @history
//	 */
//	public Integer getMerChantEvaluationStatisCount(Map<String, Object> param)
//			throws Exception;
//	
//	/**
//	 * 
//	 *  ｛获取数据库系统时间｝
//	 *  @return
//	 *  @throws Exception
//	 *  @author Liuxingwen
//	 *  @created 2016年8月18日 下午2:34:55
//	 *  @lastModified       
//	 *  @history
//	 */
//	public String getdbDateTime(Map<String, Object> param) throws Exception;
//}
