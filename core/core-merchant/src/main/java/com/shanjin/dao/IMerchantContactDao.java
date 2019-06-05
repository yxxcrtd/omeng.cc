package com.shanjin.dao;

import java.util.Map;

/**
 * 商户联系方式表Dao
 */
public interface IMerchantContactDao {

    /** 查询商户的联系方式 */
    String selectTelephone(Map<String, Object> paramMap);

    /** 保存商户的联系方式 */
    int insertTelephone(Map<String, Object> paramMap);

    /** 更新商户的联系方式 */
    int updateTelephone(Map<String, Object> paramMap);
}
