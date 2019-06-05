package com.shanjin.financial.service;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.ActivityCuttingDetail;
import com.shanjin.financial.bean.ReqParamKit;

import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public interface ActivityCuttingDetailService {
    List<ActivityCuttingDetail> getGiftData(ReqParamKit paramKit);

    long getGiftDataTotals(ReqParamKit paramKit);

    Record getGiftTotals(ReqParamKit paramKit);
}
