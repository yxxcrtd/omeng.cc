package com.shanjin.push.android;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;

import nsp.NSPClient;
import nsp.OAuth2Client;
import nsp.support.common.AccessToken;
import nsp.support.common.NSPException;

public class HuaweiPushSend {
	public static final String TIMESTAMP_NORMAL = "yyyy-MM-dd HH:mm:ss";
	static String appId = "10381452";
	static String appKey = "1a98x1td102vehwh27biosdk44unv0ec";

	/**
	 * 方法表述
	 * 
	 * @param args
	 *            void
	 * @throws NSPException
	 */
	public static void send(Map<String, Object> configMap,Map<String, Object> msg,String concatKey,List<Map<String, Object>> huaweiList) {
		try {
			NSPClient client=init();
			// 调用push单发接口
			notification_send(client,msg,huaweiList,concatKey);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static NSPClient init() throws NSPException {
		NSPClient client = null;
		try {
			/*
			 * 获取token的方法 appId为开发者联盟上面创建应用的APP ID appKey为开发者联盟上面创建应用的 APP
			 * SECRET APP ID：appid100 应用包名：com.open.test | APP
			 * SECRET：xxxxdtsb4abxxxlz2uyztxxxfaxxxxxx
			 */
			OAuth2Client oauth2Client = new OAuth2Client();

			// 读取认证文件流
			oauth2Client.initKeyStoreStream(
					HuaweiPushSend.class.getResource("/mykeystorebj.jks")
							.openStream(), "123456");

			AccessToken access_token = oauth2Client.getAccessToken(
					"client_credentials", appId, appKey);

			client = new NSPClient(access_token.getAccess_token());
			client.initHttpConnections(30, 50);// 设置每个路由的连接数和最大连接数
			client.initKeyStoreStream(
					HuaweiPushSend.class.getResource("/mykeystorebj.jks")
							.openStream(), "123456");// 如果访问https必须导入证书流和密码
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client;
	}

	/**
	 * 通知栏消息接口
	 * 
	 * @param client
	 * @throws NSPException
	 */
	public static void notification_send(NSPClient client,Map<String, Object> msg,List<Map<String, Object>> huaweiList,String concatKey) throws NSPException {
		long orderId=StringUtil.nullToLong(msg.get("orderId"));	
		long currentTime = System.currentTimeMillis();
		SimpleDateFormat dataFormat = new SimpleDateFormat(TIMESTAMP_NORMAL);
		// 推送范围，必选
		// 1：指定用户，必须指定tokens字段
		// 2：所有人，无需指定tokens，tags，exclude_tags
		// 3：一群人，必须指定tags或者exclude_tags字段
		Integer push_type = 1;

		// 目标用户，可选
		// 当push_type=1时，该字段生效
//		String tokens = "00000000000000000000000000000000,00000000000000000000000000000000";

		// 标签，可选
		// 当push_type的取值为2时，该字段生效
		String tags = "{\"tags\":[]}";

		// 排除的标签，可选
		// 当push_type的取值为2时，该字段生效
		String exclude_tags = "{\"exclude_tags\":[]}";

		// 消息内容，必选
		// 该样例是点击通知消息打开url连接。更多的android样例请参考http://developer.huawei.com/ -> 资料中心
		// -> Push服务 -> API文档 -> 4.2.1 android结构体
//		String android = "{\"notification_title\":\"the good news!\",\"notification_content\":\"Price reduction!\",\"doings\":3,\"url\":\"vmall.com\"}";

		// 消息发送时间，可选
		// 如果不携带该字段，则表示消息实时生效。实际使用时，该字段精确到分
		// 消息发送时间戳，timestamp格式ISO 8601：2013-06-03T17:30:08+08:00
		String send_time = "";

		// 消息过期时间，可选
		// timestamp格式ISO 8601：2013-06-03T17:30:08+08:00
//		String expire_time = dataFormat.format(currentTime + 3 * 60 * 60 * 1000);
        String expire_time = "";
		
		for(Map<String, Object> map : huaweiList){

			// 目标用户，必选。 由客户端获取， 32 字节长度。手机上安装了push应用后，会到push服务器申请token，申请到的token会上报给应用服务器
			String tokens=StringUtil.null2Str(map.get("pushId"));
			String concatValue=StringUtil.null2Str(map.get(concatKey));
			if(StringUtil.isEmpty(concatKey)){
				concatValue=StringUtil.null2Str(map.get("userId"));
			}			
			Long merchantId=StringUtil.nullToLong(map.get("merchantId"));
			String pushType=StringUtil.null2Str(msg.get("pushType"));
				
			String android="{\"notification_title\":\"O盟\",\"notification_content\":\""+StringUtil.null2Str(msg.get("alertMsg"))+"\",\"doings\":2,\"extras\":[{\"userId\":\""+concatValue+"\"},{\"merchantId\":\""+merchantId+"\"},{\"orderId\":\""+orderId+"\"},{\"pushType\":\""+pushType+"\"}]}";
			
			// 构造请求
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("push_type", push_type);
			hashMap.put("tokens", tokens);
			hashMap.put("tags", tags);
			hashMap.put("exclude_tags", exclude_tags);
			hashMap.put("android", android);
			hashMap.put("send_time", send_time);
			hashMap.put("expire_time", expire_time);
			System.out.println("推送的消息："+hashMap.toString());
			// 设置http超时时间
			client.setTimeout(10000, 15000);
			// 接口调用
			String rsp = client.call("openpush.openapi.notification_send", hashMap,
					String.class);
	
			// 打印响应
			// 响应样例：{"result_code":0,"request_id":"1380075138"}
			System.err.println("通知栏消息接口响应：" + rsp);
			BusinessUtil.writeLog("push",orderId+"-华为推送消息响应:" + rsp);
		}
	}

	/**
	 * 设置用户标签
	 * 
	 * @param client
	 * @throws NSPException
	 */
	public static void set_user_tag(NSPClient client) throws NSPException {
		// deviceToken，必选
		// 由客户端获取， 32 字节长度。手机上安装了push应用后，会到push服务器申请token，申请到的token会上报给应用服务器
		String token = "00000000000000000000000000000000";

		// 标签类型，必选
		String tag_key = "location";

		// 标签值，必选
		String tag_value = "NanJing";

		// 构造请求
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("token", token);
		hashMap.put("tag_key", tag_key);
		hashMap.put("tag_value", tag_value);

		// 设置http超时时间
		client.setTimeout(10000, 15000);
		String rsp = client.call("openpush.openapi.set_user_tag", hashMap,
				String.class);

		// 打印响应
		// 响应样例：{"result_code":"0","result_desc":"success","request_id":"1380075009"}
		System.err.println("设置标签接口响应：" + rsp);
	}

	/**
	 * 查询应用标签接口
	 * 
	 * @param client
	 * @throws NSPException
	 */
	public static void query_app_tags(NSPClient client) throws NSPException {
		// 设置http超时时间
		client.setTimeout(10000, 15000);
		// 接口调用
		String rsp = client.call("openpush.openapi.query_app_tags",
				new HashMap<String, Object>(), String.class);

		// 打印响应
		// 响应样例：{"request_id":"1373593606","tags":[{"应用激活状态":["已激活"]},{"name":["1","hkdajc","hshshsh","jkg\n","rt","tyu","yu"]},{"age":["1","25","45","56","6644","gh","容易"]}]}
		System.err.println("查询应用标签接口响应：" + rsp);

	}

	/**
	 * 删除用户标签
	 * 
	 * @param client
	 * @throws NSPException
	 */
	public static void delete_user_tag(NSPClient client) throws NSPException {
		// deviceToken，必选
		// 由客户端获取， 32 字节长度。手机上安装了push应用后，会到push服务器申请token，申请到的token会上报给应用服务器
		String token = "00000000000000000000000000000000";

		// 标签类型，必选
		String tag_key = "age";

		// 构造请求
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("token", token);
		hashMap.put("tag_key", tag_key);

		// 设置http超时时间
		client.setTimeout(10000, 15000);
		// 接口调用
		String rsp = client.call("openpush.openapi.delete_user_tag", hashMap,
				String.class);

		// 打印响应
		// 响应样例：{"result_code":"0","result_desc":"success","request_id":"1380075041"}
		System.err.println("删除用户标签接口响应：" + rsp);
	}

	/**
	 * 查询用户标签
	 * 
	 * @param client
	 * @throws NSPException
	 */
	public static void query_user_tag(NSPClient client) throws NSPException {
		// deviceToken，必选
		// 由客户端获取， 32 字节长度。手机上安装了push应用后，会到push服务器申请token，申请到的token会上报给应用服务器
		String token = "00000000000000000000000000000000";

		// 构造请求
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("token", token);

		// 设置http超时时间
		client.setTimeout(10000, 15000);
		// 接口调用
		String rsp = client.call("openpush.openapi.query_user_tag", hashMap,
				String.class);

		// 打印响应
		// 响应样例：{"request_id":"1380075076","tags":[{"应用激活状态":"已激活"},{"location":"NanJing"},{"name":"yewei"}]}
		System.err.println("查询用户标签接口响应：" + rsp);
	}

	/**
	 * 调用查询查询消息发送结果接口
	 * 
	 * @param client
	 * @throws NSPException
	 */
	public static void query_msg_result(NSPClient client) throws NSPException {
		// 开发者调用sengle_send和batch_send接口时返回的requestID字段值
		String request_id = "";

		// 用户标识
		// 如果携带该字段，则表示查询request_id中的token对应的消息结果；如果不携带该字段，则查询request_id对应的所有token的消息结果
		String token = "";

		// 构造请求
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("request_id", request_id);
		hashMap.put("token", token);

		// 设置http超时时间
		client.setTimeout(10000, 15000);
		// 接口调用
		String rsp = client.call("openpush.openapi.query_msg_result", hashMap,
				String.class);

		// 打印响应
		// 响应样例：{"result":[{"status":0,"token":"00000000000000000000000000000000"}],"request_id":"123456"}
		System.err.println("查询查询消息发送结果接口：" + rsp);

	}

	/**
	 * 
	 * @param client
	 * @throws NSPException
	 */
	public static void get_token_by_date(NSPClient client) throws NSPException {
		// 构造请求
		// 时间最早不能超过2014-01-01，最晚为当天的前一天
		String date = "2014-07-16";

		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("date", date);

		// 设置http超时时间
		client.setTimeout(10000, 15000);
		String rsp = client.call("openpush.openapi.get_token_by_date", hashMap,
				String.class);

		// 打印响应
		// 响应样例：{"request_id":"1399551889637254","result_code":"0","tokenFile_url":"http://huaweipushtoken.dbankcloud.com/0001011454000001/2014-05-07.zip?ts=1399595089&key=e7717b69","unzip_password":"9cd86e68-d4ad-42"}
		System.err.println("查询查询消息发送结果接口：" + rsp);

	}
}
