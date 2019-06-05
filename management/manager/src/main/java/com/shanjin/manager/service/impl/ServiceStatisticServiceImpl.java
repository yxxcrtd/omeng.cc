package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.dao.CommonDao;
import com.shanjin.manager.dao.ServiceStatisticDao;
import com.shanjin.manager.dao.impl.CommonDaoImpl;
import com.shanjin.manager.dao.impl.ServiceStatisticDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.IServiceStatisticService;

public class ServiceStatisticServiceImpl implements IServiceStatisticService {
	private ServiceStatisticDao serviceStatisticDao=new ServiceStatisticDaoImpl();
	
	@Override
	public List<MerchantsInfo> exportMerchantOrder(Map<String, Object> param) {
		List<MerchantsInfo> orderStatis=serviceStatisticDao.getMerchantOrderStatis(param);
		return orderStatis;
	}

	@Override
	public List<Record> getOrderProcerStatis(Map<String, String[]> param) {
		List<Record> orderProcer=serviceStatisticDao.getOrderProcerStatis(param);
		return orderProcer;
	}

	@Override
	public List<OrderInfo> exportOrderProcerList(Map<String, String[]> filterParam) {
		List<OrderInfo> orderProcer=serviceStatisticDao.exportOrderProcerList(filterParam);
		return orderProcer;
	}

	@Override
	public List<Pair> getExportOrderProcerTitles() {
		
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "服务商ID"));
		titles.add(new Pair("order_no", "订单编号"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("orderTypeName", "服务项目"));
		titles.add(new Pair("join_time", "下单时间"));
		titles.add(new Pair("orderStatusName", "订单状态"));
		titles.add(new Pair("push_count", "近7日推送商家数"));
		titles.add(new Pair("grab_count", "抢单数"));
		titles.add(new Pair("name", "合作服务商"));
		titles.add(new Pair("first_time", "首次抢单时间"));
		titles.add(new Pair("confirm_time", "选定服务商时间"));
		titles.add(new Pair("finish_time", "服务完成时间"));
		titles.add(new Pair("deal_time", "付款完成时间"));
		titles.add(new Pair("cacel_time", "用户关闭时间"));
		titles.add(new Pair("over_time", "订单过期时间"));
		titles.add(new Pair("orderPayType", "付款方式"));
		titles.add(new Pair("isDel", "删除状态"));
		return titles;
	}

	@Override
	public List<AgentEmployee> exportAgentInstallList(Map<String, String[]> filterParam) {
		List<AgentEmployee> agentInstallList=serviceStatisticDao.exportAgentInstallList(filterParam);
		return agentInstallList;
	}

	@Override
	public List<Pair> getExportAgentInstallTitles() {
		List<Record> appList=serviceStatisticDao.getAppType();
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("name", "姓名"));
		titles.add(new Pair("phone", "号码"));
		titles.add(new Pair("invite_code", "邀请码"));
		if(appList!=null&&appList.size()>0){
			for(Record re:appList){
				titles.add(new Pair(re.getStr("alias"), re.getStr("name")));	
			}
		}
		return titles;
	}

	@Override
	public List<Record> getAgentInstallStatis(Map<String, String[]> param) {
		List<Record> agentInstallList=serviceStatisticDao.getAgentInstallStatis(param);
		return agentInstallList;
	}

	@Override
	public List<Pair> getMerchantOrderTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("name", "服务商名称"));
		titles.add(new Pair("phone", "注册号码"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("auth_type", "认证类型"));
		titles.add(new Pair("auth_status", "认证状态"));
		titles.add(new Pair("join_time", "开店时间"));
		titles.add(new Pair("address", "地址"));
		titles.add(new Pair("app_name", "行业类型"));
		titles.add(new Pair("service_type", "服务项目"));
		titles.add(new Pair("push_count", "近7日累计推荐次数"));
		titles.add(new Pair("grab_count", "累计抢单数"));
		titles.add(new Pair("complete_count", "已完成订单数"));
		titles.add(new Pair("losing_count", "落选订单数"));
		titles.add(new Pair("wait_confirm_count", "待确认订单数"));
		titles.add(new Pair("wait_pay_count", "待付款订单数"));
		titles.add(new Pair("wait_evaluat_count", "待评价订单数"));
		return titles;
	}

}
