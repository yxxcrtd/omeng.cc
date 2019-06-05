package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Catalog;
import com.shanjin.manager.Bean.Recommend;
import com.shanjin.manager.Bean.SearchStatistic;
import com.shanjin.manager.Bean.SearchStatisticAttch;
import com.shanjin.manager.Bean.Slider;
import com.shanjin.manager.dao.SliderDao;
import com.shanjin.manager.dao.impl.SliderDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.ISliderService;

public class SliderServiceImpl implements ISliderService {

	private SliderDao sliderDao=new SliderDaoImpl();
	
	public List<Slider> getSliders(Map<String, String[]> param) {
		List<Slider> sliders=sliderDao.getSliders(param);
		return sliders;
	}

	public Boolean deleteSlider(String id) {
		Boolean flag = sliderDao.deleteSlider(id);
		return flag;
	}

	public List<Slider> exportSliderList() {
		List<Slider> sliders=sliderDao.getSliders();
		return sliders;
	}

	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("name", "广告名称"));
		titles.add(new Pair("slider_type", "广告类型"));
		titles.add(new Pair("join_time", "开始时间"));
		titles.add(new Pair("overdue_time", "结束时间"));
		titles.add(new Pair("slider_status", "广告状态"));
		titles.add(new Pair("path", "广告路径"));
		return titles;
	}

	public Boolean saveSlider(Map<String, String[]> param, String path) {
		return sliderDao.saveSlider(param, path);
	}

	@Override
	public List<Recommend> getRecommends(Map<String, String[]> param) {
		return sliderDao.getRecommends(param);
	}

	@Override
	public Boolean saveRecommend(Map<String, String[]> param) {
		return sliderDao.saveRecommend(param);
	}

	@Override
	public Boolean deleteRecommend(String id) {
		Boolean flag = sliderDao.deleteRecommend(id);
		return flag;
	}

	@Override
	public List<SearchStatistic> getSearchStatistics(Map<String, String[]> param) {
		return sliderDao.getSearchStatistics(param); 
	}

	@Override
	public Boolean saveSearchStatistics(Map<String, String[]> param) {
		return sliderDao.saveSearchStatistics(param);
	}

	@Override
	public Boolean deleteSearchStatistics(String id) {
		Boolean flag = sliderDao.deleteSearchStatistics(id);
		return flag;
	}

	@Override
	public List<Record> getSearchStatisticsAttch(
			Map<String, String[]> param) {
		return sliderDao.getSearchStatisticsAttch(param);
	}

	@Override
	public Boolean saveSearchStatisticsAttch(Map<String, String[]> param,
			String resultPath) {
		Boolean flag = sliderDao.saveSearchStatisticsAttch(param,resultPath);
		return flag;
	}

	@Override
	public Boolean deleteSearchStatisticsAttch(String id) {
		Boolean flag = sliderDao.deleteSearchStatisticsAttch(id);
		return flag;
	}

	@Override
	public List<Record> getThirdApp(Map<String, String[]> param) {
		return  sliderDao.getThirdApp(param);
	}

	@Override
	public Boolean saveThirdApp(Map<String, String[]> param) {
		Boolean flag = sliderDao.saveThirdApp(param);
		return flag;
	}

	@Override
	public Boolean deleteThirdApp(String id) {
		Boolean flag = sliderDao.deleteThirdApp(id);
		return flag;
	}

	@Override
	public List<Record> getServiceByRecId(Map<String, String[]> param) {
		return  sliderDao.getServiceByRecId(param);
	}

	@Override
	public int saveServiceRec(Map<String, String[]> param, String resultPath) {
		return  sliderDao.saveServiceRec(param,resultPath);
	}

	@Override
	public int startOrstopRecs(Map<String, String[]> param) {
		return sliderDao.startOrstopRecs(param);
	}

	@Override
	public List<Record> getServiceTree(Map<String, String[]> param) {
		return sliderDao.getServiceTree(param);
	}

	@Override
	public List<Record> getCatalog(Map<String, String[]> param) {
		return sliderDao.getCatalog(param);
	}

	@Override
	public boolean addCatalogService(Map<String, String[]> param) {
		return sliderDao.addCatalogService(param);
	}

	@Override
	public boolean deleteCatalogService(Map<String, String[]> param) {
		return sliderDao.deleteCatalogService(param);
	}

	@Override
	public int deleteCatalog(Map<String, String[]> param) {
		return sliderDao.deleteCatalog(param);
	}

	@Override
	public int saveCatalog(Map<String, String[]> param, String smallPath,String bigPath) {
		return sliderDao.saveCatalog(param,smallPath,bigPath);
	}

	@Override
	public boolean addRootCatalog(Map<String, String[]> param, String samllPath, String bigPath) {
		return sliderDao.addRootCatalog(param, samllPath, bigPath);
	}

	@Override
	public List<Record> getAllCatalog(Map<String, String[]> param) {
		return sliderDao.getAllCatalog(param);
	}

	@Override
	public int releaseOrrecallCatalog(Map<String, String[]> param) {
		return sliderDao.releaseOrrecallCatalog(param);
	}

	@Override
	public boolean auditCatalogService(Map<String, String[]> param) {
		return sliderDao.auditCatalogService(param);
	}

	@Override
	public List<Record> getServiceType(Map<String, String[]> param) {
		return sliderDao.getServiceType(param);
	}

	@Override
	public List<Record> getOrderServiceTree(Map<String, String[]> param) {
		return sliderDao.getOrderServiceTree(param);
	}

	@Override
	public int deleteOrderCatalog(Map<String, String[]> param) {
		return sliderDao.deleteOrderCatalog(param);
	}

	@Override
	public int releaseOrrecallOrderCatalog(Map<String, String[]> param) {
		return sliderDao.releaseOrrecallOrderCatalog(param);
	}

	@Override
	public int saveOrderCatalog(Map<String, String[]> param, String smallPath,
			String bigPath) {
		return sliderDao.saveOrderCatalog(param,smallPath,bigPath);
	}

	@Override
	public boolean addOrderRootCatalog(Map<String, String[]> param,
			String smallPath, String bigPath) {
		return sliderDao.addOrderRootCatalog(param,smallPath,bigPath);
	}

	@Override
	public List<Record> getOrderCatalog(Map<String, String[]> param) {
		return sliderDao.getOrderCatalog(param);
	}

	@Override
	public boolean addOrderCatalogService(Map<String, String[]> param) {
		return sliderDao.addOrderCatalogService(param);
	}

	@Override
	public boolean deleteOrderCatalogService(Map<String, String[]> param) {
		return sliderDao.deleteOrderCatalogService(param);
	}

	@Override
	public boolean auditOrderCatalogService(Map<String, String[]> param) {
		return sliderDao.auditOrderCatalogService(param);
	}

	@Override
	public List<Record> getShareActivity(Map<String, String[]> param) {
		
		return sliderDao.getShareActivity(param);
	}

	@Override
	public Boolean deleteShareActivity(String id) {

		return sliderDao.deleteShareActivity(id);
	}

	@Override
	public Boolean saveShareActivity(Map<String, String[]> param,
			String resultPath) {
		return sliderDao.saveShareActivity(param,resultPath);
	}

	@Override
	public int startOrstopAct(Map<String, String[]> param) {
		return sliderDao.startOrstopAct(param);
	}

	@Override
	public List<Record> getStaticActivity(Map<String, String[]> param) {
		return sliderDao.getStaticActivity(param);
	}

	@Override
	public Boolean deleteStaticActivity(String id) {
		return sliderDao.deleteStaticActivity(id);
	}

	@Override
	public Boolean saveStaticActivity(Map<String, String[]> param) {
		return sliderDao.saveStaticActivity(param);
	}

	@Override
	public int startOrstopStaAct(Map<String, String[]> param) {
		return sliderDao.startOrstopStaAct(param);
	}

	@Override
	public List<Record> getActivity(Map<String, String[]> param) {
		return sliderDao.getActivity(param);
	}

	@Override
	public boolean saveActivity(Map<String, String[]> param, String smallPath,
			String bigPath, String sharePath) {
		return sliderDao.saveActivity(param,smallPath,bigPath,sharePath);
	}

	@Override
	public Boolean deleteActivity(String id) {
		return sliderDao.deleteActivity(id);
	}

	@Override
	public int startOrstopActivity(Map<String, String[]> param) {
		return sliderDao.startOrstopActivity(param);
	}

	@Override
	public List<Record> getActivityEntranceReleation(Map<String, String[]> param) {
		return sliderDao.getActivityEntranceReleation(param);
	}

	@Override
	public boolean addActivityEntrance(Map<String, String[]> param) {
		return sliderDao.addActivityEntrance(param);
	}

	@Override
	public List<Record> getActivityEntrance(Map<String, String[]> param) {
		return sliderDao.getActivityEntrance(param);
	}

	@Override
	public Boolean deleteActivityEntrance(String id) {
		return sliderDao.deleteActivityEntrance(id);
	}

	@Override
	public List<Record> getActivityDetail(Map<String, String[]> param) {
		return sliderDao.getActivityDetail(param);
	}

	@Override
	public Boolean saveActivityDetail(Map<String, String[]> param,
			String resultPath) {
		return sliderDao.saveActivityDetail(param,resultPath);
	}

	@Override
	public Boolean deleteActivityDetail(String id) {
		return sliderDao.deleteActivityDetail(id);
	}

	@Override
	public List<Record> getActivityPlatFormReleation(Map<String, String[]> param) {
		return sliderDao.getActivityPlatFormReleation(param);
	}

	@Override
	public boolean addActivityPlatForm(Map<String, String[]> param) {
		return sliderDao.addActivityPlatForm(param);
	}

	@Override
	public List<Record> getActivityPlatForm(Map<String, String[]> param) {
		return sliderDao.getActivityPlatForm(param);
	}

	@Override
	public Boolean deleteActivityPlatForm(String id) {
		return sliderDao.deleteActivityPlatForm(id);
	}

	@Override
	public List<Record> getRecommendService(Map<String, String[]> param) {
		return sliderDao.getRecommendService(param);
	}

	@Override
	public boolean saveRecommendService(Map<String, String[]> param) {
		return sliderDao.saveRecommendService(param);
	}

	@Override
	public Boolean deleteRecommendService(String id) {
		return sliderDao.deleteRecommendService(id);
	}

	@Override
	public int startOrstopRecommendSer(Map<String, String[]> param) {
		return sliderDao.startOrstopRecommendSer(param);
	}

}
