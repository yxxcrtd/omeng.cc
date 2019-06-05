package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.AppInfo;

public interface IAppInfoService {
	List<AppInfo> appList(Map<String, String[]> param);
	
	Boolean deleteApp(String ids);
	
    int saveApp(Map<String, String[]> param);
    
}
