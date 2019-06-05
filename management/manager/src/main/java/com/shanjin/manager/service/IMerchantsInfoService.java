package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.WithDraw;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface IMerchantsInfoService {

	List<Record> getMerchantsInfoList(Map<String, String[]> param);

	Boolean editMerchants(Map<String, String[]> param);

	List<Record> getEmployeeList(Map<String, String[]> param);

	List<Record> getStoreAudit(Map<String, String[]> param);

	Boolean AuditStore(Map<String, String[]> param,String operName);

	Boolean deleteStore(Map<String, String[]> param);

	Boolean deleteEmployee(Map<String, String[]> param);

	Boolean AuditAllEmployee(Map<String, String[]> param);

	List<Pair> getExportTitles();

	List<MerchantsInfo> exportStoreList(Map<String, String[]> param);

	List<MerchantsInfo> exportEmployeeList(Map<String, String[]> filterParam);

	List<Pair> getExportEmployeeTitles();

	List<WithDraw> getMerchantsWithDraw(Map<String, String[]> param);

	Boolean AuditAllWithDraw(Map<String, String[]> param, String operUserName);

	List<WithDraw> exportWithDrawList(Map<String, String[]> param);

	List<Pair> getExportWithDrawTitles();
	
	Boolean saveEmployee(Map<String, String[]> param);
	
	Boolean saveStore(Map<String, String[]> param);

	Boolean RefuseAuditStore(Map<String, String[]> param,String operName);

	List<Record> getServiceTypeById(Map<String, String[]> param);
	
	public Map<String,Object> merchantDetail(Long merchantId);
	
	public List<String> merchantPicList(Long albumId);

	List<MerchantsInfo> exportStoreAuditExcel(Map<String, String[]> filterParam);

	List<Pair> getExportStoreAuditTitles();

	List<MerchantsInfo> exportTimeStoreList(Map<String, Object> paramMap);

	List<Pair> getTimeExportTitles();

	List<Record> getAuthDetailByMerchantId(Map<String, String[]> param);

	List<Record> getMerchantsInfoListForFensi(Map<String, String[]> param);

	Boolean addMerchantsInfoForFensi(Map<String, String[]> param);

	List<Record> getPayDeatilByMerId(Map<String, String[]> param);

}
