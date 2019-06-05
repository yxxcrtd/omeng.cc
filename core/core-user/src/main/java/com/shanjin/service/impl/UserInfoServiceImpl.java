package com.shanjin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IIpCityCacheService;
import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.constant.TopicConstants;
import com.shanjin.common.util.*;
import com.shanjin.dao.IAndroidAppPushConfigDao;
import com.shanjin.dao.IUserInfoDao;
import com.shanjin.exception.ApplicationException;
import com.shanjin.kafka.ProducerFactory;
import com.shanjin.kafka.ProducerInterface;
import com.shanjin.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 业务接口实现类
 * 
 * @author 李焕民
 * @version 2015-3-26
 *
 */
@Service("userService")
public class UserInfoServiceImpl implements IUserInfoService {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(UserInfoServiceImpl.class);
	
	
	private static  ProducerInterface vitalProducer;
	
    @Resource
    private ICommonService commonService;
	
	static {
		
		if (Constant.KAFKA_LOG)
				vitalProducer =ProducerFactory.getProducer(true, BusinessUtil.getIpAddress());
	}

	@Resource
	private IUserInfoDao userDao;

	@Resource
	private IValidateService validateService;

	@Resource
	private ICommonCacheService commonCacheService;

	@Resource
	private IUserRelatedCacheServices userRelatedCacheServices;

	@Resource
	private IIpCityCacheService ipCityCacheServices;
	
	@Resource
	private IAndroidAppPushConfigDao androidAppPushConfigDao;
	
	@Resource
	private IDictionaryService dictionaryService;
	
	@Resource
	private IPushService pushService;

	@Resource
	private IKingService kingService;

	/** 获取验证码 */
	@Override
	public JSONObject getVerificationCode(String phone, String clientId,String device,String ip) throws Exception {
		JSONObject jsonObject = null;

		String verificationCode = BusinessUtil.createVerificationCode(phone);

		//验证码限制
		
		String result="0";
		//手机号限制
		if(StringUtil.isNotEmpty(phone)){
			result=verificationCodeRestrict(phone, verificationCode,  "verificationCode_phone_restrict",CacheConstants.VERIFICATIONCODE_OUTTIME_SMS,CacheConstants.VERIFICATIONCODE_LIST_PHONE);

			if ("-1".equals(result)){
				return new ResultJSONObject("001", "1分钟内不能重复获取验证码");
			}
			
			if(!"0".equals(result)){
				return new ResultJSONObject("003", "验证码获取频繁，请于"+result+"秒后重新获取验证码");	
			}
		}
		//设备号限制
		if(StringUtil.isNotEmpty(device)){
			result=verificationCodeRestrict(device, verificationCode,  "verificationCode_device_restrict",CacheConstants.VERIFICATIONCODE_OUTTIME_SMS,CacheConstants.VERIFICATIONCODE_LIST_DEVICE);
			if ("-1".equals(result)){
				return new ResultJSONObject("001", "1分钟内不能重复获取验证码");
			}
			if(!"0".equals(result)){
				return new ResultJSONObject("004", "验证码获取频繁，请于"+result+"秒后重新获取验证码");	
			}
		}
		//IP地址限制
		if(StringUtil.isNotEmpty(ip)){
			result=verificationCodeRestrict(ip, verificationCode,  "verificationCode_ip_restrict","",CacheConstants.VERIFICATIONCODE_LIST_IP);
			if(!"0".equals(result)){
				return new ResultJSONObject("005", "验证码获取频繁，请于"+result+"秒后重新获取验证码");	
			}
		}
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("phone", phone);
		paramMap.put("verificationCode", verificationCode);
		paramMap.put("clientId", clientId);		
		userRelatedCacheServices.cacheVerifyInfo(phone, paramMap);
		
		// 短信发送验证码
		jsonObject = BusinessUtil.sendVerificationCode(phone, verificationCode, 1);
		return jsonObject;
	}

	/** 获取语音验证码 */
	@Override
	public JSONObject getVoiceVerificationCode(String phone, String clientId,String device,String ip) throws Exception {
		JSONObject jsonObject = null;
		String verificationCode = BusinessUtil.createVerificationCode(phone);
		
		//验证码限制
		String result="0";
		//手机号限制
		if(StringUtil.isNotEmpty(phone)){
			result=verificationCodeRestrict(phone, verificationCode, "verificationCode_phone_restrict",CacheConstants.VERIFICATIONCODE_OUTTIME_VOICE,CacheConstants.VERIFICATIONCODE_LIST_PHONE);
			if ("-1".equals(result)){
				return new ResultJSONObject("001", "1分钟内不能重复获取验证码");
			}
			if(!"0".equals(result)){
				return new ResultJSONObject("003", "当前手机号验证码获取频繁，请于"+result+"秒后重新获取验证码");	
			}
		}
		//设备号限制
		if(StringUtil.isNotEmpty(device)){
			result=verificationCodeRestrict(device, verificationCode, "verificationCode_device_restrict",CacheConstants.VERIFICATIONCODE_OUTTIME_VOICE,CacheConstants.VERIFICATIONCODE_LIST_DEVICE);
			if ("-1".equals(result)){
				return new ResultJSONObject("001", "1分钟内不能重复获取验证码");
			}
			if(!"0".equals(result)){
				return new ResultJSONObject("004", "当前设备号验证码获取频繁，请于"+result+"秒后重新获取验证码");	
			}
		}
		//IP地址限制
		if(StringUtil.isNotEmpty(ip)){
			result=verificationCodeRestrict(ip, verificationCode, "verificationCode_ip_restrict","",CacheConstants.VERIFICATIONCODE_LIST_IP);
			if(!"0".equals(result)){
				return new ResultJSONObject("005", "当前IP地址验证码获取频繁，请于"+result+"秒后重新获取验证码");	
			}
		}
				
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("phone", phone);
		paramMap.put("verificationCode", verificationCode);
		paramMap.put("clientId", clientId);
		userRelatedCacheServices.cacheVerifyInfo(phone, paramMap);	
		
		// 短信发送验证码
		jsonObject = BusinessUtil.sendVerificationCode(phone, verificationCode, 2);
		return jsonObject;
	}
	
	
	/** 验证验证码 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject validateVerificationCode(String phone, String verificationCode, String clientId, String clientType, String pushId, String ip,String phoneModel) throws Exception {
		JSONObject jsonObject = null;
		if (pushId == null || "".equals(pushId)) {
			pushId = clientId;
		}

		Map<String, String> cachedInfo = userRelatedCacheServices.getVerifyInfo(phone);

		if (cachedInfo == null || cachedInfo.size() < 1 ) {
			BusinessUtil.writeLog("interface",phone+"：验证码缓存为空："+cachedInfo.toString());
			return new ResultJSONObject("002", "请填写正确的验证码");
		}
		
		if ( !cachedInfo.get("verificationCode").equals(verificationCode)) {
			BusinessUtil.writeLog("interface",phone+"：缓存验证码："+cachedInfo.get("verificationCode")+"\n"+"参数验证码："+verificationCode);			
			return new ResultJSONObject("002", "请填写正确的验证码");
		}
		
		//删除验证码缓存
		commonCacheService.deleteObject(CacheConstants.USER_PRE_VERIFY_KEY+phone);
		
		Map<String, String> userInfo = null;
		Map<String, Object> user = null;
		final Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
        String userId = "";  // 用户ID
		String portraitPath = ""; // 用户头像
		//String rongCloudToken = ""; // 融云token
        
		userId = userRelatedCacheServices.getUserIdByPhone(phone);  // 缓存中查询
		if(!StringUtil.isNullStr(userId)){
			userInfo = userRelatedCacheServices.getCachedUseInfo(userId);	
		}

		jsonObject = new ResultJSONObject("000", "验证码验证通过,成功登陆");
		List<Map<String, Object>> myMerchantList=new ArrayList<Map<String,Object>>();
        if(userInfo==null||userInfo.isEmpty()){
        	// 用户信息没有缓存
        	user = this.userDao.getUserInfoByPhone(paramMap);
    		if(user!=null&&user.get("userId")!=null){
    			// 用户已经存在，登陆处理
        		userInfo = StringUtil.convertMap(user);
    			userId = StringUtil.null2Str(userInfo.get("userId"));
    			userInfo.put("id", userId);
    			portraitPath = StringUtil.null2Str(userInfo.get("portraitPath")); 
    			//rongCloudToken = StringUtil.null2Str(userInfo.get("rongCloudToken")); 
    			if(StringUtil.isNullStr(portraitPath)){
    				// 用户未上传头像，使用默认头像
    				portraitPath = Constant.DEFAULT_USER_PORTRAIT_PTAH;
    			}
    			portraitPath = BusinessUtil.disposeImagePath(portraitPath);
    			userInfo.put("portraitPath", portraitPath);  
    			
    			//登陆后返回店铺列表信息
    			myMerchantList=myMerchantList(StringUtil.nullToLong(userId));
    		}else{
    			String isRegister=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.IS_REGISTER, userId));
    			if(isRegister.equals("1")){
    				return new ResultJSONObject("001", "正在注册中，请稍后再试！");
    			}
    			String userKey=DynamicKeyGenerator.generateDynamicKey();
    			// 用户不存在，注册处理
    			user = new HashMap<String, Object>();
    			userId = StringUtil.null2Str(IdGenerator.generateID(18));
    			commonCacheService.setObject("1", 120, CacheConstants.IS_REGISTER, userId);
    			
    			user.put("id", userId);
    			user.put("userId", userId);
    			user.put("userKey", userKey);
    			user.put("phone", phone);
    			user.put("verificationCode", verificationCode);
    			user.put("clientId", clientId);
    			user.put("ip", ip);
    			user.put("name", "");
    			user.put("userType", "0");
    			user.put("province", Constant.EMPTY);
    			user.put("city", Constant.EMPTY);
    			if (ip != null && !Constant.DEVMODE) {
    				if (Constant.ASYNC_PROCESS){
    					//异步消息进行 IP-位置解析        Revoke 2015.12.9 日
    					vitalProducer.sendMsg(TopicConstants.IP_CITY_RESOLVE,BusinessUtil.getLogId(),JSONObject.toJSONString(user));
    				}else{
    					Map<String,String> locationInfo=resolveCityByIp(ip);
    					if (locationInfo.size()>0){
    						user.put("province", locationInfo.get("province"));
    						user.put("city", locationInfo.get("city"));
    					}
    				}
    			} 
    			

    			if(StringUtil.isNullStr(portraitPath)){
    				// 用户未上传头像，使用默认头像
    				portraitPath = Constant.DEFAULT_USER_PORTRAIT_PTAH;
    			}
    			portraitPath = BusinessUtil.disposeImagePath(portraitPath);

//    			rongCloudToken = RongCloudUtil.getRongCloudTokenFromProxy(userId, "", portraitPath); // 用户
//    			if (StringUtil.isNullStr(rongCloudToken)) {
//    				// 融云服务器响应异常，返回提示信息
//    				rongCloudToken = "";
//    			} else {
//    				if ("proxyerror".equals(rongCloudToken)) {
//    					rongCloudToken = "";
//    				} 
//    			}
//    			user.put("rongCloudToken", rongCloudToken);
    			this.userDao.insertUserInfoWithVerification(user);

    			userInfo = StringUtil.convertMap(user);
    	        userInfo.put("portraitPath", portraitPath);
    	        
    	        //将userKey放入缓存
    	        commonCacheService.setObject(userKey, "employeeKey", phone);
    		}


        }else{ 				
			//登陆后返回店铺列表信息
			myMerchantList=myMerchantList(StringUtil.nullToLong(userId));
        }

		jsonObject.put("myMerchantList",myMerchantList);
		//修改店铺最后活跃最后登录时间
//      List<Long> merchantIdList=userDao.getmerchantFromUser(userId);//查询此用户是否有店铺
        if(myMerchantList !=null && myMerchantList.size()>0){
        	String merchantIds="";
        	for(Map<String, Object> map : myMerchantList){
        		merchantIds+=StringUtil.null2Str(map.get("merchantId"))+",";
        	}
        	if(!merchantIds.equals("")){
        		merchantIds=merchantIds.substring(0,merchantIds.length()-1);
        	}
        	paramMap.put("merchantIds", merchantIds);
        	userDao.updLastActiveTime(paramMap);//修改此用户所属店铺的活跃时间
        }
		
		String encryptedKey = null;
		encryptedKey = this.userInfoLogin(phone, clientId);
		userInfo.put("encryptedKey", encryptedKey);

		//20160921 ADD for wx login encrypt --tj
		String dynamicKey4WX = DynamicKeyGenerator.generateDynamicKey();
		String encryptedKey4Wx = this.userInfoLogin4WX(phone, clientId, dynamicKey4WX);
		userInfo.put("dynamicKey4WX", dynamicKey4WX);
		userInfo.put("encryptedKey4WX", encryptedKey4Wx);
		//王牌计划会员标识 tj add 20161031
		userInfo.putAll(kingService.getKingLoginStatus(Long.valueOf(userId)));

		jsonObject.put("userInfo", userInfo);
		
//		if(!StringUtil.isNullStr(rongCloudToken)){
//			// 融云token获取正常，将用户信息存入缓存；否则不存入缓存
//			userRelatedCacheServices.cacheUserInfo(userInfo);
//		}
		userRelatedCacheServices.cacheUserInfo(userInfo);
		jsonObject.put("userInfo", userInfo);
		
		//重复登录推送
		Map<String,Object> pushMap=new HashMap<String, Object>();
		pushMap.put("clientId", clientId);
		pushMap.put("clientType", clientType);
		pushMap.put("userId", userInfo.get("userId"));
		pushMap.put("pushId", pushId);	
		pushMap.put("phoneModel", phoneModel);	
		pushMap.put("pushType", 8);
		pushService.basicPush(pushMap);
		return jsonObject;
	}


//	/** 验证验证码 */
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public JSONObject validateVerificationCode(String phone, String verificationCode, String clientId, String clientType, String pushId, String ip) throws Exception {
//		JSONObject jsonObject = null;
//		if (pushId == null || "".equals(pushId)) {
//			pushId = clientId;
//		}
//
//		Map<String, String> cachedInfo = userRelatedCacheServices.getVerifyInfo(phone);
//
//		if (cachedInfo == null || cachedInfo.size() < 1 || !cachedInfo.get("verificationCode").equals(verificationCode)) {
//			// 缓存过期
//			jsonObject = new ResultJSONObject("002", "错误的验证码");
//			return jsonObject;
//		}
//
//		Map<String, String> userInfo = null;
//
//		String userId = userRelatedCacheServices.getUserIdByPhone(phone);
//
//		if (userId == null || userId.equals("nil")) {
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap.put("phone", phone);
//			userId = this.userDao.getUserIdByPhone(paramMap);  // 数据库中查询用户是否存在
//			if (userId == null || userId.equals("")) {
//				// 创建新用户及相关信息，并放入缓存。
//				userInfo = new HashMap<String, String>();
//				userInfo.put("id", "" + IdGenerator.generateID(18));
//				userInfo.put("userId", userInfo.get("id"));
//				userInfo.put("userKey", DynamicKeyGenerator.generateDynamicKey());
//				userInfo.put("phone", phone);
//				userInfo.put("verificationCode", verificationCode);
//				//userInfo.put("appType", appType);
//				userInfo.put("clientId", clientId);
//
//				userInfo.put("ip", ip);
//				userInfo.put("province", Constant.EMPTY);
//				userInfo.put("city", Constant.EMPTY);
//				
//				
//				if (ip != null && !Constant.DEVMODE) {
//					if (Constant.ASYNC_PROCESS){
//						//异步消息进行 IP-位置解析        Revoke 2015.12.9 日
//						vitalProducer.sendMsg(TopicConstants.IP_CITY_RESOLVE,BusinessUtil.getLogId(),JSONObject.toJSONString(userInfo));
//					}else{
//						Map<String,String> locationInfo=resolveCityByIp(ip);
//						if (locationInfo.size()>0){
//							userInfo.put("province", locationInfo.get("province"));
//							userInfo.put("city", locationInfo.get("city"));
//						}
//					}
//				} 
//
//				this.userDao.insertUserInfoWithVerification(userInfo);
//				userInfo.put("filePath", Constant.DEFAULT_USER_PORTRAIT_PTAH);
//				this.userDao.initUserInfoPortraitDirect(userInfo);
//
//				Map<String, Object> attachMap = new HashMap<String, Object>();
//				attachMap.put("portraitPath", Constant.DEFAULT_USER_PORTRAIT_PTAH);
//				BusinessUtil.disposePath(attachMap, "portraitPath");
//				userInfo.put("portraitPath", attachMap.get("portraitPath").toString());
//
//			} else {
//				// 老用户，但不在缓存中。
//				Map<String, Object> persistInfo = this.userDao.getUserInfoByPhone(paramMap);
//				BusinessUtil.disposePath(persistInfo, "portraitPath");
//				userInfo = StringUtil.convertMap(persistInfo);
//			}
//
//		} else {
//			// 老用户，但不存在缓存中。
//			userInfo = userRelatedCacheServices.getCachedUseInfo(userId);
//			if (userInfo == null || userInfo.size() < 1) {
//				Map<String, Object> paramMap = new HashMap<String, Object>();
//				paramMap.put("phone", phone);
//				Map<String, Object> persistInfo = this.userDao.getUserInfoByPhone(paramMap);
//				BusinessUtil.disposePath(persistInfo, "portraitPath");
//				userInfo = StringUtil.convertMap(persistInfo);
//			}
//
//		}
//
//		String encryptedKey = null;
//
//		encryptedKey = this.userInfoLogin(phone, clientId);
//
//		userInfo.put("encryptedKey", encryptedKey);
//
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("clientId", pushId);
//		param.put("deviceId", clientId);
//		//param.put("appType", appType);
//		param.put("userId", userInfo.get("id"));
//		param.put("clientType", clientType);
//
//		if (!param.containsKey("phone"))
//			param.put("phone", phone);
//		
//		if (Constant.ASYNC_PROCESS){
//			//异步消息  进行推送设置          Revoke 2015.12.9 日
//			vitalProducer.sendMsg(TopicConstants.CLIENT_UPDATE_PUSH_LOGIN,  BusinessUtil.getLogId(), JSONObject.toJSONString(param));
//
//		}else {
//				updatePushInfo(param);
//		}
//		
//		jsonObject = new ResultJSONObject("000", "验证码验证通过,成功登陆");
//		jsonObject.put("userInfo", userInfo);
//
//		userRelatedCacheServices.cacheUserInfo(userInfo);
//
//		return jsonObject;
//	}
//
//	
	

	/** 更新用户信息 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateUserInfo(Long userId, int sex, String filePath) throws Exception {
		Integer result = 0;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("sex", sex);
		if (!filePath.equals("")) {
			paramMap.put("filePath", filePath);
			//判断此用户头像是否存在，如果存在则修改，如果不存在则新增
			int count =userDao.checkUserPortrait(paramMap);
			if(count>0){
				result += this.userDao.updateUserInfoPortrait(paramMap);
			}else{
				paramMap.put("id", userId);
				result += this.userDao.initUserInfoPortraitDirect(paramMap);
			}
		}
		result += this.userDao.updateUserInfo(paramMap);

		Map<String, String> userInfo = userRelatedCacheServices.getCachedUseInfo(String.valueOf(userId+""));

		if (userInfo==null || userInfo.size()<1){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			
			userInfo = this.userDao.getUserInfoByUserId(map);
			userInfo.put("id", userId.toString());
			userInfo.put("userId", userId.toString());
		}
		
		// FIX  null name 错误 2016.5.6   Revoke
		Object userName=userInfo.get("name");
		if (userName==null){
			 userInfo.put("name", "");
		}
		
		Object userType=userInfo.get("userType");
		if (userType==null){
			 userInfo.put("userType", "");
		}
		
		Object rongyuToken=userInfo.get("rongCloudToken");
		if (rongyuToken==null){	
			userInfo.put("rongCloudToken", "");
		}	

		userInfo.put("sex", String.valueOf(sex));		
		userInfo.put("id", String.valueOf(userInfo.get("id")));
		userInfo.put("phone", String.valueOf(userInfo.get("phone")));
		userInfo.put("userId", String.valueOf(userInfo.get("userId")));
		userInfo.put("userType", String.valueOf(userInfo.get("userType")));
		
		if (!filePath.equals("")) {
			Map<String, Object> attachMap = new HashMap<String, Object>();
			attachMap.put("portraitPath", filePath);
			BusinessUtil.disposePath(attachMap, "portraitPath");

			userInfo.put("portraitPath", StringUtil.null2Str(attachMap.get("portraitPath").toString()));
		}
		userRelatedCacheServices.cacheUserInfo(userInfo);

		return result > 0;
	}
	
	/** 微信端用户验证成功之后登陆 */
	public String userInfoLogin4WX(String phone, String clientId,String dynamicKey) throws Exception {
		String userKey = getUserKey(phone, clientId);
		if (logger.isDebugEnabled()) {
			logger.debug("====================dynamicKey=================" + dynamicKey);
		}
		try {
			userKey = AESUtil4WX.encrypt(dynamicKey,userKey);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("====================encryptedKey===============" + userKey);
		}
		return userKey;
	}

	/** 用户验证成功之后登陆 */
	@Override
	public String userInfoLogin(String phone, String clientId) throws Exception {
		String userKey = getUserKey(phone, clientId);
		if (logger.isDebugEnabled()) {
			logger.debug("====================userKey====================" + userKey);
		}
		String dynamicKey = validateService.getDynamicKey(clientId);
		if (logger.isDebugEnabled()) {
			logger.debug("====================dynamicKey=================" + dynamicKey);
		}
		try {
			userKey = AESUtil.parseByte2HexStr(AESUtil.encrypt(userKey, dynamicKey));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("====================encryptedKey===============" + userKey);
		}
		validateService.removeDynamicKey(clientId);
		return userKey;
	}

	@Override
	public String getUserKey(String phone, String clientId) {
		String userKey = (String) commonCacheService.getObject("userKey", clientId + phone);
		if (userKey == null) {
			userKey = getUserKeyFromDB(phone);
			if(!StringUtil.isEmpty(userKey)){
				commonCacheService.setObject(userKey, "userKey", clientId + phone);			
			}
		}
		return userKey;
	}

	private String getUserKeyFromDB(String phone) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		return userDao.getUserKey(paramMap);
	}

	/** 上传用户头像 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean uploadUserPortrait(Long userId, String filePath) throws Exception {
		boolean result = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		if (!filePath.equals("")) {
			paramMap.put("filePath", filePath);
			int check = this.userDao.checkUserPortrait(paramMap);
			if (check == 0) {
				this.userDao.uploadUserPortrait(paramMap);
			} else {
				this.userDao.updateUserInfoPortrait(paramMap);
			}
			result = true;

			// 同步更新缓存
			Map<String, String> userInfo = userRelatedCacheServices.getCachedUseInfo(userId.toString());

			Map<String, Object> attachMap = new HashMap<String, Object>();
			attachMap.put("portraitPath", filePath);
			BusinessUtil.disposePath(attachMap, "portraitPath");

			userInfo.put("portraitPath", attachMap.get("portraitPath").toString());
			userRelatedCacheServices.cacheUserInfo(userInfo);
		}
		return result;
	}

	/** 获取用户地址信息 */
	@Override
	public List<Map<String, Object>> getUserAddressInfo(Long userId, String addressType) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("addressType", addressType);
		//paramMap.put("appType", appType);
		return this.userDao.getUserAddressInfo(paramMap);
	}

	/** 更新用户的地址信息,如果存在则更新,否则插入新的记录 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateUserAddressInfo(Long userId, String addressType, Double latitude, Double longitude, String addressInfo) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("addressType", addressType);
		paramMap.put("latitude", latitude);
		paramMap.put("longitude", longitude);
		paramMap.put("addressInfo", addressInfo);
		//paramMap.put("appType", appType);
		try {
			// 验证是否存在对应的记录
			if (this.userDao.checkUserAddressInfo(paramMap) > 0) {
				this.userDao.updateUserAddressInfo(paramMap);
			} else {
				this.userDao.insertUserAddressInfo(paramMap);
			}
		} catch (Exception e) {
			throw new ApplicationException(e, "update_user_address_fail", "更新用户地址信息失败");
		}
		return true;
	}

	/** 获取用户可以使用的代金券的个数 */
	@Override
	public int getUserAvailableVouchersCount(Long userId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		return this.userDao.getUserAvailableVouchersCount(paramMap);
	}

	/** 获取用户可以使用的代金券列表 */
	@Override
	public List<Map<String, Object>> getUserAvailableVouchersInfo(Long userId, int pageNo) throws Exception {
		List<Map<String, Object>> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("startNum", pageNo * Constant.PAGESIZE);
		paramMap.put("pageSize", Constant.PAGESIZE);
		resultList = this.userDao.getUserAvailableVouchersInfo(paramMap);
		for (int i = 0; i < resultList.size(); i++) {
			BusinessUtil.disposePath(resultList.get(i), "couponsTypePicPath");
		}
		return resultList;
	}

	/** 获取用户历史的代金券的个数 */
	@Override
	public int getUserHistoryVouchersCount(Long userId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		return this.userDao.getUserHistoryVouchersCount(paramMap);
	}

	/** 获取用户历史的代金券列表 */
	@Override
	public List<Map<String, Object>> getUserHistoryVouchersInfo(Long userId, int pageNo) throws Exception {
		List<Map<String, Object>> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("startNum", pageNo * Constant.PAGESIZE);
		paramMap.put("pageSize", Constant.PAGESIZE);
		resultList = this.userDao.getUserHistoryVouchersInfo(paramMap);
		for (int i = 0; i < resultList.size(); i++) {
			BusinessUtil.disposePath(resultList.get(i), "couponsTypePicPath");
		}
		return resultList;
	}

	/** 删除用户代金券 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deleteVouchersInfo(Long vouchersId, Long userId) throws Exception {
		int result = 0;
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("vouchersId", vouchersId);
		paramMap.put("userId", userId);
		result = userDao.deleteVouchersInfo(paramMap);
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "删除代金券成功");
		} else {
			jsonObject = new ResultJSONObject("001", "删除代金券失败");
		}
		return jsonObject;
	}

	/** 用户端退出应用 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject cleanUserInfoPush(Long userId ,String clientId) {
		JSONObject jsonObject = null;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("clientId", clientId);
			paramMap.put("userId", userId);
			this.userDao.deleteUserPushByClientId(paramMap);
			jsonObject = new ResultJSONObject("000", "退出成功");
		} catch (Exception ex) {
			logger.error("", ex);
			throw new ApplicationException(ex, "exit_failure", "退出失败");

		}
		return jsonObject;
	}

	// 验证用户是否登陆
	@Override
	public JSONObject checkClient(String clientId, Long userId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		JSONObject jsonObject = null;
		//paramMap.put("appType", appType);
		paramMap.put("clientId", clientId);
		paramMap.put("userId", userId);
		int result = userDao.checkClient(paramMap);
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "用户尚未退出");
		} else {
			jsonObject = new ResultJSONObject("exit", "用户已经退出");
		}
		return jsonObject;
	}

	@Override
	public Map<String, String> getUserInfoByPhoneWithStr(String phone) throws Exception {
		String userId = userRelatedCacheServices.getUserIdByPhone(phone);
		Map<String, String> userInfo = null;
		if (userId != null) {
			userInfo = userRelatedCacheServices.getCachedUseInfo(userId);
		}

		if (userId == null || userId.equals("") || userInfo == null || userInfo.size() < 1) {
			// 不在缓存中，从数据库加载
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("phone", phone);
			Map<String, Object> persistInfo = this.userDao.getUserInfoByPhone(paramMap);
			if (persistInfo != null) {
				BusinessUtil.disposePath(persistInfo, "portraitPath");

				userInfo = StringUtil.convertMap(persistInfo);

				userRelatedCacheServices.cacheUserInfo(userInfo);
			} else {
				return null;
			}
		}

		return userInfo;
	}

	/** 更改当前使用的设备记录的clientId */
	@Override
	public JSONObject updateClientId(String pushId, Long userId, String clientType, String clientId,String phoneModel) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		JSONObject jsonObject = null;
		//paramMap.put("appType", appType);
		paramMap.put("pushId", pushId);
		paramMap.put("userId", userId);
		paramMap.put("clientId", clientId);
		paramMap.put("clientType", clientType);
		paramMap.put("phoneModel", phoneModel);

		// 2015年11月25日 增加根据deviceId 判断是否需要更新 clientId
		int checkCount = userDao.checkClientId(paramMap);
		if (checkCount == 0) {
			// 2015年11月25日 增加根据deviceId 更新 clientId
			int result = userDao.updateClientId(paramMap);
			if (result > 0) {
				jsonObject = new ResultJSONObject("000", "更改设备记录的clientId成功");
			} else {
				jsonObject = new ResultJSONObject("fail", "更改设备记录的clientId失败");
			}
		} else {
			jsonObject = new ResultJSONObject("000", "更改设备记录的clientId成功");
		}

		return jsonObject;
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public boolean  updateUserLocation(String ip,String userId) {
				boolean result = true;
				
		        Map<String,String> location = resolveCityByIp(ip);
		        
		        if (location==null || location.size()<1)
		        		return result;
		 
		        location.put("userId", userId);
		 
		        userDao.updateUserLocation(location);
		        
		        Map<String,String> userInfo = userRelatedCacheServices.getCachedUseInfo(userId);
				
		        if (userInfo.size()>0){
		        	userInfo.put("province", location.get("province"));
					userInfo.put("city", location.get("city"));
		        }
		        
		        return result;
				
	}
	
	
	
	/**
	 * 抽取  ip解析成CITY 的逻辑块为方法  2015.12.9 便于以后改为异步调用。
	 * @param ip
	 */
	private Map<String,String> resolveCityByIp(String ip) {
		// 根据IP获取省份和城市
		// ADD ip-city 缓存 ---2015.09.21 ----Revoke Yu
		JSONObject cachedIpAddress = ipCityCacheServices.getCity(ip);
		
		Map<String,String> result  = new HashMap<String,String>();

		if (cachedIpAddress != null) {
			result.put("province", cachedIpAddress.get("province").toString());
			result.put("city", cachedIpAddress.get("city").toString());
		} else {
			JSONObject jsonObjectIp = IPutil.getIpLocationBySina(ip);
			if (jsonObjectIp != null) {
				result.put("province", StringUtil.null2Str(jsonObjectIp.get("province")));
				result.put("city", StringUtil.null2Str(jsonObjectIp.get("city")));
				ipCityCacheServices.cachedCity(ip, result.get("province"), result.get("city"));
			} else {
				jsonObjectIp = IPutil.getIpLocationByBaidu(ip);
				if (jsonObjectIp != null) {
					if (jsonObjectIp.get("address") != null) {
						String address = (String) jsonObjectIp.get("address");
						if (address != null && address.length() > 0) {
							String[] addressDetail = address.split("\\|");
							if (addressDetail != null) {
								result.put("province", addressDetail[1]);
								result.put("city", addressDetail[2]);
								ipCityCacheServices.cachedCity(ip, addressDetail[1], addressDetail[2]);
							}
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateUserPushInfo(String clientId, String deviceId,
			String userId, String clientType) {
		
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("pushId", clientId);
		param.put("clientId", deviceId);
		//param.put("appType", appType);
		param.put("userId", userId);
		param.put("clientType", clientType);

		//重复登录推送
		param.put("pushType", 8);
		try {
			pushService.basicPush(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return true;
	}

	@Override
	public String getRongCloudToken(String phone) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phone", phone);
        String userId = "";  // 用户ID
		String rongCloudToken = ""; // 融云token
        String portraitUri = ""; // 用户头像

        Map<String, Object> user = this.userDao.getUserInfoByPhone(param);
    	rongCloudToken = StringUtil.null2Str(user.get("rongCloudToken"));
    	userId = StringUtil.null2Str(user.get("userId"));
    	String name = StringUtil.null2Str(user.get("name"));
    	portraitUri =  StringUtil.null2Str(user.get("portraitPath"));
    	
		if(StringUtil.isNullStr(portraitUri)){
			portraitUri = Constant.DEFAULT_USER_PORTRAIT_PTAH;
		}
		portraitUri = BusinessUtil.disposeImagePath(portraitUri);
		if(StringUtil.isNullStr(rongCloudToken)){
			// 请求融云服务器获取token
			rongCloudToken = RongCloudUtil.getRongCloudTokenFromProxy(userId, name, portraitUri);
			if (StringUtil.isNullStr(rongCloudToken)) {
				// 融云服务器响应异常，返回提示信息
				rongCloudToken = "error";
			} else {
				if ("proxyerror".equals(rongCloudToken)) {
					return rongCloudToken;
				} else {
					// 正常响应，更新融云token至数据库
					param.put("userId", userId);
					param.put("rongCloudToken", rongCloudToken);
					userDao.updateRongCloudToken(param);
					user.put("id", userId);
					user.put("rongCloudToken", rongCloudToken);
					userRelatedCacheServices.cacheUserInfo(StringUtil.convertMap(user));
				}
			}
		}

		return rongCloudToken;
	}
	public List<Map<String, Object>> myMerchantList(Long userId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		list = userDao.myMerchantList(paramMap);
		if(list!=null&&list.size()>0){
			// 处理店铺服务项目
			for(Map<String, Object> map : list){
//				String isOpened = "0"; //开店是否填写完整信息
//				String haveService = "0"; //开店是否选择了服务项目
//				String name = StringUtil.null2Str(map.get("name"));
//				String appType = StringUtil.null2Str(map.get("appType"));
//				String address = StringUtil.null2Str(map.get("address"));
//				List<Map<String, Object>> serviceList = new ArrayList<Map<String, Object>>();
//				String serviceIds = StringUtil.null2Str(map.get("serviceIds"));
				BusinessUtil.disposePath(map, "path");
//				if(!StringUtil.isNullStr(serviceIds)){
//					haveService = "1";
//					String[] ss = serviceIds.split(",");
//					int count = 0;
//					for(String serviceType:ss){
//						Map<String, Object> serviceTypeMap = commonService.getServiceType(serviceType);
//						if (serviceTypeMap != null) {
//							serviceList.add(serviceTypeMap);
//							count ++;
//							if (count >= 4) {
//								break;
//							}
//						}
//					}
//				}
//				if("1".equals(haveService) && !StringUtil.isNullStr(name) && !StringUtil.isNullStr(address)){
//					is/Opened = "1"; 
//				}
//				map.put("haveService", haveService);
//				map.put("isOpened", isOpened);
//				map.put("serviceList", serviceList);

				// authType:0-未认证 1-企业认证 2-个人认证
				String authType = map.get("authType") == null ? "0" : map.get("authType").toString();
				map.put("authType", authType);				
				map.remove("serviceIds");
			}
		}
		return list;
	}
	
	/**
	 * 验证码限制
	 * @param phone
	 * @param code
	 * @param minutesKey
	 * @param codeCountKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String verificationCodeRestrict(String phone,String code,String restrictKey,String cacheName0,String cacheName){

		
		//1分钟1次验证码
		if(StringUtil.isNotEmpty(cacheName0)){
			String cacheName1="sms";
			if(cacheName0.toLowerCase().contains("voice")){
				cacheName1="voice";
			}
			Long outTime=(Long) commonCacheService.getObject(cacheName0,cacheName1,phone);
			Long nowTime=System.currentTimeMillis();
			if(outTime!=null){
				if(nowTime-outTime < 60000){
					return "-1";
				}
			}
			commonCacheService.setObject(nowTime,60,cacheName0,"sms",phone);
		}
		
		//获取配置参数
		Map<String, Object> configMap=commonService.getConfigurationInfoByKey(restrictKey);	   
		if(configMap==null){
			return "0";
		}
		int isOpen=StringUtil.nullToInteger(configMap.get("config_value")); 
		if(isOpen==0){//关闭
			return "0";
		}
		int minutes=StringUtil.nullToInteger(configMap.get("standby_field1")); 
		int codeCount=StringUtil.nullToInteger(configMap.get("standby_field2"));
		
		List<Map<String,Object>> codeList=(List<Map<String,Object>>)commonCacheService.getObject(cacheName, phone);
		if(codeList==null){
			codeList=new ArrayList<Map<String,Object>>();
		}		
//		System.out.println(phone+" 验证码为："+JSONObject.toJSONString(codeList));
		
		Map<String,Object> map= new HashMap<String, Object>();
		map.put("phone", phone);
		map.put("time", System.currentTimeMillis());
		map.put("code", code);
		codeList.add(map);
		int size=codeList.size();
//		System.out.println(phone+" "+minutes+"分钟之内已发送"+size+"条验证码");	
		
		if(size>codeCount){
			long time1=StringUtil.nullToLong(codeList.get(0).get("time"));
			long time2=StringUtil.nullToLong(codeList.get(size-1).get("time"));
//			System.out.println(phone+" 第一条验证码时间为："+time1);
//			System.out.println(phone+" 最新一条验证码时间为："+time2);
//			System.out.println(phone+" 时间差为："+(time2-time1 - minutes*60*1000));
			if(time2-time1 < minutes*60*1000){//N分钟之内		
				String result="";
				int s=(int)((minutes*60*1000-(time2-time1))/1000);
				if(s > 60){
					result=s/60+"分钟"+s%60+"秒";
				}else{
					result=s+"秒";
				}
//				System.out.println("验证码获取频繁，请于"+result+"后重新获取验证码");	
				return result;
			}else{				
				codeList.remove(0);					
//				System.out.println(phone+" 超过"+codeCount+"条验证码，去除最后几条");							
			}
//			System.out.println(phone+" 没有超过"+minutes+"分钟，可以继续发送验证码");
		}
		commonCacheService.setObject(codeList,minutes*60*2,cacheName, phone);
		return "0";
	}
}
