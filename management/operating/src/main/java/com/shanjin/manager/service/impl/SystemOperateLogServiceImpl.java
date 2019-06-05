package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemOperateLog;
import com.shanjin.manager.dao.SystemOperateLogDao;
import com.shanjin.manager.dao.impl.SystemOperateLogDaoImpl;
import com.shanjin.manager.service.ISystemOperateLogService;

public class SystemOperateLogServiceImpl implements ISystemOperateLogService{

	private SystemOperateLogDao systemOperateLogDao=new SystemOperateLogDaoImpl();
	
	public List<SystemOperateLog> systemLogList(Map<String, String[]> param) {
		return systemOperateLogDao.systemLogList(param);
	}

	public Boolean addOperateLog(Map<String, String> param) {
		return systemOperateLogDao.addOperateLog(param);
	}

	public Boolean deleteSystemLog(String ids) {
		return systemOperateLogDao.deleteSystemLog(ids);
	}

}
