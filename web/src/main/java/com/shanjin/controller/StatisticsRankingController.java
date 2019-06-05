package com.shanjin.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.service.IStatisticsService;

/**
 * 商户版 统计排行
 */
@Controller
@RequestMapping("/statisticsRanking")
public class StatisticsRankingController {

    @Resource
    private IStatisticsService statisticsService;

    /** 统计排行的信息显示 */
    @RequestMapping("/statisticsRankingShow")
    @SystemControllerLog(description = "统计排行的信息显示")
    public @ResponseBody Object statisticsRankingShow(String appType, Long merchantId) {
        return this.statisticsService.selectMerchantStatistics(appType, merchantId);
    }
}
