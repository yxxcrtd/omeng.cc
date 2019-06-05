package com.shanjin.bank.util;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClientLogUtil{
	private static Log accessLoger = LogFactory.getLog("reqAndResLog");
	public static void  insertLog(String from,String to,String requestURL,String params,String resultStr) {
		try {
			accessLoger.info(getLogInfo(from,to,requestURL,params,resultStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getLogInfo(String from,String to,String requestURL,String params,String resultStr) throws Exception {
		String returnStr = "";
		// 时间|url|参数
		returnStr = "\n========================================================================================================================================================================"+
		"\ntime>>"+DateUtil.formatDate("yyyy-MM-dd HH:mm:ss", new Date()) + "\nfrom>>" + from + "\nto>>" + to  + "\nrequestURL>>" +requestURL + "\nparams>>" +params +"\nresultStr>>" +resultStr+
		"\n========================================================================================================================================================================\n";
		return returnStr;
	}
	
	/**
	 * @param s
	 *            处理字符串，把"|"替换为空格
	 */
	public static String stringProcess(String s) {
		if (s != null) {
			s = s.replaceAll("\\|", " ");
			return s;
		}
		return "";
	}



}
