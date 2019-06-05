package com.shanjin.mongo.dao;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shanjin.common.constant.Constant.PAGESIZE;
import static com.shanjin.common.util.DateUtil.DATE_TIME_INDEX_PLAYBILL_PATTERN;
import static com.shanjin.common.util.DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;
import static com.shanjin.common.util.DateUtil.formatDate;

@Repository
public class OrderDao implements IOrderDao{
	private static final String ORDER_COLLECTION="order";

    private static final String MERCHANT_ORDER = "merchantOrder";

    private static final String MERCHANT_PLAN = "merchantPlan";

    @Autowired
	MongoTemplate mongoTemplate;

	@Override
	public JSONObject getOrderListForSender(Map<String, Object> params) {
		JSONObject   result = new ResultJSONObject();
		
		Long catalogId = params.get("catalogId")==null?null:StringUtil.nullToLong(params.get("catalogId"));
		Long userId = StringUtil.nullToLong(params.get("userId"));
		String orderStatus = (String) params.get("orderStatus");
		int pageNo = StringUtil.nullToInteger(params.get("pageNo"));

		DBObject query= new BasicDBObject();
		query.put("userId", userId);
		
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
	
		
		
		DBCollection collection=mongoTemplate.getCollection(OrderDao.ORDER_COLLECTION);
		Long totalCount = collection.getCount(query);
		Long  totalPage = (totalCount + PAGESIZE - 1)/ PAGESIZE;
		
		result.put("resultCode", "000");
		result.put("isHistory", 1);
		result.put("message", "获取用户订单列表成功");
		result.put("orderList", resultList);
		result.put("totalPage", totalPage);
		
		if (pageNo>=0 && pageNo<totalPage) {
			int startRecNo = pageNo* PAGESIZE;
			DBObject projection = defExtractColForUserOrderList();
			
			DBObject orderBy = new BasicDBObject();
			orderBy.put("_id", -1);
			List<DBObject>  orderList = collection.find(query, projection).sort(orderBy).skip(startRecNo).limit(PAGESIZE).toArray();
			result.put("orderList",convertOrderListForSenderResult(orderList));
		}
		return result;
	}

	
	
	@Override
	public JSONObject getOrderDetailForSender(Long orderId,Long userId) {
		JSONObject result = new ResultJSONObject();
		DBObject query = new  BasicDBObject();
		query.put("_id", orderId);
		DBCollection collection=mongoTemplate.getCollection(OrderDao.ORDER_COLLECTION);
		DBObject queryResult = collection.findOne(query, defExtractColForUserOrderDetail());
		
		if (queryResult==null){
			result=new ResultJSONObject("001", "订单详情加载失败");
		}else {
			convertUserOrderDetailResult(result,queryResult);
			result.put("resultCode", "000");
			result.put("message", "获取用户端订单明细成功");
		}
		
		return result;
	}
	
	

	@Override
	public JSONObject getMerchantPlanDetail(Long merchantPlanId) {
		DBObject query = new  BasicDBObject();
		query.put("_id", merchantPlanId);
		
		return null;
		
	}
	
	


	/**
	 * 设置用户侧订单详情页需要的fields.
	 * @return
	 */
	private DBObject defExtractColForUserOrderDetail() {
		DBObject projection= new BasicDBObject();
		projection.put("orderNo",1);
		projection.put("userId",1);
		projection.put("serviceTypeId",1);
		projection.put("serviceTypeName",1);
		projection.put("orderStatus",1);
		projection.put("orderStatusName",1);
		projection.put("joinTime",1);
		projection.put("serviceTime",1);
		projection.put("orderStatusText",1);
		projection.put("address",1);
		projection.put("evaluate",1);
		projection.put("planCount",1);
		projection.put("orderPayType",1);
		projection.put("orderPrice",1);
		projection.put("priceUnit",1);
		projection.put("merchantActualPrice",1);
		projection.put("orderActualPrice",1);
		projection.put("voucherPrice",1);
		projection.put("merchantName",1);
		projection.put("merchantIcon",1);
		projection.put("merchantId",1);
		projection.put("merchantPlanId",1);
		projection.put("merchantPhone",1);
		projection.put("merchantPlanPrice",1);
		projection.put("receiveEmployeeId",1);	
		projection.put("dealTime",1);
		projection.put("confirmTime",1);
		projection.put("starLevel",1);
		
		
		projection.put("serviceRecord",1);
		
		projection.put("isImmediate",1);
		projection.put("isOffer",1);
		
		
		projection.put("orderAttachment", 1);
		projection.put("orderText", 1);
		projection.put("merchantSummary", 1);
		
		
		return projection;
	}
	
	
	/**
	 * 格式化日期，转换前端约定好的格式等等。
	 * @param result
	 * @param queryResult
	 */
	private void convertUserOrderDetailResult(JSONObject result,
			DBObject queryResult) {
	
		Map<String,Object> orderInfo= new HashMap<String,Object>();
		orderInfo.put("orderId", queryResult.get("_id"));
		orderInfo.put("orderNo", queryResult.get("orderNo"));
		orderInfo.put("userId", queryResult.get("userId"));
		orderInfo.put("merchantId", queryResult.get("merchantId"));
		orderInfo.put("serviceType", queryResult.get("serviceTypeId"));
		orderInfo.put("serviceTypeName", queryResult.get("serviceTypeName"));
		orderInfo.put("orderStatus", queryResult.get("orderStatus"));
		orderInfo.put("merchantActualPrice", queryResult.get("merchantActualPrice"));
		orderInfo.put("joinTime", formatDate(DateUtil.DATE_TIME_PATTERN,new Date((Long)queryResult.get("joinTime"))));
		orderInfo.put("evaluate", queryResult.get("evaluate"));
		orderInfo.put("serviceTime", formatDate(DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,new Date((Long)queryResult.get("serviceTime"))));
		orderInfo.put("address", queryResult.get("address"));
		orderInfo.put("voucherPrice", queryResult.get("voucherPrice"));
		orderInfo.put("orderActualPrice", queryResult.get("orderActualPrice"));
		orderInfo.put("priceUnit", queryResult.get("priceUnit"));
		orderInfo.put("merchantPlanId", queryResult.get("merchantPlanId"));
		orderInfo.put("orderPayType", queryResult.get("orderPayType"));
		orderInfo.put("orderPrice", queryResult.get("orderPrice"));
		orderInfo.put("planCount", queryResult.get("planCount"));
		orderInfo.put("merchantName", queryResult.get("merchantName"));
		
		//lack serviceRecordCount
		
		orderInfo.put("orderStatusName", queryResult.get("orderStatusName"));
		orderInfo.put("receiveEmployeesId", queryResult.get("receiveEmployeeId"));
		
		
		if (queryResult.containsKey("isImmediate")){
			orderInfo.put("isImmediate", queryResult.get("isImmediate"));
		}else{
			orderInfo.put("isImmediate", 0);
		}
		
		if (queryResult.containsKey("isOffer")){
			orderInfo.put("isOffer", queryResult.get("isOffer"));
		}else{
			orderInfo.put("isOffer", 1);
		}
		
		
		
		if (queryResult.get("serviceRecord")==null){
				orderInfo.put("serviceRecordCount", 0);
		}

		result.put("orderInfo", orderInfo);
		
		
		Map<String,Object> orderAttachment= new HashMap<String,Object>(2);
		if (queryResult.containsKey("orderAttachment")){
			 DBObject attach =  (DBObject) queryResult.get("orderAttachment");
			 orderAttachment.put("picturePath", fmtAttachArray(attach.get("picturePath").toString()));
			 orderAttachment.put("voicePath", fmtAttachArray(attach.get("voicePath").toString()));
		}
		
		result.put("orderAttachment", orderAttachment);
		
		result.put("orderStatusText", queryResult.get("orderStatusText"));
		
		
		result.put("orderText", ((BasicDBList)queryResult.get("orderText")).toArray());
		
		Map<String,Object> merchantInfo =null;
		DBObject merchantSummary =  (DBObject) queryResult.get("merchantSummary");
		
		
		if (merchantSummary!=null) {
			 merchantInfo=new HashMap<String,Object>();
			merchantInfo.put("phone",merchantSummary.get("phone"));
			merchantInfo.put("iconUrl",BusinessUtil.disposeImagePath(merchantSummary.get("iconUrl").toString()));
			merchantInfo.put("name",merchantSummary.get("merchantName"));
			merchantInfo.put("locationAddress",merchantSummary.get("locationAddress"));
			merchantInfo.put("score",merchantSummary.get("score"));
			merchantInfo.put("id",merchantSummary.get("merchantId"));
			merchantInfo.put("vipStatus",merchantSummary.get("vipStatus"));
			merchantInfo.put("appType", merchantSummary.get("appType"));
			merchantInfo.put("distance", merchantSummary.get("distance"));
			merchantInfo.put("starLevel", merchantSummary.get("starLevel"));
			merchantInfo.put("auth", merchantSummary.get("auth"));
			merchantInfo.put("authStatus", merchantSummary.get("authStatus"));
			merchantInfo.put("merchantType", merchantSummary.get("merchantType"));
		}
		result.put("merchantInfo", merchantInfo);
		result.put("isHistory", 1);
	}
	
	
	
	
	/**
	 * 设置用户侧订单列表页需要的field.
	 * @return
	 */
	private DBObject defExtractColForUserOrderList() {
		DBObject projection= new BasicDBObject();
		projection.put("userId",1);
		projection.put("serviceTypeId",1);
		projection.put("serviceTypeName",1);
		projection.put("orderStatus",1);
		projection.put("orderStatusName",1);
		projection.put("joinTime",1);
		projection.put("serviceTime",1);
		projection.put("orderStatusText",1);
		projection.put("address",1);
		projection.put("evaluate",1);
		projection.put("planCount",1);
		projection.put("orderPayType",1);
		projection.put("orderPrice",1);
		projection.put("priceUnit",1);
		projection.put("merchantActualPrice",1);
		projection.put("orderActualPrice",1);
		projection.put("merchantId",1);
		projection.put("merchantPlanId",1);
		projection.put("merchantPlanPrice",1);	
		projection.put("dealTime",1);
		projection.put("confirmTime",1);
		projection.put("isImmediate",1);
		projection.put("isOffer",1);
		
		projection.put("merchantSummary",1);
		
		return projection;
	}
	
	
	/**
	 * 转换日期格式，key值等
	 */
	private List<Map<String,Object>> convertOrderListForSenderResult(List<DBObject> orderList) {
			List<Map<String,Object>> result = new ArrayList(orderList.size());
			
			for(DBObject order:orderList){
					Map<String,Object> item= new HashMap<String,Object>();
					item.put("evaluate", order.get("evaluate"));
					item.put("orderStatusName", order.get("orderStatusName"));
					item.put("serviceType", order.get("serviceTypeId"));
					item.put("orderStatus", order.get("orderStatus"));
					item.put("priceUnit", order.get("priceUnit"));
					item.put("serviceTypeName", order.get("serviceTypeName"));
					item.put("joinTime", formatDate(DateUtil.DATE_TIME_PATTERN,new Date((Long)order.get("joinTime"))));
					item.put("address", order.get("address"));
					item.put("serviceTime", formatDate(DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,new Date((Long)order.get("serviceTime"))));
					item.put("userId", order.get("userId"));
					item.put("planCount", order.get("planCount"));
					item.put("orderId", order.get("_id"));
					item.put("orderPayType", order.get("orderPayType"));
					item.put("orderPrice", order.get("orderPrice"));
					item.put("merchantPlanPrice", order.get("merchantPlanPrice"));
					item.put("merchantActualPrice", order.get("merchantActualPrice"));
					item.put("orderActualPrice", order.get("orderActualPrice"));
					item.put("orderStatusText", order.get("orderStatusText"));
					
					item.put("merchantName", order.get("merchantName"));
					if(order.get("merchantIcon")!=null){
						item.put("merchantIcon", BusinessUtil.disposeImagePath(StringUtil.null2Str(order.get("merchantIcon"))));
					}
					item.put("merchantId", order.get("merchantId"));
					item.put("merchantPlanId", order.get("merchantPlanId"));
					item.put("merchantPhone", order.get("merchantPhone"));
					
		
					item.put("dealTime", order.get("dealTime"));
					item.put("confirmTime", order.get("confirmTime"));
					
					if(order.containsKey("isImmediate")) {
						item.put("isImmediate", order.get("isImmediate"));
					}else{
						item.put("isImmediate",0);
					}
					if (order.containsKey("isOffer")){
						item.put("isOffer", order.get("isOffer"));
					}else{
						item.put("isOffer", 1);
					}	
					
					
					DBObject merchantSummary =  (DBObject) order.get("merchantSummary");
					
					
					if (merchantSummary!=null) {
						item.put("merchantPhone",merchantSummary.get("phone"));
						item.put("merchantIcon",BusinessUtil.disposeImagePath(merchantSummary.get("iconUrl").toString()));
						item.put("merchantName",merchantSummary.get("merchantName"));
					}
					
					result.add(item);
			}
			
			return result;
	}

	
	private String fmtAttachArray(String attaches){	
			StringBuffer result =new StringBuffer();
			if (attaches!=null && attaches.length()>0) {
					String[] attachArray = attaches.split(",");
					for(int i=0;i<attachArray.length;i++){
							result.append(BusinessUtil.disposeImagePath(attachArray[i]));
							result.append(",");
					}
					result.deleteCharAt(result.length()-1);
			}
			return result.toString();
	}



	@Override
	public Map<String, Object> getOriginalOrderDetail(Long userId, Long orderId) {
		JSONObject result = new ResultJSONObject();
		DBObject query = new  BasicDBObject();
//		query.put("userId", userId);
		query.put("_id", orderId);
		
		DBCollection collection=mongoTemplate.getCollection(OrderDao.ORDER_COLLECTION);
		
		return collection.findOne(query).toMap();
	}


    @Override
	public boolean hasUserOrder(Map<String, Object> params) {
		Long catalogId = params.get("catalogId")==null?null:StringUtil.nullToLong(params.get("catalogId"));
		Long userId = StringUtil.nullToLong(params.get("userId"));
		
		DBObject query= new BasicDBObject();
		query.put("userId", userId);
		
		DBCollection collection=mongoTemplate.getCollection(OrderDao.ORDER_COLLECTION);
		List<DBObject> order=collection.find(query).limit(1).toArray();
		return order.size()>0?Boolean.TRUE:Boolean.FALSE;
	}

    public JSONObject getOrderListForReceiver(Map<String, Object> map) {
        JSONObject jsonObject = new ResultJSONObject();
        String lastOrderId = StringUtil.null2Str(map.get("lastOrderId"));
        DBCollection collection = getMongodbCollection(MERCHANT_ORDER);
        BasicDBObject condition = new BasicDBObject();
        condition.put("merchantId", StringUtil.nullToLong(map.get("merchantId")));
        if (StringUtils.isNotBlank(lastOrderId)) {
            condition.put("orderId", new BasicDBObject("$lt", Long.parseLong(lastOrderId)));
        }
        BasicDBObject sort = new BasicDBObject();
        sort.put("orderId", -1);
        List<DBObject> orderList = collection.find(condition, setMerchantOrder()).sort(sort).limit(PAGESIZE).toArray();
        long count = collection.getCount(condition);
        if (12 > orderList.size() || 0 == count) {
            jsonObject.put("hasHistory", 0);
        } else {
            jsonObject.put("hasHistory", 1);
        }
        jsonObject.put("orderList", convertList(orderList));
        jsonObject.put("resultCode", "000");
        jsonObject.put("000", "商户历史订单查询成功");
        System.out.println(jsonObject);
        return jsonObject;
    }

    public JSONObject getOrderDetailForReceiver(String orderId, Long userId) {
        JSONObject jsonObject = new ResultJSONObject();
        BasicDBObject condition = new BasicDBObject();
        condition.put("_id", Long.parseLong(orderId));
        DBCollection collection = getMongodbCollection(ORDER_COLLECTION);
        DBObject detail = collection.findOne(condition, setMerchantOrder());
        if (null == detail) {
            jsonObject.put("999", "没有获取到数据");
        } else {
            convertMerchantOrderDetailResult(jsonObject, detail, orderId);
            getMerchantOrderStatus(orderId);
            jsonObject.put("resultCode", "000");
            jsonObject.put("000", "历史订单详情查询成功");
        }
        return jsonObject;
    }

    private BasicDBObject setMerchantOrder() {
        BasicDBObject object = new BasicDBObject();
        object.put("orderId", 1);
        object.put("orderNo", 1);
        object.put("userOrderStatus", 1);
        object.put("userId", 1);
        object.put("serviceTypeId", 1);
        object.put("serviceTypeName", 1);
        object.put("userPhone", 1);
        object.put("userPortraitUrl", 1);
        object.put("planCount", 1);
        object.put("evaluate", 1);
        object.put("serviceTime", 1);
        object.put("orderPrice", 1);
        object.put("merchantActualPrice", 1);
        object.put("priceUnit", 1);
        object.put("orderPayType", 1);
        object.put("deal_time", 1);
        object.put("longitude", 1);
        object.put("latitude", 1);
        object.put("address", 1);
        object.put("merchantId", 1);
        object.put("orderStatus", 1);
        object.put("merchantPlanId", 1);
        object.put("orderStatusText", 1);
        object.put("orderStatusName", 1);
        object.put("orderText", 1);
        object.put("joinTime", 1);
        object.put("userIcon", 1);
        object.put("merchantPlanPrice", 1);
        object.put("orderActualPrice", 1);
        return object;
    }

    private List<Map<String, Object>> convertList(List<DBObject> orderList) {
        List<Map<String, Object>> list = new ArrayList(orderList.size());
        for (DBObject order : orderList) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", order.get("orderId"));
            map.put("userOrderStatus", order.get("userOrderStatus"));
            map.put("userId", order.get("userId"));
            map.put("serviceTypeId", order.get("serviceTypeId"));
            map.put("serviceTypeName", order.get("serviceTypeName"));
            map.put("userPhone", order.get("userPhone"));
            if (null != order.get("userPortraitUrl")) {
                map.put("userPortraitUrl", BusinessUtil.disposeImagePath(StringUtil.null2Str(order.get("userPortraitUrl"))));
            }
            map.put("planCount", order.get("planCount"));
            map.put("evaluate", order.get("evaluate"));
            map.put("serviceTime", formatDate(DATE_TIME_INDEX_PLAYBILL_PATTERN, new Date((Long) order.get("serviceTime"))));
            map.put("orderPrice", order.get("orderPrice"));
            map.put("merchantActualPrice", order.get("merchantActualPrice"));
            map.put("priceUnit", order.get("priceUnit"));
            map.put("orderPayType", order.get("orderPayType"));
            map.put("deal_time", order.get("deal_time"));
            map.put("longitude", order.get("longitude"));
            map.put("latitude", order.get("latitude"));
            map.put("address", order.get("address"));
            map.put("merchantId", order.get("merchantId"));
            map.put("orderStatus", order.get("orderStatus"));
            map.put("merchantPlanId", order.get("merchantPlanId"));
            map.put("orderStatusText", order.get("orderStatusText"));
            map.put("orderStatusName", order.get("orderStatusName"));
            map.put("merchantPlanPrice", order.get("merchantPlanPrice"));
            map.put("orderActualPrice", order.get("orderActualPrice"));
            list.add(map);
        }
        return list;
    }

    private DBCollection getMongodbCollection(String collectionName) {
        return mongoTemplate.getCollection(collectionName);
    }

    private void convertMerchantOrderDetailResult(JSONObject result, DBObject detail, String orderId) {
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderId", detail.get("_id"));
        orderInfo.put("orderNo", detail.get("orderNo"));
        orderInfo.put("userId", detail.get("userId"));
        orderInfo.put("merchantId", detail.get("merchantId"));
        orderInfo.put("serviceType", detail.get("serviceTypeId"));
        orderInfo.put("serviceTypeName", detail.get("serviceTypeName"));
        orderInfo.put("merchantOrderStatus", detail.get("orderStatus"));
        orderInfo.put("merchantActualPrice", detail.get("merchantActualPrice"));
        orderInfo.put("joinTime", formatDate(DateUtil.DATE_TIME_PATTERN, new Date((Long) detail.get("joinTime"))));
        orderInfo.put("evaluate", detail.get("evaluate"));
        orderInfo.put("serviceTime", formatDate(DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN, new Date((Long) detail.get("serviceTime"))));
        orderInfo.put("address", detail.get("address"));
        orderInfo.put("voucherPrice", detail.get("voucherPrice"));
        orderInfo.put("orderActualPrice", detail.get("orderActualPrice"));
        orderInfo.put("priceUnit", detail.get("priceUnit"));
        orderInfo.put("merchantPlanId", detail.get("merchantPlanId"));
        orderInfo.put("orderPayType", detail.get("orderPayType"));
        orderInfo.put("orderPrice", detail.get("orderPrice"));
        orderInfo.put("planCount", detail.get("planCount"));
        orderInfo.put("merchantName", detail.get("merchantName"));
        orderInfo.put("orderStatusName", detail.get("orderStatusName"));
        orderInfo.put("receiveEmployeesId", detail.get("receiveEmployeeId"));
        if (detail.get("serviceRecord") == null) {
            orderInfo.put("serviceRecordCount", 0);
        }
        result.put("orderInfo", orderInfo);
        Map<String, Object> orderAttachment = new HashMap<>(2);
        if (detail.containsKey("orderAttachment")) {
            DBObject attach = (DBObject) detail.get("orderAttachment");
            orderAttachment.put("picturePath", fmtAttachArray(attach.get("picturePath").toString()));
            orderAttachment.put("voicePath", fmtAttachArray(attach.get("voicePath").toString()));
        }
        result.put("orderAttachment", orderAttachment);

        result.put("orderStatusText", detail.get("orderStatusText"));
        result.put("orderText", ((BasicDBList) detail.get("orderText")).toArray());

        Map<String, Object> merchantInfo = null;
        DBObject merchantSummary = (DBObject) detail.get("merchantSummary");
        if (merchantSummary != null) {
            merchantInfo = new HashMap<>();
            merchantInfo.put("phone", merchantSummary.get("phone"));
            merchantInfo.put("iconUrl", BusinessUtil.disposeImagePath(merchantSummary.get("iconUrl").toString()));
            merchantInfo.put("name", merchantSummary.get("merchantName"));
            merchantInfo.put("locationAddress", merchantSummary.get("locationAddress"));
            merchantInfo.put("score", merchantSummary.get("score"));
            merchantInfo.put("id", merchantSummary.get("merchantId"));
            merchantInfo.put("vipStatus", merchantSummary.get("vipStatus"));
            merchantInfo.put("appType", merchantSummary.get("appType"));
            merchantInfo.put("distance", merchantSummary.get("distance"));
            merchantInfo.put("starLevel", merchantSummary.get("starLevel"));
            merchantInfo.put("auth", merchantSummary.get("auth"));
            merchantInfo.put("authStatus", merchantSummary.get("authStatus"));
            merchantInfo.put("merchantType", merchantSummary.get("merchantType"));
        }
        result.put("merchantInfo", merchantInfo);
        result.put("isHistory", 1);
        result.put("orderUserInfo", getOrderUserInfo(detail));
        if (null != detail.get("merchantPlanPrice")) {
            result.put("orderPricePlan", getOrderPricePlan(orderId));
        }
        result.put("orderActualPrice", detail.get("orderActualPrice"));
    }

    private JSONObject getOrderUserInfo(DBObject detail) {
        JSONObject jsonObject = new ResultJSONObject();
        jsonObject.put("userPhone", detail.get("userPhone"));
        if (null != detail.get("userIcon")) {
            jsonObject.put("userPortrait", BusinessUtil.disposeImagePath(StringUtil.null2Str(detail.get("userIcon"))));
        }
        return jsonObject;
    }

    private DBObject getOrderPricePlan(String orderId) {
        BasicDBObject condition = new BasicDBObject();
        condition.put("orderId", Long.parseLong(orderId));
        DBCollection collection = getMongodbCollection(MERCHANT_PLAN);
        DBObject object = collection.findOne(condition, setPlan());
        if (null != object.get("joinTime")) {
            object.put("joinTime", formatDate(DateUtil.DATE_TIME_PATTERN, new Date((Long) object.get("joinTime"))));
        }
        object.put("orderPrice", object.get("discountPrice"));
        return object;
    }

    private BasicDBObject setPlan() {
        BasicDBObject object = new BasicDBObject();
        object.put("price", 1);
        object.put("priceUnit", 1);
        object.put("discountPrice", 1);
        object.put("joinTime", 1);
        object.put("paths", 1);
        object.put("planId", 1);
        object.put("content", 1);
        return object;
    }

    private JSONObject getMerchantOrderStatus(String orderId) {
        JSONObject jsonObject = new ResultJSONObject();
        BasicDBObject condition = new BasicDBObject();
        condition.put("orderId", Long.parseLong(orderId));
        DBCollection collection = getMongodbCollection(MERCHANT_ORDER);
        DBObject object = collection.findOne(condition, setMerchantOrderStatus());
        System.out.println(orderId + " - " + object.get("orderStatus"));
        jsonObject.put("merchantOrderStatus", object.get("orderStatus"));
        return jsonObject;
    }

    private BasicDBObject setMerchantOrderStatus() {
        BasicDBObject object = new BasicDBObject();
        object.put("orderStatus", 1);
        return object;
    }

}
