

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-17 19:21:06 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class UserInfo {
	
  public static final String TABLE_NAME = "user_info";

	public static final String TABLE_ALIAS = "UserInfo";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_SEX = "SEX";
	
	public static final String COL_PHONE = "PHONE";
	
	public static final String COL_JOINTIME = "JOIN_TIME";
	
	public static final String COL_LASTLOGINTIME = "LAST_LOGIN_TIME";
	
	public static final String COL_REMARK = "REMARK";
	
	public static final String COL_VERIFICATIONCODE = "VERIFICATION_CODE";
	
	public static final String COL_VERIFICATIONTIME = "VERIFICATION_TIME";
	
	public static final String COL_ISVERIFICATION = "IS_VERIFICATION";
	
	public static final String COL_USERKEY = "USER_KEY";
	
	public static final String COL_PROVINCE = "PROVINCE";
	
	public static final String COL_CITY = "CITY";
	
	public static final String COL_IP = "IP";
	
	public static final String COL_USERTYPE = "USER_TYPE";
	
	public static final String COL_RONGCLOUDTOKEN = "RONG_CLOUD_TOKEN";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	public static final String COL_PLATFORM = "PLATFORM";
	
	
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	private Long id;
	private Long userId;
	private String name;
	private Integer sex;
	private String phone;
	private java.util.Date joinTime;
	private java.util.Date lastLoginTime;
	private String remark;
	private String verificationCode;
	private java.util.Date verificationTime;
	private Integer isVerification;
	private String userKey;
	private String province;
	private String city;
	private String ip;
	private Integer userType;
	private String rongCloudToken;
	private Integer isDel;
	private Integer platform;


	public UserInfo() {}

	public UserInfo(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setSex(Integer value) {
		this.sex = value;
	}
	
	public Integer getSex() {
		return this.sex;
	}
	
	public void setPhone(String value) {
		this.phone = value;
	}
	
	public String getPhone() {
		return this.phone;
	}
	
	public void setJoinTime(java.util.Date value) {
		this.joinTime = value;
	}
	
	public java.util.Date getJoinTime() {
		return this.joinTime;
	}
	
	public void setLastLoginTime(java.util.Date value) {
		this.lastLoginTime = value;
	}
	
	public java.util.Date getLastLoginTime() {
		return this.lastLoginTime;
	}
	
	public void setRemark(String value) {
		this.remark = value;
	}
	
	public String getRemark() {
		return this.remark;
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
	
	public void setIsVerification(Integer value) {
		this.isVerification = value;
	}
	
	public Integer getIsVerification() {
		return this.isVerification;
	}
	
	public void setUserKey(String value) {
		this.userKey = value;
	}
	
	public String getUserKey() {
		return this.userKey;
	}
	
	public void setProvince(String value) {
		this.province = value;
	}
	
	public String getProvince() {
		return this.province;
	}
	
	public void setCity(String value) {
		this.city = value;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public void setIp(String value) {
		this.ip = value;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void setUserType(Integer value) {
		this.userType = value;
	}
	
	public Integer getUserType() {
		return this.userType;
	}
	
	public void setRongCloudToken(String value) {
		this.rongCloudToken = value;
	}
	
	public String getRongCloudToken() {
		return this.rongCloudToken;
	}
	
	public void setIsDel(Integer value) {
		this.isDel = value;
	}
	
	public Integer getIsDel() {
		return this.isDel;
	}
	
	public void setPlatform(Integer value) {
		this.platform = value;
	}
	
	public Integer getPlatform() {
		return this.platform;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("Sex",getSex())
			.append("Phone",getPhone())
			.append("JoinTime",getJoinTime())
			.append("LastLoginTime",getLastLoginTime())
			.append("Remark",getRemark())
			.append("VerificationCode",getVerificationCode())
			.append("VerificationTime",getVerificationTime())
			.append("IsVerification",getIsVerification())
			.append("UserKey",getUserKey())
			.append("Province",getProvince())
			.append("City",getCity())
			.append("Ip",getIp())
			.append("UserType",getUserType())
			.append("RongCloudToken",getRongCloudToken())
			.append("IsDel",getIsDel())
			.append("Platform",getPlatform())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof UserInfo)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		UserInfo other = (UserInfo)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

