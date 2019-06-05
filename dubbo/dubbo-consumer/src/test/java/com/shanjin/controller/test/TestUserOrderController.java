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

public class TestUserOrderController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    
    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
    
    @Test
	public void testGetBasicOrderList() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getBasicOrderList");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
        parameters.add(new BasicNameValuePair("userId", "144081802267736806"));
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
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", "89"));
    	
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
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", "89"));
    	parameters.add(new BasicNameValuePair("serviceType", "1"));
    	
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
    	String orderId = "";
    	String serviceType = "1";
    	String merchantId = "144083141670402883";
    	String planId = "";
    	{
    		serviceType = "1";
    		//Step1: 保存订单
	    	orderId = this.saveWashOrder(serviceType);
	    	//Step1.5: 商户抢单
	    	this.immediately(merchantId, orderId, serviceType);
	    	//Step2: 获取订单供应商列表
	    	Thread.sleep(1000); //由于使用读写分离，需要等待1秒钟，待数据同步到读库进行下一步操作
	    	planId = this.getOrderMerchantPlan(orderId);
	    	//Step3: 用户选择订单的供应商
	    	this.chooseMerchantPlan(orderId, merchantId, planId);
	    	//Step4: 获取用户在这个订单中可用使用的代金券
	    	this.getUserAvailablePayVouchersInfo(serviceType, merchantId);
	    	//Step5: 确认订单的金额信息
	    	this.confirmOrderPrice(orderId, merchantId);
	    	//Step6: 完成现金订单
	    	this.finishCashOrder(orderId, merchantId);
	    	//Step7: 取消订单
	    	this.cancelOrder(orderId);
	    	//Step8: 删除订单
	    	this.deleteOrder(orderId);
    	}
    	
    	{
    		serviceType = "2";
    		init();
    		//Step1: 保存订单
	    	orderId = this.saveRepairCarOrder(serviceType);
	    	//Step1.5: 商户抢单
	    	this.immediately(merchantId, orderId, serviceType);
	    	//Step2: 获取订单供应商列表
	    	Thread.sleep(1000); //由于使用读写分离，需要等待1秒钟，待数据同步到读库进行下一步操作
	    	planId = this.getOrderMerchantPlan(orderId);
	    	//Step3: 用户选择订单的供应商
	    	this.chooseMerchantPlan(orderId, merchantId, planId);
	    	//Step4: 获取用户在这个订单中可用使用的代金券
	    	this.getUserAvailablePayVouchersInfo(serviceType, merchantId);
	    	//Step5: 确认订单的金额信息
	    	this.confirmOrderPrice(orderId, merchantId);
	    	//Step6: 完成现金订单
	    	this.finishCashOrder(orderId, merchantId);
	    	//Step7: 取消订单
	    	this.cancelOrder(orderId);
	    	//Step8: 删除订单
	    	this.deleteOrder(orderId);
    	}
    	
    	{
    		serviceType = "3";
    		init();
    		//Step1: 保存订单
	    	orderId = this.saveUpkeepCarOrder(serviceType);
	    	//Step1.5: 商户抢单
	    	this.immediately(merchantId, orderId, serviceType);
	    	//Step2: 获取订单供应商列表
	    	Thread.sleep(1000); //由于使用读写分离，需要等待1秒钟，待数据同步到读库进行下一步操作
	    	planId = this.getOrderMerchantPlan(orderId);
	    	//Step3: 用户选择订单的供应商
	    	this.chooseMerchantPlan(orderId, merchantId, planId);
	    	//Step4: 获取用户在这个订单中可用使用的代金券
	    	this.getUserAvailablePayVouchersInfo(serviceType, merchantId);
	    	//Step5: 确认订单的金额信息
	    	this.confirmOrderPrice(orderId, merchantId);
	    	//Step6: 完成现金订单
	    	this.finishCashOrder(orderId, merchantId);
	    	//Step7: 取消订单
	    	this.cancelOrder(orderId);
	    	//Step8: 删除订单
	    	this.deleteOrder(orderId);
    	}
    	
    	{
    		serviceType = "4";
    		init();
    		//Step1: 保存订单
	    	orderId = this.saveBeautifyCarOrder(serviceType);
	    	//Step1.5: 商户抢单
	    	this.immediately(merchantId, orderId, serviceType);
	    	//Step2: 获取订单供应商列表
	    	Thread.sleep(1000); //由于使用读写分离，需要等待1秒钟，待数据同步到读库进行下一步操作
	    	planId = this.getOrderMerchantPlan(orderId);
	    	//Step3: 用户选择订单的供应商
	    	this.chooseMerchantPlan(orderId, merchantId, planId);
	    	//Step4: 获取用户在这个订单中可用使用的代金券
	    	this.getUserAvailablePayVouchersInfo(serviceType, merchantId);
	    	//Step5: 确认订单的金额信息
	    	this.confirmOrderPrice(orderId, merchantId);
	    	//Step6: 完成现金订单
	    	this.finishCashOrder(orderId, merchantId);
	    	//Step7: 取消订单
	    	this.cancelOrder(orderId);
	    	//Step8: 删除订单
	    	this.deleteOrder(orderId);
    	}
    }
    
    private String saveBeautifyCarOrder(String serviceType) throws Exception {
    	String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("serviceType", serviceType));
    	parameters.add(new BasicNameValuePair("userId", "144081802267736806"));
    	parameters.add(new BasicNameValuePair("latitude", "31.88497"));
    	parameters.add(new BasicNameValuePair("longitude", "117.278724"));
    	parameters.add(new BasicNameValuePair("carParkAddress", "安徽省合肥市庐阳区濉溪路287号-5楼"));
    	parameters.add(new BasicNameValuePair("hopeServiceTime", "2015-09-15 14:00:00"));
    	parameters.add(new BasicNameValuePair("beautifyServiceItems", "1,2"));
    	parameters.add(new BasicNameValuePair("serviceSite", "2"));
    	parameters.add(new BasicNameValuePair("demand", "阿斯顿浪费空间为服务将饿哦发"));
    	
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

	private String saveUpkeepCarOrder(String serviceType) throws Exception {
		String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("serviceType", serviceType));
    	parameters.add(new BasicNameValuePair("userId", "144081802267736806"));
    	parameters.add(new BasicNameValuePair("latitude", "31.88497"));
    	parameters.add(new BasicNameValuePair("longitude", "117.278724"));
    	parameters.add(new BasicNameValuePair("upkeepVehicle", "奥迪A4L"));
    	parameters.add(new BasicNameValuePair("upkeepContent", "2"));
    	parameters.add(new BasicNameValuePair("hopeServiceTime", "2015-09-15 14:00:00"));
    	parameters.add(new BasicNameValuePair("demand", "阿斯顿浪费空间为服务将饿哦发"));
    	
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

	private String saveRepairCarOrder(String serviceType) throws Exception {
		String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("serviceType", serviceType));
    	parameters.add(new BasicNameValuePair("userId", "144081802267736806"));
    	parameters.add(new BasicNameValuePair("latitude", "31.88497"));
    	parameters.add(new BasicNameValuePair("longitude", "117.278724"));
    	parameters.add(new BasicNameValuePair("repairPlace", "1,2,3"));
    	parameters.add(new BasicNameValuePair("hopeServiceTime", "2015-09-15 14:00:00"));
    	parameters.add(new BasicNameValuePair("demand", "阿斯顿浪费空间为服务将饿哦发"));
    	
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

	@Test
    public void testUserEvaluation() throws Exception {
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/userEvaluation");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("merchantId", "144083141670402883"));
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
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("merchantId", "144083141670402883"));
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
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", "89"));
    	
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
    

    private String saveWashOrder(String serviceType) throws Exception{
    	String orderId = "";
    	//Step1: 保存订单
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("serviceType", serviceType));
    	parameters.add(new BasicNameValuePair("userId", "144081802267736806"));
    	parameters.add(new BasicNameValuePair("latitude", "31.88497"));
    	parameters.add(new BasicNameValuePair("longitude", "117.278724"));
    	parameters.add(new BasicNameValuePair("carParkAddress", "安徽省合肥市庐阳区濉溪路287号-5楼"));
    	parameters.add(new BasicNameValuePair("hopeServiceTime", "2015-09-15 14:00:00"));
    	parameters.add(new BasicNameValuePair("serviceSite", "2"));
    	parameters.add(new BasicNameValuePair("insideWash", "1"));
    	parameters.add(new BasicNameValuePair("demand", "阿斯顿浪费空间为服务将饿哦发"));
    	
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
    
    private void immediately(String merchantId, String orderId, String serviceType) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/merchantOrderManage/immediately");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	parameters.add(new BasicNameValuePair("merchantId", merchantId));
    	parameters.add(new BasicNameValuePair("planPrice", "15.6"));
    	parameters.add(new BasicNameValuePair("planType", serviceType));
    	parameters.add(new BasicNameValuePair("detail", "非常感谢能为你服务"));
    	
        parameters.add(new BasicNameValuePair("clientId", Constant.CLIENT_ID));
        parameters.add(new BasicNameValuePair("phone", Constant.PHONE));
        String time = System.currentTimeMillis() + "";
        String token = MD5Util.MD5_32(time +Constant.CLIENT_ID + Constant.PHONE + Constant.EMPLOYEE_KEY);
        parameters.add(new BasicNameValuePair("time", time));
        parameters.add(new BasicNameValuePair("token", token));
    	
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
    
    private String getOrderMerchantPlan(String orderId) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/getOrderMerchantPlan");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	parameters.add(new BasicNameValuePair("pageNo", "0"));
    	
    	HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
    	httpPost.setEntity(entity);
    	
    	
    	CloseableHttpResponse resp = httpClient.execute(httpPost);
    	StatusLine status = resp.getStatusLine();
    	Assert.assertEquals(200, status.getStatusCode());
    	String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
    	System.out.println(responseJson);
    	JSONObject result = (JSONObject) JSONObject.parse(responseJson);
    	String resultCode = result.getString("resultCode");
    	Assert.assertEquals("000", resultCode);
    	JSONArray merchantPlans = result.getJSONArray("merchantPlans");
    	JSONObject merchantPlan = merchantPlans.getJSONObject(0);
    	int planId = merchantPlan.getIntValue("planId");

    	resp.close();
    	httpClient.close();
    	return planId+"";
    }
    
    private void chooseMerchantPlan(String orderId, String merchantId, String planId) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/chooseMerchantPlan");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	parameters.add(new BasicNameValuePair("merchantId", merchantId));
    	parameters.add(new BasicNameValuePair("merchantPlanId", planId+""));
    	
    	HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
    	httpPost.setEntity(entity);
    	
    	
    	CloseableHttpResponse resp = httpClient.execute(httpPost);
    	StatusLine status = resp.getStatusLine();
    	Assert.assertEquals(200, status.getStatusCode());
    	String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
    	JSONObject result = (JSONObject) JSONObject.parse(responseJson);
    	String resultCode = result.getString("resultCode");
    	Assert.assertEquals("000", resultCode);
    	
    	resp.close();
    	httpClient.close();
    }
    
    private void getUserAvailablePayVouchersInfo(String serviceType, String merchantId) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/getUserAvailablePayVouchersInfo");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("pageNo", "0"));
    	parameters.add(new BasicNameValuePair("serviceType", serviceType));
    	parameters.add(new BasicNameValuePair("userId", "143772639883533501"));
    	parameters.add(new BasicNameValuePair("merchantId", merchantId));
    	
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
    
    private void confirmOrderPrice(String orderId, String merchantId) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/confirmOrderPrice");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	parameters.add(new BasicNameValuePair("merchantId", merchantId));
    	parameters.add(new BasicNameValuePair("price", "15.6"));
//    	parameters.add(new BasicNameValuePair("vouchersId", "3"));
    	parameters.add(new BasicNameValuePair("actualPrice", "15.6"));
    	
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
    
    private void finishCashOrder(String orderId, String merchantId) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/finishCashOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	parameters.add(new BasicNameValuePair("merchantId", merchantId));
    	parameters.add(new BasicNameValuePair("price", "15.6"));
    	
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
    
    private void cancelOrder(String orderId) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/cancelOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	
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
    
    private void deleteOrder(String orderId) throws Exception {
    	init();
    	httpPost = new HttpPost(Constant.HOST + "/userOrder/deleteOrder");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	
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
}
