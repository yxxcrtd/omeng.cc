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
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;


@Repository
public class MongoMerchantPlanDao implements IMongoMerchantPlanDao {
	private static final String  MERCHANTPLAN_COLLECTION="merchantPlan";
	
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public JSONObject getMerchantPlanList(Long orderId, int pageNo,
			String orderConditon, String direction) {
		JSONObject   result = new ResultJSONObject();
		
		DBObject query= new BasicDBObject();
		query.put("orderId", orderId);
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		DBCollection collection=mongoTemplate.getCollection(MERCHANTPLAN_COLLECTION);
		Long totalCount = collection.getCount(query);
		Long  totalPage = (totalCount + Constant.PAGESIZE - 1)/Constant.PAGESIZE;
		
		result.put("resultCode", "000");
		result.put("message", "获取历史报价方案列表成功");
		result.put("merchantPlans", resultList);
		result.put("totalPage", totalPage);
		
		if (pageNo>=0 && pageNo<totalPage) {
			int startRecNo = pageNo*Constant.PAGESIZE;
			
			DBObject orderBy = new BasicDBObject();
			int direct=1;
			if (direction!=null && direction!=""){
		            direct=-1;
		    }
		    			
			switch (orderConditon) {
	            case "1": // 1的场合按优惠价格升序排列
	                orderBy.put("discountPrice",direct);
	                break;
	            case "2": // 2的场合按距离升序排列
	            	  orderBy.put("distance",direct);
	                break;
	            case "3": // 3按加入时间排序
	            	 orderBy.put("joinTime",direct);
	                 break;
	            default: // 默认按增值服务及私人助理等优先级排序
	            	orderBy.put("weight",direct);
	                break;
	        }
	       
			
			
			orderBy.put("_id", -1);
			
			
			List<DBObject>  planList = collection.find(query).sort(orderBy).skip(startRecNo).limit(Constant.PAGESIZE).toArray();
			result.put("merchantPlans",convertPricePlanList(planList));
		}
		result.put("isHistory", 1);
		return result;
	}

	private  List<Map<String,Object>> convertPricePlanList(List<DBObject> planList) {
		List<Map<String,Object>>  result = new ArrayList<Map<String,Object>>();
		if (planList!=null && planList.size()>0){
			
				for(DBObject plan:planList){
						Map<String,Object> item=new HashMap<String,Object>();
						item.put("orderPlanGoodsList", convertPath((ArrayList) plan.get("orderPlanGoodsList")));
						item.put("icoPath", BusinessUtil.disposeImagePath(plan.get("iconPath").toString()));
						item.put("vipStatus", plan.get("vipStatus"));
						item.put("merchantPoint", plan.get("merchantPoint"));
						item.put("enterpriseAuth", plan.get("enterpriseAuth"));
						item.put("personalAuth", plan.get("personalAuth"));
						item.put("auth", plan.get("auth"));
						item.put("score", plan.get("score"));
						item.put("merchantName", plan.get("merchantName"));
						item.put("merchantAddress", plan.get("merchantAddress"));
						item.put("distance", plan.get("distance"));
						item.put("isPrivateAssistant", plan.get("isPrivateAssistant"));
						item.put("priceUnit", plan.get("priceUnit"));
						item.put("discountPrice", plan.get("discountPrice"));
						item.put("merchantType", plan.get("merchantType"));
						item.put("deposit", plan.get("deposit"));
						item.put("merchantType", plan.get("merchantType"));
						if(plan.get("picturePath")!=null &&  !plan.get("picturePath").equals("")){
							item.put("picturePath", BusinessUtil.disposeImagePath(plan.get("picturePath").toString()));
						}else{
							item.put("picturePath",null);
						}
						if(plan.get("voicePath")!=null &&  !plan.get("voicePath").equals("")){
							item.put("voicePath", BusinessUtil.disposeImagePath(plan.get("voicePath").toString()));
						}else{
							item.put("voicePath",null);
						}
						item.put("merchantId", plan.get("merchantId"));
						item.put("appType", plan.get("appType"));
						item.put("promise", plan.get("promise"));
						item.put("planId", plan.get("_id"));
						item.put("receiveEmployeesId", plan.get("receiveEmployeesId"));
						item.put("content", plan.get("content"));
						item.put("totalCount", plan.get("totalCount"));
						item.put("telephone", plan.get("telephone"));
						
						if (plan.containsKey("isOffer")){
							item.put("isOffer", plan.get("isOffer"));
						}else{
							item.put("isOffer", 1);
						}
					
						item.put("joinTime", DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN,new Date((Long)plan.get("joinTime"))));
						result.add(item);
				}
		}
		return result;
	}

	private List convertPath(ArrayList<DBObject> goodsList) {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		if (goodsList!=null) {
			for(DBObject goods:goodsList){
				Map item=goods.toMap();
				BusinessUtil.disposePath(item, "goodsPictureUrl");
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public Map<String, Object> getMerchantServiceRecord(Long orderId,
			Long merchantId) {
		  DBObject query = new BasicDBObject();
		  query.put("orderId", orderId);
		  
		  DBCollection collection=mongoTemplate.getCollection(MERCHANTPLAN_COLLECTION);
		  return collection.findOne(query).toMap();
		  
	}

	@Override
	public Map<String, Object> getMerchantPlan(Long orderId, Long merchantId) {
		DBObject query = new BasicDBObject();
		  query.put("orderId", orderId);
		  query.put("merchantId", merchantId);
		  
		  DBCollection collection=mongoTemplate.getCollection(MERCHANTPLAN_COLLECTION);
		  return collection.findOne(query).toMap();
	}
}
