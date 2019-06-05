package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.AppUpdate;

public interface TerminalDao {

	public List<AppUpdate> getClientVersionList(Map<String, String[]> param);
	
	public boolean saveClientVersion(Map<String, String[]> param, String downloadUrl);
	
	public boolean deleteClientVersion(String ids);
	
	public boolean updatePublishStatus(String ids,int status);
	
}
