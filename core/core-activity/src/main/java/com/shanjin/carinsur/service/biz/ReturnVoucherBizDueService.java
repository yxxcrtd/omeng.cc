package com.shanjin.carinsur.service.biz;

import com.shanjin.carinsur.dao.CiUserVoucherMapper;
import com.shanjin.carinsur.dao.MsgComInsurInfoMapper;
import com.shanjin.carinsur.model.CiOrder;
import com.shanjin.carinsur.model.CiUserVoucher;
import com.shanjin.carinsur.model.MsgComInsurInfo;
import com.shanjin.carinsur.util.BizTypeEnum;
import com.shanjin.carinsur.util.DateUtil;
import com.shanjin.carinsur.util.UserVouStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc f返现券业务处理类
 */
@Service("returnVoucherBizDueService")
public class ReturnVoucherBizDueService extends AbsBizDueService {


    @Autowired
    private CiUserVoucherMapper userVoucherMapper;
    @Autowired
    private MsgComInsurInfoMapper comInsurInfoMapper;


    public ReturnVoucherBizDueService() {
        super(BizTypeEnum.CI_RETURN_VOUCHER);
    }

    /**
     * 检查该车险券是否符合业务规则
     *
     * @param orderNo
     * @return
     */
    @Override
    protected boolean isSupportBizRule(String orderNo) {
        MsgComInsurInfo insurInfo = comInsurInfoMapper.getEntityByKey(orderNo);
        return null != insurInfo;
    }

    @Override
    protected boolean dueBiz(CiOrder oldOrder) {
        String vouNo = oldOrder.getBizNo();
        int count = userVoucherMapper.updateVoucherStatus(vouNo,
                UserVouStatusEnum.HAS_USED.getValue());
        logger.info("更新车险券状态数量：{}", count);
        return count > 0;
    }

    @Override
    protected void createOrderHook(String bizNo) {

        CiUserVoucher userVoucher = userVoucherMapper.getUserVoucherByVouNo(bizNo);
        if (null != userVoucher) {
            Date end = userVoucher.getEndTime();
            if(UserVouStatusEnum.NOT_USED.getValue() != userVoucher.getStatus()){
                throw new RuntimeException("所选车险券已使用");
            }
            if (end.getTime() < DateUtil.date2YmdDate(new Date()).getTime()) {
                throw new RuntimeException("所选车险券已过期");
            }
            return;
        }
        throw new RuntimeException("找不到指定券编号:" + bizNo);
    }
}
