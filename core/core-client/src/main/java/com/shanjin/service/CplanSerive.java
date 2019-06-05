package com.shanjin.service;

import com.shanjin.model.PersAssInfo;

import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/17
 * @desc TODO
 * @see
 */
public interface CplanSerive {

    /**
     *查询商户 私人助理信息
     * @Param merchantId 店铺Id
     * @return 返回用户信息
     */
    PersAssInfo getPersAssInfo(Long merchantId)  throws RuntimeException;


    /**
     * 为私人助理开店
     * @param persAssInfo
     */
    Map<String,Object> openShopForPersAss(PersAssInfo persAssInfo);

    /**
     * 为私人助理关点
     * @param userId
     * @param merchantId
     */
    void closeShopForPersAss(Long userId,Long merchantId);
}
