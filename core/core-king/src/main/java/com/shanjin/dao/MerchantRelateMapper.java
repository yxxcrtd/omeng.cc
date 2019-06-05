package com.shanjin.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc TODO
 */
public interface MerchantRelateMapper {

    /**
     * 查询商户相关信息  如  头像 ，商户名
     * @param merchantId
     * @return
     */
    Map<String,Object> getBasicMerchantInfo(@Param("merchantId") Long merchantId);


    /**
     * 获取详细商户信息
     * @param merchant
     * @return
     */
    Map<String,Object> getDetailMerchantInfo(@Param("merchantId") Long merchant);

}
