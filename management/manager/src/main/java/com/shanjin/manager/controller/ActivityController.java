package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;



import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.FensiAddRanking;
import com.shanjin.manager.Bean.FensiAddTotal;
import com.shanjin.manager.Bean.MerPhotoStatic;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.OrderRewardAccount;
import com.shanjin.manager.Bean.OrderUserRewardAccount;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IActivityService;
import com.shanjin.manager.service.impl.ActivityServiceImpl;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;
import com.shanjin.sso.bean.UserSession;

public class ActivityController extends Controller {

	private IActivityService activityService = new ActivityServiceImpl();
	protected ExportService service = ExportService.service;

	public void index() {
		String join_time = Util.getLastDayByDay();
		this.setAttr("join_time", join_time);
		this.render("fensibaobiao.jsp");
	}

	/** 获取日粉丝增涨量列表*/
	public void getDayAddFensi() {
		Map<String, String[]> param = this.getParaMap();
		List<FensiAddTotal> orderList = activityService.getDayAddFensi(param);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getTotal();
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	// 导出日粉丝增涨量信息
	public void exportExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getOrderParams(param,session);
		List<FensiAddTotal> list = activityService.exportDayAddFensi(filterParam); // 查询数据
		List<Pair> usetitles = activityService.getExportTitles();
		String fileName="日粉丝增长量";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	
	public void fensiRankingindex() {
		String join_time = Util.getLastDayByDay();
		this.setAttr("join_time", join_time);
		this.render("fensiRanking.jsp");
	}
	
	/** 获取粉丝排行榜列表*/
	public void getDayFensiRanking() {
		Map<String, String[]> param = this.getParaMap();
		List<FensiAddRanking> orderList = activityService.getDayFensiRanking(param);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getTotal();
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	// 导出日粉丝增涨量信息
	public void exportExcelFensiAddRanking() {
		Map<String, String[]> param = this.getParaMap();
		List<FensiAddRanking> list = activityService.exportFensiAddRanking(param); // 查询数据
		List<Pair> usetitles = activityService.getExportFensiAddRankingTitles();
		String fileName="粉丝日排行榜";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	
	
	public void merPhotoindex() {
		this.render("merPhoto.jsp");
	}
	
	/** 获取相册列表*/
	public void getDayMerPhoto() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getDayMerPhotoParams(param,session);
		List<MerPhotoStatic> orderList = activityService.getDayMerPhoto(filterParam);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getTotal();
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	// 导出商户相册信息
	public void exportMerPhotoExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getDayMerPhotoParams(param,session);
		List<MerPhotoStatic> list = activityService.exportMerPhotoExcel(filterParam); // 查询数据
		List<Pair> usetitles = activityService.getExportMerPhotoTitles();
		String fileName="店铺相册上传统计";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	
	
	/** 获取服务商上传相册的时间*/
	public void getPhotimeByMerId() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> timeList = activityService.getPhotimeByMerId(param);
		if (timeList != null && timeList.size() > 0) {
			long total = timeList.get(0).getLong("total");
			this.renderJson(new NormalResponse(timeList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	public void merPhotoDetailIndex() {
		String province = StringUtil.nullToString(this.getPara("province"));
		String city = StringUtil.nullToString(this.getPara("city"));
		String area = StringUtil.nullToString(this.getPara("area"));
		this.setAttr("province", province);
		this.setAttr("city", city);
		this.setAttr("area", area);
		this.render("merPhotoDetail.jsp");
	}
	
	/** 获取区相册服务商列表*/
	public void getDayMerPhotoDetail() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> orderList = activityService.getDayMerPhotoDetail(param);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getLong("total");
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	public void orderRewardindex() {
	  String activity_id = StringUtil.nullToString(this.getPara("activity_id"));
	  this.setAttr("activity_id", activity_id);
	  this.render("orderReward.jsp");
	}
	
	/** 获取订单奖励活动列表*/
	public void getOrderRewardList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> orderList = activityService.getOrderRewardList(param);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getLong("total");
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 新增订单奖励活动*/
	public void addOrderReward() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = activityService.saveOrderReward(param);
		this.renderJson(flag);
	}
	
	/** 编辑订单奖励活动列表*/
	public void editOrderReward() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = activityService.saveOrderReward(param);
		this.renderJson(flag);
	}
	
	/** 删除订单奖励活动列表*/
	public void deleteOrderReward() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = activityService.deleteOrderReward(param);
		this.renderJson(flag);
	}
	
	/** 编辑订单奖励活动列表*/
	public void editOrderRewardRule() {
		Map<String, String[]> param = this.getParaMap();
		int flag  = activityService.editOrderRewardRule(param);
		if(flag==1){
			this.renderJson(true);
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
	/** 获取订单奖励活动开放城市页面*/
	public void orderRewardOpenCityindex() {
		  String activity_id = StringUtil.nullToString(this.getPara("activity_id"));
		  this.setAttr("activity_id", activity_id);
		  this.render("orderRewardOpenCity.jsp");
	}
	
	/** 获取订单奖励活动开放城市列表*/
	public void getOrderRewardOpenCity() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> orderList = activityService.getOrderRewardOpenCity(param);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getLong("total");
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	public void addOrderRewardOpenCity() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = activityService.addOrderRewardOpenCity(param);
		this.renderJson(flag);
	}
	
	public void deleteOrderRewardOpenCity() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = activityService.deleteOrderRewardOpenCity(param);
		this.renderJson(flag);
	}
	
	/** 获取订单奖励活动开放服务页面*/
	public void orderRewardOpenServiceindex() {
		  String activity_id = StringUtil.nullToString(this.getPara("activity_id"));
		  this.setAttr("activity_id", activity_id);
		  this.render("orderRewardOpenService.jsp");
	}
	
	/** 获取订单奖励活动开放城市列表*/
	public void getOrderRewardOpenService() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> orderList = activityService.getOrderRewardOpenService(param);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getLong("total");
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	public void addOrderRewardOpenService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = activityService.addOrderRewardOpenService(param);
		this.renderJson(flag);
	}
	
	public void deleteOrderRewardOpenService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = activityService.deleteOrderRewardOpenService(param);
		this.renderJson(flag);
	}
	
	/** 获取订单奖励活动账单页面*/
	public void orderRewardAccountindex() {
		  String activity_id = StringUtil.nullToString(this.getPara("activity_id"));
		  this.setAttr("activity_id", activity_id);
		  this.render("orderRewardAccount.jsp");
	}
	
	/** 获取订单奖励活动账单列表*/
	public void getOrderRewardAccountList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getOrderRewardAccountParams(param,session);
		List<Record> orderList = activityService.getOrderRewardAccountList(filterParam);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getLong("total");
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 获取订单奖励活动账单列表*/
	public void exportOrderRewardAccount() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getOrderRewardAccountParams(param,session);
		List<OrderRewardAccount> list = activityService.exportOrderRewardAccountExcel(filterParam); // 查询数据
		List<Pair> usetitles = activityService.getExportOrderRewardAccountTitles();
		String fileName="订单奖励活动账单";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	/** 订单奖励活动账单处理到帐*/
	public void delOrderReAccount() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		boolean flag = activityService.delOrderReAccount(param,operUserName);
		this.renderJson(flag);
	}
	
	/** 订单奖励活动账单处理到帐*/
	public void editOrderRewardAccount() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		boolean flag = activityService.editOrderRewardAccount(param,operUserName);
		this.renderJson(flag);
	}
	  
	/** 获取订单奖励活动账单查看页面*/
	public void orderRewardAccountbyAgent() {
		  this.render("orderRewardAccountIndex.jsp");
	}
	
	/** 获取用户补贴活动账单页面*/
	public void orderUserRewardindex() {
		  String activity_id = StringUtil.nullToString(this.getPara("activity_id"));
		  this.setAttr("activity_id", activity_id);
		  this.render("orderUserRewardAccount.jsp");
	}
	
	/** 获取用户补贴活动账单列表*/
	public void getOrderUserRewardAccountList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getOrderUserRewardAccountParams(param,session);
		List<Record> orderList = activityService.getOrderUserRewardAccountList(filterParam);
		if (orderList != null && orderList.size() > 0) {
			long total = orderList.get(0).getLong("total");
			this.renderJson(new NormalResponse(orderList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 获取用户补贴活动账单列表*/
	public void exportOrderUserRewardAccount() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getOrderUserRewardAccountParams(param,session);
		List<OrderUserRewardAccount> list = activityService.exportOrderUserRewardAccountExcel(filterParam); // 查询数据
		List<Pair> usetitles = activityService.getExportOrderUserRewardAccountTitles();
		String fileName="用户补贴活动账单";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	/** 用户补贴账单处理到帐*/
	public void delOrderUserReAccount() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		boolean flag = activityService.delOrderUserReAccount(param,operUserName);
		this.renderJson(flag);
	}
	
	/** 用户补贴账单异常处理*/
	public void editOrderUserRewardAccount() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		boolean flag = activityService.editOrderUserRewardAccount(param,operUserName);
		this.renderJson(flag);
	}
}
