package com.shanjin.incr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.dao.IValueAddedDao;
import com.shanjin.model.PersAssInfo;
import com.shanjin.service.CplanSerive;
import com.shanjin.service.IValueAddedService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 增值服务相关接口实现
 * @author Huang yulai
 *
 */
@Service("valueAddService")
public class ValueAddedServiceImpl implements IValueAddedService{
	
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(ValueAddedServiceImpl.class);
	
	@Resource
	private IValueAddedDao valueAddedDao;
	
	@Resource
	private ICommonCacheService commonCacheService;
	@Autowired
	private CplanSerive cplanSerive;
	
	@Override
	public JSONObject getServicePackageDetail(String serviceId,
			String merchantId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("serviceId", serviceId);
		paramMap.put("merchantId", merchantId);
		JSONObject jsonObject = new ResultJSONObject("000", "服务包详情查询成功");

		String serviceKey = valueAddedDao.getServiceKeyById(paramMap); //获取服务key
		paramMap.put("serviceKey", serviceKey);
		Map<String, Object> buyDetail = valueAddedDao.getPackageDetail(paramMap);
		if("merchant".equals(serviceKey)&&buyDetail!=null&&!buyDetail.isEmpty()){
			int buyCount=valueAddedDao.getMerchantBuyDetail(paramMap);
			if(buyCount>0){
				buyDetail.put("isFirst", 1);
			}else{
				buyDetail.put("isFirst", 0);
			}
		}
		
		String isBuy  = "0"; // 是否购买（0：否，1：是）
		List<Map<String, Object>> buyList = new ArrayList<Map<String, Object>>();
		if(buyDetail!=null&&!buyDetail.isEmpty()){
			isBuy  = "1";
			if("consultant".equals(serviceKey)||"vip".equals(serviceKey)){
				//私人顾问
				try {
					PersAssInfo persAssInfo = cplanSerive.getPersAssInfo(Long.valueOf(merchantId));
					if(null != persAssInfo){
						buyDetail.put("paPhone", persAssInfo.getPhone());
						buyDetail.put("paName", persAssInfo.getName());
					}
				} catch (Exception e) {
					logger.error("获取私人顾问信息",e);
					buyDetail.put("paPhone", "");
					buyDetail.put("paName", "");
				}
			}
		}else{
			buyList = valueAddedDao.getPackageListByServiceId(paramMap);
		}
		
		Map<String, Object> merchantDetail = valueAddedDao.getMerchantDetail(paramMap);
		
		jsonObject.put("buyList", buyList);
		jsonObject.put("isBuy", isBuy);
		jsonObject.put("buyDetail", buyDetail);
		jsonObject.put("merchantDetail", merchantDetail);
		
		return jsonObject;
	}
	
	@Override
	public JSONObject getValueAddServiceList(Long merchantId) {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		// 先从缓存中读取所有的增值服务列表
		List<Map<String, Object>> valueAddServiceList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.VALUEADD_SERVICE_LIST);
		if(valueAddServiceList==null){
			valueAddServiceList=valueAddedDao.getValueAddServiceList();
		}
		
		if(valueAddServiceList!=null&&valueAddServiceList.size()>0){
			List<Map<String, Object>> merchantServiceList=valueAddedDao.getMerchantServiceList(paramMap);
			Map<String, Object> merchantInfo=valueAddedDao.getMerchantInfo(paramMap);
			if(merchantInfo==null){
				merchantInfo=new HashMap<String, Object>();
				merchantInfo.put("order_surplus_price", 0);
				merchantInfo.put("max_employee_num", 0);
				merchantInfo.put("orderPushId", "");
				merchantInfo.put("emloyeeNumId", "");
			}
			for(Map<String, Object> valueAddService:valueAddServiceList){
				valueAddService.put("openStatus", 0);
				valueAddService.put("mount", 0);
				valueAddService.put("packageId", "");
				if(valueAddService.get("service_key").equals("order_push")){
					valueAddService.put("mount", merchantInfo.get("order_surplus_price"));
					valueAddService.put("packageId", merchantInfo.get("orderPushId"));
				}else if(valueAddService.get("service_key").equals("emloyee_num")){
					valueAddService.put("mount", merchantInfo.get("max_employee_num"));
					valueAddService.put("packageId", merchantInfo.get("emloyeeNumId"));
				}else{
					if(merchantServiceList!=null&&merchantServiceList.size()>0){
						for(Map<String, Object> merchantService:merchantServiceList){
							if(valueAddService.get("id").equals(merchantService.get("service_id"))){
								valueAddService.put("openStatus", 1);	
							}
						}
					}else{
						continue;
					}
				}
				
			}
			
		}
		
		jsonObject = new ResultJSONObject("000", "查询增值服务列表信息成功");
		jsonObject.put("valueAddServiceList", valueAddServiceList);
		return jsonObject;
	}

  
}
