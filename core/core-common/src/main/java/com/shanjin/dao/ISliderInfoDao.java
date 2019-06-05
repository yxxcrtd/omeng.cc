package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface ISliderInfoDao {

	/** 获取轮播图片 */
	public List<Map<String, Object>> getSliderPics(Map<String, Object> params);
}
