package com.shanjin.cache.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.shanjin.cache.service.IImageCacheService;

@Service("imageCacheService")
public class ImageCacheServiceImpl  extends GenericCacheServiceImpl implements IImageCacheService{

	@Override
	public Map<String, String> getImageMapCache(String type) {
		return (Map<String, String>) get(type);
	}

	@Override
	public String getImageUrlCache(String type, String key) {
		String v = "";
		Map<String, String> map = getImageMapCache(type); 
		if(map!=null){
			v = map.get(key);
		}
		return v;
	}

	@Override
	public Map<String, String> setImageMapCache(String type,
			Map<String, String> map) {
		set(type,map);
		return getImageMapCache(type);
	}

	@Override
	public void deleteImageMapCache(String type) {
		del(type);
	}

	@Override
	public int getImageVersionCache(String type) {
		Object o = get(type);
		int version = 0;
		if(o!=null)
			version = (int) o;
		return  version;
	}

	@Override
	public int setImageVersionCache(String type, int version) {
		set(type,version);
		return getImageVersionCache(type);
	}

	@Override
	public void deleteImageVersionCache(String type) {
		del(type);
	}

}
