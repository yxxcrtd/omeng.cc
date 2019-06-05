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

public class TestUserDGFOrderController {

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
    	String orderId = this.saveCakeOrder();
    	Thread.sleep(1000);
    	this.testGetDetialOrderInfo("dgf", orderId, "1");
    	orderId = this.savePastyOrder();
    	Thread.sleep(1000);
    	this.testGetDetialOrderInfo("dgf", orderId, "5");
    	orderId = this.saveBreadOrder();
    	Thread.sleep(1000);
    	this.testGetDetialOrderInfo("dgf", orderId, "6");
    }
    
	private String saveCakeOrder() throws Exception {
    	init();
    	String orderId="";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "dgf"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "1"));
        parameters.add(new BasicNameValuePair("cakeTaste", "1"));
        parameters.add(new BasicNameValuePair("cakeShape", "2"));
        parameters.add(new BasicNameValuePair("cakeSize", "8寸"));
        parameters.add(new BasicNameValuePair("wishPeople", "老婆"));
        parameters.add(new BasicNameValuePair("blessings", "生日快乐"));
        parameters.add(new BasicNameValuePair("isVisit", "1"));
        parameters.add(new BasicNameValuePair("deliveryTime", "20150718 10：10"));
        parameters.add(new BasicNameValuePair("longitude", "117.278513"));
        parameters.add(new BasicNameValuePair("latitude", "31.88492"));
        parameters.add(new BasicNameValuePair("address", "安徽省合肥市庐阳区濉溪路287号"));
        parameters.add(new BasicNameValuePair("demand", "我是测试我是测试我是测试我是测试我是测试我是测试"));
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
	
	private String savePastyOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "dgf"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "5"));
        parameters.add(new BasicNameValuePair("pastryType", "1"));
        parameters.add(new BasicNameValuePair("pastryWeight", "2千克"));
        parameters.add(new BasicNameValuePair("isVisit", "1"));
        parameters.add(new BasicNameValuePair("deliveryTime", "2015-07-21 10：10"));
        parameters.add(new BasicNameValuePair("longitude", "117.278513"));
        parameters.add(new BasicNameValuePair("latitude", "31.88492"));
        parameters.add(new BasicNameValuePair("address", "安徽省合肥市庐阳区濉溪路287号"));
        parameters.add(new BasicNameValuePair("demand", "只要一种，少放糖"));
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
	
	private String saveBreadOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "dgf"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "6"));
        parameters.add(new BasicNameValuePair("breadType", "1"));
        parameters.add(new BasicNameValuePair("breadUnit", "2"));
        parameters.add(new BasicNameValuePair("isVisit", "1"));
        parameters.add(new BasicNameValuePair("deliveryTime", "2015-07-21 10：10"));
        parameters.add(new BasicNameValuePair("longitude", "117.278513"));
        parameters.add(new BasicNameValuePair("latitude", "31.88492"));
        parameters.add(new BasicNameValuePair("address", "安徽省合肥市庐阳区濉溪路287号"));
        parameters.add(new BasicNameValuePair("demand", "只要一种，少放糖"));
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
