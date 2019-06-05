package com.shanjin.manager.dao.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.dao.GenerateServiceDao;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class GenerateServiceDaoImpl implements GenerateServiceDao {

    @Override
    public List<Record> getConfigGenerate(String title) {
        StringBuffer sql = new StringBuffer("SELECT id, title, content FROM config_generate WHERE title = '").append(title).append("'");
        return Db.find(sql.toString());
    }

    public boolean saveOrUpdate(Map<String, Object> map, String username) {
        Record record = Db.findById("config_generate", map.get("id"));
        if (null == record) {
            record.set("title", map.get("title")).set("content", map.get("content")).set("join_time", new Date()).set("operat_name", username);
            return Db.save("config_generate", record);
        } else {
            record.set("content", map.get("content")).set("join_time", new Date());
            return Db.update("config_generate", record);
        }
    }

}
