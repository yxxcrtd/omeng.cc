/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.service.impl <br>
 * FileName: IMerEvalStatiServiceImpl.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年8月22日
 * @last Modified 
 * @history
 */
package com.shanjin.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.dao.IMerchantEvaluationStatisDao;
import com.shanjin.service.IMerEvalStatiService;

/**
 * {该处说明该构造函数的含义及作用}
 * 
 * @author Liuxingwen
 * @created 2016年8月22日 下午1:56:14
 * @lastModified
 * @history
 */
@Service("iMerEvalStatiService")
public class IMerEvalStatiServiceImpl implements IMerEvalStatiService {
	private static final Logger logger = Logger
			.getLogger(IMerEvalStatiServiceImpl.class);
	@Resource
	private IMerchantEvaluationStatisDao iMerchantEvaluationStatisDao;
	@Resource
	private IUserRelatedCacheServices userRelatedCacheServices;
	@Resource
	private ICommonCacheService commonCacheService;

	private StringBuffer sbf = new StringBuffer();

	/**
	 * 按天统计
	 * 
	 * @param params
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月22日 下午1:56:44
	 * @lastModified
	 * @history
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void EvaluationByDaysStatis(Map<String, Object> params)
			throws Exception {
		sbf = new StringBuffer();

		// String adddays = params.get("adddays").toString();
		String datetime = params.get("datetime").toString();
		// 按天统计前一天的数据写入按天统计表merchant_evaluation_statis_bydate
		// int addone = iMerchantEvaluationStatisDao
		// .addMerchantEvaluationStatisByDay(params);
		// sbf.append("datetime:"+datetime);
		// sbf.append("按天统计新增评价==>addMerchantEvaluationStatisByDay=" + addone);
		// sbf.append("\r\n");
		// 将天统计的累加到月统计总表里merchant_evaluation_statis
		int update = iMerchantEvaluationStatisDao
				.updateMerchantsEvaluationStatisByDay(params);
		sbf.append("datetime:" + datetime);
		sbf.append("\r\n");
		// sbf.append("按天统计的数据如果月统计表已有统计数据，则更新到月统计表==>updateMerchantsEvaluationStatisByDay="
		// + update);
		sbf.append("更新记录："
				+ update
				+ ",按天统计的数据如果月统计表已有统计数据，则更新到月统计表==>updateMerchantsEvaluationStatisByDay");
		// merchant_evaluation_statis_bydate
		sbf.append("\r\n");
		// // 按天统计的数据如果月统计没有，则写到月统计表merchant_evaluation_statis
		// int addone3 = iMerchantEvaluationStatisDao
		// .addMerchantsEvaluationStatisByDay(params);
		// sbf.append("datetime:"+datetime);
		// sbf.append("按天统计的数据如果月统计表没有统计数据，则写到月统计表==>updateMerchantsEvaluationStatisByDay="
		// + addone3);
		// sbf.append("\r\n");
		printLogs(true, sbf.toString());
		// printLogs(true,"adddays:" + adddays + ",addone:" + addone);
		// printLogs(true,"update:" + adddays + ",update:" + update);
		// printLogs(true,"adddays:" + adddays + ",addone3:" + addone3);

	}

	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月23日 上午11:37:47
	 * @lastModified
	 * @history
	 */
	@Override
	public Integer addMerchantEvaluationStatisByDayListCount(
			Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao
				.addMerchantEvaluationStatisByDayListCount(params);
	}

	/**
	 * 按天统计前一天的数据写入按天统计表merchant_evaluation_statis_bydate
	 * 
	 * @param params
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月22日 下午1:56:44
	 * @lastModified
	 * @history
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addMerchantEvaluationStatisByDayList(Map<String, Object> mpMap)
			throws Exception {
		// TODO Auto-generated method stub
		sbf = new StringBuffer();
		String datetime = mpMap.get("datetime").toString();
		int addlist = iMerchantEvaluationStatisDao
				.addMerchantEvaluationStatisByDayList(mpMap);
		sbf.append("datetime:" + datetime);
		sbf.append("\r\n");
		// sbf.append("monthValue="+mpMap.get("monthValue").toString());
		// sbf.append("按天统计前一天的数据写入按天统计表，批量写入merchant_evaluation_statis_bydate==>addMerchantEvaluationStatisByDayList="
		// + addlist);
		sbf.append("新增记录：" + addlist + ",按天统计，统计评价信息写入按天统计表");

		sbf.append("\r\n");
		sbf.append("耗时：");
		// sbf.append(System.currentTimeMillis()-Long.valueOf(mpMap.get("starTime").toString()));
		sbf.append((System.currentTimeMillis() - Long.valueOf(mpMap.get(
				"starTime").toString()))
				/ 1000 + "秒");
		sbf.append("\r\n");
		sbf.append("还剩下记录：");
		sbf.append(mpMap.get("totalCount").toString());
		sbf.append("\r\n");
		// long starTime = System.currentTimeMillis();
		printLogs(true, sbf.toString());
		return addlist;

	}

	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月23日 上午11:09:59
	 * @lastModified
	 * @history
	 */
	@Override
	public String selectdbDateTime(Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao.selectdbDateTime(params);
	}

	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月23日 上午11:37:47
	 * @lastModified
	 * @history
	 */
	@Override
	public Integer selectMerchantByOrderNoStatisCount(Map<String, Object> params)
			throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao
				.selectMerchantByOrderNoStatisCount(params);
	}

	/**
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月23日 下午1:42:05
	 * @lastModified
	 * @history
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int selectIntoMerchantByOrderNoStatisList(Map<String, Object> mpMap)
			throws Exception {
		sbf = new StringBuffer();
		int addlist = iMerchantEvaluationStatisDao
				.selectIntoMerchantByOrderNoStatisList(mpMap);
		sbf.append("开始时间" + mpMap.get("beginTime"));
		sbf.append(",结束时间=" + mpMap.get("endTime"));

		sbf.append("\r\n");
		sbf.append("新增记录：" + addlist + ",暂定月初1号开始统计统计上个月,没有评价的商户以5分写入汇总表");
		sbf.append("\r\n");
		sbf.append("耗时：");
		sbf.append((System.currentTimeMillis() - Long.valueOf(mpMap.get(
				"starTime").toString()))
				/ 1000 + "秒");
		sbf.append("\r\n");

		sbf.append("还剩下记录：");
		sbf.append(mpMap.get("totalCount").toString());
		sbf.append("\r\n");

		printLogs(true, sbf.toString());
		return addlist;
	}

	/**
	 * 按天统计的数据如果月统计没有，则批量写到月统计表merchant_evaluation_statis
	 * 
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月23日 下午6:58:23
	 * @lastModified
	 * @history
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addMerchantsEvaluationStatisByDayList(Map<String, Object> mpMap)
			throws Exception {
		// TODO Auto-generated method stub
		sbf = new StringBuffer();
		String datetime = mpMap.get("datetime").toString();
		int addlist = iMerchantEvaluationStatisDao
				.addMerchantsEvaluationStatisByDayList(mpMap);
		sbf.append("datetime:" + datetime);
		sbf.append("\r\n");
		// sbf.append("monthValue="+mpMap.get("monthValue").toString());
		// sbf.append("按天统计的数据如果月统计没有，则批量写到月统计表merchant_evaluation_statis==>addMerchantEvaluationStatisByDayList="
		// + addlist);
		sbf.append("新增记录：" + addlist + ",按天统计，写入汇总表");
		sbf.append("\r\n");
		sbf.append("耗时：");
		// sbf.append(System.currentTimeMillis()-Long.valueOf(mpMap.get("starTime").toString()));
		sbf.append((System.currentTimeMillis() - Long.valueOf(mpMap.get(
				"starTime").toString()))
				/ 1000 + "秒");
		sbf.append("\r\n");

		sbf.append("还剩下记录：");
		sbf.append(mpMap.get("totalCount").toString());
		sbf.append("\r\n");

		printLogs(true, sbf.toString());
		return addlist;
	}

	/**
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月23日 下午7:07:50
	 * @lastModified
	 * @history
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addMerchantsEvaluationStatisByDayListCount(
			Map<String, Object> mpMap) throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao
				.addMerchantsEvaluationStatisByDayListCount(mpMap);
	}

	/**
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月24日 上午9:49:50
	 * @lastModified
	 * @history
	 */
	@Override
	public Integer selectMerchantsEvaluationStatisByDayListCount(
			Map<String, Object> param) throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao
				.selectMerchantsEvaluationStatisByDayListCount(param);
	}

	/**
	 * 按天统计，统计当天数据，汇总表里有的商户信息，则更新汇总表里评价次数等信息
	 * 
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月24日 上午9:49:50
	 * @lastModified
	 * @history
	 */
	@Override
	public List<Map<String, Object>> selectMerchantsEvaluationStatisByDayList(
			Map<String, Object> mpMap) throws Exception {
		return iMerchantEvaluationStatisDao
				.selectMerchantsEvaluationStatisByDayList(mpMap);
	}

	/**
	 * @param param
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月24日 上午10:09:14
	 * @lastModified
	 * @history
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer updateMerchantsEvaluationStatisByDayList(
			List<Map<String, Object>> listdatas, Map<String, Object> param)
			throws Exception {
		sbf = new StringBuffer();

		// String dbtimeString,long starTime
		// long starTime=long.v
		int addlist = iMerchantEvaluationStatisDao
				.updateMerchantsEvaluationStatisByDayList(listdatas);
		int addlist2 = iMerchantEvaluationStatisDao
				.updateMerchantsEvaluationStatisByDayListcumulative(listdatas);
		sbf.append("datetime:" + param.get("dbtimeString").toString());
		sbf.append("\r\n");
		sbf.append("更新记录：" + addlist + ",按天统计，更新汇总表评价数");
		sbf.append("\r\n");
		sbf.append("耗时：");
		// sbf.append((System.currentTimeMillis()-starTime)/1000+"秒");
		sbf.append((System.currentTimeMillis() - Long.valueOf(param.get(
				"starTime").toString()))
				/ 1000 + "秒");
		sbf.append("\r\n");
		sbf.append("还剩下记录：");
		sbf.append(param.get("totalCount").toString());
		sbf.append("\r\n");
		printLogs(true, sbf.toString());

		// paramsadd.put("dbtimeString", dbtimeString);
		// paramsadd.put("starTime", starTime);
		// paramsadd.put("totalCount", totalCount);

		return addlist;

	}

	/**
	 * @param listdatas
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月24日 下午7:59:38
	 * @lastModified
	 * @history
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer addnewmerchant_evaluation_statis_errorlog(
			Map<String, Object> listdatas) throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao
				.addnewmerchant_evaluation_statis_errorlog(listdatas);
	}

	/**
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月24日 下午7:59:38
	 * @lastModified
	 * @history
	 */
	@Override
	public List<Map<String, Object>> selectmerchant_evaluation_statis_errorlog(
			Map<String, Object> mpMap) throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao
				.selectmerchant_evaluation_statis_errorlog(mpMap);
	}

	/**
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月25日 上午8:36:58
	 * @lastModified
	 * @history
	 */
	@Override
	public Integer deletemerchant_evaluation_statis_errorlog(
			Map<String, Object> mpMap) throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao
				.deletemerchant_evaluation_statis_errorlog(mpMap);
	}

	/**
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月29日 上午11:40:36
	 * @lastModified
	 * @history
	 */
	@Override
	public Integer selectmsaveListcount(Map<String, Object> mpMap)
			throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao.selectmsaveListcount(mpMap);
	}

	/**
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月29日 上午11:40:36
	 * @lastModified
	 * @history
	 */
	@Override
	public List<Map<String, Object>> selectmsaveList(Map<String, Object> mpMap)
			throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao.selectmsaveList(mpMap);
	}

	/**
	 * @param listdatas
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月29日 上午11:40:36
	 * @lastModified
	 * @history
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer updatemsaveList(List<Map<String, Object>> listdatas,
			Map<String, Object> param) throws Exception {
		sbf = new StringBuffer();
		int addlist = iMerchantEvaluationStatisDao.updatemsaveList(listdatas);
		sbf.append("datetime:" + param.get("dbtimeString").toString());
		sbf.append("\r\n");
		sbf.append("更新记录：" + addlist + ",按天统计，更新汇总表评价数，平均值");
		sbf.append("\r\n");
		sbf.append("耗时：");
		// sbf.append((System.currentTimeMillis()-starTime)/1000+"秒");
		sbf.append((System.currentTimeMillis() - Long.valueOf(param.get(
				"starTime").toString()))
				/ 1000 + "秒");
		sbf.append("\r\n");

		sbf.append("还剩下记录：");
		sbf.append(param.get("totalCount").toString());
		sbf.append("\r\n");
		printLogs(true, sbf.toString());
		return addlist;
	}

	@Override
	public Integer updatemsaveList2(Map<String, Object> mpMap) throws Exception {
		sbf = new StringBuffer();
		int addlist = iMerchantEvaluationStatisDao.updatemsaveList2(mpMap);
		sbf.append("datetime:" + mpMap.get("dbtimeString").toString());
		sbf.append("\r\n");
		sbf.append("更新记录：" + addlist + ",按天统计，更新汇总表评价数，平均值");
		sbf.append("\r\n");
		sbf.append("耗时：");
		// sbf.append((System.currentTimeMillis()-starTime)/1000+"秒");
		sbf.append((System.currentTimeMillis() - Long.valueOf(mpMap.get(
				"starTime").toString()))
				/ 1000 + "秒");
		sbf.append("\r\n");

		sbf.append("还剩下记录：");
		sbf.append(mpMap.get("totalCount").toString());
		sbf.append("\r\n");
		printLogs(true, sbf.toString());
		return addlist;
	}

	/**
	 * @param isprint
	 * @param strContent
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月29日 下午5:01:52
	 * @lastModified
	 * @history
	 */
	@Override
	public void printLogs(Boolean isprint, String strContent) throws Exception {
		try {
			if (null == strContent)
				return;
			if (isprint) {
				// 需要输出的项
				// sbflog.append("==>" + strContent + ",当前时间：" + getFormatTime()
				// + "...>>End");
				// sbflog.append("\r\n");
				// System.out.println("=>" + strContent.toString() + ",当前时间："
				// + getFormatTime() + "...>>End");
				System.out.println("=>" + strContent.toString());

				BusinessUtil.writeLog("evaluation_stat", strContent.toString());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月30日 下午5:32:30
	 * @lastModified
	 * @history
	 */
	@Override
	public Integer selectnopjdatacount(Map<String, Object> mpMap)
			throws Exception {
		// TODO Auto-generated method stub
		return iMerchantEvaluationStatisDao.selectnopjdatacount(mpMap);
	}

	/**
	 * @param mpMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年8月30日 下午5:32:30
	 * @lastModified
	 * @history
	 */
	@Override
	public Integer selectnopjdatadelete(Map<String, Object> param)
			throws Exception {
		// TODO Auto-generated method stub
		// return iMerchantEvaluationStatisDao.selectnopjdatadelete(param);

		sbf = new StringBuffer();
		int addlist = iMerchantEvaluationStatisDao.selectnopjdatadelete(param);
		sbf.append("datetime:" + param.get("datetime").toString());
		sbf.append("\r\n");
		sbf.append("清除脏数据：" + addlist + ",5分好评");
		sbf.append("\r\n");
		sbf.append("耗时：");
		// sbf.append((System.currentTimeMillis()-starTime)/1000+"秒");
		sbf.append((System.currentTimeMillis() - Long.valueOf(param.get(
				"starTime").toString()))
				/ 1000 + "秒");
		sbf.append("\r\n");

		sbf.append("还剩下记录：");
		sbf.append(param.get("totalCount").toString());
		sbf.append("\r\n");
		printLogs(true, sbf.toString());
		return addlist;
	}

	
	public Integer getTotalGroupCountFromDay(Map<String, Object> param)
			throws Exception {
		return iMerchantEvaluationStatisDao.getTotalGroupCountFromDay(param);
	}

	public void  addMonthFromDailySummary(Map<String,Object> param) throws Exception{
		iMerchantEvaluationStatisDao.addToMonthStaticByDayGroup(param);
	}
}
