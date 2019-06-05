package com.shanjin.manager.dao.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.Catalog;
import com.shanjin.manager.Bean.Recommend;
import com.shanjin.manager.Bean.SearchStatistic;
import com.shanjin.manager.Bean.SearchStatisticAttch;
import com.shanjin.manager.Bean.Slider;
import com.shanjin.manager.Bean.Voucher;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.SliderDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.ImageMarkUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class SliderDaoImpl implements SliderDao {
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	public List<Slider> getSliders(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select si.* from manager_slider_info si where si.is_del=0  and ");
		totalSql.append("select count(1) as total from manager_slider_info si where is_del=0 and ");

	    // 时间状态  0：全部   1：使用中 2 ：未开始 3：已过期
	    int timeStatus = StringUtil.nullToInteger(paramMap.get("time_status")[0]);	    		  
		if(timeStatus==1){
			sql.append(" NOW() BETWEEN si.join_time AND si.overdue_time and ");
			totalSql.append(" NOW() BETWEEN si.join_time AND si.overdue_time and ");
		}else if (timeStatus==2){
			sql.append(" si.join_time> NOW() and ");
			totalSql.append(" si.join_time> NOW() and ");
		}else if(timeStatus==3){
			sql.append(" si.overdue_time< NOW() and ");
			totalSql.append(" si.overdue_time< NOW() and ");
		}else{
			// 查询所有
		}
		
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("si.name", paramMap.get("name"));
		strFilter.put("si.slider_status", paramMap.get("status_type"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
        long total= Slider.dao.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "si.join_time";
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
		
		List<Slider> sliders=Slider.dao.find(sql.toString());
		if(sliders.size()>0){
			sliders.get(0).setTotal(total);
			String pic = "";
			for(Slider s : sliders){
				pic = s.getStr("pics_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("pics_path", pic);
				}
			}
		}
		return sliders;
	}

	public Boolean deleteSlider(String id) {
		String[] sliderIds=id.split(",");
		boolean flag=false;
		for(int i=0;i<sliderIds.length;i++){
			Slider.dao.findById(sliderIds[i]).set("is_del", 1).update();
		}
		flag=true;
		commonCacheService.deleteObject(CacheConstants.MERCHANT_HOME_PAGE_BANNER);
		commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_BANNER);
	   return flag;
	}
 
	public List<Slider> getSliders() {
		StringBuffer sql = new StringBuffer();
		sql.append("select si.id,si.name,si.slider_type,si.join_time,si.overdue_time,si.slider_status,sp.path from manager_slider_info si inner join slider_pics sp on si.pics_id=sp.id");
		List<Slider> sliders=Slider.dao.find(sql.toString());
		return sliders;
	}

	public Boolean saveSlider(Map<String, String[]> param, String path) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String name=param.get("name")[0];
		String tag=param.get("tag")[0];
		String status=param.get("slider_status")[0].equals("")==true ? "0":param.get("slider_status")[0];
		String sliderType=param.get("slider_type")[0].equals("")==true ? "0":param.get("slider_type")[0];
		String client=param.get("client")[0].equals("")==true ? "0":param.get("client")[0];
		String linkUrl=param.get("link_url")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0":param.get("rank")[0];
		String join_time=param.get("join_time")[0].equals("")==true ? DateUtil.getDate():param.get("join_time")[0];
		String overdue_time=param.get("overdue_time")[0];
		if(StringUtil.isNullStr(path)){
			// 未上传图片
			if(StringUtil.isNotNullMap(param, "pics_path")){
				path = param.get("pics_path")[0];
				path = BusinessUtil.getFilePath(path);
			}
		}
		boolean flag = false;
		if(isUpdate){
			 // 更新
			 Slider.dao.findById(id).set("name", name).set("slider_type", sliderType)
			.set("slider_status", status).set("pics_path", path).set("tag", tag)
			.set("link_url", linkUrl).set("rank", rank).set("client",client)
			.set("join_time", join_time).set("overdue_time", overdue_time).update();
		}else{
			// 新增
			Slider slider=new Slider();
			slider.set("name", name).set("slider_type", sliderType)
					.set("slider_status", status).set("pics_path", path).set("client",client)
					.set("link_url", linkUrl).set("rank", rank).set("tag", tag)
					.set("join_time", join_time).set("overdue_time", overdue_time)
					.save();	
		}
		flag = true;
		commonCacheService.deleteObject(CacheConstants.MERCHANT_HOME_PAGE_BANNER);
		commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_BANNER);
		return flag;
	}

	@Override
	public List<Recommend> getRecommends(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select mr.*,(select count(1) from manager_hot_recommend_detail mrd where mrd.rec_id=mr.id) as recs from manager_hot_recommend mr where mr.is_del=0 and ");
		totalSql.append("select count(1) as total from manager_hot_recommend mr where mr.is_del=0 and ");

	    // 时间状态  0：全部   1：使用中 2 ：未开始 3：已过期
	    int timeStatus = StringUtil.nullToInteger(paramMap.get("time_status")[0]);	    		  
		if(timeStatus==1){
			sql.append(" NOW() BETWEEN mr.join_time AND mr.overdue_time and ");
			totalSql.append(" NOW() BETWEEN mr.join_time AND mr.overdue_time and ");
		}else if (timeStatus==2){
			sql.append(" mr.join_time> NOW() and ");
			totalSql.append(" mr.join_time> NOW() and ");
		}else if(timeStatus==3){
			sql.append(" mr.overdue_time< NOW() and ");
			totalSql.append(" mr.overdue_time< NOW() and ");
		}else{
			// 查询所有
		}
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("mr.title", paramMap.get("title"));
		strFilter.put("mr.province", paramMap.get("province"));
		strFilter.put("mr.city", paramMap.get("city"));
		strFilter.put("mr.status", paramMap.get("status"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
        long total= Recommend.dao.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "mr.join_time";
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
		
		List<Recommend> recommends=Recommend.dao.find(sql.toString());
		if(recommends.size()>0){
			recommends.get(0).setTotal(total);
			String pic = "";
			for(Recommend s : recommends){
				pic = s.getStr("pics_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("pics_path", pic);
				}
			}
		}
		return recommends;
	}

	@Override
	public Boolean saveRecommend(Map<String, String[]> param) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String province=param.get("province")[0];
		String city=param.get("city")[0];
		String join_time=param.get("join_time")[0].equals("")==true ? DateUtil.getDate():param.get("join_time")[0];
		String overdue_time=param.get("overdue_time")[0];
		
		boolean flag = false;
		if(isUpdate){
			 // 更新
			 Recommend.dao.findById(id)
			.set("province", province).set("city",city)
			.set("join_time", join_time).set("overdue_time", overdue_time).update();
		}else{
			// 新增
			Recommend recommend=new Recommend();
			recommend.set("province", province).set("city",city)
			.set("join_time", join_time).set("overdue_time", overdue_time)
			.set("status", 2).save();	
		}
		flag = true;
		if(province.equals("全国")){
			   flushRecCatach(CacheConstants.USER_HOME_PAGE_RECOMMEND);
		    }else if(city!=null&&!city.equals("")){
			   commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,city); 
		    }else{
			   flushRecCatach(CacheConstants.USER_HOME_PAGE_RECOMMEND,province); 
		    }
		commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"original");
		commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"originalPerson");
		return flag;
	}

	public Boolean deleteRecommend(String id) {
		String[] recommendIds=id.split(",");
		boolean flag=false;
		Recommend re=null;
		for(int i=0;i<recommendIds.length;i++){
			re=Recommend.dao.findById(recommendIds[i]);
			re.set("is_del", 1).update();
		    if(re.getStr("province").equals("全国")){
			   flushRecCatach(CacheConstants.USER_HOME_PAGE_RECOMMEND);
		    }else if(re.getStr("city")!=null&&!re.getStr("city").equals("")){
			   commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,re.getStr("province"),re.getStr("city")); 
		    }else{
			   flushRecCatach(CacheConstants.USER_HOME_PAGE_RECOMMEND,re.getStr("province")); 
		    }
		    commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,re.getStr("province"),"original");
			commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,re.getStr("province"),"originalPerson");	
		
			
				}
	   flag=true;
	   return flag;
	}
	private void flushRecCatach(String key, String... attachedKey) {
		final String fkey;
		String okey = key;
		if(attachedKey!=null){
			for(String ak : attachedKey){
				if(!StringUtil.isNullStr(ak)){
					okey = okey + CacheConstants.JOIN + ak;
				}
			}
		}
		fkey=okey;
		new Thread(new Runnable() {			
			@Override
			public void run() {	
				try{
					commonCacheService.delObjectContainsKey(fkey);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	@Override
	public List<SearchStatistic> getSearchStatistics(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select si.* from search_statistic si where ");
		totalSql.append("select count(1) as total from search_statistic si where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("si.service_name", paramMap.get("service_name"));
		intFilter.put("si.is_fixed", paramMap.get("is_fixed"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
        long total= SearchStatistic.dao.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "si.search_count";
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
		
		List<SearchStatistic> searchStatistics=SearchStatistic.dao.find(sql.toString());
		if(searchStatistics.size()>0){
			searchStatistics.get(0).setTotal(total);
		}
		return searchStatistics;
	}

	@Override
	public Boolean saveSearchStatistics(Map<String, String[]> param) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String service_name=param.get("service_name")[0];
		String service_id=param.get("service_id")[0];
		String is_fixed=param.get("is_fixed")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0":param.get("rank")[0];
		
		boolean flag = false;
		if(isUpdate){
			 // 更新
			SearchStatistic.dao.findById(id).set("service_id", service_id)
			.set("service_name", service_name).set("rank", rank)
			.set("is_fixed", is_fixed).update();
		}else{
			// 新增
			SearchStatistic searchStatistic=new SearchStatistic();
			searchStatistic.set("service_id", service_id)
			.set("service_name", service_name).set("rank", rank)
			.set("is_fixed", is_fixed).save();	
		}
		flag = true;
		return flag;
	}

	@Override
	public Boolean deleteSearchStatistics(String id) {
		String[] searchStatisticsId=id.split(",");
		boolean flag=false;
		for(int i=0;i<searchStatisticsId.length;i++){
			SearchStatistic.dao.deleteById(searchStatisticsId[i]);
		}
	   flag=true;
	   return flag;
	}

	@Override
	public List<Record> getSearchStatisticsAttch(
			Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ma.*,ca.name from search_statistic_attachment ma left join catalog ca on ma.catalog_id=ca.id where ma.is_del=0 and ");
		totalSql.append("select count(1) as total from search_statistic_attachment ma left join catalog ca on ma.catalog_id=ca.id where ma.is_del=0 and ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		strFilter.put("ca.name", paramMap.get("name"));
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "ma.catalog_id";
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
		
		List<Record> searchStatisticAttch=Db.find(sql.toString());
		if(searchStatisticAttch.size()>0){
			searchStatisticAttch.get(0).set("total", total);
			String pic = "";
			int catalog_id=0;
			String name="";
			for(Record s : searchStatisticAttch){
				pic = s.getStr("pics_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("pics_path", pic);
				}
				catalog_id=s.getInt("catalog_id");
				name=switchName(catalog_id);
				if(!"".equals(name)){
					s.set("name", name);
				}
				
			}
		}
		return searchStatisticAttch;
	}

	private String switchName(int catalog_id) {
		String name="";
		if(catalog_id==Constant.HOT_CATALOG_ID){
			name="热门搜索";
		}else if(catalog_id==Constant.HOT_CUSTOM_ID){
			name="个性服务";
		}else if(catalog_id==Constant.HOT_OTHER_ID){
			name="其他服务";
		}
		return name;
	}

	@Override
	public Boolean saveSearchStatisticsAttch(Map<String, String[]> param,String path) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String catalog_id=param.get("catalog_id")[0];
		String size=param.get("size")[0];
		if(StringUtil.isNullStr(path)){
			// 未上传图片
			if(StringUtil.isNotNullMap(param, "pics_path")){
				path = param.get("pics_path")[0];
				path = BusinessUtil.getFilePath(path);
			}
		}
		boolean flag = false;
		if(isUpdate){
			 // 更新
			SearchStatisticAttch.dao.findById(id).set("catalog_id", catalog_id)
			 .set("pics_path", path).set("size", size).update();
		}else{
			// 新增
			SearchStatisticAttch searchStatisticAttch=new SearchStatisticAttch();
			searchStatisticAttch.set("catalog_id", catalog_id)
			         .set("pics_path", path).set("size", size).set("is_del", 0).save();	
		}
		flag = true;
		return flag;
	}

	@Override
	public Boolean deleteSearchStatisticsAttch(String id) {
		String[] searchStatisticsId=id.split(",");
		boolean flag=false;
		for(int i=0;i<searchStatisticsId.length;i++){
			SearchStatisticAttch.dao.findById(searchStatisticsId[i]).set("is_del", 1).update();
		}
	   flag=true;
	   return flag;
	}

	@Override
	public List<Record> getThirdApp(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ta.* from third_app ta where ta.is_del=0 and ");
		totalSql.append("select count(1) as total from third_app ta where ta.is_del=0 and ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		strFilter.put("ta.name", paramMap.get("name"));
		strFilter.put("ta.status", paramMap.get("status"));
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "ta.rank";
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
		
		List<Record> re=Db.find(sql.toString());
		if(re.size()>0){
			re.get(0).set("total", total);
		}
		return re;
	}

	@Override
	public Boolean saveThirdApp(Map<String, String[]> param) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String rank=param.get("rank")[0].equals("")==true ? "0":param.get("rank")[0];
		String name=param.get("name")[0];
		String link=param.get("link")[0];
		String status=param.get("status")[0].equals("")==true ? "0":param.get("status")[0];
		
		boolean flag = false;
		Record re=null;
		if(isUpdate){
			// 更新
			re=	Db.findById("third_app", id).set("rank", rank).set("name", name)
			      .set("link", link).set("status", status);
			Db.update("third_app", re);
		}else{
			// 新增
			re=new Record();
			re.set("rank", rank).set("name", name)
            .set("link", link).set("status", status).set("is_del", 0);
			Db.save("third_app", re);
		}
		flag = true;
		return flag;
	}

	@Override
	public Boolean deleteThirdApp(String id) {
		String[] thirdAppId=id.split(",");
		boolean flag=false;
		Record re=null;
		for(int i=0;i<thirdAppId.length;i++){
			// 更新
			re=	Db.findById("third_app", thirdAppId[i]).set("is_del", 1);
			Db.update("third_app", re);
		 }
	   flag=true;
	   return flag;
	   }

	@Override
	public List<Record> getServiceByRecId(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select mr.*,(select c.name from catalog c where c.is_del=0 and c.id=mr.catalog_id) as cat_name, (select s.service_type_name from service_type s where s.is_del=0 and s.id=mr.service_id) as ser_name from manager_hot_recommend_detail mr where ");
		totalSql.append("select count(1) as total from manager_hot_recommend_detail mr where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		intFilter.put("mr.rec_id", paramMap.get("rec_id"));
		strFilter.put("mr.title", paramMap.get("title"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "mr.rank";
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
		
		List<Record> re=Db.find(sql.toString());
		if(re.size()>0){
			re.get(0).set("total", total);
			String pic = "";
			for(Record s : re){
				pic = s.getStr("pics_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("pics_path", pic);
				}
			}
		}
		return re;
	}

	@Override
	public int saveServiceRec(Map<String, String[]> param, String path) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		String catalog_id = param.get("catalog_id")[0].equals("")==true ? "0":param.get("catalog_id")[0];
		String service_id = param.get("service_id")[0].equals("")==true ? "0":param.get("service_id")[0];
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String title=param.get("title")[0];
		String detail=param.get("detail")[0];
		String type=param.get("type")[0].equals("")==true ? "0":param.get("type")[0];
		String is_hot=param.get("is_hot")[0].equals("")==true ? "1":param.get("is_hot")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0":param.get("rank")[0];
		String color=param.get("color")[0];
		String rec_id=param.get("rec_id")[0];
		if(StringUtil.isNullStr(path)){
			// 未上传图片
			if(StringUtil.isNotNullMap(param, "pics_path")){
				path = param.get("pics_path")[0];
				path = BusinessUtil.getFilePath(path);
			}
		}
		int flag = 0;
		Record re=null;
		if(isUpdate){
			// 更新
			re=	Db.findById("manager_hot_recommend_detail", id).set("title", title).set("detail", detail)
					 .set("pics_path", path).set("catalog_id", catalog_id).set("service_id", service_id)
					
					.set("type", type).set("is_hot", is_hot).set("color",color).set("rank",rank);
			Db.update("manager_hot_recommend_detail", re);
		}else{
			// 新增
			String sql="select count(1) as total from manager_hot_recommend_detail mrd where mrd.rec_id=?";
			List<Record> rcount=Db.find(sql,rec_id);
			if(rcount.size()>0){
				long recCoount=rcount.get(0).getLong("total");
				if(recCoount>5){
					flag=2;
					return flag;
				}
			}
			re=new Record();
			re.set("title", title).set("detail", detail).set("rec_id", rec_id)
			  .set("pics_path", path).set("catalog_id", catalog_id).set("service_id", service_id)
			  .set("type", type).set("is_hot", is_hot).set("color",color).set("rank",rank);;
			Db.save("manager_hot_recommend_detail", re);
		}
		flag=1;
		Recommend recom=Recommend.dao.findById(rec_id);
		if(recom.getStr("province").equals("全国")){
			   flushRecCatach(CacheConstants.USER_HOME_PAGE_RECOMMEND);
		    }else if(recom.getStr("city")!=null&&!recom.getStr("city").equals("")){
			   commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,recom.getStr("province"),recom.getStr("city")); 
		    }else{
			   flushRecCatach(CacheConstants.USER_HOME_PAGE_RECOMMEND,recom.getStr("province")); 
		    }
		
		commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,recom.getStr("province"),"original");
		commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,recom.getStr("province"),"originalPerson");
		return flag;
	}

	@Override
	public int startOrstopRecs(Map<String, String[]> param) {
		
		String id=param.get("id")[0];
		String province=param.get("province")[0];
		String city=param.get("city")[0];
		String status=param.get("status")[0];
		int flag = 0;
		if(status.equals("1")){
			String sql="select count(1) as total from manager_hot_recommend_detail mrd where mrd.rec_id=?";
			List<Record> rcount=Db.find(sql,id);
			if(rcount.size()>0){
				long recCount=rcount.get(0).getLong("total");
				if(recCount<6){
					flag=3;
					return flag;
				}
			}
			
			
			String sqlLike="select count(1) as total from manager_hot_recommend mh where mh.province=? and mh.city=? and mh.status=1 and mh.is_del=0";
			List<Record> rLikecount=Db.find(sqlLike,province,city);
			if(rLikecount.size()>0){
				long recLikeCount=rLikecount.get(0).getLong("total");
				if(recLikeCount>0){
					flag=4;
					return flag;
				}
			}
			
		}
	    Record re=Db.findById("manager_hot_recommend", id);
	    re.set("status", status);
	    Db.update("manager_hot_recommend", re);
	    if(status.equals("1")){
	    	flag=1;
	    }else{
	    	flag=2;	
	    }
	    if(province.equals("全国")){
			   flushRecCatach(CacheConstants.USER_HOME_PAGE_RECOMMEND);
		 }else if(city!=null&&!city.equals("")){
			     commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,city); 
		 }else{
			   flushRecCatach(CacheConstants.USER_HOME_PAGE_RECOMMEND,province); 
		 }
		commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"original");
		commonCacheService.deleteObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"originalPerson");
		
		return flag;
		}
	  private String getKey(String orginalKey){
		  try {
			  	return  StringUtil.byteToHexString(orginalKey.getBytes("utf-8"));
		   } catch (UnsupportedEncodingException e) {
			  	e.printStackTrace();
			  	return "";
		  }
	   }
	  private String getBytesKey(String orginalKey){
		  try {
			  	return  new String(orginalKey.getBytes("GBK"),"utf-8");
		   } catch (UnsupportedEncodingException e) {
			  	e.printStackTrace();
			  	return "";
		  }
	   }
	@Override
	public List<Record> getServiceTree(Map<String, String[]> param) {
		String node=param.get("node")[0];
		if(node.equals("NaN")){
			node="0";
			}
		List<Record> recordList=new ArrayList<Record>();
		String sql="select c.id,c.name as text,c.alias,c.leaf,c.leaf as leafVal,c.demand,c.status,c.rank,c.icon_path,c.big_icon_path from catalog c where c.is_del=0 and c.parentid=? order by rank ASC ";
		recordList=Db.find(sql,node);
		String iconCls=""; 
		String pic="";
		String picbig="";
		if(recordList!=null&&recordList.size()>0){
			for(Record ca:recordList){
				ca.set("leaf", StringUtil.IntToBol(ca.getInt("leaf")));
				iconCls=ca.getInt("status")==1?"release":"recall"; 
				ca.set("iconCls", iconCls);
				pic = ca.getStr("icon_path");
				picbig = ca.getStr("big_icon_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					ca.set("icon_path", pic);
				}
				if(!StringUtil.isNullStr(picbig)){
					picbig = BusinessUtil.getFileUrl(picbig);
					ca.set("big_icon_path", picbig);
				}
			}
			
		}
		return recordList;
	}

	@Override
	public List<Record> getCatalog(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select st.service_type_name,cs.id,cs.status from service_type st inner join catalog_service cs on st.id=cs.service_id where st.is_del=0 and ");
		totalSql.append("select count(1) as total from service_type st inner join catalog_service cs on st.id=cs.service_id where st.is_del=0 and ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		intFilter.put("cs.status", paramMap.get("status"));
		strFilter.put("cs.catalog_id", paramMap.get("catalogid"));
		strFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "st.id";
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
		
		List<Record> re=Db.find(sql.toString());
		if(re.size()>0){
			re.get(0).set("total", total);
		}
		return re;
	}

	@Override
	public boolean addCatalogService(Map<String, String[]> param) {
		String catalogid=param.get("catalogid")[0];
		String treeCatAlis=getTreeCatId(catalogid);
		if(treeCatAlis!=null&&treeCatAlis.equals("gxfw")){
			return addCatalogServiceGxfw(param);
		}
		boolean flag = false; 
		String id = param.get("id")[0];
		String[] ids=id.split(",");
		
		Record re=null;
		String sql="select id from catalog_service where catalog_id=? and service_id=?";
		List<Record> lists=null;
		for(int i=0;i<ids.length;i++){
			lists=Db.find(sql,catalogid,ids[i]);
			if(lists!=null&&lists.size()>0){
				continue;
			}
			re=new Record();
			re.set("catalog_id", catalogid).set("service_id", ids[i]).set("status", 0);
			Db.save("catalog_service", re);
			flag=true;
			CommonUtil.flushCataCache(ids[i]);
		}
		flushCaheCatlog(catalogid);
		
		return flag;
	}

	private boolean addCatalogServiceGxfw(Map<String, String[]> param) {
		String catalogid=param.get("catalogid")[0];
		boolean flag = false; 
		String id = param.get("id")[0];
		String[] ids=id.split(",");
		List<Record> catIdList=null;
		Record rs=Db.findById("catalog", catalogid);
		if(rs!=null){
			String sqlCat="select id from catalog where parentid=?";
			catIdList=Db.find(sqlCat,rs.getInt("parentid"));
		}
		Record re=null;
		String sql="select id from catalog_service where catalog_id=? and service_id=?";
		List<Record> lists=null;
		for(int i=0;i<ids.length;i++){
			boolean repeat=false;
			if(catIdList!=null&&catIdList.size()>0){
				for(Record caList:catIdList){
					lists=Db.find(sql,caList.getInt("id"),ids[i]);
					if(lists!=null&&lists.size()>0){
						repeat=true;
					}
				}
				
			}
			if(!repeat){
			re=new Record();
			re.set("catalog_id", catalogid).set("service_id", ids[i]).set("status", 0);
			Db.save("catalog_service", re);
			flag=true;
			CommonUtil.flushCataCache(ids[i]);
			}
		}
		flushCaheCatlog(catalogid);
		return flag;
		
	}

	private String getTreeCatId(String catalogid) {
		String alias="";
		if(catalogid==null||catalogid.equals("")){
			return alias;
		}
		Record re=Db.findById("catalog", catalogid);
		if(re==null){
			return alias;
		}else if(re.getInt("level")==0){
			alias=re.getStr("alias");
			return alias;
		}else{
		  return getTreeCatId(re.getInt("parentid").toString());
		}
	}

	@Override
	public boolean deleteCatalogService(Map<String, String[]> param) {
		boolean flag = false ; 
		String id = param.get("id")[0];
		String[] ids=id.split(",");
		if(ids.length>0){
			String catId=Db.findById("catalog_service", ids[0]).getInt("catalog_id").toString();
			flushCaheCatlog(catId);
			String serviceId=Db.findById("catalog_service", ids[0]).getInt("service_id").toString();
			CommonUtil.flushCataCache(serviceId);
		}
		for(int i=0;i<ids.length;i++){
			Db.deleteById("catalog_service", ids[i]);
		}
		
		flag=true;
		
		return flag;
	}

	@Override
	public int deleteCatalog(Map<String, String[]> param) {
		int flag = 0 ; 
		String id = param.get("id")[0];
		String sqlfind="select c.* from catalog c where c.parentid=?";
		List<Record> refind=Db.find(sqlfind,id);
		if(refind!=null&&refind.size()>0){
			flag=1;
			return flag;
		}
		Db.deleteById("catalog",id);
		String sql="select c.* from catalog_service c where c.catalog_id=?";
		List<Record> res=Db.find(sql,id);
	    if(res!=null&&res.size()>0){
	    	for(Record r:res){
	    		CommonUtil.flushCataCache(r.getInt("service_id").toString());
	    		Db.delete("catalog_service", r);
	    	}
	    }
		flag=2;
		flushCaheCatlog(id);
		return flag;
	}

	@Override
	public int saveCatalog(Map<String, String[]> param,String smallpath,String bigpath) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		String parentid = StringUtil.null2Str(param.get("parentid")[0]);
		boolean isUpdate = false; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		
		String name=param.get("name")[0];
		String leaf=param.get("leaf")[0];
		String demand=param.get("demand")[0];
		String alias=param.get("alias")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0":param.get("rank")[0];
		int flag = 0;
		Record re=null;
		if(isUpdate){
			// 更新
			if("1".equals(leaf)){
				String sqlfind="select c.* from catalog c where c.parentid=?";
				List<Record> refind=Db.find(sqlfind,id);
				if(refind!=null&&refind.size()>0){
					flag=1;
					return flag;
				}
				
			}
			
			int parentId=Db.queryInt("select parentid from catalog where is_del=0 and id=?",id);
			List<Record> reid=Db.find("select id from catalog where parentid=? and name=?",parentId,name);
			if(reid!=null&&reid.size()>0){
				if(reid.get(0).getInt("id")==Integer.parseInt(id)){
				    	
				}else{
				 flag=3;
				return flag;
			  }
			}
			
			re=	Db.findById("catalog", id).set("name", name).set("leaf", leaf).set("demand", demand).set("alias", alias).set("rank", rank);
			if(!StringUtil.isNullStr(smallpath)){
				re.set("icon_path", smallpath);
			}
			if(!StringUtil.isNullStr(bigpath)){
				re.set("big_icon_path", bigpath);
			}
			Db.update("catalog", re);
			flushCaheCatlog(re.getInt("id").toString());
		}else{
			// 新增
			int level=Db.findById("catalog", parentid).getInt("level")+1;
			List<Record> reid=Db.find("select id from catalog where parentid=? and name=?",parentid,name);
			if(reid!=null&&reid.size()>0){
			  flag=3;
			  return flag;
			    
			}
			
			re=new Record();
			re.set("name", name).set("leaf", leaf).set("parentid", parentid).set("alias", alias).set("rank", rank)
			.set("level", level).set("status", 0).set("demand", demand).set("icon_path", smallpath).set("big_icon_path", bigpath);
			Db.save("catalog", re);
			flushCaheCatlog(re.getLong("id").toString());
		}
		flag=2;
		//删除缓存
		return flag;
	}
  
	private void flushCaheCatlog(String catId){
		commonCacheService.deleteObject(CacheConstants.MERCHANT_CATALOG_TOP);
		commonCacheService.deleteObject(CacheConstants.USER_HOME_HOTSEARCH__SERVICES_UNDER_CATALOG,catId);
		commonCacheService.deleteObject(CacheConstants.CATALOG_AND_SERVICES,catId);
		commonCacheService.deleteObject(CacheConstants.CATALOG_AND_SERVICES_FOR_MERCHANT,catId);
		List<Record> res=Db.find("select id from catalog where level=0 and alias='gxfw'");
	    if(res!=null&&res.size()>0){
	    	String gxfwId=res.get(0).getInt("id").toString();
	    	commonCacheService.deleteObject(CacheConstants.CATALOG_AND_SERVICES_FOR_MERCHANT,gxfwId);
	    }
	}
	
	@Override
	public boolean addRootCatalog(Map<String, String[]> param, String smallpath, String bigpath) {
		boolean flag = false;
		String name=param.get("name")[0];
		String leaf=param.get("leaf")[0];
		String demand=param.get("demand")[0];
		String alias=param.get("alias")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0":param.get("rank")[0];
		long count=Db.queryLong("select count(1) from catalog where level=0 and is_del=0 and name=?",name);
		if(count>0){
			return flag;
		}
		Record re=new Record();
		re.set("name", name).set("leaf", leaf).set("parentid", 0).set("alias", alias).set("rank", rank)
		.set("level", 0).set("demand", demand).set("status", 0).set("icon_path", smallpath).set("big_icon_path", bigpath);
		Db.save("catalog", re);
		flag=true;
		return flag;
	}

	@Override
	public List<Record> getAllCatalog(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select cs.id,cs.name from catalog cs  where cs.is_del=0 and ");
		totalSql.append("select count(1) as total from catalog cs where cs.is_del=0 and ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("cs.name", paramMap.get("name"));
		
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "cs.id";
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
		
		List<Record> re=Db.find(sql.toString());
		if(re.size()>0){
			re.get(0).set("total", total);
		}
		return re;
	}

	@Override
	public int releaseOrrecallCatalog(Map<String, String[]> param) {
		int flag = 0 ; 
		String id = param.get("id")[0];
		String status = param.get("status")[0];
		Record re=Db.findById("catalog", id);
		if(re!=null){
			int sta=re.getInt("status");
			if(Integer.parseInt(status)==sta){
				flag=1;
				return flag;
			}
			
		}
		updateCata(id,status);
		flag=2;
		return flag;
	}

	private void updateCata(String id, String status) {
		Record re=Db.findById("catalog", id);
		if(re!=null){
			re.set("status", status);
			Db.update("catalog", re);
			
		}
		flushCaheCatlog(id);
		String sql="select * from catalog where parentid=?";
		List<Record> reList=Db.find(sql, id);
		if(reList!=null&&reList.size()>0){
		for(Record r:reList){
			String childid = r.getInt("id").toString();
			updateCata(childid,status);
		}
		}
	}

	@Override
	public boolean auditCatalogService(Map<String, String[]> param) {
		boolean flag = false ; 
		String id = param.get("id")[0];
		String[] ids=id.split(",");
		Record re=null;
		for(int i=0;i<ids.length;i++){
			re=Db.findById("catalog_service", ids[i]);
			if(re!=null){
				re.set("status", 1);
				Db.update("catalog_service", re);
			}
		}
		flag=true;
		String catId=Db.findById("catalog_service", ids[0]).getInt("catalog_id").toString();
		flushCaheCatlog(catId);
		
		return flag;
	}

	@Override
	public List<Record> getServiceType(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		
	    String	cataIds = getGxfwCataIds();
		
		sql.append("select st.* from service_type st join catalog_service cs on st.id=cs.service_id where cs.catalog_id in (" ).append(cataIds);
		sql.append(") and ");
		totalSql.append("select count(1) as total from service_type st join catalog_service cs on st.id=cs.service_id where cs.catalog_id in (" ).append(cataIds);
		totalSql.append(") and ");
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> serviceTypeList = Db.find(sql.toString());

		if (serviceTypeList.size() > 0) {
			serviceTypeList.get(0).set("total", total);
		}
		return serviceTypeList;
	}

	private String getGxfwCataIds() {
		String sql="select id from catalog where alias='gxfw'";
		StringBuffer cataIds = new StringBuffer();
		List<Record> res=Db.find(sql);
		if(res!=null&&res.size()>0){
			for(Record re:res){
				recursiveGxfwCataIds(cataIds,re.getInt("id"));
			}
		}
		return cataIds.substring(0,cataIds.lastIndexOf(","));
	}

	private void recursiveGxfwCataIds(StringBuffer cataIds, Integer id) {
		String sql="select id,leaf from catalog where parentid=?";
		List<Record> res=Db.find(sql,id);
		if(res!=null&&res.size()>0){
			for(Record re:res){
				if(re.getInt("leaf")==1){
					cataIds.append(re.getInt("id"));
					cataIds.append(",");
				}else{
					recursiveGxfwCataIds(cataIds,re.getInt("id"));
				}
			}
		}
		
	}

	@Override
	public List<Record> getOrderServiceTree(Map<String, String[]> param) {
		String node=param.get("node")[0];
		if(node.equals("NaN")){
			node="-1";
		}
		List<Record> recordList=new ArrayList<Record>();
		String sql="select c.id,c.name as text,c.leaf,c.leaf as leafVal,c.demand,c.status,c.icon,c.big_icon,c.rank,c.link,c.flag,c.is_close from catalog_for_order c where c.is_del=0 and c.parentid=? order by rank ASC ";
		recordList=Db.find(sql,node);
		String iconCls=""; 
		String pic="";
		String picbig="";
		if(recordList!=null&&recordList.size()>0){
			for(Record ca:recordList){
				ca.set("leaf", StringUtil.IntToBol(ca.getInt("leaf")));
				iconCls=ca.getInt("status")==1?"release":"recall"; 
				ca.set("iconCls", iconCls);
				pic = ca.getStr("icon");
				picbig = ca.getStr("big_icon");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					ca.set("icon", pic);
				}
				if(!StringUtil.isNullStr(picbig)){
					picbig = BusinessUtil.getFileUrl(picbig);
					ca.set("big_icon", picbig);
				}
			}
			
		}
		return recordList;
	}

	@Override
	public int deleteOrderCatalog(Map<String, String[]> param) {
		int flag = 0 ; 
		String id = param.get("id")[0];
		String sqlfind="select c.* from catalog_for_order c where c.parentid=?";
		List<Record> refind=Db.find(sqlfind,id);
		if(refind!=null&&refind.size()>0){
			flag=1;
			return flag;
		}
		Db.deleteById("catalog_for_order",id);
		String sql="select c.* from catalog_service_for_order c where c.catalog_id=?";
		List<Record> res=Db.find(sql,id);
	    if(res!=null&&res.size()>0){
	    	for(Record r:res){
	    		CommonUtil.flushCataOrderCache(r.getInt("service_id").toString());
	    		Db.delete("catalog_service_for_order", r);
	    	}
	    }
		flag=2;
		//flushCaheCatlog(id);
		return flag;
	}

	@Override
	public int releaseOrrecallOrderCatalog(Map<String, String[]> param) {
		int flag = 0 ; 
		String id = param.get("id")[0];
		String status = param.get("status")[0];
		Record re=Db.findById("catalog_for_order", id);
		if(re!=null){
			int sta=re.getInt("status");
			if(Integer.parseInt(status)==sta){
				flag=1;
				return flag;
			}
			
		}
		updateOrderCata(id,status);
		flag=2;
		return flag;
	}
	private void updateOrderCata(String id, String status) {
		Record re=Db.findById("catalog_for_order", id);
		if(re!=null){
			re.set("status", status);
			Db.update("catalog_for_order", re);
			
		}
		//flushCaheCatlog(id);
		String sql="select * from catalog_for_order where parentid=?";
		List<Record> reList=Db.find(sql, id);
		if(reList!=null&&reList.size()>0){
		for(Record r:reList){
			String childid = r.getInt("id").toString();
			updateOrderCata(childid,status);
		}
		}
	}
	@Override
	public int saveOrderCatalog(Map<String, String[]> param, String smallpath,
			String bigpath) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		String parentid = StringUtil.null2Str(param.get("parentid")[0]);
		boolean isUpdate = false; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		
		String name=param.get("name")[0];
		String leaf=param.get("leaf")[0];
		String demand=param.get("demand")[0];
		
		String rank=param.get("rank")[0];
		String flagSta=param.get("flag")[0];
		String link=param.get("link")[0];
		String is_close=param.get("is_close")[0];
		
		int flag = 0;
		Record re=null;
		if(isUpdate){
			// 更新
			if("1".equals(leaf)){
				String sqlfind="select c.* from catalog_for_order c where c.parentid=?";
				List<Record> refind=Db.find(sqlfind,id);
				if(refind!=null&&refind.size()>0){
					flag=1;
					return flag;
				}
				
			}
			re=	Db.findById("catalog_for_order", id).set("name", name).set("leaf", leaf).set("demand", demand)
					.set("rank", rank).set("flag", flagSta).set("link", link).set("is_close", is_close);
			if(!StringUtil.isNullStr(smallpath)){
				re.set("icon", smallpath);
			}
			if(!StringUtil.isNullStr(bigpath)){
				re.set("big_icon", bigpath);
			}
			Db.update("catalog_for_order", re);
			//flushCaheCatlog(re.getInt("id").toString());
		}else{
			// 新增
			int level=Db.findById("catalog_for_order", parentid).getInt("level")+1;
			re=new Record();
			re.set("name", name).set("leaf", leaf).set("parentid", parentid).set("level", level).set("status", 0).set("demand", demand).set("icon", smallpath).set("big_icon", bigpath)
			.set("rank", rank).set("flag", flagSta).set("link", link).set("is_close", is_close);
			Db.save("catalog_for_order", re);
			//flushCaheCatlog(re.getLong("id").toString());
		}
		flag=2;
		
		return flag;
	}

	@Override
	public boolean addOrderRootCatalog(Map<String, String[]> param,
			String smallpath, String bigpath) {
		boolean flag = false;
		String name=param.get("name")[0];
		String leaf=param.get("leaf")[0];
		String demand=param.get("demand")[0];
		String rank=param.get("rank")[0];
		String flagCon=param.get("flag")[0];
		String link=param.get("link")[0];
		
		Record re=new Record();
		re.set("name", name).set("rank", rank).set("flag", flagCon).set("link", link)
		.set("leaf", leaf).set("parentid", -1).set("level", -1).set("demand", demand).set("status", 0).set("icon", smallpath).set("big_icon", bigpath);
		Db.save("catalog_for_order", re);
		flag=true;
		return flag;
	}

	@Override
	public List<Record> getOrderCatalog(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select st.service_type_name,cs.id,cs.status from service_type st inner join catalog_service_for_order cs on st.id=cs.service_id where st.is_del=0 and ");
		totalSql.append("select count(1) as total from service_type st inner join catalog_service_for_order cs on st.id=cs.service_id where st.is_del=0 and ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		intFilter.put("cs.status", paramMap.get("status"));
		strFilter.put("cs.catalog_id", paramMap.get("catalogid"));
		strFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "st.id";
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
		
		List<Record> re=Db.find(sql.toString());
		if(re.size()>0){
			re.get(0).set("total", total);
		}
		return re;
	}

	@Override
	public boolean addOrderCatalogService(Map<String, String[]> param) {
		String catalogid=param.get("catalogid")[0];
		String treeCatAlis=getOrderTreeCatId(catalogid);
		if(treeCatAlis!=null&&treeCatAlis.equals("gxfw")){
			return addOrderCatalogServiceGxfw(param);
		}
		boolean flag = false; 
		String id = param.get("id")[0];
		String[] ids=id.split(",");
		
		Record re=null;
		String sql="select id from catalog_service_for_order where catalog_id=? and service_id=?";
		List<Record> lists=null;
		for(int i=0;i<ids.length;i++){
			lists=Db.find(sql,catalogid,ids[i]);
			if(lists!=null&&lists.size()>0){
				continue;
			}
			re=new Record();
			re.set("catalog_id", catalogid).set("service_id", ids[i]).set("status", 0);
			Db.save("catalog_service_for_order", re);
			flag=true;
			CommonUtil.flushCataOrderCache(ids[i]);
		}
		//flushCaheCatlog(catalogid);
		return flag;
	}

	@Override
	public boolean deleteOrderCatalogService(Map<String, String[]> param) {
		boolean flag = false ; 
		String id = param.get("id")[0];
		String[] ids=id.split(",");
//		if(ids.length>0){
//			String catId=Db.findById("catalog_service_for_order", ids[0]).getInt("catalog_id").toString();
//			//flushCaheCatlog(catId);
//		}
		for(int i=0;i<ids.length;i++){
			CommonUtil.flushCataOrderCache(ids[i]);
			Db.deleteById("catalog_service_for_order", ids[i]);
		}
		
		flag=true;
		return flag;
	}

	@Override
	public boolean auditOrderCatalogService(Map<String, String[]> param) {
		boolean flag = false ; 
		String id = param.get("id")[0];
		String status = param.get("status")[0];
		String[] ids=id.split(",");
		Record re=null;
		for(int i=0;i<ids.length;i++){
			re=Db.findById("catalog_service_for_order", ids[i]);
			if(re!=null){
				re.set("status", status);
				Db.update("catalog_service_for_order", re);
			}
		}
		flag=true;
		String catId=Db.findById("catalog_service_for_order", ids[0]).getInt("catalog_id").toString();
		//flushCaheCatlog(catId);
		return flag;
	}
	private String getOrderTreeCatId(String catalogid) {
		String alias="";
		if(catalogid==null||catalogid.equals("")){
			return alias;
		}
		Record re=Db.findById("catalog_for_order", catalogid);
		if(re==null){
			return alias;
		}else if(re.getInt("level")==0){
			alias=re.getStr("alias");
			return alias;
		}else{
		  return getOrderTreeCatId(re.getInt("parentid").toString());
		}
	}
	private boolean addOrderCatalogServiceGxfw(Map<String, String[]> param) {
		String catalogid=param.get("catalogid")[0];
		boolean flag = false; 
		String id = param.get("id")[0];
		String[] ids=id.split(",");
		List<Record> catIdList=null;
		Record rs=Db.findById("catalog", catalogid);
		if(rs!=null){
			String sqlCat="select id from catalog_for_order where parentid=?";
			catIdList=Db.find(sqlCat,rs.getInt("parentid"));
		}
		Record re=null;
		String sql="select id from catalog_service_for_order where catalog_id=? and service_id=?";
		List<Record> lists=null;
		for(int i=0;i<ids.length;i++){
			boolean repeat=false;
			if(catIdList!=null&&catIdList.size()>0){
				for(Record caList:catIdList){
					lists=Db.find(sql,caList.getInt("id"),ids[i]);
					if(lists!=null&&lists.size()>0){
						repeat=true;
					}
				}
				
			}
			if(!repeat){
			re=new Record();
			re.set("catalog_id", catalogid).set("service_id", ids[i]).set("status", 0);
			Db.save("catalog_service_for_order", re);
			flag=true;
			}
		}
		flushCaheCatlog(catalogid);
		return flag;
		
	}

	@Override
	public List<Record> getShareActivity(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select * from share_activity s where s.is_del=0 and ");
		totalSql.append("select count(1) as total from share_activity s where s.is_del=0 and ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("s.title", paramMap.get("title"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		String image="";
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			recordList.get(0).set("total", total);
			for(Record ca:recordList){
				image = ca.getStr("image");
				if(!StringUtil.isNullStr(image)){
					image = BusinessUtil.getFileUrl(image);
					ca.set("image", image);
				}
			}
		}
		return recordList;
	}

	@Override
	public Boolean deleteShareActivity(String id) {
		String[] ids=id.split(",");
		boolean flag=false;
		Record re=null;
		for(int i=0;i<ids.length;i++){
			// 更新
			re=	Db.findById("share_activity", ids[i]).set("is_del", 1);
			Db.update("share_activity", re);
		 }
	   flag=true;
	   return flag;
	}

	@Override
	public Boolean saveShareActivity(Map<String, String[]> param,
			String resultPath) {
		boolean flag=false;
		String id = StringUtil.null2Str(param.get("id")[0]);
		String title=param.get("title")[0];
		String desc=param.get("desc")[0];
		String clickUrl=param.get("clickUrl")[0];
		String webUrl=param.get("webUrl")[0];
		
		boolean isUpdate = false; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		
		Record re=null;
		if(isUpdate){
			re=	Db.findById("share_activity", id).set("title", title).set("desc", desc).set("clickUrl", clickUrl).set("webUrl", webUrl);
			if(!StringUtil.isNullStr(resultPath)){
				re.set("image", resultPath);
			}
			Db.update("share_activity", re);
		}else{
			// 新增
			re=new Record();
			re.set("title", title).set("desc", desc).set("clickUrl", clickUrl).set("webUrl", webUrl)
			.set("status", 1).set("is_del", 0);
			if(!StringUtil.isNullStr(resultPath)){
				re.set("image", resultPath);
			}
			Db.save("share_activity", re);
		}
		flag=true;
		return flag;
	}

	@Override
	public int startOrstopAct(Map<String, String[]> param) {
		int flag=0;
		String id = StringUtil.null2Str(param.get("id")[0]);
		String status=param.get("status")[0];
		if(status.equals("0")){
		String sql="select * from share_activity where is_del=0 and status=0";
		List<Record> res=Db.find(sql);
		if(res!=null&&res.size()>0){
			flag=3;
			return flag;
		}
		Record re=Db.findById("share_activity", id).set("status", 0);
		Db.update("share_activity", re);
		flag=1;
		}else{
		Record re=Db.findById("share_activity", id).set("status", 1);
		Db.update("share_activity", re);
		flag=2;
		}
		commonCacheService.deleteObject(CacheConstants.SHARE_HTML);
		return flag;
	}

	@Override
	public List<Record> getStaticActivity(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select * from static_activity s where  ");
		totalSql.append("select count(1) as total from static_activity s where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("s.act_key", paramMap.get("act_key"));
		intFilter.put("s.status", paramMap.get("status"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			recordList.get(0).set("total", total);
			
		}
		return recordList;
	
	}

	@Override
	public Boolean deleteStaticActivity(String id) {
		String[] ids=id.split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("static_activity", ids[i]);
		 }
	   flag=true;
	   return flag;
	}

	@Override
	public Boolean saveStaticActivity(Map<String, String[]> param) {
		boolean flag=false;
		String id = StringUtil.null2Str(param.get("id")[0]);
		String name = StringUtil.null2Str(param.get("name")[0]);
		String act_key=param.get("act_key")[0];
		String url=param.get("url")[0];
		
		boolean isUpdate = false; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		
		String sql="select * from static_activity where act_key=?";
		List<Record> res=Db.find(sql,act_key);
 		Record re=null;
		if(isUpdate){
			if(res!=null&&res.size()>0){
				String newId=res.get(0).getInt("id").toString();
				if(!id.equals(newId)){
					return flag;
				}
			}
			re=	Db.findById("static_activity", id).set("name", name).set("act_key", act_key).set("url", url).set("status", 1);
			Db.update("static_activity", re);
		}else{
			// 新增
			if(res!=null&&res.size()>0){
					return flag;
			}
			re=new Record();
			re.set("name", name).set("act_key", act_key).set("url", url);
			Db.save("static_activity", re);
		}
		flag=true;
		return flag;
	}

	@Override
	public int startOrstopStaAct(Map<String, String[]> param) {
		int flag=0;
		String id = StringUtil.null2Str(param.get("id")[0]);
		String status=param.get("status")[0];
		Record re=null;
		if(status.equals("0")){
		 re=Db.findById("static_activity", id).set("status", 0);
		Db.update("static_activity", re);
		flag=1;
		}else{
		 re=Db.findById("static_activity", id).set("status", 1);
		Db.update("static_activity", re);
		flag=2;
		}
		commonCacheService.deleteObject(CacheConstants.STATIC_HTML,re.getStr("act_key"));
		return flag;
	}

	@Override
	public List<Record> getActivity(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ai.*,(select at.type_name from activity_type at where at.id=ai.activity_type_id) as type_name from activity_info ai where  ");
		totalSql.append("select count(1) as total from activity_info ai where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("ai.title", paramMap.get("title"));
		intFilter.put("ai.is_pub", paramMap.get("is_pub"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		if(!(paramMap.get("entrance_id")[0]==null) && !paramMap.get("entrance_id")[0].equals("")){
			sql.append(" and ai.id in (select ar.activity_id from activity_entrance_relation ar where ar.entrance_id=").append(paramMap.get("entrance_id")[0]).append(")");
			totalSql.append(" and ai.id in (select ar.activity_id from activity_entrance_relation ar where ar.entrance_id=").append(paramMap.get("entrance_id")[0]).append(")");
			}
		
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			String sImage="";
			String bImage="";
			String shareImage="";
			recordList.get(0).set("total", total);
			for(Record ca:recordList){
				sImage = ca.getStr("sImage");
				bImage = ca.getStr("bImage");
				shareImage = ca.getStr("shareImage");
				if(!StringUtil.isNullStr(sImage)){
					sImage = BusinessUtil.getFileUrl(sImage);
					ca.set("sImage", sImage);
				}
				if(!StringUtil.isNullStr(bImage)){
					bImage = BusinessUtil.getFileUrl(bImage);
					ca.set("bImage", bImage);
				}
				if(!StringUtil.isNullStr(shareImage)){
					shareImage = BusinessUtil.getFileUrl(shareImage);
					ca.set("shareImage", shareImage);
				}
			}
			
		}
		return recordList;
	
	}

	@Override
	public boolean saveActivity(Map<String, String[]> param, String smallPath,
			String bigPath, String sharePath) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false; 
		boolean flag = false; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		
		String title=param.get("title")[0];
		String subtitle=param.get("subtitle")[0];
		String description=param.get("description")[0];
		String stime=param.get("stime")[0];
		String etime=param.get("etime")[0];
		String detail_table=param.get("detail_table")[0];
		String activity_type_id=param.get("activity_type_id")[0];
		String shareDesc=param.get("shareDesc")[0];
		String shareTitle=param.get("shareTitle")[0];
		String shareLink=param.get("shareLink")[0];
	
		String url=param.get("url")[0];
		
		Record re=null;
		if(isUpdate){
			// 更新
			re=	Db.findById("activity_info", id).set("title", title).set("subtitle", subtitle).set("description", description).set("stime", stime).set("etime", etime)
					.set("activity_type_id", activity_type_id).set("shareDesc", shareDesc).set("url", url).set("detail_table", detail_table)
					.set("shareTitle", shareTitle).set("shareLink", shareLink);
			if(!StringUtil.isNullStr(smallPath)){
				re.set("sImage", smallPath);
			}
			if(!StringUtil.isNullStr(bigPath)){
				re.set("bImage", bigPath);
			}
			if(!StringUtil.isNullStr(sharePath)){
				re.set("shareImage", sharePath);
			}
			Db.update("activity_info", re);
			
		}else{
			// 新增
			re=new Record();
			re.set("title", title).set("subtitle", subtitle).set("description", description).set("stime", stime).set("etime", etime)
			.set("activity_type_id", activity_type_id).set("shareDesc", shareDesc).set("url", url).set("sImage", smallPath).set("bImage", bigPath).set("detail_table", detail_table)
			.set("shareImage", sharePath).set("shareTitle", shareTitle).set("shareLink", shareLink);
			Db.save("activity_info", re);
			
		}
		flag=true;
		return flag;
	}

	@Override
	public Boolean deleteActivity(String id) {
		String[] ids=id.split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("activity_info", ids[i]);
		 }
	   flag=true;
	   return flag;
	}

	@Override
	public int startOrstopActivity(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String status=param.get("status")[0];
		int flag = 0;
		Record re=Db.findById("activity_info", id);
		re.set("is_pub", status);
		Db.update("activity_info", re);
		if(status.equals("1")){
			flag=1;
		}else{
			flag=2;
		}
		return flag;
	}

	@Override
	public List<Record> getActivityEntranceReleation(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ar.*,(select ae.description from activity_entrance ae where ar.entrance_id=ae.id) as entrance_des,(select ai.title from activity_info ai where ar.activity_id=ai.id) as activity_title from activity_entrance_relation ar where  ");
		totalSql.append("select count(1) as total from activity_entrance_relation ar where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		intFilter.put("ar.activity_id", paramMap.get("activity_id"));
		sql.append(Util.getFilter(strFilter, intFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			recordList.get(0).set("total", total);
			
		}
		return recordList;
	}

	@Override
	public boolean addActivityEntrance(Map<String, String[]> param) {
		boolean flag=false;
		String activity_id=param.get("activity_id")[0];
		String entrance_id=param.get("entrance_id")[0];
		String[] enIds=entrance_id.split(",");
		Record re=null;
		List<Record> lists=null;
		String sql="select id from activity_entrance_relation where activity_id=? and entrance_id=?";
		for(int i=0;i<enIds.length;i++){
			lists=Db.find(sql,activity_id,enIds[i]);
			if(lists!=null&&lists.size()>0){
				continue;
			}
			re=new Record();
			re.set("activity_id", activity_id);
			re.set("entrance_id", entrance_id);
			Db.save("activity_entrance_relation", re);
			flag=true;
		}
		return flag;
	}

	@Override
	public List<Record> getActivityEntrance(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ar.* from activity_entrance ar where  ");
		totalSql.append("select count(1) as total from activity_entrance ar where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		
		sql.append(Util.getFilter(strFilter, intFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			recordList.get(0).set("total", total);
			
		}
		return recordList;
	}

	@Override
	public Boolean deleteActivityEntrance(String id) {
		String[] ids=id.split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("activity_entrance_relation", ids[i]);
		 }
	   flag=true;
	   return flag;
	}

	@Override
	public List<Record> getActivityDetail(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ar.*,st.service_type_name from activity_requirement_detail ar inner join service_type st on ar.service_id=st.id where  ");
		totalSql.append("select count(1) as total from activity_requirement_detail ar inner join service_type st on ar.service_id=st.id where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		intFilter.put("ar.activity_id", paramMap.get("activity_id"));
		strFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		sql.append(Util.getFilter(strFilter, intFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "ar.id";
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
		
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			recordList.get(0).set("total", total);
			String pic = "";
			for(Record r : recordList){
				pic = r.getStr("image");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					r.set("image", pic);
				}
			}
		}
		return recordList;
	}

	@Override
	public Boolean saveActivityDetail(Map<String, String[]> param,
			String resultPath) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		String activity_id = param.get("activity_id")[0].equals("")==true ? "0":param.get("activity_id")[0];
		String service_id = param.get("service_id")[0].equals("")==true ? "0":param.get("service_id")[0];
		String service_name = param.get("service_name")[0];
		
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String title=param.get("title")[0];
		String description=param.get("description")[0];
		
		if(StringUtil.isNullStr(resultPath)){
			// 未上传图片
			if(StringUtil.isNotNullMap(param, "image")){
				resultPath = param.get("image")[0];
				resultPath = BusinessUtil.getFilePath(resultPath);
			}
		}
		Record re=null;
		String sql="select id from activity_requirement_detail where service_id=? and activity_id=?";
		List<Record> lis=Db.find(sql,service_id,activity_id);
		if(isUpdate){
			if(lis!=null&&lis.size()>0){
				String newid=lis.get(0).getLong("id").toString();
				if(!newid.equals(id)){
					return false;
				}
			}
			// 更新
			re=	Db.findById("activity_requirement_detail", id).set("title", title).set("description", description)
					 .set("image", resultPath).set("activity_id", activity_id).set("service_id", service_id).set("service_name", service_name);
			Db.update("activity_requirement_detail", re);
		}else{
			// 新增
			if(lis!=null&&lis.size()>0){
			  return false;
			}
			re=new Record();
			re.set("title", title).set("description", description)
			 .set("image", resultPath).set("activity_id", activity_id).set("service_id", service_id).set("service_name", service_name);
			Db.save("activity_requirement_detail", re);
		}
		return true;
	}

	@Override
	public Boolean deleteActivityDetail(String id) {
		String[] ids=id.split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("activity_requirement_detail", ids[i]);
		 }
	   flag=true;
	   return flag;
	}

	@Override
	public List<Record> getActivityPlatFormReleation(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ar.*,(select ae.platform from share_platform ae where ar.platform_id=ae.id) as platform,(select ai.title from activity_info ai where ar.activity_id=ai.id) as activity_title from activity_share_platform ar where  ");
		totalSql.append("select count(1) as total from activity_share_platform ar where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		intFilter.put("ar.activity_id", paramMap.get("activity_id"));
		sql.append(Util.getFilter(strFilter, intFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			recordList.get(0).set("total", total);
			
		}
		return recordList;
	}

	@Override
	public boolean addActivityPlatForm(Map<String, String[]> param) {
		boolean flag=false;
		String activity_id=param.get("activity_id")[0];
		String platform_id=param.get("platform_id")[0];
		String[] enIds=platform_id.split(",");
		Record re=null;
		List<Record> lists=null;
		String sql="select id from activity_share_platform where activity_id=? and platform_id=?";
		for(int i=0;i<enIds.length;i++){
			lists=Db.find(sql,activity_id,enIds[i]);
			if(lists!=null&&lists.size()>0){
				continue;
			}
			re=new Record();
			re.set("activity_id", activity_id);
			re.set("platform_id", enIds[i]);
			Db.save("activity_share_platform", re);
			flag=true;
		}
		return flag;
	}

	@Override
	public List<Record> getActivityPlatForm(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ar.* from share_platform ar where  ");
		totalSql.append("select count(1) as total from share_platform ar where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		
		sql.append(Util.getFilter(strFilter, intFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			recordList.get(0).set("total", total);
			
		}
		return recordList;
	}

	@Override
	public Boolean deleteActivityPlatForm(String id) {
		String[] ids=id.split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("activity_share_platform", ids[i]);
		 }
	   flag=true;
	   return flag;
	}

	@Override
	public boolean saveRecommendService(Map<String, String[]> param) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		List<Record> lists=null;
		String title=param.get("title")[0];
		String service_id=param.get("service_id")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0":param.get("rank")[0];
		String sql="select id from recommend_order_service where service_id=?";
		
		boolean flag = false;
		Record re=null;
		if(isUpdate){
			// 更新
			lists=Db.find(sql,service_id);
			if(lists!=null&&lists.size()>0){
				if(id.equals(lists.get(0).getInt("id").toString())){
					return flag;
				}
			}
			
			re=	Db.findById("recommend_order_service", id).set("title", title).set("service_id", service_id)
			      .set("is_pub", 0).set("rank", rank);
			Db.update("recommend_order_service", re);
		}else{
			// 新增
			lists=Db.find(sql,service_id);
			if(lists!=null&&lists.size()>0){
				return flag;
			}
			
			re=new Record();
			re.set("title", title).set("service_id", service_id)
		      .set("is_pub", 0).set("rank", rank);
			Db.save("recommend_order_service", re);
		}
		flag = true;
		
		return flag;
	
	}

	@Override
	public Boolean deleteRecommendService(String id) {
		String[] ids=id.split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("recommend_order_service", ids[i]);
		 }
	   flag=true;
	   return flag;
	}

	@Override
	public List<Record> getRecommendService(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select ro.*,st.service_type_name as serviceTypeName from recommend_order_service ro inner join service_type st on st.id=ro.service_id where  ");
		totalSql.append("select count(1) as total from recommend_order_service ro inner join service_type st on st.id=ro.service_id where ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strFilter.put("st.service_type_name", paramMap.get("serviceTypeName"));
		strLikeFilter.put("ro.title", paramMap.get("title"));
		intFilter.put("ro.is_pub", paramMap.get("is_pub"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strFilter));
		
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<Record> recordList=Db.find(sql.toString());
		if(recordList!=null&&recordList.size()>0){
			recordList.get(0).set("total", total);
		}
		return recordList;
	}

	@Override
	public int startOrstopRecommendSer(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String status=param.get("status")[0];
		int flag = 0;
		Record re=Db.findById("recommend_order_service", id);
		re.set("is_pub", status);
		Db.update("recommend_order_service", re);
		if(status.equals("1")){
			flag=1;
		}else{
			flag=2;
		}
		return flag;
	}
}
