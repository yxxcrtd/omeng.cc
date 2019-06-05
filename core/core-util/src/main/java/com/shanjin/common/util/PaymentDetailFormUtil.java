package com.shanjin.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 交易详情 表单处理
 * @author Administrator
 *
 */
public class PaymentDetailFormUtil {
	//组装订单交易表单详情
	public static Map<String,Object> constructOrderpaymentForm(Map<String,Object> paymentMap){
		Map<String,Object> formMap = new HashMap<String,Object>();
		
		List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
		Iterator<Map.Entry<String, Object>> iterator = paymentMap.entrySet().iterator();
		String key = null;
		int level =0;
		while(iterator.hasNext()){
			level = 0;
			key= null;
			Map.Entry<String, Object> entry = iterator.next();
			Map<String,Object> itemMap = new HashMap<String,Object>();
			if("paymentTypeDesc".equals(entry.getKey()) && entry.getValue()!=null){
				key ="交易类型";
				level = 1;
			}else if("detailsName".equals(entry.getKey()) && entry.getValue()!=null){
				key ="服务项目";
				level = 2; 
			}else if("userPhone".equals(entry.getKey()) && entry.getValue()!=null){
				key ="支付用户";
				level = 3;
			}else if("paymentTime".equals(entry.getKey()) && entry.getValue()!=null){
				key ="时间";
				level = 4;
			}else if("orderNo".equals(entry.getKey()) && entry.getValue()!=null){
				key ="订单号";
				level = 5;
			}else if("orderPayTypeName".equals(entry.getKey()) && entry.getValue()!=null){
				key ="支付方式";
				level = 6;
			}else if("tradeNo".equals(entry.getKey()) && entry.getValue()!=null){
				key ="交易流水";
				level =7;
			}else if("remark".equals(entry.getKey()) && entry.getValue()!=null){
				key ="备注";
				level =8;
			}
			
			if(!StringUtil.isEmpty(key) && !StringUtil.isEmpty(entry.getValue())){
				itemMap.put("key", key);
				itemMap.put("value", entry.getValue());
				itemMap.put("level", level);
				items.add(itemMap);
			}
		}		
		  Collections.sort(items, new Comparator<Map<String, Object>>() {
			   public int compare(Map<String, Object> o1, Map<String, Object> o2) {
	                return (int) o1.get("level") - (int) o2.get("level");
	            }
	        });
		  
		  //title标题
		  formMap.put("title", paymentMap.get("paymentTypeDesc"));
		  	 
		  //金钱展示区域
		  Integer orderPayType = (Integer) paymentMap.get("orderPayType");
		  
		  //线下支付和免单详情不展示金额		  
		  if ( null == orderPayType || ( 3 != orderPayType && 4 != orderPayType)){
			  Map<String,Object> moneyLable = new HashMap<String,Object>();
			  moneyLable.put("key", "金额(元)");
			  moneyLable.put("value",  paymentMap.get("paymentPrice"));
			  moneyLable.put("payFlag", paymentMap.get("payFlag"));
			  formMap.put("head", moneyLable);
		  }	 
		  //详细信息区域			 
		  formMap.put("items", items);
    	return formMap;
	}
	
}
