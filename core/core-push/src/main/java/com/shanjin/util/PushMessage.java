package com.shanjin.util;


import java.util.HashMap;
import java.util.Map;

import com.shanjin.common.util.StringUtil;

public class PushMessage {

	public static Map<String,Object> getPushMsg(Map<String,Object> configMap,Map<String,Object> paras) {

		String appType=paras.get("appType")==null?"":paras.get("appType").toString();
		Long merchantId=paras.get("merchantId")==null?0:StringUtil.nullToLong(paras.get("merchantId"));
		Long orderId=Long.parseLong(paras.get("orderId")==null?"0":paras.get("orderId").toString());
		int pushType=Integer.parseInt(paras.get("pushType")==null?"-1":paras.get("pushType").toString());
		int isPrivateAssistant=StringUtil.nullToInteger(paras.get("isPrivateAssistant"));
		
		
		Map<String,Object> msg = new HashMap<String,Object>();
		msg.put("appType", appType);
		msg.put("merchantId", merchantId);
		msg.put("orderId", orderId);
		msg.put("pushType", pushType);

		if(pushType==2 && isPrivateAssistant==1){//私人助理抢单
			pushType=20;
		}
		
		if(pushType==12 && isPrivateAssistant==1){//私人助理完成交付
			pushType=22;
		}
		
		String alertMsg=StringUtil.null2Str(configMap.get("pushType-"+pushType));
		if(StringUtil.isEmpty(alertMsg)){	
			if (pushType == 1) {//发送订单				
				alertMsg = "你有一条新订单，请及时查看！";
			} else if (pushType == 2) {//提供报价方案
				alertMsg = "服务商已反馈，请及时查看！"; 
			} else if (pushType == 3) {//选择报价方案
				alertMsg = "你已成功获得订单，请为用户提供优质服务！";
			} else if (pushType == 4) {//已付款
				alertMsg = "用户已付款！";
			} else if (pushType == 5) {//已评价
				alertMsg = "用户已对你进行评价，请查看！";
			}  else if (pushType == 6) {//删除员工
				alertMsg = "你已被老板删除！";
			} else if (pushType == 7) {//token失效
				alertMsg = "令牌失效，请重新登录！";
			}else if (pushType == 8) {//多终端登录
				alertMsg = "你的账号已在其他终端登录！";
			}else if (pushType == 9) {//认证通过
				alertMsg = "认证通过！";
			}else if (pushType == 10) {//认证未通过！
				alertMsg = "认证未通过！";
			}else if (pushType == 11) {//修改报价方案
				alertMsg = "商户报价方案已修改，请及时查看！";
			}else if (pushType == 12) {//服务商服务已完成
				alertMsg = "服务商服务已完成！";
			}else if (pushType == 13) {
				alertMsg = "商户交付清单已修改，请及时查看！";
			}else if (pushType == 14) {
				alertMsg = "用户已选择其他服务商 ！";
			}else if (pushType == 15) {
				alertMsg = "你的奖励已到帐，请查收！";
			}else if (pushType == 16) {
				alertMsg = "你消费我补贴活动奖励你10元已到帐，请查收！";
			}else if (pushType == 20) {
				alertMsg = "O盟私人助理已回应，协助你快速找到合适服务商！";
			}else if (pushType == 22) {
				alertMsg = "o盟私人助理已确认你的服务完成，快来确认给他评价！";
			}else if (pushType == 23) {
				alertMsg = "你的奖励已到帐，请查收！";
			}else if (pushType == 200) {
				alertMsg = "你的服务商有新订单，请及时处理";
			}else {
				alertMsg = "你有一条新通知！";
			}
		}
		msg.put("alertMsg", alertMsg);
		return msg;
	}

}
