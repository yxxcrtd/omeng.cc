package com.shanjin.common.quartz;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.shanjin.common.util.DateUtil;
import com.shanjin.service.IUserOrderService;


/**
 * 清理订单推送记录
 * @author Revoke Yu 2016.7.12
 *
 */

public class PurifyPushOrder {
	
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(PurifyPushOrder.class);

	@Resource
	private IUserOrderService userOrderService;

	
	public void purify() {
		
		if (!purifyNoBidExpireOrder()){
			 return;   //未获得批清理锁
		}
		
		if (!purifyNoCoosedExpireOrder()){
			 return;   //未获得批清理锁
		}
		
		if (!purifyCancelOrder()){
			return;   //未获得批清理锁
		}
		
		purifyInProcessOrder();

	}
	
	
	//对于无报价方案的过期订单，清理推送记录及商户缓存。
	private boolean purifyNoBidExpireOrder(){
		int total=0;
		while(true){
			total=0;
			try{
					total =	userOrderService.handlePurifyNoBidExpireOrder();
			}catch(Exception e){
				System.out.println("无报价方案过期订单，清理出现异常");
				e.printStackTrace();
				break;
			}
		    if (total<1){
		    	break;
		    }
		    else {
		    	System.out.println("无报价方案过期订单，本次清理了"+total+"条");
		    	mySleeper(5*1000);
		    }
		}
		if (total==-1){
				return false;
		}else{
				System.out.println("无报价方案过期订单--清理完毕");
				return true;
		}
	}
	
	 //对于未选定报价方案的过期订单，清理推送记录及商户缓存。
	  private boolean purifyNoCoosedExpireOrder(){
		    int total = 0;
			while(true){
				total = 0;
			    try{
			    	 total=userOrderService.handlePurifyNoChoosedOrders();
			    }catch(Exception e){
			    	 System.out.println("未选定方案过期订单，清理出现异常");
			    	 e.printStackTrace();
			    	 break;
			    }
			    if (total<1){
			    	break;
			    }
			    else {
			    	System.out.println("未选定方案过期订单，本次清理了"+total+"条");
			    	mySleeper(5*1000);
			    }
			}
			if (total==-1){
				 return false;
			}else{
				System.out.println("未选定方案过期订单--清理完毕");
				return true;
			}
		}
	
	  //对于用户主动取消的订单，清理推送记录及商户缓存。
	  private boolean purifyCancelOrder(){
		    int total =0;
			while(true){
				total =0;
				try{
					 total = userOrderService.handlePurifyCancelOrder();
				}catch(Exception e){
					 System.out.println("主动取消的订单，清理出现异常");
					 e.printStackTrace();
			    	 break;
				}
			    if (total<1){
			    	break;
			    }
			    else {
			    	System.out.println("主动取消的订单，本次清理了"+total+"条");
			    	mySleeper(5*1000);
			    }
			}
			if (total==-1){
				return false;
			}else{
				System.out.println("主动取消的过期订单--清理完毕");
				return true;
	  		}
		}
	  
	  
	  //对于用户主动取消的订单，清理推送记录及商户缓存。
	  private boolean purifyInProcessOrder(){
		    int total =0;
			while(true){
				total=0;
				try{
					 total = userOrderService.handlePurifyInProcessOrder();
				}catch(Exception e){
					 System.out.println("进行中的订单，清理出现异常");
					 e.printStackTrace();
			    	 break;
				}
			    if (total<1){
			    	break;
			    }
			    else {
			    	System.out.println("进行中的订单，本次清理了"+total+"条");
			    	mySleeper(5*1000);
			    }
			}
			if (total==-1){
					return false; 
			}else{
					System.out.println("进行中的订单--清理完毕");
					return true;
			}
		}
	 
	private void mySleeper(long millions){
		try {
			Thread.sleep(millions); 
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
}
