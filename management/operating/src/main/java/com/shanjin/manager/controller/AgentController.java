package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.Bean.AgentCharge;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.SystemUserInfo;
import com.shanjin.manager.Bean.UserSession;
import com.shanjin.manager.service.AgentService;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.AgentServiceImpl;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;
/**
 * 代理商相关信息
 * @author lijie
 *
 */
public class AgentController extends Controller {
	private AgentService agentService = new AgentServiceImpl();
	protected ExportService service = ExportService.service;
	//代理商添加记录
	public void agentShow(){
		render("agent.jsp");
	}
	//代理商扣费记录
	public void agentConsumeShow() {
		render("agentConsume.jsp");
	}
	//代理商充值记录
	public void index() {
		render("agentCharge.jsp");
	}

	//代理商员工记录
	public void agentEmpolyeeShow() {
			render("agentEmployee.jsp");
		}
	//代理商员工考核
	public void checkAgentEmpolyeeShow() {
				render("checkAgentEmployee.jsp");
			}
	/** 获取代理商充值记录 */
	public void getAgentCharge() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam = Util.getAgentCharge(param,session);
		List<Agent> agents = agentService.getAgentCharge(filterParam);
		if (agents != null && agents.size() > 0) {
			long total = agents.get(0).getTotal();
			this.renderJson(new NormalResponse(agents, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}

	/** 审核代理商充值 */
	public void AuditAgentCharge() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = agentService.AuditAgentCharge(param);
		this.renderJson(flag);
	}
	/** 删除代理商操作记录 */
	public void deleteAgentCharge() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = agentService.deleteAgentCharge(param);
		this.renderJson(flag);
	}
	
	/** 代理商充值 */
	public void addAgentCharge() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		int flag = agentService.addAgentCharge(param,operUserName);
		if(flag==1){
			this.renderJson(new NormalResponse(flag, "充值成功"));
		}else if(flag==2){
			this.renderJson(new NormalResponse(flag, "扣费成功"));
		}else{
			this.renderJson(new NormalResponse(flag, "操作失败，请稍后重试"));
		}
	}

	/** 获取代理商列表 */
	public void getAgentList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> agentList = agentService.getAgentList(param);
		if (agentList != null && agentList.size() > 0) {
			long total = agentList.get(0).getLong("total");
			this.renderJson(new NormalResponse(agentList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	// 导出代理商消费记录信息
	public void exportExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam = Util.getAgentCharge(param,session);
		List<AgentCharge> list = agentService.exportExcel(filterParam); 
		// 查询数据
		List<Pair> usetitles = agentService.getExportTitles();
		String fileName="代理商消费记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	
	//************************************代理商充值记录end************************************ 
	
	/** 获取代理商添加记录 */
	public void getAgent() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam = Util.getParams(param,session);
		List<SystemUserInfo> agents = agentService.getAgent(filterParam);
		if (agents != null && agents.size() > 0) {
			long total = agents.get(0).getTotal();
			this.renderJson(new NormalResponse(agents, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}
	
	/** 获取代理商添加记录 */
	public void deleteAgent() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = agentService.deleteAgent(param);
		this.renderJson(flag);
	}
	/** 获取代理商代理项目app */
	public void getAppByAgentId() {
		Map<String, String[]> param = this.getParaMap();
		List<AppInfo> appName = agentService.getAppByAgentId(param);
		if (appName != null && appName.size() > 0) {
			long total = appName.get(0).getTotal();
			this.renderJson(new NormalResponse(appName, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}
	
	/** 获取代理商员工列表 */
	public void getAgentEmployeeList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam = Util.getAgentEmployeeParams(param,session);
		List<AgentEmployee> agentEmployeeList = agentService.getAgentEmployeeList(filterParam);
		if (agentEmployeeList != null && agentEmployeeList.size() > 0) {
			long total = agentEmployeeList.get(0).getTotal();
			this.renderJson(new NormalResponse(agentEmployeeList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	// 导出代理商员工记录信息
		public void exportAgentEmployeeExcel() {
			Map<String, String[]> param = this.getParaMap();
			HttpSession session=this.getSession();
			Map<String, String[]> filterParam = Util.getAgentEmployeeParams(param,session);
			List<AgentEmployee> list = agentService.exportAgentEmployeeExcel(filterParam); 
			// 查询数据
			List<Pair> usetitles = agentService.getExportEmployeeTitles();
			String fileName="代理商员工记录";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
		}
		
	// 导出代理商员工考核记录
	public void exportCheckAgentEmployeeExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		Map<String, String[]> filterParam = Util.getAgentEmployeeParams(param,
				session);
		List<AgentEmployee> list = agentService
				.exportCheckAgentEmployeeExcel(filterParam);
		// 查询数据
		List<Pair> usetitles = agentService.getExportEmployeeTitles();
		String fileName = "代理商员工记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles, fileName,
				0);
		renderNull();
	}
	/** 删除代理商员工记录 */
	public void deleteAgentEmployee() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = agentService.deleteAgentEmployee(param);
		this.renderJson(flag);
	}
	
	/** 编辑代理商员工记录 */
	public void editAgentEmployee() {
		Map<String, String[]> param = this.getParaMap();
		int flag = agentService.editAgentEmployee(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "修改成功！";
		}else if (flag == 2){
			msg = "邀请码已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	/** 添加代理商员工记录 */
	public void addAgentEmployee() {
		Map<String, String[]> param = this.getParaMap();
		int flag = agentService.addAgentEmployee(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功！";
		}else if (flag == 2){
			msg = "邀请码已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	// 获取代理商员工邀请的商户
			public void getMerchantsByinviteCode() {
				Map<String, String[]> param = this.getParaMap();
				List<MerchantsInfo> merchants = agentService.getMerchantsByinviteCode(param);
				if (merchants != null && merchants.size() > 0) {
					long total = merchants.get(0).getTotal();
					this.renderJson(new NormalResponse(merchants, total));

				} else {
					this.renderJson(new EmptyResponse());

				}
			}
  /** 获取代理商员工列表 */
	public void getCheckAgentEmployeeList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam = Util.getAgentEmployeeParams(param,session);
		List<AgentEmployee> agentEmployeeList = agentService.getCheckAgentEmployeeList(filterParam);
		if (agentEmployeeList != null && agentEmployeeList.size() > 0) {
				long total = agentEmployeeList.get(0).getTotal();
				this.renderJson(new NormalResponse(agentEmployeeList, total));
		} else {
				this.renderJson(new EmptyResponse());
				}
		}		
}
