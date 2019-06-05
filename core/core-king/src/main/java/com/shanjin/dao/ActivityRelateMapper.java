
package com.shanjin.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-10-29 10:56:33 中国标准时间
 * @version	v0.1
 * @desc    活动相关DAO
 */
public interface ActivityRelateMapper {

	/**
	 * 获取所有服务包列表 要排序
	 * @return
     */
	List<Map<String,Object>> getAllPkgList();

	Map<String,Object> getActivityInfo(@Param("detailTable") String detailTable);


	/**
	 * 查询王牌活动服务包详情
	 * @param pkgId
	 * @return
     */
	Map<String,Object> getPkgDetail(@Param("pkgId") Long pkgId);


	Map<String,Object> getActivityShare(@Param("actId") Long actId);

}