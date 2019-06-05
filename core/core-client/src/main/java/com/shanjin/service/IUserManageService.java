package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

public interface IUserManageService {

    /** 商户的用户信息 */
    public JSONObject selectMerchantUsers(String appType, Long merchantId, int pageNo)throws Exception;
}
