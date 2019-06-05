

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-18 17:29:01 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MerchantAttachment {
	
  public static final String TABLE_NAME = "merchant_attachment";

	public static final String TABLE_ALIAS = "MerchantAttachment";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_MERCHANTID = "MERCHANT_ID";
	
	public static final String COL_ATTACHMENTTYPE = "ATTACHMENT_TYPE";
	
	public static final String COL_ATTACHMENTUSE = "ATTACHMENT_USE";
	
	public static final String COL_PATH = "PATH";
	
	public static final String COL_JOINTIME = "JOIN_TIME";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	
	
	private Long id;
	private Long merchantId;
	private Integer attachmentType;
	private Integer attachmentUse;
	private String path;
	private java.util.Date joinTime;
	private Integer isDel;

	public MerchantAttachment() {}

	public MerchantAttachment(Long id) {
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
	
	public void setAttachmentType(Integer value) {
		this.attachmentType = value;
	}
	
	public Integer getAttachmentType() {
		return this.attachmentType;
	}
	
	public void setAttachmentUse(Integer value) {
		this.attachmentUse = value;
	}
	
	public Integer getAttachmentUse() {
		return this.attachmentUse;
	}
	
	public void setPath(String value) {
		this.path = value;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setJoinTime(java.util.Date value) {
		this.joinTime = value;
	}
	
	public java.util.Date getJoinTime() {
		return this.joinTime;
	}
	
	public void setIsDel(Integer value) {
		this.isDel = value;
	}
	
	public Integer getIsDel() {
		return this.isDel;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("MerchantId",getMerchantId())
			.append("AttachmentType",getAttachmentType())
			.append("AttachmentUse",getAttachmentUse())
			.append("Path",getPath())
			.append("JoinTime",getJoinTime())
			.append("IsDel",getIsDel())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MerchantAttachment)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MerchantAttachment other = (MerchantAttachment)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

