package com.shanjin.mongo.dao;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class MongoDictionaryDao implements IMongoDictionary {
	private static final String DICTIONARY_COLLECTION="dictionary";
	
	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<Map<String, Object>> getTimeLineDictionary(
			Map<String, Object> param) {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		DBObject query = new BasicDBObject();
		query.put("dict_type", param.get("timelineName"));
		
		query.put("dict_key", new BasicDBObject("$in",Arrays.asList(param.get("excludeCode").toString().split(","))));
		
		DBObject orderBy = new BasicDBObject();
		orderBy.put("sort", 1);
		
		DBCollection collection = mongoTemplate.getCollection(DICTIONARY_COLLECTION);
		
		List<DBObject> dictionary = collection.find(query).sort(orderBy).toArray();
		
		for(DBObject item:dictionary){
				Map<String,Object> dic = new HashMap<String,Object>();
				dic.put("title", item.get("dict_value"));
				dic.put("remark", item.get("remark"));
				dic.put("code", item.get("dict_key"));
				result.add(dic);
		}
		return result;
	}

}
