package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 用户订单业务接口
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */
public interface IUserMerchantService {
    /** 获取收藏商户列表 */
    public JSONObject collectionMerchant(Long userId, Long merchantId, Long receiveEmployeesId)throws Exception;

    /** 删除用户收藏的商家 */
    public JSONObject delCollectionMerchant(Long userId, String merchantId)throws Exception;

    /** 获得用户收藏的商家列表信息 */
    public JSONObject getCollectionMerchant(String appType,Long userId, int pageNo)throws Exception;

}
