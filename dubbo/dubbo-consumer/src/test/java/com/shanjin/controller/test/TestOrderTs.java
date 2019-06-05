package com.shanjin.controller.test;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.MD5Util;

@SuppressWarnings("deprecation")
public class TestOrderTs {

    private long time=0;
    private String token="";
    private HttpPost httpPost;
    private File file_pic=null;
    private File file_voice=null;
    private String appType = "ts";
    private String phone="14600000000";
    private String host =Constant.HOST;
	private CloseableHttpClient httpClient;
    private String merchantId="144290661752600235";
    private String clientId="00DC29EA10FAD898F93015507D80BD1BABBADB";
    private String employee_key="C6F00434629F2D02350E861692BBC950";
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");


    
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
		String path2=path+"voice.amr";
		file_voice=new File(path2);
		if(!file_voice.exists()){
			System.err.println("声音文件不存在");
		}
	}
	
	/**
	 * 订单业务
	 * @throws Exception
	 */
	@Test
	public void orderBusiness() throws Exception {
		String orderId="";	
		String planId="";
		String userId="1";
		String serviceType="1";
		

		//step 1:保存订单- 广告位租赁
	    orderId=this.saveOrder(userId,"1");
	    Thread.sleep(1000); 
        this.detailOrderInfo(orderId, serviceType);
	    
		//step 1:保存订单- 网络营销
	    orderId=this.saveOrder(userId,"2");
	    Thread.sleep(1000); 
        this.detailOrderInfo(orderId, serviceType);
		
		//step 1:保存订单- 影视广告
	    orderId=this.saveOrder(userId,"3");
	    Thread.sleep(1000); 
        this.detailOrderInfo(orderId, serviceType);
	    
		//step 1:保存订单-线下活动
	    orderId=this.saveOrder(userId,"4");
	    Thread.sleep(1000); 
        this.detailOrderInfo(orderId, serviceType);
        
		//step 1:保存订单-其他
	    orderId=this.saveOrder(userId,"5");
	    Thread.sleep(1000); 
        this.detailOrderInfo(orderId, serviceType);
        
	    //step 2:用户订单详情
        this.detailAppOrderInfo(orderId, serviceType);
	    
        //step 3:商户抢单
	    this.immediately(orderId,serviceType);
        
        //step 4:商户方案列表
        planId=this.getOrderMerchantPlan(orderId);
        Thread.sleep(1000);
        
        //step 5:选择商户方案
        this.chooseMerchantPlan(planId, orderId);        

    	//step 6: 获取用户在这个订单中可用使用的代金券
    	this.getUserAvailablePayVouchersInfo(userId,serviceType, merchantId);
        
        //step 7:确认订单金额
        this.confirmOrderPrice(orderId, serviceType);
        
        //step 7:现金支付
        this.finishCashOrder(orderId, serviceType);
        
        //step 9:取消订单
        this.cancelOrder(orderId);
        
        //step 10:删除订单
        this.deleteOrder(orderId); 
       
	}
	/**
	 * 保存订单
	 * @param userId
	 * @param serviceType
	 * @return
	 * @throws Exception
	 */
	public String  saveOrder(String userId,String serviceType) throws Exception{
    	init();
		httpPost = new HttpPost(host+"/userOrder/saveOrder");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		checkFile();
		MultipartEntity entity = new MultipartEntity();
        entity.addPart("appType", new StringBody(appType, Charset.forName("UTF-8")));
        entity.addPart("userId", new StringBody(userId, Charset.forName("UTF-8")));
        entity.addPart("serviceType", new StringBody(serviceType, Charset.forName("UTF-8")));
        
        if(serviceType.equals("1")){//广告位租赁
            entity.addPart("adPositionType", new StringBody("1", Charset.forName("UTF-8")));
            entity.addPart("extensionTime", new StringBody("8点-11点", Charset.forName("UTF-8")));
        	
        }
        if(serviceType.equals("2")){// 网络营销
        	entity.addPart("netMarketingType", new StringBody("1", Charset.forName("UTF-8")));
            entity.addPart("extensionTime", new StringBody("8点-11点", Charset.forName("UTF-8")));
        }
        if(serviceType.equals("3")){// 影视广告
            entity.addPart("movieAdType", new StringBody("1", Charset.forName("UTF-8")));
            entity.addPart("extensionTime", new StringBody("8点-11点", Charset.forName("UTF-8")));        	
        }
        if(serviceType.equals("4")){//线下活动
            entity.addPart("offlineActivityType", new StringBody("1", Charset.forName("UTF-8")));        	
        }  
        if(serviceType.equals("5")){//其他
            entity.addPart("otherType", new StringBody("1", Charset.forName("UTF-8")));        	
        } 
        
        entity.addPart("extensionBudget", new StringBody("1w-2w", Charset.forName("UTF-8")));   
        entity.addPart("reservationTime", new StringBody(sdf.format(new Date()), Charset.forName("UTF-8")));        
        entity.addPart("address", new StringBody("安徽合肥", Charset.forName("UTF-8")));        
        entity.addPart("demand", new StringBody("距离不要太远", Charset.forName("UTF-8")));
        entity.addPart("longitude", new StringBody("117.278626", Charset.forName("UTF-8")));
        entity.addPart("latitude", new StringBody("117.278626", Charset.forName("UTF-8")));        
//        entity.addPart("picture0", new FileBody(file_pic));    
//        entity.addPart("voice", new FileBody(file_voice));   
        
        entity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
        entity.addPart("clientId", new StringBody(clientId, Charset.forName("UTF-8")));
        entity.addPart("phone", new StringBody(phone, Charset.forName("UTF-8")));
        entity.addPart("time", new StringBody(String.valueOf(time), Charset.forName("UTF-8")));               
        
        httpPost.setEntity(entity);    
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        String resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);
        
        resp.close();
        httpClient.close();
        
        JSONObject userInfo=(JSONObject)result.get("orderInfo");
        String orderId=(String)userInfo.get("orderId");
        return orderId;
	}
	/**
	 * 商户抢单
	 * @param orderId
	 * @param serviceType
	 * @throws Exception
	 */
	public void immediately(String orderId,String serviceType) throws Exception{
    	init();
        httpPost = new HttpPost(host+"/merchantOrderManage/immediately");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		checkFile();
		
		MultipartEntity entity = new MultipartEntity();
        entity.addPart("appType", new StringBody(appType, Charset.forName("UTF-8")));
        entity.addPart("merchantId", new StringBody(merchantId, Charset.forName("UTF-8")));
        entity.addPart("orderId", new StringBody(orderId, Charset.forName("UTF-8")));
        entity.addPart("planPrice", new StringBody("11.11", Charset.forName("UTF-8")));
        entity.addPart("planType", new StringBody(serviceType, Charset.forName("UTF-8")));
        entity.addPart("detail", new StringBody("我的价格 ", Charset.forName("UTF-8")));
        entity.addPart("picture0", new FileBody(file_pic));    
        entity.addPart("voice", new FileBody(file_voice));    
        
        entity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
        entity.addPart("clientId", new StringBody(clientId, Charset.forName("UTF-8")));
        entity.addPart("phone", new StringBody(phone, Charset.forName("UTF-8")));
        entity.addPart("time", new StringBody(String.valueOf(time), Charset.forName("UTF-8")));        
        
        httpPost.setEntity(entity);    
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String  responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        String resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);
        
        resp.close();
        httpClient.close();
        
        Thread.sleep(1000);
        
        
	}
	/**
	 * 订单详情
	 * @param orderId
	 * @param serviceType
	 * @throws Exception
	 */
	public void detailOrderInfo(String orderId,String serviceType) throws Exception{
    	init();
		//订单详情  
        httpPost = new HttpPost(host+"/merchantOrderManage/detailOrderInfo");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("orderId", orderId)); 
        parameters.add(new BasicNameValuePair("serviceType", serviceType)); 

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
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();
	}
	/**
	 * 用户订单详情
	 * @param orderId
	 * @param serviceType
	 * @throws Exception
	 */
	public void detailAppOrderInfo(String orderId,String serviceType) throws Exception{
    	init();
		//订单详情  
        httpPost = new HttpPost(host+"/userOrder/getDetialOrderInfo");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("orderId", orderId)); 
        parameters.add(new BasicNameValuePair("serviceType", serviceType)); 

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
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();
	}
	/**
	 * 商户方案列表
	 * @param orderId
	 * @return
	 */
	public String getOrderMerchantPlan(String orderId) throws Exception{
    	init();
		httpPost = new HttpPost(host+"/userOrder/getOrderMerchantPlan");
	    time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	    
	    parameters.add(new BasicNameValuePair("appType", appType));
	    parameters.add(new BasicNameValuePair("orderId", orderId));
	    parameters.add(new BasicNameValuePair("pageNo", "0"));

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
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);
        
        
        JSONArray pans=(JSONArray)result.get("merchantPlans");
        String planId="";
        for(Object plan:pans){
        	String planId_=((JSONObject)plan).get("planId").toString();
        	String merchantId_=((JSONObject)plan).get("merchantId").toString();
        	String planPrice=((JSONObject)plan).get("price").toString();
        	if(planPrice.equals("11.11") && merchantId_.equals(merchantId)){
        		planId=planId_;
        	}
        }
        
	    resp.close();
	    httpClient.close();
	    return planId;
	}
	/**
	 * 用户选择商家方案
	 * @param planId
	 * @param orderId
	 * @throws Exception
	 */
	public void chooseMerchantPlan(String planId,String orderId) throws Exception{
    	init();
		httpPost = new HttpPost(host+"/userOrder/chooseMerchantPlan");
	    time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	    
	    parameters.add(new BasicNameValuePair("appType", appType));
	    parameters.add(new BasicNameValuePair("merchantId", merchantId));
	    parameters.add(new BasicNameValuePair("merchantPlanId", planId));
	    parameters.add(new BasicNameValuePair("orderId", orderId));

        parameters.add(new BasicNameValuePair("token", token)); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 
        parameters.add(new BasicNameValuePair("phone", phone)); 
        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
	    
        HttpEntity entity  = new UrlEncodedFormEntity(parameters, "UTF8");
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
	/**
	 * 确认订单的金额信息
	 * @param orderId
	 * @param merchantId
	 * @throws Exception
	 */
	private void confirmOrderPrice(String orderId, String merchantId) throws Exception {
    	init();
    	httpPost = new HttpPost(host + "/userOrder/confirmOrderPrice");
	    time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", appType));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	parameters.add(new BasicNameValuePair("merchantId", merchantId));
    	parameters.add(new BasicNameValuePair("price", "15.6"));
    	parameters.add(new BasicNameValuePair("actualPrice", "15.6"));

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
        System.out.println(responseJson);
    	Assert.assertEquals("000", resultCode);
    	resp.close();
    	httpClient.close();
    }
	/**
	 * 现金支付
	 * @param orderId
	 * @param merchantId
	 * @throws Exception
	 */
	 private void finishCashOrder(String orderId, String merchantId) throws Exception {
    	init();
    	httpPost = new HttpPost(host + "/userOrder/finishCashOrder");
	    time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", appType));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	parameters.add(new BasicNameValuePair("merchantId", merchantId));
    	parameters.add(new BasicNameValuePair("price", "11.11"));
    	
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
        System.out.println(responseJson);
    	Assert.assertEquals("000", resultCode);
    	resp.close();
    	httpClient.close();
    }
	 /**
	  * 取消订单
	  * @param orderId
	  * @throws Exception
	  */
	 private void cancelOrder(String orderId) throws Exception {
    	init();
    	httpPost = new HttpPost(host + "/userOrder/cancelOrder");
	    time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", appType));
    	parameters.add(new BasicNameValuePair("orderId", orderId));
    	
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
        System.out.println(responseJson);
    	Assert.assertEquals("000", resultCode);
    	resp.close();
    	httpClient.close();
    }
	 /**
	  * 删除订单
	  * @param orderId
	  * @throws Exception
	  */
	 private void deleteOrder(String orderId) throws Exception {
    	init();
    	httpPost = new HttpPost(host + "/userOrder/deleteOrder");
	    time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", appType));
    	parameters.add(new BasicNameValuePair("orderId", orderId));

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
        System.out.println(responseJson);
    	Assert.assertEquals("000", resultCode);
    	resp.close();
    	httpClient.close();
    }
	 /**
	  * 用户可使用的代金券
	  * @param userId
	  * @param serviceType
	  * @param merchantId
	  * @throws Exception
	  */
	 private void getUserAvailablePayVouchersInfo(String userId,String serviceType, String merchantId) throws Exception {
    	init();
    	httpPost = new HttpPost(host + "/userOrder/getUserAvailablePayVouchersInfo");
    	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    	parameters.add(new BasicNameValuePair("appType", appType));
    	parameters.add(new BasicNameValuePair("serviceType", serviceType));
    	parameters.add(new BasicNameValuePair("userId",userId));
    	parameters.add(new BasicNameValuePair("merchantId", merchantId));
    	parameters.add(new BasicNameValuePair("pageNo", "0"));
    	
    	HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
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
