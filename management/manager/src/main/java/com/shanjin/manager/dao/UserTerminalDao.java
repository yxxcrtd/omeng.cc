package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.UserChannel;
import com.shanjin.manager.Bean.UserStartUp;
import com.shanjin.manager.Bean.UserVisit;

public interface UserTerminalDao {

    public List<Record> userAreaList(Map<String, String[]> param);
	
	public List<Record> userAreaPieList(Map<String, String[]> param);
	
	public List<Record> userAreaColList(Map<String, String[]> param);
	
	public List<Record> userVersionList(Map<String, String[]> param);
	
	public List<Record> userVersionPieList(Map<String, String[]> param);
	
	public List<Record> userVersionColList(Map<String, String[]> param);
	
	public List<Record> userDeviceList(Map<String, String[]> param);
	
	public List<Record> userDevicePieList(Map<String, String[]> param);
	
	public List<Record> userDeviceColList(Map<String, String[]> param);
	
	public List<Record> userStartUpList(Map<String, String[]> param);
	
	public List<Record> userStartUpLineList(Map<String, String[]> param);
	
	public List<Record> userChannelList(Map<String, String[]> param);
	
	public List<Record> userChannelPieList(Map<String, String[]> param);
	
	public List<Record> userChannelColList(Map<String, String[]> param);

	public List<UserVisit> exportUserAreaListExcel(Map<String, String[]> param);

	public List<UserVisit> exportUserDeviceListExcel(Map<String, String[]> param);

	public List<UserVisit> exportUserVersionListExcel(
			Map<String, String[]> param);

	public List<UserStartUp> exportUserStartUpListExcel(
			Map<String, String[]> param);

	public List<UserChannel> exportUserChannelListExcel(
			Map<String, String[]> param);
	
}
