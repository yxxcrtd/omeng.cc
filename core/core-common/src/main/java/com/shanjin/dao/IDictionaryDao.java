package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 字典表Dao
 */
public interface IDictionaryDao {

	/** 查询一级字典项 */
	List<Map<String, Object>> getParentDict(Map<String, Object> params);

	/** 查询子字典项 */
	List<Map<String, Object>> getChildDict(Map<String, Object> params);

	/** 查询所有字典项 */
	List<Map<String, Object>> getAllDict();

	/** 根据属性查询字典项 */
	List<Map<String, Object>> getDictByValue(Map<String, Object> params);

	/** 用于查询汽车信息 */
	List<Map<String, Object>> getCarsInfo();
	
	/** 获取表单通用链接列表 */
	List<Map<String, Object>> getFormDictList(Map<String, Object> params);

	/** 用于查询汽车品牌首字母 */
	List<Map<String, Object>> getCarAlpha();

	/** 用于查询汽车品牌 */
	List<Map<String, Object>> getCarBrands(String alpha);

	/** 临时用于查询汽车品牌型号 */
	List<Map<String, Object>> getCarModel(Long parentDictId);

    /** 查询数据版本号*/
    List<Map<String, Object>> getDataVersion();

	/** 更新数据版本号*/
    int updateDataVersion(String dataType);
    
    /**多级字典*/
    List<Map<String, Object>> getMultistageDict(Map<String, Object> params);
	
}
