package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/22
 * @desc 商户服务类型
 * @see
 */
public class MerchantServiceType {

    private Long merchantId;
    private Long serviceTypeId;
    private String appType;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }


    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Long getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Long serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


}
