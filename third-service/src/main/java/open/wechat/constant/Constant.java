package open.wechat.constant;

/**
 * 常量定义
 * @author Huang yulai
 *
 */
public class Constant {
	public final static String AppId ="wx1303f436d8e44d80";
	public final static String AppSecret ="ed99f33160ed0bb0dc2ec0b705a0f0d2";
	public final static String Partner ="1237612702";
	public final static String Partnerkey ="o2omengshanjinkeji85201536meng12";
	public final static String getTokenUrl ="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	public final static String getCodeUrl="https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
	public final static String getOAuthAccessToken="https://101.226.90.58/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	
	public final static String getreferAccessUrl="https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
	public final static String getOAuthUserNews="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	public final static String isOAuthAccessToken="https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID";
	
	public final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	public final static String ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
	public final static String oauthUrl = "http://activity.omeng.cc/wechat/callback";
	
	
	public final static String notify_url = "http://activity.omeng.cc/wechat/notifyUser";
	public final static String topay_uri = "http://activity.omeng.cc/wechat/topayOrder";
	public final static String createOrder_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
}
