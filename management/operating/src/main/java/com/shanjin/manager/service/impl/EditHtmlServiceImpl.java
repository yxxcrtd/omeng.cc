package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.dao.EditHtmlDao;
import com.shanjin.manager.dao.impl.EditHtmlDaoImpl;
import com.shanjin.manager.service.IEditHtmlService;

public class EditHtmlServiceImpl implements IEditHtmlService {

	private EditHtmlDao editHtmlDao=new EditHtmlDaoImpl();
	
	@Override
	public List<Record> getEditHtmlList(Map<String, String[]> param) {
		return editHtmlDao.getEditHtmlList(param);
	}

	@Override
	public Boolean saveEditHtml(Map<String, String[]> param, String resultPath) {
		return editHtmlDao.saveEditHtml(param,resultPath);
	}

	@Override
	public Boolean deleteEditHtml(Map<String, String[]> param) {
		return editHtmlDao.deleteEditHtml(param);
	}

	@Override
	public Boolean cancelRecordAll(Map<String, String[]> param) {
		return editHtmlDao.cancelRecordAll(param);
	}

}
