package com.shanjin.incr.util;

import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.model.RuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/25
 * @desc 规则配置缓存
 * hash 存储
 * 1.pkgIdfield 不一致 重新库表查询
 * 2.规则最早过期时间判断 --过期 重启库表查询
 * --未过期 实例化成规则返回
 */
@Service("ruleConfigCache")
public class RuleConfigCache {


    private static final Logger logger = LoggerFactory.getLogger(RuleConfigCache.class);

    private static final String RULE_CONFIG_HASH_PREKEY = "ruleconfig:merchantid:";

    private static final String FIELD_PKG_ORDER_ID = "pkgOrderId";

    private static final String FIELD_MIN_VALIDITY_TIME = "minValidityTime";//最短有效期

    @Autowired
    private ICommonCacheService commonCacheService;


    /**
     * 获取商户规则配置信息
     *
     * @param merchantId
     * @return
     */
    public RuleConfig getRuleConfig(Long merchantId, Long latestOrderId) {
        String key = RULE_CONFIG_HASH_PREKEY + merchantId;
        Map<String, String> map = commonCacheService.hmget(key);
        if (null != map && !map.isEmpty()) {
            String orderId = map.get(FIELD_PKG_ORDER_ID);
            String validTime = map.get(FIELD_MIN_VALIDITY_TIME);
            if (StringUtils.isEmpty(orderId)) {
                return null;
            }
            //订单id比较
            if (!orderId.equals(latestOrderId.toString())) {
                return null;
            }
            //有效期校验
            if (!StringUtils.isEmpty(validTime)) {
                long validTimeLong = Long.parseLong(validTime);
                if (validTimeLong < new Date().getTime()) {
                    return null;
                }
            }
            //创建RuleConfig
            return RuleConfigBuilder.build(map,merchantId);
        }
        return null;
    }


    /**
     * 缓存ruleCOnfig
     *
     * @param merchantId
     * @param ruleConfig
     */
    public void setRuleConfig(Long merchantId, RuleConfig ruleConfig, Long validDate, Long orderId) {

        String key = RULE_CONFIG_HASH_PREKEY + merchantId;

        Map<String, String> map = parseCacheMap(ruleConfig);
        if (null != map) {
            map.put(FIELD_PKG_ORDER_ID, orderId + "");
            map.put(FIELD_MIN_VALIDITY_TIME, validDate == null ? "" : validDate.toString());
        }
        commonCacheService.hmset(key, map);
    }


    private Map<String, String> parseCacheMap(RuleConfig ruleConfig) {

        Map<String, String> cacheMap = new HashMap<>();

        try {
            for (RuleConfig.RuleCodeEnum codeEnum : RuleConfig.RuleCodeEnum.values()) {
                Class<?> resultType = codeEnum.getClazz();
                Class<?> clazz = ruleConfig.getClass();
                Field field = clazz.getDeclaredField(codeEnum.getField());
                boolean accessAble = field.isAccessible();
                field.setAccessible(true);
                Object value = field.get(ruleConfig);
                field.setAccessible(accessAble);
                //类型判断
                if (resultType.isAssignableFrom(Boolean.class)) {
                    Boolean val = (Boolean) value;
                    if (val.equals(true)) {
                        cacheMap.put(codeEnum.getRuleCode(), val.toString());
                    }
                } else {
                    if (null != value) {
                        cacheMap.put(codeEnum.getRuleCode(), value.toString());
                    }
                }
            }
            return cacheMap;
        } catch (Exception e) {
            logger.error("反射字段异常，确认ruleCodeEnum 枚举类的field 是否匹配...", e);
            return null;
        }
    }

}
