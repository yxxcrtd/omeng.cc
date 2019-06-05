package com.shanjin.cache.service;

import com.alibaba.fastjson.JSONObject;

public interface IIpCityCacheService {
	
	public JSONObject getCity(String ip);
	
	
	public void cachedCity(String ip,String province,String city);
	
	
	public void cleanOldIpCache();

}
