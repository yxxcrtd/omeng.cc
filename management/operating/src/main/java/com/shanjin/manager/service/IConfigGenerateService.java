package com.shanjin.manager.service;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Map;

public interface IConfigGenerateService {

    List<Record> getConfigGenerate(String title);

    boolean saveOrUpdate(Map<String, Object> map, String username);

}
