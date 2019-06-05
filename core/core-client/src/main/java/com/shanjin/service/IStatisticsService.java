package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

public interface IStatisticsService {

    /** 商户统计信息 */
    public JSONObject selectMerchantStatistics(String appType, Long merchantId) throws Exception; 
}
