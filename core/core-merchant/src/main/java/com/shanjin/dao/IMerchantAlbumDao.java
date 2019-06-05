package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户相册表Dao
 */
public interface IMerchantAlbumDao {

    /** 查询相册 */
    List<Map<String, Object>> selectAlbum(Map<String, Object> paramMap);
    
    /** 查询相册数量 */
    int selectAlbumCount(Map<String, Object> paramMap);

    /** 新建相册 */
    int insertAlbum(Map<String, Object> paramMap);

    /** 重命名相册 */
    int updateAlbum(Map<String, Object> paramMap);

    /** 删除相册 */
    int updateAlbumStatus(Map<String, Object> paramMap);
}
