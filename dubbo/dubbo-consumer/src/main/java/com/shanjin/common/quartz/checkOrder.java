package com.shanjin.common.quartz;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.shanjin.common.util.DateUtil;
import com.shanjin.service.IUserOrderService;

public class checkOrder {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(checkOrder.class);

	@Resource
	private IUserOrderService userOrderService;

	
	//已有报价方案，用户预期未选择
	public void checkNoChoosedOrder() {
		while(true){
		    int total =	userOrderService.handleNoChoosedOrders();
		    if (total==0){
		    	break;
		    }
		    else {
		    	System.out.println("已有报价方案，用户未选择,本次处理了"+total+"条");
		    	try {
					Thread.sleep(60*1000);  //让数据库主从同步跑一回。
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
		    }
		}
		System.out.println("已有报价方案，用户未选择处理完毕");
	
	}
	
	
	//无报价方案的订单
	public void checkNoBidOrder() {
		while(true){
			int total=userOrderService.handNoBidOrder();
			if (total==0){
				break;
			}else{
				System.out.println("无报价方案的订单处理完毕，本次处理了"+total+"条");
				try {
					Thread.sleep(60*1000);  //让数据库主从同步跑一回。
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
		}
		System.out.println("无报价方案的订单处理完毕");
		
	}
	
	
	public void cleanUserCache() {
		 userOrderService.cleanUserCache();
		 logger.info(DateUtil.getNowYYYYMMDDHHMMSS() + ">>用户缓存清理完毕");
	}
}
