package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.dao.ServiceCityDao;
import com.shanjin.manager.dao.impl.ServiceCityDaoImpl;
import com.shanjin.manager.service.IServiceCityService;

public class ServiceCityServiceImpl implements IServiceCityService{

	ServiceCityDao serviceCityDao = new ServiceCityDaoImpl();
	
	@Override
	public List<Record> getServiceCityList(Map<String, String[]> param) {
		return serviceCityDao.getServiceCityList(param);
	}

	@Override
	public int saveServiceCity(Map<String, String[]> param) {
		return serviceCityDao.saveServiceCity(param);
	}

	@Override
	public void delServiceCity(String ids) {
		serviceCityDao.delServiceCity(ids);
	}

	@Override
	public boolean updateOpenStatus(String ids, String status) {
		return serviceCityDao.updateOpenStatus(ids, status);
	}

}
