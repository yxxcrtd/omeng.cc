package com.shanjin.web.proxy.servlet;

import com.shanjin.common.util.EscapeUtil;
import com.shanjin.web.proxy.wechat.http.HttpClientConnectionManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/21
 * @desc TODO
 */

@WebServlet(name = "weChatAuth", value = {"/wechat/token", "/wechat/token/client_credential", "/wechat/js_api_ticket","/wechat/user_info"})
public class WeChatAuthServlet extends HttpServlet {


    private static final String WE_CHAT_ACCESS_TOKEN_PROXY = "https://101.226.90.58/sns/oauth2/access_token";//微信授权获取accessToken
    private static final String WE_CHAT_ACCESS_TOKEN_CLIENT_CRED_PROXY = "https://api.weixin.qq.com/cgi-bin/token";//微信获取客户端认证accessToken
    private static final String WE_CHAT_JS_API_TICKET_PROXY = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";//jsApiTicket 获取接口
    private static final String WE_CHAT_GET_USER_INFO_PROXY = "https://api.weixin.qq.com/sns/userinfo";//微信获取用户信息


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //业务处理http转发

        String servletUrl = req.getServletPath();
        String url = "";
        switch (servletUrl) {
            case "/wechat/token": {
                url = WE_CHAT_ACCESS_TOKEN_PROXY;
                break;
            }
            case "/wechat/token/client_credential": {
                url = WE_CHAT_ACCESS_TOKEN_CLIENT_CRED_PROXY;
                break;
            }
            case "/wechat/js_api_ticket": {
                url = WE_CHAT_JS_API_TICKET_PROXY;
                break;
            }
            case "/wechat/user_info": {
                url = WE_CHAT_GET_USER_INFO_PROXY;
                break;
            }
            default: {
                System.out.println("没有匹配的servletURL....");
            }
        }

        if (null == url || "".equals(url)) {
            System.out.println("未匹配url，请求不处理...");
            return;
        }
        Enumeration<String> paramEnums = req.getParameterNames();
        StringBuilder sb = new StringBuilder("");
        if (paramEnums != null) {
            while (paramEnums.hasMoreElements()) {
                String paramName = paramEnums.nextElement();
                sb.append("&").append(paramName).append("=").append(req.getParameter(paramName));
            }
        }
        if (sb.length() > 1) {
            sb.replace(0, 1, "?");
        }
        url += sb.toString();

        HttpGet get = HttpClientConnectionManager.getGetMethod(url);
        HttpClient httpclient = new DefaultHttpClient();
        httpclient = HttpClientConnectionManager.getSSLInstance(httpclient); // 接受任何证书的浏览器客户端

        HttpResponse response = httpclient.execute(get);
        System.out.println("响应码：" + response.getStatusLine().getStatusCode());
        byte[] bytes = EntityUtils.toByteArray(response.getEntity());
        String content = new String(bytes, "UTF-8");
        System.out.println("响应结果:" + content);
        content = EscapeUtil.escape(content);
        System.out.println("编码后的content为:"+content);
        resp.getOutputStream().write(content.getBytes("UTF-8"));
    }


}
