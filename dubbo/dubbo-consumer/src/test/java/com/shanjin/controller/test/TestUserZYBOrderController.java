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

public class TestUserZYBOrderController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private String appType = "zyb";
    
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
    	String orderId = this.testSaveMoveOrder();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("zyb_kbj", orderId, "1");
    	orderId = this.testSaveCleanOrder();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("zyb_ybj", orderId, "2");
    	orderId = this.testSaveNannyOrder();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("zyb_ajz", orderId, "3");
    	orderId = this.testSaveRepaireOrder();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("zyb_smx", orderId, "4");
    	orderId = this.testSaveWashOrder();
    	Thread.sleep(1000);
    	testGetDetialOrderInfo("zyb_kxy", orderId, "5");
    }
    
    private String testSaveMoveOrder() throws Exception {
    	init();
    	String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", "zyb_kbj"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "1"));
        parameters.add(new BasicNameValuePair("startSiteLongitude", "117.278601"));
        parameters.add(new BasicNameValuePair("startSiteLatitude", "31.884904"));
        parameters.add(new BasicNameValuePair("startSite", "安徽省合肥市庐阳区沿河路"));
        parameters.add(new BasicNameValuePair("endSiteLongitude", "117.274511"));
        parameters.add(new BasicNameValuePair("endSiteLatitude", "31.89196"));
        parameters.add(new BasicNameValuePair("endSite", "安徽省合肥市庐阳区界首路19号"));
        parameters.add(new BasicNameValuePair("startSiteHaveElevator", "1"));
        parameters.add(new BasicNameValuePair("endSiteHaveElevator", "1"));
        parameters.add(new BasicNameValuePair("startSiteFloor", "7"));
        parameters.add(new BasicNameValuePair("endSiteFloor", "15"));
        parameters.add(new BasicNameValuePair("serviceTime", "31.88492"));
        parameters.add(new BasicNameValuePair("moveArticle", "家具,贵重物品,其他物品"));
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
    
    private String testSaveCleanOrder() throws Exception {
    	init();
    	String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", "zyb_ybj"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "2"));
        parameters.add(new BasicNameValuePair("cleanType", "2"));
        parameters.add(new BasicNameValuePair("timeLimit", "2"));
        parameters.add(new BasicNameValuePair("serviceItemValue", "2,3"));
        parameters.add(new BasicNameValuePair("cleanArea", "少于50m²"));
        parameters.add(new BasicNameValuePair("havePet", "0"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-07-18 10:10"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
    }
    
    private String testSaveNannyOrder() throws Exception {
    	init();
    	String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", "zyb_ajz"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "3"));
        parameters.add(new BasicNameValuePair("nannyType", "1"));
        parameters.add(new BasicNameValuePair("houseArea", "110-140m²"));
        parameters.add(new BasicNameValuePair("serviceItem", "1"));
        parameters.add(new BasicNameValuePair("dietPredilection", "2,3"));
//        parameters.add(new BasicNameValuePair("preProductionPeriod", "0"));
        parameters.add(new BasicNameValuePair("serviceFrequency", "1,2,3,4,5,6,7"));
        parameters.add(new BasicNameValuePair("babySex", "1"));
        parameters.add(new BasicNameValuePair("babyAge", "0"));
        parameters.add(new BasicNameValuePair("nurseCondition", "0"));
        parameters.add(new BasicNameValuePair("careType", "0"));
        parameters.add(new BasicNameValuePair("havePet", "1"));
        parameters.add(new BasicNameValuePair("speculativePriceLevel", "20-25元/小时"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-08-13 00:00:00"));
        parameters.add(new BasicNameValuePair("longitude", "117.278457"));
        parameters.add(new BasicNameValuePair("latitude", "31.884938"));
        parameters.add(new BasicNameValuePair("address", "安徽省合肥市庐阳区濉溪路287号"));
        parameters.add(new BasicNameValuePair("resideIn", "0"));
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
    
    private String testSaveRepaireOrder() throws Exception {
    	init();
    	String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", "zyb_smx"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "4"));
        parameters.add(new BasicNameValuePair("repairContent", "2"));
        parameters.add(new BasicNameValuePair("visitService", "1"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-07-18 10:10"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
    }
    
    private String testSaveWashOrder() throws Exception {
    	init();
    	String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", "zyb_kxy"));
        parameters.add(new BasicNameValuePair("userId", "66"));
        parameters.add(new BasicNameValuePair("serviceType", "5"));
        parameters.add(new BasicNameValuePair("washType", "1,2,3,4"));
        parameters.add(new BasicNameValuePair("washCount", "1"));
        parameters.add(new BasicNameValuePair("visitService", "1"));
        parameters.add(new BasicNameValuePair("serviceTime", "2015-09-30 15:20"));
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
    	JSONObject orderInfo = result.getJSONObject("orderInfo");
    	orderId = orderInfo.getString("orderId");
    	resp.close();
    	httpClient.close();
    	return orderId;
    }
   
}
