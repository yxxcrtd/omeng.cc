package com.shanjin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IDictionaryDao;
import com.shanjin.service.IDictionaryService;

@Service("dictionaryService")
@Transactional(rollbackFor = Exception.class)
public class DictionaryServiceImpl implements IDictionaryService {
	
	private static final Logger logger = Logger.getLogger(DictionaryServiceImpl.class);

	@Resource
	private IDictionaryDao dictionaryDao;
	@Resource
	private ICommonCacheService commonCacheService;

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getDict(String dictType, Long parentId) throws Exception{
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentId", parentId);
		paramMap.put("dictType", dictType);

		List<Map<String, Object>> results = null;
		LinkedHashMap<Long, Map<String, Object>> dicts =null;
		// ******先读取缓存数据*****
		dicts = (LinkedHashMap<Long, Map<String, Object>>) commonCacheService.getObject(CacheConstants.DICT_LIST_KEY, dictType, StringUtil.null2Str(parentId));
		if (dicts == null || dicts.size() < 1) {
			dicts= new LinkedHashMap<Long, Map<String, Object>>();
			// 读db
			if (parentId == null) {
				results = dictionaryDao.getParentDict(paramMap);
			} else {
				results = dictionaryDao.getChildDict(paramMap);
			}
	
			if (results != null) {
				for (Map<String, Object> result : results) {
					Long id = (Long) result.get("id");
					String dictKey = (String) result.get("dictKey");
					String dictValue = (String) result.get("dictValue");
					Integer isLeaves = (Integer) result.get("isLeaves");
					Integer attachmentType = (Integer) result.get("attachmentType");
					String attachmentStyle = (String) result.get("attachmentStyle");
					String path = (String) result.get("path");
	
					Map<String, Object> values = dicts.get(id);
					if (values == null) {
						values = new HashMap<String, Object>();
						values.put("id", id);
						values.put("dictKey", dictKey);
						values.put("dictValue", dictValue);
						values.put("isLeaves", isLeaves);
						dicts.put(id, values);
					}
					if (attachmentType != null && attachmentStyle != null && path != null) {
						values.put("attachmentType", attachmentType);
						values.put(attachmentStyle, BusinessUtil.disposeImagePath(path));
					}
				}
			}
			commonCacheService.setObject(dicts, CacheConstants.DICT_LIST_KEY, dictType, StringUtil.null2Str(parentId));

		}
		jsonObject = new ResultJSONObject("000", "获得字典列表成功");
		jsonObject.put("dicts", dicts.values());

		return jsonObject;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getMultistageDict(String appType,String dictType ) throws Exception{
		JSONObject jsonObject = new ResultJSONObject();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentDictId", null);
		paramMap.put("dictType", dictType);
		
		List<Map<String, Object>> dictList=null;
		
		dictList=(List<Map<String, Object>>)commonCacheService.getObject(CacheConstants.MULTISTAGE_DICT_LIST_KEY, appType,dictType);
		if(dictList==null || dictList.size()==0){
			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
			getMultistageDict_(results,dictType,null);
			
			if(results!=null){
				dictList=BusinessUtil.handlerMultistageDict(results);
			}
			commonCacheService.setObject(dictList, CacheConstants.MULTISTAGE_DICT_LIST_KEY, appType,dictType);
		}
		jsonObject.put("dictList", dictList);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "多级字典加载成功");
		
	return jsonObject;
	}
	/**
	 * 多级字典查询
	 */
	private List<Map<String, Object>> getMultistageDict_(List<Map<String, Object>> results,String dictType,String parentDictId){

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentDictId", parentDictId);
		paramMap.put("dictType",dictType);
		List<Map<String, Object>> results_=dictionaryDao.getMultistageDict(paramMap);
		for(Map<String, Object> map:results_){
			if(map!=null){
				results.add(map);
				String parentDictId_=map.get("id").toString();
				getMultistageDict_(results,null,parentDictId_);
			}
		}
		return results;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getAllDict() {
		List<Map<String, Object>> results = null;
		// ******先读取缓存数据*****
		results = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.DICT_LIST_KEY);
		if (results == null || results.size() < 1) {
			results = dictionaryDao.getAllDict();
			commonCacheService.setObject(results, CacheConstants.DICT_LIST_KEY);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getDictMap() {
		Map<String, String> dictMap = (Map<String, String>) commonCacheService.getObject(CacheConstants.DICT_MAP_KEY);
		if (dictMap == null) {
			dictMap = new HashMap<String, String>();
			List<Map<String, Object>> dicts = this.getAllDict();
			for (Map<String, Object> dictItem : dicts) {
				String dictType = (String) dictItem.get("dictType");
				String dictKey = (String) dictItem.get("dictKey");
				String dictValue = (String) dictItem.get("dictValue");
				StringBuilder finalKey = new StringBuilder();
				finalKey.append(dictType).append("-->").append(dictKey);
				dictMap.put(finalKey.toString(), dictValue);
			}
			commonCacheService.setObject(dictMap, CacheConstants.DICT_MAP_KEY);
		}
		return dictMap;
	}

	@Override
	public String getDictsLiteral(String dictType, Object dictKeyOriginal, Map<String, String> dictMap) {
		if (dictKeyOriginal == null || "".equals(dictKeyOriginal.toString())) {
			return "";
		}
		String[] dictKeyArray = dictKeyOriginal.toString().split(",");
		StringBuilder dictsLiteral = new StringBuilder();
		for (String dictKey : dictKeyArray) {
			dictsLiteral.append(",").append(dictMap.get(dictType + "-->" + dictKey));
		}

		String result=dictsLiteral.toString();
		if(result.contains(",null")){
			result=result.replace(",null", "");
		}
		if (result.length() > 0) {
			return result.substring(1);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getUserCarBrandModel() throws Exception {
		JSONObject jsonObject = null;
		List<Map<String, Object>> carList = null;
		List<Map<String, Object>> carsInfo = null;
		// ******先读取缓存数据*****
		carList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.USER_CAR_BRAND_MODEL_KEY);
		if (carList == null || carList.size() < 1) {
			carList = new ArrayList<Map<String, Object>>();
			carsInfo = dictionaryDao.getCarsInfo();
			if (carsInfo != null) {
				List<String> alphaList = new ArrayList<String>();
				for (Map<String, Object> carInfo : carsInfo) {
					if (carInfo.get("remark") != null && carInfo.get("dictType").equals("carBrand") && !(carInfo.get("remark").equals(""))) {
						if (!alphaList.contains(String.valueOf(carInfo.get("remark")))) {
							alphaList.add(String.valueOf(carInfo.get("remark")));
						}
					}
				}
				for (String alpha : alphaList) {
					Map<String, Object> carBrand = new HashMap<String, Object>();
					carBrand.put("alpha", alpha);
					List<Map<String, Object>> typeInfo = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> brand : carsInfo) {
						if (brand.get("remark") != null && String.valueOf(brand.get("dictType")).equals("carBrand") && String.valueOf(brand.get("remark")).equals(alpha)) {
							Map<String, Object> brandInfo = new HashMap<String, Object>();
							brandInfo.put("title", brand.get("dictValue"));
							List<Map<String, Object>> models = new ArrayList<Map<String, Object>>();
							for (Map<String, Object> model : carsInfo) {
								if (model.get("parentDictId") != null && String.valueOf(model.get("dictType")).equals("carModel") && String.valueOf(model.get("parentDictId")).equals(String.valueOf(brand.get("id")))) {
									Map<String, Object> modeInfo = new HashMap<String, Object>();
									modeInfo.put("name", model.get("dictValue"));
									models.add(modeInfo);
								}
							}
							brandInfo.put("detail", models);
							typeInfo.add(brandInfo);
						}
					}
					carBrand.put("types", typeInfo);
					carList.add(carBrand);
				}
			}
			commonCacheService.setObject(carList, CacheConstants.USER_CAR_BRAND_MODEL_KEY);		
		}
		jsonObject = new ResultJSONObject("000", "车辆信息加载成功");
		jsonObject.put("typesAlpha", carList);
		return jsonObject;
	}

	@Override
	public JSONObject getDataVersion()throws Exception {

		List<Map<String, Object>> dataVersions = this.dictionaryDao.getDataVersion();
		Map<String, Object> result = new HashMap<String, Object>();
		if (dataVersions != null) {
			for (Map<String, Object> dataVersion : dataVersions) {
				result.put(dataVersion.get("dataType").toString(), dataVersion.get("version"));
			}
		}
		JSONObject jsonObject = new ResultJSONObject("000", "获得数据版本成功");
		jsonObject.put("dataVersions", result);
		return jsonObject;
	}

	@Override
	public int updateDataVersion(String dataType) throws Exception{
		return this.dictionaryDao.updateDataVersion(dataType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getIosPushCertFromDict(String dictType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dictType", dictType);
		List<Map<String, Object>> results = null;
		Map<String, Object> cert = new HashMap<String, Object>();
		// ******先读取缓存数据*****
		results = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.DICT_LIST_KEY, dictType);
		if (results == null || results.size() < 1) {
			// 读db
		    results = dictionaryDao.getParentDict(paramMap);
			commonCacheService.setObject(results, CacheConstants.DICT_LIST_KEY, dictType);
		}
		if(results!=null&&results.size()>0){
			for(Map<String, Object> map : results){
				cert.put(StringUtil.null2Str(map.get("dictKey")), map.get("dictValue"));
			}
		}
		return cert;
	}
	
	public static void main(String[] args){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", 1);
		map.put("title", "A");
		map.put("parentId", null);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("id", 2);
		map.put("title", "奥迪");
		map.put("parentId", 1);
		list.add(map);	
		map = new HashMap<String,Object>();
		map.put("id", 3);
		map.put("title", "阿斯顿马丁");
		map.put("parentId", 1);
		list.add(map);	
		map = new HashMap<String,Object>();
		map.put("id", 4);
		map.put("title", "奥迪A4L");
		map.put("parentId", 2);
		list.add(map);		
		map = new HashMap<String,Object>();
		map.put("id", 5);
		map.put("title", "奥迪A6L");
		map.put("parentId", 2);
		list.add(map);	
		map = new HashMap<String,Object>();
		map.put("id", 6);
		map.put("title", "奥迪A8L");
		map.put("parentId", 2);
		list.add(map);	
		map = new HashMap<String,Object>();
		map.put("id", 7);
		map.put("title", "阿斯顿马丁Vanquish");
		map.put("parentId", 3);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("id", 8);
		map.put("title", "阿斯顿马丁DBS");
		map.put("parentId", 3);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("id", 9);
		map.put("title", "阿斯顿马丁Virage");
		map.put("parentId", 3);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("id", 10);
		map.put("title", "阿斯顿马丁Zagato");
		map.put("parentId", 3);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("id", 11);
		map.put("title", "阿斯顿马丁Rapide");
		map.put("parentId", 3);
		list.add(map);
		System.out.println("getChildList=="+getChildList("1",list));
	}
	
	private static List<Map<String, Object>> getChildList(String dictId,List<Map<String, Object>> dictList){
		List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
		if(dictList!=null&&dictList.size()>0){
			for(Map<String, Object> dict : dictList){
				if(dictId.equals(StringUtil.null2Str(dict.get("parentId")))){
					// 有孩子
					Map<String,Object> map = new HashMap<String,Object>();
					List<Map<String, Object>> children = getChildList(StringUtil.null2Str(dict.get("id")),dictList);
					map.put("title",  StringUtil.null2Str(dict.get("title")));
					String image = StringUtil.null2Str(dict.get("image"));
					if(!StringUtil.isNullStr(image)){
						image = BusinessUtil.disposeImagePath(image);
					}
					map.put("image",image);
					map.put("childList",  children);
					childList.add(map);
				}
			}
		}
		return childList;
	}

	@Override
	public JSONObject getFormDictList(String dictType) throws Exception {
		Map<String,Object> paras = new HashMap<String,Object>();
		paras.put("dictType", dictType);
		JSONObject jsonObject = null;
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> dictList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
		// ******先读取缓存数据*****
		resultList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.DICT_FORM_KEY,dictType);
		if (resultList == null || resultList.size() < 1) {
			resultList = new ArrayList<Map<String, Object>>();
			dictList = dictionaryDao.getFormDictList(paras);
			if (dictList != null&&dictList.size()>0) {
				// 查找第一级（parentId为空的）
				for (Map<String, Object> dict : dictList) {
					String parentId = StringUtil.null2Str(dict.get("parentId"));
					if(StringUtil.isNullStr(parentId)){
						// 第一级
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("title",  StringUtil.null2Str(dict.get("title")));
						String image = StringUtil.null2Str(dict.get("image"));
						if(!StringUtil.isNullStr(image)){
							image = BusinessUtil.disposeImagePath(image);
						}
						map.put("image",image);
						// 递归子项
						childList = getChildList(StringUtil.null2Str(dict.get("id")),dictList);
						map.put("childList", childList);
						resultList.add(map);
					}
				}
			}
			commonCacheService.setObject(resultList, CacheConstants.DICT_FORM_KEY,dictType);		
		}
		jsonObject = new ResultJSONObject("000", "获取表单通用链接成功");
		jsonObject.put("resultList", resultList);
		return jsonObject;
	}
}
