package com.shanjin.incr.service.impl;

import com.shanjin.dao.ActivityMerchantGoodRewardMapper;
import com.shanjin.dao.IncPkgOrderMapper;
import com.shanjin.incr.model.ActivityMerchantGoodReward;
import com.shanjin.model.reward.GoodRewardInfo;
import com.shanjin.model.reward.GoodRewardItem;
import com.shanjin.service.IGoodRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/22
 * @desc 好评奖励 实现  查询明细展示
 * @see
 */
@Service("goodRewardService")
public class GoodRewardServiceImpl implements IGoodRewardService {


    private static final BigDecimal TOTAL_REWARD_AMOUNT = new BigDecimal(1000);//总奖励金额  暂时写死

    @Autowired
    private ActivityMerchantGoodRewardMapper goodRewardMapper;
    @Autowired
    private IncPkgOrderMapper incPkgOrderMapper;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");

    private static final String CXFWS_SERVICE_KEY="merchant";

    /**
     * 可能存在多包情况 暂时不考虑 多次享受好评奖励的记录
     */
    @Override
    public GoodRewardInfo queryGoodRewardInfo(Long merchantId) {

        GoodRewardInfo goodRewardInfo = new GoodRewardInfo();
        goodRewardInfo.setTotalAmount(TOTAL_REWARD_AMOUNT);


        // 查询记录
        ActivityMerchantGoodReward reward = new ActivityMerchantGoodReward();
        reward.setMerchantId(merchantId);

        List<ActivityMerchantGoodReward> list = goodRewardMapper.findGoodRewardByMerchantId(merchantId,CXFWS_SERVICE_KEY);

        if (CollectionUtils.isEmpty(list)) {
            goodRewardInfo.setSurplusAmount(TOTAL_REWARD_AMOUNT);
            goodRewardInfo.setItems(Collections.EMPTY_LIST);
        } else {
            List<GoodRewardItem> items = new ArrayList<>(list.size());
            BigDecimal countMoney = BigDecimal.ZERO;

            for (ActivityMerchantGoodReward goodReward : list) {
                countMoney = countMoney.add(goodReward.getRewardAmount());

                GoodRewardItem item = new GoodRewardItem();
                item.setMerchantScore(goodReward.getRewardMonth().substring(4) + "月份评分" + goodReward.getScore().toString());
                item.setRewardAmount(goodReward.getRewardAmount());
                item.setRewardMonth(goodReward.getRewardMonth());
                item.setRewardDate(simpleDateFormat.format(goodReward.getRewardDate()));

                items.add(item);
            }
            //排序 asc 排序
            Collections.sort(items);
            goodRewardInfo.setItems(items);
            goodRewardInfo.setSurplusAmount(goodRewardInfo.getTotalAmount().subtract(countMoney));
        }

        return goodRewardInfo;
    }

}
