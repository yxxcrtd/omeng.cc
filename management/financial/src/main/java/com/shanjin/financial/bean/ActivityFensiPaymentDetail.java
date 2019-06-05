package com.shanjin.financial.bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by Administrator on 2016/7/6.
 */
public class ActivityFensiPaymentDetail extends Model<ActivityFensiPaymentDetail> {
    public static ActivityFensiPaymentDetail dao = new ActivityFensiPaymentDetail();
    private long total;
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
}
