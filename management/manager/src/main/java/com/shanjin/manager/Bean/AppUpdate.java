package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 客户端版本升级记录
 * 
 * @author Huang yulai
 *
 */
public class AppUpdate extends Model<AppUpdate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final AppUpdate dao = new AppUpdate();

	private Long id;

	private String app_type;

	private String package_name;

	private String version;

	private String channel;

	private String download_url;

	private String detail;

	private int package_type;

	private int update_type;

	private int is_del;

	private int publish_status;

	private Long total;

	public AppUpdate() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getPackage_type() {
		return package_type;
	}

	public void setPackage_type(int package_type) {
		this.package_type = package_type;
	}

	public int getUpdate_type() {
		return update_type;
	}

	public void setUpdate_type(int update_type) {
		this.update_type = update_type;
	}

	public int getIs_del() {
		return is_del;
	}

	public void setIs_del(int is_del) {
		this.is_del = is_del;
	}

	public int getPublish_status() {
		return publish_status;
	}

	public void setPublish_status(int publish_status) {
		this.publish_status = publish_status;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	
}
