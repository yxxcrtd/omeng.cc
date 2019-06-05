package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 数据迁移到MongoDB 所服务记录
 * @author Revoke
 *
 */
public interface IServiceRecordDao {

		/**
		 * 获取已完成订单对应的服务记录
		 * @param ids
		 * @return
		 */
		List<Map<String,Object>> getServiceRecordsByIds(Map<String,Object> ids);
		
		
		/**
		 * 获取已完成订单对应的服务记录附件
		 * @param ids
		 * @return
		 */
		List<Map<String,Object>> getServiceRecordsAttaByIds(Map<String,Object> ids);
		
		
		/**
		 * 按订单ids获取服务记录的ids
		 * @param ids
		 * @return
		 */
		public String getServiceRecordByOrderIds(Map<String,Object> ids);
		
		
		
		
		/**
		 * 按服务记录的ids 删除 服务记录附件表
		 * @param ids
		 */
		public void delServiceRecordAttByIds(Map<String,Object> ids);
		
		
		/**
		 * 按服务记录IDS删除的商户服务记录
		 * @param ids
		 */
		public void delServiceRecordByIds(Map<String,Object> ids);
		
}
