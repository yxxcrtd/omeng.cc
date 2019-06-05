package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderInfo;

public interface ServiceStatisticDao {

	List<MerchantsInfo> getMerchantOrderStatis(Map<String, Object> param);

	List<Record> getOrderProcerStatis(Map<String, String[]> param);

	List<OrderInfo> exportOrderProcerList(Map<String, String[]> filterParam);

	List<AgentEmployee> exportAgentInstallList(Map<String, String[]> filterParam);

	List<Record> getAppType();

	List<Record> getAgentInstallStatis(Map<String, String[]> param);

}
