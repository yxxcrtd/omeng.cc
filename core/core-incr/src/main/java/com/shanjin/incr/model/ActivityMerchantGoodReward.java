

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-30 17:26:11 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class ActivityMerchantGoodReward {
	
  public static final String TABLE_NAME = "activity_merchant_good_reward";

	public static final String TABLE_ALIAS = "ActivityMerchantGoodReward";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_USERID = "USER_ID";
	
	public static final String COL_MERCHANTID = "MERCHANT_ID";
	
	public static final String COL_ACTIVITYID = "ACTIVITY_ID";
	
	public static final String COL_PKGID = "PKG_ID";
	
	public static final String COL_SCORE = "SCORE";
	
	public static final String COL_REWARDDATE = "REWARD_DATE";
	
	public static final String COL_REWARDCOUNT = "REWARD_COUNT";
	
	public static final String COL_REWARDAMOUNT = "REWARD_AMOUNT";
	
	public static final String COL_REWARDMONTH = "REWARD_MONTH";
	
	public static final String COL_ISTRANSFER = "IS_TRANSFER";
	
	public static final String COL_ACCOUNTTIME = "ACCOUNT_TIME";
	
	public static final String COL_USERNAME = "USER_NAME";
	
	public static final String COL_REMARKS = "REMARKS";
	
	
	
	private Long id;
	private Long userId;
	private Long merchantId;
	private Long activityId;
	private Long pkgId;
	private java.math.BigDecimal score;
	private java.util.Date rewardDate;
	private Integer rewardCount;
	private java.math.BigDecimal rewardAmount;
	private String rewardMonth;
	private Integer isTransfer;
	private java.util.Date accountTime;
	private String userName;
	private String remarks;

	public ActivityMerchantGoodReward() {}

	public ActivityMerchantGoodReward(Long id) {
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
	
	public void setMerchantId(Long value) {
		this.merchantId = value;
	}
	
	public Long getMerchantId() {
		return this.merchantId;
	}
	
	public void setActivityId(Long value) {
		this.activityId = value;
	}
	
	public Long getActivityId() {
		return this.activityId;
	}
	
	public void setPkgId(Long value) {
		this.pkgId = value;
	}
	
	public Long getPkgId() {
		return this.pkgId;
	}
	
	public void setScore(java.math.BigDecimal value) {
		this.score = value;
	}
	
	public java.math.BigDecimal getScore() {
		return this.score;
	}
	
	public void setRewardDate(java.util.Date value) {
		this.rewardDate = value;
	}
	
	public java.util.Date getRewardDate() {
		return this.rewardDate;
	}
	
	public void setRewardCount(Integer value) {
		this.rewardCount = value;
	}
	
	public Integer getRewardCount() {
		return this.rewardCount;
	}
	
	public void setRewardAmount(java.math.BigDecimal value) {
		this.rewardAmount = value;
	}
	
	public java.math.BigDecimal getRewardAmount() {
		return this.rewardAmount;
	}
	
	public void setRewardMonth(String value) {
		this.rewardMonth = value;
	}
	
	public String getRewardMonth() {
		return this.rewardMonth;
	}
	
	public void setIsTransfer(Integer value) {
		this.isTransfer = value;
	}
	
	public Integer getIsTransfer() {
		return this.isTransfer;
	}
	
	public void setAccountTime(java.util.Date value) {
		this.accountTime = value;
	}
	
	public java.util.Date getAccountTime() {
		return this.accountTime;
	}
	
	public void setUserName(String value) {
		this.userName = value;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public void setRemarks(String value) {
		this.remarks = value;
	}
	
	public String getRemarks() {
		return this.remarks;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("UserId",getUserId())
			.append("MerchantId",getMerchantId())
			.append("ActivityId",getActivityId())
			.append("PkgId",getPkgId())
			.append("Score",getScore())
			.append("RewardDate",getRewardDate())
			.append("RewardCount",getRewardCount())
			.append("RewardAmount",getRewardAmount())
			.append("RewardMonth",getRewardMonth())
			.append("IsTransfer",getIsTransfer())
			.append("AccountTime",getAccountTime())
			.append("UserName",getUserName())
			.append("Remarks",getRemarks())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof ActivityMerchantGoodReward)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		ActivityMerchantGoodReward other = (ActivityMerchantGoodReward)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

