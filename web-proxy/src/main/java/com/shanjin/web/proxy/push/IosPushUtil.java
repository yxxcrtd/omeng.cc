package com.shanjin.web.proxy.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javapns.Push;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

import org.apache.log4j.Logger;

import com.shanjin.web.proxy.common.Constant;
import com.shanjin.web.proxy.util.StringUtil;

/**
 * ios消息推送公用类
 * 
 * @author Huang yulai
 *
 */
public class IosPushUtil {
	private static final Logger log = Logger.getLogger(IosPushUtil.class);

	// private static final String iosToken =
	// "417b086439371b701f355f42958c2b214e3648251b099c97380d8a033986358d";

	/**
	 * ios 推送消息
	 * 
	 * @param tokenList
	 *            苹果终端token列表
	 * @param msg
	 *            推送消息内容
	 * @param cert
	 *            推送证书
	 */
	public static void push(Map<String, Object> iosPushInfo, Map<String, Object> msg, Map<String, Object> cert) {
		try {
			PushIosMessage pim = new PushIosMessage(msg, cert, iosPushInfo);
			Thread push = new Thread(pim);
			push.start();
		} catch (Exception e) {
			log.error("ios push Exception", e);
		}
	}

	/**
	 * 发送推送消息至苹果服务器
	 * 
	 * @param deviceList
	 *            苹果终端设备列表
	 * @param msg
	 *            推送消息内容
	 * @param cert
	 *            推送证书
	 */
	public static void send(Map<String, Object> dev, Map<String, Object> msg, Map<String, Object> cert) {
		Object keystore = cert.get(Constant.IOS_PUSH_KEYSTORE);
		String password = StringUtil.null2Str(cert.get(Constant.IOS_PUSH_PASSWORD));
		boolean production = StringUtil.null2Str(cert.get(Constant.IOS_PUSH_PRODUCTION)).equals("true") ? true : false;
		int threads = StringUtil.nullToInteger(cert.get(Constant.IOS_PUSH_THREADS));
		PushNotificationPayload payload = PushNotificationPayload.complex();
		String certType = StringUtil.null2Str(cert.get(Constant.IOS_CERT_TYPE));

		try {
			payload.addBadge(1);
			payload.addSound("default");
			payload.addAlert(StringUtil.null2Str(msg.get("alertMsg")));
			StringBuilder ms = new StringBuilder();// ios推送字节限制，将扩展的参数值都拼接在ms中，用“|”隔开
			ms.append(msg.get("exit")).append("|");
			ms.append(msg.get("pushType")).append("|");
			ms.append(msg.get("appType")).append("|");
			ms.append(msg.get("orderId")).append("|");
			List<Device> deviceList = new ArrayList<Device>();

			if (certType.equals(Constant.IOS_MERCHANT_CERT)) {
				// 商户
				ms.append(dev.get("merchantId"));
			} else {
				// 用户
				ms.append(dev.get("userId"));
			}
			ms.append("|").append(msg.get("token"));
			payload.addCustomDictionary("msg", ms);
			Device device = new BasicDevice(StringUtil.null2Str(dev.get("token")));
			deviceList.add(device);
			List<PushedNotification> notifications = Push.payload(payload, keystore, password, production, threads, deviceList);
			for (PushedNotification notification : notifications) {
				if (notification.isSuccessful()) {
					String validToken = notification.getDevice().getToken();
					log.info("**********Push notification sent success to:token->[" + validToken + "]");
				} else {
					String invalidToken = notification.getDevice().getToken();
					log.info("**********Push notification sent failed to:token->[" + invalidToken + "]");
					Exception theProblem = notification.getException();
					theProblem.printStackTrace();
					ResponsePacket theErrorResponse = notification.getResponse();
					if (theErrorResponse != null) {
						log.error(theErrorResponse.getMessage());
					}
				}
			}
			deviceList.clear();
		} catch (Exception e) {
			log.error("ios send Exception", e);
		}
	}

	public static void main(String[] args) {
		 List<String> tokenList = new ArrayList<String>();
		 //
		 tokenList.add("417b086439371b701f355f42958c2b214e3648251b099c97380d8a033986358d");
		 //
		 tokenList.add("e7ab72d0da1b4afce22a4a646e117e8a07f9e8b4699da6f7a1b94b5800127806");
		 tokenList.add("78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e");
		 Map<String, Object> msg =
		 PushMessageUtil.getIosMerchantPushMsg("cbt", 1139L, 3, "0");
		 Map<String, Object> cert = new HashMap<String, Object>();
		 Map<String, Object> map = new HashMap<String, Object>();
		 map.put("merchantId", 1L);
		 map.put("token",
		 "78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e");
		 cert.put(Constant.IOS_PUSH_KEYSTORE, "D:/aps_dev_merchant.p12");
		 cert.put(Constant.IOS_PUSH_PRODUCTION, false);
		 cert.put(Constant.IOS_PUSH_PASSWORD, "123456");
		 cert.put(Constant.IOS_PUSH_THREADS, 1);
		 cert.put(Constant.IOS_CERT_TYPE, Constant.IOS_MERCHANT_CERT);
		 System.out.print("msg==" + msg);
		 push(map, msg, cert);
		 System.out.println("ios push compeleted");
		boolean production = StringUtil.null2Str(Constant.IOS_PUSH_PRODUCTION).equals("production") ? true : false;
		System.out.println(production);
	}

}
