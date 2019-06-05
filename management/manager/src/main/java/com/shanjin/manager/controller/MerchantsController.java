package com.shanjin.manager.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.sso.bean.UserSession;
import com.shanjin.manager.Bean.WithDraw;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IMerchantsInfoService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.MerchantsInfoServiceImpl;
import com.shanjin.manager.time.StatisticalUtil;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class MerchantsController extends Controller {

	private IMerchantsInfoService merchantsInfoService = new MerchantsInfoServiceImpl();
	protected ExportService service = ExportService.service;

	public void index() {
		String	start_time=Util.getLastMonth();
		this.setAttr("start_time", start_time);
		render("employee.jsp");

	}
	//下载商户的运营信息
	public void downloadExcel() {
		HttpSession session=this.getSession();
		Map<String, String[]> param = this.getParaMap();
		//String fileName="merchant_"+StatisticalUtil.getCurrentTime()+".xlsx";
		String fileName=Util.getExportMerFileName(session,param);
		if(fileName.equals("")){
			this.renderJson("");
		}else if(fileName.equals("1")){
			this.renderJson("1");
		}else{
		String filePath = BusinessUtil.manFolder()+BusinessUtil.DIR_STATISTIC+BusinessUtil.PATH_SEPARATOR+BusinessUtil.DIR_MERCHANT+BusinessUtil.PATH_SEPARATOR;
		this.renderJson(BusinessUtil.getFileUrl(filePath+fileName));
		}
	}
	public void showStore() {
		String invite_code = StringUtil.nullToString(this.getPara("invite_code"));
		String province = StringUtil.nullToString(this.getPara("province"));
		String city = StringUtil.nullToString(this.getPara("city"));
		String provinceDesc = StringUtil.nullToString(this.getPara("provinceDesc"));
		String cityDesc = StringUtil.nullToString(this.getPara("cityDesc"));
		String start_time1 = StringUtil.nullToString(this.getPara("start_time1"));
		String start_time =getStartTime(start_time1,this.getPara("start_time"));
		String off_time = Util.getChangeDate(StringUtil.nullToString(this.getPara("off_time")));
		if(start_time==null||start_time.equals("")){
			start_time=Util.getLastMonth();
		}
		String name = StringUtil.nullToString(this.getPara("name"));
		this.setAttr("invite_code", invite_code);
		this.setAttr("start_time", start_time);
		this.setAttr("off_time", off_time);
		this.setAttr("province", province);
		this.setAttr("city", city);
		this.setAttr("provinceDesc", provinceDesc);
		this.setAttr("cityDesc", cityDesc);
		this.setAttr("name", name);
		render("store.jsp");
	}

	private String getStartTime(String start_time1, String start_time) {
		String stime=Util.getChangeDate(StringUtil.nullToString(start_time));
		if(stime.equals("")){
			return start_time1;
		}else {
			try {
				return Util.getMaxTime(start_time1,stime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return start_time1;
	}
	public void showStoreAudit() {
		String invite_code = StringUtil.nullToString(this.getPara("invite_code"));
		String auth_status = StringUtil.nullToString(this.getPara("auth_status"));
		String auth_type = StringUtil.nullToString(this.getPara("auth_type"));
		String start_time1 = StringUtil.nullToString(this.getPara("start_time1"));
		String start_time =getStartTime(start_time1,this.getPara("start_time"));
		String off_time = Util.getChangeDate(StringUtil.nullToString(this.getPara("off_time")));
		String province = StringUtil.nullToString(this.getPara("province"));
		String city = StringUtil.nullToString(this.getPara("city"));
		String provinceDesc = StringUtil.nullToString(this.getPara("provinceDesc"));
		String cityDesc = StringUtil.nullToString(this.getPara("cityDesc"));
		if(start_time==null||start_time.equals("")){
			start_time=Util.getLastMonth();
		}
		this.setAttr("invite_code", invite_code);
		this.setAttr("auth_status", auth_status);
		this.setAttr("auth_type", auth_type);
		this.setAttr("start_time", start_time);
		this.setAttr("off_time", off_time);
		this.setAttr("province", province);
		this.setAttr("city", city);
		this.setAttr("provinceDesc", provinceDesc);
		this.setAttr("cityDesc", cityDesc);
		render("storeAudit.jsp");
	}

	public void showWithDraw() {
		render("withDraw.jsp");
	}
	
	/**获取商户列表*/
	public void getMerchantsInfoList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getMerchantParams(param,session);
		List<Record> merchantslist = merchantsInfoService
				.getMerchantsInfoList(filterParam);
		if (merchantslist != null && merchantslist.size() > 0) {
			long total = merchantslist.get(0).getLong("total");
			this.renderJson(new NormalResponse(merchantslist, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}
	
	/** 获取商户员工列表*/
	public void getEmployeeList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getEmployeeParams(param,session);
		List<Record> employeeList = merchantsInfoService.getEmployeeList(filterParam);
		if (employeeList != null && employeeList.size() > 0) {
			long total = employeeList.get(0).getLong("total");
			this.renderJson(new NormalResponse(employeeList, total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 获取商户审核列表*/
	public void getStoreAudit() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getStoreAuditParams(param,session);
		List<Record> storeList = merchantsInfoService.getStoreAudit(filterParam);
		if (storeList != null && storeList.size() > 0) {
			long total = storeList.get(0).getLong("total");
			this.renderJson(new NormalResponse(storeList, total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 根据商户id查看该商户之前认证详情*/
	public void getAuthDetailByMerchantId() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> storeList = merchantsInfoService.getAuthDetailByMerchantId(param);
		if (storeList != null && storeList.size() > 0) {
			long total = storeList.get(0).getLong("total");
			this.renderJson(new NormalResponse(storeList, total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 编辑商户*/
	public void editMerchants() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = merchantsInfoService.editMerchants(param);
		this.renderJson(flag);
	}

	/** 审核商户*/
	public void AuditStore() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		Boolean flag = merchantsInfoService.AuditStore(param,operUserName);
		this.renderJson(flag);
	}
	/** 拒绝审核商户*/
   public void RefuseAuditStore(){
	   Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		Boolean flag = merchantsInfoService.RefuseAuditStore(param,operUserName);
		this.renderJson(flag);
}
	/** 删除商户*/
	public void deleteStore() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = merchantsInfoService.deleteStore(param);
		this.renderJson(flag);
	}

	/** 删除员工*/
	public void deleteEmployee() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = merchantsInfoService.deleteEmployee(param);
		this.renderJson(flag);
	}

	/** 审核所有员工*/
	public void AuditAllEmployee() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = merchantsInfoService.AuditAllEmployee(param);
		this.renderJson(flag);
	}
	
	/** 保存员工信息*/
	public void saveEmployee() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = merchantsInfoService.saveEmployee(param);
		this.renderJson(flag);
	}
	
	/** 保存商户信息*/
	public void saveStore() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = merchantsInfoService.saveStore(param);
		this.renderJson(flag);
	}

	/** 获取商户提现记录*/
	public void getMerchantsWithDraw() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getWithDrawParams(param,session);
		List<WithDraw> withDrawList = merchantsInfoService
				.getMerchantsWithDraw(filterParam);
		if (withDrawList != null && withDrawList.size() > 0) {
			long total = withDrawList.get(0).getTotal();
			this.renderJson(new NormalResponse(withDrawList, total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 审核商户提现记录*/
	public void AuditAllWithDraw() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		Boolean flag = merchantsInfoService.AuditAllWithDraw(param,operUserName);
		this.renderJson(flag);
	}

	/**导出店铺信息*/ 
	public void exportExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getMerchantParams(param,session);

		List<MerchantsInfo> list = merchantsInfoService.exportStoreList(filterParam); // 查询数据
		List<Pair> titles = merchantsInfoService.getExportTitles();
		String fileName="店铺";
		// 导出
		service.export(getResponse(), getRequest(), list, titles,fileName,0);
		list.clear();       
		System.gc() ;
		renderNull();
	}

	/**导出店铺認真信息*/ 
	public void exportStoreAuditExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getStoreAuditParams(param,session);
		List<MerchantsInfo> list = merchantsInfoService.exportStoreAuditExcel(filterParam); // 查询数据
		List<Pair> titles = merchantsInfoService.getExportStoreAuditTitles();
		String fileName="店铺认证";
		// 导出
		service.export(getResponse(), getRequest(), list, titles,fileName,0);
		list.clear();       
		System.gc() ;
		renderNull();
	}
	
	/** 导出员工信息*/
	public void exportEmployeeExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getEmployeeParams(param,session);
		List<MerchantsInfo> list = merchantsInfoService.exportEmployeeList(filterParam); // 查询数据
		List<Pair> usetitles = merchantsInfoService.getExportEmployeeTitles();
		String fileName="员工";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		list.clear();       
		System.gc() ;
		renderNull();
	}
	/** 导出服务商提现记录*/
	public void exportWithDrawExcel() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		Map<String, String[]> filterParam=Util.getWithDrawParams(param,session);
		List<WithDraw> list = merchantsInfoService.exportWithDrawList(filterParam); // 查询数据
		List<Pair> usetitles = merchantsInfoService.getExportWithDrawTitles();
		String fileName="服务商提现记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		list.clear();       
		System.gc() ;
		renderNull();
	}
	
	public void getServiceTypeById(){
		Map<String, String[]> param = this.getParaMap();
		List<Record> serviceType= merchantsInfoService.getServiceTypeById(param);
		if (serviceType != null && serviceType.size() > 0) {
			long total = serviceType.get(0).getLong("total");
			this.renderJson(new NormalResponse(serviceType, total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**服务商详情页*/
	public void merchantDetail(){
		Long merchantId = StringUtil.nullToLong(this.getPara("merchantId"));
		Map<String,Object> merchantDetail = merchantsInfoService.merchantDetail(merchantId);
		this.setAttr("data", merchantDetail);
		render("/view/merchants/merchantDetail.jsp");
	}
	
	/**服务商相册列表*/
	public void merchantPicList(){
		Long albumId = StringUtil.nullToLong(this.getPara("albumId"));
		List<String> merchantPicList = merchantsInfoService.merchantPicList(albumId);
		this.renderJson(merchantPicList);
	}
	
	/**更新商户索引*/
	public void rebuildMerchantIndex(){
		CommonUtil.rebuildMerchantIndex();
	}
	
	
	public void MerchantsInfoListForFensiIndex() {
		String	start_time=Util.getLastMonth();
		this.setAttr("start_time", start_time);
		render("storeFensi.jsp");

	}
	/**获取商户粉丝列表*/
	public void getMerchantsInfoListForFensi() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> merchantslist = merchantsInfoService
				.getMerchantsInfoListForFensi(param);
		if (merchantslist != null && merchantslist.size() > 0) {
			long total = merchantslist.get(0).getLong("total");
			this.renderJson(new NormalResponse(merchantslist, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}
	/** 添加粉丝*/
	public void addMerchantsInfoForFensi() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = merchantsInfoService.addMerchantsInfoForFensi(param);
		this.renderJson(flag);
	}
	
	/**获取商户收入明细*/
	public void getPayDeatilByMerId() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> merchantslist = merchantsInfoService
				.getPayDeatilByMerId(param);
		if (merchantslist != null && merchantslist.size() > 0) {
			long total = merchantslist.get(0).getLong("total");
			this.renderJson(new NormalResponse(merchantslist, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}
	
}
