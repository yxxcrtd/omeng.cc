package open.wechat.util;

import open.wechat.constant.Constant;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import open.wechat.util.HttpClientConnectionManager;
/**
 * 微信分享接口工具类
 * @author Huang yulai
 *
 */
public class ShareUtil {

	public static DefaultHttpClient httpclient;
	private static Logger log=Logger.getLogger(ShareUtil.class.getName());
	static {
		httpclient = new DefaultHttpClient();
		httpclient = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient); // 接受任何证书的浏览器客户端
	}
	 /**
     * 获取接口访问凭证
     * 
     * @param appid 凭证
     * @param appsecret 密钥
     * @return
     */
	public static String getAccessToken(String appid, String secret) throws Exception {
		// 凭证获取(GET)
		String requestUrl = Constant.token_url.replace("APPID", appid).replace("APPSECRET", secret);
		// 发起GET请求获取凭证
		HttpGet get = HttpClientConnectionManager.getGetMethod(requestUrl);
		DefaultHttpClient httpclient1= new DefaultHttpClient();
		httpclient1 = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient1); // 接受任何证书的浏览器客户端
		
		HttpResponse response = httpclient1.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		
		//JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		String access_token = null;
		if (null != jsonObject){
			try {
				access_token = jsonObject.getString("access_token");
			}catch (JSONException e) {
				// 获取token失败
				log.error("获取token失败 errcode:{} errmsg:{}",e);
				return access_token;
			}
		}
		httpclient1.close();
		return access_token;
	}

	/**
     * 调用微信JS接口的临时票据
     * 
     * @param access_token 接口访问凭证
     * @return
     */
	public static String getJsApiTicket(String access_token) throws Exception {
	        String requestUrl = Constant.ticket_url.replace("ACCESS_TOKEN", access_token);
	        // 发起GET请求获取凭证
	        HttpGet get = HttpClientConnectionManager.getGetMethod(requestUrl);
	        DefaultHttpClient httpclient1= new DefaultHttpClient();
			httpclient1 = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient1); // 接受任何证书的浏览器客户端
			
	        HttpResponse response = httpclient1.execute(get);
			String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
			JSONObject jsonObject = JSON.parseObject(jsonStr);
	        
	      //  JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
	        String ticket = null;
	        if (null != jsonObject) {
	            try {
	                ticket = jsonObject.getString("ticket");
	            } catch (JSONException e) {
	                // 获取token失败
	                log.error("获取token失败 errcode:{} errmsg:{}", e);
	    	        return ticket;
	            }
	        }
	        httpclient1.close();
	        return ticket;
	}
	
	

}
