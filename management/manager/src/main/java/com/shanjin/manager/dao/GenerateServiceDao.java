package com.shanjin.manager.dao;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Map;

public interface GenerateServiceDao {

    List<Record> getConfigGenerate(String title);

    boolean saveOrUpdate(Map<String, Object> param, String username);

}
