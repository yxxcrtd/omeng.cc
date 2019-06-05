

package com.shanjin.incr.model;

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
public class IncRule {
	
  public static final String TABLE_NAME = "inc_rule";

	public static final String TABLE_ALIAS = "IncRule";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_RULECODE = "RULE_CODE";
	
	public static final String COL_DESCR = "DESCR";
	
	public static final String COL_VALUE = "VALUE";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATER = "CREATER";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFER = "MODIFER";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	
	
	private Integer id;
	private String name;
	private String ruleCode;
	private String descr;
	private Integer value;
	private java.util.Date createTime;
	private String creater;
	private java.util.Date modifyTime;
	private String modifer;
	private Boolean isDel;

	public IncRule() {}

	public IncRule(Integer id) {
		this.id = id;
	}

	public void setId(Integer value) {
		this.id = value;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setRuleCode(String value) {
		this.ruleCode = value;
	}
	
	public String getRuleCode() {
		return this.ruleCode;
	}
	
	public void setDescr(String value) {
		this.descr = value;
	}
	
	public String getDescr() {
		return this.descr;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return this.value;
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
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("RuleCode",getRuleCode())
			.append("Descr",getDescr())
			.append("Value",getValue())
			.append("CreateTime",getCreateTime())
			.append("Creater",getCreater())
			.append("ModifyTime",getModifyTime())
			.append("Modifer",getModifer())
			.append("IsDel",getIsDel())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof IncRule)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		IncRule other = (IncRule)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

