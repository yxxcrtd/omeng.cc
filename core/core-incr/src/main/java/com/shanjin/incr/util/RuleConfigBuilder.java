package com.shanjin.incr.util;

import com.shanjin.model.RuleConfig;
import com.shanjin.model.RuleItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/25
 * @desc ruleConfig 工厂类
 */
public class RuleConfigBuilder {


    private static final Logger logger = LoggerFactory.getLogger(RuleConfigBuilder.class);

    /**
     * RuleConfig 构建
     *
     * @return
     */
    public static RuleConfig build(Map<String, String> map, Long merchantId) {

        RuleConfig ruleConfig = getRawRuleConfig(merchantId);
        if (null != ruleConfig) {
            if (null != map || !map.isEmpty()) {
                for (RuleConfig.RuleCodeEnum ruleCodeEnum : RuleConfig.RuleCodeEnum.values()) {
                    String ev = map.get(ruleCodeEnum.getRuleCode());
                    if (null != ev) {
                        Object value;
                        if (Boolean.class.isAssignableFrom(ruleCodeEnum.getClazz())) {
                            value = Boolean.valueOf(ev);
                        } else if (Integer.class.isAssignableFrom(ruleCodeEnum.getClazz())) {
                            value = Integer.valueOf(ev);
                        } else {
                            throw new RuntimeException("存在其他返回类型字段待处理，类型：" + ruleCodeEnum.getClazz().getName());
                        }
                        //设置字段值
                        setField(ruleConfig, ruleCodeEnum.getField(), value);
                    }
                }
            }
        }
        return ruleConfig;
    }

    /**
     * RuleConfig 构建
     *
     * @return
     */
    public static RuleConfig build(List<RuleItem> ruleList, Long merchantId) {
        RuleConfig ruleConfig = getRawRuleConfig(merchantId);

        if (!CollectionUtils.isEmpty(ruleList) && null !=ruleConfig) {
            for (RuleItem rule : ruleList) {
                RuleConfig.RuleCodeEnum rcEnum = RuleConfig.RuleCodeEnum.getRuleCodeEnum(rule.getRuleCode());
                if (null != rcEnum) {
                    Object result = null;
                    if (Boolean.class.isAssignableFrom(rcEnum.getClazz())) {
                        result = true;
                    } else if (Integer.class.isAssignableFrom(rcEnum.getClazz())) {
                        Integer value = (Integer) getField(ruleConfig, rcEnum.getField());
                        Integer newVal = rule.getRuleValue();
                        //newVal 为库表配置必须不能为空
                        if (null == newVal) {
                            throw new RuntimeException("规则配置类初始化异常,库表数据异常，incRule记录信息:" + rule.toString());
                        }
                        if (value == null || newVal.compareTo(value) > 0) {
                            result = newVal;
                        }
                    }
                    //值设定
                    if (null != result) {
                        setField(ruleConfig, rcEnum.getField(), result);
                    }
                }
            }
        }
        return ruleConfig;
    }


    private static final RuleConfig getRawRuleConfig(Long merchantId) {
        try {
            RuleConfig ruleConfig = RuleConfig.class.newInstance();
            setField(ruleConfig, "merchantId", merchantId);
            return ruleConfig;
        } catch (Exception e) {
            logger.error("反射ruleConfig异常", e);
        }
        return null;
    }

    private static void setField(RuleConfig ruleConfig, String fieldName, Object value) {
        try {
            Field field = RuleConfig.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(ruleConfig, value);
            field.setAccessible(false);
        } catch (Exception e) {
            logger.error("反射ruleConfig异常", e);
        }
    }

    private static Object getField(RuleConfig ruleConfig, String fieldName) {
        Object value = null;
        try {
            Field field = RuleConfig.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            value = field.get(ruleConfig);
            field.setAccessible(false);
        } catch (Exception e) {
            logger.error("反射ruleConfig异常", e);
        }
        return value;
    }


    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

    }

}
