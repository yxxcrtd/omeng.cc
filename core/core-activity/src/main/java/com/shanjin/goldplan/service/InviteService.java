package com.shanjin.goldplan.service;

import com.shanjin.goldplan.model.InvitedRecord;
import com.shanjin.util.ActivityException;

import java.util.Map;

import org.springframework.context.ApplicationContextException;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/19
 * @desc 邀请服务
 */
public interface InviteService {

    /**
     * 添加邀请记录
     * @param record
     * @return 添加记录统计
     */
    int addInvitedRecord(InvitedRecord record) throws ActivityException;

    /**
     * 获取邀请人相关信息
     * @param userId
     * @return
     */
    Map<String,String> getInviteUserInfo(Long userId) throws ActivityException;

    /**
     * 查询摸金机会
     * @param userId
     * @return
     */
	Map<String, Object> findTouchGoldChance(Long userId,Long activityId);

	/**
	 * 摸金并返回结果
	 * @param userId
	 * @return
	 */
	Map<String, Object> touchGold(Long userId,Long activityId,int source) throws InterruptedException,ApplicationContextException,Exception ;

}
