package com.shanjin.service;

import com.shanjin.model.PersAssInfo;

import java.util.List;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc 联合营销接口
 */
public interface IUnitedMarketingService {

    /**
     * 是否加入联合营销
     * @param merchantId
     * @return
     */

    Map<String,Object> getUnitedMarketingInfo(Long merchantId);

    /**
     * 查询商户私人助理信息
     * @param merchantId
     * @return
     */
    PersAssInfo queryPersInfoForMerchant(Long merchantId);

    /**
     * 绑定商户私人助理信息
     * @param merchantId
     * @param inviteCode
     * @return
     */
    PersAssInfo bindMerchantPersAss(Long merchantId,String inviteCode);


    /**
     * 加入联合营销活动
     * @param merchantId
     */
    Map<String,Object> joinUmPlan(String merchantId);


    /**
     * 查询分成列表
     * @param merchantId
     * @return
     */
    List<Map<String,Object>>  findBainList(Long merchantId,int pageIndex,int pageSize);

}
