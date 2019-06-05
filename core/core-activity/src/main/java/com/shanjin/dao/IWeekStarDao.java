package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public abstract interface IWeekStarDao
{
  public abstract Map<String, Object> getMerchantInfo(Map<String, Object> paramMap);

  public abstract int getMerchantCuttingNum(Map<String, Object> paramMap);

  public abstract int getMerchantCuttingCount(Map<String, Object> paramMap);

  public abstract List<Map<String, Object>> getLabelList(Map<String, Object> paramMap);

  public abstract Map<String, Object> getLabelCount(Map<String, Object> paramMap);

  public abstract int insertCuttingInfo(Map<String, Object> paramMap);

  public abstract int updateCuttingInfo(Map<String, Object> paramMap);

  public abstract int checkMerchantCutting(Map<String, Object> paramMap);

  public abstract int getMerchantCount();

  public abstract List<Map<String, Object>> getWeekStar(Map<String, Object> paramMap);

  public abstract List<Map<String, Object>> getWeekStarByName(Map<String, Object> paramMap);

  public abstract int getTotalWeekStarById(Map<String, Object> paramMap);

  public abstract int getTotalWeekStar(Map<String, Object> paramMap);

  public abstract int getLessTotalWeekStar(Map<String, Object> paramMap);

  public abstract int getMoreTotalWeekStar(Map<String, Object> paramMap);

  public abstract List<Map<String, Object>> getDetailWeekStar(Map<String, Object> paramMap);

  public abstract void updateCutting(Map<String, Object> paramMap);
}