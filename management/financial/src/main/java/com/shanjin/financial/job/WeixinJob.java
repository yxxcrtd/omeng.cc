package com.shanjin.financial.job;

import com.shanjin.financial.service.BillService;
import com.shanjin.financial.service.impl.BillServiceImpl;
import com.shanjin.financial.util.DateUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/8/3.
 * 微信对账任务
 */
public class WeixinJob {
    private BillService billService = new BillServiceImpl();

    public void work() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        String day = DateUtil.formatDate("yyyyMMdd", c.getTime());

        billService.saveBillWx(day);
        billService.reconciliationWx(day);
    }
}
