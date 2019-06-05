package com.shanjin.goldplan.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.JedisLock;
import com.shanjin.common.util.DateUtil;
import com.shanjin.dao.IMerchantInfoForH5Dao;
import com.shanjin.dao.IUserDao;
import com.shanjin.goldplan.dao.InvitedRecordMapper;
import com.shanjin.goldplan.model.InvitedRecord;
import com.shanjin.goldplan.model.Prize;
import com.shanjin.goldplan.service.InviteService;
import com.shanjin.goldplan.util.InvitedStatusEnum;
import com.shanjin.goldplan.util.PrizeMathRandom;
import com.shanjin.outServices.aliOss.AliOssUtil;
import com.shanjin.util.ActivityException;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/19
 * @desc TODO
 */
@Service("inviteService")
public class InviteServiceImpl implements InviteService {

	private static final Logger logger = Logger.getLogger(InviteServiceImpl.class);

    @Autowired
    private IUserDao userDao;
    @Autowired
    private InvitedRecordMapper invitedRecordMapper;
    
    @Autowired
    private IMerchantInfoForH5Dao merchantInfoForH5Dao;

	@Resource
	private ICommonCacheService commonCacheService;

    @Override
    public int addInvitedRecord(InvitedRecord record) throws ActivityException {

        Map<String, String> map = userDao.getUserInfo(record.getUserId());
        if (null == map || map.isEmpty()) {
            throw new ActivityException("002", "邀请用户不存在");
        }
        if(!record.getUserPhone().equals(map.get("phone"))){
            throw new ActivityException("003","邀请手机号与用户ID不一致");
        }
        if(record.getUserPhone().equals(record.getInvitedPhone())){
            throw new ActivityException("004","输入的手机号与邀请人号码相同");
        }
        //带插入参数补齐
        record.setStatus(InvitedStatusEnum.UN_EFFECTIVE.getStatus());
        record.setInvitedTime(new Date());
        int count =invitedRecordMapper.atomSaveEntity(record);
        return count;
    }

    @Override
    public Map<String, String> getInviteUserInfo(Long userId_) {

        Map<String, String> rstMap = new HashMap<>(2);

        Map<String, String> map = userDao.getUserInfo(userId_);

        if (null == map || map.isEmpty()) {
            throw new ActivityException("002", "邀请用户不存在");
        }
        //非空处理 path
        String photoUrl = "";
        String path = map.get("path");
        if (!StringUtils.isEmpty(path)) {
            photoUrl = AliOssUtil.getViewUrl(path);
        }
        rstMap.put("phone", map.get("phone"));
        rstMap.put("photoUrl", photoUrl);
        return rstMap;
    }

	@Override
	public Map<String, Object> findTouchGoldChance(Long userId_,Long activityId) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("userId_", userId_);
		param.put("activityId", activityId);
		
		//查询活动有效期
		Map<String,Object> activityInfo = (Map<String,Object>) commonCacheService.getObject(CacheConstants.ACTIVITY_INFO, null);
		if(activityInfo == null || activityInfo.size() == 0){
			activityInfo = merchantInfoForH5Dao.findActivityInfo(param);
			if(activityInfo == null || activityInfo.size() == 0){
				throw new ApplicationContextException("活动信息未初始化......");
			}
			commonCacheService.setObject(activityInfo,CacheConstants.ACTIVITY_INFO, null);
		}
		long acStartTime = Long.parseLong(activityInfo.get("acStartTime").toString());//YYYYMMDD
		long acEndTime = Long.parseLong(activityInfo.get("acEndTime").toString());//YYYYMMDD
		long nowTime = Long.parseLong(activityInfo.get("nowTime").toString());//YYYYMMDD
		if(nowTime < acStartTime){
			throw new ApplicationContextException("活动暂未开始。");
		}
		if(nowTime > acEndTime){
			throw new ApplicationContextException("活动已经结束，敬请期待下次活动。");
		}
		
		//查询订单有效时间
		Map<String,Object> activityOrderTime = (Map<String, Object>) commonCacheService.getObject(CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_BASIC);
		if(activityOrderTime == null || activityOrderTime.size() == 0){
			activityOrderTime = invitedRecordMapper.getActivityOrderTime(param);
			if(activityOrderTime == null || activityOrderTime.size() == 0){
				throw new ApplicationContextException("活动信息未初始化......");
			}
			commonCacheService.setObject(activityOrderTime,CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_BASIC);
		}
		param.putAll(activityOrderTime);
		
		//查询今日发单并且超过十分钟无人接单记录
		int orderNum = merchantInfoForH5Dao.getNumberByOrder(param);
		//查询今日邀请好友并且好友已发单记录
		List<Map<String,Long>> userPhones = invitedRecordMapper.getShareUserPhone(userId_);
		int shareNum = 0;
		if(userPhones != null && userPhones.size() > 0){
			shareNum = merchantInfoForH5Dao.getNumberByShare(userPhones);
		}
		//已摸金次数
		int alreadyTouchNum = invitedRecordMapper.getNumberByOrder(param);
		//剩余次数
		int surplusNum = orderNum + shareNum - alreadyTouchNum;
		if(surplusNum < 0){
			surplusNum = 0;
		}
		//查询累计摸金次数及获得现金总额
		Map<String,Object> total = invitedRecordMapper.getTotalTouchGold(param);
		//查询今日已摸金次数
		param.put("today", true);
		Map<String,Object> today = invitedRecordMapper.getTotalTouchGold(param);
		int todayNum = Integer.parseInt(today.get("totalNum").toString());

		resultMap.put("orderChance", orderNum);//发单且超过十分钟
		resultMap.put("surplusNum", surplusNum);
		resultMap.put("todayNum", todayNum);
		resultMap.putAll(total);
		//放入缓存，用于抽奖时判断，用户次数每次查询时刷新一次
		commonCacheService.setObject(resultMap, CacheConstants.ACT_REWARD_ELEMENT, String.valueOf(userId_),DateUtil.getCurrentday());
		return resultMap;
	}
	
	@Transactional
	public synchronized Map<String, Object> touchGold(Long userId_,Long activityId,int source) throws InterruptedException,ApplicationContextException,Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(this.getTouchLock(activityId)){
				//判断是否有机会
				Map userMap = (Map) commonCacheService.getObject(CacheConstants.ACT_REWARD_ELEMENT, String.valueOf(userId_),DateUtil.getCurrentday());
				if(userMap == null || userMap.size() == 0){
//					userMap = this.findTouchGoldChance(userId_,activityId);
					throw new ApplicationContextException("请先查询摸金机会，再进行摸金。");
				}
				int surplusNum = Integer.parseInt(userMap.get("surplusNum").toString());
//				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
//				String date = sdf1.format(new Date());
				if(surplusNum > 0){
					Map<String, String> userInfo = userDao.getUserInfo(userId_);
					//缓存获取奖励配置相关信息(奖品名称、概率、总数、每日数、当日剩余数等)
					
					List<Map<String,Object>> rewardElementConfigLst = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.ACT_REWARD_ELEMENT,CacheConstants.ACT_REWARD_ELEMENT_CONFIG);
					Map<String,Object> rewardElementMap = (Map<String, Object>) commonCacheService.getObject(CacheConstants.ACT_REWARD_ELEMENT, "");
					Map<String,Object> rewardElementTextMap = (Map<String, Object>) commonCacheService.getObject(CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_TEXT);

					Map<String,Object> param = new HashMap<String, Object>();
					param.put("activityId", activityId);
					//获取奖励配置
					if(rewardElementConfigLst == null || rewardElementConfigLst.size() == 0){
						rewardElementConfigLst = invitedRecordMapper.findRewardElementConfig(param);
						commonCacheService.setObject(rewardElementConfigLst, CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_CONFIG);//奖励配置
						
					}
					if(rewardElementMap == null || rewardElementTextMap == null){
						List<Map<String,Object>> rewardElementLst = invitedRecordMapper.findRewardElementLst(param);
						if(rewardElementLst == null || rewardElementLst.size() == 0){//未配置
							throw new ApplicationContextException("请配置摸金参数");
						}
						//提取奖励元素
						for(Map<String,Object> rewardElement: rewardElementLst){
							int elementId = Integer.parseInt(rewardElement.get("id").toString());
							if(rewardElementMap == null || rewardElementMap.size() == 0){
								rewardElementMap = new HashMap<String, Object>();
							}
							if(!rewardElementMap.containsKey(String.valueOf(elementId))){
								rewardElementMap.put(String.valueOf(elementId), rewardElement);
							}
							param.put("elementId", elementId);
							List<Map<String,Object>> rewardElementTextLst = invitedRecordMapper.findRewardElementTextLst(param);
							if(rewardElementTextLst == null || rewardElementTextLst.size() ==0){
								throw new ApplicationContextException("请配置摸金参数");
							}
							if(rewardElementTextMap == null || rewardElementTextMap.size() == 0){
								rewardElementTextMap = new HashMap<String, Object>();
							}
							if(!rewardElementTextMap.containsKey(String.valueOf(elementId))){
								rewardElementTextMap.put(String.valueOf(elementId), rewardElementTextLst);
							}
						}
						
						commonCacheService.setObject(rewardElementMap, CacheConstants.ACT_REWARD_ELEMENT, "");//奖励元素
						commonCacheService.setObject(rewardElementTextMap, CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_TEXT);//奖励文案
					}
					//奖品还有剩余
					if(rewardElementConfigLst != null && rewardElementConfigLst.size() > 0){
						//抽奖
						List<Prize> prizeLst = new ArrayList<Prize>();
						for(Map<String,Object> rewardElementConfig : rewardElementConfigLst ){
							int lastLimit = Integer.parseInt(rewardElementConfig.get("lastLimit").toString());
							if(lastLimit >0 ){//今日有剩余的奖品参与抽奖
								Prize p = new Prize(
										Integer.parseInt(rewardElementConfig.get("elementId").toString()),
										rewardElementConfig.get("elementName").toString(),
										lastLimit,
										Integer.parseInt(rewardElementConfig.get("odds").toString())
										);
								prizeLst.add(p);
							}
						}
						//奖品有剩余，可抽奖
						//中奖结果
						int selected = PrizeMathRandom.getPrizeIndex(prizeLst);
						Map<String,Object> rewardElementConfig = rewardElementConfigLst.get(selected);
						int elementId = Integer.parseInt(rewardElementConfig.get("elementId").toString());
						int lastLimit = Integer.parseInt(rewardElementConfig.get("lastLimit").toString());
						if(lastLimit > 0){
							lastLimit --;
							if(lastLimit == 0){//已抽完,移除此奖项
								rewardElementConfigLst.remove(selected);
								if(rewardElementConfigLst.size() == 0){
									rewardElementConfig.put("lastLimit", lastLimit);
									rewardElementConfigLst.add(rewardElementConfig);
								}
							}else{//更新缓存中奖项设置
								rewardElementConfig.put("lastLimit", lastLimit);
								rewardElementConfigLst.remove(selected);
								rewardElementConfigLst.add(rewardElementConfig);
							}
							surplusNum--;
							userMap.put("surplusNum", surplusNum);
							commonCacheService.setObject(userMap,CacheConstants.ACT_REWARD_ELEMENT, String.valueOf(userId_),DateUtil.getCurrentday());
							//更新数量后放入缓存
							commonCacheService.setObject(rewardElementConfigLst, CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_CONFIG);
							
							//返回抽奖结果
							List<Map<String,Object>> rewardElementTextLst = (List<Map<String, Object>>) rewardElementTextMap.get(String.valueOf(elementId));
							int textNum = PrizeMathRandom.getRandom(0, rewardElementTextLst.size());
							Map<String,Object> rewardElementText = rewardElementTextLst.get(textNum);
							Map<String,Object> rewardElement = (Map<String, Object>) rewardElementMap.get(String.valueOf(elementId));
							
							resultMap.put("rewardType", rewardElement.get("type"));
							resultMap.put("valueType", rewardElement.get("valueType"));
							resultMap.put("rewardContext", rewardElement.get("name"));
							resultMap.put("rewardUrl", rewardElement.get("icon"));
							resultMap.put("rewardText", rewardElementText.get("text"));
							
							rewardElement.put("userId_", userId_);
							rewardElement.put("activityId", activityId);
							rewardElement.put("phone", userInfo.get("phone"));
							rewardElement.put("source", source);
							invitedRecordMapper.saveRewardUserDetail(rewardElement);

							//TODO 异步更新数据库
//							AsyncPush push = new AsyncPush(rewardElement);
//							CalloutServices.executor(push);
						}else{//已抽完
							resultMap.put("rewardType", "0");
							resultMap.put("valueType", "0");
							resultMap.put("rewardContext", "");
							resultMap.put("rewardUrl", "");
							resultMap.put("rewardText", "本活动奖品已抽完，请期待下次活动吧。");
						}
					
					}else{//奖品已抽完
						resultMap.put("rewardType", "0");
						resultMap.put("valueType", "0");
						resultMap.put("rewardContext", "");
						resultMap.put("rewardUrl", "");
						resultMap.put("rewardText", "本活动奖品已抽完，请期待下次活动吧。");
					}
				}else{
					resultMap.put("rewardType", "0");
					resultMap.put("valueType", "0");
					resultMap.put("rewardContext", "");
					resultMap.put("rewardUrl", "");
					int orderChance = Integer.parseInt(userMap.get("orderChance").toString());
					if(orderChance==0){
						resultMap.put("rewardText", "您今日没有摸金机会，快发布需求来获得摸金机会吧。");
					}else{
						resultMap.put("rewardText", "您今日摸金机会已用完，快通过邀请好友来获得摸金机会吧。");	
					}
					
				}
			}else{
				resultMap.put("rewardType", "0");
				resultMap.put("valueType", "0");
				resultMap.put("rewardContext", "");
				resultMap.put("rewardUrl", "");
				resultMap.put("rewardText", "本活动奖品已抽完，请期待下次活动吧。");
			}
			System.out.println(resultMap);
			return resultMap;
		}catch(ApplicationContextException e){
			throw new ApplicationContextException(e.getMessage());
		}catch(Exception e){
			logger.error(e.getMessage());
			throw new ApplicationContextException("摸金异常，请与管理员联系。");
		}
	}

	
	/**
	 * 获取摸金活动锁，防止超卖
	 * @param activityId
	 * @return
	 * @throws InterruptedException
	 */
	private Boolean getTouchLock(long activityId) throws InterruptedException{
		Boolean flag = false;
		int limitCount = 0;//奖品总数
		int buyCount = 0;//已抽人数
		JedisLock lock = commonCacheService.getLock(CacheConstants.ACT_REWARD_ELEMENT_LOCK, 5*1000, 1*1000);
		try{
			if(lock.acquire()){//获取锁
				List<Map<String,Object>> rewardElementConfigLst = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.ACT_REWARD_ELEMENT,CacheConstants.ACT_REWARD_ELEMENT_CONFIG);
				if(rewardElementConfigLst == null || rewardElementConfigLst.size() == 0){
					Map<String,Object> param = new HashMap<String, Object>();
					param.put("activityId", activityId);
					rewardElementConfigLst = invitedRecordMapper.findRewardElementConfig(param);
					if(rewardElementConfigLst == null || rewardElementConfigLst.size() == 0){
						throw new ApplicationContextException("请配置摸金活动参数。");
					}
					commonCacheService.setObject(rewardElementConfigLst,CacheConstants.ACT_REWARD_ELEMENT,CacheConstants.ACT_REWARD_ELEMENT_CONFIG);
				}
				Object limitCountObj = commonCacheService.getObject(CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_LIMITCOUNT);
				if(limitCountObj == null){
					for(Map<String,Object> rewardElementConfig: rewardElementConfigLst){
						limitCount = limitCount + Integer.parseInt(rewardElementConfig.get("lastLimit").toString());
					}
				}else{
					limitCount = Integer.parseInt(limitCountObj.toString());
				}
				
				Object buyCountObj = commonCacheService.getObject(CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_BUYCOUNT);
				if(buyCountObj != null){
					buyCount = Integer.parseInt(buyCountObj.toString());
				}
				buyCount++;
				if(limitCount - buyCount >= 0){//奖品有剩余
					flag = true;
				}
//				System.out.println(limitCount +"="+ buyCount);
				commonCacheService.setObject(limitCount, CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_LIMITCOUNT);
				commonCacheService.setObject(buyCount, CacheConstants.ACT_REWARD_ELEMENT, CacheConstants.ACT_REWARD_ELEMENT_BUYCOUNT);
			}else{
				throw new ApplicationContextException("活动太火爆啦，请稍候重试。");
			}
		}catch(ApplicationContextException e){
			throw new ApplicationContextException(e.getMessage());
		}catch(Exception e){
			logger.error(e.getMessage());
			throw new ApplicationContextException("摸金异常，请与管理员联系。");
		}finally{
			commonCacheService.releaseLock(lock);
		}
		return flag;
	}

	/**
	 * 异步更新抽奖结果 线程
	 * @author Administrator
	 *
	 */
	class AsyncPush implements Runnable {
		 private Map<String, Object> paras ;
		 
		public AsyncPush(Map<String, Object> paras){
//				this.pushDao = pushDao;
				this.paras = paras;
		}
		
		@Override
		public void run() {
			try {
				invitedRecordMapper.saveRewardUserDetail(paras);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
