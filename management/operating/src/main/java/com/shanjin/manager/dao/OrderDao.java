package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.OrderInfo;

public interface OrderDao {

	List<OrderInfo> getOrderList(Map<String, String[]> param);

	Boolean editOrder(Map<String, String[]> param);

	Boolean deleteOrder(String id);

	List<OrderInfo> exportOrderList(Map<String, String[]> param);

	List<Record> getOrderDetail(String orderId, String tableName);

	List<Record> getMerchantsByOrderId(Map<String, String[]> paramMap);

	List<Record> getHzOrderDetail(String orderId, String tableName);
	
	public Map<String,Object> orderStatusDetail(Long orderId);

	List<String> getOrderDet(String orderId, String serviceTypeId);

	List<Record> getPushMerchantsByOrderId(Map<String, String[]> param);

}
