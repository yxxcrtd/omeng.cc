package com.shanjin.model.reward;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/20
 * @desc 好评奖励信息
 * @see
 */
public class GoodRewardInfo implements Serializable {

    private static final long serialVersionUID = -9170646605430991789L;

    private BigDecimal totalAmount;
    private BigDecimal surplusAmount;
    private List<GoodRewardItem> items;


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getSurplusAmount() {
        return surplusAmount;
    }

    public void setSurplusAmount(BigDecimal surplusAmount) {
        this.surplusAmount = surplusAmount;
    }

    public List<GoodRewardItem> getItems() {
        return items;
    }

    public void setItems(List<GoodRewardItem> items) {
        this.items = items;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
