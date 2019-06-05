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

public class TestUserManageController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private String host = Constant.HOST;
    private String appType = Constant.APP_TYPE;
    
	@Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
	
	/**
	 * 商户端-客户管理中无记录
	 * @throws Exception
	 */
	@Test
	public void userShow() throws Exception {
		httpPost = new HttpPost(host+"/userManage/userShow");
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", "143892618516662667")); //商户ID
        parameters.add(new BasicNameValuePair("pageNo", "0"));
        
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        String resultCode = result.getString("resultCode");
        System.out.println("客户管理列表-返回结果："+responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();
	}
	/**
	 * 商户端-客户管理中有记录
	 * @throws Exception
	 */
	@Test
	public void userShow1() throws Exception {
		httpPost = new HttpPost(host+"/userManage/userShow");
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", "143900526695629665")); //商户ID
        parameters.add(new BasicNameValuePair("pageNo", "0"));
        
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        String resultCode = result.getString("resultCode");
        System.out.println("客户管理列表-返回结果："+responseJson);
        Assert.assertEquals("000", resultCode);

        
        resp.close();
        httpClient.close();
	}
}
