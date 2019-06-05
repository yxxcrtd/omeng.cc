package com.shanjin.controller.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestOrderPayComplete {

    private CloseableHttpClient httpClient;
    private HttpPost httpPost;

    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    /**
     * 完成支付宝订单
     */
    @Test
    public void finishAliPayOrder() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/finishAliPayOrder");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("orderId", "380555"));
        params.add(new BasicNameValuePair("tradeNo", "4005642001201607148958108455"));

        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);

        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        System.out.println(JSONObject.parse(responseJson));
        resp.close();
        httpClient.close();
    }

    /**
     * 完成微信订单
     */
    @Test
    public void finishWeChatOrder() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/finishWeChatOrder");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("orderId", "380555"));
        params.add(new BasicNameValuePair("tradeNo", "4005642001201607148958108455"));

        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);

        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        System.out.println(JSONObject.parse(responseJson));
        resp.close();
        httpClient.close();
    }

    /**
     * sendMQ
     */
    @Test
    public void sendMQ() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/sendMQ");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("orderId", "380555"));
        params.add(new BasicNameValuePair("tradeNo", "4005642001201607148958108455"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        System.out.println(JSONObject.parse(responseJson));
        resp.close();
        httpClient.close();
    }

    /**
     * insertMQSendFailure
     */
    @Test
    public void insertMQSendFailure() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/insertMQSendFailure");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("orderId", "380555"));
        params.add(new BasicNameValuePair("tradeNo", "4005642001201607148958108455"));
        params.add(new BasicNameValuePair("type", "1"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        System.out.println(JSONObject.parse(responseJson));
        resp.close();
        httpClient.close();
    }

    /**
     * updateMQSendFailure
     */
    @Test
    public void updateMQSendFailure() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/updateMQSendFailure");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", "2"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        System.out.println(JSONObject.parse(responseJson));
        resp.close();
        httpClient.close();
    }

    /**
     * sendMQ1
     */
    @Test
    public void sendMQ1() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/sendMQ");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        System.out.println(JSONObject.parse(responseJson));
        resp.close();
        httpClient.close();
    }

}
