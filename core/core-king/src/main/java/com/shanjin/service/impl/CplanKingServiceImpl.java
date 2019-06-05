package com.shanjin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.MerchantRelateMapper;
import com.shanjin.dao.UserRelateMapper;
import com.shanjin.model.PersAssInfo;
import com.shanjin.outServices.aliOss.AliOssUtil;
import com.shanjin.service.ICplanKingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/10/28
 * @desc TODO
 */
@Service("cplanKingServiceImpl")
public class CplanKingServiceImpl implements ICplanKingService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserRelateMapper userRelateMapper;
    @Autowired
    private MerchantRelateMapper merchantRelateMapper;

    private final String GET_PERS_ASS_INFO_URL;
    private final String VALIDATE_PERS_ASS_URL;
    private final String BIND_PERS_ASS_URL;
    private final String GET_PERS_ASS_INFO_BY_MERCHANT_ID_URL;
    private final String BIND_PERS_ASS_FOR_MCH_URL;


    private final String queueName = "cplan.orderCustomerQueue";
    private final String exchange = "cplan.orderCustomerExchange";

    private static final Logger logger = LoggerFactory.getLogger(CplanKingServiceImpl.class);

    {
        if (Constant.DEVMODE) {
            GET_PERS_ASS_INFO_URL = "http://192.168.1.130:8800/interface/get-cplan-info-by-userid";
            VALIDATE_PERS_ASS_URL = "http://192.168.1.130:8800/interface/check-service-number";
            BIND_PERS_ASS_URL = "http://192.168.1.130:8800/interface/order-customer";
            GET_PERS_ASS_INFO_BY_MERCHANT_ID_URL = "http://192.168.1.130:8800/interface/get-cplan-info-by-merchantid";
            BIND_PERS_ASS_FOR_MCH_URL = "http://192.168.1.130:8800/interface/bind-cplan-by-invitecode";
        } else {
            GET_PERS_ASS_INFO_URL = "http://100.98.151.8/interface/get-cplan-info-by-userid";
            VALIDATE_PERS_ASS_URL = "http://100.98.151.8/interface/check-service-number";
            BIND_PERS_ASS_URL = "http://100.98.151.8/interface/order-customer";
            GET_PERS_ASS_INFO_BY_MERCHANT_ID_URL = "http://100.98.151.8/interface/get-cplan-info-by-merchantid";
            BIND_PERS_ASS_FOR_MCH_URL = "http://100.98.151.8/interface/bind-cplan-by-invitecode";
        }
    }


    @Override
    public PersAssInfo getPersAssInfoByUserId(Long userId) {
        PersAssInfo persAssInfo = null;
        if (null != userId) {
            Map<String, Long> map = new HashMap<>(1);
            map.put("userId", userId);
            try {
                String rspStr = restTemplate.postForObject(GET_PERS_ASS_INFO_URL, map, String.class);
                JSONObject jsonObject = JSONObject.parseObject(rspStr);
                String retCode = jsonObject.getString("retcode");
                if ("000".equals(retCode)) {
                    persAssInfo = JSON.parseObject(jsonObject.getString("retdata"), PersAssInfo.class);
                } else if ("001".equals(retCode)) {
                    logger.info("找不到该用户关联的私人助理信息");
                } else {
                    String retMsg = jsonObject.getString("retmsg");
                    throw new RuntimeException("接口响应异常，响应码:" + retCode + ",响应消息:" + retMsg);
                }
            } catch (RuntimeException e) {
                logger.error("服务异常", e);
                throw new RuntimeException("c计划服务调用异常", e);
            }

        }
        return persAssInfo;
    }

    @Override
    public PersAssInfo validatePersAssInviteCode(String code) {
        PersAssInfo persAssInfo = null;
        if (null != code) {
            Map<String, String> map = new HashMap<>(1);
            map.put("serviceNumber", code);
            try {
                String rspStr = restTemplate.postForObject(VALIDATE_PERS_ASS_URL, map, String.class);
                JSONObject jsonObject = JSONObject.parseObject(rspStr);
                String retCode = jsonObject.getString("retcode");
                if ("000".equals(retCode)) {
                    persAssInfo = JSON.parseObject(jsonObject.getString("retdata"), PersAssInfo.class);
                } else if ("001".equals(retCode)) {
                    logger.info("验证失败，服务号不正确");
                } else {
                    String retMsg = jsonObject.getString("retmsg");
                    logger.error("接口响应异常，响应码:{},响应消息：{}", retCode, retMsg);
                    throw new RuntimeException("c计划服务调用异常...");
                }
            } catch (RuntimeException e) {
                logger.error("服务异常", e);
                throw new RuntimeException("c计划服务调用异常...", e);
            }
        }
        return persAssInfo;
    }

    /**
     * @param code 邀请码
     * @return
     */
    @Override
    public boolean bindPersAss(String code, Long userId, Date orderTime) {

        if (StringUtil.isEmpty(code) || null == userId || null == orderTime) {
            logger.info("需要的参数存在空值，拒绝绑定操作:code={},userId={},orderTime={}", code, userId, orderTime);
            return false;
        }
        //拼装消息体
        //获取用户信息
        Map<String, Object> userMap = userRelateMapper.getUserInfoById(userId);
        if (null == userMap || userMap.isEmpty()) {
            logger.info("无法获取制定userId信息，userId={}", userId);
            return false;
        }
        String image = (String) userMap.get("image");
        image = StringUtil.isEmpty(image) ? "" : AliOssUtil.getViewUrl(image);
        userMap.put("image", image);
        userMap.put("orderTime", "" + DateUtil.dateToString(orderTime));
        userMap.put("orderType", 1);//线上购买
        userMap.put("serviceType", 0);
        userMap.put("pkgName", "王牌计划");
        userMap.put("serviceNumber", code);
        userMap.put("userId", userId);

        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
            Object value = entry.getValue();
            if (null == value) {
                entry.setValue("");
            }
        }
        try {
            String rspStr = restTemplate.postForObject(BIND_PERS_ASS_URL, userMap, String.class);
            JSONObject jsonObject = JSONObject.parseObject(rspStr);
            String retCode = jsonObject.getString("retcode");
            if ("000".equals(retCode)) {
                logger.info("绑定成功");
                return true;
            } else if ("001".equals(retCode)) {
                logger.info("参数错误");
            } else {
                String retMsg = jsonObject.getString("retmsg");
                logger.error("接口响应异常，响应码:{},响应消息：{}", retCode, retMsg);
            }

        } catch (RuntimeException e) {
            logger.error("服务异常", e);
        }
        return false;

    }

    @Override
    public PersAssInfo queryPersAssInfoByMerchantId(Long merchantId) {
        Map<String,Object> map = new HashMap<>(2);
        map.put("merchantId",merchantId);
        try {
            String rspStr = restTemplate.postForObject(GET_PERS_ASS_INFO_BY_MERCHANT_ID_URL, map, String.class);
            JSONObject jsonObject = JSONObject.parseObject(rspStr);
            String retCode = jsonObject.getString("retcode");
            if("000".equals(retCode)){
                return JSON.parseObject(jsonObject.getString("retdata"),PersAssInfo.class);
            }
            logger.info("查询失败，报文:{}",rspStr);
        } catch (RuntimeException e) {
            logger.error("商户查询私人助理信息异常",e);
        }

        return null;
    }

    @Override
    public PersAssInfo bindPersAssForMch(Long merchantId, String inviteCode) {
        //获取商户信息
        Map<String, Object> detailMerchantInfoMap = merchantRelateMapper.getDetailMerchantInfo(merchantId);
        String image = (String) detailMerchantInfoMap.get("image");
        if (!StringUtil.isEmpty(image)) {
            detailMerchantInfoMap.put("image", AliOssUtil.getViewUrl(image));
        }
        detailMerchantInfoMap.put("inviteCode",inviteCode);
        String authTime = DateUtil.dateToString((Date) detailMerchantInfoMap.get("authTime"));
        detailMerchantInfoMap.put("authTime",authTime);
        try {
            String rspStr = restTemplate.postForObject(BIND_PERS_ASS_FOR_MCH_URL, detailMerchantInfoMap, String.class);
            JSONObject jsonObject = JSONObject.parseObject(rspStr);
            String retCode = jsonObject.getString("retcode");
            if ("000".equals(retCode)) {
                PersAssInfo persAssInfo = JSON.parseObject(jsonObject.getString("retdata"), PersAssInfo.class);
                logger.info("商户绑定私人助理成功");
                logger.info("绑定返回的私人助理信息 persAssInfo:{}", persAssInfo);
                return persAssInfo;
            } else if ("001".equals(retCode)) {
                logger.info("商户绑定私人助理失败");
            } else {
                String retMsg = jsonObject.getString("retmsg");
                logger.error("接口响应异常，响应码:{},响应消息：{}", retCode, retMsg);
            }
        } catch (RuntimeException e) {
            logger.error("服务异常", e);
        }
        return null;
    }


}
