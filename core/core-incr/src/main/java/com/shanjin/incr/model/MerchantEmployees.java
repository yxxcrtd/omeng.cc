

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-17 21:22:51 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MerchantEmployees {
	
  public static final String TABLE_NAME = "merchant_employees";

	public static final String TABLE_ALIAS = "MerchantEmployees";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_MERCHANTID = "MERCHANT_ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_PHONE = "PHONE";
	
	public static final String COL_EMPLOYEESTYPE = "EMPLOYEES_TYPE";
	
	public static final String COL_JOINTIME = "JOIN_TIME";
	
	public static final String COL_PASSWORD = "PASSWORD";
	
	public static final String COL_VERIFICATIONCODE = "VERIFICATION_CODE";
	
	public static final String COL_VERIFICATIONTIME = "VERIFICATION_TIME";
	
	public static final String COL_VERIFICATIONSTATUS = "VERIFICATION_STATUS";
	
	public static final String COL_LASTLOGINTIME = "LAST_LOGIN_TIME";
	
	public static final String COL_APPTYPE = "APP_TYPE";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	public static final String COL_EMPLOYEEKEY = "EMPLOYEE_KEY";
	
	public static final String COL_USERID = "USER_ID";
	
	
	
	private Long id;
	private Long merchantId;
	private String name;
	private String phone;
	private Integer employeesType;
	private java.util.Date joinTime;
	private String password;
	private String verificationCode;
	private java.util.Date verificationTime;
	private Integer verificationStatus;
	private java.util.Date lastLoginTime;
	private String appType;
	private Integer isDel;
	private String employeeKey;
	private Long userId;

	public MerchantEmployees() {}

	public MerchantEmployees(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setMerchantId(Long value) {
		this.merchantId = value;
	}
	
	public Long getMerchantId() {
		return this.merchantId;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setPhone(String value) {
		this.phone = value;
	}
	
	public String getPhone() {
		return this.phone;
	}
	
	public void setEmployeesType(Integer value) {
		this.employeesType = value;
	}
	
	public Integer getEmployeesType() {
		return this.employeesType;
	}
	
	public void setJoinTime(java.util.Date value) {
		this.joinTime = value;
	}
	
	public java.util.Date getJoinTime() {
		return this.joinTime;
	}
	
	public void setPassword(String value) {
		this.password = value;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setVerificationCode(String value) {
		this.verificationCode = value;
	}
	
	public String getVerificationCode() {
		return this.verificationCode;
	}
	
	public void setVerificationTime(java.util.Date value) {
		this.verificationTime = value;
	}
	
	public java.util.Date getVerificationTime() {
		return this.verificationTime;
	}
	
	public void setVerificationStatus(Integer value) {
		this.verificationStatus = value;
	}
	
	public Integer getVerificationStatus() {
		return this.verificationStatus;
	}
	
	public void setLastLoginTime(java.util.Date value) {
		this.lastLoginTime = value;
	}
	
	public java.util.Date getLastLoginTime() {
		return this.lastLoginTime;
	}
	
	public void setAppType(String value) {
		this.appType = value;
	}
	
	public String getAppType() {
		return this.appType;
	}
	
	public void setIsDel(Integer value) {
		this.isDel = value;
	}
	
	public Integer getIsDel() {
		return this.isDel;
	}
	
	public void setEmployeeKey(String value) {
		this.employeeKey = value;
	}
	
	public String getEmployeeKey() {
		return this.employeeKey;
	}
	
	public void setUserId(Long value) {
		this.userId = value;
	}
	
	public Long getUserId() {
		return this.userId;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("MerchantId",getMerchantId())
			.append("Name",getName())
			.append("Phone",getPhone())
			.append("EmployeesType",getEmployeesType())
			.append("JoinTime",getJoinTime())
			.append("Password",getPassword())
			.append("VerificationCode",getVerificationCode())
			.append("VerificationTime",getVerificationTime())
			.append("VerificationStatus",getVerificationStatus())
			.append("LastLoginTime",getLastLoginTime())
			.append("AppType",getAppType())
			.append("IsDel",getIsDel())
			.append("EmployeeKey",getEmployeeKey())
			.append("UserId",getUserId())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MerchantEmployees)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MerchantEmployees other = (MerchantEmployees)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

