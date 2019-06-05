package com.shanjin.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DynamicKeyGenerator;
import com.shanjin.common.util.IdGenerator;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IUserDao;
import com.shanjin.service.IUserService;

/**
 * 用户相关实现类
 * @author 
 *
 */
@Service("tuserService")
public class UserServiceImpl implements IUserService{
	
	@Resource
	private ICommonCacheService commonCacheService;
	
	@Resource
	private IUserRelatedCacheServices userRelatedCacheServices;

	@Resource
	private IUserDao userDao;
	
	@Override
	public JSONObject getVerificationCode(String phone,String content) {
		JSONObject jsonObject = null;
		String verificationCode = BusinessUtil.createVerificationCode(phone);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("phone", phone);
		paramMap.put("verificationCode", verificationCode);

		userRelatedCacheServices.cacheVerifyInfo(phone, paramMap);

		// 短信发送验证码
		jsonObject = BusinessUtil.sendVerificationCode(phone, verificationCode, 1,content);
//		//****test
//		Map<String, Object> map=new HashMap<String, Object>();
//		map.put("headimgUrl", "http://www.meiwenting.com/uploads/userup/14334/142P19146-2J5.jpg");
//		map.put("nickname", "黄玉来--小呆呆");
//		map.put("openid", 123456789);
//		map.put("province", "安徽");
//		map.put("city", "合肥");
//		map.put("sex", 1);
//		map.put("country", "中国");
//		int checkWechatUser = userDao.checkWechatUserIsEmpty(map); // 检查该微信用户是否被保存过
//		if(checkWechatUser==0){
//			// 未保存
//			userDao.insertWechatUser(map);
//		}else{
//			// 更新
//			userDao.updateWechatUser(map);
//		}
//		commonCacheService.setObject(map,CacheConstants.USER_PRE_VERIFY_TIMEOUT,CacheConstants.WEIXIN_USER, "123456789");
//		//**test
		return jsonObject;
	}

	/**
	 * 微信端发起注册流程
	 * 1.先验证短信验证码是否合法
	 * 2.先根据微信用户标识openId获取到微信用户信息（如果缓存失效，只保存openId），wechat_user表中
	 * 3.将微信用户信息转化成平台用户信息，完成注册，user_info表中
	 */
	@Override
    @Transactional
	public JSONObject validateVerificationCode(String phone,
			String verificationCode, String ip,String openId) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("phone", phone);
		paramMap.put("openid", openId);
		Map<String, String> cachedInfo = userRelatedCacheServices.getVerifyInfo(phone);

		if (cachedInfo == null || cachedInfo.size() < 1 || !cachedInfo.get("verificationCode").equals(verificationCode)) {
			// 缓存过期
			jsonObject = new ResultJSONObject("002", "错误的验证码");
			return jsonObject;
		}
		Map<String,Object> wechatUser = (Map<String, Object>) commonCacheService.getObject(CacheConstants.WEIXIN_USER, openId);// 缓存中读取微信用户信息
        if(wechatUser!=null&&!wechatUser.isEmpty()){
        	// 信息存在
        }else{
        	wechatUser = userDao.getWechatByOpenId(paramMap);
        	if(wechatUser!=null&&!wechatUser.isEmpty()){
        		commonCacheService.setObject(wechatUser,CacheConstants.USER_PRE_VERIFY_TIMEOUT,CacheConstants.WEIXIN_USER, openId);
        	}else{
        		wechatUser = new HashMap<String,Object>();
        		wechatUser.put("openid", openId);
        	}
        }
        Map<String,Object> user = this.userDao.getUserInfoByPhone(paramMap); // 平台用户信息
        if(user!=null&&!user.isEmpty()){
        	// 平台已存在
        	paramMap.put("userId", user.get("id"));
        	userDao.updateWechatUserForId(paramMap);
        }else{
        	// 平台不存在，保存用户
        	user = new HashMap<String, Object>();
			String userId = StringUtil.null2Str(IdGenerator.generateID(18));
			user.put("id", userId);
			user.put("userId", userId);
			user.put("userKey", DynamicKeyGenerator.generateDynamicKey());
			user.put("phone", phone);
			user.put("verificationCode", verificationCode);
			user.put("ip", ip);
			user.put("sex", wechatUser.get("sex"));
			user.put("name", wechatUser.get("nickname"));
			user.put("userType", "0");
			user.put("province", wechatUser.get("province"));
			user.put("city", wechatUser.get("city"));
			user.put("platform", 1); //微信平台
			int havePlatform = userDao.getPlatform(paramMap);
			user.put("havePlatform", havePlatform);
        	userDao.insertUserInfoWithVerification(user);// 用户基本信息
        	//
        	String portraitPath = StringUtil.null2Str(wechatUser.get("headimgUrl"));
        	paramMap.put("userId", userId);
  			if(StringUtil.isNullStr(portraitPath)){
				// 微信头像为空，默认平台
				portraitPath = Constant.DEFAULT_USER_PORTRAIT_PTAH;
				portraitPath = BusinessUtil.disposeImagePath(portraitPath); 
			}else{
				// 保存头像
				paramMap.put("filePath", portraitPath);
				userDao.initUserInfoPortraitDirect(paramMap);
			}
			
  			user.put("portraitPath", portraitPath);
  		
        	userDao.updateWechatUserForId(paramMap);
        }

		jsonObject = new ResultJSONObject("000", "验证码验证通过,成功登陆");
		jsonObject.put("userInfo", user);
		return jsonObject;
	}
	
	@Override
	public JSONObject collectionMerchant(String openId, Long merchantId)
			throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("openid", openId);
		Map<String, Object> wechatUser = userDao.getWechatByOpenId(paramMap);
		if(wechatUser!=null&&!wechatUser.isEmpty()){
			Long userId = StringUtil.nullToLong(wechatUser.get("user_id"));
			if(userId.intValue()!=0){
				// 平台已注册用户，可以收藏
				paramMap.put("userId", userId);
				paramMap.put("merchantId", merchantId);
				int isCollect = userDao.isCollectionMerchant(paramMap);
				if(isCollect>0){
					// 已收藏
					jsonObject.put("resultCode", "002");
					jsonObject.put("message", "用户已收藏该商户");
				}else{
					//未收藏
					if (userDao.collectionMerchant(paramMap) > 0) {
						jsonObject.put("resultCode", "000");
						jsonObject.put("message", "用户收藏商户成功");
						//更新收藏缓存
						int num=0 ;
						Object fans=(Object) commonCacheService.getObject(CacheConstants.MERCHANT_FANS, StringUtil.null2Str(merchantId));
						if(fans!=null){
							num=(int)fans;
						}
						commonCacheService.setObject(num+1, CacheConstants.MERCHANT_FANS, StringUtil.null2Str(merchantId));
					} else {
						jsonObject.put("resultCode", "fail");
						jsonObject.put("message", "用户收藏商户失败");
					}
				}
				return jsonObject;
			}
		}
		jsonObject.put("resultCode", "001");
		jsonObject.put("message", "该微信用户还不是平台用户，需验证登陆");
		
		return jsonObject;
	}

	@Override
	public JSONObject validateVerificationCodeForDream(String phone,
			String verificationCode, String ip) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("phone", phone);
		Map<String, String> cachedInfo = userRelatedCacheServices.getVerifyInfo(phone);

		if (cachedInfo == null || cachedInfo.size() < 1 || !cachedInfo.get("verificationCode").equals(verificationCode)) {
			// 缓存过期
			jsonObject = new ResultJSONObject("002", "错误的验证码");
			return jsonObject;
		}
        String isFirst = "1"; // 是否第一次验证平台（1：是，0：否）
        Map<String,Object> user = this.userDao.getUserInfoByPhone(paramMap); // 平台用户信息
        if(user!=null&&!user.isEmpty()){
        	// 平台已存在
        	isFirst = "0";
        }else{
        	// 平台不存在，保存用户
        	user = new HashMap<String, Object>();
			String userId = StringUtil.null2Str(IdGenerator.generateID(18));
			user.put("id", userId);
			user.put("userId", userId);
			user.put("userKey", DynamicKeyGenerator.generateDynamicKey());
			user.put("phone", phone);
			user.put("verificationCode", verificationCode);
			user.put("ip", ip);
			user.put("userType", "0");
			user.put("province", Constant.EMPTY);
			user.put("city", Constant.EMPTY);
			user.put("platform", 1); //微信平台
			int havePlatform = userDao.getPlatform(paramMap);
			user.put("havePlatform", havePlatform);
        	userDao.insertUserInfoWithVerification(user);// 用户基本信息
        	//
        	String portraitPath = Constant.DEFAULT_USER_PORTRAIT_PTAH;
			portraitPath = BusinessUtil.disposeImagePath(portraitPath); 
			user.put("portraitPath", portraitPath);
        }

		jsonObject = new ResultJSONObject("000", "验证码验证通过,成功登陆");
		jsonObject.put("userInfo", user);
		jsonObject.put("isFirst", isFirst);
		return jsonObject;
	}

	@Override
	public JSONObject getUserCount() {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int count = userDao.getUserCount(paramMap);
		jsonObject.put("resultCode", "000");
		jsonObject.put("count", count);
		jsonObject.put("message", "获取平台用户数成功");
		
		return jsonObject;
	}

}
