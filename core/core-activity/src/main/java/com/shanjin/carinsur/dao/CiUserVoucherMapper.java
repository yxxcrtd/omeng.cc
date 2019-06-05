
package com.shanjin.carinsur.dao;


import com.shanjin.carinsur.model.CiUserVoucher;
import com.shanjin.common.util.CommonMybExtMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author hurd@omeng.cc
 * @date 2016-08-31 13:45:30 中国标准时间
 * @version v0.1
 * @desc TODO
 * @see
 */
public interface CiUserVoucherMapper extends CommonMybExtMapper<CiUserVoucher, Long> {


    /**
     * 乐观锁更新 券 状态
     *
     * @return
     */
    int updateVoucherStatus(@Param("vouNo") String vouNo, @Param("newStatus") int newStatus);


    /**
     * 根据券编号查询券信息
     *
     * @param vouNo
     * @return
     */
    CiUserVoucher getUserVoucherByVouNo(@Param("vouNo") String vouNo);

    /**
     * 根据车险券条件查询车险券列表信息
     *
     * @param voucher
     * @return
     */
    List<CiUserVoucher> findVounchrByParams(CiUserVoucher voucher);

    /**
     * 批量更新过去券
     * @param curTime
     */
    int batchUpdateExpireVouchers(@Param("curTime") Date curTime,@Param("newStatus") int newStatus,@Param("oldStatus") int oldStatus);
}