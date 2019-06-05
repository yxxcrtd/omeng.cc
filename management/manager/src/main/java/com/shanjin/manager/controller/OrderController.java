package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;



import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IOrderService;
import com.shanjin.manager.service.impl.OrderServiceImpl;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class OrderController extends Controller {

	private IOrderService orderService = new OrderServiceImpl();
	protected ExportService service = ExportService.service;

	public void index() {
		String start_time =Util.getLastMonth();
		this.setAttr("start_time", start_time);
		this.render("order.jsp");
	}
	
	/** 获取所有订单列表*/
	public void getOrderList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getOrderParams(param,session);
		List<OrderInfo> orderList = orderService.getOrderList(filterParam);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getTotal();
			this.renderJson(new NormalResponse(orderList, total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 编辑订单*/
	public void editOrder() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = orderService.editOrder(param);
		this.renderJson(flag);
	}

	/** 删除订单*/
	public void deleteOrder() {
		String id = this.getPara("id");
		Boolean flag = orderService.deleteOrder(id);
		this.renderJson(flag);
	}

	// 导出订单信息
	public void exportExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getOrderParams(param,session);
		List<OrderInfo> list = orderService.exportOrderList(filterParam); // 查询数据
		List<Pair> usetitles = orderService.getExportTitles();
		String fileName="订单信息";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	/**查询订单详情*/
	public void getOrderDetail() {
		String orderId = this.getPara("order_id");
		String appType=this.getPara("app_type");
		String serviceType = this.getPara("service_type");
		List<Record> orderList = orderService.getOrderDetail(orderId,appType,serviceType);
		if (orderList != null && orderList.size() > 0) {
			this.renderJson(new NormalResponse(orderList));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 通过订单id获取商户*/
	public void getMerchantsByOrderId(){
		Map<String, String[]> param = this.getParaMap();
		List<Record> orderList = orderService.getMerchantsByOrderId(param);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getLong("total");
			this.renderJson(new NormalResponse(orderList,total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**订单流程详情页*/
	public void orderStatusDetail(){
		Long orderId = StringUtil.nullToLong(this.getPara("orderId"));
		String serviceTypeId = this.getPara("serviceTypeId");
		Map<String,Object> orderStatusDetail = orderService.orderStatusDetail(orderId);
		List<String> list=orderService.getOrderDet(orderId.toString(), serviceTypeId);
		this.setAttr("data", orderStatusDetail);
		this.setAttr("list", list);
		render("/view/order/orderStatusDetail.jsp");
	}
	
	/** 通过订单id获取推送商户*/
	public void getPushMerchantsByOrderId(){
		Map<String, String[]> param = this.getParaMap();
		List<Record> orderList = orderService.getPushMerchantsByOrderId(param);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getLong("total");
			this.renderJson(new NormalResponse(orderList,total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
}
