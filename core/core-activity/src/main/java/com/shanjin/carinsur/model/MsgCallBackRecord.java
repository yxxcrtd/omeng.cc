

package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-01 03:28:29 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MsgCallBackRecord {
	
  public static final String TABLE_NAME = "msg_call_back_record";

	public static final String TABLE_ALIAS = "MsgCallBackRecord";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_ORDERNO = "ORDER_NO";
	
	public static final String COL_BODY = "BODY";
	
	public static final String COL_REQUESTTYPE = "REQUEST_TYPE";
	
	public static final String COL_SENDTIME = "SEND_TIME";
	
	public static final String COL_CREATETIME = "CREATE_TIME";
	
	public static final String COL_AGENTCODE = "AGENT_CODE";
	
	public static final String COL_SELLERID = "SELLER_ID";
	
	public static final String COL_VERSION = "VERSION";
	
	
	
	private Long id;
	private String orderNo;
	private String body;
	private String requestType;
	private String sendTime;
	private java.util.Date createTime;
	private String agentCode;
	private String sellerId;
	private String version;

	public MsgCallBackRecord() {}

	public MsgCallBackRecord(Long id) {
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
	
	public void setBody(String value) {
		this.body = value;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public void setRequestType(String value) {
		this.requestType = value;
	}
	
	public String getRequestType() {
		return this.requestType;
	}
	
	public void setSendTime(String value) {
		this.sendTime = value;
	}
	
	public String getSendTime() {
		return this.sendTime;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setAgentCode(String value) {
		this.agentCode = value;
	}
	
	public String getAgentCode() {
		return this.agentCode;
	}
	
	public void setSellerId(String value) {
		this.sellerId = value;
	}
	
	public String getSellerId() {
		return this.sellerId;
	}
	
	public void setVersion(String value) {
		this.version = value;
	}
	
	public String getVersion() {
		return this.version;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("OrderNo",getOrderNo())
			.append("Body",getBody())
			.append("RequestType",getRequestType())
			.append("SendTime",getSendTime())
			.append("CreateTime",getCreateTime())
			.append("AgentCode",getAgentCode())
			.append("SellerId",getSellerId())
			.append("Version",getVersion())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MsgCallBackRecord)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MsgCallBackRecord other = (MsgCallBackRecord)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

