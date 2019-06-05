package com.shanjin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IWeekStarDao;
import com.shanjin.service.IWeekStarService;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("weekStarService")
public class WeekStarServiceImpl
  implements IWeekStarService
{

  @Resource
  private IWeekStarDao weekStarDao;

  @Resource
  private ICommonCacheService commonCacheService;

  @Override
	public JSONObject getWeekStar(String name) {
		JSONObject jsonObject = null;
		
		String dayKey=DateUtil.formatDate("yyyyMMdd",new Date());
		String yesterday=DateUtil.getYesterday();
		String currentDay=DateUtil.getCurrentday();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", name);
		paramMap.put("yesterday", yesterday);
		paramMap.put("currentDay", currentDay);
		List<Map<String, Object>> results = null;
		
		Date date= (Date) commonCacheService.getObject(CacheConstants.TIME_SAMP_KEY);
		if(date==null){
		    commonCacheService.setObject(new Date(), CacheConstants.TIME_SAMP_KEY);  
		}else if(!DateUtil.lessThanTime(date)){
		    commonCacheService.deleteObject(CacheConstants.WEEK_STAR);
		    commonCacheService.setObject(new Date(), CacheConstants.TIME_SAMP_KEY); 
		}
		
		if(StringUtil.isNullStr(name)){
			results = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.WEEK_STAR,dayKey);
			if (results == null || results.size() < 1) {
				// 读取db数据
				results = weekStarDao.getWeekStar(paramMap);
				addRanking(results,paramMap,1);
				commonCacheService.setObject(results,CacheConstants.WEEK_STAR_EXPIRTIME, CacheConstants.WEEK_STAR,dayKey);
			}
		}else{
			results = weekStarDao.getWeekStarByName(paramMap);
			cacelInvalid(results);
			addRanking(results,paramMap,2);
		}
		
		addPortrait(results);
		
		jsonObject = new ResultJSONObject("000", "获得每周之星成功");
		jsonObject.put("weekStarList", results);
		return jsonObject;
	}

	private void addRanking(List<Map<String, Object>> results, Map<String, Object> paramMap, int type) {
	    int auth_total=0;
		if (results != null && results.size() > 0) {
			if (type == 2) {
				for (Map<String, Object> ma : results) {
					auth_total = Integer.parseInt(ma.get("auth_total")
							.toString());
					paramMap.put("auth_total", auth_total);
					int moretotal = weekStarDao.getMoreTotalWeekStar(paramMap);
					ma.put("ranking", moretotal + 1);
				}
			} else {
				int i = 1;
				for (Map<String, Object> ma : results) {
					ma.put("ranking", i);
					i++;
				}
			}
		}
		
	}

	private void addPortrait(List<Map<String, Object>> results) {
		int[] rand=StringUtil.getRandamArry();
		int i=0;
		if(results!=null&&results.size()>0){
			for(Map<String, Object> ma:results){
				ma.put("portrait", rand[i]);
				i++;
				if(i==6){
					i=0;
				}
			}
		}
		
	}

	private void cacelInvalid(List<Map<String, Object>> results) {
		if(results!=null&&results.size()>0){
			for(int i=results.size()-1;i>=0;i--){
				int auth_total= (Integer.parseInt(results.get(i).get("auth_total").toString()));
				if(auth_total<18){
					results.remove(i);
				}
			}
		}
		
	}

	@Override
	public JSONObject getWeekStarDetail(String employeeId,String authTotal) {
		JSONObject jsonObject = null;
		String yesterday=DateUtil.getYesterday();
		String currentDay=DateUtil.getCurrentday();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("employeeId", Integer.parseInt(employeeId));
		paramMap.put("yesterday", yesterday);
		paramMap.put("currentDay", currentDay);
		int total=0;
		int sumtotal=0;
		int lesstotal=0;
		
		total=weekStarDao.getTotalWeekStarById(paramMap);
		
		paramMap.put("total", total);
		sumtotal=weekStarDao.getTotalWeekStar(paramMap);
		lesstotal=weekStarDao.getLessTotalWeekStar(paramMap);
		
		 float fsumtotal=sumtotal;
		 float flesstotal=lesstotal;
		 String result="";//接受百分比的值  
		 double tempresult=flesstotal/fsumtotal;  
		 DecimalFormat df1 = new DecimalFormat("0%");    //##.00%   百分比格式，后面不足2位的用0补齐  
		 result= df1.format(tempresult);   
		 
		 jsonObject = new ResultJSONObject("000", "获得每周之星详情成功");
		 
		 List<Map<String, Object>> resultDetail = null;
		 resultDetail=weekStarDao.getDetailWeekStar(paramMap);
		 if(resultDetail != null || resultDetail.size() > 0){
		     jsonObject.put("name", resultDetail.get(0).get("name"));
		     jsonObject.put("cityName", resultDetail.get(0).get("cityName"));
		     jsonObject.put("provinceName", resultDetail.get(0).get("provinceName"));
		 }
		         
		
		 jsonObject.put("authTotal", authTotal);
		 jsonObject.put("percentage", result);
		 jsonObject.put("date", DateUtil.getYesterdayChina());
		 
		return jsonObject;
	}
	
	@Override
  public void test(){
  Map<String, Object> paramMap = new HashMap<String, Object>();
  paramMap.put("label", "label1");
  long mer=1L;
  paramMap.put("merchantId", mer);
  weekStarDao.updateCutting(paramMap);
}
}