package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 定时调度DAO
 * 以后新增的定时调度的处理记录都可以统一使用该Dao
 * @author Revoke
 * @version 2016-9-19
 *
 */
public interface IScheduleDao {

	//迁移用户侧订单信息KEY
	public static String  MIGRATE_USER_ORDER ="MIGRATE_USER_ORDER";
	
	//分类 : 用户取消订单
	public static String  MIGRATE_USER_ORDER_CANCEL="CANCEL";
	
	
	//分类 : 无报价方案订单
	public static String  MIGRATE_USER_ORDER_NOBID="NOBID";
	
	//分类 : 用户未选择报价方案的订单
	public static String  MIGRATE_USER_ORDER_NOCHOOSED="NOCHOOSED";
	
	//分类 : 正常订单
	public static String  MIGRATE_USER_ORDER_NORMAL="NORMAL";
	
	
	/**
	 * 新增一条调度记录
	 */
	public void insertScheduleRec(Map<String,Object> scheduleVO);
	
	
	/**
	 * 获取某种调度任务最后一次处理记录
	 * @param scheduleType
	 * @return
	 */
	public Map<String,Object> getLastScheduleRec(Map<String,String> param);
	
	
	
	/**
	 * 从调度表中获取已迁移到mysql，但未清理的最早的迁移记录
	 * @param param
	 * @return
	 */
	public Map<String,Object> getLastMigrateMysqlRec(Map<String,String> param);

	
	/**
	 * 更新某同步记录的清理标志位1  已清理
	 * @param id
	 */
	public void  updateRecToPurity(Long id);
	
}