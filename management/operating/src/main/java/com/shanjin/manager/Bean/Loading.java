package com.shanjin.manager.Bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 启动页
 * @author Huang yulai
 *
 */
public class Loading  extends Model<Loading> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final Loading dao = new Loading();

	private Long id;
	private String title;
	private String image;
	private int show_type;
	private String link;
	private int is_pub;
	private int is_del;
	private Date start_time;
	private Date end_time;
	private Date create_time;
	private String app_type;
	private String app_name;
	private int package_type;
	private Long total;
	
	
	
	
	public Loading() {
		
	}
	
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getShow_type() {
		return show_type;
	}
	public void setShow_type(int show_type) {
		this.show_type = show_type;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public int getIs_pub() {
		return is_pub;
	}
	public void setIs_pub(int is_pub) {
		this.is_pub = is_pub;
	}
	public int getIs_del() {
		return is_del;
	}
	public void setIs_del(int is_del) {
		this.is_del = is_del;
	}
	public Date getStart_time() {
		return start_time;
	}
	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getApp_type() {
		return app_type;
	}
	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public int getPackage_type() {
		return package_type;
	}
	public void setPackage_type(int package_type) {
		this.package_type = package_type;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}

	
}
