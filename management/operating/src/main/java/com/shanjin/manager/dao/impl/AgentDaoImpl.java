package com.shanjin.manager.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.Bean.AgentCharge;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.SystemUserInfo;
import com.shanjin.manager.constant.Constant.CHARGE;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.AgentDao;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class AgentDaoImpl implements AgentDao {

	public List<Agent> getAgentCharge(Map<String, String[]> paramMap){
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		sql.append("select m.id,m.agent_id,m.charge_money,m.charge_time,m.charge_type,m.head_name,m.order_status,m.remark,u.userName as agent_name,u.provinceDesc,u.cityDesc,u.userType from manager_agent_charge m inner join authority_user_info u on m.agent_id=u.id where m.is_del=0 and ");
		totalSql.append("select count(1) as total from manager_agent_charge m inner join authority_user_info u on m.agent_id=u.id where m.is_del=0 and ");
		strFilter.put("m.charge_type", paramMap.get("charge_type"));
		strFilter.put("u.userType", paramMap.get("userType"));
		strFilter.put("m.id", paramMap.get("order_id"));
		strFilter.put("u.provinceDesc", paramMap.get("province"));
		strFilter.put("u.cityDesc", paramMap.get("city"));
		strFilter.put("u.id", paramMap.get("agentId"));
		strLikeFilter.put("u.userName", paramMap.get("agent_name"));
		strLikeFilter.put("m.head_name", paramMap.get("head_name"));
		strFilter.put("m.order_status", paramMap.get("order_status"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("m.charge_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("m.charge_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		
		long total = Agent.dao.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "m.charge_time";
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
		List<Agent> agents = Agent.dao.find(sql
				.toString());
		if (agents.size() > 0) {
			agents.get(0).setTotal(total);
		}
		return agents;
		
	}
	

	public Boolean AuditAgentCharge(Map<String, String[]> param) {
		String[] ids = param.get("id");
		boolean flag=false;
		for (int i = 0; i < ids.length; i++) {
			String sql = "update manager_agent_charge set order_status=1 where id= "
					+ ids[i];
			Db.update(sql);
		}
		flag=true;
		return flag;
	}



	public int addAgentCharge(Map<String, String[]> param, String operUserName) {
		int flag=0;
		String agentId=param.get("agent_id")[0];
		String chageMoney=param.get("charge_money")[0];
		String chargeType=param.get("charge_type")[0];
		String charge_time=DateUtil.getDate();
		String remark=param.get("remark")[0];
		String sqlRecharge = "update authority_user_info set balance=balance+? where id=?";
		String sqlConsumer = "update authority_user_info set balance=balance-? where id=?";
		
		if(StringUtil.isNullStr(operUserName)){
			flag=3;
			return flag;
		}
		
		if(CHARGE.AGENT_RECHARGE.equals(chargeType)){
			if(100000<Integer.parseInt(chageMoney)){
				flag=3;
				return flag;
			}
			flag=1;
			Db.update(sqlRecharge,chageMoney,agentId);
		}else if(CHARGE.AGENT_CONSUMER.equals(chargeType)){
			int balance=Db.queryInt("select balance from authority_user_info where id=?",agentId);
			if(balance<Integer.parseInt(chageMoney)){
				flag=3;
				return flag;
			}
			flag=2;
			Db.update(sqlConsumer,chageMoney,agentId);	
		}
		
		 Agent agent=new Agent();
		 agent.set("agent_id", agentId).set("charge_money", chageMoney)
		.set("charge_time", charge_time).set("charge_type", chargeType).set("head_name", operUserName).set("remark", remark)
		.set("order_status", "0").save();
		
		return flag;
	}

	public List<Record> getAgentList(Map<String, String[]> paramMap) {
	
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select id,userName from authority_user_info where ");
		totalSql.append("select count(1) as total from authority_user_info where ");
		strFilter.put("userName", paramMap.get("agent_name"));
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> agentList = Db.find(sql.toString());
		if (agentList.size() > 0) {
			agentList.get(0).set("total", total);
		}
		return agentList;
	}


	public List<AgentCharge> getexportExcel(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		sql.append("select m.id,m.agent_id,m.charge_money,DATE_FORMAT(m.charge_time, '%Y-%m-%d %H:%i:%s') as charge_time,(case m.charge_type when 1 then '充值' when 2 then '扣费' else '' end) as charge_type,m.head_name,m.order_status,m.remark,u.userName as agent_name,u.provinceDesc,u.cityDesc,(case u.userType when 1 then '公司员工' when 2 then '省代理' when 3 then '市代理' when 4 then '项目代理' else '' end) as userType from manager_agent_charge m inner join authority_user_info u on m.agent_id=u.id where m.is_del=0 and ");
		strFilter.put("m.charge_type", paramMap.get("charge_type"));
		strFilter.put("u.userType", paramMap.get("userType"));
		strFilter.put("m.id", paramMap.get("id"));
		strFilter.put("u.provinceDesc", paramMap.get("province"));
		strFilter.put("u.cityDesc", paramMap.get("city"));
		strFilter.put("u.id", paramMap.get("agentId"));
		strLikeFilter.put("u.userName", paramMap.get("agent_name"));
		strLikeFilter.put("m.head_name", paramMap.get("head_name"));
		strFilter.put("m.order_status", paramMap.get("order_status"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getExportdateFilter("m.charge_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<AgentCharge> agents = AgentCharge.dao.find(sql
				.toString());
		return agents;
		
	}


	public List<SystemUserInfo> getAgent(Map<String, String[]> paramMap) {
		
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("t.userName", paramMap.get("userName"));
		strLikeFilter.put("t.phone", paramMap.get("phone"));
		sql.append("select t.id,t.userName,t.realName,t.psw,t.pswHints,t.email,t.phone,t.address,t.createTime,t.updateTime,t.updateName,t.disabled,t.remark,t.isAdmin,t.userType,t.province,t.city,t.accountName,t.accountBank,t.accountNumber,t.provinceDesc,t.cityDesc,t.userType,t.balance from authority_user_info t");
		totalSql.append("select count(1) as total from authority_user_info t");
		strFilter.put("t.id", paramMap.get("id"));
		strFilter.put("t.userName", paramMap.get("agent_name"));
		strFilter.put("t.userType", paramMap.get("userType"));	
		strFilter.put("t.province", paramMap.get("province"));
		strFilter.put("t.city", paramMap.get("city"));
		if(paramMap.get("appType")[0].equals("")){
			sql.append(" where t.isDel=0  and t.userType>1 and ");
			totalSql.append(" where t.isDel=0 and t.userType>1 and ");
		
		}else{
			sql.append(",merchant_app_info a,authority_user_app ap where t.id=ap.userId and ap.appId=a.id and t.isDel=0 and t.userType>1 and ");
			totalSql.append(",merchant_app_info a,authority_user_app ap where t.id=ap.userId and ap.appId=a.id and t.isDel=0 and t.userType>1 and ");
			strFilter.put("a.app_type", paramMap.get("appType"));
		}
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("t.createTime",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("t.createTime",
				paramMap.get("start_time"), paramMap.get("off_time")));
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "t.updateTime";
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
		List<SystemUserInfo> agentList = SystemUserInfo.dao.find(sql.toString());
		if (agentList.size() > 0) {
			agentList.get(0).setTotal(total);;
		}
		return agentList;
	}


	public Boolean deleteAgent(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			SystemUserInfo.dao.findById(ids[i]).set("isDel", 1).update();
		}
		flag = true;
		return flag;
	}

	public Boolean deleteAgentCharge(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			Agent.dao.findById(ids[i]).set("is_del", 1).update();
		}
		flag = true;
		return flag;
	}

	public List<AppInfo> getAppByAgentId(Map<String, String[]> paramMap) {
		String agentId=paramMap.get("agentId")[0];
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select app_name from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId="+agentId);
		totalSql.append("select count(1) total from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId="+agentId);
		long total = AppInfo.dao.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<AppInfo> appInfo = AppInfo.dao.find(sql.toString());
		if (appInfo.size() > 0) {
			appInfo.get(0).setTotal(total);
		}
		return appInfo;
	}


	@Override
	public List<AgentEmployee> getAgentEmployeeList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strFilter1 = new HashMap<String, String[]>();
		Map<String, String[]> intFilter1 = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		
		strFilter1.put("m.province", paramMap.get("provinceDesc"));
		
		sql.append("select a.id,a.name,a.phone,a.invite_code,a.join_time,a.province,a.city,(select ae.area from area ae where ae.id=a.province) as provinceName,(select ae.area from area ae where ae.id=a.city) as cityName,"
				+ "(select count(1) from merchant_info m where m.invitation_code=a.invite_code and m.is_del=0 and ");
		if(StringUtil.isNotNullMap(paramMap,"provinceDesc")&&StringUtil.isNotNullMap(paramMap,"cityDesc")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<m.join_time ");
		sql.append(") as merchant_total,(select count(1) from merchant_info m inner join "
				+ "merchant_auth ma on m.id=ma.merchant_id where m.is_del=0 and m.invitation_code=a.invite_code and ma.auth_status=1 and ma.auth_type=1 and ");
		if(StringUtil.isNotNullMap(paramMap,"provinceDesc")&&StringUtil.isNotNullMap(paramMap,"cityDesc")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getdateFilter("ma.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<ma.join_time ");
		sql.append(") as con_auth_total,(select count(1) from merchant_info m inner join merchant_auth ma on m.id=ma.merchant_id where m.is_del=0 and m.invitation_code=a.invite_code and ma.auth_status=1 and ma.auth_type=2 and ");
		if(StringUtil.isNotNullMap(paramMap,"provinceDesc")&&StringUtil.isNotNullMap(paramMap,"cityDesc")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getdateFilter("ma.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<ma.join_time ");
		sql.append(") as per_auth_total from agent_employee a where a.is_del=0 and ");
		totalSql.append("select count(1) as total from agent_employee a where a.is_del=0 and ");
		strFilter.put("a.province", paramMap.get("province"));
		strFilter.put("a.city", paramMap.get("city"));
		strLikeFilter.put("a.name", paramMap.get("name"));
		strLikeFilter.put("a.phone", paramMap.get("phone"));
		strLikeFilter.put("a.invite_code", paramMap.get("invite_code"));

		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		
		long total = AgentEmployee.dao.find(totalSql.toString()).get(0).getLong("total");
		
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
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		
		List<AgentEmployee> agentEmployee = AgentEmployee.dao.find(sql
				.toString());
		if (agentEmployee.size() > 0) {
			agentEmployee.get(0).setTotal(total);
		}
		return agentEmployee;
	}


	@Override
	public Boolean deleteAgentEmployee(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("ids")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			AgentEmployee.dao.findById(ids[i]).set("is_del", 1).update();
		}
		flag = true;
		return flag;
	}


	@Override
	public int editAgentEmployee(Map<String, String[]> param) {
		int flag = 0;
		String id=param.get("id")[0];
		String name=param.get("name")[0];
		String phone=param.get("phone")[0];
		String province=param.get("province")[0];
		String city=param.get("city")[0];
		String invite_code=param.get("invite_code")[0];
		
		String sql="select id,count(1) as total from agent_employee a where a.invite_code=? and a.is_del=0";
		List<Record> tolList=Db.find(sql, invite_code);
		long total=tolList.get(0).getLong("total");
		if(total>0){
			long ids=tolList.get(0).getLong("id");
			if(ids==Long.parseLong(id)){	
			}else{
				flag=2;
				return flag;
			}
		}
		AgentEmployee agentEmployee=new AgentEmployee();
		agentEmployee.findById(id).set("name", name).set("phone", phone)
		.set("province", province).set("city", city).set("invite_code", invite_code)
		.update();
		flag = 1;
		
		return flag;
	}
	
	public int addAgentEmployee(Map<String, String[]> param) {
		int flag = 0;
		String name=param.get("name")[0];
		String phone=param.get("phone")[0];
		String province=param.get("province")[0];
		String city=param.get("city")[0];
		String join_time=DateUtil.getDate();
		String invite_code=param.get("invite_code")[0];
		String sql="select count(1) as total from agent_employee a where a.invite_code=? and a.is_del=0";
		long total=Db.find(sql, invite_code).get(0).getLong("total");
		if(total>0){
			flag=2;
			return flag;
		}
		AgentEmployee agentEmployee=new AgentEmployee();
		agentEmployee.set("name", name).set("phone", phone)
		.set("province", province).set("city", city).set("join_time", join_time).set("invite_code", invite_code)
		.save();
		flag = 1;
		
		return flag;
	}


	@Override
	public List<AgentEmployee> exportAgentEmployeeExcel(
			Map<String, String[]> paramMap) {
	
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strFilter1 = new HashMap<String, String[]>();
		Map<String, String[]> intFilter1 = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strFilter1.put("m.province", paramMap.get("provinceDesc"));
		sql.append("select a.id,a.name,a.phone,a.invite_code,DATE_FORMAT(a.join_time, '%Y-%m-%d %H:%i:%s') as join_time,a.province,a.city,(select ae.area from area ae where ae.id=a.province) as provinceName,(select ae.area from area ae where ae.id=a.city) as cityName,"
				+ "(select count(1) from merchant_info m where m.invitation_code=a.invite_code and m.is_del=0 and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getExportdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<m.join_time ");
		sql.append(") as merchant_total,(select count(1) from merchant_info m inner join "
				+ "merchant_auth ma on m.id=ma.merchant_id where m.is_del=0 and m.invitation_code=a.invite_code and ma.auth_status=1 and ma.auth_type=1 and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getExportdateFilter("ma.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<ma.join_time ");
		sql.append(") as con_auth_total,(select count(1) from merchant_info m inner join merchant_auth ma on m.id=ma.merchant_id where m.is_del=0 and m.invitation_code=a.invite_code and ma.auth_status=1 and ma.auth_type=2 and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getExportdateFilter("ma.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<ma.join_time ");
		sql.append(") as per_auth_total,");
		sql.append("(select count(1) from (SELECT m.invitation_code,count(ma.id) as total FROM merchant_info m INNER JOIN merchant_auth ma ON m.id = ma.merchant_id WHERE m.is_del = 0 AND ma.auth_status = 1 AND ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getExportdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		sql.append(" group by m.id having total=2 ) as h where h.invitation_code=a.invite_code) as total ");
		sql.append(" from agent_employee a where a.is_del=0 and ");
		strFilter.put("a.province", paramMap.get("province"));
		strFilter.put("a.city", paramMap.get("city"));
		strLikeFilter.put("a.name", paramMap.get("name"));
		strLikeFilter.put("a.phone", paramMap.get("phone"));
		strLikeFilter.put("a.invite_code", paramMap.get("invite_code"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
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
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<AgentEmployee> agentEmployee = AgentEmployee.dao.find(sql
				.toString());
		
		return agentEmployee;
	}


	@Override
	public List<MerchantsInfo> getMerchantsByinviteCode(
			Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		strFilter.put("m.invitation_code", paramMap.get("invite_code"));
		sql.append(" select m.id,m.name,m.join_time,m.invitation_code,m.location_address as address from merchant_info m where m.is_del=0 and ");
		totalSql.append("select count(1) as total from merchant_info m where m.is_del=0 and ");
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		long total = MerchantsInfo.dao.find(totalSql.toString()).get(0).getLong("total");
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<MerchantsInfo> merchantsList = MerchantsInfo.dao.find(sql
				.toString());
		if (merchantsList.size() > 0) {
			merchantsList.get(0).setTotal(total);
			for(int i=0;i<merchantsList.size();i++){
			merchantsList.get(i).set("id", merchantsList.get(i).getLong("id").toString());
			}
		}
		return merchantsList;
		
	}
	
	@Override
	public List<AgentEmployee> getCheckAgentEmployeeList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strFilter1 = new HashMap<String, String[]>();
		Map<String, String[]> intFilter1 = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strFilter1.put("m.province", paramMap.get("provinceDesc"));
		sql.append("select a.id,a.name,a.phone,a.invite_code,a.join_time,a.province,a.city,(select ae.area from area ae where ae.id=a.province) as provinceName,(select ae.area from area ae where ae.id=a.city) as cityName,"
				+ "(select count(1) from merchant_info m where m.invitation_code=a.invite_code and m.is_del=0 and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<m.join_time ");
		sql.append(") as merchant_total,(select count(1) from merchant_info m inner join "
				+ "(SELECT auth_status,merchant_id,join_time,auth_type FROM (SELECT merchant_id,auth_status,join_time,auth_type FROM merchant_auth GROUP BY merchant_id,auth_status,join_time HAVING auth_type=1 and auth_status in (1,3) ORDER BY merchant_id,join_time) mau GROUP BY mau.merchant_id) ma on m.id=ma.merchant_id where m.is_del=0 and m.invitation_code=a.invite_code and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getdateFilter("ma.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<ma.join_time ");
		sql.append(") as con_auth_total,(select count(1) from merchant_info m inner join "
				+ "(SELECT auth_status,merchant_id,join_time,auth_type FROM (SELECT merchant_id,auth_status,join_time,auth_type FROM merchant_auth GROUP BY merchant_id,auth_status,join_time HAVING auth_type=2 and auth_status in (1,3) ORDER BY merchant_id,join_time) mau GROUP BY mau.merchant_id) ma on m.id=ma.merchant_id where m.is_del=0 and m.invitation_code=a.invite_code and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getdateFilter("ma.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<ma.join_time ");
		sql.append(") as per_auth_total from agent_employee a where a.is_del=0 and ");
		totalSql.append("select count(1) as total from agent_employee a where a.is_del=0 and ");
		strFilter.put("a.province", paramMap.get("province"));
		strFilter.put("a.city", paramMap.get("city"));
		strLikeFilter.put("a.name", paramMap.get("name"));
		strLikeFilter.put("a.phone", paramMap.get("phone"));
		strLikeFilter.put("a.invite_code", paramMap.get("invite_code"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		long total = AgentEmployee.dao.find(totalSql.toString()).get(0).getLong("total");
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
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		
		List<AgentEmployee> agentEmployee = AgentEmployee.dao.find(sql
				.toString());
		if (agentEmployee.size() > 0) {
			agentEmployee.get(0).setTotal(total);
		}
		return agentEmployee;
	}


	@Override
	public List<AgentEmployee> exportCheckAgentEmployeeExcel(
			Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strFilter1 = new HashMap<String, String[]>();
		Map<String, String[]> intFilter1 = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strFilter1.put("m.province", paramMap.get("provinceDesc"));
		sql.append("select a.id,a.name,a.phone,a.invite_code,DATE_FORMAT(a.join_time, '%Y-%m-%d %H:%i:%s') as join_time,a.province,a.city,(select ae.area from area ae where ae.id=a.province) as provinceName,(select ae.area from area ae where ae.id=a.city) as cityName,"
				+ "(select count(1) from merchant_info m where m.invitation_code=a.invite_code and m.is_del=0 and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getExportdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<m.join_time ");
		sql.append(") as merchant_total,(select count(1) from merchant_info m inner join "
				+ "(SELECT auth_status,merchant_id,join_time,auth_type FROM (SELECT merchant_id,auth_status,join_time,auth_type FROM merchant_auth GROUP BY merchant_id,auth_status,join_time HAVING auth_type=1 and auth_status in (1,3) ORDER BY merchant_id,join_time) mau GROUP BY mau.merchant_id) ma on m.id=ma.merchant_id where m.is_del=0 and m.invitation_code=a.invite_code and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getExportdateFilter("ma.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<ma.join_time ");
		sql.append(") as con_auth_total,(select count(1) from merchant_info m inner join "
				+ "(SELECT auth_status,merchant_id,join_time,auth_type FROM (SELECT merchant_id,auth_status,join_time,auth_type FROM merchant_auth GROUP BY merchant_id,auth_status,join_time HAVING auth_type=2 and auth_status in (1,3) ORDER BY merchant_id,join_time) mau GROUP BY mau.merchant_id) ma on m.id=ma.merchant_id where m.is_del=0 and m.invitation_code=a.invite_code and ");
		if(StringUtil.isNotNullMap(paramMap,"province")&&StringUtil.isNotNullMap(paramMap,"city")){
			if(StringUtil.matchProvince(paramMap.get("provinceDesc"))){
				String match="%"+paramMap.get("provinceDesc")[0]+"%"+paramMap.get("cityDesc")[0] +"%";
				sql.append(" m.location_address like '").append(match).append("' and ");
			}else{
				strFilter1.put("m.city", paramMap.get("cityDesc"));
			}
		}
		sql.append(Util.getFilter(strFilter1, intFilter1)).append(Util.getExportdateFilter("ma.join_time",paramMap.get("start_time"), paramMap.get("off_time"))).append(" and a.join_time<ma.join_time ");
		sql.append(") as per_auth_total from agent_employee a where a.is_del=0 and ");
		strFilter.put("a.province", paramMap.get("province"));
		strFilter.put("a.city", paramMap.get("city"));
		strLikeFilter.put("a.name", paramMap.get("name"));
		strLikeFilter.put("a.phone", paramMap.get("phone"));
		strLikeFilter.put("a.invite_code", paramMap.get("invite_code"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
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
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<AgentEmployee> agentEmployee = AgentEmployee.dao.find(sql
				.toString());
		
		return agentEmployee;
	}
}
