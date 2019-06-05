package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.Bean.UserInfo;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface StatisticService {

	List<Record> getLoginMerchantList(Map<String, String[]> param);

	List<Record> getMerchantPia(Map<String, String[]> param);

	List<Record> getMerchantByTime(Map<String, String[]> param);

	List<Record> getLoginUserList(Map<String, String[]> param);

	List<Record> getUserPia(Map<String, String[]> param);

	List<Record> getUserByTime(Map<String, String[]> param);

	List<Record> getUserOrderList(Map<String, String[]> param);

	List<Record> getUserOrderPia(Map<String, String[]> param);

	List<Record> getUserOrderByTime(Map<String, String[]> param);

	List<OrderInfo> exportOrderTrendExcel(Map<String, String[]> filterParam);

	List<Pair> getExportTrendTitles();

	List<UserInfo> exportUserTrendExcel(Map<String, String[]> filterParam);

	List<MerchantsInfo> exportMerchantTrendExcel(Map<String, String[]> filterParam);

	List<MerchantsInfo> exportLoginMerchantListExcel(Map<String, String[]> param);

	List<UserInfo> exportLoginUserListExcel(Map<String, String[]> param);

	List<OrderInfo> exportUserOrderListExcel(Map<String, String[]> param);

	List<Pair> getExportListTitles(Map<String, String[]> param);



}
