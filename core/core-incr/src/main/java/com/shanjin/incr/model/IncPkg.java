

package com.shanjin.incr.model;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-19 09:55:17 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class IncPkg {
	
  public static final String TABLE_NAME = "inc_pkg";

	public static final String TABLE_ALIAS = "IncPkg";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_SERVICEID = "SERVICE_ID";
	
	public static final String COL_CHARGE = "CHARGE";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATER = "CREATER";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFER = "MODIFER";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	public static final String COL_STATUS = "STATUS";
	
	public static final String COL_PICSPATH = "PICS_PATH";
	
	
	
	private Long id;
	private String name;
	private Integer serviceId;
	private java.math.BigDecimal charge;
	private java.util.Date createTime;
	private String creater;
	private java.util.Date modifyTime;
	private String modifer;
	private Boolean isDel;
	private Boolean status;
	private String picsPath;

	public IncPkg() {}

	public IncPkg(Long id) {
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
	
	public void setServiceId(Integer value) {
		this.serviceId = value;
	}
	
	public Integer getServiceId() {
		return this.serviceId;
	}
	
	public void setCharge(java.math.BigDecimal value) {
		this.charge = value;
	}
	
	public java.math.BigDecimal getCharge() {
		return this.charge;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreater(String value) {
		this.creater = value;
	}
	
	public String getCreater() {
		return this.creater;
	}
	
	public void setModifyTime(java.util.Date value) {
		this.modifyTime = value;
	}
	
	public java.util.Date getModifyTime() {
		return this.modifyTime;
	}
	
	public void setModifer(String value) {
		this.modifer = value;
	}
	
	public String getModifer() {
		return this.modifer;
	}
	
	public void setIsDel(Boolean value) {
		this.isDel = value;
	}
	
	public Boolean getIsDel() {
		return this.isDel;
	}
	
	public void setStatus(Boolean value) {
		this.status = value;
	}
	
	public Boolean getStatus() {
		return this.status;
	}
	
	public void setPicsPath(String value) {
		this.picsPath = value;
	}
	
	public String getPicsPath() {
		return this.picsPath;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("ServiceId",getServiceId())
			.append("Charge",getCharge())
			.append("CreateTime",getCreateTime())
			.append("Creater",getCreater())
			.append("ModifyTime",getModifyTime())
			.append("Modifer",getModifer())
			.append("IsDel",getIsDel())
			.append("Status",getStatus())
			.append("PicsPath",getPicsPath())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof IncPkg)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		IncPkg other = (IncPkg)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

