/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.service <br>
 * FileName: IMerEvalStatiService.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年8月22日
 * @last Modified 
 * @history
 */
package com.shanjin.service;

import java.util.List;
import java.util.Map;

/**
 * {好评奖励统计}
 * 
 * @author Liuxingwen
 * @created 2016年8月22日 下午12:39:23
 * @lastModified
 * @history
 */
public interface IMerEvalStatiService {
	
	
	public void printLogs(Boolean isprint, String strContent)throws Exception;

	/**
	 * 
	 * ｛评价信息按天统计（前一天信息统计）｝
	 * 
	 * @param params
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月22日 下午12:41:30
	 * @lastModified
	 * @history
	 */
	public void EvaluationByDaysStatis(Map<String, Object> params)
			throws Exception;
	
	
	public Integer addMerchantEvaluationStatisByDayListCount(Map<String, Object> params)
			throws Exception;
	public int addMerchantEvaluationStatisByDayList(Map<String, Object> params)
			throws Exception;
	
	public String selectdbDateTime(Map<String, Object> params)
			throws Exception;
	
	public Integer selectMerchantByOrderNoStatisCount(Map<String, Object> params)
			throws Exception;
	
	public int selectIntoMerchantByOrderNoStatisList(Map<String, Object> mpMap)
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
	 *  @historyString dbtimeString,long starTime
	 */
	public Integer updateMerchantsEvaluationStatisByDayList(List<Map<String, Object>> listdatas,Map<String, Object> param)
			throws Exception;
//	public Integer updateMerchantsEvaluationStatisByDayList2(Map<String, Object> param)
//			throws Exception;

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
	public Integer updatemsaveList(List<Map<String, Object>> listdatas,Map<String, Object> mpMap)
			throws Exception;
	public Integer updatemsaveList2(Map<String, Object> mpMap)
			throws Exception;
	
	
	public Integer selectnopjdatacount(Map<String, Object> mpMap)
			throws Exception;
	
	public Integer selectnopjdatadelete(Map<String, Object> mpMap)
			throws Exception;
	
	public  Integer getTotalGroupCountFromDay(Map<String,Object> param)
			throws Exception;
	
	public  void  addMonthFromDailySummary(Map<String,Object> param) throws Exception;
}
