

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-18 10:10:47 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MerchantStatistics {
	
  public static final String TABLE_NAME = "merchant_statistics";

	public static final String TABLE_ALIAS = "MerchantStatistics";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_MERCHANTID = "MERCHANT_ID";
	
	public static final String COL_GRABFREQUENCY = "GRAB_FREQUENCY";
	
	public static final String COL_SERVICEFREQUENCY = "SERVICE_FREQUENCY";
	
	public static final String COL_TOTALINCOMEPRICE = "TOTAL_INCOME_PRICE";
	
	public static final String COL_TOTALWITHDRAWPRICE = "TOTAL_WITHDRAW_PRICE";
	
	public static final String COL_SURPLUSPRICE = "SURPLUS_PRICE";
	
	public static final String COL_ORDERSURPLUSPRICE = "ORDER_SURPLUS_PRICE";
	
	public static final String COL_TOTALATTITUDEEVALUATION = "TOTAL_ATTITUDE_EVALUATION";
	
	public static final String COL_TOTALQUALITYEVALUATION = "TOTAL_QUALITY_EVALUATION";
	
	public static final String COL_TOTALSPEEDEVALUATION = "TOTAL_SPEED_EVALUATION";
	
	public static final String COL_TOTALCOUNTEVALUATION = "TOTAL_COUNT_EVALUATION";
	
	public static final String COL_APPTYPE = "APP_TYPE";
	
	
	
	private Long id;
	private Long merchantId;
	private Integer grabFrequency;
	private Integer serviceFrequency;
	private java.math.BigDecimal totalIncomePrice;
	private java.math.BigDecimal totalWithdrawPrice;
	private java.math.BigDecimal surplusPrice;
	private java.math.BigDecimal orderSurplusPrice;
	private Integer totalAttitudeEvaluation;
	private Integer totalQualityEvaluation;
	private Integer totalSpeedEvaluation;
	private Integer totalCountEvaluation;
	private String appType;

	public MerchantStatistics() {}

	public MerchantStatistics(Long id) {
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
	
	public void setGrabFrequency(Integer value) {
		this.grabFrequency = value;
	}
	
	public Integer getGrabFrequency() {
		return this.grabFrequency;
	}
	
	public void setServiceFrequency(Integer value) {
		this.serviceFrequency = value;
	}
	
	public Integer getServiceFrequency() {
		return this.serviceFrequency;
	}
	
	public void setTotalIncomePrice(java.math.BigDecimal value) {
		this.totalIncomePrice = value;
	}
	
	public java.math.BigDecimal getTotalIncomePrice() {
		return this.totalIncomePrice;
	}
	
	public void setTotalWithdrawPrice(java.math.BigDecimal value) {
		this.totalWithdrawPrice = value;
	}
	
	public java.math.BigDecimal getTotalWithdrawPrice() {
		return this.totalWithdrawPrice;
	}
	
	public void setSurplusPrice(java.math.BigDecimal value) {
		this.surplusPrice = value;
	}
	
	public java.math.BigDecimal getSurplusPrice() {
		return this.surplusPrice;
	}
	
	public void setOrderSurplusPrice(java.math.BigDecimal value) {
		this.orderSurplusPrice = value;
	}
	
	public java.math.BigDecimal getOrderSurplusPrice() {
		return this.orderSurplusPrice;
	}
	
	public void setTotalAttitudeEvaluation(Integer value) {
		this.totalAttitudeEvaluation = value;
	}
	
	public Integer getTotalAttitudeEvaluation() {
		return this.totalAttitudeEvaluation;
	}
	
	public void setTotalQualityEvaluation(Integer value) {
		this.totalQualityEvaluation = value;
	}
	
	public Integer getTotalQualityEvaluation() {
		return this.totalQualityEvaluation;
	}
	
	public void setTotalSpeedEvaluation(Integer value) {
		this.totalSpeedEvaluation = value;
	}
	
	public Integer getTotalSpeedEvaluation() {
		return this.totalSpeedEvaluation;
	}
	
	public void setTotalCountEvaluation(Integer value) {
		this.totalCountEvaluation = value;
	}
	
	public Integer getTotalCountEvaluation() {
		return this.totalCountEvaluation;
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
			.append("MerchantId",getMerchantId())
			.append("GrabFrequency",getGrabFrequency())
			.append("ServiceFrequency",getServiceFrequency())
			.append("TotalIncomePrice",getTotalIncomePrice())
			.append("TotalWithdrawPrice",getTotalWithdrawPrice())
			.append("SurplusPrice",getSurplusPrice())
			.append("OrderSurplusPrice",getOrderSurplusPrice())
			.append("TotalAttitudeEvaluation",getTotalAttitudeEvaluation())
			.append("TotalQualityEvaluation",getTotalQualityEvaluation())
			.append("TotalSpeedEvaluation",getTotalSpeedEvaluation())
			.append("TotalCountEvaluation",getTotalCountEvaluation())
			.append("AppType",getAppType())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MerchantStatistics)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MerchantStatistics other = (MerchantStatistics)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

