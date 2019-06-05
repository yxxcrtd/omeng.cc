package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.dao.SystemResourceDao;
import com.shanjin.manager.dao.impl.SystemResourceDaoImpl;
import com.shanjin.manager.service.ISystemResourceService;

public class SystemResourceServiceImpl implements ISystemResourceService{
	private SystemResourceDao systemResourceDao=new SystemResourceDaoImpl();

	public List<SystemResource> systemResourceList(Map<String, String[]> param) {
		return systemResourceDao.systemResourceList(param);
	}

	public Boolean deleteResource(String ids) {
		return systemResourceDao.deleteResource(ids);
	}

	public Boolean saveResource(Map<String, String[]> param) {
		return systemResourceDao.saveResource(param);
	}

	public List<SystemResource> getAllResource() {
		return systemResourceDao.getAllResource();
	}

	public Map<String, String> getResourceByRoleId(Long roleId) {
		return systemResourceDao.getResourceByRoleId(roleId);
	}

	public Boolean saveRoleResources(Long roleId, String resIds) {
		return systemResourceDao.saveRoleResources(roleId, resIds);
	}

	public Boolean saveGroupRoles(Long groupId, String resIds) {
		return systemResourceDao.saveGroupRoles(groupId, resIds);
	}
}
