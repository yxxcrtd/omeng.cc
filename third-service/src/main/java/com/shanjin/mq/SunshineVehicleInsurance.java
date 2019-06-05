package com.shanjin.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.carinsur.service.VoucherService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class SunshineVehicleInsurance {

    @Autowired
    private VoucherService voucherService;

    public void getMessage(String msg) throws Exception {
        Map<String, Object> map = new HashMap<>();

        System.out.println("=========== 接收到的消息是 ==========" + msg);

        if (StringUtils.isNotBlank(msg)) {
            JSONObject json = JSON.parseObject(msg);
            map.put("userId", json.get("user_id"));
            map.put("phone", json.get("phone"));
            map.put("originDesc", json.get("origin_desc"));
            map.put("originId", json.get("origin_id"));
            map.put("originId2", json.get("origin_id2"));
            map.put("originId3", json.get("origin_id3"));
            map.put("originType", json.get("origin_type"));
            map.put("startTime", json.get("start_time"));
            map.put("endTime", json.get("end_time"));
            map.put("status", json.get("status"));
            map.put("createTime", json.get("create_time"));
            map.put("vouCode", json.get("vou_code"));
            map.put("vouNo", json.get("vou_no"));
            map.put("isDel", json.get("is_del"));
            voucherService.saveUserVoucher(map);
        }
    }

}
