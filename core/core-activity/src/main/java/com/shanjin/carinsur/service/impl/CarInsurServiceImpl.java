package com.shanjin.carinsur.service.impl;

import com.shanjin.carinsur.dao.CiOrderMapper;
import com.shanjin.carinsur.dao.CiOrderStatusRecordMapper;
import com.shanjin.carinsur.dao.CiVoucherMapper;
import com.shanjin.carinsur.model.CiOrder;
import com.shanjin.carinsur.model.CiOrderStatusRecord;
import com.shanjin.carinsur.model.CiVoucher;
import com.shanjin.carinsur.service.CarInsurPushService;
import com.shanjin.carinsur.service.CarInsurService;
import com.shanjin.carinsur.service.biz.AbsBizDueService;
import com.shanjin.carinsur.util.BizResultEnum;
import com.shanjin.carinsur.util.BizTypeEnum;
import com.shanjin.carinsur.util.OrderStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc TODO
 */
@Service("carInsurService")
public class CarInsurServiceImpl implements CarInsurService, ApplicationContextAware {


    private static final Logger logger = LoggerFactory.getLogger(CarInsurServiceImpl.class);

    @Autowired
    private CiOrderMapper ciOrderMapper;
    @Autowired
    private CiOrderMapper orderMapper;
    @Autowired
    private CiOrderStatusRecordMapper orderStatusRecordMapper;
    @Autowired
    private CiVoucherMapper voucherMapper;

    private Collection<AbsBizDueService> bizDueSvcs;
    @Autowired
    private CarInsurPushService  pushService;


    @Override
    public String createOrder(Long userId, String bizType, String bizNo) {

        BizTypeEnum bizTypeEnum = BizTypeEnum.getBizTypeEnum(bizType);

        if (null == bizTypeEnum) {
            throw new RuntimeException("不合法的业务类型:" + bizType);
        }

        //创建订单前置处理
        for (AbsBizDueService svc : bizDueSvcs) {
            svc.checkPreCreateOrder(bizTypeEnum, bizNo);
        }
        // 订单创建
        CiOrder order = new CiOrder();
        order.setBizType(bizTypeEnum.getValue());
        order.setBizNo(bizNo);
        order.setBizResult(BizResultEnum.NO_DUE.getValue());
        order.setCreateTime(new Date());
        order.setName("车险订单" + bizTypeEnum.getName());
        String rawStr = "" + userId + bizTypeEnum.getValue() + order.getCreateTime().getTime();
        logger.debug("车险订单编号raw串：{}", rawStr);
        order.setOrderNo(DigestUtils.md5DigestAsHex(rawStr.getBytes()));
        order.setStatus(OrderStatusEnum.ORDER_CREATE.getValue());
        order.setUserId(userId);
        //插入订单金额//特殊业务特殊插入
        if (bizTypeEnum.equals(BizTypeEnum.CI_RETURN_VOUCHER) && !StringUtils.isEmpty(bizNo)) {
            //查询车险券金额
            CiVoucher voucher = voucherMapper.getVoucherByVouNo(bizNo);
            if (null != voucher) {
                order.setAmount(voucher.getAmount());
            }
        }
        ciOrderMapper.saveEntity(order);

        return order.getOrderNo();
    }


    @Override
    public void postDueCallBack(String orderNo) {
        //非空校验
        Assert.hasText(orderNo);

        CiOrder order = orderMapper.getCiOrderByOrderNo(orderNo);
        if (null != order) {
            BizTypeEnum bizTypeEnum = BizTypeEnum.getBizTypeEnum(order.getBizType());
            if (null != bizTypeEnum) {
                for (AbsBizDueService svc : bizDueSvcs) {
                    svc.dueBizResult(bizTypeEnum, order.getOrderNo(), order.getId());
                }
            }
            //订单成功状态标识
            CiOrder tmpOrder = new CiOrder();
            tmpOrder.setId(order.getId());
            tmpOrder.setStatus(OrderStatusEnum.ORDER_SUCCESS.getValue());
            tmpOrder.setModifyTime(new Date());
            orderMapper.updateEntity(tmpOrder);
            //订单状态变更记录
            CiOrderStatusRecord record = new CiOrderStatusRecord();
            record.setOrderNo(order.getOrderNo());
            record.setCreateTime(new Date());
            record.setDueTime(tmpOrder.getModifyTime());
            record.setOldValue(order.getStatus());
            record.setNewValue(tmpOrder.getStatus());

            orderStatusRecordMapper.saveEntity(record);

            //推送服务
            pushService.pushOrderSuccess(order.getOrderNo());
        }
    }

    @Override
    public String getOrderByBiz(Long userId, String bizNo, String bizType) {

        List<String> bizResultList = new ArrayList<>(2);
        bizResultList.add(BizResultEnum.NEED_DUE.getValue());
        bizResultList.add(BizResultEnum.SUCCESS_DUE.getValue());

        CiOrder order = new CiOrder();
        order.setBizNo(bizNo);
        order.setBizType(bizType);
        order.setStatus(OrderStatusEnum.ORDER_SUCCESS.getValue());
        order.setBizResultList(bizResultList);
        order.setUserId(userId);

        CiOrder rstOrder = orderMapper.getOrderByBiz(order);
        if (null != rstOrder) {
            return rstOrder.getOrderNo();
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, AbsBizDueService> map = applicationContext.getBeansOfType(AbsBizDueService.class);
        if (null == map || map.isEmpty()) {
            logger.error("初始化车险服务，spring 容器中找不到任何 AbsBizDueService 服务bean...请确认配置是否正确");
            throw new RuntimeException("bizDueSvcs 不能为空...");
        } else {
            logger.info("初始化 抽象业务处理服务bean，分别为:{}", map.keySet());
            bizDueSvcs = map.values();
        }
    }
}
