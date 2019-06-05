//推送改成异步的了，这里先全局注释
//package com.shanjin.push;
//
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//
//import com.gexin.rp.sdk.base.IPushResult;
//import com.gexin.rp.sdk.base.impl.SingleMessage;
//import com.gexin.rp.sdk.base.impl.Target;
//import com.gexin.rp.sdk.http.IGtPush;
//import com.gexin.rp.sdk.template.TransmissionTemplate;
//import com.shanjin.common.constant.Constant;
//
//public class PushUtil {
//
//	static String host = "http://sdk.open.api.igexin.com/apiex.htm";
//	private static Logger logger = Logger.getLogger(PushUtil.class);
//
//	// 推送消息给商户版
//	public static String pushMessageToListForSHB(String apptype, List<String> clientIDs, String transmissionContent) throws Exception {
//		Map<String, String> pushMap = (Map<String, String>) Constant.PUSH_CONFIG.PUSH_MAP.get(apptype);
//		String appkey = pushMap.get("SHB_APPKEY");
//		String master = pushMap.get("SHB_MASTERSECRET");
//		String appId = pushMap.get("SHB_APPID");
//
//		IGtPush push = new IGtPush(host, appkey, master);
//		push.connect();
//		TransmissionTemplate template = TransmissionTemplate(transmissionContent, apptype, "shb");
//		SingleMessage message = new SingleMessage();
//		message.setData(template);
//		message.setOffline(true);
//		message.setOfflineExpireTime(24 * 1000 * 3600 * 3);
//		IPushResult ret = null;
//		for (String clientID : clientIDs) {
//			Target target = new Target();
//			target.setAppId(appId);
//			target.setClientId(clientID);
//
//			try {
//				ret = push.pushMessageToSingle(message, target);
//			} catch (Exception e) {
//				e.printStackTrace();
//				ret = push.pushMessageToSingle(message, target);
//			}
//			if (ret != null) {
//				System.out.println("个推返回结果：  clientID:" + clientID + "  " + ret.getResponse().toString());
//				logger.info("个推返回结果：  clientID:" + clientID + "  " + ret.getResponse().toString());
//			} else {
//				System.out.println("个推返回结果为空");
//				logger.info("个推返回结果为空");
//			}
//
//		}
//		return ret.getResponse().toString();
//	}
//
//	// 推送消息给用户版
//	public static String pushMessageToListForYHB(String apptype, List<String> clientIDs, String transmissionContent) throws Exception {
//		String appkey = Constant.PUSH_CONFIG.OMENG_APPKEY;
//		String master = Constant.PUSH_CONFIG.OMENG_MASTERSECRET;
//		String appId = Constant.PUSH_CONFIG.OMENG_APPID;
//
//		IGtPush push = new IGtPush(host, appkey, master);
//		push.connect();
//		TransmissionTemplate template = TransmissionTemplate(transmissionContent, apptype, "yhb");
//		SingleMessage message = new SingleMessage();
//		message.setData(template);
//		message.setOffline(true);
//		message.setOfflineExpireTime(24 * 1000 * 3600 * 3);
//
//		IPushResult ret = null;
//		for (String clientID : clientIDs) {
//			Target target = new Target();
//			target.setAppId(appId);
//			target.setClientId(clientID);
//			try {
//				ret = push.pushMessageToSingle(message, target);
//			} catch (Exception e) {
//				e.printStackTrace();
//				ret = push.pushMessageToSingle(message, target);
//			}
//			if (ret != null) {
//				System.out.println("个推返回结果： clientID:" + clientID + "  " + ret.getResponse().toString());
//				logger.info("个推返回结果：  clientID:" + clientID + "  " + ret.getResponse().toString());
//			} else {
//				System.out.println("个推返回结果为空");
//				logger.info("个推返回结果为空");
//			}
//		}
//
//		return ret.getResponse().toString();
//	}
//
//	public static TransmissionTemplate TransmissionTemplate(String transmissionContent, String apptype, String useType) throws Exception {
//		String appkey = "";
//		String appId = "";
//		TransmissionTemplate template = new TransmissionTemplate();
//		if (useType.equals("shb")) {
//			Map<String, String> pushMap = (Map<String, String>) Constant.PUSH_CONFIG.PUSH_MAP.get(apptype);
//			appId = pushMap.get("SHB_APPID");
//			appkey = pushMap.get("SHB_APPKEY");
//		}
//		if (useType.equals("yhb")) {
//			appId = Constant.PUSH_CONFIG.OMENG_APPID;
//			appkey = Constant.PUSH_CONFIG.OMENG_APPKEY;
//		}
//		template.setAppId(appId);
//		template.setAppkey(appkey);
//		template.setTransmissionType(2);
//		template.setTransmissionContent(transmissionContent);
//		// template.setPushInfo("dd", 1, "ddd", "com.gexin.ios.silence", "", "",
//		// "", "");
//		return template;
//	}
// }
