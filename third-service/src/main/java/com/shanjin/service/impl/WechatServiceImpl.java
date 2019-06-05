package com.shanjin.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import open.wechat.model.UserEntity;
import open.wechat.util.ShareSignUtil;

import org.springframework.stereotype.Service;

import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.EmojiFilterUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IUserDao;
import com.shanjin.service.IWechatService;

/**
 * 微信服务具体实现
 * @author Huang yulai
 *
 */
@Service("wechatService")
public class WechatServiceImpl implements IWechatService{

	@Resource
    private ICommonCacheService commonCacheService;
	
	@Resource
	private IUserDao userDao;
	
    @Override
    public Map<String, String> getSignParam(String url) {
   
        Map<String, String> map = null;
        String jsapi_ticket=null;
        String urlB=(String) commonCacheService.getObject(CacheConstants.URL_KEY);
        if(StringUtil.isNull(urlB)||!url.equals(urlB)){
        	map = ShareSignUtil.getSignParam(url);
            commonCacheService.setObject(url, CacheConstants.URL_KEY);
            commonCacheService.setObject(new Date(), CacheConstants.TIME_SAMP_KEY);
            commonCacheService.setObject(map, CacheConstants.JSP_TICKET_KEY);
        }else{
        Date date= (Date) commonCacheService.getObject(CacheConstants.TIME_SAMP_KEY);
        if(date==null){
        	map = ShareSignUtil.getSignParam(url);
            commonCacheService.setObject(new Date(), CacheConstants.TIME_SAMP_KEY);
            commonCacheService.setObject(map, CacheConstants.JSP_TICKET_KEY);
        }else if(DateUtil.lessThanTime(date)){
        	map = (Map<String, String>) commonCacheService.getObject(CacheConstants.JSP_TICKET_KEY);
        }else{
        	map = ShareSignUtil.getSignParam(url); 
            commonCacheService.setObject(new Date(), CacheConstants.TIME_SAMP_KEY);
            commonCacheService.setObject(map, CacheConstants.JSP_TICKET_KEY);
        }
        }
        return map;
    }

	@Override
	public void saveUserInfo(String orderNo,  String demand) {
		commonCacheService.setObject(demand,CacheConstants.USER_PRE_VERIFY_TIMEOUT,orderNo, "demand");
	}

	@Override
	public Map<String, Object> getUserInfo(String orderNo,String openId) {
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, String> entity= (Map<String, String>) commonCacheService.getObject(CacheConstants.WEIXIN_USER, openId);
		String demand=(String) commonCacheService.getObject(orderNo, "demand");
		map.put("user", entity);
		map.put("demand", demand);
		return map;
	}

	@Override
	public void saveUserInfoByOpenId(String openId, UserEntity entity) {
		if(entity==null){
			return;
		}
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("headimgUrl", entity.getHeadimgurl());
		String nickname = EmojiFilterUtil.filterEmoji(StringUtil.null2Str(entity.getNickname()));
		map.put("nickname", nickname);
		map.put("openid", entity.getOpenid());
		map.put("province", entity.getProvince());
		map.put("city", entity.getCity());
		map.put("sex", entity.getSex());
		map.put("country", entity.getCountry());
		int checkWechatUser = userDao.checkWechatUserIsEmpty(map); // 检查该微信用户是否被保存过
		if(checkWechatUser==0){
			// 未保存
			userDao.insertWechatUser(map);
		}else{
			// 更新
			userDao.updateWechatUser(map);
		}
		commonCacheService.setObject(map,CacheConstants.USER_PRE_VERIFY_TIMEOUT,CacheConstants.WEIXIN_USER, openId);
	}

	@Override
	public Map<String, Object> getUser(String openId) {
		Map<String, Object> map=(Map<String, Object>) commonCacheService.getObject(CacheConstants.WEIXIN_USER, openId);
	    return map;
	}

}
