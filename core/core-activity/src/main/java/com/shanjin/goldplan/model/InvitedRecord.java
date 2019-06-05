

package com.shanjin.goldplan.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-19 15:26:55 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class InvitedRecord {
	
  public static final String TABLE_NAME = "invited_record";

	public static final String TABLE_ALIAS = "InvitedRecord";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_ACTIVITYID = "ACTIVITY_ID";
	
	public static final String COL_USERID = "USER_ID";
	
	public static final String COL_USERPHONE = "USER_PHONE";
	
	public static final String COL_INVITEDPHONE = "INVITED_PHONE";
	
	public static final String COL_INVITEDTIME = "INVITED_TIME";
	
	public static final String COL_INVITEDSUCCESSTIME = "INVITED_SUCCESS_TIME";
	
	public static final String COL_INVITEDSOURCE = "INVITED_SOURCE";
	
	public static final String COL_INVITEDIP = "INVITED_IP";
	
	public static final String COL_STATUS = "STATUS";
	
	
	
	private Long id;
	private Long activityId;
	private Long userId;
	private String userPhone;
	private String invitedPhone;
	private java.util.Date invitedTime;
	private java.util.Date invitedSuccessTime;
	private String invitedSource;
	private String invitedIp;
	private Integer status;

	public InvitedRecord() {}

	public InvitedRecord(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setActivityId(Long value) {
		this.activityId = value;
	}
	
	public Long getActivityId() {
		return this.activityId;
	}
	
	public void setUserId(Long value) {
		this.userId = value;
	}
	
	public Long getUserId() {
		return this.userId;
	}
	
	public void setUserPhone(String value) {
		this.userPhone = value;
	}
	
	public String getUserPhone() {
		return this.userPhone;
	}
	
	public void setInvitedPhone(String value) {
		this.invitedPhone = value;
	}
	
	public String getInvitedPhone() {
		return this.invitedPhone;
	}
	
	public void setInvitedTime(java.util.Date value) {
		this.invitedTime = value;
	}
	
	public java.util.Date getInvitedTime() {
		return this.invitedTime;
	}
	
	public void setInvitedSuccessTime(java.util.Date value) {
		this.invitedSuccessTime = value;
	}
	
	public java.util.Date getInvitedSuccessTime() {
		return this.invitedSuccessTime;
	}
	
	public void setInvitedSource(String value) {
		this.invitedSource = value;
	}
	
	public String getInvitedSource() {
		return this.invitedSource;
	}
	
	public void setInvitedIp(String value) {
		this.invitedIp = value;
	}
	
	public String getInvitedIp() {
		return this.invitedIp;
	}
	
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	public Integer getStatus() {
		return this.status;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("ActivityId",getActivityId())
			.append("UserId",getUserId())
			.append("UserPhone",getUserPhone())
			.append("InvitedPhone",getInvitedPhone())
			.append("InvitedTime",getInvitedTime())
			.append("InvitedSuccessTime",getInvitedSuccessTime())
			.append("InvitedSource",getInvitedSource())
			.append("InvitedIp",getInvitedIp())
			.append("Status",getStatus())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof InvitedRecord)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		InvitedRecord other = (InvitedRecord)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

