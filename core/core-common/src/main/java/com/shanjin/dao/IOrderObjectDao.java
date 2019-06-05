package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IOrderObjectDao {

	/** 获得服务类型的参数 */
	List<Map<String, Object>> getServiceParas(Map<String, Object> paramMap);

	/** 获得子集服务类型的参数 */
	List<Map<String, Object>> getServiceSubParas(Long parentId);

	/** 获得关联服务类型的参数 */
	List<Map<String, Object>> getServiceCascadeParas(Long modelId);

	/** 获得关联控件的信息 */
	List<Map<String, Object>> getServiceCascadeItems(Long modelId);

}
