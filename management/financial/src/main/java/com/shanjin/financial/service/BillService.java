package com.shanjin.financial.service;

/**
 * Created by Administrator on 2016/7/18.
 */
public interface BillService {
    /**
     * 下载微信对账单
     * @param day 格式为yyyyMMdd
     */
    void saveBillWx(String day);

    /**
     * 微信对账
     * @param billDate
     */
    void reconciliationWx(String billDate);

    /**
     * 下载支付宝对账单
     * @param day 格式为yyyy-MM-dd
     */
    void saveBillAlipay(String day);

    /**
     * 支付宝对账
     * @param billDate 格式为yyyy-MM-dd
     */
    void reconciliationAlipay(String billDate);

    /**
     * 下载钱包对账单
     * @param billDate 格式为yyyy-MM-dd
     */
    public void saveWallet(String billDate);
    /**
     * 钱包对账
     * @param billDate
     */
    void reconciliationWallet(String billDate);
}
