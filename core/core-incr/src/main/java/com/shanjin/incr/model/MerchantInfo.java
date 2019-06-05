

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-17 19:21:29 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MerchantInfo {
	
  public static final String TABLE_NAME = "merchant_info";

	public static final String TABLE_ALIAS = "MerchantInfo";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_DETAIL = "DETAIL";
	
	public static final String COL_SHORTNAME = "SHORT_NAME";
	
	public static final String COL_ADDRESS = "ADDRESS";
	
	public static final String COL_PROVINCE = "PROVINCE";
	
	public static final String COL_CITY = "CITY";
	
	public static final String COL_LOCATIONADDRESS = "LOCATION_ADDRESS";
	
	public static final String COL_DETAILADDRESS = "DETAIL_ADDRESS";
	
	public static final String COL_LONGITUDE = "LONGITUDE";
	
	public static final String COL_LATITUDE = "LATITUDE";
	
	public static final String COL_JOINTIME = "JOIN_TIME";
	
	public static final String COL_MONEYPASSWORD = "MONEY_PASSWORD";
	
	public static final String COL_MONEYREALNAME = "MONEY_REAL_NAME";
	
	public static final String COL_MONEYIDNO = "MONEY_ID_NO";
	
	public static final String COL_IP = "IP";
	
	public static final String COL_MICROWEBSITEURL = "MICRO_WEBSITE_URL";
	
	public static final String COL_MAXEMPLOYEENUM = "MAX_EMPLOYEE_NUM";
	
	public static final String COL_VIPLEVEL = "VIP_LEVEL";
	
	public static final String COL_ORDERPRICE = "ORDER_PRICE";
	
	public static final String COL_APPTYPE = "APP_TYPE";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	public static final String COL_INVITATIONCODE = "INVITATION_CODE";
	
	public static final String COL_MAXMSGID = "MAX_MSG_ID";
	
	public static final String COL_CATALOGID = "CATALOG_ID";
	
	public static final String COL_AUTHTYPE = "AUTH_TYPE";
	
	public static final String COL_AUTHSTATUS = "AUTH_STATUS";
	
	public static final String COL_FLAG = "FLAG";
	
	public static final String COL_LASTACTIVETIME = "LAST_ACTIVE_TIME";
	
	public static final String COL_MAPTYPE = "MAP_TYPE";
	
	public static final String COL_ISPRIVATEASSISTANT = "IS_PRIVATE_ASSISTANT";
	
	
	
	private Long id;
	private String name;
	private String detail;
	private String shortName;
	private String address;
	private String province;
	private String city;
	private String locationAddress;
	private String detailAddress;
	private Double longitude;
	private Double latitude;
	private java.util.Date joinTime;
	private String moneyPassword;
	private String moneyRealName;
	private String moneyIdNo;
	private String ip;
	private String microWebsiteUrl;
	private Integer maxEmployeeNum;
	private Integer vipLevel;
	private Long orderPrice;
	private String appType;
	private Integer isDel;
	private String invitationCode;
	private Long maxMsgId;
	private Integer catalogId;
	private Integer authType;
	private Integer authStatus;
	private Integer flag;
	private java.util.Date lastActiveTime;
	private Integer mapType;
	private Integer isPrivateAssistant;

	public MerchantInfo() {}

	public MerchantInfo(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setDetail(String value) {
		this.detail = value;
	}
	
	public String getDetail() {
		return this.detail;
	}
	
	public void setShortName(String value) {
		this.shortName = value;
	}
	
	public String getShortName() {
		return this.shortName;
	}
	
	public void setAddress(String value) {
		this.address = value;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public void setProvince(String value) {
		this.province = value;
	}
	
	public String getProvince() {
		return this.province;
	}
	
	public void setCity(String value) {
		this.city = value;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public void setLocationAddress(String value) {
		this.locationAddress = value;
	}
	
	public String getLocationAddress() {
		return this.locationAddress;
	}
	
	public void setDetailAddress(String value) {
		this.detailAddress = value;
	}
	
	public String getDetailAddress() {
		return this.detailAddress;
	}
	
	public void setLongitude(Double value) {
		this.longitude = value;
	}
	
	public Double getLongitude() {
		return this.longitude;
	}
	
	public void setLatitude(Double value) {
		this.latitude = value;
	}
	
	public Double getLatitude() {
		return this.latitude;
	}
	
	public void setJoinTime(java.util.Date value) {
		this.joinTime = value;
	}
	
	public java.util.Date getJoinTime() {
		return this.joinTime;
	}
	
	public void setMoneyPassword(String value) {
		this.moneyPassword = value;
	}
	
	public String getMoneyPassword() {
		return this.moneyPassword;
	}
	
	public void setMoneyRealName(String value) {
		this.moneyRealName = value;
	}
	
	public String getMoneyRealName() {
		return this.moneyRealName;
	}
	
	public void setMoneyIdNo(String value) {
		this.moneyIdNo = value;
	}
	
	public String getMoneyIdNo() {
		return this.moneyIdNo;
	}
	
	public void setIp(String value) {
		this.ip = value;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void setMicroWebsiteUrl(String value) {
		this.microWebsiteUrl = value;
	}
	
	public String getMicroWebsiteUrl() {
		return this.microWebsiteUrl;
	}
	
	public void setMaxEmployeeNum(Integer value) {
		this.maxEmployeeNum = value;
	}
	
	public Integer getMaxEmployeeNum() {
		return this.maxEmployeeNum;
	}
	
	public void setVipLevel(Integer value) {
		this.vipLevel = value;
	}
	
	public Integer getVipLevel() {
		return this.vipLevel;
	}
	
	public void setOrderPrice(Long value) {
		this.orderPrice = value;
	}
	
	public Long getOrderPrice() {
		return this.orderPrice;
	}
	
	public void setAppType(String value) {
		this.appType = value;
	}
	
	public String getAppType() {
		return this.appType;
	}
	
	public void setIsDel(Integer value) {
		this.isDel = value;
	}
	
	public Integer getIsDel() {
		return this.isDel;
	}
	
	public void setInvitationCode(String value) {
		this.invitationCode = value;
	}
	
	public String getInvitationCode() {
		return this.invitationCode;
	}
	
	public void setMaxMsgId(Long value) {
		this.maxMsgId = value;
	}
	
	public Long getMaxMsgId() {
		return this.maxMsgId;
	}
	
	public void setCatalogId(Integer value) {
		this.catalogId = value;
	}
	
	public Integer getCatalogId() {
		return this.catalogId;
	}
	
	public void setAuthType(Integer value) {
		this.authType = value;
	}
	
	public Integer getAuthType() {
		return this.authType;
	}
	
	public void setAuthStatus(Integer value) {
		this.authStatus = value;
	}
	
	public Integer getAuthStatus() {
		return this.authStatus;
	}
	
	public void setFlag(Integer value) {
		this.flag = value;
	}
	
	public Integer getFlag() {
		return this.flag;
	}
	
	public void setLastActiveTime(java.util.Date value) {
		this.lastActiveTime = value;
	}
	
	public java.util.Date getLastActiveTime() {
		return this.lastActiveTime;
	}
	
	public void setMapType(Integer value) {
		this.mapType = value;
	}
	
	public Integer getMapType() {
		return this.mapType;
	}
	
	public void setIsPrivateAssistant(Integer value) {
		this.isPrivateAssistant = value;
	}
	
	public Integer getIsPrivateAssistant() {
		return this.isPrivateAssistant;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("Detail",getDetail())
			.append("ShortName",getShortName())
			.append("Address",getAddress())
			.append("Province",getProvince())
			.append("City",getCity())
			.append("LocationAddress",getLocationAddress())
			.append("DetailAddress",getDetailAddress())
			.append("Longitude",getLongitude())
			.append("Latitude",getLatitude())
			.append("JoinTime",getJoinTime())
			.append("MoneyPassword",getMoneyPassword())
			.append("MoneyRealName",getMoneyRealName())
			.append("MoneyIdNo",getMoneyIdNo())
			.append("Ip",getIp())
			.append("MicroWebsiteUrl",getMicroWebsiteUrl())
			.append("MaxEmployeeNum",getMaxEmployeeNum())
			.append("VipLevel",getVipLevel())
			.append("OrderPrice",getOrderPrice())
			.append("AppType",getAppType())
			.append("IsDel",getIsDel())
			.append("InvitationCode",getInvitationCode())
			.append("MaxMsgId",getMaxMsgId())
			.append("CatalogId",getCatalogId())
			.append("AuthType",getAuthType())
			.append("AuthStatus",getAuthStatus())
			.append("Flag",getFlag())
			.append("LastActiveTime",getLastActiveTime())
			.append("MapType",getMapType())
			.append("IsPrivateAssistant",getIsPrivateAssistant())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MerchantInfo)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MerchantInfo other = (MerchantInfo)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

