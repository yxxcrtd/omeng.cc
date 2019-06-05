package com.shanjin.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface IDictionaryService {

	public JSONObject getDict(String dictType, Long parentId)throws Exception;

	public List<Map<String, Object>> getAllDict();

	/** 获取全部汽车品牌型号列表 */
	public JSONObject getUserCarBrandModel() throws Exception;
	
	/** 获取表单通用链接列表 */
	public JSONObject getFormDictList(String dictType) throws Exception;

	public String getDictsLiteral(String dictType, Object dictKeyOriginal, Map<String, String> dictMap);

	public Map<String, String> getDictMap();
	
	public JSONObject getDataVersion()throws Exception;

	int updateDataVersion(String dataType)throws Exception;
	
	public Map<String, Object> getIosPushCertFromDict(String dictType); // 获取ios推送证书信息（dictType为用户or商户证书）
	
	public JSONObject getMultistageDict(String appType,String dictType ) throws Exception;
}
