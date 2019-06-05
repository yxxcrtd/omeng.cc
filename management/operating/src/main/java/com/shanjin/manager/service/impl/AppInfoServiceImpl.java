package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.dao.AppInfoDao;
import com.shanjin.manager.dao.impl.AppInfoDaoImpl;
import com.shanjin.manager.service.IAppInfoService;

public class AppInfoServiceImpl implements IAppInfoService{
	private AppInfoDao appInfoDao=new AppInfoDaoImpl();

	public List<AppInfo> appList(Map<String, String[]> param) {
		return appInfoDao.appList(param);
	}

	public Boolean deleteApp(String ids) {
		return appInfoDao.deleteApp(ids);
	}

	public int saveApp(Map<String, String[]> param) {
		return appInfoDao.saveApp(param);
	}

}
