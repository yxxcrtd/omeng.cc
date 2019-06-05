package com.shanjin.common.quartz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.shanjin.service.ICustomOrderService;
import com.shanjin.service.IPushService;

public class PushToPrivateAssistant {

	@Resource
	private IPushService pushService;
	
	@Resource
	private ICustomOrderService customOrderService;
	
	public void push(){
		
		//查询30分钟无报价方案的订单
		 try {
			 
			List<Map<String,Object>> pushOrderList=customOrderService.getPushOrderList();
			if(pushOrderList==null || pushOrderList.size()==0){			
				return ;
			}
			
			//订单推送
	        Map<String,Object> pushMap=new HashMap<String, Object>();    
	        pushMap.put("pushOrderList",pushOrderList);      
	        pushMap.put("data", "");
	        pushMap.put("pushType", 20);		       
			pushService.basicPush(pushMap);
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
