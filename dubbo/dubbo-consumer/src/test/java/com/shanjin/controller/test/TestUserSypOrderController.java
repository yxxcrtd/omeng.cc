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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.MD5Util;

public class TestUserSypOrderController {

	private CloseableHttpClient httpClient;
	private HttpPost httpPost;
	private String APP_TYPE="syp";
	@Before
	public void init() {
		httpClient = HttpClients.createDefault();
	}

	@Test
	public void testGetBasicOrderList() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getBasicOrderList");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", APP_TYPE));
		parameters.add(new BasicNameValuePair("userId", "66"));
		parameters.add(new BasicNameValuePair("pageNo", "0"));

		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		httpPost.setEntity(entity);

		CloseableHttpResponse resp = httpClient.execute(httpPost);
		StatusLine status = resp.getStatusLine();
		Assert.assertEquals(200, status.getStatusCode());
		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
		String resultCode = result.getString("resultCode");
		Assert.assertEquals("000", resultCode);

		System.out.println(responseJson);
		resp.close();
		httpClient.close();
	}

	@Test
	public void testGetBasicOrder() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getBasicOrder");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", APP_TYPE));
		parameters.add(new BasicNameValuePair("orderId", "934"));

		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		httpPost.setEntity(entity);

		CloseableHttpResponse resp = httpClient.execute(httpPost);
		StatusLine status = resp.getStatusLine();
		Assert.assertEquals(200, status.getStatusCode());
		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
		String resultCode = result.getString("resultCode");
		Assert.assertEquals("000", resultCode);

		System.out.println(responseJson);
		resp.close();
		httpClient.close();
	}

	@Test
	public void testGetDetialOrderInfo() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getDetialOrderInfo");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", APP_TYPE));
		parameters.add(new BasicNameValuePair("orderId", "934"));

		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		httpPost.setEntity(entity);

		CloseableHttpResponse resp = httpClient.execute(httpPost);
		StatusLine status = resp.getStatusLine();
		Assert.assertEquals(200, status.getStatusCode());
		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
		String resultCode = result.getString("resultCode");
		Assert.assertEquals("000", resultCode);

		System.out.println(responseJson);
		resp.close();
		httpClient.close();
	}

	@Test
	public void testSaveOrder() throws Exception {
	
		String serviceType = "1";
		
		{
			//  保存订单
			this.saveSypOrder(serviceType);
		
		}

	}

	@Test
	public void testUserEvaluation() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/userEvaluation");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", APP_TYPE));
		parameters.add(new BasicNameValuePair("merchantId", "143883174936807920"));
		parameters.add(new BasicNameValuePair("pageNo", "0"));

		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		httpPost.setEntity(entity);

		CloseableHttpResponse resp = httpClient.execute(httpPost);
		StatusLine status = resp.getStatusLine();
		Assert.assertEquals(200, status.getStatusCode());
		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
		String resultCode = result.getString("resultCode");
		Assert.assertEquals("000", resultCode);

		System.out.println(responseJson);
		resp.close();
		httpClient.close();
	}

	@Test
	public void testGetMerchantInfo() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getMerchantInfo");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", APP_TYPE));
		parameters.add(new BasicNameValuePair("merchantId", "143883174936807920"));
		parameters.add(new BasicNameValuePair("userId", "66"));

		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		httpPost.setEntity(entity);

		CloseableHttpResponse resp = httpClient.execute(httpPost);
		StatusLine status = resp.getStatusLine();
		Assert.assertEquals(200, status.getStatusCode());
		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
		String resultCode = result.getString("resultCode");
		Assert.assertEquals("000", resultCode);

		System.out.println(responseJson);
		resp.close();
		httpClient.close();
	}

	@Test
	public void testGetOrderPushType() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getOrderPushType");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", APP_TYPE));
		parameters.add(new BasicNameValuePair("orderId", "264"));

		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		httpPost.setEntity(entity);

		CloseableHttpResponse resp = httpClient.execute(httpPost);
		StatusLine status = resp.getStatusLine();
		Assert.assertEquals(200, status.getStatusCode());
		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
		String resultCode = result.getString("resultCode");
		Assert.assertEquals("000", resultCode);

		System.out.println(responseJson);
		resp.close();
		httpClient.close();
	}

	private String saveSypOrder(String serviceType) throws Exception {
		String orderId = "";
		// Step1: 保存订单
		httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", APP_TYPE));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", serviceType));
        parameters.add(new BasicNameValuePair("shootScene", "1"));
        parameters.add(new BasicNameValuePair("hssyShootStyle", "1"));
        parameters.add(new BasicNameValuePair("priceExpect", "2"));
        parameters.add(new BasicNameValuePair("shootTime", "2015-07-18 10:10:45"));
        parameters.add(new BasicNameValuePair("longitude", "117.278513"));
        parameters.add(new BasicNameValuePair("latitude", "31.88492"));
        parameters.add(new BasicNameValuePair("address", "安徽省合肥市庐阳区濉溪路287号"));
        parameters.add(new BasicNameValuePair("demand", "我是测试"));
		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		httpPost.setEntity(entity);

		CloseableHttpResponse resp = httpClient.execute(httpPost);
		StatusLine status = resp.getStatusLine();
		Assert.assertEquals(200, status.getStatusCode());
		String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
		JSONObject result = (JSONObject) JSONObject.parse(responseJson);
		String resultCode = result.getString("resultCode");
		Assert.assertEquals("000", resultCode);
		System.out.println(responseJson);
		JSONObject orderInfo = result.getJSONObject("orderInfo");
		orderId = orderInfo.getString("orderId");
		resp.close();
		httpClient.close();
		return orderId;
	}

	
}
