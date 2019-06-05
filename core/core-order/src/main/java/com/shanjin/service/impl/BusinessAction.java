package com.shanjin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 用户行为编码
 * @author RevokeYu 2016.5.13
 *
 */
public class BusinessAction {
	
		//新订单无报价方案过期时间配置KEY 
		public static final String  NO_BID_ORDER="expires_day";	
	
		//用户未选择报价方案的超时配置KEY
		public static final String  NO_CHOOSED_ORDER="expires_choice_day";	
		
		
		//用户取消订单后，清理间隔时间
		public static final String  PURIFY_CANCEL_ORDER="purify_cancel_expires_day";	
		
		//订单处理中，清理间隔时间
		public static final String  PURIFY_INPROCESS_ORDER="purify_inprocess_expires_day";
		

		//未选定报价方案过期订单，清理间隔时间
		public static final String  PURIFY_NOCHOOSED_ORDER="purify_nochoised_expires_day";

		
		//无报价方案过期订单，清理间隔时间
		public static final String  PURIFY_NOBID_ORDER="purify_nobid_expires_day";
		
		
		
		//报价方案过期订单，返还抢单金间隔时间
		public static final String  REETURN_BID_FEE="return_bid_fee_expires_day";
		
	
		//用户下订单
		public static final String  PLACE_ORDER ="200";
		
		
		//选定方案
		public static final String  CHOOSE_ORDER="210";
		
		
		//用户投诉
		public  static final String  COMPLAIN="220";
		
		
		//支付订单
		public static final String  PAY_ORDER="230";
		
		//订单预期
		public static final String  PAST_ORDER="330";
		
		
		
		//评价订单
		public static final String  COMMENT_ORDER="240";
		
		//用户取消订单未报价
		public static final String  CANCEL_ORDER="250";
		
		//用户取消订单已报价未选择
		public static final String  CANCEL_ORDER_HAVEPLAN="260";
		

		//无报价方案---订单超时取消
		public static final String  TIMEOUT_ORDER="280";
		
		
		//未选择报价方案---订单超时取消
		public static final String  TIMEOUT_NOT_CHOOSE="290";
		
		
		
		//商家报价
		public static final String  BID_ORDER ="300";
		
		//商家确定已提供完服务
		public static final String  FINISH_SERVER ="310";
		
		
		//商家确认支付
		public static final String  CONFIRM_PAY ="320";
		
		
		public static Map<String,String>  merchantAcionMap;
		
		static {
			 merchantAcionMap = new HashMap<String,String>();
			 merchantAcionMap.put(PLACE_ORDER, "发布时间");
			 merchantAcionMap.put(BID_ORDER, "报价时间");
			 merchantAcionMap.put(CHOOSE_ORDER, "服务开始");
			 merchantAcionMap.put(FINISH_SERVER, "完成交付");
			 merchantAcionMap.put(PAY_ORDER, "完成服务");
			 merchantAcionMap.put(CANCEL_ORDER, "取消订单");
			 merchantAcionMap.put(COMMENT_ORDER, "用户评价");
			 merchantAcionMap.put(PAST_ORDER, "逾期时间");
			 merchantAcionMap.put(TIMEOUT_NOT_CHOOSE, "逾期时间");
			 merchantAcionMap.put(TIMEOUT_ORDER, "逾期时间");			
		}
		public static List<Map<String,String>>  merchantAcionAll;
		
		static {
			merchantAcionAll=new ArrayList<Map<String,String>>();
			Map<String,String> merchantAcionMap = new HashMap<String,String>();
			merchantAcionMap.put("title", "需求发布");
			merchantAcionMap.put("code", PLACE_ORDER);
			merchantAcionAll.add(merchantAcionMap);
			
			merchantAcionMap = new HashMap<String,String>();
			merchantAcionMap.put("title", "提供报价方案");
			merchantAcionMap.put("code", BID_ORDER);
			merchantAcionAll.add(merchantAcionMap);				
			
			merchantAcionMap = new HashMap<String,String>();
			merchantAcionMap.put("title", "服务中");
			merchantAcionMap.put("code", CHOOSE_ORDER);
			merchantAcionAll.add(merchantAcionMap);	
			
			merchantAcionMap = new HashMap<String,String>();
			merchantAcionMap.put("title", "等待收款");
			merchantAcionMap.put("code", FINISH_SERVER);
			merchantAcionAll.add(merchantAcionMap);	
			
			merchantAcionMap = new HashMap<String,String>();
			merchantAcionMap.put("title", "服务完成");
			merchantAcionMap.put("code", PAY_ORDER);
		}
		public static Map<String,String> getMerchantActionMap(){
			  return merchantAcionMap;
		}
		
		
		public static Map<String,String> getClientActionMap(){
			  return merchantAcionMap;
		}
		
		
		public static void main(String[] args) {
				Map<String,String> map = getMerchantActionMap();
				for(Entry<String, String> entry:map.entrySet()){
						System.out.println(entry.getKey()+"---->"+entry.getValue());
				}
			
		}

}
