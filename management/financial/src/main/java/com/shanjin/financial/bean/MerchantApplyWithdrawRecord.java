package com.shanjin.financial.bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by Administrator on 2016/7/5.
 */
public class MerchantApplyWithdrawRecord extends Model<MerchantApplyWithdrawRecord>{
    public static MerchantApplyWithdrawRecord dao = new MerchantApplyWithdrawRecord();
    private long total;
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
}
