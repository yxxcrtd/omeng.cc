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

	// 处理过期订单
	public void checkOrder() {
//		int resultNum = userOrderService.handlebackorders();
//		logger.info(DateUtil.getNowYYYYMMDDHHMMSS() + "处理 " + resultNum + " 条过期订单");
	}
}
