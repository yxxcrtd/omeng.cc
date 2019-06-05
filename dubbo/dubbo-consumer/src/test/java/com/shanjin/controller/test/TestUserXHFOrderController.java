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

public class TestUserXHFOrderController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    
    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
    
    private void testGetDetialOrderInfo(String appType, String orderId, String serviceType) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/getDetialOrderInfo");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", appType));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	parameters.add(new BasicNameValuePair("serviceType", serviceType));
    	
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
    	
    	JSONArray orderText = result.getJSONArray("orderText");
    	for(int i=0; i<orderText.size(); i++) {
    		String text = orderText.getString(i);
    		String[] temp = text.split("：");
    		Assert.assertFalse(temp.length < 2);
    		boolean b = "".equals(temp[1]) || "null".equals(temp[1]);
    		Assert.assertFalse(b);
    	}
    	
    	resp.close();
    	httpClient.close();
    }
    
    @Test
    public void testEntireProcess() throws Exception {
    	String orderId = this.saveFlowerBindOrder();
    	Thread.sleep(1000);
    	this.testGetDetialOrderInfo("xhf", orderId, "2");
    	orderId = this.saveBoxOrder();
    	Thread.sleep(1000);
    	this.testGetDetialOrderInfo("xhf", orderId, "2");
    	orderId = this.saveFlowerBasketOrder();
    	Thread.sleep(1000);
    	this.testGetDetialOrderInfo("xhf", orderId, "2");
    	orderId = this.saveFruitBasketOrder();
    	Thread.sleep(1000);
    	this.testGetDetialOrderInfo("xhf", orderId, "2");
    }
    
	private String saveFlowerBindOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xhf"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "2"));
        parameters.add(new BasicNameValuePair("flowerStyle", "1"));
        parameters.add(new BasicNameValuePair("flowerType", "1"));
//        parameters.add(new BasicNameValuePair("boxType", "1"));
        parameters.add(new BasicNameValuePair("orderItems", "1:10|9:9"));
//        parameters.add(new BasicNameValuePair("basketType", "1"));
//        parameters.add(new BasicNameValuePair("quantity", "0"));
        parameters.add(new BasicNameValuePair("blessings", "I LOVE U"));
        parameters.add(new BasicNameValuePair("deliveryTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("deliverySite", "安徽省合肥市庐阳区濉溪路287号"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
	}
	
	private String saveBoxOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xhf"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "2"));
        parameters.add(new BasicNameValuePair("flowerStyle", "2"));
        parameters.add(new BasicNameValuePair("flowerType", "1"));
        parameters.add(new BasicNameValuePair("boxType", "1"));
        parameters.add(new BasicNameValuePair("orderItems", "1:10|9:9"));
//        parameters.add(new BasicNameValuePair("basketType", "1"));
//        parameters.add(new BasicNameValuePair("quantity", "0"));
        parameters.add(new BasicNameValuePair("blessings", "I LOVE U"));
        parameters.add(new BasicNameValuePair("deliveryTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("deliverySite", "安徽省合肥市庐阳区濉溪路287号"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
	}
	
	private String saveFlowerBasketOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xhf"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "2"));
        parameters.add(new BasicNameValuePair("flowerStyle", "3"));
//        parameters.add(new BasicNameValuePair("flowerType", "1"));
//        parameters.add(new BasicNameValuePair("boxType", "1"));
//        parameters.add(new BasicNameValuePair("orderItems", "1:10|9:9"));
        parameters.add(new BasicNameValuePair("basketType", "1"));
        parameters.add(new BasicNameValuePair("quantity", "1"));
        parameters.add(new BasicNameValuePair("blessings", "I LOVE U"));
        parameters.add(new BasicNameValuePair("deliveryTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("deliverySite", "安徽省合肥市庐阳区濉溪路287号"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
	}
	
	private String saveFruitBasketOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xhf"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "2"));
        parameters.add(new BasicNameValuePair("flowerStyle", "3"));
//        parameters.add(new BasicNameValuePair("flowerType", "1"));
//        parameters.add(new BasicNameValuePair("boxType", "1"));
        parameters.add(new BasicNameValuePair("orderItems", "1:10|2:9"));
        parameters.add(new BasicNameValuePair("basketType", "2"));
//        parameters.add(new BasicNameValuePair("quantity", "0"));
        parameters.add(new BasicNameValuePair("blessings", "I LOVE U"));
        parameters.add(new BasicNameValuePair("deliveryTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("deliverySite", "安徽省合肥市庐阳区濉溪路287号"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
	}
}
