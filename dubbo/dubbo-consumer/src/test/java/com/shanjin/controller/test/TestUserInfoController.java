package com.shanjin.controller.test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.StatusLine;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

@SuppressWarnings("deprecation")
public class TestUserInfoController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private String host = "http://192.168.1.15:8080";
    private File file_pic=null;
	@Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
	public void checkFile(){
		String path=Class.class.getClass().getResource("/").getPath();
		path=path.substring(1,path.length());
		String path1=path+"picture.png";
		file_pic=new File(path1);
		if(!file_pic.exists()){
			System.err.println("图片文件不存在");
		}
	}
	@Test
	public void getVerificationCode() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/getVerificationCode");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "cbt"));
        parameters.add(new BasicNameValuePair("phone", "18256933775"));
        parameters.add(new BasicNameValuePair("clientId", "00DC29EA10FAD198F93015507D80BD1A"));    
        parameters.add(new BasicNameValuePair("handsetMakers", ""));
        parameters.add(new BasicNameValuePair("mobileVersion", "66"));
        parameters.add(new BasicNameValuePair("mobileNumber", "0"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);

        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	
	@Test
	public void validateVerificationCode() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/validateVerificationCode");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "cbt"));
        parameters.add(new BasicNameValuePair("phone", "15955010230"));
        parameters.add(new BasicNameValuePair("verificationCode", "123456"));
        
        parameters.add(new BasicNameValuePair("clientType", "1"));
        parameters.add(new BasicNameValuePair("clientId", "0"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void updateUserInfo() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/updateUserInfo");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("userId", "146242885504433178"));
        parameters.add(new BasicNameValuePair("sex", "1"));
      
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);

        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void uploadUserPortrait() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/uploadUserPortrait");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("userId", "143926065231415566"));
        parameters.add(new BasicNameValuePair("phone", "15955010230"));
   
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void getUserAddressInfo() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/getUserAddressInfo");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "cbt"));
        parameters.add(new BasicNameValuePair("userId", "143926065231415566"));
        parameters.add(new BasicNameValuePair("addressType", "0"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void updateUserAddressInfo() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/getVerificationCode");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "cbt"));
        parameters.add(new BasicNameValuePair("userId", "143926065231415566"));
        parameters.add(new BasicNameValuePair("addressType", "0"));
        
        parameters.add(new BasicNameValuePair("latitude", "1"));
        parameters.add(new BasicNameValuePair("longitude", "2"));
        parameters.add(new BasicNameValuePair("addressInfo", "金鼎"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void feedback() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/feedback");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "cbt"));
        parameters.add(new BasicNameValuePair("userId", "143926065231415566"));
        parameters.add(new BasicNameValuePair("content", "很实用"));
     
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void getUserAvailableVouchersInfo() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/getUserAvailableVouchersInfo");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "cbt"));
        parameters.add(new BasicNameValuePair("userId", "143926065231415566"));
        parameters.add(new BasicNameValuePair("pageNo", "0"));
        
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void getUserHistoryVouchersInfo() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/getUserHistoryVouchersInfo");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "cbt"));
        parameters.add(new BasicNameValuePair("userId", "143926065231415566"));
        parameters.add(new BasicNameValuePair("pageNo", "0"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void deleteVouchersInfo() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/deleteVouchersInfo");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("vouchersId", "2"));
        parameters.add(new BasicNameValuePair("userId", "2"));
       
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void getUserInfo() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/getUserInfo");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("phone", "15955010230")); 
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        
        
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	
	@Test
	public void userExit() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/userExit");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("clientId", "cbt"));
       
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	@Test
	public void checkClient() throws Exception {
		httpPost = new HttpPost(host+"/userInfo/checkClient");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", "cbt"));
        parameters.add(new BasicNameValuePair("clientId", "0"));
        parameters.add(new BasicNameValuePair("userId", "143926065231415566"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);

        CloseableHttpResponse resp = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(resp.getEntity(), "UTF8"));
        resp.close();
        httpClient.close();
	}
	@Test
	public void feedbackTest() throws Exception {
		httpPost = new HttpPost(host+"/common/feedback");
		checkFile();
		MultipartEntity entity = new MultipartEntity();
        entity.addPart("appType", new StringBody("omeng", Charset.forName("UTF-8")));
        entity.addPart("customerType", new StringBody("1", Charset.forName("UTF-8")));
        entity.addPart("customerId", new StringBody("144013972128576014", Charset.forName("UTF-8")));        
        entity.addPart("feedbackType", new StringBody("1", Charset.forName("UTF-8")));        
        entity.addPart("content", new StringBody("很好用非常好特别好", Charset.forName("UTF-8")));         
        entity.addPart("picture0", new FileBody(file_pic));       
        entity.addPart("picture1", new FileBody(file_pic));           
        entity.addPart("picture2", new FileBody(file_pic));           
        entity.addPart("picture3", new FileBody(file_pic));           
        entity.addPart("picture4", new FileBody(file_pic));                    
        
        httpPost.setEntity(entity);    
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        String resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);
        
        resp.close();
        httpClient.close();
	}
}
