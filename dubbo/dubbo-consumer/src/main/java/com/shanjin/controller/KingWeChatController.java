package com.shanjin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.EscapeUtil;
import com.shanjin.common.util.MD5Util;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.ISilenceLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc 一对一快捷支付
 */
@Controller
@RequestMapping("/wechat/king")
public class KingWeChatController {

    private static final String APP_ID;
    private static final String APP_SECRET;
    private static final String WE_CHAT_OAUTH_URL_PROXY; //oauth 认证接口
    private static final String WE_CHAT_REDIRECT_URL_PROXY;//oauth 服务器回调接口

    private static final String WE_CHAT_ACCESS_TOKEN_PROXY;//oauth获取accessToken 接口
    private static final String WE_CHAT_ACCESS_TOKEN__CLIENT_CRED_PROXY;//客户端认证获取token
    private static final String WE_CHAT_JS_API_TICKET_PROXY;//jsApiTicket 获取接口
    private static final String WE_CHAT_GET_USER_INFO_URL_PROXY;//微信获取用户信息代理接口


    static {
        WE_CHAT_OAUTH_URL_PROXY = "https://open.weixin.qq.com/connect/oauth2/authorize";
        WE_CHAT_ACCESS_TOKEN_PROXY = "http://webProxy:8080/wechat/token";
        WE_CHAT_ACCESS_TOKEN__CLIENT_CRED_PROXY = "http://webProxy:8080/wechat/token/client_credential";
        WE_CHAT_JS_API_TICKET_PROXY = "http://webProxy:8080/wechat/js_api_ticket";
        WE_CHAT_GET_USER_INFO_URL_PROXY = "http://webProxy:8080/wechat/user_info";

        if (Constant.DEVMODE) {
            APP_ID = "wx05d88d21f8aa1b92";//测试
            APP_SECRET = "eb6eb3a9e35fd0e59715d3d1b2b2fa33";
            WE_CHAT_REDIRECT_URL_PROXY = "http://352ec492.ngrok.io/wechat/king/token";
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
    @Autowired
    private ISilenceLoginService silenceLoginService;

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public void authCode(@RequestParam("destUrl") String destUrl, HttpServletResponse response) throws IOException {

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
        JSONObject jsonObject = JSON.parseObject(EscapeUtil.unescape(str));
        String openid = jsonObject.getString("openid");
        String accessToken = jsonObject.getString("access_token");
        if (StringUtils.isEmpty(openid) || StringUtil.isEmpty(accessToken)) {
            String errcode = jsonObject.getString("errcode");
           /* if ("40029".equals(errcode)) {
                logger.info("重复提交code请求token...重定向到申请code请求中去");
                response.sendRedirect("/wechat/code?destUrl=" + destUrl);
                return;
            }*/
            response.getOutputStream().write(str.getBytes("UTF-8"));
            return;
        }

        StringBuilder paramSb = new StringBuilder("");
        //免登陆相关参数存入
        Map<String, Object> params = silenceLoginService.getSilenceLoginParams(openid);
        if (null == params || params.isEmpty()) {
            //获取用户基本信息
            StringBuilder userInfoSb = new StringBuilder(WE_CHAT_GET_USER_INFO_URL_PROXY);
            userInfoSb.append("?").append("access_token=").append(accessToken);
            userInfoSb.append("&").append("openid=").append(openid);
            userInfoSb.append("&").append("lang=zh_CN");
            String userInfoStr = restTemplate.getForObject(userInfoSb.toString(), String.class);
            if (StringUtil.isEmpty(userInfoStr)) {
                logger.info("获取用户信息失败");
                return;
            }
            Map<String,Object> json = JSON.parseObject(EscapeUtil.unescape(userInfoStr));
            if(json.containsKey("errcode")){
                logger.error("查询用户信息失败:{}",json);
                return;
            }
            params = new HashMap<String, Object>(1);
            params.put("userInfo",userInfoStr);
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            paramSb.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(),"UTF-8"));
        }
        if (destUrl.indexOf("?") < 0) {
            paramSb.replace(0, 1, "?");
        }
        destUrl = destUrl.replace("SPECILE_CHAR", "#");
        destUrl += paramSb.toString();
        //如果是当前servletUrl
        response.sendRedirect(destUrl);
        logger.info("获取token成功。。。");

    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test(HttpServletResponse response,HttpServletRequest request) throws IOException {
        String str = request.getParameter("userInfo");
        System.out.println(EscapeUtil.unescape(str));
        response.getOutputStream().write(str.getBytes(Charset.forName("UTF-8")));
    }
}
