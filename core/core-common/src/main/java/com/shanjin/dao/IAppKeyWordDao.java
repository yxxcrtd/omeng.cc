package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 应用关键字DAO
 */
public interface IAppKeyWordDao {

	// 获得应用的关键字
	public List<Map<String, Object>> getAllAppKeyWord();

	// 根据关键词搜索APP
	public Map<String, Object> serachAppType(Map<String, Object> paramMap);

	// 根据ID获得关键词
	public Map<String, Object> getAllAppKeyWordById(Map<String, Object> paramMap);

	// 检查用户搜索的关键词是否被记录过
	public int checkUserAppKeyWord(String queryString);

	// 用户端搜索没结果的时候，记录用户搜索的关键词
	public int insertUserAppKeyWord(String queryString);

	// 检查用户搜索APP名称的关键词是否存在过
	public int checkUserAppNameKeyWord(String keyword);

	// 搜索APP名称没结果的时候，记录用户搜索的关键词
	public int insertUserAppNameKeyWord(String keyword);

	// 获取关键词词库的个数
	public Integer getAppKeyWordCount();

	// 获取关键词词库
	public List<String> getAppKeyWordDict();

	// 获取自定义词库的个数
	public Integer getCustomDictCount();

	// 获取自定义词库
	public List<String> getCustomDict();

	// 获取自定义停词库的个数
	public Integer getCustomStopDictCount();

	// 获取自定义停词库
	public List<String> getCustomStopDict();

	/**
	 * 
	 * ｛用户端搜索的结果用户认为不匹配时反馈写入数据库｝
	 * 
	 * @param queryString
	 * @return
	 * @author Liuxingwen
	 * @created 2016年10月12日 上午11:24:21
	 * @lastModified
	 * @history
	 */
	public int insertUserFeedBackAppKeyWord(Map<String, Object> objMap);

	/**
	 * 
	 * ｛检查是否已存在反馈记录｝
	 * 
	 * @param queryString
	 * @return
	 * @author Liuxingwen
	 * @created 2016年10月12日 上午11:24:50
	 * @lastModified
	 * @history
	 */
	public int checkUserFeedBackAppKeyWord(Map<String, Object> objMap);

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
	
	
	public List<Map<String, Object>> selectrecommendLabel(Map<String, Object> paramMap);
}
