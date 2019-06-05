package com.shanjin.mongo.dao;

import java.util.List;
import java.util.Map;

public interface IMongoDictionary {

		/**
		 * 获取时间轴相关的字典定义
		 * @return
		 */
		public List<Map<String,Object>> getTimeLineDictionary(Map<String,Object> param);
}
