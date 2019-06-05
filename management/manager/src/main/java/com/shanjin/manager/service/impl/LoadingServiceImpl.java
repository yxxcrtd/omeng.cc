package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.Loading;
import com.shanjin.manager.dao.LoadingDao;
import com.shanjin.manager.dao.impl.LoadingDaoImpl;
import com.shanjin.manager.service.ILoadingService;

public class LoadingServiceImpl implements ILoadingService{

	private LoadingDao loadingDao=new LoadingDaoImpl();

	@Override
	public List<Loading> getLoadingList(Map<String, String[]> param) {
		return loadingDao.getLoadingList(param);
	}

	@Override
	public boolean saveLoading(Map<String, String[]> param, String path) {
		return loadingDao.saveLoading(param, path);
	}

	@Override
	public boolean deleteLoading(String ids) {
		return loadingDao.deleteLoading(ids);
	}

	@Override
	public boolean updatePublishStatus(String ids, int status) {
		return loadingDao.updatePublishStatus(ids, status);
	}
	
}
