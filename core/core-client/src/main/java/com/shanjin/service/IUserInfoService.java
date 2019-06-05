package com.shanjin.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 用户 业务接口
 * 
 * @author 李焕民
 * @version 2015-3-26
 *
 */
public interface IUserInfoService {
	
	/** 获取验证码 */
	public JSONObject getVerificationCode(String phone, String clientId,String device,String ip) throws Exception;

	/** 获取语音验证码 */
	public JSONObject getVoiceVerificationCode(String phone, String clientId,String device,String ip) throws Exception;

	/** 验证验证码 */
	public JSONObject validateVerificationCode(String phone, String verificationCode, String clientId, String clientType, String pushId, String ip,String phoneModel) throws Exception;

	/** 更改用户信息 */
	public boolean updateUserInfo(Long userId, int sex, String filePath) throws Exception;

	/**
	 * 用户验证成功之后登陆
	 * 
	 * @throws Exception
	 */
	public String userInfoLogin(String phone, String clientId) throws Exception;

	/** 用户端退出应用 */
	public JSONObject cleanUserInfoPush(Long userId,String deviceId);

	/** 根据手机号查询用户信息 */
	// public Map<String, Object> getUserInfoByPhone(String phone);

	/** 根据手机号查询用户信息 */
	public Map<String, String> getUserInfoByPhoneWithStr(String phone) throws Exception;

	/** 上传用户头像 */
	public boolean uploadUserPortrait(Long userId, String filePath) throws Exception;

	/** 获取用户地址信息 */
	public List<Map<String, Object>> getUserAddressInfo(Long userId, String addressType) throws Exception;

	/** 更新用户地址信息 */
	public boolean updateUserAddressInfo(Long userId, String addressType, Double latitude, Double longitude, String addressInfo) throws Exception;

	/** 获取用户可以使用的代金券的个数 */
	public int getUserAvailableVouchersCount(Long userId) throws Exception;

	/** 获取用户可以使用的代金券列表 */
	public List<Map<String, Object>> getUserAvailableVouchersInfo(Long userId, int pageNo) throws Exception;

	/** 获取用户历史的代金券的个数 */
	public int getUserHistoryVouchersCount(Long userId) throws Exception;

	/** 获取用户历史的代金券列表 */
	public List<Map<String, Object>> getUserHistoryVouchersInfo(Long userId, int pageNo) throws Exception;

	/** 删除代金券 */
	public JSONObject deleteVouchersInfo(Long vouchersId, Long userId) throws Exception;

	/** 获取用户专有密钥 */
	String getUserKey(String phone, String clientId);

	/** 验证用户是否登陆 */
	public JSONObject checkClient(String clientId, Long userId) throws Exception;

	/** 更改当前使用的设备记录的clientId */
	public JSONObject updateClientId(String pushId, Long userId, String clientType, String clientId,String phoneModel) throws Exception;
	
	
	
	/**
	 * 个人用户， IP 解析成 城市   KFA消息处理 调用此接口
	 * @param ip		个人用户发请求时的IP 地址
	 * @param userId	个人用户ID
	 */
	public boolean updateUserLocation(String ip,String userId);
	
	
	/**
	 * 个人用户  验证码验证成功后，修改推送信息。 KFKA消息处理调用此接口
	 * @param clientId
	 * @param deviceId
	 * @param userId
	 * @param clientType
	 * @return
	 */
	public boolean updateUserPushInfo(String clientId,String deviceId,String userId,String clientType);
	
	/**
	 * 获取融云token信息
	 * 
	 * @param phone
     *
	 * */
	public String getRongCloudToken(String phone) throws Exception;

}
