

package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-01 03:28:29 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class CiOrderBizResultRecord {
	
  public static final String TABLE_NAME = "ci_order_biz_result_record";

	public static final String TABLE_ALIAS = "CiOrderBizResultRecord";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_ORDERNO = "ORDER_NO";
	
	public static final String COL_OLDVALUE = "OLD_VALUE";
	
	public static final String COL_NEWVALUE = "NEW_VALUE";
	
	public static final String COL_DUETIME = "DUE_TIME";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATOR = "CREATOR";
	
	
	
	private Long id;
	private String orderNo;
	private String oldValue;
	private String newValue;
	private java.util.Date dueTime;
	private java.util.Date createTime;
	private String creator;

	public CiOrderBizResultRecord() {}

	public CiOrderBizResultRecord(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setOrderNo(String value) {
		this.orderNo = value;
	}
	
	public String getOrderNo() {
		return this.orderNo;
	}
	
	public void setOldValue(String value) {
		this.oldValue = value;
	}
	
	public String getOldValue() {
		return this.oldValue;
	}
	
	public void setNewValue(String value) {
		this.newValue = value;
	}
	
	public String getNewValue() {
		return this.newValue;
	}
	
	public void setDueTime(java.util.Date value) {
		this.dueTime = value;
	}
	
	public java.util.Date getDueTime() {
		return this.dueTime;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreator(String value) {
		this.creator = value;
	}
	
	public String getCreator() {
		return this.creator;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("OrderNo",getOrderNo())
			.append("OldValue",getOldValue())
			.append("NewValue",getNewValue())
			.append("DueTime",getDueTime())
			.append("CreateTime",getCreateTime())
			.append("Creator",getCreator())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof CiOrderBizResultRecord)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		CiOrderBizResultRecord other = (CiOrderBizResultRecord)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

