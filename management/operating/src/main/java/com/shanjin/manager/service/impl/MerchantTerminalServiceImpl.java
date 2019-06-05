package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.dao.MerchantTerminalDao;
import com.shanjin.manager.dao.impl.MerchantTerminalDaoImpl;
import com.shanjin.manager.service.IMerchantTerminalService;

public class MerchantTerminalServiceImpl implements IMerchantTerminalService{
	MerchantTerminalDao merchantTerminalDao = new MerchantTerminalDaoImpl();

	@Override
	public List<Record> merchantDeviceList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantDeviceList(param);
	}

	@Override
	public List<Record> merchantDevicePieList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantDevicePieList(param);
	}

	@Override
	public List<Record> merchantDeviceColList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantDeviceColList(param);
	}

	@Override
	public List<Record> merchantStartUpList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantStartUpList(param);
	}

	@Override
	public List<Record> merchantStartUpLineList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantStartUpLineList(param);
	}

	@Override
	public List<Record> merchantChannelList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantChannelList(param);
	}

	@Override
	public List<Record> merchantChannelPieList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantChannelPieList(param);
	}

	@Override
	public List<Record> merchantChannelColList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantChannelColList(param);
	}

	@Override
	public List<Record> merchantVersionList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantVersionList(param);
	}

	@Override
	public List<Record> merchantVersionPieList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantVersionPieList(param);
	}

	@Override
	public List<Record> merchantVersionColList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantVersionColList(param);
	}

	@Override
	public List<Record> merchantAreaList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantAreaList(param);
	}

	@Override
	public List<Record> merchantAreaPieList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantAreaPieList(param);
	}

	@Override
	public List<Record> merchantAreaColList(Map<String, String[]> param) {
		return merchantTerminalDao.merchantAreaColList(param);
	}
}
