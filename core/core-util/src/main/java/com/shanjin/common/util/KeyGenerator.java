package com.shanjin.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class KeyGenerator {

	public static String generateDynamicKey() {
		return UUID.randomUUID().toString();
	}
	
	
	public static String generateWithdrawBatchNo(String userId) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("TO");  //TO transout抓出标志
		strBuf.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		strBuf.append("U").append(userId); //U用户userId
		strBuf.append("T").append(Thread.currentThread().getId());
		return strBuf.toString();
	}
	
}
