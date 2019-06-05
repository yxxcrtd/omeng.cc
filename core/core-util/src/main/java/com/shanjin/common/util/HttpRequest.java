package com.shanjin.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


public class HttpRequest {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(HttpRequest.class);

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			if(logger.isDebugEnabled()){
				for (String key : map.keySet()) {
					logger.debug(key + "--->" + map.get(key));
				}
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("发送GET请求出现异常！", e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				logger.error("input关闭异常", e);
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("发送 POST 请求出现异常！", e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("input关闭异常！", ex);
			}
		}
		return result;
	}
	
	/**
	 * 使用post方式调用
	 * @param url 调用的URL
	 * @param values 传递的参数值List
	 * @return 调用得到的字符串
	 */
//	public static String httpClientPost(String url,List<NameValuePair[]> values){
//		StringBuilder sb =new StringBuilder();
//		HttpClient httpClient = new HttpClient();
//		PostMethod postMethod = new PostMethod(url);
//		//将表单的值放入postMethod中
//		for (NameValuePair[] value : values) {
//			postMethod.addParameters(value);
//		}
//		
//		try {
//			httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
//			
//			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
//			httpClient.getHttpConnectionManager().getParams().setSoTimeout(6000);
//
//			int statusCode = httpClient.executeMethod(postMethod);
//			
//			if(statusCode != 200){
//				return "";
//			}
//			//以流的行式读入，避免中文乱码
//			InputStream is = postMethod.getResponseBodyAsStream();
//			BufferedReader dis=new BufferedReader(new InputStreamReader(is,"utf-8"));   
//			 String str ="";                           
//			 while((str =dis.readLine())!=null){
//				 sb.append(str);
//				 //sb.append("\r\n"); // 默认这里没有换行，而是让所有的字符出现在一行里面。如需要换行，请去掉前面的注释
//			 }
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			System.out.println("HttpClientUtil.httpClientPost():httpClient调用远程出错;发生网络异常;url:" + url);
//		}finally{
//			postMethod.releaseConnection();
//		}
//		return sb.toString();
//	}
	
	public static String httpClientPost(String url,List<NameValuePair> parameters){
		    HttpPost httpPost = new HttpPost(url);
		    CloseableHttpClient httpClient = HttpClients.createDefault();
		    CloseableHttpResponse resp = null;
		    String res = "";
		try {

		        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
		        httpPost.setEntity(entity);        
		        resp = httpClient.execute(httpPost);
		        StatusLine status = resp.getStatusLine();
		        res = EntityUtils.toString(resp.getEntity(), "UTF8");
		        resp.close();
		        httpClient.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("HttpClientUtil.httpClientPost():httpClient调用远程出错;发生网络异常;url:" + url);
		}
		return res;
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
	
}
