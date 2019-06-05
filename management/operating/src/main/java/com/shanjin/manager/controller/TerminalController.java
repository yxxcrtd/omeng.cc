package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.AppUpdate;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.service.ITerminalService;
import com.shanjin.manager.service.impl.TerminalServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;

/**
 * 终端管理相关
 * @author Huang yulai
 *
 */
public class TerminalController extends Controller{
	
	private ITerminalService terminalService=new TerminalServiceImpl();
	
	public void index() {
		render("terminalGrid.jsp");
	}
	
	/** 获取客户端版本列表 */
	public void clientVersionList() {

		Map<String, String[]> param = this.getParaMap();
		List<AppUpdate> list = terminalService.getClientVersionList(param);
		if (list != null && list.size() > 0) {
			long total = list.get(0).getTotal();
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 删除客户端版本 */
	public void deleteClientVersion() {
		String ids = this.getPara("ids");
		Boolean flag = terminalService.deleteClientVersion(ids);
		this.renderJson(flag);
	}
	
	/** 发布客户端版本 */
	public void publishClientVersion() {
		String ids = this.getPara("ids");
		Boolean flag = terminalService.updatePublishStatus(ids, 1);
		this.renderJson(flag);
	}
	
	/** 撤回客户端版本 */
	public void cancelPublishClientVersion() {
		String ids = this.getPara("ids");
		Boolean flag = terminalService.updatePublishStatus(ids, 2);
		this.renderJson(flag);
	}

	/** 添加新客户端版本*/
	public void addClientVersion(){
		try{
			UploadFile file = this.getFile("upload");
			Map<String, String[]> param = this.getParaMap();
			String resultPath = "";
			if (file != null) {
				String fileName = "";
				String packagePath = "";
				int packageType = StringUtil.nullToInteger(param.get("package_type")[0]);
				String appType = StringUtil.null2Str(param.get("app_type")[0]);
				String version = StringUtil.null2Str(param.get("version")[0]);
				fileName = appType + "_V_" + version; // 文件名【sxd_V_1.0.1】 
				fileName = fileName.replace(".", "_");
				if(packageType==1){
					// 商户版
					packagePath = BusinessUtil.packageFolder(Constant.PACKAGE_TYPE_MERCHANT);
				}else{
					// 用户版
					packagePath = BusinessUtil.packageFolder(Constant.PACKAGE_TYPE_USER);
				}
				resultPath = BusinessUtil.fileUpload(file, packagePath,fileName);
			}

			Boolean flag = terminalService.saveClientVersion(param, resultPath);
			this.renderJson(flag);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/** 编辑新客户端版本*/
	public void editClientVersion(){
		try{
			UploadFile file = this.getFile("upload");
			Map<String, String[]> param = this.getParaMap();
			String resultPath = "";
			if (file != null) {
				String fileName = "";
				String packagePath = "";
				int packageType = StringUtil.nullToInteger(param.get("package_type")[0]);
				String appType = StringUtil.null2Str(param.get("app_type")[0]);
				String version = StringUtil.null2Str(param.get("version")[0]);
				fileName = appType + "_V_" + version; // 文件名【sxd_V_1.0.1】
				fileName = fileName.replace(".", "_");
				if(packageType==1){
					// 商户版
					packagePath = BusinessUtil.packageFolder(Constant.PACKAGE_TYPE_MERCHANT);
				}else{
					// 用户版
					packagePath = BusinessUtil.packageFolder(Constant.PACKAGE_TYPE_USER);
				}
				resultPath = BusinessUtil.fileUpload(file, packagePath,fileName);
			}

			Boolean flag = terminalService.saveClientVersion(param, resultPath);
			this.renderJson(flag);	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
