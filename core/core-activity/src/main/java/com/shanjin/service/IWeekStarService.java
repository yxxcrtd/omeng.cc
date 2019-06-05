package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

public abstract interface IWeekStarService
{
  public abstract JSONObject getWeekStar(String paramString);

  public abstract JSONObject getWeekStarDetail(String paramString1, String paramString2);

  public abstract void test();
}