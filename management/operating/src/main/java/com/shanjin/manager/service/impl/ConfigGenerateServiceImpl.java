package com.shanjin.manager.service.impl;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.dao.GenerateServiceDao;
import com.shanjin.manager.dao.impl.GenerateServiceDaoImpl;
import com.shanjin.manager.service.IConfigGenerateService;

import java.util.List;
import java.util.Map;

public class ConfigGenerateServiceImpl implements IConfigGenerateService {

    private GenerateServiceDao generateServiceDao = new GenerateServiceDaoImpl();

    public List<Record> getConfigGenerate(String title) {
        return generateServiceDao.getConfigGenerate(title);
    }

    public boolean saveOrUpdate(Map<String, Object> param, String username) {
        return generateServiceDao.saveOrUpdate(param, username);
    }

}
