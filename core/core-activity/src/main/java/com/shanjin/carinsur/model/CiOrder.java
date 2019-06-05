

package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.List;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-01 03:28:29 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class CiOrder {

	public static final String TABLE_NAME = "ci_order";

	public static final String TABLE_ALIAS = "CiOrder";

	public static final String COL_ID = "ID";

	public static final String COL_ORDERNO = "ORDER_NO";

	public static final String COL_NAME = "NAME";

	public static final String COL_STATUS = "STATUS";

	public static final String COL_USERID = "USER_ID";

	public static final String COL_BIZNO = "BIZ_NO";

	public static final String COL_BIZTYPE = "BIZ_TYPE";

	public static final String COL_BIZRESULT = "BIZ_RESULT";

	public static final String COL_BIZDESC = "BIZ_DESC";

	public static final String COL_CREATETIME = "CREATE_TIME";

	public static final String COL_MODIFYTIME = "MODIFY_TIME";

	public static final String COL_MODIFIER = "MODIFIER";

	public static final String COL_AMOUNT = "AMOUNT";

	private java.lang.Long id;
	private java.lang.String orderNo;
	private java.lang.String name;
	private Integer status;
	private java.lang.Long userId;
	private java.lang.String bizNo;
	private java.lang.String bizType;
	private java.lang.String bizResult;
	private java.lang.String bizDesc;
	private java.util.Date createTime;
	private java.util.Date modifyTime;
	private java.lang.String modifier;
	private BigDecimal amount;


	//扩展字段
	private List<String> bizResultList;

	public CiOrder() {}

	public CiOrder(Long id) {
		this.id = id;
	}

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
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
	
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	public Integer getStatus() {
		return this.status;
	}
	
	public void setUserId(Long value) {
		this.userId = value;
	}
	
	public Long getUserId() {
		return this.userId;
	}
	
	public void setBizNo(String value) {
		this.bizNo = value;
	}
	
	public String getBizNo() {
		return this.bizNo;
	}
	
	public void setBizType(String value) {
		this.bizType = value;
	}
	
	public String getBizType() {
		return this.bizType;
	}
	
	public void setBizResult(String value) {
		this.bizResult = value;
	}
	
	public String getBizResult() {
		return this.bizResult;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setModifyTime(java.util.Date value) {
		this.modifyTime = value;
	}
	
	public java.util.Date getModifyTime() {
		return this.modifyTime;
	}


	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("Id",getId())
				.append("OrderNo",getOrderNo())
				.append("Name",getName())
				.append("Status",getStatus())
				.append("UserId",getUserId())
				.append("BizNo",getBizNo())
				.append("BizType",getBizType())
				.append("BizResult",getBizResult())
				.append("BizDesc",getBizDesc())
				.append("CreateTime",getCreateTime())
				.append("ModifyTime",getModifyTime())
				.append("Modifier",getModifier())
				.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof CiOrder)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		CiOrder other = (CiOrder)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}

	public List<String> getBizResultList() {
		return bizResultList;
	}

	public void setBizResultList(List<String> bizResultList) {
		this.bizResultList = bizResultList;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getBizDesc() {
		return bizDesc;
	}

	public void setBizDesc(String bizDesc) {
		this.bizDesc = bizDesc;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}

