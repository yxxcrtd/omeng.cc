package com.shanjin.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.ResultJSONObject; 
import com.shanjin.dao.IMerchantStatisticsDao; 

import com.shanjin.service.IStatisticsService;

@Service("statisticsService")
public class StatisticsServiceImpl implements IStatisticsService {

    @Resource
    private IMerchantStatisticsDao iMerchantStatisticsDao;

    /** 商户统计信息 */
    @Override
    public JSONObject selectMerchantStatistics(String appType, Long merchantId) throws Exception{
        JSONObject jsonObject = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        // 商户ID
        paramMap.put("merchantId", merchantId);
        // 应用程序类型
        paramMap.put("appType", appType);
        Map<String, Object> merchantStatisticsMap = this.iMerchantStatisticsDao.selectMerchantStatistics(paramMap);
        if (merchantStatisticsMap != null) {
            jsonObject = new ResultJSONObject("000", "统计排行查询成功");
            // 抢单次数
            jsonObject.put("grabFrequency", String.valueOf(merchantStatisticsMap.get("grabFrequency")));
            // 服务次数
            jsonObject.put("serviceFrequency", String.valueOf(merchantStatisticsMap.get("serviceFrequency")));
            // 抢单排名
            jsonObject.put("grabRanking", String.valueOf(merchantStatisticsMap.get("grabRanking")));
            // 服务排名
            jsonObject.put("serviceRanking", String.valueOf(merchantStatisticsMap.get("serviceRanking")));
        } else {
            jsonObject = new ResultJSONObject("statistics_ranking_query_failure", "统计排行查询失败");
        }
        return jsonObject;
    }
}
