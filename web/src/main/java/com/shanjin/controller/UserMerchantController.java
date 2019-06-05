package com.shanjin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.service.IUserMerchantService;

/**
 * 用户商户控制器
 * 
 * @author 李焕民
 * @version 2015年5月22日
 *
 */

@Controller
@RequestMapping("/userMerchant")
public class UserMerchantController {
    @Autowired
    private IUserMerchantService userMerchantService;

    /**
     * 用户收藏商家
     * 
     * @throws InterruptedException
     */
    @RequestMapping("/collectionMerchant")
    @SystemControllerLog(description = "用户收藏商家")
    public @ResponseBody Object collectionMerchant(String appType,Long userId, Long merchantId) throws InterruptedException {
        JSONObject jsonObject = new ResultJSONObject();
        jsonObject = userMerchantService.collectionMerchant(appType,userId, merchantId);
        return jsonObject;
    }

    /**
     * 删除用户收藏商家
     * 
     * @throws InterruptedException
     */
    @RequestMapping("/delCollectionMerchant")
    @SystemControllerLog(description = "删除用户收藏商家")
    public @ResponseBody Object delCollectionMerchant(String appType,Long userId, String merchantId) throws InterruptedException {
        JSONObject jsonObject = new ResultJSONObject();
        jsonObject = userMerchantService.delCollectionMerchant(appType,userId, merchantId);
        return jsonObject;
    }

    /**
     * 获得收藏的商家信息
     * 
     * @throws InterruptedException
     */
    @RequestMapping("/getCollectionMerchant")
    @SystemControllerLog(description = "获得收藏的商家信息")
    public @ResponseBody Object getCollectionMerchant(String appType,Long userId, int pageNo) throws InterruptedException {
        JSONObject jsonObject = new ResultJSONObject();
        jsonObject = userMerchantService.getCollectionMerchant(appType,userId, pageNo);
        return jsonObject;
    }

}
