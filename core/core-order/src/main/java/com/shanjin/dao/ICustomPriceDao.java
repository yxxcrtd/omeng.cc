package com.shanjin.dao;

import java.util.List;
import java.util.Map;


/**
 * 
 * 项目名称：core-order
 * 类名称：ICustomPriceDao 
 * 类描述：自定义报价方案相关操作（其中包括：表单生成，方案保存，发单人/接单人查询方案列表和详情接口）
 * 创建人：Huang yulai
 * 创建时间：2016年3月17日 上午10:41:54
 * 修改人：
 * 修改时间：
 * 修改备注：
 * @version V1.0
 */
public interface ICustomPriceDao {

	/**
	 * 获取报价方案空表单版本
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getPlanFormVersion(Map<String, Object> param) throws Exception;
	
	/**
	 * 获取报价方案空表单（表单生成）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getPricePlanForm(Map<String, Object> param) throws Exception;
	
	/**
	 * 保存报价方案
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int savePricePlan(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案列表查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getPricePlanListForSender(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案列表查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getPricePlanListForReceiver(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案详情查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getPricePlanDetailForSender(Map<String, Object> param) throws Exception;
	
	/**
	 * 报价方案详情查询（接单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getPricePlanDetailForReceiver(Map<String, Object> param) throws Exception;
}
