/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.service.impl <br>
 * FileName: MerchantInvoiceServiceImpl.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年10月14日
 * @last Modified 
 * @history
 */
package com.shanjin.service.impl;

//import java.sql.Date;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import scala.Array;
import scala.annotation.elidable;
import akka.dispatch.Foreach;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IMerchantInvoiceDao;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IMerchantInvoiceService;

/**
 * {该处说明该构造函数的含义及作用}
 * 
 * @author Liuxingwen
 * @created 2016年10月14日 下午1:37:51
 * @lastModified
 * @history
 */
@Service("merchantInvoiceService")
public class MerchantInvoiceServiceImpl implements IMerchantInvoiceService {

	@Resource
	private IMerchantInvoiceDao merchantInvoiceDao;
	@Resource
	private ICommonCacheService commonCacheService;
	@Resource
	private ICommonService commonService;

	/**
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年10月14日 下午1:37:51
	 * @lastModified
	 * @history
	 */
	@Override
	public JSONObject selectIncOrders(Map<String, Object> paramMap)
			throws Exception {
		JSONObject jsonObject = new JSONObject();
		String merchantId = String.valueOf(paramMap.get("merchantId"));
		List<Map<String, Object>> incOrderList = new ArrayList<Map<String, Object>>();
		// if
		// (commonCacheService.getObject(CacheConstants.INVOICE_VALUE_ADD_SERVICE,
		// merchantId.toString()) == null) {
		List<Map<String, Object>> selectList = merchantInvoiceDao
				.selectIncOrders(paramMap);
		for (Map<String, Object> map : selectList) {
			if (StringUtil.null2Str(map.get("type")).equals("topup")) {
				// map.put("title", "接单余额：" +
				// StringUtil.null2Str(map.get("money")) + "元");
				map.put("incorder_type", "订单推送");
				map.put("inctype_id", 1);
			} else if (StringUtil.null2Str(map.get("type")).equals(
					"employeesNum")) {
				// map.put("incorder_type",
				// "员工账号："
				// + StringUtil.null2Str(map
				// .get("applyIncreaseEmployeeNum")) + "个");

				map.put("incorder_type", "员工账号");
				map.put("inctype_id", 2);
			} else {
				String status = StringUtil.null2Str(map.get("applyStatus"));
				if (status.equals("1")) {
					map.put("applyStatus", 2);
				}
				map.put("incorder_type", map.get("type"));
				map.put("inctype_id", 3);
			}

			if (StringUtil.null2Str(map.get("applyStatus")).equals("0")) {
				map.put("applyStatusName", "待支付");
			}
			if (StringUtil.null2Str(map.get("applyStatus")).equals("1")) {
				map.put("applyStatusName", "待开通");
			}
			if (StringUtil.null2Str(map.get("applyStatus")).equals("2")) {
				map.put("applyStatusName", "已开通");
			}
			if (StringUtil.null2Str(map.get("applyStatus")).equals("3")) {
				map.put("applyStatusName", "无效");
			}
			if (StringUtil.null2Str(map.get("applyStatus")).equals("4")) {
				map.put("applyStatusName", "已过期");
			}
			map.remove("sortTime");
			map.remove("payType");
			map.remove("type");
			map.remove("applyIncreaseEmployeeNum");
			map.remove("payNo");

			BusinessUtil.disposePath(map, "iconPath");
		}

		// commonCacheService
		// .setObject(selectList,
		// CacheConstants.INVOICE_VALUE_ADD_SERVICE,
		// merchantId.toString());
		incOrderList = selectList;

		// }
		// else {
		// incOrderList = (List<Map<String, Object>>)
		// commonCacheService.getObject(CacheConstants.INVOICE_VALUE_ADD_SERVICE,
		// merchantId.toString());
		// }

		int invoiceCount = merchantInvoiceDao.selectInvoiceCount(paramMap);

		Map<String, Object> immediateDateMap = commonService
				.getConfigurationInfoByKey(CacheConstants.MAX_INVOICE_NUM_STRING);
		int max_invoice_num = Integer.parseInt(immediateDateMap.get(
				"config_value").toString());
		jsonObject = new ResultJSONObject("000", "获取商户增值服务订单");
		if (max_invoice_num > invoiceCount) {
			jsonObject.put("invoiceCount", 1);// 允许开发票
		} else {
			jsonObject.put("invoiceCount", 0);// 不允许开发票
		}

		jsonObject.put("incOrderList", incOrderList);

		return jsonObject;
	}

	/**
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年10月17日 下午2:33:05
	 * @lastModified
	 * @history
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	@Transactional(rollbackFor = Exception.class)
	@Override
	public JSONObject saveInvoice(Map<String, Object> paramMap)
			throws Exception {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		Map<String, Object> paramMap1 = paramMap;
		int insertInvoice2 = 0;
		double invoiceMoney=Double.valueOf(String.valueOf(paramMap1.get("invoiceMoney")));
		double minvalue=300;
		if(invoiceMoney<minvalue)
		{
			jsonObject = new ResultJSONObject("001", "发票保存失败，发票金额不能少于300");
			jsonObject.put("voice_id", 0);
			return jsonObject;
		}
		//判断本自然月是否开过票Start		
		int invoiceCount = merchantInvoiceDao.selectInvoiceCount(paramMap);
		Map<String, Object> immediateDateMap = commonService
				.getConfigurationInfoByKey(CacheConstants.MAX_INVOICE_NUM_STRING);
		int max_invoice_num = Integer.parseInt(immediateDateMap.get(
				"config_value").toString());
		if (invoiceCount >= max_invoice_num) {
			jsonObject = new ResultJSONObject("001", "发票保存失败，本月已不能再申请开发票,超过申请次数");
			jsonObject.put("voice_id", 0);
			return jsonObject;	
		} 		
		//====================End
		int insertInvoice = merchantInvoiceDao.insertInvoice(paramMap1);
		long voice_id = Long.valueOf(String.valueOf(paramMap.get("id")).trim());
		// System.out
		// .println("insertInvoice:" + insertInvoice + ",id:" + voice_id);
		// if (insertInvoice == 1) {
		com.alibaba.fastjson.JSONArray jsonArray = JSON.parseArray(String
				.valueOf((paramMap.get("incOrderList"))));
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		List<Integer> listtopup = new ArrayList<Integer>();
		List<Integer> listemployees = new ArrayList<Integer>();
		List<Integer> listInc = new ArrayList<Integer>();
//		List<Integer> listvoiceInfo = new ArrayList<Integer>();
		List<Map<String, Object>> listvoiceInfos = new ArrayList<Map<String, Object>>();
		int inctype_id = 0;
		for (Object object : jsonArray) {
			map2 = (Map<String, Object>) object;
			map2.put("voice_id", voice_id);
			lists.add(map2);
			inctype_id = Integer
					.valueOf(String.valueOf(map2.get("inctype_id")));
			if (inctype_id == 1) {
				listtopup.add(Integer.valueOf(String.valueOf(map2.get("id"))));
			} else if (inctype_id == 2) {
				listemployees
						.add(Integer.valueOf(String.valueOf(map2.get("id"))));
			} else if (inctype_id == 3) {
				listInc.add(Integer.valueOf(String.valueOf(map2.get("id"))));
			}
//			listvoiceInfo.add(Integer.valueOf(String.valueOf(map2.get("id"))));
			listvoiceInfos.add(map2);
			System.out.println(map2);
		}
		
		List<Map<String, Object>> listsvoiceInfo_cf = merchantInvoiceDao
				.selectInvoiceInfoList2(listvoiceInfos);
		if(null !=listsvoiceInfo_cf && listsvoiceInfo_cf.size()>0)
		{
			listvoiceInfos.clear();
			throw new Exception("errorsvoice");
		}
//		// 判断关键表里是否有申请过的增值服务订单ID
//		int glcount = merchantInvoiceDao.selectInvoiceInfoCount(listvoiceInfo);
//		//增值服务订单ID关连过发票时，判断发票状态，已驳回的状态才可以再次申请开发票
//		if (glcount > 0) {
//			List<Map<String, Object>> listsvoiceInfo = merchantInvoiceDao
//					.selectInvoiceInfoList(listvoiceInfo);
//			int audit_status = 0;
//			if (listsvoiceInfo != null)
//				for (Map<String, Object> map : listsvoiceInfo) {
//					audit_status = Integer.valueOf(String.valueOf(map
//							.get("audit_status")));
//					if (audit_status != 2) {
//						throw new Exception("errorsvoice");
//					}
//				}
//		}
		// 添加发票和订单关键表
		insertInvoice2 = merchantInvoiceDao.insertInvoiceInfo(lists);
		// 更新增值服务订单已申请过发票。
		if (listtopup != null && listtopup.size() > 0)
			this.merchantInvoiceDao.updateMerchant_topup_apply(listtopup);
		if (listemployees != null && listemployees.size() > 0)
			this.merchantInvoiceDao
					.updateMerchant_employees_num_apply(listemployees);
		if (listInc != null && listInc.size() > 0)
			this.merchantInvoiceDao.updateInc_pkg_order(listInc);
		// }
		System.out.println("insertInvoice:" + insertInvoice
				+ ",insertInvoice2:" + insertInvoice2);
		jsonObject = new ResultJSONObject("000", "发票保存成功");
		jsonObject.put("voice_id", voice_id);
		return jsonObject;
	}

	/**
	 * @param paramMap
	 * @return
	 * @throws Exception
	 * @author Liuxingwen
	 * @created 2016年10月17日 下午4:42:20
	 * @lastModified
	 * @history
	 */
	@Override
	public JSONObject selectInvoice(Map<String, Object> paramMap)
			throws Exception {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		Map<String, Object> map1 = merchantInvoiceDao.selectinvoice1(paramMap);
		if (map1 != null) {
			// map1.remove("audit_time");
			// map1.remove("treated_human");
			// map1.remove("remark");
			if (Integer
					.valueOf(String.valueOf(map1.get("audit_status")).trim()) == 0) {
				map1.put("audit_StatusText", "待开票");
			} else if (Integer.valueOf(String.valueOf(map1.get("audit_status"))
					.trim()) == 1) {
				map1.put("audit_StatusText", "已开票");
			} else if (Integer.valueOf(String.valueOf(map1.get("audit_status"))
					.trim()) == 2) {
				map1.put("audit_StatusText", "已驳回");
			}
			// map1.remove("audit_status");
			map1.put("audit_Status", Integer.valueOf(String.valueOf(
					map1.get("audit_status")).trim()));
			map1.remove("audit_status");
			// '是否处理 0-待开票 1-已开票 2-已驳回'

			if (Integer.valueOf(String.valueOf(map1.get("mailing_method"))
					.trim()) == 0) {
				map1.put("mailing_method", "免邮费");
			} else {
				map1.put("mailing_method", "免邮费");
			}

			map1.remove("mailing_Method");

		}
		List<Map<String, Object>> incVoiceList = merchantInvoiceDao
				.selectinvoice2(paramMap);
		int inctype_id = 0;
		if (incVoiceList != null) {
			for (Map<String, Object> map : incVoiceList) {

				BusinessUtil.disposePath(map, "iconPath");

				// ======================================
				// 申请状态 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）',
				// '申请状态 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）',
				// '0:待审核，1：已开通，2：失败',
				inctype_id = Integer.valueOf(String.valueOf(
						map.get("inctype_id")).trim());
				if (inctype_id == 3) {
					String status = StringUtil.null2Str(map.get("applyStatus"));
					if (status.equals("1")) {
						map.put("applyStatus", 2);
					} else if (status.equals("2")) {
						map.put("applyStatus", 3);
					}
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("0")) {
					map.put("applyStatusName", "待支付");
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("1")) {
					map.put("applyStatusName", "待开通");
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("2")) {
					map.put("applyStatusName", "已开通");
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("3")) {
					map.put("applyStatusName", "无效");
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("4")) {
					map.put("applyStatusName", "已过期");
				}
				// ======================================================
				map.remove("inctype_id");
			}

		}
		jsonObject = new ResultJSONObject("000", "获取发票详情页数据成功");
		jsonObject.put("incVoice", map1);
		jsonObject.put("incVoiceInfo", incVoiceList);
		jsonObject.put("incVoiceInfoCount", incVoiceList.size());

		return jsonObject;
	}

	@Override
	public JSONObject getInvoiceList(Map<String, Object> paramMap)
			throws Exception {
		JSONObject jsonObject = null;
		int totalPage = 0;
		List<Map<String, Object>> resultMap = null;
		resultMap = this.merchantInvoiceDao.selectinvoices(paramMap);
		String audit_time = null;
		for (Map<String, Object> map1 : resultMap) {
			// if (Integer
			// .valueOf(String.valueOf(map1.get("audit_status")).trim()) == 0) {
			// map1.put("audit_Status", "待开票");
			// } else if
			// (Integer.valueOf(String.valueOf(map1.get("audit_status"))
			// .trim()) == 1) {
			// map1.put("audit_Status", "已开票");
			// } else if
			// (Integer.valueOf(String.valueOf(map1.get("audit_status"))
			// .trim()) == 2) {
			// map1.put("audit_Status", "已驳回");
			// }
			// map1.remove("audit_status");

			if (Integer
					.valueOf(String.valueOf(map1.get("audit_status")).trim()) == 0) {
				map1.put("audit_StatusText", "待开票");
			} else if (Integer.valueOf(String.valueOf(map1.get("audit_status"))
					.trim()) == 1) {
				map1.put("audit_StatusText", "已开票");
			} else if (Integer.valueOf(String.valueOf(map1.get("audit_status"))
					.trim()) == 2) {
				map1.put("audit_StatusText", "已驳回");
			}
			// map1.remove("audit_status");
			map1.put("audit_Status", Integer.valueOf(String.valueOf(
					map1.get("audit_status")).trim()));
			map1.remove("audit_status");

			if (Integer.valueOf(String.valueOf(map1.get("mailing_method"))
					.trim()) == 0) {
				map1.put("mailing_method", "免邮费");
			} else {
				map1.put("mailing_method", "免邮费");
			}
			map1.remove("mailing_Method");
			if (null != map1.get("audit_time")
					&& !"".equals(String.valueOf(map1.get("audit_time")).trim())) {
				Date dt1 = com.shanjin.common.util.DateUtil.parseDate(
						"yyyy-MM-dd HH:mm:ss",
						String.valueOf(map1.get("audit_time")).trim());
				map1.put("auditTime", com.shanjin.common.util.DateUtil
						.formatDate("yyyy-MM-dd HH:mm:ss", dt1));
			}
		}
		totalPage = this.merchantInvoiceDao.selectinvoicesCount(paramMap);
		totalPage = BusinessUtil.totalPageCalc(totalPage);
		jsonObject = new ResultJSONObject("000", "获取开发票历史记录成功");
		jsonObject.put("InvoicesInfo", resultMap);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

}
