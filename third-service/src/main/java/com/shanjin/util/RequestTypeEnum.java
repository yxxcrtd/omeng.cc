package com.shanjin.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/2
 * @desc TODO
 */
public enum RequestTypeEnum {

    car_basicInfo_sync("car_basicInfo_sync","基础信息同步接口"),
    car_vehicleInfo_sync("car_vehicleInfo_sync","车辆信息同步接口"),
    car_packageType_sync("car_packageType_sync","精确报价同步接口"),
    car_presentInfo_sync("car_presentInfo_sync","投保礼同步接口"),
    car_proposal_sync("car_proposal_sync","核保同步接口"),
    car_paid_sync("car_paid_sync","支付同步接口"),
    car_policy_sync("car_policy_sync","出单同步接口"),;

    private String value;
    private String name;

    RequestTypeEnum(String value,String name){
        this.value =value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


    public static final RequestTypeEnum getRequestTypeEnum(String requestType){
        for (RequestTypeEnum rte : RequestTypeEnum.values()){
            if(rte.getValue().equals(requestType)){
                return rte;
            }
        }
        return null;
    }
}
