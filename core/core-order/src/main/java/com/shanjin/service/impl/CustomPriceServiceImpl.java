package com.shanjin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.ICustomPriceDao;
import com.shanjin.service.ICustomPriceService;
@Service("customPriceService")
public class CustomPriceServiceImpl implements ICustomPriceService{

	@Resource
	private ICommonCacheService commonCacheService;
	
	@Resource
	private ICustomPriceDao customPriceDao;
	
	@Override
	public JSONObject getPricePlanForm(String serviceId)throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "获得自定义报价表单成功");
		//String version = (String) commonCacheService.getObject(CacheConstants.PLAN_FORM_VERSION, serviceId);
		List<Map<String, Object>> planForm = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.PLAN_FORM, serviceId);
		if(planForm==null||planForm.isEmpty()){
			// 方案表单版本缓存不存在
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("serviceId", StringUtil.nullToLong(serviceId));
		//	version = customPriceDao.getPlanFormVersion(param);
			planForm = customPriceDao.getPricePlanForm(param);
			if(planForm!=null&&planForm.size()>0){
				for(Map<String, Object> map : planForm){
					BusinessUtil.disposePath(map, "icon");
//					String colItems = StringUtil.null2Str(map.get("colItems"));
//					String[] items = new String[0];
//					if (StringUtil.isNotEmpty(colItems)) {
//						items = colItems.split(",");
//					} 
//					map.put("colItems", items);
				}
				commonCacheService.setObject(planForm,CacheConstants.PLAN_FORM,  serviceId);
			}

		}
		//jsonObject.put("version", version);
		jsonObject.put("planForm", planForm);
		return jsonObject;
	}

	@Override
	public boolean savePricePlan(Map<String, Object> param) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject getPricePlanListForSender(Map<String, Object> param)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getPricePlanListForReceiver(Map<String, Object> param)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getPricePlanDetailForSender(Map<String, Object> param)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getPricePlanDetailForReceiver(Map<String, Object> param)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
