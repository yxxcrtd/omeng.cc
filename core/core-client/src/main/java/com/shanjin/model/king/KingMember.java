

package com.shanjin.model.king;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-10-28 16:05:14 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class KingMember {
	
  public static final String TABLE_NAME = "king_member";

	public static final String TABLE_ALIAS = "KingMember";
	
	public static final String COL_USERID = "USER_ID";
	
	public static final String COL_STARTTIME = "START_TIME";
	
	public static final String COL_ENDTIME = "END_TIME";
	
	public static final String COL_ORDERNO = "ORDER_NO";
	
	public static final String COL_INVITECODE = "INVITE_CODE";
	
	public static final String COL_CONSGOLDDENO = "CONS_GOLD_DENO";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATOR = "CREATOR";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFIER = "MODIFIER";
	
	
	
	private Long userId;
	private java.util.Date startTime;
	private java.util.Date endTime;
	private String orderNo;
	private String inviteCode;
	private java.math.BigDecimal consGoldDeno;
	private java.util.Date createTime;
	private String creator;
	private java.util.Date modifyTime;
	private String modifier;

	public KingMember() {}

	public KingMember(Long userId) {
		this.userId = userId;
	}

	public void setUserId(Long value) {
		this.userId = value;
	}
	
	public Long getUserId() {
		return this.userId;
	}
	
	public void setStartTime(java.util.Date value) {
		this.startTime = value;
	}
	
	public java.util.Date getStartTime() {
		return this.startTime;
	}
	
	public void setEndTime(java.util.Date value) {
		this.endTime = value;
	}
	
	public java.util.Date getEndTime() {
		return this.endTime;
	}
	
	public void setOrderNo(String value) {
		this.orderNo = value;
	}
	
	public String getOrderNo() {
		return this.orderNo;
	}
	
	public void setInviteCode(String value) {
		this.inviteCode = value;
	}
	
	public String getInviteCode() {
		return this.inviteCode;
	}
	
	public void setConsGoldDeno(java.math.BigDecimal value) {
		this.consGoldDeno = value;
	}
	
	public java.math.BigDecimal getConsGoldDeno() {
		return this.consGoldDeno;
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
			.append("StartTime",getStartTime())
			.append("EndTime",getEndTime())
			.append("OrderNo",getOrderNo())
			.append("InviteCode",getInviteCode())
			.append("ConsGoldDeno",getConsGoldDeno())
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
		if(!(obj instanceof KingMember)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		KingMember other = (KingMember)obj;
		return new EqualsBuilder()
			.append(getUserId(),other.getUserId())
			.isEquals();
	}
}

