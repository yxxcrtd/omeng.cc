package com.shanjin.manager.utils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
public class Test {
public static String getFile(String str) {
	String o1="'";
	return str.replace(o1, "\\'");
}

public static void main(String[] args) throws ParseException{
	Map<String,Object> configMap=new HashMap<String,Object>();
	configMap.put("merchantId", 146665060198081750l);
	configMap.put("merchantUserId", 146665038743732363l);
	configMap.put("merchantUserPhone", "18620515928");
	configMap.put("merchantName", "家政服务186");
	
	configMap.put("userName", "");
	configMap.put("activityName", "千万粉丝活动");
	configMap.put("activityId", "2");
	configMap.put("transSeq", 828);
	configMap.put("rewardTime", "2016-08-10 14:48:01");
	
	configMap.put("transAmount", 2.00);
    configMap.put("remark", "粉丝奖");
	
		String msg=JSONObject.toJSONString(configMap);
		String encryptedText="";
		try {
			encryptedText = AESUtil.parseByte2HexStr(AESUtil.encrypt(msg, "367937E1967092280C56077755E4C65B"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	   System.out.println(encryptedText);
}
}
