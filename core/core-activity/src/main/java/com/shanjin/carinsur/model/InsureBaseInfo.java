package com.shanjin.carinsur.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/2
 * @desc 保单基础信息 提供保单详情展示页
 */
public class InsureBaseInfo {

    //车主信息
    private String ownerName;
    private String ownerIdNo;
    private String ownerMobile;
    //车辆信息
    private String vehicleFrameNo;
    private String licenseNo;
    private String brand;
    private String engineNo;
    //保单信息
    private String orderNo;//保单号
    private BigDecimal premium;//保费


    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerIdNo() {
        return ownerIdNo;
    }

    public void setOwnerIdNo(String ownerIdNo) {
        this.ownerIdNo = ownerIdNo;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }

    public String getVehicleFrameNo() {
        return vehicleFrameNo;
    }

    public void setVehicleFrameNo(String vehicleFrameNo) {
        this.vehicleFrameNo = vehicleFrameNo;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
