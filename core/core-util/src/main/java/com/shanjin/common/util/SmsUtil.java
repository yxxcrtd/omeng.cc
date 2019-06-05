package com.shanjin.common.util;

import java.util.ArrayList;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;

/**
 * 短信工具类
 */
public class SmsUtil {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(SmsUtil.class);

	static String smskey;

	static String voicekey;
	
	public static void main(String[] args){
		String resultCode="";
		String phone="18655119537";
		String content="快来下载哦，http://www.meiwenting.com/a/201511/81391.html";
		try {
			String url = Constant.WEB_PROXY_URL + "/smsService";
			CloseableHttpClient hc = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);

			ArrayList nvps = new ArrayList();
			nvps.add(new BasicNameValuePair("mobile", phone));
			nvps.add(new BasicNameValuePair("message", content + "【O盟】"));
			nvps.add(new BasicNameValuePair("key", smskey));
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			CloseableHttpResponse resp = hc.execute(post);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = resp.getEntity();
				String response = EntityUtils.toString(entity, "utf8");
				EntityUtils.consume(entity);
				JSONObject resObject = JSON.parseObject(response);
				// 验证通过
				if (StringUtil.null2Str(resObject.get("error")).equals("0")) {

				} else if (StringUtil.null2Str(resObject.get("error")).equals("-40")) {
					resultCode = "-40";
				} else if (StringUtil.null2Str(resObject.get("error")).equals("-41")) {
					resultCode = "-41";
				} else if (StringUtil.null2Str(resObject.get("error")).equals("-42")) {
					resultCode = "-42";
				}
			} else {

				throw new Exception("发送短信异常：" + resp.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {

			e.printStackTrace();
			logger.error("", e);
		}
	}

	/**
	 * 发送短信
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public static JSONObject sendSms(String phone, String content) {
		JSONObject resultCode = new ResultJSONObject("000", "验证码已经发送,请耐心等候");
		System.out.println("开发模式为: " + Constant.DEVMODE);
//		if (!Constant.DEVMODE) {
			try {
				if(StringUtil.isNullStr(smskey)){
					smskey = "807c8e393fa3a9aa889145c3e84c12ea";
				}
				
				String url = Constant.WEB_PROXY_URL + "/smsService";       
				CloseableHttpClient hc = HttpClients.createDefault();
				HttpPost post = new HttpPost(url);

				ArrayList nvps = new ArrayList();
				nvps.add(new BasicNameValuePair("mobile", phone));
				nvps.add(new BasicNameValuePair("message", content + "【O盟】"));
				nvps.add(new BasicNameValuePair("key", smskey));
				post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
				CloseableHttpResponse resp = hc.execute(post);
				if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = resp.getEntity();
					String response = EntityUtils.toString(entity, "utf8");
					EntityUtils.consume(entity);
					JSONObject resObject = JSON.parseObject(response);
					// 验证通过
					if (StringUtil.null2Str(resObject.get("error")).equals("0")) {

					} else if (StringUtil.null2Str(resObject.get("error")).equals("-40")) {
						resultCode = new ResultJSONObject(StringUtil.null2Str(resObject.get("error")), "错误的手机号，检查手机号是否正确");
					} else if (StringUtil.null2Str(resObject.get("error")).equals("-41")) {
						resultCode = new ResultJSONObject(StringUtil.null2Str(resObject.get("error")), "号码因频繁发送或其他原因暂停发送，请联系开发人员");
					} else if (StringUtil.null2Str(resObject.get("error")).equals("-42")) {
						resultCode = new ResultJSONObject(StringUtil.null2Str(resObject.get("error")), "号码发送短信比较频繁，请稍后重试");
					}
				} else {
					resultCode = new ResultJSONObject("error", "发送短信验证码异常，请稍后重试");
					throw new Exception("发送短信异常：" + resp.getStatusLine().getStatusCode());
				}
			} catch (Exception e) {
				resultCode = new ResultJSONObject("error", "发送短信验证码异常，请稍后重试");
				e.printStackTrace();
				logger.error("", e);
			}
//		}
		return resultCode;
	}

	/**
	 * 发送短信
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public static JSONObject sendVoice(String phone, String content) {
		JSONObject resultCode = new ResultJSONObject("000", "验证码已经发送,请耐心等候");
		if (!Constant.DEVMODE) {
			try {
				if(StringUtil.isNullStr(smskey)){
					smskey = "1ef47b6e0b3dee01b583d988d4ae5bb9";
				}
				String url = Constant.WEB_PROXY_URL + "/voiceService";
				CloseableHttpClient hc = HttpClients.createDefault();
				HttpPost post = new HttpPost(url);

				ArrayList nvps = new ArrayList();
				nvps.add(new BasicNameValuePair("mobile", phone));
				nvps.add(new BasicNameValuePair("code", content));
				nvps.add(new BasicNameValuePair("key", voicekey));
				post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
				CloseableHttpResponse resp = hc.execute(post);
				if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = resp.getEntity();
					String response = EntityUtils.toString(entity, "utf8");
					EntityUtils.consume(entity);
					JSONObject resObject = JSON.parseObject(response);
					if (StringUtil.null2Str(resObject.get("error")).equals("0")) {

					} else if (StringUtil.null2Str(resObject.get("error")).equals("-40")) {
						resultCode = new ResultJSONObject(StringUtil.null2Str(resObject.get("error")), "错误的手机号，检查手机号是否正确");
					}
				} else {
					resultCode = new ResultJSONObject("error", "发送语音验证码异常，请稍后重试");
					throw new Exception("发送语言验证码异常：" + resp.getStatusLine().getStatusCode());
				}
			} catch (Exception e) {
				resultCode = new ResultJSONObject("error", "发送语音验证码异常，请稍后重试");
				e.printStackTrace();
				logger.error("", e);
			}
		}
		return resultCode;
	}

	/**
	 * 异步发送 文字短信
	 * @param phone
	 * @param content
	 */
	public static void asyncSendMsg(final String phone,final String content) {
			CalloutServices.executor(new Runnable(){
				@Override
				public void run() {
					sendSms(phone,content);
				}
			});
	}
	
	public static String getSmskey() {
		return smskey;
	}

	public static void setSmskey(String smskey) {
		SmsUtil.smskey = smskey;
	}

	public static String getVoicekey() {
		return voicekey;
	}

	public static void setVoicekey(String voicekey) {
		SmsUtil.voicekey = voicekey;
	}

}
