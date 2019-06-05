package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface IServiceStatisticService {

	List<MerchantsInfo> exportMerchantOrder(Map<String, Object> param);

	List<Record> getOrderProcerStatis(Map<String, String[]> param);

	List<OrderInfo> exportOrderProcerList(Map<String, String[]> filterParam);

	List<Pair> getExportOrderProcerTitles();

	List<AgentEmployee> exportAgentInstallList(Map<String, String[]> filterParam);

	List<Pair> getExportAgentInstallTitles();

	List<Record> getAgentInstallStatis(Map<String, String[]> param);

	List<Pair> getMerchantOrderTitles();

}
