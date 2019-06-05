package com.shanjin.manager.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.shanjin.manager.constant.Constant;


/**
 * 短信工具类
 */
public class SmsUtil {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(SmsUtil.class);

	/**
	 * 发送短信
	 */
	public static boolean sendSms(String mobile, String message) {
		boolean flag = true;

		try {
			String url = Constant.WEB_PROXY_URL + "/smsService";
			CloseableHttpClient hc = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("mobile", mobile ));
			nvps.add(new BasicNameValuePair("message", message + "【O盟】"));
			nvps.add(new BasicNameValuePair("key", Constant.SMS_KEY));
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			CloseableHttpResponse resp = hc.execute(post);
			System.out.println("sms send resp="+resp);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = resp.getEntity();
				String r = EntityUtils.toString(entity, "utf8");
				EntityUtils.consume(entity);
				if (r.indexOf("ok") == -1) {
					flag = false;
					throw new Exception("发送短信异常：" + mobile + "#" + r);
				}
			} else {
				flag = false;
				throw new Exception("发送短信异常："
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			logger.error("", e);
			return flag;
		}
		return flag;
	}
	
	public static void main(String[] args){
		SmsUtil.sendSms("18655119537", "小呆呆，恭喜您成功通过O盟服务商认证审核！");
	}


}
