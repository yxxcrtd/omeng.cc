package com.shanjin.manager.Bean;

import java.math.BigDecimal;
import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 表名：order_info 订单表
 */
public class OrderInfo extends Model<OrderInfo>{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static OrderInfo dao=new OrderInfo();

	/** 主键 */
    private Integer id;
    
    /** 附件关联的用户ID */
    private Integer userId;

    /** 订单类型 */
    private Integer orderType;

    /** 所选的商户ID */
    private Integer merchantsId;

    /** 接单员工ID */
    private Integer receiveEmployeesId;

    /** 所选商家方案ID */
    private Integer merchantsPlanId;

    /** 订单状态 */
    private Integer orderStatus;

    /** 订单金额 */
    private BigDecimal orderPrice;

    /** 订单实际金额 */
    private BigDecimal orderActualPrice;

    /** 支付方式 */
    private Integer orderPayType;

    /** 代金券ID */
    private Integer vouchersId;

    /** 服务态度评价 */
    private Integer attitudeEvaluation;

    /** 服务质量评价 */
    private Integer qualityEvaluation;

    /** 服务速度评价 */
    private Integer speedEvaluation;

    /** 预约时间 */
    private Date subscribeTime;

    /** 确认时间 */
    private Date confirmTime;

    /** 成交时间 */
    private Date dealTime;
    private Long total;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getMerchantsId() {
        return merchantsId;
    }

    public void setMerchantsId(Integer merchantsId) {
        this.merchantsId = merchantsId;
    }

    public Integer getReceiveEmployeesId() {
        return receiveEmployeesId;
    }

    public void setReceiveEmployeesId(Integer receiveEmployeesId) {
        this.receiveEmployeesId = receiveEmployeesId;
    }

    public Integer getMerchantsPlanId() {
        return merchantsPlanId;
    }

    public void setMerchantsPlanId(Integer merchantsPlanId) {
        this.merchantsPlanId = merchantsPlanId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getOrderActualPrice() {
        return orderActualPrice;
    }

    public void setOrderActualPrice(BigDecimal orderActualPrice) {
        this.orderActualPrice = orderActualPrice;
    }

    public Integer getOrderPayType() {
        return orderPayType;
    }

    public void setOrderPayType(Integer orderPayType) {
        this.orderPayType = orderPayType;
    }

    public Integer getVouchersId() {
        return vouchersId;
    }

    public void setVouchersId(Integer vouchersId) {
        this.vouchersId = vouchersId;
    }

    public Integer getAttitudeEvaluation() {
        return attitudeEvaluation;
    }

    public void setAttitudeEvaluation(Integer attitudeEvaluation) {
        this.attitudeEvaluation = attitudeEvaluation;
    }

    public Integer getQualityEvaluation() {
        return qualityEvaluation;
    }

    public void setQualityEvaluation(Integer qualityEvaluation) {
        this.qualityEvaluation = qualityEvaluation;
    }

    public Integer getSpeedEvaluation() {
        return speedEvaluation;
    }

    public void setSpeedEvaluation(Integer speedEvaluation) {
        this.speedEvaluation = speedEvaluation;
    }

    public Date getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Date subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Date getDealTime() {
        return dealTime;
    }

    public void setDealTime(Date dealTime) {
        this.dealTime = dealTime;
    }

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
    
}
