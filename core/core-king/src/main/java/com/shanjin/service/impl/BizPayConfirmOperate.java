package com.shanjin.service.impl;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/3
 * @desc 抽象业务操作
 */
public abstract class BizPayConfirmOperate<T> {


    /**
     * 获取bizType
     * @return
     */
    protected abstract String[] getBizTypes();

    /**
     * 实际业务操作
     */
    protected abstract void doOperate(T t);


    /**
     * 对外公开接口 添加事务
     * @param bizType
     */
    @Transactional
    public  void operateBiz(String bizType,T t){
        String[] bizTypes = getBizTypes();
        for(String b : bizTypes){
            if(b.equals(bizType)){
                doOperate(t);
                return;
            }
        }
    }

}
