package com.shanjin.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.service.IUserManageService;

/**
 * 商户版 客户管理
 */
@Controller
@RequestMapping("/userManage")
public class UserManageController {

    @Resource
    private IUserManageService userManageService;

    /** 客户管理的列表显示 */
    @RequestMapping("/userShow")
    @SystemControllerLog(description = "客户管理的列表显示")
    public @ResponseBody Object userShow(String appType, Long merchantId, int pageNo) {
        return this.userManageService.selectMerchantUsers(appType, merchantId, pageNo);
    }
}
