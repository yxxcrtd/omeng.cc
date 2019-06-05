package com.shanjin.controller.test;

import java.util.ArrayList;
import java.util.Date;
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
import com.shanjin.common.util.MD5Util;

public class TestStatisticsRankingController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private String host = Constant.HOST;
    private String appType = Constant.APP_TYPE;
    private long time=0;
    private String token="";
    private String clientId=Constant.CLIENT_ID;
    private String phone=Constant.PHONE;
    private String employee_key=Constant.EMPLOYEE_KEY;
    
	@Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
	
	/**
	 * 统计排行的信息显示
	 * @throws Exception
	 */
	@Test
	public void statisticsRankingShow() throws Exception {
		httpPost = new HttpPost(host+"/statisticsRanking/statisticsRankingShow");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", "143892618516662667")); //商户ID       

        parameters.add(new BasicNameValuePair("token", token)); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 
        parameters.add(new BasicNameValuePair("phone", phone)); 
        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
        
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        String resultCode = result.getString("resultCode");
        System.out.println("统计排行的信息显示："+responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();
	}
}
