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

public class TestUserMstOrderController {

	private CloseableHttpClient httpClient;
	private HttpPost httpPost;
	private String appType = "mst";

	@Before
	public void init() {
		httpClient = HttpClients.createDefault();
	}

	@Test
	public void testGetBasicOrderList() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getBasicOrderList");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("userId", "1"));
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
		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("orderId", "241"));

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
		Thread.sleep(1000);
		testGetDetialOrderInfo("1102223", "2");
		Thread.sleep(1000);
		testGetDetialOrderInfo("1102228", "3");
		Thread.sleep(1000);
		testGetDetialOrderInfo("1102653", "4");
	}

	public void testGetDetialOrderInfo(String oerderId, String serviceType) throws Exception {
		init();
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getDetialOrderInfo");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("orderId", oerderId));
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
		resp.close();
		httpClient.close();
	}

	@Test
	public void testSaveOrder() throws Exception {
		String orderId = "";
		String merchantId = "5";

		String planId = "";
		{
			String serviceType = "2";
			// Step1: 保存订单
			orderId = this.saveMassageOrder(serviceType);
			// Step1.5: 商户抢单
			this.immediately(merchantId, orderId, serviceType);
			// Step2: 获取订单供应商列表
			Thread.sleep(1000); // 由于使用读写分离，需要等待1秒钟，待数据同步到读库进行下一步操作
			planId = this.getOrderMerchantPlan(orderId);
			// Step3: 用户选择订单的供应商
			this.chooseMerchantPlan(orderId, merchantId, planId);
			// Step4: 获取用户在这个订单中可用使用的代金券
			this.getUserAvailablePayVouchersInfo(serviceType, merchantId);
			// Step5: 确认订单的金额信息
			this.confirmOrderPrice(orderId, merchantId);
			// Step6: 完成现金订单
			this.finishCashOrder(orderId, merchantId);
			// Step7: 取消订单
			this.cancelOrder(orderId);
			// Step8: 删除订单
			this.deleteOrder(orderId);
		}

		{
			String serviceType = "3";
			// Step1: 保存订单
			orderId = this.savePhysioOrder(serviceType);
			// Step1.5: 商户抢单
			this.immediately(merchantId, orderId, serviceType);
			// Step2: 获取订单供应商列表
			Thread.sleep(1000); // 由于使用读写分离，需要等待1秒钟，待数据同步到读库进行下一步操作
			planId = this.getOrderMerchantPlan(orderId);
			// Step3: 用户选择订单的供应商
			this.chooseMerchantPlan(orderId, merchantId, planId);
			// Step4: 获取用户在这个订单中可用使用的代金券
			this.getUserAvailablePayVouchersInfo(serviceType, merchantId);
			// Step5: 确认订单的金额信息
			this.confirmOrderPrice(orderId, merchantId);
			// Step6: 完成现金订单
			this.finishCashOrder(orderId, merchantId);
			// Step7: 取消订单
			this.cancelOrder(orderId);
			// Step8: 删除订单
			this.deleteOrder(orderId);
		}

		{
			String serviceType = "4";
			// Step1: 保存订单
			orderId = this.saveMedicatedOrder(serviceType);
			// Step1.5: 商户抢单
			this.immediately(merchantId, orderId, serviceType);
			// Step2: 获取订单供应商列表
			Thread.sleep(1000); // 由于使用读写分离，需要等待1秒钟，待数据同步到读库进行下一步操作
			planId = this.getOrderMerchantPlan(orderId);
			// Step3: 用户选择订单的供应商
			this.chooseMerchantPlan(orderId, merchantId, planId);
			// Step4: 获取用户在这个订单中可用使用的代金券
			this.getUserAvailablePayVouchersInfo(serviceType, merchantId);
			// Step5: 确认订单的金额信息
			this.confirmOrderPrice(orderId, merchantId);
			// Step6: 完成现金订单
			this.finishCashOrder(orderId, merchantId);
			// Step7: 取消订单
			this.cancelOrder(orderId);
			// Step8: 删除订单
			this.deleteOrder(orderId);
		}

	}

	@Test
	public void testUserEvaluation() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/userOrder/userEvaluation");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", appType));
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
		parameters.add(new BasicNameValuePair("appType", appType));
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
		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("orderId", "1343"));

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

	// 保存推拿订单
	private String saveMassageOrder(String serviceType) throws Exception {
		init();
		String orderId = "";
		// Step1: 保存订单
		httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("userId", "1"));
		parameters.add(new BasicNameValuePair("serviceType", serviceType));
		parameters.add(new BasicNameValuePair("serviceSite", "1"));
		parameters.add(new BasicNameValuePair("manNum", "1"));
		parameters.add(new BasicNameValuePair("womanNum", "2"));
		parameters.add(new BasicNameValuePair("serviceItem", "1,2,3"));
		parameters.add(new BasicNameValuePair("hopeServiceTime", "2015-06-12 00:00:00"));
		parameters.add(new BasicNameValuePair("longitude", "117.278626"));
		parameters.add(new BasicNameValuePair("latitude", "31.884916"));
		parameters.add(new BasicNameValuePair("demand", "demand"));

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

	// 保存理疗订单
	private String savePhysioOrder(String serviceType) throws Exception {
		init();
		String orderId = "";
		// Step1: 保存订单
		httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();

		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("userId", "1"));
		parameters.add(new BasicNameValuePair("serviceType", serviceType));
		parameters.add(new BasicNameValuePair("serviceItem", "1,2,3"));
		parameters.add(new BasicNameValuePair("hopeServiceTime", "2015-06-12 00:00:00"));
		parameters.add(new BasicNameValuePair("longitude", "117.278626"));
		parameters.add(new BasicNameValuePair("latitude", "31.884916"));
		parameters.add(new BasicNameValuePair("demand", "demand"));

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

	// 保存药浴订单
	private String saveMedicatedOrder(String serviceType) throws Exception {
		init();
		String orderId = "";
		// Step1: 保存订单
		httpPost = new HttpPost(Constant.HOST + "/userOrder/saveOrder");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();

		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("userId", "1"));
		parameters.add(new BasicNameValuePair("serviceType", serviceType));
		parameters.add(new BasicNameValuePair("manNum", "1"));
		parameters.add(new BasicNameValuePair("womanNum", "2"));
		parameters.add(new BasicNameValuePair("serviceItem", "1,2,3"));
		parameters.add(new BasicNameValuePair("hopeServiceTime", "2015-06-12 00:00:00"));
		parameters.add(new BasicNameValuePair("longitude", "117.278626"));
		parameters.add(new BasicNameValuePair("latitude", "31.884916"));
		parameters.add(new BasicNameValuePair("demand", "demand"));

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
		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("orderId", orderId));
		parameters.add(new BasicNameValuePair("merchantId", merchantId));
		parameters.add(new BasicNameValuePair("planPrice", "15.6"));
		parameters.add(new BasicNameValuePair("planType", serviceType));
		parameters.add(new BasicNameValuePair("detail", "非常感谢能为你服务"));

		parameters.add(new BasicNameValuePair("clientId", Constant.CLIENT_ID));
		parameters.add(new BasicNameValuePair("phone", "15155050000"));
		String time = System.currentTimeMillis() + "";
		String token = MD5Util.MD5_32(time + Constant.CLIENT_ID + Constant.PHONE + Constant.EMPLOYEE_KEY);
		parameters.add(new BasicNameValuePair("time", time));
		parameters.add(new BasicNameValuePair("token", token));

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
		System.out.println(responseJson);
		resp.close();
		httpClient.close();
	}

	private String getOrderMerchantPlan(String orderId) throws Exception {
		init();
		httpPost = new HttpPost(Constant.HOST + "/userOrder/getOrderMerchantPlan");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", appType));
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
		return planId + "";
	}

	private void chooseMerchantPlan(String orderId, String merchantId, String planId) throws Exception {
		init();
		httpPost = new HttpPost(Constant.HOST + "/userOrder/chooseMerchantPlan");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("orderId", orderId));
		parameters.add(new BasicNameValuePair("merchantId", merchantId));
		parameters.add(new BasicNameValuePair("merchantPlanId", planId + ""));

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
		parameters.add(new BasicNameValuePair("appType", appType));
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
		parameters.add(new BasicNameValuePair("appType", appType));
		parameters.add(new BasicNameValuePair("orderId", orderId));
		parameters.add(new BasicNameValuePair("merchantId", merchantId));
		parameters.add(new BasicNameValuePair("price", "15.6"));
		// parameters.add(new BasicNameValuePair("vouchersId", "3"));
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
		parameters.add(new BasicNameValuePair("appType", appType));
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
		parameters.add(new BasicNameValuePair("appType", appType));
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
		parameters.add(new BasicNameValuePair("appType", appType));
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
