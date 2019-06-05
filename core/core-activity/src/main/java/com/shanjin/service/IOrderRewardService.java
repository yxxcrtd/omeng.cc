package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

public interface IOrderRewardService {
	 public abstract JSONObject getOrderRewardByMerId(String paramString, String activityId);

	  public abstract JSONObject getOrderRewardList(String paramString, String activityId, int paramInt1, int paramInt2);
}
