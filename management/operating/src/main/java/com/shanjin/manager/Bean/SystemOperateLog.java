package com.shanjin.manager.Bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 系统操作日志
 * manager_operate_log
 * @author Huang yulai
 *
 */
public class SystemOperateLog extends Model<SystemOperateLog>{
	
private static final long serialVersionUID = 1L;
	
	public static final SystemOperateLog dao = new SystemOperateLog();

	private Long id;
	
	private String operate_type;//0：展示，1:查询，2：新增，3：更新，4：删除，5：上传，6：下载，7：导出(直接保存文字描述)
	
	private String source_url;
	
	private String source_name;
	
	private String operate_user;
	
	private String operate_ip;
	
	private Integer is_del;
	
	private Date operate_time;
	
	private Long total;
	
	

	public SystemOperateLog() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOperate_type() {
		return operate_type;
	}

	public void setOperate_type(String operate_type) {
		this.operate_type = operate_type;
	}

	public String getSource_url() {
		return source_url;
	}

	public void setSource_url(String source_url) {
		this.source_url = source_url;
	}

	public String getSource_name() {
		return source_name;
	}

	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}

	public String getOperate_user() {
		return operate_user;
	}

	public void setOperate_user(String operate_user) {
		this.operate_user = operate_user;
	}

	public Date getOperate_time() {
		return operate_time;
	}

	public void setOperate_time(Date operate_time) {
		this.operate_time = operate_time;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Integer getIs_del() {
		return is_del;
	}

	public void setIs_del(Integer is_del) {
		this.is_del = is_del;
	}

	public String getOperate_ip() {
		return operate_ip;
	}

	public void setOperate_ip(String operate_ip) {
		this.operate_ip = operate_ip;
	}
	
	
	
}
