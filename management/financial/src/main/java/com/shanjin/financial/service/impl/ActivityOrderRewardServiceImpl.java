package com.shanjin.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.ActivityOrderRewardDetail;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.service.ActivityOrderRewardService;

import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class ActivityOrderRewardServiceImpl implements ActivityOrderRewardService {
    @Override
    public List<ActivityOrderRewardDetail> getOrderRewardData(ReqParamKit paramKit) {
        StringBuffer sql = buildSql(paramKit);

        sql.append(" LIMIT " + paramKit.getPageNumber());
        sql.append(",");
        sql.append(paramKit.getPageSize());

        List<ActivityOrderRewardDetail> list = ActivityOrderRewardDetail.dao.find(sql.toString());

        return list;
    }

    @Override
    public long getOrderRewardDataTotals(ReqParamKit paramKit) {
        long totals = 0;
        StringBuffer sql = buildSql(paramKit);

        System.out.println("select count(*) from (" + sql.toString() + ") as a");

        totals = Db.queryLong("select count(*) from ("+sql.toString()+") as a");

        return totals;
    }

    @Override
    public Record getOrderRewardTotals(ReqParamKit paramKit) {
        Record record = new Record();
        long totals = 0;
        double prices = 0;

        StringBuffer sql = buildSql(paramKit);
        Record r = Db.find("select ifnull(count(*),0) as totals, ifnull(sum(a.amount),0) as prices from (" + sql.toString() + ") as a").get(0);
        totals = r.getLong("totals");
        prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();

        record.set("totals", totals);
        record.set("prices", prices);

        return record;
    }

    private StringBuffer buildSql(ReqParamKit paramKit) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.id,b.name,c.`deal_time`,d.`service_type_name`,c.order_actual_price,a.amount, a.create_time, a.province, a.city, a.order_no, e.app_name\n" +
                "FROM activity_order_reward_detail AS a\n" +
                "LEFT JOIN merchant_info AS b ON a.`merchant_id` = b.`id`\n" +
                "LEFT JOIN order_info AS c ON a.`order_id` = c.`id`\n" +
                "left join merchant_app_info as e on b.app_type = e.app_type "+
                "LEFT JOIN service_type AS d ON c.`service_type_id` = d.`id` where 1=1 ");
        if (paramKit.has("merchantName")) {
            sql.append(" and b.name like '%" + paramKit.getString("merchantName") + "%'");
        }
        if (paramKit.has("beginTime")) {
            sql.append(" and a.pay_time >= '" + paramKit.getString("beginTime") + " 00:00:00'");
        }
        if (paramKit.has("endTime")) {
            sql.append(" and a.pay_time <= '" + paramKit.getString("endTime") + " 23:59:59'");
        }
        if(paramKit.has("province")){
            sql.append(" and a.province = '"+paramKit.getString("province")+"'");
        }
        if(paramKit.has("city")){
            sql.append(" and a.city = '"+paramKit.getString("city")+"'");
        }
        if(paramKit.has("appType")){
            sql.append(" and b.app_type = '"+paramKit.getString("appType")+"'");
        }
        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by a.create_time desc");
        }

        return sql;
    }
}
