package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 用户商家收藏业务DAO
 * 
 * @author 李焕民
 * @version 2015年5月22日
 *
 */
public interface IUserMerchant2Dao {
    /** 增加用户商家收藏 */
    int collectionMerchant(Map<String, Object> paramMap);
    
    /** 删除用户商家收藏 */
    int delCollectionMerchant(Map<String, Object> paramMap);
    
    /** 验证用户是否收藏商家 */
    int checkCollectionMerchant(Map<String, Object> paramMap);
    
    /** 获得用户收藏的商家信息 */
    List<Map<String, Object>> getCollectionMerchant(Map<String, Object> paramMap);
    
    /** 获得用户收藏的商家信息列表总页数 */
    int getCollectionMerchantTotalPage(Map<String, Object> paramMap);
}