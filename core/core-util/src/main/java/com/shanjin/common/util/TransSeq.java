package com.shanjin.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 生成消费金交易流水号
 * @author Administrator
 *
 */
public class TransSeq {
	/**
	 * 生成提现批号
	 * @param userId
	 * @return  type 
	 * HJ:活动奖励  KJ:开业剪彩  DS:订单收入 DJ:订单奖励 TS:提现申请  CZ:充值
	 */
	public static String generateTransSeqNo(int type) {
		StringBuffer strBuf = new StringBuffer();
		if (1 == type) {
			strBuf = strBuf.append("dd") ; //订单收入
		} 
		strBuf.append(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replace("-", "").toLowerCase();
		strBuf.append(uuid);	
		return strBuf.toString();
	}
}
