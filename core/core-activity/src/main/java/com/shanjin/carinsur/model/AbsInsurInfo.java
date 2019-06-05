package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/1
 * @desc TODO
 */
public abstract class AbsInsurInfo {



    public static final String COL_ORDERNO = "ORDER_NO";

    public static final String COL_SUNORDERNO = "SUN_ORDER_NO";

    public static final String COL_PROPOSALNO = "PROPOSAL_NO";

    public static final String COL_POLICYNO = "POLICY_NO";

    public static final String COL_STARTDATE = "START_DATE";

    public static final String COL_ENDDATE = "END_DATE";

    public static final String COL_PREMIUM = "PREMIUM";

    public static final String COL_TAX = "TAX";

    public static final String COL_PAYTIME = "PAY_TIME";

    public static final String COL_OUTPAYID = "OUT_PAY_ID";

    public static final String COL_CALLBACKDETAIL = "CALLBACK_DETAIL";

    public static final String COL_CALLBACKPAY = "CALLBACK_PAY";

    public static final String COL_PCPAYURL = "PC_PAY_URL";



    private String orderNo;
    private String sunOrderNo;
    private String proposalNo;
    private String policyNo;
    private String startDate;
    private String endDate;
    private String premium;
    private String tax;
    private String payTime;
    private String outPayId;
    private String callbackDetail;
    private String callbackPay;
    private String pcPayUrl;

    public AbsInsurInfo() {}

    public AbsInsurInfo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setOrderNo(String value) {
        this.orderNo = value;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setSunOrderNo(String value) {
        this.sunOrderNo = value;
    }

    public String getSunOrderNo() {
        return this.sunOrderNo;
    }

    public void setProposalNo(String value) {
        this.proposalNo = value;
    }

    public String getProposalNo() {
        return this.proposalNo;
    }

    public void setPolicyNo(String value) {
        this.policyNo = value;
    }

    public String getPolicyNo() {
        return this.policyNo;
    }

    public void setStartDate(String value) {
        this.startDate = value;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setEndDate(String value) {
        this.endDate = value;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setPremium(String value) {
        this.premium = value;
    }

    public String getPremium() {
        return this.premium;
    }

    public void setTax(String value) {
        this.tax = value;
    }

    public String getTax() {
        return this.tax;
    }

    public void setPayTime(String value) {
        this.payTime = value;
    }

    public String getPayTime() {
        return this.payTime;
    }

    public void setOutPayId(String value) {
        this.outPayId = value;
    }

    public String getOutPayId() {
        return this.outPayId;
    }

    public void setCallbackDetail(String value) {
        this.callbackDetail = value;
    }

    public String getCallbackDetail() {
        return this.callbackDetail;
    }

    public void setCallbackPay(String value) {
        this.callbackPay = value;
    }

    public String getCallbackPay() {
        return this.callbackPay;
    }

    public void setPcPayUrl(String value) {
        this.pcPayUrl = value;
    }

    public String getPcPayUrl() {
        return this.pcPayUrl;
    }


    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
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
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getOrderNo())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof MsgTraInsurInfo)){
            return false;
        }
        if(this == obj){
            return true;
        }
        MsgTraInsurInfo other = (MsgTraInsurInfo)obj;
        return new EqualsBuilder()
                .append(getOrderNo(),other.getOrderNo())
                .isEquals();
    }
}
