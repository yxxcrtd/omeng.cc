package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 项目基本信息
 * app_info 
 * @author Huang yulai
 *
 */

public class AppInfo extends Model<AppInfo>{
	
	private static final long serialVersionUID = 1L;

	public static final AppInfo dao = new AppInfo();

	private Long id;
	
	private String app_name;
	
	private String app_type;
	
	private String app_remark;
	
	private int is_del;
	
	private Long total;
	
	public AppInfo(){
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public String getApp_remark() {
		return app_remark;
	}

	public void setApp_remark(String app_remark) {
		this.app_remark = app_remark;
	}

	public int getIs_del() {
		return is_del;
	}

	public void setIs_del(int is_del) {
		this.is_del = is_del;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}
