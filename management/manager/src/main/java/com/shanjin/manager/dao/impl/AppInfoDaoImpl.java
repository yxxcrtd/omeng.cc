package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.AppInfoDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class AppInfoDaoImpl implements AppInfoDao{

	public List<AppInfo> appList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		List<AppInfo> appList=new ArrayList<AppInfo>();
		sql.append("SELECT * from merchant_app_info t where t.is_del<>1 ");
		String app_name = "";
		if(StringUtil.isNotNullMap(param,"app_name")){
			app_name = StringUtil.null2Str(param.get("app_name")[0]);
			sql.append(" and t.app_name like '%").append(app_name).append("%'");
		}
		String app_type = "";
		if(StringUtil.isNotNullMap(param,"app_type")){
			app_type = StringUtil.null2Str(param.get("app_type")[0]);
			sql.append(" and t.app_type like '%").append(app_type).append("%'");
		}
		long total=AppInfo.dao.find(sql.toString()).size();
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
		appList=AppInfo.dao.find(sql.toString());
		if(appList.size()>0){
			appList.get(0).setTotal(total);	
		}
		return appList;
	}

	public Boolean deleteApp(String ids) {
		Boolean flag = false;
		String sql = "update app_info set is_del=1 where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	/**
	 * 0:失败；1：成功；2：名称已存在；3：类型已经存在
	 */
	public int saveApp(Map<String, String[]> param) {
		boolean isUpdate = false; // 标识是否是更新
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String app_name = "";
		if(StringUtil.isNotNullMap(param,"app_name")){
			app_name = StringUtil.null2Str(param.get("app_name")[0]);
		}
		String app_remark="";
		if(StringUtil.isNotNullMap(param,"app_remark")){
			app_remark = StringUtil.null2Str(param.get("app_remark")[0]);
		}
		String app_type="";
		if(StringUtil.isNotNullMap(param,"app_type")){
			app_type = StringUtil.null2Str(param.get("app_type")[0]);
		}

		int flag = 0;
		if(checkName(app_name,id)){
			flag = 2;
			return flag;
		}
		
		if(checkType(app_type,id)){
			flag = 3;
			return flag;
		}
		
		if(isUpdate){
			AppInfo.dao.findById(id).set("app_name", app_name).set("app_remark", app_remark).set("app_type", app_type).update();
			flag = 1;
		}else{
			AppInfo sr = new AppInfo();
			sr.set("app_name", app_name).set("app_remark", app_remark).set("app_type", app_type).save();
			flag = 1;
		}
		return flag;
	}
	
	/**
	 * 检查名称是否存在
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean checkName(String name,Long id){
		boolean flag = false;
		List<AppInfo> appList=new ArrayList<AppInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from merchant_app_info t where  t.app_name='").append(name).append("'");
		if(id!=null&&id.longValue()!=0){
			// 更新
		    sql.append(" and t.id<>").append(id);	
		}
		appList=AppInfo.dao.find(sql.toString());
		if(appList.size()>0){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 检查了类型是否存在
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean checkType(String type,Long id){
		boolean flag = false;
		List<AppInfo> appList=new ArrayList<AppInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from merchant_app_info t where  t.app_type='").append(type).append("'");
		if(id!=null&&id.longValue()!=0){
			// 更新
		    sql.append(" and t.id<>").append(id);	
		}
		appList=AppInfo.dao.find(sql.toString());
		if(appList.size()>0){
			flag = true;
		}
		return flag;
	}

	

}
