package com.shanjin.financial.job;

import com.shanjin.financial.service.BillService;
import com.shanjin.financial.service.impl.BillServiceImpl;
import com.shanjin.financial.util.DateUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/8/3.
 * 支付宝对账任务
 */
public class AlipayJob {
    private BillService billService = new BillServiceImpl();

    public void work() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        String day = DateUtil.formatDate("yyyy-MM-dd", c.getTime());

        billService.saveBillAlipay(day);
        billService.reconciliationAlipay(day);
    }
}
