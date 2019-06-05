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

public class TestUserXLBOrderController {

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
    	String orderId = this.saveCeleModelOrder();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("xlb_hdb", orderId, "3");
    	orderId = this.saveCeleModelOrder2();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("xlb_hdb", orderId, "1");
    	orderId = this.saveWeddingCarOrder();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("xlb_xlb", orderId, "2");
//    	orderId = this.saveWeddingPhotoOrder();
//    	Thread.sleep(1000);
//    	testGetDetialOrderInfo("xlb_xlb", orderId, "3");
    	orderId = this.saveWeddingOrder();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("xlb_xlb", orderId, "4");
    }
    
	private String saveCeleModelOrder() throws Exception {
		init();
    	String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xlb_hdb"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "3"));
//        parameters.add(new BasicNameValuePair("celeOrModel", "2"));
//        parameters.add(new BasicNameValuePair("celeType", "1"));
//        parameters.add(new BasicNameValuePair("celeContent", "1,2,3,4"));
//        
        parameters.add(new BasicNameValuePair("modelType", "2"));
//        parameters.add(new BasicNameValuePair("modelSex", "2"));
        parameters.add(new BasicNameValuePair("modelDetail", "1:3|2:10"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("longitude", "117.278724"));
        parameters.add(new BasicNameValuePair("latitude", "31.88497"));
        parameters.add(new BasicNameValuePair("address", "安徽省合肥市庐阳区濉溪路287号"));
        parameters.add(new BasicNameValuePair("demand", "上美女"));
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
	
	private String saveCeleModelOrder2() throws Exception {
		init();
    	String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xlb_hdb"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "1"));
//        parameters.add(new BasicNameValuePair("celeOrModel", "1"));
        parameters.add(new BasicNameValuePair("celeType", "1"));
        parameters.add(new BasicNameValuePair("celeContent", "1,2,3,5"));
//        
//        parameters.add(new BasicNameValuePair("modelType", "2"));
//        parameters.add(new BasicNameValuePair("modelSex", "2"));
//        parameters.add(new BasicNameValuePair("modelDetail", "1:3|2:10"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("longitude", "117.278724"));
        parameters.add(new BasicNameValuePair("latitude", "31.88497"));
        parameters.add(new BasicNameValuePair("address", "安徽省合肥市庐阳区濉溪路287号"));
        parameters.add(new BasicNameValuePair("demand", "上美女"));
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
	
	private String saveWeddingPhotoOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xlb_xlb"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "3"));
        parameters.add(new BasicNameValuePair("photoType", "1,2,3"));
        parameters.add(new BasicNameValuePair("photoScene", "1,2"));
        parameters.add(new BasicNameValuePair("priceExpire", "3K-5K"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("longitude", "117.278724"));
        parameters.add(new BasicNameValuePair("latitude", "31.88497"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
	}
	
	private String saveWeddingCarOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xlb_xlb"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "2"));
        parameters.add(new BasicNameValuePair("leaderCarType", "保时捷Cayman"));
        parameters.add(new BasicNameValuePair("leaderCarDriver", "1"));
        parameters.add(new BasicNameValuePair("queueCarType", "一汽-大众CC"));
        parameters.add(new BasicNameValuePair("queueCarNumber", "9"));
        parameters.add(new BasicNameValuePair("rentTime", "1"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("longitude", "117.278724"));
        parameters.add(new BasicNameValuePair("latitude", "31.88497"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
	}
	
	private String saveWeddingOrder() throws Exception {
		init();
		String orderId = "";
		httpPost = new HttpPost(Constant.HOST+"/userOrder/saveOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "xlb_xlb"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "4"));
        parameters.add(new BasicNameValuePair("weddingType", "1"));
        parameters.add(new BasicNameValuePair("weddingRoles", "1,2,3"));
        parameters.add(new BasicNameValuePair("theme", "1"));
        parameters.add(new BasicNameValuePair("priceExpire", "5K-10K"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-07-22 15:00"));
        parameters.add(new BasicNameValuePair("longitude", "117.278724"));
        parameters.add(new BasicNameValuePair("latitude", "31.88497"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
	}
}
