package com.shanjin.incr.service.impl;

import com.shanjin.dao.IncItemMapper;
import com.shanjin.dao.IncPkgOrderMapper;
import com.shanjin.dao.IncRuleMapper;
import com.shanjin.incr.model.IncItem;
import com.shanjin.incr.model.IncPkgOrder;
import com.shanjin.incr.util.RuleConfigBuilder;
import com.shanjin.incr.util.RuleConfigCache;
import com.shanjin.model.RuleConfig;
import com.shanjin.model.RuleItem;
import com.shanjin.service.IncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author hurd@omeng.cc
 * @version v0.2
 * @date 2016/8/25
 * @desc 添加缓存....
 */

@Service("incService")
public class IncServiceImpl implements IncService {


    @Autowired
    private IncPkgOrderMapper pkgOrderMapper;
    @Autowired
    private IncItemMapper itemMapper;
    @Autowired
    private IncRuleMapper ruleMapper;
    @Autowired
    private RuleConfigCache ruleConfigCache;

    private static final Logger logger = LoggerFactory.getLogger(IncServiceImpl.class);

    @Override
    public List<RuleConfig> getRuleConfig(Long... merchantIds) {
        long start = new Date().getTime();
        if (null == merchantIds || merchantIds.length == 0) {
            return null;
        }
        List<RuleConfig> list = new ArrayList<>(merchantIds.length);
        for (Long merchantId : merchantIds) {
            list.add(obtainRuleConfig(merchantId));
        }

        long end = new Date().getTime();
        logger.info("服务耗时：{}ms", end - start);
        return list;
    }

    private RuleConfig obtainRuleConfig(Long merchantId) {
        //获取商户最新订单Id
        Long orderId = pkgOrderMapper.getMaxPkgOrderIdforMerchantId(merchantId);
        if (null == orderId) {
            return RuleConfigBuilder.build((List<RuleItem>) null, merchantId);
        }
        RuleConfig ruleConfig = ruleConfigCache.getRuleConfig(merchantId, orderId);
        if (null == ruleConfig) {

            //查找商户下的所有服务包
            IncPkgOrder paramOrder = new IncPkgOrder();
            paramOrder.setIsDel(false);
            paramOrder.setMerchantId(merchantId);
            List<IncPkgOrder> orders = pkgOrderMapper.findByParamObj(paramOrder);
            logger.info("商户查找用户套餐订单：{}", orders);
            List<RuleItem> ruleList = null;
            Set<Long> itemIds = new HashSet<>();
            Long merchantOrderTime = null;
            //原子服务过期校验
            for (IncPkgOrder order : orders) {
                Long orderTime = order.getEffictiveTime().getTime();
                List<IncItem> list = itemMapper.findItemsByPkgId(order.getPkgId(), false);
                if (!CollectionUtils.isEmpty(list)) {
                    Integer days = null;
                    for (IncItem item : list) {
                        //原子服务过期校验
                        if (null != item.getEffictiveDays()) {
                            //过期不做处理
                            if (isExpire(item.getEffictiveDays(), order.getEffictiveTime())) {
                                continue;
                            } else {
                                if (null == days) {
                                    days = item.getEffictiveDays();
                                } else {
                                    days = days > item.getEffictiveDays() ? item.getEffictiveDays() : days;
                                }
                            }
                        }
                        itemIds.add(item.getId());
                    }
                    //取最小有效期
                    if (null != days) {
                        Long newMerchantOrderTime = orderTime + days * 24 * 3600 * 1000;
                        if (null == merchantOrderTime) {
                            merchantOrderTime = newMerchantOrderTime;
                        } else {
                            merchantOrderTime = merchantOrderTime > newMerchantOrderTime ? newMerchantOrderTime : merchantOrderTime;
                        }
                    }
                }
            }
            //有效期内的原子服务查询规则列表
            if (!itemIds.isEmpty()) {
                //规则列表查找
                ruleList = ruleMapper.findRulesByItemIds(itemIds);
            }
            ruleConfig = RuleConfigBuilder.build(ruleList,merchantId);

            // 设置缓存
            ruleConfigCache.setRuleConfig(merchantId, ruleConfig, merchantOrderTime, orderId);
        }

        return ruleConfig;
    }

    /**
     * 原子服务过期校验
     *
     * @param effictTime 生效时间
     * @return 返回是否过期标识
     */
    private boolean isExpire(Integer days, Date effictTime) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(effictTime);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);

        return calendar.getTime().compareTo(new Date()) < 0;
    }
}
