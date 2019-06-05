package com.shanjin.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/16
 * @desc 规则配置信息载体, 显示指定商户应具备的规则配置信息
 * @see
 */
public class RuleConfig implements Serializable {

    private static final long serialVersionUID = 1884151029570334151L;
    private static final Logger logger = LoggerFactory.getLogger(RuleConfig.class);

    private Boolean adviserCall = false; // 是否一键呼叫
    private Integer goodsUpperLimit = null; //获取商品上限个数
    private Boolean contractMerchantOrder = false;//是否店铺排序
    private Boolean vipMerchantOrder = false;


    private Boolean contractActivityPrivilege = false;//是否享有活动特权

    private Boolean vipActivityPrivilege = false;//是否享有活动特权

    private Boolean praiseAward = false;//是否好评奖励
    private Boolean vipLogoShow = false;//是否显示vip Logo

    private Long merchantId; //所属商户ID


    /**
     * 不允许 new 实例化 操作
     */
    public RuleConfig(){}

    /**
     * 是否一键呼叫服务
     *
     * @return
     */
    public Boolean isAdviserCall() {
        return adviserCall;
    }

    /**
     * 获取商品上限个数
     * 为空 说明不存在该规则
     *
     * @return
     */
    public Integer getGoodsUpperLimit() {
        return goodsUpperLimit;
    }

    /**是否签约商户店铺排序
     * @return
     */
    public Boolean isContractMerchantOrder() {
        return contractMerchantOrder;
    }

    /**
     * 是否vip店铺排序
     * @return
     */
    public Boolean isVipMerchantOrder() {
        return vipMerchantOrder;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * 是否签约商户特权服务
     * @return
     */
    public Boolean isContractActivityPrivilege() {
        return contractActivityPrivilege;
    }

    /**
     * 是否vip特权服务
     * @return
     */
    public Boolean isVipActivityPrivilege() {
        return vipActivityPrivilege;
    }

    /**
     * 是否好评奖励
     * @return
     */
    public Boolean isPraiseAward() {
        return praiseAward;
    }

    /**
     * 是否vip标签显示
     * @return
     */
    public Boolean isVipLogoShow() {
        return vipLogoShow;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    /**
     * 规则码枚举值 后期随着业务扩展依次添加
     */
    public enum RuleCodeEnum {


        ADVISER_CALL("adviser_call", Boolean.class, "adviserCall"),//一键呼叫规则
        GOODS_UPPER_LIMIT("goods_upper_limit", Integer.class, "goodsUpperLimit"),//商品上限规则
        CONTRACT_MERCHANT_ORDER("contract_merchant_order", Boolean.class, "contractMerchantOrder"),//签约商检店铺排序
        VIP_MERCHANT_ORDER("vip_merchant_order", Boolean.class, "vipMerchantOrder"),//vip店铺排序规则

        CONTRACT_ACTIVITY_PRIVILEGE("contract_activity_privilege", Boolean.class, "contractActivityPrivilege"),//签约商家活动特权规则
        VIP_ACTIVITY_PRIVILEGE("vip_activity_privilege", Boolean.class, "vipActivityPrivilege"),//活动特权规则
        PRAISE_AWARD("praise_award", Boolean.class, "praiseAward"),//好评奖励
        VIP_LOGO_SHOW("vip_logo_show", Boolean.class, "vipLogoShow"),;//vip标识显示规则

        private String ruleCode;
        private Class<?> clazz;
        private String field;

        RuleCodeEnum(String ruleCode, Class<?> clazz, String field) {
            this.clazz = clazz;
            this.ruleCode = ruleCode;
            this.field = field;
        }

        public String getRuleCode() {
            return ruleCode;
        }

        public Class<?> getClazz() {
            return clazz;
        }


        /**
         * 基于规则编码返回规则码枚举
         *
         * @param ruleCode 规则编码 对应数据库中的 code字段
         * @return 返回 匹配的规则码信息，如果不存在 返回null
         */
        public static final RuleCodeEnum getRuleCodeEnum(String ruleCode) {
            for (RuleCodeEnum rce : RuleCodeEnum.values()) {
                if (rce.ruleCode.equals(ruleCode)) {
                    return rce;
                }
            }
            return null;
        }

        public String getField() {
            return field;
        }
    }


}
