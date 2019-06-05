package com.shanjin.log.analysis.service.impl;

import java.util.Date;

import com.shanjin.log.analysis.dao.VisitLogImportDao;
import com.shanjin.log.analysis.dao.impl.VisitLogImportDaoImpl;
import com.shanjin.log.analysis.service.VisitLogImportService;

public class VisitLogImportServiceImpl implements VisitLogImportService{
	private VisitLogImportDao visitLogImportDao = new VisitLogImportDaoImpl();

	@Override
	public void importVisitLog(Date logDate) {
		visitLogImportDao.importVisitLog(logDate);
	}
}
