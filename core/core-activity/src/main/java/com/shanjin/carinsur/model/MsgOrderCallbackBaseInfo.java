

package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-09-01 03:28:30 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MsgOrderCallbackBaseInfo {
	
  public static final String TABLE_NAME = "msg_order_callback_base_info";

	public static final String TABLE_ALIAS = "MsgOrderCallbackBaseInfo";
	
	public static final String COL_ORDERNO = "ORDER_NO";
	
	public static final String COL_LICENSENO = "LICENSE_NO";
	
	public static final String COL_BRAND = "BRAND";
	
	public static final String COL_ENGINENO = "ENGINE_NO";
	
	public static final String COL_REGISTERDATE = "REGISTER_DATE";
	
	public static final String COL_OWNERNAME = "OWNER_NAME";
	
	public static final String COL_OWNERMOBILE = "OWNER_MOBILE";
	
	public static final String COL_OWNERIDNO = "OWNER_ID_NO";
	
	public static final String COL_OWNEREMAIL = "OWNER_EMAIL";
	
	public static final String COL_APPLICANTINFO = "APPLICANT_INFO";
	
	public static final String COL_APPLICANTNAME = "APPLICANT_NAME";
	
	public static final String COL_APPLICANTMOBILE = "APPLICANT_MOBILE";
	
	public static final String COL_APPLICANTIDNO = "APPLICANT_ID_NO";
	
	public static final String COL_APPLICANTEMAIL = "APPLICANT_EMAIL";
	
	public static final String COL_INSUREDNAME = "INSURED_NAME";
	
	public static final String COL_INSUREDMOBILE = "INSURED_MOBILE";
	
	public static final String COL_INSUREDIDNO = "INSURED_ID_NO";
	
	public static final String COL_INSUREDEMAIL = "INSURED_EMAIL";
	
	public static final String COL_RECEIVERNAME = "RECEIVER_NAME";
	
	public static final String COL_RECEIVERTEL = "RECEIVER_TEL";
	
	public static final String COL_ADDRESS = "ADDRESS";
	
	public static final String COL_UNSETTLEMENTCOST = "UN_SETTLEMENT_COST";
	
	public static final String COL_VEHICLEFRAMENO = "VEHICLE_FRAME_NO";
	
	
	
	private String orderNo;
	private String licenseNo;
	private String brand;
	private String engineNo;
	private String registerDate;
	private String ownerName;
	private String ownerMobile;
	private String ownerIdNo;
	private String ownerEmail;
	private String applicantInfo;
	private String applicantName;
	private String applicantMobile;
	private String applicantIdNo;
	private String applicantEmail;
	private String insuredName;
	private String insuredMobile;
	private String insuredIdNo;
	private String insuredEmail;
	private String receiverName;
	private String receiverTel;
	private String address;
	private String unSettlementCost;
	private String vehicleFrameNo;

	public MsgOrderCallbackBaseInfo() {}

	public MsgOrderCallbackBaseInfo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setOrderNo(String value) {
		this.orderNo = value;
	}
	
	public String getOrderNo() {
		return this.orderNo;
	}
	
	public void setLicenseNo(String value) {
		this.licenseNo = value;
	}
	
	public String getLicenseNo() {
		return this.licenseNo;
	}
	
	public void setBrand(String value) {
		this.brand = value;
	}
	
	public String getBrand() {
		return this.brand;
	}
	
	public void setEngineNo(String value) {
		this.engineNo = value;
	}
	
	public String getEngineNo() {
		return this.engineNo;
	}
	
	public void setRegisterDate(String value) {
		this.registerDate = value;
	}
	
	public String getRegisterDate() {
		return this.registerDate;
	}
	
	public void setOwnerName(String value) {
		this.ownerName = value;
	}
	
	public String getOwnerName() {
		return this.ownerName;
	}
	
	public void setOwnerMobile(String value) {
		this.ownerMobile = value;
	}
	
	public String getOwnerMobile() {
		return this.ownerMobile;
	}
	
	public void setOwnerIdNo(String value) {
		this.ownerIdNo = value;
	}
	
	public String getOwnerIdNo() {
		return this.ownerIdNo;
	}
	
	public void setOwnerEmail(String value) {
		this.ownerEmail = value;
	}
	
	public String getOwnerEmail() {
		return this.ownerEmail;
	}
	
	public void setApplicantInfo(String value) {
		this.applicantInfo = value;
	}
	
	public String getApplicantInfo() {
		return this.applicantInfo;
	}
	
	public void setApplicantName(String value) {
		this.applicantName = value;
	}
	
	public String getApplicantName() {
		return this.applicantName;
	}
	
	public void setApplicantMobile(String value) {
		this.applicantMobile = value;
	}
	
	public String getApplicantMobile() {
		return this.applicantMobile;
	}
	
	public void setApplicantIdNo(String value) {
		this.applicantIdNo = value;
	}
	
	public String getApplicantIdNo() {
		return this.applicantIdNo;
	}
	
	public void setApplicantEmail(String value) {
		this.applicantEmail = value;
	}
	
	public String getApplicantEmail() {
		return this.applicantEmail;
	}
	
	public void setInsuredName(String value) {
		this.insuredName = value;
	}
	
	public String getInsuredName() {
		return this.insuredName;
	}
	
	public void setInsuredMobile(String value) {
		this.insuredMobile = value;
	}
	
	public String getInsuredMobile() {
		return this.insuredMobile;
	}
	
	public void setInsuredIdNo(String value) {
		this.insuredIdNo = value;
	}
	
	public String getInsuredIdNo() {
		return this.insuredIdNo;
	}
	
	public void setInsuredEmail(String value) {
		this.insuredEmail = value;
	}
	
	public String getInsuredEmail() {
		return this.insuredEmail;
	}
	
	public void setReceiverName(String value) {
		this.receiverName = value;
	}
	
	public String getReceiverName() {
		return this.receiverName;
	}
	
	public void setReceiverTel(String value) {
		this.receiverTel = value;
	}
	
	public String getReceiverTel() {
		return this.receiverTel;
	}
	
	public void setAddress(String value) {
		this.address = value;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public void setUnSettlementCost(String value) {
		this.unSettlementCost = value;
	}
	
	public String getUnSettlementCost() {
		return this.unSettlementCost;
	}
	
	public void setVehicleFrameNo(String value) {
		this.vehicleFrameNo = value;
	}
	
	public String getVehicleFrameNo() {
		return this.vehicleFrameNo;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("OrderNo",getOrderNo())
			.append("LicenseNo",getLicenseNo())
			.append("Brand",getBrand())
			.append("EngineNo",getEngineNo())
			.append("RegisterDate",getRegisterDate())
			.append("OwnerName",getOwnerName())
			.append("OwnerMobile",getOwnerMobile())
			.append("OwnerIdNo",getOwnerIdNo())
			.append("OwnerEmail",getOwnerEmail())
			.append("ApplicantInfo",getApplicantInfo())
			.append("ApplicantName",getApplicantName())
			.append("ApplicantMobile",getApplicantMobile())
			.append("ApplicantIdNo",getApplicantIdNo())
			.append("ApplicantEmail",getApplicantEmail())
			.append("InsuredName",getInsuredName())
			.append("InsuredMobile",getInsuredMobile())
			.append("InsuredIdNo",getInsuredIdNo())
			.append("InsuredEmail",getInsuredEmail())
			.append("ReceiverName",getReceiverName())
			.append("ReceiverTel",getReceiverTel())
			.append("Address",getAddress())
			.append("UnSettlementCost",getUnSettlementCost())
			.append("VehicleFrameNo",getVehicleFrameNo())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getOrderNo())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MsgOrderCallbackBaseInfo)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MsgOrderCallbackBaseInfo other = (MsgOrderCallbackBaseInfo)obj;
		return new EqualsBuilder()
			.append(getOrderNo(),other.getOrderNo())
			.isEquals();
	}
}

