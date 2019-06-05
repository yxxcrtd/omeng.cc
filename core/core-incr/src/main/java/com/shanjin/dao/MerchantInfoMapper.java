

package com.shanjin.dao;


import com.shanjin.incr.model.MerchantInfo;
import com.shanjin.incr.util.IMybExtMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-17 19:21:29 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface MerchantInfoMapper extends IMybExtMapper<MerchantInfo, Long> {


    /**
     * 根据电话号码查找私人助理商户信息
     * @param phone
     * @return
     */
    List<MerchantInfo> findPersAssistantByPhone(@Param("phone") String phone);

}