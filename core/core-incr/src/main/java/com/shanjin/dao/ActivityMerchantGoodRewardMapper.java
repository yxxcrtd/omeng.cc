
package com.shanjin.dao;


import com.shanjin.incr.model.ActivityMerchantGoodReward;
import com.shanjin.incr.util.IMybExtMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-19 09:55:15 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface ActivityMerchantGoodRewardMapper extends IMybExtMapper<ActivityMerchantGoodReward, Long>{


    /**
     * 查找最新的诚信服务商好评奖励列表
     * @param merchantId
     * @param serviceKey
     * @return
     */
    List<ActivityMerchantGoodReward> findGoodRewardByMerchantId(@Param("merchantId") Long merchantId,@Param("serviceKey") String serviceKey);

}