
package com.shanjin.dao;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shanjin.common.util.CommonMybExtMapper;
import com.shanjin.model.king.KingAssetConsRecord;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-10-28 16:05:06 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface KingAssetConsRecordMapper extends CommonMybExtMapper<KingAssetConsRecord, Long>{

	

	public BigDecimal queryConsAmount(Map<String, Object> param);
	
	/**
	 * 分页查询消费金支付记录
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryKingUserAssetRecorderList(Map<String, Object> param);
	
	public List<KingAssetConsRecord> queryKingUserAssetRecorderParam(Map<String, Object> param);
	
	/**
	 * 查询消费金支付记录数量
	 * @param param
	 * @return
	 */
	public Integer queryKingUserAssetRecorderNum(Map<String, Object> param);
	
	
}