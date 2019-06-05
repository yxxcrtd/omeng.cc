package com.shanjin.carinsur.service.impl;

import com.alibaba.fastjson.JSON;
import com.shanjin.carinsur.dao.CiOrderMapper;
import com.shanjin.carinsur.dao.MsgComInsurInfoMapper;
import com.shanjin.carinsur.dao.MsgTraInsurInfoMapper;
import com.shanjin.carinsur.model.CiOrder;
import com.shanjin.carinsur.model.MsgComInsurInfo;
import com.shanjin.carinsur.model.MsgTraInsurInfo;
import com.shanjin.carinsur.service.CarInsurPushService;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IActivityPushDao;
import com.shanjin.push.util.PushManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/9
 * @desc 车险推送服务
 */
@Service("carInsurPushService")
public class CarInsurPushServiceImpl implements CarInsurPushService {

    private static final Logger logger = LoggerFactory.getLogger(CarInsurPushServiceImpl.class);

    @Autowired
    private IActivityPushDao pushDao;
    @Autowired
    private MsgTraInsurInfoMapper traInsurInfoMapper;
    @Autowired
    private MsgComInsurInfoMapper comInsurInfoMapper;
    @Autowired
    private CiOrderMapper orderMapper;

    private static final int CLIENT_TYPE_IOS = 2;
    private static final int CLIENT_TYPE_ANDROID = 1;

    private static final int pushType = 24;


    private void pushOrderSuccess(Long userId, BigDecimal totalAmount) {

        try {
            Map<String, Object> userPushMap = pushDao.getUserPushInfo(userId);
            if (null == userPushMap || userPushMap.isEmpty()) {
                logger.info("用户推送信息为空....取消推送,userId={}", userId);
                return;
            }
            String pushId = (String) userPushMap.get("pushId");
            Integer clientType = (Integer) userPushMap.get("clientType");
            if (StringUtil.isEmpty(pushId)) {
                logger.info("用户推送信息pushId为空....取消推送,userPushInfo:", JSON.toJSONString(userPushMap));
                return;
            }
            if (null == clientType) {
                logger.info("用户推送信息clientType为空....取消推送,userPushInfo:", JSON.toJSONString(userPushMap));
                return;
            }
            //推送相关数据封装、
            // 推送信息集合
            List<Map<String, Object>> pushInfoList = new ArrayList<>(1);
            pushInfoList.add(userPushMap);
            //推送参数
            Map<String, Object> configMap = new HashMap<>();
            configMap.put("pushType-" + pushType, "恭喜您，您已成功购买" + totalAmount + "元阳光车险");
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("pushType", 24);

            if (CLIENT_TYPE_ANDROID == clientType) {
                PushManager.androidPush(configMap, pushInfoList, paramMap, "userId");
            } else if (CLIENT_TYPE_IOS == clientType) {
                PushManager.iosPush(configMap, pushInfoList, paramMap);
            } else {
                logger.info("不合法的设备类型，取消推送，userPushInfo：{}", JSON.toJSONString(userPushMap));
            }
        } catch (Exception e) {
            logger.error("推送服务异常...", e);
        }
    }

    @Override
    public void pushOrderSuccess(String orderNo) {
        CiOrder order = orderMapper.getCiOrderByOrderNo(orderNo);
        MsgTraInsurInfo tarTraInsurInfo = traInsurInfoMapper.getEntityByKey(orderNo);
        MsgComInsurInfo comInsurInfo = comInsurInfoMapper.getEntityByKey(orderNo);

        Long userId = order.getUserId();
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (null != tarTraInsurInfo) {
            if (!StringUtils.isEmpty(tarTraInsurInfo.getPremium())) {
                totalAmount = totalAmount.add(new BigDecimal(tarTraInsurInfo.getPremium()));
            }
        }
        if (null != comInsurInfo) {
            if (!StringUtils.isEmpty(comInsurInfo.getPremium())) {
                totalAmount = totalAmount.add(new BigDecimal(comInsurInfo.getPremium()));
            }
        }
        pushOrderSuccess(userId, totalAmount);
    }

}
