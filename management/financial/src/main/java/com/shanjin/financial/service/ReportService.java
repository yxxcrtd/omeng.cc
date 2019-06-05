package com.shanjin.financial.service;

/**
 * Created by Administrator on 2016/7/12.
 */
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.OrderInfo;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.util.ExcelExportUtil;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<String> getFirstDayToToday(ReqParamKit paramKit);
    Map<String, Map<String, String>> getDayPaymentsTotalReport(String firstDay, String lastDay);

    Map<String,Map<String,String>> getDayBalanceTotalReport(String firstDay, String lastDay);

    Map<String,Map<String,String>> getDayPettyCashTotalReport(String firstDay, String lastDay);

    Map<String,String> getSumInReport(String firstDay, String lastDay);

    Map<String,String> getSumPayReport(String firstDay, String lastDay);

    Map<String,String> getSumCashReport(String firstDay, String lastDay);

    Map<String,String> getSumReceiveReport(String firstDay, String lastDay);

    Map<String,String> getSumPreparedReport(String firstDay, String lastDay);

    List<Record> getUserAccountData(ReqParamKit paramKit);

    long getUserAccountDataTotals(ReqParamKit paramKit);

    Record getUserAccountTotals(ReqParamKit paramKit);

    List<OrderInfo> getExportUserAccountData();

    List<ExcelExportUtil.Pair> getExportUserAccountDataTitles();
}
