package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 数据迁移到MongoDB 所涉及的字典项
 * @author Revoke
 *
 */
public interface IDictionaryDao {

		/**
		 * 获取订单状态的字典项目
		 * @param statusType
		 * @return
		 */
		List<Map<String,Object>> getOrderStatusFromDictonary(String statusDictionEntry);
		
		/**
		 * 获取订单状态的对应的描述信息
		 * @param statusDictionEntry
		 * @return
		 */
		List<Map<String,Object>> getOrderStatustTextList();
		
}
