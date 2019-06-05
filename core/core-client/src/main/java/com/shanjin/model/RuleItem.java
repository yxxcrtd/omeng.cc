package com.shanjin.model;

import java.io.Serializable;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/16
 * @desc 规则明细 --- 可以认为是 db Po 对应的Vo 提供上层应用使用
 * @see
 */
public class RuleItem implements Serializable {

    private String ruleCode;//规则码
    private Integer ruleValue;//规则定义值

    public RuleItem(String ruleCode, Integer ruleValue) {
        this.ruleCode = ruleCode;
        this.ruleValue = ruleValue;
    }
    public RuleItem() {
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Integer getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(Integer ruleValue) {
        this.ruleValue = ruleValue;
    }



}
