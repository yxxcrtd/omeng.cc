package com.shanjin.push;

import java.util.HashMap;
import java.util.Map;

public class PushMessageUtil {

	public static Map<String, Object> getIosMerchantPushMsg(String appType, Long orderId, int pushType, String exit, String token) {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("orderId", orderId);
		msg.put("appType", appType);
		msg.put("pushType", pushType);
		msg.put("exit", exit);
		msg.put("token", token);
		String alertMsg = "";
		if ("1".equals(exit)) {
			alertMsg = "你的账号已在别处登录！";
		} else if ("1".equals(token)) {
			alertMsg = "令牌已经失效，请重新登录！";
		} else {
			if (pushType == 1) {
				alertMsg = "你有一条新订单，请及时查看！";
			} else if (pushType == 3) {
				alertMsg = "你已成功获得订单，请为用户提供优质服务！";
			} else if (pushType == 5) {
				alertMsg = "服务完成！";
			} else if (pushType == 8) {
				alertMsg = "用户已对你进行评价，请查看！";
			} else {
				alertMsg = "你有一条新订单，请及时查看！";
			}
		}
		msg.put("alertMsg", alertMsg);
		return msg;
	}

	public static Map<String, Object> getIosUserPushMsg(String appType, Long orderId, int pushType, String exit) {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("orderId", orderId);
		msg.put("appType", appType);
		msg.put("pushType", pushType);
		msg.put("exit", exit);
		String alertMsg = "";
		if ("1".equals(exit)) {
			alertMsg = "你的账号已在别处登录！";
		} else {
			alertMsg = "已有服务商反馈，请及时查看"; // 状态栏显示的通知文本提示 用户暂时就一种提示
		}
		msg.put("alertMsg", alertMsg);
		return msg;
	}
}
