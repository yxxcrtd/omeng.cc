package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

import java.util.Date;

/**
 * 配置生成实体
 */
public class ConfigGenerate extends Model<ConfigGenerate> {

    private static final long serialVersionUID = 1L;

    public static final ConfigGenerate dao = new ConfigGenerate();

    private Long id;

    private String title;

    private String content;

    private Date join_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getJoin_time() {
        return join_time;
    }

    public void setJoin_time(Date join_time) {
        this.join_time = join_time;
    }

}
