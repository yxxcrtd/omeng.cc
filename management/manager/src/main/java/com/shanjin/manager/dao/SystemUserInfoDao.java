package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.shanjin.sso.bean.SystemUserInfo;

public interface SystemUserInfoDao {
	
	List<SystemUserInfo> systemUserList(Map<String, String> param);
	
	public Boolean deleteUser(String ids);
	
	public int setAccount(String id);
	
	public int saveUser(Map<String, String[]> param,String operUserName);
	
	public SystemUserInfo getUserByName(String userName); 
	
	Boolean editSystemUserInfo(Map<String, String[]> param,String operUserName);
	
	public int modifyUserPwd(Long userId,String oldPwd,String newPwd,String pwdHints,String operUserName);
	
	List<SystemUserInfo> systemDelUserList(Map<String, String[]> param);
	
	public Boolean deleteAbUser(String ids); // 彻底删除用户（物理删除）
	
	public Boolean recoveryUser(String ids); // 恢复已删除用户
}
