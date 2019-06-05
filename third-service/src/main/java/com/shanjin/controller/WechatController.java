package com.shanjin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import open.wechat.constant.Constant;
import open.wechat.model.OAuthAccessToken;
import open.wechat.model.UserEntity;
import open.wechat.util.OauthUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.CommonUtil;
import com.shanjin.common.util.GetWxOrderno;
import com.shanjin.common.util.HttpRequest;
import com.shanjin.common.util.RequestHandler;
import com.shanjin.common.util.ServletUtil;
import com.shanjin.common.util.Sha1Util;
import com.shanjin.common.util.StringUtil;
import com.shanjin.common.util.TenpayUtil;
import com.shanjin.service.IActivityService;
import com.shanjin.service.IWechatService;

/**
 * 微信 验证/授权/支付相关接口
 *  
 *
 */
@Controller
@RequestMapping("/wechat")
public class WechatController {
	private static Logger log=Logger.getLogger(WechatController.class.getName());
	
	@Autowired
	private IWechatService wechatService;
	
	@Autowired
	private IActivityService activityService;
	
    @RequestMapping("/getWeixin")
    @SystemControllerLog(description = "获取微信配置参数")
    public @ResponseBody Object getWeiXin(HttpServletRequest request,String url) {
    
    	if(!StringUtil.isNullStr(url)){
			Map map=request.getParameterMap();  
		    Set keSet=map.entrySet();  
		    for(Iterator itr=keSet.iterator();itr.hasNext();){  
		        Map.Entry me=(Map.Entry)itr.next();  
		        Object ok=me.getKey(); 
		        Object ov=me.getValue();  
		        String[] value=new String[1];  
		        if(ov instanceof String[]){  
		            value=(String[])ov;  
		        }else{  
		            value[0]=ov.toString();  
		        }  
		        for(int k=0;k<value.length;k++){  
			        if(!ok.toString().equals("url")){
			        	if(url.contains("?")){
			        		if(url.endsWith("?")){
			        			url=url+ok+"="+value[k];
				    		}else if(url.endsWith("&")){
				    			url=url+ok+"="+value[k];
				    		}else{
				    			url=url+"&"+ok+"="+value[k];
				    		}
			        	}else{
			        		url=url+"?"+ok+"="+value[k];
			        	}
			        }
		        }  
		    }  	
		}
         Map<String, String> jsonObject = null;
         jsonObject =wechatService.getSignParam(url);
         return jsonObject;
    }
    
    @RequestMapping("/getWeixinAuth")
    @SystemControllerLog(description = "WeixinAuth")
    public @ResponseBody Object getWeixinAuth(String signature,String timestamp,String nonce,String echostr) {
    	return echostr;
    }
    
    @RequestMapping("/oauth")
    @SystemControllerLog(description = "微信授权")
    public @ResponseBody Object oauth(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			String callUrl="";
			callUrl=(String) request.getParameter("callUrl");
//			System.out.println("callUrl="+callUrl);  
			if(!StringUtil.isNullStr(callUrl)){
				Map map=request.getParameterMap();  
			    Set keSet=map.entrySet();  
			    for(Iterator itr=keSet.iterator();itr.hasNext();){  
			        Map.Entry me=(Map.Entry)itr.next();  
			        Object ok=me.getKey(); 
			        Object ov=me.getValue();  
			        String[] value=new String[1];  
			        if(ov instanceof String[]){  
			            value=(String[])ov;  
			        }else{  
			            value[0]=ov.toString();  
			        }  
			        for(int k=0;k<value.length;k++){  
				        if(!ok.toString().equals("callUrl")){
				        	if(callUrl.contains("?")){
				        		if(callUrl.endsWith("?")){
					    			callUrl=callUrl+ok+"="+value[k];
					    		}else if(callUrl.endsWith("&")){
					    			callUrl=callUrl+ok+"="+value[k];
					    		}else{
					    			callUrl=callUrl+"&"+ok+"="+value[k];
					    		}
				        	}else{
				        		callUrl=callUrl+"?"+ok+"="+value[k];
				        	}
				        }
			        }  
			    }  	
			}

		//	System.out.println("callUrl="+callUrl);  
			String oauthUrl;
			oauthUrl = OauthUtil.getOauthUrl(callUrl);
			log.info("oauthUrl="+oauthUrl);
			response.sendRedirect(oauthUrl);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
    @RequestMapping("/callback")
    @SystemControllerLog(description = "微信授权回调")
	public @ResponseBody Object callback(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		String code = request.getParameter("code");
		String callUrl = request.getParameter("callUrl");
		
		if(!StringUtil.isNullStr(callUrl)){
			Map map=request.getParameterMap();  
		    Set keSet=map.entrySet();  
		    for(Iterator itr=keSet.iterator();itr.hasNext();){  
		        Map.Entry me=(Map.Entry)itr.next();  
		        Object ok=me.getKey(); 
		        Object ov=me.getValue();  
		        String[] value=new String[1];  
		        if(ov instanceof String[]){  
		            value=(String[])ov;  
		        }else{  
		            value[0]=ov.toString();  
		        }  
		        for(int k=0;k<value.length;k++){  
			        if(!ok.toString().equals("callUrl")){
			        	if(callUrl.contains("?")){
			        		if(callUrl.endsWith("?")){
				    			callUrl=callUrl+ok+"="+value[k];
				    		}else if(callUrl.endsWith("&")){
				    			callUrl=callUrl+ok+"="+value[k];
				    		}else{
				    			callUrl=callUrl+"&"+ok+"="+value[k];
				    		}
			        	}else{
			        		callUrl=callUrl+"?"+ok+"="+value[k];
			        	}
			        }
		        }  
		    }  	
		}
		
	    UserEntity entity=null;
	    String openId = "";
		try {
			request.setCharacterEncoding("utf-8"); 
			OAuthAccessToken oac = OauthUtil.getOAuthAccessToken(Constant.AppId, Constant.AppSecret,code);//通过code换取网页授权access_token
			log.info("--------------------"+oac.getAccessToken()+";"+oac.getRefreshToken()+";"+oac.getScope()+";"+oac.getOpenid());
			if(StringUtil.isNullStr(oac.getAccessToken())||StringUtil.isNullStr(oac.getRefreshToken())
					||StringUtil.isNullStr(oac.getScope())||StringUtil.isNullStr(oac.getOpenid())){
				response.sendRedirect("http://activity.omeng.cc/wechat/oauth?callUrl="+callUrl);
				return null;
			}
			OAuthAccessToken oacd=OauthUtil.refershOAuthAccessToken(Constant.AppId, oac.getRefreshToken());//刷新access_token
			log.info("--------------------"+oacd.getAccessToken()+";"+oacd.getRefreshToken()+";"+oacd.getScope()+";Openid:"+oacd.getOpenid());
			if(oacd==null||StringUtil.isNullStr(oacd.getAccessToken())||StringUtil.isNullStr(oac.getOpenid())){
				response.sendRedirect("http://activity.omeng.cc/wechat/oauth?callUrl="+callUrl);
				return null;
			}
			openId = oacd.getOpenid();
			entity=OauthUtil.acceptOAuthUserNews(oacd.getAccessToken(),oacd.getOpenid());//获取用户信息
			if(entity==null){
				response.sendRedirect("http://activity.omeng.cc/wechat/oauth?callUrl="+callUrl);
				return null;
			}
			log.info("--------------------"+entity.getNickname()+";"+entity.getCountry());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if(entity==null){
			return null;
		}
		
		wechatService.saveUserInfoByOpenId(openId,entity);
		
		log.info("name="+ entity.getNickname());
		log.info("user="+entity);
		if(callUrl.contains("?")){
			callUrl=callUrl+"&openId="+openId;
		}else{
			callUrl=callUrl+"?openId="+openId;
		}
		log.info("callUrl="+callUrl);
		callUrl=callUrl.replace("SPECILE_CHAR", "#");
		log.info("callUrl="+callUrl);
		try {
			response.sendRedirect(callUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
    
	
	@RequestMapping("/topayOrder")
	@SystemControllerLog(description = "统一下单")
	public @ResponseBody Object topayOrder(HttpServletRequest request,
			HttpServletResponse response) {
		log.info("---------统一下单操作开始-----------");
		JSONObject jsonObjectback = new JSONObject();
		String appid = Constant.AppId;
		String demand = request.getParameter("demand");
		String money = request.getParameter("money");
		String openId = request.getParameter("openId");
		String userId = request.getParameter("merchantId");
		
		if(StringUtil.isNullStr(userId)){
			jsonObjectback = new ResultJSONObject("001", "商户ID不可为空");
			return jsonObjectback; 
		}
		if(StringUtil.isNullStr(openId)){
			jsonObjectback = new ResultJSONObject("003", "用户openId不可为空");
			return jsonObjectback; 
		}
		if(!StringUtil.isNullStr(demand)){
			if(demand.length()>10){
				jsonObjectback = new ResultJSONObject("004", "贺词超出限制");
				return jsonObjectback; 
			}
		}
		if(StringUtil.isNullStr(money)){
			jsonObjectback = new ResultJSONObject("005", "金额不可为空");
			return jsonObjectback; 
		}
		Map<String, Object> user = wechatService.getUser(openId);
		if (user == null) {
			jsonObjectback = new ResultJSONObject("006", "用户信息不可为空");
			return jsonObjectback;
		} 
		String orderNo = appid + Sha1Util.getTimeStamp();
		//保存用户的贺词
		wechatService.saveUserInfo(orderNo,demand);
		// 网页授权后获取传递的参数
		
		// 金额转化为分为单位
		float sessionmoney = Float.parseFloat(money);
		if(sessionmoney<=0){
			jsonObjectback = new ResultJSONObject("007", "金额不可为0或者负数");
			return jsonObjectback; 
		}
		String finalmoney = String.format("%.2f", sessionmoney);
		finalmoney = finalmoney.replace(".", "");

		// 商户相关资料
		
		String appsecret = Constant.AppSecret;
		String partner = Constant.Partner;
		String partnerkey = Constant.Partnerkey;

//		if(StringUtil.isNullStr(openId)){
//			String code = request.getParameter("code");
//			String URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";
//			JSONObject jsonObject = CommonUtil.httpsRequest(URL, "GET", null);
//			if (null != jsonObject) {
//				openId = jsonObject.getString("openid");
//			}
//		}
		
		// 获取openId后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
		String currTime = TenpayUtil.getCurrTime();
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		String strReq = strTime + strRandom;

		// 商户号
		String mch_id = partner;
		// 子商户号 非必输
		// String sub_mch_id="";
		// 设备号 非必输
		String device_info = "";
		// 随机数
		String nonce_str = strReq;
		// 商品描述
		// String body = describe;

		// 商品描述根据情况修改
		String body = "红包";
		// 附加数据
		String attach = userId;
		// 商户订单号
		String out_trade_no = orderNo;
		int intMoney = Integer.parseInt(finalmoney);
       
		// 总金额以分为单位，不带小数点
		String total_fee = String.valueOf(intMoney);
		// 订单生成的机器 IP
		String spbill_create_ip = request.getRemoteAddr();
		// 订 单 生 成 时 间 非必输
		// String time_start ="";
		// 订单失效时间 非必输
		// String time_expire = "";
		// 商品标记 非必输
		// String goods_tag = "";

		// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
		String notify_url = Constant.notify_url;

		String trade_type = "JSAPI";
		String openid = openId;
		// 非必输
		// String product_id = "";
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("attach", attach);
		packageParams.put("out_trade_no", out_trade_no);

		// 金额为单位为 分
		packageParams.put("total_fee", total_fee);
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notify_url);

		packageParams.put("trade_type", trade_type);
		packageParams.put("openid", openid);

		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init(appid, appsecret, partnerkey);

		String sign = reqHandler.createSign(packageParams);
		log.info("生成的签名sign="+sign);
		String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
				+ mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
				+ "</nonce_str>" + "<sign>" + sign + "</sign>"
				+ "<body><![CDATA[" + body + "]]></body>" + "<attach>" + attach
				+ "</attach>"
				+ "<out_trade_no>"
				+ out_trade_no
				+ "</out_trade_no>"
				+"<total_fee>"
				+ total_fee
				+ "</total_fee>"
				+"<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
				+ "<notify_url>" + notify_url + "</notify_url>"
				+ "<trade_type>" + trade_type + "</trade_type>" + "<openid>"
				+ openid + "</openid>" + "</xml>";
		log.info("请求统一下单xml="+xml);
		String allParameters = "";
		try {
			allParameters = reqHandler.genPackage(packageParams);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String createOrderURL = Constant.createOrder_url;
		String prepay_id = "";
		try {
			prepay_id = new GetWxOrderno().getPayNo(createOrderURL, xml);
			log.info("prepay_id="+prepay_id);
			if (prepay_id.equals("")) {
				return jsonObjectback;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
		String appid2 = appid;
		String timestamp = Sha1Util.getTimeStamp();
		String nonceStr2 = nonce_str;
		String prepay_id2 = "prepay_id=" + prepay_id;
		String packages = prepay_id2;
		finalpackage.put("appId", appid2);
		finalpackage.put("timeStamp", timestamp);
		finalpackage.put("nonceStr", nonceStr2);
		finalpackage.put("package", packages);
		finalpackage.put("signType", "MD5");
		String finalsign = reqHandler.createSign(finalpackage);
		log.info("appid=" + appid2 + "&timeStamp="
				+ timestamp + "&nonceStr=" + nonceStr2 + "&package=" + packages
				+ "&sign=" + finalsign);
		log.info("-----下单成功---------");
		
		System.out.println("finalsign="+finalsign);
		jsonObjectback.put("appid", appid2);
		jsonObjectback.put("timeStamp", timestamp);
		jsonObjectback.put("nonceStr", nonceStr2);
		jsonObjectback.put("signType", "MD5");
		jsonObjectback.put("package", packages);
		jsonObjectback.put("sign", finalsign);
		return jsonObjectback;
	
	}
    
	@RequestMapping("/notifyUser")
    @SystemControllerLog(description = "支付通知")
    public @ResponseBody Object notifyUser(HttpServletRequest request, HttpServletResponse response) {
		log.info("-----支付完成后通知接口开始---------");
		String line = null;
		PrintWriter out = null;
		Map map = null;
		SortedMap<String, String> packageParams = null;
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(ServletInputStream) request.getInputStream()));

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			 map = GetWxOrderno.doXMLParse(sb.toString());
			 packageParams=GetWxOrderno.doXMLParseToSort(sb.toString());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String appid = Constant.AppId;
		String appsecret = Constant.AppSecret;
		String partnerkey = Constant.Partnerkey;
		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init(appid, appsecret, partnerkey);

		String sign = reqHandler.createSign(packageParams);
		System.out.println(sign);
		System.out.println(map.get("sign"));

		boolean flag =sign.equals(map.get("sign"));
		String backxml="<xml><return_code>FAIL</return_code>"+
                "<return_msg>签名失败</return_msg></xml>";
		String backCode=(String) map.get("return_code");
		System.out.println("flag="+flag);
		log.info("-----签名验证="+flag);
		log.info("-----支付状态码backCode="+backCode);
		if(backCode.equals("FAIL")||!flag){
		
		}else{
			
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("merchantId", StringUtil.nullToLong(map.get("attach")));
			if(map.get("total_fee").equals("")||map.get("total_fee").equals("0")){
				param.put("total_fee", 0);
			}else{
			float total_fee=Float.parseFloat((String) map.get("total_fee"))/100;
			param.put("total_fee", total_fee);
			}
			Map<String,Object> user=wechatService.getUserInfo((String)map.get("out_trade_no"),(String)map.get("openid"));
			
			Map<String,Object> entity=(Map<String , Object>) user.get("user");
			String demand = (String) user.get("demand");;
			
			if(entity!=null){
				param.put("nickname",entity.get("nickname"));
				param.put("headimgurl", entity.get("headimgUrl"));
			}else{
				param.put("nickname", "未知");
				param.put("headimgurl", "");
			}
			param.put("type", "2");
			param.put("label", "label7");
			param.put("labelName","红包");
			param.put("demand",demand);
			param.put("out_trade_no", map.get("out_trade_no"));
			param.put("transaction_id", map.get("transaction_id"));
			param.put("openId", (String)map.get("openid"));
			try {
				JSONObject jsonObject =	activityService.saveCuttingInfo(param);
				if(jsonObject==null || !jsonObject.getString("resultCode").equals("000")){
					
				}else{
					backxml = "<xml><return_code>SUCCESS</return_code>"+
		                    "<return_msg>OK</return_msg></xml>";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return backxml;
	}

}
