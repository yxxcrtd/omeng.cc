package com.shanjin.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.service.MerchantEmployeesNumApplyService;
import com.shanjin.financial.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class MerchantEmployeesNumApplyServiceImpl implements MerchantEmployeesNumApplyService {
    @Override
    public List<Record> getVatServiceData(ReqParamKit paramKit) {
        List<Record> totalList = new ArrayList<>();
        try {
            if (!paramKit.has("vatType")) {//获取全部
                StringBuffer sql = buildAllTypeSql(paramKit);

                sql.append(" LIMIT " + paramKit.getPageNumber());
                sql.append(",");
                sql.append(paramKit.getPageSize());

                totalList = Db.find(sql.toString());

            } else {
                if (paramKit.getInt("vatType") == 1) {
                    totalList.addAll(getListByType("VIP服务", "merchant_vip_apply", paramKit.getPageSize(), paramKit));
                } else if (paramKit.getInt("vatType") == 2) {
                    totalList.addAll(getListByType("顾问号服务", "merchant_employees_num_apply", paramKit.getPageSize(), paramKit));
                } else if (paramKit.getInt("vatType") == 3) {
                    totalList.addAll(getListByType("订单推送服务", "merchant_topup_apply", paramKit.getPageSize(), paramKit));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalList;
    }

    private StringBuffer buildAllTypeSql(ReqParamKit paramKit) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from (");
        sql.append("SELECT a.id,'顾问号服务' AS vat_type, b.`name`, c.`telephone`,a.`money` as money,a.`apply_time`, a.`pay_type`, a.`open_time`\n" +
                "FROM merchant_employees_num_apply AS a " +
                "left join merchant_info AS b on a.`merchant_id` = b.`id` \n" +
                "left join merchant_contact AS c on a.`merchant_id` = c.`merchant_id`");
        sql.append("union ");
        sql.append("SELECT a.id,'VIP服务' AS vat_type, b.`name`, c.`telephone`,a.`money` as money,a.`apply_time`, a.`pay_type`, a.`open_time`\n" +
                "FROM merchant_vip_apply AS a " +
                "left join merchant_info AS b on a.`merchant_id` = b.`id` \n" +
                "left join merchant_contact AS c on a.`merchant_id` = c.`merchant_id`");
        sql.append("union ");
        sql.append("SELECT a.id,'订单推送服务' AS vat_type, b.`name`, c.`telephone`,a.`topup_money` as money,a.`apply_time`, a.`pay_type`, a.`open_time`\n" +
                "FROM merchant_topup_apply AS a " +
                "left join merchant_info AS b on a.`merchant_id` = b.`id` \n" +
                "left join merchant_contact AS c on a.`merchant_id` = c.`merchant_id`");
        sql.append(") as a where 1=1");
        //追加查询条件和排序
        if (paramKit.has("merchantName")) {
            sql.append(" and name like '%" + paramKit.getString("merchantName") + "%'");
        }
        if (paramKit.has("payType")) {
            sql.append(" and a.pay_type =" + paramKit.getString("payType"));
        }
        if (paramKit.has("beginTime")) {
            sql.append(" and a.apply_time >= '" + paramKit.getString("beginTime") + " 00:00:00'");
        }
        if (paramKit.has("endTime")) {
            sql.append(" and a.apply_time <= '" + paramKit.getString("endTime") + "  23:59:59'");
        }
        if(paramKit.has("phone")){
            sql.append(" and a.telephone like '%"+paramKit.getString("phone")+"%'");
        }

        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by a.apply_time desc");
        }
        return sql;
    }

    private List<Record> getListByType(String type, String table, int pageSize, ReqParamKit paramKit) {
        List<Record> list = new ArrayList<>();
        try{
            StringBuffer sql = buildTypeSql(type, table, paramKit);

            sql.append(" LIMIT " + paramKit.getPageNumber());
            sql.append(",");
            sql.append(pageSize);

            list = Db.find(sql.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    private StringBuffer buildTypeSql(String type, String table, ReqParamKit paramKit) {
        String money_field = "money";
        if (type.equals("订单推送服务")) money_field = "topup_money";
        StringBuffer sql = new StringBuffer(); //顾问号
        sql.append("SELECT a.id,'" + type + "' AS vat_type, b.`name`, c.`telephone`,a.`" + money_field + "` as money,a.`apply_time`, a.`pay_type`, a.`open_time`\n" +
                "FROM " + table + " AS a " +
                "left join merchant_info AS b on a.`merchant_id` = b.`id` \n" +
                "left join merchant_contact AS c on a.`merchant_id` = c.`merchant_id` \n" +
                "WHERE 1=1");

        //追加查询条件和排序
        if (paramKit.has("merchantName")) {
            sql.append(" and name like '%" + paramKit.getString("merchantName") + "%'");
        }
        if (paramKit.has("payType")) {
            sql.append(" and a.pay_type =" + paramKit.getString("payType"));
        }
        if (paramKit.has("beginTime")) {
            sql.append(" and a.apply_time >= '" + paramKit.getString("beginTime") + " 00:00:00'");
        }
        if (paramKit.has("endTime")) {
            sql.append(" and a.apply_time <= '" + paramKit.getString("endTime") + " 23:59:59'");
        }
        if(paramKit.has("phone")){
            sql.append(" and c.telephone like '%"+paramKit.getString("phone")+"%'");
        }

        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by a.apply_time desc");
        }
        return sql;
    }

    @Override
    public long getVatServiceDataTotals(ReqParamKit paramKit) {
        long totals = 0;
        if (!paramKit.has("vatType")) {//获取全部
            totals = Db.queryLong("SELECT COUNT(*) AS totals FROM ("+buildAllTypeSql(paramKit)+") AS a");
        } else {
            if (paramKit.getInt("vatType") == 1) {
                totals = Db.queryLong("select count(*) from ("+buildTypeSql("VIP服务", "merchant_vip_apply", paramKit)+") as a");
            } else if (paramKit.getInt("vatType") == 2) {
                totals = Db.queryLong("select count(*) from ("+buildTypeSql("顾问号服务", "merchant_employees_num_apply", paramKit)+") as a");
            } else if (paramKit.getInt("vatType") == 3) {
                totals = Db.queryLong("select count(*) from ("+buildTypeSql("订单推送服务", "merchant_topup_apply", paramKit)+") as a");
            }
        }
        return totals;
    }

    /**
     * 根据类型获取
     * @param type 增值服务类型
     * @param table 增值服务对应的数据表
     * @param paramKit
     * @return
     */
    private Record getVatServiceTotalsByType(String type, String table, ReqParamKit paramKit) {
        StringBuffer sql = buildTypeSql(type, table, paramKit);

        return Db.find("select ifnull(count(a.id),0) totals, ifnull(sum(a.money),0) as prices from (" + sql.toString() + ") as a").get(0);
    }


    /**
     * 统计报表信息，订单数 总额
     *
     * @param paramKit 请求参数Kit
     * @return
     */
    @Override
    public Record getVatServiceTotals(ReqParamKit paramKit) {
        Record record = new Record();
        try {
            long totals = 0;
            double prices = 0;
            if (!paramKit.has("vatType")) {//获取全部
                StringBuffer sql = new StringBuffer(); //顾问号
                sql.append("SELECT ifnull(COUNT(*),0) as totals, ifnull(sum(money),0) as prices FROM ("+buildAllTypeSql(paramKit)+") AS a where 1=1");

//                //追加查询条件和排序
//                if (paramKit.has("merchantName")) {
//                    sql.append(" and a.name like '%" + paramKit.getString("merchantName") + "%'");
//                }
//                if (paramKit.has("payType")) {
//                    sql.append(" and a.pay_type =" + paramKit.getString("payType"));
//                }
//                if (paramKit.has("beginTime")) {
//                    sql.append(" and a.apply_time >= '" + paramKit.getString("beginTime") + " 00:00:00'");
//                }
//                if (paramKit.has("endTime")) {
//                    sql.append(" and a.apply_time <= '" + paramKit.getString("endTime") + " 23:59:59'");
//                }
//                if(paramKit.has("phone")){
//                    sql.append(" LEFT JOIN merchant_contact AS c ON a.`merchant_id` = c.`merchant_id` ");
//                    sql.append(" where c.telephone like '%"+paramKit.getString("phone")+"%'");
//                }
                Record r = Db.find(sql.toString()).get(0);
                totals = r.getLong("totals");
                prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
//                r = getVatServiceTotalsByType("VIP服务", "merchant_vip_apply", paramKit);
//                totals += r.getLong("totals");
//                prices += r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
//                r = getVatServiceTotalsByType("订单推送服务", "merchant_topup_apply", paramKit);
//                totals += r.getLong("totals");
//                prices += r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();

            } else {
                if (paramKit.getInt("vatType") == 1) {
                    Record r = getVatServiceTotalsByType("VIP服务", "merchant_vip_apply", paramKit);
                    totals = r.getLong("totals");
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                } else if (paramKit.getInt("vatType") == 2) {
                    Record r = getVatServiceTotalsByType("顾问号服务", "merchant_employees_num_apply", paramKit);
                    totals = r.getLong("totals");
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                } else if (paramKit.getInt("vatType") == 3) {
                    Record r = getVatServiceTotalsByType("订单推送服务", "merchant_topup_apply", paramKit);
                    totals = r.getLong("totals");
                    prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();
                }
            }
            record.set("totals", totals).set("prices", prices);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return record;
    }
}
