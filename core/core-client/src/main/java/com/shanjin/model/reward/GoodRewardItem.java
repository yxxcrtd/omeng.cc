package com.shanjin.model.reward;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/20
 * @desc 好评奖励明细
 * @see
 */
public class GoodRewardItem implements Serializable,Comparable<GoodRewardItem> {

    private static final long serialVersionUID = 5713248522687706285L;

    private String merchantScore;//店铺评分

    private BigDecimal rewardAmount;//奖励金额

    private String rewardDate;//yyyy-MM  || 03.15

    private transient String rewardMonth;// 200601


    public String getMerchantScore() {
        return merchantScore;
    }

    public void setMerchantScore(String merchantScore) {
        this.merchantScore = merchantScore;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }



    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getRewardDate() {
        return rewardDate;
    }

    public void setRewardDate(String rewardDate) {
        this.rewardDate = rewardDate;
    }

    public String getRewardMonth() {
        return rewardMonth;
    }

    public void setRewardMonth(String rewardMonth) {
        this.rewardMonth = rewardMonth;
    }

    @Override
    public int compareTo(GoodRewardItem o) {
        return this.rewardMonth.compareTo(o.getRewardMonth());
    }
}
