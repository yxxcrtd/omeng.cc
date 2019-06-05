package com.shanjin.manager.log.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.GroupRole;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.Bean.UserInfo;
import com.shanjin.manager.service.AgentService;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.StatisticService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.AgentServiceImpl;
import com.shanjin.manager.service.impl.StatisticServiceImpl;
import com.shanjin.manager.utils.Util;

/**
 * 统计相关信息
 * @author lijie
 *
 */
public class StatisticController extends Controller {
	private StatisticService statisticService = new StatisticServiceImpl();
	protected ExportService service = ExportService.service;
	//入驻商户统计信息
	public void loginMerchantShow(){
				render("loginMerchant.jsp");
		}
	//入驻商户趋势曲线图
	public void loginMerchantTrend(){
			render("merchantTrend.jsp");
		}
	//注册用户统计信息
	public void loginUserShow(){
					render("loginUser.jsp");
			}
	//注册用户趋势曲线图
	public void loginUserTrend(){
				render("userTrend.jsp");
			}
	//订单统计信息
	public void userOrderShow(){
					render("userOrder.jsp");
			}
	//订单趋势曲线图
	public void userOrderTrend(){
				render("userOrderTrend.jsp");
			}
	/** 获取商户入驻统计信息列表 */
	public void getLoginMerchantList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getLoginMerchantList(param);
		if (terminalList != null && terminalList.size() > 0) {
			long total = terminalList.get(0).getLong("total");
			this.renderJson(new NormalResponse(terminalList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 获取商户入驻饼状图 */
	public void getMerchantPia() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getMerchantPia(param);
		this.renderJson(new NormalResponse(terminalList));
	}
	
	/** 获取商户入驻趋势曲线图 */
	public void getMerchantByTime() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getMerchantByTime(param);
		this.renderJson(new NormalResponse(terminalList));
	}
	
	/** 获取用户注册统计信息列表 */
	public void getLoginUserList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getLoginUserList(param);
		if (terminalList != null && terminalList.size() > 0) {
			long total = terminalList.get(0).getLong("total");
			this.renderJson(new NormalResponse(terminalList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 获取用户注册饼状图 */
	public void getUserPia() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getUserPia(param);
		this.renderJson(new NormalResponse(terminalList));
	}
	
	/** 获取用户注册趋势曲线图 */
	public void getUserByTime() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getUserByTime(param);
		this.renderJson(new NormalResponse(terminalList));
	}
	
	/** 获取订单统计信息列表 */
	public void getUserOrderList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getUserOrderList(param);
		if (terminalList != null && terminalList.size() > 0) {
			long total = terminalList.get(0).getLong("total");
			this.renderJson(new NormalResponse(terminalList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 获取新增订单饼状图 */
	public void getUserOrderPia() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getUserOrderPia(param);
		this.renderJson(new NormalResponse(terminalList));
	}
	/** 获取商户入驻趋势曲线图 */
	public void getUserOrderByTime() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> terminalList = statisticService.getUserOrderByTime(param);
		this.renderJson(new NormalResponse(terminalList));
	}
	
	// 导出新增订单趋势记录
	public void exportOrderTrendExcel() {
		Map<String, String[]> param = this.getParaMap();
		List<OrderInfo> list = statisticService.exportOrderTrendExcel(param); // 查询数据
		List<Pair> usetitles = statisticService.getExportTrendTitles();
		String fileName="新增订单趋势记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
		}
	// 导出新增用户趋势记录
		public void exportUserTrendExcel() {
			Map<String, String[]> param = this.getParaMap();
			List<UserInfo> list = statisticService.exportUserTrendExcel(param); // 查询数据
			List<Pair> usetitles = statisticService.getExportTrendTitles();
			String fileName="新增用户趋势记录";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
			}
		// 导出新增商户趋势记录
		public void exportMerchantTrendExcel() {
			Map<String, String[]> param = this.getParaMap();
			List<MerchantsInfo> list = statisticService.exportMerchantTrendExcel(param); // 查询数据
			List<Pair> usetitles = statisticService.getExportTrendTitles();
			String fileName="新增商户趋势记录";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
			}
		
		// 导出商户记录
		public void exportLoginMerchantListExcel() {
			Map<String, String[]> param = this.getParaMap();
			List<MerchantsInfo> list = statisticService.exportLoginMerchantListExcel(param); // 查询数据
			List<Pair> usetitles = statisticService.getExportListTitles(param);
			String fileName="商户統計记录";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
		}
		// 导出用戶记录
		public void exportLoginUserListExcel() {
			Map<String, String[]> param = this.getParaMap();
			List<UserInfo> list = statisticService.exportLoginUserListExcel(param); // 查询数据
			List<Pair> usetitles = statisticService.getExportListTitles(param);
			String fileName="用戶統計记录";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
		}
		

		// 导出新增订单趋势记录
		public void exportUserOrderListExcel() {
			Map<String, String[]> param = this.getParaMap();
			List<OrderInfo> list = statisticService.exportUserOrderListExcel(param); // 查询数据
			List<Pair> usetitles = statisticService.getExportListTitles(param);
			String fileName="订单統計记录";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
			}
}
