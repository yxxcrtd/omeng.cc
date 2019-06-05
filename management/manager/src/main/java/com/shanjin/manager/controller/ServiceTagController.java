package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.MerchantServiceTag;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IServiceTagService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.ServiceTagServiceImpl;
import com.shanjin.manager.utils.Util;

public class ServiceTagController extends Controller{

	private IServiceTagService serviceTagService = new ServiceTagServiceImpl();
	protected ExportService service = ExportService.service;
	public void serviceTagIndex() {
		render("serviceTagGrid.jsp");
	}
	
	public void merchantServiceTagIndex() {
		render("merchantServiceTagGrid.jsp");
	}
	
	public void personalTagIndex() {
		render("personalTagGrid.jsp");
	}
	/** 获取服务标签列表 */
	public void serviceTagList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> list = serviceTagService.getServiceTagList(param);
		if (list != null && list.size() > 0) {
			long total = serviceTagService.getServiceTagListCount(param);
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 删除服务标签记录 */
	public void deleteServiceTag() {
		String ids = this.getPara("ids");
		Boolean flag = serviceTagService.deleteServiceTag(ids);
		this.renderJson(flag);
	}
	
//	/** 审核服务标签 */
//	public void auditServiceTag() {
//		String ids = this.getPara("ids");
//		String status = this.getPara("status");
//		Boolean flag = serviceTagService.auditServiceTag(ids,status);
//		this.renderJson(flag);
//	}

	/** 添加服务标签*/
	public void addServiceTag(){
		Map<String, String[]> param = this.getParaMap();
		int flag = serviceTagService.saveServiceTag(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功！";
		}else if (flag == 2){
			msg = "标签已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}

	/** 编辑服务标签*/
	public void editServiceTag() {
		Map<String, String[]> param = this.getParaMap();
		int flag = serviceTagService.saveServiceTag(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功！";
		}else if (flag == 2){
			msg = "标签已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	/** 获取个性化标签列表 */
	public void personalTagList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> list = serviceTagService.getPersonalTagList(param);
		if (list != null && list.size() > 0) {
			long total = serviceTagService.getPersonalTagListCount(param);
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 删除服务标签记录 */
	public void deletePersonalTag() {
		String ids = this.getPara("ids");
		Boolean flag = serviceTagService.deletePersonalTag(ids);
		this.renderJson(flag);
	}
	
//	/** 审核服务标签 */
//	public void auditServiceTag() {
//		String ids = this.getPara("ids");
//		String status = this.getPara("status");
//		Boolean flag = serviceTagService.auditServiceTag(ids,status);
//		this.renderJson(flag);
//	}

	/** 添加服务标签*/
	public void addPersonalTag(){
		Map<String, String[]> param = this.getParaMap();
		int flag = serviceTagService.savePersonalTag(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功！";
		}else if (flag == 2){
			msg = "标签已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}

	/** 编辑服务标签*/
	public void editPersonalTag() {
		Map<String, String[]> param = this.getParaMap();
		int flag = serviceTagService.savePersonalTag(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功！";
		}else if (flag == 2){
			msg = "标签已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	
	
	/** 获取商户服务标签列表 */
	public void merchantServiceTagList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> list = serviceTagService.getMerchantServiceTagList(param);
		if (list != null && list.size() > 0) {
			long total = serviceTagService.getMerchantServiceTagListCount(param);
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	// 导出个性标签
		public void exportExcel() {
			Map<String, String[]> param = this.getParaMap();
			List<MerchantServiceTag> list = serviceTagService.exportExcel(param); // 查询数据
			List<Pair> usetitles = serviceTagService.getExportTitles();
			String fileName="个性标签信息";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
		}
	/** 删除商户服务标签记录 */
	public void deleteMerchantServiceTag() {
		String ids = this.getPara("ids");
		Boolean flag = serviceTagService.deleteMerchantServiceTag(ids);
		this.renderJson(flag);
	}
	
	/** 审核商户服务标签 */
	public void auditMerchantServiceTag() {
		String ids = this.getPara("ids");
		String status = this.getPara("status");
		String calogId = this.getPara("calogId");
		String demand = this.getPara("demand");
		Boolean flag = serviceTagService.auditMerchantServiceTag(ids,status,calogId,demand);
		this.renderJson(flag);
	}
	
	
	/** 审核商户服务标签 */
	public void getTagRepeat() {
		String id = this.getPara("ids");
		Map map = serviceTagService.getTagRepeat(id);
		this.renderJson(map);
	}
	

}
