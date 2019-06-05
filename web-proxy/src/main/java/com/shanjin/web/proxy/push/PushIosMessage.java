package com.shanjin.web.proxy.push;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * ios设备消息多线程推送
 * 
 * @author Huang yulai
 *
 */
public class PushIosMessage implements Runnable {
	private static final Logger log = Logger.getLogger(PushIosMessage.class);

	private Map<String, Object> msg;
	private Map<String, Object> cert;
	private Map<String, Object> iosPushInfo;

	public PushIosMessage(Map<String, Object> msg, Map<String, Object> cert, Map<String, Object> iosPushInfo) {
		this.msg = msg;
		this.cert = cert;
		this.iosPushInfo = iosPushInfo;
	}

	@Override
	public void run() {
		try {
			IosPushUtil.send(iosPushInfo, msg, cert);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ios send thread Exception", e);
		}

	}

}
