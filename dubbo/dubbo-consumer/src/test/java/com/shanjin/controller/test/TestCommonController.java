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

public class TestCommonController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    
    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
    
//    @Test
	public void testIosPushTest() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/common/iosPushTest");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
        
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
	public void testGetServiceTypeName() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/common/serviceTypeName");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
        
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
	public void testCheckUpdate() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/common/checkUpdate");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("packageName", "com.shanjin.o2o.omeng"));
        parameters.add(new BasicNameValuePair("version", "110"));
        
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
    public void testGetAppList() throws Exception {
    	httpPost = new HttpPost(Constant.HOST + "/common/getAllApps");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("packageName", "com.shanjin.o2o.omeng"));
    	parameters.add(new BasicNameValuePair("version", "110"));
    	
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
    public void testGetDynamicKey() throws Exception {
    	httpPost = new HttpPost(Constant.HOST + "/common/getDynamicKey");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("clientId", Constant.CLIENT_ID));
    	
    	HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
    	httpPost.setEntity(entity);
    	
    	
    	CloseableHttpResponse resp = httpClient.execute(httpPost);
    	StatusLine status = resp.getStatusLine();
    	Assert.assertEquals(200, status.getStatusCode());
    	String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
    	JSONObject result = (JSONObject) JSONObject.parse(responseJson);
    	String resultCode = result.getString("resultCode");
//    	Assert.assertEquals("000", resultCode);
    	
    	System.out.println(responseJson);
    	resp.close();
    	httpClient.close();
    }
    
    @Test
    public void testGetSliderPics() throws Exception {
    	httpPost = new HttpPost(Constant.HOST + "/common/getSliderPics");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
    	
    	HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
    	httpPost.setEntity(entity);
    	
    	
    	CloseableHttpResponse resp = httpClient.execute(httpPost);
    	StatusLine status = resp.getStatusLine();
    	Assert.assertEquals(200, status.getStatusCode());
    	String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
    	JSONObject result = (JSONObject) JSONObject.parse(responseJson);
    	String resultCode = result.getString("resultCode");
//    	Assert.assertEquals("000", resultCode);
    	
    	System.out.println(responseJson);
    	resp.close();
    	httpClient.close();
    }
    
    @Test
    public void testCreateVerificationCode() throws Exception {
    	httpPost = new HttpPost(Constant.HOST + "/common/createVerificationCode");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("merchantId", "143927818060136526"));
    	parameters.add(new BasicNameValuePair("phone", "15821865990"));
    	
    	HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
    	httpPost.setEntity(entity);
    	
    	
    	CloseableHttpResponse resp = httpClient.execute(httpPost);
    	StatusLine status = resp.getStatusLine();
    	Assert.assertEquals(200, status.getStatusCode());
    	String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
    	JSONObject result = (JSONObject) JSONObject.parse(responseJson);
    	String resultCode = result.getString("resultCode");
//    	Assert.assertEquals("000", resultCode);
    	
    	System.out.println(responseJson);
    	resp.close();
    	httpClient.close();
    }

    @Test
	public void testGetYxtServiceTypeName() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/common/yxtServiceTypeName");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
        
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
