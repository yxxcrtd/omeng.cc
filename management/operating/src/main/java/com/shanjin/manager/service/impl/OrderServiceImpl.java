package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.dao.OrderDao;
import com.shanjin.manager.dao.impl.OrderDaoImpl;
import com.shanjin.manager.service.IOrderService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.utils.OrderUtil;

public class OrderServiceImpl implements IOrderService{
	
private OrderDao orderDao=new OrderDaoImpl();

	public List<OrderInfo> getOrderList(Map<String, String[]> param) {
		List<OrderInfo> orderList=orderDao.getOrderList(param);
		return orderList;
	}

	public Boolean editOrder(Map<String, String[]> param) {
		Boolean flag = orderDao.editOrder(param);
		return flag;
	}

	public Boolean deleteOrder(String id) {
		Boolean flag = orderDao.deleteOrder(id);
		return flag;
	}

	public List<OrderInfo> exportOrderList(Map<String, String[]> param) {
		List<OrderInfo> orderList=orderDao.exportOrderList(param);
		return orderList;
	}

	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("order_no", "订单号"));	
		titles.add(new Pair("phone", "会员账号"));
		titles.add(new Pair("join_time", "下单时间"));
		titles.add(new Pair("orderStatusName", "订单状态"));
		titles.add(new Pair("orderTypeName", "服务名称"));
		titles.add(new Pair("merchant_name", "合作服务商"));
		titles.add(new Pair("deal_time", "完成时间"));
		titles.add(new Pair("orderPayType", "付款方式"));
		titles.add(new Pair("order_price", "金额"));
		titles.add(new Pair("province", "省"));
		titles.add(new Pair("city", "市"));
		return titles;
	}

	public List<Record> getOrderDetail(String orderId, String appType,
			String serviceType) {
		
		String tableName=OrderUtil.getTableByOrder(appType, serviceType);
		if(tableName.equals("hz_order_info")){
			return orderDao.getHzOrderDetail(orderId,tableName);
		}
		List<Record> orderList=orderDao.getOrderDetail(orderId,tableName);
		return orderList;
	}

	public List<Record> getMerchantsByOrderId(Map<String, String[]> paramMap) {
		List<Record> merchantsList=orderDao.getMerchantsByOrderId(paramMap);
		return merchantsList;
	}

	@Override
	public Map<String, Object> orderStatusDetail(Long orderId) {
		return orderDao.orderStatusDetail(orderId);
	}

	@Override
	public List<String> getOrderDet(String orderId, String serviceTypeId) {
		return orderDao.getOrderDet(orderId,serviceTypeId);
	}

	@Override
	public List<Record> getPushMerchantsByOrderId(Map<String, String[]> param) {
		List<Record> merchantsList=orderDao.getPushMerchantsByOrderId(param);
		return merchantsList;
	}
}
