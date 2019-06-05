package com.shanjin.financial.service;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.OrderInfo;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.util.ExcelExportUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public interface OrderService {
    List<OrderInfo> getOrderInfoData(ReqParamKit instance);

    List<OrderInfo> getExportOrderData();

    List<ExcelExportUtil.Pair> getExportOrderDataTitles();

    Record getOrderTotals(ReqParamKit paramKit);

    long getOrderInfoDataTotals(ReqParamKit paramKit);
}
