package com.shanjin.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.shanjin.financial.bean.ActivityFensiPaymentDetail;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.service.ActivityFensiService;
import com.shanjin.financial.util.ExcelExportUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public class ActivityFensiServiceImpl implements ActivityFensiService {

    @Override
    public List<ActivityFensiPaymentDetail> getFansActivityData(ReqParamKit paramKit) {
        StringBuffer sql = buildSql(paramKit);

        sql.append(" LIMIT " + paramKit.getPageNumber());
        sql.append(",");
        sql.append(paramKit.getPageSize());

        System.out.println(sql.toString());

        List<ActivityFensiPaymentDetail> list = ActivityFensiPaymentDetail.dao.find(sql.toString());

        return list;
    }

    private StringBuffer buildSql(ReqParamKit paramKit){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT d.app_name, a.id,a.status,a.fans_count, a.fans_price, a.rank, a.rank_price, a.pay_time,b.name,b.province,b.city,c.telephone \n" +
                "FROM activity_fensi_payment_detail AS a " +
                "left join merchant_info AS b on a.merchant_id = b.id " +
                "left join merchant_contact AS c on a.merchant_id = c.merchant_id\n" +
                "left join merchant_app_info as d on b.app_type = d.app_type "+
                "WHERE 1=1");
        //追加查询条件和排序
        if (paramKit.has("merchantName")) {
            sql.append(" and b.name like '%" + paramKit.getString("merchantName") + "%'");
        }
        if (paramKit.has("phone")) {
            sql.append(" and c.telephone like '%" + paramKit.getString("phone") + "%'");
        }
        if(paramKit.has("beginDate")){
            sql.append(" and a.pay_time >= '"+paramKit.getString("beginDate")+" 00:00:00'");
        }
        if(paramKit.has("endDate")){
            sql.append(" and a.pay_time <= '"+paramKit.getString("endDate")+" 23:59:59'");
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
        }
        return sql;
    }

    @Override
    public List<ActivityFensiPaymentDetail> getExportFansActivityData() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT d.app_name, a.id,a.fans_count, a.fans_price, a.rank, a.rank_price, a.pay_time,b.name,b.province,b.city,c.telephone \n" +
                "FROM activity_fensi_payment_detail AS a " +
                "left join merchant_info AS b on a.merchant_id = b.id " +
                "left join merchant_contact AS c on a.merchant_id = c.merchant_id\n" +
                "left join merchant_app_info as d on b.app_type = d.app_type "+
                "WHERE 1=1");

        List<ActivityFensiPaymentDetail> list = ActivityFensiPaymentDetail.dao.find(sql.toString());

        return list;
    }

    @Override
    public List<ExcelExportUtil.Pair> getExportFansActivityTitles() {
        List<ExcelExportUtil.Pair> titles = new ArrayList<>();

        titles.add(new ExcelExportUtil.Pair("id", "id"));
        titles.add(new ExcelExportUtil.Pair("name", "服务商"));
        titles.add(new ExcelExportUtil.Pair("telephone", "手机号"));
        titles.add(new ExcelExportUtil.Pair("province", "省份"));
        titles.add(new ExcelExportUtil.Pair("city", "城市"));
        titles.add(new ExcelExportUtil.Pair("app_name", "行业类型"));
        titles.add(new ExcelExportUtil.Pair("fans_count", "粉丝数"));
        titles.add(new ExcelExportUtil.Pair("fans_price", "粉丝奖励"));
        titles.add(new ExcelExportUtil.Pair("rank", "排名"));
        titles.add(new ExcelExportUtil.Pair("rank_price", "排名奖励"));
        titles.add(new ExcelExportUtil.Pair("pay_time", "完成时间"));

        return titles;
    }

    @Override
    public long getFansActivityDataTotals(ReqParamKit paramKit) {
        StringBuffer sql = buildSql(paramKit);
        return Db.queryLong("SELECT COUNT(1) as totals FROM ("+sql.toString()+") as a");
    }
}
