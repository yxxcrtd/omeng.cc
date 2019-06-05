package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemRole;
import com.shanjin.manager.dao.SystemRoleDao;
import com.shanjin.manager.dao.impl.SystemRoleDaoImpl;
import com.shanjin.manager.service.ISystemRoleService;
import com.shanjin.manager.utils.ResourceTreeUtil;
import com.shanjin.manager.utils.TreeNode;

public class SystemRoleServiceImpl implements ISystemRoleService{
	private SystemRoleDao systemRoleDao=new SystemRoleDaoImpl();

	public List<SystemRole> systemRoleList(Map<String, String[]> param) {
		return systemRoleDao.systemRoleList(param);
	}

	public Boolean deleteRole(String ids) {
		return systemRoleDao.deleteRole(ids);
	}

	public int saveRole(Map<String, String[]> param,String operUserName) {
		return systemRoleDao.saveRole(param,operUserName);
	}

	private List<SystemRole> getOtherRolesByGroup(Long groupId) {
		return systemRoleDao.getOtherRolesByGroup(groupId);
	}

	private List<SystemRole> getSelfRolesByGroup(Long groupId) {
		return systemRoleDao.getSelfRolesByGroup(groupId);
	}

	public List<TreeNode> getGroupRole(Long groupId) {
		List<SystemRole> selfRoles=getSelfRolesByGroup(groupId);
		List<SystemRole> otherRoles=getOtherRolesByGroup(groupId);
		List<TreeNode> groupRole=ResourceTreeUtil.roleTreeJson(selfRoles, otherRoles);
		return groupRole;
	}

	private List<SystemRole> getOtherRolesByUser(Long userId) {
		return systemRoleDao.getOtherRolesByUser(userId);
	}

	private List<SystemRole> getSelfRolesByUser(Long userId) {
		return systemRoleDao.getSelfRolesByUser(userId);
	}
	
	public List<TreeNode> getUserRole(Long userId) {
		List<SystemRole> selfRoles=getSelfRolesByUser(userId);
		List<SystemRole> otherRoles=getOtherRolesByUser(userId);
		List<TreeNode> userRole=ResourceTreeUtil.roleTreeJson(selfRoles, otherRoles);
		return userRole;
	}

	public Boolean saveUserRoles(Long userId, String roleIds) {
		return systemRoleDao.saveUserRoles(userId,roleIds);
	}

}
