package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

public interface IMerchantTerminalService {
	
	public List<Record> merchantAreaList(Map<String, String[]> param);
	
	public List<Record> merchantAreaPieList(Map<String, String[]> param);
	
	public List<Record> merchantAreaColList(Map<String, String[]> param);
	
	public List<Record> merchantVersionList(Map<String, String[]> param);
	
	public List<Record> merchantVersionPieList(Map<String, String[]> param);
	
	public List<Record> merchantVersionColList(Map<String, String[]> param);
	
	public List<Record> merchantDeviceList(Map<String, String[]> param);
	
	public List<Record> merchantDevicePieList(Map<String, String[]> param);
	
	public List<Record> merchantDeviceColList(Map<String, String[]> param);
	
	public List<Record> merchantStartUpList(Map<String, String[]> param);
	
	public List<Record> merchantStartUpLineList(Map<String, String[]> param);
	
	public List<Record> merchantChannelList(Map<String, String[]> param);
	
	public List<Record> merchantChannelPieList(Map<String, String[]> param);
	
	public List<Record> merchantChannelColList(Map<String, String[]> param);
	
}
