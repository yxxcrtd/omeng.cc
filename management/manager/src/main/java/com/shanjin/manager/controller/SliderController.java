package com.shanjin.manager.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;


import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.Recommend;
import com.shanjin.manager.Bean.SearchStatistic;
import com.shanjin.manager.Bean.Slider;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.ISliderService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.SliderServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;

public class SliderController extends Controller {

	private ISliderService sliderService = new SliderServiceImpl();
	protected ExportService service = ExportService.service;
    //广告位
	public void index() {
		this.render("slider.jsp");
	}
	//热门推荐位
	public void recommendIndex() {
		this.render("recommend.jsp");
	}
	//热门搜索管理
	public void searchStatisticIndex() {
		this.render("searchStatistic.jsp");
	}
	//行业图标管理
	public void searchStatisticAttahIndex() {
		this.render("searchStatisticAttah.jsp");
	}
	//第三方APP管理
	public void thirdAppIndex() {
		this.render("thirdApp.jsp");
	}
	//菜单管理
	public void treeServiceIndex() {
		this.render("treeService.jsp");
	}
	//菜单管理
	public void activityIndex() {
		this.render("activity.jsp");
	}
	//菜单服务管理
	public void catalogServiceIndex() {
		String catalogid = StringUtil.nullToString(this.getPara("catalogid"));
		this.setAttr("catalogid", catalogid);
		this.render("catalogService.jsp");
	}
	
	//表单树管理
	public void treeOrderServiceIndex() {
			this.render("orderTreeService.jsp");
		}
	
	// 表单树服务管理
	public void orderCatalogServiceIndex() {
		String catalogid = StringUtil.nullToString(this.getPara("catalogid"));
		this.setAttr("catalogid", catalogid);
		this.render("orderCatalogService.jsp");
	}
	
	//分享活动界面
	public void shareActivityIndex() {
		this.render("shareActivity.jsp");
	}
	
	//分享活动界面
	public void staticActivityIndex() {
		this.render("staticActivity.jsp");
	}
	
	//活动详情配置界面
	public void activityDetailIndex() {
		String activity_id = StringUtil.nullToString(this.getPara("activity_id"));
		this.setAttr("activity_id", activity_id);
		this.render("activityDetail.jsp");
	}
		
		
		
	/** 获取广告列表 */
	public void getSliders() {
		Map<String, String[]> param = this.getParaMap();
		List<Slider> sliders = sliderService.getSliders(param);
		if (sliders != null && sliders.size() > 0) {
			long total = sliders.get(0).getTotal();
			this.renderJson(new NormalResponse(sliders, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	
	/** 删除广告 */
	public void deleteSlider() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteSlider(id);
		this.renderJson(flag);
	}

	/** 添加广告 */
	public void addSlider() throws Exception {
		UploadFile file = this.getFile("upload");

		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_BANNER);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveSlider(param, resultPath);
		this.renderJson(flag);
	}
	
	/** 编辑广告 */
	public void editSlider() throws Exception {
		UploadFile file = this.getFile("upload");
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_BANNER);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveSlider(param, resultPath);
		this.renderJson(flag);
	}

	/** 导出广告信息 */
	public void exportExcel() {
		List<Slider> list = sliderService.exportSliderList(); // 查询数据
		List<Pair> usetitles = sliderService.getExportTitles();
		String fileName = "广告信息";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles, fileName,0);
		renderNull();
	}

	/** 下载广告图片 */
	public void downLoad() throws IOException {
		String path = this.getPara("path");
		service.downLoad(getResponse(), getRequest(), path);
		renderNull();
	}
	
	/** 获取推荐位列表 */
	public void getRecommends() {

		Map<String, String[]> param = this.getParaMap();
		List<Recommend> sliders = sliderService.getRecommends(param);
		if (sliders != null && sliders.size() > 0) {
			long total = sliders.get(0).getTotal();
			this.renderJson(new NormalResponse(sliders, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 添加推荐位 */
	public void editRecommend() throws Exception {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveRecommend(param);
		this.renderJson(flag);
	}
	
	/** 删除推荐位 */
	public void deleteRecommend() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteRecommend(id);
		this.renderJson(flag);
	}
	
	/** 获取热门搜索列表 */
	public void getSearchStatistics() {
		Map<String, String[]> param = this.getParaMap();
		List<SearchStatistic> searchStatistics = sliderService.getSearchStatistics(param);
		if (searchStatistics != null && searchStatistics.size() > 0) {
			long total = searchStatistics.get(0).getTotal();
			this.renderJson(new NormalResponse(searchStatistics, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 添加热门搜索服务项目 */
	public void editSearchStatistics() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveSearchStatistics(param);
		this.renderJson(flag);
	}
	
	/** 删除热门搜索服务项目 */
	public void deleteSearchStatistics() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteSearchStatistics(id);
		this.renderJson(flag);
	}
	

	/** 获取行业图标列表 */
	public void getSearchStatisticsAttch() {

		Map<String, String[]> param = this.getParaMap();
		List<Record> sliders = sliderService.getSearchStatisticsAttch(param);
		if (sliders != null && sliders.size() > 0) {
			long total = sliders.get(0).getLong("total");
			this.renderJson(new NormalResponse(sliders, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 添加行业图标 */
	public void editSearchStatisticsAttch() throws Exception {
		UploadFile file = this.getFile("upload");
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_SEARCH_STATISTIC);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveSearchStatisticsAttch(param, resultPath);
		this.renderJson(flag);
	}
	
	/** 删除行业图标 */
	public void deleteSearchStatisticsAttch() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteSearchStatisticsAttch(id);
		this.renderJson(flag);
	}
	
	/** 获取热门搜索列表 */
	public void getThirdApp() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> thirdApps = sliderService.getThirdApp(param);
		if (thirdApps != null && thirdApps.size() > 0) {
			long total = thirdApps.get(0).getLong("total");
			this.renderJson(new NormalResponse(thirdApps, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 添加热门搜索服务项目 */
	public void editThirdApp() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveThirdApp(param);
		this.renderJson(flag);
	}
	
	/** 删除热门搜索服务项目 */
	public void deleteThirdApp() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteThirdApp(id);
		this.renderJson(flag);
	}
	
	/** 获取推荐位列表 */
	public void getServiceByRecId() {

		Map<String, String[]> param = this.getParaMap();
		List<Record> recs = sliderService.getServiceByRecId(param);
		if (recs != null && recs.size() > 0) {
			long total = recs.get(0).getLong("total");
			this.renderJson(new NormalResponse(recs, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 添加推荐位 */
//	public void editServiceRec() {
//		UploadFile file = this.getFile("upload");
//		Map<String, String[]> param = this.getParaMap();
//		String resultPath = "";
//		if (file != null) {
//			try {
//				if(!Util.checkImagSize(file.getFile(),param)){
//					this.renderText("图片尺寸不符合规格");
//					return;
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_RECOMMEND);
//			resultPath = BusinessUtil.fileUpload(file, filePath);
//		}
//		int flag = sliderService.saveServiceRec(param,resultPath);
//		if(flag==1){
//			this.renderJson(true);
//		}else if(flag==2){
//			this.renderText("推荐位超过限制");
//		}else{
//			this.renderText( "操作失败，请稍后重试");
//		}
//	}
	
	/** 推荐位启用或暂停 */
	public void startOrstopRecs() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.startOrstopRecs(param);
		if(flag==1){
			this.renderText("推荐位启用成功");
		}else if(flag==2){
			this.renderText("推荐位暂停成功");
		}else if(flag==3){
			this.renderText("推荐位不足6个，不能启用");
		}else if(flag==4){
			this.renderText("已存在相同地区的推荐方案，不能启用");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
	/** 获取分类列表 */
	public void getServiceTree() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> serviceTree = sliderService.getServiceTree(param);
		if (serviceTree != null && serviceTree.size() > 0) {
			this.renderJson(new NormalResponse(serviceTree));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 获取分类服务列表 */
	public void getCatalog() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> catalogService = sliderService.getCatalog(param);
		if (catalogService != null && catalogService.size() > 0) {
			long total = catalogService.get(0).getLong("total");
			this.renderJson(new NormalResponse(catalogService, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 添加分类服务 */
	public void addCatalogService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.addCatalogService(param);
		this.renderJson(flag);
	}
	/** 审核分类服务 */
	public void auditCatalogService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.auditCatalogService(param);
		this.renderJson(flag);
	}
	
	/** 删除分类服务 */
	public void deleteCatalogService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.deleteCatalogService(param);
		this.renderJson(flag);
	}
	/** 删除分类 */
	public void deleteCatalog() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.deleteCatalog(param);
		if(flag==1){
			this.renderText("有子节点不允许删除");
		}else if(flag==2){
			this.renderText("删除分类成功");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
	/** 添加分类服务 */
	public void addCatalog() {
		UploadFile smallfile = this.getFile("icon_path");
		UploadFile bigfile = this.getFile("big_icon_path");
		Map<String, String[]> param = this.getParaMap();
		String smallPath = "";
		String bigPath = "";
		if (smallfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_CATALOG);
			smallPath = BusinessUtil.fileUpload(smallfile, filePath);
		}
		if (bigfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_CATALOG);
			bigPath = BusinessUtil.fileUpload(bigfile, filePath);
		}
		int flag = sliderService.saveCatalog(param,smallPath,bigPath);
		if(flag==1){
			this.renderText("有子节点不允许修改成叶子");
		}else if(flag==2){
			this.renderJson(true);
		}else if(flag==3){
			this.renderText( "操作失败，分类重名");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	/** 编辑分类服务 */
	public void editCatalog() {
		UploadFile smallfile = this.getFile("icon_path");
		UploadFile bigfile = this.getFile("big_icon_path");
		Map<String, String[]> param = this.getParaMap();
		String smallPath = "";
		String bigPath = "";
		if (smallfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_CATALOG);
			smallPath = BusinessUtil.fileUpload(smallfile, filePath);
		}
		if (bigfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_CATALOG);
			bigPath = BusinessUtil.fileUpload(bigfile, filePath);
		}
		int flag = sliderService.saveCatalog(param,smallPath,bigPath);
		if(flag==1){
			this.renderText("有子节点不允许修改成叶子");
		}else if(flag==2){
			this.renderJson(true);
		}else if(flag==3){
			this.renderText( "操作失败，分类重名");
		}else {
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
	/** 发布或者撤回节点 */
	public void releaseOrrecallCatalog() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.releaseOrrecallCatalog(param);
		if(flag==1){
			this.renderText("状态没有发生改变");
		}else if(flag==2){
			this.renderText("操作成功");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	/** 添加根节点 */
	public void addRootCatalog() {
		UploadFile smallfile = this.getFile("icon_path");
		UploadFile bigfile = this.getFile("big_icon_path");
		Map<String, String[]> param = this.getParaMap();
		String smallPath = "";
		String bigPath = "";
		if (smallfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_CATALOG);
			smallPath = BusinessUtil.fileUpload(smallfile, filePath);
		}
		if (bigfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_CATALOG);
			bigPath = BusinessUtil.fileUpload(bigfile, filePath);
		}
		boolean flag = sliderService.addRootCatalog(param,smallPath,bigPath);
		if(flag){
			this.renderJson(true);
		}else{
			this.renderText( "操作失败，分类重名");
		}
	}
	
	/** 获取分类服务列表 */
	public void getAllCatalog() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> catalogService = sliderService.getAllCatalog(param);
		if (catalogService != null && catalogService.size() > 0) {
			long total = catalogService.get(0).getLong("total");
			this.renderJson(new NormalResponse(catalogService, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 获取个性服务下的所有服务 */
	public void getServiceType() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> serviceService = sliderService.getServiceType(param);
		if (serviceService != null && serviceService.size() > 0) {
			long total = serviceService.get(0).getLong("total");
			this.renderJson(new NormalResponse(serviceService, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	//表单树所有接口---------------------------------------------------------
	
	/** 获取表单树列表 */
	public void getOrderServiceTree() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> serviceTree = sliderService.getOrderServiceTree(param);
		if (serviceTree != null && serviceTree.size() > 0) {
			this.renderJson(new NormalResponse(serviceTree));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 删除表单树分类 */
	public void deleteOrderCatalog() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.deleteOrderCatalog(param);
		if(flag==1){
			this.renderText("有子节点不允许删除");
		}else if(flag==2){
			this.renderText("删除分类成功");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	/** 发布或者撤回节点 */
	public void releaseOrrecallOrderCatalog() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.releaseOrrecallOrderCatalog(param);
		if(flag==1){
			this.renderText("状态没有发生改变");
		}else if(flag==2){
			this.renderText("操作成功");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
	/** 添加分类服务 */
	public void addOrderCatalog() {
		UploadFile smallfile = this.getFile("icon");
		UploadFile bigfile = this.getFile("big_icon");
		Map<String, String[]> param = this.getParaMap();
		String smallPath = "";
		String bigPath = "";
		if (smallfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ORDER_CATALOG);
			smallPath = BusinessUtil.fileUpload(smallfile, filePath);
		}
		if (bigfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ORDER_CATALOG);
			bigPath = BusinessUtil.fileUpload(bigfile, filePath);
		}
		int flag = sliderService.saveOrderCatalog(param,smallPath,bigPath);
		if(flag==1){
			this.renderText("有子节点不允许修改成叶子");
		}else if(flag==2){
			this.renderJson(true);
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	/** 编辑分类服务 */
	public void editOrderCatalog() {
		UploadFile smallfile = this.getFile("icon");
		UploadFile bigfile = this.getFile("big_icon");
		Map<String, String[]> param = this.getParaMap();
		String smallPath = "";
		String bigPath = "";
		if (smallfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ORDER_CATALOG);
			smallPath = BusinessUtil.fileUpload(smallfile, filePath);
		}
		if (bigfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ORDER_CATALOG);
			bigPath = BusinessUtil.fileUpload(bigfile, filePath);
		}
		int flag = sliderService.saveOrderCatalog(param,smallPath,bigPath);
		if(flag==1){
			this.renderText("有子节点不允许修改成叶子");
		}else if(flag==2){
			this.renderJson(true);
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	/** 添加根节点 */
	public void addOrderRootCatalog() {
		UploadFile smallfile = this.getFile("icon");
		UploadFile bigfile = this.getFile("big_icon");
		Map<String, String[]> param = this.getParaMap();
		String smallPath = "";
		String bigPath = "";
		if (smallfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ORDER_CATALOG);
			smallPath = BusinessUtil.fileUpload(smallfile, filePath);
		}
		if (bigfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ORDER_CATALOG);
			bigPath = BusinessUtil.fileUpload(bigfile, filePath);
		}
		boolean flag = sliderService.addOrderRootCatalog(param,smallPath,bigPath);
		this.renderJson(flag);
	}
	/** 获取表单分类服务列表 */
	public void getOrderCatalog() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> catalogService = sliderService.getOrderCatalog(param);
		if (catalogService != null && catalogService.size() > 0) {
			long total = catalogService.get(0).getLong("total");
			this.renderJson(new NormalResponse(catalogService, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 添加表单分类服务 */
	public void addOrderCatalogService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.addOrderCatalogService(param);
		this.renderJson(flag);
	}
	
	/** 删除表单分类服务 */
	public void deleteOrderCatalogService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.deleteOrderCatalogService(param);
		this.renderJson(flag);
	}
	/** 审核表单分类服务 */
	public void auditOrderCatalogService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.auditOrderCatalogService(param);
		this.renderJson(flag);
	}
	
	
	
	/** 获取活动列表 */
	public void getShareActivity() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> activitys = sliderService.getShareActivity(param);
		if (activitys != null && activitys.size() > 0) {
			long total = activitys.get(0).getLong("total");
			this.renderJson(new NormalResponse(activitys, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	
	/** 删除活动 */
	public void deleteShareActivity() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteShareActivity(id);
		this.renderJson(flag);
	}

	/** 添加活动 */
	public void addShareActivity(){
		UploadFile file = this.getFile("upload");

		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ACTIVITY);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveShareActivity(param, resultPath);
		this.renderJson(flag);
	}
	
	/** 编辑活动 */
	public void editShareActivity() {
		UploadFile file = this.getFile("upload");
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ACTIVITY);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveShareActivity(param, resultPath);
		this.renderJson(flag);
	}
	
	/** 分享活动启用或暂停 */
	public void startOrstopAct() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.startOrstopAct(param);
		if(flag==1){
			this.renderText("分享活动启用成功");
		}else if(flag==2){
			this.renderText("分享活动位暂停成功");
		}else if(flag==3){
			this.renderText("已有启动的活动，不能启用");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
	
	/** 获取活动列表 */
	public void getStaticActivity() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> activitys = sliderService.getStaticActivity(param);
		if (activitys != null && activitys.size() > 0) {
			long total = activitys.get(0).getLong("total");
			this.renderJson(new NormalResponse(activitys, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	
	/** 删除活动 */
	public void deleteStaticActivity() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteStaticActivity(id);
		this.renderJson(flag);
	}

	/** 添加活动 */
	public void addStaticActivity(){
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveStaticActivity(param);
		this.renderJson(flag);
	}
	
	/** 编辑活动 */
	public void editStaticActivity() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = sliderService.saveStaticActivity(param);
		this.renderJson(flag);
	}
	
	/** 分享活动启用或暂停 */
	public void startOrstopStaAct() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.startOrstopStaAct(param);
		if(flag==1){
			this.renderText("静态活动启用成功");
		}else if(flag==2){
			this.renderText("静态活动位暂停成功");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
	/** 获取通用活动列表 */
	public void getActivity() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> activitys = sliderService.getActivity(param);
		if (activitys != null && activitys.size() > 0) {
			long total = activitys.get(0).getLong("total");
			this.renderJson(new NormalResponse(activitys, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 添加活动 */
	public void addActivity() {
		UploadFile smallfile = this.getFile("sImage");
		UploadFile bigfile = this.getFile("bImage");
		UploadFile sharefile = this.getFile("shareImage");
		Map<String, String[]> param = this.getParaMap();
		String smallPath = "";
		String bigPath = "";
		String sharePath = "";
		if (smallfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.ACTIVITY);
			smallPath = BusinessUtil.fileUpload(smallfile, filePath);
		}
		if (bigfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.ACTIVITY);
			bigPath = BusinessUtil.fileUpload(bigfile, filePath);
		}
		if (sharefile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.ACTIVITY);
			sharePath = BusinessUtil.fileUpload(sharefile, filePath);
		}
		boolean flag = sliderService.saveActivity(param,smallPath,bigPath,sharePath);
		this.renderJson(flag);
	}
	
	/** 添加根节点 */
	public void editActivity() {
		UploadFile smallfile = this.getFile("sImage");
		UploadFile bigfile = this.getFile("bImage");
		UploadFile sharefile = this.getFile("shareImage");
		Map<String, String[]> param = this.getParaMap();
		String smallPath = "";
		String bigPath = "";
		String sharePath = "";
		if (smallfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.ACTIVITY);
			smallPath = BusinessUtil.fileUpload(smallfile, filePath);
		}
		if (bigfile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.ACTIVITY);
			bigPath = BusinessUtil.fileUpload(bigfile, filePath);
		}
		if (sharefile != null) {
			String filePath = BusinessUtil.imageFolder(BusinessUtil.ACTIVITY);
			sharePath = BusinessUtil.fileUpload(sharefile, filePath);
		}
		boolean flag = sliderService.saveActivity(param,smallPath,bigPath,sharePath);
		this.renderJson(flag);
	}
	
	/** 删除活动 */
	public void deleteActivity() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteActivity(id);
		this.renderJson(flag);
	}
	/** 活动启用或暂停 */
	public void startOrstopActivity() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.startOrstopActivity(param);
		if(flag==1){
			this.renderText("活动启用成功");
		}else if(flag==2){
			this.renderText("活动暂停成功");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	//活动入口配置
		public void activityEntranceIndex() {
			String activity_id = StringUtil.nullToString(this.getPara("activity_id"));
			this.setAttr("activity_id", activity_id);
			this.render("activityEntrance.jsp");
		}
		/** 获取通用活动列表 */
		public void getActivityEntranceReleation() {
			Map<String, String[]> param = this.getParaMap();
			List<Record> activitys = sliderService.getActivityEntranceReleation(param);
			if (activitys != null && activitys.size() > 0) {
				long total = activitys.get(0).getLong("total");
				this.renderJson(new NormalResponse(activitys, total));
			} else {
				this.renderJson(new EmptyResponse());
			}
		}
		
		public void addActivityEntrance() {
			Map<String, String[]> param = this.getParaMap();
			boolean flag = sliderService.addActivityEntrance(param);
			this.renderJson(flag);
		}
		
		/** 获取活动入口列表 */
		public void getActivityEntrance() {
			Map<String, String[]> param = this.getParaMap();
			List<Record> activitys = sliderService.getActivityEntrance(param);
			if (activitys != null && activitys.size() > 0) {
				long total = activitys.get(0).getLong("total");
				this.renderJson(new NormalResponse(activitys, total));
			} else {
				this.renderJson(new EmptyResponse());
			}
		}
		/** 删除活动 */
		public void deleteActivityEntrance() {
			String id = this.getPara("id");
			Boolean flag = sliderService.deleteActivityEntrance(id);
			this.renderJson(flag);
		}	

		/** 获取活动入口列表 */
		public void getActivityDetail() {
			Map<String, String[]> param = this.getParaMap();
			List<Record> activitys = sliderService.getActivityDetail(param);
			if (activitys != null && activitys.size() > 0) {
				long total = activitys.get(0).getLong("total");
				this.renderJson(new NormalResponse(activitys, total));
			} else {
				this.renderJson(new EmptyResponse());
			}
		}
		/** 添加活动详情 */
		public void addActivityDetail(){
			UploadFile file = this.getFile("upload");

			String resultPath = "";
			if (file != null) {
				String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ACTIVITY_DETAIL);
				resultPath = BusinessUtil.fileUpload(file, filePath);
			}
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = sliderService.saveActivityDetail(param, resultPath);
			this.renderJson(flag);
		}	
		/** 添加活动详情 */
		public void editActivityDetail(){
			UploadFile file = this.getFile("upload");

			String resultPath = "";
			if (file != null) {
				String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_ACTIVITY_DETAIL);
				resultPath = BusinessUtil.fileUpload(file, filePath);
			}
			Map<String, String[]> param = this.getParaMap();
			Boolean flag = sliderService.saveActivityDetail(param, resultPath);
			this.renderJson(flag);
		}	
		
		
	/** 删除活动详情 */
	public void deleteActivityDetail() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteActivityDetail(id);
		this.renderJson(flag);
	}

	// 活动分享平台
	public void activityPlatFormIndex() {
		String activity_id = StringUtil.nullToString(this.getPara("activity_id"));
		this.setAttr("activity_id", activity_id);
		this.render("activityPlatForm.jsp");
	}
	
	/** 获取活动分享平台列表 */
	public void getActivityPlatFormReleation() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> activitys = sliderService.getActivityPlatFormReleation(param);
		if (activitys != null && activitys.size() > 0) {
			long total = activitys.get(0).getLong("total");
			this.renderJson(new NormalResponse(activitys, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	public void addActivityPlatForm() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.addActivityPlatForm(param);
		this.renderJson(flag);
	}
	
	/** 获取活动入口列表 */
	public void getActivityPlatForm() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> activitys = sliderService.getActivityPlatForm(param);
		if (activitys != null && activitys.size() > 0) {
			long total = activitys.get(0).getLong("total");
			this.renderJson(new NormalResponse(activitys, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 删除活动 */
	public void deleteActivityPlatForm() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteActivityPlatForm(id);
		this.renderJson(flag);
	}	
	
	// 推荐活动平台
	public void recommendServiceIndex() {
		this.render("recommendService.jsp");
	}
	
	/** 获取推荐服务列表 */
	public void getRecommendService() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> activitys = sliderService.getRecommendService(param);
		if (activitys != null && activitys.size() > 0) {
			long total = activitys.get(0).getLong("total");
			this.renderJson(new NormalResponse(activitys, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 删除推荐服务 */
	public void deleteRecommendService() {
		String id = this.getPara("id");
		Boolean flag = sliderService.deleteRecommendService(id);
		this.renderJson(flag);
	}
	
	/** 新增推荐服务 */
	public void addRecommendService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.saveRecommendService(param);
		this.renderJson(flag);
	}
	
	/** 编辑推荐服务 */
	public void editRecommendService() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = sliderService.saveRecommendService(param);
		this.renderJson(flag);
	}
	
	/**推荐服务启用或暂停 */
	public void startOrstopRecommendSer() {
		Map<String, String[]> param = this.getParaMap();
		int flag = sliderService.startOrstopRecommendSer(param);
		if(flag==1){
			this.renderText("推荐服务启用成功");
		}else if(flag==2){
			this.renderText("推荐服务暂停成功");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
}
