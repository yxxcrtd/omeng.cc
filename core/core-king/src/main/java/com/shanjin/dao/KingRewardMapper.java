package com.shanjin.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc TODO
 */
public interface KingRewardMapper {

    /**
     * 查询shangthu8的
     * @param merchantId
     * @return
     */
    BigDecimal getMerhcantTotalRewardAmount(@Param("merchantId") Long merchantId);

    /**
     * 查找奖励列表
     * @param merchantId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Map<String,Object>> findRewardItemList(@Param("merchantId") Long merchantId,@Param("pageNum") int pageNum,@Param("pageSize") int pageSize);
}
