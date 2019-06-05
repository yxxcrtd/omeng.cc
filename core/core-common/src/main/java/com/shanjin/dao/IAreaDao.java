package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IAreaDao {

	public List<Map<String, Object>> getProvince();

	public List<Map<String, Object>> getCity(Map<String, Object> params);

	public List<Map<String, Object>> getCityList();
	
	public List<Map<String, Object>> getAllCityList();
}
