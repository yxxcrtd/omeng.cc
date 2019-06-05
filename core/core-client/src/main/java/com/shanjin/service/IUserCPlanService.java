package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;


public interface IUserCPlanService {
    
	/**
	 * C计划注册用户
	 * @param phone
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public JSONObject registerFromCPlan(String phone,String ip)throws Exception;
}
