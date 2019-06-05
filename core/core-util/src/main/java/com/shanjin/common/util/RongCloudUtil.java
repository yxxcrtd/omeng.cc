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

public class RongCloudUtil {
	private static final Logger logger = Logger.getLogger(RongCloudUtil.class);
	
	
	public static String getRongCloudTokenFromProxy(String uid,String name,String portraitUri){
		String token = "";
		try {
			String url = Constant.WEB_PROXY_URL + "/rongCloudService";
			//String url = "http://localhost:8080/rongCloudService";
			CloseableHttpClient hc = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			ArrayList nvps = new ArrayList();
			nvps.add(new BasicNameValuePair("uid", uid));
			nvps.add(new BasicNameValuePair("name", name));
			nvps.add(new BasicNameValuePair("portraitUri", portraitUri));
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			CloseableHttpResponse resp = hc.execute(post);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = resp.getEntity();
				String response = EntityUtils.toString(entity, "utf8");
				EntityUtils.consume(entity);
				JSONObject resObject = JSON.parseObject(response);
				token = resObject.getString("token");
			} else {
				token = "proxyerror";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取融云token，web proxy服务器异常", e);
			token = "proxyerror";
			return token;
		}
		return token;
	}
	
	public static void main(String[] args){
		RongCloudUtil.getRongCloudTokenFromProxy("6","","");
	}
	
}
