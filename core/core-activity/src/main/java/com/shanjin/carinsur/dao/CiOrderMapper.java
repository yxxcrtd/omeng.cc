
package com.shanjin.carinsur.dao;


import com.shanjin.common.util.CommonMybExtMapper;
import com.shanjin.carinsur.model.CiOrder;
import org.apache.ibatis.annotations.Param;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-31 13:45:30 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface CiOrderMapper extends CommonMybExtMapper<CiOrder, Long>{


    /**
     * 根据订单编号查询订单信息
     * @param orderNo
     * @return
     */
     CiOrder getCiOrderByOrderNo(@Param("orderNo") String orderNo);


    CiOrder getOrderByBiz(CiOrder ciOrder);
}