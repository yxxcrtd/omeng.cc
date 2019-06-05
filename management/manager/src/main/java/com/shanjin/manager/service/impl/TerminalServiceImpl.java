package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.AppUpdate;
import com.shanjin.manager.dao.TerminalDao;
import com.shanjin.manager.dao.impl.TerminalDaoImpl;
import com.shanjin.manager.service.ITerminalService;

public class TerminalServiceImpl implements ITerminalService {
	
	private TerminalDao terminalDao=new TerminalDaoImpl();
	
	public List<AppUpdate> getClientVersionList(Map<String, String[]> param) {
		return terminalDao.getClientVersionList(param);
	}

	public boolean saveClientVersion(Map<String, String[]> param, String downloadUrl) {
		return terminalDao.saveClientVersion(param, downloadUrl);
	}

	public boolean deleteClientVersion(String ids) {
		return terminalDao.deleteClientVersion(ids);
	}

	public boolean updatePublishStatus(String ids, int status) {
		return terminalDao.updatePublishStatus(ids, status);
	}

}
