package com.shanjin.carinsur.service.impl;

import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.carinsur.dao.ActConfigInfoMapper;
import com.shanjin.carinsur.model.ActConfigInfo;
import com.shanjin.carinsur.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/12
 * @desc 配置信息数据获取
 */
@Service("configService")
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ActConfigInfoMapper configInfoMapper;
    @Autowired
    private ICommonCacheService commonCacheService;

    @Override
    public String getCfgValue(String cfgKey) {

        if (StringUtils.isEmpty(cfgKey)) {
            return null;
        }
        String key = CacheConstants.PREKEY_ACT_CONFIG_INFO + ":" + cfgKey;
        String value = (String) commonCacheService.getObject(key);
        if (StringUtils.isEmpty(value)) {
            ActConfigInfo actConfigInfo = configInfoMapper.getEntityByKey(cfgKey);
            if (null != actConfigInfo) {
                value = actConfigInfo.getCfgValue();
                if (!StringUtils.isEmpty(value)) {
                    commonCacheService.setObject(value, key);
                }
            }
        }
        return value;
    }
}
