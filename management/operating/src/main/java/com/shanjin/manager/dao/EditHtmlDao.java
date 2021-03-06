package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

public interface EditHtmlDao {

	List<Record> getEditHtmlList(Map<String, String[]> param);

	Boolean saveEditHtml(Map<String, String[]> param, String resultPath);

	Boolean deleteEditHtml(Map<String, String[]> param);

	Boolean cancelRecordAll(Map<String, String[]> param);

}
