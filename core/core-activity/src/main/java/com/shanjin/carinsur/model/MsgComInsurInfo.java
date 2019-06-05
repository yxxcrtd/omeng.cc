

package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-31 23:56:12 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MsgComInsurInfo extends AbsInsurInfo {
	
  	public static final String TABLE_NAME = "msg_com_insur_info";

	public static final String TABLE_ALIAS = "MsgComInsurInfo";

	public static final String COL_COMINSURKINDS = "COM_INSUR_KINDS";

	private String comInsurKinds;

	public MsgComInsurInfo() {}

	public MsgComInsurInfo(String orderNo) {
		setOrderNo(orderNo);
	}

	public void setComInsurKinds(String value) {
		this.comInsurKinds = value;
	}
	
	public String getComInsurKinds() {
		return this.comInsurKinds;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("OrderNo",getOrderNo())
			.append("SunOrderNo",getSunOrderNo())
			.append("ProposalNo",getProposalNo())
			.append("PolicyNo",getPolicyNo())
			.append("StartDate",getStartDate())
			.append("EndDate",getEndDate())
			.append("Premium",getPremium())
			.append("Tax",getTax())
			.append("PayTime",getPayTime())
			.append("OutPayId",getOutPayId())
			.append("CallbackDetail",getCallbackDetail())
			.append("CallbackPay",getCallbackPay())
			.append("PcPayUrl",getPcPayUrl())
			.append("ComInsurKinds",getComInsurKinds())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getOrderNo())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof MsgComInsurInfo)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		MsgComInsurInfo other = (MsgComInsurInfo)obj;
		return new EqualsBuilder()
			.append(getOrderNo(),other.getOrderNo())
			.isEquals();
	}
}

