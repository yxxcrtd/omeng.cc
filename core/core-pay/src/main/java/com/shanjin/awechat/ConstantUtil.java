package com.shanjin.awechat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.print.attribute.HashAttributeSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.StringUtil;


public class ConstantUtil {
	
	/**
	 * 商家可以考虑读取配置文件(测试)
	 */

	// 初始化
	public static String APP_ID_TEST = "wxc8a1b5d3801572ac";// 微信开发平台应用id
	public static String APP_SECRET_TEST = "";// 应用对应的凭证
	// 应用对应的密钥
	public static String PARTNER_TEST = "1277885101";// 财付通商户号
	public static String PARTNER_KEY_TEST = "937f58d8c60b6a1e2fd9de7a3b9cc3b6";// 商户号对应的密钥
	/**
	 * 商家可以考虑读取配置文件(线上)
	 */

	// 初始化
	public static String APP_ID = "wx08e6e8ad5f349d91";// 微信开发平台应用id
	public static String APP_SECRET = "";// 应用对应的凭证
	// 应用对应的密钥
	public static String PARTNER = "1253386901";// 财付通商户号
	public static String PARTNER_KEY = "562f50d8c68b6a1e27d36e7a3b92c34d";// 商户号对应的密钥
	
	/**
	 * h5微信支付可以考虑读取配置文件(测试)
	 */
	
	// 初始化
		public static String APP_ID_H5_TEST = "wx2170611bbd061e2a";// 微信开发平台应用id
		public static String APP_SECRET_H5_TEST = "";// 应用对应的凭证
		// 应用对应的密钥
		public static String PARTNER_H5_TEST = "1394113502";// 财付通商户号
		public static String PARTNER_KEY_H5_TEST = "gfdhre4353bfluidsfrq2gbfgbfgrewr";// 商户号对应的密钥
		
		/**
		 * h5微信支付可以考虑读取配置文件(生产)
		 */

		// 初始化
		public static String APP_ID_H5 = "wxab9427aa633751e7";// 微信开发平台应用id
		public static String APP_SECRET_H5 = "";// 应用对应的凭证
		// 应用对应的密钥
		public static String PARTNER_H5= "1393664202";// 财付通商户号
		public static String PARTNER_KEY_H5 = "kjhlkjnfg61321d32465bvfsd43gsfdg";// 商户号对应的密钥

	/**
	 * 获取随机字符串
	 * 
	 * @return
	 */
	public static String getNonceStr() {
		// 随机数
		String currTime = TenpayUtil.getCurrTime();
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		return strTime + strRandom;
	}
	
	/**
	 * 获取随机字符串
	 * 
	 * @return
	 */
	public static String getRandom() {
		 SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		    Date date = new Date();
		    String key = format.format(date);

		    Random r = new Random();
		    key = key + r.nextInt(100);
		    key = key.substring(0, 11);
		    return key;
	}
	
	/**
	 * 新获取随机字符串
	 * 
	 * @return
	 */
	public static String getRandomStr() {
		// 随机数
		Long currTime = System.currentTimeMillis();
		String currStr=currTime.toString();
		// 8位日期
		String strTime = currStr.substring(12, currStr.length());
		// 10位序列号,可以自行调整。
		return strTime;
	}
	
	
	/**
	 * 获取prepayId
	 * 
	 * @return
	 */
	public static Map<String,Object> getPrepayId(String createOrderURL,String xmlStr){
		Map<String,Object> map=new HashMap<>();
		try {
			String url = Constant.WEB_PROXY_URL + "/wechatParm";
			CloseableHttpClient hc = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);

			ArrayList nvps = new ArrayList();
			nvps.add(new BasicNameValuePair("createOrderURL", createOrderURL));
			nvps.add(new BasicNameValuePair("xml", xmlStr));
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			CloseableHttpResponse resp = hc.execute(post);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = resp.getEntity();
				String response = EntityUtils.toString(entity, "utf8");
				EntityUtils.consume(entity);
				JSONObject resObject = JSON.parseObject(response);
				// 验证通过
				String returnCode=resObject.get("returnCode").toString();
				String returnData=resObject.get("returnData").toString();
				map.put("returnCode", returnCode);
				map.put("returnData", returnData);

			} 
		} catch (Exception e) {
			e.printStackTrace();
			return map;
		}
		return map;
	}
}
