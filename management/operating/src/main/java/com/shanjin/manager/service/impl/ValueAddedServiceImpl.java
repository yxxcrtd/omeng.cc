package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantAdviserApply;
import com.shanjin.manager.Bean.MerchantPushApply;
import com.shanjin.manager.Bean.MerchantVipApply;
import com.shanjin.manager.dao.ValueAddedDao;
import com.shanjin.manager.dao.impl.ValueAddedDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.IValueAddedService;

public class ValueAddedServiceImpl implements IValueAddedService{
	private ValueAddedDao valueAddedDao = new ValueAddedDaoImpl();

	@Override
	public List<Record> vipMemberList(Map<String, String> param) {
		return valueAddedDao.vipMemberList(param);
	}

	@Override
	public long vipMemberListSize(Map<String, String> param) {
		return valueAddedDao.vipMemberListSize(param);
	}

	@Override
	public List<Record> orderPushList(Map<String, String> param) {
		return valueAddedDao.orderPushList(param);
	}

	@Override
	public long orderPushListSize(Map<String, String> param) {
		return valueAddedDao.orderPushListSize(param);
	}

	@Override
	public List<Record> adviserList(Map<String, String> param) {
		return valueAddedDao.adviserList(param);
	}

	@Override
	public long adviserListSize(Map<String, String> param) {
		return valueAddedDao.adviserListSize(param);
	}

	@Override
	public boolean deleteVipMembers(String ids) {
		return valueAddedDao.deleteVipMembers(ids);
	}

	@Override
	public boolean deleteOrderPushs(String ids) {
		return valueAddedDao.deleteOrderPushs(ids);
	}

	@Override
	public boolean deleteAdvisers(String ids) {
		return valueAddedDao.deleteAdvisers(ids);
	}

	@Override
	public boolean updateVipMemberStatus(String id, int status,Long userId,String userName) {
		return valueAddedDao.updateVipMemberStatus(id, status ,userId,userName);
	}

	@Override
	public boolean updateOrderPushStatus(String id, int status,Long userId,String userName) {
		return valueAddedDao.updateOrderPushStatus(id, status ,userId,userName);
	}

	@Override
	public boolean updateAdviserStatus(String id, int status,Long userId,String userName) {
		return valueAddedDao.updateAdviserStatus(id, status ,userId,userName);
	}

	@Override
	public List<Record> getGrabFeeList(Map<String, String[]> param) {
		return valueAddedDao.getGrabFeeList(param);
	}

	@Override
	public int addGrabFee(Map<String, String[]> param) {
		return valueAddedDao.addGrabFee(param);
	}

	@Override
	public Boolean updateGrabFee(Map<String, String[]> param) {
		return valueAddedDao.updateGrabFee(param);
	}

	@Override
	public Boolean deleteGrabFee(Map<String, String[]> param) {
		return valueAddedDao.deleteGrabFee(param);
	}

	@Override
	public Boolean editGrabFee(Map<String, String[]> param) {
		return valueAddedDao.editGrabFee(param);
	}

	@Override
	public List<MerchantVipApply> exportVipMemberExcel(
			Map<String, String> paraMap, String[] stime, String[] etime) {
		return valueAddedDao.exportVipMemberExcel(paraMap,stime,etime);
	}

	@Override
	public List<Pair> getExportVipMemberTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "编号"));
		titles.add(new Pair("merchantName", "服务商名称"));
		titles.add(new Pair("telephone", "联系方式"));
		titles.add(new Pair("address", "地址"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("appName", "app类型"));
		titles.add(new Pair("applyTime", "申请时间"));
		titles.add(new Pair("confirmTime", "确认时间"));
		titles.add(new Pair("openTime", "开通时间"));
		titles.add(new Pair("applyStatus", "状态"));
		
		return titles;
	}

	@Override
	public List<MerchantPushApply> exportPushApplyExcel(
			Map<String, String> paraMap, String[] stime, String[] etime) {
		return valueAddedDao.exportPushApplyExcel(paraMap,stime,etime);
	}

	@Override
	public List<Pair> getExportPushApplyTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "编号"));
		titles.add(new Pair("merchantName", "服务商名称"));
		titles.add(new Pair("telephone", "联系方式"));
		titles.add(new Pair("address", "地址"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("appName", "app类型"));
		titles.add(new Pair("applyTime", "申请时间"));
		titles.add(new Pair("applyNum", "申请额度"));
		titles.add(new Pair("confirmTime", "确认时间"));
		titles.add(new Pair("openTime", "开通时间"));
		titles.add(new Pair("applyStatus", "状态"));
		return titles;
	}

	@Override
	public List<MerchantAdviserApply> exportAdviserApplyExcel(
			Map<String, String> paraMap, String[] stime, String[] etime) {
		return valueAddedDao.exportAdviserApplyExcel(paraMap,stime,etime);
	}

	@Override
	public List<Pair> getExportAdviserApplyTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "编号"));
		titles.add(new Pair("merchantName", "服务商名称"));
		titles.add(new Pair("telephone", "联系方式"));
		titles.add(new Pair("address", "地址"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("appName", "app类型"));
		titles.add(new Pair("applyTime", "申请时间"));
		titles.add(new Pair("applyNum", "申请额度"));
		titles.add(new Pair("confirmTime", "确认时间"));
		titles.add(new Pair("openTime", "开通时间"));
		titles.add(new Pair("applyStatus", "状态"));
		return titles;
	}
}
