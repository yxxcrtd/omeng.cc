package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemUserInfo;
import com.shanjin.manager.dao.SystemUserInfoDao;
import com.shanjin.manager.dao.impl.SystemUserInfoDaoImpl;
import com.shanjin.manager.service.ISystemUserInfoService;

public class SystemUserInfoServiceImpl implements ISystemUserInfoService{
	private SystemUserInfoDao systemUserInfoDao=new SystemUserInfoDaoImpl();
	
	
	public List<SystemUserInfo> systemUserList(Map<String, String> param) {
		return systemUserInfoDao.systemUserList(param);
	}


	public Boolean deleteUser(String ids) {
		return systemUserInfoDao.deleteUser(ids);
	}


	public int saveUser(Map<String, String[]> param,String operUserName) {
		return systemUserInfoDao.saveUser(param,operUserName);
	}


	public SystemUserInfo getUserByName(String userName) {
		return systemUserInfoDao.getUserByName(userName);
	}


	public Boolean editSystemUserInfo(Map<String, String[]> param,String operUserName) {
		return systemUserInfoDao.editSystemUserInfo(param,operUserName);
	}


	public int modifyUserPwd(Long userId,String oldPwd,String newPwd,String pwdHints, String operUserName) {
		return systemUserInfoDao.modifyUserPwd(userId,oldPwd,newPwd,pwdHints, operUserName);
	}


	@Override
	public List<SystemUserInfo> systemDelUserList(Map<String, String[]> param) {
		return systemUserInfoDao.systemDelUserList(param);
	}


	@Override
	public Boolean deleteAbUser(String ids) {
		return systemUserInfoDao.deleteAbUser(ids);
	}


	@Override
	public Boolean recoveryUser(String ids) {
		return systemUserInfoDao.recoveryUser(ids);
	}


	@Override
	public int setAccount(String id) {
		return systemUserInfoDao.setAccount(id);
	}
	
	

}
