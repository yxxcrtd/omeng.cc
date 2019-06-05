///**	
// * <br>
// * Copyright 2014 om.All rights reserved.<br>
// * <br>			 
// * Package: com.shanjin.service.impl <br>
// * FileName: MerchantEvaluationStatisServiceImpl.java <br>
// * <br>
// * @version
// * @author Liuxingwen
// * @created 2016年8月16日
// * @last Modified 
// * @history
// */
//package com.shanjin.service.impl;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Resource;
//
//import org.apache.log4j.Logger;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.shanjin.cache.CacheConstants;
//import com.shanjin.cache.service.ICommonCacheService;
//import com.shanjin.cache.service.IUserRelatedCacheServices;
//import com.shanjin.cache.service.impl.JedisLock;
////import com.shanjin.common.quartz.EvaluationOrder;
//import com.shanjin.dao.IMerchantEvaluationStatisDao;
//import com.shanjin.service.IMerchantEvaluationStatisService;
//
///**
// * {该处说明该构造函数的含义及作用}
// * 
// * @author Liuxingwen
// * @created 2016年8月16日 下午6:17:02
// * @lastModified
// * @history
// */
//@Service("iMerchantEvaluationStatisService")
//public class MerchantEvaluationStatisServiceImpl implements
//		IMerchantEvaluationStatisService {
//	int pageSize = 1000;
//	// 本地异常日志记录对象
//	private static final Logger logger = Logger
//			.getLogger(MerchantEvaluationStatisServiceImpl.class);
//	@Resource
//	private IMerchantEvaluationStatisDao iMerchantEvaluationStatisDao;
//	@Resource
//	private IUserRelatedCacheServices userRelatedCacheServices;
//	@Resource
//	private ICommonCacheService commonCacheService;
//	private StringBuffer sbf = new StringBuffer();
//	private StringBuffer sbflog = new StringBuffer();
//
//	/**
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月20日 下午4:23:50
//	 * @lastModified
//	 * @history
//	 */
//	@Override
////	@Transactional(rollbackFor = Exception.class)
//	public void getMerchantByOrderStart(Map<String, Object> params) throws Exception {
//		// this.sbflog = sbflog;
//		// this.sbf = sbf;
//		sbf = new StringBuffer();
//		sbflog = new StringBuffer();
//
//		JedisLock schduleLock = null;
//		try {
//			schduleLock = userRelatedCacheServices.getEvaluationOrderLock();
//			if (schduleLock.acquire()) {
//
//				// 更新缓存
//				 updateCasheDatas();
//				// 订单商户列表
//				List<Map<String, Object>> merchantList = getMerchanDataLists(
//						pageSize, 1);
//				// 评价商户列表
//				List<Map<String, Object>> evaluationList = getEvaluationDataLists(
//						pageSize, 1);
//				// 新增列表
//				List<Map<String, Object>> addList = new ArrayList<Map<String, Object>>();
//				// 更新列表
//				List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
//				// 评价商户数据处理
//				disposalDataMerchantByOrder(merchantList, evaluationList,
//						updateList, addList);
//				// addSbf(true,"merchantList" + merchantList.size());
//				// addSbf(true,"evaluationList" + evaluationList.size());
//				// addSbf(true,"addList" + addList.size());
//				// 批量添加(测试未通过，待检查配置)
//				// iMerchantEvaluationStatisService
//				// .addMerchantEvaluationStatis(addList);
//				// 新增
//				addNewListData(addList);
//				// 批量修改(测试未通过，待检查配置)
//				// iMerchantEvaluationStatisService
//				// .updateMerchantEvaluationStatis(updateList);
//				// 修改
//				updateListData(updateList);
//
//			}
//
//		} catch (Exception e) {
//		} finally {
//			try {
//				if (null != schduleLock) {
//					userRelatedCacheServices.ReleaseOrderExireLock(schduleLock);
//				}
//			} catch (Exception e2) {
//			}
//
//		}
//	}
//
//	/**
//	 * 
//	 * ｛批量修改统计数据｝
//	 * 
//	 * @param updateList
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 上午9:01:55
//	 * @lastModified
//	 * @history
//	 */
//	private void updateListData(List<Map<String, Object>> updateList)
//			throws Exception {
//		addSbf(true, "updateListData批量更新开始");
//		long starTime = System.currentTimeMillis();
//		if (null != updateList && updateList.size() > 0) {
//			addSbf(true, "updateListData批量更新开始" + updateList.size());
//			for (Map<String, Object> map : updateList) {
//				updateOne(map);
//				try {
//					Thread.sleep(10);
//				} catch (Exception e) {
//				}
//			}
//		}
//		addSbf(true, "updateListData批量更新结束,耗时："
//				+ ((System.currentTimeMillis() - starTime) / 1000) + "秒");
//	}
//
//	/**
//	 * 
//	 * ｛批量修改统计数据｝
//	 * 
//	 * @param map
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 上午9:02:21
//	 * @lastModified
//	 * @history
//	 */
//	@Transactional(rollbackFor = Exception.class)
//	private void updateOne(Map<String, Object> map) throws Exception {
//		try {
//			addSbf(false, map.get("merchant_id") + "更新统计商户：");
//			int result = iMerchantEvaluationStatisDao
//					.updateMerchantEvaluationStatisOne(map);
//			// 若没有更新记录，则尝试添加记录
//			if (result == 0) {
//				addNewOne(map);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 新增 ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
//	 * 
//	 * @param addList
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午7:47:33
//	 * @lastModified
//	 * @history
//	 */
//	private void addNewListData(List<Map<String, Object>> addList)
//			throws Exception {
//		addSbf(true, "addNewListData批量添加开始");
//		long starTime = System.currentTimeMillis();
//		if (null != addList && addList.size() > 0) {
//			addSbf(true, "addNewListData批量添加数据：" + addList.size());
//			for (Map<String, Object> map : addList) {
//				addNewOne(map);
//				try {
//					Thread.sleep(10);
//				} catch (Exception e) {
//				}
//			}
//		}
//		addSbf(true, "addNewListData批量添加结束,耗时："
//				+ ((System.currentTimeMillis() - starTime) / 1000) + "秒");
//	}
//
//	/**
//	 * ｛新增单个记录｝
//	 * 
//	 * @param map
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午7:47:44
//	 * @lastModified
//	 * @history
//	 */
//	@Transactional(rollbackFor = Exception.class)
//	private void addNewOne(Map<String, Object> map) throws Exception {
//		String merchant_id = map.get("merchant_id") == null ? "" : String
//				.valueOf(map.get("merchant_id")).trim();
//		String statismonth = map.get("statismonth") == null ? "" : String
//				.valueOf(map.get("statismonth")).trim();
//		try {
//			addSbf(false, map.get("merchant_id") + "添加新统计商户：");
//			int result = iMerchantEvaluationStatisDao
//					.addMerchantEvaluationStatisOne(map);
//			if (result > 0) {
//				// 添加缓存
//				commonCacheService.setObject(map,
//						CacheConstants.MERCHANT_EVALUATION_STATIS, merchant_id,
//						statismonth);
//			}
//		} catch (Exception e) {
//			if (e.getMessage().toString().indexOf("for key 'Index_mid_mon'") >= 0) {
//				// 更新
//				updateOne(map);
//				// 添加缓存
//				commonCacheService.setObject(map,
//						CacheConstants.MERCHANT_EVALUATION_STATIS, merchant_id,
//						statismonth);
//			}
//		}
//	}
//
//	/**
//	 * 
//	 * ｛评价商户数据处理｝
//	 * 
//	 * @param merchantList
//	 * @param evaluationList
//	 * @param updateList
//	 * @param addList
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 上午11:47:36
//	 * @lastModified
//	 * @history
//	 */
//	private void disposalDataMerchantByOrder(
//			List<Map<String, Object>> merchantList,
//			List<Map<String, Object>> evaluationList,
//			List<Map<String, Object>> updateList,
//			List<Map<String, Object>> addList) {
//		addSbf(true, "disposalDataMerchantByOrder评价商户数据处理");
//		long starTime = System.currentTimeMillis();
//		if (null == merchantList || merchantList.size() == 0) {
//			return;
//		}
//		Object objectCash = null;
//		String merchant_id = "";
//		String statismonth = "";
//		Map<String, Object> ObjMap = null;
//		for (Map<String, Object> map : merchantList) {
//			merchant_id = map.get("merchant_id") == null ? "" : String.valueOf(
//					map.get("merchant_id")).trim();
//			statismonth = map.get("statismonth") == null ? "" : String.valueOf(
//					map.get("statismonth")).trim();
//			if ("".equals(merchant_id)) {
//				continue;
//			}
//
//			addSbf(false, map.get("merchant_id") + "评价商户信息处理 ：");
//			// 评价表里有信息
//			ObjMap = getObjMap(merchant_id, evaluationList);
//			if (ObjMap == null) {
//				ObjMap = map;
//				ObjMap.put("total_attitude_evaluation", 0);
//				ObjMap.put("total_quality_evaluation", 0);
//				ObjMap.put("total_speed_evaluation", 0);
//				ObjMap.put("avg_evaluation", 5);
//				ObjMap.put("pingjia", 0);// 未评价
//			} else {
//				ObjMap.put("pingjia", 1);// 已评价
//			}
//			objectCash = commonCacheService.getObject(
//					CacheConstants.MERCHANT_EVALUATION_STATIS, merchant_id,
//					statismonth);
//			if (objectCash != null) {
//				// 统计过的商户信息添加到缓存中，则执行更新操作
//				// 如果没有评价ObjMap.put("pingjia", 0);// 未评价，则不需要更新，为了节约数据库连接
//				if (Integer.valueOf(String.valueOf(ObjMap.get("pingjia"))
//						.trim()) == 1) {
//					updateList.add(ObjMap);
//				}
//			} else {
//				// 新增
//				addList.add(ObjMap);
//			}
//		}
//		addSbf(true,
//				"disposalDataMerchantByOrder评价商户数据处理,耗时："
//						+ ((System.currentTimeMillis() - starTime) / 1000)
//						+ "秒");
//	}
//
//	/**
//	 * 
//	 * ｛订单商户与评价商户比较｝
//	 * 
//	 * @param merchant_id
//	 * @param evaluationList
//	 * @return
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午6:38:52
//	 * @lastModified
//	 * @history
//	 */
//	private Map<String, Object> getObjMap(String merchant_id,
//			List<Map<String, Object>> evaluationList) {
//		Map<String, Object> ObjMap = null;
//		if (null == merchant_id || "".equals(merchant_id.trim()))
//			return null;
//		if (null == evaluationList || evaluationList.size() == 0) {
//			return null;
//		}
//		String merchant_id2 = "";
//		for (Map<String, Object> emap : evaluationList) {
//			merchant_id2 = emap.get("merchant_id") == null ? "" : String
//					.valueOf(emap.get("merchant_id")).trim();
//			if (null == merchant_id2 || "".equals(merchant_id2))
//				continue;
//			if (merchant_id.trim().equals(merchant_id2.trim())) {
//				ObjMap = emap;
//				break;
//			}
//		}
//		return ObjMap;
//	}
//
//	// 获取商户评价数据
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private List<Map<String, Object>> getEvaluationDataLists(int pageSize,
//			int currentpage) throws Exception
//
//	{
//		addSbf(true, "getEvaluationDataLists获取商户评价数据开始");
//		long starTime = System.currentTimeMillis();
//		Map params = new HashMap<String, Object>();
//		int totalCount = 0;
//		totalCount = iMerchantEvaluationStatisDao
//				.getEvaluationByMonthCount(params);
//		if (totalCount == 0) {
//			return null;
//		}
//		currentpage = 1;
//		List<Map<String, Object>> evaluationList = new ArrayList<Map<String, Object>>();
//		getEvaluationDataList(evaluationList, pageSize, currentpage, totalCount);
//		addSbf(true,
//				"getEvaluationDataLists获取商户评价数据结束,耗时："
//						+ ((System.currentTimeMillis() - starTime) / 1000)
//						+ "秒");
//		return evaluationList;
//	}
//
//	/**
//	 * ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
//	 * 
//	 * @param evaluationList
//	 * @param pageSize
//	 * @param currentpage
//	 * @param totalCount
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 上午11:30:46
//	 * @lastModified
//	 * @history
//	 */
//	private void getEvaluationDataList(
//			List<Map<String, Object>> evaluationList, int pagesize,
//			int currentpage, int totalCount) {
//		try {
//			int totalPageCount = totalCount % pagesize == 0 ? totalCount
//					/ pagesize : totalCount / pagesize + 1;
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("orderstatus", 5);
//			params.put("pagesize", pagesize);
//			int startlie = 0;
//			startlie = (currentpage - 1) * pagesize;
//			params.put("startli", startlie);
//
//			List<Map<String, Object>> evaluationList2 = iMerchantEvaluationStatisDao
//					.getEvaluationByMonth(params);
//			if (null != evaluationList2 && evaluationList2.size() > 0) {
//				evaluationList.addAll(evaluationList2);
//			}
//			if (currentpage < totalPageCount) {
//				currentpage++;
//				getEvaluationDataList(evaluationList, pagesize, currentpage,
//						totalCount);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	// 获取订单商户数据
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private List<Map<String, Object>> getMerchanDataLists(int pageSize,
//			int currentpage) throws Exception {
//		long starTime = System.currentTimeMillis();
//		addSbf(true, "getMerchanDataLists获取订单商户数据开始");
//		// 获取评价统计商户(已添加的数据，与缓存同步)
//		Map params = new HashMap<String, Object>();
//		params.put("orderstatus", 5);
//		int totalCount = 0;
//		totalCount = iMerchantEvaluationStatisDao
//				.getMerchantByOrderCount(params);
//		System.out.println("=====totalCount=====" + totalCount);
//		if (totalCount == 0) {
//			return null;
//		}
//		/**
//		 * 商户列表
//		 */
//		List<Map<String, Object>> merchantList = new ArrayList<Map<String, Object>>();
//		getMerchanDataList(merchantList, pageSize, currentpage, totalCount);
//		addSbf(true,
//				"getMerchanDataLists获取订单商户数据结束,耗时："
//						+ ((System.currentTimeMillis() - starTime) / 1000)
//						+ "秒");
//		return merchantList;
//	}
//
//	private void getMerchanDataList(List<Map<String, Object>> merchantList,
//			int pagesize, int currentpage, int totalCount) {
//		try {
//			int totalPageCount = totalCount % pagesize == 0 ? totalCount
//					/ pagesize : totalCount / pagesize + 1;
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("orderstatus", 5);
//			params.put("pagesize", pagesize);
//			int startlie = 0;
//			startlie = (currentpage - 1) * pagesize;
//			params.put("startli", startlie);
//
//			List<Map<String, Object>> merchantList2 = iMerchantEvaluationStatisDao
//					.getMerchantByOrder(params);
//			if (null != merchantList2 && merchantList2.size() > 0) {
//				merchantList.addAll(merchantList2);
//			}
//			if (currentpage < totalPageCount) {
//				currentpage++;
//				getMerchanDataList(merchantList, pagesize, currentpage,
//						totalCount);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 
//	 * ｛日志处理｝
//	 * 
//	 * @param isprint
//	 * @param strContent
//	 * @author Liuxingwen
//	 * @created 2016年8月19日 下午4:20:25
//	 * @lastModified
//	 * @history
//	 */
//	private void addSbf(Boolean isprint, String strContent) {
//		if (null == strContent)
//			return;
////		sbf.append("==>" + strContent + ",当前时间：" + getFormatTime() + "...>>End");
////		sbf.append("\r\n");
//		if (isprint) {
//			// 需要输出的项
//			sbflog.append("==>" + strContent + ",当前时间：" + getFormatTime()
//					+ "...>>End");
//			sbflog.append("\r\n");
//			System.out.println("==>" + strContent.toString() + ",当前时间："
//					+ getFormatTime() + "...>>End");
//		}
//	}
//
//	/**
//	 * 
//	 * ｛格式系统日期｝
//	 * 
//	 * @return
//	 * @author Liuxingwen
//	 * @created 2016年8月19日 上午9:43:53
//	 * @lastModified
//	 * @history
//	 */
//	private String getFormatTime() {
//		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//				.format(new java.util.Date());
//	}
//
//	// 更新缓存
//	private void updateCasheDatas() throws Exception {
//		long starTime = System.currentTimeMillis();
//		addSbf(true, "updateCasheDatas设置缓存开始");
//		String statismonth = getdbDateTime(1);// 上个月的日期201601
//		Map params = new HashMap<String, Object>();
//		params.put("statismonth", statismonth);
//		int totalCount = 0;
//		addSbf(true, "getMerChantEvaluationStatisList设置缓存读取商户统计数据开始");
//		totalCount = iMerchantEvaluationStatisDao
//				.getMerChantEvaluationStatisCount(params);
//		if (totalCount == 0) {
//			commonCacheService.delObjectContainsKey(
//					CacheConstants.MERCHANT_EVALUATION_STATIS, true);
//			return;
//		}
//		int currentpage = 1;
//		List<Map<String, Object>> MerChantEvaluation = new ArrayList<Map<String, Object>>();
//		getMerChantEvaluationStatisList(MerChantEvaluation, pageSize,
//				currentpage, totalCount, statismonth);
//		addSbf(true, "getMerChantEvaluationStatisList设置缓存读取商户统计数据结束,耗时："
//				+ ((System.currentTimeMillis() - starTime) / 1000) + "秒");
//		if (null == MerChantEvaluation || MerChantEvaluation.size() <= 0) {
//			commonCacheService.delObjectContainsKey(
//					CacheConstants.MERCHANT_EVALUATION_STATIS, true);
//			return;
//		}
//		String merchant_id = "";
//		String statismonth1 = "";
//		Object object = null;
//		long starTime1 = System.currentTimeMillis();
//		addSbf(true, "commonCacheService.delObjectContainsKey清除缓存开始");
//		commonCacheService.delObjectContainsKey(
//				CacheConstants.MERCHANT_EVALUATION_STATIS, true);
//		addSbf(true, "commonCacheService.delObjectContainsKey清除缓存结束,耗时："
//				+ ((System.currentTimeMillis() - starTime1) / 1000) + "秒");
//		long starTime2 = System.currentTimeMillis();
//		addSbf(true, "commonCacheService.setObject添加缓存开始");
//		for (Map<String, Object> map : MerChantEvaluation) {
//
//			try {
//
//				merchant_id = map.get("merchant_id") == null ? "" : String
//						.valueOf(map.get("merchant_id")).trim();
//				statismonth1 = map.get("statismonth") == null ? "" : String
//						.valueOf(map.get("statismonth")).trim();
//				addSbf(false, "设置缓存" + statismonth1 + merchant_id);
//				if (null == merchant_id || "".equals(merchant_id)
//						|| null == statismonth1 || "".equals(statismonth1)) {
//					continue;
//				}
//				object = commonCacheService.getObject(
//						CacheConstants.MERCHANT_EVALUATION_STATIS, merchant_id,
//						statismonth1);
//				if (object == null) {
//					commonCacheService.setObject(map,
//							CacheConstants.MERCHANT_EVALUATION_STATIS,
//							merchant_id, statismonth1);
//				}
//
//			} catch (Exception e) {
//			} finally {
//			}
//		}
//		addSbf(true,
//				"commonCacheService.setObject添加缓存结束,耗时："
//						+ ((System.currentTimeMillis() - starTime2) / 1000)
//						+ "秒");
//		addSbf(true,
//				"updateCasheDatas设置缓存结束,耗时："
//						+ ((System.currentTimeMillis() - starTime) / 1000)
//						+ "秒");
//	}
//
//	/**
//	 * ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
//	 * 
//	 * @param merChantEvaluation
//	 * @param pageSize2
//	 * @param currentpage
//	 * @param totalCount
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 下午3:22:37
//	 * @lastModified
//	 * @history
//	 */
//	private void getMerChantEvaluationStatisList(
//			List<Map<String, Object>> merChantEvaluation, int pagesize,
//			int currentpage, int totalCount, String statismonth) {
//		try {
//			int totalPageCount = totalCount % pagesize == 0 ? totalCount
//					/ pagesize : totalCount / pagesize + 1;
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("statismonth", statismonth);
//			params.put("pagesize", pagesize);
//			int startlie = 0;
//			startlie = (currentpage - 1) * pagesize;
//			params.put("startli", startlie);
//
//			List<Map<String, Object>> evaluationList2 = iMerchantEvaluationStatisDao
//					.getMerChantEvaluationStatis(params);
//			if (null != evaluationList2 && evaluationList2.size() > 0) {
//				merChantEvaluation.addAll(evaluationList2);
//			}
//			if (currentpage < totalPageCount) {
//				currentpage++;
//				getMerChantEvaluationStatisList(merChantEvaluation, pagesize,
//						currentpage, totalCount, statismonth);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 
//	 * ｛从数据库获取系统时间（为了时间统一）｝
//	 * 
//	 * @param mondiff
//	 * @return
//	 * @author Liuxingwen
//	 * @created 2016年8月19日 上午9:37:57
//	 * @lastModified
//	 * @history
//	 */
//	private String getdbDateTime(int mondiff) {
//		try {
//
//			Map<String, Object> maparMap = new HashMap<String, Object>();
//			maparMap.put("mondiff", mondiff);// 1：上个月的日期
//			String currentDateString = iMerchantEvaluationStatisDao
//					.getdbDateTime(maparMap);// format:201608
//			return currentDateString;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	/**
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 上午10:43:06
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public List<Map<String, Object>> getMerchantByOrder(
//			Map<String, Object> param) throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao.getMerchantByOrder(param);
//	}
//
//	/**
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月16日 下午6:17:02
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public List<Map<String, Object>> getEvaluationByMonth(
//			Map<String, Object> param) throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao.getEvaluationByMonth(param);
//	}
//
//	/**
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月16日 下午6:17:02
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public int addMerchantEvaluationStatis(List<Map<String, Object>> addList)
//			throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao
//				.addMerchantEvaluationStatis(addList);
//	}
//
//	/**
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月16日 下午6:17:02
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public String delMerchantEvaluationStatis(Map<String, Object> param)
//			throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/**
//	 * @param mapparm
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午6:32:32
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public int addMerchantEvaluationStatisOne(Map<String, Object> mapparm)
//			throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao
//				.addMerchantEvaluationStatisOne(mapparm);
//	}
//
//	/**
//	 * @param updateList
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午7:20:23
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public int updateMerchantEvaluationStatis(
//			List<Map<String, Object>> updateList) throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao
//				.updateMerchantEvaluationStatis(updateList);
//	}
//
//	/**
//	 * @param mpMap
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月17日 下午7:43:20
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public int updateMerchantEvaluationStatisOne(Map<String, Object> mpMap)
//			throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao
//				.updateMerchantEvaluationStatisOne(mpMap);
//	}
//
//	/**
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 上午10:40:10
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public Integer getMerchantByOrderCount(Map<String, Object> param)
//			throws Exception {
//		return iMerchantEvaluationStatisDao.getMerchantByOrderCount(param);
//	}
//
//	/**
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 上午11:29:19
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public Integer getEvaluationByMonthCount(Map<String, Object> param)
//			throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao.getEvaluationByMonthCount(param);
//	}
//
//	/**
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 下午2:06:00
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public List<Map<String, Object>> getMerChantEvaluationStatis(
//			Map<String, Object> param) throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao.getMerChantEvaluationStatis(param);
//	}
//
//	/**
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 下午2:15:53
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public Integer getMerChantEvaluationStatisCount(Map<String, Object> param)
//			throws Exception {
//		// TODO Auto-generated method stub
//		// getMerChantEvaluationStatisCount
//		return iMerchantEvaluationStatisDao
//				.getMerChantEvaluationStatisCount(param);
//	}
//
//	/**
//	 * @return
//	 * @throws Exception
//	 * @author Liuxingwen
//	 * @created 2016年8月18日 下午2:35:36
//	 * @lastModified
//	 * @history
//	 */
//	@Override
//	public String getdbDateTime(Map<String, Object> param) throws Exception {
//		// TODO Auto-generated method stub
//		return iMerchantEvaluationStatisDao.getdbDateTime(param);
//	}
//
//}
