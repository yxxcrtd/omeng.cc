package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.Bean.UserInfo;
import com.shanjin.manager.dao.StatisticDao;
import com.shanjin.manager.dao.impl.StatisticDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.StatisticService;
import com.shanjin.manager.utils.StringUtil;

public class StatisticServiceImpl implements StatisticService {
	private StatisticDao statisticService = new StatisticDaoImpl();
	
	@Override
	public List<Record> getLoginMerchantList(Map<String, String[]> param) {
		return statisticService.getLoginMerchantList(param);
	}
	@Override
	public List<Record> getMerchantPia(Map<String, String[]> param) {
		return statisticService.getMerchantPia(param);
	}
	@Override
	public List<Record> getMerchantByTime(Map<String, String[]> param) {
		return statisticService.getMerchantByTime(param);
	}
	@Override
	public List<Record> getLoginUserList(Map<String, String[]> param) {
		return statisticService.getLoginUserList(param);
	}
	@Override
	public List<Record> getUserPia(Map<String, String[]> param) {
		return statisticService.getUserPia(param);
	}
	@Override
	public List<Record> getUserByTime(Map<String, String[]> param) {
		return statisticService.getUserByTime(param);
	}
	@Override
	public List<Record> getUserOrderList(Map<String, String[]> param) {
		return statisticService.getUserOrderList(param);
	}
	@Override
	public List<Record> getUserOrderPia(Map<String, String[]> param) {
		return statisticService.getUserOrderPia(param);
	}
	@Override
	public List<Record> getUserOrderByTime(Map<String, String[]> param) {
		return statisticService.getUserOrderByTime(param);
	}
	@Override
	public List<OrderInfo> exportOrderTrendExcel(
			Map<String, String[]> param) {
		return statisticService.exportOrderTrendExcel(param);
	}
	@Override
	public List<Pair> getExportTrendTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("join_time", "时间"));
		titles.add(new Pair("data", "数量"));
		return titles;
	}
	@Override
	public List<UserInfo> exportUserTrendExcel(
			Map<String, String[]> param) {
		return statisticService.exportUserTrendExcel(param);
	}
	@Override
	public List<MerchantsInfo> exportMerchantTrendExcel(
			Map<String, String[]> param) {
		return statisticService.exportMerchantTrendExcel(param);
	}
	@Override
	public List<MerchantsInfo> exportLoginMerchantListExcel(
			Map<String, String[]> param) {
		return statisticService.exportLoginMerchantListExcel(param);
	}
	@Override
	public List<UserInfo> exportLoginUserListExcel(Map<String, String[]> param) {
		return statisticService.exportLoginUserListExcel(param);
	}
	@Override
	public List<OrderInfo> exportUserOrderListExcel(Map<String, String[]> param) {
		return statisticService.exportUserOrderListExcel(param);
	}
	@Override
	public List<Pair> getExportListTitles(Map<String, String[]> param) {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("terminalNum", "商户数量"));
		String dimension=param.get("dimension")[0];
		String[] dim = null;
		if(!StringUtil.isNull(dimension)){	
			dim=dimension.split(",");
		if(StringUtil.nullToBoolean(dim)){
			 for(String s:dim){
				 if("app_type".equals(s)){
					 titles.add(new Pair("app_type", "app类型")); 
				 }
				 if("province".equals(s)){
					 titles.add(new Pair("province", "省份"));
				 }
				 if("city".equals(s)){
					 titles.add(new Pair("city", "城市"));
				 }
				 if("order_status".equals(s)){
					 titles.add(new Pair("orderStatus", "订单状态"));
				 }
			 } 
		}
		}	
		return titles;
	}
	
}
