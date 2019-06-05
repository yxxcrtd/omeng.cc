package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Catalog;
import com.shanjin.manager.Bean.Recommend;
import com.shanjin.manager.Bean.SearchStatistic;
import com.shanjin.manager.Bean.SearchStatisticAttch;
import com.shanjin.manager.Bean.Slider;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface ISliderService {

	List<Slider> getSliders(Map<String, String[]> param);

	Boolean deleteSlider(String id);
	
	Boolean saveSlider(Map<String, String[]> param, String path);

	List<Slider> exportSliderList();

	List<Pair> getExportTitles();

	List<Recommend> getRecommends(Map<String, String[]> param);

	Boolean saveRecommend(Map<String, String[]> param);

	Boolean deleteRecommend(String id);

	List<SearchStatistic> getSearchStatistics(Map<String, String[]> param);

	Boolean saveSearchStatistics(Map<String, String[]> param);

	Boolean deleteSearchStatistics(String id);

	List<Record> getSearchStatisticsAttch(
			Map<String, String[]> param);

	Boolean saveSearchStatisticsAttch(Map<String, String[]> param,
			String resultPath);

	Boolean deleteSearchStatisticsAttch(String id);

	List<Record> getThirdApp(Map<String, String[]> param);

	Boolean saveThirdApp(Map<String, String[]> param);

	Boolean deleteThirdApp(String id);

	List<Record> getServiceByRecId(Map<String, String[]> param);

	int saveServiceRec(Map<String, String[]> param, String resultPath);

	int startOrstopRecs(Map<String, String[]> param);

	List<Record> getServiceTree(Map<String, String[]> param);

	List<Record> getCatalog(Map<String, String[]> param);

	boolean addCatalogService(Map<String, String[]> param);

	boolean deleteCatalogService(Map<String, String[]> param);

	int deleteCatalog(Map<String, String[]> param);

	int saveCatalog(Map<String, String[]> param, String samllPath, String bigPath);

	boolean addRootCatalog(Map<String, String[]> param, String samllPath, String bigPath);

	List<Record> getAllCatalog(Map<String, String[]> param);

	int releaseOrrecallCatalog(Map<String, String[]> param);

	boolean auditCatalogService(Map<String, String[]> param);

	List<Record> getServiceType(Map<String, String[]> param);

	List<Record> getOrderServiceTree(Map<String, String[]> param);

	int deleteOrderCatalog(Map<String, String[]> param);

	int releaseOrrecallOrderCatalog(Map<String, String[]> param);

	int saveOrderCatalog(Map<String, String[]> param, String smallPath,
			String bigPath);

	boolean addOrderRootCatalog(Map<String, String[]> param, String smallPath,
			String bigPath);

	List<Record> getOrderCatalog(Map<String, String[]> param);

	boolean addOrderCatalogService(Map<String, String[]> param);

	boolean deleteOrderCatalogService(Map<String, String[]> param);

	boolean auditOrderCatalogService(Map<String, String[]> param);

	List<Record> getShareActivity(Map<String, String[]> param);

	Boolean deleteShareActivity(String id);

	Boolean saveShareActivity(Map<String, String[]> param, String resultPath);

	int startOrstopAct(Map<String, String[]> param);

	List<Record> getStaticActivity(Map<String, String[]> param);

	Boolean deleteStaticActivity(String id);

	Boolean saveStaticActivity(Map<String, String[]> param);

	int startOrstopStaAct(Map<String, String[]> param);

	List<Record> getActivity(Map<String, String[]> param);

	boolean saveActivity(Map<String, String[]> param, String smallPath,
			String bigPath, String sharePath);

	Boolean deleteActivity(String id);

	int startOrstopActivity(Map<String, String[]> param);

	List<Record> getActivityEntranceReleation(Map<String, String[]> param);

	boolean addActivityEntrance(Map<String, String[]> param);

	List<Record> getActivityEntrance(Map<String, String[]> param);

	Boolean deleteActivityEntrance(String id);

	List<Record> getActivityDetail(Map<String, String[]> param);

	Boolean saveActivityDetail(Map<String, String[]> param, String resultPath);

	Boolean deleteActivityDetail(String id);

	List<Record> getActivityPlatFormReleation(Map<String, String[]> param);

	boolean addActivityPlatForm(Map<String, String[]> param);

	List<Record> getActivityPlatForm(Map<String, String[]> param);

	Boolean deleteActivityPlatForm(String id);

	List<Record> getRecommendService(Map<String, String[]> param);

	boolean saveRecommendService(Map<String, String[]> param);

	Boolean deleteRecommendService(String id);

	int startOrstopRecommendSer(Map<String, String[]> param);

}
