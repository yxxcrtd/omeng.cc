package com.shanjin.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 项目名称：core-client
 * 类名称：ICustomPriceService 
 * 类描述：自定义报价方案接口（其中包括：表单生成，方案保存，发单人/接单人查询方案列表和详情接口）
 * 创建人：Huang yulai
 * 创建时间：2016年3月17日 上午10:53:46
 * 修改人：
 * 修改时间：
 * 修改备注：
 * @version V1.0
 */
public interface ICustomPriceService {

	/**
	 * 获取报价方案空表单（表单生成）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanForm(String serviceId) throws Exception;
	
	/**
	 * 保存报价方案
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public boolean savePricePlan(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案列表查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanListForSender(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案列表查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanListForReceiver(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案详情查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanDetailForSender(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案详情查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanDetailForReceiver(Map<String, Object> param) throws Exception;
}
