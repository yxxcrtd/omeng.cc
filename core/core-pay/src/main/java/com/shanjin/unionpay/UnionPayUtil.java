package com.shanjin.unionpay;

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


public class UnionPayUtil {
	

	
	
	/**
	 * 获取getTn
	 * 
	 * @return
	 */
	public static Map<String,Object> getTn(String outTradeNo,String aoutTradeNo,String totalFee,String notifyUrl){
		Map<String,Object> map=new HashMap<>();
		try {
			String url = Constant.WEB_PROXY_URL + "/unionPayAppConsume";
			CloseableHttpClient hc = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);

			ArrayList nvps = new ArrayList();
			nvps.add(new BasicNameValuePair("outTradeNo", outTradeNo));
			nvps.add(new BasicNameValuePair("aoutTradeNo", aoutTradeNo));
			nvps.add(new BasicNameValuePair("totalFee", totalFee));
			nvps.add(new BasicNameValuePair("notifyUrl", notifyUrl));
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			CloseableHttpResponse resp = hc.execute(post);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = resp.getEntity();
				String response = EntityUtils.toString(entity, "utf8");
				EntityUtils.consume(entity);
				JSONObject resObject = JSON.parseObject(response);
				// 验证通过
				String tn=resObject.get("tn").toString();
				String respCode=resObject.get("respCode").toString();
				map.put("tn", tn);
				map.put("respCode", respCode);

			} 
		} catch (Exception e) {
			e.printStackTrace();
			return map;
		}
		return map;
	}
}
