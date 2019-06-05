package com.shanjin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IOrderRewardDao;
import com.shanjin.service.IOrderRewardService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("orderRewardService")
public class OrderRewardServiceImpl implements IOrderRewardService{
	
	  @Resource
	  private ICommonCacheService commonCacheService;

	  @Resource
	  private IOrderRewardDao orderRewardDao;

	  public JSONObject getOrderRewardByMerId(String merchantId,String activityId )
	  {
	    JSONObject jsonObject = null;
	    String dayKey = DateUtil.formatDate("yyyyMMdd", new Date());
	    boolean flag = getCurrentTime();
	    Map<String, Object> paramMap = new HashMap<String, Object>();
	    paramMap.put("activityId", activityId);
	    
	    
	    Map<String, Object> activityInfo=this.orderRewardDao.getRewardActivityInfo(paramMap);
	    
	    if(merchantId==null){
	    	String surplusTotal=(String) this.commonCacheService.getObject(CacheConstants.ORDER_REWARD_SURPLUS, dayKey,activityId);
	        int suCount=0;
	    	if(surplusTotal==null){
	          	long count=this.orderRewardDao.getSurplusUserReward(paramMap);
	          	suCount=(int) (50000-count>0 ? 50000-count:0);
	        }else{
	        	suCount=Integer.parseInt(surplusTotal);
	        }
	    	if (flag) {
		        this.commonCacheService.setObject(String.valueOf(suCount),CacheConstants.MERCHANT_FANS_TIMEOUT, CacheConstants.ORDER_REWARD_SURPLUS, dayKey, activityId);
		      }
	    	jsonObject = new ResultJSONObject("000", "获取用户奖励信息成功");
	    	jsonObject.put("surplusTotal", suCount);
	 	    jsonObject.put("activityInfo", activityInfo);
	 	    return jsonObject;
	    }
	    
	    String surplusTotal=(String) this.commonCacheService.getObject(CacheConstants.ORDER_REWARD_SURPLUS_MER, dayKey,activityId);
        int suCount=0;
    	if(surplusTotal==null){
          	long count=this.orderRewardDao.getSurplusMerchantReward(paramMap);
          	suCount=(int) (50000-count>0 ? 50000-count:0);
        }else{
        	suCount=Integer.parseInt(surplusTotal);
        }
	    
	    paramMap.put("merchantId", merchantId);

	    Map<String, Object> merchantInfo = this.orderRewardDao.getMerchantBaseInfo(paramMap);

	    if ((merchantInfo != null) && (!merchantInfo.isEmpty())) {
	      String icon = StringUtil.null2Str(merchantInfo.get("icon"));
	      if (StringUtil.isNullStr(icon)) {
	        icon = Constant.DEFAULT_USER_PORTRAIT_PTAH;
	      }
	      icon = BusinessUtil.disposeImagePath(icon);
	      merchantInfo.put("icon", icon);
	    }

	    Map<String, Object> rewardInfo = (Map<String, Object>)this.commonCacheService.getObject(CacheConstants.ORDER_REWARD+dayKey,activityId, merchantId);

	    if (rewardInfo == null) {
	      rewardInfo = this.orderRewardDao.getOrderReward(paramMap);

	      long count = 0L;
	      paramMap.put("province", merchantInfo.get("province").toString());
	      paramMap.put("city", merchantInfo.get("city").toString());
	      if (StringUtil.matchProvince(merchantInfo.get("province").toString()))
	        count = this.orderRewardDao.getOpenCityCountbyProvince(paramMap);
	      else {
	        count = this.orderRewardDao.getOpenCityCountbyCity(paramMap);
	      }
	      long status = (count > 0L ? 1 : 0) == 1 ? 1L : 0L;
	      
	      rewardInfo.put("status", Long.valueOf(status));
	      if (flag) {
	        this.commonCacheService.setObject(rewardInfo,CacheConstants.MERCHANT_FANS_TIMEOUT, CacheConstants.ORDER_REWARD+dayKey, activityId,merchantId);
	      }
	    }

	    jsonObject = new ResultJSONObject("000", "获取商铺订单奖励信息成功");
	    jsonObject.put("activityInfo", activityInfo);
	    jsonObject.put("merchantInfo", merchantInfo);
	    jsonObject.put("rewardInfo", rewardInfo);
	    jsonObject.put("surplusTotal", suCount);
	    return jsonObject;
	  }

	  public JSONObject getOrderRewardList(String merchantId,String activityId, int pageIndex, int pageSize)
	  {
	    JSONObject jsonObject = null;
	    String dayKey = DateUtil.formatDate("yyyyMMdd", new Date());
	    boolean flag = getCurrentTime();
	    Map<String, Object> paramMap = new HashMap<String, Object>();
	    paramMap.put("merchantId", merchantId);
	    paramMap.put("activityId", activityId);
	    if (pageSize == 0) {
	      pageSize = 10;
	    }
	    paramMap.put("pageIndex", Integer.valueOf(pageIndex * pageSize));
	    paramMap.put("pageSize", Integer.valueOf(pageSize));
	    Map rewardInfo = (Map)this.commonCacheService.getObject(CacheConstants.ORDER_REWARD+ dayKey,activityId,merchantId );

	    if (rewardInfo == null) {
	      rewardInfo = this.orderRewardDao.getOrderReward(paramMap);
	    }
	    List<Map<String, Object>> orderList = null;
	    if ((pageIndex == 0) && (pageSize == 10)) {
	      orderList = (List<Map<String, Object>>)this.commonCacheService.getObject(CacheConstants.ORDER_REWARD_LIST+dayKey,activityId,merchantId);
	    }

	    if (orderList == null) {
	      orderList = this.orderRewardDao.getOrderRewardList(paramMap);
	      if ((orderList != null) && (orderList.size() > 0)) {
	        for (Map<String, Object> order : orderList) {
	          String icon = StringUtil.null2Str(order.get("icon"));
	          if (StringUtil.isNullStr(icon)) {
	            icon = Constant.DEFAULT_USER_PORTRAIT_PTAH;
	          }
	          icon = BusinessUtil.disposeImagePath(icon);
	          order.put("icon", icon);
	        }
	      }

	      if ((flag) && (pageIndex == 0) && (pageSize == 10)) {
	        this.commonCacheService.setObject(orderList,CacheConstants.MERCHANT_FANS_TIMEOUT,  CacheConstants.ORDER_REWARD_LIST+dayKey, activityId,merchantId );
	      }
	    }

	    jsonObject = new ResultJSONObject("000", "获取商铺订单奖励列表成功");
	    jsonObject.put("rewardInfo", rewardInfo);
	    jsonObject.put("orderList", orderList);

	    return jsonObject;
	  }

	  private boolean getCurrentTime() {
	    boolean flag = false;
	    Date d = new Date();

	    int hour = d.getHours();
	    if (hour > 5) {
	      flag = true;
	    }
	    return flag;
	  }
}
