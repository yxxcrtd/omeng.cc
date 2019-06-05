package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantAdviserApply;
import com.shanjin.manager.Bean.MerchantPushApply;
import com.shanjin.manager.Bean.MerchantVipApply;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface IValueAddedService {
	
	public List<Record> vipMemberList(Map<String, String> param);
	
	public long vipMemberListSize(Map<String, String> param);
	
	public List<Record> orderPushList(Map<String, String> param);
	
	public long orderPushListSize(Map<String, String> param);
	
	public List<Record> adviserList(Map<String, String> param);
	
	public long adviserListSize(Map<String, String> param);
	
	public boolean deleteVipMembers(String ids);
	
	public boolean deleteOrderPushs(String ids);
	
	public boolean deleteAdvisers(String ids);
	
	public boolean updateVipMemberStatus(String id, int status,Long userId,String userName);
	
	public boolean updateOrderPushStatus(String id, int status,Long userId,String userName);
	
	public boolean updateAdviserStatus(String id, int status,Long userId,String userName);

	public List<Record> getGrabFeeList(Map<String, String[]> param);

	public int addGrabFee(Map<String, String[]> param);

	public Boolean updateGrabFee(Map<String, String[]> param);

	public Boolean deleteGrabFee(Map<String, String[]> param);

	public Boolean editGrabFee(Map<String, String[]> param);

	public List<MerchantVipApply> exportVipMemberExcel(
			Map<String, String> paraMap, String[] stime, String[] etime);

	public List<Pair> getExportVipMemberTitles();

	public List<MerchantPushApply> exportPushApplyExcel(
			Map<String, String> paraMap, String[] strings, String[] strings2);

	public List<Pair> getExportPushApplyTitles();

	public List<MerchantAdviserApply> exportAdviserApplyExcel(
			Map<String, String> paraMap, String[] strings, String[] strings2);

	public List<Pair> getExportAdviserApplyTitles();

}
