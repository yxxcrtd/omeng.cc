package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.Bean.AgentCharge;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.sso.bean.SystemUserInfo;
import com.shanjin.manager.dao.AgentDao;
import com.shanjin.manager.dao.impl.AgentDaoImpl;
import com.shanjin.manager.service.AgentService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public class AgentServiceImpl implements AgentService {

	private AgentDao agentDao=new AgentDaoImpl();
	
	/** 审核代理商充值 */
	public Boolean AuditAgentCharge(Map<String, String[]> param) {
		Boolean flag=agentDao.AuditAgentCharge(param);
		return flag;
	}
	/** 获取代理商充值记录 */
	public List<Agent> getAgentCharge(Map<String, String[]> param) {
		List<Agent> agents=agentDao.getAgentCharge(param);
		return agents;
	}
	
	/** 代理商充值 */
	public int addAgentCharge(Map<String, String[]> param, String operUserName) {
		int flag=agentDao.addAgentCharge(param,operUserName);
		return flag;
	}

	/** 获取代理商列表 */
	public List<Record> getAgentList(Map<String, String[]> param) {
		List<Record> agentList=agentDao.getAgentList(param);
		return agentList;
	}

	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "订单号"));
		titles.add(new Pair("agent_name", "代理商名称"));
		titles.add(new Pair("charge_time", "操作时间"));
		titles.add(new Pair("provinceDesc", "代理省份"));
		titles.add(new Pair("cityDesc", "代理城市"));
		titles.add(new Pair("userType", "代理类型"));
		titles.add(new Pair("charge_type", "金额类型"));
		titles.add(new Pair("charge_money", "金额"));
		titles.add(new Pair("head_name", "操作人"));
		return titles;
	}

	public List<AgentCharge> exportExcel(Map<String, String[]> param) {
		List<AgentCharge> agents=agentDao.getexportExcel(param);
		return agents;
	}
	public List<SystemUserInfo> getAgent(Map<String, String[]> param) {
		List<SystemUserInfo> agentList=agentDao.getAgent(param);
		return agentList;
	}
	public Boolean deleteAgent(Map<String, String[]> param) {
		Boolean flag=agentDao.deleteAgent(param);
		return flag;
	}
	public List<AppInfo> getAppByAgentId(Map<String, String[]> param) {
		List<AppInfo> appInfo=agentDao.getAppByAgentId(param);
		return appInfo;
	}
	@Override
	public Boolean deleteAgentCharge(Map<String, String[]> param) {
		Boolean flag=agentDao.deleteAgentCharge(param);
		return flag;
	}
	
	@Override
	public List<AgentEmployee> getAgentEmployeeList(
			Map<String, String[]> param) {
		List<AgentEmployee> agentEmployeeList=agentDao.getAgentEmployeeList(param);
		return agentEmployeeList;
	}
	@Override
	public Boolean deleteAgentEmployee(Map<String, String[]> param) {
		Boolean flag=agentDao.deleteAgentEmployee(param);
		return flag;
	}
	@Override
	public int editAgentEmployee(Map<String, String[]> param) {
		int flag=agentDao.editAgentEmployee(param);
		return flag;
	}
	@Override
	public int addAgentEmployee(Map<String, String[]> param) {
		int flag=agentDao.addAgentEmployee(param);
		return flag;
	}
	@Override
	public List<AgentEmployee> exportAgentEmployeeExcel(
			Map<String, String[]> param) {
		List<AgentEmployee> agentEmployeeList=agentDao.exportAgentEmployeeExcel(param);
		return agentEmployeeList;
	}
	@Override
	public List<Pair> getExportEmployeeTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "员工号"));
		titles.add(new Pair("name", "姓名"));
		titles.add(new Pair("join_time", "加入时间"));
		titles.add(new Pair("provinceName", "省份"));
		titles.add(new Pair("cityName", "城市"));
		titles.add(new Pair("phone", "联系方式"));
		titles.add(new Pair("invite_code", "邀请码"));
		titles.add(new Pair("merchant_total", "商户数量"));
		titles.add(new Pair("con_auth_total", "公司认证商户"));
		titles.add(new Pair("per_auth_total", "个人认证商户"));
		titles.add(new Pair("total", "双认证商户数量"));
		return titles;
	}
	@Override
	public List<MerchantsInfo> getMerchantsByinviteCode(
			Map<String, String[]> param) {
		List<MerchantsInfo> merchants=agentDao.getMerchantsByinviteCode(param);
		return merchants;
	}
	@Override
	public List<AgentEmployee> getCheckAgentEmployeeList(
			Map<String, String[]> filterParam) {
		List<AgentEmployee> agentEmployeeList=agentDao.getCheckAgentEmployeeList(filterParam);
		return agentEmployeeList;
	}
	@Override
	public List<AgentEmployee> exportCheckAgentEmployeeExcel(
			Map<String, String[]> filterParam) {
		List<AgentEmployee> agentEmployeeList=agentDao.exportCheckAgentEmployeeExcel(filterParam);
		return agentEmployeeList;
	}
	
}
