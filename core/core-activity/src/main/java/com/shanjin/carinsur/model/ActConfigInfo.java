

package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-07 14:11:40 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class ActConfigInfo {
	
  public static final String TABLE_NAME = "act_config_info";

	public static final String TABLE_ALIAS = "ActConfigInfo";
	
	public static final String COL_CFGKEY = "CFG_KEY";
	
	public static final String COL_CFGVALUE = "CFG_VALUE";
	
	public static final String COL_CFGTYPE = "CFG_TYPE";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	
	
	private String cfgKey;
	private String cfgValue;
	private String cfgType;
	private java.util.Date createTime;

	public ActConfigInfo() {}

	public ActConfigInfo(String cfgKey) {
		this.cfgKey = cfgKey;
	}

	public void setCfgKey(String value) {
		this.cfgKey = value;
	}
	
	public String getCfgKey() {
		return this.cfgKey;
	}
	
	public void setCfgValue(String value) {
		this.cfgValue = value;
	}
	
	public String getCfgValue() {
		return this.cfgValue;
	}
	
	public void setCfgType(String value) {
		this.cfgType = value;
	}
	
	public String getCfgType() {
		return this.cfgType;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("CfgKey",getCfgKey())
			.append("CfgValue",getCfgValue())
			.append("CfgType",getCfgType())
			.append("CreateTime",getCreateTime())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getCfgKey())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof ActConfigInfo)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		ActConfigInfo other = (ActConfigInfo)obj;
		return new EqualsBuilder()
			.append(getCfgKey(),other.getCfgKey())
			.isEquals();
	}
}

