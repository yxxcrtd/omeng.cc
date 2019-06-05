package com.shanjin.financial.bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by Administrator on 2016/7/6.
 */
public class OrderInfo extends Model<OrderInfo> {
    public static OrderInfo dao = new OrderInfo();
    private long total;
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
}
