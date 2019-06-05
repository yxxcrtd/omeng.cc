package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

public interface DefinedService {

	List<Record> getPlanTableList(Map<String, String[]> param);

	Boolean deletePlanTable(Map<String, String[]> param);

	Boolean addPlanTable(Map<String, String[]> param);

	Boolean editPlanTable(Map<String, String[]> param);

	List<Record> getPlanModelList(Map<String, String[]> param);

	Boolean deletePlanModel(Map<String, String[]> param);

	Boolean addPlanModel(Map<String, String[]> param, String resultPath);

	Boolean editPlanModel(Map<String, String[]> param, String resultPath);

	List<Record> getPlanModelItemList(Map<String, String[]> param);

	Boolean deletePlanModelItem(Map<String, String[]> param);

	Boolean addPlanModelItem(Map<String, String[]> param);

	Boolean editPlanModelItem(Map<String, String[]> param);
	
	List<Record> getObjectTableList(Map<String, String[]> param);

	Boolean deleteObjectTable(Map<String, String[]> param);

	Boolean addObjectTable(Map<String, String[]> param);

	Boolean editObjectTable(Map<String, String[]> param);

	List<Record> getObjectModelList(Map<String, String[]> param);

	Boolean deleteObjectModel(Map<String, String[]> param);

	Boolean addObjectModel(Map<String, String[]> param, String resultPath);

	Boolean editObjectModel(Map<String, String[]> param, String resultPath);

	List<Record> getObjectModelItemList(Map<String, String[]> param);

	Boolean deleteObjectModelItem(Map<String, String[]> param);

	Boolean addObjectModelItem(Map<String, String[]> param);

	Boolean editObjectModelItem(Map<String, String[]> param);
	
	Boolean pubObjectTable(String ids,String isPub);
	
	Boolean pubAllObjectTable(String isPub);

}
