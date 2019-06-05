package com.shanjin.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc TODO
 */
public interface MerchantQrCodeMapper {

    /**
     * 插入二维码路径
     * @param map
     * @return
     */
    int insert(Map<String,Object> map);

    /**
     * 根据商户id 更新二维码路径
     * @param path
     * @param merchantId
     * @return
     */
    int updatePathBymerchantId(@Param("path") String path,@Param("merchantId") Long merchantId);

    /**
     * 更具商户Id 查找二维码路径
     * @return
     */
    String queryPathBymerchatnId(@Param("merchantId") Long merchantId);
}
