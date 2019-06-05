package com.shanjin.manager.Bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 表名：user_info 用户信息�?
 */

public class UserInfo extends Model<UserInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final UserInfo dao = new UserInfo();
	/** 主键 */
	private Integer id;

	/** 姓名 */
	private String name;

	/** 性别 */
	private Integer sex;

	/** 手机�? */
	private String phone;

	/** 加入时间 */
	private Date join_time;

	/** �?���?��登陆时间 */
	private Date last_login_time;

	/** 备注 */
	private String remark;

	/** 验证�? */
	private String verification_code;

	/** 验证码发送时�? */
	private Date verification_time;

	/** 是否被验�? */
	private Integer is_verification;

	/** 是否被删�? */
	private Integer is_del;
	private Long total;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getJoin_time() {
		return join_time;
	}

	public void setJoin_time(Date join_time) {
		this.join_time = join_time;
	}

	public Date getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Date last_login_time) {
		this.last_login_time = last_login_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Integer getIs_verification() {
		return is_verification;
	}

	public void setIs_verification(Integer is_verification) {
		this.is_verification = is_verification;
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

}