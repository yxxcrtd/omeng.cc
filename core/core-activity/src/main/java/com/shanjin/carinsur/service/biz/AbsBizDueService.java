package com.shanjin.carinsur.service.biz;

import com.shanjin.carinsur.dao.CiOrderBizResultRecordMapper;
import com.shanjin.carinsur.dao.CiOrderMapper;
import com.shanjin.carinsur.model.CiOrder;
import com.shanjin.carinsur.model.CiOrderBizResultRecord;
import com.shanjin.carinsur.util.BizResultEnum;
import com.shanjin.carinsur.util.BizTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Date;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc 抽象业务服务处理
 */
public abstract class AbsBizDueService implements ApplicationContextAware {

    private BizTypeEnum bizTypeEnum;

    private CiOrderMapper ciOrderMapper;

    private CiOrderBizResultRecordMapper bizResultRecordMapper;

    protected static final Logger logger = LoggerFactory.getLogger(AbsBizDueService.class);

    public AbsBizDueService(BizTypeEnum bizTypeEnum){
        this.bizTypeEnum = bizTypeEnum;
    }

    /**
     * 是否 符合业务处理规则，解耦业务处理和订单的业务流程
     * @return true： 后续操作讲业务处理结果bizResult 变更从 未处理00-->待处理状态01
     *          false: 后续不做业务相关处理
     */
    protected abstract boolean isSupportBizRule(String orderNo);

    /**
     * 如果 #if{isSupportBizRule} 返回true ，标识业务处理中实际操作处理成功
     */
    protected abstract boolean dueBiz(CiOrder oldOrder);

    /**
     * 处理业务结果操作
     * @param bizTypeEnum
     * @param orderNo
     * @param orderId
     */
    public void dueBizResult(BizTypeEnum bizTypeEnum,String orderNo,Long orderId){
        if(this.bizTypeEnum.equals(bizTypeEnum)){
            boolean flag = isSupportBizRule(orderNo);
            CiOrder ciOrder = new CiOrder();
            if(flag){

                CiOrder oldOrder = ciOrderMapper.getEntityByKey(orderId);
                //实际业务处理
                boolean hasBizdone = dueBiz(oldOrder);
                if(hasBizdone){
                    //更新业务处理结果为待处理
                    ciOrder.setId(orderId);
                    ciOrder.setBizResult(BizResultEnum.NEED_DUE.getValue());
                    ciOrder.setModifyTime(new Date());
                    ciOrderMapper.updateEntity(ciOrder);
                    //记录业务结果变更记录
                    CiOrderBizResultRecord record = new CiOrderBizResultRecord();
                    record.setOrderNo(orderNo);
                    record.setCreateTime(new Date());
                    record.setCreator("sys");
                    record.setDueTime(ciOrder.getModifyTime());
                    record.setOldValue(oldOrder.getBizResult());
                    record.setNewValue(ciOrder.getBizResult());
                    bizResultRecordMapper.saveEntity(record);
                }
                logger.info("订单编号：{}，id:{},订单符合业务待处理规则",orderNo,orderId);
            }
        }
    }

    /**
     * 创建订单前置 检查
     */
    public void checkPreCreateOrder(BizTypeEnum bizTypeEnum,String bizNo){
        if(this.bizTypeEnum.equals(bizTypeEnum)){
            createOrderHook(bizNo);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ciOrderMapper = applicationContext.getBean(CiOrderMapper.class);
        bizResultRecordMapper = applicationContext.getBean(CiOrderBizResultRecordMapper.class);
    }

    /**
     * 创建订单钩处理
     * @param bizNo
     */
    protected void createOrderHook(String bizNo){}
}
