package com.shanjin.bank.config;

import java.util.*;

import com.shanjin.bank.util.PropUtil;


/**
 * @author wubaoliang(Restructure)
 *
 * Description	: 常量列表
 */
public class Constant {
	static Properties configPro = PropUtil.getPropUtil("config.properties");
	
	public static String COMPANY_NO=""; //企业号
	
	public static String ACCTNO=""; //付款账号
	
	static {
    	if("true".equals(configPro.getProperty("online.inuse"))){
    		COMPANY_NO = configPro.getProperty("company.no.online");
    		ACCTNO = configPro.getProperty("acctno.online");
    	}
    	else{
    		COMPANY_NO = configPro.getProperty("company.no.test");
    		ACCTNO = configPro.getProperty("acctno.test");
    	}
    }
	
	public static final int RESULT_CODE_INDEX=0; //处理结果编码下标
	
	public static final String SUCCESS_CODE="000000"; //银行处理成功返回编码
	
	public static final int STATUS_SUCCESS=1; //处理结果编码下标
	public static final int STATUS_FAIL=0; //处理结果编码下标
	
	public static final String BATCH_TRANSFER_TRAN_CODE="xhj2001"; //跨行对公向对私转账交易代码
	
	public static final String QUERY_BATCH_TRAN_CODE="xhj5002"; //批量转账交易结果查询
	
	public static final int DETAIL_BEGIN_INDEX=9; //银行批次号查询结果明细开始下标
	
	public static final int BANK_CODE_INDEX=6; //银行卡号下标
	
	public static final int USER_NAME_INDEX=7; //开户名称下标
	
	public static final int MONEY_INDEX=10; //转账金额下标
	
	public static final int DETAIL_RESULT_CODE_INDEX=13; //个人明细处理结果下标
	
	public static final int DETAIL_RESULT_MESSAGE_INDEX=14; //个人明细银行返回描述信息处理结果下标
	
	public static final int WITHDRAW_STATUS_APPLAY=10; //提现记录状态：提现申请
	
	public static final int PAGENUMBER_EXPORT = 0;
    public static final int PAGESIZE_EXPORT = 10000;
	
}
