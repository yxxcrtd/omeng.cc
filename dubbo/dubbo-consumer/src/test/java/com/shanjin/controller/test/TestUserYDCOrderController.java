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

public class TestUserYDCOrderController {
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
    	String orderId = this.saveOrder();
    	Thread.sleep(1000);
    	this.testGetDetialOrderInfo("ydc", orderId, "1");
    }
    
    private String saveOrder() throws Exception {
    	init();
    	String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", "ydc"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "1"));
        parameters.add(new BasicNameValuePair("peopleNum", "2"));
        parameters.add(new BasicNameValuePair("eatType", "2"));
        parameters.add(new BasicNameValuePair("eatAddress", "1"));
        parameters.add(new BasicNameValuePair("specialRequirements", "1,2"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-08-30 16:00"));
        parameters.add(new BasicNameValuePair("longitude", "117.274511"));
        parameters.add(new BasicNameValuePair("latitude", "31.884904"));
        parameters.add(new BasicNameValuePair("address", "安徽省合肥市庐阳区界首路19号"));
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
