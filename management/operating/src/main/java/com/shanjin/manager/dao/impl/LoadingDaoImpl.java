package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jfinal.plugin.activerecord.Db;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.Loading;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.LoadingDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class LoadingDaoImpl implements LoadingDao{
	@Resource
	private ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	
	@Override
	public List<Loading> getLoadingList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		List<Loading> loadingList=new ArrayList<Loading>();

		sql.append(" SELECT ml.* from manager_loading ml ");

		sql.append(" WHERE ml.is_del=0  ");


		if(StringUtil.isNotNullMap(param,"title")){
			String title = StringUtil.null2Str(param.get("title")[0]);
			sql.append(" and ml.title like '%").append(title).append("%'");
		}


		if(StringUtil.isNotNullMap(param,"is_pub")){
			int is_pub = StringUtil.nullToInteger(param.get("is_pub")[0]);
			sql.append(" and ml.is_pub =").append(is_pub);
		}
		if(StringUtil.isNotNullMap(param,"time_status")){
  			int time_status = StringUtil.nullToInteger(param.get("time_status")[0]);
  	        if(time_status==1){
  	        	//使用中
  				sql.append(" and NOW() BETWEEN ml.start_time AND ml.end_time ");
  	        }else if(time_status==2){
  	        	//未开始
  	      	    sql.append(" and NOW()< ml.start_time ");
  	        }else if(time_status==3){
  	        	//已过期
  	  	      	sql.append(" and NOW()> ml.end_time ");
  	        }

		}
		long total=Loading.dao.find(sql.toString()).size();
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
		loadingList=Loading.dao.find(sql.toString());
		if(loadingList.size()>0){
			loadingList.get(0).setTotal(total);
			String pic = "";
			for(Loading s : loadingList){
				pic = s.getStr("image");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("image", pic);
				}
			}
		}
		return loadingList;
	}

	@Override
	public boolean saveLoading(Map<String, String[]> param, String path) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		
		String title=param.get("title")[0];
		int show_type = StringUtil.nullToInteger(param.get("show_type")[0]);
		String start_time = param.get("start_time")[0];
		String end_time = param.get("end_time")[0];
		String link = param.get("link")[0];
		
		if(StringUtil.isNullStr(path)){
			// 未上传图片
			if(StringUtil.isNotNullMap(param, "image")){
				path = param.get("image")[0];
				path = BusinessUtil.getFilePath(path);
			}
		}
		boolean flag = false;
		if(isUpdate){
			// 更新
			 Loading.dao.findById(id).set("title", title)
			.set("show_type", show_type).set("image", path)
			.set("start_time", start_time).set("end_time", end_time).set("link",link).update();
		}else{
			// 新增
			Loading loading=new Loading();
			loading.set("title", title).set("image", path)
			.set("show_type", show_type).set("create_time", new Date())
			.set("start_time", start_time).set("end_time", end_time).set("link",link)
			.save();	
		}
		flag = true;
		return flag;
	}

	@Override
	public boolean deleteLoading(String ids) {
		Boolean flag = false;
		String sql = "update manager_loading set is_del=1 where id IN ("+ids+")";
		Db.update(sql);
		commonCacheService.deleteObject(CacheConstants.LOADING);
		flag = true;
		return flag;
	}

	@Override
	public boolean updatePublishStatus(String ids, int status) {
		Boolean flag = false;
		//刷新缓存
		String sql = "update manager_loading set is_pub="+status+" where id IN ("+ids+")";
		Db.update(sql);
		commonCacheService.deleteObject(CacheConstants.LOADING);
		flag = true;
		return flag;
	}

}
