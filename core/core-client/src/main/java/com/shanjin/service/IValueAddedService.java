package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 增值服务相关接口
 * @author Huang yulai
 *
 */
public interface IValueAddedService {
	
	/**
	 * 获取服务包详情信息
	 * @param serviceId
	 * @param merchantId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getServicePackageDetail(String serviceId, String merchantId) throws Exception;
	
	/**
	 * 获取增值服务列表信息
	 * @param serviceId
	 * @param merchantId
	 * @return
	 * @throws Exception
	 */
	JSONObject getValueAddServiceList(Long merchantId);

}
