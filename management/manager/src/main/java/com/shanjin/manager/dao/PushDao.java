package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

public interface PushDao {

	List<Record> pushList(Map<String, String[]> param);
	
	public long pushListSize(Map<String, String[]> param);

	int savePush(Map<String, String[]> param);

	boolean deletePushs(String ids);

	List<Record> getPushConfigList(Map<String, String[]> param);

	boolean deletePushConfig(String ids);

	int addPushConfig(Map<String, String[]> param);

	List<Record> getRestrictUpdate(Map<String, String[]> param);

	Boolean deleteRestrictUpdate(String id);

	Boolean saveRestrictUpdate(Map<String, String[]> param);

	int startOrstopRestrictUpdate(Map<String, String[]> param);
}
