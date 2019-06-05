package com.shanjin.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 项目名称：core-client
 * 类名称：IMongoDbCustomOrderService 
 * 类描述：订单相关的历史数据查询
 * 创建人： Revoke Yu
 * 创建时间：2016年10月8日
 * 修改人：
 * 修改时间：
 * 修改备注：
 * @version V1.0
 */
public interface IMongoCustomOrderService {
	/**
	 * 订单列表查询（发单用户视图）
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderListForSender(Map<String, Object> params) throws Exception;
	
	
	/**
	 * 订单详情查询（发单用户视图）    Revoke Yu
	 * @param orderId	        订单的id
	 * @param userId       发单用户的ID
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOrderDetailForSender(String orderId,Long userId) throws Exception;
	
		
	/**
	 * 发单用户查看报价方案列表
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanList(Map<String, Object> params) throws Exception;
	
	
	/**
	 * 报价方案详情查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPricePlanDetail(Map<String, Object> param) throws Exception;
	
	
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

	
	/**
	 * 查询服务记录
	 * @param merchnatId
	 * @param orderId
	 * @param userId
	 * @return
	 */
	public JSONObject getMerchantServiceRecord(Long merchantId,Long orderId,Long userId)throws Exception;
	
	
	/**
	 * 获取时间轴
	 * @param orderId
	 * @param orderStatus
	 * @param type 1用户，2 商户
	 * @return
	 * @throws Exception
	 */
	public JSONObject getTimeline(Long merchantId,Long orderId,int orderStatus,int type) throws Exception;


	/**
	 * 判断是否存在用户侧历史订单
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Boolean hasUserHistoryOrder(Map<String, Object> params) throws Exception;


    /**
     * 订单列表查询（接单用户视图）
     *
     * @param params
     * @return
     * @throws Exception
     */
    JSONObject getOrderListForReceiver(Map<String, Object> params) throws Exception;


    /**
     * 订单详情查询（接单用户视图）
     *
     * @param params
     * @return
     * @throws Exception
     */
    JSONObject getOrderDetailForReceiver(Map<String, Object> params) throws Exception;

}
