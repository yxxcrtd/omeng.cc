package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户商品分类关联表Dao
 */
public interface IMerchantGoodsClassificationRelationDao {

    /** 商品分类关联的保存 */
    int insertGoodsClassificationRelation(List<Map<String, Object>> paramList);

    /** 一个分类下的商品ID对应的所有商品ID集查询 */
    List<Long> selectAllGoodsIdByclassificationId(Map<String, Object> paramMap);

    /** 根据分类ID删除商品分类关联 */
    int deleteRelationByClassificationId(Map<String, Object> paramMap);

    /** 根据商品ID删除单个商品分类关联 */
    int deleteRelationByGoodsId(List goodsId);

    /** 根据分类ID和商品ID删除单个商品分类关联 */
    int deleteRelation(Map<String, Object> paramMap);
}
