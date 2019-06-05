package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 定时调度DAO
 * 以后新增的定时调度的处理记录都可以统一使用该Dao
 * @author Revoke
 * @version 2016-8-6
 *
 */
public interface IScheduleDao {

	//过期报价方案返回抢单金
	public static String  RETURN_BIDFEE_OVERTIME ="RETURN_BIDFEE_OVERTIME";
	
	//分类 : 用户取消报价方案订单
	public static String  PLAN_ORDER_NOCHOOSED_CANCEL="CANCEL";
	
	
	//分类 : 未选择报价方案逾期订单
	public static String  PLAN_ORDER_NOCHOOSED_EXPIRES="NOCHOOSED";
	
	/**
	 * 新增一条调度记录
	 */
	public void insertScheduleRec(Map<String,Object> scheduleVO);
	
	
	/**
	 * 获取某种调度任务最后一次处理记录
	 * @param scheduleType
	 * @return
	 */
	public Map<String,Object> getLastScheduleRec(String scheduleType);
	/**
	 * 获取某种调度任务最后一次处理记录
	 * @param scheduleVO
	 * @return
	 */
	public Map<String,Object> selectLastScheduleRec(Map<String,Object> scheduleVO);

	
}