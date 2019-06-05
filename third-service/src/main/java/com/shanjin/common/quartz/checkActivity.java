package com.shanjin.common.quartz;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.shanjin.service.IActivityService;

public class checkActivity {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(checkActivity.class);

	@Resource
	private IActivityService activityService;

	//定时查看1000万粉丝活动截止时间
	public void checkFansiActivity() {
		activityService.checkFansiActEndTime();
	}
	
}
