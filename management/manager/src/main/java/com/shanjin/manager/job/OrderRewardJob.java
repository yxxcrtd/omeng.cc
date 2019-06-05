package com.shanjin.manager.job;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.manager.utils.AESUtil;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.DBUtil;
import com.shanjin.manager.utils.MessageUtil;
import com.shanjin.manager.utils.MqUtil;

/**
 * 订单奖励业务
 * @author Huang yulai
 *
 */
public class OrderRewardJob {
	private final static Log log = LogFactory.getLog(OrderRewardJob.class);
	private static float rewardTotal = 0f; // 日奖励订单总额
	/**
	 * 今日处理昨日已完成的订单
	 * 1.查询当前（前一天）奖励活动列表
	 * 1.订单奖励活动基本条件过滤
	 * 2.订单具体规则匹配
	 * 
	 */
	@Before(Tx.class)
	public static void work(){
		rewardTotal = 0f;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();	//  昨日
		String dateStr = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, yesterday);
	 	SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    log.info("订单奖励job start ! 处理开始时间："+time.format(new Date())+"  日期: " + dateStr);

	    try{
	    	handleReward(dateStr);
	    }catch(Exception e){
	    	e.printStackTrace();
	    	log.error(e.getMessage());
	    }

	    log.info("订单奖励job end ! 处理结束时间："+time.format(new Date())+"  日期: " + dateStr);

	    
	}
	
	private static void handleReward(String dateStr){
		long s = System.currentTimeMillis();
		 // ***************************活动基本配置校验start***************************
		List<Map<String,Object>> actList = getRewardActList(dateStr);
		if(actList==null||actList.size()<1){
			// 无奖励活动（城市或服务没有设置）
			return;
		}
	    // ***************************活动基本配置校验end***************************
	    // ***************************日完成订单查询start*************************** 
		List<Record> orderList = getCompleteOrderList(dateStr);
		if(orderList==null||orderList.size()<1){
			// 无线上完成订单
			log.info("无新完成线上支付订单");
			return;
		}else{
			log.info("新完成线上支付订单总数："+orderList.size());
		}
	    // ***************************日完成订单查询end*************************** 
		
		//*******************************按活动规则过滤订单start********************************
		for(Map<String,Object> actMap:actList){
			log.info("活动ID="+actMap.get("activityId")+"的活动匹配开始");
			int i=0;
			 Iterator<Record> iter = orderList.iterator();  
			 while(iter.hasNext()){  
				 Record order = iter.next();  
				 float rewardValue = matchRule(order,actMap);
				 if(rewardValue>=0){
						// 奖励订单
						Record reward = new Record();
						reward.set("merchant_id", order.getLong("merchant_id")).set("user_id", order.getLong("user_id")).set("amount", rewardValue)
						.set("order_id", order.getLong("id")).set("order_no", order.getStr("order_no")).set("activity_id", actMap.get("activityId"))
						.set("pay_price", order.getBigDecimal("order_actual_price")).set("province", order.getStr("province")).set("city", order.getStr("city"))
						.set("app_type", order.getStr("app_type")).set("service_id", order.getInt("service_type_id")).set("app_type", order.getStr("app_type"))
						.set("create_time", new Date()).set("pay_time", order.getDate("deal_time"));
						Db.save("activity_order_reward_detail", reward);
						
						rewardTotal = rewardTotal+rewardValue;
						iter.remove(); 
						i++;
				 }  
			 }  
			 log.info("活动ID="+actMap.get("activityId")+"的活动匹配结束,满足奖励的订单数为："+i); 
			 int handleType = StringUtil.nullToInteger(actMap.get("handleType"));//处理方式（0：手动，1：自动）
			 int rewardType = StringUtil.nullToInteger(actMap.get("rewardType"));//奖励方式（1：统一到账，2：次日到账）
			 if(rewardType==2&&handleType==1){
				 // 判断活动到账是否为自动到账
				 sendReward(actMap,dateStr);
			 }
			 
		}
		long e = System.currentTimeMillis();
	    log.info("==================批量处理订单奖励总耗时 ："+(e-s)+"ms====================");
		if(rewardTotal>0){
			Record record = new Record();
			record.set("run_time", (e-s)).set("reward_date", dateStr).set("reward_total", rewardTotal).set("create_time", new Date());
			Db.save("activity_order_reward_total", record);
		}
		//*******************************按活动规则过滤订单end********************************
	}
	
	/**
	 * 查询线上支付已完成的非免单订单
	 * @param dateStr
	 * @return
	 */
	private static List<Record> getCompleteOrderList(String dateStr){
		List<Record> list = null;
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT t.id,t.order_no,t.order_actual_price,t.deal_time,t.user_id,t.service_type_id,t.app_type,t.merchant_id,t.province,t.city ");
		sb.append(" FROM order_info t WHERE t.order_status=5 AND t.order_pay_type in(1,2) AND t.order_actual_price>0 ");
		sb.append(" AND DATE_FORMAT(t.deal_time,'%Y-%m-%d')='").append(dateStr).append("'");
		sb.append(" ORDER BY t.deal_time ");
		list = Db.find(sb.toString());
		return list;
	}
	
	/**
	 * 获取某日的订单活动列表
	 * @param dateStr
	 * @return
	 */
	private static List<Map<String,Object>> getRewardActList(String dateStr){
	    // 查询昨日还在活动中的奖励活动
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT t.activity_id,t.handle_type,t.reward_type,ai.stime,ai.etime,ai.is_pub ");
		sb.append(" FROM activity_order_reward t LEFT JOIN activity_info ai ON t.activity_id=ai.id ");
		sb.append(" WHERE ai.is_pub=1 ");
		sb.append(" AND DATE_FORMAT(ai.stime,'%Y-%m-%d')<='").append(dateStr).append("'");
		sb.append(" AND DATE_FORMAT(ai.etime,'%Y-%m-%d')>='").append(dateStr).append("'");
		
		List<Record> activityList = Db.find(sb.toString());
		List<Map<String,Object>> actList = new ArrayList<Map<String,Object>>();
		
		if(activityList==null||activityList.size()<1){
			// 无奖励活动
			return actList;
		}
	
		for(Record r:activityList){
			Map<String,Object> map = new HashMap<String,Object>();
			// 基本信息
			map.put("activityId", r.getLong("activity_id"));
			map.put("handleType", r.getInt("handle_type"));
			map.put("rewardType", r.getInt("reward_type"));
			map.put("stime", r.getDate("stime"));
			map.put("etime", r.getDate("etime"));
			Map<String,Object> openCity = getRewardCity(r.getLong("activity_id"));
			if(openCity==null||openCity.isEmpty()){
				// 无开放城市，无须匹配
				continue;
			}
			Map<String,Object> openService = getRewardService(r.getLong("activity_id"));
			if(openService==null||openService.isEmpty()){
				// 无服务类型，无须匹配
				continue;
			}
			List<Map<String,Object>> ruleList = getRewardRuleDetail(r.getLong("activity_id"));
			if(ruleList==null||ruleList.size()<1){
				// 无具体的奖励规则
				continue;
			}
			map.put("openCity", openCity);
			map.put("openService", openService);
			map.put("ruleList", ruleList);
			actList.add(map);
		}	
		
		if(actList==null||actList.size()<1){
			// 无奖励活动（城市或服务没有设置）
			log.info("当前时间无订单奖励活动！！！");
			return actList;
		}
		log.info("当前时间有"+actList.size()+"个正在进行的订单奖励活动！！！");
		
		return actList;
	}
	
	
	/**
	 * 获取奖励活动的开放城市
	 * @param activityId
	 * @return
	 */
	private static Map<String,Object> getRewardCity(Long activityId){
		Map<String,Object> map = new HashMap<String,Object>();
		List<Record> cityList = Db.find("SELECT t.province,t.city FROM activity_order_reward_city t WHERE t.activity_id="+activityId);
		if(cityList!=null&&cityList.size()>0){
			for(Record r :cityList){
				map.put(r.getStr("province")+r.getStr("city"), 1);
			}
		}else{
			log.info("活动ID="+activityId+"未设置开放城市！！！");
		}
		return map;
	}
	
	/**
	 * 获取奖励活动的服务类型
	 * @param activityId
	 * @return
	 */
	private static Map<String,Object> getRewardService(Long activityId){
		Map<String,Object> map = new HashMap<String,Object>();
		List<Record> serviceList = Db.find("SELECT t.service_id FROM activity_order_reward_service t WHERE t.activity_id="+activityId);
		if(serviceList!=null&&serviceList.size()>0){
			for(Record r :serviceList){
				map.put(r.getLong("service_id").toString(), 1);
			}
		}else{
			log.info("活动ID="+activityId+"未设置服务类型！！！");
		}
		return map;
	}
	
	/**
	 * 获取奖励活动的详细匹配规则
	 * @param activityId
	 * @return
	 */
	private static List<Map<String,Object>> getRewardRuleDetail(Long activityId){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Record> ruleList = Db.find("SELECT t.*,r.rule_type FROM activity_order_reward_rule_detail t LEFT JOIN activity_order_reward_rule r ON t.rule_id=r.id WHERE t.activity_id=? ORDER BY t.rule_group",activityId);
		if(ruleList!=null&&ruleList.size()>0){
			for(Record r :ruleList){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("activityId", r.getLong("activity_id"));
				map.put("ruleGroup", r.getInt("rule_group"));
			    map.put("ruleType", r.getInt("rule_type"));
			    map.put("extend1", r.getStr("extend1"));
			    map.put("extend2", r.getStr("extend2"));
			    map.put("extend3", r.getStr("extend3"));
			    map.put("extend4", r.getStr("extend4"));
			    map.put("extend5", r.getStr("extend5"));
			    list.add(map);
			}
		}
		return list;
	}
	
	
	/**
	 * 奖励规则匹配（用订单信息与某一奖励活动的规则匹配，匹配成功则计算奖励额度并返回；不成功返回0）
	 * @param order
	 * @param actMap
	 */
	private static float matchRule(Record order,Map<String,Object> actMap){
		float rewardValue = -1f;  //奖励金额
		boolean flag = false;
		// 1.订单城市过滤
		String city = order.getStr("province")+order.getStr("city");
		Map<String,Object> openCity = (Map<String, Object>) actMap.get("openCity");
		if(openCity!=null&&!openCity.isEmpty()){
			if(openCity.containsKey(city)){
				//城市匹配成功
				flag = true; 
			}else{
				flag = false; 
				return rewardValue;
			}
		}
		//  2.订单行业服务过滤
		if(flag){
			Integer service = order.getInt("service_type_id");
			Map<String,Object> openService = (Map<String, Object>) actMap.get("openService");
			if(openService!=null&&!openService.isEmpty()){
				if(openService.containsKey(service.toString())){
					//服务匹配成功
					flag = true; 
				}else{
					flag = false; 
					return rewardValue;
				}
			}
		}
		// 具体奖励规则匹配
		if(flag){
			// 1.校验总额是否达到上限
			List<Map<String,Object>> ruleList = (List<Map<String, Object>>) actMap.get("ruleList");
			if(ruleList!=null&&ruleList.size()>0){
				String stime = StringUtil.null2Str(actMap.get("stime"));
				float realReward = -1f; //实际奖励额度（最后达到上限时判断,默认-1为不限制）
				for(Map<String,Object> map : ruleList){
					Long activityId = StringUtil.nullToLong(map.get("activityId"));
					int ruleGroup = StringUtil.nullToInteger(map.get("ruleGroup"));
					int ruleType = StringUtil.nullToInteger(map.get("ruleType"));
					int extend1 = StringUtil.nullToInteger(map.get("extend1"));
					int extend2 = StringUtil.nullToInteger(map.get("extend2"));
					int extend3 = StringUtil.nullToInteger(map.get("extend3"));
					int extend4 = StringUtil.nullToInteger(map.get("extend4"));
					int extend5 = StringUtil.nullToInteger(map.get("extend5"));
					// ruleGroup = 1 或 2时,有限制，规则未满足，直接return

					if(ruleGroup==1){
						//奖励总额限制
						if(ruleType==1){
							// 有限制
							BigDecimal total = Db.queryBigDecimal("/*master*/ SELECT IFNULL(SUM(t.amount),0) AS total FROM activity_order_reward_detail t WHERE t.activity_id=? AND t.merchant_id=? ", activityId,order.getLong("merchant_id").toString());
							if(total.floatValue()<extend1){
								// 总额未到上限
								realReward = extend1-total.floatValue(); // 还可以奖励金额
								flag = true; 	
							}else{
								// 总额超过限制
								flag = false; 
								return rewardValue;
							}
							
						}else if(ruleType==2){
							// 不限制
							flag = true; 
						}
					}
					if(ruleGroup==2){
						//订单服务对象
						if(ruleType==1){
							// 首次完成订单用户
							long n = Db.queryLong("/*master*/ SELECT COUNT(1) FROM order_info t WHERE t.order_status=5 AND t.user_id=? AND t.deal_time<?", order.getLong("user_id").toString(),order.getDate("deal_time")); 
							if(n<1){
								// 是第一单
								flag = true;
							}else{
								// 不是第一单
								flag = false; 
								return rewardValue;
							}
							
						}else if(ruleType==2){
							//  校验该用户是否是前N单
							long n = Db.queryLong("/*master*/ SELECT COUNT(1) FROM activity_order_reward_detail t WHERE t.activity_id=? AND t.user_id=?",activityId,order.getLong("user_id").toString());
							if(n<extend1){
								// 是前N单
								flag = true;
							}else{
								// 不是前N单
								flag = false; 
								return rewardValue;
							}
						}else if(ruleType==3){
							// 不限制
							flag = true;
						}
					}
					// 匹配订单奖励区间
					if(ruleGroup==3){
						// 单笔奖励规则
						BigDecimal orderPrice = order.getBigDecimal("order_actual_price");
						  // 获取商户所在活动中自活动开始至当前这条订单所完成的奖励订单数
						long num = Db.queryLong("/*master*/ SELECT COUNT(1) FROM activity_order_reward_detail t WHERE t.merchant_id=? AND t.activity_id=?", order.getLong("merchant_id").toString(),activityId);   
						num++;
						if(num>=extend1&&num<=extend2){
							// 满足订单奖励区间
							if(orderPrice.floatValue()>=extend3){
								// 订单实付金额大于限制金额,奖励金额=固定奖励
					
								if(realReward<0){
									// 不限制
									rewardValue = extend5;
								}else{
									rewardValue = extend5;
									rewardValue = Math.min(rewardValue, realReward);
								}

							}else{
								// 订单实付金额小于限制金额
								if(realReward<0){
									// 不限制
									rewardValue = (float) (orderPrice.floatValue()*extend4/100.0);
								}else{
									rewardValue = (float) (orderPrice.floatValue()*extend4/100.0);
									rewardValue = Math.min(rewardValue, realReward);
								}
							}
							break;
						} 
					}
					
				}
			}
		}
		return rewardValue;
	}
	

	/**
	 * 向商户发放订单奖励
	 * @param actMap
	 * @param dateStr
	 */
	public static void sendReward(Map<String,Object> actMap,String dateStr){
		Connection conn = DBUtil.getConnection();
		if(conn==null){
			return;
		}
	    String sql="UPDATE merchant_statistics SET total_income_price=total_income_price+?,surplus_price=surplus_price+? WHERE merchant_id=?"; 
	    String sqldetail="UPDATE activity_order_reward_detail SET is_transfer=1,account_time=NOW() WHERE id=?"; 
	    String sqlpaydetail="insert into merchant_payment_details(merchant_id,payment_type,business_id,payment_price,payment_time) values(?,?,?,?,?)"; 
	    List<Record> rewardList = null;
	    long s = System.currentTimeMillis();
		try {
			rewardList = Db.find("/*master*/ SELECT t.* FROM activity_order_reward_detail t WHERE t.is_transfer=0 AND t.activity_id=? AND DATE_FORMAT(t.pay_time,'%Y-%m-%d')=?", actMap.get("activityId"),dateStr);
			
			if(rewardList!=null&&rewardList.size()>0){
				PreparedStatement prest = conn.prepareStatement(sql);
				PreparedStatement prestDetail = conn.prepareStatement(sqldetail);
				PreparedStatement prestPayDetail = conn.prepareStatement(sqlpaydetail);
				String date=DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date());
				for(Record r:rewardList){
					prest.setBigDecimal(1, r.getBigDecimal("amount")); // amount
					prest.setBigDecimal(2, r.getBigDecimal("amount")); // amount
					prest.setLong(3,  r.getLong("merchant_id")); // merchant_id
					
					prestDetail.setLong(1, r.getLong("id")); // id
					
					prestPayDetail.setLong(1, r.getLong("merchant_id")); // merchant_id
					prestPayDetail.setInt(2, 5); // payment_type
					prestPayDetail.setLong(3, r.getLong("order_no")); // business_id
					prestPayDetail.setBigDecimal(4, r.getBigDecimal("amount")); // payment_price
					prestPayDetail.setString(5, date);  // payment_time
					
					prest.addBatch();
					prestDetail.addBatch();
					prestPayDetail.addBatch();
				}
				
				prest.executeBatch();
				prestDetail.executeBatch();
				prestPayDetail.executeBatch();
				conn.commit();
				
				sendMessage(rewardList);
				writeMq(rewardList,date);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		long e = System.currentTimeMillis();
		log.info("批量发放订单奖励"+rewardList.size()+"条，耗时 ："+(e-s)+"ms");
	}
	

	private static void sendMessage(List<Record> rewardList) {
		for(Record r:rewardList){
		String activityName=Db.queryStr("select title from activity_info where id=?",r.getLong("activity_id"));
		MessageUtil.createCustomerMessage(null, 1, 2, r.getLong("user_id"), activityName, "O盟"+activityName+"奖励您"+r.getBigDecimal("amount")+"元，请查收"); // 生成消息
		}
	}

	private static void writeMq(List<Record> rewardList,String date) {
		Map<String,Object> configMap=new HashMap<String,Object>();
		String sql="SELECT me.user_id,me.phone,(select mi.name from merchant_info mi where mi.id=me.merchant_id) as name "
				+ " from merchant_employees me where me.employees_type=1 and me.merchant_id=? limit 1";
		String msg="";
		String encryptedText="";
		for(Record r:rewardList){
			try {
			List<Record> merInfo=Db.find(sql,r.getLong("merchant_id"));
			if(merInfo!=null&&merInfo.size()>0){
			configMap.put("merchantId", r.getLong("merchant_id"));
			configMap.put("merchantUserId", merInfo.get(0).getLong("user_id"));
			configMap.put("merchantUserPhone", merInfo.get(0).getStr("phone"));
			configMap.put("merchantName", merInfo.get(0).getStr("name"));
			configMap.put("userName", "");
			configMap.put("orderId", r.getStr("order_no"));
			configMap.put("rewardTime", date);
			configMap.put("transSeq", r.getLong("id"));
			configMap.put("transAmount", r.getBigDecimal("amount"));
			configMap.put("remark", "订单奖励");
			}
			msg=JSONObject.toJSONString(configMap);
			encryptedText= AESUtil.parseByte2HexStr(AESUtil.encrypt(msg, "367937E1967092280C56077755E4C65B"));
			MqUtil.writeToMQ("orderTemplate","opay.orderRewardExchange",encryptedText);
			} catch (Exception e) {
				CommonUtil.writeMqFailure(r.getLong("id"), encryptedText,"opay.orderRewardExchange", 2);
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String[] args){
		float f = 0.00f;
		if(f==0){
			System.out.println("ff===true");
		}else{
			System.out.println("ff===false");
		}
	}
	
}
