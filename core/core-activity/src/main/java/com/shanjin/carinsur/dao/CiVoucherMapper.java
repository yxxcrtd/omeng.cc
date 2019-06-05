
package com.shanjin.carinsur.dao;


import com.shanjin.carinsur.model.CiVoucher;
import com.shanjin.common.util.CommonMybExtMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-31 13:45:30 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface CiVoucherMapper extends CommonMybExtMapper<CiVoucher, String>{


    /**
     * 根据用户券号查询voucher
     * @param vouNo
     * @return
     */
    CiVoucher getVoucherByVouNo(@Param("vouNo") String vouNo);

}