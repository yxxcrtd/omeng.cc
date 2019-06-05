package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 代金券Dao
 */
public interface IVouchersInfoDao {

    /** 代金券信息查询 */
    List<Map<String, Object>> selectVouchersType(Map<String, Object> paramMap);

    /** 查询代金券详情 */
    Map<String, Object> getVouchersInfo(Map<String, Object> paramMap);
    /**
     * 查询代金券的总数
     */
    Map<String, Object> getSurplusVouchersNumber(Long vouchersId);
    
    /** 查询代金券的截止日期 */
    String selectVouchersCutoffTime(Map<String, Object> paramMap);
}
