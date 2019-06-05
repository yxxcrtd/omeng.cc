package com.shanjin.dao;

import java.util.Map;

/**
 * 商户申请提现记录表Dao
 */
public interface IMerchantApplyWithdrawRecordDao {

    /** 商户申请提现记录的保存 */
    int insertMerchantApplyWithdrawRecord(Map<String, Object> merchantApplyWithdrawRecordMap);

    /** 查看当日是否有提现记录 */
    Map<String,Object> selectLastWithdrawRecord(Map<String, Object> paramMap);
}
