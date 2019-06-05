package com.shanjin.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.LocationUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IConfigurationDao;
import com.shanjin.dao.IDictionaryDao;
import com.shanjin.dao.IMerchantDao;
import com.shanjin.dao.IMerchantOrderDao;
import com.shanjin.dao.IMerchantPlanDao;
import com.shanjin.dao.IOrderDao;
import com.shanjin.dao.IScheduleDao;
import com.shanjin.dao.IServiceRecordDao;
import com.shanjin.dao.ITimelineDao;




/**
 * 用户订单相关信息的迁移服务
 * @author Revoke Yu 2016.9.19
 *
 */
public class CustomOrderMigrateService {
	private static final Short  PAGESIZE=100;
	
	//用户取消订单的迁移时限
	private static final String CONFIG_MIGRATE_CANCEL_PERIOD_KEY="migrate_cancel_expires_day";
	
	//无报价方案的过期订单的迁移时限
	private static final String CONFIG_MIGRATE_NOBID_PERIOD_KEY="migrate_nobid_expires_day";
	
	//未选中报价方案的过期订单的迁移时限
	private static final String CONFIG_MIGRATE_NOCHOOSED_PERIOD_KEY="migrate_nochoised_expires_day";
	
	//正常订单的迁移时限
	private static final String CONFIG_MIGRATE_NORMAL_PERIOD_KEY="migrate_normal_expires_day";
	
	
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Resource
	IScheduleDao  scheduleDao;

	@Resource
	IConfigurationDao configuratioDao;
	
	@Resource
	IOrderDao      orderDao;
	
	@Resource
	IMerchantPlanDao      merchantPlanDao;
	
	
	@Resource
	IMerchantDao      merchantDao;
	
	@Resource
	IDictionaryDao   dictionaryDao;
	
	@Resource
	IServiceRecordDao serviceRecordDao;
	
	@Resource
	ITimelineDao  timelineDao;
	
	@Resource
	IMerchantOrderDao  merchantOrderDao;
	
	/**
	 * 迁移用户主动取消的订单
	 */
	public void  migrateCancelOrder(){
		
		long periodMicoSec = getPeriodOfMicoSec(this.CONFIG_MIGRATE_CANCEL_PERIOD_KEY);
		
		Map<String, Object> lastMigrateRec = null; 
			
		boolean cleanFlags=false;
		
		Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
	   
		Map<String,String> statusTextMap= getOrderStatusDescList();
		
		while(true) {
			
						lastMigrateRec = getLastMigrateRec(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_CANCEL);
						
						Map<String,Object> orderParam = new HashMap<String,Object>(4);
				
						orderParam.put("last_migrateTime", lastMigrateRec.get("lastBusinessDataTime"));
						Date endMigrateTime =new Date(System.currentTimeMillis()-periodMicoSec) ;
						orderParam.put("end_migrateTime", endMigrateTime);
						orderParam.put("lastOrderId", lastMigrateRec.get("maxOrderId"));
						orderParam.put("pageSize", PAGESIZE);
	
						List<Map<String,Object>> ordersSummary=orderDao.getCancelOrders(orderParam);
		
		
						if (ordersSummary!=null && ordersSummary.size()>0){
								if(!cleanFlags){
									clean(ordersSummary,"id","merchantPlan","orderId");
									clean(ordersSummary,"id","order","_id");
									clean(ordersSummary,"id","timeline","orderId");
								}
							
								String ids = getIds(ordersSummary,"id");
								
								if (ids!=null && ids.length()>0){
									List<Map<String,Object>> planList=preparePlanList(ids);
									saveCollectionToMongo(planList,"merchantPlan");
								}
								
								
								Map<String,String> queryParam = new HashMap<String,String>(2);
								queryParam.put("ids", ids);
								List<Map<String,Object>> timelineList=timelineDao.getTimeLinesByIDS(queryParam);
								
								saveCollectionToMongo(timelineList,"timeline");
								
								
								
								queryParam.put("orderColumn","join_time");
								List<Map<String,Object>> orders=orderDao.getOrderSummaryList(queryParam);
								
								prepareOrderForMongo(orders,dictionary,statusTextMap,ids,null,false);
								
								int orderSize = saveCollectionToMongo(orders,"order");
			 
								Map<String,Object> lastOrder = orders.get(orderSize-1);
								addMigrateRecord(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_CANCEL,(Long)lastOrder.get("_id"),(Date)lastOrder.get("joinTime"));
						}else{
							break;
						}
						
						cleanFlags=true;
						
						mySleep(2);
		
		}
		
		 System.out.println("迁移用户取消的订单跑批完成");
	}


	/**
	 * 迁移无报价方案的过期订单
	 */
	public void migrateNoBidOrder(){
		 long periodMicoSec=getPeriodOfMicoSec(this.CONFIG_MIGRATE_NOBID_PERIOD_KEY);
		 
		Map<String, Object> lastMigrateRec = null;
		 
		boolean cleanFlags=false;
		
		Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
		   
		Map<String,String> statusTextMap= getOrderStatusDescList();
		
		while(true){
				
			lastMigrateRec = getLastMigrateRec(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_NOBID);
			
			Map<String,Object> orderParam = new HashMap<String,Object>(4);
			
			orderParam.put("last_migrateTime", lastMigrateRec.get("lastBusinessDataTime"));
			Date endMigrateTime =new Date(System.currentTimeMillis()-periodMicoSec) ;
			orderParam.put("end_migrateTime", endMigrateTime);
			orderParam.put("lastOrderId", lastMigrateRec.get("maxOrderId"));
			orderParam.put("pageSize", PAGESIZE);

			List<Map<String,Object>> ordersSummary=orderDao.getNoBidOrders(orderParam);	
			
			if (ordersSummary!=null && ordersSummary.size()>0){
				if(!cleanFlags){
					clean(ordersSummary,"id","order","_id");
					clean(ordersSummary,"id","timeline","orderId");
				}
				
				String ids = getIds(ordersSummary,"id");
				Map<String,String> queryParam = new HashMap<String,String>(2);
				queryParam.put("ids", ids);
				
				List<Map<String,Object>> timelineList=timelineDao.getTimeLinesByIDS(queryParam);
				
				saveCollectionToMongo(timelineList,"timeline");
				
				
				queryParam.put("orderColumn","service_time");
				List<Map<String,Object>> orders=orderDao.getOrderSummaryList(queryParam);
					
				prepareOrderForMongo(orders,dictionary,statusTextMap,ids,null,false);
				
				int orderSize = saveCollectionToMongo(orders,"order");
				
				
				Map<String,Object> lastOrder = orders.get(orderSize-1);
				
				addMigrateRecord(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_NOBID,(Long)lastOrder.get("_id"),(Date)lastOrder.get("serviceTime"));
				
			}else{
					break;
			}
			
			cleanFlags=true;
			
			mySleep(2);
		}
		
		System.out.println("无报价方案的过期订单");
	}

	
	/**
	 * 迁移未选择报价方案过期的订单
	 */
	public void migrateNoChoosedOrder(){
		 long periodMicoSec=getPeriodOfMicoSec(this.CONFIG_MIGRATE_NOCHOOSED_PERIOD_KEY);
		 
		 Map<String, Object> lastMigrateRec = null;
			 
		 boolean cleanFlags=false;
		 
		 
		Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
			   
		Map<String,String> statusTextMap= getOrderStatusDescList();
			
		
			
		 while(true){
					
				lastMigrateRec = getLastMigrateRec(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_NOCHOOSED);
				
				Map<String,Object> orderParam = new HashMap<String,Object>(4);
				
				orderParam.put("last_migrateTime", lastMigrateRec.get("lastBusinessDataTime"));
				Date endMigrateTime =new Date(System.currentTimeMillis()-periodMicoSec) ;
				orderParam.put("end_migrateTime", endMigrateTime);
				orderParam.put("lastOrderId", lastMigrateRec.get("maxOrderId"));
				orderParam.put("pageSize", PAGESIZE);

				List<Map<String,Object>> ordersSummary=orderDao.getNoChoosedOrders(orderParam);	
		 
				if (ordersSummary!=null && ordersSummary.size()>0){
					if(!cleanFlags){
						clean(ordersSummary,"id","merchantPlan","orderId");
						clean(ordersSummary,"id","timeline","orderId");
						clean(ordersSummary,"id","order","_id");
					}
					
					String ids = getIds(ordersSummary,"id");
					List<Map<String,Object>> planList=preparePlanList(ids);
					
					
					int planSize = saveCollectionToMongo(planList,"merchantPlan");
				
					Map<String,String> queryParam = new HashMap<String,String>(2);
					queryParam.put("ids", ids);
					
					List<Map<String,Object>> timelineList=timelineDao.getTimeLinesByIDS(queryParam);
					saveCollectionToMongo(timelineList,"timeline");
					
					
					List<Map<String,Object>> merchantOrderList=merchantOrderDao.getMerchantOrderInfo(queryParam);
					prepareMerchantOrderForMongo(merchantOrderList,statusTextMap,dictionary);
					saveCollectionToMongo(merchantOrderList,"merchantOrder");
					
					
					
					queryParam.put("orderColumn","service_time");
					List<Map<String,Object>> orders=orderDao.getOrderSummaryList(queryParam);
					
					
					prepareOrderForMongo(orders,dictionary,statusTextMap,ids,null,false);
					
					int orderSize = saveCollectionToMongo(orders,"order");
					
					Map<String,Object> lastOrder = orders.get(orderSize-1);
					
					addMigrateRecord(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_NOCHOOSED,(Long)lastOrder.get("_id"),(Date)lastOrder.get("serviceTime"));
				
				}else{
						break;
				}
				cleanFlags = true;
				
				mySleep(2);
		 }
		 System.out.println("未选择报价方案的过期订单");
	}

	/**
	 * 迁移正常进行的历史订单
	 */
	public void migrateNormalOrder(){
		long periodMicoSec=getPeriodOfMicoSec(this.CONFIG_MIGRATE_NORMAL_PERIOD_KEY);
		
		 Map<String, Object> lastMigrateRec = null;
		 
		 boolean cleanFlags=false;
		 
		 
		Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
		
		Map<String,String>  merchantDictionary=getOrderStatusMap("searchMerchantOrderStatus");
			   
		Map<String,String> statusTextMap= getOrderStatusDescList();
			
			
		 while(true){
					
				lastMigrateRec = getLastMigrateRec(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_NORMAL);
				
				Map<String,Object> orderParam = new HashMap<String,Object>(4);
				
				orderParam.put("last_migrateTime", lastMigrateRec.get("lastBusinessDataTime"));
				Date endMigrateTime =new Date(System.currentTimeMillis()-periodMicoSec) ;
				orderParam.put("end_migrateTime", endMigrateTime);
				orderParam.put("lastOrderId", lastMigrateRec.get("maxOrderId"));
				orderParam.put("pageSize", PAGESIZE);
				
				List<Map<String,Object>> ordersSummary=orderDao.getNormalOrders(orderParam);
				 
				if (ordersSummary!=null && ordersSummary.size()>0){
					if(!cleanFlags){
						clean(ordersSummary,"id","merchantPlan","orderId");
						clean(ordersSummary,"id","timeline","orderId");
						clean(ordersSummary,"id","order","_id");
					}
					
					String ids = getIds(ordersSummary,"id");
					List<Map<String,Object>> planList=preparePlanList(ids);
					
					Map<String,Object>  merchantOrderToPlanList=converToMap(planList);
					
					int planSize = saveCollectionToMongo(planList,"merchantPlan");
				
					Map<String,String> queryParam = new HashMap<String,String>(2);
					queryParam.put("ids", ids);
					
					List<Map<String,Object>> timelineList=timelineDao.getTimeLinesByIDS(queryParam);
					saveCollectionToMongo(timelineList,"timeline");
					
					
					List<Map<String,Object>> merchantOrderList=merchantOrderDao.getMerchantOrderInfo(queryParam);
					prepareMerchantOrderForMongo(merchantOrderList,statusTextMap,dictionary);
					saveCollectionToMongo(merchantOrderList,"merchantOrder");
					
					
					queryParam.put("orderColumn","deal_time");
					List<Map<String,Object>> orders=orderDao.getOrderSummaryList(queryParam);
					
					
					prepareOrderForMongo(orders,dictionary,statusTextMap,ids,merchantOrderToPlanList,true);
					
					
					
					
					int orderSize = saveCollectionToMongo(orders,"order");
					
					Map<String,Object> lastOrder = orders.get(orderSize-1);
					
					addMigrateRecord(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_NORMAL,(Long)lastOrder.get("_id"),(Date)lastOrder.get("dealTime"));
					
				}else{
					break;
				}
				cleanFlags = true;
				mySleep(2);
		 }
		
		System.out.println("正常进行的订单");
	}
	


	/**
	 * 从列表中生成id 逗号分隔。 如果列表未空，则返回""
	 * @param items
	 * @param key
	 * @return
	 */
	private String getIds(List<Map<String,Object>> items,String key){
        StringBuffer result=new StringBuffer(100*20*100);
        for(Map<String, Object> item : items){
            long id=StringUtil.nullToLong(item.get(key));
            result.append(",").append(id);
        }
        if(!result.equals("")){
            result.deleteCharAt(0);
        }
        return result.toString();
	}
	
	
	/**
	 * 将java 列表转换为Mongodb的列表
	 * @param items
	 * @return
	 */
	private List<com.mongodb.DBObject> convertToMongo(List<Map<String,Object>> items){
		List<com.mongodb.DBObject> result =new ArrayList<com.mongodb.DBObject>();
		for(Map<String,Object> item:items){

            if (9 != StringUtil.nullToInteger(item.get("orderStatus"))) {
                String jsonStr = JSON.toJSONString(item, SerializerFeature.WriteMapNullValue);
                result.add((DBObject) com.mongodb.util.JSON.parse(jsonStr));
            }
		}
		return result;
	}

	@Transactional
	private void addMigrateRecord(String businessType,String catalog,Long maxOrderId,Date bussinessDataTime){
		Map<String,Object> scheduleVO = new HashMap<String,Object>(5);
		scheduleVO.put("businessType", businessType);
		scheduleVO.put("catalog", catalog);
		scheduleVO.put("maxOrderId", maxOrderId);
		scheduleVO.put("businessDataTime",bussinessDataTime);
		scheduleDao.insertScheduleRec(scheduleVO);
		
	}
	
	/**
	 * 读取配置文件中的历史订单迁移周期，以毫秒计返回。
	 * @param key
	 * @return
	 */
	private long getPeriodOfMicoSec(String key) {
		Map<String,Object> configureRec = configuratioDao.getConfigByKey(key);	
		int  days = Integer.parseInt((String) configureRec.get("config_value"));
		long periodMicoSec = 3600L*24 * 1000*days;
		return periodMicoSec;
	}
	
	/**
	 * 读取最近一次成功迁移的记录
	 * @param businessType
	 * @param catalog
	 * @return
	 */
	private Map<String, Object> getLastMigrateRec(String businessType,String catalog) {
		Map<String,String> param = new HashMap<String,String>(2);
		param.put("businessType", businessType);
		param.put("catalog", catalog);
		
		Map<String,Object> lastMigrateRec=scheduleDao.getLastScheduleRec(param);
		
		if (lastMigrateRec==null){
			 throw new RuntimeException("未找到上次迁移记录：bussinessType="+businessType+",catalog="+catalog);
		}
		return lastMigrateRec;
	}
	
	
	
	/**
	 * 准备存到MongoDB 中的订单列表
	 * @param orders
	 * @param dictionary 状态名称翻译Map
	 * @param orderTextMap 状态描述翻译map
	 * @param finished  完成订单标志位
	 */
	private void prepareOrderForMongo(List<Map<String, Object>> orders,
			Map<String, String> dictionary,Map<String,String> orderTextMap,String orderIds,Map<String,Object> planMap,boolean finished) {
		Map<String,Object> idsParam = new HashMap<String,Object>();
		idsParam.put("orderId", orderIds);
	
		List<Map<String,Object>> attachments=orderDao.selectOrderAttachment(idsParam);
		
		Map<Long,Object> orderAttachments = convertToMap(attachments,"order_id");
		
		
		Map<Long,Object> merchantInfos = new HashMap();
		Map<Long,Object> serviceRecordMap = new HashMap();
		if (finished){
			List<Map<String,Object>> winnerInfos=merchantPlanDao.getWinnerInfos(idsParam);
					
			merchantInfos=converToFormalMap(winnerInfos,"order_id");
			
			List<Map<String,Object>> serviceRecords=serviceRecordDao.getServiceRecordsByIds(idsParam);
			
			String srIds = getIds(serviceRecords,"id");
			idsParam.clear();
			idsParam.put("serviceRecordId", srIds);
			
			List<Map<String,Object>> serviceRecordAtta=serviceRecordDao.getServiceRecordsAttaByIds(idsParam);
			Map<Long,Object> serviceRecordAttaMap = convertToList(serviceRecordAtta,"merchant_service_record_id");
			
			for(Map<String,Object> serviceRecord:serviceRecords){
				 serviceRecord.put("attachments", serviceRecordAttaMap.get(serviceRecord.get("id")));	
			}
		  //serviceRecordMap = convertToList(serviceRecords,"order_id");
			serviceRecordMap = converToFormalMap(serviceRecords,"order_id");
		}
		
		
		
		for(Map<String,Object> order:orders){
				order.put("orderStatusName", dictionary.get(order.get("orderStatus").toString()));
				//对于状态为6 的翻译时，需要特殊处理一下
				if (!finished &&  order.get("orderStatus").equals(6) && (Long)(order.get("planCount"))>0 ){
					order.put("orderStatusText", orderTextMap.get("searchOrderStatus_"+8));
				}
				else {
					order.put("orderStatusText", orderTextMap.get("searchOrderStatus_"+order.get("orderStatus")));
				}
				order.put("orderText", getOrderDetail(order.get("jsonDetail").toString()));
				Long orderId= (Long) order.get("_id");
				if (orderAttachments.containsKey(orderId)){
						order.put("orderAttachment", orderAttachments.get(orderId));
				}
				
				if (finished){
						//选中的报价方案的，补充商户信息
						if (order.get("merchantId")==null){
							 continue; //忽略掉错误数据。
						}
					    StringBuffer key = new StringBuffer(order.get("merchantId").toString()).append("_").append(orderId);
					    Map<String,Object> planInfo= (Map<String, Object>) planMap.get(key.toString());
					    Map<String,Object> merchantSummary = (Map<String, Object>) merchantInfos.get(orderId);
					    if (merchantSummary==null || planInfo==null){
					    		System.out.println(key);
					    }else{
					    		merchantSummary.put("vipStatus", planInfo.get("vipStatus"));
					    		merchantSummary.put("score", planInfo.get("score"));
					    		merchantSummary.put("distance", planInfo.get("distance"));
					    		merchantSummary.put("starLevel", planInfo.get("merchantPoint"));
					    		Map authInfo=getAuthInfo((Long) order.get("merchantId"));
					    		merchantSummary.put("authStatus", authInfo.get("authStatus"));
					    		merchantSummary.put("auth", authInfo.get("auth"));
					    		
					    		// 0未知 1企业 2自由（个性）服务
								String appType = StringUtil.null2Str(merchantSummary.get("appType"));
								if (appType == null) {
									merchantSummary.put("merchantType", 0);
								} else if (!appType.equals("gxfw")) {
									merchantSummary.put("merchantType", 1);
								} else {
									merchantSummary.put("merchantType", 2);
								}		
								
					    }
					    order.put("merchantSummary", merchantSummary);
				}else{
						order.put("merchantSummary", merchantInfos.get(orderId));
				}
				order.put("serviceRecord",serviceRecordMap.get(orderId));
				if (!finished){
					 order.put("merchantInfo", null);
				}
		}
	}
	
	private void prepareMerchantOrderForMongo(
			List<Map<String, Object>> merchantOrderList,
			Map<String, String> statusTextMap,Map<String,String> dictionary) {
		
		 for(Map<String,Object> merchantOrder:merchantOrderList){
			 merchantOrder.put("orderStatusText", statusTextMap.get("searchMerchantOrderStatus_"+merchantOrder.get("orderStatus")));
			 merchantOrder.put("orderStatusName", dictionary.get(merchantOrder.get("orderStatus").toString()));
		 }
	}

	
	
	/**
	 * 准备存到MongoDb 中的报价方案列表
	 * @param ids
	 * @return
	 */
	private List<Map<String, Object>> preparePlanList(String ids) {
		Map<String,String> queryParam = new HashMap<String,String>(1);
		queryParam.put("ids", ids);
		
		List<Map<String,Object>> merchantPlanSummary=merchantPlanDao.getPlanListByOrderIds(queryParam);
		
		if (merchantPlanSummary==null || merchantPlanSummary.size()<1){
			return null;
		}
		List<Map<String,Object>> plansAttachments = getPlansAttach(merchantPlanSummary);
		
		Map<Long,Object>   planToAttachMap = convertToMap(plansAttachments,"merchant_plan_id");
		
		
		List<Map<String,Object>> planGoodsAttachments = getPlanGoodsAttach(merchantPlanSummary);
		
		Map<Long,Object>   planToGoodsMap = convertToList(planGoodsAttachments,"plan_id");
		
		if (merchantPlanSummary!=null && merchantPlanSummary.size()>0){
				for (Map<String,Object> merchantPlan:merchantPlanSummary){
						Long merchant_plan_id=(Long) merchantPlan.get("_id");
						if (planToAttachMap.containsKey(merchant_plan_id)){
							  Map<String,String> attachItem = (Map<String, String>) planToAttachMap.get(merchant_plan_id);
							  merchantPlan.put("picturePath", attachItem.get("picturePath"));
							  merchantPlan.put("voicePath", attachItem.get("voicePath"));
						}else{
							merchantPlan.put("picturePath", null);
							merchantPlan.put("voicePath", null);
						}
						merchantPlan.put("orderPlanGoodsList", planToGoodsMap.get(merchant_plan_id));
						
						genMerchantType(merchantPlan);
						
						genAuthType(merchantPlan);
						
						genScore(merchantPlan);
						
						genVipStatus(merchantPlan);
						
						genWeight(merchantPlan);
						
						 // 对用户和商户之间的距离进行编辑				
			            String distanceStr = null;
			            if (merchantPlan.get("distance")==null){
			            		distanceStr=LocationUtil.showDistance(200D*1000);  //无经纬度或私人顾问，具体为200KM
			            }else {
			            		distanceStr=LocationUtil.showDistance(((Integer)merchantPlan.get("distance")).doubleValue());
			            }
			            merchantPlan.put("distance", distanceStr);
				}
			
		}
		
		
		return merchantPlanSummary;
	}
	


	/**
	 * 获取报价放方案列表ids 对应的附件。
	 * @param merchantPlanSummary
	 * @return
	 */
	private List<Map<String, Object>> getPlansAttach(
			List<Map<String, Object>> merchantPlanSummary) {
		String planIds = getIds(merchantPlanSummary,"_id");
		Map<String,Object> param= new HashMap<String,Object>();
		param.put("ids", planIds);
		List<Map<String,Object>> attachList=merchantPlanDao.getPlanAttachmentListByPlanIds(param);
		return attachList;
	}


	/**
	 * 获取报价方案对应的商品列表
	 * @param merchantPlanSummary
	 * @return
	 */
	private List<Map<String, Object>> getPlanGoodsAttach(
			List<Map<String, Object>> merchantPlanSummary) {
		String planIds = getIds(merchantPlanSummary,"_id");
		Map<String,Object> param= new HashMap<String,Object>();
		param.put("ids", planIds);
		List<Map<String,Object>> attachList=merchantPlanDao.getPlanGoodsListByPlanIds(param);
		return attachList;
	}



	/**
	 * 保存订单列表到MongoDb中
	 * @param dataCollection
	 * @return
	 */
	private int saveCollectionToMongo(List<Map<String, Object>> dataCollection,String mongoCollectionName) {
		if (dataCollection==null || dataCollection.size()<1){
			return 0;
		}
		List<DBObject> mongoOrderList=convertToMongo(dataCollection);

		DBCollection collection = mongoTemplate.getCollection(mongoCollectionName);
		WriteResult   result=collection.insert(mongoOrderList);

		int   dataSize = mongoOrderList.size();

		return dataSize;
	}
	
	
	/**
	 * 从MongoDb 中按传入条件集合中的key的值  清理上次未正确迁移的数据
	 * @param batchData    存放待数据的条件集合
	 * @param key          条件集合里的依据列
	 * @param collectionName  MongoDb中的集合名称
	 * @param collectionName  MongoDb中的集合名称
	 * 
	 */
	private void clean(List<Map<String,Object>> batchData,String key,String collectionName,String fieldName){
		BasicDBObject query = new BasicDBObject();
		List<Long> ids = new ArrayList<Long>(batchData.size());
		for(Map<String,Object> item:batchData){
			ids.add((Long)item.get(key));
		}
		query.put(fieldName, new BasicDBObject("$in", ids));
		mongoTemplate.getCollection(collectionName).remove(query);
	}
	
	
	
	
	//获取订单状态编码定义
    private Map<String,String> getOrderStatusMap(String type){
        Map<String,String>  result =new HashMap<String,String>();
        List<Map<String,Object>> dictionaries = dictionaryDao.getOrderStatusFromDictonary(type);
        if (dictionaries!=null || dictionaries.size()>0){
            for (Map<String,Object> item:dictionaries){
                result.put(item.get("dict_key").toString(), item.get("dict_value").toString());
            }
        }
        return result;
    }
    
    //获取单状态描述的定义
    private  Map<String,String> getOrderStatusDescList() { 
       Map<String,String>  result = new HashMap<String,String>();
       List<Map<String,Object>> describeInfo= dictionaryDao.getOrderStatustTextList();
       
       for (Map<String,Object> info: describeInfo){
		   result.put(info.get("dict_type").toString()+"_"+StringUtil.null2Str(info.get("dict_key")), StringUtil.null2Str(info.get("remark")));
	  }
       return result;
    }
    

    
    //构造  merchantID_orderId   报价方案    键值对
	private Map<String, Object> converToMap(List<Map<String, Object>> planList) {
		Map<String,Object>  result = new HashMap<String,Object>();
		
		if (planList!=null && planList.size()>0){
			for(Map<String, Object> item : planList) {
				StringBuffer key=new StringBuffer(item.get("merchantId").toString()).append("_").append(item.get("orderId"));
			
				result.put(key.toString(), item);
				if (item.get("orderId").equals("19")){
					break;
				}
			}
		}
		return result;
	}
    
    
	 //按指定列名为key,将列表中的元素转换为 Map
	private Map<Long, Object> converToFormalMap(List<Map<String, Object>> dataList,String columnName) {
			Map<Long,Object>  result = new HashMap<Long,Object>();
		
			if (dataList!=null && dataList.size()>0){
					for(Map<String, Object> item : dataList) {
							result.put((Long)item.get(columnName), item);
					}
			}
					
		    return result;
	}
	
    
    /**
     * 获取订单的对应的附件，结果集未order_id,附件数组
     * @param ids
     * @return
     */
	private Map<Long, Object> convertToMap(List<Map<String,Object>> attachments,String columnName) {
			Map<Long,Object>  result = new HashMap<Long,Object>();
			
			Long lastId=null;
			Long currId=null;
			StringBuffer itemPictures = new StringBuffer();
			StringBuffer itemVoices = new StringBuffer();
			Map<String,String> itemAttachments = new HashMap<String,String>();
			if (attachments!=null && attachments.size()>0){
				for(Map<String, Object> attachment : attachments) {
					currId = (Long) attachment.get(columnName);
					if (lastId!=null && lastId.compareTo(currId)!=0){
						itemAttachments.put("picturePath", itemPictures.toString());
						itemAttachments.put("voicePath", itemVoices.toString());
						result.put(lastId, itemAttachments);
						itemAttachments=new HashMap<String,String>();
						itemPictures=new StringBuffer();
						itemVoices=new StringBuffer();
					}
					lastId=currId;
					
					Integer type = (Integer)attachment.get("type");
					String path = (String) attachment.get("path");
					if (path==null){
						continue;
					}
					if(type == 1){
						if(itemPictures.length()==0) {
							itemPictures.append(path);
						} else {
							itemPictures.append(",").append(path);
						}
					} else if (type == 2) {
						if(itemVoices.length()==0) {
							itemVoices.append(path);
						} else {
							itemVoices.append(",").append(path);
						}
					}
				}
			}
			//补充最后一个
			if (!result.containsKey(lastId)){
				itemAttachments.put("picturePath", itemPictures.toString());
				itemAttachments.put("voicePath", itemVoices.toString());
				result.put(lastId, itemAttachments);
			}

		return result;
	}

	
	/**
	 * 以columnName 为分组 ，返回 columnName 对应的值为 key,分组列表未value 的Map
	 * @param dataList
	 * @param columnName
	 * @return
	 */
	private Map<Long, Object> convertToList(List<Map<String,Object>> dataList,String columnName) {
		Map<Long,Object>  result = new HashMap<Long,Object>();
		
		Long lastId=null;
		Long currId=null;
		List group = new ArrayList();
		if (dataList!=null && dataList.size()>0){
			for(Map<String, Object> item : dataList) {
				currId = (Long) item.get(columnName);
				if (lastId!=null && lastId.compareTo(currId)!=0){
					result.put(lastId, group);
					group=new ArrayList();
				}
				lastId=currId;
				group.add(item);
			}
		}
		//补充最后一个
		if (!result.containsKey(lastId)){
			result.put(lastId, group);
		}
    	return result;
    }
	
    
    private Object getOrderDetail(String jsonDetail) {
        List<String> list = new ArrayList<String>();
       
        if (jsonDetail != null && !jsonDetail.isEmpty()) {
            JSONArray arr= JSONObject.parseArray(jsonDetail);
            if (arr != null&&arr.size()>0) {
                for(int i=0;i<arr.size();i++){
                    JSONObject jsonObject = arr.getJSONObject(i);
                    String value = StringUtil.null2Str(jsonObject.get("value"));
                    if(!StringUtil.isNullStr(value)){
                        value = jsonObject.get("colDesc")+ "：" + value;
                        list.add(value);
                    }
                }
            }
        }
         return list.toArray();
     }
  
    /**
     * 判断商户关联的服务项目类型
     * @param merchantPlan
     */
    private void  genMerchantType(Map<String,Object> merchantPlan){
    	  if(StringUtil.null2Str(merchantPlan.get("appType")).equals("gxfw")){
    		  merchantPlan.put("merchantType", 2);
          }else{
        	  merchantPlan.put("merchantType", 1);
          }
    }
    
    
    //报价方案中的权重计算
    private void genWeight(Map<String,Object> merchantPlan){
    	long  vipWeight = 9*10000*10000l;
        long  honestWeight = 8*1000*1000l;
        long  privateWeight = -7*1000*1000l;
    	
        long now = System.currentTimeMillis();
        
		long score=(now - ((Date)merchantPlan.get("joinTime")).getTime())/1000;
				
		//特权权重
		int vipStatus=(Integer) merchantPlan.get("vipStatus");
		if (vipStatus==2){
			score = score+vipWeight;
		}else if (this.hasBookContact(merchantPlan)){
			 score = score+honestWeight;
		}else{
			//无排序特权
		}
		
		
		//私人助理权重
		if (((Integer)merchantPlan.get("isPrivateAssistant"))==1){
					score = score + privateWeight;
		}
				
		merchantPlan.put("weight", score);	
    }
    
    
    /**
     * 生成认证类型
     */
    private void genAuthType(Map<String,Object> merchantPlan) {
        Object objEA =merchantPlan.get("enterpriseAuth");
        int intEnterpriseAuth = null == objEA ? 0 : Integer.parseInt(objEA.toString());

        Object objPA = merchantPlan.get("personalAuth");
        int intPersonalAuth = null == objPA ? 0 : Integer.parseInt(objPA.toString());

        if (intEnterpriseAuth > 0) {
        	merchantPlan.put("auth", 1); // 企业认证
        } else if (intPersonalAuth > 0) {
        	merchantPlan.put("auth", 2); // 个人认证
        } else {
        	merchantPlan.put("auth", 0); // 没有认证
        }
    }
    
    
    //生成VIP状态标志位
	private void genVipStatus(Map<String, Object> merchantPlan) {
		Map<String,Object> param =new HashMap<String,Object>();
		param.put("merchantId", merchantPlan.get("merchantId"));
		param.put("joinTime", merchantPlan.get("joinTime"));
		param.put("ruleCode", "vip_merchant_order");
		Integer existsNum=merchantPlanDao.getIncBookNum(param);
		if (existsNum>0){
			merchantPlan.put("vipStatus", 2);
		}else{
			merchantPlan.put("vipStatus", -1);
		}
		
	}
	
	/**
	 * 是否为签约商户
	 * @param merchantPlan
	 * @return
	 */
	private  boolean hasBookContact(Map<String, Object> merchantPlan) {
		
		Map<String,Object> param =new HashMap<String,Object>();
		param.put("merchantId", merchantPlan.get("merchantId"));
		param.put("joinTime", merchantPlan.get("joinTime"));
		param.put("ruleCode", "contract_merchant_order");
		Integer existsNum=merchantPlanDao.getIncBookNum(param);

		return existsNum>0?true:false;
		
	}
	
    
    
    private void genScore(Map<String,Object> merchantPlans){
    	   int count = (Integer) merchantPlans.get("totalEval");
    	   int re = 5;
           BigDecimal score = new BigDecimal(5);
           if (count != 0) {
               Integer totalAttitudeEvaluation = Integer
                       .parseInt(merchantPlans.get(
                               "totalAttitudeEvaluation") == null ? "0"
                               : merchantPlans.get(
                               "totalAttitudeEvaluation")
                               + "");
               Integer totalQualityEvaluation = Integer
                       .parseInt(merchantPlans.get(
                               "totalQualityEvaluation") == null ? "0"
                               : merchantPlans.get(
                               "totalQualityEvaluation")
                               + "");
               Integer totalSpeedEvaluation = Integer
                       .parseInt(merchantPlans.get(
                               "totalSpeedEvaluation") == null ? "0"
                               : merchantPlans.get(
                               "totalSpeedEvaluation")
                               + "");
               // 总服务态度评价+总服务质量评价+总服务速度评价
               Integer totalEvaluation = totalAttitudeEvaluation
                       + totalQualityEvaluation + totalSpeedEvaluation;
               // 星级
               BigDecimal starLevel = new BigDecimal(totalEvaluation)
                       .divide(new BigDecimal(count)
                                       .multiply(new BigDecimal(3)), 0,
                               BigDecimal.ROUND_HALF_UP);
               re = starLevel.intValue();
               // 分数
               score = new BigDecimal(totalEvaluation).divide(new BigDecimal(count).multiply(new BigDecimal(3)), 1, BigDecimal.ROUND_DOWN);
           }
           if (re > 5) {
               re = 5;
           }
           if (re < 0) {
               re = 0;
           }
           merchantPlans.put("merchantPoint", re);
           merchantPlans.put("score", score);
    }
    
    private void mySleep(long sec){
    	 try {
			Thread.sleep(sec*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 获取商户的认证信息。
     * @param merchantId
     * @return
     */
    private Map<String,Object> getAuthInfo(Long merchantId) {
    	    Map<String,Object> result=new HashMap<String,Object>(2);
    		Map<String, Object> enterpriseAuth = null;
    		Map<String, Object> personAuth = null;
    		List<Map<String,Object>> ListAuthInfo=merchantDao.selectMerchantAuthList(merchantId);
    		for (Map<String, Object> authInfo : ListAuthInfo) {
    			BusinessUtil.disposePath(authInfo, "path");
    			int authType = Integer
    					.parseInt(authInfo.get("authType") == null ? "0" : authInfo
    							.get("authType").toString());
    			if (authType == 1) {// 1-企业认证
    				enterpriseAuth = authInfo;
    			}
    			if (authType == 2) {// 2-个人认证
    				personAuth = authInfo;
    			}
    		}
    		if (enterpriseAuth==null && personAuth==null){
    				result.put("authStatus", 0);
    				result.put("auth", null);
    		}else if (enterpriseAuth!=null){
    				result.put("authStatus", 1);
    				result.put("auth", 2);
    		}else{
    			result.put("authStatus", 1);
				result.put("auth", 1);
    		}
    		return result;
    }
}
