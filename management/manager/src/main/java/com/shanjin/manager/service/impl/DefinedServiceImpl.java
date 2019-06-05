package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.dao.DefinedDao;
import com.shanjin.manager.dao.impl.DefinedDaoImpl;
import com.shanjin.manager.service.DefinedService;

public class DefinedServiceImpl implements DefinedService {
	private DefinedDao definedDao=new DefinedDaoImpl();
	@Override
	public List<Record> getPlanTableList(Map<String, String[]> param) {
		List<Record> objectTableList=definedDao.getPlanTableList(param);
		return objectTableList;
	}

	@Override
	public Boolean deletePlanTable(Map<String, String[]> param) {
		Boolean flag=definedDao.deletePlanTable(param);
		return flag;
	}

	@Override
	public Boolean addPlanTable(Map<String, String[]> param) {
		Boolean flag=definedDao.addPlanTable(param);
		return flag;
	}

	@Override
	public Boolean editPlanTable(Map<String, String[]> param) {
		Boolean flag=definedDao.editPlanTable(param);
		return flag;
	}

	@Override
	public List<Record> getPlanModelList(Map<String, String[]> param) {
		List<Record> objectModelList=definedDao.getPlanModelList(param);
		return objectModelList;
	}

	@Override
	public Boolean deletePlanModel(Map<String, String[]> param) {
		Boolean flag=definedDao.deletePlanModel(param);
		return flag;
	}

	@Override
	public Boolean addPlanModel(Map<String, String[]> param, String resultPath) {
		Boolean flag=definedDao.addPlanModel(param,resultPath);
		return flag;
	}

	@Override
	public Boolean editPlanModel(Map<String, String[]> param, String resultPath) {
		Boolean flag=definedDao.editPlanModel(param,resultPath);
		return flag;
	}

	@Override
	public List<Record> getPlanModelItemList(Map<String, String[]> param) {
		List<Record> objectModelItemList=definedDao.getPlanModelItemList(param);
		return objectModelItemList;
	}

	@Override
	public Boolean deletePlanModelItem(Map<String, String[]> param) {
		Boolean flag=definedDao.deletePlanModelItem(param);
		return flag;
	}

	@Override
	public Boolean addPlanModelItem(Map<String, String[]> param) {
		Boolean flag=definedDao.addPlanModelItem(param);
		return flag;
	}

	@Override
	public Boolean editPlanModelItem(Map<String, String[]> param) {
		Boolean flag=definedDao.editPlanModelItem(param);
		return flag;
	}
	@Override
	public List<Record> getObjectTableList(Map<String, String[]> param) {
		List<Record> objectTableList=definedDao.getObjectTableList(param);
		return objectTableList;
	}

	@Override
	public Boolean deleteObjectTable(Map<String, String[]> param) {
		Boolean flag=definedDao.deleteObjectTable(param);
		return flag;
	}

	@Override
	public Boolean addObjectTable(Map<String, String[]> param) {
		Boolean flag=definedDao.addObjectTable(param);
		return flag;
	}

	@Override
	public Boolean editObjectTable(Map<String, String[]> param) {
		Boolean flag=definedDao.editObjectTable(param);
		return flag;
	}

	@Override
	public List<Record> getObjectModelList(Map<String, String[]> param) {
		List<Record> objectModelList=definedDao.getObjectModelList(param);
		return objectModelList;
	}

	@Override
	public Boolean deleteObjectModel(Map<String, String[]> param) {
		Boolean flag=definedDao.deleteObjectModel(param);
		return flag;
	}

	@Override
	public Boolean addObjectModel(Map<String, String[]> param, String resultPath) {
		Boolean flag=definedDao.addObjectModel(param,resultPath);
		return flag;
	}

	@Override
	public Boolean editObjectModel(Map<String, String[]> param, String resultPath) {
		Boolean flag=definedDao.editObjectModel(param,resultPath);
		return flag;
	}

	@Override
	public List<Record> getObjectModelItemList(Map<String, String[]> param) {
		List<Record> objectModelItemList=definedDao.getObjectModelItemList(param);
		return objectModelItemList;
	}

	@Override
	public Boolean deleteObjectModelItem(Map<String, String[]> param) {
		Boolean flag=definedDao.deleteObjectModelItem(param);
		return flag;
	}

	@Override
	public Boolean addObjectModelItem(Map<String, String[]> param) {
		Boolean flag=definedDao.addObjectModelItem(param);
		return flag;
	}

	@Override
	public Boolean editObjectModelItem(Map<String, String[]> param) {
		Boolean flag=definedDao.editObjectModelItem(param);
		return flag;
	}

	@Override
	public Boolean pubObjectTable(String ids, String isPub) {
		return definedDao.pubObjectTable(ids, isPub);
	}

	@Override
	public Boolean pubAllObjectTable(String isPub) {
		return definedDao.pubAllObjectTable(isPub);
	}

}
