package open.wechat.util;

import java.net.URLEncoder;

import open.wechat.constant.Constant;
import open.wechat.model.OAuthAccessToken;
import open.wechat.model.UserEntity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 微信授权接口工具类
 * @author Huang yulai
 *
 */
public class OauthUtil {


	public static DefaultHttpClient httpclient;
	private static Logger log=Logger.getLogger(OauthUtil.class.getName());
	static {
		httpclient = new DefaultHttpClient();
		httpclient = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient); // 接受任何证书的浏览器客户端
	}
	
	/**
	 * 微信OAuth2.0授权（目前微信只支持在微信客户端发送连接，实现授权）
	 * */
	public static String getCodeUrl(String appid, String redirect_uri,String scope,String state) throws Exception {
		redirect_uri = URLEncoder.encode(redirect_uri, "utf-8");
		String getCodeUrl=Constant.getCodeUrl.replace("APPID", appid).replace("REDIRECT_URI", redirect_uri).replace("SCOPE", scope).replace("STATE", state);
		log.info("getCodeUrl Value:"+getCodeUrl);
		return getCodeUrl;
	}
	/**
	 * 微信OAuth2.0授权-获取accessToken(这里通过code换取的网页授权access_token,与基础支持中的access_token不同）
	 */
	public static OAuthAccessToken getOAuthAccessToken(String appid, String secret,String code) throws Exception {
		String getOAuthAccessToken=Constant.getOAuthAccessToken.replace("APPID", appid).replace("SECRET", secret).replace("CODE", code);
		log.info("getOAuthAccessToken Value:"+getOAuthAccessToken);
		HttpGet get = HttpClientConnectionManager.getGetMethod(getOAuthAccessToken);
		DefaultHttpClient httpclient1= new DefaultHttpClient();
		httpclient1 = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient1); // 接受任何证书的浏览器客户端
		HttpResponse response = httpclient1.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		log.info("jsonObject Value:"+jsonObject);
		OAuthAccessToken accessToken=new OAuthAccessToken();
		accessToken.setAccessToken(jsonObject.getString("access_token"));
		accessToken.setExpiresIn(jsonObject.getIntValue("expires_in"));
		accessToken.setRefreshToken(jsonObject.getString("refresh_token"));
		accessToken.setOpenid(jsonObject.getString("openid"));
		accessToken.setScope(jsonObject.getString("scope"));
		httpclient1.close();
		return accessToken;
	}
	/**
	 * 微信OAuth2.0授权-刷新access_token（如果需要）
	 */
	public static OAuthAccessToken refershOAuthAccessToken(String appid, String refresh_token) throws Exception {
		String getreferAccessUrl=Constant.getreferAccessUrl.replace("APPID", appid).replace("REFRESH_TOKEN", refresh_token);
		log.info("getreferAccessUrl Value:"+getreferAccessUrl);
		HttpGet get = HttpClientConnectionManager.getGetMethod(getreferAccessUrl);
		DefaultHttpClient httpclient1= new DefaultHttpClient();
		httpclient1 = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient1); // 接受任何证书的浏览器客户端
		HttpResponse response = httpclient1.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		OAuthAccessToken accessToken=new OAuthAccessToken();
		accessToken.setAccessToken(jsonObject.getString("access_token"));
		accessToken.setExpiresIn(jsonObject.getIntValue("expires_in"));
		accessToken.setRefreshToken(jsonObject.getString("refresh_token"));
		accessToken.setOpenid(jsonObject.getString("openid"));
		accessToken.setScope(jsonObject.getString("scope"));
		httpclient1.close();
		return accessToken;
	}
	/**
	 * 微信OAuth2.0授权-检验授权凭证（access_token）是否有效
	 */
	public static boolean isAccessTokenValid(String accessToken, String openId) throws Exception {
		String isOAuthAccessToken=Constant.isOAuthAccessToken.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		log.info("isOAuthAccessToken Value:"+isOAuthAccessToken);
		HttpGet get = HttpClientConnectionManager.getGetMethod(isOAuthAccessToken);
		DefaultHttpClient httpclient1= new DefaultHttpClient();
		httpclient1 = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient1); // 接受任何证书的浏览器客户端
		HttpResponse response = httpclient1.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		int returnNum=jsonObject.getIntValue("errcode");
		httpclient1.close();
		if(returnNum==0){
			return true;
		}
		return false;
	}
	/**
	 * 微信OAuth2.0授权-获取用户信息（网页授权作用域为snsapi_userinfo，则此时开发者可以通过access_token和openid拉取用户信息）
	 */
	public static UserEntity acceptOAuthUserNews(String accessToken, String openId) throws Exception {
		String getOAuthUserNews=Constant.getOAuthUserNews.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		log.info("getOAuthUserNews Value:"+getOAuthUserNews);
		HttpGet get = HttpClientConnectionManager.getGetMethod(getOAuthUserNews);
		DefaultHttpClient httpclient1= new DefaultHttpClient();
		httpclient1 = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient1); // 接受任何证书的浏览器客户端
		HttpResponse response = httpclient1.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		UserEntity entity=new UserEntity();
		entity.setOpenid(jsonObject.getString("openid"));
		entity.setNickname(jsonObject.getString("nickname"));
		entity.setSex(jsonObject.getIntValue("sex"));
		entity.setProvince(jsonObject.getString("province"));
		entity.setCity(jsonObject.getString("city"));
		entity.setCountry(jsonObject.getString("country"));
		entity.setHeadimgurl(jsonObject.getString("headimgurl"));
		httpclient1.close();
		return entity;
	}
	/**
	 * 获取完整的授权地址
	 */
	public static String getOauthUrl(String callUrl) throws Exception {
		String url=Constant.oauthUrl+"?callUrl="+callUrl;
		String pathUrl = getCodeUrl(Constant.AppId,url,"snsapi_userinfo", "state");
		System.out.println(pathUrl);
		return pathUrl;
	}
	
public static void main(String[] args) {
	try {
		OAuthAccessToken oac = OauthUtil.getOAuthAccessToken(Constant.AppId, Constant.AppSecret,"021vvyAv0C8Olp1KzqDv0W0wAv0vvyAw");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}//通过code换取网页授权access_token
}
}
