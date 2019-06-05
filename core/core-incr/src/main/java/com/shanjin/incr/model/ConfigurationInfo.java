

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-18 14:45:53 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class ConfigurationInfo {
	
  public static final String TABLE_NAME = "configuration_info";

	public static final String TABLE_ALIAS = "ConfigurationInfo";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_CONFIGKEY = "CONFIG_KEY";
	
	public static final String COL_CONFIGVALUE = "CONFIG_VALUE";
	
	public static final String COL_REMARK = "REMARK";
	
	public static final String COL_ISSHOW = "IS_SHOW";
	
	public static final String COL_STANDBYFIELD1 = "STANDBY_FIELD1";
	
	public static final String COL_STANDBYFIELD2 = "STANDBY_FIELD2";
	
	public static final String COL_STANDBYFIELD3 = "STANDBY_FIELD3";
	
	public static final String COL_STANDBYFIELD4 = "STANDBY_FIELD4";
	
	public static final String COL_STANDBYFIELD5 = "STANDBY_FIELD5";
	
	
	
	private Integer id;
	private String configKey;
	private String configValue;
	private String remark;
	private Integer isShow;
	private String standbyField1;
	private String standbyField2;
	private String standbyField3;
	private String standbyField4;
	private String standbyField5;

	public ConfigurationInfo() {}

	public ConfigurationInfo(Integer id) {
		this.id = id;
	}

	public void setId(Integer value) {
		this.id = value;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setConfigKey(String value) {
		this.configKey = value;
	}
	
	public String getConfigKey() {
		return this.configKey;
	}
	
	public void setConfigValue(String value) {
		this.configValue = value;
	}
	
	public String getConfigValue() {
		return this.configValue;
	}
	
	public void setRemark(String value) {
		this.remark = value;
	}
	
	public String getRemark() {
		return this.remark;
	}
	
	public void setIsShow(Integer value) {
		this.isShow = value;
	}
	
	public Integer getIsShow() {
		return this.isShow;
	}
	
	public void setStandbyField1(String value) {
		this.standbyField1 = value;
	}
	
	public String getStandbyField1() {
		return this.standbyField1;
	}
	
	public void setStandbyField2(String value) {
		this.standbyField2 = value;
	}
	
	public String getStandbyField2() {
		return this.standbyField2;
	}
	
	public void setStandbyField3(String value) {
		this.standbyField3 = value;
	}
	
	public String getStandbyField3() {
		return this.standbyField3;
	}
	
	public void setStandbyField4(String value) {
		this.standbyField4 = value;
	}
	
	public String getStandbyField4() {
		return this.standbyField4;
	}
	
	public void setStandbyField5(String value) {
		this.standbyField5 = value;
	}
	
	public String getStandbyField5() {
		return this.standbyField5;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("ConfigKey",getConfigKey())
			.append("ConfigValue",getConfigValue())
			.append("Remark",getRemark())
			.append("IsShow",getIsShow())
			.append("StandbyField1",getStandbyField1())
			.append("StandbyField2",getStandbyField2())
			.append("StandbyField3",getStandbyField3())
			.append("StandbyField4",getStandbyField4())
			.append("StandbyField5",getStandbyField5())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof ConfigurationInfo)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		ConfigurationInfo other = (ConfigurationInfo)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

