package com.shanjin.log.analysis;

public class Constant {
	/**
	 * 访问日志文件所在的文件夹数组
	 */
	public static String[] VISIT_LOG_DIRS;
	
	public static final String ENCODING = "UTF-8";
	
	public static final int BATCH_NUM = 10000; // 日志导入，批量提交
	
	public static final String VISIT_LOG_TABLE_NAME = "record_visit_log"; //访问日志数据库表名
	
	public static class DB{
		
		public static String DRIVERCLASS = "";
		
		public static String URL = "";
		
		public static String USERNAME = "";
		
		public static String PASSWORD = "";
		
	}
}
