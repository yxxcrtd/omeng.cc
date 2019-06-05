package com.shanjin.dao;

import java.util.Map;

/**
 * 商户附件表Dao
 */
public interface IMerchantAttachmentDao {

    /** 保存店铺图标（路径） */
    int insertMerchantIcon(Map<String, Object> paramMap);

    /** 查看店铺图标的存储路径 */
    String selectMerchantIcon(Map<String, Object> paramMap);

    /** 查看店铺二维码的存储路径 */
    String selectMerchantQrcode(Map<String, Object> paramMap);

    /** 保存店铺二维码（路径） */
    int insertMerchantQrcode(Map<String, Object> paramMap);

    /** 更新店铺图标（路径） */
    int updateMerchantIcon(Map<String, Object> paramMap);
}
