package com.shanjin.mongo.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 时间轴查询
 * @author Revoke 2016.10.10
 *
 */
public interface IMongoTimeLineDao {
	
	/**
	 * 时间轴查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getTimeLine(Long orderId);

}
