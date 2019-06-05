package com.shanjin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.AESUtil;
import com.shanjin.service.IMyMerchantService;

/**
 * 字典数据 业务控制器
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */

@Controller
@RequestMapping("/encrypt")
public class EncryptController {
	
	@Resource
    private IMyMerchantService merchantService;
	
	private Logger logger = Logger.getLogger(EncryptController.class);
	

    @Value("#{configProperties['encrypt.host']}")
    private String host;
	
	@RequestMapping("/do")
	@SystemControllerLog(description = "加密")
	public @ResponseBody Object decryptEncrypt(String clientId, String appType, String phone, String encryptedParams, HttpServletRequest request) {
		CloseableHttpResponse resp = null;
		JSONObject jsonObject = null;
		CloseableHttpClient httpClient = null;
		try{
			if(encryptedParams ==null || "".equals(encryptedParams)) {
				throw new Exception("bad request! the encryptedParams can not be null or empty");
			}
			httpClient = HttpClients.createDefault();
			
	        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	        parameters.add(new BasicNameValuePair("clientId", clientId));
	        parameters.add(new BasicNameValuePair("appType", appType));
	        parameters.add(new BasicNameValuePair("phone", phone));
			String employeeKey = merchantService.getEmployeeKey(phone);
			String paramsJson = AESUtil.decrypt(encryptedParams, employeeKey);
			JSONObject params = JSONObject.parseObject(paramsJson);
			Object url = params.get("url");
			if(url == null) {
				throw new Exception("bad request! the url parameter must be cantained in encryptedParams");
			}
			logger.info("============================================================" + paramsJson);
			for(Entry<String, Object> entry : params.entrySet()) {
				parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			}
			
			Enumeration<String> paramNames = request.getParameterNames();
			for(;paramNames.hasMoreElements();){
				String paramName = paramNames.nextElement();
				if(!paramName.equals("clientId") && !paramName.equals("appType") && !paramName.equals("phone") && !paramName.equals("encryptedParams")) {
					parameters.add(new BasicNameValuePair(paramName, request.getParameter(paramName)));
				}
			}



			HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
			url = url.toString().replaceAll("\\\\", "");
			logger.info("============================================================" + url);
			HttpPost httpPost = new HttpPost(host + url);

			//set header with redirect for apisecurity
			Enumeration hnames=request.getHeaderNames();
			for (Enumeration e = hnames ; e.hasMoreElements() ;) {
				String thisName=e.nextElement().toString();
				String thisValue=request.getHeader(thisName);
				if(!"Content-Length".toLowerCase().equals(thisName.toLowerCase())){
					httpPost.setHeader(thisName,thisValue);
				}
			}

			httpPost.setEntity(entity);
			resp = httpClient.execute(httpPost);
			StatusLine status = resp.getStatusLine();
			if(status.getStatusCode() == 200) {
				String resPlainText = EntityUtils.toString(resp.getEntity(), "UTF8");
				String encrypteText = AESUtil.parseByte2HexStr(AESUtil.encrypt(resPlainText, employeeKey));
				jsonObject = new ResultJSONObject("000", "请求成功");
				jsonObject.put("encryptedResp", encrypteText);
			} else {
				jsonObject = new ResultJSONObject(status.getStatusCode()+"", "请求失败");
			}
		}catch(Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"do");
			
			throw new RuntimeException(e);
		} finally {
	        try {
	        	if(resp != null) {
	        		resp.close();
	        	}
	        	if(httpClient != null) {
	        		httpClient.close();
	        	}
			} catch (IOException e) {
				MsgTools.sendMsgOrIgnore(e,"do");
				logger.error(e.getMessage(), e);
			}
		}
		return jsonObject;
	}

}
