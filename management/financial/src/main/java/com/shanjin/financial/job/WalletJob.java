package com.shanjin.financial.job;

import com.shanjin.financial.service.BillService;
import com.shanjin.financial.service.impl.BillServiceImpl;
import com.shanjin.financial.util.DateUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/8/3.
 * 钱包对账任务
 */
public class WalletJob {
    private BillService billService = new BillServiceImpl();

    public void work() {
        System.out.println("Start WalletJob.....");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        String day = DateUtil.formatDate("yyyy-MM-dd", c.getTime());

        billService.saveWallet(day);
        billService.reconciliationWallet(day);
    }
}
