package com.shanjin.manager.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.UserSession;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.job.OrderRewardJob;
import com.shanjin.manager.utils.MqUtil;
import com.shanjin.manager.utils.StringUtil;


public class WelcomeController extends Controller{
	
	public void index() {
		
		render("index.jsp");
	}
	
//	public void ayou() {
//		//BigDecimal total = Db.queryBigDecimal("/*master*/ SELECT IFNULL(SUM(t.amount),0) AS total FROM activity_order_reward_detail t WHERE t.activity_id=? AND t.merchant_id=? ", activityId,order.getLong("merchant_id").toString());
//		//OrderRewardJob.work();
//		List<Record> list = Db.find("/*master*/ SELECT * FROM push_merchant_order t WHERE t.merchant_id=146312001485465228 LIMIT 0,10");
//		if(list!=null&&list.size()>0){
//			for(Record r: list){
//				System.out.println("orderId =="+r.getLong("order_id"));
//			}
//		}
//		this.renderJson(true);
//	}
	
	public static String sendGET(String url,String param){
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
		   }finally{
		    if(read!=null){//关闭流
		     try {
		      read.close();
		     } catch (IOException e) {
		      e.printStackTrace();
		     }
		    }
		   }
		     
		   return result; 
		 }
	
	
	/**自定义统计分析*/
	public void normalAnalysis(){
		String content = StringUtil.null2Str(this.getPara("content"));
		//Map<String,Object> merchantDetail = merchantsInfoService.merchantDetail(merchantId);
		Map<String,Object> dataMap = new HashMap<String,Object>();
		String detail = "contentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontent";
		dataMap.put("detail", detail);
		this.setAttr("data", dataMap);
		render("/view/welcome/normalAnalysis.jsp");
	}
	
//	public void test(){
//		String ip = this.getPara("ip");
//		//String city = IpCityUtil.getCity(ip);
//		long a = System.currentTimeMillis();
//		String city = IpCityUtil.getCity(ip);
//		long b = System.currentTimeMillis();
//		System.out.println("city = "+city+" 耗时："+(b-a)+"ms");
//		this.renderJson(new NormalResponse(null,"ip转换成城市"));
//	}
	
	public static void main(String[] args){
//		String s1="2.3.3.1";
//		String s2="2.3.3";
//		int i = s1.compareTo(s2);
//		System.out.println("i====="+i);
//		
//		System.out.println("s====="+pubVersionStrToInt("2.3.3.160114"));
		String strIp = "192.168.1.117,182.15.1.24";
		if(strIp.contains(",")){
			strIp = strIp.split(",")[0];
		}
		System.out.println("s====="+strIp);
	}
	
	/**
	 * 将整型版本号转换成字符串（233-->2.3.3或者233160114-->2.3.3.160114）
	 * @param version
	 * @return
	 */
	private static String versionIntToStr(int version){
		
		String s = "";
		String v = version+"";
		char [] vs = v.toCharArray();
		for(int i=0;i<vs.length;i++){
			if(i<3){
				s=s+vs[i];
			}
		}
//		if(v.length()==3){
//			s=v.
//		}
		s=version/100+".";
		version = version%100;
		s=s+version/10+"."+version%10;
	    return s;
	}
	
	private static int versionStrToInt(String version){
		int v = 0;
		version=version.replace(".", "");
		v = StringUtil.nullToInteger(version);
		return v;
	}
	
	private static int pubVersionStrToInt(String pubVersion) {
		int version = 0;
		if (pubVersion.contains(".")) {
			// “.”号分割的版本号，如1.0.2
			String v = "";
			String[] arr = pubVersion.split("\\.");
			if (arr != null && arr.length > 0) {
				for (int i = 0; i < arr.length; i++) {
					v = v + arr[i];
				}
			}
			version = StringUtil.nullToInteger(v);
		} else {
			version = StringUtil.nullToInteger(pubVersion);
		}
		return version;
	}
	
	
}
