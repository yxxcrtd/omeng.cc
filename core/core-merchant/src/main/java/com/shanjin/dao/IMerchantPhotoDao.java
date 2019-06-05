package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户相片表Dao
 */
public interface IMerchantPhotoDao {

    /** 根据相册Id查询相册中的相片 */
    List<Map<String, Object>> selectPhotoByAlbumId(Map<String, Object> paramMap);
    
    /** 查询单个商户的总相片数 */
    int selectPhotoTotal(Map<String, Object> paramMap);

    /** 新建相片 */
    int insertPhoto(Map<String, Object> paramMap);

    /** 新建多个相片 */
    int insertPhotos(List<Map<String, Object>> photoPathList);

    /** 删除相片(根据相片Id批量删除) */
    int updatePhotoStatusById(List<Long> photoIdList);

    /** 删除相片(根据相册Id批量删除) */
    int updatePhotoStatusByAlbumId(Map<String, Object> paramMap);

    /** 根据商户ID查询最新的1张照片 */
    Map<String, Object> selectLast1Photo(Map<String, Object> paramMap);

    /** 根据商户ID查询最新的2张照片 */
    Map<String, Object> selectLast2Photo(Map<String, Object> paramMap);

    /** 根据商户ID查询最新的4张照片 */
    Map<String, Object> selectLast4Photo(Map<String, Object> paramMap);
    
    Map<String, Object>  selectLastPhotos(Map<String, Object> paramMap);
}
