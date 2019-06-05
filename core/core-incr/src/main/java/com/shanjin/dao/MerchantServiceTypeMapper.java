package com.shanjin.dao;

import com.shanjin.incr.model.MerchantServiceType;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/22
 * @desc 商户服务类型Id
 * @see
 */
public interface MerchantServiceTypeMapper {

    /**
     * 保存 商户服务类型
     * @param merchantServiceType
     * @return
     */
    MerchantServiceType saveEntity(MerchantServiceType merchantServiceType);
}
