

package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-01 03:28:30 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MsgComInsureType {
	
  public static final String TABLE_NAME = "msg_com_insure_type";

	public static final String TABLE_ALIAS = "MsgComInsureType";
	
	public static final String COL_CODE = "CODE";
	
	public static final String COL_NAME = "NAME";
	
	
	
	private String code;
	private String name;

	public MsgComInsureType() {}

	public MsgComInsureType(String code) {
		this.code = code;
	}

	public void setCode(String value) {
		this.code = value;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Code",getCode())
			.append("Name",getName())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getCode())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MsgComInsureType)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MsgComInsureType other = (MsgComInsureType)obj;
		return new EqualsBuilder()
			.append(getCode(),other.getCode())
			.isEquals();
	}
}

