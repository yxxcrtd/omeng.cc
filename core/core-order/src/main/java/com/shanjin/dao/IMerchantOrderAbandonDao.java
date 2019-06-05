package com.shanjin.dao;

import java.util.Map;

/**
 * 字典表Dao
 */
public interface IMerchantOrderAbandonDao {

    /** 屏蔽订单，将商户不想看的订单隐藏 */
    int insertMerchantOrderAbandon(Map<String, Object> paramMap);
}
