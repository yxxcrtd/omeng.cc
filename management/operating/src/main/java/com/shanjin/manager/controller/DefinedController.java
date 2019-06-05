package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;


import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.service.DefinedService;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.impl.DefinedServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;
/**
 * 自定义表单
 * @author lijie
 *
 */
public class DefinedController extends Controller {
	private DefinedService definedService = new DefinedServiceImpl();
	protected ExportService service = ExportService.service;
	//*****************************自定义报价方案表单**************************
	
	public void planTableShow(){
		render("planTable.jsp");
	}
	//自定义表单维护
	public void planModelShow() {
		String object_id = StringUtil.nullToString(this.getPara("object_id"));
		this.setAttr("object_id", object_id);
		render("planModel.jsp");
	}
	//自定义表单附件表维护
		public void planModelItemShow(){
			String model_id = StringUtil.nullToString(this.getPara("model_id"));
			this.setAttr("model_id", model_id);
			render("planModelItem.jsp");
		}
		
	/** 获取订单附件表记录 */
	public void getPlanTableList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> objectTables = definedService.getPlanTableList(param);
		if (objectTables != null && objectTables.size() > 0) {
			long total = objectTables.get(0).getLong("total");
			this.renderJson(new NormalResponse(objectTables, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}

	/** 删除订单附件表记录*/
	public void deletePlanTable() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = definedService.deletePlanTable(param);
		this.renderJson(flag);
	}
	
	/** 增加订单附件表 */
	public void addPlanTable() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = definedService.addPlanTable(param);
		this.renderJson(flag);
	}

	/** 修改订单附件表 */
	public void editPlanTable() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = definedService.editPlanTable(param);
		this.renderJson(flag);
	}
	
	/** 获取自定义表记录 */
	public void getPlanModelList() {
		
		Map<String, String[]> param = this.getParaMap();
		List<Record> objectModels = definedService.getPlanModelList(param);
		if (objectModels != null && objectModels.size() > 0) {
			long total = objectModels.get(0).getLong("total");
			this.renderJson(new NormalResponse(objectModels, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}

	/** 删除自定义表记录*/
	public void deletePlanModel() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = definedService.deletePlanModel(param);
		this.renderJson(flag);
	}
	
	/** 增加自定义表 */
	public void addPlanModel() {
		UploadFile file = this.getFile("upload");
		Map<String, String[]> param = this.getParaMap();
		String resultPath = "";
		
		if (file != null) {
			String filePath = BusinessUtil.DefinedPath();
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Boolean flag = definedService.addPlanModel(param,resultPath);
		this.renderJson(flag);
		
	}

	/** 修改自定义表 */
	public void editPlanModel() {
		UploadFile file = this.getFile("upload");
		Map<String, String[]> param = this.getParaMap();
		String resultPath = "";
		
		if (file != null) {
			String filePath = BusinessUtil.DefinedPath();
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Boolean flag = definedService.editPlanModel(param,resultPath);
		this.renderJson(flag);
		
	}
	
	/** 获取自定义表单附件表记录 */
	public void getPlanModelItemList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> objectModelItems = definedService.getPlanModelItemList(param);
		if (objectModelItems != null && objectModelItems.size() > 0) {
			long total = objectModelItems.get(0).getLong("total");
			this.renderJson(new NormalResponse(objectModelItems, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}

	/** 删除自定义表单附件表记录*/
	public void deletePlanModelItem() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = definedService.deletePlanModelItem(param);
		this.renderJson(flag);
	}
	
	/** 增加订单附件表 */
	public void addPlanModelItem() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = definedService.addPlanModelItem(param);
		this.renderJson(flag);
	}

	/** 修改自定义表单附件表 */
	public void editPlanModelItem() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = definedService.editPlanModelItem(param);
		this.renderJson(flag);
	}
	//*******************************自定义订单表单*****************************************8
	
		public void objectTableShow(){
			render("objectTable.jsp");
		}
		//自定义表单维护
		public void objectModelShow() {
			String object_id = StringUtil.nullToString(this.getPara("object_id"));
			this.setAttr("object_id", object_id);
			render("objectModel.jsp");
		}
		//自定义表单附件表维护
		public void objectModelItemShow(){
				String model_id = StringUtil.nullToString(this.getPara("model_id"));
				this.setAttr("model_id", model_id);
				render("objectModelItem.jsp");
		}
		/** 获取订单附件表记录 */
		public void getObjectTableList() {
			Map<String, String[]> param = this.getParaMap();
			List<Record> objectTables = definedService.getObjectTableList(param);
			if (objectTables != null && objectTables.size() > 0) {
				long total = objectTables.get(0).getLong("total");
				this.renderJson(new NormalResponse(objectTables, total));

			} else {
				this.renderJson(new EmptyResponse());

			}
		}

		/** 删除订单附件表记录*/
		public void deleteObjectTable() {
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = definedService.deleteObjectTable(param);
			this.renderJson(flag);
		}
		
		/** 增加订单附件表 */
		public void addObjectTable() {
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = definedService.addObjectTable(param);
			this.renderJson(flag);
		}

		/** 修改订单附件表 */
		public void editObjectTable() {
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = definedService.editObjectTable(param);
			this.renderJson(flag);
		}
		
		/** 获取自定义表记录 */
		public void getObjectModelList() {
			
			Map<String, String[]> param = this.getParaMap();
			List<Record> objectModels = definedService.getObjectModelList(param);
			if (objectModels != null && objectModels.size() > 0) {
				long total = objectModels.get(0).getLong("total");
				this.renderJson(new NormalResponse(objectModels, total));

			} else {
				this.renderJson(new EmptyResponse());

			}
		}

		/** 删除自定义表记录*/
		public void deleteObjectModel() {
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = definedService.deleteObjectModel(param);
			this.renderJson(flag);
		}
		
		/** 发布或撤回自定义表记录*/
		public void pubObjectTable() {
			String ids = this.getPara("ids");
			String isPub = this.getPara("isPub");
			Boolean flag = definedService.pubObjectTable(ids, isPub);
			this.renderJson(flag);
		}
		
		/** 全部发布或撤回自定义表记录*/
		public void pubAllObjectTable() {
			String isPub = this.getPara("isPub");
			Boolean flag = definedService.pubAllObjectTable(isPub);
			this.renderJson(flag);
		}
		
		/** 增加自定义表 */
		public void addObjectModel() {
			UploadFile file = this.getFile("upload");
			String resultPath = "";
			if (file != null) {
				String filePath = BusinessUtil.DefinedPath();
				resultPath = BusinessUtil.fileUpload(file, filePath);
			}
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = definedService.addObjectModel(param,resultPath);
			
			if(flag){
				this.renderJson(true);
			}else{
				this.renderText( "字段名称重复,请更换");
			}
			
		}

		/** 修改自定义表 */
		public void editObjectModel() {
			UploadFile file = this.getFile("upload");
			Map<String, String[]> param = this.getParaMap();
			String resultPath = "";
			if (file != null) {
				String filePath = BusinessUtil.DefinedPath();
				resultPath = BusinessUtil.fileUpload(file, filePath);
			}
			Boolean flag = definedService.editObjectModel(param,resultPath);
			if(flag){
				this.renderJson(true);
			}else{
				this.renderText( "字段名称重复,请更换");
			}
		}
		
		/** 获取自定义表单附件表记录 */
		public void getObjectModelItemList() {
			Map<String, String[]> param = this.getParaMap();
			List<Record> objectModelItems = definedService.getObjectModelItemList(param);
			if (objectModelItems != null && objectModelItems.size() > 0) {
				long total = objectModelItems.get(0).getLong("total");
				this.renderJson(new NormalResponse(objectModelItems, total));

			} else {
				this.renderJson(new EmptyResponse());

			}
		}

		/** 删除自定义表单附件表记录*/
		public void deleteObjectModelItem() {
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = definedService.deleteObjectModelItem(param);
			this.renderJson(flag);
		}
		
		/** 增加订单附件表 */
		public void addObjectModelItem() {
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = definedService.addObjectModelItem(param);
			this.renderJson(flag);
		}

		/** 修改自定义表单附件表 */
		public void editObjectModelItem() {
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = definedService.editObjectModelItem(param);
			this.renderJson(flag);
		}
}
