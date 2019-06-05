package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.service.IServiceCityService;
import com.shanjin.manager.service.impl.ServiceCityServiceImpl;

public class OperateController extends Controller{
	
	IServiceCityService serviceCityService = new ServiceCityServiceImpl();
	
	public void serviceCityIndex() {
		render("serviceCityGrid.jsp");
		return;
	}
	
	/** 获取服务城市列表 */
	public void serviceCityList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> list = serviceCityService.getServiceCityList(param);
		if (list != null && list.size() > 0) {
			long total = list.get(0).getLong("total");
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 删除服务城市记录 */
	public void delServiceCity() {
		String ids = this.getPara("ids");
		serviceCityService.delServiceCity(ids);
		this.renderJson(true);
	}
	
	/** 开通服务城市 */
	public void openServiceCity() {
		String ids = this.getPara("ids");
		Boolean flag = serviceCityService.updateOpenStatus(ids, "1");
		this.renderJson(flag);
	}
	
	/** 关闭服务城市 */
	public void closeServiceCity() {
		String ids = this.getPara("ids");
		Boolean flag = serviceCityService.updateOpenStatus(ids, "0");
		this.renderJson(flag);
	}

	/** 添加服务城市*/
	public void addServiceCity() throws Exception {
		Map<String, String[]> param = this.getParaMap();
		int flag = serviceCityService.saveServiceCity(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "服务城市已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}

	/** 编辑服务城市*/
	public void editServiceCity() throws Exception {
		Map<String, String[]> param = this.getParaMap();
		int flag = serviceCityService.saveServiceCity(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "服务城市已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
}
