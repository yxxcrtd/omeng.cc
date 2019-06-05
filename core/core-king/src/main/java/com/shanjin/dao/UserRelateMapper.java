package com.shanjin.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/10/28
 * @desc 用户相关sql
 */
public interface UserRelateMapper {

    Map<String,Object> getUserInfoById(@Param("userId") Long userId);

    /**
     * 根据openId获取用户信息
     * @param openId
     * @return
     */
    Map<String,Object> getUserInfoByOpenId(@Param("openId") String openId);
    
    /**
     * 快捷支付H5登出：删除openid 关联的用户id
     * @param param
     * @return
     */
    public int delWechatUser(Map<String, Object> param);
}
