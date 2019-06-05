package com.shanjin.controller.test;

import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

public class TestUserMerchantController {
	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private String host = Constant.HOST;
    private String appType = Constant.APP_TYPE;
    private String merchantId = "143926300447068375";
    private String userId = "66";
	@Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
	
	@Test
	public void userMerchant() throws Exception {
		// 参数： appType, userId,  merchantId
		httpPost = new HttpPost(host+"/userMerchant/collectionMerchant");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("userId", userId));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void delCollectionMerchant() throws Exception {
		// 参数： appType, userId,  merchantId
		httpPost = new HttpPost(host+"/userMerchant/delCollectionMerchant");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("userId", userId));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void getCollectionMerchant() throws Exception {
		// 参数： appType, userId, pageNo
		httpPost = new HttpPost(host+"/userMerchant/getCollectionMerchant");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("pageNo", "0"));
        parameters.add(new BasicNameValuePair("userId", userId));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
}
