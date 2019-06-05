package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.WithDraw;
import com.shanjin.manager.dao.MerchantsInfoDao;
import com.shanjin.manager.dao.impl.MerchantsInfoDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.IMerchantsInfoService;

public class MerchantsInfoServiceImpl implements IMerchantsInfoService {
	
	private MerchantsInfoDao merchantsInfoDao=new MerchantsInfoDaoImpl();
	
	public List<Record> getMerchantsInfoList(Map<String, String[]> param) {
		List<Record> merchantsList=merchantsInfoDao.getMerchantsInfoList(param);
		return merchantsList;
		
	}

	public Boolean editMerchants(Map<String, String[]> param) {
		Boolean flag = merchantsInfoDao.editMerchants(param);
		return flag;
		
	}

	public List<Record> getEmployeeList(Map<String, String[]> param) {
		List<Record> employeeList=merchantsInfoDao.getEmployeeList(param);
		return employeeList;
	}

	public List<Record> getStoreAudit(Map<String, String[]> param) {
		List<Record> storeList=merchantsInfoDao.getStoreAudit(param);
		return storeList;
	}

	public Boolean AuditStore(Map<String, String[]> param,String operName) {
		Boolean flag = merchantsInfoDao.AuditStore(param,operName);
		return flag;
	}

	public Boolean deleteStore(Map<String, String[]> param) {
		Boolean flag = merchantsInfoDao.deleteStore(param);
		return flag;
	}

	public Boolean deleteEmployee(Map<String, String[]> param) {
		Boolean flag = merchantsInfoDao.deleteEmployee(param);
		return flag;
	}

	public Boolean AuditAllEmployee(Map<String, String[]> param) {
		Boolean flag = merchantsInfoDao.AuditAllEmployee(param);
		return flag;
	}

	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "服务商ID"));
		titles.add(new Pair("name", "服务商名称"));
		titles.add(new Pair("telephone", "注册号码"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("auth_type", "认证类型"));
		titles.add(new Pair("auth_status", "认证状态"));
		titles.add(new Pair("perAuth", "个人认证状态"));
		titles.add(new Pair("join_time", "开店时间"));
		titles.add(new Pair("address", "地址"));
		titles.add(new Pair("longitude", "经度"));
		titles.add(new Pair("latitude", "纬度"));
		titles.add(new Pair("app_name", "行业类型"));
		titles.add(new Pair("vouchers", "代金券"));
		titles.add(new Pair("employee", "员工"));
		titles.add(new Pair("invitation_code", "邀请码"));
		return titles;
	}

	public List<MerchantsInfo> exportStoreList(Map<String, String[]> param) {
		List<MerchantsInfo> merchantsList=merchantsInfoDao.getExportMerchantsList(param);
		return merchantsList;
	}

	public List<MerchantsInfo> exportEmployeeList(Map<String, String[]> filterParam) {
		List<MerchantsInfo> employeeList=merchantsInfoDao.getexportEmployeeList(filterParam);
		return employeeList;
	}

	public List<Pair> getExportEmployeeTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "编号"));
		titles.add(new Pair("merchants_name", "服务商名称"));
		titles.add(new Pair("name", "员工姓名"));
		titles.add(new Pair("phone", "注册号码"));
		titles.add(new Pair("employees_type", "员工类型"));
		titles.add(new Pair("join_time", "加入时间"));
	
		return titles;
	}

	public List<WithDraw> getMerchantsWithDraw(Map<String, String[]> param) {
		List<WithDraw> withDrawList=merchantsInfoDao.getMerchantsWithDraw(param);
		return withDrawList;
	}

	public Boolean AuditAllWithDraw(Map<String, String[]> param, String operUserName) {
		Boolean flag = merchantsInfoDao.AuditAllWithDraw(param,operUserName);
		return flag;
	}

	public List<WithDraw> exportWithDrawList(Map<String, String[]> param) {
		List<WithDraw> withDrawList=merchantsInfoDao.getexportWithDraw(param);
		return withDrawList;
	}

	public List<Pair> getExportWithDrawTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("merchant_id", "商户ID"));
		titles.add(new Pair("merchants_name", "商户名"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("real_name", "姓名"));
		titles.add(new Pair("telephone", "注册号码"));
		titles.add(new Pair("ID_No", "身份证号"));
		titles.add(new Pair("withdraw_name", "银行类型"));
		titles.add(new Pair("withdraw_no", "银行卡号"));
		titles.add(new Pair("withdraw_price", "提现金额"));
		titles.add(new Pair("withdraw_time", "申请时间"));
		titles.add(new Pair("withdraw_status", "提现状态"));
		return titles;
	}

	public Boolean saveEmployee(Map<String, String[]> param) {
		return merchantsInfoDao.saveEmployee(param);
	}

	public Boolean saveStore(Map<String, String[]> param) {
		return merchantsInfoDao.saveStore(param);
	}

	@Override
	public Boolean RefuseAuditStore(Map<String, String[]> param,String operName) {
		Boolean flag = merchantsInfoDao.RefuseAuditStore(param,operName);
		return flag;
	}

	@Override
	public List<Record> getServiceTypeById(Map<String, String[]> param) {
		return merchantsInfoDao.getServiceTypeById(param);
	}

	@Override
	public Map<String, Object> merchantDetail(Long merchantId) {
		return merchantsInfoDao.merchantDetail(merchantId);
	}

	@Override
	public List<String> merchantPicList(Long albumId) {
		return merchantsInfoDao.merchantPicList(albumId);
	}

	@Override
	public List<MerchantsInfo> exportStoreAuditExcel(
			Map<String, String[]> filterParam) {
		List<MerchantsInfo> merchantsList=merchantsInfoDao.getExportStoreAuditExcel(filterParam);
		return merchantsList;
	}

	@Override
	public List<Pair> getExportStoreAuditTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("merchantId", "服务商ID"));
		titles.add(new Pair("name", "服务商名称"));
		titles.add(new Pair("telephone", "注册号码"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("auth_total", "认证次数"));
		titles.add(new Pair("authType", "认证类型"));
		titles.add(new Pair("authStatus", "认证状态"));
		titles.add(new Pair("join_time", "申请时间"));
		titles.add(new Pair("app_name", "app类型"));
		titles.add(new Pair("address", "地址"));
		titles.add(new Pair("auth_time", "操作时间"));
		titles.add(new Pair("invitation_code", "邀请码"));
		titles.add(new Pair("remark", "拒绝原因"));
		return titles;
	}

	@Override
	public List<MerchantsInfo> exportTimeStoreList(Map<String, Object> paramMap) {
		List<MerchantsInfo> merchantsList=merchantsInfoDao.getExportStoreExcel(paramMap);
		return merchantsList; 
	}

	@Override
	public List<Pair> getTimeExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "服务商ID"));
		titles.add(new Pair("name", "服务商名称"));
		titles.add(new Pair("telephone", "联系方式"));
		titles.add(new Pair("province", "省份"));
		titles.add(new Pair("city", "城市"));
		titles.add(new Pair("auth_status", "认证状态"));
		titles.add(new Pair("auth_type", "认证类型"));
		titles.add(new Pair("perAuth", "个人认证状态"));
		titles.add(new Pair("join_time", "开店时间"));
		titles.add(new Pair("address", "地址"));
		titles.add(new Pair("app_name", "app类型"));
		titles.add(new Pair("vouchers", "代金券"));
		titles.add(new Pair("invitation_code", "邀请码"));
		
		return titles;
	}

	@Override
	public List<Record> getAuthDetailByMerchantId(Map<String, String[]> param) {
		List<Record> storeList=merchantsInfoDao.getAuthDetailByMerchantId(param);
		return storeList;
	}

	@Override
	public List<Record> getMerchantsInfoListForFensi(Map<String, String[]> param) {
		List<Record> merchantsList=merchantsInfoDao.getMerchantsInfoListForFensi(param);
		return merchantsList;
	}

	@Override
	public Boolean addMerchantsInfoForFensi(Map<String, String[]> param) {
		Boolean flag = merchantsInfoDao.addMerchantsInfoForFensi(param);
		return flag;
	}

	@Override
	public List<Record> getPayDeatilByMerId(Map<String, String[]> param) {
		List<Record> merchantsList=merchantsInfoDao.getPayDeatilByMerId(param);
		return merchantsList;
	}

}
