package com.shanjin.log.analysis.service.impl;

import java.util.Date;

import com.shanjin.log.analysis.dao.VisitLogHandleDao;
import com.shanjin.log.analysis.dao.impl.VisitLogHandleDaoImpl;
import com.shanjin.log.analysis.service.VisitLogHandleService;

public class VisitLogHandleServiceImpl implements VisitLogHandleService{
	
	private VisitLogHandleDao visitLogHandleDao = new VisitLogHandleDaoImpl();

	@Override
	public void insertTerminalStatistics(Date logDate) {
		visitLogHandleDao.insertTerminalStatistics(logDate);		
	}

	@Override
	public void insertFirstVisitTerminal(Date logDate) {
		visitLogHandleDao.insertFirstVisitTerminal(logDate);
	}

}
