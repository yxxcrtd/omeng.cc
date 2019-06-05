package com.shanjin.carinsur.service;

import com.shanjin.carinsur.model.InsureBaseInfo;
import com.shanjin.carinsur.model.MsgCallBackRecord;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc 阳光车险 回调 数据处理接口
 */
public interface MsgCallBackService {

    /**
     * 存储消息回调记录
     * @param record 回调消息记录
     */
    MsgCallBackRecord saveMsgCallBackRecord(MsgCallBackRecord record);


    /**
     * 解析存储消息记录
     */
    void parseMsgCallBack(MsgCallBackRecord record);

    /**
     * 查询保单基本信息
     * @param orderNo 订单编号
     * @return
     */
    InsureBaseInfo getBaseInfoByBizNo(String orderNo);
}
