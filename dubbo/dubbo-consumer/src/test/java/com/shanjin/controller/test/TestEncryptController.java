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
import com.shanjin.common.util.AESUtil;
import com.shanjin.common.util.MD5Util;

public class TestEncryptController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private String host = Constant.HOST;
    private String appType = Constant.APP_TYPE;
    
	@Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
	
	@Test
	public void testEncryptDecrypt() throws Exception {
		httpPost = new HttpPost(host+"/encrypt/do");
		String paramsPlainText = "{\"merchantId\":\"143866002612164585\", \"pageNo\":0, \"url\":\"/myIncome/myIncomeShow\"}";
		String encryptedParams = AESUtil.parseByte2HexStr(AESUtil.encrypt(paramsPlainText, Constant.EMPLOYEE_KEY));
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("clientId", Constant.CLIENT_ID));
        parameters.add(new BasicNameValuePair("phone", Constant.PHONE));
        parameters.add(new BasicNameValuePair("encryptedParams", encryptedParams));
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
        
        
        resp.close();
        httpClient.close();
	}
	
}
