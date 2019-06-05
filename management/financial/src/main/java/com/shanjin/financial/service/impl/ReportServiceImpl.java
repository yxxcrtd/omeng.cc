package com.shanjin.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.OrderInfo;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.service.ReportService;
import com.shanjin.financial.util.DateUtil;
import com.shanjin.financial.util.ExcelExportUtil;
import com.shanjin.financial.util.MD5Util;
import com.shanjin.financial.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2016/7/12.
 */
public class ReportServiceImpl implements ReportService {
    @Override
    public List<String> getFirstDayToToday(ReqParamKit paramKit) {
        List<String> dateList = new ArrayList<>();

        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int year = paramKit.has("year") ? paramKit.getInt("year") : now.get(Calendar.YEAR);
        int month = paramKit.has("month") ? paramKit.getInt("month") - 1 : now.get(Calendar.MONTH);

        int curDay = now.get(Calendar.DAY_OF_MONTH);
        //如果查询的月份小于当前月，那么显示全月数据
        if (month < now.get(Calendar.MONTH)) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, 1);
            curDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        for (int i = 1; i <= curDay; i++) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, i);
            dateList.add(sdf.format(c.getTime()));
        }

        return dateList;
    }

    /**
     * 收/付款汇总日计表
     *
     * @param firstDay
     * @param lastDay
     * @return
     */
    @Override
    public Map<String, Map<String, String>> getDayPaymentsTotalReport(String firstDay, String lastDay) {
        firstDay += " 00:00:00";
        lastDay += " 23:59:59";
        Map<String, Map<String, String>> reportMap = new HashMap<>();
        String sql = "";
        List<Record> list = null;
        //1. 客户备付金增加 - 钱包充值笔数+钱包充值金额
        sql = "SELECT LEFT(bill_create_time,10) as daytime, COUNT(*)AS totals, SUM(bill_amount) AS prices FROM w_bill\n" +
                "WHERE bill_type = 6 AND bill_create_time IS NOT NULL\n" +
                " and bill_create_time >= '" + firstDay + "'" + " and bill_create_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(bill_create_time,10)";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjzj_qbcz_1", r.get("totals") + "");
            rMap.put("khbfjzj_qbcz_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }

        //2. 客户备付金增加 - 商户订单笔数+商户订单金额
        sql = "SELECT LEFT(join_time,10) as daytime, COUNT(*)AS totals, SUM(order_actual_price) AS prices FROM order_info\n" +
                "WHERE order_status = 5 AND order_pay_type BETWEEN 1 AND 2 AND join_time IS NOT NULL\n" +
                " and join_time >= '" + firstDay + "'" + " and join_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(join_time,10)";
        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjzj_shdj_1", r.get("totals") + "");
            rMap.put("khbfjzj_shdj_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }
        //3. 客户备付金增加 - 剪彩红包笔数+剪彩红包金额
        sql = "SELECT LEFT(join_time,10) AS daytime, COUNT(*)AS totals, SUM(price) AS prices FROM activity_cutting_detail\n" +
                "WHERE join_time IS NOT NULL\n" +
                " and join_time >= '" + firstDay + "'" + " and join_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(join_time,10)";

        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjzj_jchb_1", r.get("totals") + "");
            rMap.put("khbfjzj_jchb_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }

        //4. 增值服务线上支付 + 增值服务线下支付
        getZzfw(firstDay, lastDay, reportMap);

        //5. 财务付款 - 提现
        sql = "SELECT LEFT(withdraw_create_time,10) AS daytime, COUNT(*)AS totals, SUM(withdraw_money) AS prices FROM w_withdraw\n" +
                "WHERE withdraw_status = 20 AND withdraw_create_time IS NOT NULL\n" +
                " and withdraw_create_time >= '" + firstDay + "'" + " and withdraw_create_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(withdraw_create_time,10)";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("cwfk_tx_1", r.get("totals") + "");
            rMap.put("cwfk_tx_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }

        return reportMap;
    }

    /**
     * 获取增值服务金额和笔数，按日期分组
     *
     * @param firstDay
     * @param lastDay
     * @param reportMap
     */
    private void getZzfw(String firstDay, String lastDay, Map<String, Map<String, String>> reportMap) {
        String sql;
        List<Record> list;
        sql = "SELECT 'VIP' AS t, pay_type AS payType, LEFT(open_time,10) AS daytime ,COUNT(*) AS totals, SUM(money) AS prices FROM merchant_vip_apply WHERE open_time >= '" + firstDay + "'" + " and open_time <= '" + lastDay + "' and open_time IS NOT NULL GROUP BY LEFT(open_time, 10),pay_type\n";
        sql += "UNION ALL\n";
        sql += "SELECT '顾问号' AS t, pay_type AS payType, LEFT(open_time,10) AS daytime ,COUNT(*) AS totals, SUM(money) AS prices FROM merchant_employees_num_apply WHERE open_time >= '" + firstDay + "'" + " and open_time <= '" + lastDay + "' and  open_time IS NOT NULL GROUP BY LEFT(open_time, 10),pay_type\n";
        sql += "UNION ALL\n";
        sql += "SELECT '抢单金' AS t, pay_type AS payType, LEFT(open_time,10) AS daytime ,COUNT(*) AS totals, SUM(topup_money) AS prices FROM merchant_topup_apply WHERE open_time >= '" + firstDay + "'" + " and open_time <= '" + lastDay + "' and  open_time IS NOT NULL GROUP BY LEFT(open_time, 10),pay_type";

        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            String type = r.getStr("t");
            int payType = StringUtil.nullToInteger(r.get("payType"));
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            long totals = 0;
            double prices = 0;
            if ("VIP".equals(type)) {
                if (payType == 1 || payType == 2) { //微信、支付宝
                    totals = r.getLong("totals");
                    if (rMap.get("zzfwxs_vip_1") != null)
                        totals = Long.parseLong(rMap.get("zzfwxs_vip_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (rMap.get("zzfwxs_vip_2") != null)
                        prices = Double.parseDouble(rMap.get("zzfwxs_vip_2")) + prices;

                    rMap.put("zzfwxs_vip_1", totals + "");
                    rMap.put("zzfwxs_vip_2", prices + "");
                } else if (payType == 3) { //现金
                    totals = r.getLong("totals");
                    if (rMap.get("zzfwxx_vip_1") != null)
                        totals = Long.parseLong(rMap.get("zzfwxx_vip_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (rMap.get("zzfwxx_vip_2") != null)
                        prices = Double.parseDouble(rMap.get("zzfwxx_vip_2")) + prices;

                    rMap.put("zzfwxx_vip_1", totals + "");
                    rMap.put("zzfwxx_vip_2", prices + "");
                }
            } else if ("顾问号".equals(type)) {
                if (payType == 1 || payType == 2) { //微信、支付宝
                    totals = r.getLong("totals");
                    if (rMap.get("zzfwxs_gwh_1") != null)
                        totals = Long.parseLong(rMap.get("zzfwxs_gwh_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (rMap.get("zzfwxs_gwh_2") != null)
                        prices = Double.parseDouble(rMap.get("zzfwxs_gwh_2")) + prices;

                    rMap.put("zzfwxs_gwh_1", totals + "");
                    rMap.put("zzfwxs_gwh_2", prices + "");
                } else if (payType == 3) { //现金
                    totals = r.getLong("totals");
                    if (rMap.get("zzfwxx_gwh_1") != null)
                        totals = Long.parseLong(rMap.get("zzfwxx_gwh_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (rMap.get("zzfwxx_gwh_2") != null)
                        prices = Double.parseDouble(rMap.get("zzfwxx_gwh_2")) + prices;

                    rMap.put("zzfwxx_gwh_1", totals + "");
                    rMap.put("zzfwxx_gwh_2", prices + "");
                }
            } else if ("抢单金".equals(type)) {
                if (payType == 1 || payType == 2) { //微信、支付宝
                    totals = r.getLong("totals");
                    if (rMap.get("zzfwxs_qdj_1") != null)
                        totals = Long.parseLong(rMap.get("zzfwxs_qdj_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (rMap.get("zzfwxs_qdj_2") != null)
                        prices = Double.parseDouble(rMap.get("zzfwxs_qdj_2")) + prices;
                    rMap.put("zzfwxs_qdj_1", totals + "");
                    rMap.put("zzfwxs_qdj_2", prices + "");
                } else if (payType == 3) { //现金
                    totals = r.getLong("totals");
                    if (rMap.get("zzfwxx_qdj_1") != null)
                        totals = Long.parseLong(rMap.get("zzfwxx_qdj_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (rMap.get("zzfwxx_qdj_2") != null)
                        prices = Double.parseDouble(rMap.get("zzfwxx_qdj_2")) + prices;

                    rMap.put("zzfwxx_qdj_1", totals + "");
                    rMap.put("zzfwxx_qdj_2", prices + "");
                }
            }

            reportMap.put(date, rMap);
        }
    }

    /**
     * 获取增值服务金额和笔数，不按日期分组
     *
     * @param firstDay
     * @param lastDay
     * @param reportMap
     */
    private void getZzfw2(String firstDay, String lastDay, Map<String, String> reportMap) {
        String sql;
        List<Record> list;
        sql = "SELECT 'VIP' AS t, pay_type AS payType ,COUNT(*) AS totals, SUM(money) AS prices FROM merchant_vip_apply WHERE open_time >= '" + firstDay + "'" + " and open_time <= '" + lastDay + "' and open_time IS NOT NULL GROUP BY pay_type\n";
        sql += "UNION ALL\n";
        sql += "SELECT '顾问号' AS t, pay_type AS payType ,COUNT(*) AS totals, SUM(money) AS prices FROM merchant_employees_num_apply WHERE open_time >= '" + firstDay + "'" + " and open_time <= '" + lastDay + "' and  open_time IS NOT NULL GROUP BY pay_type\n";
        sql += "UNION ALL\n";
        sql += "SELECT '抢单金' AS t, pay_type AS payType ,COUNT(*) AS totals, SUM(topup_money) AS prices FROM merchant_topup_apply WHERE open_time >= '" + firstDay + "'" + " and open_time <= '" + lastDay + "' and  open_time IS NOT NULL GROUP BY pay_type";


        list = Db.find(sql);
        for (Record r : list) {
            String type = r.getStr("t");
            int payType = StringUtil.nullToInteger(r.get("payType"));
            long totals = 0;
            double prices = 0;
            if ("VIP".equals(type)) {
                if (payType == 1 || payType == 2) { //微信、支付宝
                    totals = r.getLong("totals");
                    if (reportMap.get("zzfwxs_vip_1") != null)
                        totals = Long.parseLong(reportMap.get("zzfwxs_vip_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (reportMap.get("zzfwxs_vip_2") != null)
                        prices = Double.parseDouble(reportMap.get("zzfwxs_vip_2")) + prices;

                    reportMap.put("zzfwxs_vip_1", totals + "");
                    reportMap.put("zzfwxs_vip_2", prices + "");
                } else if (payType == 3) { //现金
                    totals = r.getLong("totals");
                    if (reportMap.get("zzfwxx_vip_1") != null)
                        totals = Long.parseLong(reportMap.get("zzfwxx_vip_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (reportMap.get("zzfwxx_vip_2") != null)
                        prices = Double.parseDouble(reportMap.get("zzfwxx_vip_2")) + prices;

                    reportMap.put("zzfwxx_vip_1", totals + "");
                    reportMap.put("zzfwxx_vip_2", prices + "");
                }
            } else if ("顾问号".equals(type)) {
                if (payType == 1 || payType == 2) { //微信、支付宝
                    totals = r.getLong("totals");
                    if (reportMap.get("zzfwxs_gwh_1") != null)
                        totals = Long.parseLong(reportMap.get("zzfwxs_gwh_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (reportMap.get("zzfwxs_gwh_2") != null)
                        prices = Double.parseDouble(reportMap.get("zzfwxs_gwh_2")) + prices;

                    reportMap.put("zzfwxs_gwh_1", totals + "");
                    reportMap.put("zzfwxs_gwh_2", prices + "");
                } else if (payType == 3) { //现金
                    totals = r.getLong("totals");
                    if (reportMap.get("zzfwxx_gwh_1") != null)
                        totals = Long.parseLong(reportMap.get("zzfwxx_gwh_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (reportMap.get("zzfwxx_gwh_2") != null)
                        prices = Double.parseDouble(reportMap.get("zzfwxx_gwh_2")) + prices;

                    reportMap.put("zzfwxx_gwh_1", totals + "");
                    reportMap.put("zzfwxx_gwh_2", prices + "");
                }
            } else if ("抢单金".equals(type)) {
                if (payType == 1 || payType == 2) { //微信、支付宝
                    totals = r.getLong("totals");
                    if (reportMap.get("zzfwxs_qdj_1") != null)
                        totals = Long.parseLong(reportMap.get("zzfwxs_qdj_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (reportMap.get("zzfwxs_qdj_2") != null)
                        prices = Double.parseDouble(reportMap.get("zzfwxs_qdj_2")) + prices;
                    reportMap.put("zzfwxs_qdj_1", totals + "");
                    reportMap.put("zzfwxs_qdj_2", prices + "");
                } else if (payType == 3) { //现金
                    totals = r.getLong("totals");
                    if (reportMap.get("zzfwxx_qdj_1") != null)
                        totals = Long.parseLong(reportMap.get("zzfwxx_qdj_1")) + totals;
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                    if (reportMap.get("zzfwxx_qdj_2") != null)
                        prices = Double.parseDouble(reportMap.get("zzfwxx_qdj_2")) + prices;

                    reportMap.put("zzfwxx_qdj_1", totals + "");
                    reportMap.put("zzfwxx_qdj_2", prices + "");
                }
            }
        }
    }

    /**
     * 收/支汇总日计表
     *
     * @param firstDay
     * @param lastDay
     * @return
     */
    @Override
    public Map<String, Map<String, String>> getDayBalanceTotalReport(String firstDay, String lastDay) {
        firstDay += " 00:00:00";
        lastDay += " 23:59:59";
        Map<String, Map<String, String>> reportMap = new HashMap<>();
        String sql = "";
        List<Record> list = null;
        //1.线上支付收入+线下支付收入
        getZzfw(firstDay, lastDay, reportMap);

        //2. 钱包支付收入
        // TODO: 2016/7/22 钱包模块暂时不支持支付，等支持支付时再补充

        //3. 费用支出 - 订单奖励
        sql = "SELECT LEFT(create_time, 10) AS daytime, COUNT(*) AS totals, SUM(amount) AS prices FROM activity_order_reward_detail\n" +
                "WHERE create_time IS NOT NULL\n" +
                " and create_time >= '" + firstDay + "'" + " and create_time <= '" + lastDay + " '\n" +
                "GROUP BY LEFT(create_time, 10)";
        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("fyzc_ddjl_1", r.get("totals") + "");
            rMap.put("fyzc_ddjl_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }
        //3.1 费用支出 - 活动奖励
        sql = "SELECT LEFT(pay_time, 10) AS daytime, COUNT(*) AS totals, SUM(fans_price+rank_price) AS prices FROM activity_fensi_payment_detail\n" +
                "WHERE pay_time IS NOT NULL AND STATUS = 1\n" +
                " and pay_time >= '" + firstDay + "'" + " and pay_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(pay_time, 10)";
        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("fyzc_hdjl_1", r.get("totals") + "");
            rMap.put("fyzc_hdjl_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }

        return reportMap;
    }

    /**
     * 客户备付金汇总日计表
     *
     * @param firstDay
     * @param lastDay
     * @return
     */
    @Override
    public Map<String, Map<String, String>> getDayPettyCashTotalReport(String firstDay, String lastDay) {
        firstDay += " 00:00:00";
        lastDay += " 23:59:59";
        Map<String, Map<String, String>> reportMap = new HashMap<>();
        String sql = "";
        List<Record> list = null;

        //1. 客户备用金增加
        //1.1 钱包充值
        sql = "SELECT LEFT(bill_create_time,10) as daytime, COUNT(*)AS totals, SUM(bill_amount) AS prices FROM w_bill\n" +
                "WHERE bill_type = 6 AND bill_create_time IS NOT NULL\n" +
                " and bill_create_time >= '" + firstDay + "'" + " and bill_create_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(bill_create_time,10)";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjzj_qbcz_1", r.get("totals") + "");
            rMap.put("khbfjzj_qbcz_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }

        //1.2 商户订单
        sql = "SELECT LEFT(join_time,10) as daytime, COUNT(*)AS totals, SUM(order_actual_price) AS prices FROM order_info\n" +
                "WHERE order_status = 5 AND join_time IS NOT NULL\n" +
                " and join_time >= '" + firstDay + "'" + " and join_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(join_time,10)";
        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjzj_shdd_1", r.get("totals") + "");
            rMap.put("khbfjzj_shdd_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }
        //1.3 剪彩红包
        sql = "SELECT LEFT(join_time,10) AS daytime, COUNT(*)AS totals, SUM(price) AS prices FROM activity_cutting_detail\n" +
                "WHERE join_time IS NOT NULL\n" +
                " and join_time >= '" + firstDay + "'" + " and join_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(join_time,10)";

        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjzj_jchb_1", r.get("totals") + "");
            rMap.put("khbfjzj_jchb_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }
        //1.4 订单奖励
        sql = "SELECT LEFT(create_time, 10) AS daytime, COUNT(*) AS totals, SUM(amount) AS prices FROM activity_order_reward_detail\n" +
                "WHERE create_time IS NOT NULL\n" +
                " and create_time >= '" + firstDay + "'" + " and create_time <= '" + lastDay + " '\n" +
                "GROUP BY LEFT(create_time, 10)";
        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjzj_ddjl_1", r.get("totals") + "");
            rMap.put("khbfjzj_ddjl_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }
        //1.5 活动奖励
        sql = "SELECT LEFT(pay_time, 10) AS daytime, COUNT(*) AS totals, SUM(fans_price+rank_price) AS prices FROM activity_fensi_payment_detail\n" +
                "WHERE pay_time IS NOT NULL AND STATUS = 1\n" +
                " and pay_time >= '" + firstDay + "'" + " and pay_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(pay_time, 10)";
        list = Db.find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjzj_hdjl_1", r.get("totals") + "");
            rMap.put("khbfjzj_hdjl_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }


        //2. 客户备付金减少
        //2.1 用户提现
        sql = "SELECT LEFT(withdraw_create_time,10) AS daytime, COUNT(*)AS totals, SUM(withdraw_money) AS prices FROM w_withdraw\n" +
                "WHERE withdraw_status = 20 AND withdraw_create_time IS NOT NULL\n" +
                " and withdraw_create_time >= '" + firstDay + "'" + " and withdraw_create_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(withdraw_create_time,10)";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("khbfjjs_tx_1", r.get("totals") + "");
            rMap.put("khbfjjs_tx_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }
        //2.2 购买VIP特权+顾问号+抢单金（统计商户通过钱包支付的VIP特权+顾问号+抢单金的笔数和金额）
        // TODO: 2016/7/22 钱包模块暂时不支持支付，等支持支付时再补充


        //3. 备付金内部交易
        //3.1 商户订单收入（统计商户收到用户通过钱包支付的订单的笔数和金额）
        sql = "SELECT LEFT(bill_create_time,10) as daytime, COUNT(*)AS totals, SUM(bill_amount) AS prices FROM w_bill\n" +
                "WHERE bill_type = 1 AND bill_create_time IS NOT NULL\n" +
                " and bill_create_time >= '" + firstDay + "'" + " and bill_create_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(bill_create_time,10)";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            String date = r.getStr("daytime");
            Map<String, String> rMap = reportMap.get(date);
            if (rMap == null)
                rMap = new HashMap<>();
            rMap.put("bfjnbjy_shddsr_1", r.get("totals") + "");
            rMap.put("bfjnbjy_shddsr_2", r.get("prices") == null ? "0" : r.get("prices") + "");

            reportMap.put(date, rMap);
        }
        //3.2 用户订单支付（统计用户通过钱包支付订单的笔数和金额）
        // TODO: 2016/7/22 钱包模块暂时不支持支付，等支持支付时再补充


        //4. 备付金净值（钱包充值金额+商户订单金额+剪彩红包金额+订单奖励金额+活动奖励金额-用户提现金额-购买VIP特权金额-购买顾问号金额-购买抢单金金额）
        //@see dayPettyCashTotalReport.jsp 通过页面脚本计算


        return reportMap;
    }

    /**
     * 收款统计表
     *
     * @param firstDay
     * @param lastDay
     * @return
     */
    @Override
    public Map<String, String> getSumInReport(String firstDay, String lastDay) {
        firstDay += " 00:00:00";
        lastDay += " 23:59:59";
        Map<String, String> reportMap = new HashMap<>();
        String sql = "";
        List<Record> list = null;

        //1. 客户备付金增加
        //1.1 钱包充值
        sql = "SELECT COUNT(*)AS totals, SUM(bill_amount) AS prices FROM w_bill\n" +
                "WHERE bill_type = 6 AND bill_create_time IS NOT NULL\n" +
                " and bill_create_time >= '" + firstDay + "'" + " and bill_create_time <= '" + lastDay + "'";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_qbcz_1", r.get("totals") + "");
            reportMap.put("khbfjzj_qbcz_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }
        //1.2 商户订单
        sql = "SELECT COUNT(*)AS totals, SUM(order_actual_price) AS prices FROM order_info\n" +
                "WHERE order_status = 5 and order_pay_type between 1 and 2 AND join_time IS NOT NULL\n" +
                " and join_time >= '" + firstDay + "'" + " and join_time <= '" + lastDay + "'";
        list = Db.find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_shdd_1", r.get("totals") + "");
            reportMap.put("khbfjzj_shdd_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }
        //1.3 剪彩红包
        sql = "SELECT COUNT(*)AS totals, SUM(price) AS prices FROM activity_cutting_detail\n" +
                "WHERE join_time IS NOT NULL\n" +
                " and join_time >= '" + firstDay + "'" + " and join_time <= '" + lastDay + "'";

        list = Db.find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_jchb_1", r.get("totals") + "");
            reportMap.put("khbfjzj_jchb_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }
        //2. 增值服务线上支付收入+线下支付收入
        getZzfw2(firstDay, lastDay, reportMap);

        return reportMap;
    }

    @Override
    public Map<String, String> getSumPayReport(String firstDay, String lastDay) {
        firstDay += " 00:00:00";
        lastDay += " 23:59:59";
        Map<String, String> reportMap = new HashMap<>();
        String sql = "";
        List<Record> list = null;

        //1. 客户备付金增加
        //1.1 提现记录
        sql = "SELECT COUNT(*)AS totals, SUM(withdraw_money) AS prices FROM w_withdraw\n" +
                "WHERE withdraw_status = 20 AND withdraw_create_time IS NOT NULL\n" +
                " and withdraw_create_time >= '" + firstDay + "'" + " and withdraw_create_time <= '" + lastDay + "'";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            reportMap.put("khbfjjs_tx_1", r.get("totals") + "");
            reportMap.put("khbfjjs_tx_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }

        return reportMap;
    }

    /**
     * 费用统计表
     * @param firstDay
     * @param lastDay
     * @return
     */
    @Override
    public Map<String, String> getSumCashReport(String firstDay, String lastDay) {
        firstDay += " 00:00:00";
        lastDay += " 23:59:59";
        Map<String, String> reportMap = new HashMap<>();
        String sql = "";
        List<Record> list = null;

        //1. 活动+订单奖励
        //1.1 活动奖励
        sql = "SELECT COUNT(*) AS totals, SUM(amount) AS prices FROM activity_order_reward_detail\n" +
                "WHERE create_time IS NOT NULL\n" +
                " and create_time >= '" + firstDay + "'" + " and create_time <= '" + lastDay + "'";
        list = Db.find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_ddjl_1", r.get("totals") + "");
            reportMap.put("khbfjzj_ddjl_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }
        //1.2 订单奖励
        sql = "SELECT COUNT(*) AS totals, SUM(fans_price+rank_price) AS prices FROM activity_fensi_payment_detail\n" +
                "WHERE pay_time IS NOT NULL AND STATUS = 1\n" +
                " and pay_time >= '" + firstDay + "'" + " and pay_time <= '" + lastDay + "'";
        list = Db.find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_hdjl_1", r.get("totals") + "");
            reportMap.put("khbfjzj_hdjl_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }

        return reportMap;
    }

    @Override
    public Map<String, String> getSumReceiveReport(String firstDay, String lastDay) {
        firstDay += " 00:00:00";
        lastDay += " 23:59:59";
        Map<String, String> reportMap = new HashMap<>();
        String sql = "";
        List<Record> list = null;

        //1. 增值服务线上支付收入+线下支付收入
        getZzfw2(firstDay, lastDay, reportMap);
        //计算尚未到失效日期的增值服务，计算出月份并扣除未到月份的费用

        return reportMap;
    }

    /**
     * 客户备付金
     * @param firstDay
     * @param lastDay
     * @return
     */
    public Map<String, String> getSumPreparedReport(String firstDay, String lastDay) {
        firstDay += " 00:00:00";
        lastDay += " 23:59:59";
        Map<String, String> reportMap = new HashMap<>();
        String sql = "";
        List<Record> list = null;

        //1. 客户备付金增加
        //1.1 钱包充值
        sql = "SELECT COUNT(*)AS totals, SUM(bill_amount) AS prices FROM w_bill\n" +
                "WHERE bill_type = 6 AND bill_create_time IS NOT NULL\n" +
                " and bill_create_time >= '" + firstDay + "'" + " and bill_create_time <= '" + lastDay + "'";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_qbcz_1", r.get("totals") + "");
            reportMap.put("khbfjzj_qbcz_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }
        //1.2 商户订单
        sql = "SELECT COUNT(*)AS totals, SUM(order_actual_price) AS prices FROM order_info\n" +
                "WHERE order_status = 5 AND order_pay_type between 1 and 2 and  join_time IS NOT NULL\n" +
                " and join_time >= '" + firstDay + "'" + " and join_time <= '" + lastDay + "'";
        list = Db.find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_shdd_1", r.get("totals") + "");
            reportMap.put("khbfjzj_shdd_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }
        //1.3 剪彩红包
        sql = "SELECT COUNT(*)AS totals, SUM(price) AS prices FROM activity_cutting_detail\n" +
                "WHERE join_time IS NOT NULL\n" +
                " and join_time >= '" + firstDay + "'" + " and join_time <= '" + lastDay + "'";

        list = Db.find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_jchb_1", r.get("totals") + "");
            reportMap.put("khbfjzj_jchb_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }
        //1.4 订单奖励
        sql = "SELECT COUNT(*) AS totals, SUM(fans_price+rank_price) AS prices FROM activity_fensi_payment_detail\n" +
                "WHERE pay_time IS NOT NULL AND STATUS = 1\n" +
                " and pay_time >= '" + firstDay + "'" + " and pay_time <= '" + lastDay + "'";
        list = Db.find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_hdjl_1", r.get("totals") + "");
            reportMap.put("khbfjzj_hdjl_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }
        //1.5 活动奖励
        sql = "SELECT COUNT(*) AS totals, SUM(amount) AS prices FROM activity_order_reward_detail\n" +
                "WHERE create_time IS NOT NULL\n" +
                " and create_time >= '" + firstDay + "'" + " and create_time <= '" + lastDay + "'";
        list = Db.find(sql);
        for (Record r : list) {
            reportMap.put("khbfjzj_ddjl_1", r.get("totals") + "");
            reportMap.put("khbfjzj_ddjl_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }

        //2 客户备付金减少
        //2.1 用户提现
        sql = "SELECT LEFT(withdraw_create_time,10) AS daytime, COUNT(*)AS totals, SUM(withdraw_money) AS prices FROM w_withdraw\n" +
                "WHERE withdraw_status = 20 AND withdraw_create_time IS NOT NULL\n" +
                " and withdraw_create_time >= '" + firstDay + "'" + " and withdraw_create_time <= '" + lastDay + "'\n" +
                "GROUP BY LEFT(withdraw_create_time,10)";
        list = Db.use("opay").find(sql);
        for (Record r : list) {
            reportMap.put("khbfjjs_yhtx_1", r.get("totals") + "");
            reportMap.put("khbfjjs_yhtx_2", r.get("prices") == null ? "0" : r.get("prices") + "");
        }

        return reportMap;
    }

    /**
     * 用户资产明细
     * @param paramKit
     * @return
     */
    @Override
    public List<Record> getUserAccountData(ReqParamKit paramKit) {
        List<Record> list = new ArrayList<>();
        try {

            StringBuffer sbSql = buildUserAccountSql(paramKit);

            String sort = paramKit.getString("sort");
            if (sort != null) {
                JSONArray sortArray = JSON.parseArray(sort);
                sbSql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
            } else {
                sbSql.append(" order by a.user_name desc");
            }

            sbSql.append(" LIMIT " + paramKit.getPageNumber());
            sbSql.append(",");
            sbSql.append(paramKit.getPageSize());

            list = Db.use("opay").find(sbSql.toString());
        }catch (Exception e){
            e.printStackTrace();
        }


        return list;
    }

    public StringBuffer buildUserAccountSql(ReqParamKit paramKit){
        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT a.user_id as 'id', a.user_phone AS 'user_phone',a.user_name AS 'user_name',b.settle_date AS 'settle_date', " +
                "(b.user_account_available_money + b.user_account_freeze_money) AS 'user_account_total', \n" +
                "c.total_waiting_settle_account_money AS 'user_account_djs',\n" +
                "b.user_account_available_money AS 'user_account_ktx',\n" +
                "c.total_with_drawing_money AS 'user_account_txz'\n" +
                " FROM w_user_info AS a \n" +
                "LEFT JOIN w_user_account AS b ON a.user_id = b.user_id\n" +
                "RIGHT JOIN w_user_settle_account_day AS c ON b.user_id = c.user_id AND b.settle_date = c.settle_date\n" +
                "WHERE a.user_source_type = 0");

        if(paramKit.has("userName")){
            sbSql.append(String.format(" and a.user_name like '%s'", "%"+paramKit.getString("userName")+"%"));
        }
        if(paramKit.has("phone")){
            sbSql.append(String.format(" and a.user_phone like '%s'", "%"+paramKit.getString("phone")+"%"));
        }
        if(paramKit.has("txBegin")){
            sbSql.append(String.format(" and b.user_account_available_money >= %s", paramKit.getString("txBegin")));
        }
        if(paramKit.has("txEnd")){
            sbSql.append(String.format(" and b.user_account_available_money <= %s", paramKit.getString("txEnd")));
        }

        return sbSql;
    }

    @Override
    public long getUserAccountDataTotals(ReqParamKit paramKit) {
        String sql = "select count(*) from ("+buildUserAccountSql(paramKit).toString()+") as a";
        return Db.use("opay").queryLong(sql);
    }

    @Override
    public Record getUserAccountTotals(ReqParamKit paramKit) {
        StringBuffer sbSql = new StringBuffer("select ifnull(sum(user_account_total),0) as totals, ifnull(sum(user_account_ktx),0) as prices from (");
        sbSql.append(buildUserAccountSql(paramKit));
        sbSql.append(") as a");

        return Db.use("opay").find(sbSql.toString()).get(0);
    }

    @Override
    public List<OrderInfo> getExportUserAccountData() {
        List<Record> list = new ArrayList<>();
        try {

            StringBuffer sbSql = buildUserAccountSql(ReqParamKit.getInstance(new HashMap<String, String[]>()));

            sbSql.append(" LIMIT " + 0);
            sbSql.append(",");
            sbSql.append(5000);

            list = Db.use("opay").find(sbSql.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        List<OrderInfo> orderInfoList = new ArrayList<>();

        for(Record record : list){
            OrderInfo order = new OrderInfo();
            for(String name : record.getColumnNames()){
                order.put(name, record.get(name));
            }

            orderInfoList.add(order);
        }

        return orderInfoList;
    }

    @Override
    public List<ExcelExportUtil.Pair> getExportUserAccountDataTitles() {
        List<ExcelExportUtil.Pair> titles = new ArrayList<>();

        titles.add(new ExcelExportUtil.Pair("id", "id"));
        titles.add(new ExcelExportUtil.Pair("user_name", "姓名"));
        titles.add(new ExcelExportUtil.Pair("user_phone", "手机号"));
        titles.add(new ExcelExportUtil.Pair("user_account_totals", "钱包总额"));
        titles.add(new ExcelExportUtil.Pair("user_account_djs", "待结算金额"));
        titles.add(new ExcelExportUtil.Pair("user_account_ktx", "可提现金额"));
        titles.add(new ExcelExportUtil.Pair("user_account_txz", "提现中金额"));

        return titles;
    }
}
