package com.shanjin.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.ActivityCuttingDetail;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.service.ActivityCuttingDetailService;

import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class ActivityCuttingDetailServiceImpl implements ActivityCuttingDetailService {
    @Override
    public List<ActivityCuttingDetail> getGiftData(ReqParamKit paramKit) {
        StringBuffer sql = buildSql(paramKit);

        sql.append(" LIMIT " + paramKit.getPageNumber());
        sql.append(",");
        sql.append(paramKit.getPageSize());

        List<ActivityCuttingDetail> list = ActivityCuttingDetail.dao.find(sql.toString());
        return list;
    }

    private StringBuffer buildSql(ReqParamKit paramKit){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.id,a.name as src, b.`name`, c.`telephone`, a.`price`, '微信' as `pay_type`, a.`join_time`, b.province, b.city, d.app_name\n" +
                "FROM activity_cutting_detail AS a " +
                "left join merchant_info AS b on  a.`merchant_id` = b.`id` " +
                "left join merchant_contact AS c on a.`merchant_id` = c.`merchant_id`\n" +
                "left join merchant_app_info AS d on b.`app_type` = d.`app_type`\n" +
                "WHERE 1=1 and a.price is not null");

        //追加查询条件和排序
        if (paramKit.has("merchantName")) {
            sql.append(" and b.name like '%" + paramKit.getString("merchantName") + "%'");
        }
        if (paramKit.has("phone")) {
            sql.append(" and c.telephone like '%" + paramKit.getString("phone") + "%'");
        }
        if(paramKit.has("source")){
            sql.append(" and a.name =" + paramKit.getString("source"));
        }
        if(paramKit.has("payType")){

        }
        if (paramKit.has("beginTime")) {
            sql.append(" and a.join_time >= '" + paramKit.getString("beginTime") + " 00:00:00'");
        }
        if (paramKit.has("endTime")) {
            sql.append(" and a.join_time <= '" + paramKit.getString("endTime") + " 23:59:59'");
        }
        if(paramKit.has("province")){
            sql.append(" and b.province = '"+paramKit.getString("province")+"'");
        }
        if(paramKit.has("city")){
            sql.append(" and b.city = '"+paramKit.getString("city")+"'");
        }
        if(paramKit.has("appType")){
            sql.append(" and b.app_type = '"+paramKit.getString("appType")+"'");
        }

        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by a.join_time desc");
        }
        return sql;
    }

    @Override
    public long getGiftDataTotals(ReqParamKit paramKit) {
        long totals = 0;
        StringBuffer sql = buildSql(paramKit);

        totals = Db.queryLong("select count(*) from ("+sql.toString()+") as a");

        return totals;
    }

    @Override
    public Record getGiftTotals(ReqParamKit paramKit) {
        Record record = new Record();
        long totals=0; double prices = 0;

        StringBuffer sql = buildSql(paramKit);
        Record r = Db.find("select ifnull(count(*),0) as totals, ifnull(sum(a.price),0) as prices from ("+sql.toString()+") as a").get(0);
        totals = r.getLong("totals");
        prices = r.get("prices") == null ? 0 : r.getBigDecimal("prices").doubleValue();

        record.set("totals", totals);
        record.set("prices", prices);

        return record;
    }
}
