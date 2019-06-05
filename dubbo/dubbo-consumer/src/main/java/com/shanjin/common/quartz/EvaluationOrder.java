/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.common.quartz <br>
 * FileName: EvaluationOrder.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年8月16日
 * @last Modified 
 * @history
 */
package com.shanjin.common.quartz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.cache.service.impl.JedisLock;
import com.shanjin.common.util.DateUtil;
import com.shanjin.service.IMerEvalStatiService;

/**
 * {商户评价统计信息}
 * 
 * @author Liuxingwen
 * @created 2016年8月16日 下午8:14:05
 * @lastModified
 * @history
 */
public class EvaluationOrder {
	
	/**
	 * 按天统计：统计当前时间前几天的数据，暂定为统计当前时间，提前一天的数据
	 */
	private int addday;
	
	/**
	 * 月初的时候统计上个月暂定为月初一号，统计没有好评的商户，计算好评平均分
	 */
	private int startday;

	/**
	 * @return 返回 startday.
	 */
	public int getStartday() {
		return startday;
	}
	/**
	 * @return 返回 addday.
	 */
	public int getAddday() {
		return addday;
	}
	/**
	 * @param addday 设置 addday
	 */
	public void setAddday(int addday) {
		this.addday = addday;
	}
	/**
	 * @param startday
	 *            设置 startday
	 */
	public void setStartday(int startday) {
		this.startday = startday;
	}
	int pageSize = 1000;
	// 本地异常日志记录对象
	private static final Logger logger = Logger
			.getLogger(EvaluationOrder.class);
//	@Resource
//	private IMerchantEvaluationStatisService iMerchantEvaluationStatisService;
	@Resource
	private IUserRelatedCacheServices userRelatedCacheServices;
	@Resource
	private IMerEvalStatiService iMerEvalStatiService;
	private boolean isrun = false;
	private StringBuffer sbf = new StringBuffer();

	/**
	 * 
	 *  ｛JOB入口｝
	 *  @author Liuxingwen
	 *  @created 2016年8月24日 上午11:52:28
	 *  @lastModified       
	 *  @history
	 */ 
	public void getMerchantByOrder() {
		if (isrun) {
			printLogs(false, "getMerchantByOrder好评奖励统计跑批任务开始");
			printLogs(false, "getMerchantByOrder跑批任务正在进行中，本次任务跳过");
			return;
		}
		JedisLock schduleLock = null;
		sbf = new StringBuffer();
		long starTime = System.currentTimeMillis();
		printLogs(true, "getMerchantByOrder好评奖励统计跑批任务开始*");
		try {
			isrun = true;
			schduleLock = userRelatedCacheServices.getEvaluationOrderLock();
			if (schduleLock.acquire()) {
				Map<String, Object> params = new HashMap<String, Object>();
//				params.put("adddays", 59);//56
				params.put("adddays", this.addday);
				// select date(date_sub(curdate(),interval 115 day));
				EvaluationByDaysStatisStart(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
			printLogs(true, e.getMessage());
		} finally {
			try {
				printLogs(true,"getMerchantByOrder好评奖励统计跑批任务结束,耗时："+ ((System.currentTimeMillis() - starTime) / 1000)+ "秒");
				if (null != schduleLock) {
					userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
				}
			} catch (Exception e2) {
			}
			isrun = false;
		}
	}
	private void EvaluationByDaysStatisStart(Map<String, Object> params) {
		long starTime = System.currentTimeMillis();
		try {
			Integer addd = Integer.valueOf(String.valueOf(params.get("adddays")).trim());
			// select date(date_sub(curdate(),interval 200 day))
			//判断提前跑数据的天数，最低跑前一天的数据，当天数据延后一天
			if (addd <= 0)
				return;
			String dbtimeString = iMerEvalStatiService.selectdbDateTime(params);
			
			//清除未到月底的5分好评数据
			selectnopjdatadelete(10000, 1, 0, addd.toString(),dbtimeString);
			params.put("datetime", dbtimeString);
			Map<String, Object> errorMap=null;
			// 按天统计前一天的数据写入按天统计表merchant_evaluation_statis_bydate
			addMerchantEvaluationStatisByDayList(10000, 1, 0, addd.toString(),dbtimeString);
			// (1)按天统计//待优化注意顺序不能乱，先更新，再添加
            //iMerEvalStatiService.EvaluationByDaysStatis(params);//原先方法先不删除
			int callorderCount=1;//调用次数，以便作为事件出错时用。
			/* 改为在追加无评级商户到月统计表前，一次新从日表汇总到月表中   Revoke 2016.09.14 
			//(1)按天统计数据，如果月汇总表里已有本月评价商户数据，则累加商户评价数量（因为是增量更新，所以需要做容错处理，key=1,callorderCount是标识 ）
			Map<String, Object> paraMap2 = new HashMap<String, Object>();
			paraMap2.put("adddays", addd);
			int totalCount2 = iMerEvalStatiService.selectMerchantsEvaluationStatisByDayListCount(paraMap2);
			errorMap=getErrorLog(1,dbtimeString);
			//按天统计，扫描月统计数据，当月统计数据已存在商户，则累加评价总数
			selectMerchantsEvaluationStatisByDayList(1000, 1, totalCount2, addd.toString(),dbtimeString,callorderCount,1,errorMap);//key:1
			// (2)按天统计统计前一天的数据如果月统计没有，则批量写到月汇总统计表merchant_evaluation_statis
			callorderCount=1;//调用次数，以便作为事件出错时用。（因为是增量更新，所以需要做容错处理，callorderCount是错误行号标识 ）
			errorMap=getErrorLog(2,dbtimeString);
			addMerchantsEvaluationStatisByDayList(10000, 1, 0, addd.toString(),dbtimeString,callorderCount,2,errorMap);//key:2
			
			// 判断是否为月初,暂定为每月一号整理，月初月统计上个月的评价数量
			// 月底统计没有评价的商户信息
			//Date date = StrToDate(dbtimeString);
			//String dbtimeString2 = DateToStr(date, new SimpleDateFormat("dd"));
			*/
			
			
			//改为在指定的日期，统计上月无评价的商户信息   Revoke 2016.9.8
			Date toDay= new Date(starTime);
			if (this.startday == toDay.getDate() && addd==1) {
				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(toDay);
				rightNow.add(Calendar.MONTH, -1);// 跑上个月的数据，日期加3个月
				Date dt1 = rightNow.getTime();
				String reStr = DateToStr(dt1, new SimpleDateFormat("yyyy-MM"));// "yyyy-MM-dd HH:mm:ss"
				int totalCount = 0;
//				Map<String, Object> paraMap = new HashMap<String, Object>();
//				paraMap.put("monthValue", reStr);
//				totalCount = iMerEvalStatiService.selectMerchantByOrderNoStatisCount(paraMap);
//				if (totalCount <= 0)
//					return;
//				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				//评价的开始时间 及结束时间 
				String beginTime = DateUtil.getFirstDayOfMonth(reStr)+" 00:00:00";
				String endTime = DateUtil.getLastDayOfMonth(reStr) +" 23:59:59";
				String monthValue =DateToStr(dt1, new SimpleDateFormat("yyyyMM"));
				
				//汇总日评价到月评价表中
				genMonthFromDailyRecord(monthValue);
				
				//将没有评价的商户以5分好评写入月汇总表
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("beginTime", beginTime);
				paraMap.put("endTime", endTime);
				totalCount = iMerEvalStatiService.selectMerchantByOrderNoStatisCount(paraMap);
				
				selectIntoMerchantByOrderNoStatisList(10000, 1,totalCount, beginTime,endTime,monthValue);
				//计算平均分
				selectmsaveList(1000, 1, -1, monthValue,dbtimeString,callorderCount,3,errorMap);//key:3
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
//				Thread.sleep(6000);
				int addays = Integer.valueOf(params.get("adddays").toString().trim());
				printLogs(true,iMerEvalStatiService.selectdbDateTime(params)+"，耗时："+((System.currentTimeMillis() - starTime) / 1000)+"秒");
//				printLogs(true,"========================================================================================================");
				addays--;
				if (addays <= 0)
					return;
				params.remove("adddays");
				params.put("adddays", addays);
				params.remove("starTime");
				EvaluationByDaysStatisStart(params);
			} catch (Exception e2) {
			}
		}
	}
	
	
	/**
	 * 将日评价中的评价记录按merchant_id汇总，并插入到月评价中
	 * @param monthValue
	 */
	private void genMonthFromDailyRecord(String monthValue) throws Exception {
		 Map<String,Object> param =new HashMap<String,Object>();
		 param.put("statsMonth", monthValue);
		 int totalRecord= iMerEvalStatiService.getTotalGroupCountFromDay(param);
		 int pagesize=10000;
		 int totalPageCount = totalRecord % pagesize == 0 ? totalRecord / pagesize : totalRecord / pagesize + 1;
		 int  startli=0;
		 param.put("pagesize", pagesize);
		 for (int pageNo=1;pageNo<=totalPageCount;pageNo++){
			 startli = (pageNo-1)*pagesize;
			 param.put("startli", startli);
			 iMerEvalStatiService.addMonthFromDailySummary(param);
		 }
		
	}
	/**
	 * 
	 *  ｛清除脏数据｝
	 *  @param pagesize
	 *  @param currentpage
	 *  @param totalCount
	 *  @param adddays
	 *  @param dbtimeString
	 *  @author Liuxingwen
	 *  @created 2016年8月30日 下午5:34:08
	 *  @lastModified       
	 *  @history
	 */
	private void selectnopjdatadelete(int pagesize,
			int currentpage, int totalCount, String adddays, String dbtimeString) {
		int totalPageCount=0;
		try {
			pagesize=10000;
			long starTime = System.currentTimeMillis();
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("adddays", adddays);
			totalCount = iMerEvalStatiService.selectnopjdatacount(paraMap);
			if (totalCount <= 0)
			{
				StringBuffer sbf = new StringBuffer();
				sbf.append("datetime:" + dbtimeString);
				sbf.append("\r\n");
				sbf.append("清除脏数据：0 ,5分好评");
				sbf.append("\r\n");
				printLogs(true,sbf.toString());
				return;
			}
			currentpage = 1;
			totalPageCount = totalCount % pagesize == 0 ? totalCount / pagesize : totalCount / pagesize + 1;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("adddays", adddays);
			params.put("pagesize", pagesize);
			params.put("datetime", dbtimeString);
			params.put("starTime", starTime);
			int startlie = 0;
			startlie = (currentpage - 1) * pagesize;
			params.put("startli", startlie);
			params.put("totalCount", totalCount);
			int add = iMerEvalStatiService.selectnopjdatadelete(params);
			if (currentpage < totalPageCount) {
//				currentpage++;
//				callorderCount++;//调用次数，以便作为事件出错时用。
				// TDODThread.sleep(1000);
				selectnopjdatadelete(pagesize, currentpage,totalCount, adddays, dbtimeString);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			
			sbf.append("datetime:" + dbtimeString);
			sbf.append("\r\n");
			sbf.append("清除脏数据：0 ,5分好评"+e.getMessage());
			sbf.append("\r\n");
			printLogs(true,sbf.toString());
			if (currentpage < totalPageCount) {
//				currentpage++;
//				callorderCount++;//调用次数，以便作为事件出错时用。
				// TDODThread.sleep(1000);
				selectnopjdatadelete(pagesize, currentpage,totalCount, adddays, dbtimeString);
			}
		}
	}
	
	/**
	 * 
	 *  ｛批量更新平均值｝
	 *  @param pagesize
	 *  @param currentpage
	 *  @param totalCount
	 *  @param adddays
	 *  @param dbtimeString
	 *  @param callorderCount
	 *  @param keyid
	 *  @param errorMap
	 *  @author Liuxingwen
	 *  @created 2016年8月29日 上午11:51:32
	 *  @lastModified       
	 *  @history
	 */
	private void selectmsaveList(int pagesize,
			int currentpage, int totalCount, String monthStr, String dbtimeString,int callorderCount,int keyid,Map<String, Object> errorMap) {
		int totalPageCount=0;
		try {
			//TODO errorMap是错误记录
			pagesize=10000;
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("monthValue", monthStr);
			totalCount = iMerEvalStatiService.selectmsaveListcount(paraMap);
			if (totalCount <= 0)
			{
				sbf = new StringBuffer();
				sbf.append("datetime:" + dbtimeString);
				sbf.append("\r\n");
				sbf.append("更新记录：0 ,平价月汇总表没有需要平均统计的数据");
				sbf.append("\r\n");
				printLogs(true,sbf.toString());
				return;
			}
			 currentpage = 1;
			totalPageCount = totalCount % pagesize == 0 ? totalCount / pagesize : totalCount / pagesize + 1;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("monthValue", monthStr);
			params.put("pagesize", pagesize);
			params.put("datetime", dbtimeString);
			long starTime = System.currentTimeMillis();
//			params.put("starTime", System.currentTimeMillis());
			int startlie = 0;
			startlie = (currentpage - 1) * pagesize;
			params.put("startli", startlie);
			params.put("totalCount", totalCount);
			
			params.put("dbtimeString", dbtimeString);
			params.put("starTime", starTime);
			params.put("totalCount", totalCount);
			
			iMerEvalStatiService.updatemsaveList2(params);
//			List<Map<String, Object>> listDatas = iMerEvalStatiService.selectmsaveList(params);
//			if (null != listDatas && listDatas.size() > 0) {
//				// 更新平均值
//				Map<String, Object> paramsadd = new HashMap<String, Object>();
//				paramsadd.put("dbtimeString", dbtimeString);
//				paramsadd.put("starTime", starTime);
//				paramsadd.put("totalCount", totalCount);
//				iMerEvalStatiService.updatemsaveList(listDatas,paramsadd);
//			}
			// TDODThread.sleep(1000);
			if (currentpage < totalPageCount) {
//				currentpage++;
				callorderCount++;//调用次数，以便作为事件出错时用。
				selectmsaveList(pagesize, currentpage, totalCount, monthStr, dbtimeString,callorderCount,keyid,errorMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Map<String, Object> paraMaperrorlog = new HashMap<String, Object>();
				//TODO keyid
				paraMaperrorlog.put("keyid", keyid);
				//TODO statis_time
				paraMaperrorlog.put("statis_time", dbtimeString);
				//TODO keypagecount
				paraMaperrorlog.put("keypagecount", callorderCount);
				iMerEvalStatiService.addnewmerchant_evaluation_statis_errorlog(paraMaperrorlog);
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			sbf = new StringBuffer();
			sbf.append("datetime:" + dbtimeString);
			sbf.append("\r\n");
			sbf.append("更新记录：0 ,平价月汇总表没有需要平均统计的数据"+e.getMessage());
			sbf.append("\r\n");
			printLogs(true,sbf.toString());
			if (currentpage < totalPageCount) {
//				currentpage++;
//				callorderCount++;//调用次数，以便作为事件出错时用。
				selectmsaveList(pagesize, currentpage, totalCount, monthStr, dbtimeString,callorderCount,keyid,errorMap);
			}
			
		}
	}
	
	/**
	 * 
	 * ｛按天统计，统计当天数据，汇总表里有的商户信息，则更新汇总表里评价次数等信息分批处理 ｝
	 * 
	 * @param pagesize
	 * @param currentpage
	 * @param totalCount
	 * @param adddays
	 * @param dbtimeString
	 * @author Liuxingwen
	 * @created 2016年8月24日 上午9:53:30
	 * @lastModified
	 * @history
	 */
	private void selectMerchantsEvaluationStatisByDayList(int pagesize,
			int currentpage, int totalCount, String adddays, String dbtimeString,int callorderCount,int keyid,Map<String, Object> errorMap) {
		int totalPageCount=0;
		try {
			//TODO errorMap是错误记录
			pagesize=1000;
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("adddays", adddays);
			totalCount = iMerEvalStatiService.selectMerchantsEvaluationStatisByDayListCount(paraMap);
			if (totalCount <= 0)
			{
				sbf = new StringBuffer();
				sbf.append("datetime:" + dbtimeString);
				sbf.append("\r\n");
				sbf.append("更新记录：0 ,按天统计，更新汇总表评价数");
				sbf.append("\r\n");
				printLogs(true,sbf.toString());
				return;
			}
			 currentpage = 1;
			totalPageCount = totalCount % pagesize == 0 ? totalCount / pagesize : totalCount / pagesize + 1;
			//TODO 上次跑批出现错误的批号
			int errorOrderCount=0;
			if(null !=errorMap)
			{
				errorOrderCount=Integer.valueOf(String.valueOf(errorMap.get("keypagecount")).trim());
			}
			if(errorOrderCount!=0)
			{
				if(callorderCount>=errorOrderCount)
				{
					//开始跑
					long starTime = System.currentTimeMillis();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("adddays", adddays);
					params.put("pagesize", pagesize);
					params.put("datetime", dbtimeString);
//					params.put("starTime", System.currentTimeMillis());
					int startlie = 0;
					startlie = (currentpage - 1) * pagesize;
					params.put("startli", startlie);
//					params.put("totalCount", totalCount);
					List<Map<String, Object>> listDatas = iMerEvalStatiService.selectMerchantsEvaluationStatisByDayList(params);
					if (null != listDatas && listDatas.size() > 0) {
						// 按天统计，统计当天数据，汇总表里有的商户信息，则更新汇总表里评价次数等信息 分批处理
						Map<String, Object> paramsadd = new HashMap<String, Object>();
						paramsadd.put("dbtimeString", dbtimeString);
						paramsadd.put("starTime", starTime);
						paramsadd.put("totalCount", totalCount);
						iMerEvalStatiService.updateMerchantsEvaluationStatisByDayList(listDatas,paramsadd);
					}
				}
			}
			else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("adddays", adddays);
				params.put("pagesize", pagesize);
				params.put("datetime", dbtimeString);
				long starTime = System.currentTimeMillis();
//				params.put("starTime", System.currentTimeMillis());
				int startlie = 0;
				startlie = (currentpage - 1) * pagesize;
				params.put("startli", startlie);
				params.put("dbtimeString", dbtimeString);
				params.put("starTime", starTime);
				params.put("totalCount", totalCount);
				
				List<Map<String, Object>> listDatas = iMerEvalStatiService.selectMerchantsEvaluationStatisByDayList(params);
				if (null != listDatas && listDatas.size() > 0) {
					// 按天统计，统计当天数据，汇总表里有的商户信息，则更新汇总表里评价次数等信息 分批处理
					Map<String, Object> paramsadd = new HashMap<String, Object>();
					paramsadd.put("dbtimeString", dbtimeString);
					paramsadd.put("starTime", starTime);
					paramsadd.put("totalCount", totalCount);
					iMerEvalStatiService.updateMerchantsEvaluationStatisByDayList(listDatas,paramsadd);
				}
			}
			// TDODThread.sleep(1000);
			if (currentpage < totalPageCount) {
				currentpage++;
				callorderCount++;//调用次数，以便作为事件出错时用。
				selectMerchantsEvaluationStatisByDayList(pagesize, currentpage, totalCount, adddays, dbtimeString,callorderCount,keyid,errorMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Map<String, Object> paraMaperrorlog = new HashMap<String, Object>();
				//TODO keyid
				paraMaperrorlog.put("keyid", keyid);
				//TODO statis_time
				paraMaperrorlog.put("statis_time", dbtimeString);
				//TODO keypagecount
				paraMaperrorlog.put("keypagecount", callorderCount);
				iMerEvalStatiService.addnewmerchant_evaluation_statis_errorlog(paraMaperrorlog);
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			
			sbf = new StringBuffer();
			sbf.append("datetime:" + dbtimeString);
			sbf.append("\r\n");
			sbf.append("更新记录：0 ,按天统计，更新汇总表评价数"+e.getMessage());
			sbf.append("\r\n");
			printLogs(true,sbf.toString());
			if (currentpage < totalPageCount) {
				currentpage++;
//				callorderCount++;//调用次数，以便作为事件出错时用。
				selectMerchantsEvaluationStatisByDayList(pagesize, currentpage, totalCount, adddays, dbtimeString,callorderCount,keyid,errorMap);
			}
		}
	}
	/**
	 * 
	 *  ｛获取上次跑批出现的错误记录，这个记录不是一定要存在｝
	 *  @param keyid
	 *  @param dateString
	 *  @return 获取上次跑批出现的错误记录
	 *  @author Liuxingwen
	 *  @created 2016年8月25日 上午9:44:51
	 *  @lastModified       
	 *  @history
	 */
	@SuppressWarnings("unused")
	private Map<String, Object> getErrorLog(int keyid, String dateString) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("keyid", keyid);
			params.put("statis_time", dateString);
			List<Map<String, Object>> lists;
			lists = iMerEvalStatiService.selectmerchant_evaluation_statis_errorlog(params);
			if (null == lists || lists.size() <= 0)
				return null;
			iMerEvalStatiService.deletemerchant_evaluation_statis_errorlog(lists.get(0));
			return lists.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unused")
	private void addMerchantsEvaluationStatisByDayList(int pagesize,
			int currentpage, int totalCount, String adddays, String dbtimeString,int callorderCount,int keyid,Map<String, Object> errorMap) {
		int totalPageCount=0;
		try {
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("adddays", adddays);
			totalCount = iMerEvalStatiService.addMerchantsEvaluationStatisByDayListCount(paraMap);
			if (totalCount <= 0)
			{
				sbf = new StringBuffer();
				sbf.append("datetime:" + dbtimeString);
				sbf.append("\r\n");
				sbf.append("新增记录：0 ,按天统计，写入汇总表");
				sbf.append("\r\n");
				printLogs(true,sbf.toString());
				return;
			}
			//TODO 上次跑批出现错误的批号
			int errorOrderCount=0;
			if(null !=errorMap)
			{
				errorOrderCount=Integer.valueOf(String.valueOf(errorMap.get("keypagecount")).trim());
			}
			currentpage = 1;
			totalPageCount = totalCount % pagesize == 0 ? totalCount / pagesize : totalCount / pagesize + 1;
			if(errorOrderCount!=0)
			{
				if(callorderCount>=errorOrderCount)
				{
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("adddays", adddays);
					params.put("pagesize", pagesize);
					params.put("datetime", dbtimeString);
					params.put("starTime", System.currentTimeMillis());
					int startlie = 0;
					startlie = (currentpage - 1) * pagesize;
					params.put("startli", startlie);
					params.put("totalCount", totalCount);
					
					int add = iMerEvalStatiService.addMerchantsEvaluationStatisByDayList(params);					
				}
			}
			else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("adddays", adddays);
				params.put("pagesize", pagesize);
				params.put("datetime", dbtimeString);
				int startlie = 0;
				startlie = (currentpage - 1) * pagesize;
				params.put("startli", startlie);
				params.put("starTime", System.currentTimeMillis());
				params.put("totalCount", totalCount);
				int add = iMerEvalStatiService.addMerchantsEvaluationStatisByDayList(params);
			}
			if (currentpage < totalPageCount) {
				// TDODThread.sleep(1000);
//				currentpage++;
				callorderCount++;//调用次数，以便作为事件出错时用。
				addMerchantsEvaluationStatisByDayList(pagesize, currentpage, totalCount, adddays, dbtimeString,callorderCount,keyid,errorMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				
				
				Map<String, Object> paraMaperrorlog = new HashMap<String, Object>();
				//TODO keyid
				paraMaperrorlog.put("keyid", keyid);
				//TODO statis_time
				paraMaperrorlog.put("statis_time", dbtimeString);
				//TODO keypagecount
				paraMaperrorlog.put("keypagecount", callorderCount);
				iMerEvalStatiService.addnewmerchant_evaluation_statis_errorlog(paraMaperrorlog);
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			sbf = new StringBuffer();
			sbf.append("datetime:" + dbtimeString);
			sbf.append("\r\n");
			sbf.append("新增记录：0 ,按天统计，写入汇总表");
			sbf.append(e.getMessage());
			sbf.append("\r\n");
			printLogs(true,sbf.toString());
			if (currentpage < totalPageCount) {
				// TDODThread.sleep(1000);
//				currentpage++;
//				callorderCount++;//调用次数，以便作为事件出错时用。
				addMerchantsEvaluationStatisByDayList(pagesize, currentpage, totalCount, adddays, dbtimeString,callorderCount,keyid,errorMap);
			}
			
		}
	}

	@SuppressWarnings("unused")
	private void addMerchantEvaluationStatisByDayList(int pagesize,
			int currentpage, int totalCount, String adddays, String dbtimeString) {
		int totalPageCount=0;
		try {
			long starTime = System.currentTimeMillis();
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("adddays", adddays);
			totalCount = iMerEvalStatiService.addMerchantEvaluationStatisByDayListCount(paraMap);
			if (totalCount <= 0)
			{
				StringBuffer sbf = new StringBuffer();
				sbf.append("datetime:" + dbtimeString);
				sbf.append("\r\n");
				sbf.append("新增记录：0 ,按天统计，统计评价信息写入按天统计表");
				sbf.append("\r\n");
				printLogs(true,sbf.toString());
				return;
			}
			currentpage = 1;
			totalPageCount = totalCount % pagesize == 0 ? totalCount / pagesize : totalCount / pagesize + 1;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("adddays", adddays);
			params.put("pagesize", pagesize);
			params.put("datetime", dbtimeString);
			params.put("starTime", starTime);
			int startlie = 0;
			startlie = (currentpage - 1) * pagesize;
			params.put("startli", startlie);
			params.put("totalCount", totalCount);
			int add = iMerEvalStatiService.addMerchantEvaluationStatisByDayList(params);
			if (currentpage < totalPageCount) {
				currentpage++;
//				callorderCount++;//调用次数，以便作为事件出错时用。
				// TDODThread.sleep(1000);
				addMerchantEvaluationStatisByDayList(pagesize, currentpage,totalCount, adddays, dbtimeString);
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer sbf = new StringBuffer();
			sbf.append("datetime:" + dbtimeString);
			sbf.append("\r\n");
			sbf.append("新增记录：0 ,按天统计，统计评价信息写入按天统计表");
			sbf.append(e.getMessage());
			sbf.append("\r\n");
			printLogs(true,sbf.toString());
			if (currentpage < totalPageCount) {
				currentpage++;
//				callorderCount++;//调用次数，以便作为事件出错时用。
				// TDODThread.sleep(1000);
				addMerchantEvaluationStatisByDayList(pagesize, currentpage,totalCount, adddays, dbtimeString);
			}
		}
	}

	@SuppressWarnings("unused")
	private void selectIntoMerchantByOrderNoStatisList( int pagesize,
			int currentpage, int totalCount, String beginTime, String endTime,String monthValue) {
		int totalPageCount=0;
		try {
			totalPageCount = totalCount % pagesize == 0 ? totalCount / pagesize : totalCount / pagesize + 1;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("pagesize", pagesize);
			int startlie = 0;
			startlie = (currentpage - 1) * pagesize;
			params.put("startli", startlie);
			params.put("beginTime", beginTime);
			params.put("starTime", System.currentTimeMillis());
			params.put("totalCount", totalCount-currentpage * pagesize);
		
			params.put("endTime", endTime);
			params.put("monthValue", monthValue);
			int add = iMerEvalStatiService.selectIntoMerchantByOrderNoStatisList(params);
			// if (null != evaluationList2 && evaluationList2.size() > 0) {
			// merChantEvaluation.addAll(evaluationList2);
			// }
			// TDODThread.sleep(1000);
			if (currentpage < totalPageCount) {
				currentpage++;
				selectIntoMerchantByOrderNoStatisList(pagesize, currentpage, totalCount, beginTime,endTime,monthValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
			sbf = new StringBuffer();
			sbf.append("开始时间=" + beginTime);
			sbf.append(",结束时间=" + endTime); 
			sbf.append("\r\n");
			sbf.append("新增记录：0 ,月初1号开始统计,没有评价的商户以5分写入汇总表");
			sbf.append(e.getMessage());
			sbf.append("\r\n");
			printLogs(true,sbf.toString());
			if (currentpage < totalPageCount) {
				currentpage++;
				selectIntoMerchantByOrderNoStatisList(pagesize, currentpage, totalCount, beginTime,endTime,monthValue);
			}
		}
	}
	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date, SimpleDateFormat format) {

		// SimpleDateFormat format = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// format = new SimpleDateFormat("dd");
		String str = format.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * ｛日志处理｝
	 * 
	 * @param isprint
	 * @param strContent
	 * @author Liuxingwen
	 * @created 2016年8月19日 下午4:20:25
	 * @lastModified
	 * @history
	 */
	private void printLogs(Boolean isprint, String strContent) {
		try {
			iMerEvalStatiService.printLogs(isprint, strContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * ｛格式系统日期｝
	 * 
	 * @return
	 * @author Liuxingwen
	 * @created 2016年8月19日 上午9:43:53
	 * @lastModified
	 * @history
	 */
	private String getFormatTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new java.util.Date());
	}
	//
	// // 更新缓存
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// private void updateCasheDatas() throws Exception {
	// long starTime = System.currentTimeMillis();
	// addSbf(true, "updateCasheDatas设置缓存开始");
	// String statismonth = getdbDateTime(1);// 上个月的日期201601
	// Map params = new HashMap<String, Object>();
	// params.put("statismonth", statismonth);
	// int totalCount = 0;
	// addSbf(true, "getMerChantEvaluationStatisList设置缓存读取商户统计数据开始");
	// totalCount = iMerchantEvaluationStatisService
	// .getMerChantEvaluationStatisCount(params);
	// if (totalCount == 0) {
	// commonCacheService.delObjectContainsKey(
	// CacheConstants.MERCHANT_EVALUATION_STATIS, true);
	// return;
	// }
	// int currentpage = 1;
	// List<Map<String, Object>> MerChantEvaluation = new ArrayList<Map<String,
	// Object>>();
	// getMerChantEvaluationStatisList(MerChantEvaluation, pageSize,
	// currentpage, totalCount, statismonth);
	// addSbf(true, "getMerChantEvaluationStatisList设置缓存读取商户统计数据结束,耗时："
	// + ((System.currentTimeMillis() - starTime) / 1000) + "秒");
	// if (null == MerChantEvaluation || MerChantEvaluation.size() <= 0) {
	// commonCacheService.delObjectContainsKey(
	// CacheConstants.MERCHANT_EVALUATION_STATIS, true);
	// return;
	// }
	// String merchant_id = "";
	// String statismonth1 = "";
	// Object object = null;
	// long starTime1 = System.currentTimeMillis();
	// addSbf(true, "commonCacheService.delObjectContainsKey清除缓存开始");
	// commonCacheService.delObjectContainsKey(
	// CacheConstants.MERCHANT_EVALUATION_STATIS, true);
	// addSbf(true, "commonCacheService.delObjectContainsKey清除缓存结束,耗时："
	// + ((System.currentTimeMillis() - starTime1) / 1000) + "秒");
	// long starTime2 = System.currentTimeMillis();
	// addSbf(true, "commonCacheService.setObject添加缓存开始");
	// for (Map<String, Object> map : MerChantEvaluation) {
	//
	// try {
	//
	// merchant_id = map.get("merchant_id") == null ? "" : String
	// .valueOf(map.get("merchant_id")).trim();
	// statismonth1 = map.get("statismonth") == null ? "" : String
	// .valueOf(map.get("statismonth")).trim();
	// addSbf(false, "设置缓存" + statismonth1 + merchant_id);
	// if (null == merchant_id || "".equals(merchant_id)
	// || null == statismonth1 || "".equals(statismonth1)) {
	// continue;
	// }
	// object = commonCacheService.getObject(
	// CacheConstants.MERCHANT_EVALUATION_STATIS, merchant_id,
	// statismonth1);
	// if (object == null) {
	// commonCacheService.setObject(map,
	// CacheConstants.MERCHANT_EVALUATION_STATIS,
	// merchant_id, statismonth1);
	// }
	//
	// } catch (Exception e) {
	// } finally {
	// }
	// }
	// addSbf(true,
	// "commonCacheService.setObject添加缓存结束,耗时："
	// + ((System.currentTimeMillis() - starTime2) / 1000)
	// + "秒");
	// addSbf(true,
	// "updateCasheDatas设置缓存结束,耗时："
	// + ((System.currentTimeMillis() - starTime) / 1000)
	// + "秒");
	// }
	//
	// /**
	// * ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	// *
	// * @param merChantEvaluation
	// * @param pageSize2
	// * @param currentpage
	// * @param totalCount
	// * @author Liuxingwen
	// * @created 2016年8月18日 下午3:22:37
	// * @lastModified
	// * @history
	// */
	// private void getMerChantEvaluationStatisList(
	// List<Map<String, Object>> merChantEvaluation, int pagesize,
	// int currentpage, int totalCount, String statismonth) {
	// try {
	// int totalPageCount = totalCount % pagesize == 0 ? totalCount
	// / pagesize : totalCount / pagesize + 1;
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("statismonth", statismonth);
	// params.put("pagesize", pagesize);
	// int startlie = 0;
	// startlie = (currentpage - 1) * pagesize;
	// params.put("startli", startlie);
	//
	// List<Map<String, Object>> evaluationList2 =
	// iMerchantEvaluationStatisService
	// .getMerChantEvaluationStatis(params);
	// if (null != evaluationList2 && evaluationList2.size() > 0) {
	// merChantEvaluation.addAll(evaluationList2);
	// }
	// if (currentpage < totalPageCount) {
	// currentpage++;
	// getMerChantEvaluationStatisList(merChantEvaluation, pagesize,
	// currentpage, totalCount, statismonth);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// // 获取商户评价数据
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// private List<Map<String, Object>> getEvaluationDataLists(int pageSize,
	// int currentpage) throws Exception
	//
	// {
	// addSbf(true, "getEvaluationDataLists获取商户评价数据开始");
	// long starTime = System.currentTimeMillis();
	// Map params = new HashMap<String, Object>();
	// int totalCount = 0;
	// totalCount = iMerchantEvaluationStatisService
	// .getEvaluationByMonthCount(params);
	// if (totalCount == 0) {
	// return null;
	// }
	// currentpage = 1;
	// List<Map<String, Object>> evaluationList = new ArrayList<Map<String,
	// Object>>();
	// getEvaluationDataList(evaluationList, pageSize, currentpage, totalCount);
	// addSbf(true,
	// "getEvaluationDataLists获取商户评价数据结束,耗时："
	// + ((System.currentTimeMillis() - starTime) / 1000)
	// + "秒");
	// return evaluationList;
	// }
	//
	// // 获取订单商户数据
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// private List<Map<String, Object>> getMerchanDataLists(int pageSize,
	// int currentpage) throws Exception {
	// long starTime = System.currentTimeMillis();
	// addSbf(true, "getMerchanDataLists获取订单商户数据开始");
	// // 获取评价统计商户(已添加的数据，与缓存同步)
	// Map params = new HashMap<String, Object>();
	// params.put("orderstatus", 5);
	// int totalCount = 0;
	// totalCount = iMerchantEvaluationStatisService
	// .getMerchantByOrderCount(params);
	// if (totalCount == 0) {
	// return null;
	// }
	// /**
	// * 商户列表
	// */
	// List<Map<String, Object>> merchantList = new ArrayList<Map<String,
	// Object>>();
	// getMerchanDataList(merchantList, pageSize, currentpage, totalCount);
	// addSbf(true,
	// "getMerchanDataLists获取订单商户数据结束,耗时："
	// + ((System.currentTimeMillis() - starTime) / 1000)
	// + "秒");
	// return merchantList;
	// }
	//
	// /**
	// * ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	// *
	// * @param evaluationList
	// * @param pageSize
	// * @param currentpage
	// * @param totalCount
	// * @author Liuxingwen
	// * @created 2016年8月18日 上午11:30:46
	// * @lastModified
	// * @history
	// */
	// private void getEvaluationDataList(
	// List<Map<String, Object>> evaluationList, int pagesize,
	// int currentpage, int totalCount) {
	// try {
	// int totalPageCount = totalCount % pagesize == 0 ? totalCount
	// / pagesize : totalCount / pagesize + 1;
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("orderstatus", 5);
	// params.put("pagesize", pagesize);
	// int startlie = 0;
	// startlie = (currentpage - 1) * pagesize;
	// params.put("startli", startlie);
	//
	// List<Map<String, Object>> evaluationList2 =
	// iMerchantEvaluationStatisService
	// .getEvaluationByMonth(params);
	// if (null != evaluationList2 && evaluationList2.size() > 0) {
	// evaluationList.addAll(evaluationList2);
	// }
	// if (currentpage < totalPageCount) {
	// currentpage++;
	// getEvaluationDataList(evaluationList, pagesize, currentpage,
	// totalCount);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private void getMerchanDataList(List<Map<String, Object>> merchantList,
	// int pagesize, int currentpage, int totalCount) {
	// try {
	// int totalPageCount = totalCount % pagesize == 0 ? totalCount
	// / pagesize : totalCount / pagesize + 1;
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("orderstatus", 5);
	// params.put("pagesize", pagesize);
	// int startlie = 0;
	// startlie = (currentpage - 1) * pagesize;
	// params.put("startli", startlie);
	//
	// List<Map<String, Object>> merchantList2 =
	// iMerchantEvaluationStatisService
	// .getMerchantByOrder(params);
	// if (null != merchantList2 && merchantList2.size() > 0) {
	// merchantList.addAll(merchantList2);
	// }
	// if (currentpage < totalPageCount) {
	// currentpage++;
	// getMerchanDataList(merchantList, pagesize, currentpage,
	// totalCount);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// *
	// * ｛批量修改统计数据｝
	// *
	// * @param updateList
	// * @throws Exception
	// * @author Liuxingwen
	// * @created 2016年8月18日 上午9:01:55
	// * @lastModified
	// * @history
	// */
	// private void updateListData(List<Map<String, Object>> updateList)
	// throws Exception {
	// addSbf(true, "updateListData批量更新开始");
	// long starTime = System.currentTimeMillis();
	// if (null != updateList && updateList.size() > 0) {
	// addSbf(true, "updateListData批量更新开始" + updateList.size());
	// for (Map<String, Object> map : updateList) {
	// updateOne(map);
	// try {
	// Thread.sleep(10);
	// } catch (Exception e) {
	// }
	// }
	// }
	// addSbf(true, "updateListData批量更新结束,耗时："
	// + ((System.currentTimeMillis() - starTime) / 1000) + "秒");
	// }
	//
	// /**
	// *
	// * ｛批量修改统计数据｝
	// *
	// * @param map
	// * @throws Exception
	// * @author Liuxingwen
	// * @created 2016年8月18日 上午9:02:21
	// * @lastModified
	// * @history
	// */
	// private void updateOne(Map<String, Object> map) throws Exception {
	// try {
	// addSbf(false, map.get("merchant_id") + "更新统计商户：");
	// int result = iMerchantEvaluationStatisService
	// .updateMerchantEvaluationStatisOne(map);
	// // 若没有更新记录，则尝试添加记录
	// if (result == 0) {
	// addNewOne(map);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 新增 ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	// *
	// * @param addList
	// * @throws Exception
	// * @author Liuxingwen
	// * @created 2016年8月17日 下午7:47:33
	// * @lastModified
	// * @history
	// */
	// private void addNewListData(List<Map<String, Object>> addList)
	// throws Exception {
	// addSbf(true, "addNewListData批量添加开始");
	// long starTime = System.currentTimeMillis();
	// if (null != addList && addList.size() > 0) {
	// addSbf(true, "addNewListData批量添加数据：" + addList.size());
	// for (Map<String, Object> map : addList) {
	// addNewOne(map);
	// try {
	// Thread.sleep(10);
	// } catch (Exception e) {
	// }
	// }
	// }
	// addSbf(true, "addNewListData批量添加结束,耗时："
	// + ((System.currentTimeMillis() - starTime) / 1000) + "秒");
	// }
	//
	// /**
	// * ｛新增单个记录｝
	// *
	// * @param map
	// * @throws Exception
	// * @author Liuxingwen
	// * @created 2016年8月17日 下午7:47:44
	// * @lastModified
	// * @history
	// */
	// private void addNewOne(Map<String, Object> map) throws Exception {
	// String merchant_id = map.get("merchant_id") == null ? "" : String
	// .valueOf(map.get("merchant_id")).trim();
	// String statismonth = map.get("statismonth") == null ? "" : String
	// .valueOf(map.get("statismonth")).trim();
	// try {
	// addSbf(false, map.get("merchant_id") + "添加新统计商户：");
	// int result = iMerchantEvaluationStatisService
	// .addMerchantEvaluationStatisOne(map);
	// if (result > 0) {
	// // 添加缓存
	// commonCacheService.setObject(map,
	// CacheConstants.MERCHANT_EVALUATION_STATIS, merchant_id,
	// statismonth);
	// }
	// } catch (Exception e) {
	// if (e.getMessage().toString().indexOf("for key 'Index_mid_mon'") >= 0) {
	// // 更新
	// updateOne(map);
	// // 添加缓存
	// commonCacheService.setObject(map,
	// CacheConstants.MERCHANT_EVALUATION_STATIS, merchant_id,
	// statismonth);
	// }
	// }
	// }
	//
	// /**
	// *
	// * ｛从数据库获取系统时间（为了时间统一）｝
	// *
	// * @param mondiff
	// * @return
	// * @author Liuxingwen
	// * @created 2016年8月19日 上午9:37:57
	// * @lastModified
	// * @history
	// */
	// private String getdbDateTime(int mondiff) {
	// try {
	//
	// Map<String, Object> maparMap = new HashMap<String, Object>();
	// maparMap.put("mondiff", mondiff);// 1：上个月的日期
	// String currentDateString = iMerchantEvaluationStatisService
	// .getdbDateTime(maparMap);// format:201608
	// return currentDateString;
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// }
	//
	// /**
	// *
	// * ｛评价商户数据处理｝
	// *
	// * @param merchantList
	// * @param evaluationList
	// * @param updateList
	// * @param addList
	// * @author Liuxingwen
	// * @created 2016年8月17日 上午11:47:36
	// * @lastModified
	// * @history
	// */
	// private void disposalDataMerchantByOrder(
	// List<Map<String, Object>> merchantList,
	// List<Map<String, Object>> evaluationList,
	// List<Map<String, Object>> updateList,
	// List<Map<String, Object>> addList) {
	// addSbf(true, "disposalDataMerchantByOrder评价商户数据处理");
	// long starTime = System.currentTimeMillis();
	// if (null == merchantList || merchantList.size() == 0) {
	// return;
	// }
	// Object objectCash = null;
	// String merchant_id = "";
	// String statismonth = "";
	// Map<String, Object> ObjMap = null;
	// for (Map<String, Object> map : merchantList) {
	// merchant_id = map.get("merchant_id") == null ? "" : String.valueOf(
	// map.get("merchant_id")).trim();
	// statismonth = map.get("statismonth") == null ? "" : String.valueOf(
	// map.get("statismonth")).trim();
	// if ("".equals(merchant_id)) {
	// continue;
	// }
	//
	// addSbf(false, map.get("merchant_id") + "评价商户信息处理 ：");
	// // 评价表里有信息
	// ObjMap = getObjMap(merchant_id, evaluationList);
	// if (ObjMap == null) {
	// ObjMap = map;
	// ObjMap.put("total_attitude_evaluation", 0);
	// ObjMap.put("total_quality_evaluation", 0);
	// ObjMap.put("total_speed_evaluation", 0);
	// ObjMap.put("avg_evaluation", 5);
	// ObjMap.put("pingjia", 0);// 未评价
	// } else {
	// ObjMap.put("pingjia", 1);// 已评价
	// }
	// objectCash = commonCacheService.getObject(
	// CacheConstants.MERCHANT_EVALUATION_STATIS, merchant_id,
	// statismonth);
	// if (objectCash != null) {
	// // 统计过的商户信息添加到缓存中，则执行更新操作
	// // 如果没有评价ObjMap.put("pingjia", 0);// 未评价，则不需要更新，为了节约数据库连接
	// if (Integer.valueOf(String.valueOf(ObjMap.get("pingjia"))
	// .trim()) == 1) {
	// updateList.add(ObjMap);
	// }
	// } else {
	// // 新增
	// addList.add(ObjMap);
	// }
	// }
	// addSbf(true,
	// "disposalDataMerchantByOrder评价商户数据处理,耗时："
	// + ((System.currentTimeMillis() - starTime) / 1000)
	// + "秒");
	// }
	//
	// /**
	// *
	// * ｛订单商户与评价商户比较｝
	// *
	// * @param merchant_id
	// * @param evaluationList
	// * @return
	// * @author Liuxingwen
	// * @created 2016年8月17日 下午6:38:52
	// * @lastModified
	// * @history
	// */
	// private Map<String, Object> getObjMap(String merchant_id,
	// List<Map<String, Object>> evaluationList) {
	// Map<String, Object> ObjMap = null;
	// if (null == merchant_id || "".equals(merchant_id.trim()))
	// return null;
	// if (null == evaluationList || evaluationList.size() == 0) {
	// return null;
	// }
	// String merchant_id2 = "";
	// for (Map<String, Object> emap : evaluationList) {
	// merchant_id2 = emap.get("merchant_id") == null ? "" : String
	// .valueOf(emap.get("merchant_id")).trim();
	// if (null == merchant_id2 || "".equals(merchant_id2))
	// continue;
	// if (merchant_id.trim().equals(merchant_id2.trim())) {
	// ObjMap = emap;
	// break;
	// }
	// }
	// return ObjMap;
	// }
}
