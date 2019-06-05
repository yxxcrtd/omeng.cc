package com.shanjin.carinsur.service.biz;

import com.shanjin.carinsur.model.CiOrder;
import com.shanjin.carinsur.util.BizTypeEnum;
import org.springframework.stereotype.Service;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/2
 * @desc TODO
 */
@Service("noBizDueService")
public class NoBizDueService extends AbsBizDueService{

    public NoBizDueService() {
        super(BizTypeEnum.NO_BIZ);
    }

    @Override
    protected boolean isSupportBizRule(String orderNo) {
        return false;
    }

    @Override
    protected boolean dueBiz(CiOrder oldOrder) {
        //不做任何业务处理，默认返回时失败
        return false;
    }
}
