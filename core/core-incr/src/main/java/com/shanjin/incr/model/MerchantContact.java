

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-18 09:32:38 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MerchantContact {
	
  public static final String TABLE_NAME = "merchant_contact";

	public static final String TABLE_ALIAS = "MerchantContact";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_MERCHANTID = "MERCHANT_ID";
	
	public static final String COL_TELEPHONE = "TELEPHONE";
	
	
	
	private Long id;
	private Long merchantId;
	private String telephone;

	public MerchantContact() {}

	public MerchantContact(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setMerchantId(Long value) {
		this.merchantId = value;
	}
	
	public Long getMerchantId() {
		return this.merchantId;
	}
	
	public void setTelephone(String value) {
		this.telephone = value;
	}
	
	public String getTelephone() {
		return this.telephone;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("MerchantId",getMerchantId())
			.append("Telephone",getTelephone())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MerchantContact)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MerchantContact other = (MerchantContact)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

