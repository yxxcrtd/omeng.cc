package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户客户信息表Dao
 */
public interface IMerchantUsersDao {

    /** 商户客户信息记录数查询 */
    int selectMerchantUsersCount(Map<String, Object> paramMap);

    /** 商户客户信息查询 */
    List<Map<String, Object>> selectMerchantUsers(Map<String, Object> paramMap);
}
