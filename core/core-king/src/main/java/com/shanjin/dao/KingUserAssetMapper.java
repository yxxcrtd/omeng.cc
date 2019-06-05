
package com.shanjin.dao;


import java.util.Map;

import com.shanjin.common.util.CommonMybExtMapper;
import com.shanjin.model.king.KingUserAsset;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-10-28 16:05:23 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface KingUserAssetMapper extends CommonMybExtMapper<KingUserAsset, Long>{

	/**
	 * 修改消费金可用和冻结
	 * @param param
	 */
	public void modifyUserAsset(Map<String, Object> param);

}