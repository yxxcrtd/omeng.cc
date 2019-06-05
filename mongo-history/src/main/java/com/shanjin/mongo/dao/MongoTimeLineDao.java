package com.shanjin.mongo.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;

@Repository
public class MongoTimeLineDao implements IMongoTimeLineDao {
	private static final String  TIMELINE="timeline";
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	
	public List<Map<String,Object>> getTimeLine(Long orderId){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		DBObject query= new BasicDBObject();
		query.put("orderId", orderId);
		
		DBObject orderby =new BasicDBObject();
		orderby.put("_id", 1);
		
		DBObject projection =new BasicDBObject();
		projection.put("merchantId", 1);
		projection.put("actionCode", 1);
		projection.put("actionTime", 1);
		projection.put("_id", -1);
		
		DBCollection collection=mongoTemplate.getCollection(TIMELINE);
		List<DBObject> timelines=collection.find(query,projection).sort(orderby).limit(Short.MAX_VALUE).toArray();
		for (DBObject item:timelines){
			 item.put("actionTime", DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN,new Date((Long)item.get("actionTime"))));
			 result.add(item.toMap());
		}
		return result;
	}

}
