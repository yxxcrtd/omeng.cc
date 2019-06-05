package com.shanjin.manager.Bean;

/**
 * 表名：merchants_info 商户信息表
 */
import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

public class MerchantsInfo extends Model<MerchantsInfo>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final MerchantsInfo dao = new MerchantsInfo();
    /** 主键 */
    private Long id;
    private String ww;
    /** 商户名称 */
    private String name;

    /** 商户简称 */
    private String short_name;

    /** 商户地址 */
    private String address;

    /** 纬度 */
    private Double latitude;

    /** 经度 */
    private Double longitude;

    /** 定位得到的地址信息 */
    private String location_address;

    /** 加入时间 */
    private Date join_time;

    /** 提现密码 */
    private String money_password;

    /** 提现真实姓名 */
    private String money_real_name;

    /** 提现真实身份证号 */
    private String money_id_no;

    /** 验证码 */
    private String verification_code;

    /** 验证时间 */
    private Date verification_time;

    /** 验证状态 */
    private Integer verification_status;

    /** 是否被删除 */
    private Integer is_del;
    
    /** 省 */
    private String province;

    /** 市 */
    private String city;
    private Long total;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShort_name() {
		return short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getLocation_address() {
		return location_address;
	}

	public void setLocation_address(String location_address) {
		this.location_address = location_address;
	}

	public Date getJoin_time() {
		return join_time;
	}

	public void setJoin_time(Date join_time) {
		this.join_time = join_time;
	}

	public String getMoney_password() {
		return money_password;
	}

	public void setMoney_password(String money_password) {
		this.money_password = money_password;
	}

	public String getMoney_real_name() {
		return money_real_name;
	}

	public void setMoney_real_name(String money_real_name) {
		this.money_real_name = money_real_name;
	}

	public String getMoney_id_no() {
		return money_id_no;
	}

	public void setMoney_id_no(String money_id_no) {
		this.money_id_no = money_id_no;
	}

	public String getVerification_code() {
		return verification_code;
	}

	public void setVerification_code(String verification_code) {
		this.verification_code = verification_code;
	}

	public Date getVerification_time() {
		return verification_time;
	}

	public void setVerification_time(Date verification_time) {
		this.verification_time = verification_time;
	}

	public Integer getVerification_status() {
		return verification_status;
	}

	public void setVerification_status(Integer verification_status) {
		this.verification_status = verification_status;
	}

	public Integer getIs_del() {
		return is_del;
	}

	public void setIs_del(Integer is_del) {
		this.is_del = is_del;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public String getWw() {
		return ww;
	}

	public void setWw(String ww) {
		this.ww = ww;
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

   
}
