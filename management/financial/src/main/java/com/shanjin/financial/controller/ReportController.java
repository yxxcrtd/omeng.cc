package com.shanjin.financial.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.*;
import com.shanjin.financial.service.*;
import com.shanjin.financial.service.impl.*;
import com.shanjin.financial.util.ExcelExportUtil;
import com.shanjin.financial.util.StringUtil;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ReportController extends Controller {
    private OrderService service = new OrderServiceImpl();
    private ActivityFensiService activityFensiService = new ActivityFensiServiceImpl();
    private MerchantEmployeesNumApplyService employeesNumApplyService = new MerchantEmployeesNumApplyServiceImpl();
    private ActivityCuttingDetailService cuttingDetailService = new ActivityCuttingDetailServiceImpl();
    private ActivityOrderRewardService orderRewardService = new ActivityOrderRewardServiceImpl();
    private ExportService exportService = ExportService.service;
    private ReportService reportService = new ReportServiceImpl();

    /**
     * 已完成订单
     */
    public void orderComplete() {
        this.render("orderComplete.jsp");
    }

    /**
     * 订单数据
     */
    public void orderData() {
        List<OrderInfo> orderInfoList = service.getOrderInfoData(ReqParamKit.getInstance(this.getParaMap()));
        long totals = service.getOrderInfoDataTotals(ReqParamKit.getInstance(this.getParaMap()));//Db.queryLong("SELECT COUNT(*) FROM order_info WHERE is_del = 0");

        this.renderJson(new NormalResponse(orderInfoList, totals));
    }

    /**
     * 订单汇总信息
     */
    public void orderTotals() {
        Record record = service.getOrderTotals(ReqParamKit.getInstance(this.getParaMap()));
        double prices = record.get("prices") == null ? 0 : record.getBigDecimal("prices").doubleValue();

        this.renderText(String.format("订单笔数：%s笔，总金额：%s元", record.get("totals"), String.format("%.2f", prices)));
    }

    /**
     * 导出订单数据
     * <p>
     * 备注：为防止数据量太多导致内存溢出，目前设定到处条数为 5000
     */
    public void exportOrderData() {
        List<OrderInfo> list = service.getExportOrderData();
        for (OrderInfo order : list) {
            if (1 == StringUtil.nullToInteger(order.get("order_pay_type")))
                order.set("order_pay_type", "支付宝");
            else if (2 == StringUtil.nullToInteger(order.get("order_pay_type")))
                order.set("order_pay_type", "微信");
            else if (3 == StringUtil.nullToInteger(order.get("order_pay_type")))
                order.set("order_pay_type", "现金");

            if (1 == StringUtil.nullToInteger(order.get("order_status")))
                order.set("order_status", "新预约");
            else if (2 == StringUtil.nullToInteger(order.get("order_status")))
                order.set("order_status", "待选择");
            else if (3 == StringUtil.nullToInteger(order.get("order_status")))
                order.set("order_status", "已确认");
            else if (4 == StringUtil.nullToInteger(order.get("order_status")))
                order.set("order_status", "已完成");
            else if (5 == StringUtil.nullToInteger(order.get("order_status")))
                order.set("order_status", "支付完成");
            else if (6 == StringUtil.nullToInteger(order.get("order_status")))
                order.set("order_status", "订单已过期");
            else if (7 == StringUtil.nullToInteger(order.get("order_status")))
                order.set("order_status", "无效订单");
        }
        List<ExcelExportUtil.Pair> titles = service.getExportOrderDataTitles();
        exportService.export(getResponse(), getRequest(), list, titles, "订单明细", 0);
        this.renderNull();
    }

    /**
     * 千万粉丝活动明细
     */
    public void fansActivityDetail() {
        this.render("fansActivityDetail.jsp");
    }

    public void fansActivityData() {
        List<ActivityFensiPaymentDetail> list = activityFensiService.getFansActivityData(ReqParamKit.getInstance(this.getParaMap()));
        long totals = activityFensiService.getFansActivityDataTotals(ReqParamKit.getInstance(this.getParaMap()));

        this.renderJson(new NormalResponse(list, totals));
    }

    public void exportFansActivityData() {
        List<ActivityFensiPaymentDetail> list = activityFensiService.getExportFansActivityData();
        List<ExcelExportUtil.Pair> titles = activityFensiService.getExportFansActivityTitles();
        exportService.export(getResponse(), getRequest(), list, titles, "千万粉丝活动明细", 0);

        this.renderNull();
    }

    /**
     * 增值服务明细
     */
    public void vatServiceDetail() {
        this.render("vatServiceDetail.jsp");
    }

    /**
     * 增值服务数据
     */
    public void vatServiceData() {
        List<Record> list = employeesNumApplyService.getVatServiceData(ReqParamKit.getInstance(this.getParaMap()));
        long totals = employeesNumApplyService.getVatServiceDataTotals(ReqParamKit.getInstance(this.getParaMap()));

        this.renderJson(new NormalResponse(list, totals));
    }

    /**
     * 增值服务数据统计
     */
    public void vatServiceTotals() {
        Record record = employeesNumApplyService.getVatServiceTotals(ReqParamKit.getInstance(this.getParaMap()));

        this.renderText(String.format("订单笔数：%s笔，总金额：%s元", record.get("totals"), String.format("%.2f", record.getDouble("prices"))));
    }

    /**
     * 剪彩红包明细
     */
    public void giftDetail() {
        this.render("giftDetail.jsp");
    }

    /**
     * 剪彩红包数据
     */
    public void giftData() {
        List<ActivityCuttingDetail> list = cuttingDetailService.getGiftData(ReqParamKit.getInstance(this.getParaMap()));
        long totals = cuttingDetailService.getGiftDataTotals(ReqParamKit.getInstance(this.getParaMap()));

        this.renderJson(new NormalResponse(list, totals));

    }

    public void giftTotals() {
        Record record = cuttingDetailService.getGiftTotals(ReqParamKit.getInstance(this.getParaMap()));

        this.renderText(String.format("订单笔数：%s笔，总金额：%s元", record.get("totals"), String.format("%.2f", record.getDouble("prices"))));
    }

    /**
     * 订单奖励明细
     */
    public void orderRewardDetail() {
        this.render("orderRewardDetail.jsp");
    }

    /**
     * 订单奖励数据
     */
    public void orderRewardData() {
        final List<ActivityOrderRewardDetail> orderRewardData = orderRewardService.getOrderRewardData(ReqParamKit.getInstance(this.getParaMap()));
        List<ActivityOrderRewardDetail> list = orderRewardData;

        long totals = 0;
        try {
            totals = orderRewardService.getOrderRewardDataTotals(ReqParamKit.getInstance(this.getParaMap()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(new NormalResponse(list, totals));
    }

    /**
     * 订单奖励汇总显示
     */
    public void orderRewardTotals() {
        Record record = orderRewardService.getOrderRewardTotals(ReqParamKit.getInstance(this.getParaMap()));

        this.renderText(String.format("订单笔数：%s笔，总金额：%s元", record.get("totals"), String.format("%.2f", record.getDouble("prices"))));
    }

    /**
     * 处理时间查询
     */
    public void resolveDateTime(ReqParamKit paramKit){
        Calendar now = Calendar.getInstance();
        //前5年
        List<Integer> yearList = new ArrayList<>();
        int year = now.get(Calendar.YEAR);
        for(int i=-5;i<=0;i++){
            yearList.add(year + i);
        }
        List<Integer> monthList = new ArrayList<>();
        int month = now.get(Calendar.MONTH) + 1;
        for(int i=1;i<=12;i++)
            monthList.add(i);

        setAttr("yearList", yearList);
        setAttr("monthList", monthList);
        setAttr("year", paramKit.has("year")?paramKit.getString("year"):year);
        setAttr("month", paramKit.has("month")?paramKit.getString("month"):month);
    }
    /**
     * 收/付款汇总日计表
     */
    public void dayPaymentsTotalReport() {
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
        resolveDateTime(paramKit);
        //生成日期，本月1号 - 当前日期
        List<String> dateList = reportService.getFirstDayToToday(paramKit);

        String firstDay = dateList.get(0);
        String lastDay = dateList.get(dateList.size() - 1);

        /**************查询数据***************/
        Map<String, Map<String, String>> reportMap = reportService.getDayPaymentsTotalReport(firstDay, lastDay);

        setAttr("dateList", dateList);
        setAttr("reportMap", reportMap);

        this.render("dayPaymentsTotalReport.jsp");
    }

    /**
     * 收/支汇总日计表
     */
    public void dayBalanceTotalReport() {
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
        resolveDateTime(paramKit);
        //生成日期，本月1号 - 当前日期
        List<String> dateList = reportService.getFirstDayToToday(paramKit);

        String firstDay = dateList.get(0);
        String lastDay = dateList.get(dateList.size() - 1);

        Map<String, Map<String, String>> reportMap = reportService.getDayBalanceTotalReport(firstDay, lastDay);

        setAttr("dateList", dateList);
        setAttr("reportMap", reportMap);

        this.render("dayBalanceTotalReport.jsp");
    }

    /**
     * 客户备付金汇总日计表
     */
    public void dayPettyCashTotalReport() {
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
        resolveDateTime(paramKit);
        //生成日期，本月1号 - 当前日期
        List<String> dateList = reportService.getFirstDayToToday(paramKit);

        String firstDay = dateList.get(0);
        String lastDay = dateList.get(dateList.size() - 1);

        Map<String, Map<String, String>> reportMap = reportService.getDayPettyCashTotalReport(firstDay, lastDay);

        setAttr("dateList", dateList);
        setAttr("reportMap", reportMap);

        this.render("dayPettyCashTotalReport.jsp");
    }

    /**
     * 导出Excel
     */
    public void htmlToExcel(){
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());

        if(paramKit.has("html")){
            File f = new File(paramKit.getString("filename")+".xls");
            if(f.exists())
                f.delete();
            try {
                f.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriterWithEncoding(f, "utf-8"));
                String html = paramKit.getString("html");
                html = "<html xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">\n" +
                        " <head>\n" +
                        "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n" +
                        "  <meta name=\"ProgId\" content=\"Excel.Sheet\"/>\n" +
                        "  <meta name=\"Generator\" content=\"WPS Office ET\"/><style>\n" +
                        ".x-num\n" +
                        "\t{mso-style-parent:style0;\n" +
                        "\tmso-number-format:\"0\";\n" +
                        "\twhite-space:normal;\n" +
                        "\tmso-font-charset:134;}\n" +
                        ".x-money\n" +
                        "\t{mso-style-parent:style0;\n" +
                        "\tmso-number-format:\"0.00\";\n" +
                        "\twhite-space:normal;\n" +
                        "\tmso-font-charset:134;}\n" +
                        ".x-date\n" +
                        "\t{mso-style-parent:style0;\n" +
                        "\tmso-number-format:\"yyyy/m/d\\\\ h:mm:ss\";\n" +
                        "\twhite-space:normal;\n" +
                        "\tfont-size:10.0pt;\n" +
                        "\tfont-family:Arial Unicode MS;\n" +
                        "\tmso-font-charset:134;}"+
                        "</style></head>"+html;
                html += "</html>";

                bw.write(html);
                bw.close();
                getResponse().setContentType("application/ms-excel");
                getResponse().setCharacterEncoding("UTF-8");
                this.renderFile(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            this.renderNull();
        }
    }


    /**
     * 收款统计表
     */
    public void sumInReport(){
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
        //生成日期，本月1号 - 当前日期
        List<String> dateList = reportService.getFirstDayToToday(paramKit);

        String firstDay = paramKit.has("firstDay")?paramKit.getString("firstDay"):dateList.get(0);
        String lastDay = paramKit.has("lastDay")?paramKit.getString("lastDay"):dateList.get(dateList.size() - 1);

        Map<String, String> reportMap = reportService.getSumInReport(firstDay, lastDay);


        this.setAttr("firstDay", firstDay);
        this.setAttr("lastDay", lastDay);
        this.setAttr("reportMap", reportMap);

        this.render("sumInReport.jsp");
    }

    /**
     * 付款统计表
     */
    public void sumPayReport(){
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
        //生成日期，本月1号 - 当前日期
        List<String> dateList = reportService.getFirstDayToToday(paramKit);

        String firstDay = paramKit.has("firstDay")?paramKit.getString("firstDay"):dateList.get(0);
        String lastDay = paramKit.has("lastDay")?paramKit.getString("lastDay"):dateList.get(dateList.size() - 1);

        Map<String, String> reportMap = reportService.getSumPayReport(firstDay, lastDay);


        this.setAttr("firstDay", firstDay);
        this.setAttr("lastDay", lastDay);
        this.setAttr("reportMap", reportMap);

        this.render("sumPayReport.jsp");
    }

    /**
     * 费用统计表
     */
    public void sumCashReport(){
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
        //生成日期，本月1号 - 当前日期
        List<String> dateList = reportService.getFirstDayToToday(paramKit);

        String firstDay = paramKit.has("firstDay")?paramKit.getString("firstDay"):dateList.get(0);
        String lastDay = paramKit.has("lastDay")?paramKit.getString("lastDay"):dateList.get(dateList.size() - 1);

        Map<String, String> reportMap = reportService.getSumCashReport(firstDay, lastDay);


        this.setAttr("firstDay", firstDay);
        this.setAttr("lastDay", lastDay);
        this.setAttr("reportMap", reportMap);

        this.render("sumCashReport.jsp");
    }

    /**
     * 收入统计表
     */
    public void sumReceiveReport(){
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
        //生成日期，本月1号 - 当前日期
        List<String> dateList = reportService.getFirstDayToToday(paramKit);

        String firstDay = paramKit.has("firstDay")?paramKit.getString("firstDay"):dateList.get(0);
        String lastDay = paramKit.has("lastDay")?paramKit.getString("lastDay"):dateList.get(dateList.size() - 1);

        Map<String, String> reportMap = reportService.getSumReceiveReport(firstDay, lastDay);


        this.setAttr("firstDay", firstDay);
        this.setAttr("lastDay", lastDay);
        this.setAttr("reportMap", reportMap);

        this.render("sumReceiveReport.jsp");
    }

    /**
     * 客户备付金统计表
     */
    public void sumPreparedReport(){
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
        //生成日期，本月1号 - 当前日期
        List<String> dateList = reportService.getFirstDayToToday(paramKit);

        String firstDay = paramKit.has("firstDay")?paramKit.getString("firstDay"):dateList.get(0);
        String lastDay = paramKit.has("lastDay")?paramKit.getString("lastDay"):dateList.get(dateList.size() - 1);

        Map<String, String> reportMap = reportService.getSumPreparedReport(firstDay, lastDay);


        this.setAttr("firstDay", firstDay);
        this.setAttr("lastDay", lastDay);
        this.setAttr("reportMap", reportMap);

        this.render("sumPreparedReport.jsp");
    }

    /**
     * 用户资产
     */
    public void userAccountReport(){
        this.render("userAccountReport.jsp");
    }

    /**
     * 用户资产明细数据
     */
    public void userAccountData() {
        List<Record> orderInfoList = reportService.getUserAccountData(ReqParamKit.getInstance(this.getParaMap()));
        long totals = reportService.getUserAccountDataTotals(ReqParamKit.getInstance(this.getParaMap()));//Db.queryLong("SELECT COUNT(*) FROM order_info WHERE is_del = 0");

        this.renderJson(new NormalResponse(orderInfoList, totals));
    }

    /**
     * 订单汇总信息
     */
    public void userAccountTotals() {
        Record record = reportService.getUserAccountTotals(ReqParamKit.getInstance(this.getParaMap()));
        double prices = record.get("prices") == null ? 0 : record.getBigDecimal("prices").doubleValue();

        this.renderText(String.format("累计余额：%s元，累计可提现余额：%s元", record.get("totals"), String.format("%.2f", prices)));
    }

    /**
     * 导出订单数据
     * <p>
     * 备注：为防止数据量太多导致内存溢出，目前设定到处条数为 5000
     */
    public void exportUserAccountData() {
        try {
            List<OrderInfo> list = reportService.getExportUserAccountData();
            List<ExcelExportUtil.Pair> titles = reportService.getExportUserAccountDataTitles();
            exportService.export(getResponse(), getRequest(), list, titles, "用户资产明细", 0);

        }catch (Exception e){
            e.printStackTrace();
        }
        this.renderNull();
    }

}
