package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.FYB;
import com.shanjin.manager.dao.CommonDao;
import com.shanjin.manager.dao.impl.CommonDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.ICommonService;
import com.shanjin.manager.Bean.SystemUserInfo;

public class CommonServiceImpl implements ICommonService {
private CommonDao commonDao=new CommonDaoImpl();

    @Override
    public List<Record> getAppTypeByPackType(String packageType) {
	    List<Record> appType= new ArrayList<Record>();
	    appType.add(new Record().set("app_type", "").set("app_name", "全部"));
	    appType.addAll(commonDao.getAppTypeByPackType(packageType));
	    return appType;
    }
	public List<Record> getAppType() {
		List<Record> appType= new ArrayList<Record>();
		appType.add(new Record().set("app_type", "").set("app_name", "全部"));
		appType.addAll(commonDao.getAppType());
		return appType;
	}
	public List<Record> getMerOrderStatus() {
		List<Record> merOrderStatus= new ArrayList<Record>();
		merOrderStatus.add(new Record().set("dict_key", "").set("dict_value", "全部"));
		merOrderStatus.addAll(commonDao.getMerOrderStatus());
		return merOrderStatus;
	}
	public List<Record> getUserOrderStatus() {
		List<Record> userOrderStatus= new ArrayList<Record>();
		userOrderStatus.add(new Record().set("dict_key", "").set("dict_value", "全部"));
		userOrderStatus.addAll(commonDao.getUserOrderStatus());
		return userOrderStatus;
	}
	public List<Record> getServiceType(String app_type) {
		List<Record> serviceType= new ArrayList<Record>();
		serviceType.add(new Record().set("service_type", "").set("service_name", "全部"));
		serviceType.addAll(commonDao.getServiceType(app_type));
		return serviceType;
	}
	public List<Record> getWithdraw() {
		List<Record> withdrawList= new ArrayList<Record>();
		withdrawList.add(new Record().set("id", "").set("dict_value", "全部"));
		withdrawList.addAll(commonDao.getWithdraw());
		return withdrawList;
	}
	public List<Record> getArea(Long parentId) {
		
		return	commonDao.getArea(parentId);
	
	}
	public List<Record> getAppList() {
		return commonDao.getAppList();
	}
	public String getUserPermission(Long userId) {
		return commonDao.getUserPermission(userId);
	}
	
	public List<AppInfo> userApp(Long userId) {
		return commonDao.userApp(userId);
	}
	public Map<String, Long> getAllData(SystemUserInfo user) {
		Map<String, Long> data=commonDao.getAllData(user);
		return data;
	}
	public List<Record> getPredilection(String travel_predilection) {
		return commonDao.getPredilection(travel_predilection);
	}
	public List<Record> getCleanType(String service_item) {
		return commonDao.getCleanType(service_item);
	}
	public List<Record> getNannyType(String service_item) {
		return commonDao.getNannyType(service_item);
	}
	public List<Record> getDietPredilection(String diet_predilection) {
		return commonDao.getDietPredilection(diet_predilection);
	}
	public List<Record> getServiceFrequency(String service_frequency) {
		return commonDao.getServiceFrequency(service_frequency);
	}
	
	public List<Record> getBeautifyService(String beautify_service_items) {
		return commonDao.getBeautifyService(beautify_service_items);
	}
	public List<Record> getRepairPlace(String repair_place) {
		return commonDao.getRepairPlace(repair_place);
	}
	public List<SystemResource> getAllResouse() {
		return commonDao.getAllResouse();
	}
	public List<Record> getHouseType(String house_type, String service_type) {
		if(service_type!=null){
		int service=Integer.parseInt(service_type);
		switch(service){
		case 1:
			service_type = FYB.fyb_buyHouse_type;
			break;
		case 2:
			service_type = FYB.fyb_rentHouse_type;
			break;
		}
		
		}
		return commonDao.getHouseType(house_type,service_type);
	}
	public List<Record> getPositionType(String position_type) {
		return commonDao.getPositionType(position_type);
	}
//	public List<Record> getApplyjobPost(String apply_job_post) {
//		return commonDao.getApplyjobPost(apply_job_post);
//	}
	public List<Record> getPxgwItem(String pxgw_service_item) {
		return commonDao.getPxgwItem(pxgw_service_item);
	}
	public List<Record> getFlzxdetail(String flzx_detail_content) {
		return commonDao.getFlzxdetail(flzx_detail_content);
	}
	public List<Record> getPosition_type(String position_type) {
		return commonDao.getPosition_type(position_type);
	}
	public List<Record> getApplyJobPost(String apply_job_post) {
		return commonDao.getApplyJobPost(apply_job_post);
	}
	public List<Record> getDecorationRange(String decoration_range) {
		return commonDao.getDecorationRange(decoration_range);
	}
	public String getAgentType(String userName,Integer userType) {
		if(Constant.ADMIN.equals(userName)){
			return "管理员";
		}else if(userType==1){
			return "普通用户";	
		}else{
			return "代理商";
		}
	}
	public List<Record> getData() {
		return commonDao.getData();
	}
	@Override
	public List<Record> getwashType(String wash_type) {
		return commonDao.getwashType(wash_type);
	}
	@Override
	public List<Record> getAppendService(String append_service) {
		return commonDao.getAppendService(append_service);
	}
	@Override
	public List<Record> getVoucherServiceType(String app_type) {
		
		return commonDao.getServiceType(app_type);
	}
	@Override
	public List<Record> getServiceTypeForSearch() {
		return commonDao.getServiceTypeForSearch();
	}
	@Override
	public List<Record> getHssyShootStyle(String hssy_shoot_style) {
		return commonDao.getHssyShootStyle(hssy_shoot_style);
	}
	@Override
	public List<Record> getGrxzShootStyle(String grxz_shoot_style, String sex) {
		return commonDao.getGrxzShootStyle(grxz_shoot_style,sex);
	}
	@Override
	public List<Record> getCommonName(String type, String dictType) {
		return commonDao.getCommonName(type,dictType);
	}
	@Override
	public List<Record> getServiceName(String serviceId, String appType) {
		return commonDao.getServiceName(serviceId,appType);
	}
	@Override
	public List<Record> showControlChildType(String control_type) {
		return commonDao.getControlChildType(control_type);
	}
	@Override
	public List<Record> getChinaArea(String parentId) {
		List<Record> chinaAreaList= new ArrayList<Record>();
		if(parentId==null){
		chinaAreaList.add(new Record().set("name", "全国").set("area", "全国"));
		}
		chinaAreaList.addAll(commonDao.getChinaArea(parentId));
		return	chinaAreaList;
	}
	@Override
	public List<Record> showControlType() {
		return commonDao.getControlType();
	}
	
	@Override
	public List<Record> getAllCatalog() {
		return commonDao.getAllCatalog();
	}
	@Override
	public List<Record> getServiceByCatId(String app_type) {
		List<Record> serviceByCatList= new ArrayList<Record>();
		serviceByCatList.add(new Record().set("service_type", "").set("service_name", "全部"));
		serviceByCatList.addAll(commonDao.getServiceByCatId(app_type));
		return serviceByCatList;
		
	}
	@Override
	public List<Record> getGxfwCatalog() {
		return commonDao.getGxfwCatalog();
	}
	@Override
	public List<Record> getActivityType() {
		return commonDao.getActivityType();
	}
	@Override
	public List<Record> getEntranceType() {
		return commonDao.getEntranceType();
	}
	@Override
	public List<MerchantsInfo> exportExcel() {
		return commonDao.exportExcel();
	}
	@Override
	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("phone", "手机号"));
		titles.add(new Pair("name", "店铺名称"));
		titles.add(new Pair("detail", "店铺详情"));
		titles.add(new Pair("location_address", "地址"));
		titles.add(new Pair("join_time", "加入时间"));
		titles.add(new Pair("max_employee_num", "最大员工数"));
		titles.add(new Pair("invitation_code", "邀请码"));
		titles.add(new Pair("fensi", "粉丝数"));
		return titles;
	}
	@Override
	public List<Record> getServiveCity(Long parentId) {
		return	commonDao.getServiveCity(parentId);
	}
	@Override
	public List<Record> getNewArea(Long parentId) {
		return	commonDao.getNewArea(parentId);
	}
	@Override
	public List<Record> getOrderObjectStore(String group) {
		return	commonDao.getOrderObjectStore(group);
	}
	@Override
	public List<Record> getAllProAndCity(Map<String, String[]> param) {
		return	commonDao.getAllProAndCity(param);
	}
	@Override
	public List<Record> getRewardList(Map<String, String[]> param) {
		return	commonDao.getRewardList(param);
	}


}
