package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 订单表Dao
 */
public interface IOrderDao {

	/** 查询新订单数量 */
	int selectNewOrderNum(Map<String, Object> paramMap);

	/** 删除用户设备的记录  Long userId*/
	int deleteUserPushByUserId(Map<String, Object> paramMap);
	
	/** 删除用户设备的记录  String clientId*/
	int deleteUserPushByClientId(Map<String, Object> paramMap);

	/** 保存用户推送设备记录信息 */
	int insertUserPush(Map<String, Object> paramMap);

	/** 查询订单的评价数量，按好中差区分 */
	List<Map<String, String>> selectEvaluationCount(Map<String, Object> paramMap);
}
