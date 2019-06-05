

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-22 14:51:14 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class ServiceType {
	
  public static final String TABLE_NAME = "service_type";

	public static final String TABLE_ALIAS = "ServiceType";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_SERVICETYPEID = "SERVICE_TYPE_ID";
	
	public static final String COL_SERVICETYPENAME = "SERVICE_TYPE_NAME";
	
	public static final String COL_APPNAME = "APP_NAME";
	
	public static final String COL_APPTYPE = "APP_TYPE";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	public static final String COL_PARENTID = "PARENT_ID";
	
	public static final String COL_ISLEAVES = "IS_LEAVES";
	
	public static final String COL_SERVICENICK = "SERVICE_NICK";
	
	public static final String COL_ISPUB = "IS_PUB";
	
	public static final String COL_VERSION = "VERSION";
	
	
	
	private Long id;
	private Integer serviceTypeId;
	private String serviceTypeName;
	private String appName;
	private String appType;
	private Integer isDel;
	private Long parentId;
	private Integer isLeaves;
	private String serviceNick;
	private Integer isPub;
	private String version;

	public ServiceType() {}

	public ServiceType(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setServiceTypeId(Integer value) {
		this.serviceTypeId = value;
	}
	
	public Integer getServiceTypeId() {
		return this.serviceTypeId;
	}
	
	public void setServiceTypeName(String value) {
		this.serviceTypeName = value;
	}
	
	public String getServiceTypeName() {
		return this.serviceTypeName;
	}
	
	public void setAppName(String value) {
		this.appName = value;
	}
	
	public String getAppName() {
		return this.appName;
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
	
	public void setParentId(Long value) {
		this.parentId = value;
	}
	
	public Long getParentId() {
		return this.parentId;
	}
	
	public void setIsLeaves(Integer value) {
		this.isLeaves = value;
	}
	
	public Integer getIsLeaves() {
		return this.isLeaves;
	}
	
	public void setServiceNick(String value) {
		this.serviceNick = value;
	}
	
	public String getServiceNick() {
		return this.serviceNick;
	}
	
	public void setIsPub(Integer value) {
		this.isPub = value;
	}
	
	public Integer getIsPub() {
		return this.isPub;
	}
	
	public void setVersion(String value) {
		this.version = value;
	}
	
	public String getVersion() {
		return this.version;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("ServiceTypeId",getServiceTypeId())
			.append("ServiceTypeName",getServiceTypeName())
			.append("AppName",getAppName())
			.append("AppType",getAppType())
			.append("IsDel",getIsDel())
			.append("ParentId",getParentId())
			.append("IsLeaves",getIsLeaves())
			.append("ServiceNick",getServiceNick())
			.append("IsPub",getIsPub())
			.append("Version",getVersion())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof ServiceType)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		ServiceType other = (ServiceType)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

