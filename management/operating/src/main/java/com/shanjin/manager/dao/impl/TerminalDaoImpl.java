package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.AppUpdate;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.TerminalDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class TerminalDaoImpl implements TerminalDao {
	@Resource
	private ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	
	public List<AppUpdate> getClientVersionList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		List<AppUpdate> versionList=new ArrayList<AppUpdate>();
		sql.append(" SELECT au.* from app_update au ");
		sql.append(" WHERE au.is_del=0  ");
		String package_name = "";
		if(StringUtil.isNotNullMap(param,"package_name")){
			package_name = StringUtil.null2Str(param.get("package_name")[0]);
			sql.append(" and au.package_name like '%").append(package_name).append("%'");
		}
		int package_type = 0;
		if(StringUtil.isNotNullMap(param,"package_type")){
			package_type = StringUtil.nullToInteger(param.get("package_type")[0]);
			if(package_type!=0){
				sql.append("  and au.package_type=").append(package_type);
			}
		}
		int publish_status = 0;
		if(StringUtil.isNotNullMap(param,"publish_status")){
			publish_status = StringUtil.nullToInteger(param.get("publish_status")[0]);
			if(publish_status!=0){
				sql.append("  and au.publish_status=").append(publish_status);
			}
		}
		String app_type = "";
		if(StringUtil.isNotNullMap(param,"app_type")){
			app_type = StringUtil.null2Str(param.get("app_type")[0]);
			sql.append("  and au.app_type='").append(app_type).append("'");
		}
		long total=AppUpdate.dao.find(sql.toString()).size();
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
		versionList=AppUpdate.dao.find(sql.toString());
		if(versionList.size()>0){
			versionList.get(0).setTotal(total);	
		}
		return versionList;
	}

	public boolean saveClientVersion(Map<String, String[]> param, String downloadUrl) {
		Long id = 0L;
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String package_name=param.get("package_name")[0];
		String app_type=param.get("app_type")[0];
		String version=param.get("version")[0];
		int package_type = StringUtil.nullToInteger(param.get("package_type")[0]);
		int update_type = StringUtil.nullToInteger(param.get("update_type")[0]);
		int publish_status = StringUtil.nullToInteger(param.get("publish_status")[0]);
		//String channel=param.get("channel")[0];
		String download_url=param.get("download_url")[0];
		String detail=param.get("detail")[0];
		
		if(!StringUtil.isNullStr(downloadUrl)){
			// 上传了安装包，则更新下载地址
			download_url = BusinessUtil.getFileUrl(downloadUrl);;
		}
		boolean flag = false;
		if(isUpdate){
			// 更新
			AppUpdate.dao.findById(id).set("package_name", package_name).set("app_type", app_type)
			.set("version", version).set("package_type", package_type)
			.set("update_type", update_type).set("publish_status", publish_status).set("channel","")
			.set("download_url", download_url).set("detail", detail).set("is_del", 0).update();
		}else{
			// 新增
			AppUpdate clientVersion=new AppUpdate();
			clientVersion.set("package_name", package_name).set("app_type", app_type)
					.set("version", version).set("package_type", package_type)
					.set("update_type", update_type).set("publish_status", publish_status).set("channel","")
					.set("download_url", download_url).set("detail", detail).set("is_del", 0)
					.save();	
		}
		flag =true;
		return flag;
	}

	public boolean deleteClientVersion(String ids) {
		Boolean flag = false;
		String sql = "update app_update set is_del=1 where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	
	public boolean updatePublishStatus(String ids, int status) {
		Boolean flag = false;
		//刷新缓存
		String sql = "update app_update set publish_status="+status+" where id IN ("+ids+")";
		Db.update(sql);
		commonCacheService.deleteObject(CacheConstants.UPDATE_CLIENT_VERSION);
		//  查询已经发布的客户端版本
		List<Record> publishList= Db.find("SELECT * FROM app_update au WHERE au.publish_status=1 AND au.is_del=0");
		if(publishList!=null&&publishList.size()>0){
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			for(Record r : publishList){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", r.getLong("id"));
				map.put("packageName", r.getStr("package_name"));
				map.put("packageType", r.getInt("package_type"));
				map.put("updateType", r.getInt("update_type"));
				map.put("appType", r.getStr("app_type"));
				map.put("version", r.getStr("version"));
				map.put("downloadUrl", r.getStr("download_url"));
				map.put("detail", r.getStr("detail"));
				mapList.add(map);
			}
			commonCacheService.setObject(mapList, CacheConstants.UPDATE_CLIENT_VERSION);
		}
		flag = true;
		return flag;
	}

}
