package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.OrderDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class OrderDaoImpl implements OrderDao {

	public List<OrderInfo> getOrderList(Map<String, String[]> paramMap) {
		
		if(paramMap.get("service_type_name")[0].equals("")){
			return getConsicOrderList(paramMap);
		}
		
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select oi.*,(select name from merchant_info mi where mi.id=oi.merchant_id) as merchant_name,st.service_type_name as orderTypeName,");
		sql.append("(select phone from user_info where id = oi.user_id) as phone,(select name from user_info where id = oi.user_id) as userName,");
		sql.append(" (select dict_value from dictionary where dict_key = oi.order_status and dict_type ='managerOrderStatusMap' and is_del = 0) as orderStatusName");
		sql.append(" from order_info oi inner join service_type st on st.id=oi.service_type_id where st.is_del=0 and ");
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		totalSql.append("select count(1) as total from order_info oi inner join service_type st on st.id=oi.service_type_id and st.is_del=0 where ");	
		Map<String,String[]> strfilter=new HashMap<String,String[]>();
		Map<String,String[]> intfilter=new HashMap<String,String[]>();
		
		strfilter.put("oi.province", paramMap.get("province"));
		strfilter.put("oi.order_no", paramMap.get("order_id"));	
		intfilter.put("oi.order_pay_type", paramMap.get("order_pay_type"));
		intfilter.put("oi.order_status", paramMap.get("purchase_status"));
		strfilter.put("st.service_type_name", paramMap.get("service_type_name"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and oi.address like '").append(match).append("' ");
				totalSql.append(" and oi.address like '").append(match).append("' ");
			}else{
				strfilter.put("oi.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
			totalSql.append(Util.getFilter(strfilter, intfilter));
		}
		
		sql.append(Util.getdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ))
		    .append(Util.getdateFilter("oi.deal_time", paramMap.get("del_start_time"),paramMap.get("del_off_time") ));
		totalSql.append(Util.getdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ))
		     .append(Util.getdateFilter("oi.deal_time", paramMap.get("del_start_time"),paramMap.get("del_off_time") ));
		
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and oi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			totalSql.append(" and oi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		
		if(!(paramMap.get("phone")[0]==null) && !paramMap.get("phone")[0].equals("")){
			sql.append(" and oi.user_id in (select id from user_info ui where ui.phone=").append(paramMap.get("phone")[0]).append(")");
			totalSql.append(" and oi.user_id in (select id from user_info ui where ui.phone=").append(paramMap.get("phone")[0]).append(")");
		}
		
		if(!(paramMap.get("merchant_name")[0]==null) && !paramMap.get("merchant_name")[0].equals("")){
			sql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id=(select id from merchant_info mi where mi.name='").append(paramMap.get("merchant_name")[0]).append("'))");
			totalSql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select id from merchant_info mi where mi.name='").append(paramMap.get("merchant_name")[0]).append("'))");
		}
		
		if(!(paramMap.get("merchant_phone")[0]==null) && !paramMap.get("merchant_phone")[0].equals("")){
			sql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select merchant_id from merchant_employees me where me.employees_type=1 and me.phone='").append(paramMap.get("merchant_phone")[0]).append("'))");
			totalSql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select merchant_id from merchant_employees me where me.employees_type=1 and me.phone='").append(paramMap.get("merchant_phone")[0]).append("'))");
		}
		
		long total=OrderInfo.dao.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "oi.join_time";
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
		List<OrderInfo> orderList=OrderInfo.dao.find(sql.toString());
		if(orderList.size()>0){
			orderList.get(0).setTotal(total);
			for(int i=0;i<orderList.size();i++){
				if(orderList.get(i).getLong("merchant_id") != null){
				orderList.get(i).set("merchant_id", orderList.get(i).getLong("merchant_id").toString());
				}else{
				orderList.get(i).set("merchant_id","");
				}
				}
		}
		return orderList;
	}

	public List<OrderInfo> getConsicOrderList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select oi.*,(select name from merchant_info mi where mi.id=oi.merchant_id) as merchant_name,");
		sql.append("(select phone from user_info where id = oi.user_id) as phone,(select name from user_info where id = oi.user_id) as userName,");
		sql.append("(select service_type_name from service_type st where st.id=oi.service_type_id) as orderTypeName,");
		sql.append(" (select dict_value from dictionary where dict_key = oi.order_status and dict_type ='managerOrderStatusMap' and is_del = 0) as orderStatusName");
		sql.append(" from order_info oi where ");
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		totalSql.append("select count(1) as total from order_info oi where ");	
		Map<String,String[]> strfilter=new HashMap<String,String[]>();
		Map<String,String[]> intfilter=new HashMap<String,String[]>();
		
		strfilter.put("oi.province", paramMap.get("province"));
		strfilter.put("oi.order_no", paramMap.get("order_id"));	
		intfilter.put("oi.order_status", paramMap.get("purchase_status"));
		intfilter.put("oi.order_pay_type", paramMap.get("order_pay_type"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and oi.address like '").append(match).append("' ");
				totalSql.append(" and oi.address like '").append(match).append("' ");
			}else{
				strfilter.put("oi.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
				totalSql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
			totalSql.append(Util.getFilter(strfilter, intfilter));
		}
		
		sql.append(Util.getdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ))
		.append(Util.getdateFilter("oi.deal_time", paramMap.get("del_start_time"),paramMap.get("del_off_time") ));
		
		totalSql.append(Util.getdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ))
		.append(Util.getdateFilter("oi.deal_time", paramMap.get("del_start_time"),paramMap.get("del_off_time") ));
	
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and oi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			totalSql.append(" and oi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
		}
		
		if(!(paramMap.get("phone")[0]==null) && !paramMap.get("phone")[0].equals("")){
			sql.append(" and oi.user_id in (select id from user_info ui where ui.phone=").append(paramMap.get("phone")[0]).append(")");
			totalSql.append(" and oi.user_id in (select id from user_info ui where ui.phone=").append(paramMap.get("phone")[0]).append(")");
		}
		
		if(!(paramMap.get("merchant_name")[0]==null) && !paramMap.get("merchant_name")[0].equals("")){
			sql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select id from merchant_info mi where mi.name='").append(paramMap.get("merchant_name")[0]).append("'))");
			totalSql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select id from merchant_info mi where mi.name='").append(paramMap.get("merchant_name")[0]).append("'))");
		}
		
		if(!(paramMap.get("merchant_phone")[0]==null) && !paramMap.get("merchant_phone")[0].equals("")){
			sql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select merchant_id from merchant_employees me where me.employees_type=1 and me.phone='").append(paramMap.get("merchant_phone")[0]).append("'))");
			totalSql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select merchant_id from merchant_employees me where me.employees_type=1 and me.phone='").append(paramMap.get("merchant_phone")[0]).append("'))");
		}
		
		long total=OrderInfo.dao.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "oi.join_time";
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
		List<OrderInfo> orderList=OrderInfo.dao.find(sql.toString());
		if(orderList.size()>0){
			orderList.get(0).setTotal(total);
			for(int i=0;i<orderList.size();i++){
				if(orderList.get(i).getLong("merchant_id") != null){
				orderList.get(i).set("merchant_id", orderList.get(i).getLong("merchant_id").toString());
				}else{
				orderList.get(i).set("merchant_id","");
				}
				}
		}
		return orderList;
	}
	
	
	public Boolean editOrder(Map<String, String[]> param) {
		boolean flag=false;
		String id=param.get("id")[0];
		String order_status=param.get("order_status")[0];
		OrderInfo.dao.findById(id).set("order_status", order_status).update();
		flag=true;
		return flag;
	}

	public Boolean deleteOrder(String id) {
		String[] orderIds=id.split(",");
		boolean flag=false;
		for(int i=0;i<orderIds.length;i++){
			 OrderInfo.dao.findById(orderIds[i]).set("is_del", 1).update();
		}
		flag=true;
		return flag;
	}

	public List<OrderInfo> exportOrderList(Map<String, String[]> paramMap) {
		
		if(paramMap.get("service_type_name")[0].equals("")){
			return exportConsicOrderList(paramMap);
		}
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append("select oi.*,(select name from merchant_info mi where mi.id=oi.merchant_id) as merchant_name,DATE_FORMAT(oi.join_time, '%Y-%m-%d %H:%i:%s') as join_time,st.service_type_name as orderTypeName,");
		sql.append("(case oi.order_pay_type when 1 then '支付宝支付' when 2 then '微信支付' when 3 then '现金支付' when 4 then '免单' else '' end) as orderPayType, (select phone from user_info where id = oi.user_id) as phone,(select name from user_info where id = oi.user_id) as userName,");
		sql.append("(select name from merchant_info mer where mer.id=oi.merchant_id and mer.is_del=0) as merchants_name,");
		sql.append(" (select dict_value from dictionary where dict_key = oi.order_status and dict_type ='managerOrderStatusMap' and is_del = 0) as orderStatusName");
		sql.append(" from order_info oi inner join service_type st on st.id=oi.service_type_id and st.is_del=0  where ");
		Map<String,String[]> strfilter=new HashMap<String,String[]>();
		Map<String,String[]> intfilter=new HashMap<String,String[]>();
		
		strfilter.put("oi.province", paramMap.get("province"));
		strfilter.put("oi.order_no", paramMap.get("order_id"));	
		intfilter.put("oi.order_status", paramMap.get("purchase_status"));
		intfilter.put("oi.order_pay_type", paramMap.get("order_pay_type"));
		strfilter.put("st.service_type_name", paramMap.get("service_type_name"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and oi.address like '").append(match).append("' ");
			}else{
				strfilter.put("oi.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
		}
		
		sql.append(Util.getExportdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ))
		.append(Util.getExportdateFilter("oi.deal_time", paramMap.get("del_start_time"),paramMap.get("del_off_time") ));;
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and oi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			}
		if(!(paramMap.get("phone")[0]==null) && !paramMap.get("phone")[0].equals("")){
			sql.append(" and oi.user_id in (select id from user_info ui where ui.phone=").append(paramMap.get("phone")[0]).append(")");
			}
		if(!(paramMap.get("merchant_name")[0]==null) && !paramMap.get("merchant_name")[0].equals("")){
			sql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select id from merchant_info mi where mi.name='").append(paramMap.get("merchant_name")[0]).append("'))");
			}
		
		if(!(paramMap.get("merchant_phone")[0]==null) && !paramMap.get("merchant_phone")[0].equals("")){
			sql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select merchant_id from merchant_employees me where me.employees_type=1 and me.phone='").append(paramMap.get("merchant_phone")[0]).append("'))");
		}
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<OrderInfo> orderList=OrderInfo.dao.find(sql.toString());
		
		return orderList;
	}

	private List<OrderInfo> exportConsicOrderList(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append("select oi.*,(select name from merchant_info mi where mi.id=oi.merchant_id) as merchant_name,DATE_FORMAT(oi.join_time, '%Y-%m-%d %H:%i:%s') as join_time,");
		sql.append(" (case oi.order_pay_type when 1 then '支付宝支付' when 2 then '微信支付' when 3 then '现金支付' when 4 then '免单' else '' end) as orderPayType,(select service_type_name from service_type st where st.id=oi.service_type_id) as orderTypeName,");
		sql.append("(select phone from user_info where id = oi.user_id) as phone,(select name from user_info where id = oi.user_id) as userName,");
		sql.append("(select name from merchant_info mer where mer.id=oi.merchant_id and mer.is_del=0) as merchants_name,");
		sql.append(" (select dict_value from dictionary where dict_key = oi.order_status and dict_type ='managerOrderStatusMap' and is_del = 0) as orderStatusName");
		sql.append(" from order_info oi where ");
		Map<String,String[]> strfilter=new HashMap<String,String[]>();
		Map<String,String[]> intfilter=new HashMap<String,String[]>();
		
		strfilter.put("oi.province", paramMap.get("province"));
		strfilter.put("oi.order_no", paramMap.get("order_id"));	
		intfilter.put("oi.order_status", paramMap.get("purchase_status"));
		intfilter.put("oi.order_pay_type", paramMap.get("order_pay_type"));
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strfilter, intfilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and oi.address like '").append(match).append("' ");
			}else{
				strfilter.put("oi.city", paramMap.get("city"));
				sql.append(Util.getFilter(strfilter, intfilter));
			}
		}else{
			sql.append(Util.getFilter(strfilter, intfilter));
		}
		sql.append(Util.getExportdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ))
		.append(Util.getExportdateFilter("oi.deal_time", paramMap.get("del_start_time"),paramMap.get("del_off_time") ));;
		
		if(!(paramMap.get("agentId")[0]==null) && !paramMap.get("agentId")[0].equals("")){
			sql.append(" and oi.app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(paramMap.get("agentId")[0]).append(")");
			}
		if(!(paramMap.get("phone")[0]==null) && !paramMap.get("phone")[0].equals("")){
			sql.append(" and oi.user_id in (select id from user_info ui where ui.phone=").append(paramMap.get("phone")[0]).append(")");
			}
		if(!(paramMap.get("merchant_name")[0]==null) && !paramMap.get("merchant_name")[0].equals("")){
			sql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select id from merchant_info mi where mi.name='").append(paramMap.get("merchant_name")[0]).append("'))");
			}
		
		if(!(paramMap.get("merchant_phone")[0]==null) && !paramMap.get("merchant_phone")[0].equals("")){
			sql.append(" and oi.id in (select order_id from merchant_plan mp where mp.merchant_id in (select merchant_id from merchant_employees me where me.employees_type=1 and me.phone='").append(paramMap.get("merchant_phone")[0]).append("'))");
		}
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<OrderInfo> orderList=OrderInfo.dao.find(sql.toString());
		
		return orderList;
	}

	public List<Record> getOrderDetail(String orderId, String tableName) {

		String sql = "select * from " + tableName + " where order_id=?";
		List<Record> orderList = Db.find(sql, orderId);
		return orderList;
	}

	public List<Record> getMerchantsByOrderId(Map<String, String[]> paramMap) {
		 StringBuffer sql = new StringBuffer();
		 String orderId=paramMap.get("order_id")[0];
		 long merchantsId=0L;
		 if(paramMap.get("merchants_id")[0]!=null){
		  merchantsId=Long.parseLong(paramMap.get("merchants_id")[0].equals("")==true?"0":paramMap.get("merchants_id")[0]);
		 }
		 sql.append("select mi.name,mi.id from merchant_plan mp inner join merchant_info mi on mp.merchant_id=mi.id where mp.order_id=?");
		 int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		 int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		 long total=Db.find(sql.toString(),orderId).size();
			sql.append(" limit ");
			sql.append(pageNumber);
			sql.append(",");
			sql.append(pageSize);
			
			List<Record> merchants=Db.find(sql.toString(),orderId);
			if(merchants.size()>0){
				for(Record mer:merchants){
					if(mer.getLong("id")==merchantsId){
						mer.set("attr", "合作服务商");
					}else{
						mer.set("attr", "待选择服务商");
					}
					mer.set("id", mer.getLong("id").toString());
				}
				
				merchants.get(0).set("total",total);
			}
		return merchants;
	}

	@Override
	public List<Record> getHzOrderDetail(String orderId, String tableName) {
		String sql = "select * from " + tableName + " where order_id=?";
		String sqlItems="SELECT d.dict_key AS serviceItemId,d.dict_value AS itemName,(select dict_value from dictionary where id=d.parent_dict_id) AS serviceName FROM dictionary d WHERE d.dict_key = ? AND d.dict_type='hzItem'";
		List<Record> orderList = Db.find(sql, orderId);
		String serviceItemIds=null;
		StringBuffer items=new StringBuffer();
		if(orderList.size()>0){
			
		serviceItemIds=orderList.get(0).getStr("service_item");
		
		if (serviceItemIds != null && !serviceItemIds.equals("")) {
			String[] ids = serviceItemIds.split(",");
			for (String itemId : ids) {
				List<Record> ItemsName = Db.find(sqlItems, itemId);
				if (ItemsName != null && ItemsName.size() > 0) {
					
					items.append(ItemsName.get(0).get("serviceName")); 
					items.append(": ");
					items.append(ItemsName.get(0).get("itemName"));
					items.append(",");
				}
			
	}
	}
		if(items.length()>1){
			orderList.get(0).set("items", items.substring(0,items.length()-1));
		}
		
		}
		return orderList;
		
	}

	@Override
	public Map<String, Object> orderStatusDetail(Long orderId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> orderPlanDetail = new ArrayList<Map<String,Object>>();
		Map<String, Object> planMap = null;
		Record orderBasic = Db.findById("order_info", orderId);
		String phone = Db.queryStr("select u.phone FROM user_info u WHERE u.id=?", orderBasic.getLong("user_id"));
		orderBasic.set("phone", phone);
		String service_name = Db.queryStr("select s.service_type_name FROM service_type s WHERE s.id=?", orderBasic.getInt("service_type_id"));
		orderBasic.set("service_name", service_name);
		
		List<Record> orderPlan = Db.find("SELECT t.*,m.name FROM merchant_plan t LEFT JOIN merchant_info m ON t.merchant_id=m.id WHERE t.order_id=?", orderId);
		Long confirmMerId = orderBasic.getLong("merchant_id");
		Record confirmMerchant = null;
		if(confirmMerId!=null&&confirmMerId.intValue()!=0){
			confirmMerchant = Db.findById("merchant_info", confirmMerId);
		}
        int orderStatus = orderBasic.getInt("order_status");
        int order_pay_type=StringUtil.nullToInteger(orderBasic.getInt("order_pay_type"));
        String orderPayType=getPayType(order_pay_type);
        orderBasic.set("orderPayType", orderPayType);
        
        List<Record> timeLine=Db.find("select actionTime from timeline where actionCode='310' and orderId=? limit 1",orderId);
        if(timeLine!=null&&timeLine.size()>0){
        	orderBasic.set("finshService_time", timeLine.get(0).getDate("actionTime"));
        }else{
        	orderBasic.set("finshService_time", null);
        }
        
        List<Record> confirmtimeLine=Db.find("select actionTime from timeline where actionCode='210' and orderId=? limit 1",orderId);
        if(timeLine!=null&&timeLine.size()>0){
        	orderBasic.set("confirmTime", confirmtimeLine.get(0).getDate("actionTime"));
        }else{
        	orderBasic.set("confirmTime", null);
        }
        
        Record evaluation = null;
        if(orderStatus==5){
        	// 支付完成才有评价
        	List<Record> list = Db.find("SELECT * FROM evaluation t WHERE t.order_id=? AND t.merchant_id=?", orderId,confirmMerId);
        	if(list!=null&&list.size()>0){
        		evaluation = list.get(0);
        	}
        }
        
        for(Record r : orderPlan){
        	planMap=new HashMap<String, Object>();
        	planMap.put("id", r.getLong("id"));
        	planMap.put("price", r.getBigDecimal("price"));
        	planMap.put("discount_price", r.getBigDecimal("discount_price"));
        	planMap.put("content", r.getStr("content"));
        	planMap.put("detail", getPlanDetail(r.getLong("id")));
        	orderPlanDetail.add(planMap);
        }
        long pushCount=Db.queryLong("select count(1) from push_merchant_order where order_id=?", orderId);
        
        map.put("pushCount", pushCount);
        map.put("orderBasic", orderBasic);
        map.put("orderPlan", orderPlan);
        map.put("orderPlanDetail", orderPlanDetail);
        map.put("confirmMerchant", confirmMerchant);
        map.put("evaluation", evaluation);
		return map;
	}

	private String getPayType(int order_pay_type) {
		String s="";
		switch (order_pay_type){
		case 1: s="支付宝支付";break;
		case 2: s="微信支付";break;
		case 3: s="现金支付";break;
		case 4: s="免单";break;
		default : s="" ;break; 
		}
		return s;
	}

	private List<String> getPlanDetail(long merchant_plan_id) {
		List<String> list = new ArrayList<String>();
		String sql="SELECT t.merchant_plan_id AS planId,t.content FROM merchant_plan_detail t WHERE t.merchant_plan_id=? LIMIT 0,1";
		List<Record> planDetail = Db.find(sql, merchant_plan_id);
		if (planDetail != null && planDetail.size()>0) {
			String content = StringUtil.null2Str(planDetail.get(0).getStr("content"));
			
			JSONArray arr= JSONObject.parseArray(content);
			if (arr != null&&arr.size()>0) {
				for(int i=0;i<arr.size();i++){
					JSONObject jsonObject = arr.getJSONObject(i);
					String s="";
					s=s+jsonObject.get("colDesc")+":"+jsonObject.get("value");
					list.add(s);
 				}

			}

		}
		return list;
	}
	
	
	@Override
	public List<String> getOrderDet(String orderId, String serviceTypeId) {
		String orderSql="select * from order_info_detail where order_id=?";
		List<String> list = new ArrayList<String>();
		List<Record> orderDet=Db.find(orderSql,orderId);
		if(orderDet!=null&&orderDet.size()>0){
		String content=orderDet.get(0).getStr("json_detail");
		//JSONObject jsonObject = JSONObject.parseObject(jsonString);
		JSONArray arr= JSONObject.parseArray(content);
		if (arr != null&&arr.size()>0) {
			for(int i=0;i<arr.size();i++){
				JSONObject jsonObject = arr.getJSONObject(i);
				String s="";
				s=s+jsonObject.get("colDesc")+":"+jsonObject.get("value");
				list.add(s);
				}

		}
//		for (String key : jsonObject.keySet()) {
//			if (key.startsWith("format")) {
//				String val = StringUtil.null2Str(jsonObject.get(key));
//				list.add(val);
//			}
//			
//		}	
//		String sql="select om.* from order_object_model om where om.object_id=? order by om.rank";
//		List<Record> res=Db.find(sql,serviceTypeId);
//		if(res!=null&&res.size()>0){
//			for(Record r:res){
//			String priKey = StringUtil.null2Str(r.getStr("col_name"));
//			String val=r.getStr("col_desc")+":"+StringUtil.nullToString(jsonObject.get(priKey));
//			list.add(val);
//			}
//		}
//		}
		}
		return list;
}

	@Override
	public List<Record> getPushMerchantsByOrderId(Map<String, String[]> paramMap) {

		 StringBuffer sql = new StringBuffer();
		 String orderId=paramMap.get("order_id")[0];
		 long merchantsId=0L;
		 if(paramMap.get("merchants_id")[0]!=null){
		  merchantsId=Long.parseLong(paramMap.get("merchants_id")[0].equals("")==true?"0":paramMap.get("merchants_id")[0]);
		 }
		 sql.append("select mi.name,mi.id from push_merchant_order po inner join merchant_info mi on po.merchant_id=mi.id where po.order_id=?");
		 int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		 int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		 long total=Db.find(sql.toString(),orderId).size();
			sql.append(" limit ");
			sql.append(pageNumber);
			sql.append(",");
			sql.append(pageSize);
			
			List<Record> merchants=Db.find(sql.toString(),orderId);
			if(merchants.size()>0){
				for(Record mer:merchants){
					if(mer.getLong("id")==merchantsId){
						mer.set("attr", "合作服务商");
					}else{
						mer.set("attr", "待选择服务商");
					}
					mer.set("id", mer.getLong("id").toString());
				}
				
				merchants.get(0).set("total",total);
			}
		return merchants;
	
	}
	
}
