package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.UserChannel;
import com.shanjin.manager.Bean.UserStartUp;
import com.shanjin.manager.Bean.UserVisit;
import com.shanjin.manager.dao.UserTerminalDao;
import com.shanjin.manager.dao.impl.UserTerminalDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.IUserTerminalService;
import com.shanjin.manager.utils.StringUtil;

public class UserTerminalServiceImpl implements IUserTerminalService{
    
	UserTerminalDao userTerminalDao = new UserTerminalDaoImpl();
	
	@Override
	public List<Record> userAreaList(Map<String, String[]> param) {
		return userTerminalDao.userAreaList(param);
	}

	@Override
	public List<Record> userAreaPieList(Map<String, String[]> param) {
		return userTerminalDao.userAreaPieList(param);
	}

	@Override
	public List<Record> userAreaColList(Map<String, String[]> param) {
		return userTerminalDao.userAreaColList(param);
	}

	@Override
	public List<Record> userVersionList(Map<String, String[]> param) {
		return userTerminalDao.userVersionList(param);
	}

	@Override
	public List<Record> userVersionPieList(Map<String, String[]> param) {
		return userTerminalDao.userVersionPieList(param);
	}

	@Override
	public List<Record> userVersionColList(Map<String, String[]> param) {
		return userTerminalDao.userVersionColList(param);
	}

	@Override
	public List<Record> userDeviceList(Map<String, String[]> param) {
		return userTerminalDao.userDeviceList(param);
	}

	@Override
	public List<Record> userDevicePieList(Map<String, String[]> param) {
		return userTerminalDao.userDevicePieList(param);
	}

	@Override
	public List<Record> userDeviceColList(Map<String, String[]> param) {
		return userTerminalDao.userDeviceColList(param);
	}

	@Override
	public List<Record> userStartUpList(Map<String, String[]> param) {
		return userTerminalDao.userStartUpList(param);
	}

	@Override
	public List<Record> userStartUpLineList(Map<String, String[]> param) {
		return userTerminalDao.userStartUpLineList(param);
	}

	@Override
	public List<Record> userChannelList(Map<String, String[]> param) {
		return userTerminalDao.userChannelList(param);
	}

	@Override
	public List<Record> userChannelPieList(Map<String, String[]> param) {
		return userTerminalDao.userChannelPieList(param);
	}

	@Override
	public List<Record> userChannelColList(Map<String, String[]> param) {
		return userTerminalDao.userChannelColList(param);
	}

	@Override
	public List<UserVisit> exportUserAreaListExcel(Map<String, String[]> param) {
		return userTerminalDao.exportUserAreaListExcel(param);
	}

	@Override
	public List<Pair> getExportUserAreaTitles(Map<String, String[]> param) {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("accumulateNum", "累计用户"));
		titles.add(new Pair("addNum", "新增用户"));
		titles.add(new Pair("activityNum", "活跃用户"));
		String dimension=param.get("dimension")[0];
		String[] dim = null;
		if(!StringUtil.isNull(dimension)){	
			dim=dimension.split(",");
		if(StringUtil.nullToBoolean(dim)){
			 for(String s:dim){
				 
				 if("province".equals(s)){
					 titles.add(new Pair("province", "省份"));
				 }
				 if("city".equals(s)){
					 titles.add(new Pair("city", "城市"));
				 }
				 
			 } 
		}
		}	
		return titles;
	}

	@Override
	public List<UserVisit> exportUserDeviceListExcel(Map<String, String[]> param) {
		return userTerminalDao.exportUserDeviceListExcel(param);
	}

	@Override
	public List<Pair> getExportUserDeviceTitles(Map<String, String[]> param) {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("num", "设备数量"));
		titles.add(new Pair("firstVersion", "占比"));
	
		String dimension=param.get("dimension")[0];
		String[] dim = null;
		if(!StringUtil.isNull(dimension)){	
			dim=dimension.split(",");
		if(StringUtil.nullToBoolean(dim)){
			 for(String s:dim){
				 if("model".equals(s)){
					 titles.add(new Pair("model", "设备型号"));
				 }
				 if("clientType".equals(s)){
					 titles.add(new Pair("clientType", "设备类型"));
				 }
				 if("system".equals(s)){
					 titles.add(new Pair("system", "系统版本"));
				 }
			 } 
		}
		}	
		return titles;
	}

	@Override
	public List<UserVisit> exportUserVersionListExcel(
			Map<String, String[]> param) {
		return userTerminalDao.exportUserVersionListExcel(param);
	}

	@Override
	public List<Pair> getExportUserVersionTitles(Map<String, String[]> param) {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("version", "版本号"));
		titles.add(new Pair("num", "用户数"));
		return titles;
	}

	@Override
	public List<UserChannel> exportUserChannelListExcel(
			Map<String, String[]> param) {
		return userTerminalDao.exportUserChannelListExcel(param);
	}

	@Override
	public List<Pair> getExportUserChannelTitles(Map<String, String[]> param) {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("channel", "渠道"));
		titles.add(new Pair("model", "新增用户"));
		titles.add(new Pair("resolution", "活跃用户"));
		return titles;
	}

	@Override
	public List<UserStartUp> exportUserStartUpListExcel(
			Map<String, String[]> param) {
		return userTerminalDao.exportUserStartUpListExcel(param);
	}

	@Override
	public List<Pair> getExportUserStartUpTitles(Map<String, String[]> param) {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("visitDay", "日期"));
		titles.add(new Pair("aphoneStartNum", "启动次数(aphone)"));
		titles.add(new Pair("iphoneStartNum", "启动次数(iphone)"));
		titles.add(new Pair("startNum", "启动次数(合计)"));
		return titles;
	}
	
	
}
