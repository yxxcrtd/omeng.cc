

package com.shanjin.model.king;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-10-28 16:05:06 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class KingAssetConsRecord {
	
  public static final String TABLE_NAME = "king_asset_cons_record";

	public static final String TABLE_ALIAS = "KingAssetConsRecord";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_USERID = "USER_ID";
	
	public static final String COL_CONSAMOUNT = "CONS_AMOUNT";
	
	public static final String COL_CONSDESC = "CONS_DESC";
	
	public static final String COL_CONSTYPE = "CONS_TYPE";
	
	public static final String COL_BIZNO = "BIZ_NO";
	
	public static final String COL_STATUS = "STATUS";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_CREATOR = "CREATOR";
	
	public static final String COL_MODIFYTIME = "MODIFY_TIME";
	
	public static final String COL_MODIFIER = "MODIFIER";
	
	public static final String COL_SOURCE_TRANS_SEQ = "SOURCE_TRANS_SEQ";
	
	
	
	private Long id;
	private Long userId;
	private java.math.BigDecimal consAmount;
	private String consDesc;
	private Integer consType;
	private String bizNo;
	private Integer status;
	private java.util.Date createTime;
	private String creator;
	private java.util.Date modifyTime;
	private String modifier;
	private String sourceTransSeq;

	public String getSourceTransSeq() {
		return sourceTransSeq;
	}

	public void setSourceTransSeq(String sourceTransSeq) {
		this.sourceTransSeq = sourceTransSeq;
	}

	public KingAssetConsRecord() {}

	public KingAssetConsRecord(Long id) {
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
	
	public void setConsAmount(java.math.BigDecimal value) {
		this.consAmount = value;
	}
	
	public java.math.BigDecimal getConsAmount() {
		return this.consAmount;
	}
	
	public void setConsDesc(String value) {
		this.consDesc = value;
	}
	
	public String getConsDesc() {
		return this.consDesc;
	}
	
	public void setConsType(Integer value) {
		this.consType = value;
	}
	
	public Integer getConsType() {
		return this.consType;
	}
	
	public void setBizNo(String value) {
		this.bizNo = value;
	}
	
	public String getBizNo() {
		return this.bizNo;
	}
	
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	public Integer getStatus() {
		return this.status;
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
			.append("Id",getId())
			.append("UserId",getUserId())
			.append("ConsAmount",getConsAmount())
			.append("ConsDesc",getConsDesc())
			.append("ConsType",getConsType())
			.append("BizNo",getBizNo())
			.append("Status",getStatus())
			.append("CreateTime",getCreateTime())
			.append("Creator",getCreator())
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
		if(!(obj instanceof KingAssetConsRecord)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		KingAssetConsRecord other = (KingAssetConsRecord)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

