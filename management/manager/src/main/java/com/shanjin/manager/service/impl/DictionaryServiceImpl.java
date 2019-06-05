package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.dao.DictionaryDao;
import com.shanjin.manager.dao.impl.DictionaryDaoImpl;
import com.shanjin.manager.service.IDictionaryService;

public class DictionaryServiceImpl implements IDictionaryService{

	private DictionaryDao dictionaryDao = new DictionaryDaoImpl();
	
	@Override
	public List<Record> getDictionaryList(Map<String, String[]> param) {
		
		return dictionaryDao.getDictionaryList(param);
	}

	@Override
	public boolean addDictionary(Map<String, String[]> param) {
		return dictionaryDao.addDictionary(param);
	}

	@Override
	public boolean deleteDictionary(Map<String, String[]> param) {
		return dictionaryDao.deleteDictionary(param);
	}

	@Override
	public boolean editDictionary(Map<String, String[]> param) {
		return dictionaryDao.editDictionary(param);
	}

	@Override
	public List<Record> getServiceTypeList(Map<String, String[]> param) {
		return dictionaryDao.getServiceTypeList(param);
	}

	@Override
	public boolean addServiceType(Map<String, String[]> param) {
		return dictionaryDao.addServiceType(param);
	}

	@Override
	public boolean editServiceType(Map<String, String[]> param) {
		return dictionaryDao.editServiceType(param);
	}

	@Override
	public boolean deleteServiceType(Map<String, String[]> param) {
		return dictionaryDao.deleteServiceType(param);
	}

	@Override
	public boolean saveServiceType(Map<String, String[]> param,
			String showIconPath, String orderIconPath) {
		return dictionaryDao.saveServiceType(param, showIconPath, orderIconPath);
	}

	@Override
	public List<Record> getConfigurationList(Map<String, String[]> param) {
		return dictionaryDao.getConfigurationList(param);
	}

	@Override
	public boolean addConfiguration(Map<String, String[]> param) {
		return dictionaryDao.addConfiguration(param);
	}

	@Override
	public boolean editConfiguration(Map<String, String[]> param) {
		return dictionaryDao.editConfiguration(param);
	}

	@Override
	public List<Record> getValueLabelList(Map<String, String[]> param) {
		return dictionaryDao.getValueLabelList(param);
	}

	@Override
	public boolean addValueLabel(Map<String, String[]> param) {
		return dictionaryDao.addValueLabel(param);
	}

	@Override
	public boolean editValueLabel(Map<String, String[]> param) {
		return dictionaryDao.editValueLabel(param);
	}

	@Override
	public boolean deleteValueLabel(Map<String, String[]> param) {
		return dictionaryDao.deleteValueLabel(param);
	}

	@Override
	public boolean startOrstopLabel(Map<String, String[]> param) {
		return dictionaryDao.startOrstopLabel(param);
	}

	@Override
	public int addSystemPicParam(Map<String, String[]> param,
			String resultPath) {
		return dictionaryDao.addSystemPicParam(param,resultPath);
	}

	@Override
	public List<Record> getDictAttchList(Map<String, String[]> param) {
		return dictionaryDao.getDictAttchList(param);
	}

	@Override
	public boolean deleteDictAttch(Map<String, String[]> param) {
		return dictionaryDao.deleteDictAttch(param);
	}

	@Override
	public boolean deleteConfigurationParam(Map<String, String[]> param) {
		return dictionaryDao.deleteConfigurationParam(param);
	}

}
