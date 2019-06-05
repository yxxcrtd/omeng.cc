package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemGroup;

public interface SystemGroupDao {

	List<SystemGroup> systemGroupList(Map<String, String[]> param);
	
	Boolean deleteGroup(String ids);
	
    int saveGroup(Map<String, String[]> param,String operUserName);
    
	List<SystemGroup> getOtherGroupsByUser(Long userId);
	
	List<SystemGroup> getSelfGroupsByUser(Long userId);
	
	Boolean saveUserGroups(Long userId,String groupIds);
	
}
