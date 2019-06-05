package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemOperateLog;

public interface SystemOperateLogDao {
	
	List<SystemOperateLog> systemLogList(Map<String, String[]> param);
	
	Boolean addOperateLog(Map<String, String> param);
	
	Boolean deleteSystemLog(String ids);
}
