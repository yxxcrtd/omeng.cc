package com.shanjin.common.util;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

public class IPutil {
    public static String getRemortIP(HttpServletRequest request) {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static JSONObject getIpLocation(String ip) {
        JSONObject jsonobject = new JSONObject();
        String param = "ak=4b85605436f29eae01eb6c95ce1c0cf7&ip=" + ip + "&coor=bd09ll";
        String resultString = HttpRequest.sendGet("http://api.map.baidu.com/location/ip", param);
        System.out.println(resultString);
        jsonobject = JSONObject.parseObject(resultString);
        System.out.println(jsonobject);
        return jsonobject;
    }

    public static void main(String[] args) throws Exception {
        getIpLocation("114.97.68.69");
    }
}
