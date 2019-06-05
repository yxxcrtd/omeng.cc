package com.shanjin.web.proxy.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.shanjin.web.proxy.util.StringUtil;

/**
 * Servlet implementation class doPush
 */
@WebServlet("/doPushToSHB")
public class doPushToSHB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String host = "http://sdk.open.api.igexin.com/apiex.htm";
	private static Logger logger = Logger.getLogger(doPushToSHB.class);

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String AndroidPushInfo = request.getParameter("AndroidPushInfo");
		String transmissionContent = request.getParameter("transmissionContent");
		String pushMap = request.getParameter("pushMap");
		String concatKey = request.getParameter("concatKey");
		System.out.println("");
		System.out.println("=====安卓商户版推送 时间 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "====");
		System.out.println("SHB AndroidPushInfo:" + AndroidPushInfo);
		System.out.println("SHB transmissionContent:" + transmissionContent);
		System.out.println("SHB pushMap:" + pushMap);
		if (AndroidPushInfo != null && transmissionContent != null && pushMap != null) {
			List<Map<String, Object>> AndroidPushInfoObject = (List<Map<String, Object>>) JSONObject.parse(AndroidPushInfo);
			Map<String, Object> pushMapObject = JSONObject.parseObject(pushMap);
			if (pushMapObject != null) {
				System.out.println("商户版 推送配置 pushMapObject: " + pushMapObject);
				String appkey = StringUtil.null2Str(pushMapObject.get("appKey"));
				String master = StringUtil.null2Str(pushMapObject.get("masterSecret"));
				String appId = StringUtil.null2Str(pushMapObject.get("appId"));
				IGtPush push = new IGtPush(host, appkey, master);
				try {
					push.connect();

					IPushResult ret = null;
					for (Map<String, Object> pushInfo : AndroidPushInfoObject) {
						System.out.println("商户版 pushInfo:" + pushInfo);
						String transData = transmissionContent;
						transData = transData.concat("," + StringUtil.null2Str(pushInfo.get(concatKey)));
						transData = transData.concat("," + StringUtil.null2Str(pushInfo.get("appType")));
						System.out.println("transData:" + transData);
						TransmissionTemplate template = TransmissionTemplate(transData, pushMapObject);
						SingleMessage message = new SingleMessage();
						message.setData(template);
						message.setOffline(true);
						message.setOfflineExpireTime(24 * 1000 * 3600);

						Target target = new Target();
						target.setAppId(appId);
						target.setClientId(StringUtil.null2Str(pushInfo.get("clientId")));

						try {
							ret = push.pushMessageToSingle(message, target);
						} catch (Exception e) {
							e.printStackTrace();
							ret = push.pushMessageToSingle(message, target);
						}
						if (ret != null) {
							System.out.println("个推返回结果：  pushInfo:" + pushInfo + "  " + ret.getResponse().toString());
							logger.info("个推返回结果：  pushInfo:" + pushInfo + "  " + ret.getResponse().toString());
						} else {
							System.out.println("个推返回结果为空");
							logger.info("个推返回结果为空");
						}

					}
					push.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					push.close();
				}
			} else {
				System.out.println("推送配置为空");
				logger.error("推送配置为空");
			}
		}
	}

	public static TransmissionTemplate TransmissionTemplate(String transmissionContent, Map<String, Object> pushMap) throws Exception {
		String appkey = "";
		String appId = "";
		TransmissionTemplate template = new TransmissionTemplate();
		appId = StringUtil.null2Str(pushMap.get("appId"));
		appkey = StringUtil.null2Str(pushMap.get("appKey"));
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTransmissionType(2);
		template.setTransmissionContent(transmissionContent);
		return template;
	}
}
