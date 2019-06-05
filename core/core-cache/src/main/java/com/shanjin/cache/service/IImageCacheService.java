package com.shanjin.cache.service;

import java.util.Map;

/**
 * 图片缓存公共接口
 * @author Huang yulai
 *
 */
public interface IImageCacheService {
	/**
	 * 根据图片的业务类型获取图片
	 * @param type
	 * @return
	 */
	public Map<String,String> getImageMapCache(String type);
	
	/**
	 * 根据图片的业务类型和业务ID获取图片url
	 * @param type
	 * @param key
	 * @return
	 */
	public String getImageUrlCache(String type,String key);
	
	/**
	 * 根据业务类型设置图片缓存
	 * @param type
	 * @param map
	 * @return
	 */
	public Map<String,String> setImageMapCache(String type,Map<String,String> map);
	
	/**
	 * 根据业务类型删除图片缓存
	 * @param type
	 * @return
	 */
	public void deleteImageMapCache(String type);
	
	/**
	 * 根据图片的业务类型获取图片版本号
	 * @param type
	 * @return
	 */
	public int getImageVersionCache(String type);
	
	/**
	 * 根据业务类型设置图片版本缓存
	 * @param type
	 * @param map
	 * @return
	 */
	public int setImageVersionCache(String type,int version);
	
	/**
	 * 根据业务类型删除图片版本号缓存
	 * @param type
	 * @return
	 */
	public void deleteImageVersionCache(String type);
}
