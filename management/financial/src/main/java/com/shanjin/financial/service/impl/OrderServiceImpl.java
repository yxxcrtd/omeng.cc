package com.shanjin.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.OrderInfo;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.service.OrderService;
import com.shanjin.financial.util.ExcelExportUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public class OrderServiceImpl implements OrderService{
    @Override
    public List<OrderInfo> getOrderInfoData(ReqParamKit paramKit) {
        StringBuffer sql = buildSql(paramKit);

        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        }

        sql.append(" LIMIT " + paramKit.getPageNumber());
        sql.append(",");
        sql.append(paramKit.getPageSize());

        List<OrderInfo> list = OrderInfo.dao.find(sql.toString());
        return list;
    }

    @Override
    public List<OrderInfo> getExportOrderData() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.id,a.order_no,a.province,a.city, b.name AS merchant_name, b1.telephone AS merchant_phone, c.service_type_name, a.order_price, d.name AS user_name,\n" +
                "a.order_pay_type, a.order_status,a.deal_time, e.trade_no\n" +
                "FROM order_info AS a LEFT JOIN merchant_info AS b ON a.`merchant_id` = b.`id`\n" +
                "LEFT JOIN merchant_contact b1 ON a.`merchant_id` = b1.`merchant_id`\n" +
                "LEFT JOIN service_type AS c ON a.service_type_id = c.id\n" +
                "LEFT JOIN user_info AS d ON a.user_id = d.id " +
                "left join merchant_payment_details as e on a.id=e.business_id where 1=1\n");

        sql.append("order by a.deal_time desc");

        sql.append(" LIMIT " + 0);
        sql.append(",");
        sql.append(5000);

        List<OrderInfo> list = OrderInfo.dao.find(sql.toString());
        return list;
    }

    @Override
    public List<ExcelExportUtil.Pair> getExportOrderDataTitles() {
        List<ExcelExportUtil.Pair> titles = new ArrayList<>();

        titles.add(new ExcelExportUtil.Pair("id", "id"));
        titles.add(new ExcelExportUtil.Pair("order_no", "订单号"));
        titles.add(new ExcelExportUtil.Pair("province", "省份"));
        titles.add(new ExcelExportUtil.Pair("city", "城市"));
        titles.add(new ExcelExportUtil.Pair("merchant_name", "服务商"));
        titles.add(new ExcelExportUtil.Pair("merchant_phone", "手机号"));
        titles.add(new ExcelExportUtil.Pair("service_type_name", "服务项目"));
        titles.add(new ExcelExportUtil.Pair("order_price", "金额"));
        titles.add(new ExcelExportUtil.Pair("user_name", "服务对象"));
        titles.add(new ExcelExportUtil.Pair("order_pay_type", "支付方式"));
        titles.add(new ExcelExportUtil.Pair("order_pay_no", "关联支付单号"));
        titles.add(new ExcelExportUtil.Pair("order_status", "支付状态"));
        titles.add(new ExcelExportUtil.Pair("deal_time", "完成时间"));

        return titles;
    }

    @Override
    public Record getOrderTotals(ReqParamKit paramKit) {
        StringBuffer sql = buildSql(paramKit);

        Record record = Db.find(String.format("select ifnull(count(a.id),0) as totals, ifnull(sum(a.order_price),0) as prices from (%s) as a", sql.toString())).get(0);
        return record;
    }

    private StringBuffer buildSql(ReqParamKit paramKit){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.id,a.order_no,a.province,a.city, b.name AS merchant_name, b1.telephone AS merchant_phone, c.service_type_name, a.order_price, d.phone AS user_name,\n" +
                "a.order_pay_type, a.order_status,a.deal_time, e.trade_no, e.payment_price\n" +
                "FROM order_info AS a LEFT JOIN merchant_info AS b ON a.`merchant_id` = b.`id`\n" +
                "LEFT JOIN merchant_contact b1 ON a.`merchant_id` = b1.`merchant_id`\n" +
                "LEFT JOIN service_type AS c ON a.service_type_id = c.id\n" +
                "LEFT JOIN user_info AS d ON a.user_id = d.id \n" +
                "left join merchant_payment_details as e on a.id=e.business_id and e.payment_type=0 where 1=1 and a.order_pay_type between 1 and 2");


        //追加查询条件和排序
        if (paramKit.has("orderNo")) {
            sql.append(" and a.order_no like '%" + paramKit.getString("orderNo") + "%'");
        }
        if (paramKit.has("serviceName")) {
            sql.append(" and b.name like '%" + paramKit.getString("serviceName") + "%'");
        }
        if (paramKit.has("phone")) {
            sql.append(" and b1.telephone like '%" + paramKit.getString("phone") + "%'");
        }
        if (paramKit.has("payType")) {
            sql.append(" and a.order_pay_type=" + paramKit.getInt("payType"));
        }
        if(paramKit.has("dealBeginTime")){
            sql.append(" and a.deal_time >= '"+paramKit.getString("dealBeginTime")+" 00:00:00'");
        }
        if(paramKit.has("dealEndTime")){
            sql.append(" and a.deal_time <= '"+paramKit.getString("dealEndTime")+" 23:59:59'");
        }
        if(paramKit.has("payNo")){
            sql.append(" and e.trade_no like '%"+paramKit.getString("payNo")+"%'");
        }
//        if(paramKit.has("payStatus")){
//            sql.append(" and a.order_status = "+paramKit.getInt("payStatus"));
//        }
        if(paramKit.has("province")){
            sql.append(" and a.province = '"+paramKit.getString("province")+"'");
        }
        if(paramKit.has("city")){
            sql.append(" and a.city = '"+paramKit.getString("city")+"'");
        }
        sql.append(" and a.order_status = 5");

        return sql;
    }

    @Override
    public long getOrderInfoDataTotals(ReqParamKit paramKit) {
        StringBuffer sql = buildSql(paramKit);
        return Db.queryLong("SELECT COUNT(1) as totals FROM ("+sql.toString()+") as a");
    }
}
