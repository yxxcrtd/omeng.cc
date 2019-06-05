package com.shanjin.common.quartz;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.shanjin.common.util.DateUtil;
import com.shanjin.service.ICustomOrderService;
import com.shanjin.service.IUserOrderService;

/**
 * 商户侧订单定时调度处理类
 * @author revokeYu  2016.6.11
 *
 */

public class CheckMerchantOrder {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(CheckMerchantOrder.class);

	@Resource
	private ICustomOrderService orderService;

	
	//迁移推送订单到历史表中。
	public void moveToHistoryPushOrder() {
		while(true){
		    int total =	orderService.moveToHistoryPushOrder();
		    if (total==0){
		    	break;
		    }
		    else {
		    	System.out.println("已迁移"+total+"条订单到历史表中");
		    	try {
					Thread.sleep(60*1000);  //让数据库主从同步跑一会。
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
		    }
		}
		System.out.println("历史订单迁移处理完毕");
	
	}
	
	/**
	 * 清理商户侧推送订单缓存
	 */
	public void cleanMerchantCache() {
		 orderService.cleanMerchantCache();
		 logger.info(DateUtil.getNowYYYYMMDDHHMMSS() + ">>商户侧订单推送缓存清理完毕");
	}
}
