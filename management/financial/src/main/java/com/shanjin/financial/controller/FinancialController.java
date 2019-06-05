package com.shanjin.financial.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.*;
import com.shanjin.financial.service.BillService;
import com.shanjin.financial.service.ExportService;
import com.shanjin.financial.service.FinancialService;
import com.shanjin.financial.service.impl.BillServiceImpl;
import com.shanjin.financial.service.impl.FinancialServiceImpl;
import com.shanjin.financial.util.AESUtil;
import com.shanjin.financial.util.DateUtil;
import com.shanjin.financial.util.ExcelExportUtil;
import com.shanjin.financial.util.PropUtil;
import com.shanjin.sso.bean.SystemUserInfo;
import com.shanjin.sso.bean.UserSession;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2016/7/4.
 */
public class FinancialController extends Controller {
    private FinancialService financialService = new FinancialServiceImpl();
    private ExportService exportService = ExportService.service;
    private BillService billService = new BillServiceImpl();

    /**
     * 服务商提现记录
     */
    public void index() {
        //从Session中获取用户名
        SystemUserInfo user = (SystemUserInfo) getSession().getAttribute(UserSession._USER);
        String custName = user.get("realName");
        Properties props = PropUtil.getPropUtil("/api.properties");
        //加密处理
        JSONObject json = new JSONObject();
        json.put("custName", custName);
        try {
            custName = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(json), props.getProperty("bank.key")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        setAttr("custName", custName);
        setAttr("url", props.getProperty("bank.txjl.url"));
        this.render("audit.jsp");
//        this.render("index.jsp");
    }

    /**
     * 提现记录数据
     */
    public void withdrawalData(){

        List<Record> list = financialService.getWithdrawalData(this.getParaMap());

        long totals = Db.find("select count(*) as total from merchant_apply_withdraw_record where is_del=0").get(0).getLong("total");

        this.renderJson(new NormalResponse(list, totals));
    }

    /**
     * 导出提现记录报表
     */
    public void exportWithdrawal(){
        try{
            List<MerchantApplyWithdrawRecord> list = financialService.getAllWithdrawalData();


            List<ExcelExportUtil.Pair> titles = financialService.getWithdrawalTitles();
            exportService.export(getResponse(), getRequest(), list, titles, "服务商提现记录", 0);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.renderNull();
    }

    /**
     * 服务商提现审核
     */
    public void audit(){
        //从Session中获取用户名
        SystemUserInfo user = (SystemUserInfo) getSession().getAttribute(UserSession._USER);
        String custName = user.get("realName");
        Properties props = PropUtil.getPropUtil("/api.properties");
        //加密处理
        JSONObject json = new JSONObject();
        json.put("custName", custName);
        try {
            custName = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(json), props.getProperty("bank.key")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        setAttr("custName", custName);
        setAttr("url", props.getProperty("bank.txjlsh.url"));
        this.render("audit.jsp");
    }

    public void bankQuery(){
        //从Session中获取用户名
        SystemUserInfo user = (SystemUserInfo) getSession().getAttribute(UserSession._USER);
        String custName = user.get("realName");
        Properties props = PropUtil.getPropUtil("/api.properties");
        //加密处理
        JSONObject json = new JSONObject();
        json.put("custName", custName);
        try {
            custName = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(json), props.getProperty("bank.key")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        setAttr("custName", custName);
        setAttr("url", props.getProperty("bank.txjg.url"));
        this.render("audit.jsp");
    }

    public void auditData() {

        List<Record> list = financialService.getAuditData(this.getParaMap());

        long totals = Db.find("select count(*) as total from merchant_apply_withdraw_record where is_del=0 and withdraw_status = 2").get(0).getLong("total");

        this.renderJson(new NormalResponse(list, totals));
    }
   
    /**
     * 服务商执行提现审核
     */
    public void doAudit(){
        ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());

        System.out.println(paramKit.getString("ids"));

        this.renderText("提交成功，正在联系银行处理...");
    }

    /**
     * 微信对账
     */
    public void statementWx(){
        this.render("statementWx.jsp");
    }

    /**
     * 微信对账数据
     */
    public void statementWxData(){
        List<Record> list = financialService.getStatementWxData(ReqParamKit.getInstance(this.getParaMap()));
        long totals = Db.queryLong("select count(*) from bill_wx_day");

        this.renderJson(new NormalResponse(list, totals));
    }

    /**
     * 微信对账详情
     */
    public void statementWxDetail(){
        List<Record> list = financialService.getStatementWxDetailData(ReqParamKit.getInstance(this.getParaMap()));

        this.setAttr("list", list);

        this.render("statementWxDetail.jsp");
    }
    /**
     * 微信 重新对账
     */
    public void reconciliationWx() throws ParseException {
        String day = this.getPara("day");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(day);
        billService.reconciliationWx(DateUtil.formatDate("yyyyMMdd", date));

        this.renderText("OK");
    }

    /**
     * 支付宝对账
     */
    public void statementAlipay(){
        getResponse().setHeader("Access-Control-Allow-Origin", "http://192.168.1.168:8080/");
        this.render("statementAlipay.jsp");
    }

    /**
     * 支付宝 对账数据
     */
    public void statementAlipayData(){
        List<Record> list = financialService.getStatementAlipayData(ReqParamKit.getInstance(this.getParaMap()));
        long totals = Db.queryLong("select count(*) from bill_ali_day");

        this.renderJson(new NormalResponse(list, totals));
    }

    /**
     * 支付宝对账详情
     */
    public void statementAlipayDetail(){
        List<Record> list = financialService.getStatementAlipayDetailData(ReqParamKit.getInstance(this.getParaMap()));

        this.setAttr("list", list);

        this.render("statementAlipayDetail.jsp");
    }

    /**
     * 支付宝 重新对账
     */
    public void reconciliationAlipay() {
        String day = this.getPara("day");

        billService.reconciliationAlipay(day);
        this.renderText("OK");
    }

    /**
     * 内部对账
     */
    public void statementSelf(){
        this.render("statementSelf.jsp");
    }

    /**
     * 支付宝 对账数据
     */
    public void statementSelfData(){
        List<Record> list = financialService.getStatementWalletData(ReqParamKit.getInstance(this.getParaMap()));
        long totals = Db.queryLong("select count(*) from bill_wallet_day");

        this.renderJson(new NormalResponse(list, totals));
    }

    /**
     * 内部对账详情
     */
    public void statementSelfDetail(){
        List<Record> list = financialService.getStatementWalletDetailData(ReqParamKit.getInstance(this.getParaMap()));

        this.setAttr("list", list);

        this.render("statementSelfDetail.jsp");
    }

    /**
     * 内部 重新对账
     */
    public void reconciliationSelf() {
        String day = this.getPara("day");

        billService.reconciliationWallet(day);
        this.renderText("OK");
    }
}
