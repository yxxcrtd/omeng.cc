package com.shanjin.log.analysis.service;

import java.util.Date;

/**
 * 将服务器上的访问日志导入到数据库record_visit_log表中
 * @author Huang yulai
 *
 */
public interface VisitLogImportService {

	/**
	 * 导入访问记录log文件到DB
	 * @param logDate  要处理的Log记录的日期
	 */
	public void importVisitLog(Date logDate);
	
}
