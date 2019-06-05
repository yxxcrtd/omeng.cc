package com.shanjin.manager.Bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 服务商员工表
 * merchants_employees
 * @author Huang yulai
 *
 */
public class MerchantsEmployees extends Model<MerchantsEmployees>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final MerchantsEmployees dao = new MerchantsEmployees();
    /** 主键 */
    private Long id;
    
    /** 服务商ID */
    private Long merchants_id; 

    /** 员工姓名 */
    private String name;

    /** 员工手机号 */
    private String phone;

    /** 员工类型 */
    private Integer employees_type; //员工类型 1-老板 2-普通员工 3-财务  默认2

    /** 密码 */
    private String password;

    /** 加入时间 */
    private Date join_time;

    /** 验证码 */
    private String verification_code;
   
    /** 验证状态 */
    private Integer verification_status;

    /** 验证时间 */
    private Date verification_time;
    
    /** 最后登陆时间 */
    private Date last_login_time;

    /** 项目类型 */
    private String app_type;

    /** 是否被删除 */
    private Integer is_del;
    
    private Long total;

	public MerchantsEmployees() {
	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMerchants_id() {
		return merchants_id;
	}

	public void setMerchants_id(Long merchants_id) {
		this.merchants_id = merchants_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getEmployees_type() {
		return employees_type;
	}

	public void setEmployees_type(Integer employees_type) {
		this.employees_type = employees_type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getJoin_time() {
		return join_time;
	}

	public void setJoin_time(Date join_time) {
		this.join_time = join_time;
	}

	public String getVerification_code() {
		return verification_code;
	}

	public void setVerification_code(String verification_code) {
		this.verification_code = verification_code;
	}

	public Integer getVerification_status() {
		return verification_status;
	}

	public void setVerification_status(Integer verification_status) {
		this.verification_status = verification_status;
	}

	public Date getVerification_time() {
		return verification_time;
	}

	public void setVerification_time(Date verification_time) {
		this.verification_time = verification_time;
	}

	public Date getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Date last_login_time) {
		this.last_login_time = last_login_time;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
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
