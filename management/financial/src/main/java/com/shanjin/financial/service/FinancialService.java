package com.shanjin.financial.service;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.MerchantApplyWithdrawRecord;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.util.ExcelExportUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/6.
 */
public interface FinancialService {
    public List<Record> getWithdrawalData(Map<String, String[]> paramMap);

    public List<ExcelExportUtil.Pair> getWithdrawalTitles();

    public List<MerchantApplyWithdrawRecord> getAllWithdrawalData();

    List<Record> getAuditData(Map<String, String[]> paraMap);

    List<Record> getStatementWxData(ReqParamKit paramKit);

    List<Record> getStatementAlipayData(ReqParamKit paramKit);

    List<Record> getStatementWxDetailData(ReqParamKit paramKit);

    List<Record> getStatementAlipayDetailData(ReqParamKit paramKit);

    List<Record> getStatementWalletData(ReqParamKit paramKit);

    List<Record> getStatementWalletDetailData(ReqParamKit paramKit);
}
