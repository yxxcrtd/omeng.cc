package com.shanjin.push.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.apache.log4j.Logger;



import com.shanjin.push.util.PushManager;


/**
 * 推送工具类
 * @author 
 *
 */
public class PushUtil {
	// 本地异常日志记录对象
		private static final Logger logger = Logger.getLogger(PushUtil.class);
		
		/**
		 * 服务商认证消息推送
		 * @param configMap 
		 * @param data 
		 */
		public static boolean pushMerchantMsg(Map<String, Object> configMap, List<Map<String, Object>> pushMerchantList,String appType, String data) {
			boolean flag = true;
            String merchantId="";
			try {
				
				if(pushMerchantList==null||pushMerchantList.size()<1){
					return flag;
				}
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for(Map<String, Object> r : pushMerchantList){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("pushId", r.get("pushId"));
					map.put("userId", r.get("userId"));
					map.put("merchantId", r.get("merchant_id"));
					map.put("clientType", r.get("clientType"));
					merchantId= r.get("merchant_id").toString();
					list.add(map);
				}
				Map<String, Object> paras = new HashMap<String,Object>();
				int pushType = 11; // 11 剪彩成功
				
				paras.put("pushType", pushType);
				paras.put("appType", appType);
				paras.put("data", data);
				paras.put("merchantId", merchantId);
				
				PushManager.push(configMap,list, paras,"userId");
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
				logger.error("", e);
				return flag;
			}
			return flag;
		}

}