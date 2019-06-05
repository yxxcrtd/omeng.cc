package com.shanjin.common.quartz;

import com.shanjin.carinsur.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/6
 * @desc 券过期检查任务的调度
 */
public class VoucherExpireTask {

    @Autowired
    private VoucherService voucherService;

    private static final Logger logger = LoggerFactory.getLogger(VoucherExpireTask.class);

    public void checkExpireVouchers(){
        //查询所有未使用的 过期时间
        logger.info("批量更新过期券任务调度开始....");
        int count = voucherService.updateExpireUserVouchers();
        logger.info("批量更新过期券任务调度完成，本次任务更新记录数为：{}条",count);
    }
}
