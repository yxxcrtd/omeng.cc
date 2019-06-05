package com.shanjin.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract interface IOrderRewardDao
{
  public abstract Map<String, Object> getMerchantBaseInfo(Map<String, Object> paramMap);

  public abstract Map<String, Object> getOrderReward(Map<String, Object> paramMap);

  public abstract Long getOrderRewardListCount(Map<String, Object> paramMap);

  public abstract List<Map<String, Object>> getOrderRewardList(Map<String, Object> paramMap);

  public abstract long getOpenCityCountbyProvince(Map<String, Object> paramMap);

  public abstract long getOpenCityCountbyCity(Map<String, Object> paramMap);

  public abstract long getSurplusUserReward(Map<String, Object> paramMap);

public abstract long getSurplusMerchantReward(Map<String, Object> paramMap);

public abstract Map<String, Object> getRewardActivityInfo(Map<String, Object> paramMap);
}