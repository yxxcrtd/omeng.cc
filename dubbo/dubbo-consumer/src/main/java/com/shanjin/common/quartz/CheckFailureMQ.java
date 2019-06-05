package com.shanjin.common.quartz;

import com.shanjin.service.ICustomOrderService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CheckFailureMQ {

	// 日志
	private static final Logger logger = Logger.getLogger(CheckFailureMQ.class);

    @Resource
    private ICustomOrderService customOrderService;

    public void reWrite() throws Exception {
        try {
            customOrderService.dealUnSentList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
