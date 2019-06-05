package com.shanjin.web.proxy.unionpay;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.chinaums.xm.macUtil.Base64;
import com.chinaums.xm.macUtil.DESPlus;
import com.chinaums.xm.macUtil.MACGenerator;
import com.chinaums.xm.macUtil.Translation;
import com.shanjin.common.util.StringUtil;


/**
 * 银联Http接口工具
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>E-mail:337661917@qq.com</p>
 * @author xmSheng
 * @date  2016年9月6日
 *
 */
public class UnionpayHttp {
	
    public static final int HTTP_RESPONSE_SUCCESS_CODE = 200;
    
    
    public static final String CHAESET="UTF8";

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(UnionpayHttp.class);
	
	public static String httpsClientPost(String url,Map<String,String> params){
		    HttpPost httpPost = new HttpPost(url);
		    UnionpayHttp.setHttpTimeOut(httpPost);
	/*	    CloseableHttpClient httpClient = HttpClients.createDefault();*/
		    CloseableHttpClient httpClient = null;
		    CloseableHttpResponse response = null;
		    String httpResponse = null;
		    logger.info("发送银联接口，请求参数-->"+JSONObject.toJSONString(params));
		try {
				httpClient = new SSLClient();
			 	List<NameValuePair> parametersList = new ArrayList<NameValuePair>(); 
			 	Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
			 	while(iterator.hasNext()){
			 		Map.Entry<String, String> paramEntry = iterator.next();
			 		parametersList.add(new BasicNameValuePair(paramEntry.getKey(),paramEntry.getValue()));
			 	}
		        HttpEntity entity = new UrlEncodedFormEntity(parametersList, UnionpayHttp.CHAESET);
		        httpPost.setEntity(entity); 
		        response = (CloseableHttpResponse) httpClient.execute(httpPost);
		        StatusLine status = response.getStatusLine();
		        logger.info("银联接口调用网络相应状态码--->"+status.getStatusCode());
		        if(UnionpayHttp.HTTP_RESPONSE_SUCCESS_CODE == status.getStatusCode()){
		        	httpResponse = EntityUtils.toString(response.getEntity(),  UnionpayHttp.CHAESET);
		        	logger.info("调用银联接口返回信息-->"+httpResponse);
		        }else{
		        	logger.info("调用银联接口异常,接口返回状态码-->"+status.getStatusCode());
		        }	      
		} catch (Exception e) {
			logger.error("调用银联接口异常-->{}",e);
		}finally{
			 try {
				 if(null != response) {
						response.close();
				 }
			} catch (IOException e) {
				e.printStackTrace();
			}
		     try {
		    	  if(null != httpClient){
		  			httpClient.close();
		    	  }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return httpResponse;
	}
	
	/**
	 * 得到当前域名及端口及上下文
	 * 
	 * @param request
	 *            如果为空则读取配制文件httpstr属性
	 * @return
	 */
	public static String getDomain(HttpServletRequest request) {
		String httpstr = "";
		if (request != null) {
			httpstr = "http://" + request.getServerName();
			int port = request.getServerPort();
			if (port != 0 && port != 80)
				httpstr += ":" + port;
			httpstr += request.getContextPath();
		}
		return httpstr;
	}
	
	
	public static  HttpPost setHttpTimeOut(HttpPost httpPost){
		if(null == httpPost){
			return null;
		}
		RequestConfig requestConfig = RequestConfig.custom()
				    .setSocketTimeout(2000)
				 	.setConnectTimeout(2000)
				 	.build();		//设置请求和传输超时时间
		httpPost.setConfig(requestConfig);
		return httpPost;
	}
	
	/**
	 * 银联实名认证验签，解析参数
	 * @param pub_key
	 * @return
	 */
	public static Map<String,String> parseUnionpayVerifyBgResponse(String pubKey,String response){
		if(StringUtil.isEmpty(pubKey)){
			logger.info("参数[pubKey]验证签名公钥不能为空");
			return null;
		}
		if(StringUtil.isEmpty(response)){
			logger.info("参数[response]银联返回报文不能为空");
			return null;
		}
		Map<String,String> resultMap = StringUtil.parseUrlStr2Map(response);
		StringBuffer sBuf = new StringBuffer();
		sBuf.append(resultMap.get("transNo")).append(resultMap.get("merId"));
		sBuf.append(resultMap.get("private1")).append(resultMap.get("respCode"));
		sBuf.append(resultMap.get("respMsg"));
		String reqMac = MACGenerator.getANSIX99MAC(sBuf.toString(), pubKey);
		String chkValue = new String(Base64.encode(reqMac.getBytes()));
		if(StringUtil.isEmpty(chkValue) || !chkValue.equals(resultMap.get("chkValue"))){
			logger.info("银联返回数据签名验证不合法");
			return null;
		}
		//转换respMsg编码
		resultMap.put("respMsg", Translation.tozhCN(resultMap.get("respMsg")));
		return resultMap;
	}
	
	
	public static void main(String args[]){
/*		String url="http://218.5.69.213:6080/mer/cvMer/verifyBg.sp?comVerifyBg";
		String transNo="00"+String.valueOf(System.currentTimeMillis()); //系统交易流水号
		transNo="338444393413953";
		String merId="898350200000003"; //银联提供给平台的商户id		
		String mer_key="5FA57CD3130A2567";
		String pub_key="13a81A21E607cB69";
		
		SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df2 = new SimpleDateFormat("hhmmss");
		String inputDate=df1.format(new Date()); //日期
		String inputTime=df2.format(new Date()); //时间
		
		String realName=Translation.toUnicode("张三"); //真实姓名
		DESPlus des = new DESPlus(mer_key);
		String certNo = des.encrypt("342423199012023767");//身份证号码
		String cardNo = des.encrypt("6256415258664585681000");//卡号
		
		String private1=""; //银联提供给平台的私密域
		String reqStr = transNo + merId + inputDate + inputTime + realName+ certNo + cardNo + private1;
		String reqMac = MACGenerator.getANSIX99MAC(reqStr, mer_key);
		String chkValue = new String(Base64.encode(reqMac.getBytes()));
		Map<String,String> parmsMap = new HashMap<String,String>();
		parmsMap.put("transNo", transNo);
		parmsMap.put("merId", merId);
		parmsMap.put("inputDate", inputDate);
		parmsMap.put("inputTime", inputTime);
		parmsMap.put("realName", realName);
		parmsMap.put("certNo", certNo);
		parmsMap.put("cardNo", cardNo);
		parmsMap.put("private1", private1);
		parmsMap.put("chkValue", chkValue);
		String res = UnionpayHttp.httpsClientPost(url, parmsMap);
		Map<String,String> result = UnionpayHttp.parseUnionpayVerifyBgResponse(pub_key, res);
		System.out.println(JSONObject.toJSONString(result));*/
		
	}
	
}
