package com.shanjin.controller.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.IdGenerator;
import com.shanjin.common.util.MD5Util;
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

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("deprecation")
public class TestMyMerchantController {

	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private long time=0;
    private File file=null;
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
	public void checkFile(){
		String path=Class.class.getClass().getResource("/").getPath();
		path=path.substring(1,path.length());
		path=path+"test.png";
		file=new File(path);
		if(!file.exists()){
			System.err.println("图片不存在");
		}
	}
	/**
	 * 获取商户验证码
	 * @throws Exception
	 */
	@Test
	public void getVerificationCode() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/getVerificationCode");
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType",appType));
        parameters.add(new BasicNameValuePair("phone", "14700000000")); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 
        parameters.add(new BasicNameValuePair("handsetMakers","1")); 
        parameters.add(new BasicNameValuePair("mobileVersion", "1"));  
        parameters.add(new BasicNameValuePair("mobileNumber", "1")); 
        
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
        
        init();
		httpPost = new HttpPost(host+"/myMerchant/validateVerificationCode");
		
        parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType",appType));
        parameters.add(new BasicNameValuePair("phone", "13800000000")); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 	
        parameters.add(new BasicNameValuePair("verificationCode","123456")); //验证码
        parameters.add(new BasicNameValuePair("clientType", "1")); 			//手机系统类型
        
        entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);        
        
        resp = httpClient.execute(httpPost);
        
        status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        result = (JSONObject) JSONObject.parse(responseJson);
        resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();
	}
	
	/**
	 * 我的店铺 
	 * @throws Exception
	 */
	@Test
	public void myMerchantShow() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/myMerchantShow");
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("phone", phone)); 
        parameters.add(new BasicNameValuePair("merchantId", merchantId));	
        
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
	 * 店铺信息 保存
	 * @throws Exception
	 */
	@Test
	public void merchantNameSave() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/merchantNameSave");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("name", "我家店铺"+time)); 
        parameters.add(new BasicNameValuePair("detail", "哈哈哈哈"));     

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
	 * 店铺图标 保存
	 * @throws Exception
	 */
	@Test
	public void merchantIconSave() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/merchantIconSave");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		checkFile();
        			
        MultipartEntity entity = new MultipartEntity();
        entity.addPart("appType", new StringBody(appType, Charset.forName("UTF-8")));
        entity.addPart("merchantId", new StringBody(merchantId, Charset.forName("UTF-8")));
        
        entity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
        entity.addPart("clientId", new StringBody(clientId, Charset.forName("UTF-8")));
        entity.addPart("phone", new StringBody(phone, Charset.forName("UTF-8")));
        entity.addPart("time", new StringBody(String.valueOf(time), Charset.forName("UTF-8")));        
        entity.addPart("iconFile", new FileBody(file));           
        
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
	 * 商户服务项目
	 * @throws Exception
	 */
	@Test
	public void serviceItem() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/serviceItem");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));  

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
	 * 商户服务项目保存
	 * @throws Exception
	 */
	@Test
	public void serviceItemSave() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/serviceItemSave");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("serviceTypes", "1,2,3,4")); 
        parameters.add(new BasicNameValuePair("phone", phone));  

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
	 * 地理位置保存
	 * @throws Exception
	 */
	@Test
	public void locationSave() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/locationSave");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("latitude", "12.123456")); 
        parameters.add(new BasicNameValuePair("longitude", "12.123456"));  
        parameters.add(new BasicNameValuePair("location", "安徽合肥瑶海区"));  

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
	 * 联系方式保存
	 * @throws Exception
	 */
	@Test
	public void contactSave() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/contactSave");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("telephone", phone)); 

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
	 * 当前代金券
	 * @throws Exception
	 */
	@Test
	public void currentVouchersInfo() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/currentVouchersInfo");
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
	
	/**
	 * 历史代金券
	 * @throws Exception
	 */
	@Test
	public void historyVouchersInfo() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/historyVouchersInfo");
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
	/**
	 * 添加代金券
	 * @throws Exception
	 */
	@Test
	public void vouchersPermissionsSave() throws Exception {
		
		httpPost = new HttpPost(host+"/myMerchant/vouchersPermissionsSave");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String cutOffTime=s.format(new Date());
		cutOffTime=s.format(DateUtil.getNextDay(s.parse(cutOffTime), -1));//过期时间
		
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("vouchersId", "9")); 
        parameters.add(new BasicNameValuePair("count", "100")); 
        parameters.add(new BasicNameValuePair("cutoffTime",cutOffTime)); 
        
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
	 * 删除历史代金券
	 * @throws Exception
	 */
	@Test
	public void deleteHistoryVouchers () throws Exception{
		

        //得到历史代金券
        httpPost = new HttpPost(host+"/myMerchant/historyVouchersInfo");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair>  parameters = new ArrayList<NameValuePair>(); 	
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
        
        
        //从历史代金券中筛选出，添加的代金券，并去得ID
        JSONArray ja=result.getJSONArray("historyVouchersInfoList");
        for(Object map:ja){
     	   String id=((JSONObject)map).get("id").toString();     	   
       
			//删除过期代金券
	        init();
	        httpPost = new HttpPost(host+"/myMerchant/deleteHistoryVouchers");
			time=new Date().getTime();
			token=MD5Util.MD5_32(time+clientId+phone+employee_key);
					
			parameters = new ArrayList<NameValuePair>(); 	
	        parameters.add(new BasicNameValuePair("appType", appType));
	        parameters.add(new BasicNameValuePair("merchantId", merchantId));
	        parameters.add(new BasicNameValuePair("userPhone", "")); 
	        parameters.add(new BasicNameValuePair("id", id)); 
	        
	        parameters.add(new BasicNameValuePair("token", token)); 
	        parameters.add(new BasicNameValuePair("clientId", clientId)); 
	        parameters.add(new BasicNameValuePair("phone", phone)); 
	        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
	        
	        entity = new UrlEncodedFormEntity(parameters, "UTF8");
	        httpPost.setEntity(entity);        
	        
	        resp = httpClient.execute(httpPost);
	        
	        status = resp.getStatusLine();
	        Assert.assertEquals(200, status.getStatusCode());
	        responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
	        result = (JSONObject) JSONObject.parse(responseJson);
	        resultCode = result.getString("resultCode");
	        System.out.println(responseJson);
	        Assert.assertEquals("000", resultCode);
	
	        resp.close();
	        httpClient.close();
        }
	}
	
	/**
	 * 顾客评价
	 * @throws Exception
	 */
	@Test
	public void userEvaluation() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/userEvaluation");
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
	
	/**
	 * 申请认证
	 * @throws Exception
	 */
	@Test
	public void applyAuth() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/applyAuth");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

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
	 * 提交认证申请
	 * @throws Exception
	 */
	@Test
	public void submitApplyAuth() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/submitApplyAuth");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		checkFile();
		
		MultipartEntity entity = new MultipartEntity();
        entity.addPart("appType", new StringBody(appType, Charset.forName("UTF-8")));
        entity.addPart("merchantId", new StringBody(merchantId, Charset.forName("UTF-8")));
        entity.addPart("authType", new StringBody("1", Charset.forName("UTF-8")));
        entity.addPart("authPicFile", new FileBody(file));    
        
        entity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
        entity.addPart("clientId", new StringBody(clientId, Charset.forName("UTF-8")));
        entity.addPart("phone", new StringBody(phone, Charset.forName("UTF-8")));
        entity.addPart("time", new StringBody(String.valueOf(time), Charset.forName("UTF-8")));       
        
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
	 * 查询相册
	 * @throws Exception
	 */
	@Test
	public void selectAlbum() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/selectAlbum");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

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
	 * 新建相册-重命名相册-删除相册
	 * @throws Exception
	 */
	@Test
	public void insertToRenameToDeltetAlbum() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/insertAlbum");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		long times=new Date().getTime();
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("albumName", "我的相册-"+times)); 

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
        
        //得到新建相册的id
        String albumId=((JSONObject)result.get("albumInfo")).get("albumId").toString();
        
        //重命名相册
        init();
       
        httpPost = new HttpPost(host+"/myMerchant/updateAlbum");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("albumId", albumId)); 
        parameters.add(new BasicNameValuePair("albumName", "重命名相册-"+times)); 

        parameters.add(new BasicNameValuePair("token", token)); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 
        parameters.add(new BasicNameValuePair("phone", phone)); 
        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
        
        entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);      
	        
        resp = httpClient.execute(httpPost);
        
        status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        result = (JSONObject) JSONObject.parse(responseJson);
        resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();
        
        
        //删除相册
        init();
       
        httpPost = new HttpPost(host+"/myMerchant/deleteAlbum");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("albumId", albumId)); 

        parameters.add(new BasicNameValuePair("token", token)); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 
        parameters.add(new BasicNameValuePair("phone", phone)); 
        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
        
        entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);      
	        
        resp = httpClient.execute(httpPost);
        
        status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        result = (JSONObject) JSONObject.parse(responseJson);
        resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();
	}
	
	/**
	 * 新建相片-删除相片
	 * @throws Exception
	 */
	@Test 
	public void insertToDeletePhoto() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/insertPhoto");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		checkFile();
		
		String albumId="166";
		MultipartEntity entity = new MultipartEntity();
        entity.addPart("appType", new StringBody(appType, Charset.forName("UTF-8")));
        entity.addPart("merchantId", new StringBody(merchantId, Charset.forName("UTF-8")));
        entity.addPart("albumId", new StringBody(albumId, Charset.forName("UTF-8")));
        entity.addPart("iconFile", new FileBody(file));    
        
        entity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
        entity.addPart("clientId", new StringBody(clientId, Charset.forName("UTF-8")));
        entity.addPart("phone", new StringBody(phone, Charset.forName("UTF-8")));
        entity.addPart("time", new StringBody(String.valueOf(time), Charset.forName("UTF-8")));       
        
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
        
        //获得图片ID
        String photoId=((JSONObject)result.get("photoInfo")).get("photoId").toString();
        
        //删除相片
        init();
        httpPost = new HttpPost(host+"/myMerchant/deletePhoto");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("photoIds", photoId)); 

        parameters.add(new BasicNameValuePair("token", token)); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 
        parameters.add(new BasicNameValuePair("phone", phone)); 
        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
        
        HttpEntity entity_ = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity_);      
	        
        resp = httpClient.execute(httpPost);
        
        status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        result = (JSONObject) JSONObject.parse(responseJson);
        resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();
	}
	
	
	/**
	 * 获取验证码-新增员工-查询员工-删除员工
	 * @throws Exception
	 */
	@Test
	public void employeeConfirm() throws Exception {
		
		 //查询员工
		httpPost = new HttpPost(host+"/myMerchant/employeesInfo");
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
        
        JSONArray ja=(JSONArray)result.get("employeesInfo");

        Thread.sleep(1000);
        
        for(Object o:ja){
        	
        	JSONObject j=(JSONObject)o;
        	String employeePhone=j.get("phone").toString();
        	
	        //删除员工
	        init();
	        httpPost = new HttpPost(host+"/myMerchant/deleteEmployee");
			time=new Date().getTime();
			token=MD5Util.MD5_32(time+clientId+phone+employee_key);
			
			parameters = new ArrayList<NameValuePair>(); 	
	        parameters.add(new BasicNameValuePair("appType", appType));
	        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
	        parameters.add(new BasicNameValuePair("employeePhone",employeePhone )); 
	
	        parameters.add(new BasicNameValuePair("token", token)); 
	        parameters.add(new BasicNameValuePair("clientId", clientId)); 
	        parameters.add(new BasicNameValuePair("phone", phone)); 
	        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
	        
	        entity = new UrlEncodedFormEntity(parameters, "UTF8");
	        httpPost.setEntity(entity);      
		        
	        resp = httpClient.execute(httpPost);
	        
	        status = resp.getStatusLine();
	        Assert.assertEquals(200, status.getStatusCode());
	        responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
	        result = (JSONObject) JSONObject.parse(responseJson);
	        resultCode = result.getString("resultCode");
	        System.out.println(responseJson);
	        Assert.assertEquals("000", resultCode);
	
	        resp.close();
	        httpClient.close();
        }

        Thread.sleep(1000);
		//获取
        init();
		httpPost = new HttpPost(host+"/myMerchant/getVerificationCodeForAddEmployee");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("name", "赵三"));  
        parameters.add(new BasicNameValuePair("phone", "14700000002")); 
        
        entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);      
	        
        resp = httpClient.execute(httpPost);
        
        status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        result = (JSONObject) JSONObject.parse(responseJson);
        resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();

        Thread.sleep(1000);
        
        //添加员工
        init();
        httpPost = new HttpPost(host+"/myMerchant/addEmployeeConfirm");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("verificationCode", "123456")); 
        parameters.add(new BasicNameValuePair("employeePhone", "14700000002")); 

        parameters.add(new BasicNameValuePair("token", token)); 
        parameters.add(new BasicNameValuePair("clientId", clientId)); 
        parameters.add(new BasicNameValuePair("phone", phone)); 
        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
        
        entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);      
	        
        resp = httpClient.execute(httpPost);
        
        status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        result = (JSONObject) JSONObject.parse(responseJson);
        resultCode = result.getString("resultCode");
        System.out.println(responseJson);
        Assert.assertEquals("000", resultCode);

        resp.close();
        httpClient.close();    
        
        Thread.sleep(1000);
        
       
	}
	/**
	 * 剩余可添加员工（顾问号）数
	 * @throws Exception
	 */
	@Test
	public void surplusEmployeesNum() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/surplusEmployeesNum");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

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
	 * 增加员工数申请 
	 * @throws Exception
	 */
	@Test
	public void increaseEmployeeNumApply() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/increaseEmployeeNumApply");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("increaseEmployeeNum", "10")); 

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
	 * VIP申请
	 * @throws Exception
	 */
	@Test
	public void vipApply() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/vipApply");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

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
	 * 获取商户在当前应用程序中所能提供的服务项目(订单页面标题显示用)
	 * @throws Exception
	 */
	@Test
	public void getMerchantServiceItemName() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/merchantServiceItemName");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

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
	 * 根据经纬度计算用户与商户之间的距离
	 * @throws Exception
	 */
	@Test
	public void calcDistance() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/calcDistance");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("longitude", "117.279187")); 
        parameters.add(new BasicNameValuePair("latitude", "31.884875")); 
        parameters.add(new BasicNameValuePair("range", "1000")); 

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
	 * 商户端退出应用
	 * @throws Exception
	 */
	@Test
	public void merchantExit() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/merchantExit");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));

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
	 * 验证商户是否被退出
	 * @throws Exception
	 */
	@Test
	public void checkClient() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/checkClient");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

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
        if(resultCode.equals("exit")){
            Assert.assertEquals("exit", resultCode);
        }else{
        	Assert.assertEquals("000", resultCode);
        }

        resp.close();
        httpClient.close();
	}
	/**
	 * 更新微官网URL
	 * @throws Exception
	 */
	@Test
	public void updateMicroWebsiteUrl() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/updateMicroWebsiteUrl");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("microWebsiteUrl", "www.baidu.com")); 

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
	 * 增值服务
	 * @throws Exception
	 */
	@Test
	public void addedServices() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/addedServices");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

//        parameters.add(new BasicNameValuePair("token", token)); 
//        parameters.add(new BasicNameValuePair("clientId", clientId)); 
//        parameters.add(new BasicNameValuePair("phone", phone)); 
//        parameters.add(new BasicNameValuePair("time", String.valueOf(time)));
        
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
	 * 会员申请状态
	 * @throws Exception
	 */
	@Test
	public void vipApplyStatus() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/vipApplyStatus");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

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

	@Test
	public void testGoodsClassification() throws Exception {
		// Step1: 新建（商品）分类
		this.addGoodsClassification();
		// Step2: 查询（商品）分类信息
		String classificationId = this.selectGoodsClassificationInfo();
		// Step3: 重命名（商品）分类
		this.renameGoodsClassification(classificationId);
		// Step4: 新建商品
		this.addGoods(classificationId);
		// Step5: 查询商品信息
		String goodsId = this.selectGoodsInfo(classificationId);
		// Step6: 更新商品信息
		this.updateGoodsInfo(goodsId);
		// Step7: 删除商品
		this.deleteGoods(classificationId, goodsId);
		// Step8: 删除（商品）分类
		this.deleteGoodsClassification(classificationId);
	}

	/**
	 * 查询（商品）分类信息
	 * @throws Exception
	 */
	private String selectGoodsClassificationInfo() throws Exception {
		init();
		String classificationId = "";
		httpPost = new HttpPost(host+"/myMerchant/selectGoodsClassificationInfo");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 

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
        List<Map<String, Object>> goodsClassificatioInfoList = (List<Map<String, Object>>) result.get("goodsClassificatioInfoList");
        classificationId = String.valueOf(goodsClassificatioInfoList.get(0).get("classificationId"));
        resp.close();
        httpClient.close();
        
        return classificationId;
	}

	/**
	 * 新建（商品）分类
	 * @throws Exception
	 */
	private void addGoodsClassification() throws Exception {
		httpPost = new HttpPost(host+"/myMerchant/addGoodsClassification");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("classificationName", "商品分类测试0001"));

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
	 * 重命名（商品）分类
	 * @throws Exception
	 */
	private void renameGoodsClassification(String classificationId) throws Exception {
		init();
		httpPost = new HttpPost(host+"/myMerchant/renameGoodsClassification");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("classificationId", classificationId));
        parameters.add(new BasicNameValuePair("classificationName", "商品分类测试1001"));

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
	 * 删除（商品）分类
	 * @throws Exception
	 */
	private void deleteGoodsClassification(String classificationId) throws Exception {
		init();
		httpPost = new HttpPost(host+"/myMerchant/deleteGoodsClassification");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("classificationId", classificationId));

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
	 * 查询商品信息
	 * @throws Exception
	 */
	private String selectGoodsInfo(String classificationId) throws Exception {
		String goodsId = "";
		init();
		httpPost = new HttpPost(host+"/myMerchant/selectGoodsInfo");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("classificationId", classificationId));
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
        List<Map<String, Object>> goodsInfoList = (List<Map<String, Object>>) result.get("goodsInfoList");
        goodsId = String.valueOf(goodsInfoList.get(0).get("goodsId"));
        resp.close();
        httpClient.close();
        
        return goodsId;
	}

	/**
	 * 新建商品
	 * @throws Exception
	 */
	private void addGoods(String classificationIds) throws Exception {
		init();
		httpPost = new HttpPost(host+"/myMerchant/addGoods");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		checkFile();
		
		MultipartEntity entity = new MultipartEntity();
        entity.addPart("appType", new StringBody(appType, Charset.forName("UTF-8")));
        entity.addPart("merchantId", new StringBody(merchantId, Charset.forName("UTF-8")));
		entity.addPart("classificationIds", new StringBody(classificationIds, Charset.forName("UTF-8")));
        entity.addPart("goodsName", new StringBody("商品测试01", Charset.forName("UTF-8")));
        entity.addPart("goodsPrice", new StringBody("2", Charset.forName("UTF-8")));
        entity.addPart("goodsDescribe", new StringBody("好商品", Charset.forName("UTF-8")));
        entity.addPart("goodsPicture", new FileBody(file));    
        
        entity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
        entity.addPart("clientId", new StringBody(clientId, Charset.forName("UTF-8")));
        entity.addPart("phone", new StringBody(phone, Charset.forName("UTF-8")));
        entity.addPart("time", new StringBody(String.valueOf(time), Charset.forName("UTF-8"))); 

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
	 * 更新商品信息
	 * @throws Exception
	 */
	private void updateGoodsInfo(String goodsId) throws Exception {
		init();
		httpPost = new HttpPost(host+"/myMerchant/updateGoodsInfo");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId));
        parameters.add(new BasicNameValuePair("goodsId", goodsId));
        parameters.add(new BasicNameValuePair("classificationIds", "1,2"));
        parameters.add(new BasicNameValuePair("goodsName", "商品测试02"));
        parameters.add(new BasicNameValuePair("goodsPrice", "2"));
        parameters.add(new BasicNameValuePair("goodsDescribe", "好商品"));

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
	 * 删除商品
	 * @throws Exception
	 */
	private void deleteGoods(String classificationId, String goodsId) throws Exception {
		init();
		httpPost = new HttpPost(host+"/myMerchant/deleteGoods");
		time=new Date().getTime();
		token=MD5Util.MD5_32(time+clientId+phone+employee_key);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 	
        parameters.add(new BasicNameValuePair("appType", appType));
        parameters.add(new BasicNameValuePair("merchantId", merchantId)); 
        parameters.add(new BasicNameValuePair("classificationId", classificationId));
        parameters.add(new BasicNameValuePair("goodsId", goodsId));

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
     * 开通增值服务
     */
    @Test
    public void openIncreaseService() throws Exception {
        init();
        httpPost = new HttpPost(host + "/myMerchant/openIncreaseService");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("pkgId", "6"));
        parameters.add(new BasicNameValuePair("userId", "146719401961241611"));
        parameters.add(new BasicNameValuePair("merchantId", "146658327874276158"));
        parameters.add(new BasicNameValuePair("payType", "1")); // 1-支付宝支付 2-微信支付 3-现金支付
        parameters.add(new BasicNameValuePair("tradeAmount", "5800"));
        parameters.add(new BasicNameValuePair("orderNo", IdGenerator.getOrderNo(20)));
        parameters.add(new BasicNameValuePair("tradeNo", UUID.randomUUID().toString().replace("-", ""))); // 第三方支付号
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        System.out.println(result);
        resp.close();
        httpClient.close();
    }

    @Test
    public void openIncreaseEmployeeNumApply() throws Exception {
        init();
        httpPost = new HttpPost(host + "/myMerchant/increaseEmployeeNumApply");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("pkgId", "4"));
        parameters.add(new BasicNameValuePair("appType", "lxz"));
        parameters.add(new BasicNameValuePair("merchantId", "147219751824625346"));
        parameters.add(new BasicNameValuePair("increaseEmployeeNum", "5"));
        parameters.add(new BasicNameValuePair("money", "10000"));
        parameters.add(new BasicNameValuePair("applyStatus", "2"));
        parameters.add(new BasicNameValuePair("payNo", "weChatiii1472197518246253467561"));
        parameters.add(new BasicNameValuePair("payType", "2"));
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        System.out.println(result);
        resp.close();
        httpClient.close();
    }

}
