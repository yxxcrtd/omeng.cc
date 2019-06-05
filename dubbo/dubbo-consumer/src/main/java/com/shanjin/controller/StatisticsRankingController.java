package com.shanjin.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.service.IStatisticsService;

/**
 * 商户版 统计排行
 */
@Controller
@RequestMapping("/statisticsRanking")
public class StatisticsRankingController {

    @Reference
    private IStatisticsService statisticsService;
	private static final Logger logger = Logger.getLogger(StatisticsRankingController.class);

    /** 统计排行的信息显示 */
    @RequestMapping("/statisticsRankingShow")
    @SystemControllerLog(description = "统计排行的信息显示")
    public @ResponseBody Object statisticsRankingShow(String appType, Long merchantId) {
		System.out.println("创建个性服务的索引");
		JSONObject jsonObject = null;
		try {
			jsonObject= this.statisticsService.selectMerchantStatistics(appType, merchantId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"statisticsRankingShow");
			
			jsonObject = new ResultJSONObject("statisticsRankingShow_exception", "统计排行的信息显示失败");
			logger.error("统计排行的信息显示失败", e);
		}
		return jsonObject;
    }
}
