package com.shanjin.common.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class Message {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(Message.class);

	public static void main(String[] args) {
		try {
			testSend("18355186949");
		} catch (Exception e) {
			logger.error("发送失败", e);
		}
	}

	private static void testSend(String tel) throws Exception {
		String url = "http://sms-api.luosimao.com/v1/send.xml";
		CloseableHttpClient hc = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.addHeader("Authorization", "Basic " + new Base64().encodeToString("api:key-807c8e393fa3a9aa889145c3e84c12ea".getBytes("utf-8")));

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		ArrayList nvps = new ArrayList();
		nvps.add(new BasicNameValuePair("mobile", tel));
		nvps.add(new BasicNameValuePair("message", "验证码:123456" + " 时间:" + df.format(new Date()) + "【善金科技】"));
		post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
		CloseableHttpResponse resp = hc.execute(post);
		if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = resp.getEntity();
			String r = EntityUtils.toString(entity, "utf8");
			EntityUtils.consume(entity);
			if (r.indexOf("ok") == -1) {
				throw new Exception("发送短信异常：" + tel + "#" + r);
			}
		} else {
			throw new Exception("发送短信异常：" + resp.getStatusLine().getStatusCode());
		}
	}

	private static String testStatus() throws Exception {
		String url = "http://sms-api.luosimao.com/v1/status.xml";
		CloseableHttpClient hc = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.addHeader("Authorization", "Basic " + new Base64().encodeToString("api:1dc74e82085555d13a308fas8da8da".getBytes("utf-8")));
		CloseableHttpResponse resp = hc.execute(post);
		if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = resp.getEntity();
			String r = EntityUtils.toString(entity, "utf8");
			EntityUtils.consume(entity);
			if (r.indexOf("0") == -1) {
				throw new Exception("查询状态异常：" + r);
			} else {
				return r;
			}
		} else {
			throw new Exception("查询状态异常：" + resp.getStatusLine().getStatusCode());
		}
	}

}
