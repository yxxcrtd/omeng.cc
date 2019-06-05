package com.shanjin.financial.dao;

        import com.jfinal.plugin.activerecord.Record;
        import com.shanjin.financial.bean.OrderInfo;
        import com.shanjin.financial.bean.ReqParamKit;

        import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public interface OrderDao {
    List<OrderInfo> getOrderInfoData(ReqParamKit paramKit);

    List<OrderInfo> getExportOrderData();

    Record getOrderTotals(ReqParamKit paramKit);

    long getOrderInfoDataTotals(ReqParamKit paramKit);
}
