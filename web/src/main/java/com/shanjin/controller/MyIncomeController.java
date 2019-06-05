package com.shanjin.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.service.IDictionaryService;
import com.shanjin.service.IMyIncomeService;

/**
 * 商户版 我的收入
 */
@Controller
@RequestMapping("/myIncome")
public class MyIncomeController {

    @Resource
    private IDictionaryService dictionaryService;

    @Resource
    private IMyIncomeService myIncomeService;

    /** 我的收入 */
    @RequestMapping("/myIncomeShow")
    @SystemControllerLog(description = "我的收入")
    public @ResponseBody Object myIncomeShow(String appType, Long merchantId) {
        return this.myIncomeService.selectMyIncome(appType, merchantId);
    }

    /** 绑定银行卡 初期化（银行信息列表加载,当前商户已绑定银行卡查询） */
    @RequestMapping("/bindingBank")
    @SystemControllerLog(description = "银行信息列表加载,当前商户已绑定银行卡查询")
    public @ResponseBody Object bindingBank(String appType, Long merchantId) {
        return this.myIncomeService.bindingBank(appType, merchantId);
    }

    /** 绑定银行卡 确定 */
    @RequestMapping("/bankCardSave")
    @SystemControllerLog(description = "绑定银行卡")
    public @ResponseBody Object bankCardSave(@RequestParam Map<String, Object> requestParamMap) {
        return this.myIncomeService.insertMerchantWithdraw(requestParamMap);
    }

    /** 申请提现 初期化（可绑定的银行卡信息加载） */
    @RequestMapping("/applyWithdrawInit")
    @SystemControllerLog(description = "可绑定的银行卡信息加载")
    public @ResponseBody Object applyWithdrawInit(String appType, Long merchantId) {
        return this.myIncomeService.applyWithdrawInit(appType, merchantId);
    }

    /** 申请提现 确定 */
    @RequestMapping("/applyWithdrawSave")
    @SystemControllerLog(description = "申请提现")
    public @ResponseBody Object applyWithdrawSave(String appType, Long merchantId, Long withdraw, String withdrawNo,
            String withdrawPrice, String payPassword) {
        return this.myIncomeService.insertApplyWithdrawRecord(appType, merchantId, withdraw, withdrawNo,
                withdrawPrice, payPassword);
    }

    /** 收支明细 */
    @RequestMapping("/paymentDetails")
    @SystemControllerLog(description = "收支明细")
    public @ResponseBody Object paymentDetails(String appType, Long merchantId, int pageNo) {
        return this.myIncomeService.selectPaymentDetails(appType, merchantId, pageNo);
    }
}
