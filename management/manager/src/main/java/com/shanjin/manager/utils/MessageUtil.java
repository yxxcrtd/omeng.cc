package com.shanjin.manager.utils;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class MessageUtil {

	/**
	 * 生成用户（用户和商户）消息
	 * 
	 * @param messageId
	 *            // 主消息ID,若无主消息，默认为0
	 * @param messageType
	 *            // 消息类型 （0：广播，其他：业务）
	 * @param customerType
	 *            // 用户类型（1商户，2用户）
	 * @param customerId
	 *            // 用户或商户ID
	 * @param title
	 * @param content
	 * @return
	 */
	public static boolean createCustomerMessage(Long messageId,
			int messageType, int customerType, Long customerId, String title,
			String content) {
		boolean flag = false;
		Record record = new Record();
		
		record.set("message_id", StringUtil.nullToLong(messageId))
				.set("message_type", messageType)
				.set("customer_type", customerType)
				.set("customer_id", customerId).set("title", title)
				.set("content", content).set("join_time", new Date());
		Db.save("customer_message_center", record);
		return flag;
	}
}
