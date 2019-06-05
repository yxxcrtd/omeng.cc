package com.shanjin.financial.service;

import com.shanjin.financial.bean.ActivityFensiPaymentDetail;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.util.ExcelExportUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public interface ActivityFensiService {
    List<ActivityFensiPaymentDetail> getFansActivityData(ReqParamKit paramKit);

    List<ActivityFensiPaymentDetail> getExportFansActivityData();

    List<ExcelExportUtil.Pair> getExportFansActivityTitles();

    long getFansActivityDataTotals(ReqParamKit paramKit);
}
