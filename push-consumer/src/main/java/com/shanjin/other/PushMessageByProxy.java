package com.shanjin.other;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.shanjin.common.util.HttpRequest;

public class PushMessageByProxy {


	private String url;
	List<NameValuePair> parameters = new ArrayList<NameValuePair>();


	public PushMessageByProxy(String url, String param) {
		this.url = url;
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
	
	public void run() {
		HttpRequest.httpClientPost(url, parameters);
	}

}
