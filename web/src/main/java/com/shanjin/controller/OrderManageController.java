package com.shanjin.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.service.IMerchantOrderManageService;

/**
 * 商户版 订单管理
 */
@Controller
@RequestMapping("/merchantOrderManage")
public class OrderManageController {

    @Resource
    private IMerchantOrderManageService merchantOrderManageService;
    
    /** 订单基础信息列表查询 */
    @RequestMapping("/basicOrderList")
    @SystemControllerLog(description = "订单基础信息列表查询")
    public @ResponseBody Object basicOrderList(String appType, Long merchantId, Long serviceType, int pageNo) {
        return this.merchantOrderManageService.selectBasicOrderList(appType,merchantId, serviceType, pageNo);
    }
    /** 订单详情信息查询 */
    @RequestMapping("/detailOrderInfo")
    @SystemControllerLog(description = "订单详情信息查询")
    public @ResponseBody Object detailOrderInfo(String appType,Long merchantId, Long orderId, Long serviceType) {
        return this.merchantOrderManageService.selectDetailOrderInfo(appType,merchantId,orderId,serviceType);
    }
    
    /** 立即抢单提供方案 */
    @RequestMapping("/immediately")
    @SystemControllerLog(description = "立即抢单提供方案")
    public @ResponseBody Object provideScheme(String appType, String phone, Long merchantId, Long orderId,
            BigDecimal planPrice, Long planType, String detail, HttpServletRequest request) {
        return this.merchantOrderManageService.immediately(phone, merchantId, appType,orderId, planPrice, planType, detail,
                request);
    }

    /** 隐藏订单 */
    @RequestMapping("/shieldOrder")
    @SystemControllerLog(description = "隐藏订单")
    public @ResponseBody Object shieldOrder(String appType, Long merchantId, Long orderId) {
        return this.merchantOrderManageService.shieldOrder(merchantId, orderId);
    }

    /** 推送场合，根据订单ID查询订单的基础信息 */
    @RequestMapping("/pushBasicOrder")
    @SystemControllerLog(description = "推送场合，根据订单ID查询订单的基础信息")
    public @ResponseBody Object basicOrderForPush(String appType, Long orderId) {
        return this.merchantOrderManageService.selectBasicOrderForPush(orderId);
    }

    /** 推送场合，根据订单ID查询订单的详细信息 */
    @RequestMapping("/pushDetailOrder")
    @SystemControllerLog(description = "推送场合，根据订单ID查询订单的详细信息")
    public @ResponseBody Object detailOrderForPush(String appType,Long merchantId, Long orderId, Long serviceType) {
        return detailOrderInfo(appType,merchantId, orderId,serviceType);
    }
}
