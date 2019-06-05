

package com.shanjin.model.king;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-10-28 16:05:23 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class KingUserAsset {
	
  public static final String TABLE_NAME = "king_user_asset";

	public static final String TABLE_ALIAS = "KingUserAsset";
	
	public static final String COL_USERID = "USER_ID";
	
	public static final String COL_ASSETAMOUNT = "ASSET_AMOUNT";
	
	public static final String COL_BLOCKAMOUNT = "BLOCK_AMOUNT";
	
	public static final String COL_LIMITEDAMOUNT = "LIMITED_AMOUNT";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATOR = "CREATOR";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFIER = "MODIFIER";
	
	
	
	private Long userId;
	private java.math.BigDecimal assetAmount;
	private java.math.BigDecimal blockAmount;
	private java.math.BigDecimal limitedAmount;
	private java.util.Date createTime;
	private String creator;
	private java.util.Date modifyTime;
	private String modifier;

	public KingUserAsset() {}

	public KingUserAsset(Long userId) {
		this.userId = userId;
	}

	public void setUserId(Long value) {
		this.userId = value;
	}
	
	public Long getUserId() {
		return this.userId;
	}
	
	public void setAssetAmount(java.math.BigDecimal value) {
		this.assetAmount = value;
	}
	
	public java.math.BigDecimal getAssetAmount() {
		return this.assetAmount;
	}
	
	public void setBlockAmount(java.math.BigDecimal value) {
		this.blockAmount = value;
	}
	
	public java.math.BigDecimal getBlockAmount() {
		return this.blockAmount;
	}
	
	public void setLimitedAmount(java.math.BigDecimal value) {
		this.limitedAmount = value;
	}
	
	public java.math.BigDecimal getLimitedAmount() {
		return this.limitedAmount;
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
	
	public void setModifyTime(java.util.Date value) {
		this.modifyTime = value;
	}
	
	public java.util.Date getModifyTime() {
		return this.modifyTime;
	}
	
	public void setModifier(String value) {
		this.modifier = value;
	}
	
	public String getModifier() {
		return this.modifier;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("UserId",getUserId())
			.append("AssetAmount",getAssetAmount())
			.append("BlockAmount",getBlockAmount())
			.append("LimitedAmount",getLimitedAmount())
			.append("CreateTime",getCreateTime())
			.append("Creator",getCreator())
			.append("ModifyTime",getModifyTime())
			.append("Modifier",getModifier())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getUserId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof KingUserAsset)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		KingUserAsset other = (KingUserAsset)obj;
		return new EqualsBuilder()
			.append(getUserId(),other.getUserId())
			.isEquals();
	}
}

