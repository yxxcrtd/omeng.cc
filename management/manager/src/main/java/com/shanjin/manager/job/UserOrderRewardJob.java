package com.shanjin.manager.job;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.PropUtil;

import java.util.*;

/**
 * Created by Administrator on 2016/8/5.
 * 用户订单奖励
 */
public class UserOrderRewardJob {


    public void work() {
    	System.out.println("---用户补贴job开始------");
    	
        //读取配置文件，从config.properties
        Properties props = PropUtil.getPropUtil("config.properties");
        int totals = Integer.parseInt(props.getProperty("uor.totals", "50000"));
        double price = Double.parseDouble(props.getProperty("uor.price", "20"));
        int status = Integer.parseInt(props.getProperty("uor.status", "5"));
        double rewardPrice = Double.parseDouble(props.getProperty("uor.rewardPrice", "10"));
        //0. 判断活动是否已经截至

        //1. 判断总奖励订单是否已经达到 totals
        //2. 获取支付方式为：线上支付 && 实付金额>= @price && 订单状态== @status
        //3. 用户必须为新用户
        //4. 保存明细

        //#0
        Record activity = Db.find("select id, stime, etime from activity_info where detail_table='activity_user_order_reward' and is_pub=1").get(0);
        long stime = activity.getTimestamp("stime").getTime();
        long etime = activity.getTimestamp("etime").getTime();
        long activityId = activity.getLong("id");

        String strStartTime = DateUtil.formatLongToTxt(stime);
        if(stime > System.currentTimeMillis() || etime < System.currentTimeMillis()) return;
        //#1
        long count = Db.queryLong("select count(*) from activity_user_order_reward");
        if (count <= totals) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            String day = DateUtil.formatDate("yyyy-MM-dd", c.getTime());

            try {
                //获取符合条件的用户
                String sql = "";

                sql = "select user_id, order_id from activity_user_order_reward";
                List<Record> userList = Db.find(sql);
                Map<Long, Long> userMap = new HashMap<Long, Long>();
                for (Record record : userList) {
                    userMap.put(record.getLong("user_id"), record.getLong("order_id"));
                }

                //获取当日支付完成、支付金额大于price的订单
                StringBuffer idList = new StringBuffer();
                sql = "select * from order_info where deal_time between '"+day+" 00:00:00' and '"+day+" 23:59:59' AND order_status = "+status+" order by deal_time asc";
                List<Record> orderList = Db.find(sql);
                for(Record order : orderList){
                    Long userId = order.getLong("user_id");
                    System.out.println(order.get("order_no"));

                    //判断此用户是否为新用户
                    if(userMap.get(userId) != null)
                        continue;
                    if(order.getBigDecimal("order_actual_price").doubleValue() < price){
                        userMap.put(order.getLong("user_id"), order.getLong("id"));
                        continue;
                    }

                    idList.append(","+order.getLong("id"));
                    userMap.put(order.getLong("user_id"), order.getLong("id"));
                }
                if(idList.length() > 0){
                    StringBuffer sbSql = new StringBuffer("insert into activity_user_order_reward(amount, activity_id, user_id, order_id, order_no, merchant_id, service_type_id, order_status, province, city, pay_time, deal_time, " +
                    "pay_type, order_amount, pay_amount, industry_type) ");
                    sbSql.append("SELECT "+rewardPrice+" as amount, "+activityId+" as activity_id, a.user_id, a.`id` as ordre_id, a.`order_no`, a.`merchant_id`, a.service_type_id, a.order_status, a.`province`, a.`city`, a.deal_time as pay_time, a.deal_time, " +
                    "a.order_pay_type as pay_type, a.order_price as order_amount, a.order_actual_price as pay_amount,a.app_type as industry_type FROM order_info AS a \n" +
                    "LEFT JOIN merchant_info AS b ON a.`merchant_id` = b.`id`\n" +
                    "WHERE a.id in("+idList.substring(1)+")");

                    System.out.println(sbSql.toString());

                    Db.update(sbSql.toString());
                }

                System.out.println(idList);

            }catch (Exception e){
                e.printStackTrace();
            }






//            //#2
//            StringBuffer sql = new StringBuffer("select count(*) from (SELECT a.deal_time FROM order_info AS a \n" +
//                    "LEFT JOIN merchant_info AS b ON a.`merchant_id` = b.`id`\n" +
////                    "LEFT JOIN merchant_payment_details AS c ON a.`id` = c.business_id AND c.payment_type = 0 " +
//                    "LEFT JOIN (SELECT user_id, COUNT(user_id) AS totals FROM activity_user_order_reward GROUP BY user_id) AS d ON a.`user_id` = d.user_id " +
//                    "WHERE (d.totals = 0 or d.totals is null)");
//            sql.append(" and a.order_pay_type between 1 and 2"); //限制支付类型
//            sql.append(" and a.order_actual_price >=" + price); // 限制订单支付金额
//            sql.append(" and a.order_status = " + status); // 限制订单状态
//
//            sql.append(" order by a.deal_time asc) as a");
//            sql.append(" where a.deal_time >= '" + day + " 00:00:00'"); // 支付时间范围
//            sql.append(" and a.deal_time <= '" + day + " 23:59:59'"); // 支付时间范围
//
//
////            System.out.println(sql);
//            long orderCount = Db.queryLong(sql.toString());
//
//            sql = new StringBuffer("insert into activity_user_order_reward(amount, activity_id, user_id, order_id, order_no, merchant_id, service_type_id, order_status, province, city, pay_time, deal_time, " +
//                    "pay_type, order_amount, pay_amount, industry_type) ");
//            sql.append("SELECT * FROM (SELECT "+rewardPrice+" as amount, "+activityId+" as activity_id, a.user_id, a.`id` as ordre_id, a.`order_no`, a.`merchant_id`, a.service_type_id, a.order_status, a.`province`, a.`city`, min(a.deal_time) as pay_time, a.deal_time, " +
//                    "a.order_pay_type as pay_type, a.order_price as order_amount, a.order_actual_price as pay_amount,a.app_type as industry_type FROM order_info AS a \n" +
//                    "LEFT JOIN merchant_info AS b ON a.`merchant_id` = b.`id`\n" +
////                    "LEFT JOIN merchant_payment_details AS c ON a.`id` = c.business_id AND c.payment_type = 0 " +
//                    "LEFT JOIN (SELECT user_id, COUNT(user_id) AS totals FROM activity_user_order_reward GROUP BY user_id) AS d ON a.`user_id` = d.user_id " +
//                    "WHERE (d.totals = 0 or d.totals is null)");
//            sql.append(" and a.order_pay_type between 1 and 2"); //限制支付类型
//            sql.append(" and a.order_actual_price >=" + price); // 限制订单支付金额
//            sql.append(" and a.order_status = " + status); // 限制订单状态
//
//            sql.append(" GROUP BY a.user_id order by a.deal_time asc) as a");
//            sql.append(" where a.deal_time >= '" + day + " 00:00:00'"); // 支付时间范围
//            sql.append(" and a.deal_time <= '" + day + " 23:59:59'"); // 支付时间范围
//
//            if (orderCount > totals - count) { // 如果符合条件的订单数量大于剩余数量，则按照付款的先后顺序进行发放，直至额度耗尽为止
//                sql.append(" limit 0," + (totals - count));
//            }
//
////            System.out.println(sql.toString());
//            Db.update(sql.toString());

        }
    
        System.out.println("---用户补贴job结束------");
    }
}
