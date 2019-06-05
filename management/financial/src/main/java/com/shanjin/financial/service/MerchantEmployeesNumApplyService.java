package com.shanjin.financial.service;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.MerchantEmployeesNumApply;
import com.shanjin.financial.bean.ReqParamKit;

import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 * 顾问号申请
 */
public interface MerchantEmployeesNumApplyService {
    List<Record> getVatServiceData(ReqParamKit paramKit);

    long getVatServiceDataTotals(ReqParamKit paramKit);

    Record getVatServiceTotals(ReqParamKit paramKit);
}
