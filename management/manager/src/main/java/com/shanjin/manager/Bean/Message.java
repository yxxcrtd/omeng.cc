package com.shanjin.manager.Bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 
 * @author Huang yulai
 *
 */
public class Message  extends Model<Message> {

	/**
	 * 主消息
	 */
	private static final long serialVersionUID = 1L;

	public static final Message dao = new Message();

	private Long id;
		
    private int customer_type;
    
	private Long customer_id;
	
	private String province;
	
	private String city;
	
	private String app_type;
	
    private int is_vip;
	
	private String title;
	
	private String content;
	
	private Date send_time;
	
	private Date join_time;
	
	private int is_use;
	
	private int is_push;
	
	private int is_sendSms;
	
	private int is_del;
	
	private int push_type;
	
	private int send_type;
	
	private Long total;
	
	public Message(){
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCustomer_type() {
		return customer_type;
	}

	public void setCustomer_type(int customer_type) {
		this.customer_type = customer_type;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public int getIs_vip() {
		return is_vip;
	}

	public void setIs_vip(int is_vip) {
		this.is_vip = is_vip;
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

	public Date getSend_time() {
		return send_time;
	}

	public void setSend_time(Date send_time) {
		this.send_time = send_time;
	}

	public Date getJoin_time() {
		return join_time;
	}

	public void setJoin_time(Date join_time) {
		this.join_time = join_time;
	}

	public int getIs_use() {
		return is_use;
	}

	public void setIs_use(int is_use) {
		this.is_use = is_use;
	}

	public int getIs_push() {
		return is_push;
	}

	public void setIs_push(int is_push) {
		this.is_push = is_push;
	}

	public int getIs_sendSms() {
		return is_sendSms;
	}

	public void setIs_sendSms(int is_sendSms) {
		this.is_sendSms = is_sendSms;
	}

	public int getIs_del() {
		return is_del;
	}

	public void setIs_del(int is_del) {
		this.is_del = is_del;
	}

	public int getPush_type() {
		return push_type;
	}

	public void setPush_type(int push_type) {
		this.push_type = push_type;
	}

	public int getSend_type() {
		return send_type;
	}

	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
	
	
}
