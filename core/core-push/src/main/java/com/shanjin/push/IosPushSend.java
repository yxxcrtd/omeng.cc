package com.shanjin.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.util.PushConfig;

import com.shanjin.push.ios.apns.APNS;
import com.shanjin.push.ios.apns.ApnsNotification;
import com.shanjin.push.ios.apns.ApnsService;
import com.shanjin.push.ios.apns.PayloadBuilder;

/**
 * 苹果推送消息发送类
 * @author Huang yulai
 *
 */
public class IosPushSend {

	private static ApnsService iphoneApnsService;
	private static String MESSAGE_KEY = "msg";
	
	
	/**
	 * 推送发送
	 * @param msg  消息体
	 * @param iosPushInfoList  推送设备对象
	 */
	public static void send(Map<String, Object> configMap,Map<String, Object> msg,List<Map<String, Object>> iosPushInfoList){
		
//		int pushType = StringUtil.nullToInteger(msg.get("pushType")).intValue();
//	    if (pushType == 8) {
//	      System.out.println("暂时去除多终端登录推送");
//	      return;
//	    }

		//判断推送数量是否超过设定的最大值
		int push_size =StringUtil.nullToInteger(configMap.get("push_size")==null?"20":configMap.get("push_size"));
		
		long orderId=StringUtil.nullToLong(msg.get("orderId"));	
		
		if(iosPushInfoList!=null&&iosPushInfoList.size()>0){
			
			IosPushSend ips = new IosPushSend();
			ips.init(configMap);
			for(int i=0;i<iosPushInfoList.size();i++){
				Map<String, Object> map = iosPushInfoList.get(i);
				
				//超过指定数量，则不推送
				if(push_size!=0 && i>push_size){
					BusinessUtil.writeLog("push",orderId+"-当前推送第"+i+"个，超过上限"+push_size);
					return ;
				}
				String token = StringUtil.null2Str(map.get("pushId"));
				String userId = StringUtil.null2Str(map.get("userId"));
				StringBuilder ms = new StringBuilder();// ios推送字节限制，将扩展的参数值都拼接在ms中，用“|”隔开
				ms.append(StringUtil.null2Str(msg.get("pushType"))).append("|");
				ms.append(StringUtil.null2Str(msg.get("orderId"))).append("|");
				ms.append(userId).append("|");
				long merchantId=StringUtil.nullToLong(map.get("merchantId"));
				if(merchantId == 0){
					merchantId=StringUtil.nullToLong(msg.get("merchantId"));
				}	
				ms.append(merchantId+"");			
//				ms.append(StringUtil.null2Str(msg.get("appType")));
				String alert = StringUtil.null2Str(msg.get("alertMsg"));

				if(!Constant.PRESSURETEST){
					BusinessUtil.writeLog("push",orderId+"-IOS推送对象："+map.toString());
					BusinessUtil.writeLog("push",orderId+"-IOS推送消息："+ms+"\n");
				}
				int badge = 0;
				try{
					ips.pushIphoneNotification(token, alert, badge, true, ms.toString());
				}catch(Exception e){
					throw new RuntimeException("推送通知出错");
				}
		     }
	    }
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Map<String, Object> msg = new HashMap<String, Object>();
//		msg = PushMessage.getPushMsg("",0L, 3);
//		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("pushId", "78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e");
//		map.put("userId", "145743968466565900");
//		iosPushInfoList.add(map);
//		send(msg,iosPushInfoList);
		
//		ApnsService service = APNS.newService()
//				.withCert(PushConfig.iphoneCertPath, PushConfig.iphoneCertPassword)	//使用指定的p12文件以及密匙
//				.withSandboxDestination()	//使用apple的测试服务器
//				.build();
//		//创建一个消息
//		String payload = APNS.newPayload()
//				.alertBody("hello world!")	//推送通知显示的文字
//				.sound("default")	//推送时附带的声音提示
//				.badge(1)	//应用程序图标右上角显示的数字
//				.build();
//		//token由客户端获取
//		String token = "78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e";
//		//发送消息到iOS设备
//		service.push(token, payload);

//		IosPushSend test = new IosPushSend();
//		test.init();
//		List<String> devList = new ArrayList<String>();
//		devList.add("78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e");
//		//devList.add("e7ab72d0da1b4afce22a4a646e117e8a07f9e8b4699da6f7a1b94b5800127806");
//		//devList.add("78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e");
//		
//		//String deviceToken = "de***********************************6";
//		String alert = "测试推送消息";
//		int badge = 0;
//		String message = "消息内容";
//		for(String deviceToken : devList){
//			test.pushIphoneNotification(deviceToken, alert, badge, true, message);
//		}

		
	}

	public  void init (Map<String,Object> configMap) {
		String iphoneCertPath=StringUtil.null2Str(configMap.get("push-iphoneCertPath"));
		if(StringUtil.isEmpty(iphoneCertPath)){
			iphoneCertPath=PushConfig.iphoneCertPath;
		}
		String iphoneCertPassword=StringUtil.null2Str(configMap.get("push-iphoneCertPassword"));
		if(StringUtil.isEmpty(iphoneCertPassword)){
			iphoneCertPassword=PushConfig.iphoneCertPassword;
		}
		
		
		//底层的代码根据isProduction来选择是生产环境的地址和端口还是测试环境的地址和端口
		if(Constant.DEVMODE){
			iphoneApnsService = APNS.newService().withCert(iphoneCertPath, iphoneCertPassword).withSandboxDestination().build();
		}else{
			iphoneApnsService = APNS.newService().withCert(iphoneCertPath, iphoneCertPassword).withAppleDestination(PushConfig.isProduction).build();	
		}
	


	}
	
	/**
	 * 推送单个iphone消息
	 */
	public  void pushIphoneNotification(String deviceToken,String alert,int badge,boolean soundTip,String message) {
		
		try {
			if (StringUtils.isEmpty(deviceToken)) {
				throw new RuntimeException("deviceToken为空");
			}
			
			String sound = null;
			if (soundTip) {
				sound = "default";
			}
			
			sendIphoneNotification(alert, badge, sound, message, deviceToken);
			
		} catch (Exception e) {
			throw new RuntimeException("推送通知出错");
		}
	}
	
	
	
	/**
	 * 推送iphone消息
	 * @param payload
	 * @param tokenList
	 * @param password
	 * @param production
	 */
	private  void sendIphoneNotification(String alert,int badge,String sound,String message, String deviceToken) {
		
		try {
			
			PayloadBuilder payloadBuilder = APNS.newPayload();
			if (StringUtils.isNotEmpty(alert)) {
				payloadBuilder.alertBody(alert);
			}
			if (badge > 0) {
				payloadBuilder.badge(badge);
			}
			if (StringUtils.isNotEmpty(sound)) {
				payloadBuilder.sound(sound);
			}
			if (StringUtils.isNotEmpty(message)) {
				payloadBuilder.customField(MESSAGE_KEY,message);
			}
			
			if (payloadBuilder.isTooLong()) {
				throw new RuntimeException("信息过长");
			}
			
//			//次数发送给单个设备，也可以同时发给多个设备
			ApnsNotification a=iphoneApnsService.push(deviceToken, payloadBuilder.build());
//			if(a==null){
//				System.out.println("推送失败");
//			}else{
//				System.out.println("推送成功");	
//			}
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("推送消息出错");
		}
	       
	}
	
}
