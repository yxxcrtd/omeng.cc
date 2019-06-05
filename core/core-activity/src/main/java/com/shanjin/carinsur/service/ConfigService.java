package com.shanjin.carinsur.service;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/12
 * @desc 配置服务
 */
public interface ConfigService {

    /**
     *
     * @param cfgKey
     * @return 返回值
     */
    String  getCfgValue(String cfgKey);
}
