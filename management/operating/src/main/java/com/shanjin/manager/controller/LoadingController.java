package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.shanjin.manager.Bean.Loading;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.service.ILoadingService;
import com.shanjin.manager.service.impl.LoadingServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;

public class LoadingController extends Controller{
	private ILoadingService loadingService=new LoadingServiceImpl();
	
	
	public void loadingIndex() {
		render("loadingGrid.jsp");
		return;
	}
	
	/** 获取启动页列表 */
	public void loadingList() {

		Map<String, String[]> param = this.getParaMap();
		List<Loading> list = loadingService.getLoadingList(param);
		if (list != null && list.size() > 0) {
			long total = list.get(0).getTotal();
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 删除启动页记录 */
	public void deleteLoading() {
		String ids = this.getPara("ids");
		Boolean flag = loadingService.deleteLoading(ids);
		this.renderJson(flag);
	}
	
	/** 发布启动页 */
	public void publishLoading() {
		String ids = this.getPara("ids");
		Boolean flag = loadingService.updatePublishStatus(ids, 1);
		this.renderJson(flag);
	}
	
	/** 撤回启动页 */
	public void cancelPublishLoading() {
		String ids = this.getPara("ids");
		Boolean flag = loadingService.updatePublishStatus(ids, 0);
		this.renderJson(flag);
	}

	/** 添加启动页*/
	public void addLoading() throws Exception {
		UploadFile file = this.getFile("upload");
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_LOADING);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = loadingService.saveLoading(param, resultPath);
		this.renderJson(flag);
	}

	/** 编辑启动页*/
	public void editLoading() throws Exception {
		UploadFile file = this.getFile("upload");
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_LOADING);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = loadingService.saveLoading(param, resultPath);
		this.renderJson(flag);
	}
}
