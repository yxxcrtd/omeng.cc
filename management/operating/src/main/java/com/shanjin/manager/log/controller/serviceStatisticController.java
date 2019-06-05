package com.shanjin.manager.log.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IServiceStatisticService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.ServiceStatisticServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.Util;

public class serviceStatisticController extends Controller {
	IServiceStatisticService serviceStatistic=new ServiceStatisticServiceImpl();
	protected ExportService service = ExportService.service;
	//服务商业务统计信息
	public void merchantOrderShow(){
		render("merchantOrder1.jsp");
	}
	//订单过程统计信息
	public void orderProcerShow(){
		String start_time =Util.getLastMonth();
		this.setAttr("start_time", start_time);
		render("orderProcer.jsp");
	}
	
	//行业安装信息
	public void agentInstallShow() {
		String start_time = Util.getLastMonth();
		this.setAttr("start_time", start_time);
		render("agentInstall.jsp");
	}
	
	// 下载商户订单的运营信息
	public void downloadExcel() {

		Map<String, String[]> param = this.getParaMap();
		// String fileName="merchant_"+StatisticalUtil.getCurrentTime()+".xlsx";
		String fileName = Util.getExportMerOrderFileName(param);
		if (fileName.equals("")) {
			this.renderJson("");
		} else {
			String filePath = BusinessUtil.manFolder() + BusinessUtil.DIR_STATISTIC + BusinessUtil.PATH_SEPARATOR
					+ BusinessUtil.ORDER + BusinessUtil.PATH_SEPARATOR;
			this.renderJson(BusinessUtil.getFileUrl(filePath + fileName));
		}
	}
	
	/** 订单过程列表 */
	public void getOrderProcerStatis() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getStaticsParams(param,session);
		List<Record> orderProcerList = serviceStatistic.getOrderProcerStatis(filterParam);
		if (orderProcerList != null && orderProcerList.size() > 0) {
			long total = orderProcerList.get(0).get("total");
			this.renderJson(new NormalResponse(orderProcerList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	// 导出订单信息
	public void exportOrderProcerExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getStaticsParams(param,session);
		List<OrderInfo> list = serviceStatistic.exportOrderProcerList(filterParam); // 查询数据
		List<Pair> usetitles = serviceStatistic.getExportOrderProcerTitles();
		String fileName="订单过程统计";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	
	/** 员工安装列表 */
	public void getAgentInstallStatis() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getStaticsInstallParams(param,session);
		List<Record> orderProcerList = serviceStatistic.getAgentInstallStatis(filterParam);
		if (orderProcerList != null && orderProcerList.size() > 0) {
			long total = orderProcerList.get(0).get("total");
			this.renderJson(new NormalResponse(orderProcerList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	// 导出员工安装信息
	public void exportAgentInstallExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getStaticsInstallParams(param,session);
		List<AgentEmployee> list = serviceStatistic.exportAgentInstallList(filterParam); // 查询数据
		List<Pair> usetitles = serviceStatistic.getExportAgentInstallTitles();
		String fileName="员工安装统计";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
		}
}
