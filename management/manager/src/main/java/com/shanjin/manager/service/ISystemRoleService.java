package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemRole;
import com.shanjin.manager.utils.TreeNode;

public interface ISystemRoleService {

	List<SystemRole> systemRoleList(Map<String, String[]> param);
	
	Boolean deleteRole(String ids);
	
	int saveRole(Map<String, String[]> param,String operUserName);
	
	/**
	 * 群组角色树
	 * @param groupId
	 * @return
	 */
	List<TreeNode> getGroupRole(Long groupId);
	
	/**
	 * 用户角色树
	 * @param userId
	 * @return
	 */
	List<TreeNode> getUserRole(Long userId);
	
	Boolean saveUserRoles(Long userId,String roleIds);
	
}
