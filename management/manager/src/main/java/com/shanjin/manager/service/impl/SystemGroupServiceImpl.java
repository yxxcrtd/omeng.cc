package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemGroup;
import com.shanjin.manager.dao.SystemGroupDao;
import com.shanjin.manager.dao.impl.SystemGroupDaoImpl;
import com.shanjin.manager.service.ISystemGroupService;
import com.shanjin.manager.utils.ResourceTreeUtil;
import com.shanjin.manager.utils.TreeNode;

public class SystemGroupServiceImpl implements ISystemGroupService{
	private SystemGroupDao systemGroupDao=new SystemGroupDaoImpl();

	public List<SystemGroup> systemGroupList(Map<String, String[]> param) {
		return systemGroupDao.systemGroupList(param);
	}

	public Boolean deleteGroup(String ids) {
		return systemGroupDao.deleteGroup(ids);
	}

	public int saveGroup(Map<String, String[]> param,String operUserName) {
		return systemGroupDao.saveGroup(param,operUserName);
	}
	
	private List<SystemGroup> getOtherGroupsByUser(Long userId) {
		return systemGroupDao.getOtherGroupsByUser(userId);
	}

	private List<SystemGroup> getSelfGroupsByUser(Long userId) {
		return systemGroupDao.getSelfGroupsByUser(userId);
	}
	
	public List<TreeNode> getUserGroup(Long userId) {
		List<SystemGroup> selfGroups=getSelfGroupsByUser(userId);
		List<SystemGroup> otherGroups=getOtherGroupsByUser(userId);
		List<TreeNode> userGroup=ResourceTreeUtil.groupTreeJson(selfGroups, otherGroups);
		return userGroup;
	}

	public Boolean saveUserGroups(Long userId, String groupIds) {
		return systemGroupDao.saveUserGroups(userId, groupIds);
	}
}
