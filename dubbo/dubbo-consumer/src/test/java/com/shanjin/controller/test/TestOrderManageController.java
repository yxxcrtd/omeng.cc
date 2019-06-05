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

import com.shanjin.common.util.MD5Util;
public class TestOrderManageController {
	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private String host = Constant.HOST;
    private String appType = Constant.APP_TYPE;
    private String serviceType = "1";
    private String phone = "15821865990";
    private String merchantId = "143927818060136526";
    
    
 
    private String clientId = "00DC29EA10FAD898F93015507D80BD1A";
    private String EMPLOYEE_KEY = "6B5C33655F317BE8D1E2C0E4C98F0F53";
    
    private String getToken(long time){
    	String validToken = MD5Util.MD5_32(time+ ""+ clientId + "" + phone + "" +EMPLOYEE_KEY);
    	return  validToken;
    }

	@Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
	
	@Test
	public void basicOrderList() throws Exception {
		// 参数： appType,  merchantId, serviceType, pageNo
		httpPost = new HttpPost(host+"/merchantOrderManage/basicOrderList");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        Long time = System.currentTimeMillis();
        parameters.add(new BasicNameValuePair("time", time+""));
        parameters.add(new BasicNameValuePair("clientId", clientId));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("token", getToken(time)));
        
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("serviceType", serviceType));
        parameters.add(new BasicNameValuePair("pageNo", "0"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void detailOrderInfo() throws Exception {
		// 参数：  appType,  merchantId,  orderId,  serviceType
		httpPost = new HttpPost(host+"/merchantOrderManage/detailOrderInfo");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        Long time = System.currentTimeMillis();
        parameters.add(new BasicNameValuePair("time", time+""));
        parameters.add(new BasicNameValuePair("clientId", clientId));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("token", getToken(time)));
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("serviceType", serviceType));
        parameters.add(new BasicNameValuePair("orderId", "1351"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void immediately() throws Exception {
		// 参数：  appType,  phone,  merchantId,  orderId,  planPrice,  planType,  detail,  request
		httpPost = new HttpPost(host+"/merchantOrderManage/immediately");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        Long time = System.currentTimeMillis();
        parameters.add(new BasicNameValuePair("time", time+""));
        parameters.add(new BasicNameValuePair("clientId", clientId));
        parameters.add(new BasicNameValuePair("token", getToken(time)));
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("planPrice", "123.2"));
        parameters.add(new BasicNameValuePair("planType", "1"));
        parameters.add(new BasicNameValuePair("orderId", "1351"));
        parameters.add(new BasicNameValuePair("detail", "hahahhah"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void shieldOrder() throws Exception {
		// 参数： appType,  merchantId,  orderId
		httpPost = new HttpPost(host+"/merchantOrderManage/shieldOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        Long time = System.currentTimeMillis();
        parameters.add(new BasicNameValuePair("time", time+""));
        parameters.add(new BasicNameValuePair("clientId", clientId));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("token", getToken(time)));
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("orderId", "1351"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void pushBasicOrder() throws Exception {
		// 参数： appType,  orderId
		httpPost = new HttpPost(host+"/merchantOrderManage/pushBasicOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        Long time = System.currentTimeMillis();
        parameters.add(new BasicNameValuePair("time", time+""));
        parameters.add(new BasicNameValuePair("clientId", clientId));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("token", getToken(time)));
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("orderId", "1351"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void pushDetailOrder() throws Exception {
		// 参数：appType, merchantId, orderId, serviceType
		httpPost = new HttpPost(host+"/merchantOrderManage/pushDetailOrder");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        Long time = System.currentTimeMillis();
        parameters.add(new BasicNameValuePair("time", time+""));
        parameters.add(new BasicNameValuePair("clientId", clientId));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("token", getToken(time)));
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("serviceType", serviceType));
        parameters.add(new BasicNameValuePair("orderId", "1351"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}

	@Test
	public void detailOrderPlanInfo() throws Exception {
		// 参数：appType, merchantId, orderId
		httpPost = new HttpPost(host+"/merchantOrderManage/detailOrderPlanInfo");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        Long time = System.currentTimeMillis();
        parameters.add(new BasicNameValuePair("time", time+""));
        parameters.add(new BasicNameValuePair("clientId", clientId));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("token", getToken(time)));
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", "143987012357935178"));
        parameters.add(new BasicNameValuePair("orderId", "1948"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
}
