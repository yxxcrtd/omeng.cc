package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户方案附件表Dao
 */
public interface IMerchantPlanAttachmentDao {

    /** 商户抢单后提供的方案中的图片和语音的保存 */
    int insertMerchantPlanAttachment(List<Map<String, Object>> merchantPlanAttachmentList);
    /** 商户抢单后提供的方案中的图片和语音的删除 */
    int deleteMerchantPlanAttachment(Map<String, Object> params);
}
