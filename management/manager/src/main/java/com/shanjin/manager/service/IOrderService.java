package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface IOrderService {

	List<OrderInfo> getOrderList(Map<String, String[]> param);

	Boolean editOrder(Map<String, String[]> param);

	Boolean deleteOrder(String id);

	List<OrderInfo> exportOrderList(Map<String, String[]> param);

	List<Pair> getExportTitles();

	List<Record> getOrderDetail(String orderId, String appType,
			String serviceType);

	List<Record> getMerchantsByOrderId(Map<String, String[]> paramMap);
	
	public Map<String,Object> orderStatusDetail(Long orderId);

	List<String> getOrderDet(String orderId, String serviceTypeId);

	List<Record> getPushMerchantsByOrderId(Map<String, String[]> param);
	
}
