/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.dao <br>
 * FileName: IMerchantEvaluationStatisDao.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年8月16日
 * @last Modified 
 * @history
 */
package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * {商户评价统计信息}
 * 
 * @author Liuxingwen
 * @created 2016年8月16日 下午3:47:08
 * @lastModified
 * @history
 */
public interface IMerchantEvaluationStatisDao {

	/**
	 * 
	 * ｛根据订单获取，开通原子服务的商户列表｝
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月16日 下午3:48:31
	 * @lastModified
	 * @history
	 */
//	public List<Map<String, Object>> getMerchantByOrder(
//			Map<String, Object> param) throws Exception;

	/**
	 * 
	 * ｛根据订单获取，开通原子服务的商户列表总数｝
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月18日 上午10:39:16
	 * @lastModified
	 * @history
	 */
//	public Integer getMerchantByOrderCount(Map<String, Object> param)
//			throws Exception;

	/**
	 * 
	 * ｛获取上月订单评价信息｝
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月16日 下午3:49:36
	 * @lastModified
	 * @history
	 */

//	public List<Map<String, Object>> getEvaluationByMonth(
//			Map<String, Object> param) throws Exception;

	/**
	 * 
	 * ｛获取上月订单评价信息总数｝
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月18日 上午10:39:16
	 * @lastModified
	 * @history
	 */
//	public Integer getEvaluationByMonthCount(Map<String, Object> param)
//			throws Exception;

	/**
	 * 
	 * ｛批量将评价统计信息添加到数据库｝
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月16日 下午5:26:00
	 * @lastModified
	 * @history
	 */
//	public int addMerchantEvaluationStatis(List<Map<String, Object>> addList)
//			throws Exception;

	/**
	 * 
	 * ｛单个将评价统计信息添加到数据库｝
	 * 
	 * @param mapparm
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月17日 下午6:29:44
	 * @lastModified
	 * @history
	 */
//	public int addMerchantEvaluationStatisOne(Map<String, Object> mapparm)
//			throws Exception;

	/**
	 * 
	 * ｛批量逻辑删除统计数据｝
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月16日 下午6:12:20
	 * @lastModified
	 * @history
	 */
	// public String delMerchantEvaluationStatis(Map<String, Object> param)
	// throws Exception;

	/**
	 * ｛批量修改｝
	 * 
	 * @param updateList
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月17日 下午6:47:43
	 * @lastModified
	 * @history
	 */
//	public int updateMerchantEvaluationStatis(
//			List<Map<String, Object>> updateList) throws Exception;

	/**
	 * ｛单个修改｝
	 * 
	 * @param updateList
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月17日 下午6:47:43
	 * @lastModified
	 * @history
	 */
//	public int updateMerchantEvaluationStatisOne(Map<String, Object> mpMap)
//			throws Exception;

	/**
	 * ｛获取上月的好评统计缓存｝
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月18日 下午2:04:50
	 * @lastModified
	 * @history
	 */
//	public List<Map<String, Object>> getMerChantEvaluationStatis(
//			Map<String, Object> param) throws Exception;

	/**
	 * ｛获取上月的好评统计缓存总数｝
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月18日 下午2:04:50
	 * @lastModified
	 * @history
	 */
//	public Integer getMerChantEvaluationStatisCount(Map<String, Object> param)
//			throws Exception;

	/**
	 * 
	 * ｛获取数据库系统时间｝
	 * 
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月18日 下午2:34:55
	 * @lastModified
	 * @history
	 */
	public String getdbDateTime(Map<String, Object> param) throws Exception;

	
	/**
	 * 
	 *  ｛获取数据库系统时间字符串｝
	 *  @param param
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月23日 上午11:07:53
	 *  @lastModified       
	 *  @history
	 */
	public String selectdbDateTime(Map<String, Object> param) throws Exception;

	
	
	
	/**
	 * 
	 * ｛按天统计写到天统计表merchant_evaluation_statis_bydate｝
	 * 
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月22日 下午2:00:50
	 * @lastModified
	 * @history
	 */
	public int addMerchantEvaluationStatisByDay(Map<String, Object> mpMap)
			throws Exception;
	
	/**
	 * 
	 * ｛按天统计写到天统计表   比量merchant_evaluation_statis_bydate总数｝
	 * 
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月22日 下午2:00:50
	 * @lastModified
	 * @history
	 */
	public int addMerchantEvaluationStatisByDayListCount(Map<String, Object> mpMap)
			throws Exception;
	/**
	 * 
	 * ｛按天统计写到天统计表   比量merchant_evaluation_statis_bydate｝
	 * 
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月22日 下午2:00:50
	 * @lastModified
	 * @history
	 */
	public int addMerchantEvaluationStatisByDayList(Map<String, Object> mpMap)
			throws Exception;
	
	
	
	/**
	 * 
	 *  ｛按天统计的数据如果月统计没有，则写到月统计表merchant_evaluation_statis｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月22日 下午4:09:01
	 *  @lastModified       
	 *  @history
	 */
	public int addMerchantsEvaluationStatisByDay(Map<String, Object> mpMap)
			throws Exception;

	/**
	 * 
	 *  ｛按天统计的数据如果月统计没有，则批量写到月统计表merchant_evaluation_statis｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月22日 下午4:09:01
	 *  @lastModified       
	 *  @history
	 */
	public int addMerchantsEvaluationStatisByDayList(Map<String, Object> mpMap)
			throws Exception;
	/**
	 * 
	 *  ｛按天统计的数据如果月统计没有，则批量写到月统计表merchant_evaluation_statis总数｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月22日 下午4:09:01
	 *  @lastModified       
	 *  @history
	 */
	public int addMerchantsEvaluationStatisByDayListCount(Map<String, Object> mpMap)
			throws Exception;
	
	
	
	/**
	 * 
	 *  ｛按天统计的数据，累计到月统计merchant_evaluation_statis｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月22日 下午4:09:01
	 *  @lastModified       
	 *  @history
	 */
	public int updateMerchantsEvaluationStatisByDay(Map<String, Object> mpMap)
			throws Exception;
	
	/**
	 * 
	 *  ｛根据订单获取当月没有评价商户总数｝
	 *  @param param
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月23日 上午10:55:10
	 *  @lastModified       
	 *  @history
	 */
	public Integer selectMerchantByOrderNoStatisCount(Map<String, Object> param)
			throws Exception;

	/**
	 * 
	 *  ｛根据订单获取当月没有评价商户列表，写入merchant_evaluation_statis｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月23日 上午10:56:12
	 *  @lastModified       
	 *  @history
	 */
	public int selectIntoMerchantByOrderNoStatisList(Map<String, Object> mpMap)
			throws Exception;
	
	
	
	
	
	
	/**
	 * 
	 *  ｛按天统计，统计当天数据，汇总表里有的商户信息，则更新汇总表里评价次数等信息  分页总数｝
	 *  @param param
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月23日 上午10:55:10
	 *  @lastModified       
	 *  @history
	 */
	public Integer selectMerchantsEvaluationStatisByDayListCount(Map<String, Object> param)
			throws Exception;

	/**
	 * 
	 *  ｛按天统计，统计当天数据，汇总表里有的商户信息，则更新汇总表里评价次数等信息｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月23日 上午10:56:12
	 *  @lastModified       
	 *  @history
	 */
	public List<Map<String, Object>> selectMerchantsEvaluationStatisByDayList(Map<String, Object> mpMap)
			throws Exception;
	
	
	/**
	 * 
	 *  ｛按天统计，统计当天数据，汇总表里有的商户信息，则更新汇总表里评价次数等信息  分页处理 ｝
	 *  @param param
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月23日 上午10:55:10
	 *  @lastModified       
	 *  @history
	 */
	public Integer updateMerchantsEvaluationStatisByDayList(List<Map<String, Object>> listdatas)
			throws Exception;
	
//	public Integer updateMerchantsEvaluationStatisByDayList2(Map<String, Object> param)
//			throws Exception;
	
	public Integer updateMerchantsEvaluationStatisByDayListcumulative(List<Map<String, Object>> listdatas)
			throws Exception;
	/**
	 * 
	 *  ｛跑批任务出现错误时记录批次 ｝
	 *  @param param
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月23日 上午10:55:10
	 *  @lastModified       
	 *  @history
	 */
	public Integer addnewmerchant_evaluation_statis_errorlog(Map<String, Object> listdatas)
			throws Exception;
	
	/**
	 * 
	 *  ｛获取跑批任务出现错误时记录批次｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月23日 上午10:56:12
	 *  @lastModified       
	 *  @history
	 */
	public List<Map<String, Object>> selectmerchant_evaluation_statis_errorlog(Map<String, Object> mpMap)
			throws Exception;
	
	/**
	 * 
	 *  ｛处理错误的时候删除错误记录，下次不用再执行｝
	 *  @param id
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月25日 上午8:34:08
	 *  @lastModified       
	 *  @history
	 */
	public Integer deletemerchant_evaluation_statis_errorlog(Map<String, Object> mpMap)
			throws Exception;
	
	
	/**
	 * 
	 *  ｛计算平均值｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月29日 上午11:33:30
	 *  @lastModified       
	 *  @history
	 */
	public Integer selectmsaveListcount(Map<String, Object> mpMap)
			throws Exception;
	
	/**
	 * 
	 *  ｛计算平均值分页更新｝
	 *  @param mpMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月29日 上午11:34:23
	 *  @lastModified       
	 *  @history
	 */
	public List<Map<String, Object>> selectmsaveList(Map<String, Object> mpMap)
			throws Exception;
	
	
	/**
	 * 
	 *  ｛开始更新平价统计｝
	 *  @param listdatas
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年8月29日 上午11:39:16
	 *  @lastModified       
	 *  @history
	 */
	public Integer updatemsaveList(List<Map<String, Object>> listdatas)
			throws Exception;
	
	public Integer updatemsaveList2(Map<String, Object> mpMap)
			throws Exception;
	
	public Integer selectnopjdatacount(Map<String, Object> mpMap)
			throws Exception;
	
	public Integer selectnopjdatadelete(Map<String, Object> mpMap)
			throws Exception;
	
	
	/**
	 * 统计日评价按商户分组后的统计数据。
	 * @param mpMap
	 * @return
	 */
	public Integer getTotalGroupCountFromDay(Map<String, Object> param) throws Exception;
	
	
	/**
	 *  将日评价分组统计的结果插入到月统计表中
	 */
	public void addToMonthStaticByDayGroup(Map<String,Object> param) throws Exception;
	
}
