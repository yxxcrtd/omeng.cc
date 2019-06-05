
package com.shanjin.dao;


import com.shanjin.common.util.CommonMybExtMapper;
import com.shanjin.model.king.KingActOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-10-28 16:04:54 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface KingActOrderMapper extends CommonMybExtMapper<KingActOrder, Long>{


    /**
     * 原子操作新增订单
     * @param order
     * @return
     */
    int atomInsertOrder(KingActOrder order);

    /**
     * 原子操作你更新订单
     * @param order
     * @return
     */
    int atomUpdateConfirmOrder(KingActOrder order);


    /**
     * 用户确认或待确认订单数
     * @param userId
     * @return
     */
    int getConfirmOrderCount(@Param("userId") Long userId);

    /**
     * 查询订单id和名称
     * @param innerTradeNo
     * @return
     */
    Map<String,Object> selectKingOrderByInnerTradeNo(@Param("innerTradeNo") String innerTradeNo);
}