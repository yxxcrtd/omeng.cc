/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.service.impl <br>
 * FileName: QueryBuildersUtils.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年8月6日
 * @last Modified 
 * @history
 */
package com.shanjin.service.impl;

import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * {该处说明该构造函数的含义及作用}
 * 
 * @author Liuxingwen
 * @created 2016年8月6日 上午10:26:22
 * @lastModified
 * @history
 */
public class QueryBuildersUtils {
	/**
	 * 
	 * ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	 * 
	 * @param analyzeKeywords
	 * @return
	 * @author Liuxingwen
	 * @created 2016年8月8日 上午11:31:41
	 * @lastModified
	 * @history
	 */
	public static QueryBuilder multiMatchQueryM1(List<String> analyzeKeywords) {
		StringBuffer modifiedQueryString = new StringBuffer("");
		if (analyzeKeywords.size()>0){
				for(String keywords:analyzeKeywords){
					modifiedQueryString.append(" or ").append(keywords);
				}
				modifiedQueryString.delete(0, 4);
		}else{
			modifiedQueryString = new StringBuffer(modifiedQueryString);
		}
	
		return QueryBuilders.queryStringQuery(modifiedQueryString.toString()).field("keyword").analyzer("ik_smart");
	}

	/**
	 * 
	 * ｛多条件，多字段｝
	 * 
	 * @param analyzeKeywords
	 * @return "aa","bb","cc"
	 * @author Liuxingwen
	 * @created 2016年8月6日 上午10:33:55
	 * @lastModified
	 * @history
	 */

	public static QueryBuilder multiMatchQueryM2(List<String> analyzeKeywords) {
		StringBuffer modifiedQueryString = new StringBuffer("");
		if (analyzeKeywords.size() > 0) {
			for (String keywords : analyzeKeywords) {
				modifiedQueryString.append(",").append(keywords);
			}
			modifiedQueryString.delete(0, 1);
		} else {
			modifiedQueryString = new StringBuffer(modifiedQueryString);
		}
		return QueryBuilders.multiMatchQuery(modifiedQueryString.toString(),
				"keyword").analyzer("ik_smart")
		// .analyzer("ik_syno")
		;
	}

}
