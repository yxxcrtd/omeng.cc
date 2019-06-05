package com.shanjin.financial.bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by Administrator on 2016/7/7.
 */
public class MerchantEmployeesNumApply extends Model<MerchantEmployeesNumApply> {
    public static MerchantEmployeesNumApply dao = new MerchantEmployeesNumApply();
    private long total;
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
}
