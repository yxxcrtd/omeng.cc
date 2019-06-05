package com.shanjin.push;

import java.util.List;
import java.util.Map;




import org.apache.log4j.Logger;



/**
 * ios设备消息多线程推送
 * @author Huang yulai
 *
 */
public class PushIosMessage implements Runnable{
	private  static final Logger log = Logger.getLogger(PushIosMessage.class);
	
	private Map<String,Object> msg;
	private Map<String,Object> cert;
	private List<Map<String, Object>> iosPushInfoList;
	
	public PushIosMessage(Map<String,Object> msg,Map<String,Object> cert,List<Map<String, Object>> iosPushInfoList){
		this.msg = msg;
		this.cert = cert;
		this.iosPushInfoList = iosPushInfoList;
	}
	
	@Override
	public void run() {
		try {
			IosPushUtil.send(iosPushInfoList, msg, cert);
		} catch (Exception e) {
			log.error("ios send thread Exception", e);
		}

	}

	

	
}
