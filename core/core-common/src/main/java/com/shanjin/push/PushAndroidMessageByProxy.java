package com.shanjin.push;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.shanjin.common.util.HttpRequest;

public class PushAndroidMessageByProxy implements Runnable {

	static String host = "http://sdk.open.api.igexin.com/apiex.htm";
	private static Logger logger = Logger.getLogger(PushAndroidMessageByProxy.class);

	private String url;
	private String param;
	// private NameValuePair[] parameters = null;
	List<NameValuePair> parameters = new ArrayList<NameValuePair>();

	// public PushAndroidMessageByProxy(String url, String param) {
	// this.url = url;
	// this.param = param;
	// }

	public PushAndroidMessageByProxy(String url, String param) {
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

	// public PushAndroidMessageByProxy(String url, String param) {
	// this.url = url;
	// this.param = param;
	// if(param!=null&&!"".equals(param)){
	// String[] ps = param.split("&");
	// if(ps!=null&&ps.length>0){
	// int i=0;
	// for(String s : ps){
	// if(s!=null&&!"".equals(s)){
	// String[] pa=s.split("=");
	// if(pa!=null&&pa.length>0){
	// String v="";
	// if(pa.length>1){
	// v=pa[1];
	// }
	// parameters.add(new BasicNameValuePair(pa[0], v));
	// }
	// }
	//
	// }
	// }
	// }
	//
	// // NameValuePair[] nvps = null;
	// // if(param!=null&&!"".equals(param)){
	// // String[] ps = param.split("&");
	// // if(ps!=null&&ps.length>0){
	// // nvps = new NameValuePair[ps.length];
	// // int i=0;
	// // for(String s : ps){
	// // if(s!=null&&!"".equals(s)){
	// // String[] pa=s.split("=");
	// // if(pa!=null&&pa.length>0){
	// // nvps[i] = new NameValuePair();
	// // nvps[i].setName(ps[0]);
	// // String v="";
	// // if(pa.length>1){
	// // v=pa[1];
	// // }
	// // nvps[i].setValue(v);
	// // i++;
	// // }
	// // }
	// //
	// // }
	// // }
	// // }
	// // parameters = nvps;
	// // for (NameValuePair nvp : nvps) {
	// // System.out.println(" http param :" + nvp.getName() + " = "+
	// nvp.getValue());
	// // }
	// }

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
		// HttpRequest.sendPost(url, param);
		HttpRequest.httpClientPost(url, parameters);
	}

}
