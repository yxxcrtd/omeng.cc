package com.shanjin.service.api.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.shanjin.common.util.DateUtil;
import com.shanjin.service.api.IOrderApiService;

@Service("orderApiService")
public class BaseOrderApiServiceImpl implements IOrderApiService {

	@Override
	public int saveAppOrder(Map<String, Object> orderInfo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Object> selectAppOrderText(Map<String, Object> paramMap)throws Exception {
		return this.selectOrderText(paramMap);
	}

	@Override
	public Map<String, Object> selectOrderText(Map<String, Object> paramMap)throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getMerchantClientIds(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}
	/** 获取经纬度*/
	public Map<String, Object> selectOrderLocation(Map<String, Object> paramMap){
		return null;
	}

	/** 根据订单ID查询省市信息-在云端*/
	public Map<String, Object> getProvinceAndCityByorderId(Long orderId){
		return null;
	}
	
	/**
	 * 从请求串中服务时间模板方法----子类根据需要改写
	 * @return
	 */
	public String  extractServiceTime( Map<String, Object> requestInfo) {
		 return (String) requestInfo.get("serviceTime");
	}
	
	/**
	 * 格式化显示时间---这里显示默认的格式 --01-12 20:39 ---子类可根据需要改写      
	 * @return
	 */
	public String  formatServiceTime(Date serviceTime,String appType,String serviceType) {
		 return DateUtil.formatDate(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN, serviceTime);
	}
}
