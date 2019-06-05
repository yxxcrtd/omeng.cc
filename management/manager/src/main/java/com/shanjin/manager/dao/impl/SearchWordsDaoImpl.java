package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.AppKey;
import com.shanjin.manager.Bean.CustomKeyWords;
import com.shanjin.manager.Bean.SearchWord;
import com.shanjin.manager.Bean.ServiceWord;
import com.shanjin.manager.Bean.StopKeyWords;
import com.shanjin.manager.Bean.UserWord;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.SearchWordsDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.HttpUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class SearchWordsDaoImpl implements SearchWordsDao{

	/**
	 * 
	 * @param param
	 * @param flag(true:查列表，false:查总数)
	 * @return
	 */
	private String searchWordsListSql(Map<String, String[]> param,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(flag){
			sql.append(" SELECT akw.id,akw.keyword,akw.service_type AS serviceType,akw.service_type_name AS serviceTypeName,akw.app_type AS appType,");
			sql.append(" akw.img_path AS img,round(LENGTH(akw.keyword)/3) as wordsNum,akw.url,akw.is_del AS isDel, ");
			sql.append(" (SELECT t.name FROM catalog t WHERE t.alias=akw.app_type and t.level=0) AS appName ");
			
			sql.append(" FROM app_key_words akw LEFT JOIN service_type st ON akw.service_type=st.id AND st.is_del=0");
			sql.append(" WHERE  akw.is_del=0 ");
		}else{
			sql.append(" SELECT count(1) FROM app_key_words akw LEFT JOIN service_type st  ON akw.service_type=st.id AND st.is_del=0 WHERE akw.is_del=0 ");
		}
		String keyword = "";
		if(StringUtil.isNotNullMap(param,"keyword")){
			keyword = StringUtil.null2Str(param.get("keyword")[0]);
			sql.append(" and akw.keyword like '%").append(keyword).append("%'");
		}
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(param,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(param.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and round(LENGTH(akw.keyword)/3)=").append(wordsNum);
			}
		}
		String appType = "";
		if(StringUtil.isNotNullMap(param,"appType")){
			appType = StringUtil.null2Str(param.get("appType")[0]);
			sql.append(" and akw.app_type like '%").append(appType).append("%'");
		}
		String serviceType = "";
		if(StringUtil.isNotNullMap(param,"serviceType")){
			serviceType = StringUtil.null2Str(param.get("serviceType")[0]);
			sql.append(" and akw.service_type=").append(serviceType);
		}
		
		if(flag){
			String property = "id";
			String direction = SORT.DESC;
			if(StringUtil.isNotNullMap(param,"sort")){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")[0]));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			int start = 0;
			if(StringUtil.isNotNullMap(param,"start")){
				start = StringUtil.nullToInteger(param.get("start")[0]);
			}
			int pageSize = PAGE.PAGESIZE;
			if(StringUtil.isNotNullMap(param,"limit")){
				pageSize = StringUtil.nullToInteger(param.get("limit")[0]);
			}
			sql.append(" limit ");
			sql.append(start);
			sql.append(",");
			sql.append(pageSize);
		}
		return sql.toString();
	}
	
	
	@Override
	public List<Record> searchWordsList(Map<String, String[]> param) {
		List<Record> list = new ArrayList<Record>();
		list = Db.find(searchWordsListSql(param,true));
		if(list.size()>0){
			String pic = "";
			String appType = "";
			String appName = "";
			for(Record s : list){
				pic = s.getStr("img");
				appType = s.getStr("appType");
				appName = s.getStr("appName");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("img", pic);
				}
				if(StringUtil.isNullStr(appName)){
					s.set("appName", Constant.thirdAppInfoMap.get(appType));
				}
			}
		}
		return list;
	}

	@Override
	public int saveSearchWords(Map<String, String[]> param,String path) {
		int flag = 0;// 0失败 1成功 2 关键词已存在
		boolean isUpdate = false; // 标识是否是更新
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String keyword = "";
		if(StringUtil.isNotNullMap(param,"keyword")){
			keyword = StringUtil.null2Str(param.get("keyword")[0]);
		}
		String appType="";
		if(StringUtil.isNotNullMap(param,"appType")){
			appType = StringUtil.null2Str(param.get("appType")[0]);
		}
		Long serviceType=0L;;
		if(StringUtil.isNotNullMap(param,"serviceType")){
			serviceType = StringUtil.nullToLong(param.get("serviceType")[0]);
		}
		String serviceTypeName="";
		if(serviceType.intValue()!=0){
			serviceTypeName = Db.findById("service_type", serviceType).getStr("service_type_name");
		}
		
		if(checkSearchKeyword(keyword,isUpdate,id,appType,serviceType)){
			flag = 2;
			return flag;
		}
		String url="";
		if(StringUtil.isNotNullMap(param,"url")){
			url = StringUtil.null2Str(param.get("url")[0]);
		}
		if(StringUtil.isNullStr(path)){
			// 未上传图片
			if(StringUtil.isNotNullMap(param, "img")){
				path = param.get("img")[0];
				path = BusinessUtil.getFilePath(path);
			}
		}

		if(isUpdate){
			Record record = Db.findById("app_key_words", id).set("keyword", keyword).set("service_type", serviceType)
					.set("service_type_name", serviceTypeName).set("app_type", appType).set("url", url).set("img_path", path);
			Db.update("app_key_words", record);
			final Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("indexName", "appkeyword");
			paramMap.put("ids", id);
			//String res = HttpUtil.SendGET(Constant.WEB_SERACH_URL+"updateDocument","indexName=appkeyword&ids="+id);
			new Thread(new Runnable() {			
				@Override
				public void run() {
					long s = new Date().getTime();
					String res = HttpUtil.httpClientPost(Constant.WEB_SERACH_URL+"updateDocument", paramMap);
					long e = new Date().getTime();
					System.out.println("创建搜索索引耗时："+(e-s)+"ms");
				}
			}).start();
		}else{
			Record record = new Record();
			record.set("keyword", keyword).set("service_type", serviceType).set("app_type", appType)
			.set("service_type_name", serviceTypeName).set("url", url).set("img_path", path);
			Db.save("app_key_words", record);
			final Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("ids", record.get("id"));
			paramMap.put("indexName", "appkeyword");
			//String res = HttpUtil.SendGET(Constant.WEB_SERACH_URL+"addDocument","indexName=appkeyword&ids="+record.get("id"));
			new Thread(new Runnable() {			
				@Override
				public void run() {
					long s = new Date().getTime();
					String res = HttpUtil.httpClientPost(Constant.WEB_SERACH_URL+"addDocument", paramMap);
					long e = new Date().getTime();
					System.out.println("创建搜索索引耗时："+(e-s)+"ms");
				}
			}).start();
		}
		flag = 1;
		return flag;
	}
	
	/**
	 * 检查搜索词是否存在
	 * @param keyword
	 * @param isUpdate
	 * @param id
	 * @param serviceType
	 * @return
	 */
	private boolean checkSearchKeyword(String keyword,boolean isUpdate,Long id,String appType,Long serviceType){
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		int num = 0;
		sql.append(" SELECT COUNT(1) FROM app_key_words t ");
		sql.append(" WHERE t.is_del=0 and t.app_type='").append(appType).append("'");
		sql.append(" AND t.keyword='").append(keyword).append("'");
		sql.append(" AND t.service_type=").append(serviceType);
		if(isUpdate){
			sql.append(" AND t.id<>").append(id);
		}
		num = Db.queryLong(sql.toString()).intValue();
		if(num>0) 
			flag = true;
		return flag;
	}

	@Override
	public boolean deleteSearchWords(String ids) {
		boolean flag = false;
		//String sql = "UPDATE app_key_words SET is_del=1 WHERE id in("+ids+")";
		String sql = "DELETE FROM app_key_words WHERE id in("+ids+")";
		Db.update(sql);
		final Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("ids", ids);
		paramMap.put("indexName", "appkeyword");
		//String res = HttpUtil.SendGET(Constant.WEB_SERACH_URL+"addDocument","indexName=appkeyword&ids="+record.get("id"));
		//String res = HttpUtil.httpClientPost(Constant.WEB_SERACH_URL+"delDocument", paramMap);
		//String res = HttpUtil.SendGET(Constant.WEB_SERACH_URL+"delDocument","indexName=appkeyword&ids="+ids);
		new Thread(new Runnable() {			
			@Override
			public void run() {
				String res = HttpUtil.httpClientPost(Constant.WEB_SERACH_URL+"delDocument", paramMap);
				System.out.println("当前时间："+new Date()+"resp="+res);
			}
		}).start();
		flag = true;
		return flag;
	}

	@Override
	public long searchWordsListSize(Map<String, String[]> param) {
		return Db.queryLong(searchWordsListSql(param,false));
	}
	
	/**
	 * 
	 * @param param
	 * @param flag(true:查列表，false:查总数)
	 * @return
	 */
	private String appKeyWordListSql(Map<String, String[]> param,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(flag){
			sql.append(" SELECT t.*,round(LENGTH(t.keyword)/3) as wordsNum from app_name_key_words t");
		}else{
			sql.append(" SELECT count(1) FROM app_name_key_words t");
		}
		sql.append(" where t.is_del=0");
		String keyword = "";
		if(StringUtil.isNotNullMap(param,"keyword")){
			keyword = StringUtil.null2Str(param.get("keyword")[0]);
			sql.append(" and t.keyword like '%").append(keyword).append("%'");
		}
		
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(param,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(param.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and LENGTH(t.keyword)=").append(wordsNum*3);
			}
		}
		String app_type = "";
		if(StringUtil.isNotNullMap(param,"app_type")){
			app_type = StringUtil.null2Str(param.get("app_type")[0]);
			sql.append(" and t.app_type ='").append(app_type).append("'");
		}
		
		if(flag){
			String property = "id";
			String direction = SORT.DESC;
			if(StringUtil.isNotNullMap(param,"sort")){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")[0]));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			int start = 0;
			if(StringUtil.isNotNullMap(param,"start")){
				start = StringUtil.nullToInteger(param.get("start")[0]);
			}
			int pageSize = PAGE.PAGESIZE;
			if(StringUtil.isNotNullMap(param,"limit")){
				pageSize = StringUtil.nullToInteger(param.get("limit")[0]);
			}
			sql.append(" limit ");
			sql.append(start);
			sql.append(",");
			sql.append(pageSize);
		}
		return sql.toString();
	}

	@Override
	public List<Record> appKeywordsList(Map<String, String[]> param) {
		return Db.find(appKeyWordListSql(param,true));
	}


	@Override
	public long appKeywordsListSize(Map<String, String[]> param) {
		return Db.queryLong(appKeyWordListSql(param,false));
	}


	@Override
	public int saveAppKeywords(Map<String, String[]> param) {
		int flag = 0;// 0失败 1成功 2 关键词已存在
		boolean isUpdate = false; // 标识是否是更新
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String keyword = "";
		if(StringUtil.isNotNullMap(param,"keyword")){
			keyword = StringUtil.null2Str(param.get("keyword")[0]);
		}
		String app_type="";
		if(StringUtil.isNotNullMap(param,"app_type")){
			app_type = StringUtil.null2Str(param.get("app_type")[0]);
		}
		if(checkKeyword(keyword,isUpdate,id)){
			flag = 2;
			return flag;
		}
		String app_name = getNameByType(app_type);
		if(isUpdate){
			Record record = Db.findById("app_name_key_words", id).set("keyword", keyword).set("app_type", app_type).set("app_name", app_name);
			Db.update("app_name_key_words", record);
		}else{
			Record record = new Record();
			record.set("keyword", keyword).set("app_type", app_type).set("app_name", app_name);
			Db.save("app_name_key_words", record);
		}
		flag = 1;
		return flag;
	}
	
	private String getNameByType(String appType){
		String name = "";
		List<AppInfo> list = AppInfo.dao.find("SELECT t.app_name FROM merchant_app_info t WHERE t.is_del=0 AND t.app_type=?",appType);
		if(list!=null&&list.size()>0){
			name = list.get(0).getStr("app_name");
		}
		return name;
	}
	
	/**
	 * 检查关键词是否存在
	 * @param keyword
	 * @param isUpdate
	 * @param id
	 * @return
	 */
	private boolean checkKeyword(String keyword,boolean isUpdate,Long id){
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		int num = 0;
		sql.append(" SELECT COUNT(1) FROM app_name_key_words t ");
		sql.append(" WHERE t.is_del=0 ");
		sql.append(" AND t.keyword='").append(keyword).append("'");
		if(isUpdate){
			sql.append(" AND t.id<>").append(id);
		}
		num = Db.queryLong(sql.toString()).intValue();
		if(num>0) 
			flag = true;
		return flag;
	}


	@Override
	public boolean deleteAppKeywords(String ids) {
		boolean flag = false;
		//String sql = "UPDATE app_name_key_words SET is_del=1 WHERE id in("+ids+")";
		String sql = "DELETE FROM app_name_key_words WHERE id in("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}


	@Override
	public List<SearchWord> exportSearchWords(Map<String, String[]> param) {
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT akw.id,akw.keyword,akw.service_type AS serviceType,akw.service_type_name AS serviceTypeName,akw.app_type AS appType,");
		sql.append(" akw.img_path AS img,round(LENGTH(akw.keyword)/3) as wordsNum,akw.url,akw.is_del AS isDel, ");
		sql.append(" (SELECT t.app_name FROM merchant_app_info t WHERE t.app_type=akw.app_type) AS appName ");
		sql.append(" FROM app_key_words akw ");
		sql.append(" WHERE akw.is_del=0 ");
		String keyword = "";
		if(StringUtil.isNotNullMap(param,"keyword")){
			keyword = StringUtil.null2Str(param.get("keyword")[0]);
			sql.append(" and akw.keyword like '%").append(keyword).append("%'");
		}
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(param,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(param.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and round(LENGTH(akw.keyword)/3)=").append(wordsNum);
			}
		}
		String appType = "";
		if(StringUtil.isNotNullMap(param,"appType")){
			appType = StringUtil.null2Str(param.get("appType")[0]);
			sql.append(" and akw.app_type like '%").append(appType).append("%'");
		}
		String serviceType = "";
		if(StringUtil.isNotNullMap(param,"serviceType")){
			serviceType = StringUtil.null2Str(param.get("serviceType")[0]);
			sql.append(" and akw.service_type=").append(serviceType);
		}
		
			sql.append(" limit ");
			sql.append(pageNumber);
			sql.append(",");
			sql.append(pageSize);
 			List<SearchWord> searchWord=SearchWord.dao.find(sql.toString());
		
		    return searchWord;
	}


	@Override
	public List<AppKey> exportAppKeywords(Map<String, String[]> param) {
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT t.*,round(LENGTH(t.keyword)/3) as wordsNum from app_name_key_words t");
		sql.append(" where t.is_del=0");
		String keyword = "";
		if(StringUtil.isNotNullMap(param,"keyword")){
			keyword = StringUtil.null2Str(param.get("keyword")[0]);
			sql.append(" and t.keyword like '%").append(keyword).append("%'");
		}
		
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(param,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(param.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and LENGTH(t.keyword)=").append(wordsNum*3);
			}
		}
		String app_type = "";
		if(StringUtil.isNotNullMap(param,"app_type")){
			app_type = StringUtil.null2Str(param.get("app_type")[0]);
			sql.append(" and t.app_type ='").append(app_type).append("'");
		}

			String property = "id";
			String direction = SORT.DESC;
			if(StringUtil.isNotNullMap(param,"sort")){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")[0]));
				if(sortMap!=null){
					if(!StringUtil.isNullStr(sortMap.get("property"))){
						property = StringUtil.null2Str(sortMap.get("property"));
					}
					if(!StringUtil.isNullStr(sortMap.get("direction"))){
						direction = StringUtil.null2Str(sortMap.get("direction"));
					}
				}
			}
			sql.append(" ORDER BY ").append(property).append(" ").append(direction);
			sql.append(" limit ");
			sql.append(pageNumber);
			sql.append(",");
			sql.append(pageSize);
		    List<AppKey> searchhWords=AppKey.dao.find(sql.toString());
				
			return searchhWords;
	}


	@Override
	public List<Record> userWordsList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select u.app_key_word,u.join_time,u.is_audit,u.remark from user_supply_app_key_word u where u.is_del=0 ");
		totalSql.append("select count(1) as total from user_supply_app_key_word u where u.is_del=0 ");
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.app_key_word", paramMap.get("app_key_word"));
		sql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		totalSql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(paramMap,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(paramMap.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and LENGTH(u.app_key_word)=").append(wordsNum*3);
			}
		}
		String property = "u.join_time";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
			if(sortMap!=null){
				if(!StringUtil.isNullStr(sortMap.get("property"))){
					property = StringUtil.null2Str(sortMap.get("property"));
				}
				if(!StringUtil.isNullStr(sortMap.get("direction"))){
					direction = StringUtil.null2Str(sortMap.get("direction"));
				}
			}
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		long total=Db.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<Record> list =Db.find(sql.toString());
	    if(list.size()>0){
	    	list.get(0).set("total", total);
	    	
	    }
		return list;
	}


	@Override
	public List<Record> serviceWordsList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select u.app_key_word,u.join_time,u.is_audit,u.remark from user_supply_app_name_key_word u where u.is_del=0 ");
		totalSql.append("select count(1) as total from user_supply_app_name_key_word u where u.is_del=0 ");
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.app_key_word", paramMap.get("app_key_word"));
		sql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		totalSql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(paramMap,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(paramMap.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and LENGTH(u.app_key_word)=").append(wordsNum*3);
			}
		}
		String property = "u.join_time";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
			if(sortMap!=null){
				if(!StringUtil.isNullStr(sortMap.get("property"))){
					property = StringUtil.null2Str(sortMap.get("property"));
				}
				if(!StringUtil.isNullStr(sortMap.get("direction"))){
					direction = StringUtil.null2Str(sortMap.get("direction"));
				}
			}
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		long total=Db.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<Record> list =Db.find(sql.toString());
	    if(list.size()>0){
	    	list.get(0).set("total", total);
	    	
	    }
		return list;
	}


	@Override
	public Boolean AuditUserWord(Map<String, String[]> param) {
		boolean flag = false;
		String app_key_word = StringUtil.null2Str(param.get("app_key_word")[0]);
		String sql="update user_supply_app_key_word set is_audit=1 where app_key_word='"+app_key_word+"'";
		Db.update(sql);
		flag=true;
		return flag;
	}


	@Override
	public Boolean RefuseUserWord(Map<String, String[]> param) {
		boolean flag = false;
		String app_key_word = StringUtil.null2Str(param.get("app_key_word")[0]);
		String remark = StringUtil.null2Str(param.get("remark")[0]);
		String sql="update user_supply_app_key_word set is_audit=2,remark=? where app_key_word='"+app_key_word+"'";
		Db.update(sql,remark);
		flag=true;
		return flag;
	}


	@Override
	public Boolean AuditServiceWord(Map<String, String[]> param) {
		boolean flag = false;
		String app_key_word = StringUtil.null2Str(param.get("app_key_word")[0]);
		String sql="update user_supply_app_name_key_word set is_audit=1 where app_key_word='"+app_key_word+"'";
		Db.update(sql);
		flag=true;
		return flag;
	}


	@Override
	public Boolean RefuseServiceWord(Map<String, String[]> param) {
		boolean flag = false;
		String app_key_word = StringUtil.null2Str(param.get("app_key_word")[0]);
		String remark = StringUtil.null2Str(param.get("remark")[0]);
		String sql="update user_supply_app_name_key_word set is_audit=2,remark=? where app_key_word='"+app_key_word+"'";
		Db.update(sql,remark);
		flag=true;
		return flag;
	}


	@Override
	public Boolean deletetUserWord(Map<String, String[]> param) {
		boolean flag = false;
		String[] app_key_words = param.get("app_key_word")[0].split(",");
		String sql="update user_supply_app_key_word set is_del=1 where app_key_word=?";
		for(int i=0;i<app_key_words.length;i++){	
		Db.update(sql,app_key_words[i]);	
		}
		flag=true;
		return flag;
	}
	@Override
	public Boolean deletetServiceWord(Map<String, String[]> param) {
		boolean flag = false;
		String[] app_key_words = param.get("app_key_word")[0].split(",");
		String sql="update user_supply_app_name_key_word set is_del=1 where app_key_word=?";
		for(int i=0;i<app_key_words.length;i++){	
		Db.update(sql,app_key_words[i]);	
		}
		flag=true;
		return flag;
	}


	@Override
	public List<UserWord> exportUserWord(Map<String, String[]> paramMap) {
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		StringBuffer sql = new StringBuffer();
		sql.append("select u.app_key_word,u.join_time,(case u.is_audit when 0 then '未审核' when 1 then '已通过' when 2 then '未通过' else '' end) as isAudit,u.remark,round(LENGTH(u.app_key_word)/3) as wordsNum from user_supply_app_key_word u where u.is_del=0 ");
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.app_key_word", paramMap.get("app_key_word"));
		sql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getExportdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(paramMap,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(paramMap.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and LENGTH(u.app_key_word)=").append(wordsNum*3);
			}
		}
		String property = "u.join_time";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
			if(sortMap!=null){
				if(!StringUtil.isNullStr(sortMap.get("property"))){
					property = StringUtil.null2Str(sortMap.get("property"));
				}
				if(!StringUtil.isNullStr(sortMap.get("direction"))){
					direction = StringUtil.null2Str(sortMap.get("direction"));
				}
			}
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<UserWord> list =UserWord.dao.find(sql.toString());
		return list;
	}


	@Override
	public List<ServiceWord> exportServiceWord(Map<String, String[]> paramMap) {
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		StringBuffer sql = new StringBuffer();
		sql.append("select u.app_key_word,u.join_time,(case u.is_audit when 0 then '未审核' when 1 then '已通过' when 2 then '未通过' else '' end) as isAudit,u.remark,round(LENGTH(u.app_key_word)/3) as wordsNum from user_supply_app_name_key_word u where u.is_del=0 ");
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.app_key_word", paramMap.get("app_key_word"));
		sql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getExportdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(paramMap,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(paramMap.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and LENGTH(u.app_key_word)=").append(wordsNum*3);
			}
		}
		String property = "u.join_time";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
			if(sortMap!=null){
				if(!StringUtil.isNullStr(sortMap.get("property"))){
					property = StringUtil.null2Str(sortMap.get("property"));
				}
				if(!StringUtil.isNullStr(sortMap.get("direction"))){
					direction = StringUtil.null2Str(sortMap.get("direction"));
				}
			}
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<ServiceWord> list =ServiceWord.dao.find(sql.toString());
		return list;
	}


	@Override
	public Map<String, Object> getSearchWordsMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "SELECT t.* FROM app_key_words t WHERE t.is_del=0";
		List<Record> list = Db.find(sql);
		if(list!=null&&list.size()>0){
			for(Record r:list){
				String key = r.getStr("keyword")+"_"+r.getStr("service_type");
				map.put(key, "1");
			}
		}
		return map;
	}


	@Override
	public List<StopKeyWords> stopKeywordsList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		
		sql.append("select u.id,u.keyword,round(LENGTH(u.keyword)/3) as wordsNum from app_key_words_stop_dic u where 0=0 ");
		totalSql.append("select count(1) as total from app_key_words_stop_dic u where 0=0 ");
		
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.keyword", paramMap.get("keyword"));
		sql.append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getLikeFilter(strLikeFilter));
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(paramMap,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(paramMap.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and round(LENGTH(u.keyword)/3)=").append(wordsNum);
				totalSql.append(" and round(LENGTH(u.keyword)/3)=").append(wordsNum);
			}
		}
		long total=Db.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<StopKeyWords> list =StopKeyWords.dao.find(sql.toString());
	    if (list.size() > 0) {
	    	list.get(0).setTotal(total);
		}
		return list;
	}

	@Override
	public int saveStopKeywords(Map<String, String[]> param) {
		int flag = 0;// 0失败 1成功 2 关键词已存在
		String keyword = "";
		if (StringUtil.isNotNullMap(param, "keyword")) {
			keyword = StringUtil.null2Str(param.get("keyword")[0]);
		}
        String sql="select count(1) as total from app_key_words_stop_dic aw where aw.keyword=?";
        long total =Db.find(sql,keyword).get(0).getLong("total");
        if(total>0){
        	flag = 2;
        	return flag;
        }
		Record record = new Record();
		record.set("keyword", keyword);
		Db.save("app_key_words_stop_dic", record);

		flag = 1;
		return flag;
	}


	@Override
	public boolean deleteStopKeywords(String ids) {
		boolean flag = false;
		String[] id = ids.split(",");
		StopKeyWords stopKeyWords;
		for (int i = 0; i < id.length; i++) {
			stopKeyWords=new StopKeyWords();
			stopKeyWords.deleteById(id[i]);
		}
		flag = true;
		return flag;
	}


	@Override
	public List<CustomKeyWords> customKeywordsList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		
		sql.append("select u.id,u.keyword,round(LENGTH(u.keyword)/3) as wordsNum from app_key_words_custom_dic u where 0=0 ");
		totalSql.append("select count(1) as total from app_key_words_custom_dic u where 0=0 ");
		
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.keyword", paramMap.get("keyword"));
		sql.append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getLikeFilter(strLikeFilter));
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(paramMap,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(paramMap.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and round(LENGTH(u.keyword)/3)=").append(wordsNum);
				totalSql.append(" and round(LENGTH(u.keyword)/3)=").append(wordsNum);
			}
		}
		long total=Db.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<CustomKeyWords> list =CustomKeyWords.dao.find(sql.toString());
	    if (list.size() > 0) {
	    	list.get(0).setTotal(total);
		}
		return list;
	}

	@Override
	public int saveCustomKeywords(Map<String, String[]> param) {
		int flag = 0;// 0失败 1成功 2 关键词已存在
		String keyword = "";
		if (StringUtil.isNotNullMap(param, "keyword")) {
			keyword = StringUtil.null2Str(param.get("keyword")[0]);
		}
        String sql="select count(1) as total from app_key_words_custom_dic aw where aw.keyword=?";
        long total =Db.find(sql,keyword).get(0).getLong("total");
        if(total>0){
        	flag = 2;
        	return flag;
        }
		Record record = new Record();
		record.set("keyword", keyword);
		Db.save("app_key_words_custom_dic", record);

		flag = 1;
		return flag;
	}


	@Override
	public boolean deleteCustomKeywords(String ids) {
		boolean flag = false;
		String[] id = ids.split(",");
		CustomKeyWords customKeyWords;
		for (int i = 0; i < id.length; i++) {
			customKeyWords=new CustomKeyWords();
			customKeyWords.deleteById(id[i]);
		}
		flag = true;
		return flag;
	}


	@Override
	public List<StopKeyWords> exportStopAppKeywords(Map<String, String[]> paramMap) {
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		StringBuffer sql = new StringBuffer();
		
		sql.append("select u.id,u.keyword,round(LENGTH(u.keyword)/3) as wordsNum from app_key_words_stop_dic u where 0=0 ");
		
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.keyword", paramMap.get("keyword"));
		sql.append(Util.getLikeFilter(strLikeFilter));
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(paramMap,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(paramMap.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and round(LENGTH(u.keyword)/3)=").append(wordsNum);
			}
		}
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<StopKeyWords> list =StopKeyWords.dao.find(sql.toString());
	    
		return list;
	}


	@Override
	public List<CustomKeyWords> exportCustomAppKeywords(
			Map<String, String[]> paramMap) {
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		StringBuffer sql = new StringBuffer();
		
		sql.append("select u.id,u.keyword,round(LENGTH(u.keyword)/3) as wordsNum from app_key_words_custom_dic u where 0=0 ");
		
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.keyword", paramMap.get("keyword"));
		sql.append(Util.getLikeFilter(strLikeFilter));
		int wordsNum = 0; // 搜索字数
		if(StringUtil.isNotNullMap(paramMap,"wordsNum")){
			wordsNum = StringUtil.nullToInteger(paramMap.get("wordsNum")[0]);
			if(wordsNum>0){
				sql.append(" and round(LENGTH(u.keyword)/3)=").append(wordsNum);
			}
		}
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<CustomKeyWords> list =CustomKeyWords.dao.find(sql.toString());
	    
		return list;
	}

}
