package com.shanjin.controller;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/21
 * @desc 微信认证授权 controller
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.CommonResultVo;
import com.shanjin.common.util.HttpRequest;
import com.shanjin.common.util.MD5Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

import static com.shanjin.cache.CacheConstants.WECHAT_JSAPI_TICKET_PREKEY;


@Controller
@RequestMapping("/wechat")
public class WeChatAuthController {


    private static final String APP_ID;
    private static final String APP_SECRET;
    private static final String WE_CHAT_OAUTH_URL_PROXY; //oauth 认证接口
    private static final String WE_CHAT_REDIRECT_URL_PROXY;//oauth 服务器回调接口

    private static final String WE_CHAT_ACCESS_TOKEN_PROXY;//oauth获取accessToken 接口
    private static final String WE_CHAT_ACCESS_TOKEN__CLIENT_CRED_PROXY;//客户端认证获取token
    private static final String WE_CHAT_JS_API_TICKET_PROXY;//jsApiTicket 获取接口


    static {
        WE_CHAT_OAUTH_URL_PROXY = "https://open.weixin.qq.com/connect/oauth2/authorize";
        WE_CHAT_ACCESS_TOKEN_PROXY = "http://webProxy:8080/wechat/token";
        WE_CHAT_ACCESS_TOKEN__CLIENT_CRED_PROXY = "http://webProxy:8080/wechat/token/client_credential";
        WE_CHAT_JS_API_TICKET_PROXY = "http://webProxy:8080/wechat/js_api_ticket";

        if (Constant.DEVMODE) {
            APP_ID = "wx2170611bbd061e2a";//测试
            APP_SECRET = "314597ee91773dcab50c5a469926373e";
            WE_CHAT_REDIRECT_URL_PROXY = "http://wxtest.omeng.cc/wechat/token";
        } else {
            APP_ID = "wxab9427aa633751e7";//生产
            APP_SECRET = "91004e1f60444d5335af8ef2d6c89909";
            WE_CHAT_REDIRECT_URL_PROXY = "http://api.oomeng.cn/wechat/token";
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(WeChatAuthController.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ICommonCacheService commonCacheService;

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public void authCode(@RequestParam("destUrl") String destUrl, HttpServletResponse response) throws IOException {

        if (StringUtils.isEmpty(destUrl)) {
            response.getOutputStream().write("error page,invalid destUrl...".getBytes("UTF-8"));
            response.flushBuffer();
            return;//TODO 建议重定向到错误页面
        }

        //瓶装codeUrl
        StringBuilder sb = new StringBuilder(WE_CHAT_OAUTH_URL_PROXY);
        sb.append("?").append("appid=").append(APP_ID);
        sb.append("&").append("redirect_uri=").append(URLEncoder.encode(WE_CHAT_REDIRECT_URL_PROXY + "?destUrl=" + destUrl, "UTF-8"));
        sb.append("&").append("response_type=").append("code");
        sb.append("&").append("scope=").append("snsapi_userinfo");
        sb.append("&").append("state=").append(MD5Util.MD5_32("" + Calendar.getInstance().getTime()) + new Random().nextInt(999)).append("#wechat_redirect");
        String codeUrl = sb.toString();
        logger.info("获取authcode 请求url:{}", codeUrl);

        response.sendRedirect(codeUrl);
        logger.info("重定向到微信登录授权页面");
    }

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public void getToken(@RequestParam("destUrl") String destUrl, @RequestParam("code") String code, HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (StringUtils.isEmpty(destUrl) || StringUtils.isEmpty(code)) {
            response.getOutputStream().write("error page,invalid destUrl or code ...".getBytes("UTF-8"));
            response.flushBuffer();
            return;
        }
        //tokenUrl拼装
        StringBuilder sb = new StringBuilder(WE_CHAT_ACCESS_TOKEN_PROXY);
        sb.append("?").append("appid=").append(APP_ID);
        sb.append("&").append("secret=").append(APP_SECRET);
        sb.append("&").append("code=").append(code);
        sb.append("&").append("grant_type=").append("authorization_code");

        String tokenUrl = sb.toString();
        logger.info("token 请求url:{}", tokenUrl);
        String str = restTemplate.getForObject(tokenUrl, String.class);
        JSONObject jsonObject = JSON.parseObject(str);
        String openid = jsonObject.getString("openid");
        if (StringUtils.isEmpty(openid)) {
            String errcode = jsonObject.getString("errcode");
            if ("40029".equals(errcode)) {
                logger.info("重复提交code请求token...重定向到申请code请求中去");
                response.sendRedirect("/wechat/code?destUrl=" + destUrl);
                return;
            }
            response.getOutputStream().write(str.getBytes("UTF-8"));
            return;
        }
        //重定向到目标url去
        if (destUrl.indexOf("?") > -1) {
            destUrl += "&openId=" + openid;
        } else {
            destUrl += "?openId=" + openid;
        }
        destUrl = destUrl.replace("SPECILE_CHAR", "#");
        //如果是当前servletUrl
        response.sendRedirect(destUrl);
        logger.info("获取token成功。。。");

    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test(HttpServletResponse response, @RequestParam("openId") String openid) throws IOException {
        String msg = "测试成功，获得的openid：" + openid;
        response.getOutputStream().write(msg.getBytes(Charset.forName("UTF-8")));
    }

    @RequestMapping(value = "/getWxConfig")
    @ResponseBody
    public CommonResultVo<?> getWxConfig(@RequestParam("url") String url) throws UnsupportedEncodingException {
        Date start = new Date();

        //参数校验
        if (StringUtils.isEmpty(url)) {
            return new CommonResultVo<Object>("001", "url不能为空，为必传字段");
        }
        //url 解码 处理
        url = URLDecoder.decode(url, "UTF-8");
        System.out.println("####签名url:" + url);
        String ticket = getJsApiTicket();
        if (StringUtils.isEmpty(ticket)) {
            return new CommonResultVo<Object>("001", "获取jsApiTicket失败");
        }
        //拼装签名
        long time = Calendar.getInstance().getTimeInMillis();
        String timestamp = "" + (time / 1000);
        String nonceStr = MD5Util.MD5_32("" + time + new Random(999).nextInt());
        StringBuilder sb = new StringBuilder();
        sb.append("jsapi_ticket=").append(ticket).append("&");
        sb.append("noncestr=").append(nonceStr).append("&");
        sb.append("timestamp=").append(timestamp).append("&");
        sb.append("url=").append(url);
        String signature = org.apache.commons.codec.digest.DigestUtils.sha1Hex(sb.toString());

        Map<String, String> map = new HashMap<String, String>(4);
        map.put("appid", APP_ID);
        map.put("timestamp", timestamp);
        map.put("nonceStr", nonceStr);
        map.put("signature", signature);

        CommonResultVo<Map<String, String>> resultVo = new CommonResultVo<Map<String, String>>();
        resultVo.setData(map);
        Date end = new Date();
        System.out.println("获取微jsapi调用签名，总共耗时:" + (end.getTime() - start.getTime()) + "ms");
        return resultVo;
    }

    /**
     * 生成AccessToken
     *
     * @return if null exception & not null  is normal
     */
    private String getAccessToken() {
        String accessToken = "";
        StringBuilder sbUrl = new StringBuilder(WE_CHAT_ACCESS_TOKEN__CLIENT_CRED_PROXY);
        sbUrl.append("?appid=").append(APP_ID);
        sbUrl.append("&secret=").append(APP_SECRET);
        sbUrl.append("&grant_type=").append("client_credential");
        String str = restTemplate.getForObject(sbUrl.toString(), String.class);
        System.out.println("获取token 返回的数据:" + str);
        if (!StringUtils.isEmpty(str)) {
            JSONObject json = JSON.parseObject(str);
            accessToken = json.getString("access_token");
        }
        return accessToken;
    }

    /**
     * 获取jsApiTicket
     *
     * @return
     */
    private String getJsApiTicket() {
        String ticket = getCacheTicket();
        if (StringUtils.isEmpty(ticket)) {
            synchronized (this) {
                if (StringUtils.isEmpty(ticket)) {
                    //获取accessToken 值
                    String accessToken = getAccessToken();
                    if (StringUtils.isEmpty(accessToken)) {
                        System.out.println("accessToken获取异常");
                        return null;
                    }
                    //获取临时票据ticket
                    StringBuilder sbUrl = new StringBuilder(WE_CHAT_JS_API_TICKET_PROXY);
                    sbUrl.append("?access_token=").append(accessToken);
                    sbUrl.append("&type=jsapi");

                    String str = restTemplate.getForObject(sbUrl.toString(), String.class);
                    System.out.println("获取到的响应结果:" + str);
                    if (!StringUtils.isEmpty(str)) {
                        JSONObject json = JSON.parseObject(str);
                        ticket = json.getString("ticket");
                    }
                    if (null != ticket) {
                        setCacheTicket(ticket);
                    }
                }
            }
        }
        return ticket;
    }

    private void setCacheTicket(String ticket) {
        commonCacheService.setObject(ticket, 7000, WECHAT_JSAPI_TICKET_PREKEY + APP_ID);
    }

    /**
     * 获取缓存ticket
     *
     * @return
     */
    private String getCacheTicket() {
        return (String) commonCacheService.getObject(WECHAT_JSAPI_TICKET_PREKEY + APP_ID);
    }
    
	/**识别商户二维码 */
	@RequestMapping("/readQRcode")
	@SystemControllerLog(description = "识别商户二维码")
	public @ResponseBody void readQRcode(@RequestParam Map<String,Object> param, HttpServletRequest request, HttpServletResponse response){
		try{
			// 根据ua判断进入不同的页面
			String destUrl = HttpRequest.getDomain(request)+"/h5/quickpay/index.html?merchantId="+param.get("merchantId");
			String url = HttpRequest.getDomain(request)
					+ "/wechat/code?destUrl="+destUrl;
			response.sendRedirect(url);
	        response.flushBuffer();
	        return;
		}catch(Exception e){
			MsgTools.sendMsgOrIgnore(e, "readQRcode",param.toString());
			logger.error("识别商户二维码", e);	
		}
	}

}
