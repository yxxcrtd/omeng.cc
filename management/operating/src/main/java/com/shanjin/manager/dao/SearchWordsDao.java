package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AppKey;
import com.shanjin.manager.Bean.CustomKeyWords;
import com.shanjin.manager.Bean.SearchWord;
import com.shanjin.manager.Bean.ServiceWord;
import com.shanjin.manager.Bean.StopKeyWords;
import com.shanjin.manager.Bean.UserWord;

public interface SearchWordsDao {

	// 搜索关键词
	List<Record> searchWordsList(Map<String, String[]> param);
	
	public long searchWordsListSize(Map<String, String[]> param);

    int saveSearchWords(Map<String, String[]> param,String path);

	boolean deleteSearchWords(String ids);
	
	public Map<String,Object> getSearchWordsMap();
	
	//应用关键词
	
	List<Record> appKeywordsList(Map<String, String[]> param);
	
	public long appKeywordsListSize(Map<String, String[]> param);

	int saveAppKeywords(Map<String, String[]> param);

	boolean deleteAppKeywords(String ids);
	
	//隐藏关键词
	
	List<StopKeyWords> stopKeywordsList(Map<String, String[]> param);

	int saveStopKeywords(Map<String, String[]> param);

	boolean deleteStopKeywords(String ids);
	
	//不拆分关键词
	
	List<CustomKeyWords> customKeywordsList(Map<String, String[]> param);
	

	int saveCustomKeywords(Map<String, String[]> param);

	boolean deleteCustomKeywords(String ids);

	List<SearchWord> exportSearchWords(Map<String, String[]> param);

	List<AppKey> exportAppKeywords(Map<String, String[]> param);

	List<Record> userWordsList(Map<String, String[]> param);

	List<Record> serviceWordsList(Map<String, String[]> param);

	Boolean AuditUserWord(Map<String, String[]> param);

	Boolean RefuseUserWord(Map<String, String[]> param);

	Boolean AuditServiceWord(Map<String, String[]> param);

	Boolean RefuseServiceWord(Map<String, String[]> param);

	Boolean deletetUserWord(Map<String, String[]> param);

	Boolean deletetServiceWord(Map<String, String[]> param);

	List<UserWord> exportUserWord(Map<String, String[]> param);

	List<ServiceWord> exportServiceWord(Map<String, String[]> param);

	List<StopKeyWords> exportStopAppKeywords(Map<String, String[]> param);

	List<CustomKeyWords> exportCustomAppKeywords(Map<String, String[]> param);
	
}
