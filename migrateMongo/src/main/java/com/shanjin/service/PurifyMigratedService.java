package com.shanjin.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.util.JedisPoolUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IMerchantOrderDao;
import com.shanjin.dao.IMerchantPlanDao;
import com.shanjin.dao.IOrderDao;
import com.shanjin.dao.IScheduleDao;
import com.shanjin.dao.IServiceRecordDao;

/**
 * 历史订单清理服务
 * @author Revoke  2016.10.12
 *
 */
@Service
public class PurifyMigratedService {
	@Resource
	IScheduleDao  scheduleDao;
	
	@Resource
	IOrderDao orderDao;
	
	@Resource
	IMerchantOrderDao merchantOrderDao;
	
	@Resource
	IMerchantPlanDao  merchantPlanDao;
	
	
	@Resource
	IServiceRecordDao  serviceRecordDao;
	
	
	
	
	
	
	/**
	 * 获取最早已迁移到Mysql 但尚未清理的记录
	 * @return
	 */
	public Map<String,Object> getLastMigrateRecord(){
		return getLastMigrateMySqlRec(scheduleDao.MIGRATE_USER_ORDER,scheduleDao.MIGRATE_USER_ORDER_CANCEL);
	}
	
	
	
	/**
	 * 清理迁移记录对应的订单 的缓存
	 * @throws Exception 
	 */
	public List<Map<String,Object>>  purifyCache(Map<String,Object> lastMigrateRec) throws Exception{
		List<Map<String, Object>> orders = null;			
		Jedis jedis=null;
		
		try{
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("last_migrateTime", lastMigrateRec.get("lastBusinessDataTime"));
				param.put("lastOrderId", lastMigrateRec.get("lastOrderId"));
				
				switch(lastMigrateRec.get("catalog").toString()){
							case "CANCEL":
									orders = orderDao.getNeedPurifyCancelOrders(param);
									break;
							case "NOBID":
								orders = orderDao.getNeedPurifyNobidOrders(param);
								break;
							case "NOCHOOSED":
								orders = orderDao.getNeedPurifyNochoosedOrders(param);
								break;
							case "NORMAL":
								orders = orderDao.getNeedPurifyNormalOrders(param);
								break;
				}
				if (orders!=null && orders.size()>0){				
						 jedis=JedisPoolUtil.getJedis();
						 String orderId=null;
						 String userId=null;
						 Map<String,Object> orderParam = new HashMap<String,Object>();
						 
						 for(Map<String,Object> order:orders){
							 Pipeline pipeLine = jedis.pipelined();
							 
							 orderId=order.get("id").toString();
							 orderParam.put("orderId", orderId);
							 //获取本次待清理的被取消的订单,清理该订单关联的商户订单缓存
							 List<Map<String,Object>> pushMerchants=merchantOrderDao.getMerchantsForSpeicalOrder(orderParam);
							 if (pushMerchants!=null && pushMerchants.size()>0){
									
								 	for(Map<String,Object> pushMerchant:pushMerchants){
								 		 	String merchantPushOrderMapKey=CacheConstants.MERCHANT_PUSH_ORDER_PREFIX+pushMerchant.get("merchant_id");
								 		 	String merchantPushOrderListKey=CacheConstants.MERCHANT_PUSH_ORDER_IDS+pushMerchant.get("merchant_id");
								 		 	pipeLine.hdel(merchantPushOrderMapKey, orderId);
								 		 	pipeLine.lrem(merchantPushOrderListKey, 1, orderId);
								 	}
							
							 }
							 
							 //清理该订单对应的用户侧缓存。
							 userId=order.get("user_id").toString();
							 
							 pipeLine.hdel(CacheConstants.ORDER_TO_USER,
										orderId);
							 
							 pipeLine.hdel(CacheConstants.ORDER_USER_KEY + userId,orderId);
							 
							 pipeLine.sync();
						 }
				  }	 
				  return  orders;	 		 	
			}catch(Exception e){
							 	throw new Exception("迁移到Mysql订单--缓存清理跑批失败",e);
			}finally{
						 if (jedis!=null){
								 JedisPoolUtil.returnRes(jedis);
							 }
			}
	}
	
	
	/**
	 * 删除相关表中数据
	 * @param orders
	 */
	@Transactional(rollbackFor = Exception.class)
	public void removeMigratedRec(List<Map<String, Object>> orders,Long id) 
	{
	 	
	 	Map<String,Object> oderParam = new HashMap<String,Object>(1);
	 	String orderIds=getIds(orders,"id");
	 	oderParam.put("ids", orderIds);
	 	
	 	String planIds = merchantPlanDao.getMerchantPlanIds(oderParam);
	 	Map<String,Object> planParam =new HashMap<String,Object>(1);
	 	planParam.put("ids", planIds);
	 	
	 	
	 	String serviceIds = null;
	 	if (planIds!=null){
	 		serviceIds = serviceRecordDao.getServiceRecordByOrderIds(oderParam);
	 	}
	 	Map<String,Object> serviceParam =new HashMap<String,Object>(1);
	 	
	 	serviceParam.put("ids", serviceIds);
	 	
	 	if (serviceIds!=null && !serviceIds.equals("")){
	 			serviceRecordDao.delServiceRecordAttByIds(serviceParam);
	 			serviceRecordDao.delServiceRecordByIds(serviceParam);
	 	}
	 	
	 	
	 	if (planIds!=null && !planIds.equals("")){
	 		merchantPlanDao.delMerchantPlansAttachmentsIds(planParam);
		 	merchantPlanDao.delMerchantPlansDetailsByIds(planParam);
		 	merchantPlanDao.delMerchantPlansGoods(planParam);
		 	merchantPlanDao.delMerchantPlansByIds(oderParam);
	 	}
	 	
	 	
	 	orderDao.delUserOrderAttachments(oderParam);
	 	orderDao.delUserOrderDetail(oderParam);
	 	orderDao.delUserOrderByIds(oderParam);
	 	
	 	
	 	//设置当前迁移记录清理标志位为1
	 	scheduleDao.updateRecToPurity(id);	
	}
	
	
	/**
	 * 读取最近一次成功迁移到Mysql的记录
	 * @param businessType
	 * @param catalog
	 * @return
	 */
	private Map<String, Object> getLastMigrateMySqlRec(String businessType,String catalog) {
		Map<String,String> param = new HashMap<String,String>(2);
		param.put("businessType", businessType);
			
		Map<String,Object> lastMigrateRec=scheduleDao.getLastMigrateMysqlRec(param);
	
		return lastMigrateRec;
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
	
}
