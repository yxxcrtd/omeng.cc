package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AppKey;
import com.shanjin.manager.Bean.CustomKeyWords;
import com.shanjin.manager.Bean.SearchWord;
import com.shanjin.manager.Bean.ServiceWord;
import com.shanjin.manager.Bean.StopKeyWords;
import com.shanjin.manager.Bean.UserWord;
import com.shanjin.manager.dao.SearchWordsDao;
import com.shanjin.manager.dao.impl.SearchWordsDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.ISearchWordsService;

public class SearchWordsServiceImpl implements ISearchWordsService{
	
	private SearchWordsDao searchWordsDao=new SearchWordsDaoImpl();

	@Override
	public List<Record> searchWordsList(Map<String, String[]> param) {
		return searchWordsDao.searchWordsList(param);
	}

	@Override
	public int saveSearchWords(Map<String, String[]> param,String path) {
		return searchWordsDao.saveSearchWords(param,path);
	}

	@Override
	public boolean deleteSearchWords(String ids) {
		return searchWordsDao.deleteSearchWords(ids);
	}

	@Override
	public long searchWordsListSize(Map<String, String[]> param) {
		return searchWordsDao.searchWordsListSize(param);
	}

	@Override
	public List<Record> appKeywordsList(Map<String, String[]> param) {
		return searchWordsDao.appKeywordsList(param);
	}

	@Override
	public long appKeywordsListSize(Map<String, String[]> param) {
		return searchWordsDao.appKeywordsListSize(param);
	}

	@Override
	public int saveAppKeywords(Map<String, String[]> param) {
		return searchWordsDao.saveAppKeywords(param);
	}

	@Override
	public boolean deleteAppKeywords(String ids) {
		return searchWordsDao.deleteAppKeywords(ids);
	}

	@Override
	public List<SearchWord> exportSearchWords(Map<String, String[]> param) {
		return searchWordsDao.exportSearchWords(param);
	}

	@Override
	public List<Pair> getExportSearchWordsTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "id"));
		titles.add(new Pair("keyword", "关键词"));
		titles.add(new Pair("serviceTypeName", "服务类型"));
		titles.add(new Pair("appType", "app类型"));
		titles.add(new Pair("appName", "app名称"));
		titles.add(new Pair("wordsNum", "字数"));
		titles.add(new Pair("url", "url"));
		return titles;
	}

	@Override
	public List<AppKey> exportAppKeywords(Map<String, String[]> param) {
		return searchWordsDao.exportAppKeywords(param);
	}

	@Override
	public List<Pair> getExportAppKeyTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "id"));
		titles.add(new Pair("keyword", "关键词"));
		titles.add(new Pair("app_name", "app名称"));
		titles.add(new Pair("wordsNum", "字数"));
		return titles;
	}

	@Override
	public List<Record> userWordsList(Map<String, String[]> param) {
		return searchWordsDao.userWordsList(param);
	}

	@Override
	public List<Record> serviceWordsList(Map<String, String[]> param) {
		return searchWordsDao.serviceWordsList(param);
	}

	@Override
	public Boolean AuditUserWord(Map<String, String[]> param) {
		return searchWordsDao.AuditUserWord(param);
	}

	@Override
	public Boolean RefuseUserWord(Map<String, String[]> param) {
		return searchWordsDao.RefuseUserWord(param);
	}

	@Override
	public Boolean AuditServiceWord(Map<String, String[]> param) {
		return searchWordsDao.AuditServiceWord(param);
	}

	@Override
	public Boolean RefuseServiceWord(Map<String, String[]> param) {
		return searchWordsDao.RefuseServiceWord(param);
	}

	@Override
	public Boolean deletetUserWord(Map<String, String[]> param) {
		return searchWordsDao.deletetUserWord(param);
	}

	@Override
	public Boolean deletetServiceWord(Map<String, String[]> param) {
		return searchWordsDao.deletetServiceWord(param);
	}

	@Override
	public List<UserWord> exportUserWord(Map<String, String[]> param) {
		return searchWordsDao.exportUserWord(param);
	}

	@Override
	public List<Pair> getExportUserWordTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("app_key_word", "关键词"));
		titles.add(new Pair("wordsNum", "字数"));
		titles.add(new Pair("join_time", "添加时间"));
		titles.add(new Pair("isAudit", "审核状态"));
		return titles;
	}

	@Override
	public List<ServiceWord> exportServiceWord(Map<String, String[]> param) {
		return searchWordsDao.exportServiceWord(param);
	}

	@Override
	public List<Pair> getExportServiceWordTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("app_key_word", "关键词"));
		titles.add(new Pair("wordsNum", "字数"));
		titles.add(new Pair("join_time", "添加时间"));
		titles.add(new Pair("isAudit", "审核状态"));
		return titles;
	}

	@Override
	public Map<String, Object> getSearchWordsMap() {
		return searchWordsDao.getSearchWordsMap();
	}

	@Override
	public List<StopKeyWords> stopKeywordsList(Map<String, String[]> param) {
		return searchWordsDao.stopKeywordsList(param);
	}

	@Override
	public int saveStopKeywords(Map<String, String[]> param) {
		return searchWordsDao.saveStopKeywords(param);
	}

	@Override
	public boolean deleteStopKeywords(String ids) {
		return searchWordsDao.deleteStopKeywords(ids);
	}

	@Override
	public List<CustomKeyWords> customKeywordsList(Map<String, String[]> param) {
		return searchWordsDao.customKeywordsList(param);
	}

	
	@Override
	public int saveCustomKeywords(Map<String, String[]> param) {
		return searchWordsDao.saveCustomKeywords(param);
	}

	@Override
	public boolean deleteCustomKeywords(String ids) {
		return searchWordsDao.deleteCustomKeywords(ids);
	}

	@Override
	public List<StopKeyWords> exportStopAppKeywords(Map<String, String[]> param) {
		return searchWordsDao.exportStopAppKeywords(param);
	}

	@Override
	public List<Pair> getExportStopAppKeyTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("keyword", "关键词"));
		titles.add(new Pair("wordsNum", "字数"));
		return titles;
	}

	@Override
	public List<CustomKeyWords> exportCustomAppKeywords(
			Map<String, String[]> param) {
		return searchWordsDao.exportCustomAppKeywords(param);
	}

	@Override
	public List<Pair> getExportCustomAppKeyTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("keyword", "关键词"));
		titles.add(new Pair("wordsNum", "字数"));
		return titles;
	}
	
}
