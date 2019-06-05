package com.shanjin.manager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.manager.Bean.AgentCharge;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.ICommonService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.CommonServiceImpl;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class CommonController extends Controller{
	private ICommonService commonService=new CommonServiceImpl();
	protected ExportService service = ExportService.service;
	/** 根据包类型获取所有app类型*/
	public void showAppTypeByPackType(){
		String packageType=this.getPara("package_type");
		//List<Record> appTypeList=commonService.getAppTypeByPackType(packageType);
		List<Map<String,Object>> appTypeList = new  ArrayList<Map<String,Object>>();
		if(packageType.equals(Constant.PACKAGE_TYPE_MERCHANT)){
			appTypeList=Constant.merchantAppInfoMapList;
		}else{
			appTypeList=Constant.userAppInfoMapList;
		}
		if(appTypeList!=null&&appTypeList.size()>0){
			this.renderJson(new NormalResponse(appTypeList));
		}else{
			this.renderJson(new EmptyResponse());
		}
	}

/** 获取商户端所有app类型*/
public void showAppType(){
	List<Map<String,Object>> appType = Constant.merchantAppInfoMapList;
	//List<Record> appType=commonService.getAppType();
	if(appType!=null&&appType.size()>0){
		this.renderJson(new NormalResponse(appType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
/** 获取用户端所有app类型*/
public void showUserAppType(){
	List<Map<String,Object>> appType = Constant.userAppInfoMapList;
	//List<Record> appType=commonService.getAppType();
	if(appType!=null&&appType.size()>0){
		this.renderJson(new NormalResponse(appType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
/** 获取所有订单商户端状态*/
public void showMerOrderStatus(){
	List<Record> merOrderStatus=commonService.getMerOrderStatus();
	if(merOrderStatus!=null&&merOrderStatus.size()>0){
		this.renderJson(new NormalResponse(merOrderStatus));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/** 获取所有订单用户端状态*/
public void showUserOrderStatus(){
	List<Record> merOrderStatus=commonService.getUserOrderStatus();
	if(merOrderStatus!=null&&merOrderStatus.size()>0){
		this.renderJson(new NormalResponse(merOrderStatus));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
/** 根据appType来查询对应的服务类型*/
public void showServiceType(){
	String app_type=this.getPara("app");
	
	List<Record> serviceType=commonService.getServiceType(app_type);
	if(serviceType!=null&&serviceType.size()>0){
		this.renderJson(new NormalResponse(serviceType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/** 根据control_type来查询对应的具体实现类型*/
public void showControlType(){
	List<Record> showType=commonService.showControlType();
	if(showType!=null&&showType.size()>0){
		this.renderJson(new NormalResponse(showType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
/** 根据control_type来查询对应的具体实现类型*/
public void showControlShowType(){
	String control_type=this.getPara("control_type");
	List<Record> showType=commonService.showControlChildType(control_type);
	if(showType!=null&&showType.size()>0){
		this.renderJson(new NormalResponse(showType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
/** 根据appType来查询对应的服务类型*/
public void showVoucherServiceType(){
	String app_type=this.getPara("app");
	
	List<Record> serviceType=commonService.getVoucherServiceType(app_type);
	if(serviceType!=null&&serviceType.size()>0){
		this.renderJson(new NormalResponse(serviceType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/** 获取所有银行*/
public void showWithdraw(){
	List<Record> withdrawList=commonService.getWithdraw();
	if(withdrawList!=null&&withdrawList.size()>0){
		this.renderJson(new NormalResponse(withdrawList));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/**
 * 地区信息
 */
public void showArea(){
	Long parentId = null;
	if(this.getPara("parentId")!=null&&!"".equals(this.getPara("parentId"))){
		parentId=StringUtil.nullToLong(this.getPara("parentId"));
	}
	List<Record> areaList=commonService.getArea(parentId);
	if(areaList!=null&&areaList.size()>0){
		this.renderJson(new NormalResponse(areaList));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/**
 * 地区信息
 */
public void showNewArea(){
	Long parentId = null;
	if(this.getPara("parentId")!=null&&!"".equals(this.getPara("parentId"))){
		parentId=StringUtil.nullToLong(this.getPara("parentId"));
	}
	List<Record> areaList=commonService.getNewArea(parentId);
	if(areaList!=null&&areaList.size()>0){
		this.renderJson(new NormalResponse(areaList));
	}else{
		this.renderJson(new EmptyResponse());
	}
}



/**
 * 地区信息
 */
public void showServiveCity(){
	Long parentId = null;
	if(this.getPara("parentId")!=null&&!"".equals(this.getPara("parentId"))){
		parentId=StringUtil.nullToLong(this.getPara("parentId"));
	}
	List<Record> areaList=commonService.getServiveCity(parentId);
	if(areaList!=null&&areaList.size()>0){
		this.renderJson(new NormalResponse(areaList));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/**
 * 地区信息
 */
public void showChinaArea(){
	String parentId = null;
	if(this.getPara("parentId")!=null&&!"".equals(this.getPara("parentId"))){
		parentId=StringUtil.nullToString(this.getPara("parentId"));
	}
	List<Record> areaList=commonService.getChinaArea(parentId);
	if(areaList!=null&&areaList.size()>0){
		this.renderJson(new NormalResponse(areaList));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/**
 * app项目信息(查询所有app并对项目代理的用户设置代理标志)
 */
public void showApp(){
	List<Record> appList = commonService.getAppList(); //获取所有app
	if(appList!=null&&appList.size()>0){
		String userId=this.getPara("userId");
		if(userId!=null&&userId!=""){
			List<AppInfo> userApp = commonService.userApp(StringUtil.nullToLong(userId));
			if(userApp!=null&&userApp.size()>0){
				for(Record r : appList){
					r.set("checked", false);
					for(AppInfo a : userApp){
						if(r.getLong("id").equals(a.getLong("id"))){
							r.set("checked", true);
							break;
						}
					}
				}
			}
			
		}
		this.renderJson(new NormalResponse(appList));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/**
 * 订单详情页，根据金装修订单的装修范围
 * decoration_range查询对应的名称
 */
public void getData(){
//	String decoration_range = this.getPara("decoration_range");
	List<Record> item=commonService.getData();
	if(item!=null&&item.size()>0){
		this.renderJson(new NormalResponse(item));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
/**
 * 查询所有的资源
 * 
 */
public List<SystemResource> getAllResouse(){
	List<SystemResource> resouse=commonService.getAllResouse();
	return resouse;
}

/**
 * 刷新系统参数
 */
public void flushSystemParam() {
	CommonUtil.initAppInfoList();
	CommonUtil.flushSystemParam();
	this.renderJson(true);
}

/**
 * 刷新服务类型
 */
public void flushServiceType() {
	CommonUtil.initServiceType();
	CommonUtil.initServiceTypeById();
	CommonUtil.flushImageCache(CacheConstants.IMAGE_CACHE.SHOW_ICON,true);
	CommonUtil.flushImageCache(CacheConstants.IMAGE_CACHE.ORDER_ICON,true);
	this.renderJson(true);
}

/**
 * 刷新系统资源
 */
public void flushSystemResource() {
	CommonUtil.initSystemResourceMap();
	this.renderJson(true);
}

/**
 * 订单详情页，根据类型查询名称
 * 
 */
public void getCommonName(){
	String type = this.getPara("type");
	String dictType=this.getPara("dictType");
	List<Record> item=commonService.getCommonName(type,dictType);
	if(item!=null&&item.size()>0){
		this.renderJson(new NormalResponse(item));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
/**
 * 订单详情页，根据类型查询名称
 * 
 */
public void getServiceName(){
	String serviceId = this.getPara("serviceId");
	String appType=this.getPara("appType");
	List<Record> item=commonService.getServiceName(serviceId,appType);
	if(item!=null&&item.size()>0){
		this.renderJson(new NormalResponse(item));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/** 获取商户端一级分类*/
public void showAllCatalog(){
	
	List<Record> appType=commonService.getAllCatalog();
	if(appType!=null&&appType.size()>0){
		this.renderJson(new NormalResponse(appType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

/** 获取分类下所有的服务*/
public void getServiceByCatId(){
	String app_type = this.getPara("app");
	List<Record> serviceType=commonService.getServiceByCatId(app_type);
	if(serviceType!=null&&serviceType.size()>0){
		this.renderJson(new NormalResponse(serviceType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

public void getGxfwCatalog(){
	List<Record> catalog=commonService.getGxfwCatalog();
	if(catalog!=null&&catalog.size()>0){
		this.renderJson(new NormalResponse(catalog));
	}else{
		this.renderJson(new EmptyResponse());
	}
}


public void getActivityType(){
	List<Record> activityType=commonService.getActivityType();
	if(activityType!=null&&activityType.size()>0){
		this.renderJson(new NormalResponse(activityType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

public void getEntranceType(){
	List<Record> entranceType=commonService.getEntranceType();
	if(entranceType!=null&&entranceType.size()>0){
		this.renderJson(new NormalResponse(entranceType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
/**
 * 刷新活动缓存
 */
public void flushActivityCache() {
	CommonUtil.flushActivityCache();
	this.renderJson(true);
}


// 导出代理商消费记录信息
public void exportExcel() {
	
	List<MerchantsInfo> list = commonService.exportExcel(); 
	// 查询数据
	List<Pair> usetitles = commonService.getExportTitles();
	String fileName="代理商消费记录";
	// 导出
	service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
	renderNull();
}
public void showOrderObjectStore(){
	String group = this.getPara("group");
	List<Record> entranceType=commonService.getOrderObjectStore(group);
	if(entranceType!=null&&entranceType.size()>0){
		this.renderJson(new NormalResponse(entranceType));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
//显示所有的省市信息
public void showAllProAndCity(){
	Map<String, String[]> param = this.getParaMap();
	List<Record> procity=commonService.getAllProAndCity(param);
	if(procity!=null&&procity.size()>0){
		long total = procity.get(0).getLong("total");
		this.renderJson(new NormalResponse(procity, total));
	}else{
		this.renderJson(new EmptyResponse());
	}
}
//显示所有的奖励活动
public void getRewardList(){
	Map<String, String[]> param = this.getParaMap();
	List<Record> rewardList=commonService.getRewardList(param);
	if(rewardList!=null&&rewardList.size()>0){
		this.renderJson(new NormalResponse(rewardList));
	}else{
		this.renderJson(new EmptyResponse());
	}
}

}
