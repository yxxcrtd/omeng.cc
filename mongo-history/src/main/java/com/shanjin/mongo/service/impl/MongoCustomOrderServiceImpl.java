package com.shanjin.mongo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBObject;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.mongo.dao.IMongoDictionary;
import com.shanjin.mongo.dao.IMongoMerchantPlanDao;
import com.shanjin.mongo.dao.IMongoTimeLineDao;
import com.shanjin.mongo.dao.IOrderDao;
import com.shanjin.service.IMongoCustomOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * 项目名称：mongo- 类名称：MongoCustomOrderServiceImpl 类描述：历史订单查询      实现类 创建人：Revoke Yu
 * 创建时间：2016年10月8日 
 * 修改人：
 * 修改时间： 
 * 修改备注：
 * 
 * @version V1.0
 */
@Service("mongoCustomOrderService")

public class MongoCustomOrderServiceImpl implements IMongoCustomOrderService {
	
	@Resource
	IOrderDao  orderDao;
	
	@Resource
	IMongoDictionary mongoDictionaryDao;
	
	@Resource
	IMongoTimeLineDao  timeLineDao;
	
	@Resource
	IMongoMerchantPlanDao merchantPlanDao;
	
	@Override
	public JSONObject getOrderDetailForSender(String orderId,Long userId)
			throws Exception {
		JSONObject result =  orderDao.getOrderDetailForSender(StringUtil.nullToLong(orderId),userId);
		return result;
	}

	@Override
	public JSONObject getOrderDetailForReceiver(Map<String, Object> params) throws Exception {
        String orderId = StringUtil.null2Str(params.get("orderId"));
        Long userId = StringUtil.nullToLong(params.get("userId"));
        return orderDao.getOrderDetailForReceiver(orderId, userId);
	}

	@Override
	public JSONObject getPricePlanList(Map<String, Object> params)
			throws Exception {
		Long orderId = StringUtil.nullToLong(params.get("orderId"));
        int pageNo = StringUtil.nullToInteger(params.get("pageNo"));
        String orderBy = StringUtil.null2Str(params.get("orderBy"));
        String direction = StringUtil.null2Str(params.get("direction"));
        
		JSONObject result = merchantPlanDao.getMerchantPlanList(orderId, pageNo, orderBy, direction);
		return result;
	}

	@Override
	public JSONObject getPricePlanDetail(Map<String, Object> params)
			throws Exception {
			JSONObject result = new ResultJSONObject("000", "获取订单方案信息成功");
			result.put("isHistory", true);
		
			Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
	        Long orderId = StringUtil.nullToLong(params.get("orderId"));
	        Long userId = StringUtil.nullToLong(params.get("userId"));
	        
	        Map<String,Object> merchantPlanDetail = merchantPlanDao.getMerchantPlan(orderId, merchantId);
	        
	        Map<String,Object> orderDetail =  orderDao.getOriginalOrderDetail(userId, orderId);
	                      
	        Map<String,Object> orderPlanInfo = new HashMap<String,Object>();
	        orderPlanInfo.put("merchantId", merchantId);
	        orderPlanInfo.put("merchantName", merchantPlanDetail.get("merchantName"));
	        orderPlanInfo.put("appType", merchantPlanDetail.get("appType"));
	        orderPlanInfo.put("icoPath", BusinessUtil.disposeImagePath(merchantPlanDetail.get("iconPath").toString()));
	        orderPlanInfo.put("planId", merchantPlanDetail.get("_id"));
	        orderPlanInfo.put("planPrice", merchantPlanDetail.get("price"));
	        orderPlanInfo.put("discountPrice", merchantPlanDetail.get("merchantActualPrice"));
	        orderPlanInfo.put("deposit", merchantPlanDetail.get("deposit"));
	        orderPlanInfo.put("promise", merchantPlanDetail.get("promise"));
	        orderPlanInfo.put("content", merchantPlanDetail.get("content"));
	        orderPlanInfo.put("distance", merchantPlanDetail.get("distance"));
	        orderPlanInfo.put("joinTime", DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN,new Date((Long)merchantPlanDetail.get("joinTime"))));
	        orderPlanInfo.put("receiveEmployeesId", merchantPlanDetail.get("receiveEmployeesId"));
	        orderPlanInfo.put("priceUnit", merchantPlanDetail.get("priceUnit"));
	        orderPlanInfo.put("enterpriseAuth", merchantPlanDetail.get("enterpriseAuth"));
	        orderPlanInfo.put("personalAuth", merchantPlanDetail.get("personalAuth"));
	   
	        orderPlanInfo.put("planVoicePath", merchantPlanDetail.get("voicePath"));
	        orderPlanInfo.put("vipStatus", merchantPlanDetail.get("vipStatus"));
	        orderPlanInfo.put("merchantPoint", merchantPlanDetail.get("merchantPoint"));
	        orderPlanInfo.put("score", merchantPlanDetail.get("score"));
	        orderPlanInfo.put("auth", merchantPlanDetail.get("auth"));
	        orderPlanInfo.put("merchantType", merchantPlanDetail.get("merchantType"));
	        
	        if (orderDetail.containsKey("isImmediate")){
	        	orderPlanInfo.put("isImmediate", orderDetail.get("isImmediate"));
	        }else{
	        	orderPlanInfo.put("isImmediate", 0);
	        }
	        
	        if (orderDetail.containsKey("isOffer")){
	        	orderPlanInfo.put("isOffer", orderDetail.get("isOffer"));
	        }else{
	        	orderPlanInfo.put("isOffer", 1);
	        }
	        
	        if(merchantPlanDetail.get("picturePath")!=null &&  !merchantPlanDetail.get("picturePath").equals("")){
	        	orderPlanInfo.put("planPicturePath", BusinessUtil.disposeImagePath(merchantPlanDetail.get("picturePath").toString()));
			}else{
				orderPlanInfo.put("planPicturePath",null);
			}
			if(merchantPlanDetail.get("voicePath")!=null &&  !merchantPlanDetail.get("voicePath").equals("")){
				orderPlanInfo.put("planVoicePath", BusinessUtil.disposeImagePath(merchantPlanDetail.get("voicePath").toString()));
			}else{
				orderPlanInfo.put("planVoicePath",null);
			}
	        
			orderPlanInfo.put("telephone", merchantPlanDetail.get("telephone"));
			orderPlanInfo.put("totalCount", merchantPlanDetail.get("totalCount"));
	        
	        ArrayList goodsList = (ArrayList) merchantPlanDetail.get("orderPlanGoodsList");
	        result.put("orderPlanGoodsList", convertPath(goodsList));
	        
	        result.put("orderPlanInfo", orderPlanInfo);
	        return result;
	}


	@Override
	public JSONObject getPricePlanDetailForSender(Map<String, Object> param)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getPricePlanDetailForReceiver(Map<String, Object> param)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public JSONObject getTimeline(Long merchantId, Long orderId,
			int orderStatus, int type) throws Exception {
		
		    if(type!=1 && type!=2){
	            return null;
	        }

	        String timelineName="";
	        if(type==1){//用户
	            timelineName="timeline_user";
	        }
	        if(type==2){//商户
	            timelineName="timeline_merchant";
	        }

	        String excludeCode="";
	        List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
	        if(orderStatus==1 || orderStatus==2 || orderStatus==3 || orderStatus==4 || orderStatus==5){
	            excludeCode="200,300,210,310,230";
	        }

	        if(orderStatus==6 || orderStatus==7 || orderStatus==8 || orderStatus==9){//订单过期
	            excludeCode="200,250,280,290,300,330,360";
	        }

	        Map<String,Object> paramMap=new HashMap<String, Object>();
	        paramMap.put("orderId", orderId);
	        paramMap.put("excludeCode", excludeCode);
	        paramMap.put("timelineName", timelineName);
	        
	        List<Map<String,Object>> allLine=mongoDictionaryDao.getTimeLineDictionary(paramMap);
	        List<Map<String,Object>> getTimelineList=timeLineDao.getTimeLine(orderId);
	        if(orderStatus==1 || orderStatus==2 || orderStatus==3 || orderStatus==4 || orderStatus==5){
	            for(Map<String,Object> map : allLine){
	                String code=StringUtil.null2Str(map.get("code"));
	                int x=0;
	                int y=0;
	                for(Map<String,Object> map_ : getTimelineList){
	                    String code_=StringUtil.null2Str(map_.get("actionCode"));
	                    if(type==2 && code_.equals("300")){//商户 如果提供了报价方案，且报价方案不是自己的，则去除这一项
	                        Long merchantId_=StringUtil.nullToLong(map_.get("merchantId"));
	                        if(merchantId!=0 && merchantId_!=0 && !merchantId_.equals(merchantId)){
	                            continue;
	                        }
	                    }

	                    if(StringUtil.isNotEmpty(code) && StringUtil.isNotEmpty(code_) && code.equals(code_)){

	                        if(type==1 && code_.equals("300")){//用户查看时间轴，显示第一条报价方案的时间
	                            if(y>0){
	                                continue;
	                            }
	                            y+=1;
	                        }
	                        map.put("actionTime", map_.get("actionTime"));
	                        map.put("flag", 1);

	                        x=1;
	                    }
	                }
	                if(x==0){
	                    map.put("flag", 0);
	                }
	                resultList.add(map);
	            }

	        }else{
	            for(Map<String,Object> map : getTimelineList){
	                String code=StringUtil.null2Str(map.get("actionCode"));

	                if(type==2){//

	                    Long merchantId_=StringUtil.nullToLong(map.get("merchantId"));
	                    if(code.equals("210")){//用户选择报价方案
	                        if(merchantId!=0 && merchantId_!=0 && !merchantId_.equals(merchantId)){
	                            code="360";
	                        }
	                    }else{
	                        if(merchantId!=0 && merchantId_!=0 && !merchantId_.equals(merchantId)){
	                            continue;
	                        }
	                    }
	                }
	                int x=0;
	                for(Map<String,Object> map_ : allLine){
	                    String code_=StringUtil.null2Str(map_.get("code"));

	                    if(StringUtil.isNotEmpty(code) && StringUtil.isNotEmpty(code_) && code.equals(code_)){
	                        map.put("title", map_.get("title"));
	                        map.put("remark", map_.get("remark"));
	                        map.put("code", map_.get("code"));
	                        map.put("flag", 1);
	                        map.remove("actionCode");
	                        map.remove("merchantId");
	                        x=1;
	                    }
	                }
	                if(x==0){
	                    map.put("flag", 0);
	                }
	                resultList.add(map);
	            }

	        }
	        JSONObject jsonObject = new ResultJSONObject("000", "查询时间轴数据成功");
	        jsonObject.put("timeLineList", resultList);
	        return jsonObject;
	}

	@Override
	public JSONObject getOrderListForSender(Map<String, Object> params)
			throws Exception {
		JSONObject result =  orderDao.getOrderListForSender(params);
		return result;
	}

    /**
     * 商户侧历史订单
     * @param params
     * @return
     * @throws Exception
     */
	@Override
	public JSONObject getOrderListForReceiver(Map<String, Object> params) throws Exception {
        return orderDao.getOrderListForReceiver(params);
    }

	@Override
	public JSONObject getMerchantServiceRecord(Long merchantId,Long orderId, Long userId) {
		JSONObject result = new ResultJSONObject("000", "商户查询服务记录成功");
		Map<String,Object> orderDetail = orderDao.getOriginalOrderDetail(userId, orderId);
		
		Map<String,Object>  originalServiceRecord= (Map<String, Object>) orderDetail.get("serviceRecord");
		Map<String,Object> serviceRecordMap= new HashMap<String,Object>();
		serviceRecordMap.put("id", originalServiceRecord.get("id"));
		serviceRecordMap.put("price", originalServiceRecord.get("price"));
		serviceRecordMap.put("remark", originalServiceRecord.get("remark"));
		serviceRecordMap.put("path", fmtArrayAttach((String[]) originalServiceRecord.get("attachments")));
				
		serviceRecordMap.put("joinTime", DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN,new Date((Long)originalServiceRecord.get("join_time"))));
		serviceRecordMap.put("payType", originalServiceRecord.get("pay_type"));
		serviceRecordMap.put("merchantId", merchantId);
		serviceRecordMap.put("orderId", orderId);
		
		result.put("merchantServiceRecord", serviceRecordMap);
		
		Map<String,Object> merchantSummary = (Map<String, Object>) orderDetail.get("merchantSummary");
		Map<String,Object> merchantInfo  = new HashMap<String,Object>();
		merchantInfo.put("merchantIcon", BusinessUtil.disposeImagePath(merchantSummary.get("iconUrl").toString()));
		merchantInfo.put("merchantName", merchantSummary.get("merchantName"));
		merchantInfo.put("authType",  merchantSummary.get("authType"));
		merchantInfo.put("merchantAddress", merchantSummary.get("locationAddress"));
		
		result.put("merchantInfo", merchantInfo);
		
		Map<String,Object> orderPriceInfo = new HashMap<String,Object>();
		orderPriceInfo.put("vouchersPrice", orderDetail.get("voucherPrice"));
		orderPriceInfo.put("deposit", orderDetail.get("deposit"));
		orderPriceInfo.put("orderPayType", orderDetail.get("orderPayType"));
		orderPriceInfo.put("merchantActualPrice", orderDetail.get("merchantActualPrice"));
		orderPriceInfo.put("orderPrice", orderDetail.get("orderPrice"));
		orderPriceInfo.put("orderActualPrice", orderDetail.get("orderActualPrice"));
		result.put("orderPriceInfo", orderPriceInfo);
		result.put("isHistory", true);
		
		return result;
	}
	
	
	private String fmtArrayAttach(String[] attachments){
	     StringBuffer result =new StringBuffer();
		 if (attachments==null){
			  return null;
		 }
		 for(String attachment:attachments){
			  result.append(BusinessUtil.disposeImagePath(attachment));
			  result.append(",");
		 }
		 result.deleteCharAt(result.length()-1);
		 return result.toString();
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
	public Boolean hasUserHistoryOrder(Map<String, Object> params)
			throws Exception {
		return orderDao.hasUserOrder(params);
	}
}
