package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户推送信息表Dao
 */
public interface IMerchantPushDao {

	/** 获取商户的设备ID */
	List<Map<String, Object>> selectMerchantPush(Map<String, Object> paramMap);

	/** 删除商户app的clientId */
	int deleteMerchantPush(Map<String, Object> paramMap);

	/** 根据传递的clientId删除推送信息 */
	int deleteMerchantPushByClientId(Map<String, Object> paramMap);

	/** 根据传递的deviceId删除推送信息 */
	int deleteMerchantPushByDeviceId(Map<String, Object> paramMap);

	/** 保存商户app的clientId */
	int insertMerchantPush(Map<String, Object> paramMap);

	/** 验证设备ID是否登陆 */
	int checkDeviceId(Map<String, Object> paramMap);

	/** 验证商户是否登陆 */
	int checkClient(Map<String, Object> paramMap);

	/** 验证商户是否登陆 */
	int checkClientByUserId(Long userId);
	
	/**验证员工是否在线**/
	int checkEmployeeOnline(Map<String, Object> paramMap);

	/** 检查商户的ClientId是否需要更新 */
	int checkClientId(Map<String, Object> paramMap);

	/** 更改当前使用的设备记录的clientId */
	int updateClientId(Map<String, Object> paramMap);

}
