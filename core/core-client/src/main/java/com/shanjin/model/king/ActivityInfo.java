

package com.shanjin.model.king;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-10-29 10:56:33 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class ActivityInfo {
	
  public static final String TABLE_NAME = "activity_info";

	public static final String TABLE_ALIAS = "ActivityInfo";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_TITLE = "TITLE";
	
	public static final String COL_SUBTITLE = "SUBTITLE";
	
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	
	public static final String COL_STIME = "STIME";
	
	public static final String COL_ETIME = "ETIME";
	
	public static final String COL_SIMAGE = "SIMAGE";
	
	public static final String COL_BIMAGE = "BIMAGE";
	
	public static final String COL_NEWIMAGE = "NEW_IMAGE";
	
	public static final String COL_ACTIVITYTYPEID = "ACTIVITY_TYPE_ID";
	
	public static final String COL_ISPUB = "IS_PUB";
	
	public static final String COL_SHAREDESC = "SHARE_DESC";
	
	public static final String COL_PV = "PV";
	
	public static final String COL_UV = "UV";
	
	public static final String COL_URL = "URL";
	
	public static final String COL_NEWURL = "NEW_URL";
	
	public static final String COL_DETAILTABLE = "DETAIL_TABLE";
	
	public static final String COL_SHARETITLE = "SHARE_TITLE";
	
	public static final String COL_SHAREIMAGE = "SHARE_IMAGE";
	
	public static final String COL_SHARELINK = "SHARE_LINK";
	
	
	
	private Long id;
	private String title;
	private String subtitle;
	private String description;
	private java.util.Date stime;
	private java.util.Date etime;
	private String simage;
	private String bimage;
	private String newImage;
	private Long activityTypeId;
	private Integer isPub;
	private String shareDesc;
	private Long pv;
	private Long uv;
	private String url;
	private String newUrl;
	private String detailTable;
	private String shareTitle;
	private String shareImage;
	private String shareLink;

	public ActivityInfo() {}

	public ActivityInfo(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setTitle(String value) {
		this.title = value;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setSubtitle(String value) {
		this.subtitle = value;
	}
	
	public String getSubtitle() {
		return this.subtitle;
	}
	
	public void setDescription(String value) {
		this.description = value;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setStime(java.util.Date value) {
		this.stime = value;
	}
	
	public java.util.Date getStime() {
		return this.stime;
	}
	
	public void setEtime(java.util.Date value) {
		this.etime = value;
	}
	
	public java.util.Date getEtime() {
		return this.etime;
	}
	
	public void setSimage(String value) {
		this.simage = value;
	}
	
	public String getSimage() {
		return this.simage;
	}
	
	public void setBimage(String value) {
		this.bimage = value;
	}
	
	public String getBimage() {
		return this.bimage;
	}
	
	public void setNewImage(String value) {
		this.newImage = value;
	}
	
	public String getNewImage() {
		return this.newImage;
	}
	
	public void setActivityTypeId(Long value) {
		this.activityTypeId = value;
	}
	
	public Long getActivityTypeId() {
		return this.activityTypeId;
	}
	
	public void setIsPub(Integer value) {
		this.isPub = value;
	}
	
	public Integer getIsPub() {
		return this.isPub;
	}
	
	public void setShareDesc(String value) {
		this.shareDesc = value;
	}
	
	public String getShareDesc() {
		return this.shareDesc;
	}
	
	public void setPv(Long value) {
		this.pv = value;
	}
	
	public Long getPv() {
		return this.pv;
	}
	
	public void setUv(Long value) {
		this.uv = value;
	}
	
	public Long getUv() {
		return this.uv;
	}
	
	public void setUrl(String value) {
		this.url = value;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setNewUrl(String value) {
		this.newUrl = value;
	}
	
	public String getNewUrl() {
		return this.newUrl;
	}
	
	public void setDetailTable(String value) {
		this.detailTable = value;
	}
	
	public String getDetailTable() {
		return this.detailTable;
	}
	
	public void setShareTitle(String value) {
		this.shareTitle = value;
	}
	
	public String getShareTitle() {
		return this.shareTitle;
	}
	
	public void setShareImage(String value) {
		this.shareImage = value;
	}
	
	public String getShareImage() {
		return this.shareImage;
	}
	
	public void setShareLink(String value) {
		this.shareLink = value;
	}
	
	public String getShareLink() {
		return this.shareLink;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Title",getTitle())
			.append("Subtitle",getSubtitle())
			.append("Description",getDescription())
			.append("Stime",getStime())
			.append("Etime",getEtime())
			.append("Simage",getSimage())
			.append("Bimage",getBimage())
			.append("NewImage",getNewImage())
			.append("ActivityTypeId",getActivityTypeId())
			.append("IsPub",getIsPub())
			.append("ShareDesc",getShareDesc())
			.append("Pv",getPv())
			.append("Uv",getUv())
			.append("Url",getUrl())
			.append("NewUrl",getNewUrl())
			.append("DetailTable",getDetailTable())
			.append("ShareTitle",getShareTitle())
			.append("ShareImage",getShareImage())
			.append("ShareLink",getShareLink())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof ActivityInfo)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		ActivityInfo other = (ActivityInfo)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

