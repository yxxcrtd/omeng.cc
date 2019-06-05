package com.shanjin.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商户代金券权限表Dao
 */
public interface IMerchantVouchersPermissionsDao {

    /** 查询当前还未被领取的代金券记录数 */
    int selectCurrentVouchersInfoCount(Map<String, Object> paramMap);

    /** 查询当前还未被领取的代金券信息 */
    List<Map<String, Object>> selectCurrentVouchersInfo(Map<String, Object> paramMap);

    /** 查询过期的或者已被使用的代金券记录数 */
    int selectHistoryVouchersInfoCount(Map<String, Object> paramMap);

    /** 查询过期的或者已被使用的代金券信息 */
    List<Map<String, Object>> selectHistoryVouchersInfo(Map<String, Object> paramMap);

    /** 删除过期代金券信息 */
    int deletePastVouchers(Map<String, Object> paramMap);

    /** 删除已使用代金券信息 */
    int deleteUsedVouchers(Map<String, Object> paramMap);

    /** 查询当前还未被领取的代金券数量 */
    Map<String, BigDecimal> selectSurplusVouchersNum(Map<String, Object> paramMap);

    /** 添加代金券 */
    int insertMerchantVouchersPermissions(Map<String, Object> paramMap);

    /** 添加代金券（多条） */
    int insertMerchantVouchersPermissionsBatch(List<Map<String, Object>> vouchersList);
}
