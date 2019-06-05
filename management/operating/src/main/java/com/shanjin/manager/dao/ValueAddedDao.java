package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantAdviserApply;
import com.shanjin.manager.Bean.MerchantPushApply;
import com.shanjin.manager.Bean.MerchantVipApply;

public interface ValueAddedDao {
	
	public List<Record> vipMemberList(Map<String, String> param);
	
	public long vipMemberListSize(Map<String, String> param);
	
	public List<Record> orderPushList(Map<String, String> param);
	
	public long orderPushListSize(Map<String, String> param);
	
	public List<Record> adviserList(Map<String, String> param);
	
	public long adviserListSize(Map<String, String> param);
	
	public boolean deleteVipMembers(String ids);
	
	public boolean deleteOrderPushs(String ids);
	
	public boolean deleteAdvisers(String ids);
	
	public boolean updateVipMemberStatus(String id, int status,Long userId, String userName);
	
	public boolean updateOrderPushStatus(String id, int status,Long userId, String userName);
	
	public boolean updateAdviserStatus(String id, int status,Long userId, String userName);

	public List<Record> getGrabFeeList(Map<String, String[]> param);

	public int addGrabFee(Map<String, String[]> param);

	public Boolean updateGrabFee(Map<String, String[]> param);

	public Boolean deleteGrabFee(Map<String, String[]> param);

	public Boolean editGrabFee(Map<String, String[]> param);

	public List<MerchantVipApply> exportVipMemberExcel(
			Map<String, String> paraMap, String[] stime, String[] etime);

	public List<MerchantPushApply> exportPushApplyExcel(
			Map<String, String> paraMap, String[] stime, String[] etime);

	public List<MerchantAdviserApply> exportAdviserApplyExcel(
			Map<String, String> paraMap, String[] stime, String[] etime);
	
}
