package com.shanjin.manager.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.MerchantAdviserApply;
import com.shanjin.manager.Bean.MerchantPushApply;
import com.shanjin.manager.Bean.MerchantVipApply;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.ValueAddedDao;
import com.shanjin.manager.utils.MessageUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;
import com.shanjin.manager.utils.ValueAddedIncomeUtil;

public class ValueAddedDaoImpl implements ValueAddedDao {

	@Resource
	private ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	
	/**
	 * vip申请列表查询sql
	 * @param param
	 * @param flag(true:列表查询，false:列表总记录数查询)
	 * @return
	 */
	private String vipMemberSql(Map<String, String> param,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(!flag){
			//统计
			sql.append(" SELECT COUNT(1) AS count FROM ( ");	
		}
		sql.append(" SELECT mva.id,mva.apply_status AS applyStatus,mva.apply_time AS applyTime,mva.merchant_id AS merchantId, ");
		sql.append(" mva.apply_vip_level AS level,mva.app_type AS appType,mva.confirm_time AS confirmTime, ");
		sql.append(" mva.confirm_user AS confirmUser,mva.failure_time AS failureTime,mva.open_time AS openTime, ");
		sql.append(" mva.open_user AS openUser,mi.location_address as address,mi.province,mi.city,mi.`name` AS merchantName,mc.telephone,ai.app_name AS appName ");
		sql.append(" FROM merchant_vip_apply mva LEFT JOIN merchant_info mi ON mva.merchant_id=mi.id ");
		sql.append(" LEFT JOIN merchant_contact mc ON mva.merchant_id=mc.merchant_id  ");
		sql.append(" LEFT JOIN merchant_app_info ai ON mva.app_type=ai.app_type ");
		sql.append(" WHERE mva.is_del=0 ");
		String merchantName = param.get("merchantName");
		if(!StringUtil.isNullStr(merchantName)){
			sql.append(" and mi.`name` like '%").append(merchantName).append("%'");
		}
		String telephone = param.get("telephone");
		if(!StringUtil.isNullStr(telephone)){
			sql.append(" and mc.telephone like '%").append(telephone).append("%'");
		}
		String stime = param.get("stime");
		if(!StringUtil.isNullStr(stime)){
			sql.append(" and mva.apply_time>='").append(stime).append("'");
		}
		String etime = param.get("etime");
		if(!StringUtil.isNullStr(etime)){
			sql.append(" and mva.apply_time<='").append(etime).append("'");
		}
		String appType = param.get("appType");
		if(!StringUtil.isNullStr(appType)){
			sql.append(" and mva.app_type in(").append(handleAppType(appType)).append(")");
		}
		String applyStatus = param.get("applyStatus");
		if(!StringUtil.isNullStr(applyStatus)){
			sql.append(" and mva.apply_status=").append(applyStatus);
		}
		String province = param.get("province");
		if(!StringUtil.isNullStr(province)){
			sql.append(" and mi.province='").append(province).append("'");
		}
		
		String city = param.get("city");
		if(!StringUtil.isNullStr(province)&&!StringUtil.isNullStr(city)){	
			if(StringUtil.matchProvince(param.get("province"))){
				String match="%"+param.get("province")+"市"+param.get("city")+"%";
				sql.append(" and mi.location_address like '").append(match).append("'");	
			}else{
				sql.append(" and mi.city='").append(city).append("'");
			}
		}
		
		if(flag){
			//列表
			String property = "id";
			String direction = SORT.DESC;
			if(!StringUtil.isNullStr(param.get("sort"))){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			int start = 0;
			if(!StringUtil.isNullStr(param.get("start"))){
				start = StringUtil.nullToInteger(param.get("start"));
			}
			int pageSize = PAGE.PAGESIZE;
			if(!StringUtil.isNullStr(param.get("limit"))){
				pageSize = StringUtil.nullToInteger(param.get("limit"));
			}
			sql.append(" limit ");
			sql.append(start);
			sql.append(",");
			sql.append(pageSize);
		}else{
			//统计
			sql.append(" ) a ");	
		}
		System.out.println(sql.toString());
		return sql.toString();
	}
	
	private String handleAppType(String appType){
		// 处理appType供sql查询
		String str = "";
		String[] ats = appType.split(",");
		if(ats!=null&&ats.length>0){
			for(int i=0;i<ats.length;i++){
				if(i==0){
					str = "'" + ats[i] + "'";
				}else{
					str = str + "," + "'" + ats[i] + "'";
				}
			}
		}
	    return str;
	}
	
	@Override
	public List<Record> vipMemberList(Map<String, String> param) {
		List<Record> list = Db.find(vipMemberSql(param,true));
		return list;
	}
	
	@Override
	public long vipMemberListSize(Map<String, String> param) {
		return Db.queryLong(vipMemberSql(param,false));
	}

	/**
	 * 订单推送申请列表查询sql
	 * @param param
	 * @param flag(true:列表查询，false:列表总记录数查询)
	 * @return
	 */
	private String orderPushSql(Map<String, String> param,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(!flag){
			//统计
			sql.append(" SELECT COUNT(1) AS count FROM ( ");	
		}
		sql.append(" SELECT mva.id,mva.apply_status AS applyStatus,mva.apply_time AS applyTime,mva.merchant_id AS merchantId, ");
		sql.append(" mva.topup_money AS applyNum,mva.app_type AS appType,mva.confirm_time AS confirmTime, ");
		sql.append(" mva.confirm_user AS confirmUser,mva.open_time AS openTime, ");
		sql.append(" mva.open_user AS openUser,mi.location_address as address,mi.province,mi.city,mi.`name` AS merchantName,mc.telephone,ai.app_name AS appName ");
		sql.append(" FROM merchant_topup_apply mva LEFT JOIN merchant_info mi ON mva.merchant_id=mi.id ");
		sql.append(" LEFT JOIN merchant_contact mc ON mva.merchant_id=mc.merchant_id  ");
		sql.append(" LEFT JOIN merchant_app_info ai ON mva.app_type=ai.app_type ");
		sql.append(" WHERE mva.is_del=0 ");
		String merchantName = param.get("merchantName");
		if(!StringUtil.isNullStr(merchantName)){
			sql.append(" and mi.`name` like '%").append(merchantName).append("%'");
		}
		String telephone = param.get("telephone");
		if(!StringUtil.isNullStr(telephone)){
			sql.append(" and mc.telephone like '%").append(telephone).append("%'");
		}
		String stime = param.get("stime");
		if(!StringUtil.isNullStr(stime)){
			sql.append(" and mva.apply_time>='").append(stime).append("'");
		}
		String etime = param.get("etime");
		if(!StringUtil.isNullStr(etime)){
			sql.append(" and mva.apply_time<='").append(etime).append("'");
		}
		String appType = param.get("appType");
		if(!StringUtil.isNullStr(appType)){
			sql.append(" and mva.app_type in(").append(handleAppType(appType)).append(")");
		}
		String applyStatus = param.get("applyStatus");
		if(!StringUtil.isNullStr(applyStatus)){
			sql.append(" and mva.apply_status=").append(applyStatus);
		}
		String province = param.get("province");
		if(!StringUtil.isNullStr(province)){
			sql.append(" and mi.province='").append(province).append("'");
		}
		String city = param.get("city");
		if(!StringUtil.isNullStr(province)&&!StringUtil.isNullStr(city)){	
			if(StringUtil.matchProvince(param.get("province"))){
				String match="%"+param.get("province")+"市"+param.get("city")+"%";
				sql.append(" and mi.location_address like '").append(match).append("'");	
			}else{
					sql.append(" and mi.city='").append(city).append("'");
			}
		}
		if(flag){
			//列表
			String property = "id";
			String direction = SORT.DESC;
			if(!StringUtil.isNullStr(param.get("sort"))){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			int start = 0;
			if(!StringUtil.isNullStr(param.get("start"))){
				start = StringUtil.nullToInteger(param.get("start"));
			}
			int pageSize = PAGE.PAGESIZE;
			if(!StringUtil.isNullStr(param.get("limit"))){
				pageSize = StringUtil.nullToInteger(param.get("limit"));
			}
			sql.append(" limit ");
			sql.append(start);
			sql.append(",");
			sql.append(pageSize);
		}else{
			//统计
			sql.append(" ) a ");	
		}
		System.out.println(sql.toString());
		return sql.toString();
	}

	@Override
	public List<Record> orderPushList(Map<String, String> param) {
		List<Record> list = Db.find(orderPushSql(param,true));
		return list;
	}

	@Override
	public long orderPushListSize(Map<String, String> param) {
		return Db.queryLong(orderPushSql(param,false));
	}

	/**
	 * 顾问号申请列表查询sql
	 * @param param
	 * @param flag(true:列表查询，false:列表总记录数查询)
	 * @return
	 */
	private String adviserSql(Map<String, String> param,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(!flag){
			//统计
			sql.append(" SELECT COUNT(1) AS count FROM ( ");	
		}
		sql.append(" SELECT mea.id,mea.merchant_id AS merchantId,mea.apply_status AS applyStatus, ");
		sql.append(" mea.apply_increase_employee_num AS applyNum,mea.apply_time AS applyTime, ");
		sql.append(" mea.app_type AS appType,mea.confirm_time AS confirmTime,mea.failure_time AS failureTime, ");
		sql.append(" mea.open_time AS openTime,mea.confirm_user AS confirmUser,mea.open_user AS openUser, ");
		sql.append(" mi.location_address as address,mi.province,mi.city,mi.`name` AS merchantName,mc.telephone,ai.app_name AS appName ");
		sql.append(" FROM merchant_employees_num_apply mea ");
		sql.append(" LEFT JOIN merchant_info mi ON mea.merchant_id=mi.id   ");
		sql.append(" LEFT JOIN merchant_contact mc ON mea.merchant_id=mc.merchant_id ");
		sql.append(" LEFT JOIN merchant_app_info ai ON mea.app_type=ai.app_type ");
		sql.append(" WHERE mea.is_del=0 ");
		String merchantName = param.get("merchantName");
		if(!StringUtil.isNullStr(merchantName)){
			sql.append(" and mi.`name` like '%").append(merchantName).append("%'");
		}
		String telephone = param.get("telephone");
		if(!StringUtil.isNullStr(telephone)){
			sql.append(" and mc.telephone like '%").append(telephone).append("%'");
		}
		String stime = param.get("stime");
		if(!StringUtil.isNullStr(stime)){
			sql.append(" and mea.apply_time>='").append(stime).append("'");
		}
		String etime = param.get("etime");
		if(!StringUtil.isNullStr(etime)){
			sql.append(" and mea.apply_time<='").append(etime).append("'");
		}
		String appType = param.get("appType");
		if(!StringUtil.isNullStr(appType)){
			sql.append(" and mea.app_type in(").append(handleAppType(appType)).append(")");
		}
		String applyStatus = param.get("applyStatus");
		if(!StringUtil.isNullStr(applyStatus)){
			sql.append(" and mea.apply_status=").append(applyStatus);
		}
		String province = param.get("province");
		if(!StringUtil.isNullStr(province)){
			sql.append(" and mi.province='").append(province).append("'");
		}
		String city = param.get("city");
		if(!StringUtil.isNullStr(province)&&!StringUtil.isNullStr(city)){	
			if(StringUtil.matchProvince(param.get("province"))){
				String match="%"+param.get("province")+"市"+param.get("city")+"%";
				sql.append(" and mi.location_address like '").append(match).append("'");	
			}else{
					sql.append(" and mi.city='").append(city).append("'");
			}
		}
		if(flag){
			//列表
			String property = "id";
			String direction = SORT.DESC;
			if(!StringUtil.isNullStr(param.get("sort"))){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			int start = 0;
			if(!StringUtil.isNullStr(param.get("start"))){
				start = StringUtil.nullToInteger(param.get("start"));
			}
			int pageSize = PAGE.PAGESIZE;
			if(!StringUtil.isNullStr(param.get("limit"))){
				pageSize = StringUtil.nullToInteger(param.get("limit"));
			}
			sql.append(" limit ");
			sql.append(start);
			sql.append(",");
			sql.append(pageSize);
		}else{
			//统计
			sql.append(" ) a ");	
		}
		System.out.println(sql.toString());
		return sql.toString();
	}
	
	@Override
	public List<Record> adviserList(Map<String, String> param) {
		List<Record> list = Db.find(adviserSql(param,true));
		return list;
	}

	@Override
	public long adviserListSize(Map<String, String> param) {
		return Db.queryLong(adviserSql(param,false));
	}

	@Override
	public boolean deleteVipMembers(String ids) {
		Boolean flag = false;
		String sql = "update merchant_vip_apply set is_del=1 where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	@Override
	public boolean deleteOrderPushs(String ids) {
		Boolean flag = false;
		String sql = "update merchant_topup_apply set is_del=1 where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	@Override
	public boolean deleteAdvisers(String ids) {
		Boolean flag = false;
		String sql = "update merchant_employees_num_apply set is_del=1 where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	@Before(Tx.class)
	public boolean updateVipMemberStatus(String id, int status,Long userId,final String userName) {
		//申请状态 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）
		Boolean flag = false;
		Record applyRecord = Db.findById("merchant_vip_apply", id);
		final Long merchantId = StringUtil.nullToLong(applyRecord.getLong("merchant_id"));
		int recordStatus = StringUtil.nullToInteger(applyRecord.getInt("apply_status"));	
		Date nowDate = new Date();
		if(status==1||status==3){
			if(recordStatus==1||recordStatus==3){
			    //已经处理了 
				//nothing to do
			}else{
				applyRecord.set("apply_status", status).set("confirm_time", new Date()).set("confirm_user", userName).set("confirm_user_id", userId);
				Db.update("merchant_vip_apply", applyRecord);
				if(status==1){
					//确认支付 ，扣除代理商充值余额(reason （扣费原因1：服务商vip开通；2：订单推送开通；3：顾问号开通）)

				}
				if(status==3){
					MessageUtil.createCustomerMessage(null, 1, 1, merchantId, "增值服务开通", "抱歉vip会员服务开通无效，如果疑问请咨询400-020-0505。");
				}
			}

		}else if(status==2){
			// 开通功能
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(nowDate);
	        calendar.add(Calendar.YEAR, 1);
	        Date failureDate = calendar.getTime();
			applyRecord.set("apply_status", status).set("open_time", new Date()).set("failure_time", failureDate).set("open_user", userName).set("open_user_id", userId);
			Db.update("merchant_vip_apply", applyRecord);
			Db.update("UPDATE merchant_info SET vip_level=1 WHERE id="+merchantId);
			//确认支付 ，扣除代理商充值余额(reason （扣费原因1：服务商vip开通；2：订单推送开通；3：顾问号开通）)
			ValueAddedIncomeUtil.calculateIncome(merchantId, Constant.VALUEADDED_VIP_PRICE, userName, Constant.VALUEADDED_VIP);
			MessageUtil.createCustomerMessage(null, 1, 1, merchantId, "增值服务开通", "恭喜您，vip会员服务已开通。");
		}
		commonCacheService.deleteObject(CacheConstants.VALUE_ADD_SERVICE,StringUtil.null2Str(merchantId));
		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
		flag = true;
		return flag;
	}

	@Before(Tx.class)
	public boolean updateOrderPushStatus(String id, int status,Long userId,final String userName) {
		//申请状态 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）
		Boolean flag = false;
		StringBuffer sql = new StringBuffer();
		Record applyRecord = Db.findById("merchant_topup_apply", id);
		final Long merchantId = StringUtil.nullToLong(applyRecord.getLong("merchant_id"));
		final int applyNum = applyRecord.getBigDecimal("topup_money").intValue();
		int recordStatus = StringUtil.nullToInteger(applyRecord.getInt("apply_status"));
		String appType = applyRecord.getStr("app_type");
		if(status==1||status==3){
			if(recordStatus==1||recordStatus==3){
			    //已经处理了 
				//nothing to do
			}else{
				applyRecord.set("apply_status", status).set("confirm_time", new Date()).set("confirm_user", userName).set("confirm_user_id", userId);
				Db.update("merchant_topup_apply", applyRecord);
				if(status==1){
					//确认支付 ，扣除代理商充值余额(reason （扣费原因1：服务商vip开通；2：订单推送开通；3：顾问号开通）)
				}
				
				if(status==3){
					MessageUtil.createCustomerMessage(null, 1, 1, merchantId, "增值服务开通", "抱歉订单推送服务申请开通无效，如果疑问请咨询400-020-0505。");
				}
			}
		
		}else if(status==2){
			sql.append(" UPDATE merchant_statistics SET order_surplus_price=order_surplus_price+").append(applyNum);
			sql.append(" WHERE merchant_id=").append(merchantId);
			Db.update(sql.toString());
			applyRecord.set("apply_status", status).set("open_time", new Date()).set("open_user", userName).set("open_user_id", userId);
			Db.update("merchant_topup_apply", applyRecord);
//			Record r = new Record();
//			r.set("merchant_id", merchantId).set("pay_type", 1).set("pay_money", applyNum).set("pay_time",new Date()).set("app_type", appType);
//			Db.save("merchant_order_payment_details", r);
			//确认支付 ，扣除代理商充值余额(reason （扣费原因1：服务商vip开通；2：订单推送开通；3：顾问号开通）)
			ValueAddedIncomeUtil.calculateIncome(merchantId, applyNum, userName, Constant.VALUEADDED_PUSH);
			MessageUtil.createCustomerMessage(null, 1, 1, merchantId, "增值服务开通", "恭喜您，订单推送服务已开通。");
		}
		commonCacheService.deleteObject(CacheConstants.VALUE_ADD_SERVICE,StringUtil.null2Str(merchantId));
		flag = true;
		return flag;
	}

	@Before(Tx.class)
	public boolean updateAdviserStatus(String id, int status,Long userId,final String userName) {
		//申请状态 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）
		Boolean flag = false;
		StringBuffer sql = new StringBuffer();
		Record applyRecord = Db.findById("merchant_employees_num_apply", id);
		final Long merchantId = StringUtil.nullToLong(applyRecord.getLong("merchant_id"));
		int recordStatus = StringUtil.nullToInteger(applyRecord.getInt("apply_status"));	
		final int applyNum = applyRecord.getInt("apply_increase_employee_num");
		Date nowDate = new Date();
		if(status==1||status==3){
			if(recordStatus==1||recordStatus==3){
			    //已经处理了 
					//nothing to do
			}else{
				applyRecord.set("apply_status", status).set("confirm_time", new Date()).set("confirm_user", userName).set("confirm_user_id", userId);
				Db.update("merchant_employees_num_apply", applyRecord);
				if(status==1){
					//确认支付 ，扣除代理商充值余额(reason （扣费原因1：服务商vip开通；2：订单推送开通；3：顾问号开通）)
				}
				if(status==3){
					MessageUtil.createCustomerMessage(null, 1, 1, merchantId, "增值服务开通", "抱歉顾问号申请服务开通无效，如果疑问请咨询400-020-0505。");
				}
			}

		}else if(status==2){
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(nowDate);
	        calendar.add(Calendar.YEAR, 1);
	        Date failureDate = calendar.getTime();
			sql.append("UPDATE merchant_info SET max_employee_num=max_employee_num+").append(applyNum);
			sql.append(" WHERE id=").append(merchantId);
			Db.update(sql.toString());
			applyRecord.set("apply_status", status).set("open_time", new Date()).set("failure_time", failureDate).set("open_user", userName).set("open_user_id", userId);
			Db.update("merchant_employees_num_apply", applyRecord);
			
			//确认支付 ，扣除代理商充值余额(reason （扣费原因1：服务商vip开通；2：订单推送开通；3：顾问号开通）)
			ValueAddedIncomeUtil.calculateIncome(merchantId, applyNum*Constant.VALUEADDED_ADVISER_PRICE, userName, Constant.VALUEADDED_ADVISER);
			MessageUtil.createCustomerMessage(null, 1, 1, merchantId, "增值服务开通", "恭喜您，顾问号服务已开通。");
		}
		commonCacheService.deleteObject(CacheConstants.VALUE_ADD_SERVICE,StringUtil.null2Str(merchantId));
		flag = true;
		return flag;
	}

	@Override
	public List<Record> getGrabFeeList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select g.id,g.province,g.city,g.app_type,(select name from catalog where alias=g.app_type and level=0) as app_name,g.grab_fee,g.status,g.time from manager_grab_fee g where g.is_del=0 and ");
		totalSql.append("select count(1) as total from manager_grab_fee g where g.is_del=0 and ");
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		strFilter.put("g.province", paramMap.get("province"));
		strFilter.put("g.city", paramMap.get("city"));
		strFilter.put("g.app_type", paramMap.get("app_type"));
		strFilter.put("g.status", paramMap.get("status"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total=Db.find(totalSql.toString()).get(0).getLong("total");
		
		 String property = "g.time";
			String direction = SORT.DESC;
			if(StringUtil.isNotNullMap(paramMap,"sort")){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<Record> grabFeeList =Db.find(sql.toString());
	    if(grabFeeList.size()>0){
	    	grabFeeList.get(0).set("total", total);
	    }
		return grabFeeList;
	}

	@Override
	public int addGrabFee(Map<String, String[]> param) {
		int flag=0;
		String province=param.get("province")[0];
		String city=param.get("city")[0];
		String app_type=param.get("app_type")[0];
		String grab_fee=param.get("grab_fee")[0];
		String sql="select count(1) as total from manager_grab_fee where is_del=0 and province=? and city=? and app_type=? ";
		List<Record> grabFee=Db.find(sql,province,city,app_type);
		if(grabFee.get(0).getLong("total")>0){
			flag=2;
			return flag;
		}
		Record record=new Record();
		record.set("province", province).set("city", city)
				.set("app_type", app_type).set("grab_fee", grab_fee)
				.set("time", new Date()).set("status", 0).set("is_del", 0);
		Db.save("manager_grab_fee", record);
		flag=1;
		return flag;
	}

	@Override
	public Boolean updateGrabFee(Map<String, String[]> param) {
		boolean flag=false;
		double order_price=Double.parseDouble(param.get("grab_fee")[0]);
		String id=param.get("id")[0];
		StringBuffer sql = new StringBuffer();
		sql.append("update merchant_info set order_price=? where ");
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		strFilter.put("province", param.get("province"));
		strFilter.put("city", param.get("city"));
		strFilter.put("app_type", param.get("app_type"));
		sql.append(Util.getFilter(strFilter, intFilter));
		int upda=Db.update(sql.toString(),order_price);
		if(upda>0){
		String sqlNew="update manager_grab_fee set status=1 where id=?";
		flag=Db.update(sqlNew.toString(),id)>0;
		}
		return flag;
	}
	@Override
	public Boolean deleteGrabFee(Map<String, String[]> param) {
		boolean flag=false;
		String[] ids=param.get("id")[0].split(",");
		for(int i=0;i<ids.length;i++){
		String sql="update manager_grab_fee set is_del=1 where id="+ids[i];
		Db.update(sql);
		}
		flag=true;
		return flag;
	}

	@Override
	public Boolean editGrabFee(Map<String, String[]> param) {
		boolean flag=false;
		String id=param.get("id")[0];
		String grab_fee=param.get("grab_fee")[0];
		String sql="update manager_grab_fee set grab_fee="+grab_fee+" where id="+id;
		Db.update(sql);
		String statuSql="update manager_grab_fee set status=0 where id="+id;
		Db.update(statuSql);
		flag=true;
		return flag;
	}

	@Override
	public List<MerchantVipApply> exportVipMemberExcel(
			Map<String, String> param, String[] stime, String[] etime) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append(" SELECT mva.id,(case mva.apply_status when 1 then '已确认' when 2 then '已开通' when 3 then '无效' when 4 then '已过期' else '待开通' end) AS applyStatus,mva.apply_time AS applyTime,mva.merchant_id AS merchantId, ");
		sql.append(" mva.apply_vip_level AS level,mva.app_type AS appType,mva.confirm_time AS confirmTime, ");
		sql.append(" mva.confirm_user AS confirmUser,mva.failure_time AS failureTime,mva.open_time AS openTime, ");
		sql.append(" mva.open_user AS openUser,mi.location_address as address,mi.province,mi.city,mi.`name` AS merchantName,mc.telephone,ai.app_name AS appName ");
		sql.append(" FROM merchant_vip_apply mva LEFT JOIN merchant_info mi ON mva.merchant_id=mi.id ");
		sql.append(" LEFT JOIN merchant_contact mc ON mva.merchant_id=mc.merchant_id  ");
		sql.append(" LEFT JOIN merchant_app_info ai ON mva.app_type=ai.app_type ");
		sql.append(" WHERE mva.is_del=0 ");
		String merchantName = param.get("merchantName");
		if(!StringUtil.isNullStr(merchantName)){
			sql.append(" and mi.`name` like '%").append(merchantName).append("%'");
		}
		String telephone = param.get("telephone");
		if(!StringUtil.isNullStr(telephone)){
			sql.append(" and mc.telephone like '%").append(telephone).append("%'");
		}
		
		sql.append(Util.getExportdateFilter("mva.apply_time", stime,etime));
		String appType = param.get("appType");
		if(!StringUtil.isNullStr(appType)){
			sql.append(" and mva.app_type in(").append(handleAppType(appType)).append(")");
		}
		String applyStatus = param.get("applyStatus");
		if(!StringUtil.isNullStr(applyStatus)){
			sql.append(" and mva.apply_status=").append(applyStatus);
		}
		String province = param.get("province");
		if(!StringUtil.isNullStr(province)){
			sql.append(" and mi.province='").append(province).append("'");
		}
		String city = param.get("city");
		if(!StringUtil.isNullStr(province)&&!StringUtil.isNullStr(city)){	
			if(StringUtil.matchProvince(param.get("province"))){
				String match="%"+param.get("province")+"市"+param.get("city")+"%";
				sql.append(" and mi.location_address like '").append(match).append("'");	
			}else{
					sql.append(" and mi.city='").append(city).append("'");
			}
		}
			//列表
			String property = "id";
			String direction = SORT.DESC;
			if(!StringUtil.isNullStr(param.get("sort"))){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			
			sql.append(" limit ");
			sql.append(pageNumber);
			sql.append(",");
			sql.append(pageSize);
		
			List<MerchantVipApply> merchantVipApply = MerchantVipApply.dao.find(sql
					.toString());
			
		    return merchantVipApply;
	}

	@Override
	public List<MerchantPushApply> exportPushApplyExcel(
			Map<String, String> param, String[] stime, String[] etime) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append(" SELECT mva.id,(case mva.apply_status when 1 then '已确认' when 2 then '已开通' when 3 then '无效' when 4 then '已过期' else '待开通' end) AS applyStatus,mva.apply_time AS applyTime,mva.merchant_id AS merchantId, ");
		sql.append(" mva.topup_money AS applyNum,mva.app_type AS appType,mva.confirm_time AS confirmTime, ");
		sql.append(" mva.confirm_user AS confirmUser,mva.open_time AS openTime, ");
		sql.append(" mva.open_user AS openUser,mi.location_address as address,mi.province,mi.city,mi.`name` AS merchantName,mc.telephone,ai.app_name AS appName ");
		sql.append(" FROM merchant_topup_apply mva LEFT JOIN merchant_info mi ON mva.merchant_id=mi.id ");
		sql.append(" LEFT JOIN merchant_contact mc ON mva.merchant_id=mc.merchant_id  ");
		sql.append(" LEFT JOIN merchant_app_info ai ON mva.app_type=ai.app_type ");
		sql.append(" WHERE mva.is_del=0 ");
		String merchantName = param.get("merchantName");
		if(!StringUtil.isNullStr(merchantName)){
			sql.append(" and mi.`name` like '%").append(merchantName).append("%'");
		}
		String telephone = param.get("telephone");
		if(!StringUtil.isNullStr(telephone)){
			sql.append(" and mc.telephone like '%").append(telephone).append("%'");
		}
		sql.append(Util.getExportdateFilter("mva.apply_time", stime,etime));
		String appType = param.get("appType");
		if(!StringUtil.isNullStr(appType)){
			sql.append(" and mva.app_type in(").append(handleAppType(appType)).append(")");
		}
		String applyStatus = param.get("applyStatus");
		if(!StringUtil.isNullStr(applyStatus)){
			sql.append(" and mva.apply_status=").append(applyStatus);
		}
		String province = param.get("province");
		if(!StringUtil.isNullStr(province)){
			sql.append(" and mi.province='").append(province).append("'");
		}
		String city = param.get("city");
		if(!StringUtil.isNullStr(province)&&!StringUtil.isNullStr(city)){	
			if(StringUtil.matchProvince(param.get("province"))){
				String match="%"+param.get("province")+"市"+param.get("city")+"%";
				sql.append(" and mi.location_address like '").append(match).append("'");	
			}else{
					sql.append(" and mi.city='").append(city).append("'");
			}
		}
		
			//列表
			String property = "id";
			String direction = SORT.DESC;
			if(!StringUtil.isNullStr(param.get("sort"))){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			
			sql.append(" limit ");
			sql.append(pageNumber);
			sql.append(",");
			sql.append(pageSize);
			List<MerchantPushApply> merchantPushApply = MerchantPushApply.dao.find(sql
					.toString());
			
		    return merchantPushApply;
	}

	@Override
	public List<MerchantAdviserApply> exportAdviserApplyExcel(
			Map<String, String> param, String[] stime, String[] etime) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append(" SELECT mea.id,mea.merchant_id AS merchantId,(case mea.apply_status when 1 then '已确认' when 2 then '已开通' when 3 then '无效' when 4 then '已过期' else '待开通' end) AS applyStatus, ");
		sql.append(" mea.apply_increase_employee_num AS applyNum,mea.apply_time AS applyTime, ");
		sql.append(" mea.app_type AS appType,mea.confirm_time AS confirmTime,mea.failure_time AS failureTime, ");
		sql.append(" mea.open_time AS openTime,mea.confirm_user AS confirmUser,mea.open_user AS openUser, ");
		sql.append(" mi.location_address as address,mi.province,mi.city,mi.`name` AS merchantName,mc.telephone,ai.app_name AS appName ");
		sql.append(" FROM merchant_employees_num_apply mea ");
		sql.append(" LEFT JOIN merchant_info mi ON mea.merchant_id=mi.id   ");
		sql.append(" LEFT JOIN merchant_contact mc ON mea.merchant_id=mc.merchant_id ");
		sql.append(" LEFT JOIN merchant_app_info ai ON mea.app_type=ai.app_type ");
		sql.append(" WHERE mea.is_del=0 ");
		String merchantName = param.get("merchantName");
		if(!StringUtil.isNullStr(merchantName)){
			sql.append(" and mi.`name` like '%").append(merchantName).append("%'");
		}
		String telephone = param.get("telephone");
		if(!StringUtil.isNullStr(telephone)){
			sql.append(" and mc.telephone like '%").append(telephone).append("%'");
		}
		sql.append(Util.getExportdateFilter("mva.apply_time", stime,etime));
		String appType = param.get("appType");
		if(!StringUtil.isNullStr(appType)){
			sql.append(" and mea.app_type in(").append(handleAppType(appType)).append(")");
		}
		String applyStatus = param.get("applyStatus");
		if(!StringUtil.isNullStr(applyStatus)){
			sql.append(" and mea.apply_status=").append(applyStatus);
		}
		String province = param.get("province");
		if(!StringUtil.isNullStr(province)){
			sql.append(" and mi.province='").append(province).append("'");
		}
		String city = param.get("city");
		if(!StringUtil.isNullStr(province)&&!StringUtil.isNullStr(city)){	
			if(StringUtil.matchProvince(param.get("province"))){
				String match="%"+param.get("province")+"市"+param.get("city")+"%";
				sql.append(" and mi.location_address like '").append(match).append("'");	
			}else{
					sql.append(" and mi.city='").append(city).append("'");
			}
		}
		
			//列表
			String property = "id";
			String direction = SORT.DESC;
			if(!StringUtil.isNullStr(param.get("sort"))){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			
			sql.append(" limit ");
			sql.append(pageNumber);
			sql.append(",");
			sql.append(pageSize);
			List<MerchantAdviserApply> merchantAdviserApply = MerchantAdviserApply.dao.find(sql
					.toString());
			
		    return merchantAdviserApply;
	}
}
