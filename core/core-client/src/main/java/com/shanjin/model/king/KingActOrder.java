

package com.shanjin.model.king;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-11-04 16:44:52 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class KingActOrder {
	
  public static final String TABLE_NAME = "king_act_order";

	public static final String TABLE_ALIAS = "KingActOrder";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_USERID = "USER_ID";
	
	public static final String COL_BIZTYPE = "BIZ_TYPE";
	
	public static final String COL_ORDERNO = "ORDER_NO";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_BIZNO = "BIZ_NO";
	
	public static final String COL_INVITECODE = "INVITE_CODE";
	
	public static final String COL_MERCHANTID = "MERCHANT_ID";
	
	public static final String COL_STATUS = "STATUS";
	
	public static final String COL_ORDERAMOUNT = "ORDER_AMOUNT";
	
	public static final String COL_PAYTYPE = "PAY_TYPE";
	
	public static final String COL_PAYTIME = "PAY_TIME";
	
	public static final String COL_PAYAMOUNT = "PAY_AMOUNT";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATOR = "CREATOR";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFIER = "MODIFIER";
	
	public static final String COL_REMARK = "REMARK";
	
	
	
	private Long id;
	private Long userId;
	private String bizType;
	private String orderNo;
	private String name;
	private String bizNo;
	private String inviteCode;
	private Long merchantId;
	private Integer status;
	private java.math.BigDecimal orderAmount;
	private Integer payType;
	private java.util.Date payTime;
	private java.math.BigDecimal payAmount;
	private java.util.Date createTime;
	private String creator;
	private java.util.Date modifyTime;
	private String modifier;
	private String remark;

	public KingActOrder() {}

	public KingActOrder(Long id) {
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
	
	public void setBizType(String value) {
		this.bizType = value;
	}
	
	public String getBizType() {
		return this.bizType;
	}
	
	public void setOrderNo(String value) {
		this.orderNo = value;
	}
	
	public String getOrderNo() {
		return this.orderNo;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setBizNo(String value) {
		this.bizNo = value;
	}
	
	public String getBizNo() {
		return this.bizNo;
	}
	
	public void setInviteCode(String value) {
		this.inviteCode = value;
	}
	
	public String getInviteCode() {
		return this.inviteCode;
	}
	
	public void setMerchantId(Long value) {
		this.merchantId = value;
	}
	
	public Long getMerchantId() {
		return this.merchantId;
	}
	
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	public Integer getStatus() {
		return this.status;
	}
	
	public void setOrderAmount(java.math.BigDecimal value) {
		this.orderAmount = value;
	}
	
	public java.math.BigDecimal getOrderAmount() {
		return this.orderAmount;
	}
	
	public void setPayType(Integer value) {
		this.payType = value;
	}
	
	public Integer getPayType() {
		return this.payType;
	}
	
	public void setPayTime(java.util.Date value) {
		this.payTime = value;
	}
	
	public java.util.Date getPayTime() {
		return this.payTime;
	}
	
	public void setPayAmount(java.math.BigDecimal value) {
		this.payAmount = value;
	}
	
	public java.math.BigDecimal getPayAmount() {
		return this.payAmount;
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
	
	public void setRemark(String value) {
		this.remark = value;
	}
	
	public String getRemark() {
		return this.remark;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("UserId",getUserId())
			.append("BizType",getBizType())
			.append("OrderNo",getOrderNo())
			.append("Name",getName())
			.append("BizNo",getBizNo())
			.append("InviteCode",getInviteCode())
			.append("MerchantId",getMerchantId())
			.append("Status",getStatus())
			.append("OrderAmount",getOrderAmount())
			.append("PayType",getPayType())
			.append("PayTime",getPayTime())
			.append("PayAmount",getPayAmount())
			.append("CreateTime",getCreateTime())
			.append("Creator",getCreator())
			.append("ModifyTime",getModifyTime())
			.append("Modifier",getModifier())
			.append("Remark",getRemark())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof KingActOrder)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		KingActOrder other = (KingActOrder)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

