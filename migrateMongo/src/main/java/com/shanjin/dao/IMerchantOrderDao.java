package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IMerchantOrderDao {

    List<Map<String, Object>> getMerchantOrderInfo(Map<String, String> param);

    
    List<Map<String, Object>> getMerchantsForSpeicalOrder(Map<String, Object> orderParam);
}
