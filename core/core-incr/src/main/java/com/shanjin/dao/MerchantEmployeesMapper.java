

package com.shanjin.dao;


import com.shanjin.incr.model.MerchantEmployees;
import com.shanjin.incr.util.IMybExtMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-17 21:22:51 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface MerchantEmployeesMapper extends IMybExtMapper<MerchantEmployees, Long> {


    /**
     * 逻辑删除 商户下的所有雇员信息
     * @param merchantId
     */
	void logicDelByMerchantId(@Param("merchantId") Long merchantId);

}