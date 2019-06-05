package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户员工信息表Dao
 */
public interface IMerchantEmployeesDao {

	/** 验证手机号是否已经存在 */
	Integer checkEmployeesType(Map<String, Object> paramMap);

	/** 根据手机号查询商户Id */
	Long selectMerchantIdByPhone(Map<String, Object> paramMap);

	/** 根据手机号查询商户信息，用于登陆接口 */
	Map<String, Object> selectMerchantInfoForLoginByPhone(Map<String, Object> paramMap);

	/** 根据手机号查询商户开店信息，用于登陆接口 */
	Map<String, Object> selectOpenedInfo(Map<String, Object> paramMap);

	/** 更新验证码,最后登陆时间 */
	int updateVerificationInfo(Map<String, Object> paramMap);

	/** 保存手机号,保存验证码 */
	int insertPhone(Map<String, Object> paramMap);

	/** 获取商户验证码信息 */
	Map<String, Object> validateVerificationCode(Map<String, Object> paramMap);

	/** 验证码验证通过之后更新验证状态 */
	int updateVerificationStatus(Map<String, Object> paramMap);

	/** 根据商户Id删除所有员工（包括老板） */
	int deleteAllEmployee(Map<String, Object> paramMap);

	/** 更新员工的状态 */
	int updateEmployeesStatus(Map<String, Object> paramMap);

	/** 验证员工手机号是否已被当前商户添加 */
	Integer checkEmployeesPhoneInCurrent(Map<String, Object> paramMap);

	/** 验证员工手机号是否已被其他商户添加 */
	int checkEmployeesPhoneInOther(Map<String, Object> paramMap);

	/** 更新验证码（添加员工场合） */
	int updateVerification(Map<String, Object> paramMap);

	/** 查询商户员工记录数 */
	int selectMerchantEmployeesCount(Map<String, Object> paramMap);

	/** 查询商户员工信息 */
	List<Map<String, Object>> selectMerchantEmployeesInfo(Map<String, Object> paramMap);

	/** 删除单个员工 */
	int deleteEmployee(Map<String, Object> paramMap);

	String getEmployeeKey(Map<String, Object> paramMap);

	/**
	 * 根据商户id查询老板的手机号码
	 */
	String getPhoneByMerchantId(Long merchantId);

	/** 更新员工的类型 */
	int updateEmployeeType(Map<String, Object> paramMap);

	/** 获得员工的推送信息 */
	Map<String, Object> getEmployeeClientMap(Map<String, Object> paramMap);
	/*version：1.1.0，date：2016-3-9，author：wangrui*/
	Map<String, Object> getUserInfoByPhone(String phone);
	/*end*/
	

	/** 注册新用户,填写基本手机号和验证码、验证时间 */
	int insertUserInfo(Map<String, Object> paramMap);
	/** 删除用户设备的记录  Long userId*/
	int deleteUserPushByUserId(Map<String, Object> paramMap);
	
	/**根据merchantId查询userId**/
	Long getUserIdByMerchantId(Map<String, Object> paramMap);
	
}
