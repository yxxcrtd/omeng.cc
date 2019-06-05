

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-19 09:55:18 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MerchantAuth {
	
  public static final String TABLE_NAME = "merchant_auth";

	public static final String TABLE_ALIAS = "MerchantAuth";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_MERCHANTID = "MERCHANT_ID";
	
	public static final String COL_AUTHTYPE = "AUTH_TYPE";
	
	public static final String COL_AUTHSTATUS = "AUTH_STATUS";
	
	public static final String COL_PATH = "PATH";
	
	public static final String COL_JOINTIME = "JOIN_TIME";
	
	public static final String COL_AUTHTIME = "AUTH_TIME";
	
	public static final String COL_REMARK = "REMARK";
	
	public static final String COL_OPERUSER = "OPER_USER";
	
	
	
	private Long id;
	private Long merchantId;
	private Integer authType;
	private Integer authStatus;
	private String path;
	private java.util.Date joinTime;
	private java.util.Date authTime;
	private String remark;
	private String operUser;

	public MerchantAuth() {}

	public MerchantAuth(Long id) {
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
	
	public void setAuthType(Integer value) {
		this.authType = value;
	}
	
	public Integer getAuthType() {
		return this.authType;
	}
	
	public void setAuthStatus(Integer value) {
		this.authStatus = value;
	}
	
	public Integer getAuthStatus() {
		return this.authStatus;
	}
	
	public void setPath(String value) {
		this.path = value;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setJoinTime(java.util.Date value) {
		this.joinTime = value;
	}
	
	public java.util.Date getJoinTime() {
		return this.joinTime;
	}
	
	public void setAuthTime(java.util.Date value) {
		this.authTime = value;
	}
	
	public java.util.Date getAuthTime() {
		return this.authTime;
	}
	
	public void setRemark(String value) {
		this.remark = value;
	}
	
	public String getRemark() {
		return this.remark;
	}
	
	public void setOperUser(String value) {
		this.operUser = value;
	}
	
	public String getOperUser() {
		return this.operUser;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("MerchantId",getMerchantId())
			.append("AuthType",getAuthType())
			.append("AuthStatus",getAuthStatus())
			.append("Path",getPath())
			.append("JoinTime",getJoinTime())
			.append("AuthTime",getAuthTime())
			.append("Remark",getRemark())
			.append("OperUser",getOperUser())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MerchantAuth)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MerchantAuth other = (MerchantAuth)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

