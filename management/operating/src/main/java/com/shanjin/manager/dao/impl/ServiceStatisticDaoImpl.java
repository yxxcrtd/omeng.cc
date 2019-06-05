package com.shanjin.manager.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.dao.ServiceStatisticDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class ServiceStatisticDaoImpl implements ServiceStatisticDao{

	@Override
	public List<MerchantsInfo> getMerchantOrderStatis(Map<String, Object> paramMap) {
		StringBuffer sql = new StringBuffer();
		String date=(String) paramMap.get("time");
		sql.append("select m.name,m.province,m.city,m.join_time,m.location_address as address,(select name from catalog where alias=m.app_type and level=0) as app_name,(case m.auth_status when 0 then '未通过' when 1 then '已认证' when 2 then '待认证' when 3 then '已取消' else '空' end) as auth_status,(CASE m.auth_type WHEN 1 THEN '企业认证' WHEN 2 THEN '个人认证' ELSE '未认证' END) as auth_type,(select phone from merchant_employees where employees_type=1 and merchant_id=m.id) as phone,"
				+ "(select GROUP_CONCAT(s.service_type_name) from merchant_service_type mst inner join service_type s on mst.service_type_id=s.id where mst.merchant_id=m.id) as service_type,(select count(1) from push_merchant_order pmo where pmo.merchant_id=m.id and DATE_FORMAT(pmo.join_time,'%Y-%m-%d')=?) as push_count,"
				+ "(select count(1) from merchant_plan mp where mp.merchant_id=m.id and DATE_FORMAT(mp.join_time,'%Y-%m-%d')=?) as grab_count,"
				+ "(select count(1) from order_info oi where oi.order_status=5 and m.id=oi.merchant_id and DATE_FORMAT(oi.join_time,'%Y-%m-%d')=?) as complete_count,"
				+ "(select count(1) from merchant_plan mp inner join order_info oi on mp.order_id=oi.id where mp.merchant_id=m.id and oi.order_status=2 and DATE_FORMAT(oi.join_time,'%Y-%m-%d')=?) as wait_confirm_count,"
				+ "(select count(1) from merchant_plan mp inner join order_info oi on mp.order_id=oi.id where mp.merchant_id=m.id and oi.order_status in (3,4,5) and DATE_FORMAT(oi.join_time,'%Y-%m-%d')=?) as losing_count,"
				+ "(select count(1) from order_info oi where oi.order_status=3 and m.id=oi.merchant_id and DATE_FORMAT(oi.join_time,'%Y-%m-%d')=?) as service_confirm_count,"
				+ "(select count(1) from order_info oi where oi.order_status=4 and m.id=oi.merchant_id and DATE_FORMAT(oi.join_time,'%Y-%m-%d')=?) as wait_pay_count,"
				+ "(select count(1) from order_info oi where oi.order_status=5 and m.id=oi.merchant_id and oi.id not in (select order_id from evaluation) and DATE_FORMAT(oi.join_time,'%Y-%m-%d')=?) as wait_evaluat_count"
				+ "  from merchant_info m INNER JOIN (select * from merchant_plan mp where DATE_FORMAT(mp.join_time, '%Y-%m-%d') =? group by mp.merchant_id) mpn ON m.id = mpn.merchant_id  ");
		
		List<MerchantsInfo> merchantOrderList =
				MerchantsInfo.dao.find(sql.toString(),date,date,date,date,date,date,date,date,date);

		return merchantOrderList;
	
	}

	@Override
	public List<Record> getOrderProcerStatis(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		strFilter.put("oi.province", paramMap.get("province"));
		strFilter.put("oi.order_no", paramMap.get("order_no"));
		sql.append("select oi.*,(select service_type_name from service_type st where st.id=oi.service_type_id) as orderTypeName,"
				+ "(select dict_value from dictionary where dict_key = oi.order_status and dict_type ='managerOrderStatusMap' and is_del = 0) as orderStatusName,"
				+ "(select count(1) from push_merchant_order pmo where pmo.order_id=oi.id) as push_count,"
				+ "(select count(1) from merchant_plan mp where mp.order_id=oi.id) as grab_count,"
				+ "(select name from merchant_info mi where mi.id=oi.merchant_id) as name,"
				+ "(select mp.join_time from merchant_plan mp where mp.order_id=oi.id order by mp.join_time asc limit 1) as first_time,"
				+ "(select tl.actionTime from timeline tl where actionCode='210' and tl.orderId=oi.id limit 1) as confirm_time,"
				+ "(select tl.actionTime from timeline tl where actionCode='310' and tl.orderId=oi.id limit 1) as finish_time,"
				+ "(select tl.actionTime from timeline tl where actionCode='250' and tl.orderId=oi.id limit 1) as cacel_time,"
				+ "(select tl.actionTime from timeline tl where actionCode in ('280,290') and tl.orderId=oi.id  limit 1) as over_time "
				
				+ " from order_info oi where ");
		totalSql.append("select count(1) as total from order_info oi where ");
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strFilter, intFilter));
				totalSql.append(Util.getFilter(strFilter, intFilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and oi.address like '").append(match).append("' ");
				totalSql.append(" and oi.address like '").append(match).append("' ");
			}else{
				strFilter.put("oi.city", paramMap.get("city"));
				sql.append(Util.getFilter(strFilter, intFilter));
				totalSql.append(Util.getFilter(strFilter, intFilter));
			}
		}else{
			sql.append(Util.getFilter(strFilter, intFilter));
			totalSql.append(Util.getFilter(strFilter, intFilter));
		}
		
		
		
		
		sql.append(Util.getdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		totalSql.append(Util.getdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" order by oi.join_time desc limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> orderList = Db.find(sql.toString());

		if (orderList.size() > 0) {
			orderList.get(0).set("total", total);
		}

		return orderList;
	
	
	}

	@Override
	public List<OrderInfo> exportOrderProcerList(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		strFilter.put("oi.province", paramMap.get("province"));
		strFilter.put("oi.order_no", paramMap.get("order_no"));
		
		sql.append("select oi.*,(select service_type_name from service_type st where st.id=oi.service_type_id) as orderTypeName,"
				+ "(case oi.order_pay_type when 1 then '支付宝支付' when 2 then '微信支付' when 3 then '现金支付' when 4 then '免单' else '' end) as orderPayType,"
				+ "(case oi.is_del when 0 then '未删除' when 1 then '已删除' else '' end) as isDel,"
				+ "(select dict_value from dictionary where dict_key = oi.order_status and dict_type ='managerOrderStatusMap' and is_del = 0) as orderStatusName,"
				+ "(select count(1) from push_merchant_order pmo where pmo.order_id=oi.id) as push_count,"
				+ "(select count(1) from merchant_plan mp where mp.order_id=oi.id) as grab_count,"
				+ "(select name from merchant_info mi where mi.id=oi.merchant_id) as name,"
				+ "(select mp.join_time from merchant_plan mp where mp.order_id=oi.id order by mp.join_time asc limit 1) as first_time,"
				+ "(select tl.actionTime from timeline tl where actionCode='210' and tl.orderId=oi.id limit 1) as confirm_time,"
				+ "(select tl.actionTime from timeline tl where actionCode='310' and tl.orderId=oi.id limit 1) as finish_time,"
				+ "(select tl.actionTime from timeline tl where actionCode='250' and tl.orderId=oi.id limit 1) as cacel_time,"
				+ "(select tl.actionTime from timeline tl where actionCode in ('280,290') and tl.orderId=oi.id  limit 1) as over_time "
				
				+ " from order_info oi where ");
		
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("province"))){
				sql.append(Util.getFilter(strFilter, intFilter));
				String match="%"+paramMap.get("province")[0]+"市"+paramMap.get("city")[0] +"%";
				sql.append(" and oi.address like '").append(match).append("' ");
				
			}else{
				strFilter.put("oi.city", paramMap.get("city"));
				sql.append(Util.getFilter(strFilter, intFilter));
			}
		}else{
			sql.append(Util.getFilter(strFilter, intFilter));
		}
		sql.append(Util.getExportdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		
	    sql.append(" order by oi.join_time desc limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<OrderInfo> orderList = OrderInfo.dao.find(sql.toString());

		return orderList;
	
	}

	@Override
	public List<AgentEmployee> exportAgentInstallList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = 1000;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		intFilter.put("ae.province", paramMap.get("province"));
		intFilter.put("ae.city", paramMap.get("city"));
		strFilter.put("ae.invite_code", paramMap.get("invite_code"));
		sql.append("select ae.* ");
		List<Record> appList=Db.find("select alias,name from catalog where `level`=0 and status=1 group by alias");
		if(appList!=null&&appList.size()>0){
			for(Record re:appList){
			sql.append(",");
			sql.append("(select count(1) from merchant_info mi where invitation_code=ae.invite_code and app_type='").append(re.getStr("alias")).append("' ");
			sql.append(Util.getExportdateFilter("mi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));
			sql.append(") as ").append(re.getStr("alias"));
			}	
		}
		sql.append(" from agent_employee ae where ae.is_del=0 and ");

		sql.append(Util.getFilter(strFilter, intFilter))
		.append(Util.getExportdateFilter("ae.join_time", paramMap.get("e_start_time"),paramMap.get("e_off_time") ));;

		sql.append(" order by ae.join_time desc limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<AgentEmployee> agentEmployee = AgentEmployee.dao.find(sql.toString());
		return agentEmployee;
	}
	
	@Override
	public List<Record> getAppType() {
		List<Record> appList=Db.find("select alias,name from catalog where `level`=0 and status=1 group by alias");
		return appList;
	}

	@Override
	public List<Record> getAgentInstallStatis(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		intFilter.put("ae.province", paramMap.get("province"));
		intFilter.put("ae.city", paramMap.get("city"));
		strFilter.put("ae.invite_code", paramMap.get("invite_code"));
		int i=0;
		sql.append("select ae.* ");
		List<Record> appList=Db.find("select alias,name from catalog where `level`=0 and status=1 group by alias");
		if(appList!=null&&appList.size()>0){
			for(Record re:appList){
				i++;
			sql.append(",");
			sql.append("(select count(1) from merchant_info mi where invitation_code=ae.invite_code and app_type='").append(re.getStr("alias")).append("' ");
			sql.append(Util.getdateFilter("mi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));
			sql.append(") as ").append(re.getStr("alias"));
			if(i==4){
				break;
			}
			}	
		}
		sql.append(" from agent_employee ae where ae.is_del=0 and ");
		totalSql.append("select count(1) as total from agent_employee ae where ae.is_del=0 and ");
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("ae.join_time", paramMap.get("e_start_time"),paramMap.get("e_off_time") ));;;
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("ae.join_time", paramMap.get("e_start_time"),paramMap.get("e_off_time") ));;;
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		
		sql.append(" order by ae.join_time desc limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> agentEmployee = Db.find(sql.toString());
		
		if (agentEmployee.size() > 0) {
			agentEmployee.get(0).set("total", total);
		}
		return agentEmployee;
	
	}
	
}
