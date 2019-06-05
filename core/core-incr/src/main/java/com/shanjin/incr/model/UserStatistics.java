

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-18 10:19:04 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class UserStatistics {
	
  public static final String TABLE_NAME = "user_statistics";

	public static final String TABLE_ALIAS = "UserStatistics";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_USERID = "USER_ID";
	
	public static final String COL_BESPEAKFREQUENCY = "BESPEAK_FREQUENCY";
	
	public static final String COL_SERVICEFREQUENCY = "SERVICE_FREQUENCY";
	
	public static final String COL_TOTALPAYPRICE = "TOTAL_PAY_PRICE";
	
	public static final String COL_TOTALACTUALPRICE = "TOTAL_ACTUAL_PRICE";
	
	public static final String COL_APPTYPE = "APP_TYPE";
	
	
	
	private Long id;
	private Long userId;
	private Integer bespeakFrequency;
	private Integer serviceFrequency;
	private java.math.BigDecimal totalPayPrice;
	private java.math.BigDecimal totalActualPrice;
	private String appType;

	public UserStatistics() {}

	public UserStatistics(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setUserId(Long value) {
		this.userId = value;
	}
	
	public Long getUserId() {
		return this.userId;
	}
	
	public void setBespeakFrequency(Integer value) {
		this.bespeakFrequency = value;
	}
	
	public Integer getBespeakFrequency() {
		return this.bespeakFrequency;
	}
	
	public void setServiceFrequency(Integer value) {
		this.serviceFrequency = value;
	}
	
	public Integer getServiceFrequency() {
		return this.serviceFrequency;
	}
	
	public void setTotalPayPrice(java.math.BigDecimal value) {
		this.totalPayPrice = value;
	}
	
	public java.math.BigDecimal getTotalPayPrice() {
		return this.totalPayPrice;
	}
	
	public void setTotalActualPrice(java.math.BigDecimal value) {
		this.totalActualPrice = value;
	}
	
	public java.math.BigDecimal getTotalActualPrice() {
		return this.totalActualPrice;
	}
	
	public void setAppType(String value) {
		this.appType = value;
	}
	
	public String getAppType() {
		return this.appType;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("UserId",getUserId())
			.append("BespeakFrequency",getBespeakFrequency())
			.append("ServiceFrequency",getServiceFrequency())
			.append("TotalPayPrice",getTotalPayPrice())
			.append("TotalActualPrice",getTotalActualPrice())
			.append("AppType",getAppType())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof UserStatistics)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		UserStatistics other = (UserStatistics)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

