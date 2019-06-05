package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemRole;

public interface SystemRoleDao {

	List<SystemRole> systemRoleList(Map<String, String[]> param);
	
	Boolean deleteRole(String ids);
	
	int saveRole(Map<String, String[]> param,String operUserName);
	
	List<SystemRole> getOtherRolesByGroup(Long groupId);
	
	List<SystemRole> getSelfRolesByGroup(Long groupId);
	
	List<SystemRole> getOtherRolesByUser(Long userId);
	
	List<SystemRole> getSelfRolesByUser(Long userId);
	
	Boolean saveUserRoles(Long userId,String roleIds);
}
