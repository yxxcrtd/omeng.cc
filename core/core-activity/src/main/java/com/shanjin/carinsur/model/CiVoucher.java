

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
public class CiVoucher {
	
  public static final String TABLE_NAME = "ci_voucher";

	public static final String TABLE_ALIAS = "CiVoucher";
	
	public static final String COL_CODE = "CODE";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_AMOUNT = "AMOUNT";
	
	public static final String COL_VOUTYPE = "VOU_TYPE";
	
	public static final String COL_EFFECTIVEDAYS = "EFFECTIVE_DAYS";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	
	
	private String code;
	private String name;
	private java.math.BigDecimal amount;
	private Integer vouType;
	private Integer effectiveDays;
	private Integer isDel;

	public CiVoucher() {}

	public CiVoucher(String code) {
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
	
	public void setAmount(java.math.BigDecimal value) {
		this.amount = value;
	}
	
	public java.math.BigDecimal getAmount() {
		return this.amount;
	}
	
	public void setVouType(Integer value) {
		this.vouType = value;
	}
	
	public Integer getVouType() {
		return this.vouType;
	}
	
	public void setEffectiveDays(Integer value) {
		this.effectiveDays = value;
	}
	
	public Integer getEffectiveDays() {
		return this.effectiveDays;
	}
	
	public void setIsDel(Integer value) {
		this.isDel = value;
	}
	
	public Integer getIsDel() {
		return this.isDel;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Code",getCode())
			.append("Name",getName())
			.append("Amount",getAmount())
			.append("VouType",getVouType())
			.append("EffectiveDays",getEffectiveDays())
			.append("IsDel",getIsDel())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getCode())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof CiVoucher)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		CiVoucher other = (CiVoucher)obj;
		return new EqualsBuilder()
			.append(getCode(),other.getCode())
			.isEquals();
	}
}

