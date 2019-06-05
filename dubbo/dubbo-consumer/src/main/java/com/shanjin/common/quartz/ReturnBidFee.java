package com.shanjin.common.quartz;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.shanjin.service.IUserOrderService;

/**
 * 返还抢单金
 * @author Revoke
 *
 */
public class ReturnBidFee {
		
		// 本地异常日志记录对象
		private static final Logger logger = Logger.getLogger(ReturnBidFee.class);

		@Resource
		private IUserOrderService userOrderService;
		
		
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

		private void mySleeper(long millions){
			try {
				Thread.sleep(millions); 
			} catch (InterruptedException e) {
			
			}
		}
}
