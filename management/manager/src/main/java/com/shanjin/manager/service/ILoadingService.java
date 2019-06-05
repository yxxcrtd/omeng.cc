package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.Loading;

public interface ILoadingService {
	
	public List<Loading> getLoadingList(Map<String, String[]> param);
	
	public boolean saveLoading(Map<String, String[]> param, String path);
	
	public boolean deleteLoading(String ids);
	
	public boolean updatePublishStatus(String ids,int status);
}
