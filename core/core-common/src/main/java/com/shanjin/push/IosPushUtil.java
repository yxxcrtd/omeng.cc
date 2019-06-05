package com.shanjin.push;

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

import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.StringUtil;

/**
 * ios消息推送公用类
 * 
 * @author Huang yulai
 *
 */
public class IosPushUtil {
	private static final Logger log = Logger.getLogger(IosPushUtil.class);
	private static Logger logger = Logger.getLogger(IosPushUtil.class);

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
	public static void push(List<Map<String, Object>> iosPushInfoList, Map<String, Object> msg, Map<String, Object> cert) {
		if (iosPushInfoList == null || iosPushInfoList.size() < 1)
			return;
		try {
			// List<Device> devices = new ArrayList<Device>();
			// Device device = null;
			// for (String token : tokenList) {
			// if (!StringUtil.isNullStr(token)) {
			// device = new BasicDevice(token);
			// devices.add(device);
			// }
			// }
			if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
				PushIosMessage pim = new PushIosMessage(msg, cert, iosPushInfoList);
				Thread push = new Thread(pim);
				push.start();
			}
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
	public static void send(List<Map<String, Object>> iosPushInfoList, Map<String, Object> msg, Map<String, Object> cert) {
		Object keystore = cert.get(Constant.IOS_PUSH_KEYSTORE);
		String password = StringUtil.null2Str(cert.get(Constant.IOS_PUSH_PASSWORD));
		boolean production = StringUtil.null2Str(cert.get(Constant.IOS_PUSH_PRODUCTION)) == "true" ? true : false;
		int threads = StringUtil.nullToInteger(cert.get(Constant.IOS_PUSH_THREADS));
		PushNotificationPayload payload = PushNotificationPayload.complex();
		String certType = StringUtil.null2Str(cert.get(Constant.IOS_CERT_TYPE));

		try {
			payload.addBadge(1);
			payload.addSound("default");
			payload.addAlert(StringUtil.null2Str(msg.get("alertMsg")));
			for (Map.Entry<String, Object> entry : msg.entrySet()) {
				if (!entry.getKey().equals("alertMsg")) {
					payload.addCustomDictionary(entry.getKey(), StringUtil.null2Str(entry.getValue()));
				}
			}
			if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
				List<Device> deviceList = new ArrayList<Device>();
				for (Map<String, Object> dev : iosPushInfoList) {
					if (certType.equals(Constant.IOS_MERCHANT_CERT)) {
						// 商户
						payload.addCustomDictionary("merchantId", dev.get("merchantId"));
					} else {
						// 用户
						payload.addCustomDictionary("userId", dev.get("userId"));
					}
					Device device = new BasicDevice(StringUtil.null2Str(dev.get("token")));
					deviceList.add(device);
					List<PushedNotification> notifications = Push.payload(payload, keystore, password, production, threads, deviceList);
					for (PushedNotification notification : notifications) {
						if (notification.isSuccessful()) {
							String validToken = notification.getDevice().getToken();
							if (log.isInfoEnabled()) {
								log.info("**********Push notification sent success to:token->[" + validToken + "]");
							}
						} else {
							String invalidToken = notification.getDevice().getToken();
							if (log.isInfoEnabled()) {
								log.info("**********Push notification sent failed to:token->[" + invalidToken + "]");
							}
							Exception theProblem = notification.getException();
							theProblem.printStackTrace();
							ResponsePacket theErrorResponse = notification.getResponse();
							if (theErrorResponse != null) {
								log.error(theErrorResponse.getMessage());
							}
						}
					}
					deviceList.clear();
				}
			}

		} catch (Exception e) {
			log.error("ios send Exception", e);
		}
	}

	public static void main(String[] args) {
		List<String> tokenList = new ArrayList<String>();
		// tokenList.add("417b086439371b701f355f42958c2b214e3648251b099c97380d8a033986358d");
		// tokenList.add("e7ab72d0da1b4afce22a4a646e117e8a07f9e8b4699da6f7a1b94b5800127806");
		tokenList.add("78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e");
		Map<String, Object> msg = PushMessageUtil.getIosMerchantPushMsg("cbt", 1139L, 0, "0", "0");
		Map<String, Object> cert = new HashMap<String, Object>();
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("merchantId", 144041400126794160L);
		map.put("token", "78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e");
		iosPushInfoList.add(map);
		cert.put(Constant.IOS_PUSH_KEYSTORE, "D:/apsShanjinMerchant.p12");
		cert.put(Constant.IOS_PUSH_PRODUCTION, false);
		cert.put(Constant.IOS_PUSH_PASSWORD, "123456");
		cert.put(Constant.IOS_PUSH_THREADS, 1);
		cert.put(Constant.IOS_CERT_TYPE, Constant.IOS_MERCHANT_CERT);
		push(iosPushInfoList, msg, cert);
		logger.info("ios push compeleted");
	}

}
