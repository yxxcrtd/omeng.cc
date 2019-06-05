package com.shanjin.service.impl;

import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.KingRewardMapper;
import com.shanjin.dao.MerchantQrCodeMapper;
import com.shanjin.dao.MerchantRelateMapper;
import com.shanjin.model.PersAssInfo;
import com.shanjin.outServices.aliOss.AliOssUtil;
import com.shanjin.service.ICplanKingService;
import com.shanjin.service.IUnitedMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc 联合营销接口
 */
@Service("unitedMarketingServiceImpl")
public class UnitedMarketingServiceImpl implements IUnitedMarketingService {


    @Autowired
    private MerchantQrCodeMapper merchantQrCodeMapper;
    @Autowired
    private MerchantRelateMapper merchantRelateMapper;
    @Autowired
    private KingRewardMapper kingRewardMapper;
    @Autowired
    private ICplanKingService cplanKingService;


    @Override
    public Map<String, Object> getUnitedMarketingInfo(Long merchantId) {
        System.out.println("sss");
        Map<String, Object> map = new HashMap<>();
        String qrPath = merchantQrCodeMapper.queryPathBymerchatnId(merchantId);
        //是否加入标识
        boolean isJoin = StringUtil.isEmpty(qrPath) ? false : true;
        if (isJoin) {
            //插入二维码图片路径
            map.put("qrPath", AliOssUtil.getViewUrl(qrPath));
            //查询私人助理信息
            PersAssInfo persAssInfo = queryPersInfoForMerchant(merchantId);
            map.put("persInfo", persAssInfo);
            //商户信息
            Map<String, Object> merchantInfoMap = merchantRelateMapper.getBasicMerchantInfo(merchantId);
            if (null != merchantInfoMap && !merchantInfoMap.isEmpty()) {
                String path = (String) merchantInfoMap.get("photoPath");
                if (!StringUtil.isEmpty(path)) {
                    merchantInfoMap.put("photoPath", AliOssUtil.getViewUrl(path));
                }
            }
            map.put("merchantInfo", merchantInfoMap);
            //收益分成统计
            BigDecimal totalAmount = kingRewardMapper.getMerhcantTotalRewardAmount(merchantId);
            map.put("totalAmount", totalAmount);
        }
        return map;
    }

    @Override
    public PersAssInfo queryPersInfoForMerchant(Long merchantId) {

        return cplanKingService.queryPersAssInfoByMerchantId(merchantId);
    }

    @Override
    public PersAssInfo bindMerchantPersAss(Long merchantId, String inviteCode) {
        return cplanKingService.bindPersAssForMch(merchantId, inviteCode);
    }


    @Deprecated
    @Override
    public Map<String, Object> joinUmPlan(String merchantId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> findBainList(Long merchantId, int pageIndex, int pageSize) {
        return kingRewardMapper.findRewardItemList(merchantId, pageIndex, pageSize);
    }
}
