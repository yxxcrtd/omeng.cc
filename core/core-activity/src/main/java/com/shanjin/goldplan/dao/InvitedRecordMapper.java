
package com.shanjin.goldplan.dao;


import java.util.List;
import java.util.Map;

import com.shanjin.common.util.CommonMybExtMapper;
import com.shanjin.goldplan.model.InvitedRecord;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-19 14:17:38 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface InvitedRecordMapper extends CommonMybExtMapper<InvitedRecord, Long>{

    /**
     * 原子操作保存邀请记录
     * @param record
     * @return
     */
    int atomSaveEntity(InvitedRecord record);

	/**
	 * 获取累积摸金次数及金额
	 * @param userId
	 * @return
	 */
	Map<String, Object> getTotalTouchGold(Map<String,Object> param);

	/**
	 * 获取被分享人手机号
	 * @param userId
	 * @return
	 */
	List<Map<String,Long>> getShareUserPhone(Long userId);

	/**
	 * 获取摸金奖励配置
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> findRewardElementConfig(Map<String, Object> param);

	/**
	 * 获取活动下奖励元素
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> findRewardElementLst(Map<String, Object> param);

	/**
	 * 获取奖励元素文案
	 * @param elementMap
	 * @return
	 */
	List<Map<String, Object>> findRewardElementTextLst(Map<String, Object> elementMap);

	/**
	 * 保存抽奖结果
	 * @param paras
	 */
	void saveRewardUserDetail(Map<String, Object> paras);

	/**
	 * 今日是否已摸金
	 * @param userId
	 * @return
	 */
	int getNumberByOrder(Map<String, Object> param);

	/**
	 * 查询防止刷抽奖机会时间
	 * @param param
	 * @return
	 */
	Map<String, Object> getActivityOrderTime(Map<String, Object> param);

}