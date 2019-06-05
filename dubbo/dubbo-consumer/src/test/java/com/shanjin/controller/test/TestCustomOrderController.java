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

import static com.shanjin.common.util.StringUtil.filterFourCharString;

public class TestCustomOrderController {

    private CloseableHttpClient httpClient;
    private HttpPost httpPost;

    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    /**
     * 保存订单
     *
     * @throws Exception
     */
    @Test
    public void testSaveCustomOrder() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/saveCustomOrder");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("serviceId", "9")); // service_type表中主键ID
        params.add(new BasicNameValuePair("longitude", "117.2414273433"));
        params.add(new BasicNameValuePair("latitude", "31.8246109216"));
        params.add(new BasicNameValuePair("province", "安徽"));
        params.add(new BasicNameValuePair("city", "合肥"));
        params.add(new BasicNameValuePair("userId", "146561243747444178"));
        params.add(new BasicNameValuePair("serviceTime", "2016-11-12 13:14:15"));
        params.add(new BasicNameValuePair("address", "南二环和潜山路交叉口（天鹅湖北边）华邦世贸城"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);

        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        String resultCode = result.getString("resultCode");
//        Assert.assertEquals("000", resultCode);
        System.out.println(responseJson);
        resp.close();
        httpClient.close();
    }

    /**
     * 提供报价方案
     *
     * @throws Exception
     */
    @Test
    public void testPricePlanVerify() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/supplyPricePlan");

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("userId", "146719401961241611"));
        parameters.add(new BasicNameValuePair("merchantId", "146719401961241611")); // 146444889775973052   146716643881640541  146444850425612459
        parameters.add(new BasicNameValuePair("orderId", "381134")); // 999 70388 23483
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF8");
        httpPost.setEntity(entity);

        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        String resultCode = result.getString("resultCode");

//        Assert.assertEquals("000", resultCode);

        System.out.println(responseJson);
        resp.close();
        httpClient.close();
    }

    @Test
    public void testTemp() {
        String title = "A😁B?C\uD83D\uDC4CD";
        System.out.println(filterFourCharString(title).replaceAll("0000", ""));
    }

    /**
     * 支付待确认
     *
     * @throws Exception
     */
    @Test
    public void tobeConfirmed() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/tobeConfirmed");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("merchantId", "146719401961241611"));
        params.add(new BasicNameValuePair("orderPayType", "0")); // 0 收入；1支出
        params.add(new BasicNameValuePair("orderId", "381817"));
        params.add(new BasicNameValuePair("paymentPrice", "1.23"));
        params.add(new BasicNameValuePair("consumePrice", "4.56"));
        params.add(new BasicNameValuePair("nowTime", "2016-12-12 13:14:15"));
        params.add(new BasicNameValuePair("tradeNo", "2016081021001004300259993915"));
        params.add(new BasicNameValuePair("buyerNo", "meifaxin2211@163.com"));
        params.add(new BasicNameValuePair("tradeType", "1")); // 1 支付宝；2微信；3银联；9消费抵用金支付
        params.add(new BasicNameValuePair("inviteCode", "123456789")); // 1 支付宝；2微信；3银联；9消费抵用金支付
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        System.out.println(result);
        resp.close();
        httpClient.close();
    }

    @Test
    public void findPaymentByOrderId() throws Exception {
        httpPost = new HttpPost(Constant.HOST + "/customOrder/findPaymentByOrderId");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("orderId", "383383"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF8");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = httpClient.execute(httpPost);
        StatusLine status = resp.getStatusLine();
        Assert.assertEquals(200, status.getStatusCode());
        String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
        JSONObject result = (JSONObject) JSONObject.parse(responseJson);
        System.out.println(result);
        resp.close();
        httpClient.close();
    }

}
