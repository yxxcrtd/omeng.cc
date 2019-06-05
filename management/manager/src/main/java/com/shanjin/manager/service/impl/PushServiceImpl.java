package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.dao.PushDao;
import com.shanjin.manager.dao.impl.PushDaoImpl;
import com.shanjin.manager.service.IPushService;

public class PushServiceImpl implements IPushService{

	private PushDao pushDao = new PushDaoImpl();
	
	@Override
	public List<Record> pushList(Map<String, String[]> param) {
		return pushDao.pushList(param);
	}

	@Override
	public long pushListSize(Map<String, String[]> param) {
		return pushDao.pushListSize(param);
	}

	@Override
	public int savePush(Map<String, String[]> param) {
		return pushDao.savePush(param);
	}

	@Override
	public boolean deletePushs(String ids) {
		return pushDao.deletePushs(ids);
	}

	@Override
	public List<Record> getPushConfigList(Map<String, String[]> param) {
		return pushDao.getPushConfigList(param);
	}
	
	@Override
	public boolean deletePushConfig(String ids) {
		return pushDao.deletePushConfig(ids);
	}
	
	@Override
	public int addPushConfig(Map<String, String[]> param){
		return pushDao.addPushConfig(param);
	}

	@Override
	public List<Record> getRestrictUpdate(Map<String, String[]> param) {
		return pushDao.getRestrictUpdate(param);
	}

	@Override
	public Boolean deleteRestrictUpdate(String id) {
		return pushDao.deleteRestrictUpdate(id);
	}

	@Override
	public Boolean saveRestrictUpdate(Map<String, String[]> param) {
		return pushDao.saveRestrictUpdate(param);
	}

	@Override
	public int startOrstopRestrictUpdate(Map<String, String[]> param) {
		return pushDao.startOrstopRestrictUpdate(param);
	};
}
