

package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-01 03:28:29 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class CiUserVoucher {

	public static final String TABLE_NAME = "ci_user_voucher";

	public static final String TABLE_ALIAS = "CiUserVoucher";

	public static final String COL_ID = "ID";

	public static final String COL_USERID = "USER_ID";

	public static final String COL_PHONE = "PHONE";

	public static final String COL_VOUCODE = "VOU_CODE";

	public static final String COL_ORIGINDESC = "ORIGIN_DESC";

	public static final String COL_ORIGINID = "ORIGIN_ID";

	public static final String COL_ORGINID2 = "ORGIN_ID2";

	public static final String COL_ORGINID3 = "ORGIN_ID3";

	public static final String COL_ORGINID4 = "ORGIN_ID4";

	public static final String COL_ORIGINTYPE = "ORIGIN_TYPE";

	public static final String COL_STARTTIME = "START_TIME";

	public static final String COL_ENDTIME = "END_TIME";

	public static final String COL_VOUNO = "VOU_NO";

	public static final String COL_STATUS = "STATUS";

	public static final String COL_CREATETIME = "CREATE_TIME";

	public static final String COL_MIDIFYTIME = "MIDIFY_TIME";

	public static final String COL_ISDEL = "IS_DEL";



	private java.lang.Long id;
	private java.lang.Long userId;
	private java.lang.String phone;
	private java.lang.String vouCode;
	private java.lang.String originDesc;
	private java.lang.Long originId;
	private java.lang.String orginId2;
	private java.lang.String orginId3;
	private java.lang.String orginId4;
	private Integer originType;
	private java.util.Date startTime;
	private java.util.Date endTime;
	private java.lang.String vouNo;
	private Integer status;
	private java.util.Date createTime;
	private java.util.Date midifyTime;
	private Integer isDel;

	/**
	 * 扩展字段
     */
	private Date curTime;//当前时间
	private BigDecimal amount;//金额
	private Integer vouType;
	private String vouName;//券名称

	public CiUserVoucher() {}
	public CiUserVoucher(Long id) {
		this.id = id;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVouCode() {
		return vouCode;
	}

	public void setVouCode(String vouCode) {
		this.vouCode = vouCode;
	}

	public String getOriginDesc() {
		return originDesc;
	}

	public void setOriginDesc(String originDesc) {
		this.originDesc = originDesc;
	}

	public Long getOriginId() {
		return originId;
	}

	public void setOriginId(Long originId) {
		this.originId = originId;
	}

	public String getOrginId2() {
		return orginId2;
	}

	public void setOrginId2(String orginId2) {
		this.orginId2 = orginId2;
	}

	public String getOrginId3() {
		return orginId3;
	}

	public void setOrginId3(String orginId3) {
		this.orginId3 = orginId3;
	}

	public String getOrginId4() {
		return orginId4;
	}

	public void setOrginId4(String orginId4) {
		this.orginId4 = orginId4;
	}

	public Integer getOriginType() {
		return originType;
	}

	public void setOriginType(Integer originType) {
		this.originType = originType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getVouNo() {
		return vouNo;
	}

	public void setVouNo(String vouNo) {
		this.vouNo = vouNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getMidifyTime() {
		return midifyTime;
	}

	public void setMidifyTime(Date midifyTime) {
		this.midifyTime = midifyTime;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Date getCurTime() {
		return curTime;
	}

	public void setCurTime(Date curTime) {
		this.curTime = curTime;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getVouType() {
		return vouType;
	}

	public void setVouType(Integer vouType) {
		this.vouType = vouType;
	}

	public String getVouName() {
		return vouName;
	}

	public void setVouName(String vouName) {
		this.vouName = vouName;
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("Id",getId())
				.append("UserId",getUserId())
				.append("Phone",getPhone())
				.append("VouCode",getVouCode())
				.append("OriginDesc",getOriginDesc())
				.append("OriginId",getOriginId())
				.append("OrginId2",getOrginId2())
				.append("OrginId3",getOrginId3())
				.append("OrginId4",getOrginId4())
				.append("OriginType",getOriginType())
				.append("StartTime",getStartTime())
				.append("EndTime",getEndTime())
				.append("VouNo",getVouNo())
				.append("Status",getStatus())
				.append("CreateTime",getCreateTime())
				.append("MidifyTime",getMidifyTime())
				.append("IsDel",getIsDel())
				.toString();
	}

	public int hashCode() {
		return new HashCodeBuilder()
				.append(getId())
				.toHashCode();
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof CiUserVoucher)){
			return false;
		}
		if(this == obj){
			return true;
		}
		CiUserVoucher other = (CiUserVoucher)obj;
		return new EqualsBuilder()
				.append(getId(),other.getId())
				.isEquals();
	}
}

