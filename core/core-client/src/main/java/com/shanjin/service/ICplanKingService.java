package com.shanjin.service;

import com.shanjin.model.PersAssInfo;

import java.util.Date;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/10/28
 * @desc 王牌会员c计划相关接口
 */
public interface ICplanKingService {


    /**
     * 根据用户id获取私人助理信息
     * @param userId 用户id、
     * @return 返回私人助理bean
     */
    PersAssInfo getPersAssInfoByUserId(Long userId);

    /**
     * 校验私人助理验证码
     * @param code 邀请码
     * @return if return is not null -->success
     *              return null -->fail
     */
    PersAssInfo validatePersAssInviteCode(String code);

    /**
     * 绑定私人助理
     * @param code 邀请码
     * @param userId 用户ID
     * @return
     */
    boolean bindPersAss(String code, Long userId, Date orderTime);

    /**
     * 查询商户私人助理信息
     * @param merchantId
     * @return
     */
    PersAssInfo queryPersAssInfoByMerchantId(Long merchantId);

    /**
     * 为店铺绑定私人助理信息
     * @param merchantId
     * @param inviteCode
     * @return
     */
    PersAssInfo bindPersAssForMch(Long merchantId,String inviteCode);


}
