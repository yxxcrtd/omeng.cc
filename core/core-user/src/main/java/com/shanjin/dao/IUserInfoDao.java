package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 用户 业务DAO
 * 
 * @author 李焕民
 * @version 2015-3-26
 *
 */
public interface IUserInfoDao {
	/** 验证该手机号是否已经存在 */
	int checkUserIsEmpty(Map<String, Object> paramMap);

	/** 注册新用户,填写基本手机号和验证码、验证时间 */
	int insertUserInfoWithVerification(Map<String, Object> paramMap);

//	/** 获取用户的设备ID */
//	List<Map<String, Object>> selectUserInfoPush(Map<String, Object> paramMap);
//
//	/** 设置推送信息前，先清空这个设备ID的记录 */
//	int cleanUserInfoPush(Map<String, Object> paramMap);
//
//	/** 用户退出清空这个设备ID的记录 */
//	int cleanUserInfoPushByClientId(Map<String, Object> paramMap);
//
//	/** 用户退出清空这个设备ID的记录 */
//	int cleanUserInfoPushByDeviceId(Map<String, Object> paramMap);
//
//	/** 更改用户推送信息 */
//	int initHaveUserInfoPush(Map<String, Object> paramMap);
//
//	/** 注册成功之后初始化用户推送信息 */
//	int initUserInfoPushDirect(Map<String, Object> paramMap);

	/** 初始化用户头像 */
	int initUserInfoPortraitDirect(Map<String, Object> paramMap);

	/** 更改改手机号对应的验证码和验证时间 */
	int updateUserInfoVerification(Map<String, Object> paramMap);

	/** 更改用户信息 */
	int updateUserInfo(Map<String, Object> paramMap);

	/** 更改用户 */
	int updateUserInfoPortrait(Map<String, Object> paramMap);

	/** 用户验证成功之后登陆 */
	int userInfoLogin(Map<String, Object> paramMap);

	/** 更改用户最后登录时间信息 */
	int updateUserLastLoginTime(Map<String, Object> paramMap);

	/** 获取用户验证码信息 */
	Map<String, Object> validateVerificationCode(Map<String, Object> paramMap);

	/** 根据手机号查询用户信息 */
	String getUserIdByPhone(Map<String, Object> paramMap);

	/** 根据手机号查询用户信息 */
	Map<String, Object> getUserInfoByPhone(Map<String, Object> paramMap);

	/** 根据手机号查询用户信息 */
	Map<String, String> getUserInfoByUserId(Map<String, Object> paramMap);

	/** 上传用户头像 */
	int uploadUserPortrait(Map<String, Object> paramMap);

	/** 获取用户地址信息 */
	List<Map<String, Object>> getUserAddressInfo(Map<String, Object> paramMap);

	/** 插入用户地址信息 */
	int insertUserAddressInfo(Map<String, Object> paramMap);

	/** 更新用户地址信息 */
	int updateUserAddressInfo(Map<String, Object> paramMap);

	/** 判断所需要插入的地址记录是否存在 */
	int checkUserAddressInfo(Map<String, Object> paramMap);

	/** 判断用户头像是否存在 */
	int checkUserPortrait(Map<String, Object> paramMap);

	/** 获取用户可以使用的代金券的个数 */
	int getUserAvailableVouchersCount(Map<String, Object> paramMap);

	/** 获取用户可以使用的代金券列表 */
	List<Map<String, Object>> getUserAvailableVouchersInfo(Map<String, Object> paramMap);

	/** 获取用户历史的代金券的个数 */
	int getUserHistoryVouchersCount(Map<String, Object> paramMap);

	/** 获取用户历史的代金券列表 */
	List<Map<String, Object>> getUserHistoryVouchersInfo(Map<String, Object> paramMap);

	/** 获取轮播图列表 */
	List<Map<String, Object>> getSliderPics(Map<String, Object> paramMap);

	/** 删除代金券 */
	int deleteVouchersInfo(Map<String, Object> paramMap);

	/** 获取用户密钥 */
	String getUserKey(Map<String, Object> paramMap);

	/** 验证设备用户是否登陆 */
	int checkClient(Map<String, Object> paramMap);

	/** 检查用户的ClientId是否需要更新 */
	int checkClientId(Map<String, Object> paramMap);

	/** 更改当前使用的设备记录的clientId */
	int updateClientId(Map<String, Object> paramMap);
	
	/** 更新用户访问系统时，IP归属的城市、省份信息 **/
	int updateUserLocation(Map<String,String> paramMap);
	
	/** 根据用户ID获取用户推送设备列表 Long userId*/
	List<Map<String, Object>> selectUserPushList(Map<String, Object> paramMap);
	
	/** 根据订单的appType和serviceType,找出能提供服务的商户所有接单用户的设备列表 String appType,String serviceType */
	List<Map<String, Object>> selectAcceptUserPushList(Map<String, Object> paramMap);
	
	/** 删除用户设备的记录  Long userId*/
	int deleteUserPushByUserId(Map<String, Object> paramMap);
	
	/** 删除用户设备的记录  String clientId*/
	int deleteUserPushByClientId(Map<String, Object> paramMap);

	/** 保存用户推送设备记录信息 */
	int insertUserPush(Map<String, Object> paramMap);
	
	/** 更新融云token */
	int updateRongCloudToken(Map<String, Object> paramMap);
	
	/** 我的店铺列表 */
	List<Map<String, Object>> myMerchantList(Map<String, Object> paramMap);
	
	//List<Long> getmerchantFromUser(String userId);
	
	int updLastActiveTime(Map<String, Object> paramMap);
	
	/**统计用户今日银行卡银联认证次数**/
	int countTodayComVerifyBg(Long userId);
	
	/** 查询本地银联认证信息**/
	Map<String,Object> selectUserVeifyBgInfo(Map<String,Object> params);
	
	/** 保存用户调用银联实名认证接口 请求信息 **/
	int insertConVerifyBgInfo(Map<String,Object> paramsMap);
	
	/** 统计用户状态是否合法**/
	int countAvaliablByUserId(Long userId);
}