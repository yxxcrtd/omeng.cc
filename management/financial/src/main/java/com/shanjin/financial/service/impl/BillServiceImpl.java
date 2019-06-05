package com.shanjin.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.IDownloadEvent;
import com.shanjin.financial.constant.Constants;
import com.shanjin.financial.service.BillService;
import com.shanjin.financial.util.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2016/7/18.
 */
public class BillServiceImpl implements BillService, IDownloadEvent {


    /**
     * 获取批量插入的数据库链接
     *
     * @return
     */
    public Connection getConnection() {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(this.getClass().getResource("/").getPath() + "/database.properties");
            props.load(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dbUrl = props.getProperty("jdbcUrl") + "&rewriteBatchedStatements=true";
        String userName = props.getProperty("user");
        String password = props.getProperty("password");
        String driverClass = props.getProperty("driverClass");
        Connection conn = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(dbUrl, userName, password);
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            return conn;
        }
        return conn;
    }

    /******************************************以下为微信账单代码*****************************************/
    String wx_appid = "wx08e6e8ad5f349d91";
    String wx_billType = "ALL";
    String wx_deviceInfo = "061123";
    String wx_mch_id = "1253386901";
    String wx_nonceStr = DateUtil.getCurrentTimeYYYYMMddHHMMssSSS();
    String wx_key = "562f50d8c68b6a1e27d36e7a3b92c34d";

    @Override
    public void saveBillWx(String billDate) {
        StringBuffer param = new StringBuffer();

        param.append("appid=" + wx_appid);
        param.append("&bill_date=" + billDate);
        param.append("&bill_type=" + wx_billType);
        param.append("&device_info=" + wx_deviceInfo);
        param.append("&mch_id=" + wx_mch_id);
        param.append("&nonce_str=" + wx_nonceStr);
        param.append("&key=" + wx_key);

        String sign = MD5Util.MD5_32(param.toString());

        StringBuffer xml = new StringBuffer();
        xml.append("<xml>\n");

        xml.append("\t<appid>" + wx_appid + "</appid>\n");
        xml.append("\t<bill_date>" + billDate + "</bill_date>\n");
        xml.append("\t<bill_type>" + wx_billType + "</bill_type>\n");
        xml.append("\t<device_info>" + wx_deviceInfo + "</device_info>\n");
        xml.append("\t<mch_id>" + wx_mch_id + "</mch_id>\n");
        xml.append("\t<nonce_str>" + wx_nonceStr + "</nonce_str>\n");
        xml.append("\t<sign>" + sign + "</sign>\n");
        xml.append("</xml>");

        PrintWriter out = null;
        BufferedReader in = null;
        try {
            URL url = new URL("https://api.mch.weixin.qq.com/pay/downloadbill");
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);

            out = new PrintWriter(conn.getOutputStream());
            out.print(xml.toString());
            out.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            List<String> billList = new ArrayList<>();
            while (in.ready()) {
                String line = in.readLine();
                billList.add(line);
            }

            //解析数据，组装JSON
            //获取详情
            JSONArray detailList = new JSONArray();
            for (int i = 1; i < billList.size() - 2; i++) {
                String data = billList.get(i);
                data = data.replaceAll("`", "");

                String[] arr = data.split(",");

                JSONObject obj = new JSONObject();
                obj.put("time", arr[0]);//交易时间
                obj.put("appId", arr[1]);//公众账号ID
                obj.put("mchId", arr[2]);//商户号
                obj.put("subMch", arr[3]);//子商户号
                obj.put("deviceId", arr[4]);//设备号
                obj.put("wxOrder", arr[5]);//微信订单号
                obj.put("bzOrder", arr[6]);//商户订单号
                obj.put("openId", arr[7]);//用户标识
                obj.put("tradeType", arr[8]);//交易类型
                obj.put("tradeStatus", arr[9]);//交易状态
                obj.put("bank", arr[10]);//付款银行
                obj.put("currency", arr[11]);//货币种类
                obj.put("totalMoney", arr[12]);//总金额
                obj.put("redPacketMoney", arr[13]);//企业红包总金额
                obj.put("commodityName", arr[14]);//商品名称
                obj.put("dataPacket", arr[15]);//商户数据包
                obj.put("fee", arr[16]);//手续费
                obj.put("rate", arr[17]);//费率

                detailList.add(obj);
            }

            //获取汇总信息
            String totalData = billList.get(billList.size() - 1);
            JSONObject totalObj = new JSONObject();

            totalData = totalData.replaceAll("`", "");
            String[] totalArr = totalData.split(",");
            totalObj.put("orderCount", totalArr[0]);//总交易单数
            totalObj.put("total", totalArr[1]);//总交易额
            totalObj.put("totalRefund", totalArr[2]);//总退款金额
            totalObj.put("totalRedPacketRefund", totalArr[3]);//总企业红包退款金额
            totalObj.put("totalFee", totalArr[4]);//手续费总金额

            JSONObject json = new JSONObject();
            json.put("list", detailList);
            json.put("total", totalObj);

            System.out.println(billDate + "日 微信对账单下载成功");

            //先清空数据
            Db.update("delete from bill_wx_day where bill_date=?", billDate);
            Db.update("delete from bill_wx_detail where bill_date=?", billDate);
            //保存详细数据
            batchWxBillDetail(detailList, billDate);
            //保存汇总数据
            Db.update("insert into bill_wx_day(bill_date, wx_num, wx_amount, create_time) values(?,?,?,?)", billDate, totalObj.get("orderCount"), totalObj.get("total"), DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date()));

            System.out.println(billDate + "日 微信对账单入库成功");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void batchWxBillDetail(List detailList, String billDate) {
        if (detailList.size() < 0) return;
        Connection conn = getConnection();
        if (conn == null) return;

        String sql = "INSERT INTO `bill_wx_detail`(wx_no,wx_user,wx_amount,wx_date,wx_status,bill_date,create_time) values(?,?,?,?,?,?,?)";
        String createTime = DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date());
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            int len = detailList.size();
            for (int i = 0; i < len; i++) {
                JSONObject obj = (JSONObject) detailList.get(i);
                pstmt.setString(1, obj.getString("wxOrder"));
                pstmt.setString(2, obj.getString("openId"));
                pstmt.setString(3, obj.getString("totalMoney"));
                pstmt.setString(4, obj.getString("time"));
                pstmt.setString(5, obj.getString("tradeStatus"));
                pstmt.setString(6, billDate);
                pstmt.setString(7, createTime);

                pstmt.addBatch();

                if (i % 50 == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            pstmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对账
     *
     * @param billDate 对账日期 yyyyMMdd
     */
    public void reconciliationWx(String billDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = sdf.parse(billDate);

            String beginDate = DateUtil.formatDate("yyyy-MM-dd", date) + " 00:00:00";
            String endDate = DateUtil.formatDate("yyyy-MM-dd", date) + " 23:59:59";

            String sql;

            List<Record> recList = new ArrayList<>();//微信-omeng对账数据
            //读出当前日期的对账数据
            List<Record> billDataList = Db.find("select id, wx_no from bill_wx_detail where bill_date=? and wx_status='SUCCESS'", billDate);
            //转换为HashMap
            Map<String, Record> billDataMap = new HashMap<>();
            for (Record record : billDataList) {
                String wx_no = record.get("wx_no") + "";
                billDataMap.put(wx_no, record);
            }
            //1. 订单支付
            StringBuffer sbSql = new StringBuffer("select id as omeng_no, pay_amount as money, third_trans_seq as trade_no, pay_time as omeng_date from w_income_order where order_pay_type=1 and pay_time between '" + beginDate + "' and '" + endDate + "'");
            long totals = Db.use("opay").queryLong("select count(*) from w_income_order where order_pay_type=1  and pay_time between '" + beginDate + "' and '" + endDate + "'");
            long pageSize = 50;
            long pageCount = totals / pageSize;
            if (totals % pageSize > 0) pageCount++;

            for (int i = 0; i < pageCount; i++) {
                long pageNumber = i * pageSize;

                sbSql.append(" LIMIT " + pageNumber);
                sbSql.append(",");
                sbSql.append(pageSize);

                List<Record> list = Db.use("opay").find(sbSql.toString());
                for (Record rec : list) {
                    String trade_no = rec.getStr("trade_no");
                    Record bill = billDataMap.get(trade_no);
                    if (bill == null) {
                        continue;
                    }
                    String wx_no = bill.getStr("wx_no");

                    if (wx_no.equals(trade_no)) {
                        bill.set("omeng_no", rec.getLong("omeng_no"));
                        bill.set("omeng_amount", rec.getBigDecimal("money"));
                        bill.set("omeng_type", "订单支付");
                        bill.set("omeng_date", rec.get("omeng_date"));

                        recList.add(bill);
                    }
                }
            }

            //2. 增值服务（VIP+抢单金+顾问号）
            sql = "SELECT id AS omeng_no, 'VIP' AS t, money as money, trade_no AS trade_no, apply_time as omeng_date FROM merchant_vip_apply WHERE apply_time between '" + beginDate + "' and '" + endDate + "' AND pay_type = 2\n";
            sql += "UNION ALL\n";
            sql += "SELECT id AS omeng_no, '顾问号' AS t, money as money, pay_no AS trade_no, apply_time as omeng_date FROM merchant_employees_num_apply WHERE apply_time  between '" + beginDate + "' and '" + endDate + "' AND pay_type = 2\n";
            sql += "UNION ALL\n";
            sql += "SELECT id AS omeng_no, '抢单金' AS t, topup_money as money, trade_no AS trade_no, apply_time as omeng_date FROM merchant_topup_apply WHERE apply_time  between '" + beginDate + "' and '" + endDate + "' AND pay_type = 2\n";

            List<Record> zzfwList = Db.find(sql);
            for (Record zzfw : zzfwList) {
                String trade_no = zzfw.getStr("trade_no");
                Record bill = billDataMap.get(trade_no);
                if (bill == null) {
                    continue;
                }
                String wx_no = bill.getStr("wx_no");

                if (wx_no.equals(trade_no)) {
                    bill.set("omeng_no", zzfw.getLong("omeng_no"));
                    bill.set("omeng_amount", zzfw.getBigDecimal("money"));
                    bill.set("omeng_type", zzfw.getStr("t"));
                    bill.set("omeng_date", zzfw.get("omeng_date"));

                    recList.add(bill);
                }

            }

            //3. 剪彩红包
//            sbSql = new StringBuffer("select id as omeng_no, pay_amount as money, third_trans_no as trade_no, pay_time as omeng_date from w_income_cutting where pay_type=1 and pay_time between '" + beginDate + "' and '" + endDate + "'");
//            totals = Db.use("opay").queryLong("select count(*) from w_income_cutting where pay_type=1  and pay_time between '" + beginDate + "' and '" + endDate + "'");
//            pageSize = 50;
//            pageCount = totals / pageSize;
//            if (totals % pageSize > 0) pageCount++;
//
//            for (int i = 0; i < pageCount; i++) {
//                long pageNumber = i * pageSize;
//
//                sbSql.append(" LIMIT " + pageNumber);
//                sbSql.append(",");
//                sbSql.append(pageSize);
//
//                List<Record> list = Db.use("opay").find(sbSql.toString());
//                for (Record rec : list) {
//                    String trade_no = rec.getStr("trade_no");
//                    Record bill = billDataMap.get(trade_no);
//                    if (bill == null) {
//                        continue;
//                    }
//                    String wx_no = bill.getStr("wx_no");
//
//                    if (wx_no.equals(trade_no)) {
//                        bill.set("omeng_no", rec.getLong("omeng_no"));
//                        bill.set("omeng_amount", rec.getBigDecimal("money"));
//                        bill.set("omeng_type", "剪彩红包");
//                        bill.set("omeng_date", rec.get("omeng_date"));
//
//                        recList.add(bill);
//                    }
//                }
//            }
            sbSql = new StringBuffer("select id as omeng_no, price as money, join_time as omeng_date, merchant_id as omeng_merchant from activity_cutting_detail where join_time between '" + beginDate + "' and '" + endDate + "'");
            List<Record> jchbList = Db.find(sbSql.toString());
            for (Record record : jchbList) {
                String trade_no = record.getStr("trade_no");
                Record bill = billDataMap.get(trade_no);
                if (bill == null) {
                    continue;
                }
                String wx_no = bill.getStr("wx_no");

                if (wx_no.equals(trade_no)) {
                    bill.set("omeng_no", record.getLong("omeng_no"));
                    bill.set("omeng_amount", record.getBigDecimal("money"));
                    bill.set("omeng_type", record.getStr("t"));
                    bill.set("omeng_date", record.get("omeng_date"));

                    recList.add(bill);
                }
            }


            //4. 更新详细数据
            batchReconciliationWx(recList);
            Db.update("UPDATE bill_wx_detail SET result = (wx_amount = IFNULL(omeng_amount, 0)) WHERE bill_date=?", billDate);

            //5. 更新汇总数据
            Record totalRecord = Db.find("select sum(omeng_amount) as amount, count(*) as c from bill_wx_detail where omeng_no is not null and omeng_amount is not null and bill_date=?", billDate).get(0);
            String totalSql = "update bill_wx_day set omeng_num = ?, omeng_amount=? where bill_date=?";
            Db.update(totalSql, totalRecord.get("c"), StringUtil.nullToFloat(totalRecord.get("amount")), billDate);
            Db.update("UPDATE bill_wx_day SET result = (wx_num = omeng_num AND wx_amount = omeng_amount) WHERE bill_date=?", billDate);

            System.out.println(billDate + "日 微信对账单对账结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void batchReconciliationWx(List<Record> recordList) {
        if (recordList.size() == 0) return;
        Connection conn = getConnection();
        try {

            PreparedStatement pstmt = conn.prepareStatement("update bill_wx_detail set omeng_no=?, omeng_amount=?, omeng_type=?, omeng_date=? where id=?");
            for (int i = 0; i < recordList.size(); i++) {
                Record rec = recordList.get(i);
                pstmt.setString(1, rec.getLong("omeng_no") + "");
                pstmt.setBigDecimal(2, rec.getBigDecimal("omeng_amount"));
                pstmt.setString(3, rec.getStr("omeng_type"));
                pstmt.setString(4, rec.get("omeng_date")+"");
                pstmt.setLong(5, rec.getLong("id"));

                pstmt.addBatch();

                if (i % 50 == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    /******************************************以下为支付宝账单代码*****************************************/
    String alipay_apiUrl = "https://openapi.alipay.com/gateway.do";
    String alipay_appId = "2015082000223693";
    String alipay_privateKey = "MIICdwIBADANBgkqhkiG9wBAQEFAASCAmEwggJdAgEAAoGBAKkJ6FulrT2XvCnXg50DMz9jI9SyDEDJipxA0Kow1MQIxvUbrbJTcdh8PpQTsTnDpGjimEbAblHKzK1VjmpfWzZ9RfUiaQ+WDRlh7G5Cpj6DJsk+diqDQd/ukuiHRcDBi9tcqqDweYt732QJc8lN6fPHLIbhIIPDMcUByQddK7QlAgMBAAECgYBC62oyhgdsf9p9EhXHnhG2wW/Y71fMej1GmJ5DEivZI8RoGroMA97pHl7Dznv16072Ouaf8+R4uvmGkX1c/T0lUzHmP1CQv4yx/mp8xignSJtZaTRA93tq3g0fjgHMVJkOU9j5GFxQtRJezC19PoX+4i1FZbirPXSrspEPGE+oTQJBAN1dLbpHJ6bQb6rKbUliiTNRP+3QtXC82+aEFFp+yC/UWn2I4IRv25Au1SYpCO1KMURUIuWUdedlnhyoKkta3acCQQDDfNHGX8+kCTX+aFB50BUDtrEfffpHoUgFQXrqoA1V3A+8u4AOXblXALCHbVnZWskIEX7IEfW+ChXzdSu3TFFTAkEAtpTPTbC41M9g+2bhg0Dh11DxwM5/iRBM9DIGs6mUplapmJdYUAQO/jqSlloMQeQLBMe8zM2J/iUDp7FQyTyWSwJARJpOJ9bB0Kgm2aQT8duzND1txUZ5iZ+w3Z9QGnyWkXYL08jdNK1xeHXWfYBDksKIYBt7qYyb99gkQe7xq37N3wJBAJHbBmN4KpoA9tLA7BEwQ+Y2CauMhtAwOloaYiUHXm/KE2lVUOSbtzeV6SydAUX3G6NbGRUMLWeks9MAFFxg7fI=";
    String alipay_format = "json";
    String alipay_charset = "GBK";
    String alipay_publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    String alipay_billType = "trade";
    String alipay_billDate = "";// 交易日期

    @Override
    public synchronized void saveBillAlipay(String day) {
        alipay_billDate = day;
        //1. 下载支付宝账单文件
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            alipay_charset = "GBK";
        } else if (System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
            alipay_charset = "UTF-8";
        }

        if (StringUtil.isNullStr(alipay_billDate)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -1); // 默认昨日账单
            Date yesterday = calendar.getTime();
            alipay_billDate = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, yesterday);
        }

        AlipayClient alipayClient = new DefaultAlipayClient(alipay_apiUrl, alipay_appId, alipay_privateKey, alipay_format, alipay_charset, alipay_publicKey);
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        Map<String, String> content = new HashMap<String, String>();
        content.put("bill_type", alipay_billType);
        content.put("bill_date", alipay_billDate);
        request.setBizContent(JsonUtil.map2json(content));
        String downloadurl = "";
        String fileName = "";
        try {
            AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
            JSONObject jsonObject = JSON.parseObject(response.getBody());
            JSONObject bill = (JSONObject) jsonObject.get("alipay_data_dataservice_bill_downloadurl_query_response");
            downloadurl = bill.getString("bill_download_url");
            String[] paras = downloadurl.split("&");
            for (String para : paras) {
                if (para.contains("downloadFileName")) {
                    fileName = para.split("=")[1];
                }
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        try {
            DownloadUtil.downLoadFromUrl(downloadurl, fileName, Constants.ali_bill_file_path, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取
     *
     * @param filepath zip文件路径
     */
    private JSONObject readAlipay(String filepath) {
        JSONObject result = new JSONObject();

        //1. 解压zip文件
        File srcFile = new File(filepath);
        String destDir = srcFile.getParent() + "/alipay_bill/";
        UnZipUtil.unZip(filepath, destDir);


        //读取明细
        List<String> billList = new ArrayList<>();

        filepath = destDir + srcFile.getName().replace(".csv.zip", "_业务明细.csv");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "GBK"));

            while (reader.ready()) {
                billList.add(reader.readLine());
            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (billList.size() > 9) {
            JSONArray array = new JSONArray();

            // 解析成JSON格式
            for (int i = 5; i < billList.size() - 4; i++) {
                String[] arr = billList.get(i).split(",");

                JSONObject obj = new JSONObject();

                obj.put("alipayOrder", arr[0].trim()); //支付宝交易号
                obj.put("bzOrder", arr[1].trim());// 商户订单号
                obj.put("tradeType", arr[2].trim());// 业务类型
                obj.put("goods", arr[3].trim());// 商品名称
                obj.put("createTime", arr[4].trim());// 创建时间
                obj.put("completeTime", arr[5].trim());// 完成时间
                obj.put("shopNo", arr[6].trim());// 门店编号
                obj.put("shopName", arr[7].trim());// 门店名称
                obj.put("operator", arr[8].trim());// 操作员
                obj.put("deviceNo", arr[9].trim());// 终端号
                obj.put("account", arr[10].trim());// 对方账户
                obj.put("amount", arr[11].trim());// 订单金额
                obj.put("received", arr[12].trim());// 商家实收
                obj.put("zfbhb", arr[13].trim());// 支付宝红包（元）
                obj.put("jfb", arr[14].trim());// 集分宝（元）
                obj.put("zfbyh", arr[15].trim());// 支付宝优惠（元）
                obj.put("sjyh", arr[16].trim());// 商家优惠（元）
                obj.put("qhxje", arr[17].trim());// 券核销金额（元）
                obj.put("qmc", arr[18].trim());// 券名称
                obj.put("sjhbxfje", arr[19].trim());// 商家红包消费金额（元）
                obj.put("kxfje", arr[20].trim());// 卡消费金额（元）
                obj.put("refundNo", arr[21].trim());// 退款批次号/请求号
                obj.put("fwf", arr[22].trim());// 服务费（元）
                obj.put("fr", arr[23].trim());// 分润（元）
                obj.put("bz", arr[24].trim());// 备注

                array.add(obj);
            }

            result.put("list", array);
        }

        //读取汇总表
        List<String> totalBillList = new ArrayList<>();
        filepath = destDir + srcFile.getName().replace(".csv.zip", "_业务明细(汇总).csv");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "GBK"));

            while (reader.ready()) {
                totalBillList.add(reader.readLine());
            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (totalBillList.size() >= 9) {
            String line = totalBillList.get(6);

            String[] arr = line.split(",");

            JSONObject total = new JSONObject();

            total.put("shopNo", arr[0].trim());// 门店编号
            total.put("shopName", arr[1].trim());// 门店名称
            total.put("orderCount", arr[2].trim());// 交易订单总笔数
            total.put("refundOrderCount", arr[3].trim());// 退款订单总笔数
            total.put("orderAmount", arr[4].trim());// 订单金额（元）
            total.put("orderReceived", arr[5].trim());// 商家实收（元）
            total.put("zfbyh", arr[6].trim());// 支付宝优惠（元）
            total.put("sjhy", arr[7].trim());// 商家优惠（元）
            total.put("kxfje", arr[8].trim());// 卡消费金额（元）
            total.put("fwf", arr[9].trim());// 服务费（元）
            total.put("fr", arr[10].trim());// 分润（元）
            total.put("received", arr[11].trim());// 实收净额（元）

            result.put("total", total);
        }

        return result;
    }

    /**
     * 下载完成回调函数
     *
     * @param filepath
     */
    @Override
    public void complete(String filepath) {
        System.out.println(alipay_billDate + "日 支付宝对账单下载成功");

        JSONObject result = readAlipay(filepath);
        // 保存到数据库
        //先清空数据
        Db.update("delete from bill_ali_day where bill_date=?", alipay_billDate);
        Db.update("delete from bill_ali_detail where bill_date=?", alipay_billDate);
        //保存详细数据
        JSONArray detailList = result.getJSONArray("list");
        batchAlipayBillDetail(detailList, alipay_billDate);
        //保存汇总数据
        JSONObject totalObj = result.getJSONObject("total");
        Db.update("insert into bill_ali_day(bill_date, ali_num, ali_amount, create_time) values(?,?,?,?)", alipay_billDate, totalObj.get("orderCount"), totalObj.get("orderReceived"), DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date()));

        System.out.println(alipay_billDate + "日 支付宝对账单入库成功");
    }

    private void batchAlipayBillDetail(List detailList, String billDate) {
        if (detailList.size() < 0) return;
        Connection conn = getConnection();
        if (conn == null) return;

        String sql = "INSERT INTO `bill_ali_detail`(ali_no,ali_user,ali_amount,ali_date,ali_status,bill_date,create_time) values(?,?,?,?,?,?,?)";
        String createTime = DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date());
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            int len = detailList.size();
            for (int i = 0; i < len; i++) {
                JSONObject obj = ((JSONObject) detailList.get(i));
                pstmt.setString(1, obj.getString("alipayOrder"));
                pstmt.setString(2, obj.getString("account"));
                pstmt.setString(3, obj.getString("received"));
                pstmt.setString(4, obj.getString("completeTime"));
                pstmt.setString(5, obj.getString("tradeType"));
                pstmt.setString(6, billDate);
                pstmt.setString(7, createTime);

                pstmt.addBatch();

                if (i % 50 == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            pstmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 支付宝对账
     *
     * @param billDate 对账日期
     */
    @Override
    public void reconciliationAlipay(String billDate) {
        try {
            String beginDate = billDate + " 00:00:00";
            String endDate = billDate + " 23:59:59";

            String sql;
            List<Record> recList = new ArrayList<>();//微信-omeng对账数据
            //读出当前日期的对账数据
            List<Record> billDataList = Db.find("select id, ali_no from bill_ali_detail where bill_date=? and ali_status='交易'", billDate);
            //转换为HashMap
            Map<String, Record> billDataMap = new HashMap<>();
            for (Record record : billDataList) {
                String ali_no = record.get("ali_no") + "";
                billDataMap.put(ali_no, record);
            }
            //1. 订单支付
            StringBuffer sbSql = null;
            //分页读取
            long totals = Db.use("opay").queryLong("select count(*) from w_income_order where order_pay_type=2  and pay_time between '" + beginDate + "' and '" + endDate + "'");
            long pageSize = 50;
            long pageCount = totals / pageSize;
            if (totals % pageSize > 0) pageCount++;
            for (int i = 0; i < pageCount; i++) {
                long pageNumber = i * pageSize;

                sbSql = new StringBuffer("select id as omeng_no, pay_amount as money, third_trans_seq as trade_no, pay_time as omeng_date from w_income_order where order_pay_type=2 and pay_time between '" + beginDate + "' and '" + endDate + "'");
                sbSql.append(" LIMIT " + pageNumber);
                sbSql.append(",");
                sbSql.append(pageSize);

                List<Record> list = Db.use("opay").find(sbSql.toString());
                for (Record rec : list) {
                    String trade_no = rec.getStr("trade_no");
                    Record bill = billDataMap.get(trade_no);
                    if (bill == null) {
                        continue;
                    }
                    String wx_no = bill.getStr("ali_no");

                    if (wx_no.equals(trade_no)) {
                        bill.set("omeng_no", rec.getLong("omeng_no"));
                        bill.set("omeng_amount", rec.getBigDecimal("money"));
                        bill.set("omeng_type", rec.getStr("t"));
                        bill.set("omeng_date", rec.get("omeng_date"));

                        recList.add(bill);
                    }
                }
            }

            //2. 增值服务（VIP+抢单金+顾问号）
            sql = "SELECT id AS omeng_no, 'VIP' AS t, money as money, trade_no AS trade_no, apply_time as omeng_date FROM merchant_vip_apply WHERE apply_time between '" + beginDate + "' and '" + endDate + "' AND pay_type = 2\n";
            sql += "UNION ALL\n";
            sql += "SELECT id AS omeng_no, '顾问号' AS t, money as money, pay_no AS trade_no, apply_time as omeng_date FROM merchant_employees_num_apply WHERE apply_time  between '" + beginDate + "' and '" + endDate + "' AND pay_type = 2\n";
            sql += "UNION ALL\n";
            sql += "SELECT id AS omeng_no, '抢单金' AS t, topup_money as money, trade_no AS trade_no, apply_time as omeng_date FROM merchant_topup_apply WHERE apply_time  between '" + beginDate + "' and '" + endDate + "' AND pay_type = 2\n";

            List<Record> zzfwList = Db.find(sql);
            for (Record zzfw : zzfwList) {
                String trade_no = zzfw.getStr("trade_no");
                Record bill = billDataMap.get(trade_no);
                if (bill == null) {
                    continue;
                }
                String wx_no = bill.getStr("ali_no");

                if (wx_no.equals(trade_no)) {
                    bill.set("omeng_no", zzfw.getLong("omeng_no"));
                    bill.set("omeng_amount", zzfw.getBigDecimal("money"));
                    bill.set("omeng_type", zzfw.getStr("t"));
                    bill.set("omeng_date", zzfw.get("omeng_date"));

                    recList.add(bill);
                }

            }

            //3. 剪彩红包
//            sbSql = new StringBuffer("select id as omeng_no, pay_amount as money, third_trans_seq as trade_no, pay_time as omeng_date from w_income_cutting where pay_type=2 and pay_time between '" + beginDate + "' and '" + endDate + "'");
//            totals = Db.use("opay").queryLong("select count(*) from w_income_cutting where pay_type=2  and pay_time between '" + beginDate + "' and '" + endDate + "'");
//            pageSize = 50;
//            pageCount = totals / pageSize;
//            if (totals % pageSize > 0) pageCount++;
//
//            for (int i = 0; i < pageCount; i++) {
//                long pageNumber = i * pageSize;
//
//                sbSql.append(" LIMIT " + pageNumber);
//                sbSql.append(",");
//                sbSql.append(pageSize);
//
//                List<Record> list = Db.use("opay").find(sbSql.toString());
//                for (Record rec : list) {
//                    String trade_no = rec.getStr("trade_no");
//                    Record bill = billDataMap.get(trade_no);
//                    if (bill == null) {
//                        continue;
//                    }
//                    String wx_no = bill.getStr("wx_no");
//
//                    if (wx_no.equals(trade_no)) {
//                        bill.set("omeng_no", rec.getLong("omeng_no"));
//                        bill.set("omeng_amount", rec.getBigDecimal("money"));
//                        bill.set("omeng_type", "剪彩红包");
//                        bill.set("omeng_date", rec.get("omeng_date"));
//
//                        recList.add(bill);
//                    }
//
//                }
//            }
            sbSql = new StringBuffer("select id as omeng_no, price as money, join_time as omeng_date, merchant_id as omeng_merchant from activity_cutting_detail where join_time between '" + beginDate + "' and '" + endDate + "'");
            List<Record> jchbList = Db.find(sbSql.toString());
            for (Record record : jchbList) {
                String trade_no = record.getStr("trade_no");
                Record bill = billDataMap.get(trade_no);
                if (bill == null) {
                    continue;
                }
                String ali_no = bill.getStr("ali_no");

                if (ali_no.equals(trade_no)) {
                    bill.set("omeng_no", record.getLong("omeng_no"));
                    bill.set("omeng_amount", record.getBigDecimal("money"));
                    bill.set("omeng_type", record.getStr("t"));
                    bill.set("omeng_date", record.get("omeng_date"));

                    recList.add(bill);
                }
            }


            //4. 更新详细数据
            batchReconciliationAlipay(recList);
            Db.update("UPDATE bill_ali_detail SET result = (wx_amount = IFNULL(omeng_amount, 0)) WHERE bill_date=?", billDate);

            //5. 更新汇总数据
            Record totalRecord = Db.find("select sum(omeng_amount) as amount, count(*) as c from bill_ali_detail where omeng_no is not null and omeng_amount is not null and bill_date=?", billDate).get(0);
            String totalSql = "update bill_ali_day set omeng_num = ?, omeng_amount=? where bill_date=?";
            Db.update(totalSql, totalRecord.get("c"), StringUtil.nullToFloat(totalRecord.get("amount")), billDate);
            Db.update("UPDATE bill_ali_day SET result = (ali_num = omeng_num AND ali_amount = omeng_amount) WHERE bill_date=?", billDate);

            System.out.println(billDate + "日 支付宝对账单对账结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void batchReconciliationAlipay(List<Record> recordList) {
        if (recordList.size() == 0) return;
        Connection conn = getConnection();
        try {

            PreparedStatement pstmt = conn.prepareStatement("update bill_alipay_detail set omeng_no=?, omeng_amount=?, omeng_type=?, omeng_date=? where id=?");
            for (int i = 0; i < recordList.size(); i++) {
                Record rec = recordList.get(i);
                pstmt.setString(1, rec.getLong("omeng_no") + "");
                pstmt.setBigDecimal(2, rec.getBigDecimal("omeng_amount"));
                pstmt.setString(3, rec.getStr("omeng_type"));
                pstmt.setString(4, rec.get("omeng_date")+"");
                pstmt.setLong(5, rec.getLong("id"));

                pstmt.addBatch();

                if (i % 50 == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }


    /******************************************以下为钱包账单代码*****************************************/
    public void saveWallet(String billDate) {
        try {
            String beginDate = billDate + " 00:00:00";
            String endDate = billDate + " 23:59:59";
            Db.update("delete from bill_wallet_day where bill_date=?", billDate);
            Db.update("delete from bill_wallet_detail where bill_date=?", billDate);
            //保存数据
            List<JSONObject> detailList = new ArrayList<>();

            StringBuffer sbSql = new StringBuffer();

            long totals = Db.use("opay").queryLong("select count(*) from w_bill where bill_create_time between '" + beginDate + "' and '" + endDate + "' and bill_type <=4");
            long pageSize = 50;
            long pageCount = totals / pageSize;
            if (totals % pageSize > 0) pageCount++;
            for (int i = 0; i < pageCount; i++) {
                long pageNumber = i * pageSize;

                sbSql = new StringBuffer("SELECT bill_user_id, bill_type, bill_amount, bill_status, bill_trans_seq, source_trans_seq, bill_create_time FROM w_bill" +
                        " where bill_create_time between '" + beginDate + "' and '" + endDate + "' and bill_type <=4");
                sbSql.append(" LIMIT " + pageNumber);
                sbSql.append(",");
                sbSql.append(pageSize);

                List<Record> list = Db.use("opay").find(sbSql.toString());

                for (Record record : list) {
                    JSONObject obj = new JSONObject();

                    obj.put("walletOrder", record.getStr("bill_trans_seq"));
                    obj.put("account", record.getLong("bill_user_id").toString());
                    obj.put("received", record.getBigDecimal("bill_amount").doubleValue() + "");
                    obj.put("completeTime", record.getTimestamp("bill_create_time"));
                    obj.put("tradeType", record.getInt("bill_status"));
                    obj.put("omengNo", record.getStr("source_trans_seq"));
                    String omengType = "";
                    switch (record.getInt("bill_type")) {
                        case 1:
                            omengType = "订单收入";
                            break;
                        case 2:
                            omengType = "订单奖励";
                            break;
                        case 3:
                            omengType = "活动奖励";
                            break;
                        case 4:
                            omengType = "剪彩红包";
                            break;
                    }
                    obj.put("omengType", omengType);

                    detailList.add(obj);
                }

                batchWalletBillDetail(detailList, billDate);
                detailList.clear();
            }

            //保存汇总数据
            Record totalRecord = Db.find("select count(*) as totals , sum(wallet_amount) as prices from bill_wallet_detail where wallet_date between '" + beginDate + "' and '" + endDate + "'").get(0);
            Db.update("insert into bill_wallet_day(bill_date, wallet_num, wallet_amount, create_time) values(?,?,?,?)", billDate, totalRecord.get("totals"), totalRecord.get("prices"), DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void batchWalletBillDetail(List detailList, String billDate) {
        if (detailList.size() < 0) return;
        Connection conn = getConnection();
        if (conn == null) return;

        String sql = "INSERT INTO `bill_wallet_detail`(wallet_no,wallet_user,wallet_amount,wallet_date,wallet_status,bill_date,create_time, omeng_type, omeng_no) values(?,?,?,?,?,?,?,?,?)";
        String createTime = DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date());
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            int len = detailList.size();
            for (int i = 0; i < len; i++) {
                JSONObject obj = ((JSONObject) detailList.get(i));
                pstmt.setString(1, obj.getString("walletOrder"));
                pstmt.setString(2, obj.getString("account"));
                pstmt.setString(3, obj.getString("received"));
                pstmt.setString(4, obj.getString("completeTime"));
                pstmt.setString(5, obj.getString("tradeType"));
                pstmt.setString(6, billDate);
                pstmt.setString(7, createTime);
                pstmt.setString(8, obj.getString("omengType"));
                pstmt.setString(9, obj.getString("omengNo"));

                pstmt.addBatch();

                if (i % 50 == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            pstmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 钱包对账
     *
     * @param billDate
     */
    @Override
    public void reconciliationWallet(String billDate) {
        try {
            String beginDate = billDate + " 00:00:00";
            String endDate = billDate + " 23:59:59";
            String sql;
            List<Record> recList = new ArrayList<>();//微信-omeng对账数据
            //读出当前日期的对账数据
            List<Record> billDataList = Db.find("select id, omeng_no, omeng_type from bill_wallet_detail where bill_date=? and wallet_status='2'", billDate);
            //转换为HashMap
            Map<String, Record> billDataMap = new HashMap<>();
            for (Record record : billDataList) {
                String wallet_omeng_no = record.get("omeng_no") + "";
                billDataMap.put(wallet_omeng_no, record);
            }
            //1. 订单支付
            StringBuffer sbSql = new StringBuffer("select id as omeng_no, order_actual_price as money, deal_time as omeng_date, merchant_id as omeng_merchant from order_info where deal_time between '" + beginDate + "' and '" + endDate + "'");
            List<Record> ddzfList = Db.find(sbSql.toString());
            for (Record record : ddzfList) {
                Long omeng_no = record.getLong("omeng_no");
                Record bill = billDataMap.get(omeng_no + "");
                if (bill == null) {
                    continue;
                }
                String wallet_omeng_no = bill.get("omeng_no") + "";
                String omeng_type = bill.get("omeng_type") + "";

                if (wallet_omeng_no.equals(omeng_no + "") && "订单收入".equals(omeng_type)) {
                    bill.set("omeng_no", record.getLong("omeng_no"));
                    bill.set("omeng_amount", record.getBigDecimal("money"));
                    bill.set("omeng_type", "订单收入");
                    bill.set("omeng_date", record.getTimestamp("omeng_date") + "");
                    bill.set("omeng_merchant", record.get("omeng_merchant") + "");

                    recList.add(bill);
                }
            }
            //2. 订单奖励
            sbSql = new StringBuffer("select id as omeng_no, pay_price as money, pay_time as omeng_date, merchant_id as omeng_merchant from activity_order_reward_detail where pay_time between '" + beginDate + "' and '" + endDate + "'");
            List<Record> ddjlList = Db.find(sbSql.toString());
            for (Record record : ddjlList) {
                Long omeng_no = record.getLong("omeng_no");
                Record bill = billDataMap.get(omeng_no + "");
                if (bill == null) {
                    continue;
                }
                String wallet_omeng_no = bill.getStr("omeng_no");
                String omeng_type = bill.get("omeng_type") + "";

                if (wallet_omeng_no.equals(omeng_no + "") && "订单奖励".equals(omeng_type)) {
                    bill.set("omeng_no", record.getLong("omeng_no"));
                    bill.set("omeng_amount", record.getBigDecimal("money"));
                    bill.set("omeng_type", "订单奖励");
                    bill.set("omeng_date", record.getTimestamp("omeng_date") + "");
                    bill.set("omeng_merchant", record.get("omeng_merchant") + "");

                    recList.add(bill);
                }
            }
            //3. 活动奖励
            sbSql = new StringBuffer("select id as omeng_no, fans_price as money, pay_time as omeng_date, merchant_id as omeng_merchant from activity_fensi_payment_detail where pay_time between '" + beginDate + "' and '" + endDate + "'");
            List<Record> hdjlList = Db.find(sbSql.toString());
            for (Record record : hdjlList) {
                Long omeng_no = record.getLong("omeng_no");
                Record bill = billDataMap.get(omeng_no + "");
                if (bill == null) {
                    continue;
                }
                String wallet_omeng_no = bill.get("omeng_no") + "";
                String omeng_type = bill.get("omeng_type") + "";

                if (wallet_omeng_no.equals(omeng_no + "") && "活动奖励".equals(omeng_type)) {
                    bill.set("omeng_no", record.getLong("omeng_no"));
                    bill.set("omeng_amount", record.getBigDecimal("money"));
                    bill.set("omeng_type", "活动奖励");
                    bill.set("omeng_date", record.getTimestamp("omeng_date") + "");
                    bill.set("omeng_merchant", record.get("omeng_merchant") + "");

                    recList.add(bill);
                }

            }
            //4. 剪彩红包
            sbSql = new StringBuffer("select id as omeng_no, price as money, join_time as omeng_date, merchant_id as omeng_merchant from activity_cutting_detail where join_time between '" + beginDate + "' and '" + endDate + "'");
//            sbSql = new StringBuffer("SELECT b.id AS omeng_no, a.price AS money, a.join_time AS omeng_date, a.merchant_id AS omeng_merchant FROM activity_cutting_detail AS a\n" +
//                    "LEFT JOIN merchant_payment_details AS b ON a.`id` = b.business_id\n" +
//                    " WHERE a.join_time BETWEEN '"+beginDate+"' AND '"+endDate+"'\n");
            List<Record> jchbList = Db.find(sbSql.toString());
            for (Record record : jchbList) {
                Long omeng_no = record.getLong("omeng_no");
                Record bill = billDataMap.get(omeng_no + "");
                if (bill == null) {
                    continue;
                }
                String wallet_omeng_no = bill.get("omeng_no") + "";

                String omeng_type = bill.get("omeng_type") + "";

                if (wallet_omeng_no.equals(omeng_no + "") && "剪彩红包".equals(omeng_type)) {
                    bill.set("omeng_no", record.getLong("omeng_no"));
                    bill.set("omeng_amount", record.getBigDecimal("money"));
                    bill.set("omeng_type", "剪彩红包");
                    bill.set("omeng_date", record.getTimestamp("omeng_date") + "");
                    bill.set("omeng_merchant", record.get("omeng_merchant") + "");

                    recList.add(bill);
                }
            }


            //4. 更新详细数据
            batchReconciliationWallet(recList);
            Db.update("UPDATE bill_wallet_detail SET result = (wallet_amount = IFNULL(omeng_amount, 0)) WHERE bill_date=?", billDate);

            //5. 更新汇总数据
            Record totalRecord = Db.find("select sum(omeng_amount) as amount, count(*) as c from bill_wallet_detail where omeng_no is not null and omeng_amount is not null and bill_date=?", billDate).get(0);
            String totalSql = "update bill_wallet_day set omeng_num = ?, omeng_amount=? where bill_date=?";
            Db.update(totalSql, totalRecord.get("c"), StringUtil.nullToFloat(totalRecord.get("amount")), billDate);
            Db.update("UPDATE bill_wallet_day SET result = IFNULL((wallet_num = omeng_num AND wallet_amount = omeng_amount), 0) WHERE bill_date=?", billDate);

            //6. 汇总钱包用户数据
            //6.1 判断是否是第一次
            long num = Db.queryLong("select count(*) from bill_wallet_user where bill_date=?", billDate);
            if (num == 0) {//第一次
                sbSql = new StringBuffer("INSERT INTO bill_wallet_user(wallet_user_id,bill_date,wallet_amount,wallet_num,omeng_amount,omeng_num,result, create_time) \n" +
                        "SELECT  wallet_user AS wallet_user_id, bill_date, \n" +
                        "SUM(wallet_amount) AS wallet_amount, COUNT(wallet_amount) wallet_num, \n" +
                        "SUM(omeng_amount) AS omeng_amount, COUNT(omeng_amount) AS omeng_num, \n" +
                        "IFNULL((wallet_amount = omeng_amount),0) AS result, \n" +
                        "NOW() AS create_time\n" +
                        "FROM bill_wallet_detail where bill_date=? GROUP BY wallet_user");
                Db.update(sbSql.toString(), billDate);
                //更新批次号
                sbSql = new StringBuffer("update bill_wallet_user set batch_no = ? where bill_date=?");
                Db.update(sbSql.toString(), UUID.randomUUID().toString().substring(0, 8), billDate);

                //判断是不是全平
                long sNum = Db.queryLong("select count(*) from bill_wallet_user where bill_date=? and result=0", billDate);
                if (sNum > 0) {//有部分不平
                    // 通知不平接口
                    sendWalletNotify(1, 0, billDate, null);
                } else {//全平
                    // 通知全平接口
                    sendWalletNotify(1, 1, billDate, null);
                }
            } else {//第二次
                sbSql = new StringBuffer("UPDATE bill_wallet_user AS a RIGHT JOIN (\n" +
                        "SELECT  wallet_user AS wallet_user_id, bill_date, \n" +
                        "SUM(wallet_amount) AS wallet_amount, COUNT(wallet_amount) wallet_num, \n" +
                        "SUM(omeng_amount) AS omeng_amount, COUNT(omeng_amount) AS omeng_num, \n" +
                        "IFNULL((wallet_amount = omeng_amount),0) AS result, \n" +
                        "NOW() AS create_time\n" +
                        "FROM bill_wallet_detail WHERE IFNULL((wallet_amount = omeng_amount),0)=1 and bill_date=?GROUP BY wallet_user \n" +
                        ") AS b ON a.wallet_user_id = b.wallet_user_id\n" +
                        "SET a.result=2, a.wallet_amount=b.wallet_amount,a.wallet_num=b.wallet_num, a.omeng_amount=b.omeng_amount, a.omeng_num=b.omeng_num\n" +
                        "WHERE a.result = 0 and a.bill_date=?");
                int updateNum = Db.update(sbSql.toString(), billDate, billDate);
                if (updateNum > 0) { //有部分恢复
                    //更新批次号
                    String batchNo = UUID.randomUUID().toString().substring(0, 8);
                    sbSql = new StringBuffer("update bill_wallet_user set batch_no = ?, result = 1 where bill_date=? and result = 2");
                    Db.update(sbSql.toString(), batchNo, billDate);

                    //通知
                    // 等待二次对账 恢复用户账户状态接口
                    sendWalletNotify(2, 1, billDate, batchNo);
                }
            }

            System.out.println(billDate + "日 钱包对账结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void batchReconciliationWallet(List<Record> recordList) {
        if (recordList.size() == 0) return;
        Connection conn = getConnection();
        try {

            PreparedStatement pstmt = conn.prepareStatement("update bill_wallet_detail set omeng_no=?, omeng_amount=?, omeng_type=?, omeng_date=?, omeng_merchant=? where id=?");
            for (int i = 0; i < recordList.size(); i++) {
                Record rec = recordList.get(i);
                pstmt.setString(1, rec.getLong("omeng_no") + "");
                pstmt.setBigDecimal(2, rec.getBigDecimal("omeng_amount"));
                pstmt.setString(3, rec.getStr("omeng_type"));
                pstmt.setString(4, rec.getStr("omeng_date"));
                pstmt.setString(5, rec.getStr("omeng_merchant"));
                pstmt.setLong(6, rec.getLong("id"));

                pstmt.addBatch();

                if (i % 50 == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendWalletNotify(final int type, final int result, final String billDate, final String batchNo) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String baseUrl = PropUtil.getPropUtil("/api.properties").getProperty("opay.wallet.url");
                String key = PropUtil.getPropUtil("/api.properties").getProperty("opay.wallet.key");
                String notifyUrl = baseUrl + "/financialBackStage/reconciliationInform";
                String revUrl = baseUrl + "/financialBackStage/userAccountBackNormalInform";

                String requestUrl = "";
                JSONObject param = new JSONObject();
                if (type == 1) {// 第一次对账通知
                    param.put("dateTime", billDate);
                    param.put("resultCode", result + "");

                    requestUrl = notifyUrl;
                } else {//恢复对账
                    param.put("dateTime", billDate);
                    param.put("batchNo", batchNo);

                    requestUrl = revUrl;
                }
                try {
                    Map<String, String> headerMap = new HashMap<>();
                    headerMap.put("APIVERSION", "1.0");
                    String encryptedParams = AESUtil.parseByte2HexStr(AESUtil.encrypt(param.toJSONString(), key));
                    int count = 1;
                    while (count < 10) {
                        String response = HttpRequest.sendPost(requestUrl, headerMap, "encryptedParams=" + encryptedParams);
                        try {
                            JSONObject json = JSONObject.parseObject(response);
                            String code = json.getString("resultCode");
                            if (code != null && code.equals("000")) {
                                System.out.println("【opay.wallet】 接口调用成功！");
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println("【opay.wallet】 接口调用错误，稍后重试(" + count + ")");
                            e.printStackTrace();
                            try {
                                Thread.sleep(5000 * count);// 等待5秒
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        count++;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
}
