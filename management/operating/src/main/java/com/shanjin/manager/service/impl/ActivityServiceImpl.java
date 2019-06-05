package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.FensiAddRanking;
import com.shanjin.manager.Bean.FensiAddTotal;
import com.shanjin.manager.Bean.MerPhotoStatic;
import com.shanjin.manager.Bean.OrderRewardAccount;
import com.shanjin.manager.dao.ActivityDao;
import com.shanjin.manager.dao.impl.ActivityDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.IActivityService;

public class ActivityServiceImpl implements IActivityService {
	
	private ActivityDao activityDao=new ActivityDaoImpl();
	
	@Override
	public List<FensiAddTotal> getDayAddFensi(Map<String, String[]> param) {
		return activityDao.getDayAddFensi(param);
	}

	@Override
	public List<FensiAddTotal> exportDayAddFensi(Map<String, String[]> param) {
		return activityDao.exportDayAddFensi(param);
	}

	@Override
	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "id"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("head_date", "日期"));
		titles.add(new Pair("increase_merchant_num", "新增服务商"));
		titles.add(new Pair("increase_fans_merchant_num", "增粉服务商"));
		titles.add(new Pair("increase_fans_num", "粉丝数"));
		titles.add(new Pair("average_fans_num", "平均粉丝数"));
		return titles;
	}

	@Override
	public List<FensiAddRanking> getDayFensiRanking(Map<String, String[]> param) {
		return activityDao.getDayFensiRanking(param);
	}

	@Override
	public List<FensiAddRanking> exportFensiAddRanking(Map<String, String[]> param) {
		return activityDao.exportFensiAddRanking(param);
	}

	@Override
	public List<Pair> getExportFensiAddRankingTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "id"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("head_date", "日期"));
		titles.add(new Pair("top", "排名"));
		titles.add(new Pair("merchant_name", "服务商名称"));
		titles.add(new Pair("phone", "注册号码"));
		titles.add(new Pair("app_name", "行业"));
		titles.add(new Pair("total_fans_num", "总粉丝数"));
		titles.add(new Pair("yesterday_increase_fans_num", "昨日新增粉丝"));
		titles.add(new Pair("day_before_increase_fans_num", "前日新增粉丝"));
		titles.add(new Pair("increase_rate", "增长比例"));
		return titles;
	}

	@Override
	public List<MerPhotoStatic> getDayMerPhoto(Map<String, String[]> param) {
		return activityDao.getDayMerPhoto(param);
	}

	@Override
	public List<MerPhotoStatic> exportMerPhotoExcel(Map<String, String[]> param) {
		return activityDao.exportMerPhotoExcel(param);
	}

	@Override
	public List<Pair> getExportMerPhotoTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "id"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("area", "区"));
		titles.add(new Pair("head_date", "日期"));
		titles.add(new Pair("totalPhotoCount", "累计上传相片商户"));
		titles.add(new Pair("singleDayCount", "单日上传相片商"));
		return titles;
	}

	@Override
	public List<Record> getDayMerPhotoDetail(Map<String, String[]> param) {
		return activityDao.getDayMerPhotoDetail(param);
	}

	@Override
	public List<Record> getOrderRewardList(Map<String, String[]> param) {
		return activityDao.getOrderRewardList(param);
	}

	@Override
	public Boolean saveOrderReward(Map<String, String[]> param) {
		return activityDao.saveOrderReward(param);
	}

	@Override
	public Boolean deleteOrderReward(Map<String, String[]> param) {
		return activityDao.deleteOrderReward(param);
	}

	@Override
	public int editOrderRewardRule(Map<String, String[]> param) {
		return activityDao.editOrderRewardRule(param);
	}

	@Override
	public List<Record> getOrderRewardOpenCity(Map<String, String[]> param) {
		return activityDao.getOrderRewardOpenCity(param);
	}

	@Override
	public boolean addOrderRewardOpenCity(Map<String, String[]> param) {
		return activityDao.addOrderRewardOpenCity(param);
	}

	@Override
	public boolean deleteOrderRewardOpenCity(Map<String, String[]> param) {
		return activityDao.deleteOrderRewardOpenCity(param);
	}

	@Override
	public List<Record> getOrderRewardOpenService(Map<String, String[]> param) {
		return activityDao.getOrderRewardOpenService(param);
	}

	@Override
	public boolean addOrderRewardOpenService(Map<String, String[]> param) {
		return activityDao.addOrderRewardOpenService(param);
	}

	@Override
	public boolean deleteOrderRewardOpenService(Map<String, String[]> param) {
		return activityDao.deleteOrderRewardOpenService(param);
	}

	@Override
	public List<Record> getOrderRewardAccountList(Map<String, String[]> param) {
		return activityDao.getOrderRewardAccountList(param);
	}

	@Override
	public List<OrderRewardAccount> exportOrderRewardAccountExcel(Map<String, String[]> param) {
		return activityDao.exportOrderRewardAccountExcel(param);
	}

	@Override
	public List<Pair> getExportOrderRewardAccountTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "id"));
		titles.add(new Pair("order_no", "订单号"));
		titles.add(new Pair("activity_name", "活动名称"));
		titles.add(new Pair("payTime", "奖励时间"));
		titles.add(new Pair("merchant_name", "店铺名称"));
		titles.add(new Pair("app_name", "行业"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("service_name", "订单类型"));
		titles.add(new Pair("evaluation", "评价"));
		titles.add(new Pair("pay_price", "订单金额"));
		titles.add(new Pair("amount", "奖励金额"));
		titles.add(new Pair("orderPayType", "付款方式"));
		titles.add(new Pair("isTransfer", "到帐状态"));
		titles.add(new Pair("userPhone", "用户号码"));
		titles.add(new Pair("merPhone", "店铺号码"));
		return titles;
	}

	@Override
	public boolean delOrderReAccount(Map<String, String[]> param,String name) {
		return activityDao.delOrderReAccount(param,name);
	}

	@Override
	public List<Record> getPhotimeByMerId(Map<String, String[]> param) {
		return activityDao.getPhotimeByMerId(param);
	}

	@Override
	public boolean editOrderRewardAccount(Map<String, String[]> param, String operUserName) {
		return activityDao.editOrderRewardAccount(param,operUserName);
	}

}
