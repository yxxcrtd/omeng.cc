package com.shanjin.bank.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.bank.controller.BankController;
import com.shanjin.bank.util.AESUtil;
import com.shanjin.bank.util.ClientLogUtil;
import com.shanjin.bank.util.PropUtil;

public class OpayService {
	private final Logger log = Logger.getLogger(OpayService.class);
	private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    static Properties properties = PropUtil.getPropUtil("ipport.properties");
    private static String host = "";
    String time;
    
    static {
    	if("true".equals(properties.getProperty("opay.server.online.inuse"))){
    		host = "http://"+properties.getProperty("opay.server.online.ip")+":"+properties.getProperty("opay.server.online.port");
    	}
    	else if("true".equals(properties.getProperty("opay.server.test.inuse"))){
    		host = "http://"+properties.getProperty("opay.server.test.ip")+":"+properties.getProperty("opay.server.test.port");
    	}
    	else{
    		host = "http://"+properties.getProperty("opay.server.dev.ip")+":"+properties.getProperty("opay.server.dev.port");
    	}
    }
    public void init() {
        httpClient = HttpClients.createDefault();
        time = System.currentTimeMillis() + "";
    }
    
    public void setHead(List<NameValuePair> parameters) throws Exception{
        String clientType = "1";
        String version = "1.0";
        String apiVersion = "1.0";
        httpPost.setHeader("APIVERSION","1.0");
    }
    
    public String sendRequestOpay(String seviceName, String paramsPlainText){
    	String rsp="";
    	try{
    		init();
        	httpPost = new HttpPost(host+seviceName);

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            //请求头
          	setHead(parameters);
            

            //业务请求参数
            String encryptedParams = AESUtil.parseByte2HexStr(AESUtil.encrypt(paramsPlainText, "100FF0D086A0A9AF44376C14624616CC"));
            parameters.add(new BasicNameValuePair("encryptedParams", encryptedParams));


            HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
            httpPost.setEntity(entity);

            CloseableHttpResponse resp = httpClient.execute(httpPost);
            

            String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
            JSONObject result = (JSONObject) JSONObject.parse(responseJson);
            String encryptedResp = result.getString("encryptedResp");
            
            //将钱包返回的结果进行解密
            rsp = AESUtil.decrypt(encryptedResp, "100FF0D086A0A9AF44376C14624616CC");
            resp.close();
            httpClient.close();
            
    	}
    	catch(Exception e){
    		log.error(e.getMessage(),e);
    	}
    	finally{
    		ClientLogUtil.insertLog("前置机","opay",httpPost.getURI().toString(),paramsPlainText,rsp);
    	}
    	return rsp;
    	
    }

}
