package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemGroup;
import com.shanjin.manager.utils.TreeNode;

public interface ISystemGroupService {

	List<SystemGroup> systemGroupList(Map<String, String[]> param);
	
	Boolean deleteGroup(String ids);
	
	/**
	 * 0:失败；1：成功；2：名称已存在
	 * @param param
	 * @return
	 */
	int saveGroup(Map<String, String[]> param,String operUserName);
	
	/**
	 * 用户群组树
	 * @param userId
	 * @return
	 */
	List<TreeNode> getUserGroup(Long userId);
	
	Boolean saveUserGroups(Long userId,String groupIds);
}
