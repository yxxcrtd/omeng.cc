package com.shanjin.dao;


import com.shanjin.incr.model.IncItem;
import com.shanjin.incr.util.IMybExtMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-12 17:22:43 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface IncItemMapper extends IMybExtMapper<IncItem, Long> {

    List<IncItem> findItemsByPkgId(@Param("pkgId") Long pkgId, @Param("isDel") boolean isDelete);
}