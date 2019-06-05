package com.shanjin.financial.service;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.ActivityOrderRewardDetail;
import com.shanjin.financial.bean.ReqParamKit;

import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public interface ActivityOrderRewardService {
    List<ActivityOrderRewardDetail> getOrderRewardData(ReqParamKit paramKit);

    long getOrderRewardDataTotals(ReqParamKit paramKit);

    Record getOrderRewardTotals(ReqParamKit paramKit);
}
