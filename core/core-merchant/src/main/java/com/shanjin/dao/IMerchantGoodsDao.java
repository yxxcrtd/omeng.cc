package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户商品分类表Dao
 */
public interface IMerchantGoodsDao {

    /** 商品信息记录数查询 */
	int selectGoodsInfoCount(Map<String, Object> paramMap);

    /** 商品信息查询 */
    List<Map<String, Object>> selectGoodsInfo(Map<String, Object> paramMap);

    /** 商品名字查询 */
    List<Map<String, Object>> selectGoodsName(Map<String, Object> paramMap);

    /** 商品保存 */
    int insertGoods(Map<String, Object> paramMap);

    /** 更新商品信息 */
    int updateGoodsInfo(Map<String, Object> paramMap);

    /** 删除单个商品*/
    int updateGoodsStatus(Map<String, Object> paramMap);

    /** 删除多个商品*/
    int updateManyGoodsStatus(List goodsId);

    /** 单个商家可用商品数量查询 */
	int selectGoodsCount(Map<String, Object> paramMap);

    /** 查询商户最新2张商品图片 */
    Map<String, Object> selectLast2GoodsPictures(Map<String, Object> paramMap);

    /** 查询商户最新3个商品信息 */
    List<Map<String, Object>> selectLast3GoodsInfo(Map<String, Object> paramMap);
    
    
    /** 查询商户最新n个商品信息 */
    List<Map<String, Object>> selectLastnGoodsInfo(Map<String, Object> paramMap);
    
    Map<String, Object> selectLastGoodsPic(Map<String, Object> paramMap);
    
    List<Map<String, Object>>  selectLastGoodsInfo(Map<String, Object> paramMap);

    /**
     * 插入商品照片
     * @param goodsPicList
     * @return
     */
	int insertGoodsPic(List<Map<String, Object>> goodsPicList) throws Exception;
	
	/**
	 * 删除商品照片
	 * @param goodsId 多id
	 * @return
	 */
	int deleteGoodsPic(List goodsId);

	/**
	 * 商品上下架
	 * @param paramMap
	 * @return
	 */
	int changeGoodsStatus(Map<String, Object> paramMap);

	/**
	 * 查询商品图片
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> selectGoodsPic(Map<String, Object> paramMap);

    /**
     * 商品快照保存
     * @param paramMap
     * @return
     */
    int insertGoodsHistory(Map<String, Object> paramMap);
    /**
     * 插入商品快照照片
     * @param goodsPicList
     * @return
     */
	int insertGoodsHistoryPic(Map<String, Object> paramMap);

	/**
	 * 查询商品快照图片
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> selectGoodsHistoryPic(Map<String, Object> paramMap);
    
	/**
	 * 查看商品快照信息
	 * @param paramMap
	 * @return
	 */
    Map<String, Object> selectGoodsHistoryInfo(Map<String, Object> paramMap);

    /**
     * 根据产品id、版本号查找是否存在快照
     * @param paramMap
     * @return
     */
	Long findeGoodsHistoryByGoodsId(Map<String, Object> paramMap);

	/**
	 * 更新产品版本号
	 * @param paramMap
	 * @return
	 */
	int updateGoodsVersion(Map<String, Object> paramMap);

	/**
	 * 查看商品详情
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> selectGoodsDetail(Map<String, Object> paramMap);
	
	
}
