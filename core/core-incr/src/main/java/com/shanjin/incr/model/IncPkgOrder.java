

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-23 16:40:14 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class IncPkgOrder {
	
  public static final String TABLE_NAME = "inc_pkg_order";

	public static final String TABLE_ALIAS = "IncPkgOrder";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_PKGID = "PKG_ID";
	
	public static final String COL_USERID = "USER_ID";
	
	public static final String COL_MERCHANTID = "MERCHANT_ID";
	
	public static final String COL_EFFICTIVETIME = "EFFICTIVE_TIME";
	
	public static final String COL_LOSEEFFICTIVETIME = "LOSE_EFFICTIVE_TIME";
	
	public static final String COL_TRADENO = "TRADE_NO";
	
	public static final String COL_PAYTYPE = "PAY_TYPE";
	
	public static final String COL_STATUS = "STATUS";
	
	public static final String COL_ORDERNO = "ORDER_NO";
	
	public static final String COL_TRADEAMOUNT = "TRADE_AMOUNT";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATER = "CREATER";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFER = "MODIFER";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	
	
	private Long id;
	private String name;
	private Long pkgId;
	private Long userId;
	private Long merchantId;
	private java.util.Date effictiveTime;
	private java.util.Date loseEffictiveTime;
	private String tradeNo;
	private Integer payType;
	private Integer status;
	private String orderNo;
	private java.math.BigDecimal tradeAmount;
	private java.util.Date createTime;
	private String creater;
	private java.util.Date modifyTime;
	private String modifer;
	private Boolean isDel;

	public IncPkgOrder() {}

	public IncPkgOrder(Long id) {
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
	
	public void setPkgId(Long value) {
		this.pkgId = value;
	}
	
	public Long getPkgId() {
		return this.pkgId;
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
	
	public void setEffictiveTime(java.util.Date value) {
		this.effictiveTime = value;
	}
	
	public java.util.Date getEffictiveTime() {
		return this.effictiveTime;
	}
	
	public void setLoseEffictiveTime(java.util.Date value) {
		this.loseEffictiveTime = value;
	}
	
	public java.util.Date getLoseEffictiveTime() {
		return this.loseEffictiveTime;
	}
	
	public void setTradeNo(String value) {
		this.tradeNo = value;
	}
	
	public String getTradeNo() {
		return this.tradeNo;
	}
	
	public void setPayType(Integer value) {
		this.payType = value;
	}
	
	public Integer getPayType() {
		return this.payType;
	}
	
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	public Integer getStatus() {
		return this.status;
	}
	
	public void setOrderNo(String value) {
		this.orderNo = value;
	}
	
	public String getOrderNo() {
		return this.orderNo;
	}
	
	public void setTradeAmount(java.math.BigDecimal value) {
		this.tradeAmount = value;
	}
	
	public java.math.BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreater(String value) {
		this.creater = value;
	}
	
	public String getCreater() {
		return this.creater;
	}
	
	public void setModifyTime(java.util.Date value) {
		this.modifyTime = value;
	}
	
	public java.util.Date getModifyTime() {
		return this.modifyTime;
	}
	
	public void setModifer(String value) {
		this.modifer = value;
	}
	
	public String getModifer() {
		return this.modifer;
	}
	
	public void setIsDel(Boolean value) {
		this.isDel = value;
	}
	
	public Boolean getIsDel() {
		return this.isDel;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("PkgId",getPkgId())
			.append("UserId",getUserId())
			.append("MerchantId",getMerchantId())
			.append("EffictiveTime",getEffictiveTime())
			.append("LoseEffictiveTime",getLoseEffictiveTime())
			.append("TradeNo",getTradeNo())
			.append("PayType",getPayType())
			.append("Status",getStatus())
			.append("OrderNo",getOrderNo())
			.append("TradeAmount",getTradeAmount())
			.append("CreateTime",getCreateTime())
			.append("Creater",getCreater())
			.append("ModifyTime",getModifyTime())
			.append("Modifer",getModifer())
			.append("IsDel",getIsDel())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof IncPkgOrder)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		IncPkgOrder other = (IncPkgOrder)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

