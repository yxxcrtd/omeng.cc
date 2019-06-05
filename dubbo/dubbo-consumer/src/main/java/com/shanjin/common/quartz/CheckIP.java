package com.shanjin.common.quartz;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.shanjin.cache.service.IIpCityCacheService;
import com.shanjin.service.IUserOrderService;

public class CheckIP {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(CheckIP.class);
	
	
	@Resource
	private IIpCityCacheService ipCityCacheService;
	
	
	public void cleanIpCachec(){
		ipCityCacheService.cleanOldIpCache();
		logger.info("ip-city cach 清理完毕");
	}
}
