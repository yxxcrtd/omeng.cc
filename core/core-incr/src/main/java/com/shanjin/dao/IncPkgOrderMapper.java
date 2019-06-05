package com.shanjin.dao;


import com.shanjin.incr.model.IncPkgOrder;
import com.shanjin.incr.util.IMybExtMapper;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-12 17:22:43 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface IncPkgOrderMapper extends IMybExtMapper<IncPkgOrder, Long> {


    /**
     * 获取商户最大订单Id
     * @param merchantId
     * @return
     */
    Long getMaxPkgOrderIdforMerchantId(Long merchantId);
}