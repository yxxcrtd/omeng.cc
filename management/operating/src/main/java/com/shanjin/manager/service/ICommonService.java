package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.Bean.SystemUserInfo;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface ICommonService {


	List<Record> getAppType();
	
	List<Record> getAppTypeByPackType(String packageType);

	List<Record> getMerOrderStatus();

	List<Record> getUserOrderStatus();

	List<Record> getServiceType(String app_type);

	List<Record> getWithdraw();
	
	List<Record> getArea(Long parentId);
	
	List<Record> getAppList();
	
	String getUserPermission(Long userId);
	
	List<AppInfo> userApp(Long userId);

	List<Record> getPredilection(String travel_predilection);

	List<Record> getCleanType(String service_item);

	List<Record> getNannyType(String service_item);

	List<Record> getDietPredilection(String diet_predilection);

	List<Record> getServiceFrequency(String service_frequency);
	
	Map<String, Long> getAllData(SystemUserInfo user);

	List<Record> getBeautifyService(String beautify_service_items);

	List<Record> getRepairPlace(String repair_place);

	List<SystemResource> getAllResouse();

	List<Record> getHouseType(String house_type, String service_type);

	List<Record> getPositionType(String position_type);

//	List<Record> getApplyjobPost(String apply_job_post);

	List<Record> getPxgwItem(String pxgw_service_item);

	List<Record> getFlzxdetail(String flzx_detail_content);

	List<Record> getPosition_type(String position_type);

	List<Record> getApplyJobPost(String apply_job_post);

	List<Record> getDecorationRange(String decoration_range);

	String getAgentType(String userName, Integer userType);

	List<Record> getData();

	List<Record> getwashType(String wash_type);

	List<Record> getAppendService(String append_service);

	List<Record> getVoucherServiceType(String app_type);
	
	List<Record> getServiceTypeForSearch();

	List<Record> getHssyShootStyle(String hssy_shoot_style);

	List<Record> getGrxzShootStyle(String grxz_shoot_style, String sex);

	List<Record> getCommonName(String type, String dictType);

	List<Record> getServiceName(String serviceId, String appType);

	List<Record> showControlChildType(String control_type);

	List<Record> getChinaArea(String parentId);

	List<Record> showControlType();

	List<Record> getAllCatalog();

	List<Record> getServiceByCatId(String app_type);

	List<Record> getGxfwCatalog();

	List<Record> getActivityType();

	List<Record> getEntranceType();

	List<MerchantsInfo> exportExcel();

	List<Pair> getExportTitles();

	List<Record> getServiveCity(Long parentId);

	List<Record> getNewArea(Long parentId);

	List<Record> getOrderObjectStore(String group);

	List<Record> getAllProAndCity(Map<String, String[]> param);

	List<Record> getRewardList(Map<String, String[]> param);

}
