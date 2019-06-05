package com.shanjin.dubbo.provider;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.shanjin.service.ICommonService;
import com.shanjin.service.ICustomOrderService;
import com.shanjin.service.IUserOrderService;


/**
 * 手工触发定时调度
 * @author Revoke
 *
 */
public class ScheduleTrigger {
	IUserOrderService  userOrderService=null;
	
	public static void main(String[] args) throws Exception {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] {  "classpath*:spring/spring-rest.xml", "classpath*:spring/applicationContext.xml", "classpath*:spring/spring-mybatis.xml", "classpath*:spring/mq-beans.xml" });
		context.start();
		 
		new ScheduleTrigger().trigger(context);

		}
	
	private void trigger(ClassPathXmlApplicationContext context) {
		userOrderService= (IUserOrderService) context.getBean("userOrderService");
		
		returnFee();
		
//		purifyNoBidExpireOrder();
//	
//		purifyNoCoosedExpireOrder();
//		
//		purifyCancelOrder();
//		
//		purifyInProcessOrder();
	
		
	}
	
	
	
	
	public void returnFee(){
		while(true){
			int total =0;
			try{
				 total = userOrderService.handleReturnBidFee();
			}catch(Exception e){
				 System.out.println("已有报价方案过期，返回抢单金,处理失败");
				 e.printStackTrace();
		    	 break;
			}
		    if (total==0){
		    	break;
		    }
		    else {
		    	System.out.println("已有报价方案过期，返回抢单金,本次处理了"+total+"条");
		    	mySleeper(1*1000);
		    }
		}
		System.out.println("已有报价方案过期，返回抢单金处理完毕");	
	}
	
	
	//对于无报价方案的过期订单，清理推送记录及商户缓存。
	private void purifyNoBidExpireOrder(){
		
		while(true){
			int total=0;
			try{
					total =	userOrderService.handlePurifyNoBidExpireOrder();
			}catch(Exception e){
				System.out.println("无报价方案过期订单，清理出现异常");
				e.printStackTrace();
				break;
			}
		    if (total==0){
		    	break;
		    }
		    else {
		    	System.out.println("无报价方案过期订单，本次清理了"+total+"条");
		    	mySleeper(1*1000);
		    }
		}
		System.out.println("无报价方案过期订单--清理完毕");
	}
	
	 //对于未选定报价方案的过期订单，清理推送记录及商户缓存。
	  private void purifyNoCoosedExpireOrder(){
			
			while(true){
				int total = 0;
			    try{
			    	 total=userOrderService.handlePurifyNoChoosedOrders();
			    }catch(Exception e){
			    	 System.out.println("未选定方案过期订单，清理出现异常");
			    	 e.printStackTrace();
			    	 break;
			    }
			    if (total==0){
			    	break;
			    }
			    else {
			    	System.out.println("未选定方案过期订单，本次清理了"+total+"条");
			    	mySleeper(1*1000);
			    }
			}
			System.out.println("未选定方案过期订单--清理完毕");
		}
	
	  //对于用户主动取消的订单，清理推送记录及商户缓存。
	  private void purifyCancelOrder(){
			
			while(true){
				int total =0;
				try{
					 total = userOrderService.handlePurifyCancelOrder();
				}catch(Exception e){
					 System.out.println("主动取消的订单，清理出现异常");
					 e.printStackTrace();
			    	 break;
				}
			    if (total==0){
			    	break;
			    }
			    else {
			    	System.out.println("主动取消的订单，本次清理了"+total+"条");
			    	mySleeper(1*1000);
			    }
			}
			System.out.println("主动取消的过期订单--清理完毕");
		}
	  
	  
	  //对于用户主动取消的订单，清理推送记录及商户缓存。
	  private void purifyInProcessOrder(){
			
			while(true){
				int total=0;
				try{
					 total = userOrderService.handlePurifyInProcessOrder();
				}catch(Exception e){
					 System.out.println("进行中的订单，清理出现异常");
					 e.printStackTrace();
			    	 break;
				}
			    if (total==0){
			    	break;
			    }
			    else {
			    	System.out.println("进行中的订单，本次清理了"+total+"条");
			    	mySleeper(1*1000);
			    }
			}
			System.out.println("进行中的订单--清理完毕");
		}
	 
	private void mySleeper(long millions){
		try {
			Thread.sleep(millions); 
		} catch (InterruptedException e) {
		
		}
	}
	

}
