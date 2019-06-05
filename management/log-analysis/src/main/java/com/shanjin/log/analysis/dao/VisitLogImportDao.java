package com.shanjin.log.analysis.dao;

import java.util.Date;

public interface VisitLogImportDao {
	/**
	 * 导入访问记录log文件到DB
	 * @param logDate  要处理的Log记录的日期
	 */
	public void importVisitLog(Date logDate);
}
