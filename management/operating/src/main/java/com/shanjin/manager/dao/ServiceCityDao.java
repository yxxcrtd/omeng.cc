package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

public interface ServiceCityDao {

	public List<Record> getServiceCityList(Map<String, String[]> param);
	
	public int saveServiceCity(Map<String, String[]> param);
	
	public void delServiceCity(String ids);
	
	public boolean updateOpenStatus(String ids,String status);
	
}
