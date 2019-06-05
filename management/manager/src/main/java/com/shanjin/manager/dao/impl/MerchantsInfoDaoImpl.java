package com.shanjin.manager.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.MerchantsEmployees;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.WithDraw;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.MerchantsInfoDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.ImageMarkUtil;
import com.shanjin.manager.utils.MessageUtil;
import com.shanjin.manager.utils.MqUtil;
import com.shanjin.manager.utils.PushUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class MerchantsInfoDaoImpl implements MerchantsInfoDao {
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	public List<Record> getMerchantsInfoList(Map<String, String[]> paramMap) {
		if(paramMap.get("telephone")[0].equals("")){
			return getMerchantsConciseList(paramMap);
		}else{
			return getMerchantsAuthList(paramMap);
		}
	}

	private List<Record> getMerchantsAuthList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("m.name", paramMap.get("name"));
		strFilter.put("m.province", paramMap.get("province"));
		strFilter.put("m.app_type", paramMap.get("app_type"));
		strFilter.put("m.invitation_code", paramMap.get("invite_code"));
		strFilter.put("me.phone", paramMap.get("telephone"));
		intFilter.put("m.auth_type", paramMap.get("auth_type"));
		
		sql.append(" select m.id,m.name,m.join_time,m.location_address as address,m.vip_level,m.province,m.city,m.app_type,m.is_del,m.longitude,m.latitude,m.invitation_code,me.phone as telephone,(select name from catalog where alias=m.app_type and level=0) as app_name,m.auth_status,m.auth_type,"
				+ " (select count(DISTINCT mp.vouchers_id) from merchant_vouchers_permissions mp  where mp.merchant_id=m.id and mp.is_del=0) as vouchers "
				+ "from merchant_info m left join merchant_employees me on me.merchant_id=m.id and me.is_del=0 and me.employees_type=1 where  m.is_del=0 and ");
		totalSql.append("select count(1) as total from merchant_info m left join merchant_employees me on me.merchant_id=m.id and  me.employees_type=1 where m.is_del=0 and ");
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strFilter, intFilter));
				totalSql.append(Util.getFilter(strFilter, intFilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
				totalSql.append(" and m.location_address like '").append(match).append("' ");
			}else{
				strFilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strFilter, intFilter));
				totalSql.append(Util.getFilter(strFilter, intFilter));
			}
		}else{
			sql.append(Util.getFilter(strFilter, intFilter));
			totalSql.append(Util.getFilter(strFilter, intFilter));
		}
		
		sql.append(Util.getdateFilter("m.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("m.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!paramMap.get("auth_status")[0].equals("")){
			if(paramMap.get("auth_status")[0].equals("null")){
				sql.append(" and m.auth_status is null ");
				totalSql.append(" and m.auth_status is null ");
			}else{
				sql.append(" and m.auth_status=").append(paramMap.get("auth_status")[0]);
				totalSql.append(" and m.auth_status=").append(paramMap.get("auth_status")[0]);
			}
		}
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			totalSql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		String property = "m.join_time";
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
		
		List<Record> merchantsList =  Db.find(sql
				.toString());

		if (merchantsList.size() > 0) {
			merchantsList.get(0).set("total", total);
			for(int i=0;i<merchantsList.size();i++){
			merchantsList.get(i).set("id", merchantsList.get(i).getLong("id").toString());
			}
		}

		return merchantsList;
	}
	
	//当条件没有认证类型和状态时，不关联认证表查询商家
	private List<Record> getMerchantsConciseList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("m.name", paramMap.get("name"));
		strFilter.put("m.province", paramMap.get("province"));
		strFilter.put("m.app_type", paramMap.get("app_type"));
		strFilter.put("m.invitation_code", paramMap.get("invite_code"));
		intFilter.put("m.auth_type", paramMap.get("auth_type"));
		
		sql.append(" select m.id,m.name,m.join_time,m.location_address as address,m.vip_level,m.province,m.city,m.app_type,m.invitation_code,(select name from catalog where alias=m.app_type and level=0) as app_name,m.auth_status,m.auth_type,m.is_del,m.longitude,m.latitude,"
				+ " (select me.phone from merchant_employees me where me.merchant_id=m.id and me.is_del=0 and me.employees_type=1 limit 1) as telephone, "
				+ " (select count(DISTINCT mp.vouchers_id) from merchant_vouchers_permissions mp  where mp.merchant_id=m.id and mp.is_del=0) as vouchers "
				+ "from merchant_info m where m.is_del=0 and ");
		totalSql.append("select count(1) as total from merchant_info m where m.is_del=0 and ");
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strFilter, intFilter));
				totalSql.append(Util.getFilter(strFilter, intFilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
				totalSql.append(" and m.location_address like '").append(match).append("' ");
			}else{
				strFilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strFilter, intFilter));
				totalSql.append(Util.getFilter(strFilter, intFilter));
			}
		}else{
			sql.append(Util.getFilter(strFilter, intFilter));
			totalSql.append(Util.getFilter(strFilter, intFilter));
		}
		
		sql.append(Util.getdateFilter("m.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("m.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!paramMap.get("auth_status")[0].equals("")){
			if(paramMap.get("auth_status")[0].equals("null")){
				sql.append(" and m.auth_status is null ");
				totalSql.append(" and m.auth_status is null ");
			}else{
				sql.append(" and m.auth_status=").append(paramMap.get("auth_status")[0]);
				totalSql.append(" and m.auth_status=").append(paramMap.get("auth_status")[0]);
			}
		}
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			totalSql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		String property = "m.join_time";
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
		List<Record> merchantsList =  Db.find(sql
				.toString());
		
		if (merchantsList.size() > 0) {
			merchantsList.get(0).set("total", total);
			for(int i=0;i<merchantsList.size();i++){
			merchantsList.get(i).set("id", merchantsList.get(i).getLong("id").toString());
			}
		}

		return merchantsList;
	}
	
	
	@Before(Tx.class)
	public Boolean editMerchants(Map<String, String[]> param) {
		boolean flag=false;
		String id = param.get("id")[0];
		String name = param.get("name")[0];
		String telephone = param.get("telephone")[0];
		String withdraw = param.get("withdraw")[0];
		String withdraw_no = param.get("withdraw_no")[0];
		String address = param.get("address")[0];
		String app_type = param.get("app_type")[0];
		String service_type = param.get("service_type")[0];
//		String sql = "update merchant_contact set telephone=? where merchant_id=?";
//		String serviceSql = "select merchant_id from merchant_service_type where merchant_id=? and service_type_id=? and app_type=?";
//		if (!service_type.equals("") && !app_type.equals("")) {
//			int size = Db.find(serviceSql, id, service_type, app_type).size();
//			if (size == 0) {
//				// 更新商户服务类型表
//				String addservice = "insert into merchant_service_type values("
//						+ id + "," + service_type + ",'" + app_type + "')";
//				Db.update(addservice);
//
//			}
//		}
//		if (!withdraw_no.equals("") && !withdraw.equals("")&& !app_type.equals("")) {
//
//			int size = Db
//					.find("select id from merchant_withdraw where merchant_id=? and app_type=? and withdraw=? and withdraw_no=?",
//							id,app_type,withdraw, withdraw_no).size();
//			if (size == 0) {
//				 Db.update("insert into merchant_withdraw(merchant_id,withdraw,withdraw_no,app_type) values(?,?,?,?)",id,withdraw,withdraw_no,app_type);
//			}
//		}
		String sql="select id from merchant_info where name=? and app_type=?";
		List<Record> lis=Db.find(sql,name,app_type);
		if(lis!=null&&lis.size()>0){
			String newid=lis.get(0).getLong("id").toString();
			if(!newid.equals(id)){
				return flag;
			}
		}
		
		MerchantsInfo merchantInfo=new MerchantsInfo();
		merchantInfo.findById(id).set("name", name).update();
//		Db.update(sql, telephone, id);
		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO, id);
		flag=true;
		return flag;
	}

	public List<Record> getEmployeeList(Map<String, String[]> paramMap) {
		if(paramMap.get("province")[0].equals("")&&paramMap.get("city")[0].equals("")
				&&paramMap.get("name")[0].equals("")&&paramMap.get("merchantsid")[0].equals("")
				&&paramMap.get("agentId")[0].equals("")){
			return getEmployeeConsiList(paramMap);
		}
		
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select m.name as merchants_name,e.id,e.merchant_id,e.name,e.phone,e.employees_type,e.join_time,e.password,e.verification_code,e.verification_time,e.verification_status,e.last_login_time,e.app_type from merchant_employees e INNER JOIN merchant_info m on m.id=e.merchant_id where e.is_del=0 and ");
		totalSql.append("select count(1) as total from merchant_employees e INNER JOIN merchant_info m on m.id=e.merchant_id where e.is_del=0 and ");
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		strfilter.put("e.phone", paramMap.get("phone"));
		strfilter.put("m.name", paramMap.get("name"));
		strfilter.put("e.employees_type", paramMap.get("employees_type"));
		strfilter.put("m.province", paramMap.get("province"));
		intfilter.put("m.id", paramMap.get("merchantsid"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
				totalSql.append(" and m.location_address like '").append(match).append("' ");
			}else{
				strfilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
			totalSql.append(Util.getFilter(strfilter, intfilter));
		}
		
		sql.append(Util.getdateFilter("e.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("e.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			totalSql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "e.join_time";
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
		List<Record> employee = Db.find(sql.toString());
		if (employee.size() > 0) {
			employee.get(0).set("total", total);
			for(int i=0;i<employee.size();i++){
				employee.get(i).set("id", employee.get(i).getLong("id").toString());
				employee.get(i).set("merchant_id", employee.get(i).getLong("merchant_id").toString());
				}
		}

		return employee;
	}

	private List<Record> getEmployeeConsiList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select (select m.name from merchant_info m where m.id=e.merchant_id) as merchants_name,e.id,e.merchant_id,e.name,e.phone,e.employees_type,e.join_time,e.password,e.verification_code,e.verification_time,e.verification_status,e.last_login_time,e.app_type from merchant_employees e where e.is_del=0 and ");
		totalSql.append("select count(1) as total from merchant_employees e where e.is_del=0 and ");
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		strfilter.put("e.phone", paramMap.get("phone"));
		strfilter.put("e.employees_type", paramMap.get("employees_type"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
				totalSql.append(" and m.location_address like '").append(match).append("' ");
			}else{
				strfilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
			totalSql.append(Util.getFilter(strfilter, intfilter));
		}
		sql.append(Util.getdateFilter("e.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("e.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "e.join_time";
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
		List<Record> employee = Db.find(sql.toString());
		if (employee.size() > 0) {
			employee.get(0).set("total", total);
			for(int i=0;i<employee.size();i++){
				employee.get(i).set("id", employee.get(i).getLong("id").toString());
				employee.get(i).set("merchant_id", employee.get(i).getLong("merchant_id").toString());
				}
		}

		return employee;
	}

	public List<Record> getStoreAudit(Map<String, String[]> paramMap) {
		if(paramMap.get("telephone")[0].equals("")){
			return getStoreAuditConciseList(paramMap);
		}else{
			return getStoreAuditList(paramMap);
		}
	}
	
	private List<Record> getStoreAuditConciseList(Map<String, String[]> paramMap) {

		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		sql.append("select a.id,m.id AS merchantId,m.name,a.oper_user,m.app_type,(select name from catalog where alias=m.app_type and level=0) as app_name,m.location_address as address,m.province,m.city,");
		sql.append("(select me.phone from merchant_employees me where me.merchant_id=a.merchant_id and me.is_del=0 and me.employees_type=1 limit 0,1) AS telephone,");
		sql.append("a.auth_type, a.auth_status,a.path,a.join_time,a.auth_time,a.remark,");
		sql.append(" '' as auth_total ");
		sql.append(" FROM merchant_auth a INNER JOIN merchant_info m ON m.id = a.merchant_id ");
		sql.append(" where m.is_del=0 and ");
		if(paramMap.get("name")[0].equals("")&&paramMap.get("province")[0].equals("")&&paramMap.get("app_type")[0].equals("")
				&&paramMap.get("invite_code")[0].equals("")){
			totalSql.append("select count(1) as total from merchant_auth a where  ");
		}else{
			totalSql.append("select count(1) as total from merchant_auth a inner join merchant_info m on m.id=a.merchant_id where m.is_del=0 and ");
		}
		strfilter.put("m.name", paramMap.get("name"));
		strfilter.put("m.province", paramMap.get("province"));
		strfilter.put("m.app_type", paramMap.get("app_type"));
		strfilter.put("m.invitation_code", paramMap.get("invite_code"));
		intfilter.put("a.auth_status", paramMap.get("auth_status"));
		intfilter.put("a.auth_type", paramMap.get("auth_type"));
		strfilter.put("a.remark", paramMap.get("remark"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
				totalSql.append(" and m.location_address like '").append(match).append("' ");
			}else{
				strfilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
			totalSql.append(Util.getFilter(strfilter, intfilter));
		}
		
		sql.append(Util.getdateFilter("a.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("a.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			totalSql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		String property = "a.join_time";
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
		if(property.equals("join_time")){
			property="a.join_time";
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	
		List<Record> storeList = Db.find(sql.toString());
		if (storeList.size() >0) {
				storeList.get(0).set("total", total);
				String pic = "";
				String appType="";
				String appName="";
				for(int i=0;i<storeList.size();i++){
					pic = storeList.get(i).getStr("path");
					appType=storeList.get(i).getStr("app_type");
					if(!StringUtil.isNullStr(pic)){
						pic = BusinessUtil.getFileUrl(pic);
						storeList.get(i).set("path",pic);
					}
//					if(!StringUtil.isNullStr(appType)){
//						appName = (String) Constant.merchantAppInfoMap.get(appType);
//						storeList.get(i).set("app_name",appName);
//					}
					
					storeList.get(i).set("merchantId", storeList.get(i).getLong("merchantId").toString());
					String countSql="select count(1) as auth_total from merchant_auth ma where ma.merchant_id=?";
					long auth_total = Db.find(countSql,storeList.get(i).getStr("merchantId")).get(0).getLong("auth_total");
					storeList.get(i).set("auth_total", auth_total);
				}
		}
	
		return storeList;
	}

	private List<Record> getStoreAuditList(Map<String, String[]> paramMap) {

		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		sql.append("select a.id,m.id AS merchantId,m.name,a.oper_user,m.app_type,(select name from catalog where alias=m.app_type and level=0) as app_name,m.location_address as address,m.province,m.city,");
		sql.append(" me.phone as telephone,a.auth_type, a.auth_status,a.path,a.join_time,a.auth_time,a.remark,");
		sql.append(" '' as auth_total ");
		sql.append(" FROM merchant_auth a INNER JOIN merchant_info m ON m.id = a.merchant_id ");
		sql.append(" LEFT JOIN  merchant_employees me on me.merchant_id=a.merchant_id and me.is_del=0 and me.employees_type=1 where m.is_del=0 and ");
		totalSql.append("select count(1) as total from merchant_auth a inner join merchant_info m on m.id=a.merchant_id left join merchant_employees me on me.merchant_id=m.id and me.is_del=0 and me.employees_type=1 where m.is_del=0 and ");
		strfilter.put("m.name", paramMap.get("name"));
		strfilter.put("m.province", paramMap.get("province"));
		strfilter.put("m.app_type", paramMap.get("app_type"));
		strfilter.put("m.invitation_code", paramMap.get("invite_code"));
		intfilter.put("a.auth_status", paramMap.get("auth_status"));
		intfilter.put("a.auth_type", paramMap.get("auth_type"));
		strfilter.put("me.phone", paramMap.get("telephone"));
		strfilter.put("a.remark", paramMap.get("remark"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
				totalSql.append(" and m.location_address like '").append(match).append("' ");
			}else{
				strfilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
			totalSql.append(Util.getFilter(strfilter, intfilter));
		}
		
		sql.append(Util.getdateFilter("a.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("a.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			totalSql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		String property = "a.join_time";
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
		if(property.equals("join_time")){
			property="a.join_time";
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<Record> storeList = Db.find(sql.toString());
		if (storeList.size() >0) {
				storeList.get(0).set("total", total);
				String pic = "";
				String appType="";
				String appName="";
				for(int i=0;i<storeList.size();i++){
					pic = storeList.get(i).getStr("path");
					appType=storeList.get(i).getStr("app_type");
					if(!StringUtil.isNullStr(pic)){
						pic = BusinessUtil.getFileUrl(pic);
						storeList.get(i).set("path",pic);
					}
//					if(!StringUtil.isNullStr(appType)){
//						appName = (String) Constant.merchantAppInfoMap.get(appType);
//						storeList.get(i).set("app_name",appName);
//					}
//					
					storeList.get(i).set("merchantId", storeList.get(i).getLong("merchantId").toString());
					String countSql="select count(1) as auth_total from merchant_auth ma where ma.merchant_id=?";
					long auth_total = Db.find(countSql,storeList.get(i).getStr("merchantId")).get(0).getLong("auth_total");
					storeList.get(i).set("auth_total", auth_total);
				}
		}
		return storeList;
	}

	@Before(Tx.class)
	public Boolean AuditStore(Map<String, String[]> param,String operName) {
		boolean flag = false;
		Long id = StringUtil.nullToLong(param.get("id")[0]);
		String phone = StringUtil.null2Str(param.get("telephone")[0]);
		final Long merchantId = StringUtil.nullToLong(param.get("merchantId")[0]);
		String merchantName = StringUtil.null2Str(param.get("name")[0]);
		final String appType = StringUtil.null2Str(param.get("app_type")[0]);
		final Record r = Db.findById("merchant_auth", id);
		if(r.getInt("auth_status")!=2){
		   return flag;
		}
		r.set("auth_status", 1).set("auth_time", new Date()).set("oper_user", operName);
		Db.update("merchant_auth", r);
		
		Db.update("update merchant_info set auth_status=1 where id=?", merchantId);
//		String sql = "update merchant_auth set auth_status=1,auth_time='"+ DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date())+"'  where id="+id;
//		Db.update(sql);
		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO, StringUtil.null2Str(merchantId));
		MessageUtil.createCustomerMessage(null, 1, 1, merchantId, "服务商认证", "恭喜您，认证通过。"); // 生成消息
//		String appName = StringUtil.null2Str(Constant.merchantAppInfoMap.get(appType));
//		StringBuffer smsContent = new StringBuffer();
//		smsContent.append("恭喜您,您在【").append(appName).append("】中的店铺");
//		if(!StringUtil.isNullStr(merchantName)){
//			smsContent.append("【").append(merchantName).append("】");
//		}
//		smsContent.append("已成功通过O盟服务商认证审核！");
//		SmsUtil.sendSms(phone, smsContent.toString());
		new Thread(new Runnable() {			
			@Override
			public void run() {	
				try{
				  //  PushUtil.pushAuditMsg(merchantId, appType, 1);
					String img = r.getStr("path");
					if(!StringUtil.isNullStr(img)){
						img = BusinessUtil.getFileUrl(img);
						ImageMarkUtil.waterMarkImage(img,  0, 0,0, 0.9f);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();

		
		try {
			Map<String,Object> mqMsg = new HashMap<String,Object>();
			String sql = "SELECT t.user_id,m.location_address AS address FROM merchant_employees t LEFT JOIN merchant_info m ON t.merchant_id=m.id WHERE t.is_del=0 AND t.employees_type=1 AND t.merchant_id=?";
			List<Record> user = Db.find(sql,merchantId);
			if(user!=null&&user.size()>0){
				mqMsg.put("userId", user.get(0).getLong("user_id"));
				mqMsg.put("merchantId", merchantId);
				mqMsg.put("merchantName", merchantName);
				mqMsg.put("merchantAddress", user.get(0).getStr("address"));
				mqMsg.put("authType", r.getInt("auth_type"));
				if(appType.equals("gxfw")){
					mqMsg.put("merchantType", "0");
				}else{
					mqMsg.put("merchantType", "1");
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				mqMsg.put("authTime",sdf.format(new Date()));
				MqUtil.writeToMQ("cplanAuthQueue","cplanAuthExchange",JSONObject.toJSONString(mqMsg));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		flag=true;
		return flag;
	}

	public Boolean deleteStore(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		String[] phones = param.get("phone")[0].split(",");
		String[] appTypes = param.get("appType")[0].split(",");
		for(int i=0;i<ids.length;i++){	
			MerchantsInfo.dao.findById(ids[i]).set("is_del", 1).update();
			String sql = "update merchant_employees set is_del=1 where merchant_id=?";
			Db.update(sql, ids[i]);
			CommonUtil.flushMerchant(phones[i], appTypes[i], ids[i]);
		}
		flag=true;
		return flag;
	}

	public Boolean deleteEmployee(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			String sql="update merchant_employees set is_del=1 where id=?";
			Db.update(sql,ids[i]);
		}
		flag=true;
		return flag;
	}

	public Boolean AuditAllEmployee(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			String sql = "update merchant_employees set verification_status=1 where id="
					+ ids[i];
			Db.update(sql);
		}
		flag=true;
		return flag;
	}

	public List<MerchantsInfo> getExportMerchantsList(Map<String, String[]> paramMap) {
		if(paramMap.get("telephone")[0].equals("")){
			return getExportMerchantsConciseList(paramMap);
		}else{
			
		StringBuffer sql = new StringBuffer();
		int pageNumber = PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT_MER;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("m.name", paramMap.get("name"));
		strFilter.put("m.province", paramMap.get("province"));
		strFilter.put("m.app_type", paramMap.get("app_type"));
		strFilter.put("m.invitation_code", paramMap.get("invite_code"));
		strFilter.put("me.phone", paramMap.get("telephone"));
		intFilter.put("m.auth_type", paramMap.get("auth_type"));
		sql.append(" select m.id,m.name,DATE_FORMAT(m.join_time, '%Y-%m-%d %H:%i:%s') as join_time,m.location_address as address,m.vip_level,m.province,m.city,m.longitude,m.latitude,m.app_type,m.invitation_code,me.phone as telephone,(select name from catalog where alias=m.app_type and level=0) as app_name,(case m.auth_status when 0 then '未通过' when 1 then '已认证' when 2 then '待认证' when 3 then '已取消' else '空' end) as auth_status,"
				+"(select count(DISTINCT mp.vouchers_id) from merchant_vouchers_permissions mp  where mp.merchant_id=m.id and mp.is_del=0) as vouchers,"

				+ " (select count(me.id) from merchant_employees me where me.merchant_id=m.id and me.is_del=0) as employee,"
				+ "	(CASE m.auth_type WHEN 1 THEN '企业认证' WHEN 2 THEN '个人认证' ELSE '未认证' END) AS auth_type, "
				+"(select (CASE ma.auth_type WHEN 2 THEN '个人认证' ELSE '' END) AS auth_type from merchant_auth ma where ma.auth_status=1 and ma.auth_type=2 and ma.merchant_id=m.id limit 0,1) as perAuth "
				+ "from merchant_info m  left join merchant_employees me on me.merchant_id=m.id and me.is_del=0 and me.employees_type=1 where m.is_del=0 and ");
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strFilter, intFilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
			
			}else{
				strFilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strFilter, intFilter));
			}
		}else{
			sql.append(Util.getFilter(strFilter, intFilter));
		}
		
		sql.append(Util.getExportdateFilter("m.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		
		if(!paramMap.get("auth_status")[0].equals("")){
			if(paramMap.get("auth_status")[0].equals("null")){
				sql.append(" and m.auth_status is null ");
			}else{
				sql.append(" and m.auth_status=").append(paramMap.get("auth_status")[0]);
			}
		}
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			
		}
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<MerchantsInfo> merchantsList = MerchantsInfo.dao.find(sql
				.toString());
		return merchantsList;
		}
	}

	private List<MerchantsInfo> getExportMerchantsConciseList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber = PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT_MER;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("m.name", paramMap.get("name"));
		strFilter.put("m.province", paramMap.get("province"));
		strFilter.put("m.app_type", paramMap.get("app_type"));
		strFilter.put("m.invitation_code", paramMap.get("invite_code"));
		intFilter.put("m.auth_type", paramMap.get("auth_type"));
		
		sql.append(" select m.id,m.name,DATE_FORMAT(m.join_time, '%Y-%m-%d %H:%i:%s') as join_time,m.location_address as address,m.vip_level,m.province,m.city,m.app_type,m.invitation_code,m.longitude,m.latitude,(select name from catalog where alias=m.app_type and level=0) as app_name,"
				+"(case m.auth_status when 0 then '未通过' when 1 then '已认证' when 2 then '待认证' when 3 then '已取消' else '空' end) as auth_status, "
				+"(case m.auth_type WHEN 1 THEN '企业认证' WHEN 2 THEN '个人认证' ELSE '未认证' end) as auth_type, "
				+"(select me.phone from merchant_employees me where me.merchant_id=m.id and me.is_del=0 and me.employees_type=1) as telephone,  "
				
				+"(select count(DISTINCT mp.vouchers_id) from merchant_vouchers_permissions mp  where mp.merchant_id=m.id and mp.is_del=0) as vouchers,"
				+ " (select count(me.id) from merchant_employees me where me.merchant_id=m.id and me.is_del=0) as employee,"
				
				+"(select (CASE ma.auth_type WHEN 2 THEN '个人认证' ELSE '' END) AS auth_type from merchant_auth ma where ma.auth_status=1 and ma.auth_type=2 and ma.merchant_id=m.id limit 0,1) as perAuth "
				+ "from merchant_info m where m.is_del=0 and ");
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strFilter, intFilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
			
			}else{
				strFilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strFilter, intFilter));
			}
		}else{
			sql.append(Util.getFilter(strFilter, intFilter));
		}
		sql.append(Util.getExportdateFilter("m.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!paramMap.get("auth_status")[0].equals("")){
			if(paramMap.get("auth_status")[0].equals("null")){
				sql.append(" and m.auth_status is null ");
			}else{
				sql.append(" and m.auth_status=").append(paramMap.get("auth_status")[0]);
			}
		}
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			
		}
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<MerchantsInfo> merchantsList = MerchantsInfo.dao.find(sql
				.toString());
		return merchantsList;
	}

	public List<MerchantsInfo> getexportEmployeeList(Map<String, String[]> paramMap) {
		if(paramMap.get("province")[0].equals("")&&paramMap.get("city")[0].equals("")
				&&paramMap.get("name")[0].equals("")&&paramMap.get("merchantsid")[0].equals("")
				&&paramMap.get("agentId")[0].equals("")){
			return getexportEmployeeConsiList(paramMap);
		}
		
		
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append("select m.name as merchants_name,e.id,e.merchant_id,e.name,e.phone,(case e.employees_type when 1 then '老板' when 2 then '普通员工' when 3 then '财务' else '' end) as employees_type,DATE_FORMAT(e.join_time, '%Y-%m-%d %H:%i:%s') as join_time,e.password,e.verification_code,e.verification_time,e.verification_status,e.last_login_time,e.app_type from merchant_employees e INNER JOIN merchant_info m on m.id=e.merchant_id where e.is_del=0 and ");
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		strfilter.put("m.province", paramMap.get("province"));
		strfilter.put("e.employees_type", paramMap.get("employees_type"));
		
		//strfilter.put("m.city", paramMap.get("city"));
		strfilter.put("m.name", paramMap.get("name"));
		strfilter.put("e.phone", paramMap.get("phone"));
		intfilter.put("m.id", paramMap.get("merchantsid"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("'  ");
			}else{
				strfilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			   sql.append(Util.getFilter(strfilter, intfilter));
		}
		
		sql.append(Util.getExportdateFilter("e.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
					sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		
		String property = "e.join_time";
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
		List<MerchantsInfo> employee = MerchantsInfo.dao.find(sql.toString());

		return employee;
	}

	private List<MerchantsInfo> getexportEmployeeConsiList(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append("select (select m.name from merchant_info m where m.id=e.merchant_id) as merchants_name,e.id,e.merchant_id,e.name,e.phone,(case e.employees_type when 1 then '老板' when 2 then '普通员工' when 3 then '财务' else '' end) as employees_type,DATE_FORMAT(e.join_time, '%Y-%m-%d %H:%i:%s') as join_time,e.password,e.verification_code,e.verification_time,e.verification_status,e.last_login_time,e.app_type from merchant_employees e  where e.is_del=0 and ");
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		strfilter.put("e.employees_type", paramMap.get("employees_type"));
		strfilter.put("e.phone", paramMap.get("phone"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("'  ");
			}else{
				strfilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			   sql.append(Util.getFilter(strfilter, intfilter));
		}
		
		sql.append(Util.getExportdateFilter("e.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		String property = "e.join_time";
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
		List<MerchantsInfo> employee = MerchantsInfo.dao.find(sql.toString());

		return employee;
	}

	public List<WithDraw> getMerchantsWithDraw(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("mi.name", paramMap.get("merchants_name"));
		sql.append("select m.id,m.merchant_id,m.withdraw_price,m.withdraw_time,withdraw_status,m.withdraw,(select dict_value from dictionary where id=m.withdraw) as withdraw_name,m.withdraw_no,m.remark,m.audit_name,mi.name as merchants_name,mi.province,mi.city,");
		sql.append("(select me.phone from merchant_employees me where me.merchant_id=m.merchant_id and me.is_del=0 and me.employees_type=1 limit 0,1) as telephone,(select mw.real_name from merchant_withdraw mw where mw.merchant_id=m.merchant_id limit 0,1) as real_name,(select mw.ID_No from merchant_withdraw mw where mw.merchant_id=m.merchant_id limit 0,1) as ID_No ");
		sql.append(" from merchant_apply_withdraw_record m INNER JOIN merchant_info mi on mi.id=m.merchant_id where mi.is_del=0 and m.is_del=0 and  ");
		totalSql.append("select count(1) as total from merchant_apply_withdraw_record m INNER JOIN merchant_info mi on mi.id=m.merchant_id where mi.is_del=0 and m.is_del=0 and ");
		intFilter.put("m.withdraw_status", paramMap.get("withdraw_status"));
		strFilter.put("mi.province", paramMap.get("province"));
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" mi.location_address like '").append(match).append("' and ");
				totalSql.append(" mi.location_address like '").append(match).append("' and ");
			}else{
				strFilter.put("mi.city", paramMap.get("city"));
			}
		}
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("m.withdraw_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("m.withdraw_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and mi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			totalSql.append(" and mi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		
		long total = WithDraw.dao.find(totalSql.toString()).get(0).getLong("total");
		String property = "m.id";
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
		List<WithDraw> withDrawList = WithDraw.dao.find(sql
				.toString());

		if (withDrawList.size() > 0) {
			withDrawList.get(0).setTotal(total);
			for(int i=0;i<withDrawList.size();i++){
				withDrawList.get(i).set("merchant_id", withDrawList.get(i).getLong("merchant_id").toString());
				}
		}
		return withDrawList;
	}
	public Boolean AuditAllWithDraw(Map<String, String[]> param, String operUserName) {
		boolean flag = false;
		String id = param.get("id")[0];
		String remark=param.get("remark")[0];
        String status=param.get("withdraw_status")[0];
        String merchant_id=param.get("merchant_id")[0];
        String withdraw_price=param.get("withdraw_price")[0];
        Record record = Db.findById("merchant_apply_withdraw_record", id);
        if(record!=null&&record.getInt("withdraw_status")!=2){
        	return flag;
        }
        if(record!=null){
        record.set("withdraw_status", status).set("remark", remark).set("audit_name", operUserName);
        Db.update("merchant_apply_withdraw_record", record);
        String content="";
        if("1".equals(status)){
        	content="提现成功。";
        }else if("0".equals(status)){
        	String sql="update merchant_statistics set total_withdraw_price=total_withdraw_price-?,surplus_price=surplus_price+? where merchant_id=?";
        	Db.update(sql, withdraw_price,withdraw_price,merchant_id);
        	if(!StringUtil.isNullStr(remark)){
               	content="提现失败("+remark+")。";
        	}else{
        	 	content="提现失败。";
        	}
        }
        MessageUtil.createCustomerMessage(null, 1, 1, record.getLong("merchant_id"), "申请提现", content);
		flag=true;
        }
		return flag;
	}

	public List<WithDraw> getexportWithDraw(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("mi.name", paramMap.get("merchants_name"));
		sql.append("select m.id,m.merchant_id,m.withdraw_price,DATE_FORMAT(m.withdraw_time, '%Y-%m-%d %H:%i:%s') as withdraw_time,(case m.withdraw_status when 0 then '已拒绝' when 1 then '已完成' when 2 then '待审核' else '' end) as withdraw_status,m.withdraw,(select dict_value from dictionary where id=m.withdraw) as withdraw_name,m.withdraw_no,m.remark,m.audit_name,mi.name as merchants_name,mi.province,mi.city,");
		sql.append("(select me.phone from merchant_employees me where me.merchant_id=m.merchant_id and me.is_del=0 and me.employees_type=1 limit 0,1) as telephone,(select mw.real_name from merchant_withdraw mw where mw.merchant_id=m.merchant_id limit 0,1) as real_name,(select mw.ID_No from merchant_withdraw mw where mw.merchant_id=m.merchant_id limit 0,1) as ID_No ");
		sql.append(" from merchant_apply_withdraw_record m INNER JOIN merchant_info mi on mi.id=m.merchant_id where m.is_del=0 and mi.is_del=0 and ");
		intFilter.put("m.withdraw_status", paramMap.get("withdraw_status"));
		strFilter.put("mi.province", paramMap.get("province"));
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" mi.location_address like '").append(match).append("' and ");
			}else{
				strFilter.put("mi.city", paramMap.get("city"));
			}
		}
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getExportdateFilter("m.withdraw_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and mi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			}
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<WithDraw> withDrawList = WithDraw.dao.find(sql
				.toString());
		return withDrawList;
	}

	public Boolean saveEmployee(Map<String, String[]> param) {
		boolean isUpdate = false; // 标识是否是更新
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String employeeName = "";
		if(StringUtil.isNotNullMap(param,"employeeName")){
			employeeName = StringUtil.null2Str(param.get("employeeName")[0]);
		}
		String employeePhone="";
		if(StringUtil.isNotNullMap(param,"employeePhone")){
			employeePhone = StringUtil.null2Str(param.get("employeePhone")[0]);
		}
		int employeeType = 1;
		if(StringUtil.isNotNullMap(param,"employeeType")){
			employeeType = StringUtil.nullToInteger(param.get("employeeType")[0]);
		}
		boolean flag = false;
		if(isUpdate){
			flag = MerchantsEmployees.dao.findById(id).set("name", employeeName).set("phone", employeePhone).set("employees_type", employeeType).update();
		}else{
			MerchantsEmployees me = new MerchantsEmployees();
			flag = me.set("name", employeeName).set("phone", employeePhone).set("employees_type", employeeType).save();
		}
		return flag;
	}

	public Boolean saveStore(Map<String, String[]> param) {
		boolean isUpdate = false; // 标识是否是更新
		Long merchantId = 0L;
		if(StringUtil.isNotNullMap(param,"merchantId")){
			isUpdate = true;
			merchantId = StringUtil.nullToLong(param.get("merchantId")[0]);
		}
		String name = "";
		if(StringUtil.isNotNullMap(param,"name")){
			name = StringUtil.null2Str(param.get("name")[0]);
		}
		String telephone="";
		if(StringUtil.isNotNullMap(param,"telephone")){
			telephone = StringUtil.null2Str(param.get("telephone")[0]);
		}
		String location_address="";
		if(StringUtil.isNotNullMap(param,"location_address")){
			location_address = StringUtil.null2Str(param.get("location_address")[0]);
		}
		String app_type="";
		if(StringUtil.isNotNullMap(param,"app_type")){
			app_type = StringUtil.null2Str(param.get("app_type")[0]);
		}
		boolean flag = false;
		String updatePhone = "UPDATE merchant_contact SET telephone='"+telephone+"' WHERE merchant_id="+merchantId;
		
		if(isUpdate){
			MerchantsInfo.dao.findById(merchantId).set("name", name).set("location_address", location_address).update();
			Db.update(updatePhone);
			flag = true;
			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO, StringUtil.null2Str(merchantId),app_type);
		}else{
			MerchantsInfo me = new MerchantsInfo();
			me.set("name", name).set("location_address", location_address).save();
			flag =true;
		}
		return flag;
	}

	@Override
	public Boolean RefuseAuditStore(Map<String, String[]> param,String operName) {
		boolean flag = false;
		Long id = StringUtil.nullToLong(param.get("id")[0]);
		String phone = StringUtil.null2Str(param.get("telephone")[0]);
		Long merchantId = StringUtil.nullToLong(param.get("merchantId")[0]);
		String merchantName = StringUtil.null2Str(param.get("name")[0]);
		String appType = StringUtil.null2Str(param.get("app_type")[0]);
		String remark = StringUtil.null2Str(param.get("remark")[0]);
		final Record r = Db.findById("merchant_auth", id);
		if(r.getInt("auth_status")!=2){
			   return flag;
		  }
		r.set("auth_status", 0).set("auth_time", new Date()).set("remark", remark).set("oper_user", operName);
		Db.update("merchant_auth", r);
		
		Db.update("update merchant_info set auth_status=0 where id=?", merchantId);
//		String sql = "update merchant_auth set auth_status=0,remark=?,auth_time='"+ DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date())+"'  where id="+id;
//		Db.update(sql,remark);
		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO, StringUtil.null2Str(merchantId));
		MessageUtil.createCustomerMessage(null, 1, 1, merchantId, "服务商认证", "很抱歉，您的认证信息未通过。认证不通过原因："+remark);
//		String appName = StringUtil.null2Str(Constant.merchantAppInfoMap.get(appType));
//		StringBuffer smsContent = new StringBuffer();
//		smsContent.append("您好,您在【").append(appName).append("】中的店铺");
//		if(!StringUtil.isNullStr(merchantName)){
//			smsContent.append("【").append(merchantName).append("】");
//		}
//		smsContent.append("认证资料未通过审核，原因：").append(remark).append("，为了您能更好的接单，请重新上传！");
//		SmsUtil.sendSms(phone, smsContent.toString());
       // PushUtil.pushAuditMsg(merchantId, appType, 0);
		flag=true;
		return flag;
	}

	@Override
	public List<Record> getServiceTypeById(Map<String, String[]> param) {
 		String id = param.get("merchantId")[0];
		String sql="select s.service_type_name from service_type s,merchant_service_type m where s.service_type_id=m.service_type_id and s.app_type=m.app_type and m.merchant_id=?";
	    String totalSql="select count(1) as total from service_type s,merchant_service_type m where s.service_type_id=m.service_type_id and s.app_type=m.app_type and m.merchant_id=?";
	    long total = Db.find(totalSql.toString(),id).get(0).getLong("total");
	    List<Record> serviceType=Db.find(sql,id);
	    if (serviceType.size() > 0) {
	    	serviceType.get(0).set("total",total);
		}
	    return serviceType;
	}

	@Override
	public Map<String, Object> merchantDetail(Long merchantId) {
		//认证状态 0-未通过 1-已通过 2-验证中
		Map<String, Object> resMap = new HashMap<String,Object>();
		StringBuffer sqlBasic = new StringBuffer();
		sqlBasic.append(" SELECT mi.id,mi.`name`,mi.detail,mi.province,mi.city,mi.location_address AS address, ");
		sqlBasic.append(" mi.app_type AS appType,mi.max_employee_num as maxEmployeeNum,(select name from catalog where alias=mi.app_type and level=0) as appName,mc.telephone ");
		sqlBasic.append(" FROM merchant_info mi LEFT JOIN merchant_contact mc ON mi.id=mc.merchant_id ");
		sqlBasic.append(" WHERE mi.id=").append(merchantId).append(" AND mi.is_del=0 ");
		List<Record> basicList = Db.find(sqlBasic.toString());
		if(basicList!=null&&basicList.size()>0){
			Record basicRecord = basicList.get(0);
			String sql="select * from merchant_auth mau where mau.merchant_id=? order by mau.auth_time DESC limit 1";
			List<Record> lis=Db.find(sql,merchantId);
			
			resMap.put("merchantId",  basicRecord.getLong("id"));
			resMap.put("merchantName",StringUtil.null2Str(basicRecord.getStr("name")));
			resMap.put("detail",StringUtil.null2Str(basicRecord.getStr("detail")));
			resMap.put("province", StringUtil.null2Str(basicRecord.getStr("province")));
			resMap.put("city",StringUtil.null2Str(basicRecord.getStr("city")));
			resMap.put("address",StringUtil.null2Str(basicRecord.getStr("address")));
			resMap.put("appName",StringUtil.null2Str(basicRecord.getStr("appName")));
			resMap.put("appType",StringUtil.null2Str(basicRecord.getStr("appType")));
			resMap.put("telephone",StringUtil.null2Str(basicRecord.getStr("telephone")));
			resMap.put("maxEmployeeNum",StringUtil.nullToInteger(basicRecord.getInt("maxEmployeeNum")));
			if(lis!=null&&lis.size()>0){
			String pic =StringUtil.null2Str(lis.get(0).getStr("path"));
			if(!StringUtil.isNullStr(pic)){
				pic = BusinessUtil.getFileUrl(pic);
			}
			resMap.put("authPath", pic);
			resMap.put("authStatus",StringUtil.nullToInteger(lis.get(0).getInt("auth_status")));
			}else{
				resMap.put("authPath", "");
				resMap.put("authStatus","");
			}
		}
		
		//加上服务商抢单相关信息
		StringBuffer sqlGrab = new StringBuffer();
		sqlGrab.append("select (select count(1) from push_merchant_order po where po.merchant_id=?) as pushCount,ms.grab_frequency as grabFrequency,ms.service_frequency as serviceFrequency,ms.order_surplus_price as orderSurplusPrice,ms.total_count_evaluation as totalCountEvaluation "
				     + " from merchant_statistics ms where ms.merchant_id=?");
		List<Record> grabList = Db.find(sqlGrab.toString(),merchantId,merchantId);
		if(grabList!=null&&grabList.size()>0){
			Record grabMerchant = grabList.get(0);
			resMap.put("grabFrequency",  StringUtil.nullToInteger(grabMerchant.getInt("grabFrequency")));
			resMap.put("serviceFrequency",  StringUtil.nullToInteger(grabMerchant.getInt("serviceFrequency")));
			resMap.put("totalCountEvaluation", StringUtil.nullToInteger(grabMerchant.getInt("totalCountEvaluation")));
			resMap.put("orderSurplusPrice", StringUtil.nullToBigDe(grabMerchant.getBigDecimal("orderSurplusPrice")));
			resMap.put("pushCount", StringUtil.nullToInteger(grabMerchant.getLong("pushCount")));
		}
		StringBuffer sqlValueAdded = new StringBuffer();
		sqlValueAdded.append(" SELECT * FROM ( ");
		sqlValueAdded.append(" (SELECT mva.apply_status AS vipStatus FROM merchant_vip_apply mva WHERE mva.merchant_id=").append(merchantId).append(") a,");
		sqlValueAdded.append(" (SELECT SUM(mea.apply_increase_employee_num) AS adviserNum FROM merchant_employees_num_apply mea WHERE mea.merchant_id=").append(merchantId).append(" AND mea.apply_status=2) b,");
		sqlValueAdded.append(" (SELECT SUM(mta.topup_money) AS pushFee FROM merchant_topup_apply mta WHERE mta.merchant_id=").append(merchantId).append(" AND mta.apply_status=2) c)");
		List<Record> valuedAddedList = Db.find(sqlValueAdded.toString());
		int vipStatus = 0;
		int adviserNum = 0;
        if(valuedAddedList!=null&&valuedAddedList.size()>0){
        	Record valuedAddedRecord = valuedAddedList.get(0);
        	vipStatus = StringUtil.nullToInteger(valuedAddedRecord.getInt("vipStatus")); // 申请vip 
        	adviserNum = StringUtil.nullToInteger(valuedAddedRecord.getInt("adviserNum")); // 申请顾问号数
        	resMap.put("pushFee", StringUtil.nullToBigDe(valuedAddedRecord.getBigDecimal("pushFee"))); // 申请订单推送金额
        }
    	resMap.put("vipStatus", vipStatus);
		resMap.put("adviserNum",adviserNum );
		StringBuffer sqlEmployee = new StringBuffer();
		sqlEmployee.append("SELECT t.phone,t.`name` FROM merchant_employees t WHERE t.is_del=0 AND t.employees_type!=1 and t.merchant_id=").append(merchantId);
		List<Record> employeeList = Db.find(sqlEmployee.toString());
		List<Map<String,Object>> empList = new ArrayList<Map<String,Object>>();
		int employeeNum = 0;
		if(employeeList!=null&&employeeList.size()>0){
			for(Record r : employeeList){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("name",StringUtil.null2Str(r.getStr("name")));
				map.put("phone",StringUtil.null2Str(r.getStr("phone")));
				empList.add(map);
				employeeNum = empList.size();
			}
		}
		resMap.put("employeeNum", employeeNum); // 员工数量
		resMap.put("employeeList", empList);
		StringBuffer sqlAlbum = new StringBuffer();
		sqlAlbum.append("SELECT t.id AS albumId,t.album_name AS albumName FROM merchant_album t WHERE t.is_del=0 AND t.merchant_id=").append(merchantId);
		List<Record> albumList = Db.find(sqlAlbum.toString());
		int albumNum = 0;  // 相册数
		List<Map<String,Object>> albList = new ArrayList<Map<String,Object>>();
		if(albumList!=null&&albumList.size()>0){
			for(Record r : albumList){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("albumId", r.getLong("albumId"));
				map.put("albumName", StringUtil.null2Str(r.getStr("albumName")));
				albList.add(map);
				albumNum = albList.size();
			}
		}
		resMap.put("albumNum", albumNum);
		resMap.put("albumList", albList);
		StringBuffer sqlServiceType = new StringBuffer();
		sqlServiceType.append(" select s.service_type_name AS serviceName from service_type s,merchant_service_type m ");
		sqlServiceType.append(" where s.id=m.service_type_id and m.merchant_id=").append(merchantId);
		List<Record> serviceTypeList = Db.find(sqlServiceType.toString());
		int serviceNum = 0; // 服务类型数
		List<Map<String,Object>> serviceList = new ArrayList<Map<String,Object>>();
		if(serviceTypeList!=null&&serviceTypeList.size()>0){
			for(Record r : serviceTypeList){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("serviceName",StringUtil.null2Str(r.getStr("serviceName")));
				serviceList.add(map);
				serviceNum = serviceList.size();
			}
		}
		resMap.put("serviceNum", serviceNum);
		resMap.put("serviceTypeList", serviceList);
		return resMap;
	}

	@Override
	public List<String> merchantPicList(Long albumId) {
		List<String> resList = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.path FROM merchant_photo t WHERE t.is_del=0 AND t.album_id=").append(albumId);
		List<Record> list = Db.find(sql.toString());
		if(list!=null&&list.size()>0){
			for(Record r : list){
				String pic = StringUtil.null2Str(r.getStr("path"));
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					resList.add(pic);
				}
			}
		}
		return resList;
	}

	@Override
	public List<MerchantsInfo> getExportStoreAuditExcel(
			Map<String, String[]> paramMap) {
		
		if(paramMap.get("telephone")[0].equals("")){
			return getExportStoreAuditExcelConcise(paramMap);
		}else{
			return getExportStoreAuditExcelList(paramMap);
		}
	}

	
	private List<MerchantsInfo> getExportStoreAuditExcelList(
			Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT_MER;
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		sql.append("select a.id,m.id AS merchantId,m.name,m.app_type,m.invitation_code,(select name from catalog where alias=m.app_type and level=0) as app_name,m.location_address as address,m.province,m.city,");
		sql.append(" me.phone as telephone,(case a.auth_type when 1 then '企业认证' when 2 then '个人认证' else '' end) as authType, (case a.auth_status when 0 then '未通过' when 1 then '已认证' when 2 then '待认证' when 3 then '已取消' else '' end) as authStatus,a.remark,a.path,DATE_FORMAT(a.join_time, '%Y-%m-%d %H:%i:%s') as join_time,DATE_FORMAT(a.auth_time, '%Y-%m-%d %h:%i:%s') as auth_time, ");
		sql.append(" (select count(1) from merchant_auth ma where ma.merchant_id=a.merchant_id) as auth_total ");
		sql.append(" FROM merchant_auth a INNER JOIN merchant_info m ON m.id = a.merchant_id ");
		sql.append(" LEFT JOIN merchant_employees me on me.merchant_id=m.id and me.is_del=0 and me.employees_type=1 ");
		
		sql.append(" where m.is_del=0  and ");
		
		strfilter.put("m.name", paramMap.get("name"));
		strfilter.put("m.province", paramMap.get("province"));
		strfilter.put("m.app_type", paramMap.get("app_type"));
		strfilter.put("m.invitation_code", paramMap.get("invite_code"));
		strfilter.put("a.auth_type", paramMap.get("auth_type"));
		strfilter.put("a.remark", paramMap.get("remark"));
		strfilter.put("me.phone", paramMap.get("telephone"));
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
			
			}else{
				strfilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
		}
		sql.append(Util.getExportdateFilter("a.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!paramMap.get("auth_status")[0].equals("")){
			if(paramMap.get("auth_status")[0].equals("null")){
				
			}else{
				sql.append(" and a.auth_status=").append(paramMap.get("auth_status")[0]);
			}
		} 
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			}
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<MerchantsInfo> merchantsList = MerchantsInfo.dao.find(sql
				.toString());
		return merchantsList;
	}

	private List<MerchantsInfo> getExportStoreAuditExcelConcise(
			Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT_MER;
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		sql.append("select a.id,m.id AS merchantId,m.name,m.app_type,m.invitation_code,(select name from catalog where alias=m.app_type and level=0) as app_name,m.location_address as address,m.province,m.city,");
		sql.append("(select me.phone from merchant_employees me where me.merchant_id=a.merchant_id and me.is_del=0 and me.employees_type=1 limit 0,1) AS telephone,");
		sql.append("(case a.auth_type when 1 then '企业认证' when 2 then '个人认证' else '' end) as authType, (case a.auth_status when 0 then '未通过' when 1 then '已认证' when 2 then '待认证' when 3 then '已取消' else '' end) as authStatus,a.remark,a.path,DATE_FORMAT(a.join_time, '%Y-%m-%d %H:%i:%s') as join_time,DATE_FORMAT(a.auth_time, '%Y-%m-%d %h:%i:%s') as auth_time, ");
		sql.append(" (select count(1) from merchant_auth ma where ma.merchant_id=a.merchant_id) as auth_total ");
		sql.append(" FROM merchant_auth a INNER JOIN merchant_info m ON m.id = a.merchant_id ");
		sql.append(" where m.is_del=0  and ");
		
		strfilter.put("m.name", paramMap.get("name"));
		strfilter.put("m.province", paramMap.get("province"));
		strfilter.put("m.app_type", paramMap.get("app_type"));
		strfilter.put("m.invitation_code", paramMap.get("invite_code"));
		strfilter.put("a.auth_type", paramMap.get("auth_type"));
		strfilter.put("a.remark", paramMap.get("remark"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and m.location_address like '").append(match).append("' ");
			
			}else{
				strfilter.put("m.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
		}
		sql.append(Util.getExportdateFilter("a.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		if(!paramMap.get("auth_status")[0].equals("")){
			if(paramMap.get("auth_status")[0].equals("null")){
				
			}else{
				sql.append(" and a.auth_status=").append(paramMap.get("auth_status")[0]);
			}
		} 
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and m.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			}
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<MerchantsInfo> merchantsList = MerchantsInfo.dao.find(sql
				.toString());
		return merchantsList;
	}

	public List<MerchantsInfo> getExportStoreExcel(Map<String,Object> paramMap) {
		StringBuffer sql = new StringBuffer();
		boolean flag=false;
		sql.append(" select m.id,m.name,DATE_FORMAT(m.join_time, '%Y-%m-%d %H:%i:%s') as join_time,m.location_address as address,m.vip_level,m.province,m.city,m.app_type,m.invitation_code,m.longitude,m.latitude,(select name from catalog where alias=m.app_type and level=0) as app_name,"
				+"(case m.auth_status when 0 then '未通过' when 1 then '已认证' when 2 then '待认证' when 3 then '已取消' else '空' end) as auth_status, "
				+"(case m.auth_type WHEN 1 THEN '企业认证' WHEN 2 THEN '个人认证' ELSE '未认证' end) as auth_type, "
				+"(select me.phone from merchant_employees me where me.merchant_id=m.id and me.is_del=0 and me.employees_type=1) as telephone,  "
				
				+"(select count(DISTINCT mp.vouchers_id) from merchant_vouchers_permissions mp  where mp.merchant_id=m.id and mp.is_del=0) as vouchers,"
				+ " (select count(me.id) from merchant_employees me where me.merchant_id=m.id and me.is_del=0) as employee,"
				
				+"(select (CASE ma.auth_type WHEN 2 THEN '个人认证' ELSE '' END) AS auth_type from merchant_auth ma where ma.auth_status=1 and ma.auth_type=2 and ma.merchant_id=m.id limit 0,1) as perAuth "
				+ "from merchant_info m where m.is_del=0 and ");
		
		if (paramMap.get("province") != null) {
			sql.append(" m.province='").append(paramMap.get("province")).append("' and ");
			if (StringUtil.matchProvince(paramMap.get("province").toString())) {
				flag = true;
			}
		}

		if (paramMap.get("city") != null) {
			if (flag) {
				String match = "%" + paramMap.get("province") + "市" + paramMap.get("city") + "%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			} else {
				sql.append(" m.city='").append(paramMap.get("city")).append("' and ");
			}
		}
		
		sql.append(" m.join_time>'").append(Util.getExportdateFilter(1)).append("' and m.join_time<'").append(Util.getExportdateFilter(2)).append("'");
		
		List<MerchantsInfo> merchantsList = MerchantsInfo.dao.find(sql
				.toString());
		return merchantsList;
	}

	@Override
	public List<Record> getAuthDetailByMerchantId(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		sql.append("select a.id,m.id AS merchantId,m.name,a.oper_user,m.app_type,'' as app_name,m.location_address as address,m.province,m.city,");
		sql.append(" mc.telephone,a.auth_type, a.auth_status,a.path,a.join_time,a.auth_time,a.remark ");
		sql.append(" FROM merchant_auth a LEFT JOIN merchant_info m ON m.id = a.merchant_id ");
		sql.append(" LEFT JOIN merchant_contact mc ON mc.merchant_id = m.id ");
		sql.append(" where m.is_del=0 and ");
		totalSql.append("select count(1) as total from merchant_info m inner join merchant_auth a on m.id=a.merchant_id left join merchant_contact mc on mc.merchant_id=m.id where m.is_del=0 and ");
		strfilter.put("a.merchant_id", paramMap.get("merchants_id"));
		
		sql.append(Util.getFilter(strfilter, intfilter));
		totalSql.append(Util.getFilter(strfilter, intfilter));
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		String property = "a.join_time";
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
		if(property.equals("join_time")){
			property="a.join_time";
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);

		List<Record> storeList = Db.find(sql.toString());
		if (storeList.size() >0) {
				storeList.get(0).set("total", total);
				String appType="";
				String appName="";
				for(int i=0;i<storeList.size();i++){
					appType=storeList.get(i).getStr("app_type");
					if(!StringUtil.isNullStr(appType)){
						appName = (String) Constant.merchantAppInfoMap.get(appType);
						storeList.get(i).set("app_name",appName);
					}
					storeList.get(i).set("merchantId", storeList.get(i).getLong("merchantId").toString());
				}
		}
	
		return storeList;
	}

	@Override
	public List<Record> getMerchantsInfoListForFensi(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("m.name", paramMap.get("name"));
		strFilter.put("m.province", paramMap.get("province"));
		strFilter.put("m.app_type", paramMap.get("app_type"));
		strLikeFilter.put("me.phone", paramMap.get("telephone"));
		intFilter.put("m.auth_type", paramMap.get("auth_type"));
		
		sql.append(" select m.id,m.name,m.join_time,m.location_address as address,m.province,m.city,m.app_type,me.phone as telephone,(select name from catalog where alias=m.app_type and level=0) as app_name "
				+",(select count(1) from user_merchant_collection umc where umc.merchant_id=m.id) as fensiTotal "
				+ " from merchant_info m left join merchant_employees me on me.merchant_id=m.id and me.is_del=0 and me.employees_type=1 where m.is_del=0 and  ");
		totalSql.append("select count(1) as total from merchant_info m left join merchant_employees me on me.merchant_id=m.id and me.is_del=0 and me.employees_type=1 where m.is_del=0 and ");
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
				totalSql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter.put("m.city", paramMap.get("city"));
			}
		}
		
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("m.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("m.join_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> merchantsList =  Db.find(sql
				.toString());

		if (merchantsList.size() > 0) {
			merchantsList.get(0).set("total", total);
			for(int i=0;i<merchantsList.size();i++){
			merchantsList.get(i).set("id", merchantsList.get(i).getLong("id").toString());
			}
		}

		return merchantsList;
	}

	@Override
	public Boolean addMerchantsInfoForFensi(Map<String, String[]> param) {
		boolean flag=false;
		String merchantId = param.get("id")[0];
		String total = param.get("fensiTotal")[0].equals("")==true ? "0" : param.get("fensiTotal")[0];
		int fensiCount=Integer.parseInt(total);
		if(merchantId.equals("")){
			return flag;
		}
		
		if(fensiCount>0){
			List<Record> phoneList=Db.find("select id from user_info where phone>14000008000 and phone<14000008500 and id not in (select user_id from user_merchant_collection where merchant_id=?) ",merchantId);
			List<String> sqlList=new ArrayList<String>();
			int ss=0;
			if(phoneList!=null){
				if(phoneList.size()>fensiCount){
					ss=fensiCount;
				}else{
					ss=phoneList.size();
				}
			}
			if(ss==0){
				return flag;
			}
			for(int i=0;i<ss;i++){
				Long userId=phoneList.get(i).getLong("id");
				String joinTime=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()); 
				String sql="insert into user_merchant_collection(user_id,merchant_id,join_time) values("+userId+","+merchantId+",'"+joinTime+"')";
				sqlList.add(sql);
			}
			Db.batch(sqlList, sqlList.size());
	  }
		
		flag=true;
		return flag;
	}

	@Override
	public List<Record> getPayDeatilByMerId(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		
		Map<String, String[]> strfilter = new HashMap<String, String[]>();
		Map<String, String[]> intfilter = new HashMap<String, String[]>();
		
		sql.append("select (select order_no from order_info where id=mp.business_id) as order_no,mp.business_id,mp.payment_type,mp.payment_price,mp.payment_time,(select order_pay_type from order_info oi where oi.id=mp.business_id) as order_pay_type from merchant_payment_details mp where ");
		totalSql.append("select count(1) as total from merchant_payment_details mp where ");
		
		strfilter.put("mp.merchant_id", paramMap.get("merchantsid"));
		
		sql.append(Util.getFilter(strfilter, intfilter));
		totalSql.append(Util.getFilter(strfilter, intfilter));
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		String property = "mp.payment_time";
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

		List<Record> storeList = Db.find(sql.toString());
		if (storeList.size() >0) {
			storeList.get(0).set("total", total);
			
		}
	
		return storeList;
	}

}
