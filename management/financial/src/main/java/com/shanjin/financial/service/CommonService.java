package com.shanjin.financial.service;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by Administrator on 2016/8/6.
 */
public interface CommonService {
    List<Record> getArea(Long parentId);

    List<Record> getServiveCity(Long parentId);

    public List<Record> getAppType();
}
