package com.shanjin.push.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.shanjin.push.util.HttpRequest;

public class PushMessageByProxy implements Runnable{

	static String host = "http://sdk.open.api.igexin.com/apiex.htm";
	private static Logger logger = Logger.getLogger(PushMessageByProxy.class);

	private String url;
	private String param;
	List<NameValuePair> parameters = new ArrayList<NameValuePair>();


	public PushMessageByProxy(String url, String param) {
		this.url = url;
		this.param = param;
		if (param != null && !"".equals(param)) {
			String[] ps = param.split("&");
			if (ps != null && ps.length > 0) {
				for (String s : ps) {
					if (s != null && !"".equals(s)) {
						String[] pa = s.split("=");
						if (pa != null && pa.length > 0) {
							String v = "";
							if (pa.length > 1) {
								v = pa[1];
							}
							parameters.add(new BasicNameValuePair(pa[0], v));
						}
					}

				}
			}
		}
	}



	public static void main(String[] args) {
		StringBuilder param = new StringBuilder();
		param.append("iosPushInfoList=54fcb19a6a0fc1a3d6bfcdadbaafed958a58bad6426b037418e8ea8c59852987").append("&msg=").append("你有一条新订单").append("&cert=").append("sgdsfds");
		// String param = "iosPushInfoList=" +
		// JSONObject.toJSONString(iosPushInfoList) + "&msg=" +
		// JSONObject.toJSONString(msg) + "&cert=" +
		// JSONObject.toJSONString(cert);

	}

	@Override
	public void run() {
		HttpRequest.httpClientPost(url, parameters);
	}

}
