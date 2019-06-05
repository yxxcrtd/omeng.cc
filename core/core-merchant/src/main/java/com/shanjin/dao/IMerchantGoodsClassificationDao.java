package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户商品分类表Dao
 */
public interface IMerchantGoodsClassificationDao {

    /** 商品分类的信息查询 */
    List<Map<String, Object>> selectGoodsClassificationInfo(Map<String, Object> paramMap);

    /** 商品分类名查询 */
    List<Map<String, Object>> selectGoodsClassificationName(Map<String, Object> paramMap);

    /** 商品分类数量查询 */
    int selectGoodsClassificationNum(Map<String, Object> paramMap);

    /** 商品分类的保存 */
    int insertGoodsClassification(Map<String, Object> paramMap);

    /** 更新商品分类名 */
    int updateGoodsClassificationName(Map<String, Object> paramMap);

    /** 删除商品分类*/
    int updateGoodsClassificationStatus(Map<String, Object> paramMap);

	/**
	 * 查询已下架商铺个数
	 * @param paramMap
	 * @return
	 */
	int selectIsOutGoodsCount(Map<String, Object> paramMap);
}
