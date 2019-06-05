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

public class TestDictController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    
    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
    
    @Test
	public void testGetCitys() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/dictInfo/getCitys");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
        parameters.add(new BasicNameValuePair("clientId", Constant.CLIENT_ID));
        parameters.add(new BasicNameValuePair("phone", Constant.PHONE));
        
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
	public void testGetCitysWithParentId() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/dictInfo/getCitys");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
        parameters.add(new BasicNameValuePair("clientId", Constant.CLIENT_ID));
        parameters.add(new BasicNameValuePair("phone", Constant.PHONE));
        parameters.add(new BasicNameValuePair("parentId", "75"));
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
	public void testGetAreaList() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/dictInfo/getAreaList");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
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
	public void testGetDicts() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/dictInfo/getDicts");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", Constant.APP_TYPE));
        parameters.add(new BasicNameValuePair("clientId", Constant.CLIENT_ID));
        parameters.add(new BasicNameValuePair("phone", Constant.PHONE));
        parameters.add(new BasicNameValuePair("dictType", "userOrderStatus"));
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
	public void testGetUserCarBrandModel() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/dictInfo/getUserCarBrandModel");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
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
	public void testGetDataVersions() throws Exception {
		httpPost = new HttpPost(Constant.HOST + "/dictInfo/getDataVersions");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
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
