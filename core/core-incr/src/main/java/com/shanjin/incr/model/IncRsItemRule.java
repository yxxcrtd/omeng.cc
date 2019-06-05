

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
public class IncRsItemRule {
	
  public static final String TABLE_NAME = "inc_rs_item_rule";

	public static final String TABLE_ALIAS = "IncRsItemRule";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_ITEMID = "ITEM_ID";
	
	public static final String COL_RULEID = "RULE_ID";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATER = "CREATER";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFER = "MODIFER";
	
	
	
	private Long id;
	private String name;
	private Long itemId;
	private Integer ruleId;
	private java.util.Date createTime;
	private String creater;
	private java.util.Date modifyTime;
	private String modifer;

	public IncRsItemRule() {}

	public IncRsItemRule(Long id) {
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
	
	public void setItemId(Long value) {
		this.itemId = value;
	}
	
	public Long getItemId() {
		return this.itemId;
	}
	
	public void setRuleId(Integer value) {
		this.ruleId = value;
	}
	
	public Integer getRuleId() {
		return this.ruleId;
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
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("ItemId",getItemId())
			.append("RuleId",getRuleId())
			.append("CreateTime",getCreateTime())
			.append("Creater",getCreater())
			.append("ModifyTime",getModifyTime())
			.append("Modifer",getModifer())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof IncRsItemRule)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		IncRsItemRule other = (IncRsItemRule)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

