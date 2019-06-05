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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.MD5Util;

public class TestMyIncomeController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private long time=0;
    private String token="";
    private String appType = "cbt_cbt";
    private String phone="14700000000";
    private String host = Constant.HOST;
    private String clientId="00DC29EA10FAD123898F93015507D80BD1BB";
    private String employee_key="18BE5812BEDD1F75ACB064A81165AD0F";
    private String merchantId="144005018208285506";


    
	@Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }
	
	/**
	 * 我的收入
	 * @throws Exception
	 */
	@Test
	public void myIncomeShow() throws Exception {
		httpPost = new HttpPost(host+"/myIncome/myIncomeShow");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID        

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
	 * 确认绑定银行卡
	 * @throws Exception
	 */
	@Test
	public void bankCardHandler() throws Exception {
		
		String cardNo="777777777777777777";
		
		//step 1:银行卡初始化
		bindingBank();
		
		//step 2:绑定银行卡
		String cardId=bindedBankCard(cardNo);
		
		//step 3:得到已绑定银行卡列表
		getBindedBankCards();
		
		//step 4:申请提现初始化
		applyWithdrawInit();
		
		//step 5:提现
		applyWithdrawSave(cardNo);
		
		//step 6:修改密码
		updPayPassword();
		
		//step 7:找回支付密码
		findPayPassWord(cardNo);
		
		//step 8:验证支付密码
		verificationPayPassword();
		
		//step 9:解绑银行卡
		unbindedBankCard(cardId);      
	}
	/**
	 * 绑定银行卡 初始化
	 * @throws Exception
	 */
	
	public void bindingBank() throws Exception {
		init();
		httpPost = new HttpPost(host+"/myIncome/bindingBank");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID 

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
	 * 绑定银行卡
	 * @return
	 * @throws Exception
	 */
	public String bindedBankCard(String cardNo)throws Exception{
		init();
		httpPost = new HttpPost(host+"/myIncome/bankCardSave");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID
        parameters.add(new BasicNameValuePair("realName", "张三")); 					//真实姓名
        parameters.add(new BasicNameValuePair("moneyIdNo", "342601198702082174")); 	//身份证号
        parameters.add(new BasicNameValuePair("payPassword", "123456")); 			//支付密码
        parameters.add(new BasicNameValuePair("cardType", "1")); 					//银行卡种类
        parameters.add(new BasicNameValuePair("cardNo", cardNo)); 	//银行卡号 

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
        
       JSONArray ja=result.getJSONArray("merchantWithdrawList");
       String temp="";
       for(Object map:ja){
    	   String withdrawId=((JSONObject)map).get("withdrawId").toString();
    	   String withdrawNo=((JSONObject)map).get("withdrawNo").toString();
    	   if(withdrawNo.equals(cardNo)){
    		   temp=withdrawId;
    	   }
    	   
       }
       return temp;
	}
	/**
	 * 解绑银行卡
	 */
	public void unbindedBankCard(String cardId)throws Exception{
		//解绑银行卡
        init();
        httpPost = new HttpPost(host+"/myIncome/unbindBankCard");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair>  parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID
        parameters.add(new BasicNameValuePair("withdrawId", cardId)); 				//绑定的银行卡ID
        parameters.add(new BasicNameValuePair("payPassword", "123456")); 			//支付密码 

        parameters.add(new BasicNameValuePair("token", token)); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 
        parameters.add(new BasicNameValuePair("phone", phone)); 
        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
        
        HttpEntity  entity = new UrlEncodedFormEntity(parameters, "UTF8");
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
	 * 申请提现初始化
	 * @throws Exception
	 */
	public void applyWithdrawInit() throws Exception {
		init();
		httpPost = new HttpPost(host+"/myIncome/applyWithdrawInit");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID 

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
	 * 确认申请提现
	 * @throws Exception
	 */
	public void applyWithdrawSave(String cardNo) throws Exception {
		init();
		httpPost = new HttpPost(host+"/myIncome/applyWithdrawSave");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID
        parameters.add(new BasicNameValuePair("withdraw", "1")); 					//银行卡种类
        parameters.add(new BasicNameValuePair("withdrawNo", cardNo)); //银行卡号
        parameters.add(new BasicNameValuePair("withdrawPrice", "0.01")); 			//提现金额
        parameters.add(new BasicNameValuePair("payPassword", "123456")); 			//支付密码 

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
	 * 收支明细
	 * @throws Exception
	 */
	@Test
	public void paymentDetails() throws Exception {
		httpPost = new HttpPost(host+"/myIncome/paymentDetails");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID
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
        
        resp.close();
        httpClient.close();
	}
	/**
	 * 获取已绑定银行卡
	 * @throws Exception
	 */
	public void getBindedBankCards() throws Exception {
		init();
		httpPost = new HttpPost(host+"/myIncome/getBindedBankCards");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID 

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
	 * 找回支付密码
	 * @throws Exception
	 */
	public void findPayPassWord(String cardNo) throws Exception {
		init();
		httpPost = new HttpPost(host+"/myIncome/findPayPassWord");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID
        parameters.add(new BasicNameValuePair("realName", "张三")); 					//真实姓名
        parameters.add(new BasicNameValuePair("IDNo", "342601198702082174")); 		//身份证号
        parameters.add(new BasicNameValuePair("cardNo", cardNo)); 	//银行卡号 

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
	 * 验证支付密码
	 * @throws Exception
	 */
	public void verificationPayPassword() throws Exception {
		init();
		httpPost = new HttpPost(host+"/myIncome/verificationPayPassword");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID
        parameters.add(new BasicNameValuePair("payPassword", "123456")); 			//支付密码 

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
	 * 修改支付密码
	 * @throws Exception
	 */
	public void updPayPassword() throws Exception {
		init();
		httpPost = new HttpPost(host+"/myIncome/updPayPassword");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); //商户ID
        parameters.add(new BasicNameValuePair("newPayPassword", "123456")); 		//新的支付密码 
        
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
	 * 订单余额充值申请
	 * @throws Exception
	 */
	@Test
	public void topupApply() throws Exception {
		httpPost = new HttpPost(host+"/myIncome/topupApply");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("money", "1000")); 		
        
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
	 * 订单余额收支明细
	 * @throws Exception
	 */
	@Test
	public void orderPaymentDetails() throws Exception {
		httpPost = new HttpPost(host+"/myIncome/orderPaymentDetails");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
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
        
        resp.close();
        httpClient.close();
	}
}
