package com.shanjin.service;

import com.shanjin.model.reward.GoodRewardInfo;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/20
 * @desc 好评奖励查询接口
 * @see
 */
public interface IGoodRewardService {


    /**
     * 查询好评奖励信息
     * @return
     */
     GoodRewardInfo queryGoodRewardInfo(Long merchantId);
}
