package com.shanjin.controller.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class TestKingController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private String host = "http://192.168.1.33:8080";
    String time;
    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
        time = System.currentTimeMillis() + "";
    }

	
//	@Test
//	public void queryUserAssetAmount() throws Exception {
//		httpPost = new HttpPost(host + "/king/queryUserAssetAmount");
//		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//		parameters.add(new BasicNameValuePair("userId", "2"));
//		parameters.add(new BasicNameValuePair("pageNo", "0"));
//
//		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
//		httpPost.setEntity(entity);
//
//		CloseableHttpResponse resp = httpClient.execute(httpPost);
//		StatusLine status = resp.getStatusLine();
//		Assert.assertEquals(200, status.getStatusCode());
//		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
//		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
//		String resultCode = result.getString("resultCode");
//		Assert.assertEquals("000", resultCode);
//
//		System.out.println(responseJson);
//		resp.close();
//		httpClient.close();
//	}
//    
//    @Test
//	public void queryUserAssetRecorders() throws Exception {
//		httpPost = new HttpPost(host + "/king/queryUserAssetRecorders");
//		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//		parameters.add(new BasicNameValuePair("userId", "2"));
//		parameters.add(new BasicNameValuePair("pageSize", "2"));
//		parameters.add(new BasicNameValuePair("pageNo", "1"));
//
//		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
//		httpPost.setEntity(entity);
//
//		CloseableHttpResponse resp = httpClient.execute(httpPost);
//		StatusLine status = resp.getStatusLine();
//		Assert.assertEquals(200, status.getStatusCode());
//		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
//		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
//		String resultCode = result.getString("resultCode");
////		Assert.assertEquals("000", resultCode);
//
//		System.out.println(responseJson);
//		resp.close();
//		httpClient.close();
//	}
    
    @Test
	public void queryKingUserAssetInfo() throws Exception {
		httpPost = new HttpPost(host + "/king/queryKingUserAssetInfo");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userId", "147140308717208706"));

		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		httpPost.setEntity(entity);

		CloseableHttpResponse resp = httpClient.execute(httpPost);
		StatusLine status = resp.getStatusLine();
		Assert.assertEquals(200, status.getStatusCode());
		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
		String resultCode = result.getString("resultCode");
		Assert.assertEquals("000", resultCode);

//		System.out.println(responseJson);
		resp.close();
		httpClient.close();
	}
    
    @Test
  	public void queryKingInfo() throws Exception {
  		httpPost = new HttpPost(host + "/king/queryKingInfo");
  		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
  		parameters.add(new BasicNameValuePair("userId", "147140308717208706"));

  		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
  		httpPost.setEntity(entity);

  		CloseableHttpResponse resp = httpClient.execute(httpPost);
  		StatusLine status = resp.getStatusLine();
  		Assert.assertEquals(200, status.getStatusCode());
  		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
  		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
  		String resultCode = result.getString("resultCode");
  		Assert.assertEquals("000", resultCode);

//  		System.out.println(responseJson);
  		resp.close();
  		httpClient.close();
  	}
//    
//    @Test
//	public void userAssetPayment() throws Exception {
//		httpPost = new HttpPost(host + "/king/userAssetPayment");
//		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//		parameters.add(new BasicNameValuePair("userId", "2"));
//		parameters.add(new BasicNameValuePair("orderId", "4"));
//		parameters.add(new BasicNameValuePair("amount", "10.4"));
//		parameters.add(new BasicNameValuePair("orderType", "1"));
//		
//
//		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
//		httpPost.setEntity(entity);
//
//		CloseableHttpResponse resp = httpClient.execute(httpPost);
//		StatusLine status = resp.getStatusLine();
//		Assert.assertEquals(200, status.getStatusCode());
//		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
//		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
//		String resultCode = result.getString("resultCode");
////		Assert.assertEquals("000", resultCode);
//
//		System.out.println(responseJson);
//		resp.close();
//		httpClient.close();
//	}
//    
//    @Test
//	public void userAssetAdvance() throws Exception {
//		httpPost = new HttpPost(host + "/king/userAssetAdvance");
//		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//		parameters.add(new BasicNameValuePair("userId", "2"));
//		parameters.add(new BasicNameValuePair("orderId", "8"));
//		parameters.add(new BasicNameValuePair("amount", "10.8"));
//		parameters.add(new BasicNameValuePair("orderType", "1"));
//
//		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
//		httpPost.setEntity(entity);
//
//		CloseableHttpResponse resp = httpClient.execute(httpPost);
//		StatusLine status = resp.getStatusLine();
//		Assert.assertEquals(200, status.getStatusCode());
//		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
//		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
//		String resultCode = result.getString("resultCode");
////		Assert.assertEquals("000", resultCode);
//
//		System.out.println(responseJson);
//		resp.close();
//		httpClient.close();
//	}
    
//    @Test
//	public void userAssetPayCallBack() throws Exception {
//		httpPost = new HttpPost(host + "/king/userAssetPayCallBack");
//		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//		parameters.add(new BasicNameValuePair("userId", "2"));
//		parameters.add(new BasicNameValuePair("recorderId", "9"));
//		parameters.add(new BasicNameValuePair("status", "0"));
//
//		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
//		httpPost.setEntity(entity);
//
//		CloseableHttpResponse resp = httpClient.execute(httpPost);
//		StatusLine status = resp.getStatusLine();
//		Assert.assertEquals(200, status.getStatusCode());
//		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
//		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
//		String resultCode = result.getString("resultCode");
////		Assert.assertEquals("000", resultCode);
//
//		System.out.println(responseJson);
//		resp.close();
//		httpClient.close();
//	}
}
