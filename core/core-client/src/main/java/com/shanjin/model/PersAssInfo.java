package com.shanjin.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/17
 * @desc 私人助理信息
 * @see
 */
public class PersAssInfo implements Serializable {

    private static final long serialVersionUID = 2580411280493658151L;


    private String avator; // 头像地址
    private String city;//城市
    private Date dueTime;//处理日期
    private Long id;//主键
    private String name;
    private String phone;
    private Double score;

    private String province;

    private String address;

    private String serviceNumber;//私人助理服务号 ==邀请码

    private static final String[] PIM_ARRAY = {"北京", "天津", "上海", "重庆"};


    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 是否是直辖市
     *
     * @return
     */
    public boolean isPIM() {
        for (String str : PIM_ARRAY) {
            if (this.province.indexOf(str) > -1) {
                return true;
            }
        }
        return false;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }
}
