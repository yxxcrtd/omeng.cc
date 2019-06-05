package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

public interface DictionaryDao {

	List<Record> getDictionaryList(Map<String, String[]> param);

	boolean addDictionary(Map<String, String[]> param);

	boolean deleteDictionary(Map<String, String[]> param);

	boolean editDictionary(Map<String, String[]> param);

	List<Record> getServiceTypeList(Map<String, String[]> param);

	boolean addServiceType(Map<String, String[]> param);

	boolean editServiceType(Map<String, String[]> param);

	boolean deleteServiceType(Map<String, String[]> param);
	
	public boolean saveServiceType(Map<String, String[]> param, String showIconPath,String orderIconPath);

	List<Record> getConfigurationList(Map<String, String[]> param);

	boolean addConfiguration(Map<String, String[]> param);

	boolean editConfiguration(Map<String, String[]> param);

	List<Record> getValueLabelList(Map<String, String[]> param);

	boolean addValueLabel(Map<String, String[]> param);

	boolean editValueLabel(Map<String, String[]> param);

	boolean deleteValueLabel(Map<String, String[]> param);

	boolean startOrstopLabel(Map<String, String[]> param);

	int addSystemPicParam(Map<String, String[]> param, String resultPath);

	List<Record> getDictAttchList(Map<String, String[]> param);

	boolean deleteDictAttch(Map<String, String[]> param);

	boolean deleteConfigurationParam(Map<String, String[]> param);

}
