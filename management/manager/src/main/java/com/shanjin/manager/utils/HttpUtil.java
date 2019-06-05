package com.shanjin.manager.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
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

public class HttpUtil {

	public static String SendGET(String url,String param){
		   String result="";//访问返回结果
		   BufferedReader read=null;//读取访问结果
		   try {
		    //创建url
		    URL realurl=new URL(url+"?"+param);
		    //打开连接
		    URLConnection connection=realurl.openConnection();
		     // 设置通用的请求属性
		             connection.setRequestProperty("accept", "*/*");
		             connection.setRequestProperty("connection", "Keep-Alive");
		             connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
		             //建立连接
		             connection.connect();
		          // 获取所有响应头字段
		            // Map<String, List<String>> map = connection.getHeaderFields();
		             // 遍历所有的响应头字段，获取到cookies等
		             //for (String key : map.keySet()) {
		                // System.out.println(key + "--->" + map.get(key));
		            // }
		             // 定义 BufferedReader输入流来读取URL的响应
		             read = new BufferedReader(new InputStreamReader(
		                     connection.getInputStream(),"UTF-8"));
		             String line;//循环读取
		             while ((line = read.readLine()) != null) {
		                 result += line;
		             }
		   } catch (IOException e) {
		        e.printStackTrace(); 
				return result; 
		   }finally{
		    if(read!=null){//关闭流
		     try {
		      read.close();
		     } catch (IOException e) {
		       e.printStackTrace();
			   return result; 
		     }
		    }
		   }
		     
		   return result; 
		 }
	
	
//	public static String httpClientPost(String url,Map<String,Object> paramMap){
//	    HttpPost httpPost = new HttpPost(url);
//	    CloseableHttpClient httpClient = HttpClients.createDefault();
//	    CloseableHttpResponse resp = null;
//	    String res = "";
//	try {
//		    List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 
//		    for (String key : paramMap.keySet()) {
//		    	  parameters.add(new BasicNameValuePair(key, StringUtil.null2Str(paramMap.get(key))));
//		    }
//	        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
//	        httpPost.setEntity(entity);        
//	        resp = httpClient.execute(httpPost);
//	        StatusLine status = resp.getStatusLine();
//	        res = EntityUtils.toString(resp.getEntity(), "UTF8");
//	        resp.close();
//	        httpClient.close();
//	} catch (Exception e) {
//		System.out.println(e.getMessage());
//		System.out.println("HttpClientUtil.httpClientPost():httpClient调用远程出错;发生网络异常;url:" + url);
//	}
//	return res;
//}
	
	/**
	 * 使用post方式调用
	 * @param url 调用的URL
	 * @param values 传递的参数值List
	 * @return 调用得到的字符串
	 */
	public static String httpClientPost(String url,Map<String,Object> paramMap){
		StringBuilder sb =new StringBuilder();
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		//将表单的值放入postMethod中

	    for (String key : paramMap.keySet()) {
	    	postMethod.addParameter(key, StringUtil.null2Str(paramMap.get(key)));
        }
		
		try {
			httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000);

			int statusCode = httpClient.executeMethod(postMethod);
			
			if(statusCode != 200){
				System.out.println("HttpClient Error : statusCode = " + statusCode + " where visit url :" + url);
				return "";
			}
			//以流的行式读入，避免中文乱码
			InputStream is = postMethod.getResponseBodyAsStream();
			BufferedReader dis=new BufferedReader(new InputStreamReader(is,"utf-8"));   
			 String str ="";                           
			 while((str =dis.readLine())!=null){
				 sb.append(str);
				 //sb.append("\r\n"); // 默认这里没有换行，而是让所有的字符出现在一行里面。如需要换行，请去掉前面的注释
			 }
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("HttpClientUtil.httpClientPost():httpClient调用远程出错;发生网络异常;url:" + url);
		}finally{
			postMethod.releaseConnection();
		}
		return sb.toString();
	}
	
}
