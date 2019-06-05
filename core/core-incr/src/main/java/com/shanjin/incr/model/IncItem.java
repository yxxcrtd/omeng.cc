

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
public class IncItem {
	
  public static final String TABLE_NAME = "inc_item";

	public static final String TABLE_ALIAS = "IncItem";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_EFFICTIVEDAYS = "EFFICTIVE_DAYS";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATER = "CREATER";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFER = "MODIFER";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	public static final String COL_PICSPATH = "PICS_PATH";
	
	
	
	private Long id;
	private String name;
	private Integer effictiveDays;
	private java.util.Date createTime;
	private String creater;
	private java.util.Date modifyTime;
	private String modifer;
	private Boolean isDel;
	private String picsPath;

	public IncItem() {}

	public IncItem(Long id) {
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
	
	public void setEffictiveDays(Integer value) {
		this.effictiveDays = value;
	}
	
	public Integer getEffictiveDays() {
		return this.effictiveDays;
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
			.append("EffictiveDays",getEffictiveDays())
			.append("CreateTime",getCreateTime())
			.append("Creater",getCreater())
			.append("ModifyTime",getModifyTime())
			.append("Modifer",getModifer())
			.append("IsDel",getIsDel())
			.append("PicsPath",getPicsPath())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof IncItem)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		IncItem other = (IncItem)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

