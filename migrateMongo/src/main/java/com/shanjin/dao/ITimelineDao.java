package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface ITimelineDao {

	List<Map<String, Object>> getTimelineList(Map<String, Object> map);

	
	/**
	 * 按订单id 获取 时间轴数据
	 * @param ids
	 * @return
	 */
	List<Map<String,Object>> getTimeLinesByIDS(Map<String,String> param);
}
