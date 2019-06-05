package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 增值服务Dao
 */
public interface IMerchantValueAddServiceDao {

	/** 查询增值服务记录 */
	List<Map<String, Object>> selectMerchantValueAddService(Map<String, Object> paramMap);

	/** 删除抢单余额记录 */
	int delTopupService(Map<String, Object> paramMap);

	/** 删除VIP记录 */
	int delVipService(Map<String, Object> paramMap);

	/** 删除抢增加员工次数记录 */
	int delEmployeesService(Map<String, Object> paramMap);

    /** 修改开通时间 */
    int updateMerchantEmployeesNumApplyOpenTime(Map<String, Object> paramMap);

    /** 查询审核中充值、vip、最大员工数最新记录*/
	Map selectMerchantApplying(Map<String, Object> paramMap);

}
