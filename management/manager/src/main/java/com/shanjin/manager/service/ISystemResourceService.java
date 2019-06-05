package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemResource;

public interface ISystemResourceService {
	
	List<SystemResource> systemResourceList(Map<String, String[]> param);
	
	Boolean deleteResource(String ids);
	
	Boolean saveResource(Map<String, String[]> param);
	
	List<SystemResource> getAllResource();
	
	Map<String,String> getResourceByRoleId(Long roleId);
	
	Boolean saveRoleResources(Long roleId,String resIds);

	Boolean saveGroupRoles(Long groupId, String resIds);
	

}
