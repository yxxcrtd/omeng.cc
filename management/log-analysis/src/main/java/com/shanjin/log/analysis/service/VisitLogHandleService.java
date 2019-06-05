package com.shanjin.log.analysis.service;

import java.util.Date;

/**
 * 日志文件处理接口
 * @author Huang yulai
 *
 */
public interface VisitLogHandleService {

	/**
	 * 批量插入终端数据，每日从访问日志表中提取，
	 * 分别从不同的维度生成统计数据（clientFlag,clientType,province,city,channel）
	 * @param logDate
	 */
	public void  insertTerminalStatistics(Date logDate);
	
	/**
	 * 批量插入每日新增的终端数据，从访问日志表中提取，
	 * 同一个终端若同时安装了o盟用户端和商户端，那么保存两条数据
	 * 数据插入record_first_visit_terminal表中，即o盟平台的所有终端用户记录
	 * @param logDate
	 */
	public void  insertFirstVisitTerminal(Date logDate);
}
