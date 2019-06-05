package com.shanjin.web.proxy.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@WebServlet(name = "voiceService", urlPatterns = { "/voiceService" })
public class voiceServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String mobile = request.getParameter("mobile");
			String code = request.getParameter("code");
			String key = request.getParameter("key");

			System.out.println("====发送语音验证码 时间 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "====");
			System.out.println("mobile:" + mobile);
			System.out.println("code:" + code);
			System.out.println("key:" + key);

			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("api", "key-" + key));
			WebResource webResource = client.resource("http://voice-api.luosimao.com/v1/verify.json");
			MultivaluedMapImpl formData = new MultivaluedMapImpl();
			formData.add("mobile", mobile);
			formData.add("code", code);
			ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
			String textEntity = clientResponse.getEntity(String.class);
			int status = clientResponse.getStatus();
			PrintWriter pw = response.getWriter();
			pw.print(textEntity);
			pw.flush();
			pw.close();

			/*
			 * String url = "http://voice-api.luosimao.com/v1/verify.json";
			 * String apiKey = "api:key-" + key; CloseableHttpClient hc =
			 * HttpClients.createDefault(); HttpPost post = new HttpPost(url);
			 * PostMethod postMethod = new PostMethod(url);
			 * postMethod.setRequestHeader(headerName, headerValue);
			 * post.addHeader("Authorization", "Basic " + new
			 * Base64().encodeToString(apiKey.getBytes("utf-8")));
			 * 
			 * SimpleDateFormat df = new
			 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 * 
			 * ArrayList nvps = new ArrayList(); nvps.add(new
			 * BasicNameValuePair("mobile", mobile)); nvps.add(new
			 * BasicNameValuePair("code", code)); post.setEntity(new
			 * UrlEncodedFormEntity(nvps, "utf-8")); CloseableHttpResponse resp
			 * = hc.execute(post);
			 * response.setStatus(resp.getStatusLine().getStatusCode());
			 * Header[] headers = resp.getAllHeaders(); if (headers != null &&
			 * headers.length > 0) { for (Header header : headers) {
			 * response.setHeader(headers[1].getName(), headers[1].getValue());
			 * } } String r = EntityUtils.toString(resp.getEntity(), "utf8");
			 * PrintWriter pw = response.getWriter(); pw.print(r); pw.flush();
			 * pw.close();
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
