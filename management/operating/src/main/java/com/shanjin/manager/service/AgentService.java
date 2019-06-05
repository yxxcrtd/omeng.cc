package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.Bean.AgentCharge;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.SystemUserInfo;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface AgentService {

	Boolean AuditAgentCharge(Map<String, String[]> param);

	List<Agent> getAgentCharge(Map<String, String[]> param);

	int addAgentCharge(Map<String, String[]> param, String operUserName);

	List<Record> getAgentList(Map<String, String[]> param);

	List<Pair> getExportTitles();

	List<AgentCharge> exportExcel(Map<String, String[]> param);

	List<SystemUserInfo> getAgent(Map<String, String[]> param);

	Boolean deleteAgent(Map<String, String[]> param);

	List<AppInfo> getAppByAgentId(Map<String, String[]> param);

	Boolean deleteAgentCharge(Map<String, String[]> param);

	List<AgentEmployee> getAgentEmployeeList(Map<String, String[]> filterParam);

	Boolean deleteAgentEmployee(Map<String, String[]> param);

	int editAgentEmployee(Map<String, String[]> param);

	int addAgentEmployee(Map<String, String[]> param);

	List<AgentEmployee> exportAgentEmployeeExcel(
			Map<String, String[]> filterParam);

	List<Pair> getExportEmployeeTitles();

	List<MerchantsInfo> getMerchantsByinviteCode(Map<String, String[]> param);

	List<AgentEmployee> getCheckAgentEmployeeList(
			Map<String, String[]> filterParam);

	List<AgentEmployee> exportCheckAgentEmployeeExcel(
			Map<String, String[]> filterParam);

}
