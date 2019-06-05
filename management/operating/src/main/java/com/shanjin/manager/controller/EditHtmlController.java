package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.FensiAddRanking;
import com.shanjin.manager.Bean.FensiAddTotal;
import com.shanjin.manager.Bean.MerPhotoStatic;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.OrderRewardAccount;
import com.shanjin.manager.Bean.UserSession;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IActivityService;
import com.shanjin.manager.service.IEditHtmlService;
import com.shanjin.manager.service.impl.ActivityServiceImpl;
import com.shanjin.manager.service.impl.EditHtmlServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class EditHtmlController extends Controller {

	private IEditHtmlService editHtmlService = new EditHtmlServiceImpl();
	protected ExportService service = ExportService.service;

	public void index() {
		this.render("serviceEditHtml.jsp");
	}

	/** 获取编辑器列表*/
	public void getEditHtmlList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> editHtmlList = editHtmlService.getEditHtmlList(param);
		if (editHtmlList != null && editHtmlList.size() > 0) {
			long total = editHtmlList.get(0).getLong("total");
			this.renderJson(new NormalResponse(editHtmlList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 新增订单奖励活动*/
	public void addEditHtml() {
		UploadFile file = this.getFile("upload");
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_BANNER);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = editHtmlService.saveEditHtml(param,resultPath);
		this.renderJson(flag);
	}
	
	/** 编辑订单奖励活动列表*/
	public void editEditHtml() {
		UploadFile file = this.getFile("upload");
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_BANNER);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = editHtmlService.saveEditHtml(param,resultPath);
		this.renderJson(flag);
	}
	
	/** 删除订单奖励活动列表*/
	public void deleteEditHtml() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = editHtmlService.deleteEditHtml(param);
		this.renderJson(flag);
	}
	
	/** 删除订单奖励活动列表*/
	public void cancelRecordAll() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = editHtmlService.cancelRecordAll(param);
		this.renderJson(flag);
	}	
}
