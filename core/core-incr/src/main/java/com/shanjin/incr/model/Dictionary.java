

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-17 15:04:32 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class Dictionary {
	
  public static final String TABLE_NAME = "dictionary";

	public static final String TABLE_ALIAS = "Dictionary";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_DICTTYPE = "DICT_TYPE";
	
	public static final String COL_DICTKEY = "DICT_KEY";
	
	public static final String COL_DICTVALUE = "DICT_VALUE";
	
	public static final String COL_REMARK = "REMARK";
	
	public static final String COL_PARENTDICTID = "PARENT_DICT_ID";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	public static final String COL_DICTLEVEL = "DICT_LEVEL";
	
	public static final String COL_DICTDOMAIN = "DICT_DOMAIN";
	
	public static final String COL_ISLEAVES = "IS_LEAVES";
	
	public static final String COL_SORT = "SORT";
	
	
	
	private Long id;
	private String dictType;
	private String dictKey;
	private String dictValue;
	private String remark;
	private Integer parentDictId;
	private Integer isDel;
	private String dictLevel;
	private String dictDomain;
	private Integer isLeaves;
	private Integer sort;

	public Dictionary() {}

	public Dictionary(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setDictType(String value) {
		this.dictType = value;
	}
	
	public String getDictType() {
		return this.dictType;
	}
	
	public void setDictKey(String value) {
		this.dictKey = value;
	}
	
	public String getDictKey() {
		return this.dictKey;
	}
	
	public void setDictValue(String value) {
		this.dictValue = value;
	}
	
	public String getDictValue() {
		return this.dictValue;
	}
	
	public void setRemark(String value) {
		this.remark = value;
	}
	
	public String getRemark() {
		return this.remark;
	}
	
	public void setParentDictId(Integer value) {
		this.parentDictId = value;
	}
	
	public Integer getParentDictId() {
		return this.parentDictId;
	}
	
	public void setIsDel(Integer value) {
		this.isDel = value;
	}
	
	public Integer getIsDel() {
		return this.isDel;
	}
	
	public void setDictLevel(String value) {
		this.dictLevel = value;
	}
	
	public String getDictLevel() {
		return this.dictLevel;
	}
	
	public void setDictDomain(String value) {
		this.dictDomain = value;
	}
	
	public String getDictDomain() {
		return this.dictDomain;
	}
	
	public void setIsLeaves(Integer value) {
		this.isLeaves = value;
	}
	
	public Integer getIsLeaves() {
		return this.isLeaves;
	}
	
	public void setSort(Integer value) {
		this.sort = value;
	}
	
	public Integer getSort() {
		return this.sort;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("DictType",getDictType())
			.append("DictKey",getDictKey())
			.append("DictValue",getDictValue())
			.append("Remark",getRemark())
			.append("ParentDictId",getParentDictId())
			.append("IsDel",getIsDel())
			.append("DictLevel",getDictLevel())
			.append("DictDomain",getDictDomain())
			.append("IsLeaves",getIsLeaves())
			.append("Sort",getSort())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Dictionary)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		Dictionary other = (Dictionary)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

