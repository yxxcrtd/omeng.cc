package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IManagerSliderInfoDao {
	
	 // 获得用户首页图片轮询列表
    List<Map<String, Object>> getUserBannerList();
    
    
    
    // 获得商户首页图片轮询列表
    List<Map<String, Object>> getMerchantBannerList();
    
    
    //获取首页用户附近商家列表
    List<Map<String,Object>> getUserHomePageByRange(Map<String,Object> param);
    
    
    
    //按ids获取首页用户附近商家列表
    List<Map<String,Object>> getUserHomePageByIds(Map<String,Object> param);
    

    //获取若干商户的评价
    List<Map<String,Object>> getMerchantTags(Map<String,Object> merchantIds);
    
    
    //获取商户勾选服务对应的服务名称
    List<Map<String,Object>> getMerchantServiceName(Map<String,Object> merchantIds);
    
    

  
    
}
