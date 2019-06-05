package open.wechat.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import open.wechat.constant.Constant;


public class ShareSignUtil {
	    public static String token = null;
	    public static String time = null;
	    public static String jsapi_ticket = null;
	    /**
	     * 
	     * @param appId   公账号appId
	     * @param appSecret
	     * @param url    当前网页的URL，不包含#及其后面部分
	     * @return
	     */
	    public static Map<String, String> getSignParam(String url){
		    try {
				token = ShareUtil.getAccessToken(Constant.AppId, Constant.AppSecret);
			    if(token!=null&&!"".equals(token)){
			    	jsapi_ticket = ShareUtil.getJsApiTicket(token);
			    	if(jsapi_ticket!=null&&!"".equals(jsapi_ticket)){
			    	    time = getTime();
			    	}
			    }
			} catch (Exception e) {
				e.printStackTrace();
				return new HashMap<String,String>();
			}

		    
//	        if(token == null){
//	            token = CommonUtil.getToken(appId, appSecret);
//	            jsapi_ticket = CommonUtil.getJsApiTicket(token.getAccessToken());
//	            time = getTime();
//	        }else{
//	            if(!time.substring(0, 13).equals(getTime().substring(0, 13))){ //每小时刷新一次
//	                token = null;
//	                token = CommonUtil.getToken(appId, appSecret);
//	                jsapi_ticket = CommonUtil.getJsApiTicket(token.getAccessToken());
//	                time = getTime();
//	            }
//	        }
	         
	        //String url = getUrl();
	         
	        Map<String, String> params = sign(jsapi_ticket, url);
	        params.put("appId", Constant.AppId);
//	         
//	        JSONObject jsonObject = JSONObject.parseObject(params);  
//	        String jsonStr = jsonObject.toString();
	        System.out.println(params.toString());
	        return params;
	    }
	     
//	    private static String getUrl(){
//	        HttpServletRequest request = ServletActionContext.getRequest();
//	         
//	        StringBuffer requestUrl = request.getRequestURL();
//	         
//	        String queryString = request.getQueryString();
//	        String url = requestUrl +"?"+queryString;
//	        return url;
//	    }
	     
	    public static Map<String, String> sign(String jsapi_ticket, String url) {
	        Map<String, String> ret = new HashMap<String, String>();
	        String nonce_str = create_nonce_str();
	        String timestamp = create_timestamp();
	        String str;
	        String signature = "";
	 
	        //注意这里参数名必须全部小写，且必须有序
	        str = "jsapi_ticket=" + jsapi_ticket +
	                  "&noncestr=" + nonce_str +
	                  "&timestamp=" + timestamp +
	                  "&url=" + url;
	 
	        try
	        {
	            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	            crypt.reset();
	            crypt.update(str.getBytes("UTF-8"));
	            signature = byteToHex(crypt.digest());
	        }
	        catch (NoSuchAlgorithmException e)
	        {
	            e.printStackTrace();
	        }
	        catch (UnsupportedEncodingException e)
	        {
	            e.printStackTrace();
	        }
	 
	        ret.put("url", url);
	        ret.put("jsapi_ticket", jsapi_ticket);
	        ret.put("nonceStr", nonce_str);
	        ret.put("timestamp", timestamp);
	        ret.put("signature", signature);
	 
	        return ret;
	    }
	 
	    private static String byteToHex(final byte[] hash) {
	        Formatter formatter = new Formatter();
	        for (byte b : hash)
	        {
	            formatter.format("%02x", b);
	        }
	        String result = formatter.toString();
	        formatter.close();
	        return result;
	    }
	 
	    private static String create_nonce_str() {
	        return UUID.randomUUID().toString();
	    }
	 
	    private static String create_timestamp() {
	        return Long.toString(System.currentTimeMillis());
	    }
	     
	    //获取当前系统时间 用来判断access_token是否过期
	    public static String getTime(){
	        Date dt=new Date();
	        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        return sdf.format(dt);
	    }
	}

