package com.shanjin.service;

import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc 微信静默登录相关接口
 */
public interface ISilenceLoginService {


    /**
     * 获取静默登录相关参数
     * @param openId  openId
     * @return
     */
    Map<String,Object> getSilenceLoginParams(String openId);

}
