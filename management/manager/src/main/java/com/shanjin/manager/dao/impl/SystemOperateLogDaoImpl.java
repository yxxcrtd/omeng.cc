package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.shanjin.manager.Bean.SystemOperateLog;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.SystemOperateLogDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class SystemOperateLogDaoImpl implements SystemOperateLogDao{

	public List<SystemOperateLog> systemLogList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		List<SystemOperateLog> logList = new ArrayList<SystemOperateLog>();
		sql.append("SELECT t.id,t.operate_type,t.source_url,t.source_name,t.operate_user,t.operate_time,t.operate_ip from manager_operate_log t where t.is_del<>1 ");
		String source_name = "";
		if(StringUtil.isNotNullMap(param,"source_name")){
			source_name = StringUtil.null2Str(param.get("source_name")[0]);
			sql.append(" and t.source_name like '%").append(source_name).append("%'");
		}
		String source_url="";
		if(StringUtil.isNotNullMap(param,"source_url")){
			source_url = StringUtil.null2Str(param.get("source_url")[0]);
			sql.append(" and t.source_url like '%").append(source_url).append("%'");
		}
		
		String operate_type = "";
		if(StringUtil.isNotNullMap(param,"operate_type")){
			operate_type = StringUtil.null2Str(param.get("operate_type")[0]);
			sql.append(" and t.operate_type like '%").append(operate_type).append("%'");
		}
		String operate_user="";
		if(StringUtil.isNotNullMap(param,"operate_user")){
			operate_user = StringUtil.null2Str(param.get("operate_user")[0]);
			sql.append(" and t.operate_user like '%").append(operate_user).append("%'");
		}
		//operate_time
		String stime="";
		if(StringUtil.isNotNullMap(param,"stime")){
			stime = StringUtil.null2Str(param.get("stime")[0]);
			sql.append(" and t.operate_time>='").append(stime).append("'");
		}
		String etime="";
		if(StringUtil.isNotNullMap(param,"etime")){
			etime = StringUtil.null2Str(param.get("etime")[0]);
			sql.append(" and t.operate_time<='").append(etime).append("'");
		}
		long total = SystemOperateLog.dao.find(sql.toString()).size();
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
		logList=SystemOperateLog.dao.find(sql.toString());
		if(logList.size()>0){
			logList.get(0).setTotal(total);	
		}
		return logList;
	}

	public Boolean addOperateLog(Map<String, String> param) {

		String source_name = StringUtil.null2Str(param.get("source_name"));
		String source_url = StringUtil.null2Str(param.get("source_url"));
		String operate_type = StringUtil.null2Str(param.get("operate_type"));
		String operate_user = StringUtil.null2Str(param.get("operate_user"));
		String operate_ip = StringUtil.null2Str(param.get("operate_ip"));
		boolean flag = false;
		SystemOperateLog sol = new SystemOperateLog();
		sol.set("source_name", source_name).set("source_url", source_url).set("operate_type", operate_type).set("operate_user", operate_user).set("operate_ip", operate_ip).set("operate_time", new Date()).save();
		flag = true;
		return flag;
	}

	public Boolean deleteSystemLog(String ids) {
		Boolean flag = false;
		String sql = "update manager_operate_log set is_del=1 where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

}
