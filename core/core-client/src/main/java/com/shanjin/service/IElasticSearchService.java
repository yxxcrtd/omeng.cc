package com.shanjin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface IElasticSearchService {

	/** 服务关键词搜索 */
	public JSONObject searchAppWords(String queryString, Boolean isTest,String prefix) throws Exception;

	/** 服务关键词建立索引 */
	public JSONObject createAppWordsIndex() throws Exception;

	/** 服务关键词建立索引 */
	public JSONObject delIndex(String indexName) throws Exception;

	/** 个性服务搜索 */
	public JSONObject iGxfwSearch(String queryString, Boolean isTest) throws Exception;

	/** 个性服务搜索商户ID */
	public ArrayList<Map> iGxfwSearchMerchantId(String queryString);

	/** 个性服务搜索商户ID */
	public void testSearchMerchantId(String queryString);

	/** 个性服务建立索引 */
	public JSONObject iCreateGxfwIndex() throws Exception;

	
	/** 服务商建立索引 */
	public JSONObject iCreateMerchantIndex() throws Exception;
	
	
	/** 创建索引文档 */
	public JSONObject addDocument(String ids, String indexName) throws Exception;

	/** 删除索引文档 */
	public JSONObject delDocument(String ids, String indexName) throws Exception;
	
	
	
	/** 更新商户位置索引文档 */
	public JSONObject updateDocument(String id,String name,Double latitude,Double longitude) throws Exception;
	

	/** 获取关键词词库个数 */
	public Integer getAppKeyWordDictCount();

	/** 获取关键词词库 */
	public List<String> getAppKeyWordDict();

	/** 获取自定义词库个数 */
	public Integer getCustomDictCount();

	/** 获取自定义词库 */
	public List<String> getCustomDict();

	/** 获取自定义停词库个数 */
	public Integer getCustomStopDictCount();

	/** 获取自定义停词库 */
	public List<String> getCustomStopDict();

	/** 搜索并按分组的最大得分倒排序  **/
	public JSONObject searchAppWordsByGrpScore(String queryString);
	
	
	/**
	 * 按距离远近，返回distance 内商户
	 * @param distance   范围  10m 5km ...
	 * @param latitude   经度
	 * @param longitude  纬度
	 * @param size       满足条件的记录数
	 * @return
	 */
	public  String  getMerchantIds(String distance,String latitude,String longitude,Integer size);
	
	
	
	/**
	 * 按服务项目及距离查找附近商家
	 * @param longitude  发单用户的经度
	 * @param latitude   发单用户的纬度
	 * @param searchRange 搜素范围
	 * @param serviceTypeId 服务项目id
	 * @param limit		 结果集尺寸
	 * @return
	 */
	public List<Map<String,Object>> getUserServiceTypeIdNearBy(double longitude, double latitude, double searchRange,String serviceTypeId,Integer limit,List<Map<String,Object>> allVIPMerchantList);
	
	
	
	
	public JSONObject getJsonUserServiceTypeIdNearBy(double longitude, double latitude, double searchRange,String serviceTypeId,Integer limit);

	public JSONObject test_search(String queryString, Boolean isTest,String prefix) throws Exception;
	
	

	/**
	 * 
	 * ｛用户端搜索的结果用户认为不匹配时，如果已记录，则更新反馈写入数据库｝
	 * 
	 * @param queryString
	 * @return
	 * @author Liuxingwen
	 * @created 2016年10月12日 上午11:25:04
	 * @lastModified
	 * @history
	 */
	public int updateUserFeedBackAppKeyWord(Map<String, Object> objMap);
	
	/**
	 * 
	 *  ｛首页推荐文案｝
	 *  @param objMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年10月18日 下午4:20:18
	 *  @lastModified       
	 *  @history
	 */
	public JSONObject getrecommendLabel(Map<String, Object> objMap) throws Exception;
	
}
