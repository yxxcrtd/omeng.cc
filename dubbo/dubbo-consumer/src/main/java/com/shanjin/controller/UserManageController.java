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
import com.shanjin.service.IUserManageService;

/**
 * 商户版 客户管理
 */
@Controller
@RequestMapping("/userManage")
public class UserManageController {

    @Reference
    private IUserManageService userManageService;
    
	private static final Logger logger = Logger.getLogger(UserManageController.class);

    /** 客户管理的列表显示 */
    @RequestMapping("/userShow")
    @SystemControllerLog(description = "客户管理的列表显示")
    public @ResponseBody Object userShow(String appType, Long merchantId, int pageNo) {
		JSONObject jsonObject = null;
		try {
			jsonObject= this.userManageService.selectMerchantUsers(appType, merchantId, pageNo);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"userShow");
			
			jsonObject = new ResultJSONObject("userShow_exception", "客户管理的列表显示失败");
			logger.error("客户管理的列表显示失败", e);
		}
		return jsonObject;
    }
}
