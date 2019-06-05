package com.shanjin.manager.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AgentCharge;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.MerchantAdviserApply;
import com.shanjin.manager.Bean.MerchantPushApply;
import com.shanjin.manager.Bean.MerchantVipApply;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.sso.bean.SystemUserInfo;
import com.shanjin.sso.bean.UserSession;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IValueAddedService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.ValueAddedServiceImpl;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

/**
 * 增值服务相关
 * 
 * @author Huang yulai
 *
 */
public class ValueAddedController extends Controller {

	private IValueAddedService valueAddedService = new ValueAddedServiceImpl();
	protected ExportService service = ExportService.service;

	public void vipMemberIndex() {
		render("vipMembers.jsp");
	}

	public void orderPushApplyIndex() {
		render("orderPushApply.jsp");
	}

	public void adviserApplyIndex() {
		render("adviserApply.jsp");
	}

	public void grabFeeShow() {
		render("grabFee.jsp");
	}

	public void vipMemberApplyList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		Map<String, String> paraMap = Util.getvipMemberApplyFilter(param,
				session);
		if (paraMap.get("appTypeBoo").equals("false")) {
			// 代理商未代理该项目
			this.renderJson(new EmptyResponse());
			return;
		}
		List<Record> list = valueAddedService.vipMemberList(paraMap);
		if (list != null && list.size() > 0) {
			long total = valueAddedService.vipMemberListSize(paraMap);
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	public void editVipMemberStatus() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = valueAddedService.editVipMemberStatus(param);
		this.renderJson(flag);
	}
	
	
	public void orderPushApplyList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		Map<String, String> paraMap = Util.getvipMemberApplyFilter(param,
				session);
		if (paraMap.get("appTypeBoo").equals("false")) {
			// 代理商未代理该项目
			this.renderJson(new EmptyResponse());
			return;
		}
		List<Record> list = valueAddedService.orderPushList(paraMap);
		if (list != null && list.size() > 0) {
			long total = valueAddedService.orderPushListSize(paraMap);
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	public void adviserApplyList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		Map<String, String> paraMap = Util.getvipMemberApplyFilter(param,
				session);
		if (paraMap.get("appTypeBoo").equals("false")) {
			// 代理商未代理该项目
			this.renderJson(new EmptyResponse());
			return;
		}
		List<Record> list = valueAddedService.adviserList(paraMap);
		if (list != null && list.size() > 0) {
			long total = valueAddedService.adviserListSize(paraMap);
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	public void deleteVipMemberApply() {
		String ids = this.getPara("ids");
		Boolean flag = valueAddedService.deleteVipMembers(ids);
		this.renderJson(flag);
	}

	public void deleteOrderPushApply() {
		String ids = this.getPara("ids");
		Boolean flag = valueAddedService.deleteOrderPushs(ids);
		this.renderJson(flag);
	}

	public void deleteAdviserApply() {
		String ids = this.getPara("ids");
		Boolean flag = valueAddedService.deleteAdvisers(ids);
		this.renderJson(flag);
	}

	/**
	 * 确认收款
	 */
	public void confirmVipMemberStatus() {
		String id = this.getPara("id");
		int status = StringUtil.nullToInteger(this.getPara("status"));
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		int balance = CommonUtil.getUserBalance(operUserId);
		int result = 0;// 0:失败 1：成功 2：余额不足
		if (balance < 5800) {
			// 余额不足
			result = 2;

		} else {
			Boolean flag = valueAddedService.updateVipMemberStatus(id, status,
					operUserId, operUserName);
			if (flag)
				result = 1;
		}
		this.renderJson(result);
	}

	/**
	 * 关闭申请，置无效
	 */
	public void closeVipMemberStatus() {
		String id = this.getPara("id");
		int status = StringUtil.nullToInteger(this.getPara("status"));
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		Boolean flag = valueAddedService.updateVipMemberStatus(id, status,
				operUserId, operUserName);
		this.renderJson(flag);
	}

	/**
	 * 开通功能
	 */
	public void openVipMemberStatus() {
		String id = this.getPara("id");
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		int status = 2;
		Boolean flag = valueAddedService.updateVipMemberStatus(id, status,
				operUserId, operUserName);
		this.renderJson(flag);
	}

	/**
	 * 确认收款
	 */
	public void confirmOrderPushStatus() {
		String id = this.getPara("id");
		int status = StringUtil.nullToInteger(this.getPara("status"));
		int num = StringUtil.nullToInteger(this.getPara("num"));
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		int balance = CommonUtil.getUserBalance(operUserId);
		int result = 0;// 0:失败 1：成功 2：余额不足
		if (balance < num) {
			// 余额不足
			result = 2;
		} else {
			Boolean flag = valueAddedService.updateOrderPushStatus(id, status,0f,
					operUserId, operUserName);
			if (flag)
				result = 1;
		}
		this.renderJson(result);
	}

	/**
	 * 关闭申请，置无效
	 */
	public void closeOrderPushStatus() {
		String id = this.getPara("id");
		int status = StringUtil.nullToInteger(this.getPara("status"));
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		Boolean flag = valueAddedService.updateOrderPushStatus(id, status,0f,
				operUserId, operUserName);
		this.renderJson(flag);
	}

	/**
	 * 开通功能
	 */
	public void openOrderPushStatus() {
		String id = this.getPara("id");
		float topup_money = StringUtil.nullToFloat(this.getPara("price"));
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		int status = 2;
		Boolean flag = valueAddedService.updateOrderPushStatus(id, status,topup_money,
				operUserId, operUserName);
		this.renderJson(flag);
	}

	/**
	 * 确认收款
	 */
	public void confirmAdviserStatus() {
		String id = this.getPara("id");
		int status = StringUtil.nullToInteger(this.getPara("status"));
		int num = StringUtil.nullToInteger(this.getPara("num"));
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		int balance = CommonUtil.getUserBalance(operUserId);
		int result = 0;// 0:失败 1：成功 2：余额不足
		if (balance < num * 1500) {
			// 余额不足
			result = 2;
		} else {
			Boolean flag = valueAddedService.updateAdviserStatus(id, status,
					operUserId, operUserName);
			if (flag)
				result = 1;
		}
		this.renderJson(result);
	}

	/**
	 * 关闭申请，置无效
	 */
	public void closeAdviserStatus() {
		String id = this.getPara("id");
		int status = StringUtil.nullToInteger(this.getPara("status"));
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		Boolean flag = valueAddedService.updateAdviserStatus(id, status,
				operUserId, operUserName);
		this.renderJson(flag);
	}

	/**
	 * 开通功能
	 */
	public void openAdviserStatus() {
		String id = this.getPara("id");
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session
				.getAttribute(UserSession._USER_NAME));
		Long operUserId = StringUtil.nullToLong(session
				.getAttribute(UserSession._USER_ID));
		int status = 2;
		Boolean flag = valueAddedService.updateAdviserStatus(id, status,
				operUserId, operUserName);
		this.renderJson(flag);
	}

	/**
	 * 获取所有抢单费用操作列表
	 */
	public void getGrabFeeList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> grabFeelist = valueAddedService.getGrabFeeList(param);
		if (grabFeelist != null && grabFeelist.size() > 0) {
			long total = grabFeelist.get(0).getLong("total");
			this.renderJson(new NormalResponse(grabFeelist, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}

	/**
	 * 添加抢单费用操作
	 */
	public void addGrabFee() {
		Map<String, String[]> param = this.getParaMap();
		int flag = valueAddedService.addGrabFee(param);
		if (flag == 1) {
			this.renderJson(new NormalResponse(flag));
		} else if (flag == 2) {
			this.renderJson(new NormalResponse(flag, "该城市此APP类型已添加"));
		} else {
			this.renderJson(new NormalResponse(flag, "保存失败"));
		}
	}

	/**
	 * 编辑抢单费用操作
	 */
	public void editGrabFee() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = valueAddedService.editGrabFee(param);
		this.renderJson(flag);
	}

	/**
	 * 更新抢单费用
	 */
	public void updateGrabFee() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = valueAddedService.updateGrabFee(param);
		this.renderJson(flag);
	}

	/**
	 * 删除抢单费用
	 */
	public void deleteGrabFee() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = valueAddedService.deleteGrabFee(param);
		this.renderJson(flag);
	}

	// 导出vip会员申请记录信息
	public void exportVipMemberExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		Map<String, String> paraMap = Util.getvipMemberApplyFilter(param,
				session);
		List<MerchantVipApply> list;
		if (paraMap.get("appTypeBoo").equals("false")) {
			// 代理商未代理该项目
			list = new ArrayList<MerchantVipApply>();
		} else {
			list = valueAddedService.exportVipMemberExcel(paraMap,
					param.get("stime"), param.get("etime"));
		}
		// 查询数据
		List<Pair> usetitles = valueAddedService.getExportVipMemberTitles();
		String fileName = "vip会员申请记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles, fileName,
				0);
		renderNull();
	}

	// 导出订单推送金额申请记录信息
	public void exportPushApplyExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		Map<String, String> paraMap = Util.getvipMemberApplyFilter(param,
				session);
		List<MerchantPushApply> list;
		if (paraMap.get("appTypeBoo").equals("false")) {
			// 代理商未代理该项目
			list = new ArrayList<MerchantPushApply>();
		} else {
			list = valueAddedService.exportPushApplyExcel(paraMap,
					param.get("stime"), param.get("etime"));
		}
		// 查询数据
		List<Pair> usetitles = valueAddedService.getExportPushApplyTitles();
		String fileName = "订单推送申请记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles, fileName,
				0);
		renderNull();
	}
	// 导出订单推送金额申请记录信息
		public void exportAdviserApplyExcel() {
			Map<String, String[]> param = this.getParaMap();
			HttpSession session = this.getSession();
			Map<String, String> paraMap = Util.getvipMemberApplyFilter(param,
					session);
			List<MerchantAdviserApply> list;
			if (paraMap.get("appTypeBoo").equals("false")) {
				// 代理商未代理该项目
				list = new ArrayList<MerchantAdviserApply>();
			} else {
				list = valueAddedService.exportAdviserApplyExcel(paraMap,
						param.get("stime"), param.get("etime"));
			}
			// 查询数据
			List<Pair> usetitles = valueAddedService.getExportAdviserApplyTitles();
			String fileName = "订单推送申请记录";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles, fileName,
					0);
			renderNull();
		}
}
