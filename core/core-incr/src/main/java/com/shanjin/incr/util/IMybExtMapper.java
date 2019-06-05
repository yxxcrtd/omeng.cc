package com.shanjin.incr.util;

import java.io.Serializable;
import java.util.List;

/**
 * @author hurd@oemng.cc
 * @version V0.1
 * @desc mybatis 公共DAO(Mapper)
 * @see
 */
public interface IMybExtMapper<T , ID extends Serializable> extends Serializable{

    /**
     * 保存实体
     */
    int saveEntity(T t);

    /**
     * 更新实体
     */
    int updateEntity(T t);

    /**
     * 根据主键删除
     */
    int deleteByKey(ID id);

    /**
     * 批量删除 注意 where条件中不附带 ID条件过滤
     */
    int deleteEntity(T t);

    /**
     * 根据id集合批量删除
     */
    int batchDelete(List<ID> ids);

    /**
     * 根据主键查询实体信息
     */
    T getEntityByKey(ID id);

    /**
     * 获取所有实体列表
     */
    List<T> getAll();

    /**
     * 根据条件统计
     */
    int getCount(T t);

    /**
     * 根据主键列表查询实体集合
     */
    List<T> findInByKeyIds(List<ID> ids);

    /**
     * 根据条件查询实体集合
     */
    List<T> findByParamObj(T t);


}
